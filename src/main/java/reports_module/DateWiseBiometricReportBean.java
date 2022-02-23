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

@ManagedBean(name="dateWiseBiometricReport")
@ViewScoped

public class DateWiseBiometricReportBean implements Serializable
{
	Date date = new Date();
	String dateStr = "";
	ArrayList<EmpInfo> teacherDetails = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,sessionValue;
	
	public DateWiseBiometricReportBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schoolId=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		date = new Date();
		show();
		
	}
	
	public void show()
	{
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		Connection conn= DataBaseConnection.javaConnection();
		teacherDetails=obj.allEmpInfo(conn);
		try
		{
			ArrayList<String> punchList = new ArrayList<>();
			String dd = "";
			for(EmpInfo tt :teacherDetails)
			{
				//punchList = new ArrayList<>();
				dd="";
				if(tt.getBioCode()!=null && !tt.getBioCode().equals("") && tt.getBioDeviceCode()!=null && !tt.getBioDeviceCode().equals(""))
				{
					//punchList = dbr.staffBiometricRecord(conn,date,tt.getBioCode(),tt.getBioDeviceCode());	
					dd = dbr.staffBiometricRecord(conn,date,tt.getBioCode(),tt.getBioDeviceCode());
				}
				//tt.setPunchList(punchList);
				tt.setStatus(dd);
				
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

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	
}
