
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

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="assignAgeGroupStudent")
@ViewScoped
public class AssignAgeGroupToStudentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,sectionList,ageGroupList;
	String selectedClass,selectedSection,selectedAgeGroup,schid,session;
	ArrayList<StudentInfo> studentList,selectedStudentList=new ArrayList<>();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	int flag=0;
	
	public AssignAgeGroupToStudentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schid=DBM.schoolId();
		session=DBM.selectedSessionDetails(schid,conn);
		classList=DBM.allClass(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		ageGroupList=objEYFS.ageGroupOfSelectedClass(selectedClass, schid, session, conn);
		sectionList=DBM.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		flag=0;
		ArrayList<String> list=objStd.basicFieldsForStudentList();
		studentList=objStd.studentDetail("", selectedSection,"", QueryConstants.AGE_GROUP,QueryConstants.NOT_ASSIGNED_AGE_GROUP, null,null,"","","","",session, schid, list, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByClassSectionForRemove()
	{
		flag=1;
		Connection conn=DataBaseConnection.javaConnection();
		
		ArrayList<String> list=objStd.basicFieldsForStudentList();
		studentList=objStd.studentDetail(selectedAgeGroup, selectedSection,"", QueryConstants.AGE_GROUP,QueryConstants.AGE_GROUP, null,null,"","","","",session, schid, list, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(selectedStudentList.size()>0)
		{
			int i=objEYFS.assignAgeGroupToStudent(selectedStudentList,selectedAgeGroup,schid,session,conn);
			if(i>0) 
			{
				selectedStudentList=new ArrayList<>();
				selectedClass=selectedAgeGroup=selectedSection="";flag=0;
				sectionList=ageGroupList=new ArrayList<>();
				studentList=new ArrayList<>();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Age Group Assigned Successfully to Student(s)"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Student"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void remove()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(selectedStudentList.size()>0)
		{
			int i=objEYFS.removeAgeGroupToStudent(selectedStudentList,selectedAgeGroup,schid,session,conn);
			if(i>0) 
			{
				selectedStudentList=new ArrayList<>();
				selectedClass=selectedAgeGroup=selectedSection="";flag=0;
				sectionList=ageGroupList=new ArrayList<>();
				studentList=new ArrayList<>();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student(s) Are Deallocated From Age Group Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Student"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
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

	public String getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(String selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
