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

@ManagedBean(name="masterFeeLockBean")
@ViewScoped
public class MasterFeeLockBean implements Serializable{


	Date date;
	String info;

	String schid="251";
	public MasterFeeLockBean()
	{

		/*	HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schid = (String) ss.getAttribute("schoolid");
		 */	Connection conn = DataBaseConnection.javaConnection();
		 Date lockDate = new DatabaseMethods1().checkLockDate(schid,conn);

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
		int i = new DatabaseMethods1().lockFees(schid,date,conn);
		if(i>=1)
		{
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
