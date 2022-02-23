package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;


@ManagedBean(name="report")
@ViewScoped
public class ReportGeneration implements Serializable {

	private static final long serialVersionUID = 1L;
	String selectedFilter,selectedQuarter;
	String selectedMonth,selectedWeek;
	boolean reportShow;
	ArrayList<SelectItem> quarterList;
	int toatalamount;
	Date startingDate,endingDate;
	ArrayList<IncomeList> report;
	int totalAmount;
	int incomeAmount,expenseAmount,feeAmount,otherIncAmount;
	String selectedYear;
	ArrayList<SelectItem> yearList;
	boolean dailyShow,weeklyShow,monthlyShow,yearlyShow,customShow,quarterlyShow;
	Date dailySelected,weeklySelected;
	ArrayList<SelectItem> monthList;
	boolean show;



	public void quarterlyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
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

		report=new DatabaseMethods1().filteredReportTable(selectedDate, endDate,conn);
		if(report.size()>0)
		{
			for(IncomeList info:report)
			{
				if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
				{
					otherIncAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
				{
					feeAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else
				{
					expenseAmount+=info.getAmount();
					//totalAmount-=info.getAmount();
				}
			}

			incomeAmount = otherIncAmount + feeAmount;
			totalAmount = incomeAmount - expenseAmount;
		}

		Collections.sort(report);

		reportShow=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void yearlyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
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

		report=new DatabaseMethods1().filteredReportTable(selectedDate, endDate,conn);
		if(report.size()>0)
		{
			for(IncomeList info:report)
			{
				if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
				{
					otherIncAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
				{
					feeAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else
				{
					expenseAmount+=info.getAmount();
					//totalAmount-=info.getAmount();
				}
			}

			incomeAmount = otherIncAmount + feeAmount;
			totalAmount = incomeAmount - expenseAmount;
		}
		Collections.sort(report);
		reportShow=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void weeklyReport() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
		Timestamp tt=new Timestamp(weeklySelected.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));

		int week=Integer.parseInt(d1.substring(8, 10));
		week+=6;

		StringBuffer d2=new StringBuffer(d1);

		d2=d2.replace(8, 10, String.valueOf(week));

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		weeklySelected=sdf.parse(String.valueOf(d1));

		report=new DatabaseMethods1().filteredReportTable( weeklySelected,endDate,conn);
		if(report.size()>0)
		{
			for(IncomeList info:report)
			{
				if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
				{
					otherIncAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
				{
					feeAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else
				{
					expenseAmount+=info.getAmount();
					//totalAmount-=info.getAmount();
				}
			}

			incomeAmount = otherIncAmount + feeAmount;
			totalAmount = incomeAmount - expenseAmount;
		}
		Collections.sort(report);
		reportShow=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showTable()
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
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
				report=new DatabaseMethods1().filteredReportTable(startingDate, endingDate,conn);
				if(report.size()>0)
				{
					for(IncomeList info:report)
					{
						if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
						{
							otherIncAmount+=info.getAmount();
							//totalAmount+=info.getAmount();
						}
						else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
						{
							feeAmount+=info.getAmount();
							//totalAmount+=info.getAmount();
						}
						else
						{
							expenseAmount+=info.getAmount();
							//totalAmount-=info.getAmount();
						}
					}

					incomeAmount = otherIncAmount + feeAmount;
					totalAmount = incomeAmount - expenseAmount;
					Collections.sort(report);
				}

				reportShow=true;
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

	public void selectedFilter()
	{
		if(selectedFilter.equals("Daily"))
		{
			dailyShow=true;

			weeklyShow=false;
			quarterlyShow=false;
			monthlyShow=false;
			yearlyShow=false;
			customShow=false;
			reportShow=false;

		}
		else if(selectedFilter.equals("Weekly"))
		{
			weeklyShow=true;

			dailyShow=false;
			monthlyShow=false;
			yearlyShow=false;
			customShow=false;
			quarterlyShow=false;
			reportShow=false;

		}
		else if(selectedFilter.equals("Monthly"))
		{
			monthlyShow=true;

			weeklyShow=false;
			dailyShow=false;
			yearlyShow=false;
			customShow=false;
			quarterlyShow=false;
			reportShow=false;
		}
		else if(selectedFilter.equals("Yearly"))
		{
			yearlyShow=true;


			weeklyShow=false;
			monthlyShow=false;
			dailyShow=false;
			customShow=false;
			quarterlyShow=false;
			reportShow=false;

		}
		else if(selectedFilter.equals("Customreport"))
		{
			customShow=true;

			weeklyShow=false;
			monthlyShow=false;
			yearlyShow=false;
			dailyShow=false;
			quarterlyShow=false;
			reportShow=false;
		}
		else if(selectedFilter.equals("Quarterly"))
		{
			quarterlyShow=true;

			dailyShow=false;
			weeklyShow=false;
			monthlyShow=false;
			yearlyShow=false;
			customShow=false;
			reportShow=false;
		}
		else
		{

			quarterlyShow=false;
			dailyShow=false;
			weeklyShow=false;
			monthlyShow=false;
			yearlyShow=false;
			customShow=false;
			reportShow=false;

		}

	}

	public void monthlyReport() throws ParseException
	{
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
		Connection conn=DataBaseConnection.javaConnection();
		Date selectedDate=new Date();
		Timestamp tt=new Timestamp(selectedDate.getTime());
		StringBuffer d1=new StringBuffer(String.valueOf(tt));
		if(selectedMonth.length()==1)
		{
			selectedMonth="0"+selectedMonth;
		}
		d1=d1.replace(5, 7, selectedMonth);
		d1=d1.replace(8, 10, "01");
		StringBuffer d2=new StringBuffer(d1);

		Calendar c = Calendar.getInstance();
		int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		d2=d2.replace(8, 10, String.valueOf(monthMaxDays));

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

		Date endDate=sdf.parse(String.valueOf(d2));
		selectedDate=sdf.parse(String.valueOf(d1));

		report=new DatabaseMethods1().filteredReportTable(selectedDate, endDate,conn);
		if(report.size()>0)
		{
			for(IncomeList info:report)
			{
				if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
				{
					otherIncAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
				{
					feeAmount+=info.getAmount();
					//totalAmount+=info.getAmount();
				}
				else
				{
					expenseAmount+=info.getAmount();
					//totalAmount-=info.getAmount();
				}
			}

			incomeAmount = otherIncAmount + feeAmount;
			totalAmount = incomeAmount - expenseAmount;
		}
		Collections.sort(report);
		reportShow=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ReportGeneration()
	{

		try
		{


			monthList=new ArrayList<>();

			SelectItem item=new SelectItem();
			item.setLabel("January");
			item.setValue("1");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("February");
			item.setValue("2");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("March");
			item.setValue("3");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("Aprail");
			item.setValue("4");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("May");
			item.setValue("5");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("June");
			item.setValue("6");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("July");
			item.setValue("7");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("August");
			item.setValue("8");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("September");
			item.setValue("9");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("October");
			item.setValue("10");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("November");
			item.setValue("11");
			monthList.add(item);

			item=new SelectItem();
			item.setLabel("December");
			item.setValue("12");
			monthList.add(item);


			yearList=new ArrayList<>();
			for(int i=2000;i<=2020;i++)
			{
				SelectItem item1=new SelectItem();
				item1.setLabel(String.valueOf(i));
				item1.setValue(String.valueOf(i));

				yearList.add(item1);
			}

			quarterList=new ArrayList<>();
			for(int i=1;i<=4;i++)
			{
				SelectItem item1=new SelectItem();
				item1.setLabel(String.valueOf(i));
				item1.setValue(String.valueOf(i));

				quarterList.add(item1);
			}

			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public void dailyReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		incomeAmount=0;expenseAmount=0;totalAmount=feeAmount=otherIncAmount=0;
		try
		{
			Date endDate=new Date(dailySelected.getTime());
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);

			//  dailselected timestamp is==> 00:00:00
			//  endate timestamp is set to 23:59:59 for same date that is dailyseleted

			report=new DatabaseMethods1().filteredReportTable(dailySelected, endDate,conn);
			if(report.size()>0)
			{
				for(IncomeList info:report)
				{
					if(info.getIncome().equalsIgnoreCase("Income") && !info.getCategory().equalsIgnoreCase("Fee"))
					{
						otherIncAmount+=info.getAmount();
						//totalAmount+=info.getAmount();
					}
					else if(info.getIncome().equalsIgnoreCase("Income") && info.getCategory().equalsIgnoreCase("Fee"))
					{
						feeAmount+=info.getAmount();
						//totalAmount+=info.getAmount();
					}
					else
					{
						expenseAmount+=info.getAmount();
						//totalAmount-=info.getAmount();
					}
				}

				incomeAmount = otherIncAmount + feeAmount;
				totalAmount = incomeAmount - expenseAmount;
			}
			Collections.sort(report);
			reportShow=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	public int getIncomeAmount() {
		return incomeAmount;
	}

	public void setIncomeAmount(int incomeAmount) {
		this.incomeAmount = incomeAmount;
	}

	public int getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(int expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
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

	public ArrayList<IncomeList> getReport() {
		return report;
	}

	public void setReport(ArrayList<IncomeList> report) {
		this.report = report;
	}

	public int getToatalamount() {
		return toatalamount;
	}

	public void setToatalamount(int toatalamount) {
		this.toatalamount = toatalamount;
	}

	public int getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(int feeAmount) {
		this.feeAmount = feeAmount;
	}

	public int getOtherIncAmount() {
		return otherIncAmount;
	}

	public void setOtherIncAmount(int otherIncAmount) {
		this.otherIncAmount = otherIncAmount;
	}


}
