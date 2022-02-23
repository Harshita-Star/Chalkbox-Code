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

@ManagedBean(name = "hostelRoomDetails")
@ViewScoped
public class HostelRoomDetailsBean implements Serializable {

	ArrayList<RoomInfo> roomList;
	ArrayList<SelectItem> bedsList;
	ArrayList<String> occupiedBeds;
	DataBaseMethodsHostelModule obj = new DataBaseMethodsHostelModule();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public HostelRoomDetailsBean() {
		Connection con = DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		roomList = obj.viewAllRoomsReport(con, "all", "");

		for(RoomInfo tt: roomList)
		{
			bedsList=new ArrayList<>();
			occupiedBeds=new ArrayList<>();

			for(int i=1;i<=Integer.valueOf(tt.getNoOfBed());i++)
			{
				SelectItem nn=new SelectItem();

				nn.setLabel("");
				nn.setValue(i);
				bedsList.add(nn);
				for(int j=0;j<=Integer.valueOf(tt.getOccpiedBeds());j++) {
					occupiedBeds.add(String.valueOf(j));
				}
				tt.setOccupiedBeds(occupiedBeds);
				tt.setOccupiedBedList(bedsList);
			}

		}

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

	public ArrayList<SelectItem> getBedsList() {
		return bedsList;
	}

	public void setBedsList(ArrayList<SelectItem> bedsList) {
		this.bedsList = bedsList;
	}

	public ArrayList<String> getOccupiedBeds() {
		return occupiedBeds;
	}

	public void setOccupiedBeds(ArrayList<String> occupiedBeds) {
		this.occupiedBeds = occupiedBeds;
	}
}
