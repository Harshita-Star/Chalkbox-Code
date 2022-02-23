package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name = "viewHostelWiseRoomReport")
@ViewScoped
public class VIewHostelWiseRoomReportBean implements Serializable {

	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> hostelNameList;
	ArrayList<RoomInfo> roomList ;
	String hostelName, roomNo;
	DataBaseMethodsHostelModule obj= new DataBaseMethodsHostelModule();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	boolean showTable;
	public VIewHostelWiseRoomReportBean() {

		Connection con = DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		hostelNameList = obj.viewAllHostelNames(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allRooms() {

		showTable=true;
		Connection con = DataBaseConnection.javaConnection();
		roomList = obj.viewAllRoomsReport(con,hostelName,roomNo);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getHostelNameList() {
		return hostelNameList;
	}

	public void setHostelNameList(ArrayList<SelectItem> hostelNameList) {
		this.hostelNameList = hostelNameList;
	}

	public ArrayList<RoomInfo> getRoomList() {
		return roomList;
	}

	public void setRoomList(ArrayList<RoomInfo> roomList) {
		this.roomList = roomList;
	}

	public String getHostelName() {
		return hostelName;
	}

	public void setHostelName(String hostelName) {
		this.hostelName = hostelName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}

