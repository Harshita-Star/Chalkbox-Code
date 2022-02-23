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

@ManagedBean(name="pendingGalleryJson")
@ViewScoped

public class PendingGalleryJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public PendingGalleryJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");
			String userid=params.get("userid");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<NotificationInfo> info=DBJ.pendingGallaryList(schid,userid,conn);
				SchoolInfoList list=DBJ.fullSchoolInfo(schid,conn);

				for(NotificationInfo ss:info)
				{
					JSONObject obj = new JSONObject();
					obj.put("image", ss.getMessage());
					obj.put("url",list.getDownloadpath());
					obj.put("tag",ss.getFilename());
					obj.put("tagId",ss.getTagid());
					obj.put("id",ss.getId());
					obj.put("type",ss.getType());
					obj.put("date", ss.getStrUdate());
					obj.put("classid", ss.getClassid());
					obj.put("classname", ss.getClassName());
					obj.put("userid", ss.getUsername());
					obj.put("empid", ss.getUserid());
					obj.put("addedBy", ss.getAddedBy());
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
