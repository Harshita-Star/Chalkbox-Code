package lecture_plan;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="fillLecPlan")
@ViewScoped
public class FillLecturePlanBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	Date startDate,maxDate;
	String type,username,subject,schid,session,selectedClass,selectedSection,empid,lectureNo,lessonNo,lessonName,description,filePath="NA";
	ArrayList<SelectItem> classList,sectionList,subjectList,unitList,lectureList,lessonNoList,lessonNameList;
	static UploadedFile imageFile;
	StreamedContent file;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	exam_module.DataBaseMethodsExam objExam=new exam_module.DataBaseMethodsExam();
	DBMethodsLecturePlan objLecture=new DBMethodsLecturePlan();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public FillLecturePlanBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		startDate=new Date();
		maxDate=new Date();
		subjectList=new ArrayList<>();
		
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		
		session=obj.selectedSessionDetails(schid,conn);
		
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office")
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
			if(type.equalsIgnoreCase("admin"))
			{
				empid=username;
			}
			else
			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			}
		}
		else if(type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schid,conn);
		}
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allSection()
	{
		Connection conn= DataBaseConnection.javaConnection();
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office")
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allSubjects()
	{
		Connection conn= DataBaseConnection.javaConnection();
		
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") 
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			subjectList=obj.selectedSubjectTypeofClassSection(selectedClass,"NA",conn);
		}
		else
		{
			subjectList=objExam.AllEMployeeSubjectByType(empid,selectedSection,schid,"NA",conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void searchData()
	{
		Connection con=DataBaseConnection.javaConnection();
		
		lectureList=objLecture.singleFieldListFromLecturePlan("lecture_no",selectedClass, selectedSection, subject, schid, session, con);
		if(lectureList.size()==0)
    	{
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Intial Lesson Plan First For Subject"));
    	}
		lessonName=lessonNo=description="";
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkSubDes()
	{
		Connection con= DataBaseConnection.javaConnection();
		LecturePlanInfo info=objLecture.dailyLectureDetailOfSubjectByLectureNo(empid, selectedClass, selectedSection, lectureNo, subject, session, schid, con);
		if(info!=null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Daily Lesson Plan For This Lecture Is Already Submitted.. Please Go To Edit Section For Any Changes"));
			lectureNo="";
		}
		else
		{
			lessonNoList=objLecture.singleFieldListFromLecturePlan("unit_no",selectedClass, selectedSection, subject, schid, session, con);
			lessonNameList=objLecture.singleFieldListFromLecturePlan("unit_name",selectedClass, selectedSection, subject, schid, session, con);
			info=objLecture.lecturePlanDetailOfSubjectByLectureNo(empid,selectedClass, selectedSection, lectureNo,subject,session,schid,con);
			lessonName=info.getUnitName();
			lessonNo=info.getUnitNo();
			description=info.getDescription();
		}
		
    	try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void submitLecturePlan()
	{
		Connection con= DataBaseConnection.javaConnection();
		
		if(imageFile!=null && imageFile.getSize()>0)
		{
			String dt = new SimpleDateFormat("yMdhms").format(startDate);
			int rendomNumber = (int) (Math.random() * 9000) + 1000;
			String exten[] = imageFile.getFileName().split("\\.");
			String className=obj.classNameFromidSchid(schid, selectedClass, session, con);
			String sectionName=obj.sectionNameByIdSchid(schid, selectedSection, con);
			String subjectName=obj.subjectNameFromid(subject, con);
			filePath = subjectName+"_"+className+"_"+sectionName+"_"+lectureNo+"_"+dt+"_"+rendomNumber+"_"+ "." + exten[exten.length-1];
			obj.makeProfileSchid(schid, imageFile, filePath, con);
		}
		else
		{
			filePath="NA";
		}
		int i=objLecture.addDailyLecturePlan(empid,startDate,selectedClass, selectedSection, lectureNo,subject,lessonName,lessonNo,description,session,schid,con,filePath);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(con,filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Submitted Successfully!"));
			startDate=new Date();
			selectedClass=selectedSection=lectureNo=lessonName=lessonNo=description=subject="";
			lectureList=lessonNameList=lessonNoList=sectionList=subjectList=new ArrayList<SelectItem>();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Fill Data Correctly!"));
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn,String filePath)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String st = formater.format(startDate); 
		
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject+" --Date-"+st;
		value = "Lecture No-"+lectureNo+" --Lession No-"+lessonNo+" --Lession Name"+lessonName+" --Description-"+description+" --Filepath-"+filePath;
		
		String refNo = workLg.saveWorkLogMehod(language,"Daily lession Plan Add","WEB",value,conn);
		return refNo;
	}

	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<SelectItem> getUnitList() {
		return unitList;
	}

	public void setUnitList(ArrayList<SelectItem> unitList) {
		this.unitList = unitList;
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

	public String getLectureNo() {
		return lectureNo;
	}

	public void setLectureNo(String lectureNo) {
		this.lectureNo = lectureNo;
	}

	public String getLessonNo() {
		return lessonNo;
	}

	public void setLessonNo(String lessonNo) {
		this.lessonNo = lessonNo;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getLectureList() {
		return lectureList;
	}

	public void setLectureList(ArrayList<SelectItem> lectureList) {
		this.lectureList = lectureList;
	}

	public ArrayList<SelectItem> getLessonNoList() {
		return lessonNoList;
	}

	public void setLessonNoList(ArrayList<SelectItem> lessonNoList) {
		this.lessonNoList = lessonNoList;
	}

	public ArrayList<SelectItem> getLessonNameList() {
		return lessonNameList;
	}

	public void setLessonNameList(ArrayList<SelectItem> lessonNameList) {
		this.lessonNameList = lessonNameList;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public UploadedFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(UploadedFile imageFile) {
		this.imageFile = imageFile;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
}
