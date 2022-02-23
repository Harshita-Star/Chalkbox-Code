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
import schooldata.HomeWorkInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="eventJson")
@ViewScoped
public class EventJsonBean implements Serializable  {


	String json;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();


	ArrayList<HomeWorkInfo> list;
	public EventJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String limit=params.get("limit");
			String startingpoint=params.get("startpoint");
			String schid=params.get("Schoolid");

			//   Date newDate=null;
			/*  try {
			//newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			 */

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				list=dbj.eventListForApp(limit,startingpoint,schid,conn);

				//  ArrayList<NotificationInfo> info=new DatabaseMethods1().allMessageforapp(studentid);
				SchoolInfoList ls=dbj.fullSchoolInfo(schid,conn);

				for(HomeWorkInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("Event Date", ss.getUploadDateStr());
					obj.put("Event  Create Date",ss.getOpeningDateStr());
					obj.put("desc", ss.getDescription());
					obj.put("event name", ss.getAssignmentName());
					if(ss.getAssignment_photo1()==null||ss.getAssignment_photo1().equals(""))
					{
						obj.put("file","");
					}
					else
					{
						obj.put("file",ls.getDownloadpath()+ss.getAssignment_photo1());
					}
					arr.add(obj);
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
