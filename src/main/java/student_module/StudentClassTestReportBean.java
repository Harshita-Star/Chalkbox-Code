package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;

import Json.DataBaseMeathodJson;
import schooldata.ClassTest;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="studentClassTestReport")
@ViewScoped
public class StudentClassTestReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name,className,sectionName;
	int total,maxMarks = 0;
	ArrayList<StudentInfo> studentList;
	boolean show;
	ArrayList<ClassTest> list;
	BarChartModel testGraph;
	ArrayList<ClassTest> obtainedMarks = new ArrayList<>();
	ArrayList<ClassTest> totalMarks = new ArrayList<>();
	String[] arrOfStr = new String[4];
	String schid;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();



	public StudentClassTestReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		schid = obj.schoolId();
		

		try
		{
			StudentInfo info=DBJ.studentDetailslistByAddNo(studentid,schid,conn);
			className=info.getClassName();

			list=obj.allTestReport(studentid,conn,"student");
			total=list.size();


			testGraph = new BarChartModel();
			BarChartSeries chart = new BarChartSeries();
			chart.setLabel("Marks");

			for(int j=0;j<list.size();j++)
			{
				arrOfStr = list.get(j).getTestMarks().split("/");
				ClassTest obtain = new ClassTest();
				ClassTest total = new ClassTest();
				
				if(arrOfStr[0].equalsIgnoreCase("AB") || arrOfStr[0].equalsIgnoreCase("ML") || arrOfStr[0].equalsIgnoreCase("NA") || arrOfStr[0].equals(""))
				{
				   obtain.setObtain(0);
				}
				else
				{
					obtain.setObtain(Double.parseDouble(arrOfStr[0]));
					if(Integer.parseInt(arrOfStr[0])>maxMarks)
					{
						maxMarks = Integer.parseInt(arrOfStr[0]);
					}
				}

				
				//total.setTotal(Integer.parseInt(arrOfStr[1]));
				total.setTotalmarks(arrOfStr[1]);
				obtainedMarks.add(obtain);

				totalMarks.add(total);
			}

			for(int i = 0;i<list.size();i++)
			{
				chart.set(list.get(i).getTestName()+"-"+list.get(i).getSubject()+"("+totalMarks.get(i).getTotalmarks()+")",obtainedMarks.get(i).getObtain());
				//chart.set("w", 34);

			}

			testGraph.addSeries(chart);
			//    testGraph.setSeriesColors("ff9933, 77933c");
			testGraph.setTitle("Marks Report");
			testGraph.setAnimate(true);


			testGraph.setLegendPosition("ne");

			Axis yAxis = testGraph.getAxis(AxisType.Y);
			yAxis.setMin(0);
			yAxis.setMax( maxMarks + 20);
			yAxis.setLabel("Obtained Marks");

			Axis xAxis = testGraph.getAxis(AxisType.X);
			xAxis.setLabel("Test List ");

			show=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public ArrayList<ClassTest> getList() {
		return list;
	}
	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public BarChartModel getTestGraph() {
		return testGraph;
	}
	public void setTestGraph(BarChartModel testGraph) {
		this.testGraph = testGraph;
	}




}
