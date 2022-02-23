package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="onlineRegApplicationAe")
@ViewScoped

public class OnlineRegApplicationAEBean implements Serializable
{
	ArrayList<OnlineAdmInfo> list = new ArrayList<>();
	OnlineAdmInfo selected = new OnlineAdmInfo();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm DBO = new DataBaseOnlineAdm();
	LoginInfo linfo = new LoginInfo();
	String remark = "",schoolId,session;

	public OnlineRegApplicationAEBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = DBM.schoolId(); 
		session = DBM.selectedSessionDetails(schoolId,conn);
		String unm = "all reg";
		list = DBO.myApplication(unm,"",conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetail()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enq_id", selected.getId());
		if(selected.getStatus().equalsIgnoreCase("pending") ||
				selected.getStatus().equalsIgnoreCase("application accepted") ||
				selected.getStatus().equalsIgnoreCase("application rejected"))
		{
			ss.setAttribute("heading", "Registration Form");
			PrimeFaces.current().executeInitScript("window.open('viewRegApplication.xhtml')");
		}
		else
		{
			ss.setAttribute("heading", "Admission Form");
			PrimeFaces.current().executeInitScript("window.open('viewOnlineApplication.xhtml')");
		}

		/*try {

			FacesContext.getCurrentInstance().getExternalContext().redirect("viewOnlineApplication.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public void accept()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DBO.updateAdmStatus("Application Accepted", "Admission Form Pending", selected.getId(), conn);
		DBO.updateAdmRemark(remark, selected.getId(), conn);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Application Accepted Successfully! Sent to Applicant for Filling Admssion Form."));
		String unm = "all reg";
		list = DBO.myApplication(unm,"",conn);

		LoginInfo ll= DBO.signUpInfo(selected.getLogin_id(), conn);
		String schnm = DBM.schoolNameById(schoolId, conn);
		String msg = "<center>"+remark+"<br></br>"+"<strong>Please login to your account, go to 'My Applications' under 'For Applicant' section and fill the admission form.</strong><br></br>"+"<a href=\"http://chalkboxerp.in/DM/onlineAdmLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a> <br></br></center>";
		String heading = "<center>Hello "+selected.getSt_name()+",</center>" + "<center class=\"red\">Congratulations, your registration application in  "+schnm+" has been accepted!</center>";
		String subject = schnm+" Registration Application Accepted!";

		Runnable r = new Runnable()
		{
			public void run()
			{

				new DataBaseOnlineAdm().doMail(ll.getEmail(), subject, heading, msg);
			}

		};
		new Thread(r).start();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void reject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DBO.updateAdmStatus("Application Rejected", "Application Rejected", selected.getId(), conn);
		DBO.updateAdmRemark(remark, selected.getId(), conn);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Application Rejected Successfully!"));
		String unm = "all reg";
		list = DBO.myApplication(unm,"",conn);

		LoginInfo ll= DBO.signUpInfo(selected.getLogin_id(), conn);
		String schnm = DBM.schoolNameById(schoolId, conn);
		String msg = "<center>"+remark+"<br></br>"+"<strong>You can check your application from your account. Please login to your account, go to 'My Applications' under 'For Applicant' section.</strong><br></br>"+"<a href=\"http://chalkboxerp.in/DM/onlineAdmLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a> <br></br></center>";
		String heading = "<center>Hello "+selected.getSt_name()+",</center>" + "<center class=\"red\">Sorry, your registration application in  "+schnm+" has been rejected!</center>";
		String subject = schnm + " Registration Application Rejected!";

		Runnable r = new Runnable()
		{
			public void run()
			{

				new DataBaseOnlineAdm().doMail(ll.getEmail(), subject, heading, msg);
			}

		};
		new Thread(r).start();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<OnlineAdmInfo> getList() {
		return list;
	}

	public void setList(ArrayList<OnlineAdmInfo> list) {
		this.list = list;
	}

	public OnlineAdmInfo getSelected() {
		return selected;
	}

	public void setSelected(OnlineAdmInfo selected) {
		this.selected = selected;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
