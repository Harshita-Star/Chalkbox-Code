package reports_module;

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

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodsIncome_Expense;
import schooldata.DatabaseMethods1;
import schooldata.IncomeList;

@ManagedBean(name="combinedTaxReport")
@ViewScoped

public class CombinedTaxReportBean implements Serializable
{
	String taxYear, taxMonth, monthName;
	ArrayList<IncomeList> iList,eList,fList;
	ArrayList<SelectItem> yearlist = new ArrayList<>();
	double totalAmount,totalIncTax,totalExpTax;
	Date startDate = new Date(),endDate = new Date();
	String strStartDate,strEndDate = "";
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String schoolId,sessionValue;

	public CombinedTaxReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schoolId, conn);
		totalAmount = 0;
		taxMonth = String.valueOf(new Date().getMonth() + 1);
		taxYear = String.valueOf(new Date().getYear() + 1900);
		for (int i = 2015; i <= Integer.valueOf(taxYear); i++) {
			SelectItem LS = new SelectItem();
			LS.setValue(String.valueOf(i));
			LS.setLabel(String.valueOf(i));
			yearlist.add(LS);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		searchReport();
	}

	public void searchReport() {
		iList = new ArrayList<>();
		eList = new ArrayList<>();
		fList = new ArrayList<>();
		
		totalAmount=totalIncTax=totalExpTax = 0;
		Connection conn = DataBaseConnection.javaConnection();
		monthName =obj1.monthNameByNumber(Integer.parseInt(taxMonth));
		iList =new DataBaseMethodsIncome_Expense().allIncomeTax(taxMonth, taxYear,strStartDate,strEndDate,"month", conn);
		eList =new DataBaseMethodsIncome_Expense().allExpenseTax(taxMonth, taxYear,strStartDate,strEndDate,"month", conn);
		
		fList.addAll(iList);
		fList.addAll(eList);
		
		Collections.sort(fList);
		
		for (IncomeList ss : fList) 
		{
			if(ss.getIncome().equalsIgnoreCase("income"))
			{
				totalIncTax += ss.getIncTaxAmt();
			}
			else
			{
				totalExpTax += ss.getExpTaxAmt();
			}
		}
		
		totalAmount = totalIncTax-totalExpTax;


		/*int count = 1;
		for (IncomeList ss : iList) {
			ss.setsNo(count++);
		}*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	
	public void searchDateReport() {
		iList = new ArrayList<>();
		eList = new ArrayList<>();
		fList = new ArrayList<>();
		
		totalAmount=totalIncTax=totalExpTax = 0;
		Connection conn = DataBaseConnection.javaConnection();
		strStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		strEndDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		iList =new DataBaseMethodsIncome_Expense().allIncomeTax(taxMonth, taxYear,strStartDate,strEndDate,"date", conn);
		eList =new DataBaseMethodsIncome_Expense().allExpenseTax(taxMonth, taxYear,strStartDate,strEndDate,"date", conn);
		
		fList.addAll(iList);
		fList.addAll(eList);
		
		Collections.sort(fList);
		
		for (IncomeList ss : fList) 
		{
			if(ss.getIncome().equalsIgnoreCase("income"))
			{
				totalIncTax += ss.getIncTaxAmt();
			}
			else
			{
				totalExpTax += ss.getExpTaxAmt();
			}
		}
		
		totalAmount = totalIncTax-totalExpTax;

		/*int count = 1;
		for (IncomeList ss : iList) {
			ss.setsNo(count++);
		}*/

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

	public ArrayList<IncomeList> geteList() {
		return eList;
	}

	public void seteList(ArrayList<IncomeList> eList) {
		this.eList = eList;
	}

	public ArrayList<IncomeList> getfList() {
		return fList;
	}

	public void setfList(ArrayList<IncomeList> fList) {
		this.fList = fList;
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

	public double getTotalIncTax() {
		return totalIncTax;
	}

	public void setTotalIncTax(double totalIncTax) {
		this.totalIncTax = totalIncTax;
	}

	public double getTotalExpTax() {
		return totalExpTax;
	}

	public void setTotalExpTax(double totalExpTax) {
		this.totalExpTax = totalExpTax;
	}
}
