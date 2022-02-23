package tc_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="searchStdForTc")
@ViewScoped
public class SearchStudentForTCBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	String schid,session;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public SearchStudentForTCBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		studentList=objTc.searchStudentListForAllTC(schid,session,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editNow() throws Exception
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("TCDetail", selectedStudent);
		FacesContext.getCurrentInstance().getExternalContext().redirect("tcIssue.xhtml");
	}


	public void reactive()
	{
		int id=selectedStudent.getId();
		String addNumber = selectedStudent.getAddNumber();
		Connection conn = DataBaseConnection.javaConnection();

		obj.updateStudentStatus("ACTIVE", addNumber, conn);
		String refNo;
		try {
			refNo=addWorkLog(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		obj.activateUser(addNumber,conn);
		String refNo2;
		try {
			refNo2=addWorkLog2(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		objTc.deleteStruckOffInformation(id, conn);
		String refNo3;
		try {
			refNo3=addWorkLog3(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Activated Successfully"));
		studentList = objTc.searchStudentListForAllTC(schid,session,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = selectedStudent.getAddNumber(); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Student Reactive Update Status","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = selectedStudent.getAddNumber(); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Student Reactive Activate User","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = String.valueOf(selectedStudent.getId()); 

		
		String refNo = workLg.saveWorkLogMehod(language,"Student Reactive Delete Struck off","WEB",value,conn);
		return refNo;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
}
