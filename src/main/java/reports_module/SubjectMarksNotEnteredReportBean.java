package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsExamReport;
import exam_module.ExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SubjectInfo;

@ManagedBean(name="subMarksReport")
@ViewScoped
public class SubjectMarksNotEnteredReportBean implements Serializable {

	private static final long serialVersionUID = 1L;

	ArrayList<SubjectInfo> subjectList,optSubjectList,finalSubList = new ArrayList<>();
	ArrayList<ExamInfo> subjectNotList;
	String selectedClass,selectedSemester,selectedSection,selectedType,session,schid,username,userType;
	ArrayList<SelectItem> classList,termList,sectionList,subjectTypeList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsExamReport objExam=new DataBaseMethodsExamReport();


	public SubjectMarksNotEnteredReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		finalSubList = new ArrayList<>();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		subjectTypeList=obj.subjectTypeList();

		if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassListForClassTeacher(empid,schid,conn);
		}

		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void showSemester()
	{
		Connection conn = DataBaseConnection.javaConnection();

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
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchReport()
	{
		subjectNotList=new ArrayList<>();
		finalSubList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();

		subjectList=obj.mandatorySubjectListDetailsOfPerticularClass(selectedClass,selectedType,conn);
		optSubjectList=obj.optionalSubjectListDetailsOfPerticularClass(selectedClass,selectedSection,selectedType,conn);
		finalSubList.addAll(subjectList);
		finalSubList.addAll(optSubjectList);
		subjectNotList=objExam.subjectMarksNotEnteredReport(selectedClass,selectedSection,selectedType,selectedSemester,finalSubList,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}


	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}


	public String getSelectedClass() {
		return selectedClass;
	}


	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}


	public String getSelectedSemester() {
		return selectedSemester;
	}


	public void setSelectedSemester(String selectedSemester) {
		this.selectedSemester = selectedSemester;
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


	public String getSelectedSection() {
		return selectedSection;
	}


	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}


	public String getSelectedType() {
		return selectedType;
	}


	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}


	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}


	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}


	public ArrayList<ExamInfo> getSubjectNotList() {
		return subjectNotList;
	}


	public void setSubjectNotList(ArrayList<ExamInfo> subjectNotList) {
		this.subjectNotList = subjectNotList;
	}


	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}


	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}
}
