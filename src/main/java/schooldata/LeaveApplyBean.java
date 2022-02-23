package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="leaveApplyTeacher")
@ViewScoped
public class LeaveApplyBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,empId,userName,dept,days="0",description,applyDate,leaveType="Half Day",uLeaveType,uDescription,uDays;
	String uApplyDate,oldLeaveType;
	Date startDate=new Date(),endDate,viewDate,searchStartDate,searchEndDate,uStartDate,uEndDate;
	ArrayList<EmployeInfo> leaveInfo;
	EmployeInfo selectedEmp;
	boolean showEndDate,uShowEndDate;
	DatabaseMethods1 obj= new DatabaseMethods1();

	@SuppressWarnings("deprecation")
	public LeaveApplyBean()
	{
		Connection conn = DataBaseConnection.javaConnection();

		viewDate=new Date();
		searchStartDate=new Date();
		endDate=new Date();
		searchStartDate.setDate(1);
		searchEndDate=new Date();
		searchEndDate.setDate(obj.calculateDayInMonth(searchStartDate.getMonth()+1, searchStartDate.getYear()+1900));
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userName=(String) ss.getAttribute("username");
		EmployeInfo ll=obj.employeeInfoByUserName(userName,conn);
		empId=ll.getId();
		name=ll.getName();
		dept=ll.getDept();
		applyDate=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		leaveInfo=obj.statusOfLastLeaveApplied(empId,searchStartDate,searchEndDate,conn);
		checkLeaveType();
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void viewLeaves()
	{
		Connection conn= DataBaseConnection.javaConnection();
		leaveInfo=obj.statusOfLastLeaveApplied(empId,searchStartDate,searchEndDate,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void checkFrom()
	{
		endDate = startDate;
		calculateDay();
	}

	public void checkFromForUpdate()
	{
		uEndDate = uStartDate;
		calculateDayForUpdate();
	}

	public void checkLeaveType()
	{
		days="";
		if(leaveType.equalsIgnoreCase("Full Day"))
		{
			viewDate=null;
			showEndDate=true;
			days="1";
		}
		else if(leaveType.equalsIgnoreCase("Half Day"))
		{
			viewDate=null;
			showEndDate=false;
			days="0.5";
		}
	}

	public void calculateDay()
	{
		if(leaveType.equalsIgnoreCase("Full Day"))
		{
			startDate.setHours(00);
			startDate.setMinutes(00);
			startDate.setSeconds(00);
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(startDate);

			endDate.setHours(00);
			endDate.setMinutes(00);
			endDate.setSeconds(00);
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(endDate);
			days=String.valueOf(DatabaseMethods1.dayCalculateForLeave(startCalendar, endCalendar));
		}
	}

	@SuppressWarnings("deprecation")
	public void applyLeave()
	{
		FacesContext context=FacesContext.getCurrentInstance();
		Connection conn= DataBaseConnection.javaConnection();
		Timestamp sDate=new Timestamp(startDate.getTime()) ;sDate.setHours(0);sDate.setMinutes(0);sDate.setSeconds(0);sDate.setNanos(0);;
		Timestamp eDate=new Timestamp(endDate.getTime());eDate.setHours(0);eDate.setMinutes(0);eDate.setSeconds(0);eDate.setNanos(0);;
		Timestamp cDate=new Timestamp(new Date().getTime());cDate.setHours(0);cDate.setMinutes(0);cDate.setSeconds(0);cDate.setNanos(0);;
		/*if(leaveType.equalsIgnoreCase("Half Day") && sDate.equals(cDate))
		{
			flag=0;
		}
		else
		{
			attend=obj.checkAttendanceOnDate(empId, sDate, eDate,conn);
			if(attend.equals("A") || attend.equals("P_H") || attend.equals("No") )
			{
				flag=0;
			}
			else
			{
				flag=1;
			}
		}

		if(flag==0)
		{*/
		/*if(attend.equals("P_H") && leaveType.equalsIgnoreCase("Full Day"))
			{
				context.addMessage(null, new FacesMessage("You are Present Half day on this date... So only half day leave can be applied"));
			}
			else
			{*/
		if(leaveType.equalsIgnoreCase("Full Day") && startDate.after(endDate))
		{
			context.addMessage(null, new FacesMessage("Correctly Enter Start Date & End Date"));
		}
		else
		{
			if(!leaveType.equalsIgnoreCase("Full Day"))
			{
				endDate=startDate;
			}
			int x=obj.checkAlreadyAppliedDate(empId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()),conn);
			if(x==1)
			{
				context.addMessage(null, new FacesMessage("You Can Not Apply Leave... Because Leave Is Already Applied"));
			}
			else
			{
				int i=obj.applyLeave(empId,new Date(),startDate,endDate,days,description,leaveType,conn);
				if(i==1)
				{
					context.addMessage(null, new FacesMessage("Leave Applied SuccessFully"));

					leaveInfo=obj.statusOfLastLeaveApplied(empId,searchStartDate,searchEndDate,conn);

					endDate=null;startDate=new Date();days="0.5";description="";leaveType="Half Day";viewDate=new Date();
					showEndDate=false;uShowEndDate=false;
				}
				else
				{
					context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
				}
			}
		}
		//}
		/*}
		else
		{
			context.addMessage(null, new FacesMessage("You are already present on this day"));
		}*/
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void editDetails()
	{
		uApplyDate=new SimpleDateFormat("dd/MM/yyyy").format(selectedEmp.getLeaveApply());
		uStartDate=selectedEmp.getStartDate();
		uEndDate=selectedEmp.getEndDate();
		uDays=selectedEmp.getDays();
		uDescription=selectedEmp.getDescription();
		uLeaveType=selectedEmp.getLeaveType();
		oldLeaveType=selectedEmp.getLeaveType();
		checkLeaveTypeForUpdate();
	}

	public void checkLeaveTypeForUpdate()
	{
		if(uLeaveType.equalsIgnoreCase("Full Day"))
		{
			uShowEndDate=true;
		}
		else if(uLeaveType.equalsIgnoreCase("Half Day"))
		{
			uShowEndDate=false;
			uDays="0.5";
		}
	}

	public void calculateDayForUpdate()
	{
		if(uLeaveType.equalsIgnoreCase("Full Day"))
		{
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(uStartDate);

			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(uEndDate);
			uDays=String.valueOf(DatabaseMethods1.dayCalculateForLeave(startCalendar, endCalendar));
		}
	}

	public void updateLeave()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		if(uLeaveType.equalsIgnoreCase("Full Day") && startDate.after(endDate))
		{
			context.addMessage(null, new FacesMessage("Correctly Enter Start Date & End Date"));
		}
		else
		{
			if(!uLeaveType.equalsIgnoreCase("Full Day"))
			{
				uEndDate=uStartDate;
			}
			int i=obj.updateLeave(selectedEmp.getId(),uStartDate,uEndDate,uDays,uDescription,uLeaveType,selectedEmp.getLeaveApply(),conn);
			if(i==1)
			{
				context.addMessage(null, new FacesMessage("Leave Updated SuccesFully"));
				leaveInfo=obj.statusOfLastLeaveApplied(empId,searchStartDate,searchEndDate,conn);
				PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
				PrimeFaces.current().ajax().update("form");
			}
			else
			{
				context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
				PrimeFaces.current().executeInitScript("PF('editDialog').show()");
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void deleteLeave()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		int i=obj.deleteLeaveById(selectedEmp.getId(),conn);
		if(i==1)
		{
			context.addMessage(null, new FacesMessage("Leave Deleted SuccesFully"));
			leaveInfo=obj.statusOfLastLeaveApplied(empId,searchStartDate,searchEndDate,conn);
		}
		else
		{
			context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}
	public Date getViewDate()
	{
		return viewDate;
	}
	public void setViewDate(Date viewDate)
	{
		this.viewDate = viewDate;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDept() {
		return dept;
	}
	public ArrayList<EmployeInfo> getLeaveInfo() {
		return leaveInfo;
	}
	public void setLeaveInfo(ArrayList<EmployeInfo> leaveInfo) {
		this.leaveInfo = leaveInfo;
	}
	public void setDept(String dept) {
		this.dept = dept;
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
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public boolean isShowEndDate() {
		return showEndDate;
	}
	public void setShowEndDate(boolean showEndDate) {
		this.showEndDate = showEndDate;
	}
	public EmployeInfo getSelectedEmp() {
		return selectedEmp;
	}
	public void setSelectedEmp(EmployeInfo selectedEmp) {
		this.selectedEmp = selectedEmp;
	}
	public String getuLeaveType() {
		return uLeaveType;
	}
	public void setuLeaveType(String uLeaveType) {
		this.uLeaveType = uLeaveType;
	}
	public String getuDescription() {
		return uDescription;
	}
	public void setuDescription(String uDescription) {
		this.uDescription = uDescription;
	}
	public String getuDays() {
		return uDays;
	}
	public void setuDays(String uDays) {
		this.uDays = uDays;
	}
	public String getuApplyDate() {
		return uApplyDate;
	}
	public void setuApplyDate(String uApplyDate) {
		this.uApplyDate = uApplyDate;
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
	public boolean isuShowEndDate() {
		return uShowEndDate;
	}
	public void setuShowEndDate(boolean uShowEndDate) {
		this.uShowEndDate = uShowEndDate;
	}

	public Date getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public Date getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOldLeaveType() {
		return oldLeaveType;
	}

	public void setOldLeaveType(String oldLeaveType) {
		this.oldLeaveType = oldLeaveType;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
