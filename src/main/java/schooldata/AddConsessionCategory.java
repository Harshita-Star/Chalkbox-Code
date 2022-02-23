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

@ManagedBean(name="addconsessionCategory")
@ViewScoped
public class AddConsessionCategory  implements Serializable
{

	String regex=RegexPattern.REGEX;
	String category;
	ArrayList<ConnsessionList>list;
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public AddConsessionCategory() {

		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		list=dbm.allCategoryList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();

		try 
		{
			if(dbm.checkConnsessiontype(category,schoolId,conn))
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"duplicate Concession Category  Found ","Concession Category  Found"));

			}
			else
			{
				if(category.contains("--") || category.contains("'") || category.contains("#"))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Category Name is invalid","Validation Error"));
				}
				else
				{
					int id=dbm.addconncession(category,conn);
					if(id>0)
					{
						String refNo=addWorkLog(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Concession Category Added","Concession Category Added"));
						ArrayList<FeeInfo> feelist=dbm.viewFeeList(conn);
						feelist.remove(feelist.size()-1);
						feelist.remove(feelist.size()-1);
						feelist.remove(feelist.size()-1);
						dbm.insertNewConcessionCategoryInClassTableWithZeroFee(id,feelist,conn);
						list=dbm.allCategoryList(conn);
						category="";
					}
					else
					{
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Some Error Occur Please try Again","Some Error Occur Please try Again"));

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
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = category;
	
		return workLg.saveWorkLogMehod(language,"Add Concession Category","WEB",value,conn);
	}

	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ArrayList<ConnsessionList> getList() {
		return list;
	}
	public void setList(ArrayList<ConnsessionList> list) {
		this.list = list;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
