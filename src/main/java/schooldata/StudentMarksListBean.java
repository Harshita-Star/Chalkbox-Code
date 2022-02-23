package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsExam;
import exam_module.DataBaseMethodsExamReport;
import exam_module.ExamInfo;

@ManagedBean(name="studentMarksList")
@ViewScoped
public class StudentMarksListBean implements Serializable{

	String className,termName,examType,selectedType,subExam,subExamTemp, selectedExamName;
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,examTypeList,sessionList,termList,subExamList,subjectTypeList;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<SubjectInfo> subjectList;
	boolean showExam,showPrintButton,showStudentRecord,showSubExam,showGrade,showMarks;
	String selectedExam,term,selectedSection,selectedTerm,selectedClass,schoolid,session,marksType,username,userType;
	ArrayList<DayInfo> days;
	String[] arr;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsExamReport objReport=new DataBaseMethodsExamReport();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();

	private boolean enableSaveBtn=true;

	public void method()
	{
		term=selectedTerm;

		showExam=false;
		selectedType=null;
		showPrintButton=false;
		selectedExam=null;

		showStudentRecord=false;
	}

	public  StudentMarksListBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolid=obj.schoolId();
		subjectTypeList=obj.subjectTypeList();
		session=obj.selectedSessionDetails(schoolid, conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);

		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();

		termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
		
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
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		showExam=false;showPrintButton=false;
		selectedSection=null;selectedTerm=null;

