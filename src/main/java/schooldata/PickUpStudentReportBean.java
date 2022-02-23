package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="pickUpReport")
@ViewScoped
public class PickUpStudentReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String schId;
	Date startDate,endDate;
	ArrayList<StudentInfo> studentList;
	int totalVisitor;


	public PickUpStudentReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schId=new DatabaseMethods1().schoolId();
		startDate = new Date();
		endDate = new Date();
		allVisitorList();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allVisitorList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().pickUpReport(schId, startDate, endDate, conn);
		totalVisitor=studentList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSchId() {
		return schId;
	}

	public void setSchId(String schId) {
		this.schId = schId;
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

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public int getTotalVisitor() {
		return totalVisitor;
	}

	public void setTotalVisitor(int totalVisitor) {
		this.totalVisitor = totalVisitor;
	}
}
