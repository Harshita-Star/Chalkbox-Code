package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="generateLoginCredentials")
@ViewScoped
public class GenerateLoginCredentialsBean implements Serializable{
	
	
	ArrayList<StudentInfo>list=new ArrayList<>();
	ArrayList<StudentInfo>selectedStudents=new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public GenerateLoginCredentialsBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = DBM.schoolId();
		sessionValue = DBM.selectedSessionDetails(schoolId,conn);
		list=DBM.studentHavingNoLogins(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	public void generate()
	{
		Connection conn = DataBaseConnection.javaConnection();
	
		if(selectedStudents.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select Atleast 1 Student"));	
		}
		else
		{

		 for(StudentInfo vv : selectedStudents)
		 {
			 
			  String password = DBM.randomAlphaNumeric(8);
			  DBM.insertEmployeeInLogin(vv.getAddNumber(), password, "student", schoolId, conn);
			
			  String refNo2;
				try {
					refNo2=addWorkLog(conn,vv.getAddNumber(), password, "student");
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		 }
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Logins Generated"));
		 list=DBM.studentHavingNoLogins(conn);
		 selectedStudents = new ArrayList<StudentInfo>();
		 
		} 
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	public String addWorkLog(Connection conn,String addNumber,String pasword,String type )
	{
	    String value = "";
		String language= "";
		
		value = addNumber+" -- "+pasword+" -- "+type;
		
		String refNo = workLg.saveWorkLogMehod(language,"Generate Login Credentials","WEB",value,conn);
		return refNo;
	}


	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}





	public ArrayList<StudentInfo> getSelectedStudents() {
		return selectedStudents;
	}





	public void setSelectedStudents(ArrayList<StudentInfo> selectedStudents) {
		this.selectedStudents = selectedStudents;
	}


	
	

}
