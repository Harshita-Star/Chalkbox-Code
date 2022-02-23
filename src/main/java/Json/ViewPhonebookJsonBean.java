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

@ManagedBean(name="phonebookJsonBean")
@ViewScoped

public class ViewPhonebookJsonBean implements Serializable {

	String json;

	ArrayList<NotificationInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ViewPhonebookJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String schid = params.get("Schoolid");

			JSONArray arr = new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list = DBJ.viewPhonebookJson(conn, schid);

				DBJ.fullSchoolInfo(schid,conn);

				for (NotificationInfo ss : list)
				{
					JSONObject obj = new JSONObject();
					obj.put("name", ss.getContactName());
					obj.put("designation", ss.getDesignation());
					obj.put("number", ss.getContactNo());

					arr.add(obj);
				}
			}

			json = arr.toJSONString();
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
