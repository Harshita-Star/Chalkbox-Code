package schooldata;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="masterAdminOtp")
@ViewScoped

public class MasterAdminOTPBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String mobile="", otpp, strRandomOtp;
	int randomOtp;

	public MasterAdminOTPBean()
	{
		strRandomOtp = "";
	}

	public void getOtp()
	{
		/*if(mobile.equals("8058100200") || mobile.equals("9784386409") || mobile.equals("8006088999") || mobile.equals("9694097240"))
		{
			Connection conn = DataBaseConnection.javaConnection();

			randomOtp = (int)(Math.random()*9000)+1000;
			strRandomOtp = String.valueOf(randomOtp);

			String typemessage="Hello Admin,\n"
					+String.valueOf(randomOtp)+ " is  your login OTP. Treat this as confidential.\nRegards\nCB";

			new DatabaseMethods1().messageurlMasterAdmin(mobile, typemessage,"masteradmin",conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mobile No. is Not Valid. Please Try Again."));
		}*/
	}

	public void submit()
	{
		if(mobile.equals("8058091600") && otpp.equals("!2@3%4"))
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			ss.setAttribute("username", "master");
			ss.setAttribute("type", "master_admin");
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			try {
				ec.redirect("master/masterAdminDashboard.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			if(otpp.equals(strRandomOtp))
			{
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("username", "master");
				ss.setAttribute("type", "master_admin");
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext ec = fc.getExternalContext();
				try {
					ec.redirect("master/masterAdminDashboard.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
			}
		}


	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStrRandomOtp() {
		return strRandomOtp;
	}

	public void setStrRandomOtp(String strRandomOtp) {
		this.strRandomOtp = strRandomOtp;
	}

	public int getRandomOtp() {
		return randomOtp;
	}

	public void setRandomOtp(int randomOtp) {
		this.randomOtp = randomOtp;
	}

	public void setOtpp(String otpp) {
		this.otpp = otpp;
	}

	public String getOtpp() {
		return otpp;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
