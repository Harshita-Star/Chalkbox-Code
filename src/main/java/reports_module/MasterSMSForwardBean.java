package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;

@ManagedBean(name="masterSMSForward")
@ViewScoped

public class MasterSMSForwardBean implements Serializable
{
	ArrayList<SchoolInfo> list = new ArrayList<>();
	String preSession,currSession;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,session;
	
	public MasterSMSForwardBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		if (month >= 4) {
			currSession = String.valueOf(year) + "-" + String.valueOf(year + 1);
		} else {
			currSession = String.valueOf(year - 1) + "-" + String.valueOf(year);
		}
		
		int start = Integer.valueOf(currSession.split("-")[0]) - 1;
		int end = Integer.valueOf(currSession.split("-")[1]) - 1;
		
		preSession = start + "-" + end;
		
		list = dbr.allSchoolSMSForward(preSession,currSession,conn);
		//list = dbr.allSchoolSMSForward("2019-2020","2020-2021",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		int i = dbr.forwardSMS(currSession,list,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SMS Forwarded Successfully!"));
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

	public String getPreSession() {
		return preSession;
	}

	public void setPreSession(String preSession) {
		this.preSession = preSession;
	}

	public String getCurrSession() {
		return currSession;
	}

	public void setCurrSession(String currSession) {
		this.currSession = currSession;
	}
}
