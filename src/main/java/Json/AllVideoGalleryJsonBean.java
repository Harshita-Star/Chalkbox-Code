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
import schooldata.DatabaseMethods1;
import schooldata.galleryInfo;

@ManagedBean(name="allVideoGalleryJson")
@ViewScoped

public class AllVideoGalleryJsonBean implements Serializable
{
	String json;
	ArrayList<galleryInfo> galleryList;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();


	public AllVideoGalleryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				galleryList=DBM.selectAllGalleryValues(conn,schId);
				for(galleryInfo ss:galleryList)
				{
					JSONObject obj = new JSONObject();
					obj.put("tagName",ss.getTagName());
					obj.put("updateDate", ss.getVideoDate());
					obj.put("links",ss.getVideoLink());
					obj.put("totalLinks", ss.getVideoLinkNo());
					obj.put("id", ss.getTagId());

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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
