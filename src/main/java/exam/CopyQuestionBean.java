package exam;

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
import schooldata.DatabaseMethods1;

@ManagedBean(name = "copyQue")
@ViewScoped
public class CopyQuestionBean implements Serializable {

	String selectedClass, selectedSubject, examNamefoeExam, copyselectedClass, copyselectedSubject, copyexamName, schid,
			session,username, userType;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> examList;
	ArrayList<SelectItem> subjectList;
	ArrayList<SelectItem> copyclassList;
	ArrayList<SelectItem> copyexamList;
	ArrayList<SelectItem> copysubjectList;
	ArrayList<QuestionInfo> qlist = new ArrayList<>();
	DatabaseMethods1 obj = new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	boolean validata;

	public CopyQuestionBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid = obj.schoolId();
		session = obj.selectedSessionDetails(schid, conn);
		
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

	public void getSubjects() {
		Connection conn = DataBaseConnection.javaConnection();
		subjectList = obj.allSubjectClassWise(selectedClass, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getExams() {
		Connection conn = DataBaseConnection.javaConnection();
		examList = dbExam.allExamsForClass(selectedClass, session, schid, "Active", conn);
		copyclassList = obj.allClass(conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getcopySubjects() {
		Connection conn = DataBaseConnection.javaConnection();
		copysubjectList = obj.allSubjectClassWise(copyselectedClass, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getcopyExams() {
		Connection conn = DataBaseConnection.javaConnection();
		copyexamList = dbExam.allExamsForClass(copyselectedClass, session, schid, "Active", conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String check() {
		Connection conn = DataBaseConnection.javaConnection();
		qlist = dbExam.getAllQuestionForCopy(selectedClass, selectedSubject, examNamefoeExam,schid,conn);
		if (qlist.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Questions in this exam"));
		} else {
			for (QuestionInfo a : qlist) {
				a.setClassid(copyselectedClass);
				a.setSubjectid(copyselectedSubject);
				a.setExamid(copyexamName);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam Selected for copy questions."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void copy() {
		Connection conn = DataBaseConnection.javaConnection();
		if(validata == true) {
			if (qlist.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("First Select Exam.."));
			} else {
				int status = dbExam.addQuestionfromExcel(copyselectedClass, copyselectedSubject, copyexamName, qlist, schid,
						conn);
				if (status > 0) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questions copied"));
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Ocuurs", "Error Occurs"));
				}
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("First change the exam language same as above exam in edit exam."));
		}
	}
	
	public void compare() {
		
		 // System.out.println("meythods");
		Connection conn = DataBaseConnection.javaConnection();
		String examOneLanguage = dbExam.getExamLaguage(examNamefoeExam, conn);
		String examTwoLaguage = dbExam.getExamLaguage(copyexamName, conn);
		 // System.out.println(examOneLanguage);
		 // System.out.println(examTwoLaguage);
		if(examOneLanguage.equalsIgnoreCase(examTwoLaguage)) {
			 // System.out.println("aaaaaaaaaaaa");
			validata = true;
		}else {
			 // System.out.println("sssssss");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam langauge is not same"));
			validata = false;
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public String getExamNamefoeExam() {
		return examNamefoeExam;
	}

	public void setExamNamefoeExam(String examNamefoeExam) {
		this.examNamefoeExam = examNamefoeExam;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public String getCopyselectedClass() {
		return copyselectedClass;
	}

	public void setCopyselectedClass(String copyselectedClass) {
		this.copyselectedClass = copyselectedClass;
	}

	public String getCopyselectedSubject() {
		return copyselectedSubject;
	}

	public void setCopyselectedSubject(String copyselectedSubject) {
		this.copyselectedSubject = copyselectedSubject;
	}

	public String getCopyexamName() {
		return copyexamName;
	}

	public void setCopyexamName(String copyexamName) {
		this.copyexamName = copyexamName;
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

	public ArrayList<SelectItem> getCopyclassList() {
		return copyclassList;
	}

	public void setCopyclassList(ArrayList<SelectItem> copyclassList) {
		this.copyclassList = copyclassList;
	}

	public ArrayList<SelectItem> getCopyexamList() {
		return copyexamList;
	}

	public void setCopyexamList(ArrayList<SelectItem> copyexamList) {
		this.copyexamList = copyexamList;
	}

	public ArrayList<SelectItem> getCopysubjectList() {
		return copysubjectList;
	}

	public void setCopysubjectList(ArrayList<SelectItem> copysubjectList) {
		this.copysubjectList = copysubjectList;
	}

}
