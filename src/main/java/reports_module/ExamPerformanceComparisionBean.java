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
import schooldata.StudentInfo;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;


@ManagedBean(name="examPerformanceComparision")
@ViewScoped
public class ExamPerformanceComparisionBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	BarChartModel barmodel;
	ArrayList< SelectItem> barList1,barList2 = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> allSubjectList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList = new ArrayList<>();
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	String schid,session,selectClass,selectTerm,name;
	boolean show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport examReport=new DataBaseMethodsExamReport();
	double pers=0.0,pers2=0.0;
	ArrayList<String> subSelec = new ArrayList<>();
	List<SelectItem>[] arrayOfList = new List[15];
	DatabaseMethodSession objSession=new DatabaseMethodSession();

		
	public ExamPerformanceComparisionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		show=false;
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid, conn);
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		show=false;
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=obj1.searchStudentList(query,conn);
		studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");

		List<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}


	public void search()
	{
		show=true;
		Connection conn=DataBaseConnection.javaConnection();

		barList1 = new ArrayList<>();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		
	//	String classId = DBM.studentClassNyAddNo(studentAddNo, conn);
		
		allSubjectList=DBM.selectedSubjectTypeofClassSection(selectClass, /*selectedTerm,*/"scholastic",conn);
	
	for(int k=0;k<subSelec.size();k++) {
			
			allSubjectList=DBM.selectedSubjectTypeofClassSection(selectClass, /*selectedTerm,*/"scholastic",conn);
			arrayOfList[k] =allSubjectList;
			
			for(SelectItem ss:arrayOfList[k])
			{
			
				double sum=examReport.selectObtainMarksFromTable(conn,id,subSelec.get(k).toString(),selectTerm,String.valueOf(ss.getValue()),"subjectStudentWise",schid,session,"");
				double maxmarks=examReport.selectMaxMarksFromTable(conn,id,subSelec.get(k).toString(),selectTerm,String.valueOf(ss.getValue()),"subjectStudentWise",schid,session,"");
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
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void allTerm()
	{
		show=false;
		selectTerm="all";
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);

		//selectClass=obj1.studentDetailslistByAddNo(schid,id, conn).getClassId();
		ArrayList<StudentInfo> studentListForterm = new ArrayList<>();
		studentListForterm=objSession.searchStudentListWithPreSessionStudent("byName",id, "full", conn,"","");
		for(StudentInfo ab: studentListForterm)
		{
			selectClass = ab.getClassId();
		}
		
		termList=obj1.selectedTermOfClass(selectClass,conn,session,schid);


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
			// TODO: handle exception
		}
		/*ChartSeries exam1 = new ChartSeries();
		ChartSeries exam2 = new ChartSeries();
		Connection conn = DataBaseConnection.javaConnection();
		exam1.setLabel(obj1.examNameFromid(selectExam1, conn));
		exam2.setLabel(obj1.examNameFromid(selectExam2, conn));
		//ChartSeries girls = new ChartSeries();
		//  girls.setLabel("Girls");


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
		barmodel.setSeriesColors("58BA27,FFCC33,F74A4A,F52F2F,A30303");
		barmodel.setExtender("chartExtender");
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Subjects");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Percent");
		yAxis.setMin(0);
		xAxis.setTickAngle(90);
		barmodel.setExtender("barextt");
		barmodel.setSeriesColors("F790C8,9189FD,FF9933, 77933C,6732FF,1234FF");

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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}