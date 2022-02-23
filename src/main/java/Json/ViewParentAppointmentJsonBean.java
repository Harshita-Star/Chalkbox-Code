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
import student_module.AppointmentInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="viewParentAppointmentJson")
@ViewScoped
public class ViewParentAppointmentJsonBean implements Serializable{

	String json;
	ArrayList<String>timeList= new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewParentAppointmentJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
			ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schid");
			String studentId = params.get("studentid");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				appointmentList=obj.selectAppointmentList(conn,schId,studentId);

				for(AppointmentInfo mm:appointmentList)
				{
					JSONObject mainobj = new JSONObject();
					mainobj.put("studentId", mm.getStudentId());
					mainobj.put("schid", mm.getSchId());
					mainobj.put("app_date", mm.getStrAppointmentdate());
					mainobj.put("add_date", mm.getStrAdddate());
					mainobj.put("description", mm.getDescription());
					mainobj.put("toMeet", mm.getToMeet());
					mainobj.put("status", mm.getStatus());
					mainobj.put("app_time", mm.getAppointment_time());
					if(mm.getRemark()==null)
					{
						mainobj.put("remark", "");
					}
					else
					{
						mainobj.put("remark", mm.getRemark());
					}
					mainobj.put("modify", mm.getModify_status());
					mainobj.put("id", mm.getId());

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
