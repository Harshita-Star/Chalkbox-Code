package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import library_module.DataBaseMethodsLibraryModule;
import session_work.QueryConstants;
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="dashboardPrinciple")
@ViewScoped
public class DashboardPrincipleBean implements Serializable{
	public BarChartModel barModel;
	UploadedFile fileNew;
	String cunt,ticketType,userType,userid,description,billMsg,balMsg;
	String newStudent,allOverdue,errorLabel,preview,bdyPreview,leaveStudent,totalGallery,enquirystudent;
	public String getEnquirystudent() {
		return enquirystudent;
	}
	public void setEnquirystudent(String enquirystudent) {
		this.enquirystudent = enquirystudent;
	}

	ArrayList<String>datalist;
	ArrayList<EmployeeInfo> staffList = new ArrayList<>();
	EmployeeInfo selectedStaff;


	String countPendingLeave;
	boolean showClass,showStudent,showEmployee,showTransport=false,showFinance,showAttendence,showMessage,showPerformance,showTC,showPromotion,
			showPrevious,setting,report,showHomeWork,messageParents=false,messageStudents=false,showWishAll=false,showWishStaff;
	public ArrayList<EmployeeInfo> getStaffList() {
		return staffList;
	}
	public void setStaffList(ArrayList<EmployeeInfo> staffList) {
		this.staffList = staffList;
	}
	public boolean isShowWishStaff() {
		return showWishStaff;
	}
	public void setShowWishStaff(boolean showWishStaff) {
		this.showWishStaff = showWishStaff;
	}

	String messageStduent,sendTo,typeMessage="",status="";
	SchoolInfoList ls;
	String name;
	Date d=new  Date();
	StudentInfo selectStudent;
	ArrayList<StudentInfo> studentList,birthdayStudentList,sList;
	ArrayList<SelectItem> list;
	String totalCollection;
	String temp1,temp2,temp3,temp4,template="",birthdayWish="";
	ArrayList<Sum> sum;
	String timing,lang,schid,session;
	boolean showtiming,showEnglish,showHindi,notification,sms,disableNotify;
	double smsLimit;

	public DashboardPrincipleBean()
	{
		showEnglish = true;
		showHindi = false;
		sms=true;
		notification=false;
		searchData();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		
		schid=DBM.schoolId();
		session=DBM.selectedSessionDetails(schid, conn);
		ls=DBM.fullSchoolInfo(conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userid = (String) httpSession.getAttribute("username");
		userType = (String) httpSession.getAttribute("type");
		ticketType="error";
		if(!ticketType.equals("Training"))
		{
			showtiming=false;
		}
		else
		{
			showtiming=true;
		}


		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);

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

		cunt=DBM.allStudentcount(schid,"","",session,"",conn);
		Date d=new  Date();
		int year1=d.getYear()+1900;
		String selectYear=String.valueOf(year1);
		enquirystudent=DBM.allNewStudentEnquirycount(selectYear,conn);
		allOverdue = new DataBaseMethodsLibraryModule().totalOverdueBooks(new DatabaseMethods1().schoolId(),conn);
		leaveStudent=DBM.allLeaveStudent(selectYear,conn);
		messageStduent=DBM.todayMessage(DBM.schoolId(),conn);
		totalCollection=DBM.todaysCollection(DBM.schoolId(),"dashboard",conn);
		// countPendingLeave=String.valueOf(DBM.countPendingLeaveOfStudent(conn));
		birthday();
		createBarModel();
		temp1="Wishing You All Great Things In Life, Hope This Day Will Bring You An Extra Share Of All That Makes You Happiest. Happy Birthday!!";
		temp2="Wish You A Very Happy Birthday. May Life Lead You To Great Happiness, And Success. Enjoy Your Day!!";
		temp3="A Prayer To Bless You, A Wish To Lighten Your Moments, A Text To Say Happy Birthday!! God Bless U..";
		//temp4="Every B'day Presents A New Page In Ur Life, Keep Doing Good Things And Filling That Page With Good Deeds. Happy Birthday!!";
		temp4="Wish you all great things in life. May you live a long meaningful & blessful life.";
		lang = "english";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void searchData() {
		Connection conn = DataBaseConnection.javaConnection();
		Date date=new Date();
		int bDate =  date.getDate();
		int bMonth = date.getMonth() + 1;

		DatabaseMethods1 obj = new DatabaseMethods1();
		staffList = obj.allBirthdayListOfStaff(bDate, bMonth, conn);

		if (staffList.isEmpty()) {
			showWishStaff = false;
		} else {
			showWishStaff = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
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
						/*if(ls.getType().equalsIgnoreCase("basic"))
						{
							ec.redirect("dashboardBasic.xhtml");
						}
						else if(ls.getType().equalsIgnoreCase("novice"))
						{
							ec.redirect("Dashboard.xhtml");
						}
						else if(ls.getType().equalsIgnoreCase("foster"))
						{*/
						ec.redirect("dashboardPrinciple.xhtml");
						//}
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
									contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
									addNumber=addNumber+","+info.getAddNumber();
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
				/*if(ls.getType().equalsIgnoreCase("basic"))
					{
						ec.redirect("dashboardBasic.xhtml");
					}
					else if(ls.getType().equalsIgnoreCase("novice"))
					{
						ec.redirect("Dashboard.xhtml");
					}
					else if(ls.getType().equalsIgnoreCase("foster"))
					{*/
				ec.redirect("dashboardPrinciple.xhtml");
				//}
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
									contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
									addNumber=addNumber+","+info.getAddNumber();
								}

							}
						}

