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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="onlyTeacherListJson")
@ViewScoped

public class OnlyTeacherListJsonBean implements Serializable
{
	String json;
	
	public OnlyTeacherListJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DatabaseMethods1 obj = new DatabaseMethods1();
		ArrayList<SelectItem> teacherList = new ArrayList<SelectItem>();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String userType = params.get("usertype");
			String username = params.get("username");
			String schid=params.get("schid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				String categid = DBJ.employeeCategoryIdByName("Teacher", schid, conn);
				teacherList = obj.allteacherOnly(categid,schid,conn);
				
				String empId = "";
				empId=username;
				if(!userType.equalsIgnoreCase("admin"))
				{
					empId=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				}
				
				if(!userType.equalsIgnoreCase("admin"))
				{
					SelectItem ss = new SelectItem();
					loop:for(SelectItem si : teacherList)
					{
						if(si.getValue().equals(empId))
						{
							ss = si;
							break loop;
						}
					}
					
					if(teacherList.contains(ss))
					{
						teacherList.remove(ss);
					}
				}

				for(SelectItem si : teacherList)
				{
					JSONObject jobj = new JSONObject();
					jobj.put("id", String.valueOf(si.getValue()));
					jobj.put("name", si.getLabel());
					arr.add(jobj);
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
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
