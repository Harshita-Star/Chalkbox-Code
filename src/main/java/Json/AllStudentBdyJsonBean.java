package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
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
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="allStudentBdyJson")
@ViewScoped

public class AllStudentBdyJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AllStudentBdyJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String schid=params.get("schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr = new JSONArray();
			
			if(checkRequest)
			{
				
				String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
				
				Date date = new Date();
				
				ArrayList<String> tempList=new DataBaseMethodStudent().birthdayFieldList();
				ArrayList<StudentInfo> list=new DataBaseMethodStudent().studentDetail("","","",QueryConstants.IN_SCHOOL,QueryConstants.BIRTHDAY,date,date, "","","","", session, schid, tempList, conn);
				
				for(StudentInfo ss : list)
				{
					JSONObject obj = new JSONObject();
					obj.put("name", ss.getFname());
					obj.put("pic", ss.getStudent_image());
					obj.put("dob", ss.getDobStr());
					obj.put("className", ss.getClassName());
					obj.put("sectionName", ss.getSectionName());
					arr.add(obj);
				}
				
				
			}

			json = arr.toJSONString();
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
