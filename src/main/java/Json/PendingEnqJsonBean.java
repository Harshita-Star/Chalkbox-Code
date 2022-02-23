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

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;

@ManagedBean(name="pendingEnqJson")
@ViewScoped

public class PendingEnqJsonBean implements Serializable
{
	String json = "";
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public PendingEnqJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schoolid");
			String session=params.get("session");
			

			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				
				if(session==null||session.equals(""))
				{
				   session="";	
				}
				
				studentList=DBJ.pendingEnquiryList(schid,session,conn);
				
				for(StudentInfo dd : studentList)
				{
					JSONObject obj = new JSONObject();

					obj.put("name", dd.getFname());
					obj.put("father", dd.getFathersName());
					obj.put("class", dd.getClassName());
					obj.put("mobile", dd.getContactNo());
					obj.put("date", dd.getAdmissionDate());
					obj.put("address", dd.getAddress());
					obj.put("id",dd.getEnquiryNo());
					obj.put("userid", dd.getUsername());
					obj.put("uname", dd.getActionBy());

					arr.put(obj);

				}
			}
			
			json = arr.toString();
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
