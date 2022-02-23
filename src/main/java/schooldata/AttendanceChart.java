package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
@ManagedBean(name="attendanceChart")
@ViewScoped
public class AttendanceChart implements  Serializable
{
	String selectedClass,selectedSection;
	public BarChartModel barModel;
	ArrayList<SelectItem>classList=new ArrayList<>(),sectionList=new ArrayList<>();
	Date currentDate=new Date();
	ArrayList<Sum>attendanceChart;
	public AttendanceChart()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedClass="all";
		selectedSection="all";
		classList=new DatabaseMethods1().allClass(conn);
		createBarModels();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public BarChartModel getBarModel()
	{
		return barModel;
	}
	private void createBarModels()
	{
		createBarModel();
	}
	private void createBarModel()
	{
		barModel = initBarModel();

		barModel.setTitle("Attendance Chart");
		barModel.setLegendPosition("ne");

		Axis xAxis = barModel.getAxis(AxisType.X);
		if(selectedClass.equalsIgnoreCase("all")&&selectedSection.equalsIgnoreCase("all"))
		{
			xAxis.setLabel("Class");
		}
		else
		{
			xAxis.setLabel("Section");
		}
		Axis yAxis = barModel.getAxis(AxisType.Y);
		yAxis.setLabel("No.Of Students");
		yAxis.setMin(0);
	}
	private BarChartModel initBarModel()
	{
		Connection conn=DataBaseConnection.javaConnection();
		BarChartModel model = new BarChartModel();
		attendanceChart=new DatabaseMethods1().attendanceChartMethod(currentDate,selectedClass,selectedSection,classList,sectionList,conn);
		ChartSeries present = new ChartSeries();
		present.setLabel("Present");
		ChartSeries absent = new ChartSeries();
		absent.setLabel("Absent");
		ChartSeries leave = new ChartSeries();
		leave.setLabel("Leave");
		ChartSeries notMarked = new ChartSeries();
		notMarked.setLabel("Not Marked");
		for(Sum ss:attendanceChart)
		{
			present.set(ss.getClassName(),ss.getPresent());
			absent.set(ss.getClassName(),ss.getAbsent());
			leave.set(ss.getClassName(),ss.getLeave());
			notMarked.set(ss.getClassName(),ss.getNotMarked());
		}

		model.addSeries(present);
		model.addSeries(absent);
		model.addSeries(leave);
		model.addSeries(notMarked);
		model.setShowPointLabels(true);
		model.setSeriesColors("00A65A,DD4B39,F39C12,00C0EF");
		//  model.setDatatipFormat("%.0s%s");
		model.setShowDatatip(false);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return model;
	}
	public void search()
	{
		createBarModel();
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public ArrayList<Sum> getAttendanceChart() {
		return attendanceChart;
	}
	public void setAttendanceChart(ArrayList<Sum> attendanceChart) {
		this.attendanceChart = attendanceChart;
	}
	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

}
