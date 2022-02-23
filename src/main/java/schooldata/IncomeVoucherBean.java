package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "incomeVoucher")
@ViewScoped
public class IncomeVoucherBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String name, particular, receivedFrom, paymode, checkno, id1, amoutninword, amountword, date3, bankName;
	int id, total;
	Date date;
	boolean show;

	public IncomeVoucherBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		DatabaseMethods1 obj = new DatabaseMethods1();
		name = (String) ss.getAttribute("category");
		total = (Integer) ss.getAttribute("total");
		particular = (String) ss.getAttribute("particular");
		receivedFrom = (String) ss.getAttribute("from");
		paymode = (String) ss.getAttribute("paymode");
		checkno = (String) ss.getAttribute("checkno1");
		date = (Date) ss.getAttribute("date1");
		bankName = (String) ss.getAttribute("bankName");
		if (paymode.equals("Cash")) {
			show = false;
		} else {
			show = true;
		}

		id = obj.getSno(conn);
		int date1 = date.getDate();
		int month = date.getMonth();
		month = month + 1;

		int year = date.getYear();
		year = year + 1900;
		date3 = String.valueOf(date1) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
		amountword = obj.numberToWords(total);

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public String getReceivedFrom() {
		return receivedFrom;
	}

	public void setReceivedFrom(String receivedFrom) {
		this.receivedFrom = receivedFrom;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPaymode() {
		return paymode;
	}

	public void setPaymode(String paymode) {
		this.paymode = paymode;
	}

	public String getCheckno() {
		return checkno;
	}

	public void setCheckno(String checkno) {
		this.checkno = checkno;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId1() {
		return id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParticular() {
		return particular;
	}

	public void setParticular(String particular) {
		this.particular = particular;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getDate3() {
		return date3;
	}

	public void setDate3(String date3) {
		this.date3 = date3;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getAmountword() {
		return amountword;
	}

	public void setAmountword(String amountword) {
		this.amountword = amountword;
	}
}
