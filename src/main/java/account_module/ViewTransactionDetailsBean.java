package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;
@ManagedBean(name="viewTransactionDetails")
@ViewScoped


public class ViewTransactionDetailsBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String accountName,schId,financialYear,strClosingbalance="0.0",strOpeningbalance="0.0";
	ArrayList<UserInfo> statementList = new ArrayList<>();
	ArrayList<UserInfo> statementListBank = new ArrayList<>();
	ArrayList<UserInfo> statementListBill = new ArrayList<>();
	ArrayList<UserInfo> statementListAll = new ArrayList<>();
	ArrayList<String> list = new ArrayList<>();
	DataBase dbObj = new DataBase();
	double openBal=0.0,tCredit=0.0,tDebit=0.0,closeBal=0.0;
	Date startDate = new Date(),endDate = new Date();
	String accountId="";
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBase dtb = new DataBase();


	public ViewTransactionDetailsBean()
	{
		//		HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//		schId = (String) ss1.getAttribute("schId");
		//		financialYear = (String) ss1.getAttribute("financialYear");
		Connection conn=DataBaseConnection.javaConnection();
		schId = obj1.schoolId();
		financialYear = obj1.selectedSessionDetails(schId,conn);
		
		try {
			conn.close();
		} catch (Exception e) {
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




	public String searchDetails()
	{
		statementListAll = new ArrayList<>();
		openBal=tCredit=tDebit=closeBal=0.0;
		Connection con = DataBaseConnection.javaConnection();

		String code[]=accountName.split("-");

		accountId=obj1.employeeAccountNoByEmployeeId(code[code.length-1], con);
		if(startDate.after(endDate))
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Please Select Date Correctly"));
			startDate=endDate=new  Date();
		}
		else
		{
			statementList = dtb.statementDetailsAccountDt(accountId,startDate,endDate,schId,financialYear,con);
			statementListBank = dtb.statementDetailsAccountBankDt(accountId,startDate,endDate,schId,financialYear,con);
			statementListBill = dtb.statementDetailsAccountBillDt(accountId,startDate,endDate,schId,financialYear,con);
			statementListAll.addAll(statementListBill);
			statementListAll.addAll(statementList);
			statementListAll.addAll(statementListBank);

			int i = 1;
			Collections.sort(statementListAll);
			for(UserInfo obj : statementListAll)
			{
				obj.setSno(i++);
				tCredit=tCredit+obj.getCreditAmt();
				tDebit = tDebit+obj.getDebitAmt();
			}
			openBal = dbObj.calculateOpeningBalance("cashbook",accountId,startDate,schId,financialYear,con);
			openBal =openBal+dbObj.calculateOpeningBalance("bankbook",accountId,startDate,schId,financialYear,con);
			openBal =openBal+dbObj.calculateOpeningBalance("invoice",accountId,startDate,schId,financialYear,con);

			if(openBal>0)
			{
				strOpeningbalance= String.valueOf(Math.abs(openBal))+"  "+" Cr";
			}
			else if(openBal<0)
			{
				strOpeningbalance= String.valueOf(Math.abs(openBal))+"  "+" Dr";
			}
			else if(openBal==0)
			{
				strOpeningbalance= String.valueOf(Math.abs(openBal));
			}

			closeBal = openBal+tCredit-tDebit;
			closeBal=Double.parseDouble(new DecimalFormat("##.##").format(closeBal));

			if(closeBal>0)
			{
				strClosingbalance= String.valueOf(Math.abs(closeBal))+"  "+" Cr";
			}
			else if(closeBal<0)
			{
				strClosingbalance= String.valueOf(Math.abs(closeBal))+"  "+" Dr";
			}
			else if(closeBal==0)
			{
				strClosingbalance= String.valueOf(Math.abs(closeBal));
			}

		}

		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "viewTransactionDetails.xhtml";
	}






	public double getOpenBal() {
		return openBal;
	}

	public void setOpenBal(double openBal) {
		this.openBal = openBal;
	}

	public double gettCredit() {
		return tCredit;
	}

	public void settCredit(double tCredit) {
		this.tCredit = tCredit;
	}

	public double gettDebit() {
		return tDebit;
	}

	public void settDebit(double tDebit) {
		this.tDebit = tDebit;
	}

	public double getCloseBal() {
		return closeBal;
	}

	public void setCloseBal(double closeBal) {
		this.closeBal = closeBal;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public ArrayList<UserInfo> getStatementListAll() {
		return statementListAll;
	}
	public void setStatementListAll(ArrayList<UserInfo> statementListAll) {
		this.statementListAll = statementListAll;
	}
	public String getStrClosingbalance() {
		return strClosingbalance;
	}
	public void setStrClosingbalance(String strClosingbalance) {
		this.strClosingbalance = strClosingbalance;
	}
	public String getStrOpeningbalance() {
		return strOpeningbalance;
	}
	public void setStrOpeningbalance(String strOpeningbalance) {
		this.strOpeningbalance = strOpeningbalance;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	



}
