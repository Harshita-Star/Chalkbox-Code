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

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
@ManagedBean(name="deleteBillJson")
@ViewScoped
public class DeleteBillJsonBean implements Serializable
{
	String json="";
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	public DeleteBillJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String billId = params.get("billId");
			String status="";
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=obj.deleteSchoolBill(billId,conn);
				if(i>=1)
				{
					status="Sucessfully Deleted";
				}
				else
				{
					status="Error";
				}
			}
			else
			{
				status="Error";
			}
			
			mainobj.put("value", status);
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

	public DataBaseMethodsReports getObj() {
		return obj;
	}
	public void setObj(DataBaseMethodsReports obj) {
		this.obj = obj;
	}
	public void setJson(String json) {
		this.json = json;
	}
}
