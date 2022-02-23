package schooldata;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printPresentReport")
@ViewScoped
public class PrintPresentReportBean implements Serializable
{
	ArrayList<StudentInfo> studentList;
	String className,section;
	String total,strDate;

	public PrintPresentReportBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList = (ArrayList<StudentInfo>) ss.getAttribute("presentList");
		total = (String) ss.getAttribute("total");
		className = (String) ss.getAttribute("class");
		section = (String) ss.getAttribute("section");
		strDate = (String) ss.getAttribute("strDate");
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

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
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
