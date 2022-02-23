package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;

@ManagedBean(name="roomAllocationReport")
@ViewScoped
public class RoomAllocationReportBean implements Serializable{

	ArrayList<RoomInfo> roomList ;
	DataBaseMethodsHostelModule obj= new DataBaseMethodsHostelModule();

	public RoomAllocationReportBean() {
		Connection con = DataBaseConnection.javaConnection();
		roomList=obj.allAllocatedRooms(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<RoomInfo> getRoomList() {
		return roomList;
	}

	public void setRoomList(ArrayList<RoomInfo> roomList) {
		this.roomList = roomList;
	}

}
