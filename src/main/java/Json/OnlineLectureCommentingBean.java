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

import org.json.simple.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.StudentInfo;

@ManagedBean(name="onlineLectureCommentBean")
@ViewScoped
public class OnlineLectureCommentingBean implements Serializable
{
	
	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public OnlineLectureCommentingBean() {

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String id = params.get("id");
			String username = params.get("username");
			String comment = params.get("comment");
			String schid= params.get("schid");
			String usertype= params.get("type");
	        JSONArray arr=new JSONArray();

	        Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		    if(checkRequest)
			{
		    	
		    	
				int i=DBJ.addLectureComment(id,username,comment,schid,usertype,conn);
				if(i>0)
				{
					String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
					StudentInfo ls=DBJ.viewSubjectOnlineLecturejsonByID(id, schid, conn);
					JSONObject obj = new JSONObject();
					if(!usertype.equalsIgnoreCase("student"))
					{
						//DBJ.notification("E-Learning","An enquiry is posted on discussion forum","admin-"+schid,schid,"",conn);if(
						String message="New message for <"+ls.getAss3()+   "> <"+ls.getTitle()+" >.";
						
						if(ls.getSectionid().equalsIgnoreCase("All"))
						{
							DBJ.notification("E-Learning",message,ls.getClasid()+"-"+schid,schid,"",conn);
						}
						else
						{
							DBJ.notification("E-Learning",message,ls.getSectionid()+"-"+ls.getClassId()+"-"+schid,schid,"",conn);
						}
					}
					else
					{
						//System.out.println(username);
						String studentName = DBM.studentNameById(schid, username, conn);
						String teacher=DBJ.TeacherBySectionAllocat(ls.getClassId(), ls.getSectionid(), ls.getSubjectStudied(), schid, conn);
						//System.out.println(studentName);
						String message = "Dear User,\n"
								+ "An Enquiry is being posted by <"+studentName+"> regarding the <"+ls.getTitle()+"> given in <"+ls.getAss3()+"> of class <"+ls.getClassFromId()+">.\n"
								+ "Team Chalkbox";
						//System.out.println(message);
						//String message="An enquiry is posted on discussion forum <"+ls.getClassFromId()+"-"+ls.getSectionName()+"> <"+ls.getAss3()+   "> <"+ls.getTitle()+" >.";
						//System.out.println("Teacher : "+teacher);
						if(!teacher.equals(""))
						{
							String[] tt=teacher.split(",");
							for(int ii=0;ii<tt.length;ii++)
							{
								DBJ.adminnotification("E-Learning",message,"staff"+"-"+tt[ii]+"-"+schid,schid,"",conn);
								
							}
						}
						
						ArrayList<EmployeeInfo> cordinators = DBJ.cordinatorListByClassId(ls.getClassId(), schid, session, "active", conn);
						//System.out.println("cordinators : "+cordinators.size());
						for(EmployeeInfo empi : cordinators) 
						{
							DBJ.adminnotification("E-Learning",message,"staff"+"-"+empi.getId()+"-"+schid,schid,"",conn);
						}
						//DBJ.adminnotification("E-Learning",message,"admin-"+schid,schid,"",conn);
					}
					obj.put("message","Updated");
					arr.add(obj);
				
				}
				else
				{
					JSONObject obj = new JSONObject();
					obj.put("message"," not Updated");
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
			} catch (Exception e2) {
				e2.printStackTrace();
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
