package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="individualTimeTable")
@ViewScoped

public class IndividiualTimeTable implements Serializable
{
	private static final long serialVersionUID = 1L;
	String class_name,section_name,teacher_name,selectedDept,selectedTeacher;

	ArrayList<SelectItem> teacherList,classlist;
	NewTimeTableInfo newinfo = new NewTimeTableInfo();
	ArrayList<NewTimeTableInfo> timeTableList,newlist;
	boolean show=false;
	int totalLoadLab,totalLoadTheory;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBase obj=new DataBase();
	DataBaseMethodsTimeTable objTT=new DataBaseMethodsTimeTable();



	public IndividiualTimeTable()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		String categid="";
		String categid1 = obj1.employeeCategoryIdByName("Teacher", conn);
		String categid2 = obj1.employeeCategoryIdByName("Sports Department", conn);
		String categid3 = obj1.employeeCategoryIdByName("Librarian", conn);
		String categid4 = obj1.employeeCategoryIdByName("Principal", conn);
		String categid5 = obj1.employeeCategoryIdByName("Vice Principal", conn);
		String categid6 = obj1.employeeCategoryIdByName("Administrative Officer", conn);
		
		categid=categid1+"','"+categid2+"','"+categid3+"','"+categid4+"','"+categid5+"','"+categid6;
		
		classlist=obj1.allClass(conn);
		teacherList=obj1.allteacherOnly(categid,schoolId,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public void teacherListByDepartment()
	{
		teacherList=new DataBase().staffListByDept(selectedDept);
		show = false;
	}*/

	public void showTable()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		show = true;
		//String session=DataBase.selectedSessionDetails();
		teacher_name = obj1.employeeNameById(selectedTeacher,conn);

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
		newlist = objTT.teacherTimeTableDetail(selectedTeacher,newlist,schoolId,conn);
		//totalLoadLab=newlist.get(timeTableList.size()-1).getTotalLoadLab();
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
	public String getSelectedDept() {
		return selectedDept;
	}
	public void setSelectedDept(String selectedDept) {
		this.selectedDept = selectedDept;
	}
	public String getSelectedTeacher() {
		return selectedTeacher;
	}
	public void setSelectedTeacher(String selectedTeacher) {
		this.selectedTeacher = selectedTeacher;
	}
	public ArrayList<SelectItem> getTeacherList() {
		return teacherList;
	}
	public void setTeacherList(ArrayList<SelectItem> teacherList) {
		this.teacherList = teacherList;
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

	public ArrayList<SelectItem> getClasslist() {
		return classlist;
	}

	public void setClasslist(ArrayList<SelectItem> classlist) {
		this.classlist = classlist;
	}
}
