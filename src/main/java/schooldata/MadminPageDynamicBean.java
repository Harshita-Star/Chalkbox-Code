package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ManagedBean(name="madminDynamic")
@ViewScoped

public class MadminPageDynamicBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> branchList = new ArrayList<>();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	ArrayList<SchoolInfoList> schoolList;
	String id,userType,pswd,billMsg;

	public MadminPageDynamicBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String branchId=(String) ss.getAttribute("branch");
		id=(String) ss.getAttribute("id");
		userType=(String) ss.getAttribute("etype");
		pswd=(String) ss.getAttribute("pswd");
		schoolList=DBM.allSchoolListOfBranch(branchId, conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void goToWork()
	{
		Map<String,String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		Connection conn = DataBaseConnection.javaConnection();
		String school = params.get("action");;
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schoolid", school);
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		try
		{
			boolean checkSchool = DBM.checkSchoolStatus(school, conn);
			if (checkSchool == true)
			{
				boolean expired = DBM.checkSchoolExpiryDate(school, conn);
				if (expired == false)
				{
					boolean checkDue=DBM.checkSchoolBillDueDate(school,conn);
					if (checkDue == false)
					{
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

						ss.setAttribute("showSMSBalMessage", "yes");
						ss.setAttribute("showDueBillMessage", "yes");
						ss.setAttribute("showSessionMessage", "yes");
						ss.setAttribute("schoolid", school);
						ss.setAttribute("schid", school);
						ss.setAttribute("username", id);
						ss.setAttribute("type", userType);
						ss.setAttribute("mtype", userType);
						ss.setAttribute("masterAdmin", "no");
						ss.setAttribute("serviceExecutive", "no");
						
						SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
						selectedSession = info.getDefaultSession();

						ArrayList<String> message = DBM.checkmessageSetting(conn);
						ArrayList<String>template=new DatabaseMethods1().checktemplateSetting(conn);
						ss.setAttribute("selectedSession", selectedSession);
						ss.setAttribute("financialYear", selectedSession);
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

						if (userType.equals("module"))
						{
							ec.redirect("selectProjectModule.xhtml");
						}
						else
						{
							if (userType.equalsIgnoreCase("student"))
							{
								ss.setAttribute("checkstu", true);
								ec.redirect("homePage.xhtml");
							}
							else if (userType.equalsIgnoreCase("Teacher"))
							{
								ss.setAttribute("checkstu", true);
								ec.redirect("teacherHomePage.xhtml");
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

										ec.redirect("otpPage.xhtml");
									} // ***************************** OTP WORK FOR ADMIN ***********************
								}
								else {
									ArrayList<SelectItem> list = DBM.allModules(conn);
									if (list.size() > 0) {
										boolean i = DBM.checkSchoolInfo(conn);
										if (i == true) {

											ss.setAttribute("checkstu", false);

											if (userType.equalsIgnoreCase("Accounts")) {
												ec.redirect("dashboardAccount.xhtml");
											}
											else if (userType.equalsIgnoreCase("Transport Manager"))
											{
												ec.redirect("dashboardTransport.xhtml");
											}
											else if (userType.equalsIgnoreCase("Librarian"))
											{
												ec.redirect("dashboardLibrary.xhtml");
											}
											else if (userType.equalsIgnoreCase("Security"))
											{
												ec.redirect("dashboardSecurity.xhtml");
											}
											else if (userType.equalsIgnoreCase("Front Office"))
											{
												if(info.getBranch_id().equals("54"))
												{
													ec.redirect("dashboardFrontOfficeBlm.xhtml");
												}
												else
												{
													if(info.getClient_type().equalsIgnoreCase("school"))
													{
														ec.redirect("dashboardFrontOffice.xhtml");
													}
													else
													{
														ec.redirect("dashboardFrontOfficeIns.xhtml");
													}
												}

											}
											else if (userType.equalsIgnoreCase("Principal") || userType.equalsIgnoreCase("Vice Principal"))
											{
												ec.redirect("dashboardPrinciple.xhtml");
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
												}
											}

										} else {

											if (userType.equalsIgnoreCase("Admin"))
											{
												ec.redirect("schoolInformation.xhtml");
											}
											else
											{
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
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Dear User, Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services",
								"Validation error"));
						billMsg = "Dear User, Your licence invoice is overdue. Kindly pay at the earliest to avoid uninterrupted services";
						ss.setAttribute("schoolid", school);
						PrimeFaces.current().executeInitScript("PF('billDlg').show()");
						PrimeFaces.current().ajax().update("billForm");
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Sorry, license of your school ERP has been expired, Please contact Administrator for license renewal. Make the renewal as soon as possible and enjoy our services. We are here to serve you. Thanks and Regards",
							"Validation error"));
				}

			}
			else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards",
						"Validation error"));
			}


		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void logout()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("ChalkboxLogin.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	public ArrayList<SchoolInfoList> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SchoolInfoList> schoolList) {
		this.schoolList = schoolList;
	}
}
