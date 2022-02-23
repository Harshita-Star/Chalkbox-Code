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

import org.primefaces.PrimeFaces;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name = "bulkMarksheetPdfReport")
@ViewScoped
public class BulkMarksheetPdfReportBean implements Serializable {

	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section, type;
	ArrayList<ClassInfo> list;
	boolean b, showAll, showActive, showHold, showNo;
	String sectionName, examName;
	ArrayList<StudentInfo> studentList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	ArrayList<String> examList;
	StudentInfo selected = new StudentInfo();
	ArrayList<StudentInfo> selectedActiveList;

	public BulkMarksheetPdfReportBean() 
	{
		selectedActiveList = new ArrayList<>();//
		type = "all";
		//// // System.out.println("size : "+selectedActiveList.size());
		Connection conn = DataBaseConnection.javaConnection();
		classList = dbm.allClass(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = new DatabaseMethods1().allSection(selectedClass, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allExams() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		examList = new DatabaseMethods1().checkListOfExams(selectedClass, selectedSection, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentInClass() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		selectedActiveList = new ArrayList<>();
		studentList = dbm.viewClassStudentMarksheet(selectedSection, examName, type, conn);
		if (studentList.isEmpty()) 
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		}
		else 
		{
			b = true;

			if (type.equalsIgnoreCase("all")) 
			{
				showAll = true;
				showActive = false;
				showHold = false;
				showNo = false;
			} 
			else if (type.equalsIgnoreCase("active"))
			{
				showActive = true;
				showAll = true;
				showHold = false;
				showNo = false;
			} 
			else if (type.equalsIgnoreCase("hold"))
			{
				showHold = true;
				showAll = true;
				showActive = false;
				showNo = false;
			} 
			else if (type.equalsIgnoreCase("no")) 
			{
				showNo = true;
				showAll = false;
				showActive = false;
				showHold = false;
			}

			for (StudentInfo inf : studentList)
			{
				if (inf.getFileNo().equalsIgnoreCase("no")) 
				{
					inf.setFileRender(false);
				}
				else 
				{
					inf.setFileRender(true);
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDocss() {

		String filename = selected.getFileNo();
		// //// // System.out.println("sd");
		PrimeFaces.current().executeInitScript("window.open('" + filename + "')");
	}
	
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		
		if(selectedActiveList.size()>0)
		{
			int i = dbr.deleteMarksheet(selectedActiveList,conn);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Marksheet(s) deleted successfully!"));
				studentInClass();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to delete marksheet"));
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
		
		language = "ClassId-"+selectedClass+" -- Exam Name-"+examName+" --SectioId-"+selectedSection+" --Type-"+type;
		for(StudentInfo ss : selectedActiveList)
		{
		  value += "("+ss.getRfidInfo().getValue()+")" ;
		}

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Marksheet","WEB",value,conn);
		return refNo;
	}
	
	public void updateActive()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		
		if(selectedActiveList.size()>0)
		{
			int i = dbr.updateMarksheetStatus(selectedActiveList,"active",conn);
			if(i>0)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Status of Marksheet(s) updated successfully!"));
				studentInClass();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to active the marksheet"));
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "ClassId-"+selectedClass+" -- Exam Name-"+examName+" --SectioId-"+selectedSection+" --Type-"+type;
		for(StudentInfo ss : selectedActiveList)
		{
		  value += "("+ss.getRfidInfo().getValue()+")" ;
		}

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Active Marksheet","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "ClassId-"+selectedClass+" -- Exam Name-"+examName+" --SectioId-"+selectedSection+" --Type-"+type;
		for(StudentInfo ss : selectedActiveList)
		{
		  value += "("+ss.getRfidInfo().getValue()+")" ;
		}

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Hold Marksheet","WEB",value,conn);
		return refNo;
	}
	
	
	public void updateHold()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();
		
		if(selectedActiveList.size()>0)
		{
			int i = dbr.updateMarksheetStatus(selectedActiveList,"hold",conn);
			if(i>0)
			{
				String refNo3;
				try {
					refNo3=addWorkLog3(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Status of Marksheet(s) updated successfully!"));
				studentInClass();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to hold the marksheet"));
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<String> examList) {
		this.examList = examList;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}

	public ArrayList<StudentInfo> getSelectedActiveList() {
		return selectedActiveList;
	}

	public void setSelectedActiveList(ArrayList<StudentInfo> selectedActiveList) {
		this.selectedActiveList = selectedActiveList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	public boolean isShowActive() {
		return showActive;
	}

	public void setShowActive(boolean showActive) {
		this.showActive = showActive;
	}

	public boolean isShowHold() {
		return showHold;
	}

	public void setShowHold(boolean showHold) {
		this.showHold = showHold;
	}

	public boolean isShowNo() {
		return showNo;
	}

	public void setShowNo(boolean showNo) {
		this.showNo = showNo;
	}

}
