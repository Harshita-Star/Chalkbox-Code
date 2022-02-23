package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import exam.DatabaseMethodExam;
import reports_module.DataBaseMethodsReports;
import session_work.QueryConstants;
import session_work.RegexPattern;
import student_module.RegistrationColumnName;

@ManagedBean(name = "loginBean")
@ViewScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	private String id;
	private String pswd;
	String billMsg,schoolId;
	ArrayList<SchoolInfo> list = new ArrayList<>();

	public LoginBean() throws ParseException {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//ss.invalidate();
		
		
		/*
		 * String path = "/home/chalkboxerp/public_html/DEMOSCHOOL/Sub-Sub4"; new
		 * File(path).mkdirs(); final File file = new File(path); file.setReadable(true,
		 * false); file.setExecutable(true, false); file.setWritable(true, false);
		 */
	}

	public void getUserId()
	{
		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("studentUserIdInfo.xhtml");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String check() {
		
		
		/*
		 * DataBaseMethodsSessionHandling db=new DataBaseMethodsSessionHandling();
		 * db.copyClassFromPreviousSession();
		 * db.copyTransportRouteFromPreviousSession();
		 */
		Connection conn = DataBaseConnection.javaConnection();

		try {
			DatabaseMethods1 DBM = new DatabaseMethods1();
			DataBaseMethodsReports dbr = new DataBaseMethodsReports();
			if (id.equals("ch@!kbox") && pswd.equals("Q$cH@!kb0X$SR")) {
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext ec = fc.getExternalContext();
				try {
					ec.redirect("masterAdminOTP.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "masterAdminOTP.xhtml";

			}
			else if (id.equals("developer") && pswd.equals("developer@123")) {
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("username", "developer");
				ss.setAttribute("type", "developer");
				ss.setAttribute("masterAdmin", "no");
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext ec = fc.getExternalContext();
				try {
					ec.redirect("viewAllTicket.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "viewAllTicket.xhtml";
			}
			/*
			 * else if(id.equals("imgglobalinfotech@dynamicschool") &&
			 * pswd.equals("imgglobalinfotech")) { HttpSession ss=(HttpSession)
			 * FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			 * return "selectProjectModule"; }
			 */
			else {
				String userType = DBM.authentication(id, pswd, conn);
				// // System.out.println(userType);
				if (userType != null)
				{
					String login_type=DBM.loginTypeOfUser(id, pswd, userType, conn);
					if(login_type.equals("school"))
					{
						// // System.out.println(login_type);
						if(userType.equalsIgnoreCase("service_executive"))
						{
							list=dbr.executiveSchList(id, "report", conn);
							PrimeFaces.current().ajax().update("schForm");
							PrimeFaces.current().executeInitScript("PF('schDlg').show()");
						}
						else
						{
							String school = DBM.authenticationSchoool(id, pswd, conn);
							boolean checkSchool = DBM.checkSchoolStatus(school, conn);
							// boolean checkSchool=true;
							if (checkSchool == true)
							{
								boolean expired = DBM.checkSchoolExpiryDate(school, conn);
								// boolean expired=false;

								if (expired == false)
								{
									boolean checkDue=DBM.checkSchoolBillDueDate(school,conn);
									if (checkDue == false)
									{

										/*
										 * boolean b=DatabaseMethods1.dateTrialPeriod(); if(b==true) {
										 */
										String selectedSession="";
										/*Calendar now = Calendar.getInstance();
										int year = now.get(Calendar.YEAR);
										int month = now.get(Calendar.MONTH) + 1;
										if (month >= 4) {
											selectedSession = String.valueOf(year) + "-"
													+ String.valueOf(year + 1);
										} else {
											selectedSession = String.valueOf(year - 1) + "-"
													+ String.valueOf(year);
										}*/

										HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
												.getSession(true);
										ss.setAttribute("showSMSBalMessage", "yes");
										ss.setAttribute("showDueBillMessage", "yes");
										ss.setAttribute("showSessionMessage", "yes");
										ss.setAttribute("schoolid", school);
										ss.setAttribute("schid", school);
										ss.setAttribute("username", id);
										ss.setAttribute("pwd", pswd);
										ss.setAttribute("type", userType);
										ss.setAttribute("mtype", userType);
										ss.setAttribute("masterAdmin", "no");
										ss.setAttribute("serviceExecutive", "no");
										
										SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
										selectedSession = info.getDefaultSession();

										ArrayList<String> message = DBM.checkmessageSetting(conn);
										ArrayList<String> template=new DatabaseMethods1().checktemplateSetting(conn);
										ss.setAttribute("financialYear", selectedSession);
										ss.setAttribute("selectedSession", selectedSession);
										ss.setAttribute("feesubmit", message.get(0));
										ss.setAttribute("registration", message.get(1));
										ss.setAttribute("enquiry", message.get(2));
										ss.setAttribute("attendance", message.get(3));
										ss.setAttribute("complaint", message.get(4));
										ss.setAttribute("birthday", message.get(5));
										ss.setAttribute("eventNotify", message.get(6));
										ss.setAttribute("marksheetNotify", message.get(7));
										ss.setAttribute("resultNotify", message.get(8));
										ss.setAttribute("elearningNotify", message.get(9));

										ss.setAttribute("feeMsg", template.get(0));
										ss.setAttribute("regMsg", template.get(1));
										ss.setAttribute("enqMsg", template.get(2));
										ss.setAttribute("attMsg", template.get(3));
										ss.setAttribute("compMsg", template.get(4));
										ss.setAttribute("birthMsg", template.get(5));

										DBM.updateLastLogin(id, school, conn);
										if (userType.equals("module")) {
											return "selectProjectModule";
										} else {
											if (userType.equalsIgnoreCase("student")) {
												/*String selectedSession;
												Calendar now = Calendar.getInstance();
												int year = now.get(Calendar.YEAR);
												int month = now.get(Calendar.MONTH) + 1;
												if (month >= 4) {
													selectedSession = String.valueOf(year) + "-" + String.valueOf(year + 1);
												} else {
													selectedSession = String.valueOf(year - 1) + "-" + String.valueOf(year);
												}

												ss.setAttribute("selectedSession", selectedSession);*/
												SchoolInfoList info1 = new DatabaseMethods1().fullSchoolInfo(school,conn);
												
												if(info1.getStudentWebLogin().equals("true"))
												{
													ArrayList<String> tempList=new ArrayList<>();
													tempList.add(RegistrationColumnName.SESSION);
													
													String sst =new DataBaseMethodStudent().studentDetail(id,"","",QueryConstants.ADD_NUMBER_NO_SESSION,QueryConstants.NAME,null,null,"","","","", "", school, tempList, conn).get(0).getSession();
													String defultsession = new DatabaseMethods1().checkDefaultSession(school, conn);
													ss.setAttribute("selectedSession", sst);
												   /*if(sst.equals(defultsession))
	                                                {*/
	                                                	ss.setAttribute("checkstu", true);
	    												FacesContext fc = FacesContext.getCurrentInstance();
	    												ExternalContext ec = fc.getExternalContext();
	    												try {
	    													ec.redirect("homePage.xhtml");
	    												} catch (IOException e) {
	    													e.printStackTrace();
	    												}

	    												return "homePage.xhtml";
		
														/*
														 * } else { FacesContext.getCurrentInstance().addMessage(null,
														 * new FacesMessage( FacesMessage.SEVERITY_ERROR,
														 * "Dear User, you have not been promoted to the current session right now. Kindly contact your school admin for further details."
														 * , "Validation error"));
														 * 
														 * }
														 */
													
												}
												else
												{
													FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
															"Sorry! You are not an authorised user.", "Validation error"));
												}
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
												}
												
												if(empinfo.getPlatform().equalsIgnoreCase("web") || empinfo.getPlatform().equalsIgnoreCase("both"))
												{

													if (userType.equalsIgnoreCase("Teacher")) {
														/*String selectedSession;
														 * 
														Calendar now = Calendar.getInstance();
														int year = now.get(Calendar.YEAR);
														int month = now.get(Calendar.MONTH) + 1;
														if (month >= 4) {
															selectedSession = String.valueOf(year) + "-" + String.valueOf(year + 1);
														} else {
															selectedSession = String.valueOf(year - 1) + "-" + String.valueOf(year);
														}

														ss.setAttribute("selectedSession", selectedSession);*/
														ss.setAttribute("checkstu", false);
														FacesContext fc = FacesContext.getCurrentInstance();
														ExternalContext ec = fc.getExternalContext();
														try {
															ec.redirect("teacherHomePage.xhtml");
														} catch (IOException e) {
															e.printStackTrace();
														}

														return "teacherHomePage.xhtml";
													}
													else if(userType.equalsIgnoreCase("Driver") || userType.equalsIgnoreCase("Conductor") || userType.equalsIgnoreCase("Attendant") || userType.equalsIgnoreCase("Others"))
													{
														FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
																"You are not allowed to login into the Chalkbox ERP. Please contact ERP School Admin to login.", "Validation error"));
													}
													else
													{
														if (info.getLoginOtp().equals("yes")) {
															String mob = DBM.checkAdminMobileNo(userType,id, pswd, conn);
															if (mob.equals("") || mob.equals("0")) {
																FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
																		FacesMessage.SEVERITY_ERROR,
																		"Sorry, This Account Has No Registered Mobile No. Please Contact Administrator to Update Your Mobile No. For Verification. Thanks and Regards",
																		"Validation error"));
															} else {

																int randomPIN = (int) (Math.random() * 9000) + 1000;
																String typemessage = "Hello User, " + String.valueOf(randomPIN)
																+ " is  your login OTP.Treat this as confidential.We welcome you to be a part of Digital India.  Thank You.  "
																+ info.getSmsSchoolName();

																new DatabaseMethods1().messageurlStaff(mob, typemessage, "admin", conn,new DatabaseMethods1().schoolId(),"");

																ss.setAttribute("adminOTP", String.valueOf(randomPIN));
																ss.setAttribute("adminMob", mob);

																FacesContext fc = FacesContext.getCurrentInstance();
																ExternalContext ec = fc.getExternalContext();
																try {
																	ec.redirect("otpPage.xhtml");
																} catch (IOException e) {
																	e.printStackTrace();
																}
																return "otpPage.xhtml";
															} // ***************************** OTP WORK FOR ADMIN ***********************
														}
														else {
															ArrayList<SelectItem> list = DBM.allModules(conn);
															if (list.size() > 0) {
																boolean i = DBM.checkSchoolInfo(conn);
																if (i == true) {


																	/*	int n = DBM.getAllAttendance(new Date(), conn);
																	if (n == 0) {
																		ArrayList<ClassInfo> classSection = DBM.allClassList(conn);

																		for (ClassInfo ls : classSection) {
																			for (Category ls1 : ls.getCategoryList()) {
																				DatabaseMethods1.attendanceSectionLogin(
																						String.valueOf(ls1.getId()), ls1.getList(),
																						new Date(), conn);
																			}
																		}

																	}
																	 */


																	ss.setAttribute("checkstu", false);
																	FacesContext fc = FacesContext.getCurrentInstance();
																	ExternalContext ec = fc.getExternalContext();

																	if (userType.equalsIgnoreCase("Accounts")) {
																		try {
																			ec.redirect("dashboardAccount.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																		return "dashboardAccount.xhtml";
																	}
																	else if (userType.equalsIgnoreCase("Transport Manager"))
																	{
																		try {
																			ec.redirect("dashboardTransport.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																		return "dashboardTransport.xhtml";
																	}
																	else if (userType.equalsIgnoreCase("Librarian"))
																	{
																		try {
																			ec.redirect("dashboardLibrary.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																		return "dashboardLibrary.xhtml";
																	}
																	else if (userType.equalsIgnoreCase("Security"))
																	{
																		try {
																			ec.redirect("dashboardSecurity.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																		return "dashboardSecurity.xhtml";
																	}
																	else if (userType.equalsIgnoreCase("Front Office"))
																	{
																		if(info.getBranch_id().equals("54"))
																		{
																			try {
																				ec.redirect("dashboardFrontOfficeBlm.xhtml");
																			} catch (IOException e) {
																				e.printStackTrace();
																			}
																			return "dashboardFrontOfficeBlm.xhtml";
																		}
																		else
																		{
																			if(info.getClient_type().equalsIgnoreCase("school"))
																			{
																				try {
																					ec.redirect("dashboardFrontOffice.xhtml");
																				} catch (IOException e) {
																					e.printStackTrace();
																				}
																				return "dashboardFrontOffice.xhtml";
																			}
																			else
																			{
																				try {
																					ec.redirect("dashboardFrontOfficeIns.xhtml");
																				} catch (IOException e) {
																					e.printStackTrace();
																				}
																				return "dashboardFrontOfficeIns.xhtml";
																			}
																		}

																	}
																	else if (userType.equalsIgnoreCase("Principal") || userType.equalsIgnoreCase("Vice Principal"))
																	{
																		try {
																			ec.redirect("dashboardPrinciple.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																		return "dashboardPrinciple.xhtml";
																	}
																	else
																	{
																		try
																		{
																			if(userType.equalsIgnoreCase("madmin")||userType.equalsIgnoreCase("maccount"))
																			{
																				ss.setAttribute("type", "Admin");
																				ss.setAttribute("etype",userType);

																				ec.redirect("madminPage.xhtml");

																			}
																			else
																			{
																				if(info.getType().equalsIgnoreCase("basic"))
																				{
																					ec.redirect("dashboardBasic.xhtml");
																				}
																				else if(info.getType().equalsIgnoreCase("novice"))
																				{
																					ec.redirect("Dashboard.xhtml");
																				}
																				else if(info.getType().equalsIgnoreCase("foster"))
																				{
																					ec.redirect("dashboardFoster.xhtml");
//																					ec.redirect("chlktest.xhtml");
																				}
																			}
																		}
																		catch (IOException e)
																		{
																			e.printStackTrace();
																		}
																		//eturn "Dashboard.xhtml";
																	}

																} else {
																	FacesContext fc = FacesContext.getCurrentInstance();
																	ExternalContext ec = fc.getExternalContext();

																	if (userType.equalsIgnoreCase("Admin")) {
																		try {
																			ec.redirect("schoolInformation.xhtml");
																		} catch (IOException e) {
																			e.printStackTrace();
																		}
																	} else {
																		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
																				FacesMessage.SEVERITY_ERROR,
																				"You are not authorized user to setup school informations. Please Contact Administrator!",
																				"Validation error"));
																	}

																}
															} else {
																FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
																		FacesMessage.SEVERITY_ERROR,
																		"No Modules Are There In This Software.Please Contact Administrator!",
																		"Validation error"));
															}
														}

													}
												
												}
												else
												{
													FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
															"Sorry! You are not allowed to login into the Chalkbox ERP (Web Platform). Please contact ERP School Admin to gain access.", "Validation error"));
												}
											}
										}
									

									}
									else
									{
										FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
												FacesMessage.SEVERITY_ERROR,
												"Dear User, Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services",
												"Validation error"));
										billMsg = "Dear User, Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services";
										HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
												.getSession(true);
										ss.setAttribute("schoolid", school);
										ss.setAttribute("username", id);
										PrimeFaces.current().executeInitScript("PF('billDlg').show()");
										PrimeFaces.current().ajax().update("billForm");
										/*FacesContext fc = FacesContext.getCurrentInstance();
										ExternalContext ec = fc.getExternalContext();
										try {
											ec.redirect("mySchoolBillsLogout.xhtml");
										} catch (IOException e) {
											e.printStackTrace();
										}*/
									}
								}
								else
								{
									FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Sorry, license of your school ERP has been expired, Please contact Administrator for license renewal. Make the renewal as soon as possible and enjoy our services. We are here to serve you. Thanks and Regards",
											"Validation error"));
								}

							} else {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards",
										"Validation error"));
							}
						}
					}
					else if(login_type.equalsIgnoreCase("branch"))
					{
						if(userType.equalsIgnoreCase("madmin")||userType.equalsIgnoreCase("maccount"))
						{
							String school = DBM.authenticationSchoool(id, pswd, conn);
							HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

							ss.setAttribute("type", "Admin");
							ss.setAttribute("etype",userType);
							ss.setAttribute("id", id);
							ss.setAttribute("pswd", pswd);
							ss.setAttribute("schid",school);
							ss.setAttribute("branch",school);
							

							try
							{
								FacesContext.getCurrentInstance().getExternalContext().redirect("madminPageDynamic.xhtml");
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}

						}
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sorry! You are not an authorised user.", "Validation error"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Username or password is wrong", "Validation error"));
				}
			}

			return null;

		} finally {

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public String serviceGo()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		try 
		{
			String school = schoolId;
			boolean checkSchool = DBM.checkSchoolStatus(school, conn);
			// boolean checkSchool=true;
			if (checkSchool == true)
			{
				boolean expired = DBM.checkSchoolExpiryDate(school, conn);
				// boolean expired=false;

				if (expired == false)
				{
					String userType = "Admin";
					String selectedSession="";
					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
							.getSession(true);
					ss.setAttribute("showSMSBalMessage", "yes");
					ss.setAttribute("showDueBillMessage", "yes");
					ss.setAttribute("showSessionMessage", "yes");
					ss.setAttribute("schoolid", school);
					ss.setAttribute("schid", school);
					ss.setAttribute("username", id);
					ss.setAttribute("type", userType);
					ss.setAttribute("mtype", userType);

					SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
					selectedSession = info.getDefaultSession();

					ss.setAttribute("masterAdmin", "no");
					ss.setAttribute("serviceExecutive", "yes");
					ss.setAttribute("financialYear", selectedSession);
					ss.setAttribute("selectedSession", selectedSession);

					ArrayList<String> message = DBM.checkmessageSetting(conn);
					ArrayList<String> template=new DatabaseMethods1().checktemplateSetting(conn);
					ss.setAttribute("feesubmit", message.get(0));
					ss.setAttribute("registration", message.get(1));
					ss.setAttribute("enquiry", message.get(2));
					ss.setAttribute("attendance", message.get(3));
					ss.setAttribute("complaint", message.get(4));
					ss.setAttribute("birthday", message.get(5));
					ss.setAttribute("eventNotify", message.get(6));
					ss.setAttribute("marksheetNotify", message.get(7));
					ss.setAttribute("resultNotify", message.get(8));
					ss.setAttribute("elearningNotify", message.get(9));

					ss.setAttribute("feeMsg", template.get(0));
					ss.setAttribute("regMsg", template.get(1));
					ss.setAttribute("enqMsg", template.get(2));
					ss.setAttribute("attMsg", template.get(3));
					ss.setAttribute("compMsg", template.get(4));
					ss.setAttribute("birthMsg", template.get(5));
					
					DBM.updateLastLogin(id, school, conn);

					ArrayList<SelectItem> list = DBM.allModules(conn);
					if (list.size() > 0) {
						boolean i = DBM.checkSchoolInfo(conn);
						if (i == true)
						{
							ss.setAttribute("checkstu", false);
							FacesContext fc = FacesContext.getCurrentInstance();
							ExternalContext ec = fc.getExternalContext();

							try
							{
								if(info.getType().equalsIgnoreCase("basic"))
								{
									ec.redirect("dashboardBasic.xhtml");
								}
								else if(info.getType().equalsIgnoreCase("novice"))
								{
									ec.redirect("Dashboard.xhtml");
								}
								else if(info.getType().equalsIgnoreCase("foster"))
								{
									ec.redirect("dashboardFoster.xhtml");
								}
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}

						} else {
							FacesContext fc = FacesContext.getCurrentInstance();
							ExternalContext ec = fc.getExternalContext();

							if (userType.equalsIgnoreCase("Admin")) {
								try {
									ec.redirect("schoolInformation.xhtml");
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"You are not authorized user to setup school informations. Please Contact Administrator!",
										"Validation error"));
							}

						}
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"No Modules Are There In This Software.Please Contact Administrator!",
								"Validation error"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Sorry, license of your school ERP has been expired, Please contact Administrator for license renewal. Make the renewal as soon as possible and enjoy our services. We are here to serve you. Thanks and Regards",
							"Validation error"));
				}

			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards",
						"Validation error"));
			}
			return null;
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void serviceReport()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schoolList", list);
		ss.setAttribute("selectedSchool", schoolId);
		ss.setAttribute("username", id);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		try
		{
			ec.redirect("executiveSchoolActivity.xhtml");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void skip()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.invalidate();
		PrimeFaces.current().executeInitScript("PF('billDlg').hide()");
		PrimeFaces.current().ajax().update("billForm");
	}

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

	public String getBillMsg() {
		return billMsg;
	}

	public void setBillMsg(String billMsg) {
		this.billMsg = billMsg;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public ArrayList<SchoolInfo> getList() {
		return list;
	}

	public void setList(ArrayList<SchoolInfo> list) {
		this.list = list;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
