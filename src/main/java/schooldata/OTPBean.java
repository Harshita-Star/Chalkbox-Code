package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="otpBean")
@ViewScoped

public class OTPBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String otp,randomOtp,userType;

	public OTPBean()
	{

	}

	public void resendOtp()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		randomOtp=(String)ss.getAttribute("adminOTP");
		String mob = (String)ss.getAttribute("adminMob");
		SchoolInfoList info=obj.fullSchoolInfo(conn);

		String typemessage="Hello User, "
				+String.valueOf(randomOtp)+ " is  your login OTP.Treat this as confidential.\nRegards\n"+info.getSmsSchoolName();

		if (mob.length() == 10
				&& !mob.equals("2222222222")
				&& !mob.equals("9999999999")
				&& !mob.equals("1111111111")
				&& !mob.equals("1234567890")
				&& !mob.equals("0123456789"))
		{
			obj.messageurlStaff(mob, typemessage,"admin",conn,obj.schoolId(),"");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent to Your Registered Mobile No. Please Enter OTP!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid Mobile No.!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String checkOTP()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		randomOtp=(String)ss.getAttribute("adminOTP");
		userType=(String)ss.getAttribute("type");

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		try
		{
			if(otp.equals(randomOtp))
			{
				SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
				ArrayList<SelectItem> list=DBM.allModules(conn);
				if(list.size()>0)
				{
					boolean i=DBM.checkSchoolInfo(conn);
					if(i==true)
					{
						String selectedSession="";
						/*Calendar now = Calendar.getInstance();
						int year = now.get(Calendar.YEAR);
						int month=now.get(Calendar.MONTH)+1;
						if(month>=4)
						{
							selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
						}
						else
						{
							selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
						}*/
						selectedSession = info.getDefaultSession();

						/*int n=DBM.getAllAttendance(new Date(),conn);
						if(n==0)
						{
							ArrayList<ClassInfo>classSection=DBM.allClassList(conn);

							for(ClassInfo ls:classSection)
							{
								for(Category ls1:ls.getCategoryList())
								{
									 DatabaseMethods1.attendanceSectionLogin(String.valueOf(ls1.getId()),ls1.getList(),new Date(),conn);
								}
							}

						}*/

						ArrayList<String>message=DBM.checkmessageSetting(conn);
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
						ss.setAttribute("checkstu", false);
						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();
						if(userType.equalsIgnoreCase("Accounts"))
						{
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
						else if (userType.equalsIgnoreCase("Librarian"))
						{
							try {
								ec.redirect("dashboardLibrary.xhtml");
							} catch (IOException e) {
								e.printStackTrace();
							}
							return "dashboardLibrary.xhtml";
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
						}
					}
					else
					{
						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();


						if(userType.equalsIgnoreCase("Admin"))
						{
							try {
								ec.redirect("schoolInformation.xhtml");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You are not authorized user to setup school informations. Please Contact Administrator!", "Validation error"));
						}
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Modules Are There In This Software.Please Contact Administrator!", "Validation error"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again!"));

			}
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";

	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getRandomOtp() {
		return randomOtp;
	}

	public void setRandomOtp(String randomOtp) {
		this.randomOtp = randomOtp;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
