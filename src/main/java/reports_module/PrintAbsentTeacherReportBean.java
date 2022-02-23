package reports_module;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.EmpInfo;

@ManagedBean(name="printAbsentTeacherReport")
@ViewScoped
public class PrintAbsentTeacherReportBean implements Serializable{
	
	ArrayList<EmpInfo> absentList;
	String total,strDate;
	
	
	public PrintAbsentTeacherReportBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		absentList = (ArrayList<EmpInfo>) ss.getAttribute("absentList");
		 // System.out.println(absentList.size());
		total = (String) ss.getAttribute("total");
		strDate = (String) ss.getAttribute("strDate");
		
	}


	public ArrayList<EmpInfo> getAbsentList() {
		return absentList;
	}


	public void setAbsentList(ArrayList<EmpInfo> absentList) {
		this.absentList = absentList;
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
