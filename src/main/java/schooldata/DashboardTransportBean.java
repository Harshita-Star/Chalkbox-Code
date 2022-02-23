package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="dashboardTransport")
@ViewScoped
public class DashboardTransportBean implements Serializable{

	String count,transport,totalRoute,totalStopage,schid,session;
	ArrayList<StudentInfo>allStudent = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public ArrayList<StudentInfo> getAllStudent() {
		return allStudent;
	}
	public void setAllStudent(ArrayList<StudentInfo> allStudent) {
		this.allStudent = allStudent;
	}
	public DashboardTransportBean()
	{
		Connection conn  =  DataBaseConnection.javaConnection();
		
		schid=DBM.schoolId();
		session=DBM.selectedSessionDetails(schid, conn);
		
		count = DBM.allStudentcount(DBM.schoolId(),"","",session,"",conn);
		transport = DBM.allStudentcount(DBM.schoolId(), "transport","",session,"",conn);
		totalRoute = DBM.totalRouteFromtransportTable(conn);
		totalStopage = DBM.ToatalStoppageFromtransportstop(conn);
		allStudent = DBM.alltranportStudentList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getTotalRoute() {
		return totalRoute;
	}

	public void setTotalRoute(String totalRoute) {
		this.totalRoute = totalRoute;
	}

	public String getTotalStopage() {
		return totalStopage;
	}

	public void setTotalStopage(String totalStopage) {
		this.totalStopage = totalStopage;
	}
	public DatabaseMethods1 getDBM() {
		return DBM;
	}
	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
	}



}