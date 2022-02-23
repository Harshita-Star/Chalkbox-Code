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

import session_work.RegexPattern;

@ManagedBean(name="dueDebit")
@ViewScoped

public class DueChequeDebitBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<DueDebitInfo> expenseList;

	DueDebitInfo selectedNotification;
	String identity="", reason, voucherNo;
	Date chqReturnDate=new Date(), chqClearDate=new Date();
	int n=0;
	double amount;

	public DueChequeDebitBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		expenseList=new DatabaseMethods1().pendingExpenseList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void approveCheque()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		n=0;
		String /*status="approved",*/notificationId="",type=""/*,loanNo="",shareHolderNo=""*/;
		selectedNotification.getBankId();
		Double.valueOf(selectedNotification.getAmount());
		notificationId=selectedNotification.getId();
		type=selectedNotification.getType();
		identity="approve";
		n=0;
		n=DBM.updateChequeStatusForDebit(type,notificationId,identity,/*selectedNotification.getItem_loan_fdr(), */chqClearDate,conn);

		if(n>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Amount Released Successfully!"));
			if(selectedNotification.getType().equalsIgnoreCase("Salary_Dr"))
			{
				DBM.approveSalaryDueCheque(selectedNotification.getSalryId(), /*chqClearDate,*/conn);
			}
			//DBM.decreaseBankBalance(bankId, amount,conn);
			expenseList=DBM.pendingExpenseList(conn);



			identity="";
		}

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
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(chqClearDate);
		String chqDt = formater.format(selectedNotification.getChequeDate());
		
		
		language = "Check Bounce Date-"+addDt+" --Amount"+amount+" --Reason-"+reason;
		value = "Checque Dd No-"+selectedNotification.getChequeNo()+" --Cheque Date-"+chqDt+" --Selected Id-"+selectedNotification.getId()+" --Type"+selectedNotification.getType()+" --Amount-"+selectedNotification.getAmount()+" --SalryId-"+selectedNotification.getSalryId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Approve Due Cheque","WEB",value,conn);
		return refNo;
	}


	private void addChqBounce()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		voucherNo=obj.generateVoucherNo("expense_table",conn);
		String query="insert into expense_table(expense_date,category,payment_mode,amount,chq_dd_no,chq_dd_date,bank_name,bank_branch,"
				+ " description,item,chequeStatus, no_of_item,voucher_no)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try
		{
			PreparedStatement st=conn.prepareStatement(query);
			Timestamp tp = new Timestamp(chqReturnDate.getTime());
			tp.setHours(0);
			tp.setMinutes(0);
			tp.setSeconds(0);
			tp.setNanos(0);
			
			Timestamp tp2 = new Timestamp(selectedNotification.getChequeDate().getTime());
			tp2.setHours(0);
			tp2.setMinutes(0);
			tp2.setSeconds(0);
			tp2.setNanos(0);
			
			st.setTimestamp(1,tp);
			st.setString(2, "Other_Dr");
			st.setString(3, "Cheque");
			st.setString(4, String.valueOf(amount));
			st.setString(5, selectedNotification.getChequeNo());
			st.setTimestamp(6, tp2);
			st.setString(7,selectedNotification.getBankId());
			st.setString(8,"na");
			st.setString(9, reason);
			st.setString(10, "Cheque Bounce Charge");
			st.setString(11, "cleared");
			st.setString(12, "1");
			st.setString(13, voucherNo);
			n=st.executeUpdate();
			if(n>0)
			{
				//obj.decreaseBankBalance(selectedNotification.getBankId(), amount,conn);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void denyCheque()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		String /*status="returned",bankId="",*/notificationId="",type="";
		//double amount=0;
		n=0;
		//bankId=selectedNotification.getBankId();
		amount=Double.valueOf(selectedNotification.getAmount());
		notificationId=selectedNotification.getId();
		type=selectedNotification.getType();

		identity="deny";
		int x=obj.updateChequeStatusForDebit(type,notificationId,identity,/*selectedNotification.getItem_loan_fdr(), */chqClearDate,conn);
		if(x>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cheque Denied Successfully!"));
			addChqBounce();
			if(selectedNotification.getType().equalsIgnoreCase("Salary_Dr"))
			{
				obj.denySalaryDueCheque(selectedNotification.getSalryId(), /*chqClearDate,*/conn);
			}
			expenseList=obj.pendingExpenseList(conn);




			identity="";
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
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(chqReturnDate);
		String chqDt = formater.format(selectedNotification.getChequeDate());
		
		
		language = "Check Bounce Date-"+addDt+" --Amount"+amount+" --Reason-"+reason;
		value = "Checque Dd No-"+selectedNotification.getChequeNo()+" --Cheque Date-"+chqDt+" --Selected Id-"+selectedNotification.getId()+" --Type"+selectedNotification.getType()+" --Amount-"+selectedNotification.getAmount()+" --SalryId-"+selectedNotification.getSalryId();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Deny Due Cheque","WEB",value,conn);
		return refNo;
	}


	public ArrayList<DueDebitInfo> getExpenseList() {
		return expenseList;
	}
	public void setExpenseList(ArrayList<DueDebitInfo> expenseList) {
		this.expenseList = expenseList;
	}

	public DueDebitInfo getSelectedNotification() {
		return selectedNotification;
	}
	public void setSelectedNotification(DueDebitInfo selectedNotification) {
		this.selectedNotification = selectedNotification;
	}

	public Date getChqReturnDate() {
		return chqReturnDate;
	}

	public void setChqReturnDate(Date chqReturnDate) {
		this.chqReturnDate = chqReturnDate;
	}

	public Date getChqClearDate() {
		return chqClearDate;
	}

	public void setChqClearDate(Date chqClearDate) {
		this.chqClearDate = chqClearDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
