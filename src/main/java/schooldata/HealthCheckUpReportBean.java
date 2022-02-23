package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="healthCheckUpReport")
@ViewScoped
public class HealthCheckUpReportBean implements Serializable{

	String regex=RegexPattern.REGEX;
	String healthCheckUpType,selectedStudent;
	ArrayList<HealthCheckUpInfo> healthList = new ArrayList<>();
	DataBaseMethodsHostelModule obj = new DataBaseMethodsHostelModule();
	boolean showTable;
	Date startDate,endDate;

	public HealthCheckUpReportBean() {
		healthCheckUpType="all";
		startDate=new Date();
		startDate.setMonth(startDate.getMonth()-1);
		endDate=new Date();
	}

	public void search() {
		String strStart = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		String strEnd = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		showTable=true;
		Connection con = DataBaseConnection.javaConnection();
		healthList = obj.viewAllHealthCheckUpDetailsForReport(con,healthCheckUpType,strStart,strEnd);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//ArrayList<StudentInfo> studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<StudentInfo> studentList=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName",query, "basic", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudent()
	{
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf("-")+1);
		showTable=true;
		Connection con = DataBaseConnection.javaConnection();
		healthList = obj.viewAllHealthCheckUpDetailsStudentWise(con,studentId,new DatabaseMethods1().schoolId());
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getHealthCheckUpType() {
		return healthCheckUpType;
	}

	public void setHealthCheckUpType(String healthCheckUpType) {
		this.healthCheckUpType = healthCheckUpType;
	}

	public ArrayList<HealthCheckUpInfo> getHealthList() {
		return healthList;
	}

	public void setHealthList(ArrayList<HealthCheckUpInfo> healthList) {
		this.healthList = healthList;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(String selectedStudent) {
		this.selectedStudent = selectedStudent;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
