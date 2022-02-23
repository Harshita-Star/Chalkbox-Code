package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.RouteStop;


@ManagedBean(name="transportRouteAndMonthWiseReport")
@ViewScoped
public class TransportRouteAndMonthWiseReportBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<SelectItem> routeList = new ArrayList<SelectItem>();
	ArrayList<SelectItem> monthList = new ArrayList<SelectItem>();
	boolean view;
	String selectedRoute,selectedMonth;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<RouteStop>list = new ArrayList<RouteStop>();
    double totalStudentTotal,totalStudentAmount;
    String schoolId,session;
	
	public TransportRouteAndMonthWiseReportBean()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
	
		try
		{
			schoolId=obj.schoolId();
			session=obj.selectedSessionDetails(schoolId, conn);
			routeList=obj.allTransportRoutes(conn);
			monthList = obj.allMonthsTransport(conn);


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
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		

		totalStudentTotal = 0;
		totalStudentAmount = 0; 
		list=obj.routeStopListComplete(selectedRoute,selectedMonth,conn);
		
		for(RouteStop ld: list)
		{
			try {
				totalStudentTotal += Double.valueOf(ld.getTotalStudents());
				totalStudentAmount += Double.valueOf(ld.getAmount());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		view=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public boolean isView() {
		return view;
	}

	public void setView(boolean view) {
		this.view = view;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public ArrayList<RouteStop> getList() {
		return list;
	}

	public void setList(ArrayList<RouteStop> list) {
		this.list = list;
	}

	public double getTotalStudentTotal() {
		return totalStudentTotal;
	}

	public void setTotalStudentTotal(double totalStudentTotal) {
		this.totalStudentTotal = totalStudentTotal;
	}

	public double getTotalStudentAmount() {
		return totalStudentAmount;
	}

	public void setTotalStudentAmount(double totalStudentAmount) {
		this.totalStudentAmount = totalStudentAmount;
	}
	

	
}

