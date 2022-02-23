package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="pickDropActionJson")
@ViewScoped

public class PickDropActionJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public PickDropActionJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String routeid = params.get("routeId");
			String stopid = params.get("stopId");
			String studentid = params.get("studentId");
			String date = params.get("date");
			String status = params.get("status");
			String type = params.get("type");
			String value = params.get("value");
			String userid = params.get("userid");
			String remark = params.get("remark");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(userid==null || userid.equalsIgnoreCase(""))
				{
					userid = "";
				}
				
				if(remark==null || remark.equalsIgnoreCase(""))
				{
					remark = "";
				}
				
				Date actionDate=null;
				try
				{
					actionDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
				String permit = DBJ.checkCommType( schid,"attendant", conn);

				StudentInfo ls = DBJ.studentDetailslistByAddNo(studentid, schid, conn);
				
				String adminTag = ls.getFname()+" of class - "+ls.getClassName();

				String pickTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
				
				if(status.equalsIgnoreCase("pick"))
				{
					if(type.equalsIgnoreCase("normal"))
					{
						DBJ.insertStudentPickDetail(studentid, schid, stopid, routeid, remark, actionDate,"no",value,status,userid,pickTime, conn);
					}
					else
					{
						String currentStop = DBJ.studentCurrentStop(studentid,schid,conn);
						String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(schid,conn), conn, schid);
						String rem = "Picked By Other Bus";
						DBJ.insertStudentPickDetail(studentid, schid, currentStop, currentRoute, rem, actionDate,"yes",value,status,userid,pickTime, conn);
						/*DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up by the school bus of other route.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid, conn);
						DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up by the school bus of other route.",
								ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid, conn);*/
					}

					if(permit.equalsIgnoreCase("parent"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
					}
					else if(permit.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Transport", adminTag+" has been picked up from home bus stop by the school bus.", "admin-"+schid, schid, "HomePick-"+ls.getAddNumber(), conn);
					}
					else if(permit.equalsIgnoreCase("both"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from home bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						
						DBJ.adminnotification("Transport", adminTag+" has been picked up from home bus stop by the school bus.", "admin-"+schid, schid, "HomePick-"+ls.getAddNumber(), conn);
					}
				}
				else if(status.equalsIgnoreCase("schoolpick"))
				{
					if(type.equalsIgnoreCase("normal"))
					{
						DBJ.insertStudentPickDetail(studentid, schid, stopid, routeid, remark, actionDate,"no",value,status,userid,pickTime, conn);

					}
					else
					{
						String currentStop = DBJ.studentCurrentStop(studentid,schid,conn);
						String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(schid,conn), conn, schid);
						String rem = "Picked By Other Bus";
						DBJ.insertStudentPickDetail(studentid, schid, currentStop, currentRoute, rem, actionDate,"yes",value,status,userid,pickTime, conn);
						/*DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up by the school bus of other route.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid, conn);
						DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up by the school bus of other route.",
								ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid, conn);*/
					}

					if(permit.equalsIgnoreCase("parent"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{	
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
					}
					else if(permit.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Transport", adminTag+" has been picked up from school bus stop by the school bus.", "admin-"+schid, schid, "SchoolPick-"+ls.getAddNumber(), conn);
					}
					else if(permit.equalsIgnoreCase("both"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{	
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been picked up from school bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
					
						DBJ.adminnotification("Transport", adminTag+" has been picked up from school bus stop by the school bus.", "admin-"+schid, schid, "SchoolPick-"+ls.getAddNumber(), conn);
					}

				}
				else if(status.equalsIgnoreCase("drop"))
				{
					if(type.equalsIgnoreCase("normal"))
					{
						DBJ.insertStudentDropDetail(studentid, schid, stopid, routeid, remark, actionDate,"no",value,status,userid,pickTime, conn);
					}
					else
					{
						String currentStop = DBJ.studentCurrentStop(studentid,schid,conn);
						String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(schid,conn), conn, schid);
						String rem = "Dropped By Other Bus";
						DBJ.insertStudentDropDetail(studentid, schid, currentStop, currentRoute, rem, actionDate,"yes",value,status,userid,pickTime, conn);
					}

					if(permit.equalsIgnoreCase("parent"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
					}
					else if(permit.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Transport", adminTag+" has been deboarded at home bus stop by the school bus.", "admin-"+schid, schid, "HomeDrop-"+ls.getAddNumber(), conn);
					}
					else if(permit.equalsIgnoreCase("both"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at home bus stop by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						
						DBJ.adminnotification("Transport", adminTag+" has been deboarded at home bus stop by the school bus.", "admin-"+schid, schid, "HomeDrop-"+ls.getAddNumber(), conn);
					}
				}
				else if(status.equalsIgnoreCase("schooldrop"))
				{
					if(type.equalsIgnoreCase("normal"))
					{
						DBJ.insertStudentDropDetail(studentid, schid, stopid, routeid, remark, actionDate,"no",value,status,userid,pickTime, conn);
					}
					else
					{
						String currentStop = DBJ.studentCurrentStop(studentid,schid,conn);
						String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DatabaseMethods1.selectedSessionDetails(schid,conn), conn, schid);
						String rem = "Dropped By Other Bus";
						DBJ.insertStudentDropDetail(studentid, schid, currentStop, currentRoute, rem, actionDate,"yes",value,status,userid,pickTime, conn);
					}

					if(permit.equalsIgnoreCase("parent"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
					}
					else if(permit.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Transport", adminTag+" has been deboarded at school by the school bus.", "admin-"+schid, schid, "SchoolDrop-"+ls.getAddNumber(), conn);
					}
					else if(permit.equalsIgnoreCase("both"))
					{
						if(ls.getFathersPhone()==ls.getMothersPhone())
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						else
						{
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
							DBJ.notification("Transport", "Your ward "+ls.getFname()+" has been deboarded at school by the school bus.",
									ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
						}
						
						DBJ.adminnotification("Transport", adminTag+" has been deboarded at school by the school bus.", "admin-"+schid, schid, "SchoolDrop-"+ls.getAddNumber(), conn);
					}
				}

				JSONObject obj = new JSONObject();
				JSONArray arr=new JSONArray();

				obj.put("status", "done");
				arr.add(obj);
				json=arr.toJSONString();

			}
			else
			{
				JSONObject obj = new JSONObject();
				JSONArray arr=new JSONArray();

				obj.put("status", "Something Went Wrong!");
				arr.add(obj);
				json=arr.toJSONString();
			}
			
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
