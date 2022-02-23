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

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
@ManagedBean(name="deleteBroadcastingMessage")
@ViewScoped
public class DeleteBroadcastingMessage implements Serializable
{
	String json,groupId,message,type,messageId,schid,studentId;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	int i=0;
	public DeleteBroadcastingMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String id=params.get("id");
			
			JSONObject obj = new JSONObject();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i=DBJ.deleteBroadcastDetails(id,conn);

				if(i>=1)
				{
					obj.put("Msg", "Message Deleted Successfully");
				}
				else
				{
					obj.put("Msg", "Error Occured");
				}
			}
			else
			{
				obj.put("Msg", "Error Occured");
			}
			
			json=obj.toJSONString();
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
