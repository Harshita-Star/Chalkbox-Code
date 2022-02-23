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
import exam_module.ExamInfo;

@ManagedBean(name="lockExam")
@ViewScoped
public class AdminLockExamBean implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<ExamInfo> examList;
	ArrayList<SelectItem> classList;
	String selectedClass="";
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue, username, userType;

	public AdminLockExamBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId, conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			classList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
			
			selectedClass="all";
			searchData();
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList = obj.cordinatorClassList(empid, schoolId, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchData()
	{
		Connection conn = DataBaseConnection.javaConnection();
		examList=obj.examListByClassidForScholastic(selectedClass, sessionValue, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateLockStatus()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.updateExamLockStatus(examList,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exams Locked / Unlocked Sucessfully"));
			selectedClass="all";
			searchData();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Eroor Occured... Please Try Again"));
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
		String language= "Class - "+selectedClass;
		
		for(ExamInfo ec: examList)
		{
			if(value.equalsIgnoreCase(""))
			{
				value = "("+ec.getShow()+" - "+ec.getExamName()+" - "+ec.getClassid()+" - "+ec.getSemesterid()+")";
			}
			else
			{
				value = value + "("+ec.getShow()+" - "+ec.getExamName()+" - "+ec.getClassid()+" - "+ec.getSemesterid()+")";
			}
		}
		
		
	
		String refNo = workLg.saveWorkLogMehod(language,"Exam Lock","WEB",value,conn);
		return refNo;
	}

	
	public ArrayList<ExamInfo> getExamList() {
		return examList;
	}
	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
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
