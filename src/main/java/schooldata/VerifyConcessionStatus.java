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

import session_work.QueryConstants;
@ManagedBean(name="verifyConcessionStatus")
@ViewScoped
public class VerifyConcessionStatus implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>(),multipleStudentList=new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>(),concessionCategoryList=new ArrayList<>();
	String selectedClass,conceesionCategory,concessionStatus;
	String sectionName;
	String selectedSection;
	String className,section,total,session,schid;
	ArrayList<String> list=new ArrayList<>();
	boolean b;

	public VerifyConcessionStatus()
	{
		Connection conn=DataBaseConnection.javaConnection();
		concessionCategoryList=new DatabaseMethods1().allConnsessionType(conn);
		classList=new DatabaseMethods1().allClass(conn);
		schid=new DatabaseMethods1().schoolId();
		session=new DatabaseMethods1().selectedSessionDetails(schid, conn);
		list=new DataBaseMethodStudent().verifyConcessionFieldList();
		
		studentList=new DataBaseMethodStudent().studentDetail("", "-1","-1", QueryConstants.BY_CLASS_SECTION, QueryConstants.VERIFY_CONCESSION, null,null,"","","","", session, schid, list, conn);
		if (studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b = false;
		}
		else
		{
			b = true;
		}
		total = String.valueOf(studentList.size());
		className = "All";
		sectionName = "All";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void getStudentStrength()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DataBaseMethodStudent().studentDetail("", selectedSection,selectedClass, QueryConstants.BY_CLASS_SECTION, QueryConstants.VERIFY_CONCESSION, null,null,"","","","", session, schid, list, conn);
		if (studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b = false;
		}
		else
		{
			b = true;
		}
		total=String.valueOf(studentList.size());
		if (selectedSection.equals("-1"))
		{
			
			total = String.valueOf(studentList.size());
			className = "All";
			sectionName = "All";
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void accept()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		boolean updateDetail=false;
		for(StudentInfo tt : multipleStudentList)
		{
			concessionStatus="accepted";
			updateDetail=new DatabaseMethods1().updateConcessionDetailInRegistration(tt.getAddNumber(), session, tt.getNewConcessionAssign(),concessionStatus, conn);
		}
		if(multipleStudentList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Row !! "));
		}
		else
		{
			if(updateDetail==true)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Update Concession Detail Successfully "));
				selectedClass="";
				selectedSection="";
				studentList=new DataBaseMethodStudent().studentDetail("", "-1","-1", QueryConstants.BY_CLASS_SECTION, QueryConstants.VERIFY_CONCESSION,null,null,"","","","", session, schid, list, conn);
				if(studentList.isEmpty())
				{
					b=false;
				}
				else
				{
					b=true;
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occurred !!"));
			}
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class -"+className+" -- Section -"+sectionname;
		
		for(StudentInfo tt : multipleStudentList)
		{
		  value += "( Student -"+tt.getAddNumber()+" --- Concession status -accepted --- New cocession Assign- "+tt.getNewConcessionAssign()+")";	
		}
		
			
		
			
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Accept Concession Status","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class -"+className+" -- Section -"+sectionname;
		
		for(StudentInfo tt : multipleStudentList)
		{
		  value += "( Student -"+tt.getAddNumber()+" --- Concession status -accepted --- New cocession Assign - "+String.valueOf(concessionCategoryList.get(0).getValue())+")";	
		}
		
			
		
			
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Deny Concession Status","WEB",value,conn);
		return refNo;
	}
	
	
	public void deny()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		boolean updateDetail=false;
		for(StudentInfo tt : multipleStudentList)
		{
			concessionStatus="accepted";
			updateDetail=new DatabaseMethods1().updateConcessionDetailInRegistration(tt.getAddNumber(), session,String.valueOf(concessionCategoryList.get(0).getValue()),concessionStatus, conn);
		}
		if(multipleStudentList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast one Row !! "));
		}
		else
		{
			if(updateDetail==true)
			{
				String refNo;
				try {
					refNo=addWorkLog2(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Update Concession Detail Successfully "));
				selectedClass="";
				selectedSection="";
				studentList=new DataBaseMethodStudent().studentDetail("", "-1","-1", QueryConstants.BY_CLASS_SECTION, QueryConstants.VERIFY_CONCESSION,null,null,"","","","", session, schid, list, conn);
				if(studentList.isEmpty())
				{
					b=false;
				}
				else
				{
					b=true;
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occurred !!"));
			}
		}
	}
	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}
	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<StudentInfo> getMultipleStudentList() {
		return multipleStudentList;
	}
	public void setMultipleStudentList(ArrayList<StudentInfo> multipleStudentList) {
		this.multipleStudentList = multipleStudentList;
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
	public String getConceesionCategory() {
		return conceesionCategory;
	}
	public void setConceesionCategory(String conceesionCategory) {
		this.conceesionCategory = conceesionCategory;
	}
	public String getConcessionStatus() {
		return concessionStatus;
	}
	public void setConcessionStatus(String concessionStatus) {
		this.concessionStatus = concessionStatus;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}

	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
