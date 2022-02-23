package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "expenseTaxReportBean")
@ViewScoped
public class ExpenseTaxReportBean implements Serializable {

	String taxYear, taxMonth, monthName;
	ArrayList<IncomeList> iList;
	ArrayList<SelectItem> yearlist = new ArrayList<>();
	double totalAmount;
	Date startDate = new Date(),endDate = new Date();
	String strStartDate="",strEndDate = "";

	public ExpenseTaxReportBean()
	{
		totalAmount = 0;
		taxMonth = String.valueOf(new Date().getMonth() + 1);
		taxYear = String.valueOf(new Date().getYear() + 1900);
		for (int i = 2015; i <= Integer.valueOf(taxYear); i++) {
			SelectItem LS = new SelectItem();
			LS.setValue(String.valueOf(i));
			LS.setLabel(String.valueOf(i));
			yearlist.add(LS);
		}

		searchReport();
	}

	public void searchReport() {
		iList = new ArrayList<>();
		totalAmount = 0;
		Connection conn = DataBaseConnection.javaConnection();
		monthName = new DatabaseMethods1().monthNameByNumber(Integer.parseInt(taxMonth));
		iList =new DataBaseMethodsIncome_Expense().allExpenseTax(taxMonth, taxYear,strStartDate,strEndDate,"month", conn);
		for (IncomeList ss : iList) {
			totalAmount += ss.getTaxAmount();
		}

		Collections.sort(iList);

		int count = 1;
		for (IncomeList ss : iList) {
			ss.setsNo(count++);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	
	public void searchDateReport() {
		iList = new ArrayList<>();
		totalAmount = 0;
		Connection conn = DataBaseConnection.javaConnection();
		strStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		strEndDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		iList =new DataBaseMethodsIncome_Expense().allExpenseTax(taxMonth, taxYear,strStartDate,strEndDate,"date", conn);
		for (IncomeList ss : iList) {
			totalAmount += ss.getTaxAmount();
		}

		Collections.sort(iList);

		int count = 1;
		for (IncomeList ss : iList) {
			ss.setsNo(count++);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public String print() {
		return "printExpenseTaxReport.xhtml";
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

	public ArrayList<IncomeList> getiList() {
		return iList;
	}

	public void setiList(ArrayList<IncomeList> iList) {
		this.iList = iList;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
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

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

}
