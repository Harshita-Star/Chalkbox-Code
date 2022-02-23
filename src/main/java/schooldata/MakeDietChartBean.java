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


@ManagedBean(name="dietChart")
@ViewScoped
public class MakeDietChartBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> classList,weekDayList;
	ArrayList<String> selectedClass,selectedDays;
	String diet,specialIns;

	public MakeDietChartBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		classList=obj.allClass(conn);
		weekDayList=obj.allWeekDays();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int flag=0;
		if(flag==0)
		{
			int status=0;
			for(String classId : selectedClass)
			{
				for(String day:selectedDays)
				{
					status=new DataBaseValidator().duplicateDiet(classId,day,conn);
					if(status==0)
					{
						flag++;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Entry Found,Try A Different One", "Validation error"));
						break;
					}
				}
			}
		}
		if(flag==0)
		{
			int i=new DatabaseMethods1().addDiet(selectedClass,selectedDays,diet,specialIns,conn);
			if(i==1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Diet added successfully"));
				selectedClass.clear();selectedDays.clear();diet=specialIns="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An Error Occurred,Try Again", "Validation error"));
			}

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public ArrayList<SelectItem> getWeekDayList() {
		return weekDayList;
	}

	public void setWeekDayList(ArrayList<SelectItem> weekDayList) {
		this.weekDayList = weekDayList;
	}

	public ArrayList<String> getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays(ArrayList<String> selectedDays) {
		this.selectedDays = selectedDays;
	}

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getSpecialIns() {
		return specialIns;
	}

	public void setSpecialIns(String specialIns) {
		this.specialIns = specialIns;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
