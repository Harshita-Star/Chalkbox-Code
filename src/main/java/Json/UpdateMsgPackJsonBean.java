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

@ManagedBean(name="updateMsgPackJson")
@ViewScoped

public class UpdateMsgPackJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();



	public UpdateMsgPackJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			JSONObject mainobj = new JSONObject();
			JSONArray arr = new JSONArray();
			String value = "error";

			String id = params.get("id");
			String schid = params.get("schid");
			String quantity = params.get("quantity");
			String status = params.get("status");
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(status.equalsIgnoreCase("approved"))
				{
					int i=DBM.updateMsgPackRequestInStatus(id,"approved",conn);
					if(i>=1)
					{
						value="sucessfully approved";
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request approved successfully!"));
						int k = DBM.checkChhotuRechargeInMessageTransaction(conn,schid);
						if(k==1)
						{
							int j = new DatabaseMethods1().updateChhotuRechargeInMessageTransaction(conn,quantity,schid);
							if(j==1)
							{
								// FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated successfully!"));
							}
						}
						else
						{
							DBM.addMessages(schid, new Date(), Integer.valueOf(quantity), conn);
						}
					}
					else
					{
						value="error";
					}
				}
				else if(status.equalsIgnoreCase("denied"))
				{
					int i=DBM.updateMsgPackRequestInStatus(id,"denied",conn);
					if(i>=1)
					{
						value="sucessfully denied";
					}
					else
					{
						value="error";
					}
				}
			}

			mainobj.put("status", value);
			arr.add(mainobj);
			json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e)
			{
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
