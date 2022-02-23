package reports_module;

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

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="searchStudentForPrintMarksheet2")
@ViewScoped
public class SearchStudentForPrintMarksheet2 implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String name;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	boolean show;
	ArrayList<SelectItem> classSection,sectionList;
	String selectedClassSection;
	StudentInfo selectedStudent;
	String selectedSection;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String schid,session;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj1.allSection(selectedClassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public SearchStudentForPrintMarksheet2()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		boolean check=(boolean) ss.getAttribute("checkstu");
		String addNumber=(String) ss.getAttribute("username");
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid, conn);
		
		if(check==false)
		{
			classSection=obj1.allClass(conn);
		}
		else
		{
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

	public List<String> autoCompleteStudentInfo(String query){

		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj1.searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList){
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
		if(index!=0){
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					studentList=new ArrayList<>();
					studentList.add(info);
					selectedStudent=info;
					achievement();
					return "AchievementRecord2";
				}
			}
		}
		else{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please select student name from Autocomplete list"));
		}
		return null;
	}

	public void searchStudentByClassSection(){
		Connection conn=DataBaseConnection.javaConnection();
		try{
			studentList=obj1.searchStudentListByClassSection(selectedClassSection,selectedSection,conn);
			show=true;

		}
		catch(Exception ex){
			ex.printStackTrace();
		}
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

			show=false;
			name=null;
			selectedClassSection=null;
			selectedStudent=null;

			return "AchievementRecord2";
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
}