		selectedType=null;selectedExam=null;
		showStudentRecord=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExamType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		examTypeList=new DataBaseMethodsExam().mainExamListOfClassSection(selectedClass,selectedType,selectedTerm,conn);
		if(selectedType.equals("scholastic") || selectedType.equals("additional"))
		{
			showStudentRecord=false;
			showExam=true;
		}
		else
		{
			showExam=showSubExam=false;
			if(examTypeList.size()!=0)
			{	
			 selectedExam=examTypeList.get(0).getValue().toString();
//			 showPrintButton=true;
			 createSubExamList();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Exam Present"));
			}
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
		subExamList=objExam.subExamListOfClassSection(selectedClass,selectedType,selectedExam,selectedTerm,session,conn);
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			ExamInfo ee = objExam.examInfoById(selectedExam, conn);
			selectedExamName = ee.getExamName();
			if(value.contains("sub"))
			{
				showSubExam=true;
			}
			else
			{
				showSubExam=false;
//				studentDetail();
			}
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

	public void studentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedSection==null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Necessary Fields"));
		}
		else
		{	
		marksType=objExam.examSettingDetail(selectedClass, conn).getMarks_grade();
		if(marksType.equals("grade") || selectedType.equals("coscholastic") || selectedType.equals("other"))
		{
			showGrade=false;
			showMarks=true;
		}
		else
		{
			showGrade=true;
			showMarks=false;
		}
		
		subExamValue();
		showStudentRecord=true;
		studentDetails=objExam.studentBasicDetailsForMarksheet(schoolid,"", conn,"byClass",selectedSection);
		objReport.allPerformanceForMarksGradeList(selectedSection, selectedTerm, selectedExam, studentDetails,selectedType,conn,subExamTemp,schoolid,session,marksType);
		subjectList=objReport.subjectListForExamWithMaxMarks(selectedClass, selectedType, selectedTerm, selectedExam,conn,subExamTemp,schoolid,session,marksType);
		Collections.sort(studentDetails,new MySalaryComp());
		if(studentDetails.size()>0 && subjectList.size()>0)
		{
			int counter=1;
			for(int i=0;i<studentDetails.size();i++)
			{

				if(studentDetails.get(i).getStudentPerformnaceMap().get("Total").equals("-1"))
				{
					studentDetails.get(i).setRank("");
					studentDetails.get(i).setRankInt(100000);
				}
				else if(i==0 || (studentDetails.get(i).getStudentPerformnaceMap().get("Total").equals(studentDetails.get(i-1).getStudentPerformnaceMap().get("Total"))))
				{
					studentDetails.get(i).setRank(String.valueOf(counter));
					studentDetails.get(i).setRankInt(counter);
				}
				else
				{
					++counter;
					studentDetails.get(i).setRank(String.valueOf(counter));
					studentDetails.get(i).setRankInt(counter);
				}


				double per=Double.parseDouble(studentDetails.get(i).getStudentPerformnaceMap().get("Total"))*100/Double.parseDouble(studentDetails.get(i).getStudentPerformnaceMap().get("maxTotal"));
				studentDetails.get(i).setPercentage(String.valueOf(new DecimalFormat("##.##").format(per)));
			}

			Collections.sort(studentDetails,new MySalaryComp1());

		}

		className=obj.classNameFromidSchid(schoolid,selectedClass,session,conn)+"-"+obj.sectionNameByIdSchid(schoolid,selectedSection,conn);
		termName=obj.semesterNameFromid(selectedTerm,conn);
		if(selectedType.equals("scholastic"))
		{
			examType="Scholastic";
		}
		else
		{
			examType="Co-scholastic";
		}
		
		showPrintButton=true;
		showStudentRecord=true;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageToStudentprents() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		SchoolInfoList ls=obj.fullSchoolInfo(conn);
		for(StudentInfo aa:studentDetails)
		{
			int j=0;
			String mesg="Dear Parents, \n\nYour Ward "+aa.getFname()+"'s, Marks In Exam-"+selectedExam +" Are : " ;
			for(SubjectInfo ss:subjectList)
			{
				HashMap<String,String> oo =(HashMap<String, String>) aa.getStudentPerformnaceMap();
				String ba=oo.get(ss.getSubjectId());

				if(ba==null)
				{
					j++;
				}
				else
				{
					mesg=mesg+" "+ss.getSubjectName()+"-"+ba;

				}
			}
			mesg=mesg+" "+" And Rank In Class-"+aa.getRank()+", With Percentage-"+aa.getPercentage()+"%. \n\nRegards, \n"+ls.getSchoolName();

			if(j!=subjectList.size()-1)
			{
				obj.messageurl1(String.valueOf(aa.getFathersPhone()),mesg,"",conn,obj.schoolId(),"");

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
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

	public String getSelectedExamName() {
		return selectedExamName;
	}

	public void setSelectedExamName(String selectedExamName) {
		this.selectedExamName = selectedExamName;
	}

	public boolean isShowGrade() {
		return showGrade;
	}

	public void setShowGrade(boolean showGrade) {
		this.showGrade = showGrade;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public boolean isShowMarks() {
		return showMarks;
	}

	public void setShowMarks(boolean showMarks) {
		this.showMarks = showMarks;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getMarksType() {
		return marksType;
	}

	public void setMarksType(String marksType) {
		this.marksType = marksType;
	}


}

class MySalaryComp implements Comparator<StudentInfo>{

	@Override
	public int compare(StudentInfo e1, StudentInfo e2) {

		if(!(e1.getStudentPerformnaceMap().get("Total").equals("") || e1.getStudentPerformnaceMap().get("Total").equalsIgnoreCase("AB") || e1.getStudentPerformnaceMap().get("Total").equalsIgnoreCase("ML") || e2.getStudentPerformnaceMap().get("Total").equals("") || e2.getStudentPerformnaceMap().get("Total").equalsIgnoreCase("AB") || e2.getStudentPerformnaceMap().get("Total").equalsIgnoreCase("ML")))
		{
			if(Float.parseFloat(e1.getStudentPerformnaceMap().get("Total")) < Float.parseFloat(e2.getStudentPerformnaceMap().get("Total"))){
				return 1;
			}
			else if(Float.parseFloat(e1.getStudentPerformnaceMap().get("Total"))==Float.parseFloat(e2.getStudentPerformnaceMap().get("Total"))){
				return 0;
			}
			else{
				return -1;
			}
		}

		return -1;

	}
}


class MySalaryComp1 implements Comparator<StudentInfo>{

	@Override
	public int compare(StudentInfo e1, StudentInfo e2) {

		return e1.getFname().compareToIgnoreCase(e2.getFname());
	}


}