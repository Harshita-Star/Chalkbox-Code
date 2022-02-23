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
import schooldata.DatabaseMethods1;
@ManagedBean(name="updateAgreementForJson")
@ViewScoped
public class UpdateAgreementForJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();


	public UpdateAgreementForJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			JSONObject jobj = new JSONObject();
			String status="";
			String schId=params.get("schid");
			int agreement=Integer.valueOf(params.get("agreement"));

			int i=0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i = DBM.updateAgreement(schId,agreement,conn);
			}
			
			if(i>=1)
			{
				status="updated";
			}
			else
			{
				status="0";
			}
			jobj.put("value", status);
			json=jobj.toJSONString();	
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
