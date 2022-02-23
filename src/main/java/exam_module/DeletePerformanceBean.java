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

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import session_work.DatabaseMethodSession;

@ManagedBean(name="deletePerformance")
@ViewScoped

public class DeletePerformanceBean implements Serializable 
{
	ArrayList<StudentInfo> studentList, selStudentList=new ArrayList<StudentInfo>();
	ArrayList<StudentInfo>list=new ArrayList<>();
	ArrayList<ExamInfo> examList;
	ArrayList<SelectItem> classList, sectionList, termList,examTypeList,subExamTypeList,subjectTypeList;
	ArrayList<SubjectInfo> allSubExamList = new ArrayList<SubjectInfo>();
	String username, userType, schid, session, selectedSection, requestType,name;
	String selectedClass,selectedTerm,examName,subjectType,examType,selectExam,examTaken,selectSubExam,
	mainExamName,mainSubExamName, mainExamId, subExamId;
	boolean showSubExam, showExam, showExamDetails, showExamNameColumn;
	ArrayList<ExamInfo> selExamList=new ArrayList<ExamInfo>();
	ExamInfo selectedExam;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	
	public DeletePerformanceBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		subjectTypeList=obj.subjectTypeList();
		
		allClass(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allClass(Connection conn)
	{
		classList=obj.allClass(conn);
	}
	
	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);
		sectionList = obj.allSection(selectedClass, conn);
		selectedTerm=selectExam=subjectType=null;
		examTypeList = new ArrayList<>();
		subExamTypeList = new ArrayList<>();
		selectSubExam=null;
		showSubExam=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkSubjectType()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		examTypeList = new ArrayList<>();
		subExamTypeList = new ArrayList<>();
		selectSubExam=null;
		showSubExam=false;
		if(subjectType.equals("scholastic") || subjectType.equals("additional") || subjectType.equals("all"))
		{
			examTypeList=de.mainExamListOfClassSection(selectedClass,subjectType,selectedTerm,conn);
			selectExam=null;
			showExam=true;
		}
		else
		{
			showExam=false;
			examTypeList=de.mainExamListOfClassSection(selectedClass,subjectType,selectedTerm,conn);
			if(examTypeList!=null && examTypeList.size()>0)
			selectExam=(String) examTypeList.get(0).getValue();
			subExamMethod();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void subExamMethod()
	{
		selectSubExam=null;
		subExamTypeList = new ArrayList<>();
		showSubExam=false;
		Connection conn=DataBaseConnection.javaConnection();
		examTaken = de.checkSubExamTaken(selectedClass, selectExam, selectedTerm, conn);

		if(examTaken.equalsIgnoreCase("sub"))
		{
			subExamTypeList=de.subExamListInExam(selectedClass,selectExam,subjectType,selectedTerm,conn);
			showSubExam=true;
		}
		else
		{
			selectSubExam=de.examInfoById(selectExam,conn).getExamName();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void subjectDetail()
	{
		Connection conn = DataBaseConnection.javaConnection();
		examList = new ArrayList<>();
		selExamList = new ArrayList<ExamInfo>();
		studentList = new ArrayList<StudentInfo>();
		selStudentList = new ArrayList<StudentInfo>();
		
		try
		{
			showExamDetails=true;
			mainExamId = selectExam;
			if(subjectType.equals("scholastic") || subjectType.equals("additional") || subjectType.equals("all"))
			{
				subExamId = selectSubExam;
				examList=de.subExamListForExamEdit(selectExam,subjectType,session, selectedClass, selectedTerm, examTaken, selectSubExam, conn);
				ExamInfo exam= de.examInfoById(selectExam,conn);	
				
				mainExamName =exam.getExamName();
				examType=exam.getExamTaken();
				if(examTaken.equalsIgnoreCase("sub"))
				{
					mainSubExamName = selectSubExam;
				}
				else
				{
					mainSubExamName = mainExamName;
				}

				showExamNameColumn=true;
			}
			else
			{
				examTaken="main";
				ExamInfo exam= de.examInfoById(selectExam,conn);	
				
				mainExamName =exam.getExamName();
				examType=exam.getExamTaken();
				examList=de.subExamListForExamEdit(selectExam,subjectType,session, selectedClass, selectedTerm, examTaken, selectSubExam, conn);
				showExamNameColumn=false;
			}
			
			studentDetail();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void studentDetail()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		studentList=de.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkDelete()
	{
		requestType = "multiple";
		if(selExamList.size()>0)
		{
			PrimeFaces.current().executeInitScript("PF('dlg').show()");
			PrimeFaces.current().ajax().update("form3");
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one subject to proceed"));
		}
	}
	
	public void check()
	{
		requestType = "single";
	}
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//list=new DatabaseMethods1().searchStudentList(query,conn);
		list=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : list)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public String saveData()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selStudentList.size()>0)
		{
			int i = 0, x = 0;
			if(requestType.equalsIgnoreCase("multiple"))
			{
				System.out.println("multiple");
				String subject = "";
						
				for(ExamInfo ee : selExamList)
				{
					if(subject.equals(""))
					{
						subject = ee.getSubjectid();
					}
					else
					{
						subject = subject + "','" + ee.getSubjectid();
					}
				}
				for(StudentInfo ss : selStudentList)
				{
					i = de.deleteExamPerformance(ss.getAddNumber(), subject, 
							selectExam, selectedTerm, selectSubExam, examTaken, session, 
							schid, "multi", conn);
					if(i>=1)
					{
						x=x+1;
					}else if(i == -1) {
						x = i;
					}
				}
				
				//System.out.println("value of msg   "+msg);
				if(x>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance Deleted Successfully!"));
					selStudentList = new ArrayList<StudentInfo>();
					selExamList = new ArrayList<ExamInfo>();
					name = "";
				}else if(x ==-1) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong, try again later!"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is no exam performance in this class and subject"));
				}
			}
			else
			{
				for(StudentInfo ss : selStudentList)
				{
					i = de.deleteExamPerformance(ss.getAddNumber(), selectedExam.getSubjectid(), 
							selectExam, selectedTerm, selectSubExam, examTaken, session, 
							schid, "multi", conn);
					if(i>=1)
					{
						x=x+1;
					}else if(i == -1) {
						x = i;
					}
				}
				
				if(x>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance Deleted Successfully!"));
					selStudentList = new ArrayList<StudentInfo>();
					selExamList = new ArrayList<ExamInfo>();
					name = "";
				}else if(x == -1) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong, try again later!"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is no exam performance in this class and subject"));
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to delete performance"));
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void singleStudentDelete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			int i = de.deleteExamPerformance(id, "", "", "", "", "", session, schid, "single", conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Performance Deleted Successfully!"));
				selStudentList = new ArrayList<StudentInfo>();
				selExamList = new ArrayList<ExamInfo>();
				name = "";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong, try again later!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getSelStudentList() {
		return selStudentList;
	}

	public void setSelStudentList(ArrayList<StudentInfo> selStudentList) {
		this.selStudentList = selStudentList;
	}

	public ArrayList<ExamInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
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

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public ArrayList<SelectItem> getExamTypeList() {
		return examTypeList;
	}

	public void setExamTypeList(ArrayList<SelectItem> examTypeList) {
		this.examTypeList = examTypeList;
	}

	public ArrayList<SelectItem> getSubExamTypeList() {
		return subExamTypeList;
	}

	public void setSubExamTypeList(ArrayList<SelectItem> subExamTypeList) {
		this.subExamTypeList = subExamTypeList;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public ArrayList<SubjectInfo> getAllSubExamList() {
		return allSubExamList;
	}

	public void setAllSubExamList(ArrayList<SubjectInfo> allSubExamList) {
		this.allSubExamList = allSubExamList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public String getSelectExam() {
		return selectExam;
	}

	public void setSelectExam(String selectExam) {
		this.selectExam = selectExam;
	}

	public String getExamTaken() {
		return examTaken;
	}

	public void setExamTaken(String examTaken) {
		this.examTaken = examTaken;
	}

	public String getSelectSubExam() {
		return selectSubExam;
	}

	public void setSelectSubExam(String selectSubExam) {
		this.selectSubExam = selectSubExam;
	}

	public String getMainExamName() {
		return mainExamName;
	}

	public void setMainExamName(String mainExamName) {
		this.mainExamName = mainExamName;
	}

	public String getMainSubExamName() {
		return mainSubExamName;
	}

	public void setMainSubExamName(String mainSubExamName) {
		this.mainSubExamName = mainSubExamName;
	}

	public String getMainExamId() {
		return mainExamId;
	}

	public void setMainExamId(String mainExamId) {
		this.mainExamId = mainExamId;
	}

	public String getSubExamId() {
		return subExamId;
	}

	public void setSubExamId(String subExamId) {
		this.subExamId = subExamId;
	}

	public boolean isShowSubExam() {
		return showSubExam;
	}

	public void setShowSubExam(boolean showSubExam) {
		this.showSubExam = showSubExam;
	}

	public boolean isShowExam() {
		return showExam;
	}

	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public boolean isShowExamDetails() {
		return showExamDetails;
	}

	public void setShowExamDetails(boolean showExamDetails) {
		this.showExamDetails = showExamDetails;
	}

	public boolean isShowExamNameColumn() {
		return showExamNameColumn;
	}

	public void setShowExamNameColumn(boolean showExamNameColumn) {
		this.showExamNameColumn = showExamNameColumn;
	}

	public ArrayList<ExamInfo> getSelExamList() {
		return selExamList;
	}

	public void setSelExamList(ArrayList<ExamInfo> selExamList) {
		this.selExamList = selExamList;
	}

	public ExamInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamInfo selectedExam) {
		this.selectedExam = selectedExam;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	
}
