package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="incExpSetting")
@ViewScoped

public class IncExpSettingBean implements Serializable
{
	int incTax,expTax;
	
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,session;
	DatabaseMethodWorkLog wlg = new DatabaseMethodWorkLog();

	
	public IncExpSettingBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		incTax = dbr.incomeTaxValue(schoolId,session,"inc_tax",conn);
		expTax = dbr.incomeTaxValue(schoolId,session,"exp_tax",conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void add()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		int i = 0;
		boolean check = dbr.checkTaxSettings(schoolId, session, conn);
		if(check)
		{
			i = dbr.updateIncExpTaxSetting(schoolId, session, incTax, expTax, conn);
		}
		else
		{
			i = dbr.addIncExpTaxSetting(schoolId, session, incTax, expTax, conn);
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fc.addMessage(null, new FacesMessage("Tax Settings Updated Successfully!"));
		}
		else
		{
			fc.addMessage(null, new FacesMessage("Something went wrong.Please Try Again."));
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
		
		
	    value = "Tax Percentage (Income)-"+incTax+" --Tax Percentage (Expense)-"+expTax;
		
		String refNo = wlg.saveWorkLogMehod(language,"Income expense Tax Settings","WEB",value,conn);
		return refNo;
	}

	public int getIncTax() {
		return incTax;
	}

	public void setIncTax(int incTax) {
		this.incTax = incTax;
	}

	public int getExpTax() {
		return expTax;
	}

	public void setExpTax(int expTax) {
		this.expTax = expTax;
	}
	
	
}
