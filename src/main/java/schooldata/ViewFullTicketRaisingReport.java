package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
@ManagedBean(name="viewFullTicketRaisingReport")
@ViewScoped
public class ViewFullTicketRaisingReport implements Serializable
{
	String selectedType;
	boolean check;
	TicketRaisingInfo selectedTicket;
	Date startingDate=new Date(),endDate=new Date();
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();
	public ViewFullTicketRaisingReport()
	{
		startingDate.setDate(startingDate.getDate()-7);
		endDate=new Date();
		selectedType="error";
		search();
	}
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allTicketRaisedReport(selectedType,startingDate,endDate,conn);
		if(list.size()>0)
		{
			check=true;

		}
		else
		{
			check=false;

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void viewDetail()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedTicket", selectedTicket);

		try {
			ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();

			cc.redirect("ticketCommenting.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public String getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public TicketRaisingInfo getSelectedTicket() {
		return selectedTicket;
	}
	public void setSelectedTicket(TicketRaisingInfo selectedTicket) {
		this.selectedTicket = selectedTicket;
	}


}
