package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
@ManagedBean(name="CheckAmountInUnit")
@ViewScoped
public class CheckAmountInUnitBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	boolean showUnitName;
	BankInfo selectedList;
	double totalCashAmount,totalChequeAmount,totalClearAmount;
	ArrayList<BankInfo> amountListCash=new ArrayList<>();
	ArrayList<BankInfo> amountListBank=new ArrayList<>();

	public CheckAmountInUnitBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		checkAmount();
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void checkAmount()
	{
		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMethodsIncome_Expense objRevenue=new DataBaseMethodsIncome_Expense();
		totalCashAmount=totalChequeAmount=totalClearAmount=0;
		amountListBank=new ArrayList<>();
		amountListCash=new ArrayList<>();
		amountListCash=objRevenue.calculateCashAmount(conn);
		for(BankInfo ll:amountListCash)
		{
			totalCashAmount+=ll.getAmount();
		}
		amountListBank=objRevenue.calculateBankAmount(conn);
		for(BankInfo ll:amountListBank)
		{
			totalClearAmount+=ll.getAmount();
			totalChequeAmount+=ll.getTotalAmount();
		}
		DecimalFormat df= new DecimalFormat("##.##");
		totalCashAmount=(Double.parseDouble(df.format(totalCashAmount)));
		totalClearAmount=(Double.parseDouble(df.format(totalClearAmount)));
		totalChequeAmount=(Double.parseDouble(df.format(totalChequeAmount)));
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void checkLedger()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("mode",selectedList.getType());
		ss.setAttribute("Bank",selectedList.getId());
		String viewPath="modeWiseLedger.xhtml";
		PrimeFaces.current().executeInitScript("window.open('"+viewPath+"')");
	}

	public void dueChequeNotification()
	{
		PrimeFaces.current().executeInitScript("window.open('pendingTransaction.xhtml')");
	}

	public double getTotalClearAmount() {
		return totalClearAmount;
	}

	public void setTotalClearAmount(double totalClearAmount) {
		this.totalClearAmount = totalClearAmount;
	}

	public boolean isShowUnitName() {
		return showUnitName;
	}
	public void setShowUnitName(boolean showUnitName) {
		this.showUnitName = showUnitName;
	}
	public BankInfo getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(BankInfo selectedList) {
		this.selectedList = selectedList;
	}
	public ArrayList<BankInfo> getAmountListCash() {
		return amountListCash;
	}
	public void setAmountListCash(ArrayList<BankInfo> amountListCash) {
		this.amountListCash = amountListCash;
	}
	public ArrayList<BankInfo> getAmountListBank() {
		return amountListBank;
	}
	public void setAmountListBank(ArrayList<BankInfo> amountListBank) {
		this.amountListBank = amountListBank;
	}
	public double getTotalCashAmount() {
		return totalCashAmount;
	}
	public void setTotalCashAmount(double totalCashAmount) {
		this.totalCashAmount = totalCashAmount;
	}
	public double getTotalChequeAmount() {
		return totalChequeAmount;
	}
	public void setTotalChequeAmount(double totalChequeAmount) {
		this.totalChequeAmount = totalChequeAmount;
	}
}
