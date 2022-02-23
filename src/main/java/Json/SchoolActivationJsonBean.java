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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
@ManagedBean(name="schoolActivationJson")
@ViewScoped

public class SchoolActivationJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public SchoolActivationJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String schid = params.get("Schoolid");
			String userType = params.get("type");
			String appType = params.get("appType");
			String userid="",id="";
			userid=params.get("userid");
			if(userid==null || userid.equals(""))
			{
				userid="";
			}
			
			if(appType==null || appType.equals(""))
			{
				appType="normal";
			}
			
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			String session=DBM.selectedSessionDetails(schid,conn);
			if(checkRequest)
			{
				ArrayList<SelectItem> rlist = new ArrayList<>();
				if(!userType.equalsIgnoreCase("student"))
				{
					id = DBJ.employeeIdbyEmployeeName(userid, schid, conn);
					rlist = DBJ.attendantRouteList(userType,id,schid,conn);
				}

				boolean checkSchool = DBM.checkSchoolStatus(schid, conn);
				if (checkSchool == true)
				{
					boolean expired=DBM.checkSchoolExpiryDate(schid,conn);
					//boolean expired = false;
					if (expired == false)
					{
						boolean checkDue=false;
						if(!userType.equalsIgnoreCase("student"))
						{
							 checkDue=DBM.checkSchoolBillDueDate(schid,conn);	
						}
						
						if (checkDue == false)
						{
							SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
							int start=DBM.schoolStartingSession(schid,conn);
							obj.put("country", info.getCountry());
							obj.put("msg", "proceed");
							obj.put("timeTable", info.getTimetable());
							obj.put("startSession", String.valueOf(start));
							obj.put("paytm_marchent_key", info.getPaytm_marchent_key());
							obj.put("paytm_mid", info.getPaytm_mid());
							obj.put("pg_type", info.getPg_type());
							obj.put("rzp_mid", info.getRzp_mid());
							obj.put("rzp_key", info.getRzp_key());
							obj.put("rzp_key_secret", info.getRzp_key_secret());
							obj.put("gpsProvider", info.getGpsProvider());
							obj.put("session", session);

							if(rlist.size()>0)
							{
								obj.put("attendant", "yes");
							}
							else
							{
								obj.put("attendant", "no");
							}
							
							if(DBJ.appLoginPermission("Attendant", schid, conn).equalsIgnoreCase("false"))
							{
								obj.put("attendant", "no");
							}

							obj.put("followUp", "no");
							if(userType.equalsIgnoreCase("student"))
							{
								obj.put("block_modules", DBM.blockedStudentAppMods(schid, userid, "modules", conn));
								obj.put("block_msg", DBM.blockedStudentAppMods(schid, userid, "msg", conn));
								obj.put("permission", info.getStudent_app_permission());
								obj.put("attendance_msg","no");
								obj.put("status", info.getStudentApp());
								obj.put("schName",info.getSchoolName());
								obj.put("smsSchName", info.getSmsSchoolName());
								obj.put("schLogo",info.getDownloadpath()+info.getImagePath());
								obj.put("online_fee", info.getOnlineFee());
								
								if(appType.equalsIgnoreCase("common_app"))
								{
									obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
								}
								else
								{
									obj.put("appVersion", info.getAppVersion());
								}
							}
							else if(userType.equalsIgnoreCase("admin")
									|| userType.equalsIgnoreCase("authority")
									|| userType.equalsIgnoreCase("principal")
									|| userType.equalsIgnoreCase("vice principal"))
							{
								obj.put("permission", info.getAdmin_app_Permission());
								obj.put("attendance_msg","yes");
								obj.put("leavePermission", "yes");
								obj.put("login_status",DBJ.app_login_status(userid, schid, conn));
								obj.put("status",DBJ.userStatus(userid, schid, conn));
								obj.put("login_permit",DBJ.appLoginPermission(userType, schid, conn));
								obj.put("schName",info.getSchoolName());
								obj.put("smsSchName", info.getSmsSchoolName());
								obj.put("schLogo",info.getDownloadpath()+info.getImagePath());
								obj.put("appImagePermission", info.getAdmin_image_permission());

							}
							else if(userType.equalsIgnoreCase("academic coordinator") || userType.equalsIgnoreCase("Administrative Officer") 
									|| userType.equalsIgnoreCase("front office"))
							{
								obj.put("permission", info.getAcdemic_coordinator_permission());
								obj.put("attendance_msg","yes");
								obj.put("leavePermission", "yes");
								obj.put("login_status",DBJ.app_login_status(userid, schid, conn));
								obj.put("status",DBJ.userStatus(userid, schid, conn));
								obj.put("login_permit",DBJ.appLoginPermission(userType, schid, conn));
								obj.put("schName",info.getSchoolName());
								obj.put("smsSchName", info.getSmsSchoolName());
								obj.put("schLogo",info.getDownloadpath()+info.getImagePath());
								obj.put("appImagePermission", info.getAcademic_image_permission());
							}
							else if(userType.equalsIgnoreCase("Transporter")
									|| userType.equalsIgnoreCase("Attendant")
									|| userType.equalsIgnoreCase("Security")
									|| userType.equalsIgnoreCase("Driver")
									|| userType.equalsIgnoreCase("Conductor")
									|| userType.equalsIgnoreCase("Office Staff")
									|| userType.equalsIgnoreCase("Accounts")
									|| userType.equalsIgnoreCase("Sports Department")
									|| userType.equalsIgnoreCase("Others"))
							{
								obj.put("permission", "Transport studnet deatils,GPS");
								obj.put("attendance_msg","no");
								obj.put("leavePermission", "no");
								obj.put("login_status",DBJ.app_login_status(userid, schid, conn));
								obj.put("status",DBJ.userStatus(userid, schid, conn));
								obj.put("login_permit",DBJ.appLoginPermission(userType, schid, conn));
								obj.put("schName",info.getSchoolName());
								obj.put("smsSchName", info.getSmsSchoolName());
								obj.put("schLogo",info.getDownloadpath()+info.getImagePath());
								obj.put("appImagePermission", "");

							}
							else if(userType.equalsIgnoreCase("teacher") || userType.equalsIgnoreCase("Librarian") )
							{
								obj.put("permission", info.getTeacher_permission());
								String permission=DBJ.teacherAttendancePermission("attendance_message_teacher",schid,conn);
								String lpermission=DBJ.teacherAttendancePermission("teacher_leave_action",schid,conn);
								obj.put("attendance_msg",permission);
								obj.put("leavePermission", lpermission);
								obj.put("login_status",DBJ.app_login_status(userid, schid, conn));
								obj.put("status",DBJ.userStatus(userid, schid, conn));
								obj.put("login_permit",DBJ.appLoginPermission(userType, schid, conn));
								obj.put("schName",info.getSchoolName());
								obj.put("smsSchName", info.getSmsSchoolName());
								obj.put("schLogo",info.getDownloadpath()+info.getImagePath());
								obj.put("appImagePermission", info.getTeacher_image_permission());

							}

							if(!userType.equalsIgnoreCase("student"))
							{
								String mob = DBJ.employeeContactById(id,"mobile", conn);
								if(mob.equalsIgnoreCase("no"))
								{
									mob = "0";
								}

								if(id.equalsIgnoreCase(""))
								{
									id = userid;
								}

								String staffTopic="";
								if(userType.equalsIgnoreCase("teacher"))
                                {
									staffTopic= "staff-"+id+"-"+schid;
									staffTopic=staffTopic+","+"cbteacher";
                                }
								else
								{
									staffTopic="staff-"+id+"-"+schid;
								}
								
								obj.put("staffTopic", staffTopic);
								obj.put("uid", id);
								obj.put("mobile", mob);
								obj.put("galleryRequest", info.getGalleryRequest());
								obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
							}
							obj.put("billDue", "no");

							arr.add(obj);

							//mainobj.put("SchoolJson", arr);
							DBM.updateLastLogin(userid, schid, conn);

							json = arr.toJSONString();
						}
						else
						{
							obj.put("msg", "Dear User," +
									"Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services");
							obj.put("permission", "");
							obj.put("attendance_msg","");
							obj.put("timeTable", "");
							obj.put("online_fee", "");
							obj.put("country", "");

							SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
							if(!userType.equalsIgnoreCase("student"))
							{
								obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
							}
							else
							{
								if(appType.equalsIgnoreCase("common_app"))
								{
									obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
								}
								else
								{
									obj.put("appVersion", info.getAppVersion());
								}
							}
							obj.put("billDue", "yes");
							obj.put("session", session);
							arr.add(obj);

							//	mainobj.put("SchoolJson", arr);

							json = arr.toJSONString();
						}

					}
					else
					{
						obj.put("msg", "Sorry, license of your school ERP has been expired, Please contact Administrator for"
								+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
								+ " here to serve you. Thanks and Regards");
						obj.put("permission", "");
						obj.put("attendance_msg","");
						obj.put("timeTable", "");
						obj.put("online_fee", "");
						obj.put("country", "");

						SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
						if(!userType.equalsIgnoreCase("student"))
						{
							obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
						}
						else
						{
							if(appType.equalsIgnoreCase("common_app"))
							{
								obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
							}
							else
							{
								obj.put("appVersion", info.getAppVersion());
							}
						}

						obj.put("billDue", "no");
						obj.put("session", session);
						arr.add(obj);

						//	mainobj.put("SchoolJson", arr);

						json = arr.toJSONString();
					}
				}
				else
				{
					obj.put("msg", "Sorry, license of your school ERP has been expired, Please contact Administrator for" +
							" license renewal. Make the renewal as soon as possible and enjoy our services. We are" +
							" here to serve you. Thanks and Regards");
					obj.put("permission", "");
					obj.put("attendance_msg","");
					obj.put("timeTable", "");
					obj.put("online_fee", "");
					obj.put("country", "");

					SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
					if(!userType.equalsIgnoreCase("student"))
					{
						obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
					}
					else
					{
						if(appType.equalsIgnoreCase("common_app"))
						{
							obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
						}
						else
						{
							obj.put("appVersion", info.getAppVersion());
						}
					}
					obj.put("billDue", "no");
					obj.put("session", session);
					arr.add(obj);

					//	mainobj.put("SchoolJson", arr);

					json = arr.toJSONString();
				}
			}
			else
			{
				obj.put("msg", "Sorry, Request from unknown source is prohibited");
				obj.put("permission", "");
				obj.put("attendance_msg","");
				obj.put("timeTable", "");
				obj.put("online_fee", "");
				obj.put("country", "");
				obj.put("billDue", "no");

				SchoolInfoList info=DBJ.fullSchoolInfo(schid,conn);
				if(!userType.equalsIgnoreCase("student"))
				{
					obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
				}
				else
				{
					if(appType.equalsIgnoreCase("common_app"))
					{
						obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
					}
					else
					{
						obj.put("appVersion", info.getAppVersion());
					}
				}
				obj.put("session", session);
				arr.add(obj);

				//	mainobj.put("SchoolJson", arr);

				json = arr.toJSONString();
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

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
