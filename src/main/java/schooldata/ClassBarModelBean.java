package schooldata;

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

@ManagedBean(name="classBarModel")
@ViewScoped
public class ClassBarModelBean implements Serializable{

	BarChartModel barmodel;
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList = new ArrayList<>();
	ArrayList<SelectItem>sectionList = new ArrayList<>();
	ArrayList<ClassInfo> barList = new ArrayList<>();
	DatabaseMethods1 obj  = new DatabaseMethods1();
	String classid,sectionId,className="",classNameId;
	int count,count2,boySum=0,girlSum=0,totalSum=0;
	boolean showbar;
	public ClassBarModelBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=obj.allClass(conn);
		showbar=false;
		className="";
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		show();
		
	}
	private BarChartModel initBarModel() {
		////// // System.out.println("initbarmodel");
		BarChartModel model = new BarChartModel();
		ChartSeries total = new ChartSeries();
		total.setLabel(" ");
		//ChartSeries girls = new ChartSeries();
		//  girls.setLabel("Girls");

		for(ClassInfo ss:barList)
		{
			total.set(ss.getClassName(), ss.getTotalSum());
			////// // System.out.println("girls"+ss.getGirlSum());
			//boys.set(ss.getClassName(), ss.getBoySum());
			// girls.set(ss.getClassName(), ss.getGirlSum());




		}


		model.addSeries(total);
		// model.addSeries(girls);
		return model;
	}

	public void show() {
		showbar=true;
		Connection conn = DataBaseConnection.javaConnection();
		barList = new ArrayList<>();
		if(!className.equalsIgnoreCase(""))
		{
			sectionList=obj.sectionNameAndIdListByid(className,conn);
			classNameId=obj.classNameFromidSchid(obj.schoolId(),className, DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn), conn);
			for(SelectItem section:sectionList)
			{
				ClassInfo ss = new ClassInfo();
				sectionId=String.valueOf(section.getValue());

				
				count=Integer.parseInt(obj.allStudentcount(obj.schoolId(), "class_strength_graph", sectionId, DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn), "",conn));
				ss.setTotalSum(count);
				ss.setClassName(classNameId+"-"+section.getLabel());
				barList.add(ss);
			}
		}
		else
		{
			barList = new ArrayList<>();
			for(SelectItem cls:classList)
			{
				ClassInfo cl = new ClassInfo();
				classid= String.valueOf(cls.getValue());

				sectionList=obj.sectionNameAndIdListByid(classid,conn);
				cl.setClassName(cls.getLabel());
				totalSum=0;
				for(SelectItem section:sectionList)
				{
					sectionId=String.valueOf(section.getValue());
					count=Integer.parseInt(obj.allStudentcount(obj.schoolId(), "class_strength_graph", sectionId, DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn),"", conn));
					totalSum=totalSum+count;
				}
				cl.setTotalSum(totalSum);
				barList.add(cl);
			}
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		createBarModel();

	}


	private void createBarModel() {
		barmodel = initBarModel();
		barmodel.setTitle("Class Strength chart");
		barmodel.setLegendPosition("ne");
		  barmodel.setSeriesColors("F790C8,9189FD,FF9933, 77933C,6732FF,1234FF");
		barmodel.setAnimate(true);
		barmodel.setExtender("barext");
		Axis xAxis = barmodel.getAxis(AxisType.X);
		if(!className.equalsIgnoreCase("")) {
			xAxis.setLabel("Sections");
		}else {
			xAxis.setLabel("Classes");
		}
		
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Strength");
		yAxis.setMin(0);
		int max=0;

		for(ClassInfo ss:barList)
		{

			if(max<ss.getTotalSum())
			{
				max=ss.getTotalSum();
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
	public ArrayList<ClassInfo> getBarList() {
		return barList;
	}
	public void setBarList(ArrayList<ClassInfo> barList) {
		this.barList = barList;
	}
	public DatabaseMethods1 getObj() {
		return obj;
	}
	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount2() {
		return count2;
	}
	public void setCount2(int count2) {
		this.count2 = count2;
	}
	public int getBoySum() {
		return boySum;
	}
	public void setBoySum(int boySum) {
		this.boySum = boySum;
	}
	public int getGirlSum() {
		return girlSum;
	}
	public void setGirlSum(int girlSum) {
		this.girlSum = girlSum;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getTotalSum() {
		return totalSum;
	}
	public void setTotalSum(int totalSum) {
		this.totalSum = totalSum;
	}

	public String getClassNameId() {
		return classNameId;
	}
	public void setClassNameId(String classNameId) {
		this.classNameId = classNameId;
	}
	public boolean isShowbar() {
		return showbar;
	}
	public void setShowbar(boolean showbar) {
		this.showbar = showbar;
	}
}