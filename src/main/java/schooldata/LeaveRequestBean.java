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

import session_work.RegexPattern;

@ManagedBean(name="leaveRequest")
@ViewScoped
public class LeaveRequestBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<EmployeInfo> leaveInfo;
	EmployeInfo selectedItem;
	String description,userName,type,empId;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public LeaveRequestBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userName=(String) ss.getAttribute("username");
		EmployeInfo ll=obj.employeeInfoByUserName(userName,conn);
		empId=ll.getId();
		type=(String) ss.getAttribute("type");
		leaveInfo=obj.allPendingLeave(conn);
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

		if(empId==null)
		{
			int i=obj.approveLeave(selectedItem.getId(),selectedItem.getApproveComment(),empId,conn);
			if(i==1)
			{
				context.addMessage(null, new FacesMessage("Leave Allowed SuccessFully"));
				leaveInfo=obj.allPendingLeave(conn);
			}
			else if(i==-1)
			{
				context.addMessage(null, new FacesMessage("This leave is allowed by somebody else..... Please refresh your page "));
			}
			else
			{
				context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
			}
		}
		else
		{
			if(!empId.equalsIgnoreCase(selectedItem.getEmpId()))
			{
				int i=obj.approveLeave(selectedItem.getId(),selectedItem.getApproveComment(),empId,conn);
				if(i==1)
				{
					context.addMessage(null, new FacesMessage("Leave Allowed SuccessFully"));
					leaveInfo=obj.allPendingLeave(conn);
				}
				else if(i==-1)
				{
					context.addMessage(null, new FacesMessage("This leave is allowed by somebody else..... Please refresh your page "));
				}
				else
				{
					context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
				}
			}
			else
			{
				context.addMessage(null, new FacesMessage("You Are Not Allowed To Approve This Leave..."));
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void denyRequest()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();

		if(empId==null)
		{
			int i=obj.denyLeave(selectedItem.getId(),selectedItem.getApproveComment(),empId,conn);
			if(i==1)
			{
				context.addMessage(null, new FacesMessage("Leave Denied SuccessFully"));
				leaveInfo=obj.allPendingLeave(conn);
			}
			else
			{
				context.addMessage(null, new FacesMessage("An Error Occured..... Try Again"));
			}

		}
		else
		{
			if(!empId.equalsIgnoreCase(selectedItem.getEmpId()))
			{
				int i=obj.denyLeave(selectedItem.getId(),selectedItem.getApproveComment(),empId,conn);
				if(i==1)
				{
					context.addMessage(null, new FacesMessage("Leave Denied SuccessFully"));
					leaveInfo=obj.allPendingLeave(conn);
				}
				else
				{
					context.addMessage(null, new FacesMessage("An Error Occured..... Try Again"));
				}
			}
			else
			{
				context.addMessage(null, new FacesMessage("You Are Not Allowed To Deny This Leave..."));
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<EmployeInfo> getLeaveInfo() {
		return leaveInfo;
	}
	public void setLeaveInfo(ArrayList<EmployeInfo> leaveInfo) {
		this.leaveInfo = leaveInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EmployeInfo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(EmployeInfo selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
