package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;



@ManagedBean(name="printVoucher")
@ViewScoped
public class PrintVoucherBean implements Serializable
{
	String voucherNo,voucherName, type, paymentMode,payTo, vCategory, description, amount, vDateStr, amountInwords,remark,chNo,bnknm,chkdt;
	Date vDate,chequeDate;
	String maintype;
	public PrintVoucherBean()
	{
		Connection con=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		voucherName=(String) ss.getAttribute("voucherName");
		voucherNo=(String) ss.getAttribute("voucherNo");
		vDate=(Date) ss.getAttribute("expDate");
		chequeDate=(Date) ss.getAttribute("chequeDate");
		amount=(String) ss.getAttribute("amount");
		type=(String) ss.getAttribute("vchtype");
		description=(String) ss.getAttribute("description");
		payTo=(String) ss.getAttribute("payTo");
		vCategory=(String) ss.getAttribute("being");
		paymentMode=(String) ss.getAttribute("payMode");
		remark=(String) ss.getAttribute("remark");
		chNo=(String) ss.getAttribute("chequeNo");
		bnknm=(String) ss.getAttribute("bankName");

		if(type.equalsIgnoreCase("credit"))
		{
			maintype="Received From";
		}
		else
		{
			maintype="Pay To";
		}


		if(vDate!=null)
		{
			vDateStr=new SimpleDateFormat("dd/MM/yyyy").format(vDate);
		}
		if(chequeDate!=null)
		{
			chkdt=new SimpleDateFormat("dd/MM/yyyy").format(chequeDate);
		}

		if(amount.contains("."))
		{
			String arr[]=amount.split("\\.");
			amount=arr[0];
		}
		amountInwords=new DatabaseMethods1().numberToWords(Integer.parseInt(amount));

		try
		{
			bnknm=new DatabaseMethods1().bankNameById(bnknm, con);
			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getvCategory() {
		return vCategory;
	}
	public void setvCategory(String vCategory) {
		this.vCategory = vCategory;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getvDateStr() {
		return vDateStr;
	}
	public void setvDateStr(String vDateStr) {
		this.vDateStr = vDateStr;
	}
	public Date getvDate() {
		return vDate;
	}
	public void setvDate(Date vDate) {
		this.vDate = vDate;
	}
	public String getAmountInwords() {
		return amountInwords;
	}
	public void setAmountInwords(String amountInwords) {
		this.amountInwords = amountInwords;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getVoucherName() {
		return voucherName;
	}
	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}
	public String getChNo() {
		return chNo;
	}
	public void setChNo(String chNo) {
		this.chNo = chNo;
	}
	public String getBnknm() {
		return bnknm;
	}
	public void setBnknm(String bnknm) {
		this.bnknm = bnknm;
	}
	public String getChkdt() {
		return chkdt;
	}
	public void setChkdt(String chkdt) {
		this.chkdt = chkdt;
	}
	public Date getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	public String getPayTo() {
		return payTo;
	}
	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}
	public String getMaintype() {
		return maintype;
	}
	public void setMaintype(String maintype) {
		this.maintype = maintype;
	}


}

