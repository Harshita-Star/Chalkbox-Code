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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="studentLoginJson")
@ViewScoped

public class StudentLoginJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	public StudentLoginJsonBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		

		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String id = params.get("id");
			String pswd = params.get("pwd");
			String aliasName = params.get("schid");

			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			String schid = DBJ.schoolIdByAliasName(aliasName, conn);
			SchoolInfoList info = DBJ.fullSchoolInfo(schid, conn);
			String session=DBM.selectedSessionDetails(schid,conn);
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			if(checkRequest)
			{
				if(info == null)
				{
					obj.put("msg", "Unknown School Code");
					obj.put("studentid", "");
					obj.put("schid", "");
					obj.put("name", "");
					obj.put("status", "no");
					obj.put("classname", "");
					obj.put("schName", "");
					obj.put("schLogo", "");
					obj.put("appVersion", "");
					obj.put("permission", "");
					obj.put("timeTable", "");
					obj.put("online_fee", "");
					obj.put("country", "");
					obj.put("email", "");
					obj.put("upd", "");
					obj.put("session", session);
					
					arr.add(obj);

					json = arr.toJSONString();
				}
				else
				{
					String userType = DBM.authentication(id, pswd, conn);
					if (userType != null)
					{
						DBM.app_login_status_Update(id, schid, conn);
						

						boolean checkSchool = DBM.checkSchoolStatus(schid, conn);
						if (checkSchool == true)
						{
							boolean expired=DBM.checkSchoolExpiryDate(schid,conn);
							if (expired == false)
							{
								if (userType.equals("student"))
								{
									
									new DataBaseJsonDeatil().updateAppDownloadStatusStudent(id,conn);
									ArrayList<String> list=objStd.basicFieldsForStudentList();
									StudentInfo stinfo=objStd.studentDetail(id,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","",session, schid, list, conn).get(0);
									if(stinfo!=null)
									{
										String fathersMobile = String.valueOf(stinfo.getFathersPhone());
										if(fathersMobile.length()==10 
												&& !fathersMobile.equals("2222222222")
												&& !fathersMobile.equals("9999999999")
												&& !fathersMobile.equals("1111111111")
												&& !fathersMobile.equals("1234567890")
												&& !fathersMobile.equals("0123456789"))
										{
											ArrayList<StudentInfo> selectedStudent=objStd.studentDetail(fathersMobile,"","",QueryConstants.MOBILE_NO,QueryConstants.MOBILE_NO,null,null,"","","","",session, schid, list, conn);

											if (selectedStudent.size() == 0) 
											{
												obj.put("msg", "Mobile No. Not Updated, Please Contact School to Updated Your Mobile No.");
												obj.put("studentid", "");
												obj.put("schid", "");
												obj.put("upd", "");
												obj.put("name", "");
												obj.put("status", "no");
												obj.put("classname", "");
												obj.put("schName", info.getSchoolName());
												obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
												obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
												obj.put("permission", "");
												obj.put("timeTable", "");
												obj.put("online_fee", "");
												obj.put("country", "");
												obj.put("email", "");
												obj.put("session", session);
												
												arr.add(obj);

												json = arr.toJSONString();
											}
											else
											{
												String name = "", stutid = "", classname = "", pwd = "", upwd = "";
												for (StudentInfo ss : selectedStudent) {
													String studentcheck=DBM.authenticationCheck(ss.getAddNumber(), conn);
													if(!studentcheck.equals(""))
													{
														pwd = DBJ.userPassword(ss.getAddNumber(), conn);
														if(name.equals(""))
														{
															name=ss.getFname();
															stutid=ss.getAddNumber();
															classname=ss.getClassName();
															upwd=pwd;
														}
														else
														{
															name=name+","+ss.getFname();
															stutid=stutid+","+ss.getAddNumber();
															classname=classname+","+ss.getClassName();
															upwd=upwd+","+pwd;
														}
													}

												}
												
												obj.put("upd", upwd);
												obj.put("msg", "success");
												obj.put("studentid", stutid);
												obj.put("schid", schid);
												obj.put("name", name);
												obj.put("classname", classname);
												obj.put("status", info.getStudentApp());
												obj.put("permission", info.getStudent_app_permission());
												obj.put("schName", info.getSchoolName());
												obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
												obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
												obj.put("timeTable", info.getTimetable());
												obj.put("online_fee", info.getOnlineFee());
												obj.put("country", info.getCountry());
												obj.put("paytm_marchent_key", info.getPaytm_marchent_key());
												obj.put("paytm_mid", info.getPaytm_mid());
												obj.put("email", "");
												obj.put("session", session);
												arr.add(obj);

												json = arr.toJSONString();
											}
										}
										else
										{
											obj.put("msg", "Mobile No. Not Updated, Please Contact School to Updated Your Mobile No.");
											obj.put("studentid", "");
											obj.put("schid", "");
											obj.put("upd", "");
											obj.put("name", "");
											obj.put("status", "no");
											obj.put("classname", "");
											obj.put("schName", info.getSchoolName());
											obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
											obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
											obj.put("permission", "");
											obj.put("timeTable", "");
											obj.put("online_fee", "");
											obj.put("country", "");
											obj.put("email", "");
											obj.put("session", session);
											
											arr.add(obj);

											json = arr.toJSONString();
										}
									}
									else
									{
										obj.put("msg", "Wrong username or password");
										obj.put("studentid", "");
										obj.put("schid", "");
										obj.put("upd", "");
										obj.put("name", "");
										obj.put("status", "no");
										obj.put("classname", "");
										obj.put("schName", info.getSchoolName());
										obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
										obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
										obj.put("permission", "");
										obj.put("timeTable", "");
										obj.put("online_fee", "");
										obj.put("country", "");
										obj.put("email", "");
										obj.put("session", session);

										arr.add(obj);

										json = arr.toJSONString();
									}
								}
								else
								{ 
									obj.put("msg", "Wrong username or password");
									obj.put("studentid", "");
									obj.put("schid", "");
									obj.put("name", "");
									obj.put("upd", "");
									obj.put("status", "no");
									obj.put("classname", "");
									obj.put("schName", info.getSchoolName());
									obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
									obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
									obj.put("permission", "");
									obj.put("timeTable", "");
									obj.put("online_fee", "");
									obj.put("country", "");
									obj.put("email", "");
									obj.put("session", session);

									arr.add(obj);

									json = arr.toJSONString();
								}
							
							}
							else
							{
								obj.put("msg", "Sorry, license of your school ERP has been expired, Please contact Administrator for"
										+ " license renewal. Make the renewal as soon as possible and enjoy our services. We are"
										+ " here to serve you. Thanks and Regards");
								obj.put("studentid", "");
								obj.put("schid", "");
								obj.put("name", "");
								obj.put("upd", "");
								obj.put("status", "no");
								obj.put("classname", "");
								obj.put("schName", info.getSchoolName());
								obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
								obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
								obj.put("permission", "");
								obj.put("timeTable", "");
								obj.put("online_fee", "");
								obj.put("country", "");
								obj.put("email", "");
								obj.put("session", session);

								arr.add(obj);

								json = arr.toJSONString();
							}
						}
						else
						{
							obj.put("msg", "Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards");
							obj.put("studentid", "");
							obj.put("schid", "");
							obj.put("upd", "");
							obj.put("name", "");
							obj.put("status", "no");
							obj.put("classname", "");
							obj.put("schName", info.getSchoolName());
							obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
							obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
							obj.put("permission", "");
							obj.put("timeTable", "");
							obj.put("online_fee", "");
							obj.put("country", "");
							obj.put("email", "");
							obj.put("session", session);

							arr.add(obj);

							json = arr.toJSONString();
						}

					}
					else
					{

						obj.put("msg", "Wrong username or password");
						obj.put("studentid", "");
						obj.put("schid", "");
						obj.put("upd", "");
						obj.put("name", "");
						obj.put("status", "no");
						obj.put("classname", "");
						obj.put("schName", info.getSchoolName());
						obj.put("schLogo", info.getDownloadpath() + info.getImagePath());
						obj.put("appVersion", DBM.contactNo("CB Parent App", conn));
						obj.put("permission", "");
						obj.put("timeTable", "");
						obj.put("online_fee", "");
						obj.put("country", "");
						obj.put("email", "");
						obj.put("session", session);

						arr.add(obj);

						json = arr.toJSONString();
						// wrong usernmae or password
					}
				
				}
			}
			else
			{
				obj.put("msg", "Unknown Source");
				obj.put("studentid", "");
				obj.put("upd", "");
				obj.put("schid", "");
				obj.put("name", "");
				obj.put("status", "no");
				obj.put("classname", "");
				obj.put("schName", "");
				obj.put("schLogo", "");
				obj.put("appVersion", "");
				obj.put("permission", "");
				obj.put("timeTable", "");
				obj.put("online_fee", "");
				obj.put("country", "");
				obj.put("email", "");
				obj.put("session", session);

				arr.add(obj);

				json = arr.toJSONString();
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
}
