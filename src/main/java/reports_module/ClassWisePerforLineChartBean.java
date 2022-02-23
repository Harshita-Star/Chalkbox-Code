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
import schooldata.StudentInfo;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;


@ManagedBean(name="classWisePerformanceLineChart")
@ViewScoped
public class ClassWisePerforLineChartBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	LineChartModel lineModel1;
	ArrayList<termInfo> newtermList = new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>();
	ArrayList<SelectItem> termList=new ArrayList<>();
	ArrayList<SelectItem>examList = new ArrayList<>();
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	String schId,session,selectClass,selectTerm,name,studId,secId,singleTerm,selectRadio,selectSection,termName,selectSutdent;
	boolean showStudentWise,showClassWise,show=false;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExamReport objExam=new DataBaseMethodsExamReport();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	double pers=0.0;
	public ClassWisePerforLineChartBean()
	{
		selectRadio="StudentWise";
		Connection conn = DataBaseConnection.javaConnection();
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
		classList=obj1.allClass(conn);
		selectTerm="all";
		check();
		//createLineModels();
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=obj1.searchStudentList(query,conn);
		studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		for(StudentInfo ss:studentList)
		{
			studId=ss.getAddNumber();
			secId=ss.getSectionid();
		}
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

	public void check()
	{
		newtermList = new ArrayList<>();
		selectTerm="all";
		show=false;
		if(selectRadio.equalsIgnoreCase("StudentWise"))
		{
			showStudentWise=true;
			showClassWise=false;
		}
		else if(selectRadio.equalsIgnoreCase("ClassWise"))
		{
			showStudentWise=false;
			showClassWise=true;
		}
	}
	public void show()
	{
		Connection conn=DataBaseConnection.javaConnection();
		show=false;
		if(selectTerm.equalsIgnoreCase("all"))
		{
			for(termInfo st:newtermList)
			{
				singleTerm=String.valueOf(st.getTermId());
				examList=de.mainExamListOfClassSection(selectClass,"scholastic",singleTerm,conn);
		
				for(SelectItem ss:examList)
				{
					double sum=objExam.selectObtainMarksFromTable(conn, "", ss.getValue().toString(), singleTerm, selectSection, "classWise", schId, session,"");
					double maxmarks=objExam.selectMaxMarksFromTable(conn, "", ss.getValue().toString(), singleTerm, selectSection, "classWise", schId, session,"");
					pers=sum*100/maxmarks;
					ss.setDescription(String.valueOf(pers));
				}
				st.setSelectList(examList);
			}
		}
		else
		{
			examList=de.mainExamListOfClassSection(selectClass,"scholastic",selectTerm,conn);
			for(SelectItem ss:examList)
			{
				double sum=objExam.selectObtainMarksFromTable(conn, "", ss.getValue().toString(), selectTerm, selectSection, "classWise", schId, session,"");
				double maxmarks=objExam.selectMaxMarksFromTable(conn, "", ss.getValue().toString(), selectTerm, selectSection, "classWise", schId, session,"");
				pers=sum*100/maxmarks;
				ss.setDescription(String.valueOf(pers));
			}
			termName=obj1.semesterNameFromid(selectTerm, conn);
		}
		try {
		conn.close();
		} catch (SQLException e) {
		e.printStackTrace();
		}
		show=true;
		name="";
		createLineModels();
	}

	public void show1()
	{
		show=false;
		//("shoooooow");
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		String selClass=objSession.searchStudentListWithPreSessionStudent("singlestudent",id, "full", conn,"","").get(0).getClassId();
		//String selClass=obj1.studentDetailslistByAddNo(obj1.schoolId(),id, conn).getClassId();
		if(selectTerm.equalsIgnoreCase("all"))
		{
	
			for(termInfo st:newtermList)
			{
				singleTerm=String.valueOf(st.getTermId());
				examList=de.mainExamListOfClassSection(selClass,"scholastic",singleTerm,conn);
				for(SelectItem ss:examList)
				{
					double sum=obj1.selectAllMarksFromTable(conn,ss.getValue(),singleTerm,id);
					double maxmarks=obj1.selectMaxMarksFromTable(conn,ss.getValue(),singleTerm,id);
					pers=sum*100/maxmarks;
					ss.setDescription(String.valueOf(pers));
				}
			
				st.setSelectList(examList);
			}
		}
		else
		{
		
			examList=de.mainExamListOfClassSection(selClass,"scholastic",selectTerm,conn);
			for(SelectItem ss:examList)
			{
				double sum=obj1.selectAllMarksFromTable(conn,ss.getValue(),selectTerm,id);
				double maxmarks=obj1.selectMaxMarksFromTable(conn,ss.getValue(),selectTerm,id);
				pers=sum*100/maxmarks;
				ss.setDescription(String.valueOf(pers));
			}
			termName=obj1.semesterNameFromid(selectTerm, conn);
		}
	
		try {
		conn.close();
		} catch (SQLException e) {
		e.printStackTrace();
		}
		show=true;
		selectClass="";selectSection="";
		createLineModels1();
	}

	public void allTerm()
	{
		selectTerm="all";
		newtermList = new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();

		termList=obj1.selectedTermOfClass(selectClass,conn,session,schId);

		sectionList=obj1.allSection(selectClass,conn);

		for(SelectItem ss:termList)
		{
			termInfo tt  = new termInfo();
			tt.setTermId(String.valueOf(ss.getValue()));
			tt.setTermName(ss.getLabel());
			newtermList.add(tt);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allTerm1()
	{
		selectTerm="all";
		newtermList = new ArrayList<>();
		//();
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		String selClass=objSession.searchStudentListWithPreSessionStudent("singlestudent",id, "full", conn,"","").get(0).getClassId();
		//String selClass=obj1.studentDetailslistByAddNo(obj1.schoolId(),id, conn).getClassId();
		termList=obj1.selectedTermOfClass(selClass,conn,session,schId);
		for(SelectItem ss:termList)
		{
			termInfo tt  = new termInfo();
			tt.setTermId(String.valueOf(ss.getValue()));
			tt.setTermName(ss.getLabel());
			newtermList.add(tt);
		}

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
		lineModel1.setExtender("barextt");
		lineModel1.getAxis(AxisType.Y);
		lineModel1.setShowPointLabels(true);
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


		int i = 1;
		if(selectRadio.equalsIgnoreCase("StudentWise")) {
			if(selectTerm.equalsIgnoreCase("all"))
			{
				for(termInfo st:newtermList)
				{
					i=1;
					LineChartSeries series1 = new LineChartSeries();
					series1.setLabel(st.getTermName());
					for(SelectItem ss:st.getSelectList())
					{
						Object ob = ss.getLabel();
						series1.set(ob,Double.valueOf(ss.getDescription()) );
					}

					model.addSeries(series1);

				}
			}
			else
			{
				LineChartSeries series1 = new LineChartSeries();

				series1.setLabel(termName);
				for(SelectItem ss:examList)
				{
					Object ob = ss.getLabel();
					series1.set(ob,Double.valueOf(ss.getDescription()) );
					//series1.set
				}
				model.addSeries(series1);
			}
		}
		else {
		if(selectTerm.equalsIgnoreCase("all"))
		{
			for(termInfo st:newtermList)
			{
				i=1;
				LineChartSeries series1 = new LineChartSeries();
				series1.setLabel(st.getTermName());
				for(SelectItem ss:st.getSelectList())
				{
					Object ob = ss.getLabel();
					series1.set(ob,Double.valueOf(ss.getDescription()) );
				}

				model.addSeries(series1);

			}
		}
		else
		{
			LineChartSeries series1 = new LineChartSeries();

			series1.setLabel(termName);
			for(SelectItem ss:examList)
			{
				//(ss.getDescription());
				Object ob = ss.getLabel();
				series1.set(ob,Double.valueOf(ss.getDescription()) );
				
				//series1.set
			}
			model.addSeries(series1);
		}
		}

		return model;
	}
	private void createLineModels1() {
		lineModel1 = initLinearModel();
		lineModel1.setTitle("Student Performance Chart");
		lineModel1.setLegendPosition("e");
		lineModel1.setAnimate(true);
		lineModel1.getAxis(AxisType.Y);
		lineModel1.setShowPointLabels(true);
		lineModel1.setExtender("barextt");
		  lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Exam"));
		  lineModel1.setExtender("barextt");
		/*lineModel1.setShowPointLabels(true);
    lineModel1.getAxes().put(AxisType.X, new CategoryAxis("Percentage"));
    lineModel1.getAxes().put(AxisType.Y, new CategoryAxis("Percentage"));*/


		Axis yAxis = lineModel1.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setMax(100);
		yAxis.setLabel("Percentage");
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

	public String getStudId() {
		return studId;
	}




	public void setStudId(String studId) {
		this.studId = studId;
	}




	public String getSecId() {
		return secId;
	}




	public void setSecId(String secId) {
		this.secId = secId;
	}

	public String getSingleTerm() {
		return singleTerm;
	}




	public void setSingleTerm(String singleTerm) {
		this.singleTerm = singleTerm;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSelectRadio() {
		return selectRadio;
	}
	public void setSelectRadio(String selectRadio) {
		this.selectRadio = selectRadio;
	}
	public String getSelectSutdent() {
		return selectSutdent;
	}
	public void setSelectSutdent(String selectSutdent) {
		this.selectSutdent = selectSutdent;
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