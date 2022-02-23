package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


@ManagedBean(name="studentTCReportPrint")
@ViewScoped

public class StudentTCReportPrintBean implements Serializable
{
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	String className, sectionName;

	public StudentTCReportPrintBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList = (ArrayList<StudentInfo>) ss.getAttribute("studentTCReportList");
		String classid = (String) ss.getAttribute("classId");
		String sectionid = (String) ss.getAttribute("sectionId");

		String session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		if(classid.equals("-1"))
		{
			className="All";
		}
		else
		{
			className = new DatabaseMethods1().classNameFromidSchid(new DatabaseMethods1().schoolId(),classid, session, conn);
		}

		if(sectionid.equals("-1"))
		{
			sectionName="All";
		}
		else
		{
			sectionName = new DatabaseMethods1().sectionNameByIdSchid(new DatabaseMethods1().schoolId(),sectionid, conn);
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


}
