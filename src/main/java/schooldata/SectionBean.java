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

@ManagedBean(name = "sectionBean")
@ViewScoped
public class SectionBean implements Serializable {

	String regex=RegexPattern.REGEX;
	String sectionName;
	ArrayList<SelectItem> classList;
	String selectedClass;
	DatabaseMethods1 dbm=new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator dbValidatorObj = new DataBaseValidator();

	public SectionBean()
	{
		 // System.out.println("section constructor");
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		classList=dbm.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String addSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			String session = dbm.selectedSessionDetails(schoolId,conn);
			
			if(dbValidatorObj.duplicateSection(sectionName.toUpperCase(),selectedClass,session,conn))
			{
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
	
			}
			else
			{
				if(sectionName.contains("--") || sectionName.contains("'") || sectionName.contains("#"))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Section Name is not valid", "Validation error"));
				}
				else
				{
					int i=dbm.addNewSection(sectionName.toUpperCase(),selectedClass,session,conn);
					if(i==1)
					{
						String refNo = addWorkLog(conn);
						FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Section added successfully"));
						selectedClass=null;
						sectionName=null;
						return "addSection.xhtml";
		
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
					}
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "addSection.xhtml";
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		
		value = "Class-"+dbm.classNameFromidSchid(schoolId,selectedClass, sessionValue, conn) + "---" + "Section-"+sectionName.toUpperCase();
			
		String refNo = workLg.saveWorkLogMehod(language,"Add Section","WEB",value,conn);
		return refNo;
	}



	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
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
