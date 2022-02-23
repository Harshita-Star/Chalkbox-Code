package schooldata;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="rbscAttendanceCertificate")
@ViewScoped
public class RbscAttendanceCertificate implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name,designation,description;
	Date fromDate,toDate;

	public RbscAttendanceCertificate()
	{
		fromDate=new Date();
		toDate=new Date();

	}
	public String submit()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("name", name);
		ss.setAttribute("designation", designation);
		ss.setAttribute("description", description);
		ss.setAttribute("fromDate", fromDate);
		ss.setAttribute("toDate", toDate);

		PrimeFaces.current().executeInitScript("window.open('printRbscAttendenceCertificate.xhtml')");
		return "rbscAttendenceCertificate.xhtml";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
