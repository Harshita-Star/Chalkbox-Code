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
import schooldata.StudentInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="deleteAppointmentRequestJson")
@ViewScoped
public class DeleteAppointmentRequestJsonBean implements Serializable{

	String json,status;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	public DeleteAppointmentRequestJsonBean(){
		
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String tableId = params.get("id");
			String username = params.get("studentid");
			String schid = params.get("schid");
			String date = params.get("date");
			String time = params.get("time");


			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i = obj.deleteAppointmentRequest(conn,tableId,"parent_appointment");
				if(i>=1)
				{
					status="Deleted successfully!";
			    	if(username==null)
			    	{
			    		
			    	}
			    	else
			    	{
						StudentInfo ln = DBJ.studentDetailslistByAddNo(username, schid, conn);
					    DBJ.adminnotification("Appointment","Parent of "+ln.getFname()+", Class "+ln.getClassName()+" deleted the appointment request for date "+date+" "+time+".","admin-"+schid,schid,"AppointmentDelete-"+username,conn);
				
			    	}
				}
				else
				{
					status="Error Occur!";
				}
			}
			else
			{
				status="Error Occur!";
			}
			
			mainobj.put("status", status);
			arr.add(mainobj);

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
