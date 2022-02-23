package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="studentHomework")
@ViewScoped

public class StudentHomeworkBean implements Serializable
{
	ArrayList<StudentInfo> list = new ArrayList<>();
	StudentInfo selected = new StudentInfo();
	Date selectedDay;
	ArrayList<String> fileList = new ArrayList<>();
	String selected1;
	String schid;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	public StudentHomeworkBean()
	{
		schid = obj.schoolId();
		selectedDay = new Date();
		search();
	}

	public void search()
	{
		
		

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		
		if(studentid == null)
		{
			ss.invalidate();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("ChalkboxLogin.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		else
		{	
		Connection conn=DataBaseConnection.javaConnection();	
		StudentInfo info =DBJ.studentDetailslistByAddNo(studentid, schid,conn);
		String selectedSection=info.getSectionid();
		String selectedCLassSection=info.getClassId();
		list=DBJ.viewStudentAssignmentHW(selectedSection,selectedDay,schid,conn,studentid,selectedCLassSection);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}

	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		//		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);
		//		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getAss1()+"')");
		String arr1[]=selected.getAss1().split(",");
		fileList=new ArrayList<>();
		for(int i=0;i<arr1.length;i++)
		{
			fileList.add(arr1[i]);
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void view1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=DBJ.fullSchoolInfo(schid,conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected1+"')");

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public ArrayList<String> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<String> fileList) {
		this.fileList = fileList;
	}

	public String getSelected1() {
		return selected1;
	}

	public void setSelected1(String selected1) {
		this.selected1 = selected1;
	}


}
