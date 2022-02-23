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

import schooldata.AdmitCardInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="admitCardExamJson")
@ViewScoped

public class AdmitCardExamJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	
	public AdmitCardExamJsonBean() 
	{
		DataBaseMeathodJson dbj = new DataBaseMeathodJson();
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");
			String classid=params.get("classid");
			String sectionid=params.get("sectionid");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				ArrayList<AdmitCardInfo> list = dbj.admitCardExamListClassSectionWise(schid, classid, sectionid, conn);
				for(AdmitCardInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("className",ss.getClassName());
					obj.put("sectionName", ss.getSectionName());
					obj.put("groupId",ss.getGroupId());
					obj.put("type", ss.getType());
					obj.put("examName", ss.getExamName());
					obj.put("uploadType", ss.getUploadType());
					obj.put("file", ss.getPhotname());
					arr.add(obj);
				}
			}
			
			json=arr.toJSONString();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
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
