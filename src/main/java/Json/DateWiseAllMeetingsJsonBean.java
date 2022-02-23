package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DBMethodsNew;
import schooldata.DataBaseConnection;
import schooldata.MeetingInfo;

@ManagedBean(name = "dateWiseAllMeetingsJsonBean")
@ViewScoped

public class DateWiseAllMeetingsJsonBean implements Serializable
{
	String json;
	boolean showAdmin;
	String empId;
	SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	ArrayList<MeetingInfo>list=new ArrayList<>();
	
	public DateWiseAllMeetingsJsonBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DBMethodsNew dbNewObj = new DBMethodsNew();
		
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String userType = params.get("usertype");
			String username = params.get("username");
			String classid = params.get("classid"); //only needed for admin, academic coordinator, authority, principal, vice principal, front office, Administrative Officer
			String schoolid=params.get("schid");
			String stDate = params.get("start_date"); // dd/MM/yyyy
			String enDate = params.get("end_date"); // dd/MM/yyyy
			
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("academic coordinator")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office")
					|| userType.equalsIgnoreCase("office staff") 
					|| userType.equalsIgnoreCase("Administrative Officer")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				showAdmin=true;
				empId=username;
				if(!userType.equalsIgnoreCase("admin"))
				{
					empId=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				}
			}
			else
			{
				empId=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				showAdmin=false;
			}
			
			Date startDate=null;
			Date endDate=null;
			try 
			{
				startDate = sf.parse(stDate);
				endDate = sf.parse(enDate);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();

			if(checkRequest)
			{
				if(showAdmin)
				{
					list=dbNewObj.allMeetingForManage(startDate,endDate,classid,schoolid,conn);
				}
				else
				{
					list=dbNewObj.allMeetingForManageTeacher(startDate, endDate, empId,schoolid, conn);
				}

				for(MeetingInfo ls:list)
				{
					JSONObject obj = new JSONObject();
					obj.put("id",ls.getId());
					obj.put("meetingId",ls.getMeetingId());
					obj.put("joinUrl",ls.getZoomId());
					
					obj.put("addDate", ls.getAddDateStr());
					obj.put("startTime", ls.getStartTimeStr());
					obj.put("endTime", ls.getEndTimeStr());
					
					obj.put("className",ls.getClassName());
					obj.put("sectionName",ls.getSectionName());
					obj.put("subjectName", ls.getSubjectName());
					obj.put("classId",ls.getClassId());
					obj.put("sectionId",ls.getSectionId());
					obj.put("subjectId", ls.getSubjectId());
					
					obj.put("add_by", ls.getAddedbyUsername());
					obj.put("edit_by", ls.getEditedbyUsername());
					obj.put("student_visible", ls.getStudentVisibleStatus());
					
					obj.put("join_button_disable", ls.isMeetingShowStatus());
					obj.put("join_button_title", ls.getMsg());
					
					obj.put("delete_button_disable", ls.isDisableDelete());
					obj.put("edit_button_disable", ls.isDisableEdit());
					obj.put("addon_teacher", ls.getAddTeacherName());
					
					arr.add(obj);
				}
			}

			json=arr.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
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
