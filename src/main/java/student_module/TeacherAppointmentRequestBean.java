package student_module;

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
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.RegexPattern;
@ManagedBean(name="teacherAppointmentRequest")
@ViewScoped
public class TeacherAppointmentRequestBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String description,schoolId,username,appointmentTime,name,studentId,selectType,type;
	Date appointmentDate,currentDate=new Date();
	boolean showStudent;
	DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();
	DatabaseMethods1 obj=new DatabaseMethods1();

	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	ArrayList<StudentInfo>list=new ArrayList<>();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	
	public TeacherAppointmentRequestBean()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");

		selectType = "Meeting";
	}
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=obj.searchStudentList(query,conn);
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
	public void searchStudentByName()
	{
		int index=name.lastIndexOf(":")+1;
		studentId=name.substring(index);
		showStudent=true;
	}

	public String send()
	{
		if(selectType.equalsIgnoreCase("Consent"))
		{
			type="Consent";
		}
		else
		{
			type="Meeting";
		}
		Connection conn = DataBaseConnection.javaConnection();
		////// // System.out.println("stude"+studentId);
		////// // System.out.println("usr"+username);
		int i= obj1.sendTeacherAppointmentRequest(conn,appointmentDate,currentDate,description,appointmentTime,username,studentId,type,"pending",schoolId);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Teacher Appointment request is successfully sent!"));
			StudentInfo info=DBJ.studentDetailslistByAddNo(studentId,schoolId,conn);

			String msg="";
			if(selectType.equalsIgnoreCase("Consent"))
			{
				msg="School is asking for your consent. Please open the app to check the details.";
			}
			else
			{
				String appDate=new SimpleDateFormat("dd-MM-yyyy").format(appointmentDate);
				msg="School is requesting for a meeting on "+appDate+ " "+appointmentTime+ ". Please open the app to check the details.";
			}
			DBJ.notification(type,msg, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
			DBJ.notification(type,msg, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
			return "teacherAppointmentRequest.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String apdte = formatter.format(appointmentDate);
		String curdte = formatter.format(currentDate);

		language = "Appointment date-"+apdte+" --Current Date-"+curdte+" --Student-"+studentId;
		value = "Appointment Time-"+appointmentTime+" --Description-"+description;
		
		String refNo =workLg.saveWorkLogMehod(language,"teacher Appointment Request","WEB",value,conn);
		return refNo;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public boolean isShowStudent() {
		return showStudent;
	}
	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
	}
	public DataBaseOnlineAdm getObj1() {
		return obj1;
	}
	public void setObj1(DataBaseOnlineAdm obj1) {
		this.obj1 = obj1;
	}
	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getSelectType() {
		return selectType;
	}
	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}