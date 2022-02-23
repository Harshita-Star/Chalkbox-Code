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


@ManagedBean(name="enquiryStatusUpdateJson")
@ViewScoped

public class EnquiryStatusUpdateJson  implements Serializable
{
	String json="";
	DataBaseJsonDeatil objJson = new DataBaseJsonDeatil();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

	public EnquiryStatusUpdateJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String id=params.get("id");
			String schid=params.get("schoolId");
			String status=params.get("status");

			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i =objJson.updateEnquiryStatus(id,schid,status,conn);
			}
			
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			String status1="";
			if(i>0)
			{
				status1="updated";
			}
			else
			{
				status1="not update";
			}
			obj.put("status",status1);
			arr.add(obj);

			json=arr.toString();
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
