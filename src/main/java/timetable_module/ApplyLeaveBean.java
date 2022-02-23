package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="leaveApply")
@ViewScoped

public class ApplyLeaveBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> subCatList,leaveBasisList,staffList;
	Date startDate=new Date(),endDate,viewDate,searchDate,uStartDate,uEndDate,appDate;
	String leaveType="",subLeaveType="",leaveBasis="",days="0",postBy,type,description,applyDate,teacher_id,dayName,teacher_username,staff;
	boolean showSubCat,showEndDate,show;
	ArrayList<NewTimeTableInfo> list;
	int leaveId;
	NewTimeTableInfo selectedLec;
	DataBaseMethodsTimeTable objTimeTable= new DataBaseMethodsTimeTable();
	DataBase obj=new DataBase();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;

	public ApplyLeaveBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		type=(String) ss.getAttribute("type");

		teacher_username=(String) ss.getAttribute("username");
		teacher_id=objTimeTable.teacherid(teacher_username,conn);
		postBy=teacher_id;
		viewDate=new Date();
		appDate=new Date();
		applyDate=new SimpleDateFormat("dd/MM/yyyy").format(appDate);
		searchDate=new Date();
		leaveBasisList=obj.allLeaveBasis(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkLeaveType()
	{
		days="";
		if(leaveType.equalsIgnoreCase("Full Day"))
		{
			viewDate=null;
			showEndDate=true;showSubCat=false;
			subLeaveType="";
		}
		else if(leaveType.equalsIgnoreCase("Half Day"))
		{
			viewDate=null;
			endDate=new Date();
			showEndDate=false;showSubCat=true;
			days="0.5";
			subCatList=new ArrayList<>();
			subCatList.add(new SelectItem("First Half","First Half"));
			subCatList.add(new SelectItem("Second Half","Second Half"));
			subLeaveType="First Half";
		}
	}

	public void calculateDay()
	{
		if(leaveType.equalsIgnoreCase("Full Day"))
		{
			startDate.setHours(0);startDate.setMinutes(0);startDate.setSeconds(0);
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(startDate);

			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(endDate);
			days=String.valueOf(dbm.dayCalculateForLeave(startCalendar, endCalendar));
		}
	}

	public void applyLeave()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(leaveType.equalsIgnoreCase("Full Day") && startDate.after(endDate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correctly Enter Start Date & End Date"));
		}
		else
		{
			if(leaveType.equalsIgnoreCase("Half Day"))
			{
				endDate=startDate;
			}
			leaveId=objTimeTable.applyForLeave(teacher_id,startDate,endDate,appDate,days,description,
					leaveType,subLeaveType,leaveBasis,conn);
			if(leaveId>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Leave Request has Sent Successfully.Please Arrange Your Lectures!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error in Request for Leave.Please Check!"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getDescription() {
		return description;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public ArrayList<SelectItem> getStaffList() {
		return staffList;
	}

	public void setStaffList(ArrayList<SelectItem> staffList) {
		this.staffList = staffList;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}
	public ArrayList<SelectItem> getSubCatList() {
		return subCatList;
	}

	public void setSubCatList(ArrayList<SelectItem> subCatList) {
		this.subCatList = subCatList;
	}

	public Date getViewDate() {
		return viewDate;
	}

	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	public Date getuStartDate() {
		return uStartDate;
	}

	public void setuStartDate(Date uStartDate) {
		this.uStartDate = uStartDate;
	}

	public Date getuEndDate() {
		return uEndDate;
	}

	public void setuEndDate(Date uEndDate) {
		this.uEndDate = uEndDate;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getSubLeaveType() {
		return subLeaveType;
	}

	public void setSubLeaveType(String subLeaveType) {
		this.subLeaveType = subLeaveType;
	}

	public String getPostBy() {
		return postBy;
	}

	public void setPostBy(String postBy) {
		this.postBy = postBy;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(String teacher_id) {
		this.teacher_id = teacher_id;
	}
	public String getDayName() {
		return dayName;
	}
	public void setDayName(String dayName) {
		this.dayName = dayName;
	}
	public boolean isShowSubCat() {
		return showSubCat;
	}
	public void setShowSubCat(boolean showSubCat) {
		this.showSubCat = showSubCat;
	}
	public boolean isShowEndDate() {
		return showEndDate;
	}
	public void setShowEndDate(boolean showEndDate) {
		this.showEndDate = showEndDate;
	}
	public ArrayList<SelectItem> getLeaveBasisList() {
		return leaveBasisList;
	}
	public void setLeaveBasisList(ArrayList<SelectItem> leaveBasisList) {
		this.leaveBasisList = leaveBasisList;
	}
	public String getLeaveBasis() {
		return leaveBasis;
	}
	public void setLeaveBasis(String leaveBasis) {
		this.leaveBasis = leaveBasis;
	}
	public ArrayList<NewTimeTableInfo> getList() {
		return list;
	}
	public void setList(ArrayList<NewTimeTableInfo> list) {
		this.list = list;
	}
	public NewTimeTableInfo getSelectedLec() {
		return selectedLec;
	}
	public void setSelectedLec(NewTimeTableInfo selectedLec) {
		this.selectedLec = selectedLec;
	}

	public String getTeacher_username() {
		return teacher_username;
	}

	public void setTeacher_username(String teacher_username) {
		this.teacher_username = teacher_username;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
