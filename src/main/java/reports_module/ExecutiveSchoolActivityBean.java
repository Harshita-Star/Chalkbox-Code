package reports_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="executiveSchoolActivity")
@ViewScoped

public class ExecutiveSchoolActivityBean implements Serializable
{
	ArrayList<SchoolInfo> list = new ArrayList<>();
	String schid="",id="",sessionValue;
	Date selectDate;
	String strDate,fees,communication,news,gallery,work,login,attendance;
	ArrayList<ActivityInfo> activityList = new ArrayList<>();
	ActivityInfo selected = new ActivityInfo();
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	
	public ExecutiveSchoolActivityBean() 
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		list = (ArrayList<SchoolInfo>) ss.getAttribute("schoolList");
		schid = (String) ss.getAttribute("selectedSchool");
		id = (String) ss.getAttribute("username");
		selectDate=new Date();
		Connection conn=DataBaseConnection.javaConnection();
		sessionValue=obj1.selectedSessionDetails(schid, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		search();
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();

		activityList = new ArrayList<>();

		strDate=new SimpleDateFormat("yyyy-MM-dd").format(selectDate);
		String strDate1=new SimpleDateFormat("dd-MM-yyyy").format(selectDate);
		//int i=1;
		
		if(schid.equalsIgnoreCase("all"))
		{
			for(SchoolInfo ss:list)
			{
				ActivityInfo list = new ActivityInfo();
				String schoolName=ss.getAliasName();
				String schoolId=ss.getSchid();
				attendance = obj.selectAttendance(conn,selectDate,schoolId);
				fees=obj.selectFees(conn,strDate,schoolId);
				communication = obj.selectCommunication(conn,strDate,schoolId);
				news = obj.selectAllNews(conn,strDate,schoolId);
				gallery =obj.selectGallery(conn,strDate,schoolId);
				work=obj.selectHomeWork(conn,strDate,schoolId);
				login = obj.selectLogin(conn, strDate1, schoolId);
				list.setFees(fees);
				list.setAttendance(attendance);
				list.setCommunication(communication);
				list.setNews(news);
				list.setGallery(gallery);
				list.setWork(work);
				list.setLogin(login);
				list.setSchId(schoolId);
				list.setSchoolName(schoolName);

				activityList.add(list);
			}
		}
		else
		{
			loop:for(SchoolInfo ss:list)
			{
				if(schid.equals(ss.getSchid()))
				{
					ActivityInfo list = new ActivityInfo();
					String schoolName=ss.getAliasName();
					String schoolId=ss.getSchid();
					attendance = obj.selectAttendance(conn,selectDate,schoolId);
					fees=obj.selectFees(conn,strDate,schoolId);
					communication = obj.selectCommunication(conn,strDate,schoolId);
					news = obj.selectAllNews(conn,strDate,schoolId);
					gallery =obj.selectGallery(conn,strDate,schoolId);
					work=obj.selectHomeWork(conn,strDate,schoolId);
					login = obj.selectLogin(conn, strDate1, schoolId);
					list.setFees(fees);
					list.setAttendance(attendance);
					list.setCommunication(communication);
					list.setNews(news);
					list.setGallery(gallery);
					list.setWork(work);
					list.setLogin(login);
					list.setSchId(schoolId);
					list.setSchoolName(schoolName);

					activityList.add(list);
					
					break loop;
				}
			}
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	public String serviceGo()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			String school = selected.getSchId();
			boolean checkSchool = obj1.checkSchoolStatus(school, conn);
			// boolean checkSchool=true;
			if (checkSchool == true)
			{
				boolean expired = obj1.checkSchoolExpiryDate(school, conn);
				// boolean expired=false;

				if (expired == false)
				{
					String userType = "Admin";
					String selectedSession="";
					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
							.getSession(true);
					ss.setAttribute("showSMSBalMessage", "yes");
					ss.setAttribute("showDueBillMessage", "yes");
					ss.setAttribute("showSessionMessage", "yes");
					ss.setAttribute("schoolid", school);
					ss.setAttribute("schid", school);
					ss.setAttribute("username", id);
					ss.setAttribute("type", userType);
					ss.setAttribute("mtype", userType);

					SchoolInfoList info = obj1.fullSchoolInfo(conn);
					selectedSession = info.getDefaultSession();

					ss.setAttribute("masterAdmin", "no");
					ss.setAttribute("serviceExecutive", "yes");
					ss.setAttribute("financialYear", selectedSession);
					ss.setAttribute("selectedSession", selectedSession);

					ArrayList<String> message = obj1.checkmessageSetting(conn);
					ArrayList<String> template=obj1.checktemplateSetting(conn);
					ss.setAttribute("feesubmit", message.get(0));
					ss.setAttribute("registration", message.get(1));
					ss.setAttribute("enquiry", message.get(2));
					ss.setAttribute("attendance", message.get(3));
					ss.setAttribute("complaint", message.get(4));
					ss.setAttribute("birthday", message.get(5));
					ss.setAttribute("eventNotify", message.get(6));
					ss.setAttribute("marksheetNotify", message.get(7));
					ss.setAttribute("resultNotify", message.get(8));
					ss.setAttribute("elearningNotify", message.get(9));

					ss.setAttribute("feeMsg", template.get(0));
					ss.setAttribute("regMsg", template.get(1));
					ss.setAttribute("enqMsg", template.get(2));
					ss.setAttribute("attMsg", template.get(3));
					ss.setAttribute("compMsg", template.get(4));
					ss.setAttribute("birthMsg", template.get(5));

					obj1.updateLastLogin(id, school, conn);

					ArrayList<SelectItem> list = obj1.allModules(conn);
					if (list.size() > 0) {
						boolean i = obj1.checkSchoolInfo(conn);
						if (i == true)
						{
							ss.setAttribute("checkstu", false);
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

						} else {
							FacesContext fc = FacesContext.getCurrentInstance();
							ExternalContext ec = fc.getExternalContext();

							if (userType.equalsIgnoreCase("Admin")) {
								try {
									ec.redirect("schoolInformation.xhtml");
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"You are not authorized user to setup school informations. Please Contact Administrator!",
										"Validation error"));
							}

						}
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"No Modules Are There In This Software.Please Contact Administrator!",
								"Validation error"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Sorry, license of your school ERP has been expired, Please contact Administrator for license renewal. Make the renewal as soon as possible and enjoy our services. We are here to serve you. Thanks and Regards",
							"Validation error"));
				}

			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Sorry, Your School is Inactive. Please Contact Administrator. Thanks and Regards",
						"Validation error"));
			}
			return null;
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<SchoolInfo> getList() {
		return list;
	}

	public void setList(ArrayList<SchoolInfo> list) {
		this.list = list;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public ArrayList<ActivityInfo> getActivityList() {
		return activityList;
	}

	public void setActivityList(ArrayList<ActivityInfo> activityList) {
		this.activityList = activityList;
	}

	public ActivityInfo getSelected() {
		return selected;
	}

	public void setSelected(ActivityInfo selected) {
		this.selected = selected;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}
	
	
}
