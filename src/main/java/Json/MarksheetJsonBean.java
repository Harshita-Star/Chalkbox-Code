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
import schooldata.NotificationInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="marksheetJsonBean")
@ViewScoped

public class MarksheetJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();


	public MarksheetJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String username = params.get("studentid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<NotificationInfo> newsList=DBJ.allProgressList(username,schid,conn);
				SchoolInfoList ls=DBJ.fullSchoolInfo(schid,conn);
				
				for(NotificationInfo nn : newsList)
				{
					JSONObject obj = new JSONObject();

					obj.put("examName", nn.getMessage());

					if(nn.getFilename().equalsIgnoreCase("na"))
					{
						obj.put("file", "");
					}
					else
					{
						obj.put("file", ls.getDownloadpath()+nn.getFilename());
					}


					arr.add(obj);
				}
			}

			json=arr.toJSONString();
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
}
