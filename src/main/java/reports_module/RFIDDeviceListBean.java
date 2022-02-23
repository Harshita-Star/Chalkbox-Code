package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.Route;

@ManagedBean(name="rfidDeviceListExc")
@ViewScoped

public class RFIDDeviceListBean implements Serializable
{
	ArrayList<SelectItem>schoolList;
	String schid,dvcType,schnm,session;
	ArrayList<Route> list = new ArrayList<>();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports obj =new DataBaseMethodsReports();
	
	public RFIDDeviceListBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid=dbm.schoolId();
		session=dbm.selectedSessionDetails(schid, conn);
		schoolList=dbm.getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deviceList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list = obj.rfidDeviceListExclusive(schnm,"all",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getSchnm() {
		return schnm;
	}

	public void setSchnm(String schnm) {
		this.schnm = schnm;
	}

	public ArrayList<Route> getList() {
		return list;
	}

	public void setList(ArrayList<Route> list) {
		this.list = list;
	}
}
