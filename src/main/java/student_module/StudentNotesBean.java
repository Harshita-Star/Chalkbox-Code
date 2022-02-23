package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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

@ManagedBean(name="studentNotesBean")
@ViewScoped

public class StudentNotesBean implements Serializable
{
	ArrayList<StudentInfo> list = new ArrayList<>();
	StudentInfo selected = new StudentInfo();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	String schid;
	
	public StudentNotesBean() 
	{
		//// // System.out.println("print");
		Connection conn=DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		schid = obj.schoolId();

		StudentInfo info =DBJ.studentDetailslistByAddNo(studentid, schid,conn);
		String selectedSection=info.getSectionid();
		String selectedCLassSection=info.getClassId();
		list=DBJ.viewStudentAssignmentNotes(selectedSection,schid,conn,studentid,selectedCLassSection,"student");

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=DBJ.fullSchoolInfo(schid,conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getAss1()+"')");

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
}
