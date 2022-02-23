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

import exam_module.ExamInfo;
import session_work.RegexPattern;


@ManagedBean(name="editTerm")
@ViewScoped
public class EditSemesterBean implements Serializable{

	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<ExamInfo> semesterList;
	ExamInfo selectedSemester;
	ArrayList<SelectItem> classList;
	String semesterName,selectedClass,semesterNameTemp,selectedClassTemp,percentage;
	boolean editDetailsShow;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();


	public void deleteNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		int i=obj.deleteSemester(selectedSemester.getId(),conn);
		if(i==1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			semesterList=obj
					.termList(conn);
			editDetailsShow=false;

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Semester deleted successfully"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public EditSemesterBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		semesterList=obj.termList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void editTermDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=obj.allClass(conn);

		selectedClass=selectedSemester.getClassid();
		semesterName=selectedSemester.getSemesterName();
		percentage=selectedSemester.getTermPercentage();
		semesterNameTemp=semesterName;
		selectedClassTemp=selectedClass;

		editDetailsShow=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedClass = selectedSemester.getClassid();
		int flag=0;
		if(!(selectedClassTemp.equals(selectedClass) && semesterNameTemp.equals(semesterName) ))
		{
			if(flag==0)
			{
				int status=validator.duplicateTerm(selectedClass,semesterName,conn);
				if(status==0)
				{
					flag++;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));
				}
			}
		}
		if(flag==0)
		{
			int i=obj.updateTerm(selectedSemester.getId(), selectedClass, semesterName,percentage,conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Semester/Term updated successfully"));
				selectedSemester=new ExamInfo();
				semesterList=obj.termList(conn);
				editDetailsShow=false;

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
			}
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
		
		language = "Class - "+selectedClass+" -- SemesterId - "+selectedSemester.getId()+" -- SemesterName - "+semesterName+" -- Percent - "+percentage;
		value = language;	
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Semester","WEB",value,conn);
		return refNo;
	}
	

	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "SemesterId - "+selectedSemester.getId();;
			
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Semester","WEB",value,conn);
		return refNo;
	}
	
	

	public ExamInfo getSelectedSemester() {
		return selectedSemester;
	}

	public void setSelectedSemester(ExamInfo selectedSemester) {
		this.selectedSemester = selectedSemester;
	}
	public ArrayList<ExamInfo> getSemesterList() {
		return semesterList;
	}

	public void setSemesterList(ArrayList<ExamInfo> semesterList) {
		this.semesterList = semesterList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}

	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}
	public String getSemesterNameTemp() {
		return semesterNameTemp;
	}

	public void setSemesterNameTemp(String semesterNameTemp) {
		this.semesterNameTemp = semesterNameTemp;
	}

	public String getSelectedClassTemp() {
		return selectedClassTemp;
	}

	public void setSelectedClassTemp(String selectedClassTemp) {
		this.selectedClassTemp = selectedClassTemp;
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
