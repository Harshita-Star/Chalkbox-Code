package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="chhotuRechargeSubmitJson")
@ViewScoped
public class ChhotuRechargeSubmitJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson obj1=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public ChhotuRechargeSubmitJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schoolId");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			String submitted="Something Went Wrong!";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj1.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i = obj1.submitAllValuesInTable(conn,schId,"Chhotu Recharge","5000","approved","unpaid");
				if(i>=1)
				{
					submitted="Chhotu Recharge successfully!";
					DBM.chhotuRecharge(schId, new Date(), 5000, conn);

				}
				else
				{
					submitted="error";
				}
			}

			mainobj.put("status", submitted);

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
