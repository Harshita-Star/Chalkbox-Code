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

@ManagedBean(name="studentSubjPerformanceBarGraph")
@ViewScoped
public class StudentSubjPerformanceBarBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	BarChartModel barmodel;
	ArrayList< SelectItem> barList = new ArrayList<>();
	ArrayList<SelectItem> allSubjectList=new ArrayList<>(),termList=new ArrayList<>(),examList = new ArrayList<>();
	ArrayList<StudentInfo>studentList = new ArrayList<>();
	String selectClass,termId,selectTerm,name,selectExam,termName,schid,session;
	boolean show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	DataBaseMethodsExamReport examReport=new DataBaseMethodsExamReport();
	double pers=0.0;
	public StudentSubjPerformanceBarBean()
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
		studentList=de.studentBasicDetailsForMarksheet(schid,"", conn,"like",query);

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

		barList = new ArrayList<>();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);

		allSubjectList=DBM.selectedSubjectTypeofClassSection(selectClass, /*selectedTerm,*/"scholastic",conn);
		for(SelectItem ss :allSubjectList)
		{
			SelectItem bl = new SelectItem();
			double sum=examReport.selectObtainMarksFromTable(conn,id,selectExam,selectTerm,String.valueOf(ss.getValue()),"subjectStudentWise",schid,session,"");
			double maxmarks=examReport.selectMaxMarksFromTable(conn,id,selectExam,selectTerm,String.valueOf(ss.getValue()),"subjectStudentWise",schid,session,"");
			pers=sum*100/maxmarks;
			bl.setValue(String.valueOf(pers));
			bl.setLabel(ss.getLabel());
			barList.add(bl);
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
		selectClass=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("singlestudent",id, "full", conn,"","").get(0).getClassId();
		//selectClass=obj1.studentDetailslistByAddNo(schid,id, conn).getClassId();
		termList=obj1.selectedTermOfClass(selectClass,conn,session,schid);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries total = new ChartSeries();
		total.setLabel("total");

		for( SelectItem ss:barList)
		{
			total.set(ss.getLabel(), Double.valueOf((String) ss.getValue()));

		}


		model.addSeries(total);
		return model;


	}


	private void createBarModel() {
		barmodel = initBarModel();
		barmodel.setTitle("Class Strength chart");
		barmodel.setLegendPosition("ne");
		barmodel.setAnimate(true);
		barmodel.setExtender("barext");
		Axis xAxis = barmodel.getAxis(AxisType.X);
		xAxis.setLabel("Subject");
		Axis yAxis = barmodel.getAxis(AxisType.Y);
		yAxis.setLabel("Percent");
		yAxis.setMin(0);
		xAxis.setTickAngle(90);
		/*
    for(SelectItem ss:barList)
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

	public ArrayList<SelectItem> getBarList() {
		return barList;
	}

	public void setBarList(ArrayList<SelectItem> barList) {
		this.barList = barList;
	}

	public ArrayList<SelectItem> getAllSubjectList() {
		return allSubjectList;
	}

	public void setAllSubjectList(ArrayList<SelectItem> allSubjectList) {
		this.allSubjectList = allSubjectList;
	}

	public String getSelectExam() {
		return selectExam;
	}

	public void setSelectExam(String selectExam) {
		this.selectExam = selectExam;
	}

	public DatabaseMethods1 getDBM() {
		return DBM;
	}

	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
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
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getSelectTerm() {
		return selectTerm;
	}
	public void setSelectTerm(String selectTerm) {
		this.selectTerm = selectTerm;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}