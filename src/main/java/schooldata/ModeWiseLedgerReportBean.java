package schooldata;

import java.io.Serializable;
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

@ManagedBean(name="modeWiseLedgerReport")
@ViewScoped
public class ModeWiseLedgerReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String bank,mode,bankName,accountNumber;
	double openingAmount,totalCredit,totalDebit,closeBalance;
	ArrayList<LoanInfo> incomeList,projectList,expenceList,finalList,tempList,openingList;
	boolean show;
	Date startDate,endDate;
	DecimalFormat df= new DecimalFormat("##.##");
	public ModeWiseLedgerReportBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		mode=(String) ss.getAttribute("mode");
		bank=(String) ss.getAttribute("Bank");


		finalList=new ArrayList<>();
		tempList=new ArrayList<>();
		openingList=new ArrayList<>();
		endDate=new Date();
		startDate=new Date();
		startDate.setDate(1);
		double opAmount=0;

		DataBaseMethodsIncome_Expense obj= new DataBaseMethodsIncome_Expense();
		double feeAmount=obj.feeAmountBeforDateModeWise(startDate, mode,bank,conn);
		double incomeAmount=obj.IncomeAmountBeforDateModeWise(startDate, mode,bank,conn);
		double expenceAmount=obj.ExpenceAmountBeforDateModeWise(startDate, mode,bank,conn);
		if(mode.equals("Bank"))
		{

			BankInfo bankInfo= new DatabaseMethods1().obtainBankInfoById(bank, conn);
			bankName=bankInfo.getBank_name();
			accountNumber=bankInfo.getAcc_no();
			opAmount=new DatabaseMethods1().currentBankBalance(bank,conn);
		}
		openingAmount=feeAmount+incomeAmount-expenceAmount+opAmount;
		projectList=obj.feeListByDateModeWise(startDate, endDate, mode,bank,conn);
		incomeList=obj.incomeListByDateModeWise(startDate, endDate, mode,bank,conn);
		expenceList=obj.expenceListByDateModeWise(startDate, endDate, mode,bank,conn);

		LoanInfo info=new LoanInfo();
		info.setChequeDate(startDate);
		info.setStrChequeDate(new SimpleDateFormat("dd/MM/yyyy").format(startDate));
		info.setPaymentMode("Opening Balance");
		info.setCredit(Double.parseDouble(df.format(openingAmount)));
		openingList.add(info);
		finalList.addAll(openingList);
		tempList.addAll(projectList);
		tempList.addAll(incomeList);
		tempList.addAll(expenceList);


		totalCredit=0;totalDebit=0;
		int i=0;


		Collections.sort(tempList);
		finalList.addAll(tempList);
		if(finalList.size()>0)
		{
			show=true;
		}

		for(LoanInfo ll:finalList)
		{
			totalCredit+=ll.getCredit();
			totalDebit+=ll.getDebit();
			if(i==0)
			{
				String finalamt=df.format(ll.getCredit()-ll.getDebit());
				ll.setFinalAmount(Double.parseDouble(finalamt));
			}
			else
			{
				double  famoun=finalList.get(i-1).getFinalAmount();
				String finalamt=df.format(famoun+(ll.getCredit()-ll.getDebit()));
				ll.setFinalAmount(Double.parseDouble(finalamt));

			}
			i++;
		}
		closeBalance=totalCredit-totalDebit;
		closeBalance=(Double.parseDouble(df.format(closeBalance)));
		totalDebit=(Double.parseDouble(df.format(totalDebit)));
		totalCredit=(Double.parseDouble(df.format(totalCredit)));
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void createLedger()
	{
		finalList=new ArrayList<>();
		tempList=new ArrayList<>();
		openingList=new ArrayList<>();
		double opAmount=0;
		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMethodsIncome_Expense obj= new DataBaseMethodsIncome_Expense();
		double feeAmount=obj.feeAmountBeforDateModeWise(startDate, mode,bank,conn);
		double incomeAmount=obj.IncomeAmountBeforDateModeWise(startDate, mode,bank,conn);
		double expenceAmount=obj.ExpenceAmountBeforDateModeWise(startDate, mode,bank,conn);
		if(mode.equals("Bank"))
		{
			opAmount=new DatabaseMethods1().currentBankBalance(bank,conn);
		}
		openingAmount=Double.parseDouble(df.format(feeAmount+incomeAmount-expenceAmount+opAmount)) ;
		projectList=obj.feeListByDateModeWise(startDate, endDate, mode,bank,conn);
		incomeList=obj.incomeListByDateModeWise(startDate, endDate, mode,bank,conn);
		expenceList=obj.expenceListByDateModeWise(startDate, endDate, mode,bank,conn);
		LoanInfo info=new LoanInfo();
		info.setChequeDate(startDate);
		info.setStrChequeDate(new SimpleDateFormat("dd/MM/yyyy").format(startDate));
		info.setPaymentMode("Opening Balance");
		info.setCredit(openingAmount);
		finalList.add(info);
		tempList.addAll(projectList);
		tempList.addAll(incomeList);
		tempList.addAll(expenceList);

		Collections.sort(tempList);
		finalList.addAll(tempList);
		if(finalList.size()>0)
		{
			show=true;
		}

		totalCredit=0;totalDebit=0;
		int i=0;
		for(LoanInfo ll:finalList)
		{
			totalCredit+=ll.getCredit();
			totalDebit+=ll.getDebit();
			if(i==0)
			{
				String finalamt=df.format(ll.getCredit()-ll.getDebit());
				ll.setFinalAmount(Double.parseDouble(finalamt));
			}
			else
			{
				double  famoun=finalList.get(i-1).getFinalAmount();
				String finalamt=df.format(famoun+(ll.getCredit()-ll.getDebit()));
				ll.setFinalAmount(Double.parseDouble(finalamt));

			}
			i++;
		}

		closeBalance=totalCredit-totalDebit;
		closeBalance=(Double.parseDouble(df.format(closeBalance)));
		totalDebit=(Double.parseDouble(df.format(totalDebit)));
		totalCredit=(Double.parseDouble(df.format(totalCredit)));
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<LoanInfo> getIncomeList() {
		return incomeList;
	}
	public void setIncomeList(ArrayList<LoanInfo> incomeList) {
		this.incomeList = incomeList;
	}
	public ArrayList<LoanInfo> getProjectList() {
		return projectList;
	}
	public void setProjectList(ArrayList<LoanInfo> projectList) {
		this.projectList = projectList;
	}
	public ArrayList<LoanInfo> getExpenceList() {
		return expenceList;
	}
	public void setExpenceList(ArrayList<LoanInfo> expenceList) {
		this.expenceList = expenceList;
	}
	public ArrayList<LoanInfo> getFinalList() {
		return finalList;
	}
	public void setFinalList(ArrayList<LoanInfo> finalList) {
		this.finalList = finalList;
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
	public double getCloseBalance() {
		return closeBalance;
	}
	public void setCloseBalance(double closeBalance) {
		this.closeBalance = closeBalance;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
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
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public double getOpeningAmount() {
		return openingAmount;
	}
	public void setOpeningAmount(double openingAmount) {
		this.openingAmount = openingAmount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
}
