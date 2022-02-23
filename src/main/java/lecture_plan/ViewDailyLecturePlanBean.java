package lecture_plan;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="viewDailyLecPlan")
@ViewScoped
public class ViewDailyLecturePlanBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	Date startDate,endDate;
	String type,username,subject,schid,session,selectedClass,selectedSection,empid,lectureNo,lessonNo,lessonName,description,filePath,oldFilePath="NA";
	ArrayList<SelectItem> classList,sectionList,subjectList,lessonNoList,lessonNameList;
	static UploadedFile imageFile;
	StreamedContent file;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	exam_module.DataBaseMethodsExam objExam=new exam_module.DataBaseMethodsExam();
	DBMethodsLecturePlan objLecture=new DBMethodsLecturePlan();
	ArrayList<LecturePlanInfo> planList=new ArrayList<LecturePlanInfo>(),selPlanList;
	LecturePlanInfo selLecture;
	boolean show;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public ViewDailyLecturePlanBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		startDate=new Date();
		endDate=new Date();
		subjectList=new ArrayList<>();
		
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);
		
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
		planList=objLecture.dailyLectureDetailOfSubject(startDate, endDate, empid, selectedClass, selectedSection, subject, session, schid, con);
		
		if(planList.size()==0)
    	{
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Records Found"));
    		show=false;
    	}
		else
		{
			show=true;
			
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editDetails()
	{
		Connection con=DataBaseConnection.javaConnection();
		oldFilePath="NA";
		lessonNoList=objLecture.singleFieldListFromLecturePlan("unit_no",selectedClass, selectedSection, subject, schid, session, con);
		lessonNameList=objLecture.singleFieldListFromLecturePlan("unit_name",selectedClass, selectedSection, subject, schid, session, con);
		lessonName=selLecture.getUnitName();
		lessonNo=selLecture.getUnitNo();
		description=selLecture.getDescription();
		oldFilePath=selLecture.getImagePath();
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteLecturePlan()
	{
		Connection con=DataBaseConnection.javaConnection();
		int i=0;
		if(selPlanList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One Item"));
		}
		else
		{
			for(LecturePlanInfo info:selPlanList)
			{
				i=objLecture.deleteDailyLecturePlanOfSubjectLecture(info.getId(),con);
			}
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(con);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Daily Lesson Plan Deleted Successfully"));
				startDate=endDate=new Date();
				selectedClass=selectedSection=lectureNo=lessonName=lessonNo=description=subject="";show=false;
				lessonNameList=lessonNoList=sectionList=subjectList=new ArrayList<SelectItem>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject;
		value = "Selected Id- ";
		
		for(LecturePlanInfo info:selPlanList)
		{
			value += "("+info.getId()+")";
		}
		
		
		String refNo =workLg.saveWorkLogMehod(language,"Delete Lecture Plan","WEB",value,conn);
		return refNo;
	}

	
	public void viewDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		oldFilePath=selLecture.getImagePath();
		int first=oldFilePath.lastIndexOf(".")+1;
		int last=oldFilePath.length();
		String ext=oldFilePath.substring(first, last);
		
		SchoolInfoList info=obj.fullSchoolInfo(schid, conn);
		String realPath=info.getDownloadpath();
		oldFilePath=realPath+oldFilePath;
		
		if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))
		{
			PrimeFaces.current().executeInitScript("PF('viewDialog').show();");
			PrimeFaces.current().ajax().update("form1");
		}
		else if(ext.equals("pdf"))
		{
			PrimeFaces.current().executeInitScript("PF('viewDialog1').show();");
			PrimeFaces.current().ajax().update("form2");
		}
		else
		{
			PrimeFaces.current().executeInitScript("PF('downloadDialog1').show();");
			PrimeFaces.current().ajax().update("form3");
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void backUpMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int first=oldFilePath.lastIndexOf(".")+1;
		int last=oldFilePath.length();
		String ext=oldFilePath.substring(first, last);
		
		String ff=oldFilePath;
		
		
		SchoolInfoList info=obj.fullSchoolInfo(schid, conn);
		/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");*/
		
		String realPath=info.getUploadpath();  //online
		try
		{
			InputStream stream = new FileInputStream(new File(realPath+ff));
//			file = new DefaultStreamedContent(stream, "file/"+ext, ff);
			file = new DefaultStreamedContent().builder().contentType("file/"+ext).name(ff).stream(()->stream).build();
			/*if(ext.equals("docx"))
			{
				file = new DefaultStreamedContent(stream, "file/docx", ff);
			}
			else if(ext.equals("doc"))
			{
				file = new DefaultStreamedContent(stream, "file/doc", ff);
			}
			else if(ext.equals("txt"))
			{
				file = new DefaultStreamedContent(stream, "file/txt", ff);
			}
			else if(ext.equals("xlsx"))
			{
				file = new DefaultStreamedContent(stream, "file/xlsx", ff);
			}
			else if(ext.equals("xls"))
			{
				file = new DefaultStreamedContent(stream, "file/xls", ff);
			}
			else if(ext.equals("mp4"))
			{
				file = new DefaultStreamedContent(stream, "file/mp4", ff);
			}
			else if(ext.equals("avi"))
			{
				file = new DefaultStreamedContent(stream, "file/avi", ff);
			}
			else if(ext.equals("flv"))
			{
				file = new DefaultStreamedContent(stream, "file/flv", ff);
			}
			*/
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void updateNow()
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
			filePath = subjectName+"_"+className+"_"+sectionName+"_"+selLecture.getLectureNo()+"_"+dt+"_"+rendomNumber+"_"+ "." + exten[exten.length-1];
			obj.makeProfileSchid(schid, imageFile, filePath, con);
		}
		else
		{
			filePath="NA";
		}
		int i=objLecture.updateDailyLecturePlan(lessonName,lessonNo,description,selLecture.getId(),con,filePath);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(con,filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully!"));
			startDate=endDate=new Date();
			selectedClass=selectedSection=lectureNo=lessonName=lessonNo=description=subject="";show=false;
			lessonNameList=lessonNoList=sectionList=subjectList=new ArrayList<SelectItem>();
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
	
	
	public String addWorkLog2(Connection conn,String filePath)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String st = formater.format(startDate); 
		
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject+" --Lession No-"+lessonNo+" --Lession Name"+lessonName+" --Description-"+description;
		value = " Filepath-"+filePath +" --Selected Id-"+selLecture.getId();
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Daily lession Plan","WEB",value,conn);
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

	public String getOldFilePath() {
		return oldFilePath;
	}

	public void setOldFilePath(String oldFilePath) {
		this.oldFilePath = oldFilePath;
	}
	
	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<LecturePlanInfo> getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList<LecturePlanInfo> planList) {
		this.planList = planList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<LecturePlanInfo> getSelPlanList() {
		return selPlanList;
	}

	public void setSelPlanList(ArrayList<LecturePlanInfo> selPlanList) {
		this.selPlanList = selPlanList;
	}

	public LecturePlanInfo getSelLecture() {
		return selLecture;
	}

	public void setSelLecture(LecturePlanInfo selLecture) {
		this.selLecture = selLecture;
	}
}
