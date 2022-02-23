package schooldata;

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

import reports_module.ActivityInfo;
import reports_module.DataBaseMethodsReports;

@ManagedBean(name="schoolActivityMaster")
@ViewScoped
public class SchoolActivityMasterBean implements Serializable
{

	Date selectDate;
	ArrayList<ActivityInfo> activityList = new ArrayList<>(),selectedSchoolList=new ArrayList<>();
	ActivityInfo selected = new ActivityInfo();
	ArrayList<SelectItem>schoolList = new ArrayList<>();
	int totalP,totalL,totalA,totalS;
	String strDate,fees,communication,news,gallery,work,login,attendance;
	DataBaseMethodsReports obj = new DataBaseMethodsReports();


	public SchoolActivityMasterBean() {
		Connection conn=DataBaseConnection.javaConnection();
		//schoolList=new DatabaseMethods1().getAllSchool(conn);
		selectDate=new Date();
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		//search();
	}

	public void generateLoginCredentials()
	{
		DatabaseMethods1 DBM=new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedSchoolList.size()>0)
		{
			for(ActivityInfo schInfo:selectedSchoolList)
			{
				DBM.generateLoginCredentials(schInfo.getSchId(),conn);
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One School"));
		}
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void getSchoolsData()
	{
		Connection conn=DataBaseConnection.javaConnection();

		schoolList=new DatabaseMethods1().getAllSchool(conn);
		search();
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}



	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();

		activityList = new ArrayList<>();

		strDate=new SimpleDateFormat("yyyy-MM-dd").format(selectDate);
		String strDate1=new SimpleDateFormat("dd-MM-yyyy").format(selectDate);
		int i=1;
		for(SelectItem ss:schoolList)
		{
			ActivityInfo list = new ActivityInfo();
			String schoolName=ss.getLabel();
			String schoolId=String.valueOf(ss.getValue());
			attendance = obj.selectAttendance(conn,selectDate,schoolId);
			fees=obj.selectFees(conn,strDate,schoolId);
			communication = obj.selectCommunication(conn,strDate,schoolId);
			news = obj.selectAllNews(conn,strDate,schoolId);
			gallery =obj.selectGallery(conn,strDate,schoolId);
			work=obj.selectHomeWork(conn,strDate,schoolId);
			login = obj.selectLogin(conn, strDate1, schoolId);
			list.setSno(i++);
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
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}


	}

	public void schoolLogin()
	{
		DatabaseMethods1 DBM=new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		String unm=DBM.adminUserName(selected.getSchId(), conn);
		String pwd=DBM.adminPassword(selected.getSchId(), conn);
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		loginAsSchoolAdmin(unm,pwd);
	}

	public void loginAsSchoolAdmin(String unm, String pswd)
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		String userType = DBM.authentication(unm, pswd,conn);
		////// // System.out.println(userType);
		try
		{

			if(userType!=null)
			{
				String selectedSession="";
				String school = DBM.authenticationSchoool(unm, pswd,conn);
				HttpSession sss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				sss.invalidate();

				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				ss.setAttribute("showSMSBalMessage", "yes");
				ss.setAttribute("showDueBillMessage", "yes");
				ss.setAttribute("showSessionMessage", "yes");
				ss.setAttribute("schoolid", school);
				ss.setAttribute("username", unm);
				ss.setAttribute("type", userType);
				ss.setAttribute("mtype", userType);
				ss.setAttribute("masterAdmin", "yes");
				ss.setAttribute("serviceExecutive", "no");
				SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(conn);
				selectedSession = info.getDefaultSession();

				
				ArrayList<SelectItem> list=DBM.allModules(conn);
				if(list.size()>0)
				{
					boolean i=DBM.checkSchoolInfo(conn);
					if(i==true)
					{
						
						
						ArrayList<String> message = DBM.checkmessageSetting(conn);
						ArrayList<String>template=new DatabaseMethods1().checktemplateSetting(conn);
						ss.setAttribute("selectedSession", selectedSession);
						ss.setAttribute("financialYear", selectedSession);
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
						//return "Dashboard.xhtml";
					}
					else
					{
						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();


						try {
							ec.redirect("schoolInformation.xhtml");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Username or password is wrong", "Validation error"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public Date getSelectDate() {
		return selectDate;
	}
	public String getAttendance() {
		return attendance;
	}


	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}



	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}
	public String getStrDate() {
		return strDate;
	}


	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}




	public ArrayList<ActivityInfo> getActivityList() {
		return activityList;
	}


	public void setActivityList(ArrayList<ActivityInfo> activityList) {
		this.activityList = activityList;
	}


	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}


	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}


	public String getFees() {
		return fees;
	}


	public void setFees(String fees) {
		this.fees = fees;
	}


	public String getCommunication() {
		return communication;
	}


	public void setCommunication(String communication) {
		this.communication = communication;
	}


	public String getNews() {
		return news;
	}


	public void setNews(String news) {
		this.news = news;
	}


	public String getGallery() {
		return gallery;
	}


	public void setGallery(String gallery) {
		this.gallery = gallery;
	}


	public String getWork() {
		return work;
	}


	public void setWork(String work) {
		this.work = work;
	}


	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	public DataBaseMethodsReports getObj() {
		return obj;
	}


	public void setObj(DataBaseMethodsReports obj) {
		this.obj = obj;
	}


	public int getTotalP() {
		return totalP;
	}


	public void setTotalP(int totalP) {
		this.totalP = totalP;
	}


	public int getTotalL() {
		return totalL;
	}


	public void setTotalL(int totalL) {
		this.totalL = totalL;
	}


	public int getTotalA() {
		return totalA;
	}


	public void setTotalA(int totalA) {
		this.totalA = totalA;
	}


	public int getTotalS() {
		return totalS;
	}


	public void setTotalS(int totalS) {
		this.totalS = totalS;
	}

	public ActivityInfo getSelected() {
		return selected;
	}

	public void setSelected(ActivityInfo selected) {
		this.selected = selected;
	}

	public ArrayList<ActivityInfo> getSelectedSchoolList() {
		return selectedSchoolList;
	}

	public void setSelectedSchoolList(ArrayList<ActivityInfo> selectedSchoolList) {
		this.selectedSchoolList = selectedSchoolList;
	}

}
