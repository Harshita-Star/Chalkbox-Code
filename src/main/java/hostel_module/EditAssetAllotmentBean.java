package hostel_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean (name="editAssetAllotment")
@ViewScoped

public class EditAssetAllotmentBean implements Serializable{

	String regex=RegexPattern.REGEX;
	private ArrayList<RoomInfo> roomDetails;
	private String assetName,quantity,actionPerfrom,a;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();
	private ArrayList<ColumnModel> tableHeaderNames;
	private ArrayList<SelectItem> assetList;
	HashMap<String, String> assetDetails=new HashMap<>();
	private RoomInfo selectedRoom;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public EditAssetAllotmentBean() {
		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		roomDetails=obj.viewAllAllocatedAsset(con);
		assetList=obj.viewAllAssets(con);
		tableHeaderNames=new ArrayList<>();
		for(SelectItem tt: assetList) {
			tableHeaderNames.add(new ColumnModel(tt.getValue().toString(), tt.getLabel()));
		}

		for(RoomInfo tt: roomDetails) {
			assetDetails=new HashMap<>();
			for(SelectItem tt1: assetList) {

				String a=obj.viewTotalAssetQuantity(con,tt1.getValue().toString(),tt.getRoomId());
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

	public String updateAssetDetails() throws IOException {

		Connection con=DataBaseConnection.javaConnection();
		try
		{
			if(Integer.valueOf(quantity)>Integer.valueOf(a))
			{

				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Quantity cannot Be Greater than "+a+""));
			}
			else {


				if(actionPerfrom.equals("scrap"))
				{
					int i=obj.insertInScrap(con,selectedRoom.roomId,assetName,quantity);
					if(i>=1)
					{
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Assets Edited "));

						obj.allocateAssetToRoom(con,selectedRoom.roomId,assetName,"-"+quantity);

						new EditAssetAllotmentBean();
						FacesContext.getCurrentInstance().getExternalContext().redirect("editAssetTransfer.xhtml");

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Some Error Occured"));
					}

				}

				else {
					int i1=obj.allocateAssetToRoom(con,selectedRoom.roomId,assetName,"-"+quantity);
					if(i1>=1)
					{
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Assets Edited "));

						obj.updateFinalAssetValue(con,selectedRoom.roomId,"-"+quantity,assetName);
						new EditAssetAllotmentBean();

						FacesContext.getCurrentInstance().getExternalContext().redirect("editAssetTransfer.xhtml");

					}

				}
			}
			return "";
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	public void remove() {
		actionPerfrom="remove";
	}
	public void scrap() {
		actionPerfrom="scrap";
	}

	public void viewCurrentStock() {


		Connection con=DataBaseConnection.javaConnection();

		quantity=obj.viewTotalAssetQuantity(con,assetName,selectedRoom.getRoomId());
		a=quantity;
		if(quantity==null) {
			quantity="0";
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

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public RoomInfo getSelectedRoom() {
		return selectedRoom;
	}

	public void setSelectedRoom(RoomInfo selectedRoom) {
		this.selectedRoom = selectedRoom;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}

