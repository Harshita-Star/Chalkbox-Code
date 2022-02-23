package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="printAdmitCard")
@ViewScoped
public class PrintAdmitCardBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> examList,classList,sectionList;
	String selectedExam,examName,type,examLabel,selectedClass,selectedSection;
	boolean showTable,showPrint;
	boolean showAcademic,showSports,manualType,photoType,renderInst;
	ArrayList<AdmitCardInfo> examInfo;
	ArrayList<StudentInfo> studentList=new ArrayList<>(),selectedList;
	String selectedImage="",userType,username,schoolid,schoolName,instructions;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public PrintAdmitCardBean()
	{
		type="academics";
		examLabel="Exam";
		showSports=false;
		showAcademic=true;
		
		Connection conn=DataBaseConnection.javaConnection();
		//examList=new DatabaseMethods1().allExamList(conn,type);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		if(!userType.equalsIgnoreCase("Teacher"))
		{
		 classList=new DatabaseMethods1().allClass(conn);
		}
		else
		{
			String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(!userType.equalsIgnoreCase("Teacher"))
		{
		  sectionList=DBM.allSection(selectedClass,conn);
		}
		else
		{	
		  String empid=dbj.employeeIdbyEmployeeName(username,schoolid,conn);
		  sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		examList = new ArrayList<>();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkType()
	{
		selectedExam="";
		//selectedClass="";
		selectedSection="";
		//Connection conn=DataBaseConnection.javaConnection();
		//examList=new DatabaseMethods1().allExamList(conn,type);
		
		examList=new ArrayList<>();
		//sectionList=new ArrayList<>();
		
		showPrint=false;
		if(type.equalsIgnoreCase("academics"))
		{
			examLabel="Exam";
			showSports=false;
			showAcademic=true;
		}
		else
		{
			examLabel="Event";
			showSports=true;
			showAcademic=false;
		}
/*
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
	
	public void allExams()
	{
		selectedExam="";
		Connection conn=DataBaseConnection.javaConnection();
		examList=new DatabaseMethods1().allExamList(conn,type,selectedClass,selectedSection);
		
		showPrint=false;
		if(type.equalsIgnoreCase("academics"))
		{
			examLabel="Exam";
			showSports=false;
			showAcademic=true;
		}
		else
		{
			examLabel="Event";
			showSports=true;
			showAcademic=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchExam()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		studentList=new ArrayList<>();
		examInfo=obj.examInfoByGroupId(selectedExam,conn);
		examName=examInfo.get(0).getExamName();
		//ArrayList<AdmitCardInfo> list = obj.admitCardInfoForStudent(obj.schoolId(),selectedExam,selectedClass,selectedSection,conn);
		/*for(AdmitCardInfo ll:examInfo)
		{
			studentList.addAll(obj.searchStudentListByClassSection(ll.getSectionId(),conn));
		}*/
		studentList=obj.searchStudentListByClassSection(selectedClass,selectedSection,conn);
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		schoolName = ls.getSchoolName();
		if(studentList.size()>0)
		{
			
			String admitCrdPhotoDetails = obj.findAdmitCardDetails(obj.schoolId(),selectedExam,selectedClass,selectedSection,conn);
			if(admitCrdPhotoDetails.equalsIgnoreCase("no"))
			{
				photoType = false;
				manualType = true;
				
			}
			else
			{
				

				String folderName = ls.getDownloadpath();
				
				photoType = true;
				manualType = false;
				selectedImage = folderName+admitCrdPhotoDetails;
				
			}
			
			int count=1;
			for(StudentInfo ll:studentList)
			{
				ll.setSno(count++);
				ll.setAdmitCardInfo(obj.admitCardSubjectInfoForStudent(ll.getAddNumber(),obj.schoolId(),selectedExam,selectedClass,selectedSection,conn));
			    
				for(AdmitCardInfo adf : ll.getAdmitCardInfo())
				{
					ll.setInstructions(adf.getInstructions());
					if(!ll.getInstructions().trim().equalsIgnoreCase(""))
					{
						renderInst = true;
					}
				}
			}
			showTable=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student In Class"));
			showTable=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printAdmitCard()
	{
		if(selectedList.size()>0)
		{
			showTable=false;showPrint=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select Atleast One Student"));
			showTable=true;showPrint=false;
		}
	}

	public ArrayList<SelectItem> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}
	public String getSelectedExam() {
		return selectedExam;
	}
	public void setSelectedExam(String selectedExam) {
		this.selectedExam = selectedExam;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
	}
	public boolean isShowPrint() {
		return showPrint;
	}
	public void setShowPrint(boolean showPrint) {
		this.showPrint = showPrint;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExamLabel() {
		return examLabel;
	}

	public void setExamLabel(String examLabel) {
		this.examLabel = examLabel;
	}

	public boolean isShowAcademic() {
		return showAcademic;
	}

	public void setShowAcademic(boolean showAcademic) {
		this.showAcademic = showAcademic;
	}

	public boolean isShowSports() {
		return showSports;
	}

	public void setShowSports(boolean showSports) {
		this.showSports = showSports;
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

	public boolean isManualType() {
		return manualType;
	}

	public void setManualType(boolean manualType) {
		this.manualType = manualType;
	}

	public boolean isPhotoType() {
		return photoType;
	}

	public void setPhotoType(boolean photoType) {
		this.photoType = photoType;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public boolean isRenderInst() {
		return renderInst;
	}

	public void setRenderInst(boolean renderInst) {
		this.renderInst = renderInst;
	}
	
	
	
}
