package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@ManagedBean(name="unpaidBillsMaster")
@ViewScoped

public class UnpaidBillsMasterBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<BillInfo> list = new ArrayList<>();
	BillInfo selectedBill;
	StreamedContent filee;
	Date payDate = new Date();
	String file="na",orderid,paidBy;
	int days=0;
	String strRandomOtp,otpInput,contactNo;
	int randomOtp;

	public UnpaidBillsMasterBean()
	{
		file="na";
		Connection conn = DataBaseConnection.javaConnection();
		contactNo=new DatabaseMethods1().contactNo("Bill Extend",conn);
		list = new DataBaseMethodsReports().allUpaidBills(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(selectedBill.getSchid(),conn);

		file = selectedBill.getFile();

		if(file==null || file.equalsIgnoreCase("na") || file.equals(""))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Result file unavailable. Please try after sometime."));
		}
		else
		{
			file = ls.getDownloadpath()+selectedBill.getFile();
			PrimeFaces.current().ajax().update("form2");
			PrimeFaces.current().executeInitScript("PF('alrtDlg').show();");
		}

		//PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getFilename()+"')");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void edit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(otpInput.equals(strRandomOtp))
		{
			selectedBill.getDueDate().setDate(selectedBill.getDueDate().getDate()+days);

			int i = new DataBaseMethodsReports().extendBillDueDate(selectedBill,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Due Date Extended!"));
				list = new DataBaseMethodsReports().allUpaidBills(conn);
				days=0;
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("form4");
				PrimeFaces.current().executeInitScript("PF('addSchool').hide();");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured. Please Try Again!"));
				days=0;
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}

		otpInput = "";
		days = 0;

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pay()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) ss.getAttribute("username");
		String currDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(payDate);
		if(otpInput.equals(strRandomOtp))
		{
			int i = new DataBaseMethodsReports().paySchoolBill(paidBy, userid, selectedBill.getId(), orderid, currDate, conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bill Paid Successfully!"));
				list = new DataBaseMethodsReports().allUpaidBills(conn);
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("payOtpForm");
				PrimeFaces.current().executeInitScript("PF('payOtp').hide();");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured. Please Try Again!"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}

		otpInput = "";
		paidBy = "";
		orderid = "";
		payDate = new Date();

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DataBaseMethodsReports().deleteSchoolBill(selectedBill.getId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bill Deleted!"));
			list = new DataBaseMethodsReports().allUpaidBills(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error!"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void sendOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(days<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No. of days must be greater than zero"));
		}
		else
		{
			PrimeFaces.current().executeInitScript("PF('addSchool').show()");
			PrimeFaces.current().ajax().update("form4");
			strRandomOtp = "";
			randomOtp = (int)(Math.random()*9000)+1000;
			strRandomOtp = String.valueOf(randomOtp);

			String typemessage="Hello Admin,\n"
					+String.valueOf(randomOtp)+ " is your OTP to Extend Bill Due Date.\nBill : "+selectedBill.getBillNo()+"\nSch : "+selectedBill.getSmsSchoolName()+".\nRegards.\nCB";

			new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void sendPayOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		PrimeFaces.current().executeInitScript("PF('pay').hide()");
		PrimeFaces.current().ajax().update("payform");
		PrimeFaces.current().executeInitScript("PF('payOtp').show()");
		PrimeFaces.current().ajax().update("payOtpForm");
		strRandomOtp = "";
		randomOtp = (int)(Math.random()*9000)+1000;
		strRandomOtp = String.valueOf(randomOtp);

		String typemessage="Hello Admin,\n"
				+String.valueOf(randomOtp)+ " is your OTP to Pay Bill.\nBill : "+selectedBill.getBillNo()+"\nSch : "+selectedBill.getSmsSchoolName()+".\nRegards.\nCB";


		new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public ArrayList<BillInfo> getList() {
		return list;
	}

	public void setList(ArrayList<BillInfo> list) {
		this.list = list;
	}

	public BillInfo getSelectedBill() {
		return selectedBill;
	}

	public void setSelectedBill(BillInfo selectedBill) {
		this.selectedBill = selectedBill;
	}

	public StreamedContent getFilee() {
		return filee;
	}

	public void setFilee(StreamedContent filee) {
		this.filee = filee;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}



	public int getDays() {
		return days;
	}



	public void setDays(int days) {
		this.days = days;
	}



	public String getOtpInput() {
		return otpInput;
	}



	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}



	public String getOrderid() {
		return orderid;
	}



	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}



	public String getPaidBy() {
		return paidBy;
	}



	public void setPaidBy(String paidBy) {
		this.paidBy = paidBy;
	}



	public Date getPayDate() {
		return payDate;
	}



	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}



	public String getRegex() {
		return regex;
	}



	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
