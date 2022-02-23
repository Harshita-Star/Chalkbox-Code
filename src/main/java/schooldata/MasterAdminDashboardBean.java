package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="masterAdminDashboard")
@ViewScoped
public class MasterAdminDashboardBean implements Serializable{
	String totalMsgtoday,totalSchool,smsRecharge,unpaidBill,login;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	public MasterAdminDashboardBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		totalMsgtoday = DBM.todayMessagefromSaveMsg(conn);
		totalSchool = DBM.totalSchool(conn);
		smsRecharge = DBM.smsRechageThisMonth(conn);
		unpaidBill = DBM.AllUnpaidBills(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public String getTotalMsgtoday() {
		return totalMsgtoday;
	}
	public void setTotalMsgtoday(String totalMsgtoday) {
		this.totalMsgtoday = totalMsgtoday;
	}
	public String getTotalSchool() {
		return totalSchool;
	}
	public void setTotalSchool(String totalSchool) {
		this.totalSchool = totalSchool;
	}

	public String getSmsRecharge() {
		return smsRecharge;
	}
	public void setSmsRecharge(String smsRecharge) {
		this.smsRecharge = smsRecharge;
	}
	public String getUnpaidBill() {
		return unpaidBill;
	}
	public void setUnpaidBill(String unpaidBill) {
		this.unpaidBill = unpaidBill;
	}
	public DatabaseMethods1 getDBM() {
		return DBM;
	}
	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
	}

}

