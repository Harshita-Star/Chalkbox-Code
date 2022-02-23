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
@ManagedBean(name="dashboardDeveloperJson")
@ViewScoped
public class DashboardDeveloperJsonBean implements Serializable
{
	String json="";
	DataBaseMethodsReports obj1 = new DataBaseMethodsReports();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public DashboardDeveloperJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int pending,inprocess,notAppropriate,resolved,all,wrap,close;
				JSONObject mainobj = new JSONObject();
				pending = obj1.countOfAllPending("Pending",schid,conn);

				inprocess = obj1.countOfAllPending("In Process",schid,conn);

				notAppropriate = obj1.countOfAllPending("Not Appropriate",schid,conn);

				resolved = obj1.countOfAllPending("resolved",schid,conn);

				all = obj1.countOfAllPending("all",schid,conn);
				
				wrap = obj1.countOfAllPending("Wrapping Up",schid,conn);

				close = obj1.countOfAllPending("close",schid,conn);

				mainobj.put("pending", pending);
				mainobj.put("inprocess", inprocess);
				mainobj.put("notAppropriate", notAppropriate);
				mainobj.put("resolved", resolved);
				mainobj.put("wrappingUp", wrap);
				mainobj.put("closed", close);
				mainobj.put("all", all);

				arr.add(mainobj);
			}

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

	public DataBaseMethodsReports getObj1() {
		return obj1;
	}
	public void setObj1(DataBaseMethodsReports obj1) {
		this.obj1 = obj1;
	}


}
