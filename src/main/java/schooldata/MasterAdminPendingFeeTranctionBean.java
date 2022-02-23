package schooldata;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paytm.pg.merchant.CheckSumServiceHelper;

import Json.DataBaseMeathodJson;

@ManagedBean(name="masterAdminPendingFeeTranctionBean")
@ViewScoped
public class MasterAdminPendingFeeTranctionBean implements Serializable
{
	
	
	ArrayList<PendingTranctionList>list=new ArrayList<>();
	PendingTranctionList selectedTranction;
   public MasterAdminPendingFeeTranctionBean() 
   {

		Connection conn=DataBaseConnection.javaConnection();
		
		list=new DatabaseMethods1().allPendingTranction(conn);
		
   
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 	    
   }
   
   
   public String  checkStatus()
   {

		Connection conn=DataBaseConnection.javaConnection();
		
		

		
	   SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(selectedTranction.getSchoolId(), conn);
	   

		
	   

		TreeMap<String,String> paramMap = new TreeMap<>();
		paramMap.put("ORDER_ID" ,selectedTranction.getOrderid());
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
		    
		    
		
		    String addmissionNumber=selectedTranction.getAddNumber();
			String schoolid=selectedTranction.getSchoolId();
			String orderid=selectedTranction.getOrderid();
			String num="";
			
			
			
			
			String status="";
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

			int check=new DataBaseMeathodJson().updateFeeStatus(addmissionNumber,schoolid,orderid,num,status,"",conn);
			
			if(check>0)
		   	{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee Status Update Successfully"));
				list=new DatabaseMethods1().allPendingTranction(conn);
				//// // System.out.println(list.size());
				return "masterAdminPendingTranstionHistory.xhtml";
				
		   	} 
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur PLease Try Again"));
				
			}
		    
	   	}
	   	
	   
	   	responseReader.close();
	   	
	   	
	   	
	   
	   	
	   	
	   
	   } catch (Exception exception) {
	   	exception.printStackTrace();
	   }
	   
	   
	   

  
	   
	   
	   
	   
	   
	   
			   try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   
			   return "";
   }
   
   
   
   
   public void  checkStatusForAll()
   {

	   
	   
	   
		Connection conn=DataBaseConnection.javaConnection();
		
		
		
		for(PendingTranctionList selectedTranction:list)
		{
			   SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(selectedTranction.getSchoolId(), conn);
			   

				
			   

				TreeMap<String,String> paramMap = new TreeMap<>();
				paramMap.put("ORDER_ID" ,selectedTranction.getOrderid());
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
				    
				    
				
				    String addmissionNumber=selectedTranction.getAddNumber();
					String schoolid=selectedTranction.getSchoolId();
					String orderid=selectedTranction.getOrderid();
					String num="";
					
					
					
					
					String status="";
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

					int check=new DataBaseMeathodJson().updateFeeStatus(addmissionNumber,schoolid,orderid,num,status,"",conn);
					
				    
			   	}
			   	
			   
			   	responseReader.close();
			   	} catch (Exception exception) {
			   	exception.printStackTrace();
			   }
			   
			   
		}

		
	     list=new DatabaseMethods1().allPendingTranction(conn);

			   try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   
				
   }
   
   
   
   
   
public ArrayList<PendingTranctionList> getList() {
	return list;
}
public void setList(ArrayList<PendingTranctionList> list) {
	this.list = list;
}




public PendingTranctionList getSelectedTranction() {
	return selectedTranction;
}




public void setSelectedTranction(PendingTranctionList selectedTranction) {
	this.selectedTranction = selectedTranction;
}
   
   
}
