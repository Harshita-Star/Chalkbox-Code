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

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
@ManagedBean(name="thoughtOfTheDayJson")
@ViewScoped
public class ThoughtOfTheDayJson implements Serializable
{
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public ThoughtOfTheDayJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
         try {
        	 JSONArray arr=new JSONArray();
     		String fileName = "";
     		
     		Map<String, String> sysParams =FacesContext.getCurrentInstance().
     				getExternalContext().getRequestHeaderMap();
     		String userAgent = sysParams.get("User-Agent");
     		
     		
     		Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
     		String schoolid=params.get("schid");
     		
     		if(schoolid==null||schoolid.equals(""))
     		{
     		  schoolid="all";	
     		}
     		
     		
     		 // System.out.println(schoolid);
     		
     		
     		
     		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
     		
     		if(checkRequest)
     		{
     			fileName =DBJ.thoughtOfDayDetail(schoolid,conn);
     			JSONObject obj = new JSONObject();
     			obj.put("file",fileName);
     			obj.put("path","http://www.chalkboxerp.in/CBXMASTER/");
     			arr.put(obj);
     		}
     		
     		json=arr.toString();
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
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();
	}
}
