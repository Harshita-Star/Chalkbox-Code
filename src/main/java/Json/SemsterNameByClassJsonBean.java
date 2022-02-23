package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
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

@ManagedBean(name="semsterNameByClassJsonBean")
@ViewScoped
public class SemsterNameByClassJsonBean implements Serializable{

	
	String json="";
	DataBaseMeathodJson obj=new DataBaseMeathodJson();

	
	public SemsterNameByClassJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid = params.get("schid");
			String classId=params.get("classId");
			
			
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = obj.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				ArrayList<SelectItem>termList=obj.selectedTermOfClass(classId,schid,conn);
				
				for(SelectItem ls:termList)
				{
					
					JSONObject obj1 = new JSONObject();
					obj1.put("termId", ls.getValue());
					obj1.put("termName",ls.getLabel());
					arr.add(obj1);
				}
			}
			
		   json=arr.toJSONString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			 try {
					conn.close();
					   
				} catch (Exception e) {
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
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
}
