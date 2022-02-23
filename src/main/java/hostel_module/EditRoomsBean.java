package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean (name="editRooms")
@ViewScoped

public class EditRoomsBean implements Serializable {

	String regex=RegexPattern.REGEX;
	ArrayList<RoomInfo> roomList=new ArrayList<>();
	RoomInfo selectedRooms;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public EditRoomsBean() {
		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		roomList=obj.viewAllRooms(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String updateRoomsDetails() 
	{
		Connection con=DataBaseConnection.javaConnection();
		
		try 
		{
			if(selectedRooms.getHostelId().equals("") || selectedRooms.getHostelId().isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Hostel name cann't be blank"));
			}
			else
			{
				boolean checkAllocation = obj.checkRoomAllocation(selectedRooms.getId(),con);
				if(checkAllocation)
				{
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("This Room is Allocated. Please Deallocate it to Edit"));
				}
				else
				{
					boolean duplicateRooms=obj.checkForDuplicateRoomsNo(con,selectedRooms.getRoomNo(),selectedRooms.getHostelId(), selectedRooms.getNoOfBed());

					if(duplicateRooms==true)
					{
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Duplicate Room No. found"));
					}
					else 
					{
						int res=obj.updateRoomsDetails(selectedRooms,con);

						if(res>=1) {
							
							addWorkLog3(con);
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Rooms Details Updated"));
							roomList=obj.viewAllRooms(con);
						}
						else {
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Some Error Occured"));
						}
					}
				}

			}
			
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		finally {

			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return "editRooms.xhtml";

	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "Room no-"+selectedRooms.getRoomNo()+" --NoOfBed-"+selectedRooms.getNoOfBed()+" --HostelId-"+selectedRooms.getHostelId();

		value = language;
		
		String refNo =workLg.saveWorkLogMehod(language,"Edit Room","WEB",value,conn);
		return refNo;
	}

	public String deleteRooms() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			boolean checkAllocation = obj.checkRoomAllocation(selectedRooms.getId(),conn);
			if(checkAllocation)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("This Room is Allocated. Please Deallocate it to Delete"));
			}
			else
			{
				int k=obj.deleteRooms(conn,selectedRooms.getId());

				if(k>0)
				{
					addWorkLog(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted sucessfully"));
					roomList=obj.viewAllRooms(conn);
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error"));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally 
		{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "editRooms.xhtml";
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		language = value = "Room Id-"+selectedRooms.getId();
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Room","WEB",value,conn);
		return refNo;
	}

	public void damageRooms() {

		Connection conn=DataBaseConnection.javaConnection();
		boolean checkAllocation = obj.checkRoomAllocation(selectedRooms.getId(),conn);
		if(checkAllocation)
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("This Room is Allocated. Please Deallocate it to Mark as Damage"));
		}
		else
		{
			int k=obj.markRoomAsDamage(conn,selectedRooms.getId());

			if(k>0)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Damage Marked sucessfully"));

				roomList=obj.viewAllRooms(conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error"));
			}
		}



		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";

		language = value = "Room Id-"+selectedRooms.getId();
		
		String refNo = workLg.saveWorkLogMehod(language,"Damage Room","WEB",value,conn);
		return refNo;
	}
	
	public ArrayList<RoomInfo> getRoomList() {
		return roomList;
	}

	public void setRoomList(ArrayList<RoomInfo> roomList) {
		this.roomList = roomList;
	}

	public RoomInfo getSelectedRooms() {
		return selectedRooms;
	}

	public void setSelectedRooms(RoomInfo selectedRooms) {
		this.selectedRooms = selectedRooms;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
