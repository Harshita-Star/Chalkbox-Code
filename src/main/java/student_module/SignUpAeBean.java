package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="signUpAe")
@ViewScoped

public class SignUpAeBean implements Serializable
{
	String schid,name,mobile,email,unm,pwd,schnm,session;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseOnlineAdm dbo = new DataBaseOnlineAdm();

	public void signUp(String sid, String snm)
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid=sid;
		schnm=snm;
		//int rendomNumber1=(int)(Math.random()*9000)+1000;
		unm = schid + new SimpleDateFormat("yMdHms").format(new Date());
		int rendomNumber=(int)(Math.random()*9000)+1000;
		pwd = String.valueOf(rendomNumber);

		boolean checkMob = dbo.checkMobDuplicacy(mobile,"mobile",conn);
		if(checkMob==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"This Mobile No. is Already in Use. Please Try Again With Other One.", "Validation error"));
		}
		else
		{
			int i = dbo.signUp(schid,name,mobile,email,unm,pwd,conn);

			if(i>=1)
			{
				PrimeFaces.current().executeInitScript("PF('billDlg').show()");
				PrimeFaces.current().ajax().update("billForm");

				//String msg = "<center>"+"Your Login Id : <strong>"+unm+"</strong><br></br>"+"Password : <strong>"+pwd+"</strong><br></br><br></br>"+"<a href=\"http://chalkboxerp.in/DM/onlineAdmLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a></center> <br></br>"+"<center>Please Note Down Login Id and Password For Further Use.</center>";

				String msg = "<center>"+"Your Login Id : <strong>"+mobile+"</strong><br></br>"+"Password : <strong>"+pwd+"</strong><br></br>"+"Parent ID : <strong>"+unm+"</strong><br></br><br></br>"+"<a href=\"http://chalkboxerp.in/DM/onlineAdmLogin.xhtml\"> <img style=\"height: 44px;\" src=\"http://chalkboxerp.in/loginNowButton.png\"> </a></center> <br></br>"+"<center>Please Note Down Login Id and Password For Further Use.</center>"+"<br></br><center><strong>Note : While Filling Your 2nd Application From This Account, In 'Details of Legal Guardian', Select 'Existing Parent' and Write Your 'Parent ID' Mentioned Above(If you want to use your previous details).</strong></center>";
				String heading = "<center>Hello "+name+",</center>" + "<center class=\"red\">Congratulations on signing up to "+schnm+"!</center>";
				String subject = schnm+" Account Activated!";

				Runnable r = new Runnable()
				{
					public void run()
					{
						dbo.doMail(email, subject, heading, msg);
					}

				};
				new Thread(r).start();


				//new DataBaseMeathodJson().addWorkLog(email, msg, "Congrats on signing up to "+snm+"!");
				//name=schid=mobile=email=unm=pwd=schnm="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Oops! Something went wrong. Please Try Again", "Validation error"));
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUnm() {
		return unm;
	}

	public void setUnm(String unm) {
		this.unm = unm;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSchnm() {
		return schnm;
	}

	public void setSchnm(String schnm) {
		this.schnm = schnm;
	}
}
