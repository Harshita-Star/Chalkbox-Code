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

@ManagedBean(name="imprestCategoryBean")
@ViewScoped
public class ImprestCategoryBean implements Serializable {


	String regex=RegexPattern.REGEX;
	String categoryName;
	ArrayList<StudentExpeseLedgerList> list;
	FeeInfo selectedFees;
	DatabaseMethods1 obj=new DatabaseMethods1();
	public ImprestCategoryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=obj.allStudentExpenseCategoryList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public ArrayList<StudentExpeseLedgerList> getList() {
		return list;
	}

	public void setList(ArrayList<StudentExpeseLedgerList> list) {
		this.list = list;
	}
	
	

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void addNewFees()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.addStudentExpenseCategory(categoryName,conn);
		if(i>0)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Add Sucessfully"));
			categoryName="";
			list=obj.allStudentExpenseCategoryList(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
		}

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
	
		language = value = categoryName;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Imprest Category","WEB",value,conn);
		return refNo;
	}


}
