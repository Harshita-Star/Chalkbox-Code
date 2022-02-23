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

@ManagedBean(name="deleteTeacherAppointmentRequestJson")
@ViewScoped
public class DeleteTeacherAppointmentJsonBean implements Serializable {

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public DeleteTeacherAppointmentJsonBean() {

		Connection conn= DataBaseConnection.javaConnection();
		try {
			DataBaseOnlineAdm obj1 = new DataBaseOnlineAdm();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String tableid=params.get("id");

			String result="Error Occur!";

			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i = obj1.deleteAppointmentRequest(conn,tableid,"consent_table");
				if(i>=1)
				{
					result="Deleted successfully!";
				}
				else
				{
					result="Error Occur!";
				}
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