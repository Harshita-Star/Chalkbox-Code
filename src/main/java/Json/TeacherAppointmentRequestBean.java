package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONArray;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="teacherAppointmentRequestJson")
@ViewScoped
public class TeacherAppointmentRequestBean implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();

	public TeacherAppointmentRequestBean() {

		Connection conn= DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String appdate=params.get("appdate");//
			String description=params.get("description");
			String apptime=params.get("apptime");//
			String username=params.get("username");
			String studentid=params.get("studentid");
			String type=params.get("type");
			String schid=params.get("schid");

			String result="";
			Date currentDate=new Date();
			Date appointmentDate=null;
			try {
				appointmentDate=new SimpleDateFormat("dd/MM/yyyy").parse(appdate);

			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i= obj1.sendTeacherAppointmentRequest(conn,appointmentDate,currentDate,description,apptime,username,studentid,type,"pending",schid);

				if(i>=1)
				{
					result="Added successfully!";
					String msg="";
					if(type.equalsIgnoreCase("Consent"))
					{
						msg="School is asking for your consent. Please open the app to check the details.";
					}
					else
					{
						String appDate=new SimpleDateFormat("dd-MM-yyyy").format(appointmentDate);
						msg="School is requesting for a meeting on "+appDate+ " "+apptime+ ". Please open the app to check the details.";
					}
					StudentInfo info=DBJ.studentDetailslistByAddNo(studentid,schid,conn);

					DBJ.notification(type,msg, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);
					DBJ.notification(type,msg, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schid,schid,"",conn);

				}
				else
				{
					result="Error Occur!";
				}
			}
			else
			{
				result="Error Occur!";
			}

			obj.put("status", result);
			arr.put(obj);
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
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}