package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;
import session_work.RegexPattern;

@ManagedBean(name="addServiceExecutive")
@ViewScoped

public class AddServiceExecutiveBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<EmpInfo> empList = new ArrayList<>();
	EmpInfo selected = new EmpInfo();
	String name,mobile,username,password;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId,sessionValue; 
	
	public AddServiceExecutiveBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schoolId, conn);
		empList = dbr.allServiceExecutiveList("all",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String oldunm = username;
		username=username+"-service@chalkbox";
		boolean check=obj1.checkLoginDuplicacy(username,conn);
		if(check==false)
		{
			password = obj1.randomAlphaNumeric(8);
			int i = dbr.executiveAction("add",name,mobile,username,password,"","",conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Executive Added Successfully!"));
				obj1.addAdminInAllUser(username,password,"service_executive",conn,mobile,"");
				empList = dbr.allServiceExecutiveList("all",conn);
				name = mobile = username = oldunm = "";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username : "+username+" ,already exist please "
					+ "choose another one! "));
			username = oldunm;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.executiveAction("delete", "", "", "", "", selected.getId(), "", conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Executive Blocked Successfully!"));
			obj1.blockExecutive(selected.getUname(), conn);
			empList = dbr.allServiceExecutiveList("all",conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reactive()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.executiveAction("active", "", "", "", "", selected.getId(), "", conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Executive Activated Successfully!"));
			obj1.activateExecutive(selected.getUname(), conn);
			empList = dbr.allServiceExecutiveList("all",conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void edit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.executiveAction("edit", selected.getName(), selected.getMobile(), "", "", selected.getId(), "", conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Executive Updated Successfully!"));
			dbr.updateMobileInAllUser(selected.getUname(), selected.getMobile(), conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<EmpInfo> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<EmpInfo> empList) {
		this.empList = empList;
	}

	public EmpInfo getSelected() {
		return selected;
	}

	public void setSelected(EmpInfo selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
	
}
