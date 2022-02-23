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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="changeTimeTable")
@ViewScoped
public class ChangeTimeTableDetailsBean implements Serializable
{

	String msg;
	Date startTime=new Date();
	int preLec;
	String time,winterTime;
	String noOfLec,timeOfLunch="",lunchAfter="";
	ArrayList<TimeTableInfo> timeOfLecs;
	DataBaseMethodsTimeTable objTimeTable= new DataBaseMethodsTimeTable();
	TimeTableSettingInfo schedule_info;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBase obj = new DataBase();
	
	boolean showExam;

	public ChangeTimeTableDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		preLec = objTimeTable.getPreviousNoOfLecs(conn);
		schedule_info = obj.timeTableSettingInfo(conn,schoolId);
		
		if(schedule_info.getIdtimetable_info().equals(""))
		{
			
		}
		else
		{
			time = schedule_info.getStart_time();
			winterTime = schedule_info.getWinterStartTime();
			noOfLec = schedule_info.getNo_of_lec();
			timeOfLunch = schedule_info.getTime_of_lunch();
			lunchAfter = schedule_info.getLunch_after();
			showLectures();
		}
		
		
		//msg="Are you sure to update?";
		msg = "Any change in Number of lectures will delete time schedules of\n all the classes permanently. Do you still want to continue.?";
		showExam=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String updateDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//time = new SimpleDateFormat("HH:mm").format(startTime);
		//timeOfLunch="00:"+timeOfLunch;
		if(lunchAfter.trim().equals(""))
			lunchAfter="0";
		if(timeOfLunch.trim().equals(""))
			timeOfLunch="00:00";
		int i = objTimeTable.TimeTableDeatils1(time,noOfLec,timeOfLunch,lunchAfter,timeOfLecs,winterTime,conn);

		if(i==1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Time table details updated successfully"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "changeTimeTableDetails";
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Some error occured! please check"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}

	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		language ="Summer Start time-"+time+" --Winter Start time-"+winterTime+" --No of Periods-"+noOfLec+" --Lunch After-"+lunchAfter+" --Lunch Duration-"+timeOfLunch;
		
		for(TimeTableInfo tm :timeOfLecs)
		{	
	       value += "(Summer"+tm.getLecTime()+" -- Winter-"+tm.getWinterLecTime()+")";
		}
		String refNo = workLg.saveWorkLogMehod(language,"Change Time Table Settings","WEB",value,conn);
		return refNo;
	}
	

	public void showLectures()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(noOfLec==null || noOfLec.equalsIgnoreCase(""))
		{
			
		}
		else
		{
			if(preLec==Integer.valueOf(noOfLec))
			{
				timeOfLecs=objTimeTable.checkTimeTableSettings(conn);
			}
			else
			{
				timeOfLecs=new ArrayList<>();
			}

			if(Integer.valueOf(noOfLec)>0)
			{
				showExam=true;
			}
			else
			{
				showExam=false;
			}

			if(timeOfLecs.size()<=0)
			{
				timeOfLecs=new ArrayList<>();
				for(int i=0;i<Integer.valueOf(noOfLec);i++)
				{
					TimeTableInfo info = new TimeTableInfo();
					info.setSn(i+1);
					info.setLecTime("");
					info.setWinterLecTime("");

					timeOfLecs.add(info);
				}
			}
			else
			{
				int k=1;
				for(TimeTableInfo tt : timeOfLecs)
				{
					tt.setSn(k++);
				}
			}
		}
		

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public String getNoOfLec() {
		return noOfLec;
	}
	public void setNoOfLec(String noOfLec) {
		this.noOfLec = noOfLec;
	}
	public String getTimeOfLunch() {
		return timeOfLunch;
	}
	public void setTimeOfLunch(String timeOfLunch) {
		this.timeOfLunch = timeOfLunch;
	}
	public String getLunchAfter() {
		return lunchAfter;
	}
	public void setLunchAfter(String lunchAfter) {
		this.lunchAfter = lunchAfter;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ArrayList<TimeTableInfo> getTimeOfLecs() {
		return timeOfLecs;
	}
	public void setTimeOfLecs(ArrayList<TimeTableInfo> timeOfLecs) {
		this.timeOfLecs = timeOfLecs;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isShowExam() {
		return showExam;
	}
	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public String getWinterTime() {
		return winterTime;
	}

	public void setWinterTime(String winterTime) {
		this.winterTime = winterTime;
	}
}
