package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import session_work.RegexPattern;

@ManagedBean(name="addComplainDiary")
@ViewScoped
public class AddComplaintDiaryBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,note="",session,schid;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	boolean show;
	Date dateOfEntry;
	ArrayList<SelectItem> classSection,sectionList;
	String selectedClassSection,selectedSection,description,complaintBy,sms,type,balMsg,userType,username;
	StudentInfo selectedStudent;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	double smsLimit;
	ArrayList<EmployeeInfo> employeeList;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMeathodJson objJson = new DataBaseMeathodJson();

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj1.allSection(selectedClassSection,conn);
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
	
	public List<String> autoCompleteEmplInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		employeeList=obj1.searchEmployeebyName(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(EmployeeInfo info : employeeList)
		{
			studentListt.add(info.getFname()+" / "+info.getLname()+ "-"+info.getId());
		}
        
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public AddComplaintDiaryBean()
	{
		sms="no";
		type="complaint";
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj1.schoolId();
		smsLimit = obj1.smsLimitReminder(schid, conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		boolean check=(boolean) ss.getAttribute("checkstu");
		String addNumber=(String) ss.getAttribute("username");
		
		
		session=obj1.selectedSessionDetails(schid, conn);
		
		if(check==false)
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection=obj1.allClass(conn);
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection=obj1.cordinatorClassList(empid, schid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection=obj1.allClassListForClassTeacher(empid,schid,conn);
			}
		}
		else
		{
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, list, conn).get(0);
			studentList.add(sn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj1.searchStudentList(query,conn);
		List<String> studentListt=new ArrayList<>();
		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-"+info.getAddNumber());
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
		note="";
		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					studentList=new ArrayList<>();
					selectedClassSection=info.getClassId();
					studentList.add(info);
					break;
					//selectedStudent=info;
					/*achievement();
					return "printFinalMarksheet";*/
				}
			}
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Note: Please Select Student Name From Autocomplete List"));
		}

		if(studentList.size()>0)
		{
			show=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		//return null;
	}

	public void searchStudentByClassSection()
	{
		note="";
		Connection conn=DataBaseConnection.javaConnection();
		studentList=obj1.searchStudentListByClassSection(selectedClassSection, selectedSection,conn);
		if(studentList.size()>0)
		{
			show=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String achievement()
	{
		if(selectedStudent!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("StudentDetail", selectedStudent);

			show=false;name=selectedClassSection=null;selectedStudent=null;
			return "printFinalMarksheet";
		}
		else
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage(" Please select atleast one student"));
		}
		return null;
	}


	public String addComplaint()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj1;
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) ss.getAttribute("username");
		
		int i=DBM.addComplaintDiary(selectedStudent.getAddNumber(),description,complaintBy,type,userid,conn);
		if(i>=1)
		{
			String refNo;
			try {		
				refNo=addWorkLog(conn);
			}
			catch (Exception e) {
			e.printStackTrace();
		    }
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Complaint Added Successfully!"));
			
			if(selectedStudent.getFathersPhone()==selectedStudent.getMothersPhone())
			{
				objJson.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module. ", selectedStudent.getFathersPhone()+"-"+selectedStudent.getAddNumber()+"-"+DBM.schoolId(),DBM.schoolId(),"",conn);
			}
			else
			{
				objJson.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module.", selectedStudent.getFathersPhone()+"-"+selectedStudent.getAddNumber()+"-"+DBM.schoolId(),DBM.schoolId(),"",conn);
				objJson.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module.", selectedStudent.getMothersPhone()+"-"+selectedStudent.getAddNumber()+"-"+DBM.schoolId(),DBM.schoolId(),"",conn);
			}
	
			
			//HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String complaintSMS = (String) ss.getAttribute("complaint");
			if(complaintSMS.equals("true"))
			{
				double balance = obj1.smsBalance(schid, conn);
				if(balance >0 && balance <= smsLimit)
				{
					balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
							+ "We suggest you to top-up your account today to ensure uninterrupted activity";
					if (userType.equalsIgnoreCase("admin"))
					{
						PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
						PrimeFaces.current().ajax().update("MsgLimitForm");
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						return "";
					}
				}
				else if(balance <= 0)
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
					if (userType.equalsIgnoreCase("admin"))
					{
						PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
						PrimeFaces.current().ajax().update("MsgOverForm");
					}
					else
					{
						balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

						PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
						PrimeFaces.current().ajax().update("MsgOtherForm");
					}
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					description="";
					complaintBy="";
					type="complaint";

					return "";
				}

				if(String.valueOf(selectedStudent.getFathersPhone()).length()==10
						&& !String.valueOf(selectedStudent.getFathersPhone()).equals("9999999999")
						&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1111111111")
						&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1234567890")
						&& !String.valueOf(selectedStudent.getFathersPhone()).equals("0123456789"))
				{
					SchoolInfoList ls=DBM.fullSchoolInfo(conn);
					String message="Dear Parent,\n"+description+"\n"+type.toUpperCase()+" BY - "+complaintBy+"\nRegards,\n"+ls.getSmsSchoolName();
					
					DBM.messageurl1(String.valueOf(selectedStudent.getFathersPhone()), message, selectedStudent.getAddNumber(), conn,schid,"");
				}
				////// // System.out.println(sms);

			}

			//sms="no";
			description="";
			complaintBy="";
			type="complaint";


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

		
		language = "Complain By -"+complaintBy+" --- Description - "+description+" --- Type - "+type;
		value = "Student - "+selectedStudent.getAddNumber()+" --- Complain By -"+complaintBy+" --- Description - "+description+" --- Type - "+type;

		String refNo = workLg.saveWorkLogMehod(language,"Add Student Behaviour","WEB",value,conn);
		return refNo;
	}

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(String.valueOf(selectedStudent.getFathersPhone()).length()==10
				&& !String.valueOf(selectedStudent.getFathersPhone()).equals("9999999999")
				&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1111111111")
				&& !String.valueOf(selectedStudent.getFathersPhone()).equals("1234567890")
				&& !String.valueOf(selectedStudent.getFathersPhone()).equals("0123456789"))
		{
			SchoolInfoList ls=obj1.fullSchoolInfo(conn);
			String message="Dear Parent,\n"+description+"\n"+type.toUpperCase()+" BY - "+complaintBy+"\nRegards,\n"+ls.getSmsSchoolName();
			obj1.messageurl1(String.valueOf(selectedStudent.getFathersPhone()), message, selectedStudent.getAddNumber(), conn,schid,"");
		}

		description="";
		complaintBy="";
		type="complaint";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getDateOfEntry() {
		return dateOfEntry;
	}
	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComplaintBy() {
		return complaintBy;
	}

	public void setComplaintBy(String complaintBy) {
		this.complaintBy = complaintBy;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public double getSmsLimit() {
		return smsLimit;
	}

	public void setSmsLimit(double smsLimit) {
		this.smsLimit = smsLimit;
	}

	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
