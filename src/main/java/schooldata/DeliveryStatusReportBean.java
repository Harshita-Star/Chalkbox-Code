package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONException;

import Json.DataBaseMeathodJson;
import Json.DeliveryReport;
import session_work.QueryConstants;


@ManagedBean(name="deliveryStatusReportBean")
@ViewScoped
public class DeliveryStatusReportBean implements Serializable {

	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	ArrayList<String> stdColumnlist=objStd.completeFieldsForStudentList();
	String session;
	ArrayList<DeliveryReport>list,messageList;
	DeliveryReport lss;
	public DeliveryStatusReportBean() {
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		DeliveryReport lss=(DeliveryReport) ss.getAttribute("messsagelist");
		String schoolid = (String) ss.getAttribute("schoolid");
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		SchoolInfoList ls=DBJ.fullSchoolInfo(schoolid,conn);
		session=DatabaseMethods1.selectedSessionDetails(schoolid, conn);
		String status="";

		int MN=DBJ.getMessageHistoryByBatchIdtstatus(lss.getBatchId(),schoolid,conn);
		DeliveryReport list1=DBJ.allDeliveryreportDetails(schoolid,lss.getBatchId(),conn);
		if(MN!=30)
		{
			if(list1.getSms_gatway().equalsIgnoreCase("chalkbox"))
			{
				String batchStatus=DBJ.batchStatus(ls.getUsername(), lss.getBatchId());
				try {
					ArrayList<DeliveryReport>list=DBJ.getMessageHistoryByBatchId(lss.getBatchId(),schoolid,conn);
					String[] mobline=list.get(0).getContactnumber().split(",");
				    String[] statusarr=new String[mobline.length];

					org.primefaces.shaded.json.JSONObject ob=new org.primefaces.shaded.json.JSONObject(batchStatus);
					String sts=ob.getString("status");
					if(sts.equalsIgnoreCase("success"))
					{
						org.primefaces.shaded.json.JSONArray jarr=ob.getJSONArray("messages");
						for(int i=0;i<=jarr.length()-1;i++)
						{
							org.primefaces.shaded.json.JSONObject innerob=jarr.getJSONObject(i);
							String st=innerob.getString("status");
							String st1=String.valueOf(innerob.getLong("recipient"));
							String mob=st1.substring(2);
							for(int ii=0;ii<mobline.length;ii++)
							{
								if(mob.equals(mobline[ii]))
								{
									if(statusarr[ii]==null||statusarr[ii].equals(""))
									{
										statusarr[ii]=st;
										
										break;
											
									}
									
								}
							}
							
							
															
						}
						
						for(int j=0;j<statusarr.length;j++)
						{
							
							if(status.equals(""))
							{
								status=statusarr[j];
							}
							else
							{
								status=status+","+statusarr[j];
							}
						}
						
						DBJ.updateStatus(lss.getBatchId(),status,schoolid,conn);
					}
					else
					{
					}
				} 
				catch (JSONException e)
				{ 
					e.printStackTrace();
				}
			}
			else
			{
				String batchId =  lss.getBatchId();
				if(lss.getBatchId().startsWith("\"")&&lss.getBatchId().endsWith("\"")) {
					batchId =  lss.getBatchId().substring(1, lss.getBatchId().length()-1);
				}
				
			     String result=DBJ.batchStatusIMG(ls.getUsername(),batchId);
			     
			     
			     result=result.replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">", "");
				org.primefaces.shaded.json.JSONArray jsonArray = null;
				try {
				org.primefaces.shaded.json.JSONObject resultObject = new org.primefaces.shaded.json.JSONObject(result);
				jsonArray = resultObject.optJSONArray("delivery_report");
				} catch (Exception e) {
				e.printStackTrace();
				}

				JSONObject jsonInnerChildNode = null;
				try {
					ArrayList<DeliveryReport>list=DBJ.getMessageHistoryByBatchId(lss.getBatchId(),schoolid,conn);
					String[] mobline=list.get(0).getContactnumber().split(",");
				    String[] statusarr=new String[mobline.length];
				    
				   
				    if(jsonArray != null) {
				    	 
					    for (int i = 0; i <= jsonArray.length() - 1; i++) {
						org.primefaces.shaded.json.JSONObject innerob=jsonArray.getJSONObject(i);
						String st=innerob.getString("Mobile");
	                    String statusDEL=innerob.getString("DeliveryStatus");
	                    String mob=st.substring(2);
	                    
						for(int ii=0;ii<mobline.length;ii++)
						{
							   
								if(mob.equals(mobline[ii]))
								{
									if(statusarr[ii]==null||statusarr[ii].equals(""))
									{
										statusarr[ii]=statusDEL;
										break;
									}
									
									
								}
					     }
					   }
				    }
				   
				    
				    
				    for(int j=0;j<statusarr.length;j++)
					{
						if(status.equals(""))
						{
							status=statusarr[j];
						}
						else
						{
							status=status+","+statusarr[j];
						}
					}
				    
					DBJ.updateStatus(lss.getBatchId(),status,schoolid,conn);
				} catch (JSONException e) {
				e.printStackTrace();
				}
			}
		}

