package schooldata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="bankBook")
@ViewScoped

public class PrintBankBookBean implements Serializable
{
	ArrayList<UserInfo> inList=new ArrayList<>(),outList=new ArrayList<>();
	ArrayList<UserInfo> finalList=new ArrayList<>();
	double cashIn,cashOut,bankIn,bankOut, balance, openingBalance,closingBalance;
	String date, bank, bankName;
	Date dt;
	ArrayList<UserInfo> cList=new ArrayList<>();
	DecimalFormat df=new DecimalFormat("##.##");

	public PrintBankBookBean() throws SQLException
	{
		Connection conn=DataBaseConnection.javaConnection();
		inList=new ArrayList<>();outList=new ArrayList<>();

		cashIn=cashOut=bankIn=bankOut=0;
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		inList=(ArrayList<UserInfo>) ss.getAttribute("inlist");
		outList=(ArrayList<UserInfo>) ss.getAttribute("outList");
		date=(String) ss.getAttribute("date");
		bank=(String) ss.getAttribute("bank");
		openingBalance=(double) ss.getAttribute("balance");
		dt=(Date) ss.getAttribute("dt");



		bankName=new DatabaseMethods1().bankNameById(bank,conn);
		int i=1;

		UserInfo kk=new UserInfo();

		kk.setEntryDate(dt);
		kk.setType("in");

		kk.setEntryDateStr(new SimpleDateFormat("dd/MM/yyyy").format(dt));
		kk.setStatus("Opening Balance");
		kk.setAmount(openingBalance);
		////// // System.out.println("open amt: "+openingBalance);
		//kk.setCashInAmount(openingBalance);

		cList.add(kk);

		for(int j=0;j<inList.size();j++)
		{
			inList.get(j).setType("in");
		}

		for(int j=0;j<outList.size();j++)
		{
			outList.get(j).setType("out");
		}

		finalList.addAll(cList);
		finalList.addAll(inList);
		finalList.addAll(outList);


		Collections.sort(finalList);

		balance=0;
		for(UserInfo uu: finalList)
		{
			uu.setSno(i++);
			String payMode="";
			payMode=uu.getPayMode();
			if(payMode==null || payMode.equals(""))
			{

			}
			else if(payMode.equalsIgnoreCase("cash"))
			{
				payMode="Transfer";
			}
			uu.setStatus(uu.getStatus()+"- "+uu.getLoanNo()+"- "+payMode+"- "+uu.getChq_ddNo());
			if(uu.getType().equals("out"))
			{
				uu.setBankOutAmount(uu.getAmountOut());
				uu.setAmountOut(0);
				bankOut+=uu.getBankOutAmount();
				balance=balance-uu.getBankOutAmount();
				uu.setClosingBalance(BigDecimal.valueOf(balance).toPlainString());
			}
			else
			{
				uu.setBankInAmount(uu.getAmount());
				uu.setAmount(0);
				bankIn+=uu.getBankInAmount();


				balance=balance+uu.getBankInAmount();
				uu.setClosingBalance(BigDecimal.valueOf(balance).toPlainString());
			}

		}




		/*for(UserInfo uu : inList)
		{
			uu.setSno(i++);
			uu.setStatus(uu.getStatus()+"- "+uu.getLoanNo()+"- "+uu.getFirst_applicantName());
			if((uu.getTransferredToBankName()==null || uu.getTransferredToBankName().equals("")))
			{
				uu.setCashInAmount(uu.getAmount());
				uu.setAmount(0);
				cashIn+=uu.getCashInAmount();
			}
			else
			{
				uu.setBankInAmount(uu.getAmount());
				uu.setAmount(0);
				bankIn+=uu.getBankInAmount();
			//}
		}

		int k=1;
		for(UserInfo vv : outList)
		{
			vv.setSno(k++);
			vv.setStatus(vv.getStatus()+"- "+vv.getLoanNo()+"- "+vv.getFirst_applicantName());
			if(vv.getPayMode().equalsIgnoreCase("cash"))
			{
				vv.setCashOutAmount(vv.getAmountOut());
				vv.setAmountOut(0);
				cashOut+=vv.getCashOutAmount();
			}
			else
			{
				vv.setBankOutAmount(vv.getAmountOut());
				vv.setAmountOut(0);
				bankOut+=vv.getBankOutAmount();
			//}
		}*/
		/*cashIn=Double.valueOf(df.format(cashIn));
		cashOut=Double.valueOf(df.format(cashOut));*/
		bankIn=Double.valueOf(df.format(bankIn));
		bankOut=Double.valueOf(df.format(bankOut));
		////// // System.out.println("bank in: "+bankIn+" bank out: "+bankOut);
		//Collections.sort(inList);
		//Collections.sort(outList);

		closingBalance=Double.parseDouble(df.format(bankIn-bankOut));

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<UserInfo> getInList() {
		return inList;
	}

	public void setInList(ArrayList<UserInfo> inList) {
		this.inList = inList;
	}

	public ArrayList<UserInfo> getOutList() {
		return outList;
	}

	public void setOutList(ArrayList<UserInfo> outList) {
		this.outList = outList;
	}

	public double getBankIn() {
		return bankIn;
	}

	public void setBankIn(double bankIn) {
		this.bankIn = bankIn;
	}

	public double getBankOut() {
		return bankOut;
	}

	public void setBankOut(double bankOut) {
		this.bankOut = bankOut;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<UserInfo> getFinalList() {
		return finalList;
	}

	public void setFinalList(ArrayList<UserInfo> finalList) {
		this.finalList = finalList;
	}

	public double getCashIn() {
		return cashIn;
	}

	public void setCashIn(double cashIn) {
		this.cashIn = cashIn;
	}

	public double getCashOut() {
		return cashOut;
	}

	public void setCashOut(double cashOut) {
		this.cashOut = cashOut;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(double openingBalance) {
		this.openingBalance = openingBalance;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public Date getDt() {
		return dt;
	}

	public void setDt(Date dt) {
		this.dt = dt;
	}

	public ArrayList<UserInfo> getcList() {
		return cList;
	}

	public void setcList(ArrayList<UserInfo> cList) {
		this.cList = cList;
	}





}
