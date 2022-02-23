package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="parentLoginReport")
@ViewScoped

public class ParentLoginReportBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<EmployeeInfo>teacherList=new ArrayList<>();
	List<EmployeeInfo> selectedStudentList = new ArrayList<>();
	EmployeeInfo selectedEmployee;
	int total;
	String oldPassword,username,password,balMsg,userType;
	boolean b=false,show=false;
	double smsLimit;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	String selectedCLassSection,selectedSection;
	ArrayList<SelectItem> sectionList,classSection;
	String sessionValue,schoolId,name;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseOnlineAdm onlnAdm = new DataBaseOnlineAdm();
	ArrayList<StudentInfo>list=new ArrayList<>();
	DatabaseMethodSession objSession=new DatabaseMethodSession();


	public ParentLoginReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		try
		{
			classSection=obj.allClass(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	
		
		smsLimit = obj.smsLimitReminder(schoolId, conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");


//		teacherList=obj.allStudentLoginList(schoolId,selectedCLassSection,selectedSection,conn);
//		if(teacherList.size()>0)
//		{
//			b=true;
//			show=true;
//		}
//		total=teacherList.size();
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
		sectionList=obj.allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();

		teacherList=obj.allStudentLoginList(schoolId,selectedCLassSection,selectedSection,"normal",conn);
		if(teacherList.size()>0)
		{
			b=true;
			show=true;
		}
		total=teacherList.size();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void viewDetails()
	{
		oldPassword=selectedEmployee.getPassword();
		password=selectedEmployee.getPassword();
	}
	
	public String multipleLoginBlock()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{
			for(EmployeeInfo ls:selectedStudentList)
			{
				int i=obj.blockUser(ls.getEmplyeeuserid(),"block",conn);
				if(i>=1)
				{
					String refNo2;
					try {
						refNo2=addWorkLog(ls.getEmplyeeuserid(),conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
		}
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Block User Successfully"));
			selectedSection="";
			selectedCLassSection = "";
			b=false;
			show=false;
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "parentLoginReport.xhtml";
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Please select Atleast One Student"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
		
			
	
	}
	
	public void searchStudentByName() {
		
		Connection conn = DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			teacherList=obj.studentLoginListAddNumberWise(schoolId,id,"normal",conn);
			if(teacherList.size()>0)
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
			total=teacherList.size();
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
	
	
	public void editPassword()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.passwordUpdate(selectedEmployee.getEmplyeeuserid(), password, oldPassword,conn,"student");
		if(i>=1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Password Updated Successfully"));
			teacherList=obj.allStudentLoginList(schoolId,selectedCLassSection,selectedSection,"normal",conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String block()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.blockUser(selectedEmployee.getEmplyeeuserid(),"block",conn);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog(selectedEmployee.getEmplyeeuserid(),conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Block User Successfully"));
			selectedSection="";
			selectedCLassSection = "";
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "parentLoginReport.xhtml";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	return "";	
		
	}
	
	public String addWorkLog(String value, Connection conn)
	{
		String language= "";
		
		String refNo = workLg.saveWorkLogMehod(language,"Block Student In Parent Login Report","WEB",value,conn);
		return refNo;
	}


	public void sendMail()
	{
		if(selectedStudentList.size()>0)
		{
			List<EmployeeInfo> newList = selectedStudentList;
			Runnable r = new Runnable()
			{
				public void run()
				{
					Connection conn = DataBaseConnection.javaConnection();
					//String schnm = DBM.schoolNameById(DBM.schoolId(), conn);
					String subject = ls.getSchoolName() + " : Parent Portal Login Credentials!";

					for(EmployeeInfo info : newList)
					{
						StudentInfo sinfo = obj.studentDetailslistByAddNo(schoolId, info.getEmplyeeuserid(), conn);
						String msg = "<center>"+"Your Parent Portal Login Id : <strong>"+info.getEmplyeeuserid()+"</strong><br></br>"+"Password : <strong>"+info.getPassword()+"</strong><br></br>"+"<a href=\"http://chalkboxerp.in/DM/ChalkboxLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a> <br></br></center>";
						String heading = "<center>Hello "+sinfo.getFname()+"</center>";
						if(sinfo.getActionBy().equalsIgnoreCase("Father"))
						{
							onlnAdm.doMail(sinfo.getFatherEmail(), subject, heading, msg);
						}
						else if(sinfo.getActionBy().equalsIgnoreCase("Mother"))
						{
							onlnAdm.doMail(sinfo.getMotherEmail(), subject, heading, msg);
						}
						else if(sinfo.getActionBy().equalsIgnoreCase("Both"))
						{
							onlnAdm.doMail(sinfo.getFatherEmail(), subject, heading, msg);
							onlnAdm.doMail(sinfo.getMotherEmail(), subject, heading, msg);
						}
					}

					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			};
			new Thread(r).start();

			selectedStudentList = new ArrayList<>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Credentials Shared Successfully!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One Student!"));
		}
	}

	public String sendMessage() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		double balance = new DatabaseMethods1().smsBalance(schoolId, conn);
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
			return "";
		}
		else
		{
			sendMsg();
			teacherList = new ArrayList<EmployeeInfo>();
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return "parentLoginReport.xhtml";

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String tempMsg = "";
			if(info.getParentAppType().equalsIgnoreCase("common"))
			{
				tempMsg = obj.contactNo("Common App Login SMS",conn);
			}
			else
			{
				tempMsg = obj.contactNo("Student Login SMS",conn);
			}
			
			String message="";
			String msg="";
			for(EmployeeInfo list : selectedStudentList)
			{

				if(String.valueOf(list.getMobile()).length()==10
						&& !String.valueOf(list.getMobile()).equals("9999999999")
						&& !String.valueOf(list.getMobile()).equals("1111111111")
						&& !String.valueOf(list.getMobile()).equals("1234567890")
						&& !String.valueOf(list.getMobile()).equals("0123456789"))
				{
					message="";
					msg="";
					msg=tempMsg.replaceAll("#name", list.getFname());
					msg=tempMsg.replaceAll("#unm", list.getEmplyeeuserid());
					msg=msg.replaceAll("#pwd", list.getPassword());
					msg=msg.replaceAll("#link", info.getApp_link());
					
					String templateId="";
					if(info.getParentAppType().equalsIgnoreCase("common"))
					{
						msg=msg.replaceAll("#schcode", info.getAliasName());
						templateId=new DataBaseMeathodJson().templateId(info.getKey(),"COMMONLOGIN", conn);
					}
					else 
					{
						templateId=new DataBaseMeathodJson().templateId(info.getKey(),"STUDENTLOGIN", conn);
								
					}
					
					message= msg+"\n"+info.getSmsSchoolName();

					
					
					
					//msg=message;
					new DataBaseMeathodJson().messageUrlWithTemplate(String.valueOf(list.getMobile()), message,list.getEmplyeeuserid(),schoolId,conn,templateId);
                    obj.insertShareMessageTimeInAllUser(list.getEmplyeeuserid(),conn,schoolId);
				}
			}
			message="";
			msg="";

			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>0)
			{
				fc.addMessage(null, new FacesMessage("Credentials Shared Successfully With "+selectedStudentList.size()+" Students"));
			}
			selectedStudentList.clear();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Student Selected,Select Atleast One", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public ArrayList<EmployeeInfo> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<EmployeeInfo> teacherList) {
		this.teacherList = teacherList;
	}

	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<EmployeeInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(List<EmployeeInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
