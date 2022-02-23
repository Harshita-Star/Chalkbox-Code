package schooldata;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printAbTeacherReport")
@ViewScoped

public class PrintAbsentTeacherReportBean implements Serializable
{
	ArrayList<StudentInfo> studentList;
	String total,strDate;

	public PrintAbsentTeacherReportBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList = (ArrayList<StudentInfo>) ss.getAttribute("absentList");
		total = (String) ss.getAttribute("total");
		strDate = (String) ss.getAttribute("strDate");
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}


}
