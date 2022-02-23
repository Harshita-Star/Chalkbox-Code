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
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import exam_module.DataBaseMethodsExam;
import exam_module.DataBaseMethodsExamReport;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="studentPerformanceComparision")
@ViewScoped
public class StudentPerformanceComparisionBean implements Serializable{
	LineChartModel lineModel1;
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> subjectList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList1 = new ArrayList<>();
	List<SelectItem>[] arrayOfList = new List[20];
	
	String selectClass,selectTerm,singleTerm,selectSubject1,termName,schId,session;
	boolean show=false;
	String classNam,subjectName1;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport objExamReport=new DataBaseMethodsExamReport();
	double pers=0.0;
	ArrayList<String> subSelec = new ArrayList<>();
	
	public StudentPerformanceComparisionBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=obj1.allClass(conn);
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
		show=false;
		//createLineModels();
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		show=false;
		classNam=obj1.classNameFromidSchid(schId,selectClass, session, conn);
		ArrayList<SelectItem> sectionList=obj1.allSection(selectClass, conn);
		String sectionId="";
		for(SelectItem section:sectionList)
		{
			sectionId+=section.getValue()+"','";
		}
		if(sectionId.contains("','"))
			sectionId=sectionId.substring(0,sectionId.lastIndexOf("','"));
		//// // System.out.println(sectionId);
		
		for(int k=0;k<subSelec.size();k++) {
			
		examList1=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm,conn);
		arrayOfList[k] =examList1;
		
		for(SelectItem ss:arrayOfList[k])
		{
			double sum=objExamReport.selectObtainMarksFromTable(conn, "", String.valueOf(ss.getValue()), selectTerm,subSelec.get(k).toString(), "sectionWise", schId, session, sectionId);
			double maxmarks=objExamReport.selectMaxMarksFromTable(conn, "", String.valueOf(ss.getValue()), selectTerm,subSelec.get(k).toString() , "sectionWise", schId, session, sectionId);
			
			pers=sum*100/maxmarks;
			ss.setDescription(String.valueOf(pers));
		}
		}
		
		
		termName=obj1.semesterNameFromid(selectTerm, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		show=true;
		createLineModels();
	}

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();

		termList=obj1.selectedTermOfClass(selectClass,conn,session,schId);

		subjectList=obj1.selectedSubjectTypeofClassSection(selectClass,"scholastic",conn);
		//subjectList=obj1.selectedSubjectTypeofClassSectionString(selectClass,"scholastic",conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void createLineModels() {
		lineModel1 = initLinearModel();
		lineModel1.setTitle("Subject Performance Chart");
		lineModel1.setLegendPosition("e");
		lineModel1.setAnimate(true);
		lineModel1.getAxis(AxisType.Y);
		lineModel1.setShowPointLabels(true);
		lineModel1.setExtender("barextt");
		  lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Exam"));
//		  barmodel.setSeriesColors("F790C8,9189FD,FF9933, 77933C,6732FF,1234FF");
		Axis yAxis = lineModel1.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setMax(100);
		yAxis.setLabel("Percentage");
	}
	private LineChartModel initLinearModel() {
		LineChartModel model = new LineChartModel();
		Connection conn = DataBaseConnection.javaConnection();
		
		
		for(int k=0;k<subSelec.size();k++) {
		LineChartSeries series1 = new LineChartSeries();
		
		int i = 1;

		for(int j=0;j<arrayOfList[k].size();j++)
		{
			Object ob = arrayOfList[k].get(j).getLabel();
			series1.set(ob,Double.valueOf(arrayOfList[k].get(j).getDescription()) );
		}
		series1.setLabel(classNam+"-"+obj1.subjectNameFromid(subSelec.get(k).toString(), conn));
		model.addSeries(series1);
		}
		
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		return model;
	}
	public LineChartModel getLineModel1() {
		return lineModel1;
	}
	public void setLineModel1(LineChartModel lineModel1) {
		this.lineModel1 = lineModel1;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	
	
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public ArrayList<String> getSubSelec() {
		return subSelec;
	}
	public void setSubSelec(ArrayList<String> subSelec) {
		this.subSelec = subSelec;
	}
	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
	public ArrayList<SelectItem> getExamList1() {
		return examList1;
	}
	public void setExamList1(ArrayList<SelectItem> examList1) {
		this.examList1 = examList1;
	}
	public String getSelectClass() {
		return selectClass;
	}
	public void setSelectClass(String selectClass) {
		this.selectClass = selectClass;
	}
	public void setSelectTerm(String selectTerm) {
		this.selectTerm = selectTerm;
	}
	public String getSingleTerm() {
		return singleTerm;
	}
	public void setSingleTerm(String singleTerm) {
		this.singleTerm = singleTerm;
	}
	public String getSelectSubject1() {
		return selectSubject1;
	}
	public void setSelectSubject1(String selectSubject1) {
		this.selectSubject1 = selectSubject1;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getClassNam() {
		return classNam;
	}
	public void setClassNam(String classNam) {
		this.classNam = classNam;
	}
	public String getSubjectName1() {
		return subjectName1;
	}
	public void setSubjectName1(String subjectName1) {
		this.subjectName1 = subjectName1;
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
	public String getSelectTerm() {
		return selectTerm;
	}
}