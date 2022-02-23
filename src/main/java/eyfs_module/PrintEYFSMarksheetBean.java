package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsBLMExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.TermInfo;

@ManagedBean(name="printEYFSMarksheet")
@ViewScoped
public class PrintEYFSMarksheetBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	ArrayList<StudentInfo> listOfStudent;
	String dateEntry,headerImagePath,classId,sectionId,ageGroupId;
	ArrayList<TermInfo> termList;
	String fName,fatherName,className,sno,session,motherName,dobString,fathersphone;
	SchoolInfoList info;
	String termId="",tickImagePath;
	ArrayList<EyfsInfo> resultInfo;
	DataBaseMethodsBLMExam objBlmExam = new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj1=new DatabaseMethods1();
    String schoolId;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();


	public PrintEYFSMarksheetBean()
	{
		tickImagePath="rightTick.png";
		Connection conn=DataBaseConnection.javaConnection();
		
		String savePath="";
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session=(String) ss.getAttribute("session");
		termId=(String) ss.getAttribute("termId");
		classId=(String) ss.getAttribute("classId");
		sectionId=(String) ss.getAttribute("sectionId");
		ageGroupId=(String) ss.getAttribute("ageGroupId");

		//createCombinedModel();

		listOfStudent=(ArrayList<StudentInfo>) ss.getAttribute("StudentList");

		info=obj1.fullSchoolInfo(conn);
		schoolId=obj1.schoolId();

		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		headerImagePath=savePath+info.getMarksheetHeader();
		Date date=(Date) ss.getAttribute("dateOfEntry");
		dateEntry=new SimpleDateFormat("dd-MM-yyyy").format(date);

		for(StudentInfo allInfo : listOfStudent)
		{
			allInfo.setSession(session);
			fName=allInfo.getFname();
			fatherName=allInfo.getFathersName();
			className=allInfo.getClassName()+"-"+allInfo.getSectionName();
			motherName=allInfo.getMotherName();
			dobString=allInfo.getDobString();
			sno=String.valueOf(allInfo.getAddNumber());
			fathersphone=String.valueOf(allInfo.getFathersPhone());
			resultInfo=objEYFS.fillStudentMarks(termId,ageGroupId,sectionId,allInfo.getAddNumber(),session,schoolId,allInfo,conn);
			allInfo.setEyfsMarkList(resultInfo);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}

	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public String getFathersphone() {
		return fathersphone;
	}

	public void setFathersphone(String fathersphone) {
		this.fathersphone = fathersphone;
	}
	public String getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(String dateEntry) {
		this.dateEntry = dateEntry;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}


	public String getTickImagePath() {
		return tickImagePath;
	}


	public void setTickImagePath(String tickImagePath) {
		this.tickImagePath = tickImagePath;
	}

	/* public CartesianChartModel combinedModel;

	    public CartesianChartModel getCombinedModel() {
	        return combinedModel;
	    }

	    public void createCombinedModel() {
	        combinedModel = new BarChartModel();

	        BarChartSeries boys = new BarChartSeries();
	        boys.setLabel("Boys");

	        boys.set("2004", 120);
	        boys.set("2005", 100);
	        boys.set("2006", 44);
	        boys.set("2007", 150);
	        boys.set("2008", 25);

	        LineChartSeries girls = new LineChartSeries();
	        girls.setLabel("Girls");

	        girls.set("2004", 52);
	        girls.set("2005", 60);
	        girls.set("2006", 110);
	        girls.set("2007", 135);
	        girls.set("2008", 120);

	        combinedModel.addSeries(boys);
	        combinedModel.addSeries(girls);

	        combinedModel.setTitle("Bar and Line");
	        combinedModel.setLegendPosition("ne");
	        combinedModel.setMouseoverHighlight(false);
	        combinedModel.setShowDatatip(false);
	        combinedModel.setShowPointLabels(true);
	        Axis yAxis = combinedModel.getAxis(AxisType.Y);
	        yAxis.setMin(0);
	        yAxis.setMax(200);
	    }*/
}
