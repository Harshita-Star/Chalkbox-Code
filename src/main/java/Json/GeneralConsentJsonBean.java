package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;

@ManagedBean(name="generalConsentJson")
@ViewScoped

public class GeneralConsentJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

	public GeneralConsentJsonBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String studentid = params.get("studentid");
			String classid = params.get("classid");
			String sectionid = params.get("sectionid");
			String status = params.get("status"); //pending, agree, disagree
			
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(true)
			{
				SchoolInfoList ls=dbj.fullSchoolInfo(schid,conn);
				Date today = new Date();
				String tdt = new SimpleDateFormat("yyyy-MM-dd").format(today);
				ArrayList<GeneralConsentInfo> list = dbj.allGeneralConsentStudent(studentid,classid,sectionid,schid,tdt,status,conn);
				
				for(GeneralConsentInfo ss : list)
				{
					JSONObject obj = new JSONObject();
					
					obj.put("id", ss.getId());
					obj.put("description", ss.getDescription());
					obj.put("lastDate", ss.getLastDateStr());
					obj.put("addDate", ss.getAddTimeStr());
					obj.put("addedBy", ss.getAddedBy());
					
					if(ss.isShowFile())
					{
						obj.put("file",ls.getDownloadpath()+ss.getFile());
					}
					else
					{
						obj.put("file","");
					}
					
					if(!status.equalsIgnoreCase("pending"))
					{
						obj.put("status", ss.getStatus());
						obj.put("remark", ss.getRemark());
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
}
