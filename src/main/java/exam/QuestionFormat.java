package exam;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="questionFormat")
@ViewScoped
public class QuestionFormat implements Serializable{

	String classid,subjectid,examid,subjectName,examLang,schid;
	boolean disable;
	ArrayList<QuestionInfo> questionList;
	DatabaseMethodExam dbexm = new DatabaseMethodExam();
	DatabaseMethods1 dbm  = new DatabaseMethods1();
	
	public QuestionFormat() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		classid = (String) ss.getAttribute("classid");
		subjectid = (String) ss.getAttribute("subjectid");
		examid = (String) ss.getAttribute("examid");
		examLang = (String) ss.getAttribute("language");
		String ren = (String) ss.getAttribute("render");
		if(ren.equalsIgnoreCase("both")) {
			disable = false;
		}else {
			disable = true;
		}
		questionList = dbexm.getAllQuestionForCopy(classid, subjectid, examid,examLang,schid,conn);
		subjectName = dbm.subjectNameFromid(subjectid, conn);
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getQuestions() {
		Connection conn = DataBaseConnection.javaConnection();
		questionList = dbexm.getAllQuestionForCopy(classid, subjectid, examid,examLang,schid,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public String getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}
	public String getExamid() {
		return examid;
	}
	public void setExamid(String examid) {
		this.examid = examid;
	}
	public ArrayList<QuestionInfo> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(ArrayList<QuestionInfo> questionList) {
		this.questionList = questionList;
	}


	public String getSubjectName() {
		return subjectName;
	}


	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}


	public String getExamLang() {
		return examLang;
	}


	public void setExamLang(String examLang) {
		this.examLang = examLang;
	}


	public boolean isDisable() {
		return disable;
	}


	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
}
