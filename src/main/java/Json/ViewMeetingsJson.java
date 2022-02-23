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
import schooldata.MeetingInfo;
@ManagedBean(name="viewMeetingsJson")
@ViewScoped


public class ViewMeetingsJson implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public ViewMeetingsJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schoolId = params.get("schid");
			String meetingType = params.get("meetingType");
			String classId = params.get("classId");
			String sectionId = params.get("sectionId");
//			String studentId = params.get("studentid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
            checkRequest = true;
			if(checkRequest)
			{
				ArrayList<MeetingInfo> list= new ArrayList<MeetingInfo>();

				list=DBJ.viewMeetingDetails(classId,sectionId,schoolId,meetingType,conn);
				
//				if(schoolId.equals("216") || schoolId.equals("221")) {
//					list = DBJ.checkForOptionSubjectsAllocationforLiveClass(list, studentId,schoolId,conn);
//				}
				for(MeetingInfo ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("sno",ls.getSno());
					obj.put("id",ls.getId());
					obj.put("addDate",String.valueOf(ls.getAddDate()));
					obj.put("addDateStr", ls.getAddDateStr());
					obj.put("startTime",timeFormat.format(ls.getStartTime()));
					obj.put("startTimeStr",ls.getStartTimeStr());
					obj.put("endTime",timeFormat.format(ls.getEndTime()));
					obj.put("endTimeStr", ls.getEndTimeStr());
					obj.put("meetingId",ls.getZoomId());
					obj.put("classId",ls.getClassId());
					obj.put("subjectId",ls.getSubjectId());
					obj.put("subjectName", ls.getSubjectName());
					obj.put("sectionId",ls.getSectionId());
					obj.put("sectionName", ls.getSectionName());
					obj.put("addedByUsername",ls.getAddedbyUsername());
					obj.put("editedByUsername", ls.getEditedbyUsername());
			
					arr.add(obj);
				}
				
			}
			
			json=arr.toJSONString();
		}
		catch (Exception e) {
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
