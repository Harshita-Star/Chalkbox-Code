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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="enquirySourceReportJson")
@ViewScoped

public class EnquirySourceReportJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

	public EnquirySourceReportJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			String schid=params.get("schid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<SelectItem> categoryList=dbm.allRefernceCategory(conn);
				
				String session = dbm.selectedSessionDetails(schid,conn);
				String arrs[] = session.split("-");
				String strDate = "01-04-"+arrs[0];
				String enDate = "31-03-"+arrs[1];
				
				String count = "0";
				
				for(SelectItem ss : categoryList)
				{
					JSONObject obj = new JSONObject();
					count = dbr.countEnquiryBySourceId(String.valueOf(ss.getValue()),schid,strDate,enDate,conn);
					ss.setDescription(count);
					
					obj.put("id",String.valueOf(ss.getValue()));
					obj.put("name", ss.getLabel());
					obj.put("count", ss.getDescription());
					
					arr.add(obj);
				}
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
}
