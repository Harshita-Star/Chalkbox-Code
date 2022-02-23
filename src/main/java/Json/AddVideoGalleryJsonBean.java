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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="addVideoGalleryJson")
@ViewScoped

public class AddVideoGalleryJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();


	public AddVideoGalleryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schId=params.get("schid");
			String tagName=params.get("tagname");
			String values=params.get("value");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONArray arr=new JSONArray();
			JSONObject mainobj=new JSONObject();
			String status="Not Added!";
			
			if(checkRequest)
			{
				int j=DBM.checkDuplicacyFromGalleryTable(conn,tagName,schId);
				
				if(j>=1)
				{
					status="This Is already exist!Please enter again.";
				}
				else
				{
					int i=DBM.submitAllVideoLink(conn,tagName,values,schId,"All");
					if(i>=1)
					{
						status="Added successfully!";
					}
					else
					{
						status="Not added!";
					}
				}
			}

			mainobj.put("Status", status);

			arr.add(mainobj);

			json=arr.toJSONString();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{

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



}