						if(messageStudents==true)
						{
							if(info.getStudentPhone().length()==10
									&& !String.valueOf(info.getStudentPhone()).equals("2222222222")
									&& !String.valueOf(info.getStudentPhone()).equals("9999999999")
									&& !String.valueOf(info.getStudentPhone()).equals("1111111111")
									&& !String.valueOf(info.getStudentPhone()).equals("1234567890")
									&& !String.valueOf(info.getStudentPhone()).equals("0123456789"))
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
				/*if(ls.getType().equalsIgnoreCase("basic"))
					{
						ec.redirect("dashboardBasic.xhtml");
					}
					else if(ls.getType().equalsIgnoreCase("novice"))
					{
						ec.redirect("Dashboard.xhtml");
					}
					else if(ls.getType().equalsIgnoreCase("foster"))
					{*/
				ec.redirect("dashboardPrinciple.xhtml");
				//}
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
		if(!staffList.isEmpty())
		{
			PrimeFaces.current().executeInitScript("PF('birthdayStaffDialog').show()");
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
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
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
			bdyPreview="Dear Student,\n"+template+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
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
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg2').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm2");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
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
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
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
			bdyPreview="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
	}

	public void allBdMsgStaff()
	{
		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+template+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Staff Member,\n"+birthdayWish+"\nRegards,\n"+ls.getSmsSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		}
	}

	public void sendWish() throws IOException
	{

		PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevForm");
		DatabaseMethods1 DBM = new DatabaseMethods1();

		String message="",contactNumber="",addNumber="";

		Connection conn=DataBaseConnection.javaConnection();
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

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("dashboardPrinciple.xhtml");
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

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("dashboardPrinciple.xhtml");
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

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("dashboardPrinciple.xhtml");
		}

		//(contactNumber);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void sendWishStaff() throws IOException {
		PrimeFaces.current().executeInitScript("PF('bdyPrevStaffDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevStaffForm");
		String message = "", contactNumber = "";

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		String id = "";
		if(!ls.getCountry().equalsIgnoreCase("India"))
		{
			String tp = birthdayWish;
			Runnable r = new Runnable()
			{
				public void run()
				{

					if (status.equalsIgnoreCase("all"))
					{
						String heading = "";
						String subject = "Message From "+ls.getSchoolName();
						String msg = "";
						for(EmployeeInfo info : staffList)
						{
							heading = "<center class=\"red\">Happy Birthday "+info.getFname()+"!</center>";
							msg = "Dear " + info.getFname() + ",<br></br>" + tp + " <br></br>Regards,<br></br>" + ls.getSchoolName();
							if(info.getEmail()!=null && !info.getEmail().equalsIgnoreCase("") && !info.getEmail().equalsIgnoreCase("null"))
							{
								new DataBaseOnlineAdm().doMail(info.getEmail(), subject, heading, msg);
							}
						}
					}
					else
					{
						String heading = "<center class=\"red\">Happy Birthday "+selectedStaff.getFname()+"!</center>";
						String subject = "Message From "+ls.getSchoolName();
						String msg = "Dear " + selectedStaff.getFname() + ",<br></br>" + tp + " <br></br>Regards,<br></br>" + ls.getSmsSchoolName();;
						if(selectedStaff.getEmail()!=null && !selectedStaff.getEmail().equalsIgnoreCase(""))
						{
							new DataBaseOnlineAdm().doMail(selectedStaff.getEmail(), subject, heading, msg);
						}
					}
				}
			};
			new Thread(r).start();
		}
		else
		{
			if (status.equalsIgnoreCase("all"))
			{
				for (EmployeeInfo info : staffList)
				{
					id = info.getFname()+"@CB@"+info.getId();
					//(String.valueOf(info.getMobile()).length());
					if (String.valueOf(info.getMobile()).length() == 10
							&& !String.valueOf(info.getMobile()).equals("2222222222")
							&& !String.valueOf(info.getMobile()).equals("9999999999")
							&& !String.valueOf(info.getMobile()).equals("1111111111")
							&& !String.valueOf(info.getMobile()).equals("1234567890")
							&& !String.valueOf(info.getMobile()).equals("0123456789"))
					{
						name=info.getFname();
						message = "Dear " + name + ",\n" + birthdayWish + "\n" + ls.getSmsSchoolName();
						obj.messageurlStaff(String.valueOf(info.getMobile()), message, id, conn,obj.schoolId(),"");
					}
				}
			}
			else
			{
				id = selectedStaff.getFname()+"@CB@"+selectedStaff.getId();
				contactNumber = String.valueOf(selectedStaff.getMobile());
				if (contactNumber.length() == 10
						&& !contactNumber.equals("2222222222")
						&& !contactNumber.equals("9999999999")
						&& !contactNumber.equals("1111111111")
						&& !contactNumber.equals("1234567890")
						&& !contactNumber.equals("0123456789"))
				{
					message = "Dear " + selectedStaff.getFname() + ",\n" + birthdayWish + "\n" + ls.getSmsSchoolName();
					obj.messageurlStaff(contactNumber, message, id, conn,obj.schoolId(),"");
				}

			}
		}

		if (template.isEmpty() && birthdayWish.isEmpty()) {
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			//("hiiii");
			errorLabel="Message Sent";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayFormStaff");
			FacesContext.getCurrentInstance().getExternalContext().redirect("dashboardPrinciple.xhtml");
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
		if (fileNew != null && fileNew.getSize()>0)
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
			errorLabel="Ticket Added Successfully.Your Ticket Id No "+ticketid;
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
	public String getAllOverdue() {
		return allOverdue;
	}

	public void setAllOverdue(String allOverdue) {
		this.allOverdue = allOverdue;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowStudent() {
		return showStudent;
	}

	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
	}

	public boolean isShowEmployee() {
		return showEmployee;
	}

	public void setShowEmployee(boolean showEmployee) {
		this.showEmployee = showEmployee;
	}

	public boolean isShowTransport() {
		return showTransport;
	}

	public void setShowTransport(boolean showTransport) {
		this.showTransport = showTransport;
	}

	public boolean isShowFinance() {
		return showFinance;
	}

	public void setShowFinance(boolean showFinance) {
		this.showFinance = showFinance;
	}

	public boolean isShowAttendence() {
		return showAttendence;
	}

	public void setShowAttendence(boolean showAttendence) {
		this.showAttendence = showAttendence;
	}

	public boolean isShowMessage() {
		return showMessage;
	}

	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}

	public boolean isShowPerformance() {
		return showPerformance;
	}

	public void setShowPerformance(boolean showPerformance) {
		this.showPerformance = showPerformance;
	}

	public boolean isShowTC() {
		return showTC;
	}

	public void setShowTC(boolean showTC) {
		this.showTC = showTC;
	}

	public boolean isShowPromotion() {
		return showPromotion;
	}

	public void setShowPromotion(boolean showPromotion) {
		this.showPromotion = showPromotion;
	}

	public boolean isShowPrevious() {
		return showPrevious;
	}

	public void setShowPrevious(boolean showPrevious) {
		this.showPrevious = showPrevious;
	}

	public boolean isSetting() {
		return setting;
	}

	public void setSetting(boolean setting) {
		this.setting = setting;
	}

	public boolean isReport() {
		return report;
	}

	public void setReport(boolean report) {
		this.report = report;
	}

	public boolean isShowHomeWork() {
		return showHomeWork;
	}

	public void setShowHomeWork(boolean showHomeWork) {
		this.showHomeWork = showHomeWork;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getsList() {
		return sList;
	}

	public void setsList(ArrayList<StudentInfo> sList) {
		this.sList = sList;
	}

	public ArrayList<SelectItem> getList() {
		return list;
	}

	public void setList(ArrayList<SelectItem> list) {
		this.list = list;
	}

	public ArrayList<Sum> getSum() {
		return sum;
	}

	public void setSum(ArrayList<Sum> sum) {
		this.sum = sum;
	}

	public double getSmsLimit() {
		return smsLimit;
	}

	public void setSmsLimit(double smsLimit) {
		this.smsLimit = smsLimit;
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
	public EmployeeInfo getSelectedStaff() {
		return selectedStaff;
	}
	public void setSelectedStaff(EmployeeInfo selectedStaff) {
		this.selectedStaff = selectedStaff;
	}


}

