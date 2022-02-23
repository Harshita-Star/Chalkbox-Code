package schooldata;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="navigation")
public class NavigationBean {

	public String classWiseDocumentDetails()
	{
		return "showClassWiseCompleteDocuments";
	}
	public String studentWiseFeeStatement()
	{
		return "preDuplicateFeeRecipiet";
	}
	public String studentWiseAttendanceReport()
	{
		return "studentWiseAttendanceReport";
	}
	public String categoryWiseEmloyeeMessage()
	{
		return "categoryWiseEmployeeMessage";
	}
	public String categoryWiseEmloyeeReport()
	{
		return "categoryWiseEmployeeReport";
	}
	public String managePrintingWorkDashboard()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=(boolean) ss.getAttribute("checkstu");
		if(check==false)
		{
			return "managePrintingWorkDashboard";
		}
		else
		{
			return "managestudentLoginModule.xhtml";
		}

	}



	public String icardPrinting()
	{
		return "icardPrinting";
	}
	public String studentMarksList()
	{
		return "studentMarksList.xhtml";
	}
	public String studentMarksheetWithGrade()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=false;
		ss.setAttribute("checkstu", check);
		return "searchStudentForPrintMarksheet2.xhtml";
	}

	public String studentMarksheetWithGrade1()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=true;
		ss.setAttribute("checkstu", check);
		return "studentmarksheet.xhtml";
	}
	public String changePassword()
	{
		return "changePassWord";
	}
	public String backup()
	{
		return "backUp";
	}
	public String marksheet()
	{
		return "searchTermAchievementRecord";
	}
	public String struckOff()
	{
		return "showListOfStruck";
	}
	public String nonStruckOff()
	{
		return "showListOfNonStruck";
	}
	public String collectFeeNow()
	{
		return "collectFeeNow";
	}
	public String leavingcertificate()
	{
		return "preprintleaving";
	}

	public String promotion(){
		return "promoteStudentOfClass";
	}
	public String changeSession()
	{
		return "selectSession";
	}
	public String showClasStrength()
	{
		return "showClassWiseReport";
	}
	public String showStudentOnStop()
	{
		return "showRouteWiseReport";
	}
	public String showStudentOnRoute()
	{
		return "routeWiseReport";
	}
	public String showUpcomingTest()
	{
		return "allTestDetails";
	}
	public String goToLibraryDashboard()
	{
		return "manageLibraryDashboard";
	}
	public String goToExaminationDashboard()
	{
		return "manageOnlineExaminationDashboard";
	}
	public String addQuestion()
	{
		return "addQuestion";
	}
	public String addTest()
	{
		return "addTest";
	}
	public String editQuestion()
	{
		return "editQuestion";
	}
	public String editTest()
	{
		return "editTest";
	}
	public String goToManageAttendanceDashboard()
	{
		return "manageAttendanceDashboard";
	}

	public String attendanceDashboard()
	{
		return "manageAttendanceDashboard";
	}
	public String showAttendanceRecord()
	{
		return "showAttendanceRecord";
	}
	public String uploadNotes()
	{
		return "notesUploading";
	}
	public String downloadNotes()
	{
		return "notesDownloading";
	}
	public String goToNotesSection()
	{
		return "notesDashboard";
	}
	public String manageTransportDetails()
	{
		return "editTransportDetails";
	}
	public String adminHome()
	{
		return "AdminHome";
	}
	public String sendMessage()
	{
		return "sendMessageStep1";
	}
	public String manageSendMessage()
	{
		return "manageSMSSendingDashBoard";
	}
	public String feeReceipt()
	{
		return "FeeReceipt1";
	}
	public String sendMessageRouteWise()
	{
		return "sendMessageRouteWise";
	}
	public String logout() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.invalidate();
		FacesContext.getCurrentInstance().getExternalContext().redirect("LoginPage.xhtml");
		return "LoginPage";
	}
	public String manageClass()
	{
		return "manageClassDashboard";
	}
	public String addClass()
	{
		return "addClass";
	}
	public String addSection()
	{
		return "addSection";
	}

	public String addSemester()
	{
		return "addSemester";
	}
	public String addExam()
	{
		return "addExam";
	}
	public String addSubjects()
	{
		return "addSubjects";
	}

	public String backTOWelcomePage()
	{
		return "AdminHome";
	}
	public String manageStudent()
	{
		return "manageStudentDashboard";
	}
	public String studentRegistration()
	{
		return "registration1";
	}
	public String searchStudent()
	{
		return "searchStudent";
	}

	public String manageEmployee()
	{
		return "manageEmployeeDashboard";
	}
	public String employeeCategory()
	{
		return "addEmployeeCategory";
	}
	public String employeeAdmission()
	{
		return "employeeAddmission";
	}
	public String searchEmployeeDetails()
	{
		return "searchEmployee";
	}
	public String manageFinance()
	{
		return "manageFinanceDashboard";
	}
	public String addIncomeCatgeory()
	{
		return "addIncomeCategory";
	}
	public String addExpenseCategory()
	{
		return "addExpenseCategory";
	}
	public String addIncome()
	{
		return "createIncome";
	}
	public String addExpense()
	{
		return "createExpense";
	}
	public String showIncome()
	{
		return "showIncome";
	}
	public String createExpense()
	{
		return "createExpense";
	}

	public String showExpense()
	{
		return "showExpense";
	}
	public String feeCollection()
	{
		return "feeCollection";
	}
	public String attendance()
	{
		return "attendance";
	}
	public String studentPerformance()
	{
		return "studentPerformance";
	}
	public String manageReport()
	{
		return "manageReportDashboard";
	}
	public String combineReport()
	{
		return "showReport";
	}
	public String dueFeeReport()
	{
		return "dueFeeReport";
	}
	public String manageTransport()
	{
		return "manageTransportDashboard";
	}
	public String addNewRoute()
	{
		return "addNewRoute";
	}
	public String addNewStop()
	{
		return "addNewStop";
	}
	public String goToRegistrationStep3()
	{
		return "registrationStep3";
	}

	public String backToManageStudentDashboard()
	{
		return "manageStudentDashboard";
	}
	public String backToRegistration1()
	{
		return "registration1";
	}
	public String backToSeacrhStudent()
	{
		return "searchStudent";
	}
	public String backToEditStudent()
	{
		return "editStudentDetails";
	}
	public String backToManageEmployeeDashboard()
	{
		return "manageEmployeeDashboard";
	}
	public String backToSearchEmployee()
	{
		return "searchEmployee";
	}
	public String backToManageClassDashboard()
	{
		return "manageClassDashboard";
	}
	public String backToFinanceDashboard()
	{
		return "manageFinanceDashboard";
	}
	public String backToManageReportDashboard()
	{
		return "manageReportDashboard";
	}
	public String backToManageTransportDashboard()
	{
		return "manageTransportDashboard";
	}

	public String backToFeeCollection()
	{
		return "feeCollection";
	}
	public String editClass()
	{
		return "editClass";
	}
	public String editSection()
	{
		return "editSection";
	}
	public String myIncomeSection()
	{
		return "incomeDashBoard";
	}
	public String myExpenseSection()
	{
		return "expenseDashBoard";
	}
	public String editExam()
	{
		return "editExam";
	}
	public String editSemester()
	{
		return "editSemester";
	}
	public String editSubjects()
	{
		return "editSubjects";
	}
	public String editNewRoute()
	{
		return "editRouteDetails";
	}
	public String editNewStop()
	{
		return "editRouteStop";
	}
	public String editIncomeCatgeory()
	{
		return "editIncomeCategory";
	}
	public String editExpenseCategory()
	{
		return "editExpenseCategory";
	}
	public String editEmployeeCategory()
	{
		return "editEmployeeCategory";
	}
	public String dailyFeeCollection()
	{
		return "dailyFeecolletionReport";
	}
	public String manageLeavingCertificate()
	{
		return "manageLeavingCertificateDashBoard";
	}
	public String tcNotIssue()
	{
		return "searchStudentTCNotIssue";
	}
	public String monthWiseReport()
	{
		return "monthWiseAttendanceReport";
	}
}
