package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;


@ManagedBean(name="semesterBean")
@ViewScoped
public class SemesterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> classList;
	ArrayList<String> selectedClass;
	String semesterName,percentage="0";
	boolean view;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();

	public SemesterBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		classList=obj.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addSemester()
	{
		view=true;
	}
	public void editSemster()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editSemester.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public String addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		if(flag==0)
		{
			int status=0;
			for(String classId : selectedClass)
			{
				status=validator.duplicateTerm(classId,semesterName,conn);
				if(status==0)
				{
					flag++;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
					break;
				}
			}
		}
		if(flag==0)
		{
			int i=obj.addSemester(selectedClass,semesterName,percentage,conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Semester/Term added successfully"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "addSemester.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
			}

		}
		selectedClass.clear();
		semesterName=null;

		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "addSemester";
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "Class - "+selectedClass+" -- SemesterName - "+semesterName+" -- Percent - "+percentage;
		value = language;	
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Semester","WEB",value,conn);
		return refNo;
	}


	public String yes()
	{
		return "addSemester";
	}

	public String no()
	{
		return "AddClassAndSectionView";
	}

	public String getSemesterName() {
		return semesterName;
	}
	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}
	public boolean isView() {
		return view;
	}
	public void setView(boolean view) {
		this.view = view;
	}
	public ArrayList<String> getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(ArrayList<String> selectedClass) {
		this.selectedClass = selectedClass;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
