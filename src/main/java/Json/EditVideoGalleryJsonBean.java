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

@ManagedBean(name="editVideoGalleryJson")
@ViewScoped

public class EditVideoGalleryJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	ArrayList<galleryInfo> galleryList;

	public EditVideoGalleryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");
			String tagId=params.get("tagid");
			String tagName=params.get("tagname");
			JSONArray arr=new JSONArray();
			JSONObject mainobj=new JSONObject();
			String status="Not updated!";
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int j=DBM.checkDuplicacyFromGalleryTable(conn,tagName,schId);
				if(j>=1)
				{
					status="This Is already exist!Please enter again.";
				}
				else
				{
					int i=DBM.updateTagName(conn,schId,tagId,tagName);
					if(i>=1)
					{
						status="updated successfully!";
					}
					else
					{
						status="Not updated!";
					}
				}
			}

			mainobj.put("Status", status);

			arr.add(mainobj);


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



	public String getJson() {
		return json;
	}



	public void setJson(String json) {
		this.json = json;
	}



	public ArrayList<galleryInfo> getGalleryList() {
		return galleryList;
	}



	public void setGalleryList(ArrayList<galleryInfo> galleryList) {
		this.galleryList = galleryList;
	}
}
