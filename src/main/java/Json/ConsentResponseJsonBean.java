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

@ManagedBean(name="consentResponseJson")
@ViewScoped

public class ConsentResponseJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	
	public ConsentResponseJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String id = params.get("id");
			String schid = params.get("schid");
			String studentid = params.get("studentid");
			String classid = params.get("classid");
			String sectionid = params.get("sectionid");
			String status = params.get("status"); //agree, disagree
			String remark = params.get("remark");

			JSONArray arr=new JSONArray();

			String value="error";
			String msg="Unauthorised Request";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(true)
			{
				int i=DBJ.updateConsentResponse(id,classid,sectionid,studentid,status,remark,schid,conn);

				if(i>0)
				{
					value="success";
					msg = "Consent Updated Successfully!";
				}
				else
				{
					value="error";
					msg = "Something Went Wrong. Please Try Again!";
				}
			}

			JSONObject obj = new JSONObject();
			obj.put("status",value);
			obj.put("msg",msg);

			arr.add(obj);

			// mainobj.put("SchoolJson", arr);

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
