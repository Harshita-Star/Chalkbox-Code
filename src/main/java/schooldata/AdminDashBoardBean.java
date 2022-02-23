package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.primefaces.PrimeFaces;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import session_work.DatabaseMethodSession;
import session_work.QueryConstants;
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="adminMain")
@ViewScoped
public class AdminDashBoardBean implements Serializable{

	public BarChartModel barModel;
	UploadedFile fileNew;
	String cunt,ticketType,userType,userid,description,billMsg,balMsg,addPage,sessMsg,oldSession,newSession;
	String newStudent,enquirystudent,errorLabel,preview,bdyPreview,leaveStudent,totalGallery;
	ArrayList<String>datalist;
	String countPendingLeave;
	boolean showClass,showStudent,showEmployee,showTransport=false,showFinance,showAttendence,showMessage,showPerformance,showTC,showPromotion,
			showPrevious,setting,report,showHomeWork,messageParents=false,messageStudents=false,showWishAll=false;
	String messageStduent,sendTo,typeMessage="",status="";
	SchoolInfoList ls;
	String name;
	Date d=new  Date();
	Date staffBirthDay;
	StudentInfo selectStudent;
	ArrayList<StudentInfo> studentList,birthdayStudentList,sList;
	ArrayList<EmployeeInfo> staffListBirthday;
	EmployeeInfo selectStaff;
	ArrayList<SelectItem> list;
	String totalCollection;
	String temp1,temp2,temp3,temp4,temp5,template="",birthdayWish="";
	ArrayList<Sum> sum;
	String timing,lang,smsSliderValue,smsCr,smsDr,schid;
	boolean showtiming,showEnglish,showHindi,notification,sms,disableNotify;
	double smsLimit;
	int smsNo;

	public AdminDashBoardBean()
	{
		showEnglish = true;
		showHindi = false;
		sms=true;
		notification=false;

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		
		schid=DBM.schoolId();
		ls=DBM.fullSchoolInfo(conn);
		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			addPage = "addStudentAe.xhtml";
		}
		else
		{
			addPage = "registration1.xhtml";
		}
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userid = (String) httpSession.getAttribute("username");
		userType = (String) httpSession.getAttribute("type");
		oldSession = (String) httpSession.getAttribute("selectedSession");
		ticketType="error";
		if(!ticketType.equals("Training"))
		{
			showtiming=false;
		}
		else
		{
			showtiming=true;
		}

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		if (month >= 4) {
			newSession = String.valueOf(year) + "-"
					+ String.valueOf(year + 1);
		} else {
			newSession = String.valueOf(year - 1) + "-"
					+ String.valueOf(year);
		}

		int selectedSessionYear = Integer.valueOf(oldSession.split("-")[0]);
		int actualSessionYear = Integer.valueOf(newSession.split("-")[0]);

		double cr = DBM.allSMSCredit(ls.getSchid(), DatabaseMethods1.selectedSessionDetails(ls.getSchid(),conn), conn);
		double dr = DBM.allSMSDebit(ls.getSchid(), DatabaseMethods1.selectedSessionDetails(ls.getSchid(),conn), conn);

		double per = (dr/cr)*100;
		if(per>100)
		{
			per=100;
		}
		smsSliderValue = String.valueOf((int)per);
		smsCr = new DecimalFormat("##").format(cr);
		smsDr = new DecimalFormat("##").format(dr);
		//(smsSliderValue);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		Date end = new Date();
		end.setDate(end.getDate()+7);
		String endDate = new SimpleDateFormat("yyyy-MM-dd").format(end);

		String showDueBill = (String) httpSession.getAttribute("showDueBillMessage");
		String showSMS = (String) httpSession.getAttribute("showSMSBalMessage");
		String showSession = (String) httpSession.getAttribute("showSessionMessage");

