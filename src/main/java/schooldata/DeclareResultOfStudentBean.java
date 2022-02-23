package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="declareStdResult")
@ViewScoped
public class DeclareResultOfStudentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentDetails,selectedStudent;
	ArrayList<SelectItem> termList,classList,sectionList,examList,subjectTypeList;
	String selectedClass,selectedTerm,selectedSection,selectedExam,selectedType;
	String username,schoolid,type,session;
	boolean showTable;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMeathodJson objjson = new DataBaseMeathodJson();



	public  DeclareResultOfStudentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
		subjectTypeList=obj.subjectTypeList();
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		session=obj.selectedSessionDetails(schoolid, conn);
		type=(String) ss.getAttribute("type");
		if(type.equalsIgnoreCase("admin"))
		{
			classList=obj.allClass(conn);
		}
		else
		{
			String empid=objjson.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
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
		if(type.equalsIgnoreCase("admin"))
		{
			sectionList=obj.allSection(selectedClass,conn);

		}
		else
		{
			String empid=objjson.employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);

		}
		selectedSection=null;
		selectedTerm=null;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExamType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		examList = obj.examListForStdResultSelectItem(selectedClass, selectedTerm,selectedType ,schoolid, conn);
		if(examList.size()<=0)
		{
			showTable = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Any Exam Result Is Not Declared Yet"));
		}
		else
		{
			showTable = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentDetails=obj.searchStudentListByClassSectionForResultDeclaration(selectedSection,selectedExam,selectedTerm,schoolid,"declare",conn);
		if(studentDetails.size()<=0)
		{
			showTable = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found."));
		}
		else
		{
			showTable = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void studentDetail1()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentDetails=obj.searchStudentListByClassSectionForResultDeclaration(selectedSection,selectedExam,selectedTerm,schoolid,"undeclare",conn);
		if(studentDetails.size()<=0)
		{
			showTable = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found."));
		}
		else
		{
			showTable = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void declare()
	{
		if(selectedStudent.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one Student for result declaration."));
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();

			int i = obj.declareStudentResult(selectedStudent,selectedExam,selectedTerm,selectedSection,conn,"declare",schoolid);
			if(i>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				studentDetails=new ArrayList<>();showTable=false;
				selectedClass=selectedSection=selectedTerm=selectedExam="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result of Selected Student(s) is Declared Successfully."));
			}
			else if (i<0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance Is Not Added Yet"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

	public void undeclare()
	{
		if(selectedStudent.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one Student for result undeclaration."));
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();

			int i = obj.declareStudentResult(selectedStudent,selectedExam,selectedTerm,selectedSection,conn,"undeclare",schoolid);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
				     e.printStackTrace();
				}
				studentDetails=new ArrayList<>();showTable=false;
				selectedClass=selectedSection=selectedTerm=selectedExam="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result of Selected Student(s) is Set On Hold Successfully."));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "Class - "+selectedClass +" --- Term - "+selectedTerm+" --- Section - "+selectedSection+" --- Exam - "+selectedExam;
		
		for(StudentInfo ec: selectedStudent)
		{
			value += ec.getAddNumber()+" -- ";
		}
		if(selectedStudent.size()>0)
		{
			value = value.substring(0, value.length()-4); 
		}
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Undeclare Result Student","WEB",value,conn);
		return refNo;
	}

	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "Class - "+selectedClass +" --- Term - "+selectedTerm+" --- Section - "+selectedSection+" --- Exam - "+selectedExam;
		
		for(StudentInfo ec: selectedStudent)
		{
			value += ec.getAddNumber()+" -- ";
		}
		if(selectedStudent.size()>0)
		{
			value = value.substring(0, value.length()-4); 
		}
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Declare Result Student","WEB",value,conn);
		return refNo;
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
	
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public String getSelectedTerm() {
		return selectedTerm;
	}
	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
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

	public ArrayList<StudentInfo> getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(ArrayList<StudentInfo> selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public ArrayList<SelectItem> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(String selectedExam) {
		this.selectedExam = selectedExam;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
}
