package schooldata;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
@ManagedBean(name="PrintRbscAttendanceCertificate")
@ViewScoped
public class PrintRbscAttendanceCertificate implements Serializable
{
	String name,designation,description;
	String fromDate,toDate;
	long totalDays;
	public PrintRbscAttendanceCertificate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		name=(String) ss.getAttribute("name");
		designation=(String) ss.getAttribute("designation");
		description=(String) ss.getAttribute("description");
		fromDate=sdf.format(ss.getAttribute("fromDate"));
		toDate=sdf.format(ss.getAttribute("toDate"));
		totalDays=	getDifferenceDays((Date)ss.getAttribute("fromDate"),(Date)ss.getAttribute("toDate"));
	}
	public static long getDifferenceDays(Date d1, Date d2)
	{
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
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
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public long getTotalDays() {
		return totalDays;
	}
	public void setTotalDays(long totalDays) {
		this.totalDays = totalDays;
	}

}
