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
import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name="houseWiseListReportBean")
@ViewScoped
public class HouseWiseListReportBean implements Serializable 
{

	
	ArrayList<SelectItem> classSection,sectionList,sessionList,houseCategorylist;
	String className,sectionName,HouseName, userType, username, schoolid;
	ArrayList<StudentInfo>studentList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	boolean b;
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public HouseWiseListReportBean() {
		
		b=false;
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		try
		{
			houseCategorylist=obj.allHouseCategory(conn);
			
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

				ArrayList<SelectItem> temp = obj.allClass(conn);

				if(temp.size()>0)
				{
					classSection.addAll(temp);
				}
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classSection=obj.cordinatorClassList(empid, schoolid, conn);
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
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(className,conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allSection(className,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,className,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodStudent obj=new DataBaseMethodStudent();
		String schid=new DatabaseMethods1().schoolId();
		String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
		ArrayList<String> stdColList=makeStdColumnList();

		studentList =obj.studentDetail(HouseName, sectionName, className, QueryConstants.BY_CLASS_SECTION, QueryConstants.HOUSE_WISE_STUDENT_WPS, null, null,"", QueryConstants.KEYWORD, "", "", session, schid,stdColList, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		} else
			b = true;


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> makeStdColumnList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ADMISSION_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SERIAL_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SECTION_ID);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHERS_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.MOTHERS_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHERS_PHONE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.HOUSENAME);
		return list;
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
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public ArrayList<SelectItem> getHouseCategorylist() {
		return houseCategorylist;
	}
	public void setHouseCategorylist(ArrayList<SelectItem> houseCategorylist) {
		this.houseCategorylist = houseCategorylist;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getHouseName() {
		return HouseName;
	}
	public void setHouseName(String houseName) {
		HouseName = houseName;
	}
	
	
	
	
}


