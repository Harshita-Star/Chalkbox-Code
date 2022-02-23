package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import schooldata.DailyFeeCollectionBean;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="combineIncExpBarReport")
@ViewScoped
public class CombineIncExpBarReportBean implements Serializable
{
	String days,year;
	Date stdate,endate;
	Date enddate=new Date();
	Date tempdate=new Date();
	Date startdate= new Date();
	ArrayList<DailyFeeCollectionBean>incomeList = new ArrayList<>();
	ArrayList<SelectItem> monthList = new ArrayList<>();
	ArrayList<SelectItem> yearList = new ArrayList<>();
	private BarChartModel barmodel1;
	private BarChartModel barmodel2;
	int sum=0;
	boolean showCustom,showmonth,value1,value2,showBar1,showBar2;
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String schoolId,sessionValue;

	public 	CombineIncExpBarReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		sessionValue=obj1.selectedSessionDetails(schoolId, conn);
		days="lastweek";
		createBarModel();
		createBarModel2();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void custom()
	{

		if(days.equals("lastweek") || days.equals("lastmonth") || days.equals("month"))
		{

			showmonth=false;
			showCustom=false;
			if(days.equals("month"))
			{
				showmonth=true;
				yearList=new ArrayList<>();
				int yr = (new Date().getYear() + 1900);
				for(int year=2000;year<=yr;year++)
				{
					SelectItem ss = new SelectItem();
					ss.setLabel(String.valueOf(year));
					ss.setValue(year);
					yearList.add(ss);
				}
			}
		}
		else
		{
			showCustom=true;
			showmonth=false;
		}
	}

