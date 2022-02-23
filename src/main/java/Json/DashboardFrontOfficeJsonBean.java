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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="dashboardFrontOfficeJson")
@ViewScoped

public class DashboardFrontOfficeJsonBean implements Serializable
{
	String json;
	String totalEnquiry,accepted,pending,totalNewAdmission;
	DataBaseMeathodJson obj1=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();


	public DashboardFrontOfficeJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schid");
			String session=DBM.selectedSessionDetails(schId, conn);

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj1.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				totalEnquiry = obj1.allStudentEnquiry(schId,conn);
				accepted = obj1.acceptedStudent(schId,conn);
				pending = obj1.pendingStudent(schId,conn);
				totalNewAdmission = DBM.allStudentcount(schId,"newAddmission","",session,"",conn);

				mainobj.put("totalEnquiry", totalEnquiry);
				mainobj.put("accepted", accepted);
				mainobj.put("pending", pending);
				mainobj.put("newAdm", totalNewAdmission);

				arr.add(mainobj);
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
}
