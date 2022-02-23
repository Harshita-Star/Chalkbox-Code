package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="all_income_expense_cat")
@ViewScoped
public class AllIncomeExpenseCategory implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<CategoryInfo> allCategoryList;
	CategoryInfo selectedCat;
	String incomeCategory,expenseCategory,oldIncome,oldExpense;

	public AllIncomeExpenseCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allCategoryList=new DatabaseMethods1().allIncomeExpenseCategoryList(conn);
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
		incomeCategory=selectedCat.getIncomeCategory();
		expenseCategory=selectedCat.getExpenseCategory();
		oldIncome=selectedCat.getIncomeCategory();
		oldExpense+=selectedCat.getExpenseCategory();

	}

	public void updateCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		boolean checkItem=false;
		if(!oldIncome.equalsIgnoreCase(incomeCategory))
		{
			checkItem=DBM.checkDuplicateIncomeExpenseCategory(incomeCategory,"Income",conn);
		}
		if(checkItem==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Income Category Name.Please Try With a New/Unique Name."));
			incomeCategory="";
		}
		else
		{
			if(!oldExpense.equalsIgnoreCase(expenseCategory))
			{
				checkItem=DBM.checkDuplicateIncomeExpenseCategory(expenseCategory,"Expense",conn);
			}
			if(checkItem==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Expense Category Name.Please Try With a New/Unique Name."));
				expenseCategory="";
			}
			else
			{
				int n=DBM.updateIncomeExpenseCategory(incomeCategory,expenseCategory,selectedCat.getId(),conn);
				if(n>=1)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					allCategoryList=new DatabaseMethods1().allIncomeExpenseCategoryList(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Sucessfully."));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
				}
			}

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
	    language = "Income Category-"+incomeCategory+" --Expense Category-"+expenseCategory;
		value = "Selected ID-"+selectedCat.getId();
	    
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Income Expense Category","WEB",value,conn);
		return refNo;
	}
	

	public ArrayList<CategoryInfo> getAllCategoryList() {
		return allCategoryList;
	}

	public void setAllCategoryList(ArrayList<CategoryInfo> allCategoryList) {
		this.allCategoryList = allCategoryList;
	}

	public CategoryInfo getSelectedCat() {
		return selectedCat;
	}

	public void setSelectedCat(CategoryInfo selectedCat) {
		this.selectedCat = selectedCat;
	}

	public String getIncomeCategory() {
		return incomeCategory;
	}

	public void setIncomeCategory(String incomeCategory) {
		this.incomeCategory = incomeCategory;
	}

	public String getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(String expenseCategory) {
		this.expenseCategory = expenseCategory;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
