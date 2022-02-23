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

@ManagedBean(name="assignSMSGateway")
@ViewScoped

public class AssignSMSGatewayBean implements Serializable
{
	ArrayList<SchoolInfo> list = new ArrayList<>();
	String defaultGateway = "";
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
    String schoolId,sessionValue; 
	
	public AssignSMSGatewayBean() 
	{
		defaultGateway="gateway1";
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		list = dbm.allSchoolSMSGateway(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbr.assignSMSGateway(list,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SMS Gateway Updated Successfully!"));
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
	
	public void defaultAction()
	{
		for(SchoolInfo ss : list)
		{
			ss.setSmsGateway(defaultGateway);
		}
	}

	public ArrayList<SchoolInfo> getList() {
		return list;
	}

	public void setList(ArrayList<SchoolInfo> list) {
		this.list = list;
	}

	public String getDefaultGateway() {
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway) {
		this.defaultGateway = defaultGateway;
	}
}
