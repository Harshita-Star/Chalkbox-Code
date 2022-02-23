package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "viewEditDeleteHealthCheckUp")
@ViewScoped
public class ViewEditDeleteHealthCheckUpBean implements Serializable {

	ArrayList<HealthCheckUpInfo> healthList = new ArrayList<>();
	HealthCheckUpInfo selectedCheckUp;
	DataBaseMethodsHostelModule obj = new DataBaseMethodsHostelModule();
	Date startDate,endDate;


	public ViewEditDeleteHealthCheckUpBean()
	{
		startDate=new Date();
		startDate.setMonth(startDate.getMonth()-1);
		endDate=new Date();
		search();
	}

	public void search()
	{
		String strStart = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		String strEnd = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
		Connection con = DataBaseConnection.javaConnection();
		healthList = obj.viewAllHealthCheckUpDetails(strStart,strEnd,con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void deleteCheckUp() {
		Connection conn=DataBaseConnection.javaConnection();

		int k=obj.deleteCheckUp(conn,selectedCheckUp.getId());
		if(k>0)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted sucessfully"));
			String strStart = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
			String strEnd = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			healthList = obj.viewAllHealthCheckUpDetails(strStart,strEnd,conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error"));
		}



		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String language = "";
		String value= "Checkup Id-"+selectedCheckUp.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Checkup","WEB",value,conn);
		return refNo;
	}


	public String viewHealthCheckUp() {

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedCheckUp", selectedCheckUp);

		return "viewHealthCheckUp.xhtml";

	}

	public String editHealthCheckUp() {

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedCheckUp", selectedCheckUp);

		return "editHealthCheckUp.xhtml";

	}

	public HealthCheckUpInfo getSelectedCheckUp() {
		return selectedCheckUp;
	}

	public void setSelectedCheckUp(HealthCheckUpInfo selectedCheckUp) {
		this.selectedCheckUp = selectedCheckUp;
	}

	public ArrayList<HealthCheckUpInfo> getHealthList() {
		return healthList;
	}

	public void setHealthList(ArrayList<HealthCheckUpInfo> healthList) {
		this.healthList = healthList;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
