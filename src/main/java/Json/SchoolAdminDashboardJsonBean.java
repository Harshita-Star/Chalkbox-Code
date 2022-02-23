package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="schoolAdminDashboardJson")
@ViewScoped

public class SchoolAdminDashboardJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	public SchoolAdminDashboardJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
			Map<String, String> params =FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String schid=params.get("Schoolid");
			String session=dbm.selectedSessionDetails(schid, conn);

			JSONArray arr = new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = dbj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				JSONObject obj = new JSONObject();
				
				SchoolInfoList ls=DBJ.fullSchoolInfo(schid, conn);

				String totalGallery="0";
				if(ls.getType().equalsIgnoreCase("basic"))
				{
					totalGallery=DBJ.countTotalGalley(schid,conn);
				}

				String totalStudent=dbm.allStudentcount(schid,"","",session,"",conn);
				Date d=new  Date();
				int year1=d.getYear()+1900;
				String selectYear=String.valueOf(year1);
				String absentstudent=DBJ.allAbsentStudent(schid,selectYear,conn);
				String leaveStudent=DBJ.allLeaveStudent(schid,selectYear,conn);
				String messageStduent=DBJ.todayMessage(schid,conn);
				String totalCollection=DBJ.todaysCollection(schid,conn);

				obj.put("totalStudent", totalStudent);
				obj.put("todayCollection", totalCollection);
				obj.put("absentStudent", absentstudent);
				obj.put("leaveStudent", leaveStudent);
				obj.put("totalGallery", totalGallery);
				obj.put("totalMsg", messageStduent);
				obj.put("plan", ls.getType());

				arr.add(obj);
			}

			json=arr.toString();
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
