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

@ManagedBean(name="allEmployeeCategory")
@ViewScoped
public class AllEmployeeCategoryjson implements Serializable
{
	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AllEmployeeCategoryjson()
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
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				ArrayList<Category>list=DBJ.employeeCategoryList(SchoolId,conn);
				for(Category ls:list)
				{
					JSONObject obj = new JSONObject();

					obj.put("category",ls.getCategory());
					obj.put("id", ls.getId());
					arr.add(obj);
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		}
		catch (Exception e) {
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
