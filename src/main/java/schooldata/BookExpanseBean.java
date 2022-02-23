package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name = "bookExpense")
@ViewScoped
public class BookExpanseBean implements Serializable {
	private static final long serialVersionUID = 48055663406166727L;
	
	String regex=RegexPattern.REGEX;
	String amount, description,taxYear,taxMonth,partyName;
	Date expanseDate;
	int month,year;
	Date minDate,maxDate;
	ArrayList<SelectItem> yearlist=new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	double taxamount=0;
	@SuppressWarnings("deprecation")
	public BookExpanseBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String type=(String) ss.getAttribute("type");
		expanseDate = new Date();
		taxMonth=String.valueOf(new Date().getMonth()+1);
		taxYear=String.valueOf(new Date().getYear()+1900);

		maxDate=expanseDate;
		if(type.equalsIgnoreCase("master_admin"))
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -365);
			minDate=cal.getTime();
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -3);
			minDate=cal.getTime();
		}
		//  minDate.setDate(expanseDate.getDate()-3);

		for(int i=2015;i<=2030;i++)
		{
			SelectItem LS=new SelectItem();
			LS.setValue(String.valueOf(i));
			LS.setLabel(String.valueOf(i));
			yearlist.add(LS);
		}

		month=expanseDate.getMonth()+1;
		year=expanseDate.getYear()+1900;
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void addExpense()
	{
		Connection conn= DataBaseConnection.javaConnection();
		double totalAmount = Double.parseDouble(amount);

		if (totalAmount >= 0)
		{
			int i = new DataBaseMethodsIncome_Expense().addBookExpense(partyName, amount, description,expanseDate,taxamount,taxMonth,taxYear,conn);
			if(i >= 1)
			{
				//new SideMenuBean().completeCheck();
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Expense Booked Successfully"));
				amount = description =partyName="";
				taxamount=0;
				taxMonth=String.valueOf(new Date().getMonth()+1);
				taxYear=String.valueOf(new Date().getYear()+1900);
				expanseDate = new Date();
			}
			else
			{
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Some Error Occur Please Try Again"));
			}
		}
		else
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Insufficient Amount For Expense"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public Date getExpanseDate() {
		return expanseDate;
	}
	public void setExpanseDate(Date expanseDate) {
		this.expanseDate = expanseDate;
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
	public Date getMinDate() {
		return minDate;
	}
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public double getTaxamount() {
		return taxamount;
	}

	public void setTaxamount(double taxamount) {
		this.taxamount = taxamount;
	}

	public String getTaxYear() {
		return taxYear;
	}

	public void setTaxYear(String taxYear) {
		this.taxYear = taxYear;
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

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
