package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

@ManagedBean(name="onlineLectureFilterJson")
@ViewScoped
public class OnlineLectureFilterJsonBean implements Serializable{

	 String json="";
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	
	public OnlineLectureFilterJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String studentid = params.get("studentid");
			String date = params.get("date");
			String enddate = params.get("endDate");
			String selectedSection = params.get("sectionid");
			String selectedCLassSection = params.get("classid");
			String subjectid = params.get("subjectid");
			String schid=params.get("Schoolid");
	        String title=params.get("title");
	       
	        
	        JSONArray arr=new JSONArray();

	        Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
			
			if(checkRequest)
			{
				list = DBJ.viewSubjectOnlineLecturejsonFilter(selectedSection, date, enddate, schid, conn, selectedCLassSection, subjectid,title,studentid);
				SchoolInfoList list1=DBJ.fullSchoolInfo(schid,conn);
				
				if(schid.equals("216") || schid.equals("221")) {
					list = new DataBaseMeathodJson().checkForOptionSubjectsAllocation(list,studentid,schid,conn);
				}
				for(StudentInfo ss:list)
				{
					JSONObject obj = new JSONObject();
					boolean check=false;
					if(ss.getSubjectStudied().equalsIgnoreCase("all"))
					{
						check=true;
						obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
						obj.put("Subject", ss.getAss3());
						obj.put("id", ss.getId());
						obj.put("desc", ss.getDescription());
						obj.put("images", ss.getLactureImage());
						obj.put("title",ss.getTitle());
						obj.put("youtubelink",ss.getLink());
						obj.put("path",list1.getDownloadpath());
						obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
						obj.put("assignment", ss.getAssignmentSubmit());
						obj.put("views",ss.getViews());
						
					}
					else
					{
						String subjectDetails=DBJ.subjectNameAndTypeFromidwithoutStatus(ss.getSubjectStudied(), schid, conn);

						String[] temp=subjectDetails.split(",");
						if(temp[1].equalsIgnoreCase("Mandatory"))
						{
							check=true;
							obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
							obj.put("Subject", ss.getAss3());
							obj.put("id", ss.getId());
							obj.put("desc", ss.getDescription());
							obj.put("images", ss.getLactureImage());
							obj.put("title",ss.getTitle());
							obj.put("youtubelink",ss.getLink());
								obj.put("path",list1.getDownloadpath());
							obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
							obj.put("assignment", ss.getAssignmentSubmit());
							obj.put("views",ss.getViews());
							
						}
						else
						{
							check=DBJ.checkOptionalDetails(studentid,ss.getSubjectStudied(),schid,conn);
							obj.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(ss.getDate()));
							obj.put("Subject", ss.getAss3());
							obj.put("id", ss.getId());
							obj.put("desc", ss.getDescription());
							obj.put("images", ss.getLactureImage());
							obj.put("title",ss.getTitle());
							obj.put("youtubelink",ss.getLink());
							obj.put("path",list1.getDownloadpath());
							obj.put("className", ss.getClassFromId()+"-"+ss.getSectionName());
							obj.put("assignment", ss.getAssignmentSubmit());
							obj.put("views",ss.getViews());
							
						}
					}

					
					
								arr.add(obj);
					
				}
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
	
	
	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}

	
	
}
