package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="routeFeesBarReport")
@ViewScoped
public class RouteFeesBarReportBean implements Serializable{

	BarChartModel barmodel;
	private static final long serialVersionUID = 1L;
	ArrayList<FeeReportInfo> feesList = new ArrayList<>();
	ArrayList<SelectItem> studentList= new ArrayList<>();
	ArrayList<SelectItem>routeList = new ArrayList<>();
	ArrayList<SelectItem>monthList = new ArrayList<>();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports obj  = new DataBaseMethodsReports();
	String selectRoute,routefees;
	double fees;
	String schoolId,sessionValue;
	boolean showbar;
	
	public RouteFeesBarReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		routeList = dbm.allStops(conn);
		showbar=false;
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//show();
	}
	private BarChartModel initBarModel() {
		//("initbarmodel");
		BarChartModel model = new BarChartModel();
		ChartSeries Fees = new ChartSeries();
		Fees.setLabel("Fees");
		for(FeeReportInfo ss:feesList)
		{
			////// // System.out.println(ss.getDateName());
			Fees.set(ss.getDateName(), Double.valueOf(ss.getFees()));
		}
		model.addSeries(Fees);

		return model;
	}


	public void show() {
		showbar=false;
		Connection conn = DataBaseConnection.javaConnection();
		monthList = new ArrayList<>();

		SelectItem mm = new SelectItem();

		mm = new SelectItem();
		mm.setLabel("April");
		mm.setValue(4);
		monthList.add(mm);
		
		
		mm = new SelectItem();
		mm.setLabel("May");
		mm.setValue(5);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("June");
		mm.setValue(6);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("July");
		mm.setValue(7);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("August");
		mm.setValue(8);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Sept");
		mm.setValue(9);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Oct");
		mm.setValue(10);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Nov");
		mm.setValue(11);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Dec");
		mm.setValue(12);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Jan");
		mm.setValue(1);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("Feb");
		mm.setValue(2);
		monthList.add(mm);

		mm = new SelectItem();
		mm.setLabel("March");
		mm.setValue(3);
		monthList.add(mm);

		


		feesList = new ArrayList<>();
		routefees=dbm.routeStopListAllAmount(schoolId, selectRoute,conn);
	//	//// // System.out.println(selectRoute);
		fees=Double.valueOf(routefees);
	//	//// // System.out.println(fees);
		//(fees);
		studentList = obj.selectAllStudentId(conn,selectRoute);
		String session = dbm.selectedSessionDetails(schoolId,conn);
		String sess[]=session.split("-");
		String start=sess[0];
		String end = sess[1];
		String year="";
		double temp=0;
		for(SelectItem nn:monthList)
		{
			temp=0;
		//	//// // System.out.println(nn.getValue());
		//	//// // System.out.println(nn.getLabel());
			if(Integer.valueOf(String.valueOf(nn.getValue()))>=4)
			{
				year=start;
			}
			else
			{
				year=end;
			}
			FeeReportInfo ff=new FeeReportInfo();
		//	 //// // System.out.println(nn.getValue());
			for(SelectItem st:studentList)
			{
                
				String status = obj.checkStatusforSchoolId(conn,nn,st.getValue(),selectRoute,year);
				if(status.equalsIgnoreCase("yes"))
				{
					Double discnt=0.0;
					String	discount =dbm.checkDiscountFrom(conn,st.getValue());
					////// // System.out.println("Dis"+discount);
					//(discount);
					if(discount==null)
					{
						discnt=0.0;
					}
					else if(discount.equals(""))
					{
						discnt=0.0;
					}
					discnt=Double.valueOf(discount);

					temp=temp+(fees-discnt);
				}
			}
			ff.setDateName(nn.getLabel()+"-"+year);
			ff.setFees(String.valueOf(temp));
			feesList.add(ff);


		}

		showbar=true;
		createBarModel();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void createBarModel() {
		barmodel = initBarModel();
		barmodel.setTitle("Route Fees Bar Reports");
		barmodel.setLegendPosition("ne");
		barmodel.setExtender("barext");
		barmodel.setAnimate(true);
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Month");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Fees");
		yAxis.setMin(0);
		double max=0;

		for(FeeReportInfo ss:feesList)
		{

			if(max<Double.valueOf(ss.getFees()))
			{
				max=Double.valueOf(ss.getFees());
			}

		}
		yAxis.setMax(max+100);
		yAxis.setTickFormat("%d");
	}
	public BarChartModel getBarmodel() {
		return barmodel;
	}
	public void setBarmodel(BarChartModel barmodel) {
		this.barmodel = barmodel;
	}
	public ArrayList<FeeReportInfo> getFeesList() {
		return feesList;
	}
	public void setFeesList(ArrayList<FeeReportInfo> feesList) {
		this.feesList = feesList;
	}
	public ArrayList<SelectItem> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<SelectItem> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}
	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public DataBaseMethodsReports getObj() {
		return obj;
	}
	public void setObj(DataBaseMethodsReports obj) {
		this.obj = obj;
	}
	public String getSelectRoute() {
		return selectRoute;
	}
	public void setSelectRoute(String selectRoute) {
		this.selectRoute = selectRoute;
	}
	public boolean isShowbar() {
		return showbar;
	}
	public void setShowbar(boolean showbar) {
		this.showbar = showbar;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}