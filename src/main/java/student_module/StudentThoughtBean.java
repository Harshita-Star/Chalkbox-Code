package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="studentThought")
@ViewScoped

public class StudentThoughtBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String studentid,thought;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId;

	public StudentThoughtBean()
	{
       schoolId = obj1.schoolId(); 
	}

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentid=(String) ss.getAttribute("username");
		StudentInfo stuinfo = obj1.studentDetailslistByAddNo(schoolId,studentid, conn);

		String classid=stuinfo.getClassId();
		String sectionid=stuinfo.getSectionid();
		schoolId=obj1.schoolId();
		String session = obj1.selectedSessionDetails(schoolId,conn);

		Date currDt = new Date();
		String strDt = new SimpleDateFormat("yyyy-MM-dd").format(currDt);

		int i  = DBJ.addStudentThougt(studentid,thought,strDt,session,schoolId,classid,sectionid,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Thought Added Successfully!"));
			thought="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong.Please try again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getStudentid() {
		return studentid;
	}

	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}

	public String getThought() {
		return thought;
	}

	public void setThought(String thought) {
		this.thought = thought;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
