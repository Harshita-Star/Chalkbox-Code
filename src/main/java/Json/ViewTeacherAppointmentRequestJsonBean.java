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

@ManagedBean(name="viewTeacherAppointmentRequestJson")
@ViewScoped
public class ViewTeacherAppointmentRequestJsonBean implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ViewTeacherAppointmentRequestJsonBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		try {
			ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
			DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schid");
			String type=params.get("type");
			String username=params.get("username");

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(true)
			{
				appointmentList=obj1.selectAllTypeTeacherAppointmentListForJson(conn,schid,type,username);

				for(AppointmentInfo ls:appointmentList)
				{
					JSONObject obj = new JSONObject();
					obj.put("studentName", ls.getStudentName());
					obj.put("className", ls.getClassName());
					obj.put("studentId",ls.getStudentId());
					obj.put("id",ls.getId());
					obj.put("type",ls.getType());
					obj.put("description",ls.getDescription());
					obj.put("status",ls.getStatus());
					if(ls.getRemark()==null)
					{
						ls.setRemark("");
					}
					obj.put("remark",ls.getRemark());

					obj.put("userid",ls.getUserId());
					obj.put("addedBy",ls.getAddedBy());
					obj.put("apptime",ls.getAppointment_time());
					obj.put("appdate",ls.getStrAppointmentdate());
					obj.put("adddate",ls.getStrAdddate());


					arr.add(obj);

				}
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
