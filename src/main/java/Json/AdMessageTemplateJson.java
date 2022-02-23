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
@ManagedBean(name="addMessageTemplate")
@ViewScoped
public class AdMessageTemplateJson implements Serializable{


	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AdMessageTemplateJson()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			sectionid=params.get("messagetemplate");
			String name=params.get("name");
			String dynamic=params.get("dynamic");
			String SchoolId=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			
			if(checkRequest) 
			{
				int i=DBJ.addMessageTemplate(sectionid,name,dynamic,SchoolId,conn);
				
				if(i>0)
				{
					obj.put("msg", "Message Template Added Successfully");
					arr.add(obj);
				}
				else
				{
					obj.put("msg", "Error occur");
					arr.add(obj);
				}
			}
			else
			{
				obj.put("msg", "Error occur");
				arr.add(obj);
			}

			//mainobj.put("SchoolJson", arr);
			json=arr.toJSONString();
		}
		catch (Exception e) {
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}



}
