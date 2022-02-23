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

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;
import exam_module.ExamInfo;
import exam_module.SubExamInfo;

@ManagedBean(name="averageMarks")
@ViewScoped
public class MakeAverageOfMarksBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,allExamList,showExamList=new ArrayList<>(),allSubjectList;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<TermInfo> termList;
	ArrayList<String> selectedExamList;
	boolean showStudentRecord;
	String term,selectedSection,selectedTerm,selectedClass,selectedSubject,schoolid,username,userType;
	DatabaseMethods1 dbObj=new DatabaseMethods1();
	DataBaseMethodsBLMExam objExam=new DataBaseMethodsBLMExam();
	public  MakeAverageOfMarksBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff"))
		{
			classList=dbObj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=dbObj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=dbObj.allClassListForClassTeacher(empid,schoolid,conn);

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		String session=DatabaseMethods1.selectedSessionDetails(dbObj.schoolId(),conn);
		allSubjectList=dbObj.selectedSubjectTypeofClassSection(selectedClass,"scholastic",conn);

		termList=objExam.termListByClassId(selectedClass, session,"scholastic", conn,"average","all");
		String termId="",examId="",termName="",examName="",subExamName="";

		allExamList=new ArrayList<>();
		for(TermInfo term:termList)
		{
			termId=term.getTermId();
			termName=term.getTermName();
			for(ExamInfo exam:term.getExamInfoList())
			{
				examId=String.valueOf(exam.getId());
				if(exam.getId()>0)
				{
					examName=exam.getExamName();
					for(SubExamInfo subExam:exam.getSubExamList())
					{
						subExamName=subExam.getSubExamName();
						if(exam.getSubExamList().size()>1)
						{
							SelectItem ll=new SelectItem();
							ll.setLabel(termName+" - "+examName+" - "+subExamName+"("+subExam.getMaxMark()+")");
							ll.setValue(termId+" - "+examId+" - "+subExamName);
							allExamList.add(ll);
						}
						else
						{
							SelectItem ll=new SelectItem();
							ll.setLabel(termName+" - "+examName+"("+subExam.getMaxMark()+")");
							ll.setValue(termId+" - "+examId+" - "+subExamName);
							allExamList.add(ll);
						}

					}
				}
			}
		}


		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=dbObj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=dbObj.allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		
		selectedSection=null;selectedTerm=null;

		showStudentRecord=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
	 
		try 
		{
		
			if(selectedExamList.size()>0)
			{
				String schid=dbObj.schoolId();
				studentDetails=new ArrayList<>();
				//studentDetails=dd.searchStudentListByClassSection(selectedSection,conn);
				studentDetails=new DataBaseMethodsExam().studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
				objExam.marksOfExamInSubject(studentDetails,selectedSection,selectedExamList,selectedSubject,conn);

				showExamList=new ArrayList<>();
				
				for(String exam:selectedExamList)
				{
					for(SelectItem ll:allExamList)
					{
						if(exam.equals(ll.getValue()))
						{
							showExamList.add(ll);
						}
					}
				}


				SelectItem ll=new SelectItem();
				ll.setLabel("Total Marks");
				ll.setValue("Total");
				showExamList.add(ll);

				//		ll=new SelectItem();
				//		ll.setLabel("Average");
				//		ll.setValue("Average");
				//		showExamList.add(ll);

				ll=new SelectItem();
				ll.setLabel("Average %");
				ll.setValue("average per");
				showExamList.add(ll);

				showStudentRecord=true;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select at least one exam"));
				showStudentRecord=false;
			}
			
		} 
		catch (Exception e) {
			// TODO: handle exception
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

	public void printMarksheet()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedClass", selectedClass);
		ss.setAttribute("selectedSection", selectedSection);
		ss.setAttribute("selectedTerm", selectedTerm);
		PrimeFaces.current().executeInitScript("window.open('printBLM_MonthlyEvaluation.xhtml')");
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
	}

	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<SelectItem> getAllExamList() {
		return allExamList;
	}

	public void setAllExamList(ArrayList<SelectItem> allExamList) {
		this.allExamList = allExamList;
	}

	public ArrayList<String> getSelectedExamList() {
		return selectedExamList;
	}

	public void setSelectedExamList(ArrayList<String> selectedExamList) {
		this.selectedExamList = selectedExamList;
	}

	public ArrayList<SelectItem> getShowExamList() {
		return showExamList;
	}

	public void setShowExamList(ArrayList<SelectItem> showExamList) {
		this.showExamList = showExamList;
	}

	public ArrayList<SelectItem> getAllSubjectList() {
		return allSubjectList;
	}

	public void setAllSubjectList(ArrayList<SelectItem> allSubjectList) {
		this.allSubjectList = allSubjectList;
	}

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}
}
