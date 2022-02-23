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
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="editAppointmentRemarkJson")
@ViewScoped
public class EditAppointmentRemarkBean implements Serializable
{
	String json="";
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EditAppointmentRemarkBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String remark=params.get("remark");
			String id=params.get("id");
			String userid=params.get("userid");

			String value="Error Occur!";
			JSONArray jarray = new JSONArray();
			JSONObject jobj = new JSONObject();
			

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=obj.updateRemarkByAdmin(conn,id,remark,userid);
				if(i>=1)
				{
					value="Remark Update successfully!";
				}
				else
				{
					value="Error Occur!";
				}
			}

			jobj.put("status", value);
			jarray.add(jobj);

			json=jarray.toJSONString();
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

	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}

}
