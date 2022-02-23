package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="feeLock")
@ViewScoped

public class FeeLockBean implements Serializable
{
	Date date;
	String info;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	String schid="";
	public FeeLockBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schid,conn);
		
		Date lockDate = dbm.checkLockDate(schid,conn);

		if(lockDate == null)
		{
			info = "Note : You haven't lock your fees modifications till now.";
		}
		else
		{
			String strDt = new SimpleDateFormat("dd-MM-yyyy").format(lockDate);
			info = "Note : You have already locked your fees modifications upto Date : "+strDt;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = dbm.lockFees(schid,date,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee Locked Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong.Please try Again"));
		}
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
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		language = strDate;
		value = strDate;
	
		String refNo = workLg.saveWorkLogMehod(language,"Fee Lock","WEB",value,conn);
		return refNo;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
