package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="selectSession")
@SessionScoped
public class SelectSessionBean implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> sessionList;
	String selectedSession,runningSession;

	@PostConstruct
	public void init()
	{
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String page = (String) httpSession.getAttribute("sessionPage");
		Connection conn=DataBaseConnection.javaConnection();
		int start=new DatabaseMethods1().schoolStartingSession(new DatabaseMethods1().schoolId(),conn);
		runningSession = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		////("start : "+start);
		sessionList=new ArrayList<>();

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		for(int i=start;i<=year+1;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}



		if(month>=4)
		{
			selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
		}
		
		if(page.equalsIgnoreCase("setDefaultSession"))
		{
			selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public SelectSessionBean()
	{


	}*/
	public String goToNext()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 db=new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();

		int startYear = Integer.valueOf(selectedSession.split("-")[0]);
		int startYearRunnig = Integer.valueOf(runningSession.split("-")[0]);
		int endYear = Integer.valueOf(selectedSession.split("-")[1]);
		int endYearRunning = Integer.valueOf(runningSession.split("-")[1]);

		
		if(endYear > endYearRunning)
		{
			
			Date expireDate = db.checkExpiryDate(db.schoolId(), conn);
			String strDate = new SimpleDateFormat("dd-MM-yyyy").format(expireDate);
			Date newSessionStart = new Date();
			newSessionStart.setDate(1);
			newSessionStart.setMonth(3);
			newSessionStart.setYear(startYear-1900);

			Date newSessionEnd = new Date();
			newSessionEnd.setDate(31);
			newSessionEnd.setMonth(2);
			newSessionEnd.setYear(endYear-1900);

			Timestamp expDate = new Timestamp(expireDate.getTime());
			expDate.setHours(0);expDate.setMinutes(0);expDate.setSeconds(0);expDate.setNanos(0);

			Timestamp startDate = new Timestamp(newSessionStart.getTime());
			startDate.setHours(0);startDate.setMinutes(0);startDate.setSeconds(0);startDate.setNanos(0);

			Timestamp endDate = new Timestamp(newSessionEnd.getTime());
			endDate.setHours(0);endDate.setMinutes(0);endDate.setSeconds(0);endDate.setNanos(0);

			//(expDate);
			//(startDate);
			//(endDate);
			//(!(((expDate.after(startDate) || expDate.equals(startDate)) && (expDate.before(endDate) || expDate.equals(endDate))) || (expDate.after(endDate))));
			if(!(((expDate.after(startDate) || expDate.equals(startDate)) && (expDate.before(endDate) || expDate.equals(endDate))) || (expDate.after(endDate))))
			{
				if((startYear-startYearRunnig)<=1)
				{
					Date licenceDate = db.checkTempLicenceDate(db.schoolId(),selectedSession, conn);
					if(licenceDate==null)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

						return "";
					}
					else
					{
						//String strLicDate = new SimpleDateFormat("dd-MM-yyyy").format(licenceDate);
						Timestamp licDate = new Timestamp(licenceDate.getTime());
						licDate.setHours(0);licDate.setMinutes(0);licDate.setSeconds(0);licDate.setNanos(0);
						Timestamp tdyDate = new Timestamp(new Date().getTime());
						tdyDate.setHours(0);tdyDate.setMinutes(0);tdyDate.setSeconds(0);tdyDate.setNanos(0);
						if(!(tdyDate.before(licDate) || tdyDate.equals(licDate)))
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

							return "";
						}

					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

					return "";
				}




			}

		}

		SchoolInfoList info = db.fullSchoolInfo(conn);


		ArrayList<String>message=db.checkmessageSetting(conn);


		if(endYear > endYearRunning)
		{
			String schid=db.schoolId();
			int check=db.checkTableInCopiedData("class", schid, selectedSession, conn);
			if(check==0)
			{
				db.copyClassFromPreviousSessionSchid(schid,conn,selectedSession);
			}
			check=db.checkTableInCopiedData("section", schid, selectedSession, conn);
			if(check==0)
			{
				dbr.copySectionsFromPreviousSession(schid,selectedSession,conn);
			}
			check=db.checkTableInCopiedData("subjects", schid, selectedSession, conn);
			if(check==0)
			{
				db.copySubjectsFromPreviousSessionSchid(schid,conn,selectedSession);
			}
			check=db.checkTableInCopiedData("transportstop", schid, selectedSession, conn);
			if(check==0)
			{
				db.copyTransportRouteFromPreviousSessionSchid(schid,conn,selectedSession);
			}
			check=db.checkTableInCopiedData("exam_setting", schid, selectedSession, conn);
			if(check==0)
			{
				db.copyExamSettingsFromPreviousSessionSettings(schid,conn,selectedSession);
			}
			
			
			ArrayList<Transport> rlist = dbr.transportWaivedList(selectedSession,schid,conn);
			if(rlist.size()<=0)
			{
				ArrayList<Transport> list = dbr.transportWaivedList(runningSession,schid,conn);
				if(list.size()>0)
				{
					check=db.checkTableInCopiedData("transport_waive", schid, selectedSession, conn);
					if(check==0)
					{
						dbr.copyTransportWaiveEntry(list,schid,selectedSession,conn);
					}
				}
				else
				{
					ArrayList<Transport> nlist = new ArrayList<>();
					for(int i=1;i<=12;i++)
					{
						Transport tt = new Transport();
						tt.setMonth(String.valueOf(i));
						tt.setStatus("Yes");
						nlist.add(tt);
					}

					dbr.copyTransportWaiveEntry(nlist,db.schoolId(),selectedSession,conn);
				}
			}
		}

		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		httpSession.setAttribute("selectedSession", selectedSession);
		httpSession.setAttribute("feesubmit", message.get(0));
		httpSession.setAttribute("registration", message.get(1));
		httpSession.setAttribute("enquiry", message.get(2));
		httpSession.setAttribute("attendance", message.get(3));

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		try
		{
			String userType=(String) httpSession.getAttribute("mtype");

			if(userType.equalsIgnoreCase("Teacher"))
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
			else if(userType.equalsIgnoreCase("madmin"))
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

			//ec.redirect("Dashboard.xhtml");
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//	return "Dashboard.xhtml";

		return "";
	}
	
	public String defaultSession()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 db=new DatabaseMethods1();
		DataBaseMethodsReports dbr = new DataBaseMethodsReports();

		int startYear = Integer.valueOf(selectedSession.split("-")[0]);
		int startYearRunnig = Integer.valueOf(runningSession.split("-")[0]);
		int endYear = Integer.valueOf(selectedSession.split("-")[1]);
		int endYearRunning = Integer.valueOf(runningSession.split("-")[1]);

		if(endYear > endYearRunning)
		{
			Date expireDate = db.checkExpiryDate(db.schoolId(), conn);
			String strDate = new SimpleDateFormat("dd-MM-yyyy").format(expireDate);
			Date newSessionStart = new Date();
			newSessionStart.setDate(1);
			newSessionStart.setMonth(3);
			newSessionStart.setYear(startYear-1900);

			Date newSessionEnd = new Date();
			newSessionEnd.setDate(31);
			newSessionEnd.setMonth(2);
			newSessionEnd.setYear(endYear-1900);

			Timestamp expDate = new Timestamp(expireDate.getTime());
			expDate.setHours(0);expDate.setMinutes(0);expDate.setSeconds(0);expDate.setNanos(0);

			Timestamp startDate = new Timestamp(newSessionStart.getTime());
			startDate.setHours(0);startDate.setMinutes(0);startDate.setSeconds(0);startDate.setNanos(0);

			Timestamp endDate = new Timestamp(newSessionEnd.getTime());
			endDate.setHours(0);endDate.setMinutes(0);endDate.setSeconds(0);endDate.setNanos(0);

			//(expDate);
			//(startDate);
			//(endDate);
			//(!(((expDate.after(startDate) || expDate.equals(startDate)) && (expDate.before(endDate) || expDate.equals(endDate))) || (expDate.after(endDate))));
			if(!(((expDate.after(startDate) || expDate.equals(startDate)) && (expDate.before(endDate) || expDate.equals(endDate))) || (expDate.after(endDate))))
			{
				if((startYear-startYearRunnig)<=1)
				{
					Date licenceDate = db.checkTempLicenceDate(db.schoolId(),selectedSession, conn);
					if(licenceDate==null)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

						return "";
					}
					else
					{
						//String strLicDate = new SimpleDateFormat("dd-MM-yyyy").format(licenceDate);
						Timestamp licDate = new Timestamp(licenceDate.getTime());
						licDate.setHours(0);licDate.setMinutes(0);licDate.setSeconds(0);licDate.setNanos(0);
						Timestamp tdyDate = new Timestamp(new Date().getTime());
						tdyDate.setHours(0);tdyDate.setMinutes(0);tdyDate.setSeconds(0);tdyDate.setNanos(0);
						if(!(tdyDate.before(licDate) || tdyDate.equals(licDate)))
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

							return "";
						}

					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Dear User,\nYour licence is valid upto "+strDate+". Please renew your licence for the next session for uninterrupted access.","Dear User,\\nYour licence is valid upto \"+strDate+\". Please renew your licence for the next session for uninterrupted access."));

					return "";
				}




			}

		}

		SchoolInfoList info = db.fullSchoolInfo(conn);


		ArrayList<String>message=db.checkmessageSetting(conn);


		if(endYear > endYearRunning)
		{
			System.out.println("FOR CHANGING DEFAULT SESSION");
			db.copyClassFromPreviousSessionSchid(db.schoolId(),conn,selectedSession);
			dbr.copySectionsFromPreviousSession(db.schoolId(),selectedSession,conn);
			db.copySubjectsFromPreviousSessionSchid(db.schoolId(),conn,selectedSession);
			db.copyTransportRouteFromPreviousSessionSchid(db.schoolId(),conn,selectedSession);
			db.copyExamSettingsFromPreviousSessionSettings(db.schoolId(),conn,selectedSession);
			
			ArrayList<Transport> rlist = dbr.transportWaivedList(selectedSession,db.schoolId(),conn);
			if(rlist.size()<=0)
			{
				ArrayList<Transport> list = dbr.transportWaivedList(runningSession,db.schoolId(),conn);
				if(list.size()>0)
				{
					dbr.copyTransportWaiveEntry(list,db.schoolId(),selectedSession,conn);
				}
				else
				{
					ArrayList<Transport> nlist = new ArrayList<>();
					for(int i=1;i<=12;i++)
					{
						Transport tt = new Transport();
						tt.setMonth(String.valueOf(i));
						tt.setStatus("Yes");
						nlist.add(tt);
					}

					dbr.copyTransportWaiveEntry(nlist,db.schoolId(),selectedSession,conn);
				}
			}
			
			dbr.changeDefaultSession(selectedSession,db.schoolId(),conn);
		}

		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		httpSession.setAttribute("selectedSession", selectedSession);
		httpSession.setAttribute("feesubmit", message.get(0));
		httpSession.setAttribute("registration", message.get(1));
		httpSession.setAttribute("enquiry", message.get(2));
		httpSession.setAttribute("attendance", message.get(3));

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		try
		{
			String userType=(String) httpSession.getAttribute("mtype");

			if(userType.equalsIgnoreCase("Teacher"))
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
			else if(userType.equalsIgnoreCase("madmin"))
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

			//ec.redirect("Dashboard.xhtml");
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		//	return "Dashboard.xhtml";

		return "";
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}


}
