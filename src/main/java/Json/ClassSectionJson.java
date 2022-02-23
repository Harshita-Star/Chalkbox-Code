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
import schooldata.DatabaseMethods1;
@ManagedBean(name="classssectionjson")
@ViewScoped
public class ClassSectionJson implements Serializable
{

	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ClassSectionJson() {
		// TODO Auto-generated constructor stub

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("Schoolid");
			String type=params.get("type");
			String userId=params.get("userid");

			if(type.equalsIgnoreCase("admin"))
	        {
	        		json=DBJ.classlist111(schid,conn);
	        }
			else if(type.equalsIgnoreCase("academic coordinator"))
			{
				String tid=DBJ.teacherid(userId, schid, conn);
				json=DBJ.classlist111Cordinator(tid,schid,conn);
			}
	        else 
	        {
				Map<String, String> sysParams =FacesContext.getCurrentInstance().
						getExternalContext().getRequestHeaderMap();
				String userAgent = sysParams.get("User-Agent");
				boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
				
				if(checkRequest)
				{
					if(type.equalsIgnoreCase("teacher"))
					{
						String tid=DBJ.teacherid(userId, schid, conn);
						json=DBJ.classlist111ForTeacher(schid,conn,tid);
					}
					else
					{
						json=DBJ.classlist111(schid,conn);
					}
				}
				else
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr = new JSONArray();
					mainobj.put("SchoolJson", arr);
	
					json = mainobj.toJSONString();
				}
			
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
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
}
