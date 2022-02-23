package student_module;

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
import Json.GeneralConsentInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="postConsent")
@ViewScoped

public class PostConsentBean implements Serializable
{
	ArrayList<SelectItem> classSection,sectionList;
	String selectedCLassSection,selectedSection,username,schoolid,type,des,assignmentPhotoPath1="",session;
	String selectedClass,section;
	Date assShowDate;
	transient
	UploadedFile assignmentPhoto1;
	ArrayList<GeneralConsentInfo> list = new ArrayList<>();
	ArrayList<StudentInfo> sList = new ArrayList<>();
	GeneralConsentInfo selected = new GeneralConsentInfo();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseOnlineAdm dbo = new DataBaseOnlineAdm();
    DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public PostConsentBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			username=(String) ss.getAttribute("username");
			schoolid=(String) ss.getAttribute("schoolid");
			session = obj.selectedSessionDetails(schoolid,conn);
			type=(String) ss.getAttribute("type");
			if(!type.equalsIgnoreCase("Teacher"))
			{
				classSection = new ArrayList<SelectItem>();
				ArrayList<SelectItem> tempClassSection = new ArrayList<SelectItem>();
				//classList=obj.allClass(conn);
				SelectItem si = new SelectItem();
				si.setLabel("ALL");
				si.setValue("ALL");
				classSection.add(si);
				
				tempClassSection=obj.allClass(conn);
				classSection.addAll(tempClassSection);
				
			}
			else
			{
				String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
				classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);

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
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");

		if(!type.equalsIgnoreCase("Teacher"))
		{
			sectionList = new ArrayList<SelectItem>();
			ArrayList<SelectItem> tempSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("ALL");
			si.setValue("ALL");
			sectionList.add(si);
			tempSection=obj.allSection(selectedCLassSection,conn);
			sectionList.addAll(tempSection);
			
		}
		else
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
			//classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);

		}
		
		if(selectedCLassSection.equalsIgnoreCase("ALL"))
		{
			selectedSection="ALL";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSectionRep()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");

		if(!type.equalsIgnoreCase("Teacher"))
		{
			
			sectionList = new ArrayList<SelectItem>();
			ArrayList<SelectItem> tempSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("ALL");
			si.setValue("ALL");
			sectionList.add(si);
			tempSection=obj.allSection(selectedClass,conn);
			sectionList.addAll(tempSection);
		}
		else
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
			//classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String upload()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			assignmentPhotoPath1="";
			Date pDate=new Date();
			String dt=new SimpleDateFormat("yMdhms").format(pDate);
			if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(pDate);
				assignmentPhotoPath1 = assignmentPhoto1.getFileName();
				String exten[]=assignmentPhotoPath1.split("\\.");
				assignmentPhotoPath1=username+"_"+dt+"_1_"+selectedSection+"."+exten[exten.length-1];
				obj.makeProfileSchid(schoolid,assignmentPhoto1,assignmentPhotoPath1,conn);
			}

			/*HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String staff=(String) ses.getAttribute("username");
*/
			int i=dbo.addConsent(selectedCLassSection, selectedSection, des, assShowDate, assignmentPhotoPath1, schoolid,
					username, type,conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Consent Posted Successfully"));
				obj.notification(schoolid,"Consent","A Consent is added for you, please check in consent module and respond before the last date.",selectedSection+"-"+selectedCLassSection+"-"+schoolid,conn);
				return "postConsent.xhtml";

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
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dte = formatter.format(assShowDate);
		
		language = "Class Id-"+selectedCLassSection+" --Section id-"+selectedSection+" --date-"+dte;
		value = "Description-"+des+" --File-"+assignmentPhotoPath1;
		
		String refNo = workLg.saveWorkLogMehod(language,"Post Consent","WEB",value,conn);
		return refNo;
	}
	
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		list = dbj.consentListClassSectionWise(selectedClass,section,obj.schoolId(),conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sList = dbj.searchStudentListForConsent(selected.getClassId(), selected.getSectionId(), selected.getId(), schoolid, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DataBaseMeathodJson().fullSchoolInfo(schoolid,conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getFile()+"')");

		try {
			conn.close();
		} catch (Exception e) {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
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

	public UploadedFile getAssignmentPhoto1() {
		return assignmentPhoto1;
	}

	public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
		this.assignmentPhoto1 = assignmentPhoto1;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public ArrayList<GeneralConsentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<GeneralConsentInfo> list) {
		this.list = list;
	}

	public ArrayList<StudentInfo> getsList() {
		return sList;
	}

	public void setsList(ArrayList<StudentInfo> sList) {
		this.sList = sList;
	}

	public GeneralConsentInfo getSelected() {
		return selected;
	}

	public void setSelected(GeneralConsentInfo selected) {
		this.selected = selected;
	}
	
}
