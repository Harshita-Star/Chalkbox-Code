package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import session_work.QueryConstants;

@ManagedBean(name="stdFinalMarksheet")
@ViewScoped

public class StudentFinalMarksheet implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name,selectedClassSection,selectedSection,session,schoolId,totalObtainMarks,username,userType;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	ArrayList<SubjectInfo> markList;
	boolean show;
	Date date=new Date();
	ArrayList<SelectItem> classSection,sectionList;
	ArrayList<String> scholasticColumnsList = new ArrayList<>();
	StudentInfo selectedStudent;
	String totalMarksObtained;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=obj.allSection(selectedClassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedClassSection,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void proceed()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("StudentList", studentList);
		ss.setAttribute("dt", date);
		show=false;name=selectedClassSection=null;selectedStudent=null;
		PrimeFaces.current().executeInitScript(
				"window.open('printFormatOfRbseMarksheet.xhtml')");
		//return "printFinalMarksheet";
	}
	public StudentFinalMarksheet()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		boolean check=(boolean) ss.getAttribute("checkstu");
		String addNumber=(String) ss.getAttribute("username");
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId,conn);
		if(check==false)
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") 
					|| userType.equalsIgnoreCase("office staff") 
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection=obj.allClass(conn);
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection=obj.cordinatorClassList(empid, schoolId, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection=obj.allClassDeatilsForTeacher(empid,schoolId,conn);

			}
		}
		else
		{
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schoolId, list, conn).get(0);
			studentList.add(sn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=obj.searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public String searchStudentByName()
	{
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					studentList=new ArrayList<>();
					studentList.add(info);
					break;

				}
			}
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("StudentList", studentList);
			ss.setAttribute("dt", date);
			PrimeFaces.current().executeInitScript(
					"window.open('printFormatOfRbseMarksheet.xhtml')");
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please select student name from Autocomplete list"));
		}
		return null;
	}

	public void searchStudentByClassSection()
	{
		Connection conn =DataBaseConnection.javaConnection();
		studentList=obj.searchStudentListByClassSection(selectedClassSection,selectedSection,conn);
		/*for(StudentInfo tt:studentList){
	    totalMarksObtained=new DatabaseMethods1().totalmarksObtainedByStudent(tt.getAddNumber());
		}*/
		show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String achievement()
	{
		if(selectedStudent!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("StudentDetail", selectedStudent);
			ss.setAttribute("dt", date);
			show=false;
			name=null;
			selectedClassSection=null;
			selectedStudent=null;

			return "printFormatOfRbseMarksheet";
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage(" Please select atleast one student"));
		}
		return null;
	}


	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
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
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<SubjectInfo> getMarkList() {
		return markList;
	}

	public void setMarkList(ArrayList<SubjectInfo> markList) {
		this.markList = markList;
	}

	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public String getTotalObtainMarks() {
		return totalObtainMarks;
	}

	public void setTotalObtainMarks(String totalObtainMarks) {
		this.totalObtainMarks = totalObtainMarks;
	}

	public String getTotalMarksObtained() {
		return totalMarksObtained;
	}

	public void setTotalMarksObtained(String totalMarksObtained) {
		this.totalMarksObtained = totalMarksObtained;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
