package exam;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import exam_module.SubExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SubjectInfo;
import session_work.RegexPattern;

@ManagedBean(name="addExamSubject")
@ViewScoped
public class AddExam implements Serializable {

	private static final long serialVersionUID = 1L;

	String regex=RegexPattern.REGEX;
	ArrayList<SubjectInfo> subjectList=new ArrayList<>();
	String selectedClass,selectedLanguage,examName,time,sub,posiMarks,negMarks,session,schid,
	selectedClassSection,username,userType;
	boolean showExam,showExamList,showSubExam,showSubSelection,showFor12;
	ArrayList<SelectItem> classList,termList,examList,subjectTypeList;
	Date examDate,examStartingTime;
	ArrayList<SubExamInfo> subExamList;
	SubExamInfo selectedExam;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm");
	SimpleDateFormat sf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	public AddExam()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);

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

	public void addNow() {
		
		Connection conn =  DataBaseConnection.javaConnection();
		schid = obj.schoolId();
		session = obj.selectedSessionDetails(schid, conn);
		
		String addDateStr = sf1.format(examDate);
		String startDateStr = sf2.format(examStartingTime);
		startDateStr = addDateStr+" "+startDateStr;
		
		try {
		  examStartingTime = sf3.parse(startDateStr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		int status = dbExam.addExamForSubject(selectedClass,selectedLanguage,examName,time,posiMarks,negMarks,examDate,examStartingTime,session,schid,conn);
		
		if(status>0) {
			 FacesContext.getCurrentInstance().addMessage(null, new  FacesMessage("Exam is Added"));
				selectedClass = "";
				selectedLanguage="";
				examName = "";
				time = "";
				posiMarks = "";
				negMarks = "";
				examDate = null;
				examStartingTime= null;
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new  FacesMessage("Something is wrong!"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public boolean isShowExam() {
		return showExam;
	}
	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
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
	public ArrayList<SelectItem> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
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

	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPosiMarks() {
		return posiMarks;
	}

	public void setPosiMarks(String posiMarks) {
		this.posiMarks = posiMarks;
	}

	public String getNegMarks() {
		return negMarks;
	}

	public void setNegMarks(String negMarks) {
		this.negMarks = negMarks;
	}

	public Date getExamStartingTime() {
		return examStartingTime;
	}

	public void setExamStartingTime(Date examStartingTime) {
		this.examStartingTime = examStartingTime;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	
	
	
}
