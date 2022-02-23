package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;


@ManagedBean(name = "showAccountLedger")
@ViewScoped
public class ShowAccountLedgerBean implements Serializable
{

	String schId,financialYear,headName,accountName,address1,address2,pincode,state;
	ArrayList<SelectItem> childHeadList= new ArrayList<>();
	ArrayList<String> list = new ArrayList<>();
	ArrayList<UserInfo> statementList = new ArrayList<>();
	ArrayList<UserInfo> statementListBank = new ArrayList<>();
	ArrayList<UserInfo> statementListBill = new ArrayList<>();
	ArrayList<UserInfo> statementListAll = new ArrayList<>();
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	double tCredit=0.0,tDebit=0.0;
	DataBase dtb = new DataBase();

	
	public ShowAccountLedgerBean()
	{
		//	HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//    financialYear=(String) ss1.getAttribute("financialYear");
		//    schId = (String) ss1.getAttribute("schId");
		Connection conn=DataBaseConnection.javaConnection();
		schId = obj1.schoolId();
		financialYear =obj1.selectedSessionDetails(schId,conn);
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


	public String search()
	{
		statementListAll=new ArrayList<>();
		String code[]=accountName.split("-");

		Connection con = DataBaseConnection.javaConnection();

		String	accountId=obj1.employeeAccountNoByEmployeeId(code[code.length-1], con);


		statementList = dtb.statementDetailsAccountDtForSalary(accountId,schId,financialYear,con);
		statementListBank = dtb.statementDetailsAccountBankDtForSalary(accountId,schId,financialYear,con);
		//statementListBill = new DataBase().statementDetailsAccountBillForSalary(accountName,schId,financialYear,con);
		statementListAll.addAll(statementListBill);
		statementListAll.addAll(statementList);
		statementListAll.addAll(statementListBank);

		Collections.sort(statementListAll);
		int i = 1;
		for(UserInfo ff:statementListAll)
		{
			ff.setSno(i++);
			tCredit=tCredit+ff.getCreditAmt();
			tDebit=tDebit+ff.getDebitAmt();

		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "showAccountLedger.xhtml";
	}



	public ArrayList<SelectItem> getChildHeadList() {
		return childHeadList;
	}

	public void setChildHeadList(ArrayList<SelectItem> childHeadList) {
		this.childHeadList = childHeadList;
	}

	public String getHeadName() {
		return headName;
	}


	public void setHeadName(String headName) {
		this.headName = headName;
	}


	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	public String getAddress1() {
		return address1;
	}


	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	public String getAddress2() {
		return address2;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	public String getPincode() {
		return pincode;
	}


	public void setPincode(String pincode) {
		this.pincode = pincode;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public ArrayList<UserInfo> getStatementList() {
		return statementList;
	}


	public void setStatementList(ArrayList<UserInfo> statementList) {
		this.statementList = statementList;
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


	public ArrayList<UserInfo> getStatementListAll() {
		return statementListAll;
	}


	public void setStatementListAll(ArrayList<UserInfo> statementListAll) {
		this.statementListAll = statementListAll;
	}



}