package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;
import schooldata.EmployeeInfo;
import session_work.RegexPattern;

@ManagedBean(name="teacherAttendanceIdWiseReport")
@ViewScoped
public class TeacherAttendanceIdWiseReportBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	String name;
	Date startDate = new Date();
	Date endDate = new Date();
	ArrayList<EmpInfo>teacherDetails = new ArrayList<>();
	EmpInfo teacherInfo=new EmpInfo();
	ArrayList<EmpInfo>teacherList = new ArrayList<>();
	ArrayList<EmpInfo>idWiseList = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<EmployeeInfo> employeeList;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,session;
	
	
public TeacherAttendanceIdWiseReportBean() {
		
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		employeeList=obj.searchEmployeebyName(query,conn);
		List<String> studentListt=new ArrayList<String>();
		
		for(EmployeeInfo info : employeeList)
		{
			studentListt.add(info.getFname()+" / "+info.getLname()+ "-"+info.getId());
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void show()
	{
		Connection conn= DataBaseConnection.javaConnection();
		teacherList =new ArrayList<>();
		
		ArrayList<Date> dates = new ArrayList<>();
		long interval = 24*1000 * 60 * 60; // 1 hour in millis
		long endTime1 =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime1) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date d1 = null;
		Date d2 = null;
		try 
		{
			String arr[]=name.split("-");
			String teacherId=arr[1];
			
			for(int j=0;j<dates.size();j++)
			{
				Date lDate =dates.get(j);
				EmpInfo tt = new EmpInfo();
				teacherInfo = dbr.enteryList(conn,lDate,teacherId);
				tt.setName(teacherInfo.getName());
				tt.setInTime(teacherInfo.getInTime());
				tt.setOutTime(teacherInfo.getOutTime());
				tt.setStrDoj(sdf.format(lDate));
				
				if(tt.getInTime().equalsIgnoreCase(" ") || tt.getOutTime().equalsIgnoreCase(" "))
				{
					tt.setTotalTime(" - ");
				}
				else
				{
					d1 = format.parse(tt.getInTime());
					d2 = format.parse(tt.getOutTime());

					//in milliseconds
					long diff = d2.getTime() - d1.getTime();

					//long diffSeconds = diff / 1000 % 60;
					long diffMinutes = diff / (60 * 1000) % 60;
					long diffHours = diff / (60 * 60 * 1000) % 24;
					//long diffDays = diff / (24 * 60 * 60 * 1000);
					
					tt.setTotalTime(diffHours+" Hours  "+diffMinutes+" Minutes");
					/*
					// // System.out.print(diffDays + " days, ");
					// // System.out.print(diffHours + " hours, ");
					// // System.out.print(diffMinutes + " minutes, ");
					// // System.out.print(diffSeconds + " seconds.");*/
				}
				
				teacherList.add(tt);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		/*for(Date d=startDate;d.before(endDate) || d.equals(endDate); d.setDate(d.getDate()+1))
		{
			EmpInfo tt = new EmpInfo();
			String arr[]=name.split("-");
			String teacherId=arr[1];
			teacherInfo = dbr.enteryList(conn,d,teacherId);
			tt.setName(teacherInfo.getName());
			tt.setInTime(teacherInfo.getInTime());
			tt.setOutTime(teacherInfo.getOutTime());
			teacherList.add(tt);
		}*/
		
		
	}
	
	public ArrayList<EmpInfo> getTeacherDetails() {
		return teacherDetails;
	}
	public void setTeacherDetails(ArrayList<EmpInfo> teacherDetails) {
		this.teacherDetails = teacherDetails;
	}
	
	public DatabaseMethods1 getObj() {
		return obj;
	}
	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}
	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<EmpInfo> getIdWiseList() {
		return idWiseList;
	}
	public void setIdWiseList(ArrayList<EmpInfo> idWiseList) {
		this.idWiseList = idWiseList;
	}
	public EmpInfo getTeacherInfo() {
		return teacherInfo;
	}
	public void setTeacherInfo(EmpInfo teacherInfo) {
		this.teacherInfo = teacherInfo;
	}
	public ArrayList<EmpInfo> getTeacherList() {
		return teacherList;
	}
	public void setTeacherList(ArrayList<EmpInfo> teacherList) {
		this.teacherList = teacherList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
