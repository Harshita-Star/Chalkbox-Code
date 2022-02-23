package student_module;

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
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="parentAppointment")
@ViewScoped
public class ParentAppointmentBean implements Serializable {

	String regex=RegexPattern.REGEX;
	String description,toMeet,schoolId,username,selectTime;
	Date appointmentDate,currentDate=new Date();
	ArrayList<String>timeList= new ArrayList<>();
	ArrayList<SelectItem> toMeetList = new ArrayList<>();
	DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public ParentAppointmentBean()
	{
		////// // System.out.println("vhgjhk");
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		SchoolInfoList info=obj2.fullSchoolInfo(schoolId, conn);
		String t1= info.getAppointStartTime();
		String t2= info.getAppointEndTime();
		String st[]=t1.split(":");
		String et[]=t2.split(":");
		int startvalue=Integer.valueOf(st[0]);
		int lastvalue=Integer.valueOf(et[0]);
		int i=0;
		timeList= new ArrayList<>();
		for(i=startvalue;i<=lastvalue;i++)
		{
			String stime=String.valueOf(i);
			String startTime=stime+":"+"00";
			String endTime=stime+":"+"30";
			timeList.add(startTime);
			if(!startTime.equalsIgnoreCase(t2))
			{
				timeList.add(endTime);
			}
			//////// // System.out.println("timeList"+timeList.size());

		}
		ss.setAttribute("timelist", timeList);
		//////// // System.out.println("timeList"+timeList);
		toMeetList=obj1.selectToMeetList(conn,schoolId);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String submit()
	{
		currentDate=new Date();
		Connection conn = DataBaseConnection.javaConnection();
		int i= obj1.submitParentAppointmentRequest(conn,appointmentDate,currentDate,"Pending",description,toMeet,schoolId,username,selectTime);
		if(i>=1)
		{
			appointmentDate=new Date();
			description=toMeet=selectTime="";

			StudentInfo ln = DBJ.studentDetailslistByAddNo(username, schoolId, conn);
			String strdt = new SimpleDateFormat("dd-MM-yyyy").format(appointmentDate);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment requested successfully!"));
			DBJ.adminnotification("Appointment","Parent of "+ln.getFname()+", Class "+ln.getClassName()+" requested for an appointment on "+strdt+" "+selectTime+". Please check your availability and revert on it.","admin-"+schoolId,schoolId,"Appointment-"+username,conn);
			return "parentAppointment.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getToMeet() {
		return toMeet;
	}


	public void setToMeet(String toMeet) {
		this.toMeet = toMeet;
	}


	public Date getAppointmentDate() {
		return appointmentDate;
	}


	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public String getSelectTime() {
		return selectTime;
	}
	public void setSelectTime(String selectTime) {
		this.selectTime = selectTime;
	}
	public ArrayList<String> getTimeList() {
		return timeList;
	}
	public void setTimeList(ArrayList<String> timeList) {
		this.timeList = timeList;
	}
	public DataBaseOnlineAdm getObj1() {
		return obj1;
	}
	public void setObj1(DataBaseOnlineAdm obj1) {
		this.obj1 = obj1;
	}
	public DatabaseMethods1 getObj2() {
		return obj2;
	}
	public void setObj2(DatabaseMethods1 obj2) {
		this.obj2 = obj2;
	}
	public ArrayList<SelectItem> getToMeetList() {
		return toMeetList;
	}
	public void setToMeetList(ArrayList<SelectItem> toMeetList) {
		this.toMeetList = toMeetList;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
