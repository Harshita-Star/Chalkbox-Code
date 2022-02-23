package student_module;

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

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="viewParentAppointment")
@ViewScoped
public class ViewParentAppointmentBean implements Serializable {
	String description,toMeet,schoolId,username,selectTime;
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	Date appointmentDate,currentDate=new Date();
	ArrayList<String>timeList= new ArrayList<>();
	AppointmentInfo selected;
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	public ViewParentAppointmentBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//timeList=(ArrayList<String>) ss.getAttribute("timelist");
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		appointmentList=obj.selectAppointmentList(conn,schoolId,username);

		////// // System.out.println("appointmentList"+appointmentList);
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
			timeList.add(endTime);
			////// // System.out.println("timeList"+timeList.size());

		}
		ss.setAttribute("timelist", timeList);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = obj.deleteAppointmentRequest(conn,selected.getId(),"parent_appointment");
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment Request is deleted successfully!"));
			appointmentList=obj.selectAppointmentList(conn,schoolId,username);

			StudentInfo ln = DBJ.studentDetailslistByAddNo(username, schoolId, conn);
			DBJ.adminnotification("Appointment","Parent of "+ln.getFname()+", Class "+ln.getClassName()+" deleted the appointment request for date "+selected.getStrAppointmentdate()+" "+selected.getAppointment_time()+".","admin-"+schoolId,schoolId,"AppointmentDelete-"+username,conn);

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
	}


	public void modified()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selected.getStatus().equalsIgnoreCase("pending"))
		{
			int i=obj.updateAppointmentDateAndTime(conn,selected.getId(),appointmentDate,currentDate,selectTime);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment Date And Time has updated successfully!"));

				appointmentList=obj.selectAppointmentList(conn,schoolId,username);

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));

			}
		}
		else if(selected.getStatus().equalsIgnoreCase("rejected"))
		{
			int i= obj.submitParentAppointmentRequest(conn,appointmentDate,currentDate,"Pending",selected.getDescription(),selected.getToMeet(),schoolId,username,selectTime);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parent Appointment request is successfully resubmitted!"));
				obj.updateModifyStatus(conn,selected.getId(),"Yes");
				appointmentList=obj.selectAppointmentList(conn,schoolId,username);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public ArrayList<AppointmentInfo> getAppointmentList() {
		return appointmentList;
	}
	public void setAppointmentList(ArrayList<AppointmentInfo> appointmentList) {
		this.appointmentList = appointmentList;
	}
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public AppointmentInfo getSelected() {
		return selected;
	}
	public void setSelected(AppointmentInfo selected) {
		this.selected = selected;
	}
	public ArrayList<String> getTimeList() {
		return timeList;
	}
	public void setTimeList(ArrayList<String> timeList) {
		this.timeList = timeList;
	}
	public String getSelectTime() {
		return selectTime;
	}
	public void setSelectTime(String selectTime) {
		this.selectTime = selectTime;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public DatabaseMethods1 getObj2() {
		return obj2;
	}

	public void setObj2(DatabaseMethods1 obj2) {
		this.obj2 = obj2;
	}
}