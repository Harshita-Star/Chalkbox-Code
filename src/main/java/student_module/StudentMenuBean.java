package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="studentMenuBean")
@ViewScoped

public class StudentMenuBean implements Serializable
{
	String username,type,name,imagepath;
	SchoolInfoList ls=new SchoolInfoList();
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<SelectItem> pageList,innerPageList;

	boolean manageAppointmentSetting,managePhotoRequest,managePhoneBook,manageAddCalendar,manageNews,manageSyllabus,manageHomeWorkNotes;
	boolean manageTestPerformance,manageMarkAttendence,manageAcademics,manageOnlineExam;

	public StudentMenuBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");

		//username="CB39022";

		ls=obj.fullSchoolInfo(conn);
		name=ls.getSchoolName();
		imagepath=ls.getDownloadpath()+ls.getImagePath();

		innerPageList=obj.allInnerPageList("admin",conn);
		innerPermissionSetting(conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void onlineExam() throws IOException 
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		String pwd = (String) ss.getAttribute("pwd");
		String txt = new DatabaseMethods1().xorMessage(pwd, new DatabaseMethods1().pwdkey);
		String password = DatabaseMethods1.base64encode(txt);
		String url = "https://exam.chalkbox.in/Home/userlogin?name="+username+"&password="+pwd;
		PrimeFaces.current().executeInitScript("window.open('"+url+"')");
	}

	public void innerPermissionSetting(Connection conn)
	{
		String blockMods = obj.blockedStudentAppMods(obj.schoolId(), username, "modules", conn);
		if(blockMods.equals(""))
		{
			manageAcademics = true;
		}
		else
		{
			manageAcademics = false;
		}
		for(SelectItem ss:innerPageList)
		{
			if(ss.getLabel().equalsIgnoreCase("Appointment Setting"))
			{
				manageAppointmentSetting=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Student Photo Requests"))
			{
				managePhotoRequest=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Directory"))
			{
				managePhoneBook=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Mark Attendance"))
			{
				manageMarkAttendence=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Test Performance"))
			{
				manageTestPerformance=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Home Work / Notes"))
			{
				manageHomeWorkNotes=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Online Exam"))
			{
				if(blockMods.equals(""))
				{
					manageOnlineExam=true;
				}
				else
				{
					manageOnlineExam=false;
				}
			}
			if(ss.getLabel().equalsIgnoreCase("Syllabus"))
			{
				manageSyllabus=true;
			}
			if(ss.getLabel().equalsIgnoreCase("News"))
			{
				manageNews=true;
			}
			if(ss.getLabel().equalsIgnoreCase("Add Calendar"))
			{
				manageAddCalendar=true;
			}
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public ArrayList<SelectItem> getPageList() {
		return pageList;
	}

	public void setPageList(ArrayList<SelectItem> pageList) {
		this.pageList = pageList;
	}

	public ArrayList<SelectItem> getInnerPageList() {
		return innerPageList;
	}

	public void setInnerPageList(ArrayList<SelectItem> innerPageList) {
		this.innerPageList = innerPageList;
	}

	public boolean isManageAppointmentSetting() {
		return manageAppointmentSetting;
	}

	public void setManageAppointmentSetting(boolean manageAppointmentSetting) {
		this.manageAppointmentSetting = manageAppointmentSetting;
	}

	public boolean isManagePhotoRequest() {
		return managePhotoRequest;
	}

	public void setManagePhotoRequest(boolean managePhotoRequest) {
		this.managePhotoRequest = managePhotoRequest;
	}

	public boolean isManagePhoneBook() {
		return managePhoneBook;
	}

	public void setManagePhoneBook(boolean managePhoneBook) {
		this.managePhoneBook = managePhoneBook;
	}

	public boolean isManageAddCalendar() {
		return manageAddCalendar;
	}

	public void setManageAddCalendar(boolean manageAddCalendar) {
		this.manageAddCalendar = manageAddCalendar;
	}

	public boolean isManageNews() {
		return manageNews;
	}

	public void setManageNews(boolean manageNews) {
		this.manageNews = manageNews;
	}

	public boolean isManageSyllabus() {
		return manageSyllabus;
	}

	public void setManageSyllabus(boolean manageSyllabus) {
		this.manageSyllabus = manageSyllabus;
	}

	public boolean isManageHomeWorkNotes() {
		return manageHomeWorkNotes;
	}

	public void setManageHomeWorkNotes(boolean manageHomeWorkNotes) {
		this.manageHomeWorkNotes = manageHomeWorkNotes;
	}

	public boolean isManageTestPerformance() {
		return manageTestPerformance;
	}

	public void setManageTestPerformance(boolean manageTestPerformance) {
		this.manageTestPerformance = manageTestPerformance;
	}

	public boolean isManageMarkAttendence() {
		return manageMarkAttendence;
	}

	public void setManageMarkAttendence(boolean manageMarkAttendence) {
		this.manageMarkAttendence = manageMarkAttendence;
	}

	public boolean isManageAcademics() {
		return manageAcademics;
	}

	public void setManageAcademics(boolean manageAcademics) {
		this.manageAcademics = manageAcademics;
	}

	public boolean isManageOnlineExam() {
		return manageOnlineExam;
	}

	public void setManageOnlineExam(boolean manageOnlineExam) {
		this.manageOnlineExam = manageOnlineExam;
	}
}
