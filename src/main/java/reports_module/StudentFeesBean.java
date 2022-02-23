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

@ManagedBean(name="studentFeesBar")
@ViewScoped
public class StudentFeesBean implements Serializable
{
	String days,year;
	Date stdate,endate;
	Date enddate=new Date();
	Date tempdate=new Date();
	Date startdate= new Date();
	ArrayList<DailyFeeCollectionBean>feesList = new ArrayList<>();
	ArrayList<SelectItem> monthList = new ArrayList<>();
	ArrayList<SelectItem> yearList = new ArrayList<>();
	private BarChartModel barmodl;
	int sum=0;
	boolean showCustom,showmonth;
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	String schoolId,sessionValue;
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public 	StudentFeesBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		days="lastweek";
		createBarModel();
		show();

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
				for(int year=2000;year<=2020;year++)
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
		feesList=new ArrayList<>();
		if(days.equals("lastweek"))
		{
			startdate.setDate((tempdate.getDate()-7));
			for(Date i=startdate;(i.before(enddate) || i.equals(enddate));i.setDate(startdate.getDate()+1))
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();

				String dt1=new SimpleDateFormat("yyyy-MM-dd").format(i);
				String dt2=new SimpleDateFormat("dd-MM-yyyy").format(i);
				int sum=obj.selectAllFeeValuesMonthYearDateWise(conn,"","",dt1,"date");
				dd.setFeeDateStr(dt2);
				dd.setAmt(sum);
				feesList.add(dd);

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
				int sum=obj.selectAllFeeValuesMonthYearDateWise(conn,"","",dt3,"date");
				dd.setFeeDateStr(dt4);
				dd.setAmt(sum);
				feesList.add(dd);

			}
		}
		else if(days.equalsIgnoreCase("custom"))
		{
			for(Date j=stdate;j.before(endate) || j.equals(endate);j.setDate(stdate.getDate()+1))
			{
				DailyFeeCollectionBean dd = new DailyFeeCollectionBean();

				String dt3=new SimpleDateFormat("yyyy-MM-dd").format(j);
				String dt4=new SimpleDateFormat("dd-MM-yyyy").format(j);
				int sum=obj.selectAllFeeValuesMonthYearDateWise(conn,"","",dt3,"date");
				dd.setFeeDateStr(dt4);
				dd.setAmt(sum);
				feesList.add(dd);

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
				int sum=obj.selectAllFeeValuesMonthYearDateWise(conn,String.valueOf(nn.getValue()),year,"","month");
				dd.setFeeDateStr(nn.getLabel());

				dd.setAmt(sum);
				feesList.add(dd);

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createBarModel();
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries amount = new ChartSeries();
		amount.setLabel("Amount");

		for(DailyFeeCollectionBean ss:feesList)
		{
			amount.set(ss.getFeeDateStr(), ss.getAmt());
		}
		model.addSeries(amount);

		return model;
	}
	private void createBarModel() {
		barmodl = initBarModel();

		barmodl.setTitle("Bar Chart");
		barmodl.setLegendPosition("ne");
		barmodl.setAnimate(true);
		barmodl.setExtender("barext");
		Axis xAxis = barmodl.getAxis(AxisType.X);
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
		Axis yAxis = barmodl.getAxis(AxisType.Y);
		yAxis.setLabel("Amount");
		yAxis.setMin(0);
		int max=0;
		for(DailyFeeCollectionBean ss:feesList)
		{
			if(max<ss.getAmt())
			{
				max=ss.getAmt();
			}
		}
		yAxis.setMax(max+200);
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
		return barmodl;
	}

	public void setBarmodl(BarChartModel barmodl) {
		this.barmodl = barmodl;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public ArrayList<DailyFeeCollectionBean> getFeesList() {
		return feesList;
	}

	public void setFeesList(ArrayList<DailyFeeCollectionBean> feesList) {
		this.feesList = feesList;
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

}