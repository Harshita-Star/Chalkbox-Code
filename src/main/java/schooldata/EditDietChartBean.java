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

@ManagedBean(name="editDietChart")
@ViewScoped
public class EditDietChartBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<DietInfo> dietList;
	DietInfo selectedDiet;
	ArrayList<SelectItem> classList;
	String diet,specialIns,selectedClass;

	public EditDietChartBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=new DatabaseMethods1().allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchData()
	{
		Connection conn = DataBaseConnection.javaConnection();
		dietList=new DatabaseMethods1().allDietList(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editDietDetails()
	{
		diet=selectedDiet.getDiet();
		specialIns=selectedDiet.getSpecialIns();

	}

	public void updateNow()
	{
		DatabaseMethods1 DBM=new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		int i=DBM.updateDiet(selectedDiet.getId(),diet,specialIns,conn);
		if(i==1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Diet Updated Successfully"));
			dietList=DBM.allDietList(selectedClass,conn);

			DBM.notification(DBM.schoolId(),"Diet","Your Diet Is Updated", selectedClass+"-"+DBM.schoolId(),conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<DietInfo> getDietList() {
		return dietList;
	}

	public void setDietList(ArrayList<DietInfo> dietList) {
		this.dietList = dietList;
	}

	public DietInfo getSelectedDiet() {
		return selectedDiet;
	}

	public void setSelectedDiet(DietInfo selectedDiet) {
		this.selectedDiet = selectedDiet;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
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
