package exam_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;

@ManagedBean (name="viewEditExamSetting")
@ViewScoped
public class ViewEditExamSettingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<ExamSettingInfo> examList;
	ExamSettingInfo selectedExam;
	ArrayList<SelectItem> classList;
	String  classId,schid,sessionValue,username,userType;
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public ViewEditExamSettingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schid,conn);
		examList=objExam.allExamSettingList(conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") 
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassListForClassTeacher(empid,schid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
		
	public void editDetails()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedExamm", selectedExam);
		
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editExamSetting.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void viewDetails()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedExamm", selectedExam);
		
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("viewExamSetting.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}


	public ArrayList<ExamSettingInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamSettingInfo> examList) {
		this.examList = examList;
	}

	public ExamSettingInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamSettingInfo selectedExam) {
		this.selectedExam = selectedExam;
	}

}
