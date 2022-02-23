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

import schooldata.AllGroupList;
import schooldata.DataBaseConnection;
@ViewScoped
@ManagedBean(name="studentGroupJson")
public class StudentGroupListJson implements Serializable
{

	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public StudentGroupListJson()
	{

		Connection conn=DataBaseConnection.javaConnection();
		
		try {

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String SchoolId=params.get("Schoolid");
			String addNumber=params.get("studentid");
			String type=params.get("type");

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<AllGroupList>list=DBJ.allGroupsForStudent(SchoolId, addNumber,type,conn);
				
				for(AllGroupList ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("addNumber",ls.getAddNumber());
					obj.put("groupid", ls.getId());
					obj.put("gropuname",ls.getGroupName());
					obj.put("count", ls.getCount());
					String[] count=ls.getAddNumber().split(",");
					obj.put("Studentcount", count.length);
					arr.add(obj);

				}
			}

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
