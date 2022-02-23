package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.TermInfo;

@ManagedBean(name="printOverallPerReport")
@ViewScoped
public class PrintStudentOverallPerformanceReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	ArrayList<String> scholasticColumnsList = new ArrayList<>();
	String className,sectionName;
	ArrayList<TermInfo> termList;
	
	String examName;
	String termName;
	String schoolName;
	String schoolId,session,resultPreparedby,resultCheckedBy,coordinator,principle,director;
	DatabaseMethods1 dbm=new DatabaseMethods1();
	DataBaseMethodsReports obj =new DataBaseMethodsReports();


	public PrintStudentOverallPerformanceReportBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		className=(String) ss.getAttribute("className");
		studentList=(ArrayList<StudentInfo>) ss.getAttribute("studentList");
		sectionName=(String) ss.getAttribute("sectionName");
		termList=(ArrayList<TermInfo>) ss.getAttribute("termList");
		scholasticColumnsList=(ArrayList<String>) ss.getAttribute("scholasticColumnsList");
		Connection con=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,con);
		
		if(schoolId.equals("298")||schoolId.equals("299"))
		{
			schoolName="ADROIT PROGRESSIVE SCHOOL";
			
			if(termName.equalsIgnoreCase("all"))
			{
				examName="OVERALL PERFORMANCE";
			}
			else
			{
				if(termName.contains("SEP"))
				{
					examName="HALF YEARLY EXAMINATION - "+termName;
				}
				else if(termName.contains("MAY"))
				{
					examName="UNIT TEST EXAMINATION - "+termName;
				}
				else
				{
					examName="OVERALL PERFORMANCE";
				}
			}
			
		}
		
		resultPreparedby="RESULT PREPARED BY";
		resultCheckedBy="RESULT CHECKED BY";
		coordinator="COORDINATOR";
		principle="PRINCIPAL";
		director="DIRECTOR";
		
		if(schoolId.equals("243")) {
			coordinator = "";
			director = "";
		}
		
		
		
		
		else
		{
			
			SchoolInfoList ls=dbm.fullSchoolInfo(schoolId, con);
			schoolName=ls.getSchoolName();
			
			 examName="OVERALL PERFORMANCE";
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
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
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getResultPreparedby() {
		return resultPreparedby;
	}
	public void setResultPreparedby(String resultPreparedby) {
		this.resultPreparedby = resultPreparedby;
	}
	public String getResultCheckedBy() {
		return resultCheckedBy;
	}
	public void setResultCheckedBy(String resultCheckedBy) {
		this.resultCheckedBy = resultCheckedBy;
	}
	public String getCoordinator() {
		return coordinator;
	}
	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
	}
	public String getPrinciple() {
		return principle;
	}
	public void setPrinciple(String principle) {
		this.principle = principle;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	
	
}
