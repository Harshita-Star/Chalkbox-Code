package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="dayTeacherWiseTimeTable")
@ViewScoped

public class DayTeacherWiseTimeTableBean implements Serializable
{
	String dayid, searchType,dayname;
	ArrayList<ClassInfo> classSectionList = new ArrayList<>();
	ArrayList<String> lectures = new ArrayList<>();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBase obj=new DataBase();
	DataBaseMethodsTimeTable dbt = new DataBaseMethodsTimeTable();
	
	public DayTeacherWiseTimeTableBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		searchType = "current";
		try {
			conn.close();
		} catch (Exception e) {
           e.printStackTrace();		}
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		
		dayname = dbt.dayNameByDayNo(Integer.valueOf(dayid));

		String tableName="time_table";
		if(searchType.equals("current"))
		{
			tableName="time_table";
		}
		else
		{
			tableName="temp_time_table";
		}
		
		String categid="";
		String categid1 = dbm.employeeCategoryIdByName("Teacher", conn);
		String categid2 = dbm.employeeCategoryIdByName("Sports Department", conn);
		String categid3 = dbm.employeeCategoryIdByName("Librarian", conn);
		String categid4 = dbm.employeeCategoryIdByName("Principal", conn);
		String categid5 = dbm.employeeCategoryIdByName("Vice Principal", conn);
		String categid6 = dbm.employeeCategoryIdByName("Administrative Officer", conn);
		
		categid=categid1+"','"+categid2+"','"+categid3+"','"+categid4+"','"+categid5+"','"+categid6;
		classSectionList=dbt.allteacherOnly(categid,conn);
		TimeTableSettingInfo schedule_info = obj.timeTableInfo(conn, schoolId);
		
		int noOfLec = Integer.valueOf(schedule_info.getNo_of_lec());
		lectures = new ArrayList<>();
		for(int i=0;i<noOfLec;i++)
		{
			lectures.add(String.valueOf(i+1));
		}
		
		NewTimeTableInfo ttinfo = new NewTimeTableInfo();
		for(ClassInfo cc : classSectionList)
		{
			Map<String, String> tmap = new HashMap<>();
			Map<String, String> smap = new HashMap<>();
			for(String lecno : lectures)
			{
				ttinfo = dbt.dayTeacherWiseTimeTableDetail(tableName, cc.getTeacherId(), dayid, lecno, schoolId, conn);
				tmap.put(lecno, ttinfo.getClassName()+" - "+ttinfo.getSectionName());
				smap.put(lecno, ttinfo.getSubjectName());
			}

			cc.setTeacherMap(tmap);
			cc.setSubjectMap(smap);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getDayid() {
		return dayid;
	}

	public void setDayid(String dayid) {
		this.dayid = dayid;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public ArrayList<ClassInfo> getClassSectionList() {
		return classSectionList;
	}

	public void setClassSectionList(ArrayList<ClassInfo> classSectionList) {
		this.classSectionList = classSectionList;
	}

	public ArrayList<String> getLectures() {
		return lectures;
	}

	public void setLectures(ArrayList<String> lectures) {
		this.lectures = lectures;
	}

	public String getDayname() {
		return dayname;
	}

	public void setDayname(String dayname) {
		this.dayname = dayname;
	}
	
	
}
