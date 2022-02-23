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

@ManagedBean(name="printTimeTable")
@ViewScoped
public class PrintTimeTable implements Serializable
{
	private static final long serialVersionUID = 1L;
	String sem,courseName,branchName,section,sectionName,classTeacher;
	DataBase obj=new DataBase();
	TimeTableSettingInfo schedule_info = new TimeTableSettingInfo();
	NewTimeTableInfo newinfo = new NewTimeTableInfo();
	ArrayList<NewTimeTableInfo> timeTableList = new ArrayList<>();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	
	
	public PrintTimeTable()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String class_id = (String) ss.getAttribute("class_id");
		sem=(String) ss.getAttribute("sem");
		section=(String) ss.getAttribute("sec");
		classTeacher=(String) ss.getAttribute("classTeacher");
		classTeacher = classTeacher + "\n" + "( Class Teacher )";
		sectionName=obj1.sectionNameByIdSchid(schoolId,section,conn);

		courseName  = obj.courseNameById(Integer.valueOf(class_id),conn);
		/*branchName = new DataBase().branchNameById(section_id);
		branchName=branchName.toUpperCase();*/
		schedule_info = obj.timeTableInfo(conn, schoolId);
		int lunchAfter=Integer.valueOf(schedule_info.getLunch_after());
		schedule_info.getTime_of_lunch();

		ArrayList<NewTimeTableInfo> list = new ArrayList<>();
		list = (ArrayList<NewTimeTableInfo>) ss.getAttribute("timetable");
		int i = 0;
		for(NewTimeTableInfo info : list)
		{
			i = i+1;
			info.setLecTime("("+info.getLecTime()+")");
			info.setWinterLecTime("("+info.getWinterLecTime()+")");
			
			if(i==(lunchAfter+1))
			{
				newinfo.setLecNo("LUNCH");
				newinfo.setMon_tchr_name("L");
				newinfo.setTues_tchr_name("U");
				newinfo.setWed_tchr_name("N");
				newinfo.setThur_tchr_name("C");
				newinfo.setFri_tchr_name("H");
				newinfo.setSat_tchr_name("LUNCH");
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
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public ArrayList<NewTimeTableInfo> getTimeTableList() {
		return timeTableList;
	}
	public void setTimeTableList(ArrayList<NewTimeTableInfo> timeTableList) {
		this.timeTableList = timeTableList;
	}
	public String getClassTeacher() {
		return classTeacher;
	}
	public void setClassTeacher(String classTeacher) {
		this.classTeacher = classTeacher;
	}
}
