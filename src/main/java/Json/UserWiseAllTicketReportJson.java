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
import schooldata.TicketRaisingInfo;
@ManagedBean(name="userWiseAllTicketReportJson")
@ViewScoped
public class UserWiseAllTicketReportJson implements Serializable
{
	String json="";
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public UserWiseAllTicketReportJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String userid = params.get("userid");
			String schid = params.get("schid");
			String usertype=params.get("usertype");
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list=DBJ.allUserWiseTicketRaisedReport(userid,schid,usertype,"",conn);
				SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);
				
				for(TicketRaisingInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("id", ss.getId());
					obj.put("ticketDate", ss.getTicketDateStr());
					obj.put("ticketNo",ss.getTicketNo());
					obj.put("type", ss.getType());
					obj.put("description", ss.getDescription());
					obj.put("status", ss.getStatus());
					obj.put("image",ss.getScreenshot());
					obj.put("path", list1.getDownloadpath());
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
	public ArrayList<TicketRaisingInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TicketRaisingInfo> list) {
		this.list = list;
	}




}
