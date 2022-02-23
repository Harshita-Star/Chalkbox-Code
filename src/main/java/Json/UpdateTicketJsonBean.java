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
@ManagedBean(name="updateTicketJson")
@ViewScoped
public class UpdateTicketJsonBean implements Serializable
{
	String json="";
	ArrayList<TicketRaisingInfo>list=new ArrayList<>();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	public UpdateTicketJsonBean()
	{

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String status = params.get("status");
			String userId = params.get("userid");
			String schId = params.get("schid");
			String remark = params.get("remark");
			String userType = params.get("usertype");
			
			String ticketId = params.get("ticketId");
			JSONArray jaArray = new JSONArray();
			JSONObject jaobj = new JSONObject();
			String update = "";
			DatabaseMethods1 obj=new DatabaseMethods1();
			int i=0;
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i=obj.updateTicketRaisingRemark(userId, schId, ticketId, remark, userType, status, conn);
				// i=obj.updateTicketRaisingRemark(userid, selectedTicket.getSchid(), selectedTicket.getId(), remark2, usertype, "Not Appropriate", conn);
			}
			
			if(i>=1)
			{
				TicketRaisingInfo info = DBJ.ticketInfoById(ticketId, conn);
				obj.updateStatusForticket(ticketId, status,conn);
				// obj.updateStatusForticket(selectedTicket.getId(),"Not Appropriate",conn);
				if(status.equalsIgnoreCase("Resolved"))
				{
					if(userType.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Resolved","1 new message received regarding this ticket","admin-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
					else
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Resolved","1 new message received regarding this ticket","staff"+"-"+info.getEmpid()+"-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
				}
				else if(status.equalsIgnoreCase("In Process"))
				{
					if(userType.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - In Process","1 new message received regarding this ticket","admin-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
					else
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - In Process","1 new message received regarding this ticket","staff"+"-"+info.getEmpid()+"-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
				}
				else if(status.equalsIgnoreCase("Not Appropriate"))
				{
					if(userType.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Not Appropriate","1 new message received regarding this ticket","admin-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
					else
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Not Appropriate","1 new message received regarding this ticket","staff"+"-"+info.getEmpid()+"-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
				}
				else if(status.equalsIgnoreCase("Close"))
				{
					if(userType.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Closed","1 new message received regarding this ticket","admin-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
					else
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Closed","1 new message received regarding this ticket","staff"+"-"+info.getEmpid()+"-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
				}
				else if(status.equalsIgnoreCase("pending"))
				{
					if(userType.equalsIgnoreCase("admin"))
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Pending","1 new message received regarding this ticket","admin-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
					else
					{
						DBJ.adminnotification("Ticket : "+info.getTicketNo()+" - Pending","1 new message received regarding this ticket","staff"+"-"+info.getEmpid()+"-"+schId,schId,"Ticket-"+info.getTicketNo(),conn);
					}
				}
				
				
				update="updated";
			}
			else
			{
				update="error";
			}
			jaobj.put("updated", update);
			jaArray.add(jaobj);
			json=jaArray.toJSONString();
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
