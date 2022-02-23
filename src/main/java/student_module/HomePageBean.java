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
import schooldata.SchoolCalenderInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="homePage")
@ViewScoped
public class HomePageBean implements Serializable
{
	String username,type,thought,session,schid;
	String status,template,birthdayWish,errorLabel,temp1,temp2,temp3,temp4,bdyPreview,name,imagepath,teacher;
	boolean showWishAll;
	StudentInfo info=new StudentInfo(),selectStudent;
	ArrayList<StudentInfo> birthdayStudentList,classMateList=new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	ArrayList<SelectItem>teacherList;
	ArrayList<SchoolCalenderInfo> activityList;
	SchoolInfoList ls;
	StudentInfo thoughtInfo = new StudentInfo();

	public HomePageBean()
	{
		//username="CB44132";//44132 piy x-a //44688 con yes chlkbx-a //42552 shemford
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//ss.setAttribute("username", username);
		schid = obj.schoolId();
		username=(String) ss.getAttribute("username");
		String pwd = (String) ss.getAttribute("pwd");
		type=(String) ss.getAttribute("type");
		info=obj.studentDetailslistByAddNo(schid,username, conn);
		ls=obj.fullSchoolInfo(conn);
		name=ls.getSchoolName();
		imagepath=ls.getDownloadpath()+ls.getImagePath();
		ArrayList<StudentInfo> tempList=new ArrayList<>();
		tempList=obj.searchStudentListByClassSectionSmall(info.getClassId(), info.getSectionid(), conn);
		session = obj.selectedSessionDetails(ls.getSchid(),conn);
		for(StudentInfo student:tempList)
		{
			if(!student.getAddNumber().equals(info.getAddNumber()))
			{
				classMateList.add(student);
			}
		}

		////// // System.out.println(classMateList.size());

		teacher="subject";

		thoughtInfo = DBJ.viewStudentThought(obj.schoolId(),conn);
		if(thoughtInfo.getThought()==null || thoughtInfo.getThought().equals(""))
		{
			thought="";
		}
		else
		{
			thought=thoughtInfo.getThought()+"<br/><br/>Submitted By : "+thoughtInfo.getFullName()+"<br/>Class : "+thoughtInfo.getClassName()+"-"+thoughtInfo.getSectionName();
			////// // System.out.println(thought);
			//thought="Static thought Winning doesn't come cheaply, you have to pay a big prize.......... submitted by: Hema Bisht Parent of (Ronit Bisht Class: UKG-A)";

		}

//		MediaType mediaType = MediaType.parse("appliaction/json");
//		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//		  .addFormDataPart("mobile", username)
//		  .addFormDataPart("otp", pwd)
//		  //.setType(mediaType)
//		  .build();
//		Request request = new Request.Builder()
//		  .url("https://exam.chalkbox.in/Home/userlogin")
//		  .method("POST", body)
//		  .build();
//		
//		allTeacher();
//		eventList();
//		birthday();
//		
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		ec.setRequest(request);
//		try {
//			ec.redirect("https://exam.chalkbox.in/Home/userlogin");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}


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
		if(teacher.equalsIgnoreCase("subject"))
		{
			teacherList=DBJ.AllSubjectTeachers(info.getSectionid(),schid,conn);
		}
		else
		{
			String categ = obj.employeeCategoryIdByName("Teacher", conn);
			teacherList=DBJ.AllTeacherList(schid,conn,categ,info.getSectionid());
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

		birthdayStudentList=obj.allBirthdayListOfSection(bDate,bMonth,info.getSectionid(),conn);


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
		SchoolInfoList ls = obj.fullSchoolInfo(conn);
		StudentInfo selectedStudent=obj.studentDetailslistByAddNo(schid,username,conn);
		try {
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

		ss.setAttribute("selectedStudent", selectedStudent);
		if(ls.getCountry().equalsIgnoreCase("UAE"))
		{
			ss.setAttribute("enq_id", selectedStudent.getEnqUAEId());
			ss.setAttribute("heading", "Student Details");
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("studentProfileAe.xhtml");
		}
		else
		{

			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
			cc.redirect("studentProfile.xhtml");
		}
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
			errorLabel="No Students to Wish.";
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
			bdyPreview="Dear Student, "+template+" Regards,"+ls.getSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student, "+birthdayWish+" Regards,"+ls.getSchoolName();
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if(!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview="Dear Student, "+birthdayWish+" Regards,"+ls.getSchoolName();
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
			for(StudentInfo info:birthdayStudentList)
			{
				if(String.valueOf(info.getFathersPhone()).length()==10)
				{
					if(contactNumber.equals(""))
					{
						contactNumber=String.valueOf(info.getFathersPhone());
						addNumber=info.getAddNumber();
					}
					else
					{
						contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
						addNumber=addNumber+","+info.getAddNumber();
					}

				}
			}
		}
		else
		{
			contactNumber=String.valueOf(selectStudent.getFathersPhone());
			addNumber=selectStudent.getAddNumber();

		}

		if(template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel="Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if(!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			message="Dear Student,\n"+template+"\nRegards,\n"+ls.getSchoolName();
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
			message="Dear Student,\n"+birthdayWish+"\nRegards,\n"+ls.getSchoolName();
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
			message="Dear Student,\n"+birthdayWish+"\n Regards,\n"+ls.getSchoolName();
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


	public StudentInfo getInfo() {
		return info;
	}
	public void setInfo(StudentInfo info) {
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

	public StudentInfo getSelectStudent() {
		return selectStudent;
	}

	public void setSelectStudent(StudentInfo selectStudent) {
		this.selectStudent = selectStudent;
	}

	public ArrayList<StudentInfo> getBirthdayStudentList() {
		return birthdayStudentList;
	}

	public void setBirthdayStudentList(ArrayList<StudentInfo> birthdayStudentList) {
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}


}
