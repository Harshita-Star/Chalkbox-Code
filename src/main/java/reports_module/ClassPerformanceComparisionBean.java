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


@ManagedBean(name="classPerformanceComparision")
@ViewScoped
public class ClassPerformanceComparisionBean implements Serializable{
	LineChartModel lineModel1;
	ArrayList<termInfo> newtermList = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList1 = new ArrayList<>();
	String schId,session,selectClass,selectTerm,semId,singleTerm,selectRadio,termName,classNam;
	boolean show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport objExam=new DataBaseMethodsExamReport();
	
	double pers=0.0;
	ArrayList<String> subSelec = new ArrayList<>();
	List<SelectItem>[] arrayOfList = new List[10];
	  
	public ClassPerformanceComparisionBean()
	{
		selectRadio="StudentWise";
		Connection conn = DataBaseConnection.javaConnection();
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
		classList=obj1.allClass(conn);
		selectTerm="all";
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
		
		classNam=obj1.classNameFromidSchid(obj1.schoolId(),selectClass, session, conn);
		
		
		for(int k=0;k<subSelec.size();k++) 
		{
			
			examList1=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm,conn);
			arrayOfList[k] =examList1;
			
			for(SelectItem ss:arrayOfList[k])
			{
				double sum=objExam.selectObtainMarksFromTable(conn, "", ss.getValue().toString(), selectTerm, subSelec.get(k).toString(), "classWise", schId, session,"");
				double maxmarks=objExam.selectMaxMarksFromTable(conn, "", ss.getValue().toString(), selectTerm, subSelec.get(k).toString(), "classWise", schId, session,"");
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

		sectionList=obj1.allSection(selectClass,conn);



		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private void createLineModels() {
		lineModel1 = initLinearModel();
		lineModel1.setTitle("Class Performance Chart");
		lineModel1.setLegendPosition("e");
		lineModel1.setAnimate(true);
		lineModel1.getAxis(AxisType.Y);
		lineModel1.setShowPointLabels(true);
		lineModel1.setExtender("barextt");
		  lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Exam"));

		/*lineModel1.setShowPointLabels(true);
    lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Percentage"));
    lineModel1.getAxes().put(AxisType.Y, new CategoryAxis("Percentage"));*/


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
		
		

		for(int j=0;j<arrayOfList[k].size();j++)
		{
			Object ob = arrayOfList[k].get(j).getLabel();
			series1.set(ob,Double.valueOf(arrayOfList[k].get(j).getDescription()) );
		}
		series1.setLabel(classNam+"-"+obj1.sectionNameByIdSchid(obj1.schoolId(),subSelec.get(k).toString(), conn));
		model.addSeries(series1);
		}
	//	obj1.sectionNameByIdSchid(obj1.schoolId(),subSelec.get(k).toString(), conn);
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	/*	LineChartSeries series1 = new LineChartSeries();
		LineChartSeries series2 = new LineChartSeries();
		LineChartSeries series3 = new LineChartSeries();
		LineChartSeries series4 = new LineChartSeries();
		int i = 1;

		for(SelectItem ss:examList1)
		{
			Object ob = ss.getLabel();
			series1.set(ob,Double.valueOf(ss.getDescription()) );
		}
		series1.setLabel(classNam+"-"+sectionName1);
		i=1;
		for(SelectItem ss:examList2)
		{
			Object ob = ss.getLabel();
			series2.set(ob,Double.valueOf(ss.getDescription()) );
		}
		series2.setLabel(classNam+"-"+sectionName2);
		i=1;
		for(SelectItem ss:examList3)
		{
			Object ob = ss.getLabel();
			series3.set(ob,Double.valueOf(ss.getDescription()) );
		}
		series3.setLabel(classNam+"-"+sectionName3);
		i=1;
		for(SelectItem ss:examList4)
		{
			Object ob = ss.getLabel();
			series4.set(ob,Double.valueOf(ss.getDescription()) );
		}
		series4.setLabel(classNam+"-"+sectionName4);
		model.addSeries(series1);
		model.addSeries(series2);
		model.addSeries(series3);
		model.addSeries(series4);
*/

		return model;
	}

	public LineChartModel getLineModel1() {
		return lineModel1;
	}



	public void setLineModel1(LineChartModel lineModel1) {
		this.lineModel1 = lineModel1;
	}



	public ArrayList<termInfo> getNewtermList() {
		return newtermList;
	}



	public void setNewtermList(ArrayList<termInfo> newtermList) {
		this.newtermList = newtermList;
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


	public String getSelectTerm() {
		return selectTerm;
	}



	public void setSelectTerm(String selectTerm) {
		this.selectTerm = selectTerm;
	}


	public String getSemId() {
		return semId;
	}



	public void setSemId(String semId) {
		this.semId = semId;
	}



	public String getSingleTerm() {
		return singleTerm;
	}



	public void setSingleTerm(String singleTerm) {
		this.singleTerm = singleTerm;
	}


	public String getSelectRadio() {
		return selectRadio;
	}



	public void setSelectRadio(String selectRadio) {
		this.selectRadio = selectRadio;
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