package exam_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="finalOasisMarks")
@ViewScoped
public class FinalOasisMarksUpTo3Bean  implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList;
	boolean show;
	StudentInfo seletedStudent;
	ArrayList<String> selectedTerm=new ArrayList<>();
	ArrayList<SelectItem> classSection,sectionList,termList=new ArrayList<>();
	String selectedSection,selectedClassSection,name,schid,termId,session,username,userType;
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DatabaseMethods1 obj= new DatabaseMethods1();
	
	public FinalOasisMarksUpTo3Bean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") )
		{
			classSection=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj.allClassListForClassTeacher(empid,schid,conn);

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClassSection,conn,session,schid);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=new DatabaseMethods1().allSection(selectedClassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClassSection,conn);
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
		studentList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"like",query);
		List<String> studentListt=new ArrayList<>();

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

	public String searchStudentByName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);

		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						selectedClassSection=info.getClassId();
						termList=obj.selectedTermOfClass(selectedClassSection,conn,session,schid);
						selectedSection=info.getSectionid();
						studentList.add(info);
						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}


	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void fillMarks()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ArrayList<StudentInfo>list=new ArrayList<>();
		list.add(seletedStudent);
		ss.setAttribute("StudentList", list);
		ss.setAttribute("termId", termId);
		PrimeFaces.current().executeInitScript("window.open('studentPerformanceOther.xhtml')");
	}

	public void print()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ArrayList<StudentInfo>list=new ArrayList<>();
		list.add(seletedStudent);
		ss.setAttribute("StudentList", list);
		ss.setAttribute("classId", selectedClassSection);
		ss.setAttribute("sectionId", selectedSection);
		
		
		if(schid.equals("216") || schid.equals("221") )
		{
			String termId="";
			for(String term:selectedTerm)
			{
				termId+=term+"','";
			}
			termId=termId.substring(0,termId.lastIndexOf("','"));
			ss.setAttribute("termId", termId);

			PrimeFaces.current().executeInitScript("window.open('printMarksheetEvergreenPre.xhtml')");
		}
		
		
	}
	
	public void printMultiple()
	{

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
		ss.setAttribute("StudentList", studentList);
		ss.setAttribute("classId", selectedClassSection);
		ss.setAttribute("sectionId", selectedSection);
		
		

		if(schid.equals("216") || schid.equals("221") )
		{
			String termId="";
			for(String term:selectedTerm)
			{
				termId+=term+"','";
			}
			termId=termId.substring(0,termId.lastIndexOf("','"));
			ss.setAttribute("termId", termId);

			PrimeFaces.current().executeInitScript("window.open('printMarksheetEvergreenPre.xhtml')");
		}
	}
	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
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
	public String getName()
	{
		return name;
	}


	public ArrayList<String> getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(ArrayList<String> selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public StudentInfo getSeletedStudent() {
		return seletedStudent;
	}

	public void setSeletedStudent(StudentInfo seletedStudent) {
		this.seletedStudent = seletedStudent;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}
}
