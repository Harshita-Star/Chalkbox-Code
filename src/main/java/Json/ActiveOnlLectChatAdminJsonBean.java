package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="activeOnlLectChatAdminJson")
@ViewScoped

public class ActiveOnlLectChatAdminJsonBean implements Serializable
{
	String json;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public ActiveOnlLectChatAdminJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String username = params.get("username");
		    String usertype = params.get("usertype");
			String schid=params.get("Schoolid");
			
			JSONArray arr=new JSONArray();

	        Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
			
			if(checkRequest)
			{
				list = DBJ.viewSubjectOnlineLectureAdminjsonActive(username,usertype,schid, conn);
				Collections.sort(list, new MyRollNoComp());
				SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);

				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					
					obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
					obj.put("Subject", ss.getAss3());
					obj.put("id", ss.getId());
					obj.put("desc", ss.getDescription());
					obj.put("images", ss.getLactureImage());
					obj.put("title",ss.getTitle());
					obj.put("youtubelink",ss.getLink());
					obj.put("path",list1.getDownloadpath());
					obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
					obj.put("views",ss.getViews());
					
					
	                arr.add(obj);
				}
			}
			
			json=arr.toJSONString();
		}
		catch (Exception e) {
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
    
    
    class MyRollNoComp implements Comparator<StudentInfo>
	{
    		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{
			
			 try {
	                return f.parse(e2.getLatestChatTime()).compareTo(f.parse(e1.getLatestChatTime()));
	            } catch (ParseException e) {
	                throw new IllegalArgumentException(e);
	            }
		}
	}
}
