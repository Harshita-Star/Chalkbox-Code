package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="teacherWiseTimeTable")
@ViewScoped

public class TeacherwiseTimeTable implements Serializable
{
	private static final long serialVersionUID = 1L;
	String class_name,section_name,teacher_id,teacher_name,teacher_username;
	ArrayList<SelectItem> teacher;
	NewTimeTableInfo newinfo = new NewTimeTableInfo();
	ArrayList<NewTimeTableInfo> timeTableList,newlist;
	boolean show;
	int totalLoadLab,totalLoadTheory;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBaseMethodsTimeTable objTimeTable = new DataBaseMethodsTimeTable();
	DataBase obj=new DataBase();

	
	public TeacherwiseTimeTable()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		teacher_username=(String) ss.getAttribute("username");
		teacher_id=objTimeTable.teacherid(teacher_username,conn);
		show = false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		showTable();


	}

	public void showTable()
	{
		Connection conn = DataBaseConnection.javaConnection();
	

		show = true;
		//String session=DataBase.selectedSessionDetails();
		teacher_name = obj1.employeeNameById(teacher_id,conn);

		TimeTableSettingInfo schedule_info = obj.timeTableInfo(conn,schoolId);
		int lunchAfter=Integer.valueOf(schedule_info.getLunch_after());
		String lunchTime=schedule_info.getTime_of_lunch();

		int noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
		String timeOfLec[] =  schedule_info.getTime_of_lec().split(",");
		String startTime =  schedule_info.getStart_time();
		String wintimeOfLec[] =  schedule_info.getWinterLecTime().split(",");
		String winterStartTime =  schedule_info.getWinterStartTime();

		timeTableList = new ArrayList<>();
		newlist = new ArrayList<>();
		for(int i=0;i<noOfLec;i++)
		{
			NewTimeTableInfo info = new NewTimeTableInfo();
			try
			{
				info.setLecTime(startTime+" - "+timeOfLec[i]);
				info.setWinterLecTime(winterStartTime+" - "+wintimeOfLec[i]);
				info.setLecNo(String.valueOf(i+1));
				startTime=timeOfLec[i];
				winterStartTime = wintimeOfLec[i];
				if(i==lunchAfter-1)
				{
					String temp[]=startTime.split(":");
					String tempW[]=winterStartTime.split(":");
					
					String lunchtemp[]=lunchTime.split(":");

					int x=Integer.parseInt(temp[0])+Integer.parseInt(lunchtemp[0]);
					int y=Integer.parseInt(temp[1])+Integer.parseInt(lunchtemp[1]);
					startTime=x+":"+y;
					
					int a=Integer.parseInt(tempW[0])+Integer.parseInt(lunchtemp[0]);
					int b=Integer.parseInt(tempW[1])+Integer.parseInt(lunchtemp[1]);
					/*if(b>=60)
					{
						a=a+1;
						b=b-60;
					}*/
					winterStartTime=a+":"+b;
				}
			}
			catch (Exception e )
			{
				e.printStackTrace();
			}
			newlist.add(info);
		}
		newlist = objTimeTable.teacherTimeTableDetail(teacher_id,newlist,schoolId,conn);
		//totalLoadLab=newlist.get(newlist.size()-1).getTotalLoadLab();
		totalLoadTheory=newlist.get(newlist.size()-1).getTotalLoadTheory();

		int x = 0;
		for(NewTimeTableInfo info : newlist)
		{
			x = x+1;
			if(x==(lunchAfter+1))
			{
				newinfo.setLecNo("LUNCH");
				newinfo.setMon_class("L");
				newinfo.setTues_class("U");
				newinfo.setWed_class("N");
				newinfo.setThur_class("C");
				newinfo.setFri_class("H");
				newinfo.setSat_class("LUNCH");

				timeTableList.add(newinfo);
				timeTableList.add(info);
			}
			else
			{
				timeTableList.add(info);
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getClass_name() {
		return class_name;
	}
	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public ArrayList<NewTimeTableInfo> getTimeTableList() {
		return timeTableList;
	}
	public void setTimeTableList(ArrayList<NewTimeTableInfo> timeTableList) {
		this.timeTableList = timeTableList;
	}
	public String getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(String teacher_id) {
		this.teacher_id = teacher_id;
	}
	public ArrayList<SelectItem> getTeacher() {
		return teacher;
	}
	public void setTeacher(ArrayList<SelectItem> teacher) {
		this.teacher = teacher;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getTeacher_name() {
		return teacher_name;
	}
	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}
	public int getTotalLoadLab() {
		return totalLoadLab;
	}
	public void setTotalLoadLab(int totalLoadLab) {
		this.totalLoadLab = totalLoadLab;
	}
	public int getTotalLoadTheory() {
		return totalLoadTheory;
	}
	public void setTotalLoadTheory(int totalLoadTheory) {
		this.totalLoadTheory = totalLoadTheory;
	}

	public String getTeacher_username() {
		return teacher_username;
	}

	public void setTeacher_username(String teacher_username) {
		this.teacher_username = teacher_username;
	}
}
