package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="blockedParentLogin")
@ViewScoped

public class BlockedParentLoginBean implements Serializable
{
	ArrayList<EmployeeInfo>teacherList;
	List<EmployeeInfo> selectStudent;
	EmployeeInfo selectedEmployee;
	int total;
	String username,password;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public BlockedParentLoginBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		teacherList=obj.allBlockedStudentLoginList(schoolId,conn);
		total=teacherList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void activateUser()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.activateUser(selectedEmployee.getEmplyeeuserid(),conn);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog(conn);
			} catch (Exception e) {
		        e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("User Activated Successfully"));
			teacherList=obj.allBlockedStudentLoginList(schoolId,conn);
			total=teacherList.size();
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void activateUsers() {
		Connection conn = DataBaseConnection.javaConnection();
		if(selectStudent.isEmpty() == true) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please at least one student to active", "Please at least one student to active"));
		}else {
			int status = obj.activeUsersMultiple(selectStudent,conn);
			if(status>0) {
				String refNo2;
				try {
					refNo2=addWorkLogg(conn);
				} catch (Exception e) {
			        e.printStackTrace();
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("User Activated Successfully"));
				teacherList=obj.allBlockedStudentLoginList(schoolId,conn);
				total=teacherList.size();
				selectStudent=null;
			}else {
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value = selectedEmployee.getEmplyeeuserid();
		
		String refNo = workLg.saveWorkLogMehod(language,"Blocked Parent Login","WEB",value,conn);
		return refNo;
	}
	

	public String addWorkLogg(Connection conn)
	{
	    String value = "";
		String language= "";
		String refNo="";
		
	   for(EmployeeInfo x : selectStudent) {
		   value = x.getEmplyeeuserid();
			
			refNo = workLg.saveWorkLogMehod(language,"Blocked Parent Login","WEB",value,conn);
	   }
		return refNo;
	}


	public ArrayList<EmployeeInfo> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<EmployeeInfo> teacherList) {
		this.teacherList = teacherList;
	}

	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public List<EmployeeInfo> getSelectStudent() {
		return selectStudent;
	}

	public void setSelectStudent(List<EmployeeInfo> selectStudent) {
		this.selectStudent = selectStudent;
	}
	
}
