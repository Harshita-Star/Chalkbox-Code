package reports_module;

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

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.DatabaseMethodSession;

@ManagedBean(name="attendSummaryReport")
@ViewScoped
public class AttendanceSummaryReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	boolean show;
	Date startDate,endDate;
	ArrayList<SelectItem> classSection,sectionList;
	
	String selectedClassSection,selectedSection,schid,session, username, userType;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DatabaseMethodSession objSession=new DatabaseMethodSession();


	public AttendanceSummaryReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid,conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");

		classSection=obj1.allClass(conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classSection.add(si);

			ArrayList<SelectItem> temp = obj1.allClass(conn);

			if(temp.size()>0)
			{
				classSection.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection = obj1.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj1.allClassListForClassTeacher(empid,schid,conn);
		}
		startDate=new Date();
		endDate=new Date();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
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
			si.setValue("-1");
			sectionList.add(si);
			sectionList.addAll(obj1.allSection(selectedClassSection,conn));
		}	
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=obj1.allSectionListForClassTeacher(empid,selectedClassSection,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void searchReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=obj1.searchStudentListByClassSection(selectedSection,conn);
		studentList=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedClassSection,selectedSection);

		if(studentList.size()>0)
		{
			show=true;
			obj1.attendanceSummaryReport(startDate,endDate,selectedSection,schid,session,studentList,conn);
		}
		else
		{
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
