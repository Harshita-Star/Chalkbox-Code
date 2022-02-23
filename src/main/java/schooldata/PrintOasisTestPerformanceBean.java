package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsExam;
@ManagedBean(name="printOasisPerformanceClassWise")
@ViewScoped
public class PrintOasisTestPerformanceBean implements Serializable
{
	ArrayList<SelectItem> test;
	ArrayList<StudentInfo> studentDetails;
	String totalmarks,selectedClass,selectedSection,selectedTerm,selectedExam,subExam,className,date,sectionName,termName,examName;
	ArrayList<ClassTest> list;
	String schid="";
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

	public PrintOasisTestPerformanceBean()
	{
		date=sdf.format(new Date());
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedClass=(String) ss.getAttribute("selectedClass");
		selectedSection=(String) ss.getAttribute("selectedSection");
		selectedTerm=(String) ss.getAttribute("selectedTerm");
		selectedExam=(String) ss.getAttribute("selectedExam");
		subExam=(String) ss.getAttribute("subExam");
		search();
	}

	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		className=DBM.classNameFromidSchid(DBM.schoolId(),selectedClass, DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn), conn);
		sectionName=DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
		termName=DBM.semesterNameFromid(selectedTerm, conn);
		examName=DBM.examNameFromid(selectedExam, conn);
		list=DBM.allExamListClassWise(selectedClass,selectedTerm,selectedExam,subExam,conn);
		if(list.size()>0)
		{
			studentDetail();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=new DatabaseMethods1().schoolId();
		test=new DatabaseMethods1().sectionNameAndIdListByid(selectedClass,conn);
		studentDetails=new DataBaseMethodsExam().studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		ArrayList<StudentInfo> studentWithNAStudent=new ArrayList<>();
		ArrayList<StudentInfo> studentWithOutNAStudent=new ArrayList<>();

		DatabaseMethods1.unitTestPerformanceForRank(studentDetails,selectedTerm,selectedExam,subExam,selectedSection,conn,list);

		for(int j=0;j<studentDetails.size();j++)
		{

			if(studentDetails.get(j).getTotalTestNo()==null || studentDetails.get(j).getTotalTestNo().equals("NA"))
			{

				studentWithNAStudent.add(studentDetails.get(j));
			}
			else
			{
				studentWithOutNAStudent.add(studentDetails.get(j));
			}
		}

		Collections.sort(studentWithOutNAStudent,new MySalaryComp1());
		totalmarks=list.get(list.size()-1).getTotalmarks();
		studentDetails=new ArrayList<>();
		studentDetails.addAll(studentWithOutNAStudent);
		studentDetails.addAll(studentWithNAStudent);
		int counter=1;
		if(studentDetails.size()>0 )
		{
			for(int i=0;i<studentDetails.size();i++)
			{

				if(studentDetails.get(i).getTotalTestNo()==null || studentDetails.get(i).getTotalTestNo().equals("NA") )
				{
					studentDetails.get(i).setRank("NA");
				}
				else if(i==0 || (studentDetails.get(i).getTotalTestNo().equals(studentDetails.get(i-1).getTotalTestNo())))
				{
					studentDetails.get(i).setRank(String.valueOf(counter));
				}
				else
				{
					++counter;
					studentDetails.get(i).setRank(String.valueOf(counter));
				}
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<SelectItem> getTest() {
		return test;
	}

	public void setTest(ArrayList<SelectItem> test) {
		this.test = test;
	}

	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}

	public String getTotalmarks() {
		return totalmarks;
	}

	public void setTotalmarks(String totalmarks) {
		this.totalmarks = totalmarks;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(String selectedExam) {
		this.selectedExam = selectedExam;
	}

	public String getSubExam() {
		return subExam;
	}

	public void setSubExam(String subExam) {
		this.subExam = subExam;
	}

	public ArrayList<ClassTest> getList() {
		return list;
	}

	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}



	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}



	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			if(!(e1.getTotalTestNo()==null || e1.getTotalTestNo().equals("0") ||  e2.getTotalTestNo().equals("0") || e1.getTotalTestNo().equals("NA") ||  e2.getTotalTestNo().equals("NA")))
			{
				if(Float.parseFloat(e1.getTotalTestNo()) <= Float.parseFloat(e2.getTotalTestNo())){
					return 1;
				} else {
					return -1;
				}
			}

			return -1;
		}
	}

	class MySalaryComp1 implements Comparator<StudentInfo>{

		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			return e1.getFullName().compareToIgnoreCase(e2.getFullName());
		}


	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}
}
