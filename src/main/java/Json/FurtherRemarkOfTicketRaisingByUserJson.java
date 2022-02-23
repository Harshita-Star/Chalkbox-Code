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

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.TicketRaisingInfo;
@ManagedBean(name="furtherRemarkOfTicketRaisingByUserJson")
@ViewScoped
public class FurtherRemarkOfTicketRaisingByUserJson implements Serializable
{
	String json="";
	int i=0;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public FurtherRemarkOfTicketRaisingByUserJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String userid = params.get("userid");
			String usertype = params.get("usertype");
			String schid = params.get("schid");
			String id = params.get("id");
			String furtherRemark = params.get("furtherRemark");
			
			JSONObject obj = new JSONObject();
			String status="Error";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				TicketRaisingInfo info = DBJ.ticketInfoById(id, conn);
				
				i=DBJ.updateTicketRaisingRemark(userid,schid,id,furtherRemark,usertype,conn);
				
				if(i>0)
				{
					//String message="1 new message received from "+info.getSchoolName();
					if(info.getStatus().equalsIgnoreCase("Resolved") || info.getStatus().equalsIgnoreCase("Wrapping Up"))
					{
						DBJ.updateStatusForticket(id,"Wrapping Up",conn);
						//DBJ.devnotification("Ticket : "+info.getTicketNo()+" - "+"Wrapping Up", message,"cb_dev",schid,conn);
					}
					else
					{
						DBJ.updateStatusForticket(id,"pending",conn);
						//DBJ.devnotification("Ticket : "+info.getTicketNo()+" - "+"Pending", message,"cb_dev",schid,conn);
					}
					
					
		   	 		
					
					status="Futher Remark Added Successfully.";
				}
				else
				{
					status="Error";
				}
			}

			obj.put("status",status);
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

}
