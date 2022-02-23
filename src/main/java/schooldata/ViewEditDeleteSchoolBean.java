package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ManagedBean(name="evdSchool")
@ViewScoped
public class ViewEditDeleteSchoolBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SchoolInfo> dataList;
	ArrayList<SchoolInfo> selList = new ArrayList<SchoolInfo>();
	SchoolInfo selectedRow;
	int noOfStudents,msz,agreementFor;
	String adminAppKey="",schoolName,aliasName,id,contactNo,contactName,password,deviceName,imeiNo,devicePwd,gps,session,schoolSession,feeStart;
	Date startDate=new Date(),expireDate,renewalDate,date=new Date();
	double chalkBoxAmount,imgAmount,chalkBoxRenewal,imgRenewalAmount;
	String oldSchoolName,oldAliasName,oldStartDateStr,oldExpireDateStr,oldRenewalDateStr,oldContactNo,oldContactName,clientType,timetable;
	double oldChalkBoxAmount,oldImgAmount,oldChalkBoxRenewal,oldImgRenewalAmount,payment;
	ArrayList<SelectItem> sessionList ;
	String senderid,appType,oldAdminApp,type,admNoDupl,selectedSchool;
	boolean adminLogin=false,teacherLogin=false,studentApp=false,academicLogin=false,authorityLogin=false,principalLogin=false;
	boolean frontLogin=false,libraryLogin=false,attendantLogin=false,transportLogin=false,securityLogin=false,otherLogin=false;
	public ViewEditDeleteSchoolBean()
	{
		selectedSchool = "";
		adminAppKey = "AAAAaoFmDCc:APA91bFSCXCDvpPPN70s8v_d83f2p6EgJVkGOs_JIsg6muf5RUx1YQNFRxgNUP8jZjCwePaDEsx3KEDX3tynYMHq_ZI0qsGCZOoMipS2nc5C42JzeWkyCIu2rd5iTKN-elmUBH3ggDnt";
		Connection conn=DataBaseConnection.javaConnection();
		sessionList=new ArrayList<>();
		for(int i=1991;i<=2050;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		if(month>=4)
		{
			session=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			session=String.valueOf(year-1)+"-"+String.valueOf(year);
		}
		
		selList = new DatabaseMethods1().allSchoolListForExecutive(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		dataList=new DatabaseMethods1().allSchoolListChalkbox(selectedSchool, conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String adminPermission()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schid", selectedRow.getId());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("permissionByMasterAdmin.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void appPermission()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schid", selectedRow.getId());
		ss.setAttribute("adminApp", selectedRow.isAdminLogin());
		ss.setAttribute("teacherApp", selectedRow.isTeacherLogin());
		ss.setAttribute("studentApp", selectedRow.isStudentApp());
		ss.setAttribute("academicApp", selectedRow.isAcademicLogin());
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("setAppPermission.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updatePaymentDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().addPayment(id, "Extra work", date, payment, "debit",conn);
		if(i==1)
		{
			//int j=new DatabaseMethods1().updateAmount(payment);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Payment Added Successfully"));
			payment=0.0;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkGpsPermission()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String checkGps=new DataBaseValidator().checkGpsPermission(selectedRow.getId(),conn);
		if(checkGps.equalsIgnoreCase("no"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"GPS Permission Not Given to Selected School. Please Permit First From Edit Section and Then Allot GPS.","Warning"));
		}
		else
		{
			PrimeFaces.current().executeInitScript("PF('gpsDialog').show()");
			PrimeFaces.current().ajax().update("gps");
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addGps()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseValidator objValidate=new DataBaseValidator();

		String checkGps=objValidate.checkGpsPermission(selectedRow.getId(),conn);
		if(checkGps.equalsIgnoreCase("no"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Permission Not Given to Selected School. Please Permit First From Edit Section and Then Allot GPS."));
		}
		else
		{
			boolean check=objValidate.checkGpsDuplicacy(deviceName,imeiNo,selectedRow.getId(),conn);
			if(check==true)
			{
				deviceName=imeiNo=devicePwd="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Device Name or Imei No. Already Added. Please Try Again"));
			}
			else
			{
				int i=new DatabaseMethods1().addGPSDevice(deviceName, imeiNo, devicePwd, selectedRow.getId(),conn);
				if(i==1)
				{
					//int j=new DatabaseMethods1().updateAmount(payment);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Device Alloted Successfully."));
					deviceName=imeiNo=devicePwd="";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editSelectedRow()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		id=selectedRow.getId();
		schoolName=selectedRow.getSchoolName();
		aliasName=selectedRow.getAliasName();
		startDate=selectedRow.getStartDate();
		expireDate=selectedRow.getExpireDate();
		renewalDate=selectedRow.getRenewalDate();
		chalkBoxAmount=selectedRow.getChalkBoxAmount();
		imgAmount=selectedRow.getImgAmount();
		chalkBoxRenewal=selectedRow.getChalkBoxRenewal();
		imgRenewalAmount=selectedRow.getImgRenewalAmount();
		contactName=selectedRow.getContactName();
		contactNo=selectedRow.getContactNo();
		appType = selectedRow.getAppType();
		adminLogin = selectedRow.isAdminLogin();
		teacherLogin = selectedRow.isTeacherLogin();
		
		academicLogin = selectedRow.isAcademicLogin();
		authorityLogin = selectedRow.isAuthorityLogin();
		principalLogin = selectedRow.isPrincipalLogin();
		frontLogin = selectedRow.isFrontLogin();
		libraryLogin = selectedRow.isLibraryLogin();
		attendantLogin = selectedRow.isAttendantLogin();
		transportLogin = selectedRow.isTransportLogin();
		securityLogin = selectedRow.isSecurityLogin();
		otherLogin = selectedRow.isOtherLogin();
		
		studentApp = selectedRow.isStudentApp();
		type = selectedRow.getType();
		agreementFor = selectedRow.getAgreementFor();

		oldSchoolName=selectedRow.getSchoolName();
		oldAliasName=selectedRow.getAliasName();
		oldStartDateStr=sdf.format(selectedRow.getStartDate());
		oldExpireDateStr=sdf.format(selectedRow.getExpireDate());
		oldRenewalDateStr=sdf.format(selectedRow.getRenewalDate());
		oldChalkBoxAmount=selectedRow.getChalkBoxAmount();
		oldImgAmount=selectedRow.getImgAmount();
		oldContactNo=selectedRow.getContactNo();
		oldContactName=selectedRow.getContactName();
		oldChalkBoxRenewal=selectedRow.getChalkBoxRenewal();
		oldImgRenewalAmount=selectedRow.getImgRenewalAmount();
		oldAdminApp = selectedRow.getAdminApp();
		noOfStudents=selectedRow.getNoOfStudents();
		msz=selectedRow.getMsz();
		gps=selectedRow.getGps();
		session=selectedRow.getSession();
		schoolSession=selectedRow.getSchoolSession();
		feeStart=selectedRow.getFeeStart();
		clientType=selectedRow.getClientType();
		timetable=selectedRow.getTimetable();
		senderid=selectedRow.getSenderid();
		admNoDupl = selectedRow.getAdmNoDupl();
	}
	
	public void updatePassword()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.updateSchoolPassword(id,password,conn);
		if(i==1)
		{
			obj.updateSchoolPasswordInAddSchool(id,password,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password Updated Successfully"));
			dataList=obj.allSchoolListChalkbox(selectedSchool, conn);
			password="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		String adminApp="",stApp;
		if(adminLogin || teacherLogin)
		{
			adminApp = "yes";
		}
		else
		{
			adminApp = "no";
		}

		if(studentApp)
		{
			stApp = "yes";
		}
		else
		{
			stApp = "no";
		}

		if(studentApp && appType.equals(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select App Type"));
		}
		else
		{
			int i=DBM.updateSchool(schoolName,startDate, expireDate, renewalDate, chalkBoxAmount,
					imgAmount, chalkBoxRenewal, imgRenewalAmount, id,contactNo,contactName,conn,gps,session,clientType,
					timetable,adminLogin,teacherLogin,appType,studentApp,type,agreementFor,academicLogin,authorityLogin,principalLogin,frontLogin,
					libraryLogin,attendantLogin,transportLogin,securityLogin,otherLogin);
			if(i==1)
			{
				DBM.updateChalkBoxPayment(id,"Initial",date,chalkBoxAmount,"debit",conn);
				DBM.updateSchoolDetails(schoolName,contactNo,id,conn,schoolSession,feeStart,senderid,adminApp,stApp,type,admNoDupl);
				dataList=DBM.allSchoolListChalkbox(selectedSchool,conn);
				//createAndSendFile();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Updated Successfully"));
				schoolName=aliasName=contactNo=contactName="";chalkBoxAmount=chalkBoxRenewal=imgAmount=imgRenewalAmount=0;
				startDate=new Date();expireDate=renewalDate=null;
				clientType="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteSchool()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.deleteSchool(selectedRow.getId(),conn);
		if(i==1)
		{
			dataList=obj.allSchoolListChalkbox("All", conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Deleted Sucessfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void schoolLogin()
	{
		String unm=selectedRow.getUsername();
		String pwd=selectedRow.getPassword();

		loginAsSchoolAdmin(unm,pwd);
	}

	public void loginAsSchoolAdmin(String unm, String pswd)
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		String userType = DBM.authentication(unm, pswd,conn);
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try
		{

			if(userType!=null)
			{
				String selectedSession="";
				String school = DBM.authenticationSchoool(unm, pswd,conn);
				HttpSession sss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				sss.invalidate();

				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("showSMSBalMessage", "yes");
				ss.setAttribute("showDueBillMessage", "yes");
				ss.setAttribute("showSessionMessage", "yes");
				ss.setAttribute("schoolid", school);
				ss.setAttribute("username", unm);
				ss.setAttribute("type", userType);
				ss.setAttribute("mtype", userType);
				ss.setAttribute("masterAdmin", "yes");
				ss.setAttribute("serviceExecutive", "no");
				SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
				selectedSession = info.getDefaultSession();
				
				ArrayList<SelectItem> list=DBM.allModules(conn);
				if(list.size()>0)
				{
					boolean i=DBM.checkSchoolInfo(conn);
					if(i==true)
					{
						
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
						ss.setAttribute("checkstu", false);
						
						if(info.getType().equalsIgnoreCase("basic"))
						{
							ec.redirect("../dashboardBasic.xhtml");
						}
						else if(info.getType().equalsIgnoreCase("novice"))
						{
							ec.redirect("../Dashboard.xhtml");
						}
						else if(info.getType().equalsIgnoreCase("foster"))
						{
							ec.redirect("../dashboardFoster.xhtml");
						}

					}
					else
					{
					   ec.redirect("../schoolInformation.xhtml");
					}
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Username or password is wrong", "Validation error"));
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

		public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}
	public SchoolInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(SchoolInfo selectedRow) {
		this.selectedRow = selectedRow;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	public Date getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}
	public double getImgAmount() {
		return imgAmount;
	}
	public void setImgAmount(double imgAmount) {
		this.imgAmount = imgAmount;
	}
	public double getChalkBoxRenewal() {
		return chalkBoxRenewal;
	}
	public void setChalkBoxRenewal(double chalkBoxRenewal) {
		this.chalkBoxRenewal = chalkBoxRenewal;
	}
	public double getImgRenewalAmount() {
		return imgRenewalAmount;
	}
	public void setImgRenewalAmount(double imgRenewalAmount) {
		this.imgRenewalAmount = imgRenewalAmount;
	}
	public double getChalkBoxAmount() {
		return chalkBoxAmount;
	}
	public void setChalkBoxAmount(double chalkBoxAmount) {
		this.chalkBoxAmount = chalkBoxAmount;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public int getNoOfStudents() {
		return noOfStudents;
	}

	public void setNoOfStudents(int noOfStudents) {
		this.noOfStudents = noOfStudents;
	}

	public int getMsz() {
		return msz;
	}

	public void setMsz(int msz) {
		this.msz = msz;
	}
	public double getPayment() {
		return payment;
	}
	public void setPayment(double payment) {
		this.payment = payment;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getImeiNo() {
		return imeiNo;
	}

	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}

	public String getDevicePwd() {
		return devicePwd;
	}

	public void setDevicePwd(String devicePwd) {
		this.devicePwd = devicePwd;
	}

	public String getGps() {
		return gps;
	}

	public void setGps(String gps) {
		this.gps = gps;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSchoolSession() {
		return schoolSession;
	}
	public void setSchoolSession(String schoolSession) {
		this.schoolSession = schoolSession;
	}
	public String getFeeStart() {
		return feeStart;
	}
	public void setFeeStart(String feeStart) {
		this.feeStart = feeStart;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getTimetable() {
		return timetable;
	}
	public void setTimetable(String timetable) {
		this.timetable = timetable;
	}
	public String getSenderid() {
		return senderid;
	}
	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public boolean isAdminLogin() {
		return adminLogin;
	}
	public void setAdminLogin(boolean adminLogin) {
		this.adminLogin = adminLogin;
	}
	public boolean isTeacherLogin() {
		return teacherLogin;
	}
	public void setTeacherLogin(boolean teacherLogin) {
		this.teacherLogin = teacherLogin;
	}
	public boolean isStudentApp() {
		return studentApp;
	}
	public void setStudentApp(boolean studentApp) {
		this.studentApp = studentApp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getAgreementFor() {
		return agreementFor;
	}
	public void setAgreementFor(int agreementFor) {
		this.agreementFor = agreementFor;
	}
	public boolean isAcademicLogin() {
		return academicLogin;
	}
	public void setAcademicLogin(boolean academicLogin) {
		this.academicLogin = academicLogin;
	}
	public boolean isAuthorityLogin() {
		return authorityLogin;
	}
	public void setAuthorityLogin(boolean authorityLogin) {
		this.authorityLogin = authorityLogin;
	}
	public boolean isPrincipalLogin() {
		return principalLogin;
	}
	public void setPrincipalLogin(boolean principalLogin) {
		this.principalLogin = principalLogin;
	}
	public boolean isFrontLogin() {
		return frontLogin;
	}
	public void setFrontLogin(boolean frontLogin) {
		this.frontLogin = frontLogin;
	}
	public boolean isLibraryLogin() {
		return libraryLogin;
	}
	public void setLibraryLogin(boolean libraryLogin) {
		this.libraryLogin = libraryLogin;
	}
	public boolean isAttendantLogin() {
		return attendantLogin;
	}
	public void setAttendantLogin(boolean attendantLogin) {
		this.attendantLogin = attendantLogin;
	}
	public boolean isTransportLogin() {
		return transportLogin;
	}
	public void setTransportLogin(boolean transportLogin) {
		this.transportLogin = transportLogin;
	}
	public boolean isSecurityLogin() {
		return securityLogin;
	}
	public void setSecurityLogin(boolean securityLogin) {
		this.securityLogin = securityLogin;
	}
	public boolean isOtherLogin() {
		return otherLogin;
	}
	public void setOtherLogin(boolean otherLogin) {
		this.otherLogin = otherLogin;
	}
	public String getAdmNoDupl() {
		return admNoDupl;
	}
	public void setAdmNoDupl(String admNoDupl) {
		this.admNoDupl = admNoDupl;
	}
	public String getSelectedSchool() {
		return selectedSchool;
	}
	public void setSelectedSchool(String selectedSchool) {
		this.selectedSchool = selectedSchool;
	}

	public ArrayList<SchoolInfo> getSelList() {
		return selList;
	}

	public void setSelList(ArrayList<SchoolInfo> selList) {
		this.selList = selList;
	}
	

}
