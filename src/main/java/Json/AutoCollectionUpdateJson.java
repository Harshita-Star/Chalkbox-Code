package Json;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paytm.pg.merchant.CheckSumServiceHelper;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.PendingTranctionList;
import schooldata.SchoolInfoList;

@ManagedBean(name="AutoCollectionUpdateJson")
public class AutoCollectionUpdateJson implements Serializable{

	ArrayList<PendingTranctionList>list=new ArrayList<>();

	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public AutoCollectionUpdateJson() 
	{
		 // System.out.println("AUTOCOLLECTION JOB");
		Connection conn=DataBaseConnection.javaConnection();
		
		list=new DatabaseMethods1().allPendingTranction(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		checkStatusForAll();
		
		json="done";
	
	}
	
	
	 public void  checkStatusForAll()
	 {
		Connection conn=DataBaseConnection.javaConnection();
		String addmissionNumber="";
		String schoolid="";
		String orderid="";
		String num="";
		String status="";
		
		for(PendingTranctionList selectedTranction:list)
		{
			num = ""; status = "";
			addmissionNumber=selectedTranction.getAddNumber();
			schoolid=selectedTranction.getSchoolId();
			orderid=selectedTranction.getOrderid();

			SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(selectedTranction.getSchoolId(), conn);
			
		    if(ls.getPg_type().equalsIgnoreCase("paytm"))
		    {
		    	TreeMap<String,String> paramMap = new TreeMap<>();
				paramMap.put("ORDER_ID" ,orderid);
				paramMap.put("MID" , ls.getPaytm_mid());
				
			   /**
			   * Generate checksum by parameters we have
			   * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
			   * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
			   */
			    String checksum = null;
				try {
					checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(ls.getPaytm_marchent_key(), paramMap);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
	
			   /* put generated checksum value here */
			   paramMap.put("CHECKSUMHASH", checksum);
	
			   /* prepare JSON string for request */
			   JSONObject obj = new JSONObject(paramMap);
			   String post_data = obj.toString();
	
			   /* for Staging */
			    URL url = null;
				try {
					url = new URL("https://securegw.paytm.in/order/status");
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				}
	
			   /* for Production */
			   // URL url = new URL("https://securegw.paytm.in/order/status");
	
			   try {
			   	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			   	connection.setRequestMethod("POST");
			   	connection.setRequestProperty("Content-Type", "application/json");
			   	connection.setDoOutput(true);
	
			   	DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			   	requestWriter.writeBytes(post_data);
			   	requestWriter.close();
			   	String responseData = "";
			   	InputStream is = connection.getInputStream();
			   	BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			   	if ((responseData = responseReader.readLine()) != null) {
			   		
			   		String json="["+responseData+"]";
			   	 Type type = new TypeToken<java.util.List<PendingTranctionList>>(){}.getType();
				    ArrayList<PendingTranctionList> list = new Gson().fromJson(json, type);
				
				    
					if(list.get(0).getSTATUS().equalsIgnoreCase("TXN_SUCCESS"))
					{
						int number = new DatabaseMethods1().feeserailno(schoolid,conn);
						if (String.valueOf(number).length() == 1) {
							num = "0000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 2) {
							num = "000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 3) {
							num = "00" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 4) {
							num = "0" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 5) {
							num = String.valueOf(number);
						}
						status="ACTIVE";
					}
					else
					{
						status=list.get(0).getSTATUS();
					}
				    
			   	}
			   	
			   	responseReader.close();
			   
			   	updateStatus(addmissionNumber, schoolid, orderid, num, status,"", conn);
			   	
				
			   }
			   catch(Exception e) {
				   e.printStackTrace();
			   	}
		    }
		    else if(ls.getPg_type().equalsIgnoreCase("razorpay"))
		    {
			    try {
			    	RazorpayClient razorpay = new RazorpayClient(ls.getRzp_key(), ls.getRzp_key_secret());
		        	
			    	List<Payment> payments = razorpay.Orders.fetchPayments(orderid);
			    	String txnStatus = payments.get(0).get("status");
			    	String paymentId = payments.get(0).get("id");
			    	if(txnStatus.equalsIgnoreCase("failed") || txnStatus.equalsIgnoreCase("refunded"))
		        	{
			    		updateStatus(addmissionNumber, schoolid, orderid, num, "TXN_FAILURE",paymentId, conn);
		        	}
		        	else if(txnStatus.equalsIgnoreCase("captured"))
		        	{
		        		int number = new DatabaseMethods1().feeserailno(schoolid,conn);
						if (String.valueOf(number).length() == 1) {
							num = "0000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 2) {
							num = "000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 3) {
							num = "00" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 4) {
							num = "0" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 5) {
							num = String.valueOf(number);
						}
						
		        		updateStatus(addmissionNumber, schoolid, orderid, num, "ACTIVE",paymentId, conn);
		        	}
		        	else if(txnStatus.equalsIgnoreCase("authorized"))
		        	{
		        		double totalAmount = Double.parseDouble(selectedTranction.getTotalAmount());
		        		String amt = new DecimalFormat("##").format(totalAmount*100);
		        		JSONObject captureRequest = new JSONObject();
			        	captureRequest.put("amount", amt);
			        	captureRequest.put("currency", "INR");
			        	try
			        	{
			        		Payment payment = razorpay.Payments.capture(paymentId, captureRequest);
			        		
			        		Payment det = razorpay.Payments.fetch(paymentId);
				        	String txnSts = det.get("status");
				        	if(txnSts.equalsIgnoreCase("failed") || txnSts.equalsIgnoreCase("refunded"))
				        	{
					    		updateStatus(addmissionNumber, schoolid, orderid, num, "TXN_FAILURE",paymentId, conn);
				        	}
				        	else if(txnSts.equalsIgnoreCase("captured"))
				        	{
				        		int number = new DatabaseMethods1().feeserailno(schoolid,conn);
								if (String.valueOf(number).length() == 1) {
									num = "0000" + String.valueOf(number);
								} else if (String.valueOf(number).length() == 2) {
									num = "000" + String.valueOf(number);
								} else if (String.valueOf(number).length() == 3) {
									num = "00" + String.valueOf(number);
								} else if (String.valueOf(number).length() == 4) {
									num = "0" + String.valueOf(number);
								} else if (String.valueOf(number).length() == 5) {
									num = String.valueOf(number);
								}
				        		updateStatus(addmissionNumber, schoolid, orderid, num, "ACTIVE",paymentId, conn);
				        	}
			        	}
			        	catch(Exception e)
			        	{
			        		String excArr[] = e.getMessage().split(":");
			        		if(excArr.length > 1)
			        		{
			        			System.out.println(excArr[1]);
			        			if(excArr[1].equalsIgnoreCase("This payment has already been captured"))
			        			{
			        				updateStatus(addmissionNumber, schoolid, orderid, num, "ACTIVE",paymentId, conn);
			        			}
			        		}
			        		else
			        		{
			        			e.printStackTrace();
			        		}
			        		
			        	}
		        	}
			    } catch (Exception e) {
					e.printStackTrace();
				}
		    }
			   
		}

	
 		list=new DatabaseMethods1().allPendingTranction(conn);

	    try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	 }
	 
	 public void updateStatus(String addmissionNumber, String schoolid, String orderid, String num, String status, String paymentId, Connection conn)
	 {
		 int check=new DataBaseMeathodJson().updateFeeStatus(addmissionNumber,schoolid,orderid,num,status,paymentId,conn);
		 if(status.equalsIgnoreCase("ACTIVE"))
		 {
			 String modArr[]=new String[0];
			 int totalAmt = DBJ.feeAmountByOrderid(schoolid,orderid,"ACTIVE",conn);
			 DBM.blockStudentAppMods(addmissionNumber,"Fees Block",modArr,schoolid,totalAmt,"","auto",conn);
		 }
	 }
	
	
	  public void renderJson() throws IOException
		{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/json");
			externalContext.setResponseCharacterEncoding("UTF-8");
			externalContext.getResponseOutputWriter().write(json);
			facesContext.responseComplete();
		}
		public String getJson() {
			return json;
		}
		public void setJson(String json) {
			this.json = json;
		}
	
}
