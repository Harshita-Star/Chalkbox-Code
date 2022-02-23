package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ledger")
@ViewScoped
public class LedgerBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate=new Date(),endDate=new Date();

	double totalCredit,totalDebit,closingBalance,givenAmount,totalBalance;
	ArrayList<LedgerInfo> dataList;

	public LedgerBean()
	{
		startDate.setDate(1);

	}

	public void searchData()
	{
		Connection conn = DataBaseConnection.javaConnection();
		dataList=new DatabaseMethods1().createLedger(startDate,endDate,conn);
		totalCredit=totalDebit=0;closingBalance=0;
		for(LedgerInfo ll:dataList)
		{
			totalCredit+=ll.getCredit();
			totalDebit+=ll.getDebit();
		}
		closingBalance=totalCredit-totalDebit;
		givenAmount=totalDebit+closingBalance;
		totalBalance=totalDebit-totalCredit;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<LedgerInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<LedgerInfo> dataList) {
		this.dataList = dataList;
	}
	public double getTotalCredit() {
		return totalCredit;
	}
	public void setTotalCredit(double totalCredit) {
		this.totalCredit = totalCredit;
	}
	public double getTotalDebit() {
		return totalDebit;
	}
	public void setTotalDebit(double totalDebit) {
		this.totalDebit = totalDebit;
	}
	public double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}
	public double getGivenAmount() {
		return givenAmount;
	}
	public void setGivenAmount(double givenAmount) {
		this.givenAmount = givenAmount;
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

	public double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

}
