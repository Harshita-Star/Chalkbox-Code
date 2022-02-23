package schooldata;

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

import org.apache.commons.fileupload.RequestContext;
import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;

@ManagedBean(name="attendanceBean")
@ViewScoped
public class AttendanceBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList,classList;
	String selectedCLassSection,selectedSection,username,schoolid,type,defaultAtt;
	boolean renderShowTable=true,showStudentRecord;
	Date selectedDay=new Date();
	ArrayList<StudentInfo> studentDetails;
	int i,j,k,hld,ml,pl;
	DatabaseMethods1 obj=new DatabaseMethods1();
	
	public void chckforstudentatndnce()
	{
		i=j=k=hld=ml=pl=0;
		for(StudentInfo ls:studentDetails)
		{
			String we=ls.getAttendance();
			if(we.equalsIgnoreCase("A"))
			{
				i++;
			}
			if(we.equalsIgnoreCase("P"))
			{
				j++;
			}
			if(we.equalsIgnoreCase("L"))
			{
				k++;
			}
			if(we.equalsIgnoreCase("H"))
			{
				hld++;
			}
			if(we.equalsIgnoreCase("ml"))
			{
				ml++;
			}
			if(we.equalsIgnoreCase("pl"))
			{
				pl++;
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
			sectionList=obj.allSection(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			//classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public AttendanceBean()
	{
		defaultAtt="P";
		selectedDay = new Date();
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			username=(String) ss.getAttribute("username");
			schoolid=(String) ss.getAttribute("schoolid");
			type=(String) ss.getAttribute("type");
			if(!type.equalsIgnoreCase("Teacher") 
					&& !type.equalsIgnoreCase("academic coordinator") 
					&& !type.equalsIgnoreCase("Administrative Officer"))
			{
				//classList=obj.allClass(conn);
				classSection=obj.allClass(conn);
			}
			else if (type.equalsIgnoreCase("academic coordinator") 
					|| type.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
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

	public void saveNow()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
		boolean check=DBJ.checkSectionHolidayForAttendanceJSON(strDate, conn,new DatabaseMethods1().schoolId(),selectedSection);
		if(check==false)
		{
			int i=DatabaseMethods1.attendanceSection(selectedSection, studentDetails, selectedDay,conn);
			if(i!=0){
				
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				renderShowTable=false;
				selectedDay = new Date();
				selectedCLassSection=null;
				selectedSection=null;
				sectionList=null;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Attendance Saved", "Alert"));

				String tdy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

				if(tdy.equals(strDate))
				{
					PrimeFaces.current().ajax().update("alert");
					PrimeFaces.current().executeInitScript("PF('alrtDlg').show();");
				}
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Attendance cannot be marked on holidays.", "Validation error"));
		}

	}
	
	public String addWorkLog(Connection conn)
	{
	
	    String value = "";
		String language= "Class - "+selectedCLassSection+" ---- Section - "+selectedSection+" ---- Date -"+selectedDay;
		
		for(StudentInfo tp: studentDetails)
		{	
		  value += "("+tp.getAddNumber()+" - "+tp.getAttendance()+")"; 
		}
		
	    String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Attendance Mark","WEB",value,conn);
		return refNo;
	}


	public void defineHoliday()
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentDetails=obj.searchStudentListByClassSectionForAttendanceNew(selectedSection,selectedDay,conn);
		for(StudentInfo stu : studentDetails)
		{
			stu.setAttendance("P");
		}
		showStudentRecord=true;
		renderShowTable=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentDetail()
	{
		renderShowTable=true;
		showStudentRecord=false;
		defaultAtt="";
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDay);
		boolean checkHoliday=DBJ.checkSectionHolidayForAttendanceJSON(strDate, conn,new DatabaseMethods1().schoolId(),selectedSection);
		try
		{
			//boolean checkHoliday = obj.checkHolidayForAttendance(strDate,conn);
			if(checkHoliday==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Attendance on holiday cannot be marked", "Validation error"));
			}
			else
			{
				studentDetails=obj.searchStudentListByClassSectionForAttendanceNew(selectedSection,selectedDay,conn);
				studentDetails=DatabaseMethods1.allAttendance(selectedSection, selectedDay, studentDetails,conn);
				showStudentRecord=true;
				PrimeFaces.current().scrollTo("form:pnl");
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
	
	public void updateDefault()
	{
		if(studentDetails.size()>0)
		{
			for(StudentInfo stu : studentDetails)
			{
				stu.setAttendance(defaultAtt);
			}
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

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
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

	public boolean isRenderShowTable() {
		return renderShowTable;
	}

	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getHld() {
		return hld;
	}
	public void setHld(int hld) {
		this.hld = hld;
	}
	public int getMl() {
		return ml;
	}
	public void setMl(int ml) {
		this.ml = ml;
	}
	public int getPl() {
		return pl;
	}
	public void setPl(int pl) {
		this.pl = pl;
	}

	
	public String getDefaultAtt() {
		return defaultAtt;
	}
	public void setDefaultAtt(String defaultAtt) {
		this.defaultAtt = defaultAtt;
	}

}
