package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.IncomeList;

@ManagedBean(name="showIncome")
@SessionScoped
public class ShowIncomeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String category,selectedCategory,selectedYear,selectedFilter,selectedQuarter,selectedMonth,selectedWeek,payfrom;
	ArrayList<SelectItem> yearList,categoryList,monthList,quarterList,paytoList;
	boolean reportShow, reportShowOther,dailyShow,weeklyShow,monthlyShow,yearlyShow,customShow,quarterlyShow,reportShowCustom;
	ArrayList<IncomeList> incomeList;
	Date startingDate,endingDate,dailySelected,weeklySelected;
	int incomeAmount,totalCash,totalCheque;
	DatabaseMethods1 obj1=new DatabaseMethods1();

	public void selectedFilter()
	{
		if(selectedFilter.equals("Daily"))
		{
			dailyShow=true;
			weeklyShow=quarterlyShow=monthlyShow=yearlyShow=customShow=reportShow=reportShowCustom=false;
		}
		else if(selectedFilter.equals("Weekly"))
		{
			weeklyShow=true;
			dailyShow=monthlyShow=yearlyShow=customShow=quarterlyShow=reportShow=reportShowCustom=false;
		}
		else if(selectedFilter.equals("Monthly"))
		{
			monthlyShow=true;
			weeklyShow=dailyShow=yearlyShow=customShow=quarterlyShow=reportShow=reportShowCustom=false;
		}
		else if(selectedFilter.equals("Yearly"))
		{
			yearlyShow=true;
			weeklyShow=monthlyShow=dailyShow=customShow=quarterlyShow=reportShow=reportShowCustom=false;
		}
		else if(selectedFilter.equals("Customreport"))
		{
			customShow=true;
			weeklyShow=monthlyShow=yearlyShow=dailyShow=quarterlyShow=reportShow=reportShowCustom=false;
		}
		else if(selectedFilter.equals("Quarterly"))
		{
			quarterlyShow=true;
			dailyShow=weeklyShow=monthlyShow=yearlyShow=customShow=reportShow=reportShowCustom=false;
		}
		else
		{
			quarterlyShow=dailyShow=weeklyShow=monthlyShow=yearlyShow=customShow=reportShow=reportShowCustom=false;
		}
		reportShowOther=false;
	}

	public void reportCategoryListener()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(category.equals("-1"))
		{
			selectedCategory="All";
		}
		else
		{
			selectedCategory=category;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void monthlyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;totalCash=0;totalCheque=0;
		Date selectedDate=new Date();

		Timestamp tt=new Timestamp(selectedDate.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));
		if(selectedMonth.length()==1)
		{
			selectedMonth="0"+selectedMonth;
		}
		d1=d1.replace(0, 4, selectedYear);
		d1=d1.replace(5, 7, selectedMonth);
		d1=d1.replace(8, 10, "01");
		StringBuffer d2=new StringBuffer(d1);

		Calendar c = Calendar.getInstance();
		int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		d2=d2.replace(8, 10, String.valueOf(monthMaxDays));

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		selectedDate=sdf.parse(String.valueOf(d1));

		incomeList=obj1.filteredIncome( selectedDate,endDate,category,payfrom,conn);
		if(incomeList.size()>0)
		{
			for(IncomeList info:incomeList)
			{

				if(info.getPaymentMode().equalsIgnoreCase("Cash"))
				{
					totalCash+=info.getAmount();
				}
				else
				{
					totalCheque+=info.getAmount();
				}
			}

			incomeAmount=totalCash+totalCheque;
		}
		reportShowOther=reportShow=true;
		reportShowCustom=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void quarterlyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;totalCash=0;totalCheque=0;
		Date selectedDate=new Date();
		String endQuarter=null;
		Timestamp tt=new Timestamp(selectedDate.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));
		if(selectedQuarter.equals("1"))
		{
			selectedQuarter="01";
			endQuarter="05";
		}
		else if(selectedQuarter.equals("2"))
		{
			selectedQuarter="05";
			endQuarter="09";
		}
		else if(selectedQuarter.equals("3"))
		{
			selectedQuarter="09";
			endQuarter="01";
		}

		d1=d1.replace(5, 7, selectedQuarter);
		d1=d1.replace(8, 10, "01");
		StringBuffer d2=new StringBuffer(d1);

		d2=d2.replace(5, 7, endQuarter);

		if(selectedQuarter.equals("09"))
		{

			d2=d2.replace(0, 4, String.valueOf(Integer.parseInt(d1.substring(0,4))+1 )) ;
		}

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		selectedDate=sdf.parse(String.valueOf(d1));

		incomeList=obj1.filteredIncome(selectedDate,endDate,category,payfrom,conn);
		if(incomeList.size()>0)
		{
			for(IncomeList info:incomeList)
			{
				if(info.getPaymentMode().equalsIgnoreCase("Cash"))
				{
					totalCash+=info.getAmount();
				}
				else
				{
					totalCheque+=info.getAmount();
				}
			}
			incomeAmount=totalCash+totalCheque;
		}
		reportShowOther=reportShow=true;
		reportShowCustom=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void yearlyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;totalCash=0;totalCheque=0;
		Date selectedDate=new Date();

		Timestamp tt=new Timestamp(selectedDate.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));

		d1=d1.replace(0, 4, selectedYear);
		d1=d1.replace(8, 10, "01");
		d1=d1.replace(5, 7, "01");

		StringBuffer d2=new StringBuffer(d1);

		d2=d2.replace(8, 10, "31");
		d2=d2.replace(5, 7, "12");

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		selectedDate=sdf.parse(String.valueOf(d1));

		incomeList=obj1.filteredIncome( selectedDate,endDate,category,payfrom,conn);


		if(incomeList.size()>0)
		{
			for(IncomeList info:incomeList)
			{
				if(info.getPaymentMode().equalsIgnoreCase("Cash"))
				{
					totalCash+=info.getAmount();
				}
				else
				{
					totalCheque+=info.getAmount();
				}
			}
			incomeAmount=totalCash+totalCheque;
		}
		reportShowOther=reportShow=true;
		reportShowCustom=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void weeklyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;totalCash=0;totalCheque=0;

		Timestamp tt=new Timestamp(weeklySelected.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));

		int week=Integer.parseInt(d1.substring(8, 10));
		week+=6;

		StringBuffer d2=new StringBuffer(d1);

		d2=d2.replace(8, 10, String.valueOf(week));

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		weeklySelected=sdf.parse(String.valueOf(d1));
		incomeList=obj1.filteredIncome(weeklySelected,endDate,category,payfrom,conn);
		if(incomeList.size()>0)
		{
			for(IncomeList info:incomeList)
			{
				if(info.getPaymentMode().equalsIgnoreCase("Cash"))
				{
					totalCash+=info.getAmount();
				}
				else
				{
					totalCheque+=info.getAmount();
				}
			}
			incomeAmount=totalCash+totalCheque;
		}
		reportShowOther=reportShow=true;
		reportShowCustom=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void init()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=obj1.allIncomeExpenseCategory("Income",conn);
		paytoList=obj1.allPayto("receive",conn);
		monthList=obj1.allMonths();

		yearList=obj1.allYearList();

		quarterList=new ArrayList<>();
		for(int i=1;i<=4;i++)
		{
			SelectItem item1=new SelectItem();
			item1.setLabel(String.valueOf(i));
			item1.setValue(String.valueOf(i));

			quarterList.add(item1);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public ShowIncomeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=obj1.allIncomeExpenseCategory("Income",conn);

		monthList=obj1.allMonths();

		yearList=obj1.allYearList();

		quarterList=new ArrayList<SelectItem>();
		for(int i=1;i<=4;i++)
		{
			SelectItem item1=new SelectItem();
			item1.setLabel(String.valueOf(i));
			item1.setValue(String.valueOf(i));

			quarterList.add(item1);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public String printIncomeReport()
	{
		return "printIncomeReport";
	}

	public void dailyReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=totalCash=totalCheque=0;
		try
		{
			Date endDate=new Date(dailySelected.getTime());
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			incomeList=obj1.filteredIncome(dailySelected,endDate,category,payfrom,conn);
			if(incomeList.size()>0)
			{
				for(IncomeList info:incomeList)
				{
					if(info.getPaymentMode().equalsIgnoreCase("Cash"))
					{
						totalCash+=info.getAmount();
					}
					else
					{
						totalCheque+=info.getAmount();
					}
				}
				incomeAmount=totalCash+totalCheque;
			}
			reportShow=reportShowOther=true;
			reportShowCustom=false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void filterTable()
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;totalCash=0;totalCheque=0;
		int flag=0;
		if(endingDate.compareTo(startingDate)<0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ending date must be greater than starting date", "Validation error"));
		}
		if(flag==0)
		{
			try
			{
				incomeList=obj1.filteredIncome(startingDate, endingDate,category,payfrom,conn);
				if(incomeList.size()>0)
				{
					for(IncomeList info:incomeList)
					{
						if(info.getPaymentMode().equalsIgnoreCase("Cash"))
						{
							totalCash+=info.getAmount();
						}
						else
						{
							totalCheque+=info.getAmount();
						}
					}
					incomeAmount=totalCash+totalCheque;
				}
				reportShowOther=reportShow=false;
				reportShowCustom=true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public void clearFilter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeList=obj1.allIncome(conn);
		if(incomeList.size()>0)
		{
			for(IncomeInfo info:incomeList)
			{
				if(info.getIncome().equals("Income"))
				{
					incomeAmount+=info.getAmount();
				}
			}
		}
		reportShow=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public String getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public boolean isReportShowOther() {
		return reportShowOther;
	}
	public void setReportShowOther(boolean reportShowOther) {
		this.reportShowOther = reportShowOther;
	}
	public int getTotalCash() {
		return totalCash;
	}
	public void setTotalCash(int totalCash) {
		this.totalCash = totalCash;
	}
	public int getTotalCheque() {
		return totalCheque;
	}
	public void setTotalCheque(int totalCheque) {
		this.totalCheque = totalCheque;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSelectedFilter() {
		return selectedFilter;
	}
	public void setSelectedFilter(String selectedFilter) {
		this.selectedFilter = selectedFilter;
	}
	public String getSelectedQuarter() {
		return selectedQuarter;
	}
	public void setSelectedQuarter(String selectedQuarter) {
		this.selectedQuarter = selectedQuarter;
	}
	public String getSelectedMonth() {
		return selectedMonth;
	}
	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}
	public String getSelectedWeek() {
		return selectedWeek;
	}
	public void setSelectedWeek(String selectedWeek) {
		this.selectedWeek = selectedWeek;
	}
	public boolean isReportShow() {
		return reportShow;
	}
	public void setReportShow(boolean reportShow) {
		this.reportShow = reportShow;
	}
	public ArrayList<SelectItem> getQuarterList() {
		return quarterList;
	}
	public void setQuarterList(ArrayList<SelectItem> quarterList) {
		this.quarterList = quarterList;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public Date getEndingDate() {
		return endingDate;
	}
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}
	public int getIncomeAmount() {
		return incomeAmount;
	}
	public void setIncomeAmount(int incomeAmount) {
		this.incomeAmount = incomeAmount;
	}
	public Date getDailySelected() {
		return dailySelected;
	}
	public void setDailySelected(Date dailySelected) {
		this.dailySelected = dailySelected;
	}
	public Date getWeeklySelected() {
		return weeklySelected;
	}
	public void setWeeklySelected(Date weeklySelected) {
		this.weeklySelected = weeklySelected;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public String getSelectedYear() {
		return selectedYear;
	}
	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}
	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public boolean isReportShowCustom() {
		return reportShowCustom;
	}
	public void setReportShowCustom(boolean reportShowCustom) {
		this.reportShowCustom = reportShowCustom;
	}

	public boolean isDailyShow() {
		return dailyShow;
	}
	public void setDailyShow(boolean dailyShow) {
		this.dailyShow = dailyShow;
	}
	public boolean isWeeklyShow() {
		return weeklyShow;
	}
	public void setWeeklyShow(boolean weeklyShow) {
		this.weeklyShow = weeklyShow;
	}
	public boolean isMonthlyShow() {
		return monthlyShow;
	}
	public void setMonthlyShow(boolean monthlyShow) {
		this.monthlyShow = monthlyShow;
	}
	public boolean isYearlyShow() {
		return yearlyShow;
	}
	public void setYearlyShow(boolean yearlyShow) {
		this.yearlyShow = yearlyShow;
	}
	public boolean isCustomShow() {
		return customShow;
	}
	public void setCustomShow(boolean customShow) {
		this.customShow = customShow;
	}
	public boolean isQuarterlyShow() {
		return quarterlyShow;
	}
	public void setQuarterlyShow(boolean quarterlyShow) {
		this.quarterlyShow = quarterlyShow;
	}

	public ArrayList<IncomeList> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(ArrayList<IncomeList> incomeList) {
		this.incomeList = incomeList;
	}

	public String getPayfrom() {
		return payfrom;
	}

	public void setPayfrom(String payfrom) {
		this.payfrom = payfrom;
	}

	public ArrayList<SelectItem> getPaytoList() {
		return paytoList;
	}

	public void setPaytoList(ArrayList<SelectItem> paytoList) {
		this.paytoList = paytoList;
	}

}
