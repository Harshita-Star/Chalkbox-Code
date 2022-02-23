package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


@ManagedBean(name="editExpenseCategory")
@ViewScoped
public class EditExpenseCategoryBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String editCategory,category;
	int id;
	ArrayList<Category> categoryList;
	Category list;

	public EditExpenseCategoryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			categoryList=new DatabaseMethods1().expenseCategoryList(conn);
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

	public void addExpenseCategory()
	{
		Connection conn =DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int flag=0;
		int status=new DataBaseValidator().duplicateExpenseCategory(category,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));

		}
		if(flag==0)
		{
			int i=obj.addNewExpenseCategory(category,conn);
			if(i==1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Category added successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));

			}
		}
		category=null;
		categoryList=obj.expenseCategoryList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editExpenseDetails()
	{
		editCategory=list.getCategory();
	}

	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int flag=0;
		int status=new DataBaseValidator().duplicateExpenseCategory(editCategory,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));

		}
		if(flag==0)
		{
			int i=obj.updateExpenseCategory(list.getId(), editCategory,conn);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Category Updated successfully", "Validation error"));

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
			}
		}
		categoryList=obj.expenseCategoryList(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int i=obj.deleteExpenseCategory(list.getId(),conn);
		if(i==1)
		{
			categoryList=obj.expenseCategoryList(conn);

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected expense deleted successfully"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
