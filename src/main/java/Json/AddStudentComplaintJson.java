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
import schooldata.StudentInfo;
@ManagedBean(name="addStudentComplaintJson")
@ViewScoped
public class AddStudentComplaintJson implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AddStudentComplaintJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String studentId=params.get("studentid");
			String description=params.get("description");
			String schid = params.get("schid");
			String type = params.get("type");//appreciation, complaint, neutral
			String complaintBy = params.get("complaintBy");
			String username = params.get("username");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			int i=0;
			if(checkRequest)
			{
				i = DBJ.addComplaintDiary(schid, studentId, description, complaintBy, type.toLowerCase(),username, conn);
			}
			
			JSONArray arr=new JSONArray();
			JSONObject obj=new JSONObject();
			if(i>=1)
			{
				StudentInfo ls=DBJ.studentDetailslistByAddNo(studentId, schid, conn);
				if(ls.getFathersPhone()==ls.getMothersPhone())
				{
					DBJ.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module. ", ls.getFathersPhone()+"-"+ls.getAddNumber()+"-"+schid,schid,"",conn);
				}
				else
				{
					DBJ.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module.", ls.getFathersPhone()+"-"+ls.getAddNumber()+"-"+schid,schid,"",conn);
					DBJ.notification("Student Behaviour","Student Behaviour is added.Please check in behaviour module.", ls.getMothersPhone()+"-"+ls.getAddNumber()+"-"+schid,schid,"",conn);
				}
				obj.put("status","Complaint Added Successfully");
			}
			else
			{
				obj.put("status","Some Error Occured");
			}
			arr.put(obj);
			json=arr.toString();
		}
		catch (Exception e) 
		{
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
}
