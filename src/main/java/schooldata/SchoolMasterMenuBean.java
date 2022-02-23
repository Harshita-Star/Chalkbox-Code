package schooldata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="schoolMasterMenu")
@ViewScoped
public class SchoolMasterMenuBean implements Serializable{

	boolean checkuser=false;

	public SchoolMasterMenuBean() {

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String etype=(String) ss.getAttribute("etype");

		if(etype.equalsIgnoreCase("madmin"))
		{
			checkuser=true;
		}
		else
		{

			checkuser=false;
		}

	}


	public boolean isCheckuser() {
		return checkuser;
	}

	public void setCheckuser(boolean checkuser) {
		this.checkuser = checkuser;
	}




}

