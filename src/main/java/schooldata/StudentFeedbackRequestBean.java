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

@ManagedBean(name="stdFeedbackRequest")
@ViewScoped
public class StudentFeedbackRequestBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> leaveInfo;
	StudentInfo selectedItem;
	String schId,username, usertype;
	boolean show,show1,b;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public StudentFeedbackRequestBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		usertype=(String) ss.getAttribute("type");
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
		schId=info.getSchid();
		show=true;show1=false;
		list("pending", conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void list(String status, Connection conn)
	{

		if(usertype.equalsIgnoreCase("admin")
				|| usertype.equalsIgnoreCase("authority")
				|| usertype.equalsIgnoreCase("principal")
				|| usertype.equalsIgnoreCase("vice principal")
				|| usertype.equalsIgnoreCase("front office") || usertype.equalsIgnoreCase("office staff")
				|| usertype.equalsIgnoreCase("Accounts"))
		{
			leaveInfo=DBJ.SchoolTotalfeedbackByStatusAll(status,schId,conn);
		}
		else if(usertype.equalsIgnoreCase("academic coordinator") 
				|| usertype.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=DBJ.employeeIdbyEmployeeName(username, schId,conn);
			ArrayList<String> list1 = DBJ.cordinatorSectionList(empid,schId,conn);
			leaveInfo=DBJ.SchoolTotalfeedbackByStatus(status,list1,schId,conn);
		}
		else
		{
			String empid=DBJ.employeeIdbyEmployeeName(username, schId,conn);
			ArrayList<String>list1=DBJ.allClassListForTeacher(schId,empid,conn);
			leaveInfo=DBJ.SchoolTotalfeedbackByStatus(status,list1,schId,conn);
		}
	
		if (leaveInfo.isEmpty()) {
			b = false;
		} else
			b = true;
	}

	public void pendingLeave()
	{
		show=true;show1=false;
		Connection conn= DataBaseConnection.javaConnection();

		list("pending", conn);
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

		list("approve", conn);
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

		list("cancel", conn);
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
			int i=DBJ.UpdateSchoolTotalfeedbackByStatus(String.valueOf(selectedItem.getId()),"approve",schId,selectedItem.getRemark(),conn);
			if(i==1)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				context.addMessage(null, new FacesMessage("Feedback Approved SuccessFully"));
				StudentInfo info=DBJ.studentDetailslistByAddNo(selectedItem.getAddNumber(),schId,conn);

				DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Responded", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schId,schId,"",conn);
				DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Responded", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schId,schId,"",conn);

				list("pending", conn);
			}
			else
			{
				context.addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
			}
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
		
		value = "Id-"+selectedItem.getId()+" --Remark-"+selectedItem.getRemark();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Feedback approve","WEB",value,conn);
		return refNo;
	}
	

	public void denyRequest()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();

		if(selectedItem!=null)
		{
			int i=DBJ.UpdateSchoolTotalfeedbackByStatus(String.valueOf(selectedItem.getId()),"cancel",schId,selectedItem.getRemark(),conn);
			if(i==1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				context.addMessage(null, new FacesMessage("Feedback Rejected SuccessFully"));
				StudentInfo info=DBJ.studentDetailslistByAddNo(selectedItem.getAddNumber(),schId,conn);

				DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Disapproved", info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schId,schId,"",conn);
				DBJ.notification("Concern/Feedback","Your Concern/Feedback Has Been Disapproved", info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schId,schId,"",conn);

				list("pending", conn);
			}
			else
			{
				context.addMessage(null, new FacesMessage("An Error Occured..... Try Again"));
			}
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
		
		value = "Id-"+selectedItem.getId()+" --Remark-"+selectedItem.getRemark();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Feedback Deny","WEB",value,conn);
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

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}
}
