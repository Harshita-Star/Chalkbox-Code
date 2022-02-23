package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="viewScrapReport")
@ViewScoped

public class ViewScrapReportBean implements Serializable{


	private ArrayList<RoomInfo> roomDetails;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();
	private ArrayList<ColumnModel> tableHeaderNames;
	private ArrayList<SelectItem> assetList;
	HashMap<String, String> assetDetails=new HashMap<>();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public ViewScrapReportBean() {
		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		roomDetails=obj.viewAllAllocatedAssetToScrap(con);
		assetList=obj.viewAllAssets(con);
		tableHeaderNames=new ArrayList<>();
		for(SelectItem tt: assetList) {
			tableHeaderNames.add(new ColumnModel(tt.getValue().toString(), tt.getLabel()));
		}

		for(RoomInfo tt: roomDetails) {
			assetDetails=new HashMap<>();
			for(SelectItem tt1: assetList) {

				String a=obj.viewTotalAssetQuantityInScrap(con,tt1.getValue().toString(),tt.getRoomId());
				if(a==null) {
					assetDetails.put(tt1.getValue().toString(), "0");
				}
				else {
					assetDetails.put(tt1.getValue().toString(), a);
				}


				tt.setAssetDetails(assetDetails);
			}
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	static public class ColumnModel implements Serializable {

		private String key;
		private String value;

		public ColumnModel(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}
	public ArrayList<RoomInfo> getRoomDetails() {
		return roomDetails;
	}
	public void setRoomDetails(ArrayList<RoomInfo> roomDetails) {
		this.roomDetails = roomDetails;
	}
	public ArrayList<ColumnModel> getTableHeaderNames() {
		return tableHeaderNames;
	}
	public void setTableHeaderNames(ArrayList<ColumnModel> tableHeaderNames) {
		this.tableHeaderNames = tableHeaderNames;
	}
	public ArrayList<SelectItem> getAssetList() {
		return assetList;
	}
	public void setAssetList(ArrayList<SelectItem> assetList) {
		this.assetList = assetList;
	}
	public HashMap<String, String> getAssetDetails() {
		return assetDetails;
	}
	public void setAssetDetails(HashMap<String, String> assetDetails) {
		this.assetDetails = assetDetails;
	}

}