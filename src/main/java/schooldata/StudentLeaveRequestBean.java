package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="stdLeaveRequest")
@ViewScoped
public class StudentLeaveRequestBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> leaveInfo;
	StudentInfo selectedItem;
	String schId,type, username, usertype;
	boolean show,show1;
	DatabaseMethods1 DBJ=new DatabaseMethods1();
	DataBaseMeathodJson dd = new DataBaseMeathodJson();
	String remark="";
	
	public StudentLeaveRequestBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		usertype=(String) ss.getAttribute("type");
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
		schId=info.getSchid();
		show=true;show1=false;
		
		leaveList("0", conn);
		
		type = "Pending Leave Requests";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void leaveList(String status, Connection conn)
	{
		if(usertype.equalsIgnoreCase("admin")
				|| usertype.equalsIgnoreCase("authority")
				|| usertype.equalsIgnoreCase("principal")
				|| usertype.equalsIgnoreCase("vice principal")
				|| usertype.equalsIgnoreCase("front office") || usertype.equalsIgnoreCase("office staff")
				|| usertype.equalsIgnoreCase("Accounts"))
		{
			leaveInfo=DBJ.viewStudentLeaveAll(schId,status,conn);
		}
		else if(usertype.equalsIgnoreCase("academic coordinator") 
				|| usertype.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=dd.employeeIdbyEmployeeName(username, schId,conn);
			ArrayList<String> list1 = dd.cordinatorSectionList(empid,schId,conn);
			leaveInfo=DBJ.viewStudentLeave(schId,status,list1,conn);
		}
		else
		{
			String empid=dd.employeeIdbyEmployeeName(username, schId,conn);
			ArrayList<String>list1=dd.allClassListForTeacher(schId,empid,conn);
			leaveInfo=DBJ.viewStudentLeave(schId,status,list1,conn);
		}
	}

	public void pendingLeave()
	{
		show=true;show1=false;
		Connection conn= DataBaseConnection.javaConnection();

		leaveList("0", conn);
		type = "Pending Leave Requests";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void approveLeave()
	{
		show=false;show1=true;
		Connection conn= DataBaseConnection.javaConnection();

		leaveList("1", conn);
		type = "Approved Leave Requests";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void denyLeave()
	{
		show=false;show1=true;
		Connection conn= DataBaseConnection.javaConnection();

		leaveList("2", conn);
		type = "Rejected Leave Requests";
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void approveRequest()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();

		if(selectedItem!=null)
		{
			HttpSession ss  = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String unm = (String) ss.getAttribute("username");
			int i=DBJ.updateStudentLeave(selectedItem.getId(),"1","",schId,unm,conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				context.addMessage(null, new FacesMessage("Leave Allowed SuccessFully"));
				leaveList("0", conn);
			}
			else
			{
				context.addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
			}
			remark="";
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
		
		value = "Selected Id-"+selectedItem.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Leave Request Approve","WEB",value,conn);
		return refNo;
	}
	
	

	public void denyRequest()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();

		if(selectedItem!=null)
		{
			HttpSession ss  = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String unm = (String) ss.getAttribute("username");
			int i=DBJ.updateStudentLeave(selectedItem.getId(),"2",remark,schId,unm,conn);
			if(i==1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				context.addMessage(null, new FacesMessage("Leave Denied SuccessFully"));
				leaveList("0", conn);
			}
			else
			{
				context.addMessage(null, new FacesMessage("An Error Occured..... Try Again"));
			}
			
			remark="";
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
		
		value = "Selected Id-"+selectedItem.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Leave Request Deny","WEB",value,conn);
		return refNo;
	}
	
	

	public ArrayList<StudentInfo> getLeaveInfo() {
		return leaveInfo;
	}

	public void setLeaveInfo(ArrayList<StudentInfo> leaveInfo) {
		this.leaveInfo = leaveInfo;
	}

	public StudentInfo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(StudentInfo selectedItem) {
		this.selectedItem = selectedItem;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isShow1() {
		return show1;
	}

	public void setShow1(boolean show1) {
		this.show1 = show1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
