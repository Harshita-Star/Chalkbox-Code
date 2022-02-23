package schooldata;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printAllStudentList")
@ViewScoped
public class PrintStudentReportListBean implements Serializable{
	
	ArrayList<StudentInfo> list = new ArrayList<StudentInfo>();
	String total;
	
	
	public PrintStudentReportListBean()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		list = (ArrayList<StudentInfo>) ss.getAttribute("studentPrintList");
		 total = (String) ss.getAttribute("totalStudents");
	}


	public ArrayList<StudentInfo> getList() {
		return list;
	}


	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}


	public String getTotal() {
		return total;
	}


	public void setTotal(String total) {
		this.total = total;
	}
	
	
	

}
