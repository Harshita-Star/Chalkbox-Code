package schooldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
@ViewScoped
@ManagedBean(name="viewEditSyllabus")
public class ViewEditSyllabus implements Serializable
{

	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList,termList,subjList;
	String selectedClassSection,selectedSection,id,assignmentPhotoPath1="",selectedSubject,syllabusType,selectedTerm,assignmentPhoto="",
			assignmentPhotoPath2="",assignmentPhotoPath3="",assignmentPhotoPath4="",assignmentPhotoPath5="",assignmentName, staff, subject, type, label, des;
	Date assShowDate=new Date();
	Date pDate;
	boolean show=false,showTable=false,status=false;
	SubjectInfo selectedSubjectSyllabus;
	transient
	UploadedFile assignmentPhoto1,assignmentPhoto2,assignmentPhoto3,assignmentPhoto4,assignmentPhoto5;
	ArrayList<SubjectInfo>subjectList;
	int j=0;
	StreamedContent filee;
	String schoolid, userType;
	DatabaseMethods1 obj = new DatabaseMethods1();
	public ViewEditSyllabus()
	{
		selectedSection="";
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		schoolid=(String) ses.getAttribute("schoolid");
		userType=(String) ses.getAttribute("type");
		try
		{
			classSection=new DatabaseMethods1().allClass(conn);
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
	public void allSection()
	{
		selectedSection="";
		Connection conn=DataBaseConnection.javaConnection();
		/*if(syllabusType.equalsIgnoreCase("Term Syllabus"))
		{
			termList=new DatabaseMethods1().selectedTermOfClass(selectedClassSection,conn);
			show=true;
			//subjectList=new DatabaseMethods1().subjectListOfParticularClass(selectedClassSection,conn);
			//showTable=true;
		}
		else
		{
			show=false;
			//subjectList=new DatabaseMethods1().subjectListOfParticularClass(selectedClassSection,conn);
			//showTable=true;
		}*/

		sectionList = new ArrayList<SelectItem>();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			sectionList.add(si);

			sectionList.addAll(obj.allSection(selectedClassSection,conn));
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClassSection, empid,conn);
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
		
		subjList = new ArrayList<SelectItem>();
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			subjList.add(si);
			
			subjList.addAll(new DatabaseMethods1().allSubjectClassWise(selectedClassSection,conn));
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(staff,schoolid,conn);
			subjList=objJson.AllEMployeeSubject(empid,selectedSection,schoolid,conn);
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
		subjectList=new DatabaseMethods1().allSubjectSyllabusForClass(selectedClassSection,syllabusType,selectedTerm,conn,selectedSection, selectedSubject);
		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void Filedownload1()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		try
		{
			String fl=selectedSubjectSyllabus.getAssignmentPhotoPath5();
			int first=fl.lastIndexOf(".")+1;
			int last=fl.length();
			String ext=fl.substring(first, last);
			//		        //// // System.out.println(ext);
			//		        //// // System.out.println(fl);
			java.io.InputStream stream = new FileInputStream(new File(info.getUploadpath()+fl));


			if(ext.equalsIgnoreCase("docx"))
			{
//				filee = new DefaultStreamedContent(stream, "file/docx", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/docx").name(info.getDownloadpath()+fl).stream(()->stream).build();
			}
			else if(ext.equalsIgnoreCase("doc"))
			{
//				filee = new DefaultStreamedContent(stream, "file/doc", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/doc").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("txt"))
			{
//				filee = new DefaultStreamedContent(stream, "file/txt", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/txt").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("pdf"))
			{
//				filee = new DefaultStreamedContent(stream, "file/pdf", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/pdf").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("cdr"))
			{
//				filee = new DefaultStreamedContent(stream, "file/cdr", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/cdr").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("png"))
			{
//				filee = new DefaultStreamedContent(stream, "file/png", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/png").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("jpg"))
			{
//				filee = new DefaultStreamedContent(stream, "file/jpg", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/jpg").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("jpeg"))
			{
//				filee = new DefaultStreamedContent(stream, "file/jpeg", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/jpeg").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("gif"))
			{
//				filee = new DefaultStreamedContent(stream, "file/gif", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/gif").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("xls"))
			{
//				filee = new DefaultStreamedContent(stream, "file/xls", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/xls").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			else if(ext.equalsIgnoreCase("xlsx"))
			{
//				filee = new DefaultStreamedContent(stream, "file/xlsx", info.getDownloadpath()+fl);
				filee = new DefaultStreamedContent().builder().contentType("file/xlsx").name(info.getDownloadpath()+fl).stream(()->stream).build();

			}
			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('dwnlad').show();");

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File Not Found"));
			e.printStackTrace();
		}



	}
	public void deleteSyllabus()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=obj.deleteSyllabus(selectedSubjectSyllabus.getId(),conn);
		if(i>=1)
		{
			String refNo;
	          try {
	        	  refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			subjectList=obj.allSubjectSyllabusForClass(selectedClassSection,syllabusType,selectedTerm,conn,selectedSection, selectedSubject);
			fc.addMessage(null, new FacesMessage("Selected Syllabus Deleted Successfully"));
			//selectedCLassSection="";

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
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value = "Id-"+selectedSubjectSyllabus.getId(); 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Syllabus","WEB",value,conn);
		return refNo;
	}

	public void editSyllabusDetails()
	{
		id=selectedSubjectSyllabus.getId();
		assignmentPhoto=selectedSubjectSyllabus.getAssignmentPhotoPath5();
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
	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getSelectedSubject() {
		return selectedSubject;
	}
	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}
	public String getSyllabusType() {
		return syllabusType;
	}
	public void setSyllabusType(String syllabusType) {
		this.syllabusType = syllabusType;
	}
	public String getSelectedTerm() {
		return selectedTerm;
	}
	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}
	public String getAssignmentPhotoPath5() {
		return assignmentPhotoPath5;
	}
	public void setAssignmentPhotoPath5(String assignmentPhotoPath5) {
		this.assignmentPhotoPath5 = assignmentPhotoPath5;
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
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public UploadedFile getAssignmentPhoto5() {
		return assignmentPhoto5;
	}
	public void setAssignmentPhoto5(UploadedFile assignmentPhoto5) {
		this.assignmentPhoto5 = assignmentPhoto5;
	}
	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public SubjectInfo getSelectedSubjectSyllabus() {
		return selectedSubjectSyllabus;
	}
	public void setSelectedSubjectSyllabus(SubjectInfo selectedSubjectSyllabus) {
		this.selectedSubjectSyllabus = selectedSubjectSyllabus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public StreamedContent getFilee() {
		return filee;
	}
	public void setFilee(StreamedContent filee) {
		this.filee = filee;
	}
	public ArrayList<SelectItem> getSubjList() {
		return subjList;
	}
	public void setSubjList(ArrayList<SelectItem> subjList) {
		this.subjList = subjList;
	}



}
