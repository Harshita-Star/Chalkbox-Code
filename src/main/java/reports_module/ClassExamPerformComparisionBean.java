package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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


@ManagedBean(name="classExamPerformComparision")
@ViewScoped
public class ClassExamPerformComparisionBean implements Serializable{
	BarChartModel barmodel;
	// private BarChartModel barModel;
	ArrayList< SelectItem> barList1,barList2 = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> allSubjectList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList = new ArrayList<>();
	String selectClass,selectTerm,secId,selectSection,termName,schId,session;
	boolean showStudentWise,showClassWise,show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport objExamReport=new DataBaseMethodsExamReport();
	double pers=0.0,pers2=0.0;
	ArrayList<String> subSelec = new ArrayList<>();
	List<SelectItem>[] arrayOfList = new List[15];
	
	
	public ClassExamPerformComparisionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=obj1.allClass(conn);
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
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
		
		allSubjectList=obj1.selectedSubjectTypeofClassSection(selectClass, /*selectedTerm,*/"scholastic",conn);
		for(int k=0;k<subSelec.size();k++) 
		{
			allSubjectList=obj1.selectedSubjectTypeofClassSection(selectClass, /*selectedTerm,*/"scholastic",conn);
			arrayOfList[k] =allSubjectList;
			
			for(SelectItem ss:arrayOfList[k])
			{
			
				double sum=objExamReport.selectObtainMarksFromTable(conn, "", subSelec.get(k).toString(), selectTerm, String.valueOf(ss.getValue()), "sectionWise", schId, session, selectSection);
				double maxmarks=objExamReport.selectMaxMarksFromTable(conn, "", subSelec.get(k).toString(), selectTerm, String.valueOf(ss.getValue()), "sectionWise", schId, session, selectSection);
				pers=sum*100/maxmarks;
			
				ss.setDescription(String.valueOf(pers));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createBarModel();
	}
	public void show()
	{
		show=false;
		Connection conn=DataBaseConnection.javaConnection();

		examList=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm,conn);

	}

	public void allTerm()
	{
		show=false;
		selectTerm="all";

		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj1.allSection(selectClass,conn);

		termList=obj1.selectedTermOfClass(selectClass,conn,session,schId);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
	Connection conn = DataBaseConnection.javaConnection();
		
		
		for(int k=0;k<subSelec.size();k++) {
			ChartSeries series1 = new ChartSeries();
		
		int i = 1;

		for(int j=0;j<arrayOfList[k].size();j++)
		{
			Object ob = arrayOfList[k].get(j).getLabel();
			series1.set(ob,Double.valueOf(arrayOfList[k].get(j).getDescription()) );
		}
		series1.setLabel(obj1.examNameFromid(subSelec.get(k).toString(), conn));
		model.addSeries(series1);
		}
		
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	/*	ChartSeries exam1 = new ChartSeries();
		ChartSeries exam2 = new ChartSeries();
		Connection conn = DataBaseConnection.javaConnection();
		exam1.setLabel(obj1.examNameFromid(selectExam1, conn));
		exam2.setLabel(obj1.examNameFromid(selectExam2, conn));

		for( SelectItem ss:barList1)
		{
			exam1.set(ss.getLabel(), Double.valueOf((String) ss.getValue()));
		}
		for( SelectItem ss:barList2)
		{
			exam2.set(ss.getLabel(), Double.valueOf((String) ss.getValue()));
		}


		model.addSeries(exam1);
		model.addSeries(exam2);*/
		return model;


	}


	private void createBarModel(){
		barmodel = initBarModel();
		barmodel.setTitle("Class Strength chart");
		barmodel.setLegendPosition("ne");
		barmodel.setAnimate(true);
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Subjects");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Percent");
		yAxis.setMin(0);
		xAxis.setTickAngle(90);
		barmodel.setExtender("barextt");
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

	public ArrayList<SelectItem> getAllSubjectList() {
		return allSubjectList;
	}

	public void setAllSubjectList(ArrayList<SelectItem> allSubjectList) {
		this.allSubjectList = allSubjectList;
	}

	public double getPers2() {
		return pers2;
	}

	public void setPers2(double pers2) {
		this.pers2 = pers2;
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
	public String getSelectTerm() {
		return selectTerm;
	}
	public void setSelectTerm(String selectTerm) {
		this.selectTerm = selectTerm;
	}

	public String getSecId() {
		return secId;
	}




	public void setSecId(String secId) {
		this.secId = secId;
	}

	public String getSelectSection() {
		return selectSection;
	}




	public void setSelectSection(String selectSection) {
		this.selectSection = selectSection;
	}




	public String getTermName() {
		return termName;
	}




	public void setTermName(String termName) {
		this.termName = termName;
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
	public boolean isShowStudentWise() {
		return showStudentWise;
	}
	public void setShowStudentWise(boolean showStudentWise) {
		this.showStudentWise = showStudentWise;
	}
	public boolean isShowClassWise() {
		return showClassWise;
	}
	public void setShowClassWise(boolean showClassWise) {
		this.showClassWise = showClassWise;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}


	public ArrayList<String> getSubSelec() {
		return subSelec;
	}


	public void setSubSelec(ArrayList<String> subSelec) {
		this.subSelec = subSelec;
	}
	
}