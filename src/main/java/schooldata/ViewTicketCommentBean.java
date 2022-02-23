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

@ManagedBean(name="viewTicketCommentBean")
@ViewScoped
public class ViewTicketCommentBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<TicketRaisingInfo> list = new ArrayList<>();
	String comment="";
	boolean show=false;
	TicketRaisingInfo selected = new TicketRaisingInfo();
	public ViewTicketCommentBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selected = (TicketRaisingInfo) ss.getAttribute("selectedTicket");
		if(selected.getStatus().equalsIgnoreCase("close"))
		{
			show=false;
		}
		else
		{
			show=true;
		}

		showData();
	}

	public void showData()
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selected = (TicketRaisingInfo) ss.getAttribute("selectedTicket");
		String schid = selected.getSchid();
		list = DBM.allticketCommentList(selected.getId(), schid, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addRemark()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selected = (TicketRaisingInfo) ss.getAttribute("selectedTicket");
		String userid=(String) ss.getAttribute("username");
		String usertype = (String) ss.getAttribute("type");
		String schid = DBM.schoolId();
		String id = selected.getId();
		int i=DBJ.updateTicketRaisingRemark(userid,schid,id,comment,usertype,conn);

		if(i>=1)
		{
			if(selected.getStatus().equalsIgnoreCase("Resolved") || selected.getStatus().equalsIgnoreCase("Wrapping Up"))
			{
				DBM.updateStatusForticket(id,"Wrapping Up",conn);
			}
			else
			{
				DBM.updateStatusForticket(id,"pending",conn);
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Comment Added"));
			showData();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong! Please Try Again."));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}

	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public TicketRaisingInfo getSelected() {
		return selected;
	}

	public void setSelected(TicketRaisingInfo selected) {
		this.selected = selected;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
