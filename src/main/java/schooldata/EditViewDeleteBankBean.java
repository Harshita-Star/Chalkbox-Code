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

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="editViewDeleteBank")
@ViewScoped

public class EditViewDeleteBankBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	Date entryDate, chkDate=new Date();
	String description="",bankBranch="",id,bankName="",accNo="",bankAddress="",bankCode="",bankIFSC="";
	BankInfo selectedBank;
	ArrayList<BankInfo> bank_info;
	double cashBalance=0, totalIn, totalOut, closingBalance;

	public EditViewDeleteBankBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		bank_info = obj.allBankInfo(conn);
		cashBalance=obj.remainingCompanyCapitalAmount(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//	cashBalance=Double.parseDouble(new DecimalFormat("##.##").format(cashBalance));
	}

	public void editDetails()
	{
		entryDate=selectedBank.getEntry_date();
		bankName = selectedBank.getBank_name();
		bankBranch = selectedBank.getBank_branch();
		bankAddress = selectedBank.getAddress();
		accNo=selectedBank.getAcc_no();
		bankCode=selectedBank.getBank_code();
		bankIFSC=selectedBank.getBank_IFSC();
		description=selectedBank.getDescription();
		id =  selectedBank.id;

	}

	/*public void findClosingCashBalance()
	{
		totalIn=totalOut=closingBalance=0;
		totalIn=Double.parseDouble(new DecimalFormat("##.##").format(new DataBase().closingCashInBalance(chkDate)));
		totalOut=Double.parseDouble(new DecimalFormat("##.##").format(new DataBase().closingCashOutBalance(chkDate)));
		closingBalance=totalIn-totalOut;
	}*/

	public void update()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int n = obj.updateBank(id,entryDate,bankName,bankBranch,bankAddress,accNo,bankCode,bankIFSC,description,conn);
		if(n>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Bank Updated Successfully"));
			bank_info = obj.allBankInfo(conn);

			PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
			PrimeFaces.current().ajax().update("form3");
			PrimeFaces.current().ajax().update("instlmnt");
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("An Error Occured...Please Try Again"));
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
		
		value = "Id-"+id+" --Date-"+addDt+" --Bank Name-"+bankName+" --Bank branch-"+bankBranch+" --Address-"+bankAddress+" --AccNo-"+accNo+" --Bank Code-"+bankCode+" --Bank IFSC-"+bankIFSC+" --Description-"+description;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Bank","WEB",value,conn);
		return refNo;
	}
	

	public double getCashBalance() {
		return cashBalance;
	}

	public void setCashBalance(double cashBalance) {
		this.cashBalance = cashBalance;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public BankInfo getSelectedBank() {
		return selectedBank;
	}

	public void setSelectedBank(BankInfo selectedBank) {
		this.selectedBank = selectedBank;
	}

	public ArrayList<BankInfo> getBank_info() {
		return bank_info;
	}

	public void setBank_info(ArrayList<BankInfo> bank_info) {
		this.bank_info = bank_info;
	}

	public Date getChkDate() {
		return chkDate;
	}

	public void setChkDate(Date chkDate) {
		this.chkDate = chkDate;
	}

	public double getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(double totalIn) {
		this.totalIn = totalIn;
	}

	public double getTotalOut() {
		return totalOut;
	}

	public void setTotalOut(double totalOut) {
		this.totalOut = totalOut;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
