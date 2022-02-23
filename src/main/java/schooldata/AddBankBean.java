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

import session_work.RegexPattern;

@ManagedBean(name="addBank")
@ViewScoped

public class AddBankBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	Date entryDate;
	String amount="0.0",description="",bankBranch="",bankName="",accNo="",bankAddress="",bankCode="",bankIFSC="";

	public AddBankBean()
	{
		entryDate=new Date();
	}

	public void addBankAcc()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int n=new DatabaseMethods1().addBank(entryDate,bankName,bankBranch,bankAddress,accNo,bankCode,bankIFSC,description,amount,conn);
		if(n>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bank Added Successfully!"));
			amount="0.0";
			description=bankBranch=bankName=accNo=bankAddress=bankCode=bankIFSC="";
			entryDate=new Date();
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
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(entryDate);
		
		value = "Date-"+addDt+" --Bank Name-"+bankName+" --Bank branch-"+bankBranch+" --Address-"+bankAddress+" --AccNo-"+accNo+" --Bank Code-"+bankCode+" --Bank IFSC-"+bankIFSC+" --Description-"+description+" --Amount-"+amount;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Bank","WEB",value,conn);
		return refNo;
	}
	

	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getBankAddress() {
		return bankAddress;
	}
	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankIFSC() {
		return bankIFSC;
	}
	public void setBankIFSC(String bankIFSC) {
		this.bankIFSC = bankIFSC;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
