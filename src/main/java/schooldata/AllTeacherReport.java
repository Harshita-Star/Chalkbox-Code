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
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;
@ManagedBean(name="allTeacherReport")
@ViewScoped
public class AllTeacherReport implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<EmployeeInfo>teacherList=new ArrayList<>();
	List<EmployeeInfo>selectedEmployeeList=new ArrayList<>();
	EmployeeInfo selectedEmployee;
	int total;
	String oldPassword,username,password,balMsg,userType;
	boolean b=false,show=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	double smsLimit;
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public AllTeacherReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		smsLimit = new DatabaseMethods1().smsLimitReminder(schoolId, conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");

		teacherList=obj.allTeacherList(conn);
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
	public void editPassword()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userType=(String) ss.getAttribute("type");
		int i=obj.passwordUpdate(selectedEmployee.getEmplyeeuserid(), password, oldPassword,conn,userType);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Password Updated Successfully"));
			teacherList=obj.allTeacherList(conn);
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
		
		language = password;
		value =selectedEmployee.getEmplyeeuserid()+" -- "+password + " -- "+oldPassword; 
	
		String refNo = workLg.saveWorkLogMehod(language,"Teacher Password Change","WEB",value,conn);
		return refNo;
	}


	public void block()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.blockUser(selectedEmployee.getEmplyeeuserid(),"block",conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Block User Successfully"));
			teacherList=obj.allTeacherList(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";

		value =selectedEmployee.getEmplyeeuserid(); 
	
		String refNo = workLg.saveWorkLogMehod(language,"Teacher Pass Block","WEB",value,conn);
		return refNo;
	}


	public String sendMessage() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		double balance = obj.smsBalance(schoolId, conn);
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
		if(selectedEmployeeList.size()>0)
		{
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String message="";
			String msg="";
			for(EmployeeInfo list : selectedEmployeeList)
			{
				String tempMsg = obj.contactNo("Staff Login SMS",conn);
				if(String.valueOf(list.getMobile()).length()==10
						&& !String.valueOf(list.getMobile()).equals("9999999999")
						&& !String.valueOf(list.getMobile()).equals("1111111111")
						&& !String.valueOf(list.getMobile()).equals("1234567890")
						&& !String.valueOf(list.getMobile()).equals("0123456789"))
				{
					tempMsg=tempMsg.replaceAll("#name", list.getFname());
					tempMsg=tempMsg.replaceAll("#unm", list.getEmplyeeuserid());
					tempMsg=tempMsg.replaceAll("#pwd", list.getPassword());

					message= tempMsg+"\n"+info.getSmsSchoolName();

					msg=message;
					String templateId=new DataBaseMeathodJson().templateId(info.getKey(),"STAFFLOGIN",conn);
					
					obj.messageurlStaffWithTemplate(String.valueOf(list.getMobile()), msg,list.getEmplyeeuserid()+"@CB@"+list.getId(),conn,schoolId,templateId);

				}
			}
			message="";

			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedEmployeeList.size()>0)
			{
				fc.addMessage(null, new FacesMessage("Credentials Shared Successfully With "+selectedEmployeeList.size()+" Staff Members"));
			}
			selectedEmployeeList.clear();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Staff Members Selected,Select Atleast One", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void changePlatform()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedEmployeeList.size()>0)
		{
			int i = 0, x = 0;
			for(EmployeeInfo list : selectedEmployeeList)
			{
				i = obj.updateStaffAccessPlatform(list.getPlatform(), list.getEmplyeeuserid(), list.getId(), schoolId, conn);
				if(i>=1)
				{
					x+=1;
				}
			}

			FacesContext fc=FacesContext.getCurrentInstance();
			if(x>0)
			{
				fc.addMessage(null, new FacesMessage("Access Platform Updated Successfully of "+x+" Staff Members"));
				teacherList=obj.allTeacherList(conn);
				selectedEmployeeList.clear();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something Went Wrong, Please Try Again!", "Validation error"));
			}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Staff Members Selected,Select Atleast One", "Validation error"));
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
	public List<EmployeeInfo> getSelectedEmployeeList() {
		return selectedEmployeeList;
	}
	public void setSelectedEmployeeList(List<EmployeeInfo> selectedEmployeeList) {
		this.selectedEmployeeList = selectedEmployeeList;
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