		list=DBJ.getMessageHistoryByBatchId(lss.getBatchId(),schoolid,conn);

		messageList=new ArrayList<>();

		for(DeliveryReport ls1:list)
		{
			String addnum[]=ls1.getAddNumber().split(",");
			String mobileNo[]=ls1.getContactnumber().split(",");
			String status1[]=ls1.getStatus().split(",");
			for(int j=0;j<addnum.length;j++)
			{
				DeliveryReport lstt = new DeliveryReport();
				lstt.setContactnumber(mobileNo[j]);
				
					if(j<=status1.length)
					{
						if(status1[j].equals("D"))
						{
							lstt.setStatus( "DELIVERED");
						}
						else if(status1[j].equals("U"))
						{
							lstt.setStatus( "UNDELIVERED");
						}
						else if(status1[j].equals("?"))
						{
							lstt.setStatus( "PUSHED");
						}
						else if(status1[j].equals("E"))
						{
							lstt.setStatus("EXPIRED");
						}
						else
						{
							lstt.setStatus(status1[j]);
						}
					}
					else
					{
						lstt.setStatus( "DELIVERED");
					}
				

				if(lss.getType().equalsIgnoreCase("student"))
				{
					StudentInfo lst=objStd.studentDetail(addnum[j],"","",QueryConstants.ADD_NUMBER,QueryConstants.DELIVERY_REPORT,null,null, "","","","", session, schoolid, stdColumnlist, conn).get(0);
					lstt.setAddNumber(lst.getSrNo());
					lstt.setStuName(lst.getFname());
					lstt.setStuClass(lst.getClassName());
					lstt.setMessage(lss.getMessage());
					lstt.setDate(lss.getDate());
					lstt.setCount(String.valueOf(j+1));

					messageList.add(lstt);
				}
				else if(lss.getType().equalsIgnoreCase("staff"))
				{
					lstt.setAddNumber("Staff");
					lstt.setStuName(addnum[j]);
					lstt.setStuClass("Staff");
					lstt.setMessage(lss.getMessage());
					lstt.setDate(lss.getDate());
					lstt.setCount(String.valueOf(j+1));

					messageList.add(lstt);
				}
				else if(lss.getType().equalsIgnoreCase("enq"))
				{
					lstt.setAddNumber("Enquiry");
					lstt.setStuName(addnum[j]);
					lstt.setStuClass("Enquiry");
					lstt.setMessage(lss.getMessage());
					lstt.setDate(lss.getDate());
					lstt.setCount(String.valueOf(j+1));

					messageList.add(lstt);
				}
			}
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<DeliveryReport> getList() {
		return list;
	}

	public void setList(ArrayList<DeliveryReport> list) {
		this.list = list;
	}

	public ArrayList<DeliveryReport> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<DeliveryReport> messageList) {
		this.messageList = messageList;
	}

	public DeliveryReport getLss() {
		return lss;
	}

	public void setLss(DeliveryReport lss) {
		this.lss = lss;
	}

}