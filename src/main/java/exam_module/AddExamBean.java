package exam_module;

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
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.SubjectInfo;
import session_work.RegexPattern;

@ManagedBean(name="addExam")
@ViewScoped
public class AddExamBean implements Serializable {

	private static final long serialVersionUID = 1L;

	String regex=RegexPattern.REGEX;
	ArrayList<SubjectInfo> subjectList=new ArrayList<>();
	String selectedType,includeOrNot="Yes",selectedClass,selectedSemester,examName,examType,subExam
			,includeInResult="Yes",session,schid,username,userType;
	boolean showExam,showExamList,showSubExam,showSubSelection,showFor12;
	ArrayList<SelectItem> classList,termList,examList,subjectTypeList;
	ArrayList<SubExamInfo> subExamList;
	SubExamInfo selectedExam;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();


	public AddExamBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
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
				|| userType.equalsIgnoreCase("front office") 
				|| userType.equalsIgnoreCase("office staff") 
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

	public void showSubjects()
	{
	  if(examType == null)
	  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Class, Semester and Type of Exam before selecting Type of Area"));
			selectedType = "";
	  }
	  else
	  {	
		Connection conn=DataBaseConnection.javaConnection();

		subjectList=new ArrayList<>();
		subjectList=obj.subjectListDetailsOfPerticularClass(selectedClass,selectedType,conn);
		if(examType.equals("other"))
		{
			if(selectedType.equals("coscholastic") || selectedType.equals("other"))
			{
				examName="coschool";
				showExam=false;showExamList=false;
			}
			else if(selectedType.equals("scholastic")|| selectedType.equals("additional"))
			{
				examName="";
				showExam=true;showExamList=false;
			}
		}
		else
		{
			examList=obj.periodicExamNameList(selectedClass,conn);
			if(selectedType.equals("coscholastic") || selectedType.equals("other"))
			{
				examName="coschool";
				showExam=false;showExamList=false;
			}
			else if(selectedType.equals("scholastic")|| selectedType.equals("additional"))
			{
				showExam=false;showExamList=true;
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }	
	}

	public void checkSubExam()
	{
		Connection conn=DataBaseConnection.javaConnection();

		if(subExam.equals("Yes"))
		{
			showFor12=false;showSubExam=true;
			subExamList=new ArrayList<>();
			for(int i=1;i<=5;i++)
			{
				SubExamInfo exam=new SubExamInfo();
				exam.setsNo(String.valueOf(i));
				exam.setSubExamName("");
				exam.setSubjectList(subjectList);
				subExamList.add(exam);
			}
		}
		else
		{
			subjectList.clear();
			subjectList=obj.subjectListDetailsOfPerticularClass(selectedClass,selectedType,conn);
			showSubExam=false;showFor12=true;
			if(!examType.equals("other"))
			{
				String mark=objExam.examSettingDetail(selectedClass,conn).getActualMark();
				for(SubjectInfo ll: subjectList)
				{
					ll.setMarks(Integer.parseInt(mark));
				}
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void addSubjectInfoToSubExam()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<SubjectInfo> info=new ArrayList<>();
		for(SubjectInfo sub:subjectList)
		{
			info.add(sub);
		}
		for(SubExamInfo exam:subExamList)
		{
			if(selectedExam.getsNo().equals(exam.getsNo()))
			{
				exam.setSubjectList(info);
			}
		}
		subjectList.clear();
		subjectList=obj.subjectListDetailsOfPerticularClass(selectedClass,selectedType,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void clearArea()
	{
		selectedType="";showExamList=false;showExam=false;
		// // System.out.println(examType);
		if(examType.equals("periodicTest"))
		{
			includeOrNot="Yes";
			showExamList=true;showExam=false;
		}
		else
		{
			// // System.out.println("hiiii");
			includeOrNot="";
			showExamList=false;showExam=true;
		}
		
	}

	public void showSemester()
	{
		Connection conn = DataBaseConnection.javaConnection();
		ExamSettingInfo examInfo=objExam.examSettingDetail(selectedClass, conn);
		if(examInfo!=null)
		{
			termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);
		}
		else
		{
			selectedClass="";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Exam Setting First For Related Class"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try
		{
			int flag=0,flag2=0;
			if(subExam.equals("No"))
			{
				if(subjectList.isEmpty())
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Subject Record Found,Please Add Some Subject In This Class", "Validation error"));
				}
				else
				{
					flag++;
				}
			}
			else
			{
				for(SubExamInfo exam:subExamList)
				{
					if(!exam.getSubExamName().equals(""))
					{
						flag++;
						break;
					}
				}

				if(flag==0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Atleast One Sub Exam"));
				}
				else
				{
					for(SubExamInfo exam:subExamList)
					{
						if(!exam.getSubExamName().equals("") && exam.getSubjectList().isEmpty())
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Subject Marks Detail For "+exam.getSubExamName()+" Exam"));
							flag2++;
							break;
						}
					}
				}
			}

			if(flag2==0)
			{
				int status=objExam.duplicateExamType(selectedClass,examName,selectedType,selectedSemester,conn);
				if(status==0)
				{
					flag2++;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Entry Found,Try A Different One", "Validation error"));
				}
			}
			if(flag==1 && flag2==0)
			{
				int i=0;
				/*if(examType.equals("other"))
				{
					i=objExam.addExam(selectedClass,examName,subjectList,selectedType,subExamList,subExam,selectedSemester,conn,examType,includeInResult,includeOrNot);
				}
				else
				{
					if(selectedType.equalsIgnoreCase("coscholastic")  || selectedType.equals("other"))
					{
						i=objExam.addExam(selectedClass,examName,subjectList,selectedType,subExamList,subExam,selectedSemester,conn,examType,includeInResult,includeOrNot);
					}
					else
					{*/
						i=objExam.addExam(selectedClass,examName,subjectList,selectedType,subExamList,subExam,selectedSemester,conn,examType,includeInResult,includeOrNot);
/*					}

				}
*/				if(i==1)
				{
	              String refNo;
	              try {
		                refNo=addWorkLog(conn);
	               }
	              catch (Exception e) {
	            	  e.printStackTrace();
	             }
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Exam added successfully"));
					selectedType=includeOrNot=selectedClass=selectedSemester=examName=examType=includeInResult=subExam="";
					subExamList=new ArrayList<>();
					showFor12=showExamList=false;
					subjectList=new ArrayList<>();
					return "addExam.xhtml";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
				}
			}
			return null;
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
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	
		String className=obj.classname(selectedClass, schid, conn);
		
		language = "Class-"+className+" --Semester-"+selectedSemester+" --Exam Type-"+examType+" --SelectedType-"+selectedType+" --includeFinalMarksheet-"+includeOrNot+" --includeResultEve-"+includeInResult+" --SubExam-"+subExam+" --ExamName-"+examName;
		
		if(!subExam.equalsIgnoreCase("Yes"))
		{	
		  for(SubjectInfo sst: subjectList)
		  {
			  value += "(Name-"+sst.getSubjectName()+" --Marks-"+sst.getMarks()+") ";
		  }
		}
		else
		{
			value = " SubExam - :";
			for(SubExamInfo subb: subExamList)
			  {
				  value += "[SubExamName-"+subb.getSubExamName()+" --Check-"+subb.getCheckForResult()+") ";
				  value += " ,, Subjects-";
					for(SubjectInfo sst: subb.getSubjectList())
					  {
						  value += "(Name-"+sst.getSubjectName()+" --Marks-"+sst.getMarks()+") ";
					  }
					value += "]";
			  }
			
			
		}
		
		
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Exam","WEB",value,conn);
		return refNo;
	}
	

	public String getSelectedType() {
		return selectedType;
	}
	public boolean isShowExam() {
		return showExam;
	}
	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
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
	public String getSelectedSemester() {
		return selectedSemester;
	}
	public void setSelectedSemester(String selectedSemester) {
		this.selectedSemester = selectedSemester;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public boolean isShowExamList() {
		return showExamList;
	}
	public void setShowExamList(boolean showExamList) {
		this.showExamList = showExamList;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	public ArrayList<SelectItem> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}
	public String getIncludeOrNot() {
		return includeOrNot;
	}
	public void setIncludeOrNot(String includeOrNot) {
		this.includeOrNot = includeOrNot;
	}

	public String getSubExam() {
		return subExam;
	}

	public void setSubExam(String subExam) {
		this.subExam = subExam;
	}

	public boolean isShowSubExam() {
		return showSubExam;
	}

	public void setShowSubExam(boolean showSubExam) {
		this.showSubExam = showSubExam;
	}

	public boolean isShowSubSelection() {
		return showSubSelection;
	}

	public void setShowSubSelection(boolean showSubSelection) {
		this.showSubSelection = showSubSelection;
	}

	public boolean isShowFor12() {
		return showFor12;
	}

	public void setShowFor12(boolean showFor12) {
		this.showFor12 = showFor12;
	}

	public ArrayList<SubExamInfo> getSubExamList() {
		return subExamList;
	}

	public void setSubExamList(ArrayList<SubExamInfo> subExamList) {
		this.subExamList = subExamList;
	}

	public SubExamInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(SubExamInfo selectedExam) {
		this.selectedExam = selectedExam;
	}
	public String getIncludeInResult() {
		return includeInResult;
	}
	public void setIncludeInResult(String includeInResult) {
		this.includeInResult = includeInResult;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
