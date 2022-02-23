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
import schooldata.StudentInfo;
import student_module.AppointmentInfo;
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="appointmentStatusChangeJson")
@ViewScoped
public class AppointmentStatusChangeBean implements Serializable
{
	String json="";
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	
	public AppointmentStatusChangeBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String status=params.get("status");
			String remark=params.get("remark");
			String id=params.get("id");
			String studentid=params.get("studentid");
			String userid=params.get("userid");
			String schid=params.get("schid");
			String date = params.get("date");
			String time = params.get("time");

			String value="";
			JSONArray jarray = new JSONArray();
			

			JSONObject jobj = new JSONObject();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{

				int i=obj.updateStatusByAdmin(conn,id,status,remark,userid,"parent_appointment");
				if(i>=1)
				{
					value="Status change successfully!";
					StudentInfo info = DBJ.studentDetailslistByAddNo(studentid, schid, conn);
					String sts = "";
					if(status.equalsIgnoreCase("approved"))
					{
						sts = "confirmed";
					}
					else
					{
						sts = "cancelled";
					}
					DBJ.notification("Appointment","Dear Parent, your appointment request for date "+date+" "+time+" has been "+sts+". Please check the remark from school.",info.getFathersPhone()+"-"+studentid+"-"+schid,schid,"",conn);
					DBJ.notification("Appointment","Dear Parent, your appointment request for date "+date+" "+time+" has been "+sts+". Please check the remark from school.",info.getMothersPhone()+"-"+studentid+"-"+schid,schid,"",conn);
				}
				else
				{
					value="Error Occur!";
				}
			}
			else
			{
				value="Error Occur!";
			}

			jobj.put("status", value);
			jarray.add(jobj);

			json=jarray.toJSONString();
			
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
