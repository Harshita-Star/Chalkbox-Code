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

@ManagedBean(name="inactiveSchoolReport")
@ViewScoped

public class InactiveSchoolReportBean implements Serializable
{
	ArrayList<SchoolInfo> list = new ArrayList<>();
	SchoolInfo selected = new SchoolInfo();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,session;
	
	public InactiveSchoolReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		list = dbm.allSchoolInactive(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reactive()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.activateSchool(selected.getId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Activated Successfully!"));
			list = dbm.allSchoolInactive(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong. Please Try Again!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
