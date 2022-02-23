package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.EmployeeInfo;

@ManagedBean(name="teacherProfile")
@ViewScoped
public class TeacherProfileJsonBean implements Serializable
{

	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public TeacherProfileJsonBean() throws ParseException
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String stdate=params.get("Id");
			String schoolid=params.get("Schoolid");;

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				EmployeeInfo	employeeList=DBJ.searchEmployeeUsername(stdate,schoolid,conn);

				JSONObject obj = new JSONObject();
				obj.put("name", employeeList.getFname());
				obj.put("degree", employeeList.getQualification());

				obj.put("dob",employeeList.getDobStr());
				obj.put("gender", employeeList.getGender());
				obj.put("location", employeeList.getAddress());
				
				obj.put("img",employeeList.getEmpImage());
				
				obj.put("mobile", employeeList.getMobile());
				obj.put("experience", employeeList.getMarital());
				arr.add(obj);
			}	

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();

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
