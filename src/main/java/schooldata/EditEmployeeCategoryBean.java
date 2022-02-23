package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;


@ManagedBean(name="editEmpCategory")
@ViewScoped
public class EditEmployeeCategoryBean implements Serializable {

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<Category> categoryList;
	Category selectedCategory;
	int id;
	String category,employeeCategory;
	public EditEmployeeCategoryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try
		{
			categoryList=new DatabaseMethods1().employeeCategoryList(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEmployeeCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int flag=0;
		int status=new DataBaseValidator().duplicateEmployeeCategory(employeeCategory,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Category already exist,choose a different one", "Validation error"));
		}
		if(flag==0)
		{
			int i=obj.addNewEmployeeCategory(employeeCategory,conn);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Category added successfully", "Validation error"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
			}
		}
		employeeCategory=null;
		categoryList=obj.employeeCategoryList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int i=obj.deleteEmployeeCategory(selectedCategory.getId(),conn);
			if(i==1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected category deleted successfully"));

				categoryList=obj.employeeCategoryList(conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int flag=0;
			int status=new DataBaseValidator().duplicateEmployeeCategory(category,conn);
			if(status==0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Category already exist,choose a different one", "Validation error"));
			}
			if(flag==0)
			{
				try
				{
					int i=obj.updateEmployeeCategory(selectedCategory.getId(), category,conn);
					if(i==1)
					{
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Category Updated successfully"));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			categoryList=obj.employeeCategoryList(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void editEmpDetails()
	{
		id=selectedCategory.getId();
		category=selectedCategory.getCategory();
	}

	public Category getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEmployeeCategory() {
		return employeeCategory;
	}

	public void setEmployeeCategory(String employeeCategory) {
		this.employeeCategory = employeeCategory;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
