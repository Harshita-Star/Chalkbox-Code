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
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.EmployeeInfo;
import schooldata.StudentInfo;


@ManagedBean(name="viewOnlineLectureComment")
@ViewScoped
public class ViewOnlineLectureCommentBean implements Serializable 
{
	
	String json="";
    public ViewOnlineLectureCommentBean() 
    {

    	Connection conn=DataBaseConnection.javaConnection();
    	try {
    		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
    		ArrayList<Online_Lecture>list=new ArrayList<>();
			
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id = params.get("id");
			String username = params.get("schid");
			String schid = params.get("username");
			
			// // System.out.println(username+"-------"+schid);
			JSONArray arr=new JSONArray();

	        Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
			if(checkRequest)
			{
				list= DBJ.viewOnlineLectureComment(id,conn);
				
				for(Online_Lecture ls:list)
				{
					JSONObject obj = new JSONObject();
					
					if(ls.getUsertype().equalsIgnoreCase("admin"))
					{
						if(ls.getUsername().equalsIgnoreCase(username))
						{
							obj.put("name","You");
						}
						else
						{
							obj.put("name","School Admin");
						}
						
				
					}
					else
					{
						
						if(ls.getUsertype().equalsIgnoreCase("student"))
						{
							if(ls.getUsername().equalsIgnoreCase(username))
							{
								obj.put("name","You");
							}
							else 
							{
								StudentInfo info=DBJ.studentDetailslistByAddNo(ls.getUsername(),schid, conn);
								obj.put("name",info.getFullName());
							}
						}
						else
						{
							if(ls.getUsername().equalsIgnoreCase(username))
							{
								obj.put("name","You");
							}
							else
							{
								EmployeeInfo info=DBJ.searchEmployeeUsername(ls.getUsername(), schid, conn);
								obj.put("name",info.getFname());
							}
					
						}
						
						
					}
					
					obj.put("comment", ls.getComment());
					obj.put("add_time", ls.getComment_time());
					
					arr.add(obj);
				}
			}
			
			json=arr.toJSONString();
    			
		} catch (Exception e) {
			e.printStackTrace();
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
