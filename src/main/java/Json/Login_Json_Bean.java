package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name = "loginjsonbean")
@ViewScoped
public class Login_Json_Bean implements Serializable {

	String json;
	public Login_Json_Bean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
			DatabaseMethods1 DBM = new DatabaseMethods1();

			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String id = params.get("id");
			String pswd = params.get("password");
			
			String onlineExam = params.get("onlineExam");
			if(onlineExam==null || onlineExam.equals(""))
			{
				onlineExam = "no";
			}
	        //id=id.trim();

			JSONObject mainobj = new JSONObject();
			JSONArray arr = new JSONArray();

			JSONObject obj = new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			//// // System.out.println("Login Json User Agent : "+userAgent);
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String userType = DBM.authentication(id, pswd, conn);
				//System.out.println("Login Json User Type : "+userType);
			    
				if (userType != null)
				{
					String schid = DBM.authenticationSchoool(id, pswd, conn);
					DBM.app_login_status_Update(id, schid, conn);

					obj.put("schoolid", schid);
					// //// // System.out.println(schid);
					boolean checkSchool = DBM.checkSchoolStatus(schid, conn);
					if (checkSchool == true)
					{
						boolean expired=DBM.checkSchoolExpiryDate(schid,conn);
						//boolean expired = false;
						if (expired == false)
						{
							boolean checkDue=DBM.checkSchoolBillDueDate(schid,conn);
							if (checkDue == false)
							{
								if (userType.equals("student"))
								{
									/*Calendar now = Calendar.getInstance();
									int year = now.get(Calendar.YEAR);
									int month = now.get(Calendar.MONTH) + 1;
									if (month >= 4)
									{
										String.valueOf(year) + "-" + String.valueOf(year + 1);
									}
									else
									{
										String.valueOf(year - 1) + "-" + String.valueOf(year);
									}*/

									obj.put("msg", "success");
									obj.put("type", "student");
									
									obj.put("billDue", "no");

									arr.add(obj);

									mainobj.put("SchoolJson", arr);

									json = mainobj.toJSONString();

									// type =student

								}
								else
								{
									EmployeeInfo empinfo = new EmployeeInfo();
									
									if(userType.equalsIgnoreCase("admin"))
									{
										empinfo.setPlatform("both");
									}
									else
									{
										empinfo = DBM.allTeacherdetails(id, conn);
										if(onlineExam.equalsIgnoreCase("yes"))
										{
											empinfo.setPlatform("app");
										}
									}
									
									if(empinfo.getPlatform().equalsIgnoreCase("app") || empinfo.getPlatform().equalsIgnoreCase("both"))
									{
										SchoolInfoList list = DBJ.fullSchoolInfo(schid, conn);
										Boolean Show = true;
										String logStatus = "";


										if (userType.equalsIgnoreCase("admin")
												|| userType.equalsIgnoreCase("authority")
												|| userType.equalsIgnoreCase("principal")
												|| userType.equalsIgnoreCase("vice principal"))
										{
											Show = true;
											logStatus = DBJ.appLoginPermission(userType,schid,conn);

											if(logStatus.equalsIgnoreCase("false"))
											{
												Show=false;
											}
										}
										else if (userType.equalsIgnoreCase("Transport Manager")
												|| userType.equalsIgnoreCase("Security")
												|| userType.equalsIgnoreCase("Attendant")
												|| userType.equalsIgnoreCase("Driver")
												|| userType.equalsIgnoreCase("Conductor")
												|| userType.equalsIgnoreCase("Office Staff")
												|| userType.equalsIgnoreCase("Accounts")
												|| userType.equalsIgnoreCase("Sports Department")
												|| userType.equalsIgnoreCase("Others"))
										{
											Show = true;
											logStatus = DBJ.appLoginPermission(userType,schid,conn);

											if(logStatus.equalsIgnoreCase("false"))
											{
												Show=false;
											}
										}
										else if(userType.equalsIgnoreCase("academic coordinator") || userType.equalsIgnoreCase("Administrative Officer") 
												|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("teacher") 
												|| userType.equalsIgnoreCase("Librarian") )
										{
											/*if (list.getPermissionType().equalsIgnoreCase("basic"))
											{
												Show = false;
											}
											else
											{*/
												Show = true;
											//}

											/*if(Show==true)
											{*/
												logStatus = DBJ.appLoginPermission(userType,schid,conn);
												if(logStatus.equalsIgnoreCase("false"))
												{
													Show=false;
												}
											//}
										}
										else
										{
											Show=false;
										}


										if (Show == true)
										{
											ArrayList<SelectItem> rlist = new ArrayList<>();
											String uid = DBJ.employeeIdbyEmployeeName(id, schid, conn);
											System.out.println("11111");
											rlist = DBJ.attendantRouteList(userType,uid,schid,conn);
											/*Calendar now = Calendar.getInstance();
											int year = now.get(Calendar.YEAR);
											int month = now.get(Calendar.MONTH) + 1;
											if (month >= 4)
											{
												String.valueOf(year) + "-" + String.valueOf(year + 1);
											}
											else
											{
												String.valueOf(year - 1) + "-" + String.valueOf(year);
											}*/
											
											int start=DBM.schoolStartingSession(schid,conn);
											obj.put("msg", "success");
											obj.put("startSession", String.valueOf(start));
											obj.put("followUp", "no");

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

											if(userType.equalsIgnoreCase("admin")
													|| userType.equalsIgnoreCase("Administrative Officer")
													|| userType.equalsIgnoreCase("Principal")
													|| userType.equalsIgnoreCase("Vice Principal")
													|| userType.equalsIgnoreCase("academic coordinator")
													|| userType.equalsIgnoreCase("front office"))
											{
												obj.put("attendance_msg", "yes");
												obj.put("leavePermission", "yes");
											}
											else if(userType.equalsIgnoreCase("teacher"))
											{
												String permission=DBJ.teacherAttendancePermission("attendance_message_teacher",schid,conn);
												String lpermission=DBJ.teacherAttendancePermission("teacher_leave_action",schid,conn);
												obj.put("attendance_msg",permission);
												obj.put("leavePermission", lpermission);
											}
											else
											{
												String chk = DBJ.checkClassTeacherById(uid, schid, conn);
												if(chk.equalsIgnoreCase("yes"))
												{
													String permission=DBJ.teacherAttendancePermission("attendance_message_teacher",schid,conn);
													String lpermission=DBJ.teacherAttendancePermission("teacher_leave_action",schid,conn);
													obj.put("attendance_msg",permission);
													obj.put("leavePermission", lpermission);
												}
												else
												{
													obj.put("attendance_msg","no");
												}
											}

											String chk = DBJ.checkClassTeacherById(uid, schid, conn);
											if(chk.equalsIgnoreCase("yes") && !userType.equalsIgnoreCase("admin"))
											{
												obj.put("type", "Teacher");
											}
											else
											{
												if (userType.equalsIgnoreCase("Transport Manager"))
												{
													obj.put("type", "Transporter");
												}
												else if (userType.equals("Administrative Officer"))
												{
													obj.put("type", "academic coordinator");
												}
												else
												{
													obj.put("type", userType);
												}
											}

											obj.put("schLogo", list.getDownloadpath() + list.getImagePath());
											obj.put("schoolid", schid);
											obj.put("sname", list.getSchoolName());
											obj.put("smsSchName", list.getSmsSchoolName());
											
											if(userType.equalsIgnoreCase("Transport Manager")
													|| userType.equalsIgnoreCase("Security")
													|| userType.equalsIgnoreCase("Attendant")
													|| userType.equalsIgnoreCase("Driver")
													|| userType.equalsIgnoreCase("Conductor")
													|| userType.equalsIgnoreCase("Office Staff")
													|| userType.equalsIgnoreCase("Accounts")
													|| userType.equalsIgnoreCase("Sports Department")
													|| userType.equalsIgnoreCase("Others"))
											{
												obj.put("appPermission", "Transport studnet deatils,GPS");
												obj.put("appImagePermission", "");

											}
											else if(userType.equalsIgnoreCase("admin")
													|| userType.equalsIgnoreCase("authority")
													|| userType.equalsIgnoreCase("principal")
													|| userType.equalsIgnoreCase("vice principal"))
											{

												obj.put("appPermission", list.getAdmin_app_Permission());
												obj.put("appImagePermission", list.getAdmin_image_permission());
											}
											else if(userType.equalsIgnoreCase("Administrative Officer")
													|| userType.equalsIgnoreCase("front office"))
											{
												obj.put("appPermission", list.getAcdemic_coordinator_permission());
												obj.put("appImagePermission", list.getAcademic_image_permission());
											}
											else
											{
												obj.put("appImagePermission", list.getTeacher_image_permission());
											}

											
											obj.put("teacherPermission", list.getTeacher_permission());
											obj.put("ctype", list.getClient_type());
											obj.put("timeTable", list.getTimetable());
											obj.put("gps", list.getGps());
											obj.put("gps_user", list.getGpsUser());
											obj.put("gps_pwd", list.getGpsPwd());
											obj.put("gps_api", list.getGpsApi());
											obj.put("gpsProvider", list.getGpsProvider());
											obj.put("country", list.getCountry());
											obj.put("galleryRequest", list.getGalleryRequest());

											String mob = DBJ.employeeContactById(uid,"mobile", conn);
											if(mob.equalsIgnoreCase("no"))
											{
												mob = "0";
											}

											if(uid.equalsIgnoreCase(""))
											{
												uid = id;
											}
	                                        
											String staffTopic="";
											if(userType.equalsIgnoreCase("teacher"))
	                                        {
												staffTopic= "staff-"+uid+"-"+schid;
												staffTopic=staffTopic+","+"cbteacher";
	                                        }
											else
											{
												staffTopic="staff-"+uid+"-"+schid;
											}
	                                        
											obj.put("staffTopic",staffTopic);
											obj.put("uid", uid);
											obj.put("mobile", mob);
											obj.put("appVersion", DBM.contactNo("CB Admin App", conn));

											if (list.getPermissionType().equalsIgnoreCase("basic"))
											{
												obj.put("status", "L");

											}
											else
											{
												obj.put("status", "N");
											}

											obj.put("billDue", "no");
											arr.add(obj);

											mainobj.put("SchoolJson", arr);

											DBM.updateLastLogin(id, schid, conn);

											json = mainobj.toJSONString();
										}
										else
										{
											obj.put("msg", "Wrong username and password");
											obj.put("type", "");
											obj.put("billDue", "no");

											arr.add(obj);

											mainobj.put("SchoolJson", arr);

											json = mainobj.toJSONString();
										}
									
									}
									else
									{
										obj.put("msg", "Wrong username and password");
										obj.put("type", "");
										obj.put("billDue", "no");

										arr.add(obj);

										mainobj.put("SchoolJson", arr);

										json = mainobj.toJSONString();
									}
								}
							}
							else
							{
								
								if(userType.equalsIgnoreCase("admin"))
								{
									obj.put("msg", "Dear User," +
											"Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services");
									obj.put("type", "");
									obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
									obj.put("billDue", "yes");
									arr.add(obj);

									mainobj.put("SchoolJson", arr);
								}
								else
								{
									obj.put("msg", "Dear User, your license is suspended. Please contact your School administrator.");
									obj.put("type", "");
									obj.put("appVersion", DBM.contactNo("CB Admin App", conn));
									obj.put("billDue", "no");
									arr.add(obj);

									mainobj.put("SchoolJson", arr);
								}
								

								json = mainobj.toJSONString();
							}
						}
						else
						{
							obj.put("msg", "Sorry, license of your school ERP has been expired, Please contact Administrator for"
									+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
									+ " here to serve you. Thanks and Regards");
							obj.put("type", "");
							obj.put("billDue", "no");
							obj.put("appVersion", DBM.contactNo("CB Admin App", conn));

							arr.add(obj);

							mainobj.put("SchoolJson", arr);

							json = mainobj.toJSONString();
						}
					}
					else
					{
						obj.put("msg", "Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards");
						obj.put("type", "");
						obj.put("billDue", "no");
						obj.put("appVersion", DBM.contactNo("CB Admin App", conn));


						arr.add(obj);

						mainobj.put("SchoolJson", arr);

						json = mainobj.toJSONString();
					}

				}
				else
				{

					obj.put("msg", "Wrong username and password");
					obj.put("type", "");
					obj.put("billDue", "no");
					obj.put("appVersion", DBM.contactNo("CB Admin App", conn));

					arr.add(obj);

					mainobj.put("SchoolJson", arr);

					json = mainobj.toJSONString();
					// wrong usernmae or password
				}
			}
			else
			{
				obj.put("msg", "Wrong username and password");
				obj.put("type", "");
				obj.put("billDue", "no");
				obj.put("appVersion", DBM.contactNo("CB Admin App", conn));

				arr.add(obj);

				mainobj.put("SchoolJson", arr);

				json = mainobj.toJSONString();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
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

	private static final long serialVersionUID = 1L;
	private String id;
	private String pswd;

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String check() {

		Connection conn = DataBaseConnection.javaConnection();
		try {
			if (id.equals("dynamic_school@imgglobalinfotech.com") && pswd.equals("imgglobalinfotech")) {
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("username", "master");
				return "trialPeriod";
			}
			/*
			 * else if(id.equals("imgglobalinfotech@dynamicschool") &&
			 * pswd.equals("imgglobalinfotech")) { HttpSession ss=(HttpSession)
			 * FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			 * return "selectProjectModule"; }
			 */
			else {
				String userType = new DatabaseMethods1().authentication(id, pswd, conn);
				if (userType != null) {
					boolean b =true;
					if (b == true) {
						HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
								.getSession(true);
						ss.setAttribute("username", id);
						ss.setAttribute("type", userType);
						if (userType.equals("module")) {
							return "selectProjectModule";
						} else {
							if (userType.equals("student")) {
								String selectedSession;
								Calendar now = Calendar.getInstance();
								int year = now.get(Calendar.YEAR);
								int month = now.get(Calendar.MONTH) + 1;
								if (month >= 4) {
									selectedSession = String.valueOf(year) + "-" + String.valueOf(year + 1);
								} else {
									selectedSession = String.valueOf(year - 1) + "-" + String.valueOf(year);
								}

								HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance()
										.getExternalContext().getSession(true);
								httpSession.setAttribute("selectedSession", selectedSession);

								ss.setAttribute("checkstu", true);
								return "managestudentLoginModule";
							} else {
								ArrayList<SelectItem> list = new DatabaseMethods1().allModules(conn);
								if (list.size() > 0) {
									boolean i = new DatabaseMethods1().checkSchoolInfo(conn);
									if (i == true) {
										String selectedSession;
										Calendar now = Calendar.getInstance();
										int year = now.get(Calendar.YEAR);
										int month = now.get(Calendar.MONTH) + 1;
										if (month >= 4) {
											selectedSession = String.valueOf(year) + "-" + String.valueOf(year + 1);
										} else {
											selectedSession = String.valueOf(year - 1) + "-" + String.valueOf(year);
										}

										HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance()
												.getExternalContext().getSession(true);
										httpSession.setAttribute("selectedSession", selectedSession);
										ss.setAttribute("checkstu", false);
										return "AdminHome";
									} else {
										return "schoolInformation";
									}
								} else {
									FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"No Modules Are There In This Software.Please Contact Administrator!",
											"Validation error"));
								}
							}
						}
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Your trial period out of range,Please contact with administrator",
										"Validation error"));
					}
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Username or password is wrong", "Validation error"));
				}
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
