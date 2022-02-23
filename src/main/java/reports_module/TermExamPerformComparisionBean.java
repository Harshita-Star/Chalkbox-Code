package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import exam_module.DataBaseMethodsExam;
import exam_module.DataBaseMethodsExamReport;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="termExamPerformComparision")
@ViewScoped
public class TermExamPerformComparisionBean implements Serializable
{
	BarChartModel barmodel;
	ArrayList< SelectItem> barList1,barList2 = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>(),sectionList=new ArrayList<>(),termList=new ArrayList<>(),examList = new ArrayList<>();
	String selectClass,selectTerm1,selectTerm2,selectSection,termName1,termName2;
	boolean show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport examReport=new DataBaseMethodsExamReport();
	String schid,session;
	
	double pers=0.0,pers2=0.0;
	public TermExamPerformComparisionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid, conn);
		classList=obj1.allClass(conn);
		show=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public void search()
	{
		show=true;
		Connection conn=DataBaseConnection.javaConnection();

		barList1 = new ArrayList<>();
		barList2 = new ArrayList<>();
		examList=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm1,conn);

		for(SelectItem ss:examList)
		{
			SelectItem bl1 = new SelectItem();
			new SelectItem();
			double sum=examReport.selectObtainMarksFromTable(conn,"",String.valueOf(ss.getValue()),selectTerm1,selectSection,"classWise",schid,session,"");
			double maxmarks=examReport.selectMaxMarksFromTable(conn,"",String.valueOf(ss.getValue()),selectTerm1,selectSection,"classWise",schid,session,"");
			pers=sum*100/maxmarks;
			bl1.setValue(String.valueOf(pers));
			bl1.setLabel(ss.getLabel());


			barList1.add(bl1);

		}
		examList=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm2,conn);
		for(SelectItem ss:examList)
		{
			SelectItem bl2 = new SelectItem();

			double sum2=examReport.selectObtainMarksFromTable(conn,"",String.valueOf(ss.getValue()),selectTerm2,selectSection,"classWise",schid,session,"");
			double maxmarks2=examReport.selectMaxMarksFromTable(conn,"",String.valueOf(ss.getValue()),selectTerm2,selectSection,"classWise",schid,session,"");
			
			pers2=sum2*100/maxmarks2;
		
			bl2.setValue(String.valueOf(pers2));
			bl2.setLabel(ss.getLabel());
			barList2.add(bl2);

		}
		termName1=obj1.semesterNameFromid(selectTerm1, conn);
		termName2=obj1.semesterNameFromid(selectTerm2, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createBarModel();
	}

	public void allTerm()
	{
		show=false;

		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj1.allSection(selectClass,conn);

		termList=obj1.selectedTermOfClass(selectClass,conn,session,schid);



		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries exam1 = new ChartSeries();
		ChartSeries exam2 = new ChartSeries();
		DataBaseConnection.javaConnection();
		exam1.setLabel(termName1);
		exam2.setLabel(termName2);
		//ChartSeries girls = new ChartSeries();
		//  girls.setLabel("Girls");


		for( SelectItem ss:barList1)
		{
			exam1.set(ss.getLabel(), Double.valueOf((String) ss.getValue()));
		}
		for( SelectItem ks:barList2)
		{
			exam2.set(ks.getLabel(), Double.valueOf((String) ks.getValue()));
		}


		model.addSeries(exam1);
		model.addSeries(exam2);
		return model;


	}


	private void createBarModel(){
		barmodel = initBarModel();
		barmodel.setTitle("Class Strength chart");
		barmodel.setLegendPosition("ne");
		barmodel.setAnimate(true);
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Exams");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Percent");
		yAxis.setMin(0);
		// xAxis.setTickAngle(90);
		barmodel.setExtender("barextt");
		barmodel.setSeriesColors("F790C8,9189FD");

		// Color color = new Color(79, 129, 189);


		/*for(SelectItem ss:barList)
    {

        if(max<Integer.valueOf((String) (ss.getValue())))
        {
            max=Integer.valueOf((String) (ss.getValue()));
        }

 }*/
		yAxis.setMax(100);
		yAxis.setTickFormat("%d");
	}




	public BarChartModel getBarmodel() {
		return barmodel;
	}

	public void setBarmodel(BarChartModel barmodel) {
		this.barmodel = barmodel;
	}

	public ArrayList<SelectItem> getBarList1() {
		return barList1;
	}

	public void setBarList1(ArrayList<SelectItem> barList1) {
		this.barList1 = barList1;
	}

	public ArrayList<SelectItem> getBarList2() {
		return barList2;
	}

	public void setBarList2(ArrayList<SelectItem> barList2) {
		this.barList2 = barList2;
	}

	public double getPers2() {
		return pers2;
	}

	public void setPers2(double pers2) {
		this.pers2 = pers2;
	}

	public DatabaseMethods1 getDBM() {
		return DBM;
	}

	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
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

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
	public ArrayList<SelectItem> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}
	public String getSelectClass() {
		return selectClass;
	}
	public void setSelectClass(String selectClass) {
		this.selectClass = selectClass;
	}

	public String getSelectTerm1() {
		return selectTerm1;
	}


	public void setSelectTerm1(String selectTerm1) {
		this.selectTerm1 = selectTerm1;
	}


	public String getSelectTerm2() {
		return selectTerm2;
	}


	public void setSelectTerm2(String selectTerm2) {
		this.selectTerm2 = selectTerm2;
	}


	public String getSelectSection() {
		return selectSection;
	}
	public void setSelectSection(String selectSection) {
		this.selectSection = selectSection;
	}
	public String getTermName1() {
		return termName1;
	}


	public void setTermName1(String termName1) {
		this.termName1 = termName1;
	}


	public String getTermName2() {
		return termName2;
	}


	public void setTermName2(String termName2) {
		this.termName2 = termName2;
	}


	public DatabaseMethods1 getObj1() {
		return obj1;
	}




	public void setObj1(DatabaseMethods1 obj1) {
		this.obj1 = obj1;
	}




	public DataBaseMethodsExam getDe() {
		return de;
	}




	public void setDe(DataBaseMethodsExam de) {
		this.de = de;
	}




	public double getPers() {
		return pers;
	}




	public void setPers(double pers) {
		this.pers = pers;
	}
	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
}