package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="eyfs_classSubMarksReport")
@ViewScoped
public class EYFS_ClassSubMarksReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedClass,selectedSection,selectedTerm,mainParaId,selectedAgeGroup;
	ArrayList<SelectItem> classList,sectionList,termList,subParaList,mainParaList,ageGroupList;
	ArrayList<StudentInfo> studentDetails;

	boolean renderShowTable=false;
	String username,schoolid,type,session;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	DataBaseMeathodJson objJson = new DataBaseMeathodJson();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	public EYFS_ClassSubMarksReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		session=obj.selectedSessionDetails(schoolid,conn);

		if(type.equalsIgnoreCase("admin"))
		{
			classList=obj.allClass(conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
		}
		selectedSection=selectedTerm="";
		mainParaList=new ArrayList<>();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
		ageGroupList=objEYFS.ageGroupOfSelectedClass(selectedClass,schoolid, session, conn);
		if(type.equalsIgnoreCase("admin"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allMainParaList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		mainParaList=objEYFS.ageGroupMainParameterList(selectedAgeGroup,schoolid, session, conn);
		subParaList=new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSubParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subParaList=objEYFS.subParameterListByMainParameterAgeGroupTerm(selectedAgeGroup,selectedTerm,mainParaId, schoolid, session, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		ArrayList<String> list=objStd.basicFieldsForStudentList();
		studentDetails=objStd.studentDetail(selectedAgeGroup, selectedSection,"", QueryConstants.AGE_GROUP,QueryConstants.ASSIGNED_AGE_GROUP, null,null,"","","","",session, schoolid, list, conn);
		
		objEYFS.classSubMarksReportForEYFS(selectedTerm,selectedAgeGroup,schoolid,session,mainParaId,studentDetails,conn);
		if(studentDetails.size()>0)
		{
			int x = 1;
			for(StudentInfo ss : studentDetails)
			{
				ss.setSno(x++);
			}
			renderShowTable=true;
		}
		else
		{
			renderShowTable=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public boolean isRenderShowTable() {
		return renderShowTable;
	}
	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<SelectItem> getMainParaList() {
		return mainParaList;
	}

	public void setMainParaList(ArrayList<SelectItem> mainParaList) {
		this.mainParaList = mainParaList;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public String getMainParaId() {
		return mainParaId;
	}

	public void setMainParaId(String mainParaId) {
		this.mainParaId = mainParaId;
	}

	public ArrayList<SelectItem> getSubParaList() {
		return subParaList;
	}

	public void setSubParaList(ArrayList<SelectItem> subParaList) {
		this.subParaList = subParaList;
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
