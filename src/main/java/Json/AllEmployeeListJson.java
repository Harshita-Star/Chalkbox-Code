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

import schooldata.Category;
import schooldata.DataBaseConnection;
import schooldata.EmployeeInfo;

@ManagedBean(name="employeeListJson")
@ViewScoped
public class AllEmployeeListJson implements Serializable{
	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AllEmployeeListJson()
	{
		Connection conn=DataBaseConnection.javaConnection();


		try 
		{
			
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String SchoolId=params.get("Schoolid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				ArrayList<Category>list=DBJ.employeeCategory(SchoolId,conn);

				for(Category ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("category",ls.getCategory());
					obj.put("id", ls.getId());
					JSONArray arr2=new JSONArray();

					for(EmployeeInfo ls2:ls.getEmplist())
					{
						JSONObject obj2 = new JSONObject();

						obj2.put("EMP_Name",ls2.getFname());
						obj2.put("Fathers_Name", ls2.getLname());
						obj2.put("Phone", ls2.getMobile());
						obj2.put("qualification", ls2.getQualification());
						obj2.put("username",ls2.getEmplyeeuserid());
						if(String.valueOf(ls2.getId()) == null || String.valueOf(ls2.getId()).equalsIgnoreCase("") || String.valueOf(ls2.getId()).equals("0"))
						{
							obj2.put("empId", ls2.getEmplyeeuserid());
						}
						else
						{
							obj2.put("empId", String.valueOf(ls2.getId()));
						}

						obj2.put("email", ls2.getEmail());
						arr2.add(obj2);

					}

					obj.put("Emlployee", arr2);

					arr.add(obj);
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}




}