		if(showDueBill.equalsIgnoreCase("yes"))
		{
			boolean checkBill = new DataBaseMethodsReports().checkSchoolBillDueDays(strDate,endDate,ls.getSchid(),conn);

			if(checkBill==true)
			{
				billMsg = "Dear User,\n Due to overdue payment your services might be affected. "
						+ "Kindly make an immediate payment to avoid any inconvenience.";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('billDlg').show()");
					PrimeFaces.current().ajax().update("billForm");
				}
			}
			else
			{
				if(showSession.equalsIgnoreCase("yes"))
				{
					//if(!oldSession.equals(newSession))
					if(selectedSessionYear < actualSessionYear)
					{
						
						if(ls.getDefaultSessionPopup().equals("yes"))
						{
							sessMsg = "Dear User, You are still using the ERP in "+oldSession+" mode.";
							if (userType.equalsIgnoreCase("admin"))
							{
								PrimeFaces.current().executeInitScript("PF('sessionDlg').show()");
								PrimeFaces.current().ajax().update("sessionForm");
							}
								DBM.updateDefultSessionMessage("no",schid,conn);
						}
					}
					else
					{
						if(selectedSessionYear == actualSessionYear)
						{
							DBM.updateDefultSessionMessage("no",schid,conn);
							
						}
						
						if(ls.getCountry().equalsIgnoreCase("India"))
						{
							if(showSMS.equalsIgnoreCase("yes"))
							{
								smsBalCheck();
							}
						}
					}
					
				}
				else if(showSMS.equalsIgnoreCase("yes"))
				{
					if(ls.getCountry().equalsIgnoreCase("India"))
					{
						if(showSMS.equalsIgnoreCase("yes"))
						{
							smsBalCheck();
						}
					}
				}
			}
		}
		else if(showSession.equalsIgnoreCase("yes"))
		{
			//if(!oldSession.equals(newSession))
			if(selectedSessionYear < actualSessionYear)
			{
				if(ls.getDefaultSessionPopup().equalsIgnoreCase("yes"))
				{
				sessMsg = "Dear User, You are still using the ERP in "+oldSession+" mode.";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('sessionDlg').show()");
					PrimeFaces.current().ajax().update("sessionForm");
				}
				DBM.updateDefultSessionMessage("no", schid, conn);
				}
				
			}
			else
			{
				
				if(selectedSessionYear==actualSessionYear)
				{
					DBM.updateDefultSessionMessage("yes", schid, conn);
					
				}
				
				if(ls.getCountry().equalsIgnoreCase("India"))
				{
					if(showSMS.equalsIgnoreCase("yes"))
					{
						smsBalCheck();
					}
				}
			}
			
		}
		else
		{
			
			if(selectedSessionYear==actualSessionYear)
			{
				DBM.updateDefultSessionMessage("no", schid, conn);
			}
			
			if(ls.getCountry().equalsIgnoreCase("India"))
			{
				if(showSMS.equalsIgnoreCase("yes"))
				{
					smsBalCheck();
				}
			}
		}


		sendTo="student";

		datalist=new ArrayList<>();

		for(int i=0;i<5;i++)
		{
			String sss="";
			datalist.add(sss);

		}

		try
		{
			sum=DBM.getCollection(conn);
			if(sum.size()>0)
			{
				createBarModel();
				//show=true;
			}
		}
		catch(Exception ex)    {
			ex.printStackTrace();
		}


		totalGallery="0";
		if(ls.getType().equalsIgnoreCase("basic"))
		{
			totalGallery=DBM.countTotalGalley(conn);
		}

		List<String> list=new DatabaseMethodSession().noOfStudentInSession(conn);
		String p="0",np="0",total="0";
		for(String info:list)
		{
			if(info.contains("promotion"))
				p=info.substring(info.lastIndexOf("-")+1);
			if(info.contains("registration"))
				np=info.substring(info.lastIndexOf("-")+1);
			if(info.contains("total"))
				total=info.substring(info.lastIndexOf("-")+1);
			
		}
		if(new SideMenuBean().isRenderInSession()==false)
			cunt=total+"("+np+" NP ,"+p+" P)";
		else
			cunt=total;
		Date d=new  Date();
		int year1=d.getYear()+1900;
		String selectYear=String.valueOf(year1);
		enquirystudent=DBM.allNewStudentEnquirycount(selectYear,conn);
		leaveStudent=DBM.allLeaveStudent(selectYear,conn);
		messageStduent=DBM.todayMessage(DBM.schoolId(),conn);
		totalCollection=DBM.todaysCollection(DBM.schoolId(),"dashboard",conn);
		// countPendingLeave=String.valueOf(DBM.countPendingLeaveOfStudent(conn));
		birthday();
		staffBirthDay = new Date();
		staffListBirthday = DBM.allBirthdayListOfStaffnew(staffBirthDay,staffBirthDay, conn);
		
		createBarModel();
		temp1="Wishing You All Great Things In Life, Hope This Day Will Bring You An Extra Share Of All That Makes You Happiest. Happy Birthday!!";
		temp2="Wish You A Very Happy Birthday. May Life Lead You To Great Happiness, And Success. Enjoy Your Day!!";
		temp3="A Prayer To Bless You, A Wish To Lighten Your Moments, A Text To Say Happy Birthday!! God Bless U..";
		//temp4="Every B'day Presents A New Page In Ur Life, Keep Doing Good Things And Filling That Page With Good Deeds. Happy Birthday!!";
		temp4="Wish you all great things in life. May you live a long meaningful & blessful life.";
		
		lang = "english";

		httpSession.setAttribute("showSMSBalMessage", "no");
		httpSession.setAttribute("showDueBillMessage", "no");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void snooze()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(smsNo<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No. of SMS must be greater than zero"));
		}
		else
		{
			int i=new DatabaseMethods1().updateSMSLimit(DBM.schoolId(),smsNo,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SMS Alert Snoozed!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!"));
			}
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void smsBalCheck() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		smsNo=(int) smsLimit;
		double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
		// //("balance : "+balance);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlgSn').show()");
				PrimeFaces.current().ajax().update("MsgLimitFormSn");
			}
		}
		else if(balance <= 0)
		{
			balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
				PrimeFaces.current().ajax().update("MsgOverForm");
			}
			else
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

				PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
				PrimeFaces.current().ajax().update("MsgOtherForm");
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void skip()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("showDueBillMessage", "no");

		Connection conn = DataBaseConnection.javaConnection();
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		smsNo=(int) smsLimit;
		double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
		////("balance : "+balance);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlgSn').show()");
				PrimeFaces.current().ajax().update("MsgLimitFormSn");
			}
		}
		else if(balance <= 0)
		{
			balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
				PrimeFaces.current().ajax().update("MsgOverForm");
			}
			else
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

				PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
				PrimeFaces.current().ajax().update("MsgOtherForm");
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void continueOld()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("showSessionMessage", "no");
		
		PrimeFaces.current().executeInitScript("PF('sessionDlg').hide()");
		PrimeFaces.current().ajax().update("sessionForm");
		
		smsBalCheck();
	}
	
	public void continueNew() throws IOException 
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("showSessionMessage", "no");
		ss.setAttribute("sessionPage", "setDefaultSession");
		
		FacesContext.getCurrentInstance().getExternalContext().redirect("defaultSession.xhtml");
	}

	public void myBills() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("showDueBillMessage", "no");
		FacesContext.getCurrentInstance().getExternalContext().redirect("mySchoolBills.xhtml");
	}

	public void goToAllStudent() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("studentListPage", "dashboard");

		FacesContext.getCurrentInstance().getExternalContext().redirect("allStudentList.xhtml");

	}

	public void ticketCheck()
	{
		if(!ticketType.equals("Training"))
		{
			showtiming=false;
		}
		else
		{
			showtiming=true;
		}
	}

	public void checkLanguage()
	{
		typeMessage = "";
		if(lang.equalsIgnoreCase("english"))
		{
			showEnglish = true;
			showHindi = false;
		}
		else
		{
			showEnglish = false;
			showHindi = true;
			
			//lang = "english";
			//PrimeFaces.current().executeInitScript("PF('hindiDi').show();");
			//PrimeFaces.current().ajax().update("translator");
			//PrimeFaces.current().executeInitScript("PF('translateDlg').show();");

		}
	}

	public void checkCommType()
	{

		if(sendTo.equalsIgnoreCase("staff"))
		{
			notification=false;
			disableNotify=true;
		}
		else
		{
			disableNotify=false;
		}
	}

	public String sendMessageToAll()
	{
		if(sms==false && notification==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
			return "";
		}

		Connection conn=DataBaseConnection.javaConnection();
		if(ls.getCountry().equalsIgnoreCase("India") && sms==true)
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");

					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}

			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				}
				else
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}

		messageParents=messageStudents=false;

		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		if(typeMessage==null || typeMessage.equals(""))
		{
			errorLabel="Please Wrtie a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			if(sendTo.equals("student"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					PrimeFaces.current().executeInitScript("PF('msgDialog').show()");
					PrimeFaces.current().ajax().update("msgForm");
				}
				else
				{
					schoolPreview();
				}
			}
			else
			{
				staffPreview();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void sendMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		messageParents=messageStudents=false;

		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		if(typeMessage==null || typeMessage.equals(""))
		{
			errorLabel="Please Wrtie a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			if(sendTo.equals("student"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					PrimeFaces.current().executeInitScript("PF('msgDialog').show()");
					PrimeFaces.current().ajax().update("msgForm");
				}
				else
				{
					schoolPreview();
				}
			}
			else
			{
				staffPreview();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void institutePreview()
	{
		if(messageParents==false && messageStudents==false)
		{
			errorLabel="Please Select Receivers : Parents/Students";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(messageParents==true && messageStudents==false)
		{
			preview="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			if(lang.equalsIgnoreCase("hindi"))
			{
				preview="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			}

			PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
			PrimeFaces.current().ajax().update("prevForm");
		}
		else if(messageParents==false && messageStudents==true)
		{
			preview="Dear Student,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			if(lang.equalsIgnoreCase("hindi"))
			{
				preview="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			}
			PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
			PrimeFaces.current().ajax().update("prevForm");
		}
		else if(messageParents==true && messageStudents==true)
		{
			preview="Dear Parent/Student,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			if(lang.equalsIgnoreCase("hindi"))
			{
				preview="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			}
			PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
			PrimeFaces.current().ajax().update("prevForm");
		}


	}

	public void schoolPreview()
	{
		preview="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
		if(lang.equalsIgnoreCase("hindi"))
		{
			preview="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
		}
		PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
		PrimeFaces.current().ajax().update("prevForm");
	}

	public void staffPreview()
	{
		preview="Dear Staff Member,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
		if(lang.equalsIgnoreCase("hindi"))
		{
			preview="प्रिय कर्मचारी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
		}
		PrimeFaces.current().executeInitScript("PF('prevDialog').show()");
		PrimeFaces.current().ajax().update("prevForm");
	}

	public void sendMessageNow() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			if(sendTo.equals("student"))
			{

				String ctype=obj.checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					PrimeFaces.current().executeInitScript("PF('prevDialog').hide()");
					PrimeFaces.current().ajax().update("prevForm");
					sendMessageInstitute();
				}
				else
				{
					PrimeFaces.current().executeInitScript("PF('prevDialog').hide()");
					PrimeFaces.current().ajax().update("prevForm");
					sendMessageSchool();
				}
			}
			else
			{
				PrimeFaces.current().executeInitScript("PF('prevDialog').hide()");
				PrimeFaces.current().ajax().update("prevForm");

				ArrayList<EmployeInfo> eList=obj.allEmployees(conn);
				if(eList.isEmpty())
				{
					errorLabel="No Staff Member Found.";
					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					PrimeFaces.current().ajax().update("errorForm");
				}
				else
				{
					String employeeContact="";
					String employeeNumber="";
					if(ls.getCountry().equalsIgnoreCase("India"))
					{
						for(EmployeInfo ee : eList)
						{
							if(String.valueOf(ee.getMobile()).length()==10
									&& !String.valueOf(ee.getMobile()).equals("2222222222")
									&& !String.valueOf(ee.getMobile()).equals("9999999999")
									&& !String.valueOf(ee.getMobile()).equals("1111111111")
									&& !String.valueOf(ee.getMobile()).equals("1234567890")
									&& !String.valueOf(ee.getMobile()).equals("0123456789"))
							{
								if(employeeContact.equals(""))
								{
									employeeContact=String.valueOf(ee.getMobile());
									employeeNumber=String.valueOf(ee.getName())+"@CB@"+ee.getId();
								}
								else
								{
									employeeContact=employeeContact+","+String.valueOf(ee.getMobile());
									employeeNumber=employeeNumber+","+String.valueOf(ee.getName())+"@CB@"+ee.getId();
								}
							}
						}
					}


					////(employeeContact.split(",").length);
					String msg="Dear Staff Member,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
					String tp = typeMessage;
					if(sms)
					{
						if(!ls.getCountry().equalsIgnoreCase("India"))
						{
							Runnable r = new Runnable()
							{
								public void run()
								{
									String heading = "<center class=\"red\">Message From "+ls.getSchoolName()+"!</center>";
									String subject = "Message From "+ls.getSchoolName();
									String msg="<center>Dear Staff Member,<br></br>"+tp+"<br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
									for(EmployeInfo ee : eList)
									{
										if(ee.getUname()!=null && !ee.getUname().equalsIgnoreCase("") && !ee.getUname().equalsIgnoreCase("null"))
										{
											new DataBaseOnlineAdm().doMail(ee.getUname(), subject, heading, msg);
										}
									}
								}
							};
							new Thread(r).start();
						}
						else
						{
							if(lang.equalsIgnoreCase("hindi"))
							{
								msg="प्रिय कर्मचारी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
								obj.messageurlStaffHindi(employeeContact, msg,employeeNumber,conn,"");
							}
							else
							{
								obj.messageurlStaff(employeeContact, msg,employeeNumber,conn,obj.schoolId(),"");
							}
						}
					}

					if(notification)
					{
						for(EmployeInfo ee : eList)
						{
							obj.adminnotification("Message", msg, "staff-"+ee.getId()+"-"+obj.schoolId(),"StaffNotification-"+ee.getId(), conn);
						}
					}

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Message Sent Successfully."));
					typeMessage="";
					errorLabel="Message Sent Successfully.";
					//					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					//					PrimeFaces.current().ajax().update("errorForm");
					PrimeFaces.current().ajax().update("mainForm");


					ExternalContext ec = fc.getExternalContext();

					try
					{
						if(ls.getType().equalsIgnoreCase("basic"))
						{
							ec.redirect("dashboardBasic.xhtml");
						}
						else if(ls.getType().equalsIgnoreCase("novice"))
						{
							ec.redirect("Dashboard.xhtml");
						}
						else if(ls.getType().equalsIgnoreCase("foster"))
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
		}
		catch (Exception e) {
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


	public void sendMessageSchool() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		try
		{
			if(sms)
			{
				sList=DBM.allStudentList(conn);
				ls=DBM.fullSchoolInfo(conn);

				if(sList.isEmpty())
				{
					errorLabel="No Student Found.";
					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					PrimeFaces.current().ajax().update("errorForm");
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Student Found", "Validation Error"));
				}
				else
				{
					String contactNumber="";
					String addNumber="";
					if(ls.getCountry().equalsIgnoreCase("India"))
					{
						for(StudentInfo info : sList)
						{
							if(String.valueOf(info.getFathersPhone()).length()==10
									&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
							{
								if(contactNumber.equals(""))
								{
									contactNumber=String.valueOf(info.getFathersPhone());
									addNumber=info.getAddNumber();
								}
								else
								{
									if (!contactNumber.contains(String.valueOf(info
											.getFathersPhone()))) {
									contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
									addNumber=addNumber+","+info.getAddNumber();
									}
								}

							}

						}
					}

					////(contactNumber.split(",").length);
					String msg="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					String tp = typeMessage;
					if(!ls.getCountry().equalsIgnoreCase("India"))
					{
						Runnable r = new Runnable()
						{
							public void run()
							{

								String heading = "<center class=\"red\">Message From "+ls.getSchoolName()+"!</center>";
								String subject = "Message From "+ls.getSchoolName();
								String msg="<center>Dear Parent,<br></br>"+tp+" <br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
								for(StudentInfo info : sList)
								{
									if(info.getActionBy().equalsIgnoreCase("father"))
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									}
									else if(info.getActionBy().equalsIgnoreCase("mother"))
									{
										new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
									}
									else if(info.getActionBy().equalsIgnoreCase("both"))
									{
										if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
										{
											new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
										}
										else
										{
											new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
											new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
										}
									}
									else
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									}
								}
							}
						};
						new Thread(r).start();
					}
					else
					{
						if(lang.equalsIgnoreCase("hindi"))
						{
							msg="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
							//(msg.length());
							DBM.messageurlHindi(contactNumber, msg,addNumber,conn,"");
						}
						else
						{
							DBM.messageurl1(contactNumber, msg,addNumber,conn,DBM.schoolId(),"");
						}
					}
				}
			}

			if(notification)
			{
				ls=DBM.fullSchoolInfo(conn);
				String msg="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
				if(lang.equalsIgnoreCase("hindi"))
				{
					msg="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
					//(msg.length());
				}

				DBM.notification(DBM.schoolId(),"Message",msg,DBM.schoolId(),conn);
			}




			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Message Sent Successfully"));


			errorLabel="Message Sent Successfully.";
			typeMessage="";
			//PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			//PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("mainForm");
			ExternalContext ec = fc.getExternalContext();

			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void sendMessageInstitute() throws IOException
	{

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		//int x=1;
		try
		{
			if(sms)
			{
				sList=DBM.allStudentList(conn);
				ls=DBM.fullSchoolInfo(conn);
				if(messageParents==false && messageStudents==false)
				{
					errorLabel="Please Select Receivers : Parents/Students";
					PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
					PrimeFaces.current().ajax().update("errorForm");
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Receivers : Parents/Students"));
				}
				else
				{
					String contactNumber="";
					String addNumber="";

					for(StudentInfo info : sList)
					{
						if(messageParents==true)
						{
							if(String.valueOf(info.getFathersPhone()).length()==10
									&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
							{
								if(contactNumber.equals(""))
								{
									contactNumber=String.valueOf(info.getFathersPhone());
									addNumber=info.getAddNumber();
								}
								else
								{
									if (!contactNumber.contains(String.valueOf(info
											.getFathersPhone()))) {
									contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
									addNumber=addNumber+","+info.getAddNumber();
									}
								}

							}
						}

						if(messageStudents==true)
						{
							if(info.getStudentPhone().length()==10
									&& !info.getStudentPhone().equals("2222222222")
									&& !info.getStudentPhone().equals("9999999999")
									&& !info.getStudentPhone().equals("1111111111")
									&& !info.getStudentPhone().equals("1234567890")
									&& !info.getStudentPhone().equals("0123456789"))
							{
								if(contactNumber.equals(""))
								{
									contactNumber=info.getStudentPhone();
									addNumber=String.valueOf(info.getAddNumber());
								}
								else
								{
									contactNumber=contactNumber+","+info.getStudentPhone();
									addNumber=addNumber+","+String.valueOf(info.getAddNumber());
								}
							}
						}


					}

					String messageTemp="";
					if(messageParents==true && messageStudents==false)
					{
						messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(lang.equalsIgnoreCase("hindi"))
						{
							messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}
					else if(messageParents==false && messageStudents==true)
					{
						messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(lang.equalsIgnoreCase("hindi"))
						{
							messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}
					else if(messageParents==true && messageStudents==true)
					{
						messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(lang.equalsIgnoreCase("hindi"))
						{
							messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}

					//			//(addNumber);
					//			//(contactNumber.split(",").length);
					if(lang.equalsIgnoreCase("hindi"))
					{
						DBM.messageurlHindi(contactNumber, messageTemp,addNumber,conn,"");
					}
					else
					{
						DBM.messageurl1(contactNumber, messageTemp,addNumber,conn,DBM.schoolId(),"");
					}
				}
			}

			if(notification)
			{
				ls=DBM.fullSchoolInfo(conn);
				String messageTemp="";
				if(messageParents==true && messageStudents==false)
				{
					messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(lang.equalsIgnoreCase("hindi"))
					{
						messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}
				else if(messageParents==false && messageStudents==true)
				{
					messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(lang.equalsIgnoreCase("hindi"))
					{
						messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}
				else if(messageParents==true && messageStudents==true)
				{
					messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(lang.equalsIgnoreCase("hindi"))
					{
						messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}

				DBM.notification(DBM.schoolId(),"Message",messageTemp,DBM.schoolId(),conn);
			}



			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Message Sent Successfully."));

			errorLabel="Message Sent Successfully.";
			messageStudents=messageParents=false;
			typeMessage="";
			//				PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			//				PrimeFaces.current().ajax().update("errorForm");
			//
			//				PrimeFaces.current().executeInitScript("PF('msgDialog').hide()");
			//				PrimeFaces.current().ajax().update("msgForm");
			PrimeFaces.current().ajax().update("mainForm");
			ExternalContext ec = fc.getExternalContext();

			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		catch (Exception e) {
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

	public void selectStudentMsz()
	{
		status="individual";
		template=birthdayWish="";
		/*addNumber=selectStudent.getAddNumber();
        contactNumber=String.valueOf(selectStudent.getFathersPhone());
		 */////(selectStudent.getAddNumber()+status+addNumber);
	}
	
	public void selectStaffMsz()
	{
		status="individual";
		template=birthdayWish="";
		/*addNumber=selectStudent.getAddNumber();
        contactNumber=String.valueOf(selectStudent.getFathersPhone());
		 */////(selectStudent.getAddNumber()+status+addNumber);
	}

	public void allStudentMsz()
	{

		status="all";
		template=birthdayWish="";
		if(!birthdayStudentList.isEmpty())
		{
			PrimeFaces.current().executeInitScript("PF('birthdayDialog').show()");
			PrimeFaces.current().ajax().update(":birthdayForm");
		}
		else
		{
			errorLabel="No Students to Wish.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		////(status);

	}

	public void allStaffMsz()
	{

		status="all";
		template=birthdayWish="";
		if(!staffListBirthday.isEmpty())
		{
			PrimeFaces.current().executeInitScript("PF('birthdayDialogStaff').show()");
			PrimeFaces.current().ajax().update(":birthdayFormStaff");
		}
		else
		{
			errorLabel="No Staff to Wish.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		////(status);

	}
	
	public void birthday()
	{
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<String> list=new DataBaseMethodStudent().birthdayFieldList();
		String session=new DatabaseMethods1().selectedSessionDetails(schid, conn);
		birthdayStudentList=new DataBaseMethodStudent().studentDetail("","","",QueryConstants.IN_SCHOOL,QueryConstants.BIRTHDAY,d,d,"","","","", session, schid, list, conn);


		if(birthdayStudentList.isEmpty())
		{
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			showWishAll=false;
		}
		else
		{
			showWishAll=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String wishPreview()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(ls.getCountry().equalsIgnoreCase("India"))
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg1').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm1");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				}
				else
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}

		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+template+"\nThanks,\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+"\nThanks,\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+"\nThanks,\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}

		return "";

	}
	
	public String wishPreviewStaff()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(ls.getCountry().equalsIgnoreCase("India"))
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg1').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm1");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				}
				else
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}
         
	
		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+template+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialogStaff').show()");
			PrimeFaces.current().ajax().update("bdyPrevFormStaff");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+" Regards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialogStaff').show()");
			PrimeFaces.current().ajax().update("bdyPrevFormStaff");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+" Regards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialogStaff').show()");
			PrimeFaces.current().ajax().update("bdyPrevFormStaff");
		}

		return "";

	}

	public void allBdMsg()
	{
		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+template+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+" Regards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+" Regards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
	}

	public void sendWish() throws IOException
	{

		PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevForm");
		DatabaseMethods1 DBM = new DatabaseMethods1();

		String message="",contactNumber="",addNumber="";

		Connection conn=DataBaseConnection.javaConnection();
		FacesContext fc = FacesContext.getCurrentInstance();
		//ls=new DatabaseMethods1().fullSchoolInfo(conn);

		if(ls.getCountry().equalsIgnoreCase("India"))
		{
			if(status.equalsIgnoreCase("all"))
			{
				for(StudentInfo info:birthdayStudentList)
				{
					if(String.valueOf(info.getFathersPhone()).length()==10
							&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
							&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
							&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
							&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
							&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
					{
						if(contactNumber.equals(""))
						{
							contactNumber=String.valueOf(info.getFathersPhone());
							addNumber=info.getAddNumber();
						}
						else
						{
							contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
							addNumber=addNumber+","+info.getAddNumber();
						}

					}
				}
			}
			else
			{
				contactNumber=String.valueOf(selectStudent.getFathersPhone());
				addNumber=selectStudent.getAddNumber();

			}
		}

		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Student,\n"+template+"\nRegards,\n"+ls.getSmsSchoolName();
			String tp = template;
			if(!ls.getCountry().equalsIgnoreCase("India"))
			{
				Runnable r = new Runnable()
				{
					public void run()
					{
						//ArrayList<StudentInfo> bdyList = birthdayStudentList;
						String subject = "Message From "+ls.getSchoolName();
						String msg="<center>Dear Student,<br></br>"+tp+"<br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
						if(status.equalsIgnoreCase("all"))
						{
							String heading = "";
							for(StudentInfo info : birthdayStudentList)
							{
								heading = "<center class=\"red\">Happy Birthday "+info.getFname()+"!</center>";

								if(info.getActionBy().equalsIgnoreCase("father"))
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("mother"))
								{
									new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("both"))
								{
									if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									}
									else
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
										new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
									}
								}
								else
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
							}
						}
						else
						{
							String heading = "<center class=\"red\">Happy Birthday "+selectStudent.getFname()+"!</center>";

							if(selectStudent.getActionBy().equalsIgnoreCase("father"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("mother"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("both"))
							{
								if(selectStudent.getFatherEmail().equalsIgnoreCase(selectStudent.getMotherEmail()))
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
								}
								else
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
									new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
								}
							}
							else
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
						}

					}
				};
				new Thread(r).start();
			}
			else
			{
				DBM.messageurl1(contactNumber,message,addNumber,conn,DBM.schoolId(),"");
			}

			//template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			//FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
			ExternalContext ec = fc.getExternalContext();

			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			String tp = birthdayWish;
			if(!ls.getCountry().equalsIgnoreCase("India"))
			{
				Runnable r = new Runnable()
				{
					public void run()
					{
						//ArrayList<StudentInfo> bdyList = birthdayStudentList;
						String subject = "Message From "+ls.getSchoolName();
						String msg="<center>Dear Student,<br></br>"+tp+"<br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
						if(status.equalsIgnoreCase("all"))
						{
							String heading = "";
							for(StudentInfo info : birthdayStudentList)
							{
								heading = "<center class=\"red\">Happy Birthday "+info.getFname()+"!</center>";

								if(info.getActionBy().equalsIgnoreCase("father"))
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("mother"))
								{
									new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("both"))
								{
									if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									}
									else
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
										new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
									}
								}
								else
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}

							}
						}
						else
						{
							String heading = "<center class=\"red\">Happy Birthday "+selectStudent.getFname()+"!</center>";

							if(selectStudent.getActionBy().equalsIgnoreCase("father"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("mother"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("both"))
							{
								if(selectStudent.getFatherEmail().equalsIgnoreCase(selectStudent.getMotherEmail()))
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
								}
								else
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
									new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
								}
							}
							else
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
						}

					}
				};
				new Thread(r).start();
			}
			else
			{
				DBM.messageurl1(contactNumber,message,addNumber,conn,DBM.schoolId(),"");
			}

			//template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			//FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
			ExternalContext ec = fc.getExternalContext();

			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Student,\n"+birthdayWish+"\n Regards,\n"+ls.getSmsSchoolName();
			String tp = birthdayWish;
			if(!ls.getCountry().equalsIgnoreCase("India"))
			{
				Runnable r = new Runnable()
				{
					public void run()
					{
						//ArrayList<StudentInfo> bdyList = birthdayStudentList;
						String subject = "Message From "+ls.getSchoolName();
						String msg="<center>Dear Student,<br></br>"+tp+"<br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
						if(status.equalsIgnoreCase("all"))
						{
							String heading = "";
							for(StudentInfo info : birthdayStudentList)
							{
								heading = "<center class=\"red\">Happy Birthday "+info.getFname()+"!</center>";

								if(info.getActionBy().equalsIgnoreCase("father"))
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("mother"))
								{
									new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
								}
								else if(info.getActionBy().equalsIgnoreCase("both"))
								{
									if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									}
									else
									{
										new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
										new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
									}
								}
								else
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
							}
						}
						else
						{
							String heading = "<center class=\"red\">Happy Birthday "+selectStudent.getFname()+"!</center>";

							if(selectStudent.getActionBy().equalsIgnoreCase("father"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("mother"))
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
							}
							else if(selectStudent.getActionBy().equalsIgnoreCase("both"))
							{
								if(selectStudent.getFatherEmail().equalsIgnoreCase(selectStudent.getMotherEmail()))
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
								}
								else
								{
									new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
									new DataBaseOnlineAdm().doMail(selectStudent.getMotherEmail(), subject, heading, msg);
								}
							}
							else
							{
								new DataBaseOnlineAdm().doMail(selectStudent.getFatherEmail(), subject, heading, msg);
							}
						}

					}
				};
				new Thread(r).start();
			}
			else
			{
				DBM.messageurl1(contactNumber,message,addNumber,conn,DBM.schoolId(),"");
			}

			//template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			//FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
			ExternalContext ec = fc.getExternalContext();

			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		//(contactNumber);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendWishStaff() throws IOException
	{
		PrimeFaces.current().executeInitScript("PF('bdyPrevDialogStaff').hide()");
		PrimeFaces.current().ajax().update("bdyPrevFormStaff");
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		FacesContext fc = FacesContext.getCurrentInstance();
		
		SchoolInfoList ls = DBM.fullSchoolInfo(conn);
	
		String schoolName = ls.getSmsSchoolName();
		
	

		String message="",contactNumber="",addNumber="";

		String id="";
		
		if (status.equalsIgnoreCase("all"))
		{
			for (EmployeeInfo info : staffListBirthday)
			{
				id = info.getFname() + "@CB@" + String.valueOf(info.getId());
				if (String.valueOf(info.getMobile()).length() == 10
						&& !String.valueOf(info.getMobile()).equals("2222222222")
						&& !String.valueOf(info.getMobile()).equals("9999999999")
						&& !String.valueOf(info.getMobile()).equals("1111111111")
						&& !String.valueOf(info.getMobile()).equals("1234567890")
						&& !String.valueOf(info.getMobile()).equals("0123456789"))
				{
					name=info.getFname();
					
					if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
					{
						message = "Dear " + name + ",\n" + template + "\n Regards, " + schoolName;
					}
					else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
					{
						message = "Dear " + name + ",\n" + birthdayWish + "\n Regards, " + schoolName;
					}
					else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
					{
						message = "Dear " + name + ",\n" + birthdayWish + "\n Regards, " + schoolName;
					}
					
					DBM.messageurlStaff(String.valueOf(info.getMobile()), message, id, conn,DBM.schoolId(),"");
				}
			}
		}
		else
		{
			id = selectStaff.getFname()+"@CB@"+selectStaff.getId();
			name = selectStaff.getFname();
			contactNumber = String.valueOf(selectStaff.getMobile());
			if (contactNumber.length() == 10
					&& !contactNumber.equals("2222222222")
					&& !contactNumber.equals("9999999999")
					&& !contactNumber.equals("1111111111")
					&& !contactNumber.equals("1234567890")
					&& !contactNumber.equals("0123456789"))
			{
				if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
				{
					message = "Dear " + name + ",\n" + template + "\n Regards, " + schoolName;
				}
				else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
				{
					message = "Dear " + name + ",\n" + birthdayWish + "\n Regards, " + schoolName;
				}
				else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
				{
					message = "Dear " + name + ",\n" + birthdayWish + "\n Regards, " + schoolName;
				}
				DBM.messageurlStaff(contactNumber, message, id, conn,DBM.schoolId(),"");
			}

		}

		if (template.isEmpty() && birthdayWish.isEmpty()) {
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			errorLabel="Message Sent";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayFormStaff");
			ExternalContext ec = fc.getExternalContext();
			try
			{
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(ls.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public void addTicket()
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userid = (String) httpSession.getAttribute("username");
		userType = (String) httpSession.getAttribute("type");
		String schoolId = DBM.schoolId();

		String photo = "";
		if (fileNew !=null && fileNew.getSize()>0)
		{
			int rendomNumber=(int)(Math.random()*9000)+1000;
			photo = "scrnshot"+String.valueOf(rendomNumber)+"_"+schoolId+"ticket.jpg";
			DBM.makeProfileSchid(DBM.schoolId(),fileNew, photo, conn);
		}

		Date dt=new Date();

		int n = (int) (100000 + Math.random() * 900000);
		String ticketid="CB"+String.valueOf(n);
		//(ticketType);
		if(ticketType.equals("Training"))
		{
			description="Time -: "+timing+"\n"+description;
		}

		int i=DBJ.addTicketRaising(userid,schoolId,ticketType,description,dt,userType,photo,ticketid,conn);
		new DatabaseMethods1().contactNo("Developers",conn);
		new JSONObject();
		if(i>0)
		{
			errorLabel="Ticket Added Successfully.Your Ticket No is "+ticketid;
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");

			String schoolName=new DatabaseMethods1().getSMSSchoolName(schoolId, conn);
			//String message="A Ticket Has Been Raised From "+schoolName+" Ticket No "+ticketid;
   	 		//DBJ.devnotification("Ticket Raised", message,"cb_dev",schoolId,conn);
			ticketType="error";
			description="";
			fileNew=null;
			timing="09:30 AM";
			if(!ticketType.equals("Training"))
			{
				showtiming=false;
			}
			else
			{
				showtiming=true;
			}
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void createBarModel() {
		barModel = initBarModel();


		barModel.setLegendPosition("ne");


		Axis xAxis = barModel.getAxis(AxisType.X);
		xAxis.setLabel("Date");

		Axis yAxis = barModel.getAxis(AxisType.Y);
		yAxis.setLabel("Fees");
		yAxis.setMin(0);
		int max=0;

		for(Sum ss:sum)
		{

			if(max<Integer.parseInt(ss.getCollection()))
			{
				max=Integer.parseInt(ss.getCollection());
			}

		}

		yAxis.setMax(max+500);
	}

	public BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();

		ChartSeries boys = new ChartSeries();
		boys.setLabel("Collection");


		for(Sum ss:sum)
		{


			boys.set(ss.getDate(),Integer.parseInt(ss.getCollection()));

		}
		model.addSeries(boys);


		return model;
	}

	public void navigate()
	{
		Connection conn = DataBaseConnection.javaConnection();

		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		if(info.getCountry().equalsIgnoreCase("UAE"))
		{
			try
			{
				ec.redirect("addStudentAe.xhtml");
			}
			catch (IOException e)
			{
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
		else
		{
			try
			{
				ec.redirect("registration1.xhtml");
			}
			catch (IOException e)
			{
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


	}

	public String getNewStudent() {
		return newStudent;
	}

	public void setNewStudent(String newStudent) {
		this.newStudent = newStudent;
	}

	public String getCunt() {
		return cunt;
	}

	public void setCunt(String cunt) {
		this.cunt = cunt;
	}

	public String getEnquirystudent() {
		return enquirystudent;
	}

	public void setEnquirystudent(String enquirystudent) {
		this.enquirystudent = enquirystudent;
	}


	public ArrayList<String> getDatalist() {
		return datalist;
	}


	public void setDatalist(ArrayList<String> datalist) {
		this.datalist = datalist;
	}


	public String getMessageStduent() {
		return messageStduent;
	}


	public void setMessageStduent(String messageStduent) {
		this.messageStduent = messageStduent;
	}


	public String getTotalCollection() {
		return totalCollection;
	}


	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public String getCountPendingLeave() {
		return countPendingLeave;
	}

	public void setCountPendingLeave(String countPendingLeave) {
		this.countPendingLeave = countPendingLeave;
	}


	public StudentInfo getSelectStudent() {
		return selectStudent;
	}


	public void setSelectStudent(StudentInfo selectStudent) {
		this.selectStudent = selectStudent;
	}


	public ArrayList<StudentInfo> getBirthdayStudentList() {
		return birthdayStudentList;
	}


	public void setBirthdayStudentList(ArrayList<StudentInfo> birthdayStudentList) {
		this.birthdayStudentList = birthdayStudentList;
	}
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getTypeMessage() {
		return typeMessage;
	}

	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}

	public boolean isMessageParents() {
		return messageParents;
	}

	public void setMessageParents(boolean messageParents) {
		this.messageParents = messageParents;
	}

	public boolean isMessageStudents() {
		return messageStudents;
	}

	public void setMessageStudents(boolean messageStudents) {
		this.messageStudents = messageStudents;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getTemp1() {
		return temp1;
	}

	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}

	public String getTemp2() {
		return temp2;
	}

	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}

	public String getTemp3() {
		return temp3;
	}

	public void setTemp3(String temp3) {
		this.temp3 = temp3;
	}

	public String getTemp4() {
		return temp4;
	}

	public void setTemp4(String temp4) {
		this.temp4 = temp4;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getBirthdayWish() {
		return birthdayWish;
	}

	public void setBirthdayWish(String birthdayWish) {
		this.birthdayWish = birthdayWish;
	}

	public String getBdyPreview() {
		return bdyPreview;
	}

	public void setBdyPreview(String bdyPreview) {
		this.bdyPreview = bdyPreview;
	}

	public String getLeaveStudent() {
		return leaveStudent;
	}

	public void setLeaveStudent(String leaveStudent) {
		this.leaveStudent = leaveStudent;
	}

	public boolean isShowWishAll() {
		return showWishAll;
	}

	public void setShowWishAll(boolean showWishAll) {
		this.showWishAll = showWishAll;
	}

	public String getTotalGallery() {
		return totalGallery;
	}

	public void setTotalGallery(String totalGallery) {
		this.totalGallery = totalGallery;
	}

	public UploadedFile getFileNew() {
		return fileNew;
	}

	public void setFileNew(UploadedFile fileNew) {
		this.fileNew = fileNew;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTiming() {
		return timing;
	}

	public void setTiming(String timing) {
		this.timing = timing;
	}

	public boolean isShowtiming() {
		return showtiming;
	}

	public void setShowtiming(boolean showtiming) {
		this.showtiming = showtiming;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isShowEnglish() {
		return showEnglish;
	}

	public void setShowEnglish(boolean showEnglish) {
		this.showEnglish = showEnglish;
	}

	public boolean isShowHindi() {
		return showHindi;
	}

	public void setShowHindi(boolean showHindi) {
		this.showHindi = showHindi;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}

	public boolean isDisableNotify() {
		return disableNotify;
	}

	public void setDisableNotify(boolean disableNotify) {
		this.disableNotify = disableNotify;
	}

	public String getBillMsg() {
		return billMsg;
	}

	public void setBillMsg(String billMsg) {
		this.billMsg = billMsg;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public int getSmsNo() {
		return smsNo;
	}

	public void setSmsNo(int smsNo) {
		this.smsNo = smsNo;
	}

	public String getSmsSliderValue() {
		return smsSliderValue;
	}

	public void setSmsSliderValue(String smsSliderValue) {
		this.smsSliderValue = smsSliderValue;
	}

	public String getSmsCr() {
		return smsCr;
	}

	public void setSmsCr(String smsCr) {
		this.smsCr = smsCr;
	}

	public String getSmsDr() {
		return smsDr;
	}

	public void setSmsDr(String smsDr) {
		this.smsDr = smsDr;
	}

	public String getTemp5() {
		return temp5;
	}

	public void setTemp5(String temp5) {
		this.temp5 = temp5;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

	public String getAddPage() {
		return addPage;
	}

	public void setAddPage(String addPage) {
		this.addPage = addPage;
	}

	public String getSessMsg() {
		return sessMsg;
	}

	public void setSessMsg(String sessMsg) {
		this.sessMsg = sessMsg;
	}

	public String getOldSession() {
		return oldSession;
	}

	public void setOldSession(String oldSession) {
		this.oldSession = oldSession;
	}

	public String getNewSession() {
		return newSession;
	}

	public void setNewSession(String newSession) {
		this.newSession = newSession;
	}

	public ArrayList<EmployeeInfo> getStaffListBirthday() {
		return staffListBirthday;
	}

	public void setStaffListBirthday(ArrayList<EmployeeInfo> staffListBirthday) {
		this.staffListBirthday = staffListBirthday;
	}

	public EmployeeInfo getSelectStaff() {
		return selectStaff;
	}

	public void setSelectStaff(EmployeeInfo selectStaff) {
		this.selectStaff = selectStaff;
	}
	



}