	public void show() {
		Connection conn=DataBaseConnection.javaConnection();
		incomeList=new ArrayList<>();
		if(days.equals("lastweek"))
		{
			startdate.setDate((tempdate.getDate()-7));
			for(Date i=startdate;(i.before(enddate) || i.equals(enddate));i.setDate(startdate.getDate()+1))
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();

				String dt1=new SimpleDateFormat("yyyy-MM-dd").format(i);
				String dt2=new SimpleDateFormat("dd-MM-yyyy").format(i);
				if(value1==true && value2==false)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt1,"date");
					dd.setFeeDateStr(dt2);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				else if(value2==true && value1==false)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt1,"date");
					dd.setFeeDateStr(dt2);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				else if(value1==true && value2==true)
				{
					showBar1=false;
					showBar2=true;
					int sum=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt1,"date");
					int sum1=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt1,"date");
					dd.setFeeDateStr(dt2);
					dd.setAmt(sum);
					dd.setAmt1(sum1);
					incomeList.add(dd);
				}

			}
		}
		else if(days.equals("lastmonth"))
		{
			startdate.setDate(tempdate.getDate()-30);
			for(Date j=startdate;j.before(enddate) || j.equals(enddate);j.setDate(startdate.getDate()+1))
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();

				String dt3=new SimpleDateFormat("yyyy-MM-dd").format(j);
				String dt4=new SimpleDateFormat("dd-MM-yyyy").format(j);
				if(value1==true && value2==false)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==false && value2==true)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==true && value2==true)
				{
					showBar1=false;
					showBar2=true;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt3,"date");
					int sum1=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					dd.setAmt1(sum1);
					incomeList.add(dd);
				}

			}
		}
		else if(days.equalsIgnoreCase("custom"))
		{
			for(Date j=stdate;j.before(endate) || j.equals(endate);j.setDate(stdate.getDate()+1))
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();

				String dt3=new SimpleDateFormat("yyyy-MM-dd").format(j);
				String dt4=new SimpleDateFormat("dd-MM-yyyy").format(j);
				if(value1==true && value2==false)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==false && value2==true)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==true && value2==true)
				{
					showBar1=false;
					showBar2=true;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,"","",dt3,"date");
					int sum1=obj.selectAllExpenseValuesMonthYearDateWise(conn,"","",dt3,"date");
					dd.setFeeDateStr(dt4);
					dd.setAmt(sum);
					dd.setAmt1(sum1);
					incomeList.add(dd);
				}
			}
		}
		else if(days.equals("month"))
		{

			monthList = new ArrayList<>();

			SelectItem mm = new SelectItem();
			mm.setLabel("jan");
			mm.setValue(1);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("feb");
			mm.setValue(2);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("march");
			mm.setValue(3);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("apirl");
			mm.setValue(4);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("may");
			mm.setValue(5);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("june");
			mm.setValue(6);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("july");
			mm.setValue(7);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("august");
			mm.setValue(8);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("sept");
			mm.setValue(9);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("oct");
			mm.setValue(10);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("nov");
			mm.setValue(11);
			monthList.add(mm);

			mm = new SelectItem();
			mm.setLabel("dec");
			mm.setValue(12);
			monthList.add(mm);

			for(SelectItem nn:monthList)
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();
				if(value1==true && value2==false)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,String.valueOf(nn.getValue()),year,"","month");
					dd.setFeeDateStr(nn.getLabel());
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==false && value2==true)
				{
					showBar1=true;
					showBar2=false;
					int sum=obj.selectAllExpenseValuesMonthYearDateWise(conn,String.valueOf(nn.getValue()),year,"","month");
					dd.setFeeDateStr(nn.getLabel());
					dd.setAmt(sum);
					incomeList.add(dd);
				}
				if(value1==true && value2==true)
				{
					showBar1=false;
					showBar2=true;
					int sum=obj.selectAllIncomeValuesMonthYearDateWise(conn,String.valueOf(nn.getValue()),year,"","month");
					int sum1=obj.selectAllExpenseValuesMonthYearDateWise(conn,String.valueOf(nn.getValue()),year,"","month");
					dd.setFeeDateStr(nn.getLabel());
					dd.setAmt(sum);
					dd.setAmt1(sum1);
					incomeList.add(dd);
				}

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(value1==true && value2==false)
		{
			createBarModel();
		}
		if(value1==false && value2==true)
		{
			createBarModel();
		}
		if(value1==true && value2==true)
		{
			createBarModel2();
		}



	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries income1 = new ChartSeries();


		for(DailyFeeCollectionBean ss:incomeList)
		{


			if(value1==true && value2==false)
			{
				income1.set(ss.getFeeDateStr(), ss.getAmt());
				income1.setLabel("Income");
			}
			else if(value2==true && value1==false)
			{
				income1.set(ss.getFeeDateStr(), ss.getAmt());
				income1.setLabel("Expense");
			}

		}
		model.addSeries(income1);
		return model;
	}
	private BarChartModel initBarModel2() {
		BarChartModel model = new BarChartModel();
		ChartSeries income1 = new ChartSeries();
		ChartSeries income2 = new ChartSeries();
		income1.setLabel("Income");
		income2.setLabel("Expense");

		for(DailyFeeCollectionBean ss:incomeList)
		{
			income1.set(ss.getFeeDateStr(), ss.getAmt());
			income2.set(ss.getFeeDateStr(), ss.getAmt1());


		}
		model.addSeries(income1);
		model.addSeries(income2);
		return model;
	}
	private void createBarModel() {
		barmodel1 = initBarModel();
		if(value1==true && value2==false)
		{
			barmodel1.setTitle("Bar Chart(Income)");
			barmodel1.setLegendPosition("ne");
		}
		if(value1==false && value2==true)
		{
			barmodel1.setTitle("Bar Chart(Expense)");
			barmodel1.setLegendPosition("ne");
		}

		barmodel1.setExtender("barext");
		barmodel1.setAnimate(true);

		Axis xAxis = barmodel1.getAxis(AxisType.X);
		if(days.equals("custom") || days.equals("lastmonth"))
		{
			xAxis.setTickAngle(90);
		}
		else
		{
			xAxis.setTickAngle(0);
		}
		if(days.equals("lastweek") || days.equals("lastmonth"))
		{
			xAxis.setLabel("Days");
		}
		else
		{
			xAxis.setLabel("Month");
		}
		Axis yAxis = barmodel1.getAxis(AxisType.Y);
		yAxis.setLabel("Amount");
		yAxis.setMin(0);
		int max=0;
		for(DailyFeeCollectionBean ss:incomeList)
		{
			if(max<ss.getAmt())
			{
				max=ss.getAmt();
			}
		}
		yAxis.setMax(max+100);
		yAxis.setTickFormat("%d");
	}
	private void createBarModel2() {
		barmodel2 = initBarModel2();
		barmodel2.setTitle("Bar Chart(Income/expense)");
		barmodel2.setLegendPosition("ne");
		barmodel2.setAnimate(true);
		barmodel2.setExtender("barextt");
		barmodel2.setSeriesColors("F790C8,9189FD");
		Axis xAxis = barmodel2.getAxis(AxisType.X);
		if(days.equals("custom") || days.equals("lastmonth"))
		{
			xAxis.setTickAngle(90);
		}
		else
		{
			xAxis.setTickAngle(0);
		}
		if(days.equals("lastweek") || days.equals("lastmonth"))
		{
			xAxis.setLabel("Days");
		}
		else
		{
			xAxis.setLabel("Month");
		}
		Axis yAxis = barmodel2.getAxis(AxisType.Y);
		yAxis.setLabel("Amount");
		yAxis.setMin(0);
		int max=0;
		for(DailyFeeCollectionBean ss:incomeList)
		{
			if(max<ss.getAmt())
			{
				max=ss.getAmt();
			}

			if(max<ss.getAmt1())
			{
				max=ss.getAmt1();
			}
		}
		yAxis.setMax(max+100);
		yAxis.setTickFormat("%d");
	}
	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Date getTempdate() {
		return tempdate;
	}

	public void setTempdate(Date tempdate) {
		this.tempdate = tempdate;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public BarChartModel getBarmodl() {
		return barmodel1;
	}

	public void setBarmodl(BarChartModel barmodl) {
		this.barmodel1 = barmodl;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public DataBaseMethodsReports getObj() {
		return obj;
	}

	public void setObj(DataBaseMethodsReports obj) {
		this.obj = obj;
	}

	public Date getStdate() {
		return stdate;
	}

	public void setStdate(Date stdate) {
		this.stdate = stdate;
	}

	public Date getEndate() {
		return endate;
	}

	public void setEndate(Date endate) {
		this.endate = endate;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public boolean isShowCustom() {
		return showCustom;
	}

	public void setShowCustom(boolean showCustom) {
		this.showCustom = showCustom;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}
	public boolean isShowmonth() {
		return showmonth;
	}
	public void setShowmonth(boolean showmonth) {
		this.showmonth = showmonth;
	}
	public ArrayList<DailyFeeCollectionBean> getIncomeList() {
		return incomeList;
	}
	public void setIncomeList(ArrayList<DailyFeeCollectionBean> incomeList) {
		this.incomeList = incomeList;
	}

	public boolean isValue1() {
		return value1;
	}
	public void setValue1(boolean value1) {
		this.value1 = value1;
	}
	public boolean isValue2() {
		return value2;
	}
	public void setValue2(boolean value2) {
		this.value2 = value2;
	}

	public BarChartModel getBarmodel1() {
		return barmodel1;
	}

	public void setBarmodel1(BarChartModel barmodel1) {
		this.barmodel1 = barmodel1;
	}

	public BarChartModel getBarmodel2() {
		return barmodel2;
	}

	public void setBarmodel2(BarChartModel barmodel2) {
		this.barmodel2 = barmodel2;
	}

	public boolean isShowBar1() {
		return showBar1;
	}

	public void setShowBar1(boolean showBar1) {
		this.showBar1 = showBar1;
	}

	public boolean isShowBar2() {
		return showBar2;
	}

	public void setShowBar2(boolean showBar2) {
		this.showBar2 = showBar2;
	}


}