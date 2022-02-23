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

@ManagedBean(name="declareResultStudentWIse")
@ViewScoped
public class DeclareResultStudentWiseBean implements Serializable
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


	public  DeclareResultStudentWiseBean()
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
		examList = obj.completeExamListForStdResultSelectItem(selectedClass, selectedTerm,selectedType ,schoolid, conn);
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
		
//		boolean checkExamResultDeclared = obj.checkExamResultDeclared(selectedExam,conn);
//		 // System.out.println(checkExamResultDeclared);
//		if(checkExamResultDeclared)
//		{
			studentDetails=obj.searchStudentListByClassSectionForResultDeclaration(selectedSection,selectedExam,selectedTerm,schoolid,"declare",conn);	
//		}
//		else
//		{	
//		    studentDetails=obj.searchStudentListByClassSectionForResultDeclaration(selectedSection,selectedExam,selectedTerm,schoolid,"undeclare",conn);
//		}
		
		if(studentDetails.size()<=0)
		{
			showTable = false;
			boolean checkExamResultDeclared = obj.checkExamResultDeclared(selectedExam,conn);
			if(checkExamResultDeclared)
			{
				ArrayList<StudentInfo> tempStuDetails = new ArrayList<StudentInfo>();
				tempStuDetails=obj.searchStudentListByClassSectionForResultDeclaration(selectedSection,selectedExam,selectedTerm,schoolid,"undeclare",conn);	

				if(tempStuDetails.size()==0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found, Please Check if Student Performace is added for selected Exam"));
				}
				else
				{	
				 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found, Please Check if exam result already declared"));
				} 
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found, Please Check if Student Performace is added for selected Exam"));
			}
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
			
//			boolean checkExamResultDeclared = obj.checkExamResultDeclared(selectedExam,conn);
//			int i = 0;
//			if(checkExamResultDeclared)
//			{
			int	i = obj.declareStudentResult(selectedStudent,selectedExam,selectedTerm,selectedSection,conn,"declare",schoolid);
//			}
//			else
//			{
//			
//			   ArrayList<StudentInfo> unselectedStudent = new ArrayList<>();
//			   for(StudentInfo a: studentDetails)
//			   {
//				 if(!selectedStudent.contains(a))
//				 {
//					unselectedStudent.add(a); 
//				 }
//			   }
//			
//			   i = obj.declareStudentResult(unselectedStudent,selectedExam,selectedTerm,selectedSection,conn,"undeclare",schoolid);
//			   if(i>=1)
//			   {
//					
//			   }
//			} 
			if(i>=1)
			{
				obj.updateExamResultDeclare(selectedExam,conn);
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				String notify=(String) ss.getAttribute("resultNotify");
				if(notify.equalsIgnoreCase("true"))
				{
					String examName = new DatabaseMethods1().examNameFromid(selectedExam, conn);
					String SemestaerName = new DatabaseMethods1().semesterNameFromid(selectedTerm, conn);
					String msg = "Result of Exam - "+examName+", Term - "+SemestaerName+" has been declared. Please check Student Performance in app.";
					String contactno = "", addNumber = "";
					for (StudentInfo ls : selectedStudent) {
						StudentInfo ls11 = new DatabaseMethods1().studentDetailslistByAddNo(schoolid, ls.getAddNumber(), conn);
							if (String.valueOf(ls11.getFathersPhone()).length() == 10
									&& !String.valueOf(ls11.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(ls11.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(ls11.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(ls11.getFathersPhone()).equals("0123456789")) {
								if (contactno.equals("")) {
									addNumber = ls11.getAddNumber();
									contactno = String.valueOf(ls11.getFathersPhone());
								} else {
									if (!contactno.contains(String.valueOf(ls11.getFathersPhone()))) {
										addNumber = addNumber + "," + ls11.getAddNumber();
										contactno = contactno + "," + String.valueOf(ls11.getFathersPhone());
									}
								}
							}
							
								obj.notification(schoolid, "Result Declared", msg,
										ls11.getFathersPhone() + "-" + ls11.getAddNumber() + "-" + schoolid, conn);
								obj.notification(schoolid, "Result Declared", msg,
										ls11.getMothersPhone() + "-" + ls11.getAddNumber() + "-" + schoolid, conn);
					}
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
