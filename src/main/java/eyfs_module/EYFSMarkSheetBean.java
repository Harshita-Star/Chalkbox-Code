package eyfs_module;

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

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="eyfsMarksheet")
@ViewScoped
public class EYFSMarkSheetBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name,termId,selectedAgeGroup;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	Date dateOfEntry;
	ArrayList<SelectItem> classSection,sectionList,termList,ageGroupList;
	String selectedClassSection,selectedSection,session,schid;
	StudentInfo selectedStudent;
	boolean show;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();

		termList=obj1.selectedTermOfClass(selectedClassSection,conn,session,schid);
		ageGroupList=objEYFS.ageGroupOfSelectedClass(selectedClassSection,schid, session, conn);
		sectionList=obj1.allSection(selectedClassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public EYFSMarkSheetBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=(boolean) ss.getAttribute("checkstu");
		
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid,conn);
		
		if(check==false)
		{
			classSection=obj1.allClass(conn);
		}
		else
		{
			String addNumber=(String) ss.getAttribute("username");
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, list, conn).get(0);
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
		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj1.searchStudentList(query,conn);
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

	public void allTerm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);

		String selClass=obj1.studentDetailslistByAddNo(schid,id, conn).getClassId();
		termList=obj1.selectedTermOfClass(selClass,conn,session,schid);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudentByName()
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
					selectedClassSection=info.getClassId();
					studentList.add(info);
					show=true;
					break;
				}
			}
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please Select Student Name From Autocomplete List"));
		}
	}

	public void searchStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<String> list=objStd.basicFieldsForStudentList();
		studentList=objStd.studentDetail(selectedAgeGroup, selectedSection,"", QueryConstants.AGE_GROUP,QueryConstants.ASSIGNED_AGE_GROUP, null,null,"","","","",session, schid, list, conn);
		if(studentList.size()>0)
		{
			show=true;
		}
		else
		{
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void proceed()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj1.schoolId();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("StudentList", studentList);
		ss.setAttribute("session",DatabaseMethods1.selectedSessionDetails(schid,conn));
		ss.setAttribute("dateOfEntry", dateOfEntry);
		ss.setAttribute("termId", termId);
		ss.setAttribute("classId", studentList.get(0).getClassId());
		ss.setAttribute("sectionId", studentList.get(0).getSectionid());
		ss.setAttribute("ageGroupId", studentList.get(0).getAgeGroupId());
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PrimeFaces.current().executeInitScript("window.open('printEYFSMarksheet.xhtml')");
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
	public Date getDateOfEntry() {
		return dateOfEntry;
	}
	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(String selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}

}
