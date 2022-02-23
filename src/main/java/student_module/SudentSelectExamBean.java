package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import exam_module.ExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;


@ManagedBean(name="studentSelectExam")
@ViewScoped
public class SudentSelectExamBean  implements Serializable
{
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	ArrayList<ExamInfo> list = new ArrayList<>();
	ArrayList<ExamInfo> subExamList = new ArrayList<>();
	ExamInfo selected;
	String classId,semesterid;
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schid;
	public SudentSelectExamBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		schid = obj1.schoolId();
		StudentInfo info=DBJ.studentDetailslistByAddNo(studentid,schid,conn);
		classId=info.getClassId();
		list=DBJ.examNameFromclassid(info.getClassId(),schid,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void view()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String termid=selected.getSemesterid();
		String temp[] = termid.split("-");
		String term = temp[0];
		String examid = temp[1];
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		subExamList = DBJ.subExamListByTermAndExam(term,examid,classId,schid,conn);

		DBJ.studentExamPerformance(studentid, subExamList, schid, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}
	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}
	public ArrayList<ExamInfo> getList() {
		return list;
	}
	public void setList(ArrayList<ExamInfo> list) {
		this.list = list;
	}
	public ArrayList<ExamInfo> getSubExamList() {
		return subExamList;
	}
	public void setSubExamList(ArrayList<ExamInfo> subExamList) {
		this.subExamList = subExamList;
	}
	public ExamInfo getSelected() {
		return selected;
	}
	public void setSelected(ExamInfo selected) {
		this.selected = selected;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getSemesterid() {
		return semesterid;
	}
	public void setSemesterid(String semesterid) {
		this.semesterid = semesterid;
	}

}