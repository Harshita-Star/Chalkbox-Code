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

@ManagedBean(name="empWiseBiometricReport")
@ViewScoped

public class EmpWiseBiometricReportBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name;
	Date startDate = new Date();
	Date endDate = new Date();
	ArrayList<EmpInfo>teacherDetails = new ArrayList<>();
	EmployeeInfo teacherInfo=new EmployeeInfo();
	ArrayList<EmpInfo>teacherList = new ArrayList<>();
	ArrayList<EmpInfo>idWiseList = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<EmployeeInfo> employeeList;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String schoolId,sessionValue;
	
	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schoolId, conn);
		employeeList=obj1.searchEmployeebyName(query,conn);
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
		
		String arr[]=name.split("-");
		String teacherId=arr[1];
		
		for(EmployeeInfo info : employeeList)
		{
			if(String.valueOf(info.getId()).equalsIgnoreCase(teacherId))
			{
				teacherInfo = info;
				break;
			}
			
		}
		
		ArrayList<Date> dates = new ArrayList<>();
		long interval = 24*1000 * 60 * 60; // 1 hour in millis
		long endTime1 =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime1) {
			dates.add(new Date(curTime));
			curTime += interval;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try 
		{
			String dd = "";
			for(int j=0;j<dates.size();j++)
			{
				dd = "";
				Date lDate =dates.get(j);
				EmpInfo tt = new EmpInfo();
				
				tt.setName(teacherInfo.getFname());
				if(teacherInfo.getBioCode()!=null && !teacherInfo.getBioCode().equals("") && teacherInfo.getBioDeviceCode()!=null && !teacherInfo.getBioDeviceCode().equals(""))
				{
					dd = dbr.staffBiometricRecord(conn,lDate,teacherInfo.getBioCode(),teacherInfo.getBioDeviceCode());
				}
				tt.setStrDoj(sdf.format(lDate));
				tt.setStatus(dd);
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
