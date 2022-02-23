package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="masterSelectSession")
@SessionScoped

public class MasterSelectSessionBean implements Serializable
{
	ArrayList<SelectItem> sessionList;
	String selectedSession;

	@PostConstruct
	public void init()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//int start=new DatabaseMethods1().schoolStartingSession(conn);
		////// // System.out.println("start : "+start);
		sessionList=new ArrayList<>();
		for(int i=2017;i<=2050;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		if(month>=4)
		{
			selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public SelectSessionBean()
	{


	}*/
	public void goToNext()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 db=new DatabaseMethods1();

		//ArrayList<String>message=db.checkmessageSetting(conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		ArrayList<SelectItem> branchList = (ArrayList<SelectItem>) httpSession.getAttribute("branchList");

		for(SelectItem in : branchList)
		{
			db.copyClassFromPreviousSessionSchid(String.valueOf(in.getValue()),conn,selectedSession);
			db.copyTransportRouteFromPreviousSessionSchid(String.valueOf(in.getValue()),conn,selectedSession);
		}


		httpSession.setAttribute("selectedSession", selectedSession);
		/*httpSession.setAttribute("feesubmit", message.get(0));
		httpSession.setAttribute("registration", message.get(1));
		httpSession.setAttribute("enquiry", message.get(2));
		httpSession.setAttribute("attendance", message.get(3));*/


		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("schoolMasterDashboard.xhtml");
			conn.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		//	return "Dashboard.xhtml";
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}

}
