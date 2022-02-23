package exam_module;

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

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SubjectInfo;
import session_work.RegexPattern;

@ManagedBean(name="editExam")
@ViewScoped
public class EditExamBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<ExamInfo> examList;
	ArrayList<SelectItem> subjectList;
	ArrayList<SubjectInfo> newSubjectList;
	ArrayList<SubjectInfo> selectedSubjectList;
	ExamInfo selectedExam;
	boolean editDetailsShow,showExam,showExamDetails,showExamNameColumn,showSubExam,renSingleExam,renSingleSubExam,deleteMainExamRen,deleteSubExamRen;
	ArrayList<SelectItem> classList,termList,examTypeList,subExamTypeList,subjectTypeList;
	ArrayList<String> selectedSubject;
	String selectedClass,selectedTerm,selectedSemester,examName,selectedClassTemp,selectedSemesterTemp,subjectType,examType,examNameTemp,selectExam,examTaken,selectSubExam,mainExamName,mainSubExamName;
	ExamInfo exam, examInfo;
	String session,schid,username,userType,includeOrNot;
	String mainExamId,subExamId;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<SubjectInfo> allSubExamList = new ArrayList<SubjectInfo>();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsExam de=new DataBaseMethodsExam();



	public EditExamBean()
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
	}
	
	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);

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

	public void checkExamSubjects()
	{
		if((examList.size()==1)&&examTaken.equalsIgnoreCase("main")) {
			renSingleExam = true;
		}
		else
		{
			renSingleExam = false;
		}
		if((examList.size()==1)&&examTaken.equalsIgnoreCase("sub")) {
			renSingleSubExam = true;
		}
		else
		{
			renSingleSubExam = false;
		}
	
	}

	public void rowSelectionListner()
	{
		Connection conn = DataBaseConnection.javaConnection();

		if(exam!=null)
		{
			allClass(conn);
			termList=obj.selectedTermOfClass(exam.getClassid(),conn,session,schid);

			examName=exam.getExamName();
			selectedClass=exam.getClassid();
			selectedSemester=exam.getSemesterid();
			selectedClassTemp=selectedClass;
			selectedSemesterTemp=selectedSemester;
			examNameTemp=examName;

			editDetailsShow=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one exam", "Validation error"));
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

	public void checkSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		newSubjectList=new ArrayList<>();
		newSubjectList=obj.subjectListClassAndTypeWise(selectedClass,subjectType,selectedTerm,selectExam,examTaken,selectSubExam,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editExamName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String table = "exam_table_cbse";
		String column = "examName";
		String whereCol = "id";
		de.editExamName(table,column,whereCol,mainExamName,mainExamId,conn);

		if(!examTaken.equalsIgnoreCase("sub"))
		{
			table = "sub_exam_table_cbse";
			column = "examName";
			whereCol = "mainExamId";
			de.editExamName(table,column,whereCol,mainExamName,mainExamId,conn);

			table = "student_performance_cbse";
			column = "subExamName";
			whereCol = "mainExamId";
			de.editExamName(table,column,whereCol,mainExamName,mainExamId,conn);
		}

		subjectDetail();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editInclude() {
		Connection conn = DataBaseConnection.javaConnection();
		int status = de.editIncludeOrNot(mainExamId,includeOrNot,conn);
		if(status>0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated"));
			subjectDetail();
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Not Updated successfully", "Error"));
			
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editSubExamName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String table = "sub_exam_table_cbse";
		String column = "examName";
		String whereCol = "mainExamId";
		de.editSubExamName(table,column,whereCol,mainSubExamName,subExamId,mainExamId,conn);
		String refNo;
		try {
			refNo=addWorkLog(conn,table,column,whereCol,mainSubExamName,subExamId,mainExamId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		table = "student_performance_cbse";
		column = "subExamName";
		whereCol = "mainExamId";
		de.editSubExamName(table,column,whereCol,mainSubExamName,subExamId,mainExamId,conn);
		String refNo2;
		try {
			refNo2=addWorkLog(conn,table,column,whereCol,mainSubExamName,subExamId,mainExamId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		subjectDetail();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSubjects()
	{
		Connection conn = DataBaseConnection.javaConnection();

		examInfo = de.examInfoById(selectExam,conn);
		String maxMarks = examList.get(0).getMaxMark();
		
		if(selectedSubjectList.size()>0)
		{
			String enm = "";
			if(examTaken.equalsIgnoreCase("main"))
			{
				enm = examInfo.getExamName();
			}
			int i = de.addSubjectsInExistingExam(subjectType,selectedClass,selectedTerm,selectExam,selectedSubjectList, enm, maxMarks,examTaken,selectSubExam,conn);
			if(i>=1)
			{
				String refNo4;
				try {
					refNo4=addWorkLog3(conn,enm,maxMarks,selectedSubjectList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subjects Added Successfully!"));
				selectedSubjectList = new ArrayList<>();
				subjectDetail();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("All Subjects are already Added!"));
				selectedSubjectList = new ArrayList<>();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one subject!"));
			selectedSubjectList = new ArrayList<>();
		}

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
		if(subjectType.equals("scholastic") || subjectType.equals("additional"))
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
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void examDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();

		try
		{
			examList=obj.examList(subjectType,conn);
			showExamDetails=true;

			if(subjectType.equals("scholastic")|| subjectType.equals("additional"))
			{
				showExamNameColumn=true;
			}
			else if(subjectType.equals("coscholastic"))
			{
				showExamNameColumn=false;
			}

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

	public void subjectDetail()
	{
		Connection conn = DataBaseConnection.javaConnection();
		examList = new ArrayList<>();
		try
		{
			showExamDetails=true;
			mainExamId = selectExam;
			if(subjectType.equals("scholastic") || subjectType.equals("additional"))
			{
				subExamId = selectSubExam;
				examList=de.subExamListForExamEdit(selectExam,subjectType,session, selectedClass, selectedTerm, examTaken, selectSubExam, conn);
				ExamInfo exam= de.examInfoById(selectExam,conn);
				includeOrNot = exam.getInclude();
				
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
				includeOrNot = exam.getInclude();
				mainExamName =exam.getExamName();
				examType=exam.getExamTaken();
				examList=de.subExamListForExamEdit(selectExam,subjectType,session, selectedClass, selectedTerm, examTaken, selectSubExam, conn);
				showExamNameColumn=false;
			}
			
			//For deleting Main Exam button
			allSubExamList = de.findAllSubExamsOfExam(selectExam,conn);
			int flagdel = 0;
			for(SubjectInfo st : allSubExamList)
			{
				boolean check = obj.checkExamSubjectPerformance(st.getSubjectId(), selectExam, selectedTerm,st.getSubjectType(),examTaken,st.getExam(), conn);	
				
				if(check)
					flagdel = 1;
			}
			
			deleteMainExamRen = true;
			if(flagdel ==0)
			{
				deleteMainExamRen = false;
			}
			
			//For deleting sub Exam
			deleteSubExamRen = true;
			if(examTaken.equalsIgnoreCase("sub")) {
				
				int flagdelSub = 0;
				for(ExamInfo ecx : examList)
				{
					if(ecx.getPerformanceAdded().equalsIgnoreCase("yes"))
						flagdelSub = 1;
				}
				
				if(flagdelSub ==0)
				{
					deleteSubExamRen = false;
				}
				
			}
			
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

	public void editDetails()
	{
		examName=selectedExam.getExamName();
	}
	
	
	public void deleteCompleteExam()
	{
		Connection conn = DataBaseConnection.javaConnection();

		allSubExamList = de.findAllSubExamsOfExam(selectExam,conn);
		for(SubjectInfo exp : allSubExamList)
		{
				int i=obj.deleteExamSubjectNew(exp,conn);	
		}
			
		
		int p =obj.deleteExamNew(selectedSemester,selectedClass,selectExam,subjectType, conn);
		if(p==1)
		{
			FacesContext fc2=FacesContext.getCurrentInstance();
			fc2.addMessage(null, new FacesMessage("Exam Deleted Successfully!"));
		
				
		}
		showExamDetails = false;
		selectedClass = "";
		selectExam = "";
		selectedTerm = "";
		selectSubExam = "";
		subjectType = "";
		termList = new ArrayList<SelectItem>();
		subExamTypeList = new ArrayList<SelectItem>();
		examTypeList = new ArrayList<SelectItem>();
		showExam = false;
		showSubExam = false;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void deleteSubExam()
	{
		Connection conn = DataBaseConnection.javaConnection();
		//Deleting Sub Exam
		int i =0;
		for(ExamInfo exp : examList)
		{
			i=obj.deleteExamSubject(exp,conn);	
		}
		if(i>=1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Sub Exam Deleted Successfully!"));
		}
		
		//Checking  other Sub Exam Present , if not (deleting the main exam)
		allSubExamList = de.findAllSubExamsOfExam(selectExam,conn);
        if(allSubExamList.size()==0)
        {	
		  int p =obj.deleteExamNew(selectedSemester,selectedClass,selectExam,subjectType, conn);
		  if(p==1)
		  {
			FacesContext fc2=FacesContext.getCurrentInstance();
			fc2.addMessage(null, new FacesMessage("Exam Deleted Successfully!"));
			
				
		  }
        }
    	showExamDetails = false;
		selectedClass = "";
		selectExam = "";
		selectedTerm = "";
		selectSubExam = "";
		subjectType = "";
		termList = new ArrayList<SelectItem>();
		subExamTypeList = new ArrayList<SelectItem>();
		examTypeList = new ArrayList<SelectItem>();
		showExam = false;
		showSubExam = false;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();


		try
		{
			boolean check = obj.checkExamSubjectPerformance(selectedExam.getSubjectid(), selectExam, selectedTerm,selectedExam.getSubjectType(),examTaken,selectSubExam, conn);
			if(check == true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Subject Can't Be Deleted, as Performance For This Subject Is Already Added!"));
			}
			else
			{
				int i=obj.deleteExamSubject(selectedExam,conn);
				if(i>=1)
				{
					String refNo3;
					try {
						refNo3=addWorkLog2(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Selected Subject Deleted Successfully!"));
					subjectDetail();
					
					if((renSingleExam==true)&&examTaken.equalsIgnoreCase("main"))
					{
						
						int p =obj.deleteExamNew(selectedSemester,selectedClass,selectExam,subjectType, conn);
						if(p==1)
						{
							FacesContext fc2=FacesContext.getCurrentInstance();
							fc2.addMessage(null, new FacesMessage("Exam Deleted Successfully!"));
							showExamDetails = false;
							selectedClass = "";
							selectExam = "";
							selectedTerm = "";
							selectSubExam = "";
							subjectType = "";
							termList = new ArrayList<SelectItem>();
							subExamTypeList = new ArrayList<SelectItem>();
							examTypeList = new ArrayList<SelectItem>();
							showExam = false;
							showSubExam = false;
							
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
							FacesContext fcc = FacesContext.getCurrentInstance();
							ExternalContext ec = fcc.getExternalContext();
							try {
								ec.redirect("editExam.xhtml");
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}
					}
					renSingleExam = false;
					
					
					if((renSingleSubExam==true)&&examTaken.equalsIgnoreCase("sub"))
					{
						allSubExamList = de.findAllSubExamsOfExam(selectExam,conn);
                        if(allSubExamList.size()==0)
                        {	
						  int p =obj.deleteExamNew(selectedSemester,selectedClass,selectExam,subjectType, conn);
						  if(p==1)
						  {
							FacesContext fc2=FacesContext.getCurrentInstance();
							fc2.addMessage(null, new FacesMessage("Exam Deleted Successfully!"));
							showExamDetails = false;
							termList = new ArrayList<SelectItem>();
							subExamTypeList = new ArrayList<SelectItem>();
							examTypeList = new ArrayList<SelectItem>();
							selectedClass = "";
							selectExam = "";
							selectedTerm = "";
							selectSubExam = "";
							subjectType = "";
							showExam = false;
							showSubExam = false;
							
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
							FacesContext fcc = FacesContext.getCurrentInstance();
							ExternalContext ec = fcc.getExternalContext();
							try {
								ec.redirect("editExam.xhtml");
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						  }
                        } 
					}
					renSingleSubExam = false;
					
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong! Please Try Again Later.", "Validation error"));
				}
			}
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
	public void showSemester()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);
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
	public void updateExamName()
	{
		Connection conn = DataBaseConnection.javaConnection();

		int i=obj.updateExamName(selectedExam, examName,conn);
		if(i==1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Exam details updated successfully"));

			examList=obj.examList(subjectType,conn);
			editDetailsShow=false;
			showExam=false;
			exam=new ExamInfo();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//ForChanging Exam Name
		public String addWorkLog(Connection conn,String table,String column,String whereCol,String mainSubExamName,String subExamId,String mainExamId)
		{
		    String value = "";
			String language= "";
			
			language = selectedClass+" -- "+selectedTerm+" -- "+subjectType+" -- "+selectExam+" -- "+selectSubExam+" ---- "+mainSubExamName;
			value = table+" -- "+column+" -- "+whereCol+" -- "+mainSubExamName+" -- "+subExamId+" -- "+mainExamId;

			String refNo = workLg.saveWorkLogMehod(language,"Exam Name Change","WEB",value,conn);
			return refNo;
		}
		
		//For Removing
		public String addWorkLog2(Connection conn)
		{
		    String value = String.valueOf(selectedExam.getId());
			String language= selectedClass+" -- "+selectedTerm+" -- "+subjectType+" -- "+selectExam+" -- "+selectSubExam;
			

			String refNo = workLg.saveWorkLogMehod(language,"Remove Exam","WEB",value,conn);
			return refNo;
		}

		//For Modifyup
		public String addWorkLog3(Connection conn,String enm,String maxMarks,ArrayList<SubjectInfo> selSubjects)
		{
		    String value = enm+" ---- "+maxMarks+" ---- ";
			String language= selectedClass+" -- "+selectedTerm+" -- "+subjectType+" -- "+selectExam+" -- "+selectSubExam;
		
			for(SubjectInfo cv:selSubjects)
			{
				value += "("+cv.getId()+" - "+cv.getMarks()+")";
			}
			String refNo = workLg.saveWorkLogMehod(language,"Modify Exam","WEB",value,conn);
			return refNo;
		}


	public String getSubjectType() {
			return subjectType;
		}

		public void setSubjectType(String subjectType) {
			this.subjectType = subjectType;
		}

	public boolean isShowExam() {
		return showExam;
	}

	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public ExamInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamInfo selectedExam) {
		this.selectedExam = selectedExam;
	}
	public ArrayList<ExamInfo> getExamList()
	{
		return examList;
	}

	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
	}

	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}

	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
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

	public ExamInfo getExam() {
		return exam;
	}

	public void setExam(ExamInfo exam) {
		this.exam = exam;
	}public boolean isShowExamNameColumn() {
		return showExamNameColumn;
	}

	public void setShowExamNameColumn(boolean showExamNameColumn) {
		this.showExamNameColumn = showExamNameColumn;
	}
	public boolean isShowExamDetails() {
		return showExamDetails;
	}

	public void setShowExamDetails(boolean showExamDetails) {
		this.showExamDetails = showExamDetails;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(ArrayList<String> selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public ArrayList<SelectItem> getExamTypeList() {
		return examTypeList;
	}

	public void setExamTypeList(ArrayList<SelectItem> examTypeList) {
		this.examTypeList = examTypeList;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getSelectExam() {
		return selectExam;
	}

	public void setSelectExam(String selectExam) {
		this.selectExam = selectExam;
	}

	public ArrayList<SubjectInfo> getNewSubjectList() {
		return newSubjectList;
	}

	public void setNewSubjectList(ArrayList<SubjectInfo> newSubjectList) {
		this.newSubjectList = newSubjectList;
	}

	public ArrayList<SubjectInfo> getSelectedSubjectList() {
		return selectedSubjectList;
	}

	public void setSelectedSubjectList(ArrayList<SubjectInfo> selectedSubjectList) {
		this.selectedSubjectList = selectedSubjectList;
	}

	public boolean isShowSubExam() {
		return showSubExam;
	}

	public void setShowSubExam(boolean showSubExam) {
		this.showSubExam = showSubExam;
	}

	public ArrayList<SelectItem> getSubExamTypeList() {
		return subExamTypeList;
	}

	public void setSubExamTypeList(ArrayList<SelectItem> subExamTypeList) {
		this.subExamTypeList = subExamTypeList;
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

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public boolean isRenSingleExam() {
		return renSingleExam;
	}

	public void setRenSingleExam(boolean renSingleExam) {
		this.renSingleExam = renSingleExam;
	}

	public boolean isDeleteMainExamRen() {
		return deleteMainExamRen;
	}

	public void setDeleteMainExamRen(boolean deleteMainExamRen) {
		this.deleteMainExamRen = deleteMainExamRen;
	}

	public boolean isDeleteSubExamRen() {
		return deleteSubExamRen;
	}

	public void setDeleteSubExamRen(boolean deleteSubExamRen) {
		this.deleteSubExamRen = deleteSubExamRen;
	}

	public boolean isRenSingleSubExam() {
		return renSingleSubExam;
	}

	public void setRenSingleSubExam(boolean renSingleSubExam) {
		this.renSingleSubExam = renSingleSubExam;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public String getIncludeOrNot() {
		return includeOrNot;
	}

	public void setIncludeOrNot(String includeOrNot) {
		this.includeOrNot = includeOrNot;
	}

}
