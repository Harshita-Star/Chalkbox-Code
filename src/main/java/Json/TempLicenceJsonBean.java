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

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="tempLicenceJson")
@ViewScoped
public class TempLicenceJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public TempLicenceJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid =params.get("schid");
			String days =params.get("days");
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			String status = "";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String session = DBM.selectedSessionDetails(schid,conn);
				String start = String.valueOf(Integer.valueOf(session.split("-")[0])+1);
				String end = String.valueOf(Integer.valueOf(session.split("-")[1])+1);
				String nextSession = start+"-"+end;

				Date today = new Date();
				int dd = Integer.valueOf(days);
				
				today.setDate(today.getDate()+dd);

				String strDate = new SimpleDateFormat("yyyy-MM-dd").format(today);
				
				int i = new DataBaseMethodsReports().generateTempLicence(schid,nextSession,strDate,conn);
				if(i>=1)
				{
					status="sumitted";
				}
				else
				{
					status="error";
				}
			}
			else
			{
				status="error";
			}

			mainobj.put("status", status);
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
