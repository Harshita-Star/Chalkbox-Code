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
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;


@ManagedBean(name="viewEditDeleteExpense")
@ViewScoped
public class ViewEditDeleteExpenseBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<IncomeList>iList;
	IncomeList selectedIncome;
	String strIncomeDate,description,paymentMode,chequeNo,chequeDate,bankBranch,bankName,label,expenseCategory,payTo,taxMonth,taxYear,taxAmount;
	Date startDate=new Date(),endDate=new Date();
	double amount;
	boolean showCheque;

	public ViewEditDeleteExpenseBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		startDate.setMonth(startDate.getMonth()-3);
		iList=new  DataBaseMethodsIncome_Expense().allExpenseList(startDate, endDate,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void searchIncome()
	{
		Connection conn=DataBaseConnection.javaConnection();
		iList=new  DataBaseMethodsIncome_Expense().allExpenseList(startDate,endDate,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetails()
	{
		expenseCategory=selectedIncome.getCategory();
		paymentMode=selectedIncome.getPaymentMode();
		amount=selectedIncome.getAmount();
		description=selectedIncome.getDescription();
		strIncomeDate=selectedIncome.getStrIncomeDate();
		taxMonth=selectedIncome.getTaxMonthName();
		taxYear=selectedIncome.getTaxYear();
		payTo=selectedIncome.getPayTo();
		taxAmount=String.valueOf(selectedIncome.getTaxAmount());
		if(paymentMode.equals("Cash"))
		{
			showCheque=false;
		}
		else if(paymentMode.equals("Cheque"))
		{
			showCheque=true;
			chequeNo=selectedIncome.getChequeNo();
			chequeDate=selectedIncome.getStrChqDate();
			bankName=selectedIncome.getBankName();
			bankBranch=selectedIncome.getBankBranch();
			description=selectedIncome.getDescription();
		}
	}

	public void printVoucherDetail()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("voucherNo", selectedIncome.getVoucherNo());
		ss.setAttribute("expDate", selectedIncome.getIncomeDate());
		ss.setAttribute("amount", new DecimalFormat("##.##").format(selectedIncome.getAmount()));
		ss.setAttribute("vchtype", "Debit");
		ss.setAttribute("payTo", selectedIncome.getPayTo());
		ss.setAttribute("description", selectedIncome.getDescription());
		ss.setAttribute("being", selectedIncome.getCategory());
		ss.setAttribute("payMode", selectedIncome.getPaymentMode());
		ss.setAttribute("voucherName", "Expence");
		ss.setAttribute("chequeDate", selectedIncome.getChqDate());
		ss.setAttribute("bankName", selectedIncome.getTransferToBankName());
		ss.setAttribute("chequeNo", chequeNo);
		PrimeFaces.current().executeInitScript("window.open('printVoucher.xhtml')");
	}

	public void print()
	{
		SimpleDateFormat sdf=new SimpleDateFormat();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("list", iList);
		ss.setAttribute("start",sdf.format(startDate));
		ss.setAttribute("end", sdf.format(endDate));

		PrimeFaces.current().executeInitScript("window.open('printExtraExpense.xhtml')");
	}

	public void deleteSelectedIncome()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsIncome_Expense objIncome=new DataBaseMethodsIncome_Expense();

		int i=objIncome.deleteExpense(selectedIncome,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			iList=objIncome.allExpenseList(startDate, endDate,conn);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Expense Deleted Successfully"));
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
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
		
		
	    value = "Selected Id-"+selectedIncome.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Expense","WEB",value,conn);
		return refNo;
	}
	

	public ArrayList<IncomeList> getiList() {
		return iList;
	}

	public void setiList(ArrayList<IncomeList> iList) {
		this.iList = iList;
	}

	public IncomeList getSelectedIncome() {
		return selectedIncome;
	}
	public void setSelectedIncome(IncomeList selectedIncome) {
		this.selectedIncome = selectedIncome;
	}

	public String getStrIncomeDate() {
		return strIncomeDate;
	}

	public void setStrIncomeDate(String strIncomeDate) {
		this.strIncomeDate = strIncomeDate;
	}



	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public boolean isShowCheque() {
		return showCheque;
	}

	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(String expenseCategory) {
		this.expenseCategory = expenseCategory;
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
}
