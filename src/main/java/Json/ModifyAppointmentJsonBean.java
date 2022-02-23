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

import schooldata.DataBaseConnection;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="modifyAppointmentJson")
@ViewScoped
public class ModifyAppointmentJsonBean implements Serializable{



	String json;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	ArrayList<String>timeList= new ArrayList<>();
	public ModifyAppointmentJsonBean(){
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schId = params.get("schid");
			String studentId = params.get("studentid");
			String appDate = params.get("appdate");
			String appTime = params.get("apptime");
			String description = params.get("description");
			String toMeet = params.get("tomeet");
			String status = params.get("status");
			String tableId = params.get("id");

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			String value="";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				Date appointmentDate=null;
				try {
					appointmentDate= new SimpleDateFormat("dd/MM/yyyy").parse(appDate);

				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				if(status.equalsIgnoreCase("pending"))
				{
					int i=obj.updateAppointmentDateAndTime(conn,tableId,appointmentDate,new Date(),appTime);
					if(i>=1)
					{
						value="Modify Successfully!";
					}
					else
					{
						value="Error Occur!";
					}
				}
				else if(status.equalsIgnoreCase("rejected"))
				{
					int i= obj.submitParentAppointmentRequest(conn,appointmentDate,new Date(),"Pending",description,toMeet,schId,studentId,appTime);
					if(i>=1)
					{
						obj.updateModifyStatus(conn,tableId,"Yes");
						value="Submitted Successfully!";
					}
					else
					{
						value="Error Occur!";
					}
				}
			}
			
			mainobj.put("Status", value);
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
