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

@ManagedBean(name="allTicketbeanJson")
@ViewScoped
public class AllTicketBeanJson implements Serializable{

	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AllTicketBeanJson()
	{

		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			new DataBaseMeathodJson();


			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();


			String type=params.get("type");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				ArrayList<TicketRaisingInfo>list=DBM.allTicket(type,conn);

				for(TicketRaisingInfo ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("addDate", ls.getTicketDateStr());
					obj.put("username",ls.getUserName());
					obj.put("desc",ls.getDescription());
					obj.put("schoolname",ls.getSchoolName());
					obj.put("image",ls.getScreenshot());

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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
