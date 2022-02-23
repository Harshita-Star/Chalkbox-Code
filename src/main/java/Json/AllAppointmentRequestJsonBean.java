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
import student_module.AppointmentInfo;
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="allApointmentRequestJson")
@ViewScoped
public class AllAppointmentRequestJsonBean implements Serializable
{
	String json="";
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public AllAppointmentRequestJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");//
			String status=params.get("status");//className
			JSONArray jarray = new JSONArray();
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(true)
			{
				appointmentList=obj.selectAllTypeAppointmentList(conn,status,schid);
				for(AppointmentInfo mm:appointmentList)
				{
					JSONObject jobj = new JSONObject();

					jobj.put("studentName", mm.getStudentName());
					jobj.put("studentid", mm.getStudentId());
					jobj.put("id", mm.getId());
					jobj.put("classname", mm.getClassName());
					jobj.put("schid", mm.getSchId());
					jobj.put("adddate", mm.getStrAdddate());
					jobj.put("appdate", mm.getStrAppointmentdate());
					jobj.put("apptime", mm.getAppointment_time());
					jobj.put("description", mm.getDescription());
					jobj.put("remark", mm.getRemark());
					jobj.put("status", mm.getStatus());
					jobj.put("tomeet", mm.getToMeet());


					jarray.add(jobj);
				}
			}
			
			json=jarray.toJSONString();
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
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public ArrayList<AppointmentInfo> getAppointmentList() {
		return appointmentList;
	}
	public void setAppointmentList(ArrayList<AppointmentInfo> appointmentList) {
		this.appointmentList = appointmentList;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public DatabaseMethods1 getObj2() {
		return obj2;
	}
	public void setObj2(DatabaseMethods1 obj2) {
		this.obj2 = obj2;
	}





}
