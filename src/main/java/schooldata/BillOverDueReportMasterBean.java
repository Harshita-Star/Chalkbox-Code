package schooldata;

import java.io.IOException;
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

import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@SuppressWarnings("serial")
@ManagedBean(name="billOverDueReportMaster")
@ViewScoped

public class BillOverDueReportMasterBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<BillInfo> list = new ArrayList<>();
	BillInfo selectedBill;
	StreamedContent filee;
	String file="na";
	int days=0;
	String date;
	String strRandomOtp,otpInput,contactNo;
	int randomOtp,count1,count2,count3,count4;
	boolean showPaid=false;
	
	public BillOverDueReportMasterBean()
	{
		file="na";
		check1();
		Connection conn = DataBaseConnection.javaConnection();
		contactNo=new DatabaseMethods1().contactNo("Bill Extend",conn);
		String dueDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		count1 = new DataBaseMethodsReports().countOfAllBills(dueDate,conn);

		count2 = new DataBaseMethodsReports().countOfAllBills("unpaid",conn);

		count3 = new DataBaseMethodsReports().countOfAllBills("paid",conn);

		count4 = new DataBaseMethodsReports().countOfAllBills("all",conn);

		try {
			conn.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	public void check1()
	{
		Connection conn = DataBaseConnection.javaConnection();

		String dueDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		list = new DataBaseMethodsReports().allOverdueBills(dueDate,conn);
		showPaid=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check2()
	{
		Connection conn = DataBaseConnection.javaConnection();

		list = new DataBaseMethodsReports().allOverdueBills("unpaid",conn);
		showPaid=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check3()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list = new DataBaseMethodsReports().allOverdueBills("paid",conn);
		showPaid=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void check4()
	{
		Connection conn = DataBaseConnection.javaConnection();

		list = new DataBaseMethodsReports().allOverdueBills("all",conn);
		showPaid=true;
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
				days=0;
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("billOverDueReportMaster.xhtml");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error!"));
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

	public void sendOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		new DatabaseMethods1();
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
					+String.valueOf(randomOtp)+ " is your OTP to Extend Bill Due Date - "+selectedBill.getSchoolName()+".Treat this as confidential.\nRegards.\nTeam Chalkbox.";

			new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public int getCount1() {
		return count1;
	}
	public void setCount1(int count1) {
		this.count1 = count1;
	}
	public int getCount2() {
		return count2;
	}
	public void setCount2(int count2) {
		this.count2 = count2;
	}
	public int getCount3() {
		return count3;
	}
	public void setCount3(int count3) {
		this.count3 = count3;
	}
	public int getCount4() {
		return count4;
	}
	public void setCount4(int count4) {
		this.count4 = count4;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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



	public String getStrRandomOtp() {
		return strRandomOtp;
	}



	public void setStrRandomOtp(String strRandomOtp) {
		this.strRandomOtp = strRandomOtp;
	}



	public String getOtpInput() {
		return otpInput;
	}



	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}



	public int getRandomOtp() {
		return randomOtp;
	}



	public void setRandomOtp(int randomOtp) {
		this.randomOtp = randomOtp;
	}
	public boolean isShowPaid() {
		return showPaid;
	}
	public void setShowPaid(boolean showPaid) {
		this.showPaid = showPaid;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}


}
