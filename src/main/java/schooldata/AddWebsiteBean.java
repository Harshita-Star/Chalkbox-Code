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

@ManagedBean(name="addWebsite")
@ViewScoped
public class AddWebsiteBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String schoolName,contactNo,contactName;
	Date startDate=new Date(),expireDate,renewalDate;
	double chalkBoxAmount,imgAmount,chalkBoxRenewal,imgRenewalAmount;
	ArrayList<SelectItem>schoolList;
	public AddWebsiteBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void viewEditWebsite()
	{
		PrimeFaces.current().executeInitScript("window.open('viewEditDeleteWebsite.xhtml')");
	}


	public void addWebsite()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		int i=DBM.addWebsite(schoolName,startDate,expireDate,renewalDate,chalkBoxAmount,imgAmount,chalkBoxRenewal,imgRenewalAmount,contactNo,contactName,conn);
		if(i==1)
		{
			DBM.addPayment(schoolName,"Website",startDate,chalkBoxAmount,"debit",conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Website Added Sucessfully"));
			schoolName=contactName=contactNo="";chalkBoxAmount=chalkBoxRenewal=imgAmount=imgRenewalAmount=0;
			startDate=new Date();expireDate=renewalDate=null;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	public Date getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}
	public double getImgAmount() {
		return imgAmount;
	}
	public void setImgAmount(double imgAmount) {
		this.imgAmount = imgAmount;
	}
	public double getChalkBoxRenewal() {
		return chalkBoxRenewal;
	}
	public void setChalkBoxRenewal(double chalkBoxRenewal) {
		this.chalkBoxRenewal = chalkBoxRenewal;
	}
	public double getImgRenewalAmount() {
		return imgRenewalAmount;
	}
	public void setImgRenewalAmount(double imgRenewalAmount) {
		this.imgRenewalAmount = imgRenewalAmount;
	}
	public double getChalkBoxAmount() {
		return chalkBoxAmount;
	}
	public void setChalkBoxAmount(double chalkBoxAmount) {
		this.chalkBoxAmount = chalkBoxAmount;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
