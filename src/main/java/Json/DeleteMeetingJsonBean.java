package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="deleteMeetingJson")
@ViewScoped

public class DeleteMeetingJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	
	public DeleteMeetingJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String id = params.get("id");
			String meetingid = params.get("meetingid");
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
				int i=obj.deleteMeeting(id,schid,conn);
				if(i>=1)
				{
					status="success";
					if(meetingid==null || meetingid.equals("")) {
						
					}else {
						deleteZoomMeetingAPI(meetingid, ls.getJwt());
					}
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
	
	public void deleteZoomMeetingAPI(String meetingId, String jwt)
	{
		try 
		{
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = RequestBody.create(mediaType, "");
			Request request = new Request.Builder()
			  .url("https://api.zoom.us/v2/meetings/"+meetingId)
			  .method("DELETE", body)
			  .addHeader("Authorization", "Bearer "+jwt)
			  .build();
			Response response = client.newCall(request).execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
