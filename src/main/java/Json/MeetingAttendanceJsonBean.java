package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="meetingAttendanceJson")
@ViewScoped

public class MeetingAttendanceJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	
	public MeetingAttendanceJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String id = params.get("id");
			String classid = params.get("classid");
			String sectionid = params.get("sectionid");
			String subjectid = params.get("subjectid");
			String addNumber = params.get("add_number");
			String schid = params.get("schid");
			
			String status="";
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				SchoolInfoList ls = DBJ.fullSchoolInfo(schid, conn);
				Date addDate = new Date();
				String strAddDate = new SimpleDateFormat("yyyy-MM-dd").format(addDate);
				String strJoinTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(addDate);
				int i = DBJ.saveMeetingAttendance(id, classid, sectionid, subjectid, addNumber, strAddDate, strJoinTime, schid, conn);
				if(i>=1)
				{
					status = "success";
				}
				else
				{
					status="Something went terribly wrong. Please try again later!";
				}
			}
			else
			{
				status="Authentication Failed";
			}
			
			mainobj.put("status", status);
			arr.add(mainobj);
			json=arr.toJSONString();
			
		} catch (Exception e) {
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
	
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}
}
