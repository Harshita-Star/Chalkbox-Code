package student_module;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="homeOnlineAdm")
@ViewScoped

public class HomeOnlineAdmBean implements Serializable
{
	LoginInfo info = new LoginInfo();
	public HomeOnlineAdmBean()
	{
		//Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		info=(LoginInfo) ss.getAttribute("signInfo");
	}
	public LoginInfo getInfo() {
		return info;
	}
	public void setInfo(LoginInfo info) {
		this.info = info;
	}
}
