package timetable_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="printTimeTableClasswise")
@ViewScoped

public class TimeTableClasswise implements Serializable
{

	String sem,class_name,branch,lunchTime,section,section_name;
	ArrayList<TimeTableInfo> list;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	DataBase db=new DataBase();
	
	public TimeTableClasswise()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String class_id = (String) ss.getAttribute("class_id");

		sem=(String) ss.getAttribute("sem");
		lunchTime = (String) ss.getAttribute("lunch_time");
		section=(String) ss.getAttribute("sec");
		section_name=obj1.sectionNameByIdSchid(schoolId, section,conn);

		class_name  = db.courseNameById(Integer.valueOf(class_id),conn);
		//branch = new DataBase().branchNameById(section_id);
		branch=branch.toUpperCase();

		list = (ArrayList<TimeTableInfo>) ss.getAttribute("timetable");

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

	public String getLunchTime() {
		return lunchTime;
	}
	public void setLunchTime(String lunchTime) {
		this.lunchTime = lunchTime;
	}
	public ArrayList<TimeTableInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TimeTableInfo> list) {
		this.list = list;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}



}
