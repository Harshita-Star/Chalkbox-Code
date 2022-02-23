package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import tc_module.DataBaseMethodsTcModule;
@ManagedBean(name="rbscStruckOffListBean")
@ViewScoped

public class RBSCStruckOffListBean implements Serializable
{
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent = new StudentInfo();
	String schid,session;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
	
	public RBSCStruckOffListBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().rbscStruckOffList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reactive()
	{
		int id=selectedStudent.getId();
		String addNumber = selectedStudent.getAddNumber();
		Connection conn = DataBaseConnection.javaConnection();
		
		schid=obj.schoolId();
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);

		obj.updateStudentStatus("ACTIVE", addNumber, conn);
		//obj.activateUser(addNumber,conn);
		objTc.deleteStruckOffInformationRbscc(id, conn);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Activated Successfully"));
		studentList=new DatabaseMethods1().rbscStruckOffList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}


}
