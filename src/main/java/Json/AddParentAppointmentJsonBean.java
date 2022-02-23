package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="addParentAppointmentJson")
@ViewScoped
public class AddParentAppointmentJsonBean implements Serializable{


	DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	String json,status;
	public AddParentAppointmentJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schoolId = params.get("schid");
			String appDate = params.get("appointmentdate");
			String description = params.get("description");
			String toMeet = params.get("tomeet");
			String selectTime = params.get("selecttime");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			status="Error Occur!";
			
			if(checkRequest)
			{
				Date appointmentDate=null;
				Date time=null; 
				try {
					appointmentDate= new SimpleDateFormat("dd/MM/yyyy").parse(appDate);
                    time=new SimpleDateFormat("HH:mm").parse(selectTime);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				
				int i= obj1.submitParentAppointmentRequest(conn,appointmentDate,new Date(),"Pending",description,toMeet,schoolId,studentid,selectTime);
				if(i>=1)
				{
					StudentInfo ln = DBJ.studentDetailslistByAddNo(studentid, schoolId, conn);
					String strdt = new SimpleDateFormat("dd-MM-yyyy").format(appointmentDate);
					String sttime=new SimpleDateFormat("hh:mm a").format(time);
					DBJ.adminnotification("Appointment","Parent of "+ln.getFname()+", Class "+ln.getClassName()+" requested for an appointment on "+strdt+" "+sttime+". Please check your availability and revert on it.","admin-"+schoolId,schoolId,"Appointment-"+studentid,conn);
					status="submitted successfully!";
				}
				else
				{
					status="Error Occur!";
				}
				
			}
			
			mainobj.put("status", status);
			arr.add(mainobj);

			json=arr.toJSONString();	
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
