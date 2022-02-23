package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean (name="assetAllotment")
@ViewScoped
public class AssetAllotmentBean implements Serializable{

	private ArrayList<SelectItem> assetList,roomList,hostelNameList;
	private String quantity,assetName,toRoomNo,availableStock,hostelName;
	private Date entryDate;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public AssetAllotmentBean() {

		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		entryDate=new Date();
		assetList=obj.viewAllAssets(con);
		
		hostelNameList=obj.viewAllHostelNames(con);

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void roomsDetails() {
		Connection con=DataBaseConnection.javaConnection();
		
		roomList=obj.viewAllRoomsNo(con,hostelName);
		
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewCurrentStock() {
		Connection con=DataBaseConnection.javaConnection();
		availableStock=obj.viewCurrentAssetQuantity(con, assetName);

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String allotAsset() {

		Connection con=DataBaseConnection.javaConnection();
		try
		{
			if(Integer.valueOf(quantity)>Integer.valueOf(availableStock))
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Quantity cannot Be Greater than "+availableStock+""));
			}
			else {


				int i=obj.allocateAssetToRoom(con,toRoomNo,assetName,quantity);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Assets Sucessfully Allocated "));
					obj.updateFinalAssetValue(con,toRoomNo,quantity,assetName);
					return "assetAllotment.xhtml";

				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Some Error Occured"));
				}

			}
			return "";
		}
		finally
		{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public ArrayList<SelectItem> getAssetList() {
		return assetList;
	}

	public void setAssetList(ArrayList<SelectItem> assetList) {
		this.assetList = assetList;
	}

	public ArrayList<SelectItem> getRoomList() {
		return roomList;
	}

	public void setRoomList(ArrayList<SelectItem> roomList) {
		this.roomList = roomList;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getToRoomNo() {
		return toRoomNo;
	}

	public void setToRoomNo(String toRoomNo) {
		this.toRoomNo = toRoomNo;
	}

	public String getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(String availableStock) {
		this.availableStock = availableStock;
	}
	public ArrayList<SelectItem> getHostelNameList() {
		return hostelNameList;
	}
	public void setHostelNameList(ArrayList<SelectItem> hostelNameList) {
		this.hostelNameList = hostelNameList;
	}
	public String getHostelName() {
		return hostelName;
	}
	public void setHostelName(String hostelName) {
		this.hostelName = hostelName;
	}

}
