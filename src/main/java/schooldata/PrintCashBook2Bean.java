package schooldata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;



@ManagedBean(name="cashBook2")
@ViewScoped

public class PrintCashBook2Bean implements Serializable
{
	ArrayList<UserInfo> inList=new ArrayList<>(),outList=new ArrayList<>();
	ArrayList<UserInfo> finalList=new ArrayList<>();
	ArrayList<UserInfo> finalCashList=new ArrayList<>();
	double cashIn,cashOut,bankIn,bankOut, balanceIn, balanceOut, openingBalance, balance, totalCashIn, totalCashOut, closingBalance, total2;
	String date;
	Date dt;
	ArrayList<UserInfo> cList=new ArrayList<>();
	DecimalFormat df=new DecimalFormat("##.##");

	public PrintCashBook2Bean()
	{

		inList=new ArrayList<>();outList=new ArrayList<>();

		cashIn=cashOut=bankIn=bankOut=openingBalance=0;
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		inList=(ArrayList<UserInfo>) ss.getAttribute("inlist");
		outList=(ArrayList<UserInfo>) ss.getAttribute("outList");
		openingBalance=(double) ss.getAttribute("balance");
		date=(String) ss.getAttribute("date");
		dt=(Date) ss.getAttribute("dt");
		int i=1;

		total2=0;

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

		totalCashIn=totalCashOut=closingBalance=0;
		for(UserInfo uu: finalList)
		{
			uu.setSno(i++);
			////// // System.out.println(uu.getStatus()+"- "+uu.getAmountOut()+" --"+uu.getType());
			uu.setStatus(uu.getStatus()+"- "+uu.getLoanNo());

			if(uu.getType().equals("out"))
			{
				if(uu.getPayMode().equalsIgnoreCase("Cash") || uu.getPayMode().equalsIgnoreCase("Transfer"))
				{
					uu.setCashOutAmount(uu.getAmountOut());

					uu.setAmountOut(0);
					cashOut+=uu.getCashOutAmount();
					//uu.setCashOutAmountStr(BigDecimal.valueOf(uu.getCashOutAmount()).toPlainString());

					balance=balance-uu.getCashOutAmount();
					uu.setClosingBalance(BigDecimal.valueOf(balance).toPlainString());

					totalCashOut+=uu.getCashOutAmount();
					totalCashOut=Double.parseDouble(df.format((totalCashOut)));

				}


			}

			else
			{
				if((uu.getTransferredToBankName()==null || uu.getTransferredToBankName().equals("")))
				{
					////// // System.out.println(uu.getType());
					uu.setCashInAmount(uu.getAmount());
					uu.setAmount(0);
					cashIn+=uu.getCashInAmount();
					//uu.setCashInAmountStr(BigDecimal.valueOf(uu.getCashInAmount()).toPlainString());

					balance=balance+uu.getCashInAmount();
					uu.setClosingBalance(BigDecimal.valueOf(balance).toPlainString());

					totalCashIn+=uu.getCashInAmount();
					totalCashIn=Double.parseDouble(df.format((totalCashIn)));

				}

			}

			// //// // System.out.println(uu.getCashInAmount());

		}

		////// // System.out.println("in size: "+in);
		////// // System.out.println("out size: "+out);
		closingBalance=Double.parseDouble(df.format(totalCashIn-totalCashOut));
		total2=Double.parseDouble(df.format(totalCashOut+closingBalance));


		for(int m=0; m<finalList.size(); m++)
		{
			if(finalList.get(m).getCashInAmount()!=0 || finalList.get(m).getCashOutAmount()!=0)
			{
				finalCashList.add(finalList.get(m));
			}
		}



		/*finalOutList.addAll(outList);
		finalOutList.addAll(cList);
		Collections.sort(finalOutList);*/
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

	public double getBalanceIn() {
		return balanceIn;
	}

	public void setBalanceIn(double balanceIn) {
		this.balanceIn = balanceIn;
	}

	public double getBalanceOut() {
		return balanceOut;
	}

	public void setBalanceOut(double balanceOut) {
		this.balanceOut = balanceOut;
	}



	public ArrayList<UserInfo> getFinalCashList() {
		return finalCashList;
	}

	public void setFinalCashList(ArrayList<UserInfo> finalCashList) {
		this.finalCashList = finalCashList;
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

	public double getTotalCashIn() {
		return totalCashIn;
	}

	public void setTotalCashIn(double totalCashIn) {
		this.totalCashIn = totalCashIn;
	}

	public double getTotalCashOut() {
		return totalCashOut;
	}

	public void setTotalCashOut(double totalCashOut) {
		this.totalCashOut = totalCashOut;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public double getTotal2() {
		return total2;
	}

	public void setTotal2(double total2) {
		this.total2 = total2;
	}




}
