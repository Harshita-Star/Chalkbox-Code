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
import schooldata.StudentInfo;

@ManagedBean(name="allgrouplistjson")
@ViewScoped
public class AllGroupListJSON implements Serializable
{

	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AllGroupListJSON()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String SchoolId=params.get("Schoolid");
			String type=params.get("type");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				ArrayList<AllGroupList>list=DBJ.allGroups(SchoolId,type,conn);

				for(AllGroupList ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("addNumber",ls.getAddNumber());
					obj.put("groupid", ls.getId());
					obj.put("gropuname",ls.getGroupName());
					String[] count=ls.getAddNumber().split(",");
					obj.put("Studentcount", count.length);
					obj.put("count", ls.getCount());
					obj.put("time",ls.getTime());
					
					JSONArray arr2=new JSONArray();

					for(StudentInfo ls2:ls.getStlist())
					{
						JSONObject obj2 = new JSONObject();

						obj2.put("name",ls2.getFname());
						obj2.put("contact", String.valueOf(ls2.getFathersPhone()));
						obj2.put("classname", ls2.getClassName());
						obj2.put("username", ls2.getAddNumber());

						arr2.add(obj2);

					}


					obj.put("members", arr2);
					
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

		
		
		/////// // System.out.println(json);

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
