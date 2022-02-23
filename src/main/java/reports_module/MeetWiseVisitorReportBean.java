package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="meetWiseVisitorReport")
@ViewScoped
public class MeetWiseVisitorReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	int totalVisitor;
	String tempToMeet,toMeet,meetType;
	ArrayList<reports_module.VisitorInfo> visitorList;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,session;

	public MeetWiseVisitorReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteToMeet(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();

		ArrayList<StudentInfo> visitorList = dbm.autoCompleteToMeet(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : visitorList)
		{
			studentListt.add(info.getFname()+"-"+info.getAddNumber()+"/"+info.getAadharNo());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}


	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String meet="";
		if(tempToMeet.equalsIgnoreCase("other"))
		{
			meetType=toMeet.substring(toMeet.indexOf("/")+1);
			meet=toMeet.substring(toMeet.indexOf("-")+1,toMeet.indexOf("/"));
		}
		else
		{
			meet=tempToMeet;
			meetType=tempToMeet;
		}
		visitorList=dbr.meetWiseVisitorReport(meet,meetType,conn);
		totalVisitor=visitorList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public String getTempToMeet() {
		return tempToMeet;
	}
	public void setTempToMeet(String tempToMeet) {
		this.tempToMeet = tempToMeet;
	}
	public String getToMeet() {
		return toMeet;
	}
	public void setToMeet(String toMeet) {
		this.toMeet = toMeet;
	}
}
