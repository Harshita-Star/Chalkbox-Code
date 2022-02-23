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
import schooldata.DatabaseMethods1;
import schooldata.TicketRaisingInfo;
@ManagedBean(name="viewTicketCommentJson")
@ViewScoped
public class ViewTicketCommentJsonBean implements Serializable
{
	String json="";
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewTicketCommentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DatabaseMethods1 DBM = new DatabaseMethods1();
			
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("schid");
			String ticketId = params.get("ticketId");

			JSONArray arrayList = new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list = DBM.allticketCommentList(ticketId, schid, conn);
				for(TicketRaisingInfo ss:list)
				{
					JSONObject js = new JSONObject();
					js.put("userId", ss.getUserId());
					js.put("comment", ss.getComment());
					js.put("ticketdatestr", ss.getTicketDateStr());
					js.put("status", ss.getStatus());

					arrayList.add(js);
				}
			}

			json=arrayList.toJSONString();

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
	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}
}
