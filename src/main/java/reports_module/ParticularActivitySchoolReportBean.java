package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name = "particularActivitySchoolReport")
@ViewScoped
public class ParticularActivitySchoolReportBean implements Serializable {

	ArrayList<SelectItem> schoolList = new ArrayList<>();
	ArrayList<ActivityInfo> activityList = new ArrayList<>();
	ArrayList<String>dateList = new ArrayList<>();
	String schId,attendance,fees,communication,news,gallery,work,login;
	DataBaseMethodsReports obj =new DataBaseMethodsReports();
	String session;
	DatabaseMethods1 dbm=new DatabaseMethods1();
	
	public ParticularActivitySchoolReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schId,conn);
		schoolList=dbm.getAllSchool(conn);
		//("schoolId");
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public void search()
	{
		//("hello");
		Date endDate=new Date();
		Date strDate=new Date();
		Date tempDate = new Date();
		strDate.setDate(strDate.getDate()-30);
		tempDate.setDate(tempDate.getDate()-30);
		//(strDate);
		//(endDate);

		activityList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();

		ActivityInfo ss= new ActivityInfo();

		ss= new ActivityInfo();
		ss.setParticular("Attendance");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("fees");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("communication");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("news");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("gallery");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("work");
		activityList.add(ss);

		ss= new ActivityInfo();
		ss.setParticular("login");
		activityList.add(ss);

		for(Date i=strDate;(i.before(endDate));i.setDate(strDate.getDate()+1))
		{

			String dt1=new SimpleDateFormat("yyyy-MM-dd").format(i);
			dateList.add(dt1);
		}

		//(activityList.size());

		for(ActivityInfo aa:activityList)
		{

			//(aa.getParticular());
			Map<String, String> map = new HashMap<>();
			tempDate = new Date();
			tempDate.setDate(tempDate.getDate()-30);
			for(Date i=tempDate;(i.before(endDate));i.setDate(i.getDate()+1))
			{

				//(i);
				String dt1=new SimpleDateFormat("yyyy-MM-dd").format(i);
				String dt2=new SimpleDateFormat("dd-MM-yyyy").format(i);

				if(aa.getParticular().equalsIgnoreCase("attendance"))
				{
					attendance = obj.selectAttendance(conn,i,schId);
					map.put(dt1, String.valueOf(attendance));

				}
				else if(aa.getParticular().equalsIgnoreCase("fees"))
				{
					fees=obj.selectFees(conn,dt1,schId);
					map.put(dt1, String.valueOf(fees));
				}

				else if(aa.getParticular().equalsIgnoreCase("communication"))
				{
					communication = obj.selectCommunication(conn,dt1,schId);
					map.put(dt1, String.valueOf(communication));
				}
				else if(aa.getParticular().equalsIgnoreCase("news"))
				{
					news = obj.selectAllNews(conn,dt1,schId);
					map.put(dt1, String.valueOf(news));
				}
				else if(aa.getParticular().equalsIgnoreCase("gallery"))
				{
					gallery = obj.selectAllNews(conn,dt1,schId);
					map.put(dt1, String.valueOf(gallery));
				}

				else if(aa.getParticular().equalsIgnoreCase("work"))
				{
					work=obj.selectHomeWork(conn,dt1,schId);
					map.put(dt1, String.valueOf(work));
				}
				else if(aa.getParticular().equalsIgnoreCase("login"))
				{
					login=obj.selectLogin(conn, dt2, schId);
					map.put(dt1, String.valueOf(login));
				}
				//("val : "+map.get(dt1));
				aa.setActivityMap(map);

			}

		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}
	public ArrayList<ActivityInfo> getActivityList() {
		return activityList;
	}
	public void setActivityList(ArrayList<ActivityInfo> activityList) {
		this.activityList = activityList;
	}
	public ArrayList<String> getDateList() {
		return dateList;
	}
	public void setDateList(ArrayList<String> dateList) {
		this.dateList = dateList;
	}
	public String getSchId() {
		return schId;
	}
	public void setSchId(String schId) {
		this.schId = schId;
	}
	public String getAttendance() {
		return attendance;
	}
	public void setAttendance(String attendance) {
		this.attendance = attendance;
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
}

