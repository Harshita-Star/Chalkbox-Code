package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="AddClassScheduleOnlineClass")
@ViewScoped
public class AddClassScheduleOnlineClassBean implements Serializable{

	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,userType,schoolid,subject, staff;
	Date schduleDateTime;
	
	public AddClassScheduleOnlineClassBean()
 	{
 		
 		
 		Connection conn=DataBaseConnection.javaConnection();
 		DatabaseMethods1 obj=new DatabaseMethods1();
 		SchoolInfoList info=obj.fullSchoolInfo(conn);
 		
 		
 		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
 		staff=(String) ses.getAttribute("username");
 		userType=(String)ses.getAttribute("type");
 		schoolid=(String) ses.getAttribute("schoolid");
 		try
 		{
 			if(userType.equalsIgnoreCase("admin")
 					|| userType.equalsIgnoreCase("authority")
 					|| userType.equalsIgnoreCase("principal")
 					|| userType.equalsIgnoreCase("vice principal")
 					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
 					|| userType.equalsIgnoreCase("Accounts"))
 			{
 				classSection=obj.allClass(conn);

 			}
 			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
 				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
 				classSection = obj.cordinatorClassList(empid, schoolid, conn);
 			}
 			else
 			{
 				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
 				classSection=obj.allClassDeatilsForTeacher(empid,schoolid,conn);

 			}
 		}
 		catch(Exception ex)
 		{
 			ex.printStackTrace();
 		}
 		finally {
 			try {
 				conn.close();
 			} catch (SQLException e) {
 				e.printStackTrace();
 			}
 		}

 	}
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj.allSectionWithAllOption(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedCLassSection, empid,conn);
		}
		//subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		//sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(staff,schoolid,conn);
			subjectList=objJson.AllEMployeeSubject(empid,selectedSection,schoolid,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void addSchedule()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		
		int i=new DatabaseMethods1().addOnlineClassSchedule(selectedCLassSection,selectedSection,userType,schoolid,subject, staff,schduleDateTime,conn);
		if(i>0)
		{
			
			selectedCLassSection="";
			selectedSection="";
		    subject="";
		    schduleDateTime=null;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Schdule Successfully"));
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur !! Please try again"));
					
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getSchoolid() {
		return schoolid;
	}
	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public Date getSchduleDateTime() {
		return schduleDateTime;
	}

	public void setSchduleDateTime(Date schduleDateTime) {
		this.schduleDateTime = schduleDateTime;
	}
	
	
}
