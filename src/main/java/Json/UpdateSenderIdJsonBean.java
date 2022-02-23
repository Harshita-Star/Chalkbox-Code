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
@ManagedBean(name="updateSenderIdJson")
@ViewScoped
public class UpdateSenderIdJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson obj = new DataBaseMeathodJson();

	public UpdateSenderIdJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String senderId=params.get("senderId");
			String schoolId=params.get("schoolId");
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			String updated="";
			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i= obj.UpdateSenderIdInSchoolInfo(conn,senderId,schoolId);
			}
			
			if(i>=1)
			{
				updated="update";
			}
			else
			{
				updated="Error";
			}
			
			mainobj.put("value", updated);
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




}
