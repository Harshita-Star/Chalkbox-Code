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

@ManagedBean(name="edittemplate")
@ViewScoped
public class EdittemplateJsonBean implements Serializable
{
	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EdittemplateJsonBean()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String SchoolId=params.get("Schoolid");
			String id=params.get("id");
			String message=params.get("message");
			String name=params.get("name");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=DBJ.allMessageEdit(id,message,name,SchoolId,conn);

				if(i>0)
				{
					JSONObject obj = new JSONObject();
					obj.put("msg", "Template Update Successfully");
					arr.add(obj);
				}
				else
				{
					JSONObject obj = new JSONObject();
					obj.put("msg", "Template not Update ");

					arr.add(obj);
				}
			}
			else
			{
				JSONObject obj = new JSONObject();
				obj.put("msg", "Template not Update ");

				arr.add(obj);
			}

			//mainobj.put("SchoolJson", arr);
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
