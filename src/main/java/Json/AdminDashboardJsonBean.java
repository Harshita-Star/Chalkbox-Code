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

@ManagedBean(name="adminDashboardJson")
@ViewScoped
public class AdminDashboardJsonBean implements Serializable
{
	String json;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AdminDashboardJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			String totalMsgtoday,totalSchool,smsRecharge,unpaidBill;

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();

			if(checkRequest)
			{
				totalMsgtoday = DBM.todayMessagefromSaveMsg(conn);

				totalSchool = DBM.totalSchool(conn);
				smsRecharge = DBM.smsRechageThisMonth(conn);
				unpaidBill = DBM.AllUnpaidBills(conn);

				obj.put("totalMsgtoday", totalMsgtoday);
				obj.put("totalSchool", totalSchool);
				obj.put("smsRecharge", smsRecharge);
				obj.put("unpaidBill", unpaidBill);
				arr.add(obj);
			}

			json=arr.toString();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
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
