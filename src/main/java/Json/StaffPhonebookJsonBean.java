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

@ManagedBean(name = "staffPhonebookJson")
@ViewScoped

public class StaffPhonebookJsonBean implements Serializable {
	String json;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();


	public StaffPhonebookJsonBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String SchoolId = params.get("Schoolid");

			JSONArray arr = new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<Category> list = DBJ.employeeCategory(SchoolId, conn);

				for (Category ls : list)
				{
					//JSONObject obj = new JSONObject();
					for (EmployeeInfo ls2 : ls.getEmplist())
					{
						JSONObject obj2 = new JSONObject();

						obj2.put("category", ls.getCategory());
						obj2.put("EMP_Name", ls2.getFname());
						obj2.put("Fathers_Name", ls2.getLname());
						obj2.put("Phone", ls2.getMobile());
						obj2.put("qualification", ls2.getQualification());
						obj2.put("username", ls2.getEmplyeeuserid());
						obj2.put("img",ls2.getEmpImage());
						arr.add(obj2);
					}
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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}

}
