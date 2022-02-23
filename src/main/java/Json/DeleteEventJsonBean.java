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

import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;

@ManagedBean(name="deleteEventJson")
@ViewScoped

public class DeleteEventJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	
	public DeleteEventJsonBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("schid");
			String event=params.get("event");
			String id=params.get("eventid");
			String cid=params.get("classid");
			String eventfor=params.get("eventfor");
			String type=params.get("type");
			
			String msg = "Something Went Wrong!";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type.equalsIgnoreCase("event"))
				{
					int i=DBJ.deleteEvents(id,conn);
					if(i>=1)
					{
						DBJ.deleteClassHoliday(event,id,conn);
					}
					
					msg = "event deleted";
				}
				else
				{
					msg = "success";
					DBJ.deleteParticularClassFromEvent(event, cid, conn);
					if(eventfor.equalsIgnoreCase("particular"))
					{
						ArrayList<ClassInfo> eventClassList=DBJ.allClassSectionListForEvent(event,schid,id,conn);
						if(eventClassList.size()<=0)
						{
							DBJ.deleteEvents(id,conn);
							msg = "event deleted";
						}
					}
				}
			}
			
			
			JSONObject obj = new JSONObject();
			
			obj.put("msg", msg);
			json=obj.toJSONString();
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
