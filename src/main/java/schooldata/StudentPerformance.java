package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;

@ManagedBean(name="studentPerformance")
@ViewScoped
public class StudentPerformance implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<SubjectInfo> subjectList;
	ArrayList<SelectItem> examTypeList,allSubjectList,termList,sessionList,classList,sectionList,subExamList,subjectTypeList;
	String selectedType,selectedSubject,sub="",selectedClass,selectedExam,selectedTerm,selectedSection;
	boolean showExam,renderShowTable=true,showStudentRecord,showStudentRecordButton,showSubExam,showScholastic,showCoScholastic;
	String marksFormat;
	ArrayList<DayInfo> days;
	String message="";
	private boolean enableSaveBtn=true;
	String username,schoolid,type,clsTch;
	String subExam,session,maxMarks,subjectName,subExamTemp;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DataBaseMeathodJson objJson=new DataBaseMeathodJson();
	double marks;

	public void method()
	{
		showExam=false;
		selectedType=selectedExam=null;
		showStudentRecord=false;
		showStudentRecordButton=false;
	}

	public  StudentPerformance()
	{
		Connection conn=DataBaseConnection.javaConnection();
		clsTch="no";
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		session=DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		type=(String) ss.getAttribute("type");
		subjectTypeList=obj.subjectTypeList();
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal"))
		{
			classList=obj.allClass(conn);
		}
		else if (type.equalsIgnoreCase("academic coordinator") 
					|| type.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			classList = obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			clsTch = objJson.checkClassTeacherById(empid, schoolid, conn);
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
	  boolean checkExamSetting = obj.checkExamSettingMade(selectedClass,session,schoolid,conn);
	  if(checkExamSetting)
	  {	
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}

		showExam=false;
		selectedSection=null;
		selectedTerm=null;
		selectedType=null;
		selectedExam=null;
		showStudentRecord=false;
		showStudentRecordButton=false;
		
	  }
	  else
	  {
		    selectedSection= "";
			selectedTerm= "";
			selectedType= "";
			selectedExam= "";
			subExam = "";
			selectedSubject = "";
			sectionList = new ArrayList<>();
			termList = new ArrayList<SelectItem>();
			examTypeList = new ArrayList<SelectItem>();
			subExamList = new ArrayList<SelectItem>();
			allSubjectList = new ArrayList<SelectItem>();
			studentDetails = new ArrayList<StudentInfo>();
			showExam = false;
			showSubExam = false;
			showStudentRecord = false;
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Exam Setting for selected class to proceed further")); 
	  }
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExamType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		examTypeList=objExam.mainExamListOfClassSection(selectedClass,selectedType,selectedTerm,conn);
		if(type.equalsIgnoreCase("admin") 
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal"))
		{
			allSubjectList=obj.selectedSubjectTypeofClassSectionForExam(selectedClass,selectedType,conn,schoolid);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			allSubjectList=objExam.AllEMployeeSubjectByTypeForExam(empid,selectedSection,schoolid,selectedType,conn);
		}
		if(selectedType.equals("scholastic") || selectedType.equals("additional") )
		{
			showStudentRecord=false;
			renderShowTable=true;
			showExam=true;
			showScholastic=true;showCoScholastic=false;
		}
		else
		{
			showScholastic=false;showCoScholastic=true;
			showSubExam=showExam=false;
			
			try {
				
				if(examTypeList.size()>0)
				{
					selectedExam=examTypeList.get(0).getValue().toString();	
				}
			} catch (Exception e) {
				 // System.out.println("TestingCB Performance Schid-"+schoolid);
				e.printStackTrace();
			}
					
			
			createSubExamList();
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
		selectedSubject="";
		showStudentRecord=false;
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
				subExamValue();
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
	
	public void sessionDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String examAdded=objExam.checkSubjectAddedInExamOrNot(selectedClass, selectedType, selectedExam, selectedTerm, schoolid, selectedSubject, session, conn);
		if(examAdded.equals("notadded"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dear User,Subject not added in exam. Please add this subject in selected exam in EDIT EXAM option"));
		}
		else
		{
			marksFormat=objExam.examSettingDetail(selectedClass, conn).getMarks_grade();
			String status="Unlock";
			if(!type.equalsIgnoreCase("admin"))
			{
				status=obj.checkExamLockStatus(selectedClass, selectedTerm, selectedExam, selectedType, conn);
			}
			if(status.equalsIgnoreCase("Unlock"))
			{
				subExamValue();
				sub=obj.subject(selectedClass,/*selectedTerm,*/selectedType,selectedSubject,conn);
				marks=objExam.subjectMaxMarksFromId(selectedClass,selectedExam,subExamTemp,selectedSubject,selectedType,session,selectedTerm,conn);
				 // System.out.println(marks);
				maxMarks=String.valueOf(marks);
				 // System.out.println(maxMarks);
				sub=sub+"("+maxMarks+")";
				 // System.out.println(sub);
				studentDetail(conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Exam Has Been locked By Admin. Please Contact Admin For Editing in Marks!"));
			}
		}
		
		try 
		{
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void studentDetail(Connection conn)
	{
		String subjectType=obj.checkSubjectType(selectedSubject,conn);
		if(subjectType.equalsIgnoreCase("Mandatory"))
		{
			studentDetails=objExam.studentBasicDetailsForMarksheet(schoolid,"", conn,"byClass",selectedSection);
		}
		else
		{
			studentDetails=obj.searchStudentListBySubject(selectedClass,selectedSection,selectedSubject,conn);
		}
		DatabaseMethods1.allPerformance(selectedSection,selectedExam,subExamTemp, studentDetails,selectedType,selectedSubject,selectedTerm,conn);
		if(studentDetails.size()>0 )
		{
			showStudentRecordButton=true;
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			if(info.getBranch_id().equals("54"))
				Collections.sort(studentDetails, new MySalaryComp());
			else 
			{
				if(!info.getBranch_id().equals("52"))
					Collections.sort(studentDetails, new MyRollNoComp());
			}
			int x = 1;
			int p = 1;
			for(StudentInfo fv: studentDetails)
			{
				fv.setSerialNumber(p++);
			}
		}
		else
			showStudentRecordButton=false;
		
		
		showStudentRecord=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String saveData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		subExamValue();
		for(StudentInfo list: studentDetails)
		{
			if(selectedType.equalsIgnoreCase("scholastic") || selectedType.equals("additional"))
			{
				if(marksFormat.equals("marks"))
				{
					if(!list.getMarks().equalsIgnoreCase("Ab") && !list.getMarks().equalsIgnoreCase("ML") && !list.getMarks().equals(""))
					{
						try 
						{
							if(Double.parseDouble(list.getMarks())>marks)
							{
                                message="Dear User, \n Marks obtained can't be greater than total marks.";
								flag=1;
								break;
							}	
						} 
						catch (Exception e) 
						{
						     message="Dear User, \n Please Enter only Numeric Value or 'Ab' or 'ML'.";
						}
					}
				}
			}
		}
		if(flag==0)
		{
			boolean ls=objExam.addStudentPerformance(selectedSection,selectedExam,subExamTemp, studentDetails, /*subjectList,*/selectedType,selectedSubject,maxMarks,selectedTerm,conn,session);

			showStudentRecord=showStudentRecordButton=false;
			if(ls==true)
			{
				String refNo=addWorkLog(conn);
				if(refNo.equals(""))
					message="Dear User, \n Some Error Occur Please Try Again";
				else
					message="Dear User, \n Performance record updated successfully. Please Note Down Your Reference No : "+refNo+" For Any Further Query.";
					
				studentDetails=new ArrayList<>();
			}
			else
			{
				message="Dear User, \n Some Error Occur Please Try Again"	;
			}
		}
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String addWorkLog(Connection conn)
	{
		String className=obj.classname(selectedClass, schoolid, conn);
		String sectionname =obj.sectionNameByIdSchid(schoolid,selectedSection, conn);
		String termName=obj.semesterNameFromid(selectedTerm, conn);
		String typeOfArea =selectedType;
		String subjectName=obj.subjectNameFromid(selectedSubject, conn);
		String examName=obj.examNameFromid(selectedExam, conn);
		String subExamName=subExamTemp;
		String language=""+className+"-"+sectionname+"\n,"+""+termName+"\n,"+typeOfArea ;
		String description = "Class-"+selectedClass+",Section-"+selectedSection+",Term-"+selectedTerm+","+typeOfArea;
		
		if(!examName.equals(""))
		{
			language=language+"\n,"+examName;
			description += ",Exam-"+selectedExam;
		}
		
		if(!subExamName.equals(""))
		{
		   if(!examName.equals(subExamName))
		   {
			   language=language+"\n,"+subExamName;
			   description += ",SubExam-"+subExamTemp;
		   }
		}
		
		language=language+"\n,"+subjectName;
		description += ",Subject-"+selectedSubject;
		String addedBy=obj.employeeNameByuserName(username, conn);
		language=language+"\n,"+addedBy+"("+username+"-"+type+")" +"\n"+" Marks Submitted Successfully";
		
		String value="";
		for(StudentInfo lss:studentDetails)
		{
			if(value.equals(""))
			{
				if(lss.getMarks().equals(""))
				{
					value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
				}
				else
				{
					value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
				}
			}
			else
			{
				if(lss.getMarks().equals(""))
				{
					value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
				}
				else
				{
					value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
				}
			}
		}
		String refNo =obj.saveWorkLog(schoolid,language, username, "MARKS FEEDING",type, session, "WEB",value,description, conn);
		return refNo;
	}
	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}


	class MyRollNoComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			//	String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			//String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			if(e1.getRollNo()==null||e2.getRollNo()==null||e2.getRollNo().equals("")||e1.getRollNo().equals(""))
			{
				return 0;
			}
			else
			{
				if(e2.getRollNo().matches("-?\\d+(\\.\\d+)?")||e1.getRollNo().matches("-?\\d+(\\.\\d+)?"))
				{
					try
					{
						int sr1 = Integer.parseInt(e1.getRollNo());
						int sr2 = Integer.parseInt(e2.getRollNo());

						if(sr1 >= sr2)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					catch (Exception e)
					{
						return 0;
					}


				}
				else
				{
					return 0;


				}

			}




		}
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
	public String getSelectedTerm() {
		return selectedTerm;
	}
	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
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
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
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

	public String getSubExam() {
		return subExam;
	}

	public void setSubExam(String subExam) {
		this.subExam = subExam;
	}

	public String getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}

	public String getSubExamTemp() {
		return subExamTemp;
	}

	public void setSubExamTemp(String subExamTemp) {
		this.subExamTemp = subExamTemp;
	}

	public boolean isShowScholastic() {
		return showScholastic;
	}

	public void setShowScholastic(boolean showScholastic) {
		this.showScholastic = showScholastic;
	}

	public boolean isShowCoScholastic() {
		return showCoScholastic;
	}

	public void setShowCoScholastic(boolean showCoScholastic) {
		this.showCoScholastic = showCoScholastic;
	}

	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMarksFormat() {
		return marksFormat;
	}

	public void setMarksFormat(String marksFormat) {
		this.marksFormat = marksFormat;
	}
	
}
