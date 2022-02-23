package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodsIncome_Expense;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;


@ManagedBean(name = "addTransactionDetails")
@ViewScoped
public class AddTransactionDetailsBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String accountName,billType,creditType,description,paymentMode,ifscCode,bankName,chequeNo,schId,financialYear;
	double amount;
	Date chequeDate,currentDate;
	ArrayList<String> list = new ArrayList<>();
	ArrayList<SelectItem> bankList = new ArrayList<>();
	boolean chequeRen,editCreditType=false,billRender=false,billRenderOther=false;
	Method obj=new Method();
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DataBaseMethodsIncome_Expense dbExpense = new DataBaseMethodsIncome_Expense();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBase dtb = new DataBase();
	
	public AddTransactionDetailsBean()
	{
		chequeDate=currentDate=new Date();
		creditType="cr";
		Connection conn = DataBaseConnection.javaConnection();
		
		schId = obj1.schoolId();
		financialYear = obj1.selectedSessionDetails(schId,conn);
		bankList = dbExpense.allBankList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public List<String> completeText(String name)
	{
		Connection conn=DataBaseConnection.javaConnection();

		list=dtb.AccountList(name,schId,financialYear,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String checkBillType()
	{
		paymentMode="";
		if(billType.equals("cash"))
		{
			billRender = true;
			billRenderOther=false;
			chequeRen=false;
		}
		else
		{
			billRender = false;
			chequeRen=false;
			billRenderOther=true;
		}
		return "addTransaction.xhtml";
	}

	public String chequeRender()
	{
		if(paymentMode.equals("cheque"))
		{
			chequeRen = true;
		}
		else
		{
			chequeRen=false;
		}
		return "addTransaction.xhtml";
	}

	public String insertDetails()
	{
		int r=0;
		Connection con = DataBaseConnection.javaConnection();
		String code[]=accountName.split("-");

		String accountId=obj1.employeeAccountNoByEmployeeId(code[code.length-1], con);
		if(billType.equals("cash"))
		{
			if(paymentMode.equals("cash"))
			{
				r =  obj.insertCashBokDetails(accountId,creditType,currentDate,amount,description,con);
			}
			else
			{
				r = obj.insertBankBokDetails(accountId,creditType,currentDate,amount,description,chequeNo,chequeDate,ifscCode,bankName,con);
			}
		}
		else
		{
			r = obj.insertInvoiceAndBill(accountId,creditType,currentDate,amount,description,billType,con);
		}

		if(r==1)
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Transaction SuccessFully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Error Occured"));
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "addTransactionDetail.xhtml";
	}







	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public boolean isChequeRen() {
		return chequeRen;
	}

	public void setChequeRen(boolean chequeRen) {
		this.chequeRen = chequeRen;
	}

	public boolean isEditCreditType() {
		return editCreditType;
	}

	public void setEditCreditType(boolean editCreditType) {
		this.editCreditType = editCreditType;
	}

	public boolean isBillRender() {
		return billRender;
	}

	public void setBillRender(boolean billRender) {
		this.billRender = billRender;
	}

	public boolean isBillRenderOther() {
		return billRenderOther;
	}

	public void setBillRenderOther(boolean billRenderOther) {
		this.billRenderOther = billRenderOther;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}


	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}


	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
