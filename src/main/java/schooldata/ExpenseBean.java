package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;


@ManagedBean(name="expense")
@ViewScoped
public class ExpenseBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> expenseCategoryList,modeList,monthList,yearList=new ArrayList<>(),paytoList=new ArrayList<>();
	Date expenseDate,chequeDate;
	String amount,description,paymentMode,chequeNo,bankBranch,bankName,payTo,selectedCategoy,label,otherExpenseCategory="",taxMonth,taxYear,taxAmount="0";
	boolean showCheque,showBank,showPayto;
	ArrayList<SelectItem> bankList;
	BankInfo bank_info;
	String newItemName, voucherNo, otherPayto,selectedPayto;
	int tax=0;
	DatabaseMethods1 obj=new DatabaseMethods1();
	public ExpenseBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		tax = dbr.incomeTaxValue(obj.schoolId(),obj.selectedSessionDetails(obj.schoolId(),conn),"exp_tax",conn);
		expenseDate=new Date();
		modeList=new ArrayList<>();
		voucherNo=obj.generateVoucherNo("expense_table",conn);
		expenseCategoryList=obj.allIncomeExpenseCategory("Expense",conn);
		paytoList=obj.allPayto("pay",conn);
		monthList=obj.allMonths();
		taxMonth=String.valueOf(new Date().getMonth()+1);
		taxYear=String.valueOf(new Date().getYear()+1900);
		for(int i=2015;i<=2030;i++)
		{
			SelectItem LS=new SelectItem();
			LS.setValue(String.valueOf(i));
			LS.setLabel(String.valueOf(i));
			yearList.add(LS);
		}

		SelectItem ll=new SelectItem();
		ll.setLabel("Cash");
		ll.setValue("Cash");
		modeList.add(ll);

		SelectItem ll1=new SelectItem();
		ll1.setLabel("Cheque");
		ll1.setValue("Cheque");
		modeList.add(ll1);

		SelectItem ll2=new SelectItem();
		ll2.setLabel("Demand Draft");
		ll2.setValue("Demand Draft");
		modeList.add(ll2);

		SelectItem ll3=new SelectItem();
		ll3.setLabel("Bank Deduction");
		ll3.setValue("Bank Deduction");
		modeList.add(ll3);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addOtherCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();

		boolean checkItem=obj.checkDuplicateIncomeExpenseCategory(otherExpenseCategory,"Expense",conn);
		if(checkItem==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Item Name.Please Try With a New/Unique Item Name."));
		}
		else
		{
			int n=obj.addIncomeExpenseCategory(otherExpenseCategory,"Expense",conn);
			if(n>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				expenseCategoryList=obj.allIncomeExpenseCategory("Expense",conn);
				selectedCategoy=otherExpenseCategory;
				PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Added Successfully"));
				otherExpenseCategory="";
				PrimeFaces.current().ajax().update("form3");
				PrimeFaces.current().ajax().update("form1:itemCtg");
			}
		}
		/*int n=new DatabaseMethods1().addOtherExpenseCategory(otherExpenseCategory,conn);
		if(n>=1)
		{
			otherList=new DatabaseMethods1().allOtherExpenseCategory(conn);
			itemName=otherExpenseCategory;
			PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
			PrimeFaces.current().ajax().update("form3");
			PrimeFaces.current().ajax().update("form");
		}*/

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
		
		language = value = otherExpenseCategory; 
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add other Expense Category","WEB",value,conn);
		return refNo;
	}
	
	
	public void addOtherPayto()
	{
		Connection conn=DataBaseConnection.javaConnection();

		boolean checkItem=obj.checkDuplicatePayto(otherPayto,"pay",conn);
		if(checkItem==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Pay To.Please Try With a New/Unique Pay To."));
		}
		else if(otherPayto.equalsIgnoreCase("Other"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Pay To.Please Try With a New/Unique Pay To."));
		}
		else
		{
			int n=obj.addPayto(otherPayto,"pay",conn);
			if(n>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				paytoList=obj.allPayto("pay",conn);
				selectedPayto=otherPayto;
				PrimeFaces.current().executeInitScript("PF('ptDialog').hide()");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pay To Added Successfully"));
				otherPayto="";
				PrimeFaces.current().ajax().update("form4");
				PrimeFaces.current().ajax().update("form1:paytoPnl");
			}
		}
		/*int n=new DatabaseMethods1().addOtherExpenseCategory(otherExpenseCategory,conn);
		if(n>=1)
		{
			otherList=new DatabaseMethods1().allOtherExpenseCategory(conn);
			itemName=otherExpenseCategory;
			PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
			PrimeFaces.current().ajax().update("form3");
			PrimeFaces.current().ajax().update("form");
		}*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = otherPayto; 
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Other pay To","WEB",value,conn);
		return refNo;
	}
	

	public void checkBank()
	{
		Connection conn=DataBaseConnection.javaConnection();
		bank_info=obj.obtainBankInfoById(bankName,conn);
		bankBranch=bank_info.getBank_branch();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addExpense()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(!selectedPayto.equalsIgnoreCase("other"))
			{
				payTo = selectedPayto;
			}
			String normAmt = amount;
			taxAmount = new DecimalFormat("##.##").format((Double.valueOf(amount)*((double)tax/100)));
			amount = new DecimalFormat("##.##").format((Double.valueOf(amount)+Double.valueOf(taxAmount)));
			voucherNo=obj.generateVoucherNo("expense_table",conn);
			int x=new DataBaseMethodsIncome_Expense().addExpense(expenseDate,selectedCategoy,paymentMode,amount,chequeNo,bankBranch,bankName,chequeDate,
					description, voucherNo,taxMonth,taxYear,taxAmount,payTo,selectedPayto,normAmt,conn);
			if(x>=1)
			{
				String refNo3=addWorkLog3(conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Expense Added Successfully"));
				printVoucherDetail(conn);
				amount=description=paymentMode=chequeNo=bankBranch=bankName=selectedCategoy=payTo=selectedPayto="";
				showCheque=false;
				expenseDate=new Date();

				return "addExpense.xhtml";
				//FacesContext.getCurrentInstance().getExternalContext().redirect("");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}
	
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(expenseDate);
		String chqDt = "";
		if(paymentMode.equals("Cash"))
		{
			chqDt="";
		}
		else
		{
			try {
				chqDt = formater.format(chequeDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		value = "Expense Date-"+addDt+" --Category-"+selectedCategoy+" --PaymentMode-"+paymentMode+" --Amount-"+amount+" --Cheque No-"+chequeNo+" --Bank Branch-"+bankBranch+" --Bank-"+bankName+" --Cheque Date-"+chqDt+" --Description-"+
				description+" --Voucher No-"+voucherNo+" --taxMonth-"+taxMonth+" --tax Year-"+taxYear+" --Tax Amount-"+taxAmount+" --PayTo-"+payTo+" --Selected Payto-"+selectedPayto; 
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Expense","WEB",value,conn);
		return refNo;
	}
	

	public void checkPaymentMode()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsIncome_Expense objIncome=new DataBaseMethodsIncome_Expense();

		if(paymentMode.equals("Cash"))
		{
			showCheque=false;
			showBank=false;
		}
		else if(paymentMode.equals("Bank Deduction"))
		{
			bankList=new ArrayList<>();
			bankList=objIncome.allBankList(conn);
			showBank=true;
			showCheque=false;
		}
		else if(paymentMode.equals("Cheque") || paymentMode.equals("Demand Draft"))
		{
			bankList=new ArrayList<>();
			bankList=objIncome.allBankList(conn);
			if(paymentMode.equals("Cheque"))
			{
				label="Cheque";
			}
			else
			{
				label="Demand Draft";
			}
			showCheque=true;
			showBank=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printVoucherDetail(Connection conn)
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("voucherNo", voucherNo);
		ss.setAttribute("expDate", expenseDate);
		ss.setAttribute("amount", amount);
		ss.setAttribute("vchtype", "Debit");
		ss.setAttribute("payTo", payTo);
		ss.setAttribute("description", description);
		ss.setAttribute("being",obj.incomeExpenseCategoryNameById("expense_cat", selectedCategoy,conn));
		ss.setAttribute("payMode", paymentMode);
		ss.setAttribute("voucherName", "Expense");
		ss.setAttribute("chequeDate", chequeDate);
		ss.setAttribute("chequeNo", chequeNo);
		ss.setAttribute("bankName", bankName);
		PrimeFaces.current().executeInitScript("window.open('printVoucher.xhtml')");

	}
	
	public void sys()
	{
		
	}

	public Date getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	public ArrayList<SelectItem> getExpenseCategoryList() {
		return expenseCategoryList;
	}
	public void setExpenseCategoryList(ArrayList<SelectItem> expenseCategoryList) {
		this.expenseCategoryList = expenseCategoryList;
	}
	public Date getExpenseDate() {
		return expenseDate;
	}
	public void setExpenseDate(Date expenseDate) {
		this.expenseDate = expenseDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getBankBranch() {
		return bankBranch;
	}

	public boolean isShowBank() {
		return showBank;
	}

	public void setShowBank(boolean showBank) {
		this.showBank = showBank;
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
	public boolean isShowCheque() {
		return showCheque;
	}
	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}
	public String getSelectedCategoy() {
		return selectedCategoy;
	}
	public void setSelectedCategoy(String selectedCategoy) {
		this.selectedCategoy = selectedCategoy;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}

	public BankInfo getBank_info() {
		return bank_info;
	}

	public void setBank_info(BankInfo bank_info) {
		this.bank_info = bank_info;
	}

	public String getOtherExpenseCategory() {
		return otherExpenseCategory;
	}

	public void setOtherExpenseCategory(String otherExpenseCategory) {
		this.otherExpenseCategory = otherExpenseCategory;
	}

	public ArrayList<SelectItem> getModeList() {
		return modeList;
	}

	public void setModeList(ArrayList<SelectItem> modeList) {
		this.modeList = modeList;
	}
	public String getNewItemName() {
		return newItemName;
	}

	public void setNewItemName(String newItemName) {
		this.newItemName = newItemName;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public String getTaxMonth() {
		return taxMonth;
	}

	public void setTaxMonth(String taxMonth) {
		this.taxMonth = taxMonth;
	}

	public String getTaxYear() {
		return taxYear;
	}

	public void setTaxYear(String taxYear) {
		this.taxYear = taxYear;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getPayTo() {
		return payTo;
	}

	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}

	public ArrayList<SelectItem> getPaytoList() {
		return paytoList;
	}

	public void setPaytoList(ArrayList<SelectItem> paytoList) {
		this.paytoList = paytoList;
	}

	public boolean isShowPayto() {
		return showPayto;
	}

	public void setShowPayto(boolean showPayto) {
		this.showPayto = showPayto;
	}

	public String getOtherPayto() {
		return otherPayto;
	}

	public void setOtherPayto(String otherPayto) {
		this.otherPayto = otherPayto;
	}

	public String getSelectedPayto() {
		return selectedPayto;
	}

	public void setSelectedPayto(String selectedPayto) {
		this.selectedPayto = selectedPayto;
	}

	public int getTax() {
		return tax;
	}

	public void setTax(int tax) {
		this.tax = tax;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
