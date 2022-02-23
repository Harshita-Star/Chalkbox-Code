package student_module;

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
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="updateRollNo")
@ViewScoped
public class UpdateRollNoBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList;
	String selectedCLassSection,selectedSection,schoolId;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<StudentInfo>list=new ArrayList<>();
	Boolean show;
	
	public UpdateRollNoBean()
	{
	   Connection conn=DataBaseConnection.javaConnection();
	   schoolId = obj.schoolId();	
	   classSection=obj.allClass(conn);
      
	   
	   try {
		conn.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		list=obj.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
		show = true;
		
	try {
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
		
	}
	
	
	public void update()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int k =obj.updateRollNoMethod(list,conn);
		if(k>=1)
		{
			sectionList = new ArrayList<SelectItem>(); 
			selectedCLassSection="";selectedSection="";
			show = false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Roll No Updated"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		
	try {
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
		
	}
	
	
	
	

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public Boolean getShow() {
		return show;
	}

	public void setShow(Boolean show) {
		this.show = show;
	}
	




}
