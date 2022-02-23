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
@ManagedBean(name="viewAllTicketJson")
@ViewScoped
public class ViewAllTicketJsonBean implements Serializable
{
	String json="";
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public ViewAllTicketJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String status = params.get("status");
			String schid = params.get("schid");

			JSONArray arrayList = new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(schid==null || schid.equals("") || schid.equalsIgnoreCase("all"))
				{
					list=DBM.allTicketByStatus(status,conn);
				}
				else
				{
					list=DBM.allTicketByStatus(status,schid,conn);
				}

				for(TicketRaisingInfo ss:list)
				{
					JSONObject js = new JSONObject();
					js.put("Id", ss.getId());
					js.put("ticketdatestr", ss.getTicketDateStr());
					js.put("ticketno", ss.getTicketNo());
					js.put("type", ss.getType());
					js.put("userId", ss.getUserId());
					js.put("discription", ss.getDescription());
					js.put("schId", ss.getSchid());
					js.put("status", ss.getStatus());
					js.put("schoolName", ss.getSchoolName());
					js.put("userName", ss.getUserName());
					js.put("screenShot", ss.getScreenshot());
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
