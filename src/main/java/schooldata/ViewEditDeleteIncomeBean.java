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

@ManagedBean(name="viewEditDeleteIncome")
@ViewScoped
public class ViewEditDeleteIncomeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<IncomeList>iList;
	IncomeList selectedIncome;
	String strIncomeDate,transferTo,description,paymentMode,chequeNo,bankBranch,bankName,label,item_name,taxMonth,taxYear,taxAmount;
	Date startDate=new Date(),endDate=new Date();
	boolean showCheque;
	double amount;

	public ViewEditDeleteIncomeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		startDate.setMonth(startDate.getMonth()-3);
		iList=new  DataBaseMethodsIncome_Expense().allIncomeList(startDate,endDate,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void searchIncome()
	{
		Connection conn=DataBaseConnection.javaConnection();
		iList=new  DataBaseMethodsIncome_Expense().allIncomeList(startDate,endDate,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetails()
	{
		paymentMode=selectedIncome.getPaymentMode();
		item_name=selectedIncome.getCategory();
		amount=selectedIncome.getAmount();
		taxAmount=String.valueOf(selectedIncome.getTaxAmount());
		taxMonth=selectedIncome.getTaxMonthName();
		taxYear=selectedIncome.getTaxYear();
		strIncomeDate=selectedIncome.getStrIncomeDate();
		transferTo=selectedIncome.getTransferToBankName();
		if(paymentMode.equals("Cash"))
		{
			showCheque=false;

		}
		else if(paymentMode.equals("Cheque"))
		{
			showCheque=true;
			chequeNo=selectedIncome.getChequeNo();
			bankName=selectedIncome.getBankName();
			bankBranch=selectedIncome.getBankBranch();

		}

		description=selectedIncome.getDescription();
	}

	public void printVoucherDetail()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("voucherNo", selectedIncome.getVoucherNo());
		ss.setAttribute("expDate", selectedIncome.getIncomeDate());
		ss.setAttribute("amount", new DecimalFormat("##.##").format(selectedIncome.getAmount()));
		ss.setAttribute("vchtype", "Credit");
		ss.setAttribute("payTo", selectedIncome.getReceivedFrom());
		ss.setAttribute("description", selectedIncome.getDescription());
		ss.setAttribute("being", selectedIncome.getCategory());
		ss.setAttribute("payMode", selectedIncome.getPaymentMode());
		ss.setAttribute("voucherName", "Income");
		ss.setAttribute("chequeDate", selectedIncome.getChqDate());
		ss.setAttribute("bankName", selectedIncome.getTransferToBankName());
		ss.setAttribute("chequeNo", chequeNo);
		PrimeFaces.current().executeInitScript("window.open('printVoucher.xhtml')");




	}

	public void print()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("list", iList);
		ss.setAttribute("start", sdf.format(startDate));
		ss.setAttribute("end", sdf.format(endDate));

		PrimeFaces.current().executeInitScript("window.open('printExtraIncome.xhtml')");
	}

	public void deleteSelectedIncome()
	{
		Connection conn=DataBaseConnection.javaConnection();

		int i=new DataBaseMethodsIncome_Expense().deleteIncome(selectedIncome,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			iList=new DataBaseMethodsIncome_Expense().allIncomeList(startDate, endDate,conn);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Income Deleted successfully"));

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
		
		
	    language = value = "Delete income Id-"+selectedIncome.getId();
		
	    
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete income","WEB",value,conn);
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

	public String getTransferTo() {
		return transferTo;
	}

	public void setTransferTo(String transferTo) {
		this.transferTo = transferTo;
	}

	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
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
}
