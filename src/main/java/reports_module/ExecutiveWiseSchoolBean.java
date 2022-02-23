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
import schooldata.EmpInfo;
import schooldata.SchoolInfo;

@ManagedBean(name="executiveWiseSchool")
@ViewScoped

public class ExecutiveWiseSchoolBean implements Serializable
{
	String executive;
	ArrayList<EmpInfo> empList = new ArrayList<>();
	ArrayList<SchoolInfo> list = new ArrayList<>();
	SchoolInfo selected = new SchoolInfo();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	
	public ExecutiveWiseSchoolBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		empList = dbr.allServiceExecutiveList("active",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=dbr.executiveSchList(executive, "report", conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.executiveSchoolAction("remove", executive, "", selected.getId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Updated Successfully!"));
			list.remove(selected);
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

	public String getExecutive() {
		return executive;
	}

	public void setExecutive(String executive) {
		this.executive = executive;
	}

	public ArrayList<SchoolInfo> getList() {
		return list;
	}

	public void setList(ArrayList<SchoolInfo> list) {
		this.list = list;
	}

	public SchoolInfo getSelected() {
		return selected;
	}

	public void setSelected(SchoolInfo selected) {
		this.selected = selected;
	}

	public ArrayList<EmpInfo> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<EmpInfo> empList) {
		this.empList = empList;
	}
}
