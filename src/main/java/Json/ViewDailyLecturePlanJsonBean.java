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
import schooldata.SchoolInfoList;
import student_module.LectureInfo;

@ManagedBean(name="viewDailyLecturePlanJson")
@ViewScoped

public class ViewDailyLecturePlanJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

	
	public ViewDailyLecturePlanJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");
			String classid=params.get("classid");
			String sectionid=params.get("sectionid");
			String subjectid=params.get("subjectid");
			String dateStr=params.get("date");//yyyy-MM-dd

			JSONArray arr=new JSONArray();
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				SchoolInfoList ls=dbj.fullSchoolInfo(schid,conn);
				ArrayList<LectureInfo> list = dbj.dailyLecturePlanList(classid,sectionid,subjectid,dateStr,schid,conn);
				for(LectureInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("lecture",ss.getLectureno());
					obj.put("subject", ss.getSubjectname());
					obj.put("unit",ss.getUnitno());
					obj.put("unitname", ss.getUnitname());
					obj.put("description", ss.getDescription());
					if(ss.getStrfile()==null || ss.getStrfile().equalsIgnoreCase("") || ss.getStrfile().equalsIgnoreCase("NA"))
					{
						obj.put("file", "");
					}
					else
					{
						obj.put("file", ls.getDownloadpath()+ss.getStrfile());
					}
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
