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

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsExam;
@ManagedBean(name="viewEditDeleteNotes")
@ViewScoped
public class ViewEditDeleteNotes implements Serializable
{
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,assignmentPhotoPath1="",assignmentName, staff, subject, label, des,assignmentPhoto;
	Date assShowDate=new Date(), pDate, hDate=new Date();
	transient
	UploadedFile assignmentPhoto1;
	ArrayList<HomeWorkInfo>list=new ArrayList<>();
	HomeWorkInfo selectedActivity;
	boolean show=false,showAdmin=false,showTeacher=false;
	ArrayList<String> fileList = new ArrayList<>();
	String selected1;
	String schid,session,empid,type;
	
	
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();

	public ViewEditDeleteNotes()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ss.getAttribute("username");
		schid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);
		try
		{
			// // System.out.println(type);
			if(type.equalsIgnoreCase("admin") 
					|| type.equalsIgnoreCase("Principal") 
					|| type.equalsIgnoreCase("Vice Principal") 
					|| type.equalsIgnoreCase("Authority")
					|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
					|| type.equalsIgnoreCase("Accounts"))
			{
				showAdmin=true;
				showTeacher=false;
				if(type.equalsIgnoreCase("admin"))
				{
					empid=staff;
				}
				else
				{
					empid=objJSON.employeeIdbyEmployeeName(staff,schid,conn);
				}
				classSection=obj.allClass(conn);
			}
			else if(type.equalsIgnoreCase("academic coordinator") 
					|| type.equalsIgnoreCase("Administrative Officer") )
			{
				showAdmin=true;
				showTeacher=false;
				empid=objJSON.employeeIdbyEmployeeName(staff,schid,conn);
				classSection=obj.cordinatorClassList(empid, schid, conn);
			}
			else
			{
				showAdmin=false;
				showTeacher=true;
				empid=objJSON.employeeIdbyEmployeeName(staff,schid,conn);
				classSection=obj.allClassDeatilsForTeacher(empid,schid,conn);
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

		//subjectList=obj.allSubjectClassWise(obj.schoolId(),selectedCLassSection,conn);
		if(type.equalsIgnoreCase("admin") 
				|| type.equalsIgnoreCase("Principal") 
				|| type.equalsIgnoreCase("Vice Principal") 
				|| type.equalsIgnoreCase("Authority")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer")
				|| type.equalsIgnoreCase("Accounts"))
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
			sectionList=obj.allSectionForTeacher(selectedCLassSection, empid,conn);
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
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(type.equalsIgnoreCase("admin") 
				|| type.equalsIgnoreCase("Principal") 
				|| type.equalsIgnoreCase("Vice Principal") 
				|| type.equalsIgnoreCase("Authority")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer")
				|| type.equalsIgnoreCase("Accounts"))
		{
			subjectList=obj.allSubjectClassWise(selectedCLassSection,conn);
		}
		else
		{
			subjectList=objExam.AllEMployeeSubjectByType(empid,selectedSection,schid,"NA",conn);
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
		list=new DatabaseMethods1().allNotes(selectedCLassSection,subject,conn,selectedSection);
		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.deleteAssignment(selectedActivity.getId(),conn);
		if(i>=1)
		{
			list=obj.allNotes(selectedCLassSection,subject,conn,selectedSection);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Selected Notes Deleted Successfully"));
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
	
	
	
	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		//		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);
		//		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getAss1()+"')");
		String arr1[]=selectedActivity.getAss1().split(",");
		fileList=new ArrayList<>();
		for(int i=0;i<arr1.length;i++)
		{
			fileList.add(arr1[i]);
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void view1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(new DatabaseMethods1().schoolId(),conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected1+"')");

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public ArrayList<String> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<String> fileList) {
		this.fileList = fileList;
	}

	public String getSelected1() {
		return selected1;
	}

	public void setSelected1(String selected1) {
		this.selected1 = selected1;
	}

	public boolean isShowAdmin() {
		return showAdmin;
	}

	public void setShowAdmin(boolean showAdmin) {
		this.showAdmin = showAdmin;
	}

	public boolean isShowTeacher() {
		return showTeacher;
	}

	public void setShowTeacher(boolean showTeacher) {
		this.showTeacher = showTeacher;
	}

}
