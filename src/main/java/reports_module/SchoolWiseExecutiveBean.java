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
import schooldata.SchoolInfo;

@ManagedBean(name="schoolWiseExecutive")
@ViewScoped

public class SchoolWiseExecutiveBean implements Serializable
{
	String school;
	ArrayList<SchoolInfo> schList,list;
	SchoolInfo selected = new SchoolInfo();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	
	public SchoolWiseExecutiveBean() 
	{
		allSchool();
	}
	
	public void allSchool()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schList=obj.allSchoolListForExecutive(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=dbr.schoolExecutiveList(school, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.executiveSchoolAction("remove", selected.getExecUnm(), "", selected.getId(),conn);
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

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public ArrayList<SchoolInfo> getSchList() {
		return schList;
	}

	public void setSchList(ArrayList<SchoolInfo> schList) {
		this.schList = schList;
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
}
