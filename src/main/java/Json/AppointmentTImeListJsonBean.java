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
import schooldata.SchoolInfoList;

@ManagedBean(name="appointmentTimeListJson")
@ViewScoped
public class AppointmentTImeListJsonBean implements Serializable{



	String json;
	ArrayList<String>timeList= new ArrayList<>();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AppointmentTImeListJsonBean(){
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schid");


			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(true)
			{
				SchoolInfoList info=DBM.fullSchoolInfo(schId, conn);
				String t1= info.getAppointStartTime();
				String t2= info.getAppointEndTime();
				String st[]=t1.split(":");
				String et[]=t2.split(":");
				int startvalue=Integer.valueOf(st[0]);
				int lastvalue=Integer.valueOf(et[0]);
				int i=0;
				timeList= new ArrayList<>();
				for(i=startvalue;i<=lastvalue;i++)
				{
					String stime=String.valueOf(i);
					String startTime=stime+":"+"00";
					String endTime=stime+":"+"30";
					timeList.add(startTime);
					if(!startTime.equalsIgnoreCase(t2))
					{
						timeList.add(endTime);
					}
					
					////// // System.out.println("timeList"+timeList.size());
				}

				for(String mm:timeList)
				{
					JSONObject mainobj = new JSONObject();
					mainobj.put("app_time", mm);

					arr.add(mainobj);
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
