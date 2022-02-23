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

import session_work.RegexPattern;

@ManagedBean(name="evdWebsite")
@ViewScoped
public class ViewEditDeleteWebsiteBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SchoolInfo> dataList;
	SchoolInfo selectedRow;
	String schoolName,id,contactNo,contactName;
	Date startDate=new Date(),expireDate,renewalDate;
	double chalkBoxAmount,imgAmount,chalkBoxRenewal,imgRenewalAmount;
	String oldSchoolName,oldStartDateStr,oldExpireDateStr,oldRenewalDateStr,oldContactNo,oldContactName;
	double oldChalkBoxAmount,oldImgAmount,oldChalkBoxRenewal,oldImgRenewalAmount;


	public ViewEditDeleteWebsiteBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		dataList=new DatabaseMethods1().allWebsiteList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editSelectedRow()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		id=selectedRow.getId();
		schoolName=selectedRow.getSchoolName();
		startDate=selectedRow.getStartDate();
		expireDate=selectedRow.getExpireDate();
		renewalDate=selectedRow.getRenewalDate();
		chalkBoxAmount=selectedRow.getChalkBoxAmount();
		imgAmount=selectedRow.getImgAmount();
		chalkBoxRenewal=selectedRow.getChalkBoxRenewal();
		imgRenewalAmount=selectedRow.getImgRenewalAmount();

		oldSchoolName=selectedRow.getSchoolName();
		oldStartDateStr=sdf.format(selectedRow.getStartDate());
		oldExpireDateStr=sdf.format(selectedRow.getExpireDate());
		oldRenewalDateStr=sdf.format(selectedRow.getRenewalDate());
		oldChalkBoxAmount=selectedRow.getChalkBoxAmount();
		oldImgAmount=selectedRow.getImgAmount();
		oldChalkBoxRenewal=selectedRow.getChalkBoxRenewal();
		oldImgRenewalAmount=selectedRow.getImgRenewalAmount();
	}

	public void updateDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.updateWebsite(schoolName,startDate, expireDate, renewalDate, chalkBoxAmount,
				imgAmount, chalkBoxRenewal, imgRenewalAmount, id,contactNo,contactName,conn);
		if(i==1)
		{
			dataList=obj.allWebsiteList(conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Website Updated Sucessfully"));
			schoolName="";chalkBoxAmount=chalkBoxRenewal=imgAmount=imgRenewalAmount=0;
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

	public void deleteWebsite()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().deleteWebsite(selectedRow.getId(),conn);
		if(i==1)
		{
			dataList=new DatabaseMethods1().allWebsiteList(conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Website Deleted Sucessfully"));
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
	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}
	public SchoolInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(SchoolInfo selectedRow) {
		this.selectedRow = selectedRow;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
