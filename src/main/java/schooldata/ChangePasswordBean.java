package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="changePassword")
@ViewScoped
public class ChangePasswordBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String username,password,oldPassword,newpassword,conformPassword;
	public ChangePasswordBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
	}

	public void changePassword()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		String userType=(String) ss.getAttribute("type");
		String service=(String) ss.getAttribute("serviceExecutive");
		if(conformPassword.equals(newpassword))
		{
			int i = 0;
			if(service.equalsIgnoreCase("yes"))
			{
				i=new DatabaseMethods1().passwordUpdateService(username,newpassword,oldPassword,conn,"service_executive");
			}
			else
			{
				i=new DatabaseMethods1().passwordUpdate(username,newpassword,oldPassword,conn,userType);
			}
			
			if(i==1)
			{
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Password updated successfully"));
				oldPassword=null;
				newpassword=null;
				conformPassword=null;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong password found,try again", "Validation error"));
				oldPassword=null;
				newpassword=null;
				conformPassword=null;
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"New password and confirm password not matched", "Validation error"));
			oldPassword=null;
			newpassword=null;
			conformPassword=null;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void stuChangePassword()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		ss.getAttribute("type");
		if(conformPassword.equals(newpassword))
		{
			int i=new DatabaseMethods1().passwordUpdateStudent(username,newpassword,oldPassword,conn);
			if(i>=1)
			{
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Password updated successfully"));
				oldPassword=null;
				newpassword=null;
				conformPassword=null;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Wrong old password ,try again", "Validation error"));
				oldPassword=null;
				newpassword=null;
				conformPassword=null;
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"New password and confirm password not matched", "Validation error"));
			oldPassword=null;
			newpassword=null;
			conformPassword=null;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getOldPassword()
	{
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	public String getConformPassword() {
		return conformPassword;
	}
	public void setConformPassword(String conformPassword) {
		this.conformPassword = conformPassword;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
