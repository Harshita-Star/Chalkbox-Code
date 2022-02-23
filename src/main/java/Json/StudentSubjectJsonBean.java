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

import schooldata.DataBaseConnection;
import schooldata.Subjects;

@ManagedBean(name="studentSubjectJson")
@ViewScoped

public class StudentSubjectJsonBean implements Serializable
{
	String json;
	
	public StudentSubjectJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson dbj = new DataBaseMeathodJson();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");
			String classid=params.get("classid");
			String sectionid=params.get("sectionid");
			String studentid=params.get("studentid");

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				ArrayList<Subjects> list = dbj.studentWiseSubjectList(classid,sectionid,studentid,schid,conn);
				
				JSONObject objj = new JSONObject();
				objj.put("id","all");
				objj.put("subject", "All");
				arr.add(objj);
				
				for(Subjects ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("id",ss.getSubjectCode());
					obj.put("subject", ss.getSubjectName());
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
