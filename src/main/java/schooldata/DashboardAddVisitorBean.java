package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="dashboardAddVisitor")
@ViewScoped
public class DashboardAddVisitorBean implements Serializable{

	String totalVisitor,lastDays,lastMonth,todays;



	DatabaseMethods1 DBM = new DatabaseMethods1();


	public DashboardAddVisitorBean()
	{
		Connection conn  =  DataBaseConnection.javaConnection();
		totalVisitor = DBM.selectAllVisitor(conn);
		lastDays  =DBM.selectLast7DaysVisitors(conn);
		lastMonth = DBM.selectLastMonthAllVisitors(conn);
		todays = DBM.selectTodaysAllVisitors(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getTotalVisitor() {
		return totalVisitor;
	}


	public void setTotalVisitor(String totalVisitor) {
		this.totalVisitor = totalVisitor;
	}


	public String getLastDays() {
		return lastDays;
	}


	public void setLastDays(String lastDays) {
		this.lastDays = lastDays;
	}


	public String getLastMonth() {
		return lastMonth;
	}


	public void setLastMonth(String lastMonth) {
		this.lastMonth = lastMonth;
	}


	public String getTodays() {
		return todays;
	}


	public void setTodays(String todays) {
		this.todays = todays;
	}


	public DatabaseMethods1 getDBM() {
		return DBM;
	}


	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
	}



}