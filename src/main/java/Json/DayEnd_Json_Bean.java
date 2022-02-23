package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;

@ManagedBean(name="dayEndJson")
@ViewScoped
public class DayEnd_Json_Bean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String json;
	DataBaseMethodsReports DBR=  new DataBaseMethodsReports();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DataBaseMethods DB = new DataBaseMethods();

	public DayEnd_Json_Bean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String userName = params.get("id");
			String location=params.get("location");
			String latitude=params.get("lat");
			String logitude=params.get("long");
			String schid=params.get("schid");
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String logoutTime=DB.serverTime();
				Timestamp loginDate=DB.serverDate();
				int i=DB.updateLogout(userName, logoutTime, loginDate,location,latitude,logitude,conn,schid);
				if(i==1)
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("msg", "success");
					obj.put("time", logoutTime);
					arr.add(obj);
					mainobj.put("IMGJson", arr);
					json=mainobj.toJSONString();
				}
				else
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("msg", "An Error Occured");
					obj.put("time", "");
					arr.add(obj);
					mainobj.put("IMGJson", arr);
					json=mainobj.toJSONString();
				}
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "An Error Occured");
				obj.put("time", "");
				arr.add(obj);
				mainobj.put("IMGJson", arr);
				json=mainobj.toJSONString();
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


