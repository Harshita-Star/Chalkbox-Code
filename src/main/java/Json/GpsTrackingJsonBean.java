package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;

@ManagedBean(name="gpsTrackingJson")
@ViewScoped

public class GpsTrackingJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public GpsTrackingJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String startDate=params.get("date");
			String schid = params.get("Schoolid");
			String routeId = params.get("routeId");

			String strDate="";
			Date today=new Date();
			String dataPoints="";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(startDate==null || startDate.equals(""))
				{
					strDate="";
				}
				else
				{
					try
					{
						today=new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
						strDate=new SimpleDateFormat("yyyy-MM-dd").format(today);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				dataPoints=DBJ.gpsTrackingDataPoints(strDate,routeId,schid,conn);
			}

			//JSONObject mainobj = new JSONObject();
			/*JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("data_points",dataPoints);

				arr.add(obj);

				json=arr.toJSONString();*/

			json=dataPoints;
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
