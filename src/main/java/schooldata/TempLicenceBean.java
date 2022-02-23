package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@ManagedBean(name="tempLicence")
@ViewScoped

public class TempLicenceBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	Double quantity;
	String schoolId,days;
	String strRandomOtp,otpInput;
	int randomOtp;
	String contactNo="";

	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public TempLicenceBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		contactNo=new DatabaseMethods1().contactNo("Temp Licence",conn);
		days="7";
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void sendOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String session = DatabaseMethods1.selectedSessionDetails(schoolId,conn);
		String start = String.valueOf(Integer.valueOf(session.split("-")[0])+1);
		String end = String.valueOf(Integer.valueOf(session.split("-")[1])+1);
		String nextSession = start+"-"+end;

		PrimeFaces.current().executeInitScript("PF('addSchool').show()");
		PrimeFaces.current().ajax().update("form4");
		strRandomOtp = "";
		randomOtp = (int)(Math.random()*9000)+1000;
		strRandomOtp = String.valueOf(randomOtp);
		String schName = DBM.getSMSSchoolName(schoolId, conn);
		String typemessage="Hello Admin,\n"
				+String.valueOf(randomOtp)+ " is your OTP to generate Temp Licence for session "+nextSession+" - "+schName+".\nRegards.\nCB";

		new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);


		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();

		String session = DatabaseMethods1.selectedSessionDetails(schoolId,conn);
		String start = String.valueOf(Integer.valueOf(session.split("-")[0])+1);
		String end = String.valueOf(Integer.valueOf(session.split("-")[1])+1);
		String nextSession = start+"-"+end;

		Date today = new Date();
		int dd = Integer.valueOf(days);

		today.setDate(today.getDate()+dd);

		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(today);

		if(otpInput.equals(strRandomOtp))
		{
			int i = new DataBaseMethodsReports().generateTempLicence(schoolId,nextSession,strDate,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Temp Licence Generated successfully!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}

		otpInput="";

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}
	public String getStrRandomOtp() {
		return strRandomOtp;
	}
	public void setStrRandomOtp(String strRandomOtp) {
		this.strRandomOtp = strRandomOtp;
	}
	public String getOtpInput() {
		return otpInput;
	}
	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}
	public int getRandomOtp() {
		return randomOtp;
	}
	public void setRandomOtp(int randomOtp) {
		this.randomOtp = randomOtp;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
