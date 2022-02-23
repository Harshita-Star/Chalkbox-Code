package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="checkLeave")
@ViewScoped

public class CheckLeaveBean implements Serializable
{
	ArrayList<LeaveInfo> leaveList=new ArrayList<>();
	LeaveInfo selectedAss;
	Date startDate,endDate;
	String type,userid;
	DataBaseMethodsTimeTable objTimeTable=new DataBaseMethodsTimeTable();
	DataBase obj=new DataBase();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;


	public CheckLeaveBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		endDate=new Date();
		startDate=new Date();
		endDate.setDate(endDate.getDate()+7);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		type=(String) ss.getAttribute("type");
		userid=(String) ss.getAttribute("username");
		try {
			conn.close();
		} catch (Exception e) {
               e.printStackTrace();		}

	}
	public void showData()
	{

		Connection conn=DataBaseConnection.javaConnection();
		leaveList=objTimeTable.allLeavesList(startDate,endDate,type,userid,conn);

		if(leaveList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Leave Found!"));
		}
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		type=(String) ss.getAttribute("type");
		userid=(String) ss.getAttribute("username");

		leaveList=objTimeTable.allLeavesList(startDate,endDate,type,userid,conn);
		if(leaveList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Leave Found!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void approve()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String status="";
		if(type.equals("faculty_hod"))
		{
			status="forwarded";
		}
		else
		{
			status="approved";
		}
		int i=obj.updateLeaveStatus(selectedAss.getId(),status,type,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Approved!"));
			leaveList=objTimeTable.allLeavesList(startDate,endDate,type,userid,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur.Please Check!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deny()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String status="cancelled";
		int i=obj.updateLeaveStatus(selectedAss.getId(),status,type,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Cancelled!"));
			leaveList=objTimeTable.allLeavesList(startDate,endDate,type,userid,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur.Please Check!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteLeave()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.deleteLeave(selectedAss.getId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Deleted SucessFully!"));
			leaveList=objTimeTable.allLeavesList(startDate,endDate,type,userid,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur.Please Check!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<LeaveInfo> getLeaveList() {
		return leaveList;
	}

	public void setLeaveList(ArrayList<LeaveInfo> leaveList) {
		this.leaveList = leaveList;
	}

	public LeaveInfo getSelectedAss() {
		return selectedAss;
	}

	public void setSelectedAss(LeaveInfo selectedAss) {
		this.selectedAss = selectedAss;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}


}
