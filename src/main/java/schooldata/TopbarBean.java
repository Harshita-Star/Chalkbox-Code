package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="topbar")
@ViewScoped

public class TopbarBean implements Serializable
{
	public void navigate()
	{
		HttpSession ss2=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userType=(String) ss2.getAttribute("mtype");
		Connection conn = DataBaseConnection.javaConnection();
		String schid=(String) ss2.getAttribute("schoolid");

		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		if(userType.equalsIgnoreCase("madmin") && schid.equals("251"))
		{
			try
			{
				ec.redirect("schoolMasterDashboard.xhtml");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(userType.equalsIgnoreCase("Teacher"))
		{
			try
			{
				ec.redirect("teacherHomePage.xhtml");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(userType.equalsIgnoreCase("Transport Manager"))
		{
			try
			{
				ec.redirect("dashboardTransport.xhtml");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(userType.equalsIgnoreCase("Accounts"))
		{
			try
			{
				ec.redirect("dashboardAccount.xhtml");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if (userType.equalsIgnoreCase("Security"))
		{
			try {
				ec.redirect("dashboardSecurity.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (userType.equalsIgnoreCase("Front Office"))
		{
			if(info.getBranch_id().equals("54"))
			{
				try {
					ec.redirect("dashboardFrontOfficeBlm.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				if(info.getClient_type().equalsIgnoreCase("school"))
				{
					try {
						ec.redirect("dashboardFrontOffice.xhtml");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					try {
						ec.redirect("dashboardFrontOfficeIns.xhtml");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
		else if (userType.equalsIgnoreCase("Librarian"))
		{
			try {
				ec.redirect("dashboardLibrary.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (userType.equalsIgnoreCase("Principal") || userType.equalsIgnoreCase("Vice Principal"))
		{
			try {
				ec.redirect("dashboardPrinciple.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				if(info.getType().equalsIgnoreCase("basic"))
				{
					ec.redirect("dashboardBasic.xhtml");
				}
				else if(info.getType().equalsIgnoreCase("novice"))
				{
					ec.redirect("Dashboard.xhtml");
				}
				else if(info.getType().equalsIgnoreCase("foster"))
				{
					ec.redirect("dashboardFoster.xhtml");
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	public void feeRedirect() throws IOException 
	{
		
		Connection conn=DataBaseConnection.javaConnection();

		
		if(new DatabaseMethods1().installmentCheck(new DatabaseMethods1().schoolId(), conn))
		{
			
	      FacesContext.getCurrentInstance().getExternalContext().redirect("installmentWiseFeeCollection.xhtml");
		
		}
		else
		{
	  		
	       FacesContext.getCurrentInstance().getExternalContext().redirect("feeCollection.xhtml");

							
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void changeSession() throws IOException 
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("sessionPage", "normal");
		
		FacesContext.getCurrentInstance().getExternalContext().redirect("selectSession.xhtml");
	}
}
