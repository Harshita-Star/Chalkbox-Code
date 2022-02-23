package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.SchoolCalenderInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="teacherHomePage")
@ViewScoped
public class TeacherHomePageBean implements Serializable
{
	String username,type,thought,selectedClassSection="all";
	String status,template,birthdayWish,errorLabel,temp1,temp2,temp3,temp4,bdyPreview,name,imagepath,teacher;
	boolean showWishAll;
	EmployeeInfo info=new EmployeeInfo();
	EmployeeInfo selectStudent;
	ArrayList<EmployeeInfo> birthdayStudentList;
	ArrayList<StudentInfo> classMateList=new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	ArrayList<SelectItem>teacherList,teacherClassList;
	ArrayList<SchoolCalenderInfo> activityList;
	SchoolInfoList ls;
	StudentInfo thoughtInfo = new StudentInfo();
	String schid;

	public TeacherHomePageBean()
	{
		//username="anita439";//44132 piy x-a //44688 con yes chlkbx-a //42552 shemford
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");
        schid = obj.schoolId();
		info=obj.teacherInfoByUserName(username, conn);
		teacherClassList=obj.teacherClassList(info.getId(),schid,conn);

		ls=obj.fullSchoolInfo(conn);
		name=ls.getSchoolName();
		imagepath=ls.getDownloadpath()+ls.getImagePath();

		thoughtInfo = DBJ.viewStudentThought(schid,conn);
		if(thoughtInfo.getThought()==null || thoughtInfo.getThought().equals(""))
		{
			thought="";
		}
		else
		{
			thought=thoughtInfo.getThought()+"<br/><br/>Submitted By : "+thoughtInfo.getFullName()+"<br/>Class : "+thoughtInfo.getClassName()+"-"+thoughtInfo.getSectionName();
		}

		if(ss.getAttribute("stdList")==null)
		{
			 // System.out.println("hiiii");
			classmateList();
		}
		else
		{
			classMateList=(ArrayList<StudentInfo>) ss.getAttribute("stdList");
		}

		allTeacher();
		eventList();
		birthday();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void eventList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		activityList = DBJ.viewEventListCurrentMonth(schid, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTeacher()
	{
		Connection conn=DataBaseConnection.javaConnection();
		teacherList=obj.allTeacherOnlyList(schid,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void classmateList()
	{
		classMateList=new ArrayList<>();
		Connection conn=DataBaseConnection.javaConnection();

		if(selectedClassSection.equals("all"))
		{
			int count=0;
			for(SelectItem ll:teacherClassList)
			{
				String arr[]=String.valueOf(ll.getValue()).split("-");
				classMateList.addAll(obj.searchStudentListByClassSectionSmall(arr[0],arr[1], conn));
			}
			for(int i=0;i<classMateList.size();i++)
			{
				classMateList.get(i).setSno(++count);
			}

		}
		else
		{
			String arr[]=selectedClassSection.split("-");
			classMateList=obj.searchStudentListByClassSectionSmall(arr[0],arr[1], conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void birthday()
	{
		Connection conn = DataBaseConnection.javaConnection();
		Date d=new Date();
		int bDate=d.getDate();
		int bMonth=d.getMonth()+1;

		birthdayStudentList=obj.allBirthdayListOfTeacher(bDate,bMonth,conn);


		if(birthdayStudentList.isEmpty())
		{
			showWishAll=false;
		}
		else
		{
			showWishAll=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void viewDetails() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");

		EmployeeInfo selectedStudent=obj.teacherInfoByUserName(username,conn);
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

		ss.setAttribute("selectedEmployee", selectedStudent);

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("viewEmployeeDetails.xhtml");
	}

	public void allStudentMsz()
	{
		status="all";
		template=birthdayWish="";
		if(!birthdayStudentList.isEmpty())
		{
			PrimeFaces.current().executeInitScript("PF('birthdayDialog').show()");
			PrimeFaces.current().ajax().update(":birthdayForm");
		}
		else
		{
			errorLabel="No Teachers to Wish.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
	}

	public void selectStudentMsz()
	{
		status="individual";
		template=birthdayWish="";
	}

	public void wishPreview()
	{
		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Teacher, "+template+" Regards,"+ls.getSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Teacher, "+birthdayWish+" Regards,"+ls.getSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Teacher, "+birthdayWish+" Regards,"+ls.getSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}

	}

	public void sendWish() throws IOException
	{
		PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevForm");

		String message="",contactNumber="",addNumber="";

		Connection conn=DataBaseConnection.javaConnection();
		if(status.equalsIgnoreCase("all"))
		{
			for(EmployeeInfo info:birthdayStudentList)
			{
				if(String.valueOf(info.getMobile()).length()==10)
				{
					if(contactNumber.equals(""))
					{
						contactNumber=String.valueOf(info.getMobile());
						addNumber=info.getEmplyeeuserid();
					}
					else
					{
						contactNumber=contactNumber+","+String.valueOf(info.getMobile());
						addNumber=addNumber+","+info.getEmplyeeuserid();
					}

				}
			}
		}
		else
		{
			contactNumber=String.valueOf(selectStudent.getMobile());
			addNumber=selectStudent.getEmplyeeuserid();

		}

		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Teacher,\n"+template+"\nRegards,\n"+ls.getSchoolName();
			obj.messageurl1(contactNumber,message,addNumber,conn,schid,"");

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Teacher,\n"+birthdayWish+"\nRegards,\n"+ls.getSchoolName();
			obj.messageurl1(contactNumber,message,addNumber,conn,schid,"");

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Teacher,\n"+birthdayWish+"\n Regards,\n"+ls.getSchoolName();
			obj.messageurl1(contactNumber,message,addNumber,conn,schid,"");

			template=birthdayWish="";
			errorLabel="Message Sent Successfully.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("Dashboard.xhtml");
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetailReport() 
	{

		HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("stdList",classMateList);
		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		
		try {
			cc.redirect("viewClassStudent.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	

	public EmployeeInfo getInfo() {
		return info;
	}
	public void setInfo(EmployeeInfo info) {
		this.info = info;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getBirthdayWish() {
		return birthdayWish;
	}

	public void setBirthdayWish(String birthdayWish) {
		this.birthdayWish = birthdayWish;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public String getTemp1() {
		return temp1;
	}

	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}

	public String getTemp2() {
		return temp2;
	}

	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}

	public String getTemp3() {
		return temp3;
	}

	public void setTemp3(String temp3) {
		this.temp3 = temp3;
	}

	public String getTemp4() {
		return temp4;
	}

	public void setTemp4(String temp4) {
		this.temp4 = temp4;
	}

	public String getBdyPreview() {
		return bdyPreview;
	}

	public void setBdyPreview(String bdyPreview) {
		this.bdyPreview = bdyPreview;
	}

	public boolean isShowWishAll() {
		return showWishAll;
	}

	public void setShowWishAll(boolean showWishAll) {
		this.showWishAll = showWishAll;
	}

	public EmployeeInfo getSelectStudent() {
		return selectStudent;
	}

	public void setSelectStudent(EmployeeInfo selectStudent) {
		this.selectStudent = selectStudent;
	}

	public ArrayList<EmployeeInfo> getBirthdayStudentList() {
		return birthdayStudentList;
	}

	public void setBirthdayStudentList(ArrayList<EmployeeInfo> birthdayStudentList) {
		this.birthdayStudentList = birthdayStudentList;
	}

	public ArrayList<StudentInfo> getClassMateList() {
		return classMateList;
	}

	public void setClassMateList(ArrayList<StudentInfo> classMateList) {
		this.classMateList = classMateList;
	}

	public ArrayList<SelectItem> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<SelectItem> teacherList) {
		this.teacherList = teacherList;
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

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getThought() {
		return thought;
	}

	public void setThought(String thought) {
		this.thought = thought;
	}

	public ArrayList<SchoolCalenderInfo> getActivityList() {
		return activityList;
	}

	public void setActivityList(ArrayList<SchoolCalenderInfo> activityList) {
		this.activityList = activityList;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}

	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}

	public ArrayList<SelectItem> getTeacherClassList() {
		return teacherClassList;
	}

	public void setTeacherClassList(ArrayList<SelectItem> teacherClassList) {
		this.teacherClassList = teacherClassList;
	}

	public StudentInfo getThoughtInfo() {
		return thoughtInfo;
	}

	public void setThoughtInfo(StudentInfo thoughtInfo) {
		this.thoughtInfo = thoughtInfo;
	}


}
