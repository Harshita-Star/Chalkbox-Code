package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;



@ManagedBean(name="dateWiseVisitorReport")
@ViewScoped
public class DateWiseVisitorReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate,endDate;
	String date;
	int totalVisitor;
	ArrayList<reports_module.VisitorInfo> visitorList;
	DatabaseMethods1 dbm = new DatabaseMethods1(); 
	String schoolId,sessionValue;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();

	public DateWiseVisitorReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		startDate=endDate=new Date() ;
		searchData();
		
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		date=new SimpleDateFormat("dd/MM/yyyy").format(startDate)+" - "+sdf.format(endDate);
		visitorList=dbr.dateWiseVisitorReport(schoolId, startDate,endDate,conn);
		totalVisitor=visitorList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public ArrayList<reports_module.VisitorInfo> getVisitorList() {
		return visitorList;
	}
	public void setVisitorList(ArrayList<reports_module.VisitorInfo> visitorList) {
		this.visitorList = visitorList;
	}
	public int getTotalVisitor() {
		return totalVisitor;
	}
	public void setTotalVisitor(int totalVisitor) {
		this.totalVisitor = totalVisitor;
	}
}
