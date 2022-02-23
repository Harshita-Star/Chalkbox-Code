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

import org.json.simple.JSONObject;
import org.primefaces.shaded.json.JSONArray;

import schooldata.DataBaseConnection;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="changeStudentPhotoStatusJson")
@ViewScoped

public class ChangeStudentPhotoStatusJsonBean implements Serializable
{
	String json;
	DataBaseOnlineAdm dbo = new DataBaseOnlineAdm();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public ChangeStudentPhotoStatusJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schid = params.get("schid");
			String status = params.get("status");
			String type = params.get("type");
			String remark = params.get("remark");
			String photo = params.get("photo");

			String msg = "failed";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				msg="success";
				if(remark==null || remark.equalsIgnoreCase(""))
				{
					remark = " ";
				}

				if(type.contains("student"))
				{
					if(status.equalsIgnoreCase("approved"))
					{
						remark = " ";
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"approved","s_status","s_rem",remark);
						dbo.updateStudentImagePathInRegistration1(conn,photo,"student_image_path",studentid,schid);
					}
					else
					{
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"rejected","s_status","s_rem",remark);
					}
				}
				else if(type.contains("father"))
				{
					if(status.equalsIgnoreCase("approved"))
					{
						remark = " ";
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"approved","f_status","f_rem",remark);
						dbo.updateStudentImagePathInRegistration1(conn,photo,"fatherImage",studentid,schid);
					}
					else
					{
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"rejected","f_status","f_rem",remark);
					}
				}
				else if(type.contains("mother"))
				{
					if(status.equalsIgnoreCase("approved"))
					{
						remark = " ";
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"approved","m_status","m_rem",remark);
						dbo.updateStudentImagePathInRegistration1(conn,photo,"motherImage",studentid,schid);
					}
					else
					{
						dbo.updateSinglePhotoStatus(conn,studentid,schid,"rejected","m_status","m_rem",remark);
					}
				}
			}

			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();
			obj.put("status", msg);
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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
