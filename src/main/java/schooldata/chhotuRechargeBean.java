package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="chootuRecharge")
@ViewScoped
public class chhotuRechargeBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	Double quantity;
	String schoolId;
	String strRandomOtp,otpInput,contactNo;
	int randomOtp;

	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public chhotuRechargeBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		contactNo=new DatabaseMethods1().contactNo("Chotu Recharge",conn);
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

		int j=new DatabaseMethods1().checkChhotuRecharge(conn,0,schoolId,"approved");
		if(j==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chhotu Recharge is already exist for this schoolid!Please try again..."));
		}
		else
		{
			int i=new DatabaseMethods1().checkChhotuRechargeInMessageTransaction(conn,schoolId);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chhotu Recharge is already exist for this schoolid!Please try again..."));
			}
			else
			{
				PrimeFaces.current().executeInitScript("PF('addSchool').show()");
				PrimeFaces.current().ajax().update("form4");
				strRandomOtp = "";
				randomOtp = (int)(Math.random()*9000)+1000;
				strRandomOtp = String.valueOf(randomOtp);
				String schName = DBM.getSMSSchoolName(schoolId, conn);
				String typemessage="Hello Admin,\n"
						+String.valueOf(randomOtp)+ " is your OTP for Chhotu Recharge - "+schName+".\nRegards.\nCB";

				new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
				//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			}

		}


		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public void recharge()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(otpInput.equals(strRandomOtp))
		{
			int i = new DatabaseMethods1().purchaseChotuAllValuesInTable(conn,schoolId,"Chhotu Recharge","5000","approved","unpaid");
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chhotu Recharge successfully!"));
				new DatabaseMethods1().chhotuRecharge(schoolId, new Date(), 5000, conn);
				//packList=new DatabaseMethods1().selectAllMessagePackValues(conn);
				schoolId="";
				otpInput="";
				strRandomOtp="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Chhotu Recharge!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}



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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}


