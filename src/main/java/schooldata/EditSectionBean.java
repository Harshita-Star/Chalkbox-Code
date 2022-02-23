package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;

@ManagedBean(name="editSection")
@ViewScoped
public class EditSectionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String category;
	boolean editDetailsShow;
	int id;
	ArrayList<SelectItem> classList;
	String selectedClass;
	Category selectedSection;
	ArrayList<Category> categoryList;
	DatabaseMethods1 obj= new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator dbValidatorObj =  new DataBaseValidator();
	
	public EditSectionBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		categoryList=obj.sectionList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int a=obj.checkforstudent("classid",selectedSection.getId(),conn);
		if(a==2)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Delete this section because there are students in this Section" ));
		}
		else
		{
			int count = obj.countSectionInClass(selectedSection.getClassid(),conn);
			if(count>1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int i=obj.deleteSection(selectedSection.getId(),conn);
				obj.deleteClassTest2(String.valueOf(selectedSection.getId()),conn);
				if(i==1)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Selected Section deleted successfully"));

					categoryList=obj.sectionList(conn);
					editDetailsShow=false;
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You can not delete this section because minimum one section is required in a class" ));
			}


			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String sectionname =obj.sectionNameByIdSchid(schoolId,String.valueOf(selectedSection.getId()), conn);
		
		value = "SectionId-"+selectedSection.getId()+"--- Name-"+sectionname;
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Section","WEB",value,conn);
		return refNo;
	}

	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		selectedClass = selectedSection.getClassid();
		try 
		{
			if(dbValidatorObj.duplicateSection(category.toUpperCase(),selectedClass,sessionValue,conn))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));

			}
			else
			{
				if(category.contains("--") || category.contains("'") || category.contains("#"))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Section Name is not valid", "Validation error"));
				}
				else
				{
					int i=obj.updateSection(selectedSection.getId(), category,selectedClass,sessionValue,conn);
					if(i==1)
					{
						String refNo=addWorkLog(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Section updated successfully", "Validation error"));
						categoryList=obj.sectionList(conn);
						editDetailsShow=false;
						selectedSection=new Category();
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

					}
				}
			}
		
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void editSectionDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=obj.allClass(conn);
		id=selectedSection.getId();
		category=selectedSection.getCategory();
		selectedClass=selectedSection.getClassid();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String className=obj.classname(selectedClass, schoolId, conn);
		//String sectionname =obj.sectionNameByIdSchid(schid,String.valueOf(selectedSection.getId()), conn);
		
		value = "Class-"+className+" --- SectionId-"+selectedSection.getId()+"--- Name-"+category;
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Section","WEB",value,conn);
		return refNo;
	}


	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}

	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	public Category getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(Category selectedSection) {
		this.selectedSection = selectedSection;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
