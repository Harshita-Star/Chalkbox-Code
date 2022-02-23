package schooldata;

import java.io.IOException;
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

@ManagedBean(name="viewUserTicketBean")
@ViewScoped

public class ViewUserTicketBean implements Serializable
{
	ArrayList<TicketRaisingInfo> list=new ArrayList<>();
	String path="";
	TicketRaisingInfo selectedTicket = new TicketRaisingInfo();
	int resolved,not_app,pending,in_process,all,wrapped,closed;
	public ViewUserTicketBean()
	{
		searchData();
	}
	String userid, userType,schoolId;
	public void searchData()
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userid = (String) httpSession.getAttribute("username");
		userType = (String) httpSession.getAttribute("type");
		schoolId = DBM.schoolId();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"",conn);
		resolved=DBJ.tickectCounting(userid,schoolId,userType,"resolved",conn);
		in_process=DBJ.tickectCounting(userid,schoolId,userType,"In Process",conn);
		not_app=DBJ.tickectCounting(userid,schoolId,userType,"Not Appropriate",conn);
		pending=DBJ.tickectCounting(userid,schoolId,userType,"pending",conn);
		wrapped=DBJ.tickectCounting(userid,schoolId,userType,"Wrapping Up",conn);
		closed=DBJ.tickectCounting(userid,schoolId,userType,"close",conn);
		all=resolved+in_process+not_app+pending+wrapped+closed;


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reportPending()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"pending",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reportresolved()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"resolved",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void reportInprocess()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"In Process",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void reportnotapp()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"Not Appropriate",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void reportWrapped()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"Wrapping Up",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void reportClosed()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"close",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void reportAll()
	{
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		Connection conn = DataBaseConnection.javaConnection();

		list=DBJ.allUserWiseTicketRaisedReport(userid,schoolId,userType,"",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String deleteticket()
	{

		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		int i=DBM.deleteTicket(selectedTicket.getId(),conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ticket Deleted"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "viewUserTicket.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Comment Added"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}



	}


	public void viewDetail() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedTicket", selectedTicket);

		FacesContext.getCurrentInstance().getExternalContext().redirect("viewTicketComment.xhtml");
	}

	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}

	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public TicketRaisingInfo getSelectedTicket() {
		return selectedTicket;
	}

	public void setSelectedTicket(TicketRaisingInfo selectedTicket) {
		this.selectedTicket = selectedTicket;
	}

	public int getResolved() {
		return resolved;
	}

	public void setResolved(int resolved) {
		this.resolved = resolved;
	}

	public int getNot_app() {
		return not_app;
	}

	public void setNot_app(int not_app) {
		this.not_app = not_app;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getIn_process() {
		return in_process;
	}

	public void setIn_process(int in_process) {
		this.in_process = in_process;
	}

	public int getAll() {
		return all;
	}

	public void setAll(int all) {
		this.all = all;
	}

	public int getWrapped() {
		return wrapped;
	}

	public void setWrapped(int wrapped) {
		this.wrapped = wrapped;
	}

	public int getClosed() {
		return closed;
	}

	public void setClosed(int closed) {
		this.closed = closed;
	}


}
