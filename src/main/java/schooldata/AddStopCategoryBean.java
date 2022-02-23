package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="addStopCategory")
@ViewScoped
public class AddStopCategoryBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,amount,schid,session,id;
	ArrayList<BusInfo> categoryList;
	int totalCategory;
	boolean checkUpdate=false;
	BusInfo selectedCategory;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public AddStopCategoryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);
		categoryList();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void categoryList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=obj.allStopCategoryList(schid,session,conn);
		totalCategory=categoryList.size();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		id=selectedCategory.getId();
		name=selectedCategory.getBusName();
		amount=selectedCategory.getAmount();
		checkUpdate=true;
	}

	public void saveData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		if(checkUpdate==true)
		{
			PrimeFaces.current().executeInitScript("PF('askDialog').show()");
			PrimeFaces.current().ajax().update("askForm");;
		}
		else
		{
			i=new DatabaseMethods1().addStopCategory(name, amount, schid, session, conn);
			if(i>0)
			{
				name=amount="";
				categoryList();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Added Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
			}
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void deleteCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean check=obj.checkCategoryDeletedOrNot(selectedCategory.getId(),schid,conn);
		if(check==true)
		{
			int i=obj.deleteCategory(selectedCategory.getId(),conn);
			if(i>0)
			{
				categoryList();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Deleted Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Can Not Be Deleted"));
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateAllEntry()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.updateOldStopCategory(id,schid,amount,conn);
		i=new DatabaseMethods1().updateStopCategory(id,name,amount,schid,session,conn);
		if(i>0)
		{
			name=amount="";
			categoryList();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Updated Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateForFuture()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		i=new DatabaseMethods1().updateStopCategory(id,name,amount,schid,session,conn);
		if(i>0)
		{
			name=amount="";
			categoryList();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category Updated Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public ArrayList<BusInfo> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<BusInfo> categoryList) {
		this.categoryList = categoryList;
	}

	public int getTotalCategory() {
		return totalCategory;
	}

	public void setTotalCategory(int totalCategory) {
		this.totalCategory = totalCategory;
	}

	public BusInfo getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(BusInfo selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
