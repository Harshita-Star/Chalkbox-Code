package schooldata;

import java.io.IOException;
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


@ManagedBean(name="income")
@ViewScoped
public class IncomeBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> incomeCategoryList,paytoList=new ArrayList<>();
	Date incomeDate,chequeDate;
	String amount,type,description,paymentMode,chequeNo,bankBranch,bankName,submittedBankName,label,otherIncomeCategory,selectedCategoy,transfer;
	boolean showCheque,showTransfer,showTransferSelect;
	ArrayList<SelectItem> bankList,monthList,yearlist=new ArrayList<>();
	BankInfo bank_info;
	String voucherNo,taxYear,taxAmount,taxMonth;
	DatabaseMethods1 obj = new  DatabaseMethods1();
	String payfrom, otherPayfrom,selectedPayfrom;
	int tax = 0;
	
	public IncomeBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		tax = dbr.incomeTaxValue(obj.schoolId(),obj.selectedSessionDetails(obj.schoolId(),conn),"inc_tax",conn);
		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
		incomeDate=new Date();
		voucherNo=obj.generateVoucherNo("income_table",conn);
		monthList=obj.allMonths();
		paytoList=obj.allPayto("receive",conn);
		incomeCategoryList=obj.allIncomeExpenseCategory("Income",conn);
		taxMonth=String.valueOf(new Date().getMonth()+1);
		taxYear=String.valueOf(new Date().getYear()+1900);
		for(int i=2015;i<=2030;i++)
		{
			SelectItem LS=new SelectItem();
			LS.setValue(String.valueOf(i));
			LS.setLabel(String.valueOf(i));
			yearlist.add(LS);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addOtherCategory()
	{
		Connection conn = DataBaseConnection.javaConnection();
		boolean checkItem=obj.checkDuplicateIncomeExpenseCategory(otherIncomeCategory,"Income",conn);
		if(checkItem==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Item Name.Please Try With a New/Unique Item Name."));
			otherIncomeCategory="";
		}
		else
		{
			int n=obj.addIncomeExpenseCategory(otherIncomeCategory,"Income",conn);
			if(n>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				incomeCategoryList=obj.allIncomeExpenseCategory("Income",conn);
				selectedCategoy=otherIncomeCategory;
				PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Added Successfully"));
				PrimeFaces.current().ajax().update("form3");
				PrimeFaces.current().ajax().update("form1:itemCtg");
			}
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
		
		
	    language = value = "Other Income Category-"+otherIncomeCategory;
		
	    
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Other Income Category","WEB",value,conn);
		return refNo;
	}
	
	
	
	public void addOtherPayto()
	{
		Connection conn=DataBaseConnection.javaConnection();
        try 
        {
        	boolean checkItem=obj.checkDuplicatePayto(otherPayfrom,"receive",conn);
    		if(checkItem)
    		{
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Received From.Please Try With a New/Unique Received From."));
    		}
    		else if(otherPayfrom.equalsIgnoreCase("Other"))
    		{
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Received From.Please Try With a New/Unique Received From."));
    		}
    		else
    		{
    			int n=obj.addPayto(otherPayfrom,"receive",conn);
    			if(n>=1)
    			{
    				String refNo2=addWorkLog2(conn);
    				paytoList=obj.allPayto("receive",conn);
    				selectedPayfrom=otherPayfrom;
    				PrimeFaces.current().executeInitScript("PF('ptDialog').hide()");
    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Received From Added Successfully"));
    				otherPayfrom="";
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
		
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
	    language = value = "Other Pay From Receive-"+otherPayfrom;
		
	    
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Other Pay From Add Income","WEB",value,conn);
		return refNo;
	}
	
	
	
	public void checkTransferMode()
	{
		if(transfer.equals("Yes") && paymentMode.equalsIgnoreCase("Cash"))
		{
			showTransfer=true;
		}
		else if(paymentMode.equals("Cash"))
		{
			showTransfer=false;
		}

	}

	public void checkIncomeCategory()
	{

	}

	public String addIncome() throws IOException
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			voucherNo=obj.generateVoucherNo("income_table",conn);
			if(paymentMode.equalsIgnoreCase("Cash") && transfer.equalsIgnoreCase("No"))
			{
				submittedBankName="N/A";
			}

			if(!selectedPayfrom.equalsIgnoreCase("other"))
			{
				payfrom = selectedPayfrom;
			}
			
			String normAmt = amount;
			taxAmount = new DecimalFormat("##.##").format((Double.valueOf(amount)*((double)tax/100)));
			amount = new DecimalFormat("##.##").format((Double.valueOf(amount)+Double.valueOf(taxAmount)));
			
			int i=new DataBaseMethodsIncome_Expense().addIncome(incomeDate,selectedCategoy,paymentMode,amount,chequeNo,bankBranch,
					bankName,chequeDate,description,submittedBankName,voucherNo,taxAmount,taxMonth,taxYear,payfrom,"0",selectedPayfrom,normAmt,conn);
			if(i>=1)
			{
				String refNo3=addWorkLog3(conn,normAmt);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Income Added Successfully"));
				printVoucherDetail(conn);
				taxMonth=String.valueOf(new Date().getMonth()+1);
				taxYear=String.valueOf(new Date().getYear()+1900);
				taxAmount="";
				amount=description=paymentMode=chequeNo=bankBranch=label="";
				showCheque=false;
				incomeDate=new Date();
				selectedCategoy="";
				return "addIncome.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	
	public String addWorkLog3(Connection conn,String normAmt)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(incomeDate);
		
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
		
		
		
		value = "Income Date-"+addDt+" --Category-"+selectedCategoy+" --PaymentMode-"+paymentMode+" --Amount-"+amount+" --Cheque No-"+chequeNo+" --Bank Branch-"+bankBranch+" --Bank-"+bankName+" --Cheque Date-"+chqDt+" --Description-"+
				description+" --Submitted bank Name-"+submittedBankName+" --Voucher No-"+voucherNo+" --taxMonth-"+taxMonth+" --tax Year-"+taxYear+" --Tax Amount-"+taxAmount+" --PayFrom-"+payfrom+" --Selected PayFrom-"+selectedPayfrom+" --Normal AMt-"+normAmt; 
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Income","WEB",value,conn);
		return refNo;
	}

	
	public void checkPaymentMode()
	{
		if(paymentMode.equalsIgnoreCase("Cash"))
		{
			showCheque=false;
			showTransfer=false;
			showTransferSelect=true;
			transfer=bankBranch=bankName=chequeNo="";
			chequeDate=null;

		}
		else if(paymentMode.equalsIgnoreCase("Cheque") || paymentMode.equalsIgnoreCase("Demand Draft")||paymentMode.equalsIgnoreCase("NEFT")||paymentMode.equalsIgnoreCase("RTGS"))
		{
			if(paymentMode.equals("Cheque"))
			{
				label="Cheque";
			}
			else
				if(paymentMode.equals("Demand Draft"))
				{
					label="Demand Draft";
				}
				else
					if(paymentMode.equals("NEFT"))
					{
						label="NEFT";
					}
					else
					{
						label="RTGS";
					}
			showCheque=true;
			showTransferSelect=false;
			showTransfer=true;
			transfer=bankBranch=bankName=chequeNo="";
			chequeDate=null;
			submittedBankName="N/A";

		}
	}

	public void printVoucherDetail(Connection conn)
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("voucherNo", voucherNo);
		ss.setAttribute("expDate", incomeDate);
		ss.setAttribute("amount", amount);
		ss.setAttribute("vchtype", "Credit");
		ss.setAttribute("payTo", payfrom);
		ss.setAttribute("description", description);
		ss.setAttribute("being",obj.incomeExpenseCategoryNameById("income_cat", selectedCategoy,conn));
		ss.setAttribute("payMode", paymentMode);
		ss.setAttribute("voucherName", "Income");
		ss.setAttribute("chequeDate", chequeDate);
		ss.setAttribute("chequeNo", chequeNo);
		ss.setAttribute("bankName", submittedBankName);
		PrimeFaces.current().executeInitScript("window.open('printVoucher.xhtml')");

	}


	public ArrayList<SelectItem> getIncomeCategoryList() {
		return incomeCategoryList;
	}
	public void setIncomeCategoryList(ArrayList<SelectItem> incomeCategoryList) {
		this.incomeCategoryList = incomeCategoryList;
	}
	public Date getIncomeDate() {
		return incomeDate;
	}
	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}
	public Date getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
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

	public String getSubmittedBankName() {
		return submittedBankName;
	}

	public void setSubmittedBankName(String submittedBankName) {
		this.submittedBankName = submittedBankName;
	}

	public boolean isShowTransfer() {
		return showTransfer;
	}

	public void setShowTransfer(boolean showTransfer) {
		this.showTransfer = showTransfer;
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

	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	public boolean isShowTransferSelect() {
		return showTransferSelect;
	}

	public void setShowTransferSelect(boolean showTransferSelect) {
		this.showTransferSelect = showTransferSelect;
	}

	public String getOtherIncomeCategory() {
		return otherIncomeCategory;
	}

	public void setOtherIncomeCategory(String otherIncomeCategory) {
		this.otherIncomeCategory = otherIncomeCategory;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
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

	public String getTaxMonth() {
		return taxMonth;
	}

	public void setTaxMonth(String taxMonth) {
		this.taxMonth = taxMonth;
	}

	public ArrayList<SelectItem> getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList<SelectItem> yearlist) {
		this.yearlist = yearlist;
	}

	public String getPayfrom() {
		return payfrom;
	}

	public void setPayfrom(String payfrom) {
		this.payfrom = payfrom;
	}

	public ArrayList<SelectItem> getPaytoList() {
		return paytoList;
	}

	public void setPaytoList(ArrayList<SelectItem> paytoList) {
		this.paytoList = paytoList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getOtherPayfrom() {
		return otherPayfrom;
	}

	public void setOtherPayfrom(String otherPayfrom) {
		this.otherPayfrom = otherPayfrom;
	}

	public String getSelectedPayfrom() {
		return selectedPayfrom;
	}

	public void setSelectedPayfrom(String selectedPayfrom) {
		this.selectedPayfrom = selectedPayfrom;
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
