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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="addgroup")
@ViewScoped
public class AddGroupDesign implements Serializable {


	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();

	public AddGroupDesign()
	{
		Connection conn = DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();

			sectionid = params.get("group");
			String AddmissionNo = params.get("addNumber");
			String SchoolId = params.get("Schoolid");
			String type = params.get("type");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			int i = 0;
			
			if(checkRequest)
			{
				String session = DBM.selectedSessionDetails(SchoolId,conn);
				i = DBJ.addgroup(sectionid, AddmissionNo, SchoolId, type,session, conn);
			}
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr = new JSONArray();

			JSONObject obj = new JSONObject();

			if (i > 0) {
				obj.put("status", "Group Added Successfully");

				arr.add(obj);

			} else {
				obj.put("status", "Error occur");

				arr.add(obj);

			}

			mainobj.put("SchoolJson", arr);

			json = mainobj.toJSONString();
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
