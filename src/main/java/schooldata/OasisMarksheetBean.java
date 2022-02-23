package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsExam;

@ManagedBean(name="oasisMarksheetBean")
@ViewScoped
public class OasisMarksheetBean implements Serializable
{
	String className,examName,termName,examType,lockExam,lockStatus,selectedType="scholastic",subExam,subExamTemp;
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,examTypeList,sessionList,termList,subExamList;
	ArrayList<StudentInfo> studentDetails;
	StudentInfo selectedStudent;
	ArrayList<SubjectInfo> subjectList;
	boolean showExam,renderShowTable=true,showPrintButton,showStudentRecord,showStudentRecordButton,showSubExam;
	String selectedExam,term,selectedSession,selectedSection,selectedTerm,selectedClass,schid,session,username,userType;
	ArrayList<DayInfo> days;
	String[] arr;
	private boolean enableSaveBtn=true;

	public void method()
	{
		term=selectedTerm;

		showExam=false;
		selectedType=null;
		showPrintButton=false;
		selectedExam=null;

		showStudentRecord=false;showStudentRecordButton=false;
		selectedType="scholastic";
		allExamType();
	}

	public  OasisMarksheetBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=new DatabaseMethods1().schoolId();
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=new DatabaseMethods1().allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=new DatabaseMethods1().cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=new DatabaseMethods1().allClassListForClassTeacher(empid,schid,conn);

		}
		
		session=DatabaseMethods1.selectedSessionDetails(schid, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		showExam=false;showPrintButton=false;
		selectedSection=null;selectedTerm=null;

		selectedType=null;selectedExam=null;
		showStudentRecord=false;showStudentRecordButton=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExamType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		/*if(selectedType.equals("scholastic"))
		{
			examTypeList=new DatabaseMethods1().selectedExamTypeOfClassSection(selectedClass, term,selectedType,conn);

			showStudentRecord=false;renderShowTable=true;
			showPrintButton=false;showExam=true;

		}
		else if(selectedType.equals("coscholastic"))
		{
			selectedExam=null;
			showPrintButton=true;renderShowTable=true;
			showExam=false;
			sessionDetails();
		}*/

		if(selectedType.equals("scholastic"))
		{
			examTypeList=new DataBaseMethodsExam().mainExamListOfClassSection(selectedClass,selectedType,selectedTerm,conn);
			showStudentRecord=false;
			renderShowTable=true;
			showExam=true;
		}
		else if(selectedType.equals("coscholastic"))
		{
			selectedExam=null;
			renderShowTable=true;
			showExam=false;
			showSubExam=false;
			showPrintButton=true;
			renderShowTable=true;
			sessionDetails();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createSubExamList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subExamList=new DataBaseMethodsExam().subExamListOfClassSection(selectedClass,selectedType,selectedExam,selectedTerm,session,conn);
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			if(value.contains("sub"))
			{
				showSubExam=true;
			}
			else
			{
				showSubExam=false;
				sessionDetails();
			}
		}
		else
		{
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void subExamValue()
	{
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			if(value.contains("sub"))
			{
				subExamTemp=subExam.substring(0,subExam.lastIndexOf("/"));
			}
			else
			{
				subExamTemp=value.substring(0,value.lastIndexOf("/"));
			}
		}

	}


	public void sessionDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();

		studentDetail(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String termname,examname;
		ArrayList<SubjectInfo>markslist=new ArrayList<>();
		ArrayList<SubjectInfo>coscholasticMarklist=new ArrayList<>();
		DatabaseMethods1 dm=new DatabaseMethods1();
		ArrayList<StudentInfo>list=new ArrayList<>();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		/*ss.setAttribute("studendeList", selectedStudent);
		ss.setAttribute("selectedClass", selectedClass);
		ss.setAttribute("selectedSem", selectedTerm);
		ss.setAttribute("selectedExam", selectedExam);*/


		markslist=dm.allSubjectListClassWiseScholasticOasis(selectedClass,"scholastic" ,conn,selectedSection,selectedStudent.getAddNumber());

		termname=dm.semesterNameFromid(selectedTerm, conn);
		examname=dm.examNameFromid(selectedExam, conn);

		dm.termWiseMarks(selectedStudent.getAddNumber(),selectedTerm,selectedExam,markslist,conn);

		for(SubjectInfo si : markslist)
		{
			if(si.getMarksGrade().equalsIgnoreCase("grade"))
			{
				if(si.getMainmarks()==null)
				{
					si.setMainmarks("");
					si.setGradePoint("");
				}
				else
				{
					if(si.getMainmarks().equalsIgnoreCase("Ab"))
					{
						si.setMainmarks("Ab");
						si.setGradePoint("");
					}
					else
					{
						si.setMainmarks("");
						si.setGradePoint("");
					}
				}


			}
		}

		coscholasticMarklist=dm.allSubjectListClassWiseScholasticOasis(selectedClass,"coscholastic" ,conn,selectedSection,selectedStudent.getAddNumber());

		dm.termWiseMarksCoscholstic(selectedStudent.getAddNumber(), selectedTerm, selectedExam, coscholasticMarklist, conn);

		selectedStudent.setExamName(examname);

		selectedStudent.setTermName(termname);
		selectedStudent.setMarkslist(markslist);
		selectedStudent.setCoscholasticMarklist(coscholasticMarklist);
		if(coscholasticMarklist.size()>0)
		{
			selectedStudent.setShowCoscholasticSubject(true);
		}
		else
		{
			selectedStudent.setShowCoscholasticSubject(false);
		}
		list.add(selectedStudent);
		ss.setAttribute("studendeList", list);
		/*
		if(coscholasticMarklist.size()>0)
		{

		}
		else
		{
			;
		}*/
		PrimeFaces.current().executeInitScript("window.open('unitWiseMarkSheetPrint.xhtml')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void performancePrint()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedClass", selectedClass);
		ss.setAttribute("selectedSection", selectedSection);
		ss.setAttribute("selectedTerm", selectedTerm);
		ss.setAttribute("selectedExam", selectedExam);
		ss.setAttribute("subExam", subExam);
		PrimeFaces.current().executeInitScript("window.open('printOasisTestPerformance.xhtml')");
	}


	public void allStudentPrint()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 dm=new DatabaseMethods1();
		new ArrayList<>();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		/*ss.setAttribute("studendeList", selectedStudent);
		ss.setAttribute("selectedClass", selectedClass);
		ss.setAttribute("selectedSem", selectedTerm);
		ss.setAttribute("selectedExam", selectedExam);*/

		for(StudentInfo ls:studentDetails)
		{
			String termname,examname;
			ArrayList<SubjectInfo>markslist=new ArrayList<>();
			ArrayList<SubjectInfo>coscholasticMarklist=new ArrayList<>();

			markslist=dm.allSubjectListClassWiseScholasticOasis(selectedClass,"scholastic" ,conn,selectedSection,ls.getAddNumber());

			termname=dm.semesterNameFromid(selectedTerm, conn);
			examname=dm.examNameFromid(selectedExam, conn);

			dm.termWiseMarks(ls.getAddNumber(),selectedTerm,selectedExam,markslist,conn);

			for(SubjectInfo si : markslist)
			{
				if(si.getMarksGrade().equalsIgnoreCase("grade"))
				{
					if(si.getMainmarks()==null)
					{
						si.setMainmarks("");
						si.setGradePoint("");
					}
					else
					{
						if(si.getMainmarks().equalsIgnoreCase("Ab"))
						{
							si.setMainmarks("Ab");
							si.setGradePoint("");
						}
						else
						{
							si.setMainmarks("");
							si.setGradePoint("");
						}
					}


				}
			}

			coscholasticMarklist=dm.allSubjectListClassWiseScholasticOasis(selectedClass,"coscholastic" ,conn,selectedSection,ls.getAddNumber());

			dm.termWiseMarksCoscholstic(ls.getAddNumber(), selectedTerm, selectedExam, coscholasticMarklist, conn);

			ls.setExamName(examname);

			ls.setTermName(termname);
			ls.setMarkslist(markslist);
			ls.setCoscholasticMarklist(coscholasticMarklist);

			if(coscholasticMarklist.size()>0)
			{
				ls.setShowCoscholasticSubject(true);
			}
			else
			{
				ls.setShowCoscholasticSubject(false);
			}
		}

		ss.setAttribute("studendeList", studentDetails);
		/*
		if(coscholasticMarklist.size()>0)
		{

		}
		else
		{
			;
		}*/
		PrimeFaces.current().executeInitScript("window.open('unitWiseMarkSheetPrint.xhtml')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentDetail(Connection conn)
	{
		//studentDetails=dd.searchStudentListByClassSection(selectedSection,conn);
		studentDetails=new DataBaseMethodsExam().studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		showPrintButton=true;
		showStudentRecord=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public boolean isEnableSaveBtn() {
		return enableSaveBtn;
	}
	public void setEnableSaveBtn(boolean enableSaveBtn) {
		this.enableSaveBtn = enableSaveBtn;
	}
	public ArrayList<DayInfo> getDays() {
		return days;
	}
	public void setDays(ArrayList<DayInfo> days) {
		this.days = days;
	}
	public String getLockExam() {
		return lockExam;
	}
	public void setLockExam(String lockExam) {
		this.lockExam = lockExam;
	}
	public String getLockStatus() {
		return lockStatus;
	}
	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public boolean isShowExam() {
		return showExam;
	}
	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}
	public String getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
	public boolean isRenderShowTable() {
		return renderShowTable;
	}
	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}
	public ArrayList<SelectItem> getExamTypeList() {
		return examTypeList;
	}
	public void setExamTypeList(ArrayList<SelectItem> examTypeList) {
		this.examTypeList = examTypeList;
	}

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}
	public String getSelectedTerm() {
		return selectedTerm;
	}
	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}
	public boolean isShowStudentRecordButton() {
		return showStudentRecordButton;
	}
	public void setShowStudentRecordButton(boolean showStudentRecordButton) {
		this.showStudentRecordButton = showStudentRecordButton;
	}
	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}
	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public String getSelectedExam() {
		return selectedExam;
	}
	public void setSelectedExam(String selectedExam) {
		this.selectedExam = selectedExam;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public boolean isShowPrintButton() {
		return showPrintButton;
	}
	public void setShowPrintButton(boolean showPrintButton) {
		this.showPrintButton = showPrintButton;
	}

	public String getSubExam() {
		return subExam;
	}

	public void setSubExam(String subExam) {
		this.subExam = subExam;
	}

	public String getSubExamTemp() {
		return subExamTemp;
	}

	public void setSubExamTemp(String subExamTemp) {
		this.subExamTemp = subExamTemp;
	}

	public ArrayList<SelectItem> getSubExamList() {
		return subExamList;
	}

	public void setSubExamList(ArrayList<SelectItem> subExamList) {
		this.subExamList = subExamList;
	}

	public boolean isShowSubExam() {
		return showSubExam;
	}

	public void setShowSubExam(boolean showSubExam) {
		this.showSubExam = showSubExam;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}



}
