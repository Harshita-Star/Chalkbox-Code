package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DietInfo;
import schooldata.StudentInfo;
@ManagedBean(name="notificationJsonBean")
@ViewScoped
public class NotificationJsonBean implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public NotificationJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("Schoolid");
			String stduentid=params.get("studentid");
			String type=params.get("type");

			String limit=params.get("limit");
			String startingpoint=params.get("startpoint");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String notification1="",notification2="",notification3="",notification4="";

				if(type.equalsIgnoreCase("student"))
				{
					StudentInfo	selectedStudent=DBJ.studentDetailslistByAddNo(stduentid,schid,conn);
					String classid=selectedStudent.getClassId();
					String	selectedSection=selectedStudent.getSectionid();
					notification1=classid+"-"+schid;
					notification2=selectedSection+"-"+classid+"-"+schid;
					notification3=String.valueOf(selectedStudent.getFathersPhone())+"-"+stduentid+"-"+schid;
					notification4=schid;

				}
				else if(type.equalsIgnoreCase("admin"))
				{
					notification1="'admin-"+schid+"'"+",'chalkbox'";
				}
				else
				{
					notification1="'staff-"+stduentid+"-"+schid+"'"+",'cbteacher'";
				}

				ArrayList<DietInfo>list=DBJ.NotificationMeathod(notification1,notification2,notification3,notification4,schid,type,conn,limit,startingpoint);

				
				for(DietInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("title", ss.getClassName());
					obj.put("message", ss.getDayName());
					obj.put("time", ss.getDiet());
					obj.put("status", ss.getStatus());
					obj.put("type", ss.getType());

					arr.add(obj);


				}


				json=arr.toJSONString();

				if(type.equalsIgnoreCase("student"))
				{

					DBJ.NotificationReadMessage(notification1,notification2,notification3,notification4,conn);
				}
				else
				{
					DBJ.NotificationAdminReadMessage(notification1,conn);
				}
			}
			else
			{
				json=arr.toJSONString();
			}
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
