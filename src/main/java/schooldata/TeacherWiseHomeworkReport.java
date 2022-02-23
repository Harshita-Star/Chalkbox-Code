package schooldata;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
@ManagedBean(name="teacherWiseHomeworkReport")
@ViewScoped
public class TeacherWiseHomeworkReport implements Serializable
{
	ArrayList<SelectItem> employeeList=new ArrayList<>();
	String selectedTeacher="All";
	Date selecteddate=new Date();
	boolean show=false;
	ArrayList<HomeWorkInfo>list=new ArrayList<>();
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	public TeacherWiseHomeworkReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		employeeList=obj.allEmployeeList(conn);
		String hwDate = sdf.format(selecteddate);
		list=obj.teacherwiseAssignmentList(selectedTeacher,hwDate,conn);
		if(list.size()>0)
		{
			show=true;
		}
		else
		{
			show=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String hwDate = sdf.format(selecteddate);
		list=new DatabaseMethods1().teacherwiseAssignmentList(selectedTeacher,hwDate,conn);
		if(list.size()>0)
		{
			show=true;
		}
		else
		{
			show=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<HomeWorkInfo> getList() {
		return list;
	}
	public void setList(ArrayList<HomeWorkInfo> list) {
		this.list = list;
	}
	public ArrayList<SelectItem> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<SelectItem> employeeList) {
		this.employeeList = employeeList;
	}
	public String getSelectedTeacher() {
		return selectedTeacher;
	}
	public void setSelectedTeacher(String selectedTeacher) {
		this.selectedTeacher = selectedTeacher;
	}
	public Date getSelecteddate() {
		return selecteddate;
	}
	public void setSelecteddate(Date selecteddate) {
		this.selecteddate = selecteddate;
	}


}
