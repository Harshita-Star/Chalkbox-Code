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

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;
@ManagedBean(name="sideMenuBean")
@ViewScoped
public class SideMenuBean implements Serializable
{
	String name,mail,session,country;

	SchoolInfoList list;
	String imagepath,userType;
	String companyName="",headerImage="",serviceExec="no";
	int creditmessage,debitmessage;
	boolean logoutchalkbox,logouteducate;
	boolean examModule = false;
	String schid;
	

	private boolean renderInSession=false,disableInSession=true;;
	private String menu = "menu1";

	ArrayList<ModuleInfo> moduleList;
	boolean showTeacher=false,showAdmin=false,showMAdmin=false,showBackToMaster=false;
	ArrayList<SelectItem> pageList,innerPageList;

	boolean changeSession=false,help=false,master=false,permissionSetting=false,masterSetting=false, manageStudent=false,manageClassTest=false,
			manageApp=false,manageFeeCollection=false,manageInstallmentFeeCollection=false,manageAttendance=false,manageEnquiryModule=false,manageSalary=false,manageInfirmary=false,
			manageCommunication=false,manageAdmitCard=false,manageLevCertificate=false,managePrint=false,manageReports=false,
			manageStaffCommunication=false,manageTimeTable=false,manageLeaveModule=false,manageLectureArrangementModule=false,manageImprest=false,
			manageSubjectAllocationModule=false,manageStaff=false,manageRbseModule=false,schoolCalendar=false,printCertificate=false,
			manageLibCertificate=false,manageRbseTCModule=false,manageBank=false,manageExpense=false,manageIncome=false,manageCheque=false,managehostel=false,manageMeeting=false;

	boolean manageConcessionRequest=false,manageAutoMessageSetting=false,managePassword=false,manageClassMaster=false,manageExamMaster=false,manageFeeMaster=false,manageSchoolInformation=false,
			manageSemesterMaster=false,manageSubjectMaster=false,manageTransportmaster=false,managePhoneBook=false,manageAllStudentList=false,manageClassTransfer=false,managePreviuosFee=false,
			manageSearchStudent=false,manageStudentPerformance=false,manageStudentPromotion=false,manageStudentRegistration=false,manageTransportDetail=false,manageRollNo=false,manageAddClassTest=false,
			managePrintPerformance=false,manageTestPerformance=false,manageClassTestReport=false,manageClassWiseTestReport=false,manageAbsentStudentsReport=false,manageMarkAttendence=false,manageShowAttendence=false,
			manageAddEnquiry=false,manageAddInstituteEnquiry=false,manageShowEnquiry=false,manageShowInstituteEnquiry=false,manageGroupMessgae=false,manageRandomMessage=false,manageSentMessage=false,manageAddAdminCard=false,managePrintAdmitCard=false,
			manageTcGenerate=false,manageStruckOff=false,manageEditViewStruckOff=false,manageExamMarksGrade=false,manageFinalMarksheet=false,manageUnitMarksheet=false,manageMarksAverage=false,manageDetailedMarks=false,manageStudentOverallPerformance=false,manageIcard=false,manageStudentExtraValue=false,
			manageEvents=false,manageGallery=false,manageHomeWorkNotes=false,manageAddMeeting,manageViewEditDeleteMeeting,manageNews=false,manageSyllabus=false,manageViewGallery=false,manageViewAssignment=false,manageTimeTableSetting,manageGenerateTimeTable=false,
			managePrintTimeTable=false,manageMyTimeTable=false,manageIndividualTimeTable=false,manageApplyLeave=false,manageCheckLeave=false,manageArrangeLecture=false,manageSubjectWiseStudentReport=false,
			manageAllLectureArrangement=false,manageSubjectAllocation=false,manageEditSubjectAllocation=false,manageEmployeeCategory=false,manageAddEmployee=false,manageStudentWiseSubjectReport=false,
			manageEditEmployee=false,manageAddRbseExam=false,manageAddStudentPerformance=false,manageStudentFinalMarksheet=false,manageAddCalendar=false,manageViewCalendar=false,manageStudentHandWrittenMarkList=false,
			manageBonafiedCertificate=false,manageCharaceterCertificate=false,manageLoginDetails=false,manageStudentLogin=false,manageAddBank=false,manageViewEditDeleteBank=false,
			manageStudentBookHistory=false,manageAddBook=false,manageViewBook=false,manageBookQuantity=false,manageAssignBook=false,manageReceiveBook=false,manageRbscStruckOff=false,manageRbscCCGeneration=false,
			manageRbscTCGeneration=false,manageRbscTCList=false,manageAddExpense=false,manageViewDeleteExpense=false,manageAddIncome=false,manageViewDeleteIncome=false,
			manageAssetSale=false,manageAssetPurchase=false,manageAssetSaleReport=false,manageAssetPurchaseReport=false,manageDueCheque=false,managePendingTransaction=false,manageAddClassTeacher=false,manageEditClassTeacher=false,
			manageOldRbscTCGeneration=false,manageOldRbscTCList=false,feeEstimateReport=false,manageShowEnquiryblm=false,manageoasismarksheetupto3=false,manageuploadstudentmarksheet=false,manageELearing=false,manageViewELearning=false;
	boolean manageCancelledReceipts=false,manageCategoryWiseEmp=false,manageCategoryWiseStrength=false,manageCategoryWiseStudents=false,manageClassStrength=false,manageDailyFeeCollection=false,
			manageDateWiseAdmissions=false,manageDueFees=false,manageInactiveStudents=false,manageMonthWiseAttendance=false,managePendingLeavesStudent=false,managePreviousFees=false,
			manageRouteWiseStudents=false,manageSessionCollection=false,manageShowLeaves=false,manageStopWiseStudents=false,manageStudentAgeReport=false,manageStudentAttendance=false,
			manageStudentTCReport=false,manageUnMarkedAttendance=false,managePaidFeeReport=false,manageViewAttendance=false,manageClassWiseFeeCollectionReport=false,manageConcessionCategoryWiseStudentReport=false,
			manageDateWiseFeeCollectionReport=false,manageFeeStructureReport=false,manageHandicappedReport=false,manageMonthWiseFeeCollectionReport=false,manageReligionWiseReport=false,manageBookReport=false,
			manageAssignedBookReport=false,manageReceivedBookReport=false,manageSMSLedger=false,manageViewSyllabus=false,manageInstituteTimetable=false;

	boolean manageSalFormationReport=false,manageDeduction=false,manageComplaintDiary=false,manageSalaryPay=false,manageSalaryFormation=false,manageSalaryDetails=false,
			manageDeductionReport=false,manageSalaryLedger=false,manageSalaryReport=false,manageExperienceCertificate=false,
			manageAttendanceCertificate=false,manageConcessionCategory=false,manageRouteMessage=false,showAccount=false,manageBulkConcession=false,manageMultipleCertificate=false;

	boolean feeRecordRegister=false,manageDailyCollection=false,manageAccounting=false,manageGeneralReport=false,manageStudentMessage=false,
			manageStudentReport=false, manageFeeReport=false,manageLibReport=false,manageAttendanceReport=false,
			manageTransportReport=false,manageOldStudentSMS=false,manageTracking=false,manageStaffBirthday=false,manageStudentBirthday=false;

	boolean manageStaffAtt=false,manageStaffAttRep=false,manageAbsentStaff=false,manageConcernFeedback=false,manageLeaveRequest=false,manageAddNews=false,manageTemplate=false,manageMesssagePack=false;
	boolean manageStaffLeave=false,manageStaffLeaveRequest=false,manageStaffLeaveRep=false,manageGenerateRank=false,manageLibrarySetting=false,manageUniqueTeacherAttendece=false,manageMultipleTeacherAttendece=false;

	boolean managePreviousSession=false,managePreviousAttendance=false,managePreviousFee=false,managePreviousMarksheet=false,
			manageAnalaticReport=false,manageVisitorModule=false,manageAddVisitor=false,manageShowVisitor=false,
			manageAccount=false,manageAddHead=false,manageAddAccount=false,manageTransaction=false,managePendingCheque=false,
			manageAccountLedger=false,manageEditSalary=false,manageViewSalary=false,manageEnqComm=false,manageTCComm=false,
			manageOnlineAdmReq=false,manageRegApplication=false,manageAdmApplication=false,manageVideoGallery=false,managePickup=false,
			manageSalarySetting=false,manageAppointmentSetting=false,managePhotoRequest=false,manageConsent=false;

	boolean manageEYFS=false,manageEYFSParameter=false,manageEYFSPerformance=false,manageEYFSReport=false,attendantApp=false,allotRfid=false,allotBioCode=false;

	boolean manageFlyer, manageAdministrativeOfficer, manageOnlineExam, onlineExamReport, deletePerformance;
	
	public boolean isManageOldStudentSMS() {
		return manageOldStudentSMS;
	}

	public void setManageOldStudentSMS(boolean manageOldStudentSMS) {
		this.manageOldStudentSMS = manageOldStudentSMS;
	}

	public boolean isManageTransportReport() {
		return manageTransportReport;
	}

	public void setManageTransportReport(boolean manageTransportReport) {
		this.manageTransportReport = manageTransportReport;
	}

	int messageBalanace;
	String masterAdmin,hindiName,smsName;
	String startSession,endSession;
	int startSessionMonth,endSessionMonth;
	public void tim() throws IOException
	{

	}

