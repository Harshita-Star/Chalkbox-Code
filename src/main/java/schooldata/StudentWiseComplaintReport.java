package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import session_work.RegexPattern;
@ManagedBean(name="studentWiseComplaintReport")
@ViewScoped
public class StudentWiseComplaintReport implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<HomeWorkInfo>list=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	String name;

	public StudentWiseComplaintReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		list=new DatabaseMethods1().studentWiseComplaintReport(id,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<HomeWorkInfo> getList() {
		return list;
	}
	public void setList(ArrayList<HomeWorkInfo> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
