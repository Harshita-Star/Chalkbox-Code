package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONException;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="deliveryReportBatchId")
@ViewScoped
public class DelivieryReportByStudentStatusJson implements Serializable
{



	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	public DelivieryReportByStudentStatusJson(){
		Connection conn=DataBaseConnection.javaConnection();
		try {

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			/*
		    String studentid = params.get("studentid");
	        String phoneno = params.get("phoneno");
			 */
			String schoolid=params.get("Schoolid");
			String batchid=params.get("batchid");
			params.get("type");
			//String againref=params.get("ref");
			String status="";
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList ls=DBJ.fullSchoolInfo(schoolid,conn);
				DataBaseMethodStudent objStd=new DataBaseMethodStudent();
				ArrayList<String> stdColumnlist=objStd.completeFieldsForStudentList();
				String session=DatabaseMethods1.selectedSessionDetails(schoolid, conn);


				int MN=DBJ.getMessageHistoryByBatchIdtstatus(batchid,schoolid,conn);
				DeliveryReport list1=DBJ.allDeliveryreportDetails(schoolid,batchid,conn);
				if(MN!=30)
				{
					if(list1.getSms_gatway().equalsIgnoreCase("chalkbox"))
					{
						String batchStatus=DBJ.batchStatus(ls.getUsername(), batchid);
						try {
							
							ArrayList<DeliveryReport>list=DBJ.getMessageHistoryByBatchId(batchid,schoolid,conn);

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

								DBJ.updateStatus(batchid,status,schoolid,conn);
							}
							else
							{

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else
					{
						String result=DBJ.batchStatusIMG(ls.getUsername(), batchid);
						  result=result.replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">", "");
							
						   
						org.primefaces.shaded.json.JSONArray jsonArray = null;
						try {
						org.primefaces.shaded.json.JSONObject resultObject = new org.primefaces.shaded.json.JSONObject(result);
						jsonArray = resultObject.optJSONArray("delivery_report");
						// // System.out.print("slength2 =" + jsonArray.length());
						} catch (Exception e) {
						e.printStackTrace();
						}

						JSONObject jsonInnerChildNode = null;
						try {

							ArrayList<DeliveryReport>list=DBJ.getMessageHistoryByBatchId(batchid,schoolid,conn);

							String[] mobline=list.get(0).getContactnumber().split(",");
						    String[] statusarr=new String[mobline.length];
							for (int i = 0; i <= jsonArray.length() - 1; i++) 
							{
								
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
						
						
						DBJ.updateStatus(batchid,status,schoolid,conn);
						
						
						} catch (JSONException e) {
						e.printStackTrace();
						}
					}
				}

				ArrayList<DeliveryReport>list=DBJ.getMessageHistoryByBatchId(batchid,schoolid,conn);

				for(DeliveryReport ls1:list)
				{

					String addnum[]=ls1.getAddNumber().split(",");
					String mobileNo[]=ls1.getContactnumber().split(",");
					String status1[]=ls1.getStatus().split(",");
					for(int j=0;j<addnum.length;j++)
					{
						JSONObject obj = new JSONObject();

						obj.put("addNumber", addnum[j]);
						obj.put("mobileno", mobileNo[j]);
						if(j<=status1.length)
						{
							if(status1[j].equals("D"))
							{
								obj.put("status", "DELIVERED");
							}
							else if(status1[j].equals("U"))
							{
								obj.put("status", "UNDELIVERED");
							}
							else if(status1[j].equals("?"))
							{
								obj.put("status","PUSHED");
							}
							else if(status1[j].equals("E"))
							{
								obj.put("status","EXPIRED");
							}
							else
							{
								obj.put("status", status1[j]);
							}
						}
						else
						{
							obj.put("status", "DELIVERED");
						}


						if(list1.getType().equalsIgnoreCase("student"))
						{
							StudentInfo lst=objStd.studentDetail(addnum[j],"","",QueryConstants.ADD_NUMBER,QueryConstants.DELIVERY_REPORT,null,null, "","","","", session, schoolid, stdColumnlist, conn).get(0);
							obj.put("name",lst.getFname());
							obj.put("Class",lst.getClassName());

						}
						else if(list1.getType().equals("staff"))
						{
							obj.put("name",addnum[j].replaceAll("@CB@", "-"));
							obj.put("Class","");

						}
						else if(list1.getType().equals("enq"))
						{
							obj.put("name",addnum[j]);
							obj.put("Class","Enquiry");
						}

						arr.add(obj);
					}
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


}
