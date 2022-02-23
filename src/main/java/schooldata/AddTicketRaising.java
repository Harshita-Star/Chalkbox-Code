package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;

@ManagedBean(name = "addTicketRaising")
@ViewScoped
public class AddTicketRaising implements Serializable {
	
	String regex=RegexPattern.REGEX;
	Date ticketDate = new Date();
	String schoolName, type, description, userName;
	int ticketId;
	ArrayList<SelectItem> schoolList = new ArrayList<>();

	public AddTicketRaising() {
		Connection conn = DataBaseConnection.javaConnection();
		ticketDate = new Date();

		schoolList = new DatabaseMethods1().getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submit() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		if (userName == null || userName.equalsIgnoreCase("")) {
			userName = obj.adminUserName(schoolName, conn);
		}

		ticketId = obj.obtainTicketId(conn);
		int i = obj.addTicketRaisingWeb(ticketDate, ticketId, schoolName, type, description, userName, conn);
		if (i >= 1) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ticket Added Successfullly"));
			obj.updateTicketId(ticketId, conn);
			// ticketId=new DatabaseMethods1().obtainTicketId(conn);
			schoolName = "";
			type = "";
			description = "";
			userName = "";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getTicketDate() {
		return ticketDate;
	}

	public void setTicketDate(Date ticketDate) {
		this.ticketDate = ticketDate;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
