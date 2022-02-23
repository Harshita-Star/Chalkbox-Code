package exam;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="stdExam")
@ViewScoped
public class StudentExamModuleBean implements Serializable{
	
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schid,session,studentId,classid,sectionId,className,sectionName;
	StudentInfo studentDetails;
	ArrayList<QuestionInfo> questions = new ArrayList<>();
	ArrayList<ExamPojo> exams = new ArrayList<>();
	ExamPojo exam = new ExamPojo();
	
	public StudentExamModuleBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentId = (String) ss.getAttribute("username");
//		studentId = "CB109961";
		schid = dbm.schoolId();
		session = dbm.selectedSessionDetails(schid, conn);
		studentDetails = new DataBaseMeathodJson().studentDetailslistByAddNo(studentId, schid, conn);
		classid = studentDetails.getClassId();
		className = studentDetails.getClassName();
		sectionId = studentDetails.getSectionid();
		sectionName = studentDetails.getSectionName();
	
		exams = dbExam.allExamsForStudent(classid, session, schid, "Active", conn);
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public StudentInfo getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(StudentInfo studentDetails) {
		this.studentDetails = studentDetails;
	}

	public ArrayList<QuestionInfo> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<QuestionInfo> questions) {
		this.questions = questions;
	}

	public ArrayList<ExamPojo> getExams() {
		return exams;
	}

	public void setExams(ArrayList<ExamPojo> exams) {
		this.exams = exams;
	}

	public ExamPojo getExam() {
		return exam;
	}

	public void setExam(ExamPojo exam) {
		this.exam = exam;
	}
	
	
	

}
