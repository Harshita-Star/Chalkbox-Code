package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import reports_module.DataBaseMethodsReports;
import reports_module.VisitorInfo;
import schooldata.DataBaseConnection;

@ManagedBean(name="dateWiseVisitorJson")
@ViewScoped
public class DateWiseVisitorJsonBean implements Serializable
{
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseMethodsReports DBR=  new DataBaseMethodsReports();

	String json;
	public DateWiseVisitorJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date startDate = null,endDate=null;
				try {
					startDate = new SimpleDateFormat("yyyy-dd-MM").parse(params.get("startDate"));
					endDate = new SimpleDateFormat("yyyy-dd-MM").parse(params.get("endDate"));
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				String schid=params.get("sschid");
				ArrayList<VisitorInfo> visitorList=new ArrayList<>();
				visitorList=DBR.dateWiseVisitorReport(schid,startDate,endDate,conn);
				if(visitorList.size()>0)
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					for(VisitorInfo ll:visitorList)
					{
						JSONObject obj = new JSONObject();
						obj.put("sNo",ll.getsNo());
						obj.put("addDate",ll.getAddDateStr());
						obj.put("name",ll.getName());
						obj.put("mobileNo",ll.getMobileNo());
						obj.put("address",ll.getAddress());
						obj.put("toMeet",ll.getToMeet());
						obj.put("purpose",ll.getPurpose());
						obj.put("visitorId",ll.getVisitorId());
						arr.add(obj);
					}
					mainobj.put("SchoolJson", arr);
					json=mainobj.toJSONString();
				}
				else
				{
					JSONObject mainobj = new JSONObject();
					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("msg", "No Record Found");
					arr.add(obj);
					mainobj.put("SchoolJson", arr);
					json=mainobj.toJSONString();
				}
			}
			else
			{
				JSONObject mainobj = new JSONObject();
				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("msg", "No Record Found");
				arr.add(obj);
				mainobj.put("SchoolJson", arr);
				json=mainobj.toJSONString();
			}
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
