package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;

@ManagedBean(name="leaveReasonJson")
@ViewScoped

public class LeaveReasonJsonBean implements Serializable
{
	String json;
	ArrayList<String> leaveList = new ArrayList<String>();

	public LeaveReasonJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

	
		try {
			JSONArray arr=new JSONArray();
			
			
		    leaveList=new DataBaseMethodsReports().allLeaveResons(conn);
			for(String ss:leaveList)
			{
				JSONObject obj = new JSONObject();
				obj.put("reason",ss);
				arr.add(obj);
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

