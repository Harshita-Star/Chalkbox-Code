package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import session_work.RegexPattern;

@ManagedBean(name="pendingTrans")
@ViewScoped
public class PendingTransactionBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<UserInfo> incomeList;
	String regex=RegexPattern.REGEX;
	ArrayList<LoanInfo> loanEMIList;
	UserInfo selectedItem;
	Date chqReturnDate=new Date(), chqClearDate=new Date();
	boolean value,showPayDetail;
	String instlmnt_no="",label, reason;
	ArrayList<SelectItem> bankList;
	int x,a=0;
	double amount,insAmount;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public PendingTransactionBean()
	{

		createList();
	}

	public void createList()
	{

		Connection conn = DataBaseConnection.javaConnection();
		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
		incomeList=obj.pendingIncomeList(conn);
		int i=1;
		for(UserInfo ll:incomeList)
		{
			ll.setSno(i++);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String approveRequest()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i;
		if(selectedItem.getStatus().equalsIgnoreCase("Income"))
		{
			i=obj.approveTransaction(selectedItem, chqClearDate,conn,"income_table");
		}
		if(selectedItem.getStatus().equalsIgnoreCase("Expense"))
		{
			i=obj.approveTransaction(selectedItem, chqClearDate,conn,"expense_table");
		}
		else
		{
			i=obj.approveTransactionForFees(selectedItem, /*chqClearDate,*/conn);
		}
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Transaction Approved Successfully"));

			if(selectedItem.getStatus().equalsIgnoreCase("Payment For RD"))
			{
				detailsForRDFDPrint();
				PrimeFaces.current().executeInitScript("window.open('rd_fdReceipt.xhtml')");
			}
			else if(selectedItem.getStatus().equalsIgnoreCase("EMI Payment"))
			{
				detailsForLoanPrint();
				PrimeFaces.current().executeInitScript("window.open('loanPayReceipt.xhtml')");
			}
			createList();
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

		return "pendingTransaction";
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(chqClearDate);
		String chqDt = formater.format(selectedItem.getChq_ddDate());
		
		
		language = "Check Clear Date-"+addDt;
		value = "Status - "+selectedItem.getStatus()+" --Checque Dd No-"+selectedItem.getChq_ddNo()+" --Cheque Date-"+chqDt+" --Selected Id-"+selectedItem.getId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Approve Pending Transaction","WEB",value,conn);
		return refNo;
	}
	
	

	public void detailsForRDFDPrint()
	{
		int id=Integer.valueOf(selectedItem.getId());
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("shareHolderNo", selectedItem.getShareHolderNo());
		ss.setAttribute("frequency", selectedItem.getFrequency());
		ss.setAttribute("office_application_no", selectedItem.getOffice_application_no());
		ss.setAttribute("scheme_type", selectedItem.getScheme_type());
		ss.setAttribute("rd_fdNo",selectedItem.getAccountNo());
		ss.setAttribute("maturityDate",selectedItem.getStrMatDate());
		ss.setAttribute("date",selectedItem.getEntryDate());
		ss.setAttribute("issue_date",selectedItem.getOffice_date());
		ss.setAttribute("maturity_value",selectedItem.getMaturity_value());
		ss.setAttribute("receipt_no", selectedItem.getReceiptNo());
		ss.setAttribute("installment_amount", selectedItem.getAmount());
		ss.setAttribute("plan_no", selectedItem.getPlan_no());
		ss.setAttribute("id", id);
		ss.setAttribute("instlmnt_no",instlmnt_no );
	}

	public void detailsForLoanPrint()
	{
		int id=Integer.valueOf(selectedItem.getId());
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("shareHolderNo", selectedItem.getShareHolderNo());
		ss.setAttribute("loanNo",selectedItem.getLoanNo());
		ss.setAttribute("receipt_no", selectedItem.getReceiptNo());
		ss.setAttribute("installment_amount", selectedItem.getAmount());
		ss.setAttribute("loanType", selectedItem.getScheme_type());
		ss.setAttribute("receipt_date",selectedItem.getEntryDateStr());
		ss.setAttribute("id", id);
		ss.setAttribute("instlmnt_no",instlmnt_no);
	}

	public String denyRequest()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i;
		if(selectedItem.getStatus().equalsIgnoreCase("Income"))
		{
			i=obj.denyTransaction(selectedItem,conn);
		}
		else
		{
			i=obj.denyTransactionForFees(selectedItem,conn);
		}
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Transaction Denied Successfully"));
			//	if(selectedItem.getStatus().equals("EMI Payment"))
			String voucherNo=obj.generateVoucherNo("expense_table",conn);
			String schoolid= obj.schoolId();
			{
				String query="insert into expense_table(expense_date,category,payment_mode,amount,chq_dd_no,chq_dd_date,bank_name,bank_branch,"
						+ " description,item,chequeStatus, no_of_item,voucher_no,schid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				try
				{
					PreparedStatement st=conn.prepareStatement(query);
					st.setTimestamp(1,new Timestamp(chqReturnDate.getTime()));
					st.setString(2, "Other_Dr");
					st.setString(3, "Cheque");
					st.setString(4, String.valueOf(amount));
					st.setString(5, selectedItem.getChq_ddNo());
					st.setTimestamp(6, new Timestamp(selectedItem.getChq_ddDate().getTime()));
					st.setString(7,"");
					st.setString(8,"na");
					st.setString(9, reason);
					st.setString(10, "Cheque Bounce Charge");
					st.setString(11, "cleared");
					st.setString(12, "1");
					st.setString(13, voucherNo);
					st.setString(14, schoolid);
					i=st.executeUpdate();
					if(i>0)
					{
						//obj.decreaseBankBalance(transferbank, amount,conn);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			createList();
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

		return "pendingTransaction";
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(chqReturnDate);
		String chqDt = formater.format(selectedItem.getChq_ddDate());
		
		
		language = "Check Bounce Date-"+addDt+" --Amount"+amount+" --Reason-"+reason;
		value = "Status - "+selectedItem.getStatus()+" --Checque Dd No-"+selectedItem.getChq_ddNo()+" --Cheque Date-"+chqDt+" --Selected Id-"+selectedItem.getId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Deny Pending Transaction","WEB",value,conn);
		return refNo;
	}

	public void viewDetails()
	{
		if(!selectedItem.getPayMode().equalsIgnoreCase("cash"))
		{

			value=true;
		}
		else
		{
			value=false;
		}

		if(selectedItem.getStatus().equals("Payment For RD"))
		{
			label="RD/FD No.";
			showPayDetail=true;
		}
		else if(selectedItem.getStatus().equals("EMI Payment"))
		{
			label="Loan No.";
			showPayDetail=true;
		}
		else if(selectedItem.getStatus().equals("Deposit"))
		{
			label="Acc.No";
			showPayDetail=false;
		}
		else if(selectedItem.getStatus().equals("Fixed_Deposit_Cr"))
		{
			label="FDR No.";
			showPayDetail=false;
		}
		else if(selectedItem.getStatus().equals("Other_Cr") || selectedItem.getStatus().equals("Fixed_Asset_Cr"))
		{
			label="Item Name";
			showPayDetail=false;
		}
	}

	public String viewLoan()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedLoan",selectedItem);
		return "";
	}



	public UserInfo getSelectedItem() {
		return selectedItem;
	}
	public void setSelectedItem(UserInfo selectedItem) {
		this.selectedItem = selectedItem;
	}
	public boolean isValue() {
		return value;
	}
	public boolean isShowPayDetail() {
		return showPayDetail;
	}
	public void setShowPayDetail(boolean showPayDetail) {
		this.showPayDetail = showPayDetail;
	}
	public void setValue(boolean value) {
		this.value = value;
	}



	public String getInstlmnt_no() {
		return instlmnt_no;
	}

	public void setInstlmnt_no(String instlmnt_no) {
		this.instlmnt_no = instlmnt_no;
	}

	public Date getChqReturnDate() {
		return chqReturnDate;
	}

	public void setChqReturnDate(Date chqReturnDate) {
		this.chqReturnDate = chqReturnDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getChqClearDate() {
		return chqClearDate;
	}

	public void setChqClearDate(Date chqClearDate) {
		this.chqClearDate = chqClearDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ArrayList<UserInfo> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(ArrayList<UserInfo> incomeList) {
		this.incomeList = incomeList;
	}

	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
