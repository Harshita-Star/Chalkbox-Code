package account_module;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.GenerateSalaryInfo;

@ManagedBean(name="printSalaryPayoutBLM")
@ViewScoped

public class PrintSalaryPayoutBLMBean implements Serializable
{
	ArrayList<GenerateSalaryInfo> list = new ArrayList<>();
	String month,year,strdate;

	public PrintSalaryPayoutBLMBean()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		list = (ArrayList<GenerateSalaryInfo>) ss.getAttribute("salaryList");
		month = (String) ss.getAttribute("salaryMonth");
		year = (String) ss.getAttribute("salaryYear");
		strdate = (String) ss.getAttribute("paidDate");
	}

	public ArrayList<GenerateSalaryInfo> getList() {
		return list;
	}

	public void setList(ArrayList<GenerateSalaryInfo> list) {
		this.list = list;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getStrdate() {
		return strdate;
	}

	public void setStrdate(String strdate) {
		this.strdate = strdate;
	}
}