	public void showDia()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		ss.invalidate();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void onlineExam() throws IOException 
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		String pwd = (String) ss.getAttribute("pwd");
		String txt = new DatabaseMethods1().xorMessage(pwd, new DatabaseMethods1().pwdkey);
		String password = DatabaseMethods1.base64encode(txt);
		String url = "https://exam.chalkbox.in/admin/Login/AdminLoginEnd?username="+username+"&password="+pwd;
		PrimeFaces.current().executeInitScript("window.open('"+url+"')");
	}
	
	public SideMenuBean()
	{
		HttpSession ss2=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	
		if(ss2 == null)
		{
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
         
		userType=(String) ss2.getAttribute("type");
		String mType=(String) ss2.getAttribute("mtype");
		masterAdmin=(String) ss2.getAttribute("masterAdmin");
		serviceExec=(String) ss2.getAttribute("serviceExecutive");

		Connection conn=DataBaseConnection.javaConnection();
		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		startSession = session.split("-")[0];
		endSession = session.split("-")[1];
		list=new DatabaseMethods1().fullSchoolInfo(conn);

		startSessionMonth = Integer.valueOf(list.getSchoolSession().split("-")[0]);
		endSessionMonth = Integer.valueOf(list.getSchoolSession().split("-")[1]) + 12;
		
		schid = new DatabaseMethods1().schoolId();
		if(schid.equals("302")) {
			examModule = true;
		}
		
		if(serviceExec.equalsIgnoreCase("yes"))
		{
			deletePerformance=true;
		}

		if(masterAdmin.equalsIgnoreCase("no"))
		{
			master=false;
			if(list.getCtype().equalsIgnoreCase("chalkbox"))
			{
				companyName="ChalkBox";
				logoutchalkbox=true;
				logouteducate=false;
			}
			else
			{
				companyName="ChalkBox";
				logoutchalkbox=true;
				logouteducate=false;
			}
		}
		else
		{
			master=true;
			logoutchalkbox=true;
			logouteducate=false;

			if(list.getCtype().equalsIgnoreCase("chalkbox"))
			{
				companyName="ChalkBox";
			}
			else
			{
				companyName="ChalkBox";
			}
		}

		creditmessage =new DatabaseMethods1().creditMesage(conn);
		debitmessage=new DatabaseMethods1().debitMesage(conn);
		messageBalanace=creditmessage-debitmessage;

		name=list.getSchoolName();
		smsName = list.getSmsSchoolName();
		hindiName = list.getHindiName();
		mail=list.getEmailId();
		imagepath=list.getDownloadpath()+list.getImagePath();
		headerImage=list.getDownloadpath()+list.getMarksheetHeader();

		if(mType.equalsIgnoreCase("madmin"))
		{
			help=true;
			showAdmin=false;
			showTeacher=false;
			showAccount=false;
			showMAdmin=true;
			showBackToMaster=true;
		}
		else if(mType.equalsIgnoreCase("Teacher"))
		{
			showTeacher=true;
			showAdmin=false;
			showAccount=false;
			showMAdmin=false;
			help=false;
		}
		else if(mType.equalsIgnoreCase("Accounts"))
		{
			showTeacher=false;
			showAdmin=false;
			showAccount=true;
			showMAdmin=false;
			help=false;
		}
		else if(mType.equalsIgnoreCase("admin"))
		{
			help=true;
			showAdmin=true;
			showTeacher=false;
			showAccount=false;
			showMAdmin=false;
		}

		if(userType.equalsIgnoreCase("madmin"))
		{
			userType="Admin";
		}

		if(new DatabaseMethods1().schoolId().equals("215"))
		{
			manageTracking = true;
		}
		else
		{
			manageTracking = false;
		}
		
		allotRfid = new DatabaseMethods1().checkRfid(new DatabaseMethods1().schoolId(),"All",conn);
		allotBioCode = new DatabaseMethods1().checkBioDevice(new DatabaseMethods1().schoolId(),conn);
		attendantApp = new DataBaseMeathodJson().appLoginPermission("Attendant", new DatabaseMethods1().schoolId(), conn).equalsIgnoreCase("true") ? true : false;
		
		String tempUsrType = userType.toLowerCase();
		if(userType.equalsIgnoreCase("vice principal"))
		{
			tempUsrType = "principal";
		}
		
		pageList=new DatabaseMethods1().allOuterPageList(tempUsrType,conn);
		innerPageList=new DatabaseMethods1().allInnerPageList(tempUsrType,conn);
		permissionSetting(conn);
		innerPermissionSetting(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void goToAllStudent() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("studentListPage", "sidemenu");

		FacesContext.getCurrentInstance().getExternalContext().redirect("allStudentList.xhtml");

	}
	
	public void monthlyBiometricReport() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("biometricReportBasis", "monthly");

		FacesContext.getCurrentInstance().getExternalContext().redirect("monthlyBiometricRecord.xhtml");

	}
	
	public void yearlyBiometricReport() throws IOException
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("biometricReportBasis", "yearly");

		FacesContext.getCurrentInstance().getExternalContext().redirect("yearlyBiometricRecord.xhtml");

	}

	public void goToAddStudent() throws IOException
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		if(list.getCountry().equalsIgnoreCase("UAE"))
		{
			try
			{
				ec.redirect("addStudentAe.xhtml");
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
		}

	}

	public void logoutShortcut() throws IOException
	{
		if(masterAdmin.equalsIgnoreCase("no"))
		{
			if(logoutchalkbox==true)
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
			}

			if(logouteducate==true)
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect("LoginPageEducatePro.xhtml");
			}
		}
		else
		{
			backToMaster();
		}

	}

	public void tracking()
	{
		if(list.getGps().equalsIgnoreCase("yes"))
		{

			if(list.getGpsUser()==null || list.getGpsPwd()==null || list.getGpsUser().equals("") || list.getGpsPwd().equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Tracking is Unavailable in This Account."));
			}
			else
			{
				String strUrl = "https://track.chalkboxerp.in/Login_end?e="+list.getGpsUser()+"&p="+list.getGpsPwd();

				PrimeFaces.current().executeInitScript("window.open('"+strUrl+"')");
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Tracking is Unavailable in This Account."));
		}

	}

	public void navigate() throws IOException
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		if(list.getType().equalsIgnoreCase("basic"))
		{
			ec.redirect("dashboardBasic.xhtml");
		}
		else if(list.getType().equalsIgnoreCase("novice"))
		{
			ec.redirect("Dashboard.xhtml");
		}
		else if(list.getType().equalsIgnoreCase("foster"))
		{
			ec.redirect("dashboardFoster.xhtml");
		}
	}

	public void backToMaster()
	{
		HttpSession sss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sss.invalidate();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		ss.setAttribute("username", "master");
		ss.setAttribute("type", "master_admin");

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("master/masterAdminDashboard.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void permissionSetting(Connection conn)
	{
		for(SelectItem ss:pageList)
		{
			if(ss.getLabel().equalsIgnoreCase("Change Session"))
			{
				changeSession=true;
			}
			/*if(ss.getLabel().equalsIgnoreCase("School Calendar"))
		{
			schoolCalendar=true;
		}*/

			if(ss.getLabel().equalsIgnoreCase("Master Settings"))
			{
				masterSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Student"))
			{
				manageStudent=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Online Admission Request"))
			{
				manageOnlineAdmReq=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Class Test"))
			{
				manageClassTest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("EYFS Module"))
			{
				manageEYFS=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage App"))
			{
				manageApp=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Fee Collection"))
			{
				
				 session=new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
				 String[] sessionnew=session.split("-");
				 int sessionInt=Integer.parseInt(sessionnew[1]);
				if(sessionInt>=2021)
				{
					manageFeeCollection=false;
					
				}
				else
				{
					manageFeeCollection=true;
								
				}
			}
			if(ss.getLabel().equalsIgnoreCase("Installment Fee Collection"))
			{
				if(new DatabaseMethods1().installmentCheck(new DatabaseMethods1().schoolId(), conn))
				{
					manageInstallmentFeeCollection=true;
					
				}
				else
				{
					manageInstallmentFeeCollection=false;
									
				}
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Infirmary"))
			{
				manageInfirmary=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Imprest A/C"))
			{
				manageImprest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Attendance"))
			{
				manageAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Enquiry"))
			{
				manageEnquiryModule=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Communication"))
			{
				manageCommunication=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Admit Card"))
			{
				manageAdmitCard=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage TC"))
			{
				manageLevCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Library"))
			{
				manageLibCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Visitor"))
			{
				manageVisitorModule=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Print Document"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					managePrint=false;
				}
				else
				{
					managePrint=true;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Previous Session"))
			{
				managePreviousSession=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Reports"))
			{
				manageReports=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Timetable"))
			{
				if(list.getTimetable().equalsIgnoreCase("Manual"))
				{
					manageTimeTable=true;
				}
				else
				{
					manageTimeTable=false;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Leave Module"))
			{
				manageLeaveModule=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Subject Allocation Module"))
			{
				manageSubjectAllocationModule=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Staff"))
			{
				manageStaff=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Salary"))
			{
				manageSalary=true;
			}
			if(ss.getLabel().equalsIgnoreCase("RBSE MODULE"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					manageRbseModule=false;
				}
				else
				{
					manageRbseModule=true;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Print Certificate"))
			{

				printCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("RBSE CC/TC Module"))
			{
				manageRbseTCModule=true;
			}

			

			
			if(ss.getLabel().equalsIgnoreCase("Manage Accounting"))
			{
				manageAccounting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Accounts Module"))
			{
				manageAccount=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Hostel"))
			{
				managehostel=true;
			}

		}
	}
	public void innerPermissionSetting(Connection conn)
	{
		for(SelectItem ss:innerPageList)
		{
			/////////////////// MASTER SETTING
			if(ss.getLabel().equalsIgnoreCase("Permission Setting"))
			{
				permissionSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Auto Message Setting"))
			{
				manageAutoMessageSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Change Password"))
			{
				managePassword=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Class Master"))
			{
				manageClassMaster=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Exam Master"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					manageExamMaster=false;
				}
				else
				{
					manageExamMaster=true;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Fee Master"))
			{
				manageFeeMaster=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Concession Category"))
			{
				manageConcessionCategory=true;
			}
			if(ss.getLabel().equalsIgnoreCase("School Information"))
			{
				manageSchoolInformation=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Staff Logins"))
			{
				manageLoginDetails=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Logins"))
			{
				manageStudentLogin=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Semester Master"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					manageSemesterMaster=false;
				}
				else
				{
					manageSemesterMaster=true;
				}
			}
			if(ss.getLabel().equalsIgnoreCase("Subject Master"))
			{
				manageSubjectMaster=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Library Setting"))
			{
				manageLibrarySetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Salary Setting"))
			{
				manageSalarySetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Appointment Setting"))
			{
				manageAppointmentSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Transport Master"))
			{
				manageTransportmaster=true;
			}

			/////////////////// EYFS

			if(ss.getLabel().equalsIgnoreCase("EYFS Parameter Setting"))
			{
				manageEYFSParameter=true;
			}
			if(ss.getLabel().equalsIgnoreCase("EYFS Student Performance"))
			{
				manageEYFSPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("EYFS Reports"))
			{
				manageEYFSReport=true;
			}

			/////////////////// VISITOR

			if(ss.getLabel().equalsIgnoreCase("Add Visitor"))
			{
				manageAddVisitor=true;
			}

			if(ss.getLabel().equalsIgnoreCase("All Visitors"))
			{
				manageShowVisitor=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Student Pick Up"))
			{
				managePickup=true;
			}

			/////////////////// MANAGE STUDENT


			if(ss.getLabel().equalsIgnoreCase("All Student List"))
			{
				manageAllStudentList=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Concession Request"))
			{
				manageConcessionRequest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Class Transfer"))
			{
				manageClassTransfer=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Previous Fees"))
			{
				managePreviuosFee=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Search Student"))
			{
				manageSearchStudent=true;
				manageAllStudentList=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Performance"))
			{
				String ctype=new DatabaseMethods1().checkClientType(conn);
				if(ctype.equalsIgnoreCase("institute"))
				{
					manageStudentPerformance=false;
				}
				else
				{
					manageStudentPerformance=true;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Student Promotion"))
			{
				manageStudentPromotion=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Registration"))
			{
				manageStudentRegistration=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Student"))
			{
				manageStudentRegistration=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Bulk Concession Allotment"))
			{
				manageBulkConcession=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Behaviour"))
			{
				manageComplaintDiary=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Transport Details"))
			{
				manageTransportDetail=true;
			}
			
			if(ss.getLabel().equalsIgnoreCase("Update Roll No"))
			{
				manageRollNo=true;
			}
			////////////online admission UAE
			if(ss.getLabel().equalsIgnoreCase("Registration Applications"))
			{
				manageRegApplication=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Admission Applications"))
			{
				manageAdmApplication=true;
			}

			/////////////////// APP

			if(ss.getLabel().equalsIgnoreCase("Online Exam"))
			{
				manageOnlineExam=true;
			}
			
			if(ss.getLabel().equalsIgnoreCase("Add Calendar"))
			{
				manageAddCalendar=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Post Consent"))
			{
				manageConsent=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View Calendar"))
			{
				manageViewCalendar=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Concern/Feedback"))
			{
				manageConcernFeedback=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Leave Request"))
			{
				manageLeaveRequest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Events"))
			{
				manageEvents=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Gallery"))
			{
				manageGallery=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add E-Learning"))
			{
				manageELearing=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View E-Learning"))
			{
				manageViewELearning=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Video Gallery"))
			{
				manageVideoGallery=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Home Work / Notes"))
			{
				manageHomeWorkNotes=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Zoom Meeting"))
			{
				manageAddMeeting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View Zoom Meeting"))
			{
				manageViewEditDeleteMeeting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Meeting"))
			{
				manageMeeting=true;
			}
			
			if(ss.getLabel().equalsIgnoreCase("Manage Flyer"))
			{
				manageFlyer=true;
			}
			
			
			if(ss.getLabel().equalsIgnoreCase("News"))
			{
				manageNews=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Syllabus"))
			{
				manageSyllabus=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View Syllabus"))
			{
				manageViewSyllabus=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View/Edit Gallery"))
			{
				manageViewGallery=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View/Edit Homework"))
			{
				manageViewAssignment=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Directory"))
			{
				managePhoneBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Photo Requests"))
			{
				managePhotoRequest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Upload Timetable"))
			{
				if(list.getTimetable().equalsIgnoreCase("Manual"))
				{
					manageInstituteTimetable=false;
				}
				else
				{
					manageInstituteTimetable=true;
				}

			}

			///////////// Class Test
			if(ss.getLabel().equalsIgnoreCase("Add Class Test"))
			{
				manageAddClassTest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Print Performance"))
			{
				managePrintPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Test Performance"))
			{
				manageTestPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Wise ClassTest"))
			{
				manageClassTestReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Class Wise Test Performance"))
			{
				manageClassWiseTestReport=true;
			}

			////////////////// Attendance

			if(ss.getLabel().equalsIgnoreCase("Absent Students"))
			{
				manageAbsentStudentsReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Mark Attendance"))
			{
				manageMarkAttendence=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Show Attendance"))
			{
				manageShowAttendence=true;
			}
			//////////session

			if(ss.getLabel().equalsIgnoreCase("Previous Attendance Record"))
			{
				managePreviousAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Previous Fee Record"))
			{
				managePreviousFee=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Previous Marksheet"))
			{
				managePreviousMarksheet=true;
			}

			//////// Enquiry

			if(ss.getLabel().equalsIgnoreCase("Add Enquiry"))
			{
				if(list.getClient_type().equalsIgnoreCase("school"))
				{
					manageAddEnquiry=true;
					manageAddInstituteEnquiry=false;
				}
				else
				{
					manageAddEnquiry=false;
					manageAddInstituteEnquiry=true;
				}

			}
			if(ss.getLabel().equalsIgnoreCase("Show Enquiry"))
			{
				if(list.getClient_type().equalsIgnoreCase("school"))
				{
					manageShowEnquiryblm=true;
					manageShowEnquiry=false;
					manageShowInstituteEnquiry=false;
				}
				else
				{
					manageShowEnquiryblm=false;
					manageShowEnquiry=false;
					manageShowInstituteEnquiry=true;
				}
			}

			if(ss.getLabel().equalsIgnoreCase("Show Enquiry BLM")||ss.getLabel().equalsIgnoreCase("Prospectus Enquiry"))
			{
				if(list.getClient_type().equalsIgnoreCase("school"))
				{
					manageShowEnquiryblm=true;
					manageShowEnquiry=false;
					manageShowInstituteEnquiry=false;
				}
				else
				{
					manageShowEnquiryblm=false;
					manageShowEnquiry=false;
					manageShowInstituteEnquiry=true;
				}
			}

			
			if(ss.getLabel().equalsIgnoreCase("Message Pack Purchase"))
			{
				manageMesssagePack=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Message Template"))
			{
				manageTemplate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Group Message"))
			{
				manageGroupMessgae=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Random Message"))
			{
				manageRandomMessage=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Class Wise Message"))
			{
				manageSentMessage=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Wise Message"))
			{
				manageStudentMessage=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Route Wise Message"))
			{
				manageRouteMessage=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Enquiry Communication"))
			{
				manageEnqComm=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Tc Wise Communication"))
			{
				manageTCComm=true;
			}


			if(ss.getLabel().equalsIgnoreCase("Manage Bank"))
			{
				manageBank=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Expense"))
			{
				manageExpense=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Income"))
			{
				manageIncome=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Cheque"))
			{
				manageCheque=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Add Admit Card"))
			{
				manageAddAdminCard=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Print Admit Card"))
			{
				managePrintAdmitCard=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Edit/View"))
			{
				manageEditViewStruckOff=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Stu. Struck Off"))
			{
				manageStruckOff=true;
			}
			if(ss.getLabel().equalsIgnoreCase("TC Generate"))
			{
				manageTcGenerate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Exam Marks + Grade"))
			{
				manageExamMarksGrade=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Online Exam Report"))
			{
				onlineExamReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Final Marksheet"))
			{
				manageFinalMarksheet=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Unit Marksheet"))
			{
				manageUnitMarksheet=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Marks Average"))
			{
				manageMarksAverage=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Detailed Marks"))
			{
				manageDetailedMarks=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Overall Performance"))
			{
				manageStudentOverallPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("I-Card"))
			{
				manageIcard=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Extra Values"))
			{
				manageStudentExtraValue=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Cancelled Receipts"))
			{
				manageCancelledReceipts=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Category Wise Emp."))
			{
				manageCategoryWiseEmp=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Category Wise Strength"))
			{
				manageCategoryWiseStrength=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Category Wise Students"))
			{
				manageCategoryWiseStudents=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Class Strength"))
			{
				manageClassStrength=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Daily Fee Collection"))
			{
				manageDailyFeeCollection=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Daily Collection"))
			{
				manageDailyCollection=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Date Wise Admissions"))
			{
				manageDateWiseAdmissions=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Due Fees"))
			{
				manageDueFees=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Inactive Students"))
			{
				manageInactiveStudents=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Month Wise Attendance"))
			{
				manageMonthWiseAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Pending Leaves Student"))
			{
				managePendingLeavesStudent=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Previous Fees"))
			{
				managePreviousFees=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Route Wise Students"))
			{
				manageRouteWiseStudents=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Session Collection"))
			{
				manageSessionCollection=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Show Leaves"))
			{
				manageShowLeaves=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Stop Wise Students"))
			{
				manageStopWiseStudents=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Age Report"))
			{
				manageStudentAgeReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Attendance"))
			{
				manageStudentAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student TC Report"))
			{
				manageStudentTCReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("UnMarked Attendance"))
			{
				manageUnMarkedAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Paid Fee Report"))
			{
				managePaidFeeReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Fee Estimate Report"))
			{
				feeEstimateReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("View Attendence"))
			{
				manageViewAttendance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Staff Communication"))
			{
				manageStaffCommunication=true;
			}
			if(ss.getLabel().equalsIgnoreCase("TimeTable Setting"))
			{
				manageTimeTableSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Generate TimeTable"))
			{
				manageGenerateTimeTable=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Print TimeTable"))
			{
				managePrintTimeTable=true;
			}
			if(ss.getLabel().equalsIgnoreCase("My TimeTable"))
			{
				manageMyTimeTable=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Individual TimeTable"))
			{
				manageIndividualTimeTable=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Apply Leave"))
			{
				manageApplyLeave=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Check Leave"))
			{
				manageCheckLeave=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Arrange Lecture"))
			{
				manageArrangeLecture=true;
			}
			if(ss.getLabel().equalsIgnoreCase("All Lecture Arrangement"))
			{
				manageAllLectureArrangement=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Subject Allocation"))
			{
				manageSubjectAllocation=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Edit Subject Allocation"))
			{
				manageEditSubjectAllocation=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Employee Category"))
			{
				manageEmployeeCategory=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Employee"))
			{
				manageAddEmployee=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Edit Employee"))
			{
				manageEditEmployee=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Staff Attendance"))
			{
				manageStaffAtt=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Staff Attendance Report"))
			{
				manageStaffAttRep=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Absent Staff Report"))
			{
				manageAbsentStaff=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Apply Leave"))
			{
				manageStaffLeave=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Leave Requests"))
			{
				manageStaffLeaveRequest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Staff Leave Report"))
			{
				manageStaffLeaveRep=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Add RBSE Exam"))
			{
				manageAddRbseExam=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Student Performance"))
			{
				manageAddStudentPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Final Marksheet"))
			{
				manageStudentFinalMarksheet=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Wise Subject Report"))
			{
				manageStudentWiseSubjectReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Subject Wise Student Report"))
			{
				manageSubjectWiseStudentReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Message Ledger"))
			{
				manageSMSLedger=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Marks Print"))
			{
				manageStudentHandWrittenMarkList=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Bonafied Certificate"))
			{
				manageBonafiedCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Character Certificate"))
			{
				manageCharaceterCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Birth/Bonafide/Bank Certificate"))
			{
				manageMultipleCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Experience Certificate"))
			{
				manageExperienceCertificate=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Attendance Certificate"))
			{
				manageAttendanceCertificate=true;
			}
			

			if(ss.getLabel().equalsIgnoreCase("Class Wise Fee Collection Report"))
			{
				manageClassWiseFeeCollectionReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Concession Wise Student Report"))
			{
				manageConcessionCategoryWiseStudentReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Date Wise Fee Collection Report"))
			{
				manageDateWiseFeeCollectionReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Fee Structure Report"))
			{
				manageFeeStructureReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Fee Record Register"))
			{
				feeRecordRegister=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Disability Report"))
			{
				manageHandicappedReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Month Wise Fee Collection Report"))
			{
				manageMonthWiseFeeCollectionReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Religion Wise Report"))
			{
				manageReligionWiseReport=true;
			}
			

			//********Library inner pages********************LIBRARY MODULE INNER PAGES************************

			if(ss.getLabel().equalsIgnoreCase("Add Book"))
			{
				manageAddBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View/Edit Book"))
			{
				manageViewBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Book Quantity"))
			{
				manageBookQuantity=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assign Book"))
			{
				manageAssignBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Receive Book"))
			{
				manageReceiveBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("All Book Report"))
			{
				manageBookReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assigned Book Report"))
			{
				manageAssignedBookReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Received Book Report"))
			{
				manageReceivedBookReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Book History"))
			{
				manageStudentBookHistory=true;
			}

			

			if(ss.getLabel().equalsIgnoreCase("Generate Rank"))
			{
				manageGenerateRank=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Struck Off"))
			{
				manageRbscStruckOff=true;
			}
			if(ss.getLabel().equalsIgnoreCase("TC Generation"))
			{
				manageRbscTCGeneration=true;
			}
			if(ss.getLabel().equalsIgnoreCase("CC Generation"))
			{
				manageRbscCCGeneration=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Duplicate TC"))
			{
				manageRbscTCList=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Bank"))
			{
				manageAddBank=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Edit/View/Delete Bank"))
			{
				manageViewEditDeleteBank=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Expense"))
			{
				manageAddExpense=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View/Delete Expense"))
			{
				manageViewDeleteExpense=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Income"))
			{
				manageAddIncome=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View/Delete Income"))
			{
				manageViewDeleteIncome=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assest Sale"))
			{
				manageAssetSale=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assest Purchase"))
			{
				manageAssetPurchase=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assest Sale Report"))
			{
				manageAssetSale=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Assest Purchase Report"))
			{
				manageAssetPurchase=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Due Cheque"))
			{
				manageDueCheque=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Pending Transaction"))
			{
				managePendingTransaction=true;
			}

			////

			if(ss.getLabel().equalsIgnoreCase("Add Head"))
			{
				manageAddHead=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Account"))
			{
				manageAddAccount=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Transaction"))
			{
				manageTransaction=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Account Ledger"))
			{
				manageAccountLedger=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Pending Cheque"))
			{
				managePendingCheque=true;
			}

			////

			if(ss.getLabel().equalsIgnoreCase("Add Class Teacher"))
			{
				manageAddClassTeacher=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Manage Administrative Officer"))
			{
				manageAdministrativeOfficer=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Staff Birthday"))
			{
				manageStaffBirthday=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Birthday"))
			{
				manageStudentBirthday=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Edit Class Teacher"))
			{
				manageEditClassTeacher=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Old Student TC"))
			{
				manageOldRbscTCGeneration=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Old Duplicate TC"))
			{
				manageOldRbscTCList=true;
			}

			if(ss.getLabel().equalsIgnoreCase("General Reports"))
			{
				manageGeneralReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Student Reports"))
			{
				manageStudentReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Fee Reports"))
			{
				manageFeeReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Attendance Reports"))
			{
				manageAttendanceReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Library Reports"))
			{
				manageLibReport=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View Teacher Attendence(Self)"))
			{
				manageUniqueTeacherAttendece=true;
			}
			if(ss.getLabel().equalsIgnoreCase("View Teacher Attendence(Multiple)"))
			{
				manageMultipleTeacherAttendece=true;
			}
			

			if(ss.getLabel().equalsIgnoreCase("Graphical Reports"))
			{
				manageAnalaticReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Transport Reports"))
			{
				manageTransportReport=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Old Student Data"))
			{
				manageOldStudentSMS=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Lecture Arrangement"))
			{
				manageLectureArrangementModule=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Oasis Pre Marksheet"))
			{
				manageoasismarksheetupto3=true;
			}

			if(ss.getLabel().equalsIgnoreCase("Upload Student Marksheet"))
			{
				manageuploadstudentmarksheet=true;
			}
		}
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public void changePassword()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("changePassWord.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}


	public String logout()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.invalidate();
		return "LoginPageEducatePro.xhtml";
	}


	public ArrayList<ModuleInfo> getModuleList() {
		return moduleList;
	}
	public void setModuleList(ArrayList<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}
	public ArrayList<SelectItem> getPageList() {
		return pageList;
	}
	public void setPageList(ArrayList<SelectItem> pageList) {
		this.pageList = pageList;
	}
	public ArrayList<SelectItem> getInnerPageList() {
		return innerPageList;
	}
	public void setInnerPageList(ArrayList<SelectItem> innerPageList) {
		this.innerPageList = innerPageList;
	}
	public boolean isChangeSession() {
		return changeSession;
	}
	public void setChangeSession(boolean changeSession) {
		this.changeSession = changeSession;
	}


	public boolean isPermissionSetting() {
		return permissionSetting;
	}
	public void setPermissionSetting(boolean permissionSetting) {
		this.permissionSetting = permissionSetting;
	}
	public boolean isMasterSetting() {
		return masterSetting;
	}
	public void setMasterSetting(boolean masterSetting) {
		this.masterSetting = masterSetting;
	}
	public boolean isManageStudent() {
		return manageStudent;
	}
	public void setManageStudent(boolean manageStudent) {
		this.manageStudent = manageStudent;
	}
	public boolean isManageClassTest() {
		return manageClassTest;
	}
	public void setManageClassTest(boolean manageClassTest) {
		this.manageClassTest = manageClassTest;
	}
	public boolean isManageApp() {
		return manageApp;
	}
	public void setManageApp(boolean manageApp) {
		this.manageApp = manageApp;
	}
	public boolean isManageFeeCollection() {
		return manageFeeCollection;
	}
	public void setManageFeeCollection(boolean manageFeeCollection) {
		this.manageFeeCollection = manageFeeCollection;
	}
	public boolean isManageAttendance() {
		return manageAttendance;
	}
	public void setManageAttendance(boolean manageAttendance) {
		this.manageAttendance = manageAttendance;
	}
	public boolean isManageEnquiryModule() {
		return manageEnquiryModule;
	}
	public void setManageEnquiryModule(boolean manageEnquiryModule) {
		this.manageEnquiryModule = manageEnquiryModule;
	}
	public boolean isManageCommunication() {
		return manageCommunication;
	}
	public void setManageCommunication(boolean manageCommunication) {
		this.manageCommunication = manageCommunication;
	}
	public boolean isManageAdmitCard() {
		return manageAdmitCard;
	}
	public void setManageAdmitCard(boolean manageAdmitCard) {
		this.manageAdmitCard = manageAdmitCard;
	}
	public boolean isManageLevCertificate() {
		return manageLevCertificate;
	}
	public void setManageLevCertificate(boolean manageLevCertificate) {
		this.manageLevCertificate = manageLevCertificate;
	}
	public boolean isManagePrint() {
		return managePrint;
	}
	public void setManagePrint(boolean managePrint) {
		this.managePrint = managePrint;
	}
	public boolean isManageReports() {
		return manageReports;
	}
	public void setManageReports(boolean manageReports) {
		this.manageReports = manageReports;
	}
	public boolean isManageAutoMessageSetting() {
		return manageAutoMessageSetting;
	}
	public void setManageAutoMessageSetting(boolean manageAutoMessageSetting) {
		this.manageAutoMessageSetting = manageAutoMessageSetting;
	}
	public boolean isManagePassword() {
		return managePassword;
	}
	public void setManagePassword(boolean managePassword) {
		this.managePassword = managePassword;
	}
	public boolean isManageClassMaster() {
		return manageClassMaster;
	}
	public void setManageClassMaster(boolean manageClassMaster) {
		this.manageClassMaster = manageClassMaster;
	}
	public boolean isManageExamMaster() {
		return manageExamMaster;
	}
	public void setManageExamMaster(boolean manageExamMaster) {
		this.manageExamMaster = manageExamMaster;
	}
	public boolean isManageFeeMaster() {
		return manageFeeMaster;
	}
	public void setManageFeeMaster(boolean manageFeeMaster) {
		this.manageFeeMaster = manageFeeMaster;
	}
	public boolean isManageSchoolInformation() {
		return manageSchoolInformation;
	}
	public void setManageSchoolInformation(boolean manageSchoolInformation) {
		this.manageSchoolInformation = manageSchoolInformation;
	}
	public boolean isManageSemesterMaster() {
		return manageSemesterMaster;
	}
	public void setManageSemesterMaster(boolean manageSemesterMaster) {
		this.manageSemesterMaster = manageSemesterMaster;
	}
	public boolean isManageSubjectMaster() {
		return manageSubjectMaster;
	}
	public void setManageSubjectMaster(boolean manageSubjectMaster) {
		this.manageSubjectMaster = manageSubjectMaster;
	}
	public boolean isManageTransportmaster() {
		return manageTransportmaster;
	}
	public void setManageTransportmaster(boolean manageTransportmaster) {
		this.manageTransportmaster = manageTransportmaster;
	}
	public boolean isManageAllStudentList() {
		return manageAllStudentList;
	}
	public void setManageAllStudentList(boolean manageAllStudentList) {
		this.manageAllStudentList = manageAllStudentList;
	}
	public boolean isManageClassTransfer() {
		return manageClassTransfer;
	}
	public void setManageClassTransfer(boolean manageClassTransfer) {
		this.manageClassTransfer = manageClassTransfer;
	}
	public boolean isManagePreviuosFee() {
		return managePreviuosFee;
	}
	public void setManagePreviuosFee(boolean managePreviuosFee) {
		this.managePreviuosFee = managePreviuosFee;
	}
	public boolean isManageSearchStudent() {
		return manageSearchStudent;
	}
	public void setManageSearchStudent(boolean manageSearchStudent) {
		this.manageSearchStudent = manageSearchStudent;
	}
	public boolean isManageStudentPerformance() {
		return manageStudentPerformance;
	}
	public void setManageStudentPerformance(boolean manageStudentPerformance) {
		this.manageStudentPerformance = manageStudentPerformance;
	}
	public boolean isManageStudentPromotion() {
		return manageStudentPromotion;
	}
	public void setManageStudentPromotion(boolean manageStudentPromotion) {
		this.manageStudentPromotion = manageStudentPromotion;
	}
	public boolean isManageStudentRegistration() {
		return manageStudentRegistration;
	}
	public void setManageStudentRegistration(boolean manageStudentRegistration) {
		this.manageStudentRegistration = manageStudentRegistration;
	}
	public boolean isManageTransportDetail() {
		return manageTransportDetail;
	}
	public void setManageTransportDetail(boolean manageTransportDetail) {
		this.manageTransportDetail = manageTransportDetail;
	}
	public boolean isManageAddClassTest() {
		return manageAddClassTest;
	}
	public void setManageAddClassTest(boolean manageAddClassTest) {
		this.manageAddClassTest = manageAddClassTest;
	}
	public boolean isManagePrintPerformance() {
		return managePrintPerformance;
	}
	public void setManagePrintPerformance(boolean managePrintPerformance) {
		this.managePrintPerformance = managePrintPerformance;
	}
	public boolean isManageTestPerformance() {
		return manageTestPerformance;
	}
	public void setManageTestPerformance(boolean manageTestPerformance) {
		this.manageTestPerformance = manageTestPerformance;
	}
	public boolean isManageClassTestReport() {
		return manageClassTestReport;
	}
	public void setManageClassTestReport(boolean manageClassTestReport) {
		this.manageClassTestReport = manageClassTestReport;
	}
	public boolean isManageAbsentStudentsReport() {
		return manageAbsentStudentsReport;
	}
	public void setManageAbsentStudentsReport(boolean manageAbsentStudentsReport) {
		this.manageAbsentStudentsReport = manageAbsentStudentsReport;
	}
	public boolean isManageMarkAttendence() {
		return manageMarkAttendence;
	}
	public void setManageMarkAttendence(boolean manageMarkAttendence) {
		this.manageMarkAttendence = manageMarkAttendence;
	}
	public boolean isManageShowAttendence() {
		return manageShowAttendence;
	}
	public void setManageShowAttendence(boolean manageShowAttendence) {
		this.manageShowAttendence = manageShowAttendence;
	}
	public boolean isManageAddEnquiry() {
		return manageAddEnquiry;
	}
	public void setManageAddEnquiry(boolean manageAddEnquiry) {
		this.manageAddEnquiry = manageAddEnquiry;
	}
	public boolean isManageShowEnquiry() {
		return manageShowEnquiry;
	}
	public void setManageShowEnquiry(boolean manageShowEnquiry) {
		this.manageShowEnquiry = manageShowEnquiry;
	}
	public boolean isManageGroupMessgae() {
		return manageGroupMessgae;
	}
	public void setManageGroupMessgae(boolean manageGroupMessgae) {
		this.manageGroupMessgae = manageGroupMessgae;
	}
	public boolean isManageRandomMessage() {
		return manageRandomMessage;
	}
	public void setManageRandomMessage(boolean manageRandomMessage) {
		this.manageRandomMessage = manageRandomMessage;
	}
	public boolean isManageSentMessage() {
		return manageSentMessage;
	}
	public void setManageSentMessage(boolean manageSentMessage) {
		this.manageSentMessage = manageSentMessage;
	}
	public boolean isManageAddAdminCard() {
		return manageAddAdminCard;
	}
	public void setManageAddAdminCard(boolean manageAddAdminCard) {
		this.manageAddAdminCard = manageAddAdminCard;
	}
	public boolean isManagePrintAdmitCard() {
		return managePrintAdmitCard;
	}
	public void setManagePrintAdmitCard(boolean managePrintAdmitCard) {
		this.managePrintAdmitCard = managePrintAdmitCard;
	}
	public boolean isManageTcGenerate() {
		return manageTcGenerate;
	}
	public void setManageTcGenerate(boolean manageTcGenerate) {
		this.manageTcGenerate = manageTcGenerate;
	}
	public boolean isManageStruckOff() {
		return manageStruckOff;
	}
	public void setManageStruckOff(boolean manageStruckOff) {
		this.manageStruckOff = manageStruckOff;
	}
	public boolean isManageEditViewStruckOff() {
		return manageEditViewStruckOff;
	}
	public void setManageEditViewStruckOff(boolean manageEditViewStruckOff) {
		this.manageEditViewStruckOff = manageEditViewStruckOff;
	}
	public boolean isManageExamMarksGrade() {
		return manageExamMarksGrade;
	}
	public void setManageExamMarksGrade(boolean manageExamMarksGrade) {
		this.manageExamMarksGrade = manageExamMarksGrade;
	}
	public boolean isManageFinalMarksheet() {
		return manageFinalMarksheet;
	}
	public void setManageFinalMarksheet(boolean manageFinalMarksheet) {
		this.manageFinalMarksheet = manageFinalMarksheet;
	}
	public boolean isManageIcard() {
		return manageIcard;
	}
	public void setManageIcard(boolean manageIcard) {
		this.manageIcard = manageIcard;
	}
	public boolean isManageStudentExtraValue() {
		return manageStudentExtraValue;
	}
	public void setManageStudentExtraValue(boolean manageStudentExtraValue) {
		this.manageStudentExtraValue = manageStudentExtraValue;
	}
	public boolean isManageCancelledReceipts() {
		return manageCancelledReceipts;
	}
	public void setManageCancelledReceipts(boolean manageCancelledReceipts) {
		this.manageCancelledReceipts = manageCancelledReceipts;
	}
	public boolean isManageCategoryWiseEmp() {
		return manageCategoryWiseEmp;
	}
	public void setManageCategoryWiseEmp(boolean manageCategoryWiseEmp) {
		this.manageCategoryWiseEmp = manageCategoryWiseEmp;
	}
	public boolean isManageCategoryWiseStrength() {
		return manageCategoryWiseStrength;
	}
	public void setManageCategoryWiseStrength(boolean manageCategoryWiseStrength) {
		this.manageCategoryWiseStrength = manageCategoryWiseStrength;
	}
	public boolean isManageCategoryWiseStudents() {
		return manageCategoryWiseStudents;
	}
	public void setManageCategoryWiseStudents(boolean manageCategoryWiseStudents) {
		this.manageCategoryWiseStudents = manageCategoryWiseStudents;
	}
	public boolean isManageClassStrength() {
		return manageClassStrength;
	}
	public void setManageClassStrength(boolean manageClassStrength) {
		this.manageClassStrength = manageClassStrength;
	}
	public boolean isManageEvents() {
		return manageEvents;
	}
	public void setManageEvents(boolean manageEvents) {
		this.manageEvents = manageEvents;
	}
	public boolean isManageGallery() {
		return manageGallery;
	}
	public void setManageGallery(boolean manageGallery) {
		this.manageGallery = manageGallery;
	}
	public boolean isManageHomeWorkNotes() {
		return manageHomeWorkNotes;
	}
	public void setManageHomeWorkNotes(boolean manageHomeWorkNotes) {
		this.manageHomeWorkNotes = manageHomeWorkNotes;
	}
	public boolean isManageNews() {
		return manageNews;
	}
	public void setManageNews(boolean manageNews) {
		this.manageNews = manageNews;
	}
	public boolean isManageSyllabus() {
		return manageSyllabus;
	}
	public void setManageSyllabus(boolean manageSyllabus) {
		this.manageSyllabus = manageSyllabus;
	}
	public boolean isManageDailyFeeCollection() {
		return manageDailyFeeCollection;
	}
	public void setManageDailyFeeCollection(boolean manageDailyFeeCollection) {
		this.manageDailyFeeCollection = manageDailyFeeCollection;
	}
	public boolean isManageDateWiseAdmissions() {
		return manageDateWiseAdmissions;
	}
	public void setManageDateWiseAdmissions(boolean manageDateWiseAdmissions) {
		this.manageDateWiseAdmissions = manageDateWiseAdmissions;
	}
	public boolean isManageDueFees() {
		return manageDueFees;
	}
	public void setManageDueFees(boolean manageDueFees) {
		this.manageDueFees = manageDueFees;
	}
	public boolean isManageInactiveStudents() {
		return manageInactiveStudents;
	}
	public void setManageInactiveStudents(boolean manageInactiveStudents) {
		this.manageInactiveStudents = manageInactiveStudents;
	}
	public boolean isManageMonthWiseAttendance() {
		return manageMonthWiseAttendance;
	}
	public void setManageMonthWiseAttendance(boolean manageMonthWiseAttendance) {
		this.manageMonthWiseAttendance = manageMonthWiseAttendance;
	}
	public boolean isManagePendingLeavesStudent() {
		return managePendingLeavesStudent;
	}
	public void setManagePendingLeavesStudent(boolean managePendingLeavesStudent) {
		this.managePendingLeavesStudent = managePendingLeavesStudent;
	}
	public boolean isManagePreviousFees() {
		return managePreviousFees;
	}
	public void setManagePreviousFees(boolean managePreviousFees) {
		this.managePreviousFees = managePreviousFees;
	}
	public boolean isManageRouteWiseStudents() {
		return manageRouteWiseStudents;
	}
	public void setManageRouteWiseStudents(boolean manageRouteWiseStudents) {
		this.manageRouteWiseStudents = manageRouteWiseStudents;
	}
	public boolean isManageSessionCollection() {
		return manageSessionCollection;
	}
	public void setManageSessionCollection(boolean manageSessionCollection) {
		this.manageSessionCollection = manageSessionCollection;
	}
	public boolean isManageShowLeaves() {
		return manageShowLeaves;
	}
	public void setManageShowLeaves(boolean manageShowLeaves) {
		this.manageShowLeaves = manageShowLeaves;
	}
	public boolean isManageStopWiseStudents() {
		return manageStopWiseStudents;
	}
	public void setManageStopWiseStudents(boolean manageStopWiseStudents) {
		this.manageStopWiseStudents = manageStopWiseStudents;
	}
	public boolean isManageStudentAgeReport() {
		return manageStudentAgeReport;
	}
	public void setManageStudentAgeReport(boolean manageStudentAgeReport) {
		this.manageStudentAgeReport = manageStudentAgeReport;
	}
	public boolean isManageStudentAttendance() {
		return manageStudentAttendance;
	}
	public void setManageStudentAttendance(boolean manageStudentAttendance) {
		this.manageStudentAttendance = manageStudentAttendance;
	}
	public boolean isManageStudentTCReport() {
		return manageStudentTCReport;
	}
	public void setManageStudentTCReport(boolean manageStudentTCReport) {
		this.manageStudentTCReport = manageStudentTCReport;
	}
	public boolean isManageUnMarkedAttendance() {
		return manageUnMarkedAttendance;
	}
	public void setManageUnMarkedAttendance(boolean manageUnMarkedAttendance) {
		this.manageUnMarkedAttendance = manageUnMarkedAttendance;
	}
	public boolean isManagePaidFeeReport() {
		return managePaidFeeReport;
	}
	public void setManagePaidFeeReport(boolean managePaidFeeReport) {
		this.managePaidFeeReport = managePaidFeeReport;
	}
	public boolean isManageViewAttendance() {
		return manageViewAttendance;
	}
	public void setManageViewAttendance(boolean manageViewAttendance) {
		this.manageViewAttendance = manageViewAttendance;
	}
	public boolean isManageStaffCommunication() {
		return manageStaffCommunication;
	}
	public void setManageStaffCommunication(boolean manageStaffCommunication) {
		this.manageStaffCommunication = manageStaffCommunication;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public boolean isLogoutchalkbox() {
		return logoutchalkbox;
	}
	public void setLogoutchalkbox(boolean logoutchalkbox) {
		this.logoutchalkbox = logoutchalkbox;
	}
	public boolean isLogouteducate() {
		return logouteducate;
	}
	public void setLogouteducate(boolean logouteducate) {
		this.logouteducate = logouteducate;
	}
	public int getMessageBalanace() {
		return messageBalanace;
	}
	public void setMessageBalanace(int messageBalanace) {
		this.messageBalanace = messageBalanace;
	}
	public boolean isManageTimeTable() {
		return manageTimeTable;
	}
	public void setManageTimeTable(boolean manageTimeTable) {
		this.manageTimeTable = manageTimeTable;
	}
	public boolean isManageTimeTableSetting() {
		return manageTimeTableSetting;
	}
	public void setManageTimeTableSetting(boolean manageTimeTableSetting) {
		this.manageTimeTableSetting = manageTimeTableSetting;
	}
	public boolean isManageGenerateTimeTable() {
		return manageGenerateTimeTable;
	}
	public void setManageGenerateTimeTable(boolean manageGenerateTimeTable) {
		this.manageGenerateTimeTable = manageGenerateTimeTable;
	}
	public boolean isManagePrintTimeTable() {
		return managePrintTimeTable;
	}
	public void setManagePrintTimeTable(boolean managePrintTimeTable) {
		this.managePrintTimeTable = managePrintTimeTable;
	}
	public boolean isManageMyTimeTable() {
		return manageMyTimeTable;
	}
	public void setManageMyTimeTable(boolean manageMyTimeTable) {
		this.manageMyTimeTable = manageMyTimeTable;
	}
	public boolean isManageIndividualTimeTable() {
		return manageIndividualTimeTable;
	}
	public void setManageIndividualTimeTable(boolean manageIndividualTimeTable) {
		this.manageIndividualTimeTable = manageIndividualTimeTable;
	}
	public boolean isManageLeaveModule() {
		return manageLeaveModule;
	}
	public void setManageLeaveModule(boolean manageLeaveModule) {
		this.manageLeaveModule = manageLeaveModule;
	}
	public boolean isManageApplyLeave() {
		return manageApplyLeave;
	}
	public void setManageApplyLeave(boolean manageApplyLeave) {
		this.manageApplyLeave = manageApplyLeave;
	}
	public boolean isManageCheckLeave() {
		return manageCheckLeave;
	}
	public void setManageCheckLeave(boolean manageCheckLeave) {
		this.manageCheckLeave = manageCheckLeave;
	}
	public boolean isManageLectureArrangementModule() {
		return manageLectureArrangementModule;
	}
	public void setManageLectureArrangementModule(
			boolean manageLectureArrangementModule) {
		this.manageLectureArrangementModule = manageLectureArrangementModule;
	}
	public boolean isManageArrangeLecture() {
		return manageArrangeLecture;
	}
	public void setManageArrangeLecture(boolean manageArrangeLecture) {
		this.manageArrangeLecture = manageArrangeLecture;
	}
	public boolean isManageAllLectureArrangement() {
		return manageAllLectureArrangement;
	}
	public void setManageAllLectureArrangement(boolean manageAllLectureArrangement) {
		this.manageAllLectureArrangement = manageAllLectureArrangement;
	}
	public boolean isManageSubjectAllocationModule() {
		return manageSubjectAllocationModule;
	}
	public void setManageSubjectAllocationModule(
			boolean manageSubjectAllocationModule) {
		this.manageSubjectAllocationModule = manageSubjectAllocationModule;
	}
	public boolean isManageSubjectAllocation() {
		return manageSubjectAllocation;
	}
	public void setManageSubjectAllocation(boolean manageSubjectAllocation) {
		this.manageSubjectAllocation = manageSubjectAllocation;
	}
	public boolean isManageEditSubjectAllocation() {
		return manageEditSubjectAllocation;
	}
	public void setManageEditSubjectAllocation(boolean manageEditSubjectAllocation) {
		this.manageEditSubjectAllocation = manageEditSubjectAllocation;
	}
	public boolean isManageStaff() {
		return manageStaff;
	}
	public void setManageStaff(boolean manageStaff) {
		this.manageStaff = manageStaff;
	}
	public boolean isManageEmployeeCategory() {
		return manageEmployeeCategory;
	}
	public void setManageEmployeeCategory(boolean manageEmployeeCategory) {
		this.manageEmployeeCategory = manageEmployeeCategory;
	}
	public boolean isManageAddEmployee() {
		return manageAddEmployee;
	}
	public void setManageAddEmployee(boolean manageAddEmployee) {
		this.manageAddEmployee = manageAddEmployee;
	}
	public boolean isManageEditEmployee() {
		return manageEditEmployee;
	}
	public void setManageEditEmployee(boolean manageEditEmployee) {
		this.manageEditEmployee = manageEditEmployee;
	}
	public boolean isManageRbseModule() {
		return manageRbseModule;
	}
	public void setManageRbseModule(boolean manageRbseModule) {
		this.manageRbseModule = manageRbseModule;
	}
	public boolean isManageAddRbseExam() {
		return manageAddRbseExam;
	}
	public void setManageAddRbseExam(boolean manageAddRbseExam) {
		this.manageAddRbseExam = manageAddRbseExam;
	}
	public boolean isManageAddStudentPerformance() {
		return manageAddStudentPerformance;
	}
	public void setManageAddStudentPerformance(boolean manageAddStudentPerformance) {
		this.manageAddStudentPerformance = manageAddStudentPerformance;
	}
	public boolean isManageStudentFinalMarksheet() {
		return manageStudentFinalMarksheet;
	}
	public void setManageStudentFinalMarksheet(boolean manageStudentFinalMarksheet) {
		this.manageStudentFinalMarksheet = manageStudentFinalMarksheet;
	}
	public boolean isSchoolCalendar() {
		return schoolCalendar;
	}
	public void setSchoolCalendar(boolean schoolCalendar) {
		this.schoolCalendar = schoolCalendar;
	}
	public boolean isManageAddCalendar() {
		return manageAddCalendar;
	}
	public void setManageAddCalendar(boolean manageAddCalendar) {
		this.manageAddCalendar = manageAddCalendar;
	}
	public boolean isManageViewCalendar() {
		return manageViewCalendar;
	}
	public void setManageViewCalendar(boolean manageViewCalendar) {
		this.manageViewCalendar = manageViewCalendar;
	}
	public boolean isManageViewGallery() {
		return manageViewGallery;
	}
	public void setManageViewGallery(boolean manageViewGallery) {
		this.manageViewGallery = manageViewGallery;
	}
	public boolean isManageViewAssignment() {
		return manageViewAssignment;
	}
	public void setManageViewAssignment(boolean manageViewAssignment) {
		this.manageViewAssignment = manageViewAssignment;
	}
	public boolean isManageSubjectWiseStudentReport() {
		return manageSubjectWiseStudentReport;
	}
	public void setManageSubjectWiseStudentReport(
			boolean manageSubjectWiseStudentReport) {
		this.manageSubjectWiseStudentReport = manageSubjectWiseStudentReport;
	}
	public boolean isManageStudentWiseSubjectReport() {
		return manageStudentWiseSubjectReport;
	}
	public void setManageStudentWiseSubjectReport(
			boolean manageStudentWiseSubjectReport) {
		this.manageStudentWiseSubjectReport = manageStudentWiseSubjectReport;
	}
	public boolean isManageStudentHandWrittenMarkList() {
		return manageStudentHandWrittenMarkList;
	}
	public void setManageStudentHandWrittenMarkList(
			boolean manageStudentHandWrittenMarkList) {
		this.manageStudentHandWrittenMarkList = manageStudentHandWrittenMarkList;
	}
	public boolean isPrintCertificate() {
		return printCertificate;
	}
	public void setPrintCertificate(boolean printCertificate) {
		this.printCertificate = printCertificate;
	}
	public boolean isManageBonafiedCertificate() {
		return manageBonafiedCertificate;
	}
	public void setManageBonafiedCertificate(boolean manageBonafiedCertificate) {
		this.manageBonafiedCertificate = manageBonafiedCertificate;
	}
	public boolean isManageCharaceterCertificate() {
		return manageCharaceterCertificate;
	}
	public void setManageCharaceterCertificate(boolean manageCharaceterCertificate) {
		this.manageCharaceterCertificate = manageCharaceterCertificate;
	}
	
	public boolean isManageClassWiseFeeCollectionReport() {
		return manageClassWiseFeeCollectionReport;
	}
	public void setManageClassWiseFeeCollectionReport(
			boolean manageClassWiseFeeCollectionReport) {
		this.manageClassWiseFeeCollectionReport = manageClassWiseFeeCollectionReport;
	}
	public boolean isManageConcessionCategoryWiseStudentReport() {
		return manageConcessionCategoryWiseStudentReport;
	}
	public void setManageConcessionCategoryWiseStudentReport(
			boolean manageConcessionCategoryWiseStudentReport) {
		this.manageConcessionCategoryWiseStudentReport = manageConcessionCategoryWiseStudentReport;
	}
	public boolean isManageDateWiseFeeCollectionReport() {
		return manageDateWiseFeeCollectionReport;
	}
	public void setManageDateWiseFeeCollectionReport(
			boolean manageDateWiseFeeCollectionReport) {
		this.manageDateWiseFeeCollectionReport = manageDateWiseFeeCollectionReport;
	}
	public boolean isManageFeeStructureReport() {
		return manageFeeStructureReport;
	}
	public void setManageFeeStructureReport(boolean manageFeeStructureReport) {
		this.manageFeeStructureReport = manageFeeStructureReport;
	}
	public boolean isManageHandicappedReport() {
		return manageHandicappedReport;
	}
	public void setManageHandicappedReport(boolean manageHandicappedReport) {
		this.manageHandicappedReport = manageHandicappedReport;
	}
	public boolean isManageMonthWiseFeeCollectionReport() {
		return manageMonthWiseFeeCollectionReport;
	}
	public void setManageMonthWiseFeeCollectionReport(
			boolean manageMonthWiseFeeCollectionReport) {
		this.manageMonthWiseFeeCollectionReport = manageMonthWiseFeeCollectionReport;
	}
	public boolean isManageReligionWiseReport() {
		return manageReligionWiseReport;
	}
	public void setManageReligionWiseReport(boolean manageReligionWiseReport) {
		this.manageReligionWiseReport = manageReligionWiseReport;
	}
	public boolean isManageLoginDetails() {
		return manageLoginDetails;
	}
	public void setManageLoginDetails(boolean manageLoginDetails) {
		this.manageLoginDetails = manageLoginDetails;
	}
	public boolean isShowTeacher() {
		return showTeacher;
	}
	public void setShowTeacher(boolean showTeacher) {
		this.showTeacher = showTeacher;
	}
	public boolean isShowAdmin() {
		return showAdmin;
	}
	public void setShowAdmin(boolean showAdmin) {
		this.showAdmin = showAdmin;
	}
	public boolean isManageLibCertificate() {
		return manageLibCertificate;
	}
	public void setManageLibCertificate(boolean manageLibCertificate) {
		this.manageLibCertificate = manageLibCertificate;
	}
	public boolean isManageBookReport() {
		return manageBookReport;
	}
	public void setManageBookReport(boolean manageBookReport) {
		this.manageBookReport = manageBookReport;
	}
	public boolean isManageAssignedBookReport() {
		return manageAssignedBookReport;
	}
	public void setManageAssignedBookReport(boolean manageAssignedBookReport) {
		this.manageAssignedBookReport = manageAssignedBookReport;
	}
	public boolean isManageReceivedBookReport() {
		return manageReceivedBookReport;
	}
	public void setManageReceivedBookReport(boolean manageReceivedBookReport) {
		this.manageReceivedBookReport = manageReceivedBookReport;
	}
	public boolean isManageStudentBookHistory() {
		return manageStudentBookHistory;
	}
	public void setManageStudentBookHistory(boolean manageStudentBookHistory) {
		this.manageStudentBookHistory = manageStudentBookHistory;
	}
	public boolean isManageAddBook() {
		return manageAddBook;
	}
	public void setManageAddBook(boolean manageAddBook) {
		this.manageAddBook = manageAddBook;
	}
	public boolean isManageViewBook() {
		return manageViewBook;
	}
	public void setManageViewBook(boolean manageViewBook) {
		this.manageViewBook = manageViewBook;
	}
	public boolean isManageBookQuantity() {
		return manageBookQuantity;
	}
	public void setManageBookQuantity(boolean manageBookQuantity) {
		this.manageBookQuantity = manageBookQuantity;
	}
	public boolean isManageAssignBook() {
		return manageAssignBook;
	}
	public void setManageAssignBook(boolean manageAssignBook) {
		this.manageAssignBook = manageAssignBook;
	}
	public boolean isManageReceiveBook() {
		return manageReceiveBook;
	}
	public void setManageReceiveBook(boolean manageReceiveBook) {
		this.manageReceiveBook = manageReceiveBook;
	}
	public boolean isManagePhoneBook() {
		return managePhoneBook;
	}
	public void setManagePhoneBook(boolean managePhoneBook) {
		this.managePhoneBook = managePhoneBook;
	}
	public boolean isManageRollNo() {
		return manageRollNo;
	}
	public void setManageRollNo(boolean manageRollNo) {
		this.manageRollNo = manageRollNo;
	}
	public boolean isManageRbseTCModule() {
		return manageRbseTCModule;
	}
	public void setManageRbseTCModule(boolean manageRbseTCModule) {
		this.manageRbseTCModule = manageRbseTCModule;
	}
	public boolean isManageRbscStruckOff() {
		return manageRbscStruckOff;
	}
	public void setManageRbscStruckOff(boolean manageRbscStruckOff) {
		this.manageRbscStruckOff = manageRbscStruckOff;
	}
	public boolean isManageRbscTCGeneration() {
		return manageRbscTCGeneration;
	}
	public void setManageRbscTCGeneration(boolean manageRbscTCGeneration) {
		this.manageRbscTCGeneration = manageRbscTCGeneration;
	}
	public boolean isManageRbscCCGeneration() {
		return manageRbscCCGeneration;
	}
	public void setManageRbscCCGeneration(boolean manageRbscCCGeneration) {
		this.manageRbscCCGeneration = manageRbscCCGeneration;
	}
	public boolean isManageRbscTCList() {
		return manageRbscTCList;
	}
	public void setManageRbscTCList(boolean manageRbscTCList) {
		this.manageRbscTCList = manageRbscTCList;
	}
	public boolean isManageBank() {
		return manageBank;
	}
	public void setManageBank(boolean manageBank) {
		this.manageBank = manageBank;
	}
	public boolean isManageAddBank() {
		return manageAddBank;
	}
	public void setManageAddBank(boolean manageAddBank) {
		this.manageAddBank = manageAddBank;
	}
	public boolean isManageViewEditDeleteBank() {
		return manageViewEditDeleteBank;
	}
	public void setManageViewEditDeleteBank(boolean manageViewEditDeleteBank) {
		this.manageViewEditDeleteBank = manageViewEditDeleteBank;
	}
	public boolean isManageExpense() {
		return manageExpense;
	}
	public void setManageExpense(boolean manageExpense) {
		this.manageExpense = manageExpense;
	}
	public boolean isManageAddExpense() {
		return manageAddExpense;
	}
	public void setManageAddExpense(boolean manageAddExpense) {
		this.manageAddExpense = manageAddExpense;
	}
	public boolean isManageViewDeleteExpense() {
		return manageViewDeleteExpense;
	}
	public void setManageViewDeleteExpense(boolean manageViewDeleteExpense) {
		this.manageViewDeleteExpense = manageViewDeleteExpense;
	}
	public boolean isManageIncome() {
		return manageIncome;
	}
	public void setManageIncome(boolean manageIncome) {
		this.manageIncome = manageIncome;
	}
	public boolean isManageAddIncome() {
		return manageAddIncome;
	}
	public void setManageAddIncome(boolean manageAddIncome) {
		this.manageAddIncome = manageAddIncome;
	}
	public boolean isManageViewDeleteIncome() {
		return manageViewDeleteIncome;
	}
	public void setManageViewDeleteIncome(boolean manageViewDeleteIncome) {
		this.manageViewDeleteIncome = manageViewDeleteIncome;
	}
	public boolean isManageAssetSale() {
		return manageAssetSale;
	}
	public void setManageAssetSale(boolean manageAssetSale) {
		this.manageAssetSale = manageAssetSale;
	}
	public boolean isManageAssetPurchase() {
		return manageAssetPurchase;
	}
	public void setManageAssetPurchase(boolean manageAssetPurchase) {
		this.manageAssetPurchase = manageAssetPurchase;
	}
	public boolean isManageAssetSaleReport() {
		return manageAssetSaleReport;
	}
	public void setManageAssetSaleReport(boolean manageAssetSaleReport) {
		this.manageAssetSaleReport = manageAssetSaleReport;
	}
	public boolean isManageAssetPurchaseReport() {
		return manageAssetPurchaseReport;
	}
	public void setManageAssetPurchaseReport(boolean manageAssetPurchaseReport) {
		this.manageAssetPurchaseReport = manageAssetPurchaseReport;
	}
	public boolean isManageCheque() {
		return manageCheque;
	}
	public void setManageCheque(boolean manageCheque) {
		this.manageCheque = manageCheque;
	}
	public boolean isManageDueCheque() {
		return manageDueCheque;
	}
	public void setManageDueCheque(boolean manageDueCheque) {
		this.manageDueCheque = manageDueCheque;
	}
	public boolean isManagePendingTransaction() {
		return managePendingTransaction;
	}
	public void setManagePendingTransaction(boolean managePendingTransaction) {
		this.managePendingTransaction = managePendingTransaction;
	}
	public boolean isManageAddClassTeacher() {
		return manageAddClassTeacher;
	}
	public void setManageAddClassTeacher(boolean manageAddClassTeacher) {
		this.manageAddClassTeacher = manageAddClassTeacher;
	}
	public boolean isManageEditClassTeacher() {
		return manageEditClassTeacher;
	}
	public void setManageEditClassTeacher(boolean manageEditClassTeacher) {
		this.manageEditClassTeacher = manageEditClassTeacher;
	}
	public boolean isManageOldRbscTCGeneration() {
		return manageOldRbscTCGeneration;
	}
	public void setManageOldRbscTCGeneration(boolean manageOldRbscTCGeneration) {
		this.manageOldRbscTCGeneration = manageOldRbscTCGeneration;
	}
	public boolean isManageOldRbscTCList() {
		return manageOldRbscTCList;
	}
	public void setManageOldRbscTCList(boolean manageOldRbscTCList) {
		this.manageOldRbscTCList = manageOldRbscTCList;
	}
	public boolean isManageSMSLedger() {
		return manageSMSLedger;
	}
	public void setManageSMSLedger(boolean manageSMSLedger) {
		this.manageSMSLedger = manageSMSLedger;
	}
	public boolean isManageSalary() {
		return manageSalary;
	}
	public void setManageSalary(boolean manageSalary) {
		this.manageSalary = manageSalary;
	}

	public boolean isManageSalFormationReport() {
		return manageSalFormationReport;
	}
	public void setManageSalFormationReport(boolean manageSalFormationReport) {
		this.manageSalFormationReport = manageSalFormationReport;
	}
	public boolean isManageDeduction() {
		return manageDeduction;
	}
	public void setManageDeduction(boolean manageDeduction) {
		this.manageDeduction = manageDeduction;
	}
	public boolean isManageDeductionReport() {
		return manageDeductionReport;
	}
	public void setManageDeductionReport(boolean manageDeductionReport) {
		this.manageDeductionReport = manageDeductionReport;
	}
	public boolean isManageSalaryLedger() {
		return manageSalaryLedger;
	}
	public void setManageSalaryLedger(boolean manageSalaryLedger) {
		this.manageSalaryLedger = manageSalaryLedger;
	}
	public boolean isManageSalaryReport() {
		return manageSalaryReport;
	}
	public void setManageSalaryReport(boolean manageSalaryReport) {
		this.manageSalaryReport = manageSalaryReport;
	}
	public boolean isManageViewSyllabus() {
		return manageViewSyllabus;
	}
	public void setManageViewSyllabus(boolean manageViewSyllabus) {
		this.manageViewSyllabus = manageViewSyllabus;
	}
	public boolean isManageExperienceCertificate() {
		return manageExperienceCertificate;
	}
	public void setManageExperienceCertificate(boolean manageExperienceCertificate) {
		this.manageExperienceCertificate = manageExperienceCertificate;
	}
	public boolean isManageAttendanceCertificate() {
		return manageAttendanceCertificate;
	}
	public void setManageAttendanceCertificate(boolean manageAttendanceCertificate) {
		this.manageAttendanceCertificate = manageAttendanceCertificate;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
	public boolean isManageConcessionCategory() {
		return manageConcessionCategory;
	}
	public void setManageConcessionCategory(boolean manageConcessionCategory) {
		this.manageConcessionCategory = manageConcessionCategory;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public String getMasterAdmin() {
		return masterAdmin;
	}

	public void setMasterAdmin(String masterAdmin) {
		this.masterAdmin = masterAdmin;
	}

	public boolean isManageRouteMessage() {
		return manageRouteMessage;
	}

	public void setManageRouteMessage(boolean manageRouteMessage) {
		this.manageRouteMessage = manageRouteMessage;
	}

	public boolean isManageClassWiseTestReport() {
		return manageClassWiseTestReport;
	}

	public void setManageClassWiseTestReport(boolean manageClassWiseTestReport) {
		this.manageClassWiseTestReport = manageClassWiseTestReport;
	}

	public boolean isManageInstituteTimetable() {
		return manageInstituteTimetable;
	}

	public void setManageInstituteTimetable(boolean manageInstituteTimetable) {
		this.manageInstituteTimetable = manageInstituteTimetable;
	}

	public boolean isFeeEstimateReport() {
		return feeEstimateReport;
	}

	public void setFeeEstimateReport(boolean feeEstimateReport) {
		this.feeEstimateReport = feeEstimateReport;
	}

	public boolean isManageComplaintDiary() {
		return manageComplaintDiary;
	}

	public void setManageComplaintDiary(boolean manageComplaintDiary) {
		this.manageComplaintDiary = manageComplaintDiary;
	}

	public SchoolInfoList getList() {
		return list;
	}

	public void setList(SchoolInfoList list) {
		this.list = list;
	}

	public boolean isShowAccount() {
		return showAccount;
	}

	public void setShowAccount(boolean showAccount) {
		this.showAccount = showAccount;
	}

	public boolean isManageBulkConcession() {
		return manageBulkConcession;
	}

	public void setManageBulkConcession(boolean manageBulkConcession) {
		this.manageBulkConcession = manageBulkConcession;
	}

	public boolean isManageMultipleCertificate() {
		return manageMultipleCertificate;
	}

	public void setManageMultipleCertificate(boolean manageMultipleCertificate) {
		this.manageMultipleCertificate = manageMultipleCertificate;
	}

	public boolean isFeeRecordRegister() {
		return feeRecordRegister;
	}

	public void setFeeRecordRegister(boolean feeRecordRegister) {
		this.feeRecordRegister = feeRecordRegister;
	}

	public boolean isManageDailyCollection() {
		return manageDailyCollection;
	}

	public void setManageDailyCollection(boolean manageDailyCollection) {
		this.manageDailyCollection = manageDailyCollection;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isManageAddInstituteEnquiry() {
		return manageAddInstituteEnquiry;
	}

	public void setManageAddInstituteEnquiry(boolean manageAddInstituteEnquiry) {
		this.manageAddInstituteEnquiry = manageAddInstituteEnquiry;
	}

	public boolean isManageShowInstituteEnquiry() {
		return manageShowInstituteEnquiry;
	}

	public void setManageShowInstituteEnquiry(boolean manageShowInstituteEnquiry) {
		this.manageShowInstituteEnquiry = manageShowInstituteEnquiry;
	}

	public int getCreditmessage() {
		return creditmessage;
	}

	public void setCreditmessage(int creditmessage) {
		this.creditmessage = creditmessage;
	}

	public int getDebitmessage() {
		return debitmessage;
	}

	public void setDebitmessage(int debitmessage) {
		this.debitmessage = debitmessage;
	}

	public boolean isManageShowEnquiryblm() {
		return manageShowEnquiryblm;
	}

	public void setManageShowEnquiryblm(boolean manageShowEnquiryblm) {
		this.manageShowEnquiryblm = manageShowEnquiryblm;
	}

	public boolean isManageConcessionRequest() {
		return manageConcessionRequest;
	}

	public void setManageConcessionRequest(boolean manageConcessionRequest) {
		this.manageConcessionRequest = manageConcessionRequest;
	}

	public boolean isManageAccounting() {
		return manageAccounting;
	}

	public void setManageAccounting(boolean manageAccounting) {
		this.manageAccounting = manageAccounting;
	}

	public boolean isManageGeneralReport() {
		return manageGeneralReport;
	}

	public void setManageGeneralReport(boolean manageGeneralReport) {
		this.manageGeneralReport = manageGeneralReport;
	}

	public boolean isManageStudentReport() {
		return manageStudentReport;
	}

	public void setManageStudentReport(boolean manageStudentReport) {
		this.manageStudentReport = manageStudentReport;
	}

	public boolean isManageFeeReport() {
		return manageFeeReport;
	}

	public void setManageFeeReport(boolean manageFeeReport) {
		this.manageFeeReport = manageFeeReport;
	}

	public boolean isManageLibReport() {
		return manageLibReport;
	}

	public void setManageLibReport(boolean manageLibReport) {
		this.manageLibReport = manageLibReport;
	}

	public boolean isManageAttendanceReport() {
		return manageAttendanceReport;
	}

	public void setManageAttendanceReport(boolean manageAttendanceReport) {
		this.manageAttendanceReport = manageAttendanceReport;
	}

	public boolean isShowMAdmin() {
		return showMAdmin;
	}

	public void setShowMAdmin(boolean showMAdmin) {
		this.showMAdmin = showMAdmin;
	}

	public boolean isManageTracking() {
		return manageTracking;
	}

	public void setManageTracking(boolean manageTracking) {
		this.manageTracking = manageTracking;
	}

	public boolean isManageInfirmary() {
		return manageInfirmary;
	}

	public void setManageInfirmary(boolean manageInfirmary) {
		this.manageInfirmary = manageInfirmary;
	}

	public boolean isManageImprest() {
		return manageImprest;
	}

	public void setManageImprest(boolean manageImprest) {
		this.manageImprest = manageImprest;
	}

	public boolean isManageoasismarksheetupto3() {
		return manageoasismarksheetupto3;
	}

	public void setManageoasismarksheetupto3(boolean manageoasismarksheetupto3) {
		this.manageoasismarksheetupto3 = manageoasismarksheetupto3;
	}

	public boolean isManageuploadstudentmarksheet() {
		return manageuploadstudentmarksheet;
	}

	public void setManageuploadstudentmarksheet(boolean manageuploadstudentmarksheet) {
		this.manageuploadstudentmarksheet = manageuploadstudentmarksheet;
	}

	public boolean isManageStaffAtt() {
		return manageStaffAtt;
	}

	public void setManageStaffAtt(boolean manageStaffAtt) {
		this.manageStaffAtt = manageStaffAtt;
	}

	public boolean isManageStaffAttRep() {
		return manageStaffAttRep;
	}

	public void setManageStaffAttRep(boolean manageStaffAttRep) {
		this.manageStaffAttRep = manageStaffAttRep;
	}

	public boolean isManageAbsentStaff() {
		return manageAbsentStaff;
	}

	public void setManageAbsentStaff(boolean manageAbsentStaff) {
		this.manageAbsentStaff = manageAbsentStaff;
	}

	public boolean isManageStaffLeave() {
		return manageStaffLeave;
	}

	public void setManageStaffLeave(boolean manageStaffLeave) {
		this.manageStaffLeave = manageStaffLeave;
	}

	public boolean isManageStaffLeaveRequest() {
		return manageStaffLeaveRequest;
	}

	public void setManageStaffLeaveRequest(boolean manageStaffLeaveRequest) {
		this.manageStaffLeaveRequest = manageStaffLeaveRequest;
	}

	public boolean isManageStaffLeaveRep() {
		return manageStaffLeaveRep;
	}

	public void setManageStaffLeaveRep(boolean manageStaffLeaveRep) {
		this.manageStaffLeaveRep = manageStaffLeaveRep;
	}

	public boolean isManageGenerateRank() {
		return manageGenerateRank;
	}

	public void setManageGenerateRank(boolean manageGenerateRank) {
		this.manageGenerateRank = manageGenerateRank;
	}

	public boolean isManageLibrarySetting() {
		return manageLibrarySetting;
	}

	public void setManageLibrarySetting(boolean manageLibrarySetting) {
		this.manageLibrarySetting = manageLibrarySetting;
	}

	public boolean isManagehostel() {
		return managehostel;
	}

	public void setManagehostel(boolean managehostel) {
		this.managehostel = managehostel;
	}

	public boolean isManageConcernFeedback() {
		return manageConcernFeedback;
	}

	public void setManageConcernFeedback(boolean manageConcernFeedback) {
		this.manageConcernFeedback = manageConcernFeedback;
	}

	public boolean isManageLeaveRequest() {
		return manageLeaveRequest;
	}

	public void setManageLeaveRequest(boolean manageLeaveRequest) {
		this.manageLeaveRequest = manageLeaveRequest;
	}

	public boolean isManageStaffBirthday() {
		return manageStaffBirthday;
	}

	public void setManageStaffBirthday(boolean manageStaffBirthday) {
		this.manageStaffBirthday = manageStaffBirthday;
	}

	public boolean isManageStudentBirthday() {
		return manageStudentBirthday;
	}

	public void setManageStudentBirthday(boolean manageStudentBirthday) {
		this.manageStudentBirthday = manageStudentBirthday;
	}

	public boolean isManageStudentMessage() {
		return manageStudentMessage;
	}

	public void setManageStudentMessage(boolean manageStudentMessage) {
		this.manageStudentMessage = manageStudentMessage;
	}

	public boolean isManageAddNews() {
		return manageAddNews;
	}

	public void setManageAddNews(boolean manageAddNews) {
		this.manageAddNews = manageAddNews;
	}

	public boolean isManageTemplate() {
		return manageTemplate;
	}

	public void setManageTemplate(boolean manageTemplate) {
		this.manageTemplate = manageTemplate;
	}

	public boolean isManagePreviousSession() {
		return managePreviousSession;
	}

	public void setManagePreviousSession(boolean managePreviousSession) {
		this.managePreviousSession = managePreviousSession;
	}

	public boolean isManagePreviousAttendance() {
		return managePreviousAttendance;
	}

	public void setManagePreviousAttendance(boolean managePreviousAttendance) {
		this.managePreviousAttendance = managePreviousAttendance;
	}

	public boolean isManagePreviousFee() {
		return managePreviousFee;
	}

	public void setManagePreviousFee(boolean managePreviousFee) {
		this.managePreviousFee = managePreviousFee;
	}

	public boolean isManagePreviousMarksheet() {
		return managePreviousMarksheet;
	}

	public void setManagePreviousMarksheet(boolean managePreviousMarksheet) {
		this.managePreviousMarksheet = managePreviousMarksheet;
	}

	public boolean isManageAnalaticReport() {
		return manageAnalaticReport;
	}

	public void setManageAnalaticReport(boolean manageAnalaticReport) {
		this.manageAnalaticReport = manageAnalaticReport;
	}

	public boolean isManageVisitorModule() {
		return manageVisitorModule;
	}

	public void setManageVisitorModule(boolean manageVisitorModule) {
		this.manageVisitorModule = manageVisitorModule;
	}

	public boolean isManageAddVisitor() {
		return manageAddVisitor;
	}

	public void setManageAddVisitor(boolean manageAddVisitor) {
		this.manageAddVisitor = manageAddVisitor;
	}

	public boolean isManageShowVisitor() {
		return manageShowVisitor;
	}

	public void setManageShowVisitor(boolean manageShowVisitor) {
		this.manageShowVisitor = manageShowVisitor;
	}

	public String getHindiName() {
		return hindiName;
	}

	public void setHindiName(String hindiName) {
		this.hindiName = hindiName;
	}

	public String getSmsName() {
		return smsName;
	}

	public void setSmsName(String smsName) {
		this.smsName = smsName;
	}

	public boolean isManageAccount() {
		return manageAccount;
	}

	public void setManageAccount(boolean manageAccount) {
		this.manageAccount = manageAccount;
	}

	public boolean isManageAddHead() {
		return manageAddHead;
	}

	public void setManageAddHead(boolean manageAddHead) {
		this.manageAddHead = manageAddHead;
	}

	public boolean isManageAddAccount() {
		return manageAddAccount;
	}

	public void setManageAddAccount(boolean manageAddAccount) {
		this.manageAddAccount = manageAddAccount;
	}

	public boolean isManageTransaction() {
		return manageTransaction;
	}

	public void setManageTransaction(boolean manageTransaction) {
		this.manageTransaction = manageTransaction;
	}

	public boolean isManagePendingCheque() {
		return managePendingCheque;
	}

	public void setManagePendingCheque(boolean managePendingCheque) {
		this.managePendingCheque = managePendingCheque;
	}

	public boolean isManageAccountLedger() {
		return manageAccountLedger;
	}

	public void setManageAccountLedger(boolean manageAccountLedger) {
		this.manageAccountLedger = manageAccountLedger;
	}

	public boolean isManageEditSalary() {
		return manageEditSalary;
	}

	public void setManageEditSalary(boolean manageEditSalary) {
		this.manageEditSalary = manageEditSalary;
	}

	public boolean isManageViewSalary() {
		return manageViewSalary;
	}

	public void setManageViewSalary(boolean manageViewSalary) {
		this.manageViewSalary = manageViewSalary;
	}


	public boolean isManageEnqComm() {
		return manageEnqComm;
	}

	public void setManageEnqComm(boolean manageEnqComm) {
		this.manageEnqComm = manageEnqComm;
	}

	public boolean isManageOnlineAdmReq() {
		return manageOnlineAdmReq;
	}

	public void setManageOnlineAdmReq(boolean manageOnlineAdmReq) {
		this.manageOnlineAdmReq = manageOnlineAdmReq;
	}

	public boolean isManageRegApplication() {
		return manageRegApplication;
	}

	public void setManageRegApplication(boolean manageRegApplication) {
		this.manageRegApplication = manageRegApplication;
	}

	public boolean isManageAdmApplication() {
		return manageAdmApplication;
	}

	public void setManageAdmApplication(boolean manageAdmApplication) {
		this.manageAdmApplication = manageAdmApplication;
	}

	public boolean isManageVideoGallery() {
		return manageVideoGallery;
	}

	public void setManageVideoGallery(boolean manageVideoGallery) {
		this.manageVideoGallery = manageVideoGallery;
	}

	public boolean isManagePickup() {
		return managePickup;
	}

	public void setManagePickup(boolean managePickup) {
		this.managePickup = managePickup;
	}

	public boolean isManageStudentLogin() {
		return manageStudentLogin;
	}

	public void setManageStudentLogin(boolean manageStudentLogin) {
		this.manageStudentLogin = manageStudentLogin;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStartSession() {
		return startSession;
	}

	public void setStartSession(String startSession) {
		this.startSession = startSession;
	}

	public String getEndSession() {
		return endSession;
	}

	public void setEndSession(String endSession) {
		this.endSession = endSession;
	}

	public int getStartSessionMonth() {
		return startSessionMonth;
	}

	public void setStartSessionMonth(int startSessionMonth) {
		this.startSessionMonth = startSessionMonth;
	}

	public int getEndSessionMonth() {
		return endSessionMonth;
	}

	public void setEndSessionMonth(int endSessionMonth) {
		this.endSessionMonth = endSessionMonth;
	}

	public boolean isManageSalarySetting() {
		return manageSalarySetting;
	}

	public void setManageSalarySetting(boolean manageSalarySetting) {
		this.manageSalarySetting = manageSalarySetting;
	}

	public boolean isManageAppointmentSetting() {
		return manageAppointmentSetting;
	}

	public void setManageAppointmentSetting(boolean manageAppointmentSetting) {
		this.manageAppointmentSetting = manageAppointmentSetting;
	}

	public boolean isManagePhotoRequest() {
		return managePhotoRequest;
	}

	public void setManagePhotoRequest(boolean managePhotoRequest) {
		this.managePhotoRequest = managePhotoRequest;
	}

	public boolean isManageEYFS() {
		return manageEYFS;
	}

	public void setManageEYFS(boolean manageEYFS) {
		this.manageEYFS = manageEYFS;
	}

	public boolean isManageEYFSParameter() {
		return manageEYFSParameter;
	}

	public void setManageEYFSParameter(boolean manageEYFSParameter) {
		this.manageEYFSParameter = manageEYFSParameter;
	}

	public boolean isManageEYFSPerformance() {
		return manageEYFSPerformance;
	}

	public void setManageEYFSPerformance(boolean manageEYFSPerformance) {
		this.manageEYFSPerformance = manageEYFSPerformance;
	}

	public boolean isManageEYFSReport() {
		return manageEYFSReport;
	}

	public void setManageEYFSReport(boolean manageEYFSReport) {
		this.manageEYFSReport = manageEYFSReport;
	}

	public boolean isShowBackToMaster() {
		return showBackToMaster;
	}

	public void setShowBackToMaster(boolean showBackToMaster) {
		this.showBackToMaster = showBackToMaster;
	}

	public boolean isAllotRfid() {
		return allotRfid;
	}

	public void setAllotRfid(boolean allotRfid) {
		this.allotRfid = allotRfid;
	}

	public boolean isAllotBioCode() {
		return allotBioCode;
	}

	public void setAllotBioCode(boolean allotBioCode) {
		this.allotBioCode = allotBioCode;
	}

	public boolean isManageInstallmentFeeCollection() {
		return manageInstallmentFeeCollection;
	}

	public void setManageInstallmentFeeCollection(boolean manageInstallmentFeeCollection) {
		this.manageInstallmentFeeCollection = manageInstallmentFeeCollection;
	}

	public boolean isAttendantApp() {
		return attendantApp;
	}

	public void setAttendantApp(boolean attendantApp) {
		this.attendantApp = attendantApp;
	}

	public boolean isManageConsent() {
		return manageConsent;
	}

	public void setManageConsent(boolean manageConsent) {
		this.manageConsent = manageConsent;
	}

	public boolean isManageELearing() {
		return manageELearing;
	}

	public void setManageELearing(boolean manageELearing) {
		this.manageELearing = manageELearing;
	}

	public boolean isManageViewELearning() {
		return manageViewELearning;
	}

	public void setManageViewELearning(boolean manageViewELearning) {
		this.manageViewELearning = manageViewELearning;
	}

	public boolean isManageUniqueTeacherAttendece() {
		return manageUniqueTeacherAttendece;
	}

	public void setManageUniqueTeacherAttendece(boolean manageUniqueTeacherAttendece) {
		this.manageUniqueTeacherAttendece = manageUniqueTeacherAttendece;
	}



	


	public boolean isRenderInSession() 
	{
		String sessionStatus=new DatabaseMethodSession().checkSessionStatus();
		if(sessionStatus.equalsIgnoreCase("Previous"))
			renderInSession=false;
		else
			renderInSession=true;
		return renderInSession;
	}

	public void setRenderInSession(boolean renderInSession) {
		this.renderInSession = renderInSession;
	}

	public boolean isDisableInSession() 
	{
		String sessionStatus=new DatabaseMethodSession().checkSessionStatus();
		if(sessionStatus.equalsIgnoreCase("Previous"))
			disableInSession=true;
		else
			disableInSession=false;
		return disableInSession;
	}

	public void setDisableInSession(boolean disableInSession)
	{
		this.disableInSession = disableInSession;
	}

	public String getMenu() 
	{
		String sessionStatus=new DatabaseMethodSession().checkSessionStatus();
		if(sessionStatus.equalsIgnoreCase("Previous"))
			menu="menu1.xhtml";
		else
			menu="menu.xhtml";
			
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public boolean isManageTCComm() {
	return manageTCComm;
}

public void setManageTCComm(boolean manageTCComm) {
	this.manageTCComm = manageTCComm;
}

public boolean isManageMesssagePack() {
	return manageMesssagePack;
}

public void setManageMesssagePack(boolean manageMesssagePack) {
	this.manageMesssagePack = manageMesssagePack;
}

public boolean isManageSalaryPay() {
	return manageSalaryPay;
}

public void setManageSalaryPay(boolean manageSalaryPay) {
	this.manageSalaryPay = manageSalaryPay;
}

public boolean isManageSalaryFormation() {
	return manageSalaryFormation;
}

public void setManageSalaryFormation(boolean manageSalaryFormation) {
	this.manageSalaryFormation = manageSalaryFormation;
}

public boolean isManageSalaryDetails() {
	return manageSalaryDetails;
}

public void setManageSalaryDetails(boolean manageSalaryDetails) {
	this.manageSalaryDetails = manageSalaryDetails;
}

public boolean isManageMultipleTeacherAttendece() {
	return manageMultipleTeacherAttendece;
}

public void setManageMultipleTeacherAttendece(boolean manageMultipleTeacherAttendece) {
	this.manageMultipleTeacherAttendece = manageMultipleTeacherAttendece;
}

public boolean isManageUnitMarksheet() {
	return manageUnitMarksheet;
}

public void setManageUnitMarksheet(boolean manageUnitMarksheet) {
	this.manageUnitMarksheet = manageUnitMarksheet;
}

public boolean isManageMarksAverage() {
	return manageMarksAverage;
}

public void setManageMarksAverage(boolean manageMarksAverage) {
	this.manageMarksAverage = manageMarksAverage;
}

public boolean isManageDetailedMarks() {
	return manageDetailedMarks;
}

public void setManageDetailedMarks(boolean manageDetailedMarks) {
	this.manageDetailedMarks = manageDetailedMarks;
}

public boolean isManageStudentOverallPerformance() {
	return manageStudentOverallPerformance;
}

public void setManageStudentOverallPerformance(boolean manageStudentOverallPerformance) {
	this.manageStudentOverallPerformance = manageStudentOverallPerformance;
}

public boolean isManageAddMeeting() {
	return manageAddMeeting;
}

public void setManageAddMeeting(boolean manageAddMeeting) {
	this.manageAddMeeting = manageAddMeeting;
}

public boolean isManageViewEditDeleteMeeting() {
	return manageViewEditDeleteMeeting;
}

public void setManageViewEditDeleteMeeting(boolean manageViewEditDeleteMeeting) {
	this.manageViewEditDeleteMeeting = manageViewEditDeleteMeeting;
}

public boolean isManageMeeting() {
	return manageMeeting;
}

public void setManageMeeting(boolean manageMeeting) {
	this.manageMeeting = manageMeeting;
}

public boolean isManageFlyer() {
	return manageFlyer;
} 

public void setManageFlyer(boolean manageFlyer) {
	this.manageFlyer = manageFlyer;
}

public boolean isExamModule() {
	return examModule;
}

public void setExamModule(boolean examModule) {
	this.examModule = examModule;
}

public boolean isManageAdministrativeOfficer() {
	return manageAdministrativeOfficer;
}

public void setManageAdministrativeOfficer(boolean manageAdministrativeOfficer) {
	this.manageAdministrativeOfficer = manageAdministrativeOfficer;
}

public boolean isManageOnlineExam() {
	return manageOnlineExam;
}

public void setManageOnlineExam(boolean manageOnlineExam) {
	this.manageOnlineExam = manageOnlineExam;
}

public boolean isOnlineExamReport() {
	return onlineExamReport;
}

public void setOnlineExamReport(boolean onlineExamReport) {
	this.onlineExamReport = onlineExamReport;
}

public boolean isDeletePerformance() {
	return deletePerformance;
}

public void setDeletePerformance(boolean deletePerformance) {
	this.deletePerformance = deletePerformance;
}

public String getSchid() {
	return schid;
}

public void setSchid(String schid) {
	this.schid = schid;
}


}
