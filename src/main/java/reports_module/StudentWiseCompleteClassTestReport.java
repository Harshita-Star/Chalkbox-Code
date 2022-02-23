package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import exam_module.DataBaseMethodsExam;
import schooldata.ClassTest;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="stdCompltClsTstReport")
@ViewScoped
public class StudentWiseCompleteClassTestReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,className,sectionName;
	ArrayList<SubjectInfo> subjectList;
	ArrayList<StudentInfo> studentList;
	boolean show;
	ArrayList<ClassTest> list;
	DatabaseMethodSession objSess = new DatabaseMethodSession();
	DataBaseMethodsReports DBM=new DataBaseMethodsReports();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,session;
	
    public StudentWiseCompleteClassTestReport() {
		
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=new DatabaseMethods1().searchStudentList(query,conn);
		studentList=objSess.searchStudentListWithPreSessionStudent("byName",query, "basic", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	public void searchStudentByName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<SubjectInfo> coscholasticList = new ArrayList<SubjectInfo>();
	
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					studentList=new ArrayList<>();
					studentList.add(info);
					className=info.getClassName();
					subjectList=objExam.subjectListBySubjectType(info.getClassId(), "scholastic", conn, "test");
					coscholasticList=objExam.subjectListBySubjectType(info.getClassId(), "coscholastic", conn, "test");
					subjectList.addAll(coscholasticList);
					list=DBM.completeClassTestReport(info.getAddNumber(),conn);
					subjectList.remove(subjectList.size()-1);
					
					show=true;
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} 
		catch (SQLException e)
		{
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

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
