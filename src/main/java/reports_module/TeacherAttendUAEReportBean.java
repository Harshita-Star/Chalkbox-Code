package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;

@ManagedBean(name="teacherAttendanceUAEReport")
@ViewScoped
public class TeacherAttendUAEReportBean implements Serializable{

	Date date = new Date();
	EmpInfo enteryList = new EmpInfo();
	ArrayList<EmpInfo>teacherDetails = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,session;
	
	public TeacherAttendUAEReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		date = new Date();
		show();
	}
	
	public void show()
	{
		Connection conn= DataBaseConnection.javaConnection();
		teacherDetails=obj.allEmpInfo(conn);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date d1 = null;
		Date d2 = null;
		try
		{
			for(EmpInfo tt :teacherDetails)
			{
				enteryList = dbr.enteryList(conn,date,tt.getId());	
				tt.setInTime(enteryList.getInTime());
				tt.setOutTime(enteryList.getOutTime());
				
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
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
}
