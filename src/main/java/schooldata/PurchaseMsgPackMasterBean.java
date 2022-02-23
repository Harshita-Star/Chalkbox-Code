package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
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

@ManagedBean(name="purchaseMsgPackMaster")
@ViewScoped
public class PurchaseMsgPackMasterBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	MessagePackInfo selected;
	Double quantit,rate,tax,amount,totalAmount;
	String schoolId,quant="0",totAmt="0";
	String strRandomOtp,otpInput,contactNo;
	int randomOtp;
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public MessagePackInfo getSelected() {
		return selected;
	}
	public void setSelected(MessagePackInfo selected) {
		this.selected = selected;
	}
	public PurchaseMsgPackMasterBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		ArrayList<MessagePackInfo> pcklist = new ArrayList<>();
		MessagePackInfo ll = new MessagePackInfo();
		ll.setPackName("OTHERS");
		ll.setQuantity(" - ");
		ll.setRate(" - ");
		ll.setTax(" - ");
		ll.setTotalAmount(" - ");
		ll.setAmount(" - ");
		ll.setId("-1");
		pcklist.add(ll);
		ArrayList<MessagePackInfo> orglist=new DatabaseMethods1().selectAllMessagePackValues(conn);
		
		packList.addAll(pcklist);
		packList.addAll(orglist);
		
		contactNo = new DatabaseMethods1().contactNo("SMS Recharge Request", conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void checkAction()
	{
		if(selected.getId().equals("-1"))
		{
			PrimeFaces.current().executeInitScript("PF('othDlg').show()");
			PrimeFaces.current().ajax().update("othForm");
		}
		else
		{
			sendOTP();
		}
	}

	public String sendOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		
		
		if(selected.getId().equals("-1"))
		{
			if(Integer.valueOf(quant)<=0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Quantity Must Be Greater Than ZERO"));
				return "";
			}
			
			if(Integer.valueOf(totAmt)<=0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Amount Must Be Greater Than ZERO"));
				return "";
			}
			
			PrimeFaces.current().executeInitScript("PF('othDlg').hide()");
			PrimeFaces.current().ajax().update("othForm");
			selected.setQuantity(quant);
			selected.setTotalAmount(totAmt);
			selected.setRate("0");
			selected.setAmount(totAmt);
			selected.setTax("0");
		}

		
		strRandomOtp = "";
		randomOtp = (int)(Math.random()*9000)+1000;
		strRandomOtp = String.valueOf(randomOtp);
		// // System.out.println(strRandomOtp);
		String schName = DBM.getSMSSchoolName(schoolId, conn);
		String typemessage="Hello Admin,\n"
				+String.valueOf(randomOtp)+ " is your OTP for SMS Top Up of "+selected.getQuantity()+" Messages  - "+schName+".\nRegards.\nCB";

		new DatabaseMethods1().messageurlMasterAdmin(contactNo, typemessage/*,"masteradmin",conn*/);
		PrimeFaces.current().executeInitScript("PF('addSchool').show()");
		PrimeFaces.current().ajax().update("form4");
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "";

	}

	public void purchase()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		if(otpInput.equals(strRandomOtp))
		{
			int j = new DatabaseMethods1().checkChhotuRecharge(conn,0,schoolId,"approved");
			if(j==1)
			{
				int i = new DatabaseMethods1().updatePurchaseTableMaster(conn,selected,0,schoolId,"approved",username);
				if(i==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SMS Recharge successfull!"));
					new DatabaseMethods1().updateChhotuRechargeInMessageTransaction(conn,selected.getQuantity(),schoolId);
					quant=totAmt="0";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Purchase!"));

				}
			}
			else
			{
				int i = new DatabaseMethods1().purchaseAllValuesInTable(conn,selected,schoolId,"approved",username,"unpaid");
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SMS Recharge successfull!"));
					new DatabaseMethods1().addMessages(schoolId, new Date(), Integer.valueOf(selected.getQuantity()), conn);
					quant=totAmt="0";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur during Recharge!"));
				}
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}

		selected.setQuantity(" - ");
		selected.setTotalAmount(" - ");
		selected.setRate(" - ");
		selected.setAmount(" - ");
		selected.setTax(" - ");
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}
	public Double getQuantit() {
		return quantit;
	}
	public void setQuantit(Double quantit) {
		this.quantit = quantit;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}
	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
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
	public String getQuant() {
		return quant;
	}
	public void setQuant(String quant) {
		this.quant = quant;
	}
	public String getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}

