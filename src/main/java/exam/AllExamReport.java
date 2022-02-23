package exam;

import java.io.Serializable;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;

@ManagedBean(name = "allexamreportbean")
@ViewScoped
public class AllExamReport implements Serializable{

	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	ArrayList<ExamPojo> examList = new ArrayList<>();
	String session,schid;
	SchoolInfo schoolInfo;
	ExamPojo selectedExam;
	String selectedClass,selectedLanguage,examName,time,sub,posiMarks,negMarks,selectedClassSection,username,userType;
	ArrayList<SelectItem> classList;
	Date examDate,examStartingTime;
	SimpleDateFormat sf1 = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm");
	SimpleDateFormat sf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public AllExamReport() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid  = dbm.schoolId();
		session = dbm.selectedSessionDetails(schid, conn);
		examList = dbExam.getAllExamList(session,schid,conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=dbm.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=dbm.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=dbm.allClassListForClassTeacher(empid,schid,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void changeStatus(ExamPojo value) {
		Connection conn = DataBaseConnection.javaConnection();
		String examStatus;
		if(value.getStatus().equalsIgnoreCase("Active")) {
			examStatus = "Closed";
		}else {
			examStatus = "Active";
		}
		int status = dbExam.changeStatus(value.getId(),examStatus,session,schid,conn);
		if(status>0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Status Updated"));
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editDetails() throws ParseException {
		selectedClass = selectedExam.getClassId();
		selectedLanguage = selectedExam.getLanguage();
		examName = selectedExam.getExamName();
		time = selectedExam.getTime();
		posiMarks = selectedExam.getPosiMarks();
		negMarks = selectedExam.getNegMarks();
		examDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectedExam.getExamDate());
		examStartingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(selectedExam.getExamStartingTime());
		String examStartingTimeZ = new SimpleDateFormat("HH:mm").format(examStartingTime);
		examStartingTime = new SimpleDateFormat("HH:mm").parse(examStartingTimeZ);
	}
	
	public void deleteExam() {

		Connection conn = DataBaseConnection.javaConnection();
		int status = dbExam.deleteExam(session,schid,selectedExam.getId(),"Deleted",conn);
		if(status>0) {
			PrimeFaces.current().executeInitScript("PF('dlg').hide();");
			examList = dbExam.getAllExamList(session,schid,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam is deleted!"));
			
		}else {
			PrimeFaces.current().executeInitScript("PF('dlg').hide();");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void editExam() {
		Connection conn = DataBaseConnection.javaConnection();
		
		 // System.out.println(examStartingTime);
		
		String addDateStr = sf1.format(examDate);
		String startDateStr = sf2.format(examStartingTime);
		startDateStr = addDateStr+" "+startDateStr;
		
		try {
		  examStartingTime = sf3.parse(startDateStr);
		  
		  
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		int status = dbExam.updateExam(selectedExam.getId(),selectedClass,selectedLanguage,examName,time,posiMarks,negMarks,examDate,examStartingTime,session,schid,conn);
		if(status>0) {
			PrimeFaces.current().executeInitScript("PF('editDialog').hide();");
			examList = dbExam.getAllExamList(session,schid,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam is Updated"));
			
		}else {
			PrimeFaces.current().executeInitScript("PF('editDialog').hide();");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DatabaseMethodExam getDbExam() {
		return dbExam;
	}

	public void setDbExam(DatabaseMethodExam dbExam) {
		this.dbExam = dbExam;
	}

	public ArrayList<ExamPojo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamPojo> examList) {
		this.examList = examList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public SchoolInfo getSchoolInfo() {
		return schoolInfo;
	}

	public void setSchoolInfo(SchoolInfo schoolInfo) {
		this.schoolInfo = schoolInfo;
	}

	public ExamPojo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamPojo selectedExam) {
		this.selectedExam = selectedExam;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
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

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Date getExamStartingTime() {
		return examStartingTime;
	}

	public void setExamStartingTime(Date examStartingTime) {
		this.examStartingTime = examStartingTime;
	}
	
	
	
}
