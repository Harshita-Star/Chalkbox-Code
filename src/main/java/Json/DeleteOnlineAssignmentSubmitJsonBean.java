package Json;

import java.io.File;
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

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="deleteOnlineAssignmentSubmitJson")
@ViewScoped
public class DeleteOnlineAssignmentSubmitJsonBean implements Serializable {
	
	
	 String json="";
		ArrayList<StudentInfo> list;
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	    public DeleteOnlineAssignmentSubmitJsonBean() {

	    	Connection conn=DataBaseConnection.javaConnection();

	    	try {
	    		Map<String, String> params =FacesContext.getCurrentInstance().
						getExternalContext().getRequestParameterMap();
				String studentid = params.get("studentid");
			    String lectureId = params.get("lectureId");
				String schid=params.get("Schoolid");
				
				 JSONArray arr=new JSONArray();

			        Map<String, String> sysParams =FacesContext.getCurrentInstance().
							getExternalContext().getRequestHeaderMap();
					String userAgent = sysParams.get("User-Agent");
					boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
				
					
					if(checkRequest)
					{
						
						 StudentInfo list=DBJ.submitAssignmentStudent(studentid,lectureId,schid,conn);
						  SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);
						  
					  int  i=DBJ.deleteAssignmentStudent(studentid,lectureId,schid,conn);
					 
					  
					  JSONObject obj = new JSONObject();
					   if(i>0)
					   {
						 
						   String[] images=list.getLactureImage().split(",");
						   
						   for(int j=0;j<images.length;j++)
						   {
							   File file = new File(list1.getUploadpath()+images[j]);
								file.delete();
					    	
						   }
						   
						   obj.put("status", "1");
					   }
					   else
					   {
						   obj.put("status", "0");
					   }
						
						arr.add(obj);
					  

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
