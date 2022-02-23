package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="allocateRoom")
@ViewScoped
public class AllocateRoomToStudentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String selectedRoom,transferRoomNo,selectedStudent,hostelName;
	Date addDate=new Date(),deallocationDate=new Date(),transferDate=new Date();
	ArrayList<SelectItem> hostelNameList,roomList,newRoomList;
	ArrayList<RoomInfo> allocatedRoomList;
	RoomInfo selectedRow;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();

	public AllocateRoomToStudentBean()
	{
		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		hostelNameList=obj.viewAllHostelNames(con);
		allocatedRoomList=obj.allAllocatedRooms(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void roomsDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		roomList =obj.allRoomList(hostelName,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteRooms(String name)
	{
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<String> tempList=new ArrayList<>();
		tempList=obj.autoCompleteRoomforRoomAllocation(name,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tempList;
	}

	public ArrayList<String> autoCompleteStudent(String query)
	{
		Connection con=DataBaseConnection.javaConnection();
		ArrayList<StudentInfo> studentList=obj1.searchStudentList(query,con);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void allocateRoomNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf("-")+1);
		String ab=obj.forchecking(studentId,conn);
		if(ab.equals("done"))
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Room already allocated to the seleted student"));
			selectedStudent=null;
		}
		else
		{

			RoomInfo ri = obj.roomDetailByRoomId(selectedRoom, conn);

			int i=obj.allocateRoomToStudent(selectedRoom,studentId,addDate,conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Room Sucessfully Allocated"));
				int allocatedBeds = obj.allocatedBedsInRoom(selectedRoom,conn);
				//int totalBeds = allocatedBeds+1;
				if(allocatedBeds>=Integer.valueOf(ri.getNoOfBed()))
				{
					obj.changeRoomStatus(selectedRoom,"occupied",conn);
					String refNo2;
					try {
						refNo2=addWorkLog2(conn);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				allocatedRoomList=obj.allAllocatedRooms(conn);
				//roomList=new ArrayList<>();
				hostelName="";
				selectedRoom=transferRoomNo=selectedStudent="";addDate=deallocationDate=transferDate=new Date();selectedRow=new RoomInfo();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured.."));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
		String adDt = formater.format(addDate);
		
		String studentId=selectedStudent.substring(selectedStudent.lastIndexOf("-")+1);
	    language = "Hostel-"+hostelName+" --AddDate-"+adDt+" --Room-"+selectedRoom+" --Student-"+studentId;
	    
	    value = "AddDate-"+adDt+" --Room-"+selectedRoom+" --Student-"+studentId;
	   
	   	
		String refNo = workLg.saveWorkLogMehod(language,"Allocate Room","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Room-"+selectedRoom+" --Status-Occupied";
	   
	   	
		String refNo = workLg.saveWorkLogMehod(language,"Change Room Status","WEB",value,conn);
		return refNo;
	}


	public void deallocateRoom()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.deallocateRoomFromStudent(selectedRow,deallocationDate,conn);
		if(i>=1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Room Sucessfully De-Allocated"));
			allocatedRoomList=obj.allAllocatedRooms(conn);

			selectedRoom=transferRoomNo=selectedStudent="";addDate=deallocationDate=transferDate=new Date();selectedRow=new RoomInfo();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured.."));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
		String adDt = formater.format(deallocationDate);
		
		value = "Id-"+selectedRow.getId()+" --RoomId-"+selectedRow.getRoomId()+" --Date-"+adDt;
	   
	   	
		String refNo = workLg.saveWorkLogMehod(language,"Deallocate Room","WEB",value,conn);
		return refNo;
	}

	public void editTransfer()
	{
		Connection conn = DataBaseConnection.javaConnection();
		newRoomList = obj.allRoomList(selectedRow.getHostelId(),conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void transferRoom()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.transferRoomFromStudent(selectedRow,transferDate,transferRoomNo,conn);
		if(i>=1)
		{
			String refNo6;
			try {
				refNo6=addWorkLog6(conn);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Room Sucessfully Transfered"));
			RoomInfo ri = obj.roomDetailByRoomId(transferRoomNo, conn);
			int allocatedBeds = obj.allocatedBedsInRoom(transferRoomNo,conn);
			//int totalBeds = allocatedBeds+1;
			if(allocatedBeds>=Integer.valueOf(ri.getNoOfBed()))
			{
				obj.changeRoomStatus(transferRoomNo,"occupied",conn);
				String refNo5;
				try {
					refNo5=addWorkLog5(conn);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			allocatedRoomList=obj.allAllocatedRooms(conn);

			selectedRoom=transferRoomNo=selectedStudent="";addDate=deallocationDate=transferDate=new Date();selectedRow=new RoomInfo();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured.."));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog6(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
		String adDt = formater.format(transferDate);
		
		value = "Room-"+transferRoomNo+" --transfer Date-"+adDt+" --SelectedId-"+selectedRow.getId()+" --SelectedRoomId-"+selectedRow.getRoomId();
	   
	   	
		String refNo = workLg.saveWorkLogMehod(language,"transfer Room","WEB",value,conn);
		return refNo;
	}
	
	
	public String addWorkLog5(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Room-"+transferRoomNo+" --Status-Occupied";
	   
	   	
		String refNo = workLg.saveWorkLogMehod(language,"Change Room Status on Transfer","WEB",value,conn);
		return refNo;
	}


	public String getSelectedRoom() {
		return selectedRoom;
	}
	public void setSelectedRoom(String selectedRoom) {
		this.selectedRoom = selectedRoom;
	}
	public String getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(String selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public Date getDeallocationDate() {
		return deallocationDate;
	}
	public void setDeallocationDate(Date deallocationDate) {
		this.deallocationDate = deallocationDate;
	}
	public ArrayList<RoomInfo> getAllocatedRoomList() {
		return allocatedRoomList;
	}
	public void setAllocatedRoomList(ArrayList<RoomInfo> allocatedRoomList) {
		this.allocatedRoomList = allocatedRoomList;
	}
	public RoomInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(RoomInfo selectedRow) {
		this.selectedRow = selectedRow;
	}
	public String getTransferRoomNo() {
		return transferRoomNo;
	}
	public void setTransferRoomNo(String transferRoomNo) {
		this.transferRoomNo = transferRoomNo;
	}
	public Date getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public String getHostelName() {
		return hostelName;
	}

	public void setHostelName(String hostelName) {
		this.hostelName = hostelName;
	}

	public ArrayList<SelectItem> getHostelNameList() {
		return hostelNameList;
	}

	public void setHostelNameList(ArrayList<SelectItem> hostelNameList) {
		this.hostelNameList = hostelNameList;
	}

	public ArrayList<SelectItem> getRoomList() {
		return roomList;
	}

	public void setRoomList(ArrayList<SelectItem> roomList) {
		this.roomList = roomList;
	}

	public ArrayList<SelectItem> getNewRoomList() {
		return newRoomList;
	}

	public void setNewRoomList(ArrayList<SelectItem> newRoomList) {
		this.newRoomList = newRoomList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
