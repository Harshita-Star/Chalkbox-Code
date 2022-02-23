package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;

@ManagedBean(name="addTimeTableImage")
@ViewScoped

public class AddTimeTableImageBean implements Serializable
{
	ArrayList<SelectItem> classSection,sectionList,timeTableList;
	String selectedClass,selectedSection,timeTableImage;
	SelectItem selectedItem;
	transient UploadedFile timeTable;

	public AddTimeTableImageBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(conn);
		
		timeTableList = new DataBaseMethodsReports().allImageTimeTableList(new DatabaseMethods1().schoolId(),DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn),conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String upload()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String staff=(String) ses.getAttribute("username");
		String schoolid=(String) ses.getAttribute("schoolid");
		try
		{
			Date pDate=new Date();
			String dt=new SimpleDateFormat("yMdhms").format(pDate);
			if (timeTable != null && timeTable.getSize() > 0)
			{
				timeTableImage = timeTable.getFileName();
				String exten[]=timeTableImage.split("\\.");
				timeTableImage=staff+"_"+selectedClass+"_"+dt+"_TimeTable_"+selectedSection+"."+exten[exten.length-1];
				DBM.makeProfileSchid(DBM.schoolId(),timeTable,timeTableImage,conn);
			}

			int i=DBM.submitTimeTable(selectedClass, selectedSection, timeTableImage, conn);
			if(i>=1)
			{
				SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
				String imageurl = ls.getDownloadpath()+timeTableImage;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Time Table Upload Successfully"));
				
				
				if(schoolid.equals("302") || schoolid.equals("216") || schoolid.equals("221")) {
					
					String className = DBM.classNameFromidSchid(schoolid,selectedClass, DBM.selectedSessionDetails(schoolid, conn), conn);
					String sectionName = "";
					String clsSection = "";
					
					sectionName = DBM.sectionNameByIdSchid(schoolid,selectedSection, conn);
					clsSection = className+"-"+sectionName;
					
					ArrayList<StudentInfo> studentDetails = DBM.searchStudentListByClassSectionSchidWise(schoolid, selectedClass, selectedSection, conn);
					for(StudentInfo sydInfo : studentDetails) {
						String msg = "Dear "+sydInfo.getFullName()+",\r\n" + 
								"Time table for Class "+className+" is being uploaded in school app.\r\n" + 
								"Kindly check and set your academics accordingly.\r\n" + 
								"\r\n" + 
								"Regards\r\n" + 
								"\r\n" + 
								"Principal";
						
						
						DBJ.notification("Time Table",msg,sydInfo.getFathersPhone()+"-"+sydInfo.getAddNumber()+"-"+schoolid,schoolid,imageurl,conn);
						DBJ.notification("Time Table",msg, sydInfo.getMothersPhone()+"-"+sydInfo.getAddNumber()+"-"+schoolid,schoolid,imageurl,conn);
						
						
					}
					

				}
				else {
					DBJ.notification("Time Table","New Time Table Updated.",selectedSection+"-"+selectedClass+"-"+schoolid,schoolid,imageurl,conn);
				}
				return "addTimeTableImage.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public void delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsReports DBR=new DataBaseMethodsReports();
		int i = DBR.deleteImageTimeTable(String.valueOf(selectedItem.getValue()), conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Time Table Deleted Sucessfully"));
			timeTableList = new DataBaseMethodsReports().allImageTimeTableList(new DatabaseMethods1().schoolId(),DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn),conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured While Deleting Time Table!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void view()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selectedItem.getDescription()+"')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getTimeTableImage() {
		return timeTableImage;
	}

	public void setTimeTableImage(String timeTableImage) {
		this.timeTableImage = timeTableImage;
	}

	public UploadedFile getTimeTable() {
		return timeTable;
	}

	public void setTimeTable(UploadedFile timeTable) {
		this.timeTable = timeTable;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(ArrayList<SelectItem> timeTableList) {
		this.timeTableList = timeTableList;
	}

	public SelectItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(SelectItem selectedItem) {
		this.selectedItem = selectedItem;
	}


}
