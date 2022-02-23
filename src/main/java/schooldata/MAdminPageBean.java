package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="madminPageBean")
@ViewScoped

public class MAdminPageBean implements Serializable
{
	ArrayList<SelectItem> branchList = new ArrayList<>();

	public MAdminPageBean()
	{

	}

	public void blmsr()
	{
		Connection con = DataBaseConnection.javaConnection();
		String school = "251";
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schoolid", school);
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(con);
		
		String selectedSession = info.getDefaultSession();

		ss.setAttribute("financialYear", selectedSession);
		ss.setAttribute("selectedSession", selectedSession);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

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

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void blmbud()
	{
		Connection con = DataBaseConnection.javaConnection();
		String school = "252";
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("schoolid", school);
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(con);
		
		String selectedSession = info.getDefaultSession();

		ss.setAttribute("financialYear", selectedSession);
		ss.setAttribute("selectedSession", selectedSession);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

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

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void common()
	{
		Connection con = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String schid = (String) ss.getAttribute("schid");
		String branchid = DBM.branch_id(schid,con);
		branchList = new DataBaseMeathodJson().allBranchList(branchid, con);
		ss.setAttribute("branchList", branchList);
		ss.setAttribute("schoolid", "NA");

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try
		{
			ec.redirect("schoolMasterDashboard.xhtml");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		/*
		for(SelectItem in : branchList)
		{
			//// // System.out.println("Name : "+in.getLabel()+"........ ID : "+in.getValue());
		}*/

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
