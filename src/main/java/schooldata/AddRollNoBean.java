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

import exam_module.DataBaseMethodsExam;
@ManagedBean(name="addRollNoBean")
@ViewScoped
public class AddRollNoBean implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classList,termList,sectionList;
	String selectedClass,selectedSection,session,schid,fillType;
	ArrayList<ExtraFieldInfo> studentList;
	boolean showRemark,showOther,showTable,showRollNo,disableRollNo;
	DatabaseMethods1 dd=new DatabaseMethods1();
	DataBaseMethodsExam de=new DataBaseMethodsExam();
	int startRollNo;

	public AddRollNoBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=dd.allClass(conn);
		schid=dd.schoolId();
		session=dd.selectedSessionDetails(schid, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public void showSemester()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=dd.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void changeFillType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(fillType.equalsIgnoreCase("Auto"))
		{
		   showRollNo = true;
		   disableRollNo = true;
		
		}
		else
		{
			showRollNo = false;
		    disableRollNo = false;	
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void searchStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
//		String sessionStatus=new DatabaseMethodSession().checkSessionStatus();
//		if(sessionStatus.equalsIgnoreCase("previous")) {
			studentList=de.studentListByClassIdForPreSession(schid,selectedSection,conn);
//		}else {
//			studentList=de.studentListByClassId(schid,selectedSection,conn);
//		}
		if(fillType.equalsIgnoreCase("Auto"))
		{
		   
		   try {

				int startNo=startRollNo;

				for(ExtraFieldInfo info:studentList)
				{
					info.setRollNo(String.valueOf(startNo));
					startNo=startNo+1;
				}


			} catch (Exception e) {
				searchStudent();
			}
		}
		showTable=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void submitValue()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean duplicate=false;
		for(ExtraFieldInfo ll:studentList)
		{
			duplicate=new DataBaseValidator().checkDuplicateRollNo(ll.getRollNo(),selectedSection,session,schid,ll.getStudentId(),conn);
			if(duplicate==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Roll No Found For Student "+ll.getStudentName()+".. Please Try With Another One.", "Validation error"));
				break;
			}
		}
		
		if(duplicate==false)
		{
			int i=de.addRollNoOfStudent(studentList,conn);
			if(i>=1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				}
				catch (Exception e) {
				e.printStackTrace();
			    }
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Submitted Sucessfully"));
				selectedClass=selectedSection="";studentList=new ArrayList<>();
				sectionList=termList=new ArrayList<>();showTable=showOther=showRemark=false;
				showRollNo=false;disableRollNo=false;
				fillType ="";
	
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
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

		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class - "+className+" --- Section - "+sectionname+" --- FillType -"+fillType+" --- Start RollNo - "+startRollNo;
		for(ExtraFieldInfo ex : studentList)
		{
			value += "(Student - "+ex.getStudentId()+" -- Roll No - "+ex.getRollNo()+")"; 	
		}
		
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Roll No.","WEB",value,conn);
		return refNo;
	}


	public void autoFillMeathod()
	{
		try {

			int startNo=Integer.parseInt(studentList.get(0).getRollNo());

			for(ExtraFieldInfo info:studentList)
			{
				info.setRollNo(String.valueOf(startNo));
				startNo=startNo+1;
			}


		} catch (Exception e) {
			searchStudent();
		}
	}


	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getTermList() {
		return termList;
	}
	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
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
	public ArrayList<ExtraFieldInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<ExtraFieldInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShowRemark() {
		return showRemark;
	}
	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}
	public boolean isShowOther() {
		return showOther;
	}
	public void setShowOther(boolean showOther) {
		this.showOther = showOther;
	}
	public boolean isShowTable() {
		return showTable;
	}
	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}
	public String getFillType() {
		return fillType;
	}
	public void setFillType(String fillType) {
		this.fillType = fillType;
	}

	public boolean isShowRollNo() {
		return showRollNo;
	}
	public void setShowRollNo(boolean showRollNo) {
		this.showRollNo = showRollNo;
	}
	public boolean isDisableRollNo() {
		return disableRollNo;
	}
	public void setDisableRollNo(boolean disableRollNo) {
		this.disableRollNo = disableRollNo;
	}
	public int getStartRollNo() {
		return startRollNo;
	}
	public void setStartRollNo(int startRollNo) {
		this.startRollNo = startRollNo;
	}
	



}
