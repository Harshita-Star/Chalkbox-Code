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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
@ManagedBean(name="eventCategoryOfCalendarJson")
@ViewScoped
public class EventCategoryOfCalendarJson implements Serializable
{
	ArrayList<SelectItem>eventList;
	String json="",eventValue="",eventName="";
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();


	public EventCategoryOfCalendarJson()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				eventList=dbm.allEvents(conn);
				for(SelectItem ss:eventList)
				{
					if(eventValue.equals(""))
					{
						eventValue=(String) ss.getValue();
						eventName=(String) ss.getValue();
					}
					else
					{
						eventValue=eventValue+","+ss.getValue();
						eventName=eventName+","+ss.getLabel();
					}
				}
			}

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("EventName", eventName);
			obj.put("Eventid", eventValue);
			arr.add(obj);

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
