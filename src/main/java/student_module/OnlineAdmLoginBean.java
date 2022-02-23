package student_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;;

@ManagedBean(name="onlineAdmLoginBean")
@ViewScoped

public class OnlineAdmLoginBean implements Serializable
{
	String id,pswd;
	LoginInfo uinfo;

	public OnlineAdmLoginBean() throws ParseException {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.invalidate();

		/*
		 * String path = "/home/chalkboxerp/public_html/DEMOSCHOOL/Sub-Sub4"; new
		 * File(path).mkdirs(); final File file = new File(path); file.setReadable(true,
		 * false); file.setExecutable(true, false); file.setWritable(true, false);
		 */
	}

	public void check()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
		uinfo = DBO.signUpInfo(id,pswd,conn);
		if(uinfo!=null)
		{
			String selectedSession;
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			if (month >= 4) {
				selectedSession = String.valueOf(year) + "-"
						+ String.valueOf(year + 1);
			} else {
				selectedSession = String.valueOf(year - 1) + "-"
						+ String.valueOf(year);
			}

			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(true);
			ss.setAttribute("showSMSBalMessage", "yes");
			ss.setAttribute("showDueBillMessage", "yes");
			ss.setAttribute("schoolid", uinfo.getSchid());
			ss.setAttribute("schid", uinfo.getSchid());
			ss.setAttribute("username", id);
			ss.setAttribute("type", "parent");
			ss.setAttribute("mtype", "parent");
			ss.setAttribute("masterAdmin", "no");
			ss.setAttribute("financialYear", selectedSession);

			//			ArrayList<String> message = DBM.checkmessageSetting(conn);
			//			ArrayList<String>template=new DatabaseMethods1().checktemplateSetting(conn);
			ss.setAttribute("selectedSession", selectedSession);
			ss.setAttribute("feesubmit", "false");
			ss.setAttribute("registration", "false");
			ss.setAttribute("enquiry", "false");
			ss.setAttribute("attendance", "false");
			ss.setAttribute("complaint", "false");
			ss.setAttribute("birthday", "false");
			ss.setAttribute("eventNotify", "false");
			ss.setAttribute("marksheetNotify", "false");
			ss.setAttribute("resultNotify", "false");
			ss.setAttribute("elearningNotify", "false");

			ss.setAttribute("feeMsg", "false");
			ss.setAttribute("regMsg", "false");
			ss.setAttribute("enqMsg", "false");
			ss.setAttribute("attMsg", "false");
			ss.setAttribute("compMsg", "false");
			ss.setAttribute("birthMsg", "false");
			ss.setAttribute("signInfo", uinfo);


			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			try {
				ec.redirect("homeOnlineAdm.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Login Id Or Password Is Wrong", "Validation error"));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}


}
