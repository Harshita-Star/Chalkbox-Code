package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


@ManagedBean(name="editIncomeCategory")
@ViewScoped
public class EditIncomeCategoryBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String editCategory,category;
	int id;
	Category list;
	ArrayList<Category> categoryList;

	public EditIncomeCategoryBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			categoryList=new DatabaseMethods1().incomeCategoryList(conn);
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

	public void addIncomeCategory()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int flag=0;
		int status=new DataBaseValidator().duplicateIncomeCategory(category,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
		}
		if(flag==0)
		{
			try
			{
				int i=obj.addNewIncomeCategory(category,conn);
				if(i==1)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Category added successfully"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		categoryList=obj.incomeCategoryList(conn);
		category=null;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editIncomeDetails()
	{
		editCategory=list.getCategory();
	}

	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			int flag=0;
			int status=new DataBaseValidator().duplicateIncomeCategory(editCategory,conn);
			if(status==0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
			}
			if(flag==0)
			{
				try
				{
					int i=obj.updateIncomeCategory(list.getId(), editCategory,conn);
					if(i==1)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Category updated successfully", "Validation error"));

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			categoryList=obj.incomeCategoryList(conn);
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

	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			int i=obj.deleteIncomeCategory(list.getId(),conn);
			if(i==1)
			{
				categoryList=obj.incomeCategoryList(conn);

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Selected Income Category deleted successfully"));
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

	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory() {
		return category;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Category getList() {
		return list;
	}

	public void setList(Category list) {
		this.list = list;
	}

	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public String getEditCategory() {
		return editCategory;
	}

	public void setEditCategory(String editCategory) {
		this.editCategory = editCategory;
	}
}
