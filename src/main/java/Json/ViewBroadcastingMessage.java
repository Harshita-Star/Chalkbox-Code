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
@ManagedBean(name="viewBroadcastingMessage")
@ViewScoped
public class ViewBroadcastingMessage implements Serializable
{
	String json;
	ArrayList<MessageInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewBroadcastingMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list=DBJ.viewBroadcastMessage(schid,conn);
				if(list.size()>0)
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					for(MessageInfo ll:list)
					{
						JSONObject obj = new JSONObject();
						obj.put("id", ll.getId());
						obj.put("groupid",ll.getGroupId());
						obj.put("message", ll.getMessage());
						obj.put("type",ll.getType());
						obj.put("time", ll.getTime());
						obj.put("messageid", ll.getMessageId());
						obj.put("schid", ll.getSchid());
						obj.put("studentid", ll.getStudentId());
						arr.add(obj);
					}
					mainobj.put("SchoolJson", arr);
					json=mainobj.toJSONString();
				}
				else
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("Msg", "No Record Found");
					arr.add(obj);
					mainobj.put("SchoolJson", arr);
					json=mainobj.toJSONString();
				}
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("Msg", "No Record Found");
				arr.add(obj);
				mainobj.put("SchoolJson", arr);
				json=mainobj.toJSONString();
			}
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
