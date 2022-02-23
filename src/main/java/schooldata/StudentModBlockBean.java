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
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="studentModBlock")
@ViewScoped

public class StudentModBlockBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<EmployeeInfo> studentList,selectedStudentList=new ArrayList<>();
	EmployeeInfo selectedEmployee;
	int total;
	String userType, blockMsg = "";
	boolean b=false,show=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	String selectedCLassSection,selectedSection,username;
	ArrayList<SelectItem> sectionList,classSection;
	String sessionValue,schoolId,name;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseOnlineAdm onlnAdm = new DataBaseOnlineAdm();
	ArrayList<StudentInfo>list=new ArrayList<>();
	String[] selectedModule;
	ArrayList<SelectItem> allMods = new ArrayList<SelectItem>();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	
	public StudentModBlockBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		allMods = obj.appPermissions(obj.schoolId(), "school_student_permission", conn);
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		try
		{
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

				ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

				if(temp.size()>0)
				{
					classSection.addAll(temp);
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
						|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection = obj.cordinatorClassList(empid, schoolId, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection=obj.allClassListForClassTeacher(empid,schoolId,conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		

		ls=obj.fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//list=new DatabaseMethods1().searchStudentList(query,conn);
		list=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : list)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp = obj.allSection(selectedCLassSection,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();

		studentList=obj.allStudentLoginList(schoolId,selectedCLassSection,selectedSection,"ModBlock",conn);
		if(studentList.size()>0)
		{
			b=true;
			show=true;
		}
		total=studentList.size();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByName() {
		
		Connection conn = DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			studentList=obj.studentLoginListAddNumberWise(schoolId,id,"ModBlock",conn);
			if(studentList.size()>0)
			{
				b=true;
				show=true;
			}
			else
			{
				b=false;
				boolean studentBlocked = obj.checkStudentBlocked(schoolId,id,conn);
				
				if(studentBlocked)
				{	
				  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected Student is blocked"));
				}
				else
				{
					  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Generate selected Student Login Credentials"));
				}
			}
			total=studentList.size();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void copyBlockStuff() 
	{
		for(EmployeeInfo ss : studentList)
		{
			ss.setBlockModList(selectedModule);
			ss.setMsg(blockMsg);
		}
	}

	public String updateAction()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		if(selectedStudentList.size()>0)
		{
			int i = 0,x=0;
			for(EmployeeInfo ls:selectedStudentList)
			{
				i=obj.blockStudentAppMods(ls.getEmplyeeuserid(),"Explicit Block",ls.getBlockModList(),schoolId,0,ls.getMsg(),"manual",conn);
				if(i>=1)
				{
					x+=1;
				}
			}
			
			if(x>=1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Block/UnBlock Modules, Updated Successfully!"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "studentModBlock.xhtml";
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Something Went Wrong. Please Try Again!"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
			
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Please Select Atleast One Student...!"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
	}
	
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public ArrayList<EmployeeInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<EmployeeInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<EmployeeInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<EmployeeInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
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

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getSessionValue() {
		return sessionValue;
	}

	public void setSessionValue(String sessionValue) {
		this.sessionValue = sessionValue;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DatabaseMethodWorkLog getWorkLg() {
		return workLg;
	}

	public void setWorkLg(DatabaseMethodWorkLog workLg) {
		this.workLg = workLg;
	}

	public DataBaseOnlineAdm getOnlnAdm() {
		return onlnAdm;
	}

	public void setOnlnAdm(DataBaseOnlineAdm onlnAdm) {
		this.onlnAdm = onlnAdm;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public DatabaseMethodSession getObjSession() {
		return objSession;
	}

	public void setObjSession(DatabaseMethodSession objSession) {
		this.objSession = objSession;
	}

	public String getBlockMsg() {
		return blockMsg;
	}

	public void setBlockMsg(String blockMsg) {
		this.blockMsg = blockMsg;
	}

	public String[] getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(String[] selectedModule) {
		this.selectedModule = selectedModule;
	}

	public ArrayList<SelectItem> getAllMods() {
		return allMods;
	}

	public void setAllMods(ArrayList<SelectItem> allMods) {
		this.allMods = allMods;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
