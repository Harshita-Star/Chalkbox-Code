package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="editHealthCheckUp")
@ViewScoped

public class EditHealthCheckUpBean implements Serializable{

	String regex=RegexPattern.REGEX;
	HealthCheckUpInfo selectedCheckUp;
	private ArrayList<SelectItem> diseaseList;
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();

	public EditHealthCheckUpBean() {
		Connection con=DataBaseConnection.javaConnection();
		diseaseList=obj.viewAllDisease(con);

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedCheckUp=(HealthCheckUpInfo) ss.getAttribute("selectedCheckUp");
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String submit() {

		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.updateCheckUp(selectedCheckUp,conn);
		if(i>0)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check Up Updated Sucessfully."));
			return "viewEditDeleteHealthCheckUp.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
        value = selectedCheckUp.toString(); 		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Checkup","WEB",value,conn);
		return refNo;
	}


	public HealthCheckUpInfo getSelectedCheckUp() {
		return selectedCheckUp;
	}

	public void setSelectedCheckUp(HealthCheckUpInfo selectedCheckUp) {
		this.selectedCheckUp = selectedCheckUp;
	}


	public ArrayList<SelectItem> getDiseaseList() {
		return diseaseList;
	}


	public void setDiseaseList(ArrayList<SelectItem> diseaseList) {
		this.diseaseList = diseaseList;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}


