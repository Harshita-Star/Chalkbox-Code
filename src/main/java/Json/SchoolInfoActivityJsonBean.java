package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.ActivityInfo;
import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="schoolInfoActivityJson")
@ViewScoped
public class SchoolInfoActivityJsonBean implements Serializable
{
	ArrayList<ActivityInfo> activityList = new ArrayList<>();
	ArrayList<SelectItem> schoolList = new ArrayList<>();
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public SchoolInfoActivityJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			JSONArray jarray = new JSONArray();
			

			String dateStr = params.get("date"); // dd/MM/yyyy

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date selectDate=null;
				schoolList=dbm.getAllSchool(conn);
				activityList = new ArrayList<>();

				try {
					selectDate= new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);

				} catch (ParseException e) {
					e.printStackTrace();
				}


				String attendance,fees,communication,news,gallery,work,login,strDate;
				strDate=new SimpleDateFormat("yyyy-MM-dd").format(selectDate);
				String strDate1=new SimpleDateFormat("dd-MM-yyyy").format(selectDate);
				int i=1;
				for(SelectItem ss:schoolList)
				{
					ActivityInfo list = new ActivityInfo();
					String schoolName=ss.getLabel();
					String schoolId=String.valueOf(ss.getValue());

					attendance = obj.selectAttendance(conn,selectDate,schoolId);
					if(attendance.equalsIgnoreCase("tick.png"))
					{
						attendance="Yes";
					}
					else
					{
						attendance="No";
					}
					fees=obj.selectFees(conn,strDate,schoolId);
					if(fees.equalsIgnoreCase("tick.png"))
					{
						fees="Yes";
					}
					else
					{
						fees="No";
					}
					communication = obj.selectCommunication(conn,strDate,schoolId);
					if(communication.equalsIgnoreCase("tick.png"))
					{
						communication="Yes";
					}
					else
					{
						communication="No";
					}
					news = obj.selectAllNews(conn,strDate,schoolId);
					if(news.equalsIgnoreCase("tick.png"))
					{
						news="Yes";
					}
					else
					{
						news="No";
					}
					gallery =obj.selectGallery(conn,strDate,schoolId);
					if(gallery.equalsIgnoreCase("tick.png"))
					{
						gallery="Yes";
					}
					else
					{
						gallery="No";
					}
					work=obj.selectHomeWork(conn,strDate,schoolId);
					if(work.equalsIgnoreCase("tick.png"))
					{
						work="Yes";
					}
					else
					{
						work="No";
					}
					login=obj.selectLogin(conn, strDate1, schoolId);
					if(login.equalsIgnoreCase("tick.png"))
					{
						login="Yes";
					}
					else
					{
						login="No";
					}

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
				for(ActivityInfo mm:activityList)
				{
					JSONObject jobj = new JSONObject();

					jobj.put("schoolName", mm.getSchoolName());
					jobj.put("attendance", mm.getAttendance());
					jobj.put("fees", mm.getFees());
					jobj.put("communication", mm.getCommunication());
					jobj.put("news", mm.getNews());
					jobj.put("gallery", mm.getGallery());
					jobj.put("work", mm.getWork());
					jobj.put("login", mm.getLogin());


					jarray.add(jobj);
				}
			}
			
			json=jarray.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
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




}
