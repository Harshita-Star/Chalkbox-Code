package reminder_module;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;

@ManagedBean(name="login")
@ViewScoped
public class LoginBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String userId,password,type;
	ArrayList<TaskInfo> list = new ArrayList<>();

	public String loginUser() throws IOException
	{
		DataBaseMethodsReminder objReminder=new DataBaseMethodsReminder();
		if(userId.equalsIgnoreCase("img") && password.equalsIgnoreCase("img"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

			String schoolid="3",username="demo";

			objReminder.checkOneTimeReminders(conn,schoolid);
			objReminder.checkRecurringReminders(conn,schoolid);

			//			list = new DataBaseMethodsReminder().taskReminderList(conn,username,schoolid);
			//
			//			ss.setAttribute("reminderList", list);
			ss.setAttribute("username", username);
			ss.setAttribute("schoolId", schoolid);

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			ec.redirect("addTask.xhtml");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong Username Or Password", "Validation error"));
			return "loginPage";
		}
		return "";
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
