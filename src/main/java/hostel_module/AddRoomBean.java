package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="addRoom")
@ViewScoped
public class AddRoomBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String startNo,endNo,hostelName,calculation;
	int noOfRooms,noOfBeds;

	ArrayList<String> exceptionRooms;
	ArrayList<SelectItem> hostelNameList;
	ArrayList<RoomInfo> roomsNameList =new ArrayList<>();
	boolean showTable,showManual,showCommon;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();


	public AddRoomBean() {
		Connection con=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,con);
		hostelNameList=obj.viewAllHostelNames(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void roomsDetails()
	{
		if(hostelName.trim().equals("") || hostelName.isEmpty())
		{
			showCommon=false;
		}
		else
		{
			showCommon=true;
		}
		
		
	}

	public void updateRooms() {

		if(calculation.equals("manual"))
		{

			showCommon=true;
			showManual=true;
			showTable=false;

			for(int i=1;i<=noOfRooms;i++)
			{
				RoomInfo tt=new RoomInfo();
				tt.setSno(i);
				roomsNameList.add(tt);
			}

		}
		else {
			showTable=true;
			showCommon=true;
			showManual=false;
		}
	}

	public void calculateEndRooms() {

		endNo=String.valueOf(Integer.valueOf(startNo)+noOfRooms-1);

	}

	public ArrayList<String> autoCompleteRooms(String query)
	{
		ArrayList<String> roomList=new ArrayList<>();
		ArrayList<String> list=new ArrayList<>();
		int start=Integer.parseInt(startNo);
		int end=Integer.parseInt(endNo);
		for(int i=start;i<=end;i++)
		{
			list.add(String.valueOf(i));
		}

		for(String room:list)
		{
			if(room.startsWith(query))
			{
				roomList.add(room);
			}
		}
		return roomList;
	}

	public String addNow()
	{
		Connection con=DataBaseConnection.javaConnection();
		int i1=0,flag=0,flag1=0;

		
		try
		{
		  if(hostelName.trim().equals("") || hostelName.isEmpty())
		  {
			  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Hostel name cann't be blank"));
			  return "";
		  }
		  else
		  {
			  if(calculation.equals("manual")) 
				{
					for(RoomInfo tt: roomsNameList) {
						boolean duplicateRooms=obj.checkForDuplicateRoomsNo(con,tt.getRoomNo(),hostelName,String.valueOf(noOfBeds));

						if(duplicateRooms==true){

							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Duplicate Room No found"));
							flag=1;
						}
					}
					if(flag==0)
					{
						for(RoomInfo tt: roomsNameList) {

							if(!tt.getRoomNo().trim().equals(""))
							{
								i1=obj.addRoomsManuals(con,tt.getRoomNo(),hostelName,noOfBeds);
							}}
						if(i1>=1)
						{
							addWorkLog(con);
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Rooms Added Sucessfully"));
							showCommon=showTable=showManual=false;
							hostelName="";
							noOfRooms = noOfBeds = 0;
							return "addRoom.xhtml";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured"));
						}
					}
				}
			  else
				{
					for(int i=Integer.valueOf(startNo);i<=Integer.valueOf(endNo);i++)
					{
						boolean duplicateRooms=obj.checkForDuplicateRoomsNo(con,String.valueOf(i),hostelName,String.valueOf(noOfBeds));
						if(duplicateRooms)
						{
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Duplicate Room No found"));
							flag1=1;
						}
					}
					if(flag1==0)
					{
						int i=obj.addRooms(startNo,endNo,exceptionRooms,hostelName,noOfBeds,con);
						if(i>=1)
						{
							addWorkLog2(con);
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Rooms Added Sucessfully"));
							startNo=endNo="";exceptionRooms=new ArrayList<>();
							showCommon=showTable=showManual=false;
							noOfRooms = noOfBeds = 0;
							return "addRoom.xhtml";
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("An Error Occured"));
						}
					}
					
				}
				
		  }
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";

	}
	
	
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    language = "Hostel-"+hostelName+" --No. of Rooms-"+noOfRooms+" -No. of Bed-"+noOfBeds;
	   
	    for(RoomInfo tt: roomsNameList) {

			if(!tt.getRoomNo().trim().equals(""))
			{
	          value += "("+tt.getRoomNo()+")";
			}
	    }	
		String refNo = workLg.saveWorkLogMehod(language,"Add Room Manual","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    language = "Hostel-"+hostelName+" --No. of Rooms-"+noOfRooms+" -No. of Bed-"+noOfBeds;
	    value = "StartNo-"+startNo+" -- EndNo.-"+endNo+" -- exception Rooms-"; 
	    
	    if(exceptionRooms.size()!=0)
	    {	
	      for(String tt: exceptionRooms) {

	          value += "("+tt+")";
		  }
	    }  
		String refNo = workLg.saveWorkLogMehod(language,"Add Room Manual","WEB",value,conn);
		return refNo;
	}
	
	
	
	
	public String getStartNo() {
		return startNo;
	}
	public void setStartNo(String startNo) {
		this.startNo = startNo;
	}
	public String getEndNo() {
		return endNo;
	}
	public void setEndNo(String endNo) {
		this.endNo = endNo;
	}
	public ArrayList<String> getExceptionRooms() {
		return exceptionRooms;
	}
	public void setExceptionRooms(ArrayList<String> exceptionRooms) {
		this.exceptionRooms = exceptionRooms;
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

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public int getNoOfRooms() {
		return noOfRooms;
	}

	public void setNoOfRooms(int noOfRooms) {
		this.noOfRooms = noOfRooms;
	}

	public String getCalculation() {
		return calculation;
	}

	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}

	public ArrayList<RoomInfo> getRoomsNameList() {
		return roomsNameList;
	}

	public void setRoomsNameList(ArrayList<RoomInfo> roomsNameList) {
		this.roomsNameList = roomsNameList;
	}

	public boolean isShowManual() {
		return showManual;
	}

	public void setShowManual(boolean showManual) {
		this.showManual = showManual;
	}

	public boolean isShowCommon() {
		return showCommon;
	}

	public void setShowCommon(boolean showCommon) {
		this.showCommon = showCommon;
	}

	public int getNoOfBeds() {
		return noOfBeds;
	}

	public void setNoOfBeds(int noOfBeds) {
		this.noOfBeds = noOfBeds;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
