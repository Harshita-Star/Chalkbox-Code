package schooldata;

import java.io.File;
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

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
@ManagedBean(name="viewEditDeleteAssignment")
@ViewScoped
public class ViewEditDeleteAssignment implements Serializable
{
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,assignmentPhotoPath1="",assignmentName, 
			staff, subject, type, label, des,assignmentPhoto, userType, schoolid;
	Date assShowDate=new Date(), pDate, hDate=new Date(),endDate = new Date();
	transient
	UploadedFile assignmentPhoto1;
	ArrayList<HomeWorkInfo>list=new ArrayList<>();
	ArrayList<HomeWorkInfo>selectedList=new ArrayList<>();
	HomeWorkInfo selectedActivity;
	boolean show=false;

	public ViewEditDeleteAssignment()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		schoolid=(String) ses.getAttribute("schoolid");
		userType=(String) ses.getAttribute("type");

		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("All");
				classSection.add(si);

				ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

				if(temp.size()>0)
				{
					classSection.addAll(temp);
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
						|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection = new DatabaseMethods1().cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection=new DatabaseMethods1().allClassDeatilsForTeacher(empid,schoolid,conn);
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

		//subjectList=obj.allSubjectClassWise(selectedCLassSection,conn);
		if(selectedCLassSection.equalsIgnoreCase("All"))
		{
			
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("All");
			sectionList.add(si);
		}
		else
		{	
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				sectionList = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("All");
				sectionList.add(si);

				ArrayList<SelectItem> temp = obj.allSection(selectedCLassSection,conn);

				if(temp.size()>0)
				{
					sectionList.addAll(temp);
				}
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				sectionList=obj.allSectionForTeacher(selectedCLassSection,empid,conn);
			}
		}
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

	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(endDate.before(hDate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Dates Properly"));

		}
		else
		{	
		  String hwDate = new SimpleDateFormat("yyyy-MM-dd").format(hDate);
		  String endDt = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		  list=new DatabaseMethods1().allAssignment(selectedCLassSection,subject,conn,hwDate,endDt,selectedSection);
		  show=true;
		}  
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void checkType()
	{
		label=type;
	}
	public void editActivityDetails()
	{
		type=selectedActivity.getType();
		des=selectedActivity.getDescription();
		assShowDate=selectedActivity.openingDate;
		assignmentName=selectedActivity.getAssignmentName();
		assignmentPhoto=selectedActivity.getAssignment_photo1();
		//assignmentPhoto1=selectedActivity.getAssignment_photo1();
	}
	public String editNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		pDate=new Date();
		String dt=new SimpleDateFormat("dd-MM-yyyy").format(pDate);
		if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
		{
			assignmentPhotoPath1 = assignmentPhoto1.getFileName();
			String exten[]=assignmentPhotoPath1.split("\\.");
			assignmentPhotoPath1=staff+"_"+subject+"_"+dt+"_1_"+selectedSection+"."+exten[exten.length-1];
			DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto1,assignmentPhotoPath1,conn);

		}

		int i=DBM.updateAssignment(selectedActivity.getId(),selectedCLassSection, selectedSection, selectedActivity.getSubjectId(), staff, pDate, assShowDate, assignmentPhotoPath1,assignmentName, type, des,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Updated Sucessfully"));
			String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
			String subjectName = "";
			if(selectedActivity.getSubjectId().equalsIgnoreCase("All"))
			{
				subjectName = "All";
			}
			else
			{
				subjectName = DBM.subjectNameFromid(selectedActivity.getSubjectId(), conn);
			}
			String className = DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection, session, conn);
			String sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
			String clsSection = className+"-"+sectionName;

			String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
			des = cls+"\n"+des;

			DBM.notification(DBM.schoolId(),"Home Work",des,selectedSection+"-"+selectedCLassSection+"-"+new DatabaseMethods1().schoolId(),conn);
			String hwDate = new SimpleDateFormat("yyyy-MM-dd").format(hDate);
			String endDt = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			list=DBM.allAssignment(selectedCLassSection,subject,conn,hwDate,endDt,selectedSection);

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

		return "";
	}
	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		int i=obj.deleteAssignment(selectedActivity.getId(),conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			File file = new File(info.getUploadpath()+selectedActivity.getAssignment_photo2());
			file.delete();
			
			String hwDate = new SimpleDateFormat("yyyy-MM-dd").format(hDate);
			String endDt = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			list=obj.allAssignment(selectedCLassSection,subject,conn,hwDate,endDt,selectedSection);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Assignment Deleted Successfully"));
			selectedCLassSection="";
			selectedSection="";
			subject="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	public void deleteMultiple()
	{
	  if(selectedList.size()==0)
	  {
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select a Assignment"));
	  }
      else
	 {
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		int i = 0;
		for(HomeWorkInfo ss : selectedList)
		{	
		 i = obj.deleteAssignment(ss.getId(),conn);
		 if(i>=1)
		 {	 
		   File file = new File(info.getUploadpath()+ss.getAssignment_photo2());
		   file.delete();
		   String refNo;
			try {
				refNo=addWorkLogMultiple(ss.getId(),conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }   
		}
		if(i>=1)
		{
			
			String hwDate = new SimpleDateFormat("yyyy-MM-dd").format(hDate);
			String endDt = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			list=obj.allAssignment(selectedCLassSection,subject,conn,hwDate,endDt,selectedSection);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Assignment Deleted Successfully"));
			selectedCLassSection="";
			selectedSection="";
			subject="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
		}
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
		
	    value += "Selected Id-"+selectedActivity.getId(); 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Uploaded Assignment","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLogMultiple(String Id,Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+Id; 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Uploaded Assignment","WEB",value,conn);
		return refNo;
	}
	
	
	
	public ArrayList<HomeWorkInfo> getList() {
		return list;
	}
	public void setList(ArrayList<HomeWorkInfo> list) {
		this.list = list;
	}

	public HomeWorkInfo getSelectedActivity() {
		return selectedActivity;
	}
	public void setSelectedActivity(HomeWorkInfo selectedActivity) {
		this.selectedActivity = selectedActivity;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
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
	public String getAssignmentPhotoPath1() {
		return assignmentPhotoPath1;
	}
	public void setAssignmentPhotoPath1(String assignmentPhotoPath1) {
		this.assignmentPhotoPath1 = assignmentPhotoPath1;
	}
	public Date getAssShowDate() {
		return assShowDate;
	}
	public void setAssShowDate(Date assShowDate) {
		this.assShowDate = assShowDate;
	}
	public Date getpDate() {
		return pDate;
	}
	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}
	public UploadedFile getAssignmentPhoto1() {
		return assignmentPhoto1;
	}
	public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
		this.assignmentPhoto1 = assignmentPhoto1;
	}
	public String getAssignmentName() {
		return assignmentName;
	}
	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}
	public String getStaff() {
		return staff;
	}
	public void setStaff(String staff) {
		this.staff = staff;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getAssignmentPhoto() {
		return assignmentPhoto;
	}
	public void setAssignmentPhoto(String assignmentPhoto) {
		this.assignmentPhoto = assignmentPhoto;
	}
	public Date gethDate() {
		return hDate;
	}
	public void sethDate(Date hDate) {
		this.hDate = hDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public ArrayList<HomeWorkInfo> getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(ArrayList<HomeWorkInfo> selectedList) {
		this.selectedList = selectedList;
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





}
