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

@ManagedBean(name="editgroupmain")
@ViewScoped
public class EditGroupJson implements Serializable
{

	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EditGroupJson()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String groupId=params.get("groupId");
			sectionid=params.get("group");
			String AddmissionNo=params.get("addNumber");
			String SchoolId=params.get("Schoolid");
			String type=params.get("type");
			
			JSONObject mainobj = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				int i=DBJ.updategroup(sectionid,AddmissionNo,SchoolId,type,groupId,conn);
				if(i>0)
				{
					obj.put("status","Group Updated Successfully");
					arr.add(obj);

				}
				else
				{
					obj.put("status","Error occur");
					arr.add(obj);
				}
			}
			else
			{
				obj.put("status","Error occur");
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
