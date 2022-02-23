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

@ManagedBean(name="attendantRouteListJson")
@ViewScoped

public class AttendantRouteListJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public AttendantRouteListJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String schid = params.get("schid");
			String userid = params.get("username");
			String utype = params.get("usertype");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				String id = DBJ.employeeIdbyEmployeeName(userid, schid, conn);
				DBJ.employeeCategIdByEmployeeId(id, schid, conn);

				ArrayList<SelectItem> list = DBJ.attendantRouteList(utype,id,schid,conn);
				
				for(SelectItem ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("routeId",String.valueOf(ss.getValue()));
					obj.put("routeName", ss.getLabel());
					arr.add(obj);
				}
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
