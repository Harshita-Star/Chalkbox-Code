package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="addMeetingJson")
@ViewScoped
public class AddMeetingJson implements Serializable
{
	String json;
	String username,schid,classId,sectionsId,subjectId,addDateStr,startTimeStr,endTimeStr,zoomId, userType;
	String email,zoomMeetingId,topic,studentVisibleStatus, addTeacher, teacherAdded;
	int duration;
	Date addDate,startTime,endTime;
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 dbm=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	//SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
	SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	ArrayList<String> sectionList = new ArrayList<String>();
	
	public AddMeetingJson()
	{
          
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			username = params.get("username");
			userType = params.get("usertype");
			schid=params.get("schid");
			classId = params.get("class_id");
			sectionsId = params.get("section_id");
			subjectId = params.get("subject_id");
			addDateStr = params.get("add_date");
			startTimeStr = params.get("start_time");
			endTimeStr = params.get("end_time");
			addTeacher = params.get("add_teacher");
			
			if(addTeacher == null || addTeacher.equals(""))
			{
				teacherAdded = "no";
				addTeacher = "";
			}
			else
			{
				teacherAdded = "yes";
			}
			

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			String status="";
			if(checkRequest)
			{
				ls=DBJ.fullSchoolInfo(schid,conn);
				
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
					if(userType.equalsIgnoreCase("admin"))
					{
						studentVisibleStatus = "yes";
						email = ls.getEmailId().trim();
					}
					else
					{
						String empid=DBJ.employeeIdbyEmployeeName(username,schid,conn);
						email = DBJ.employeeContactById(empid, "email", conn).trim();
						studentVisibleStatus = "no";
						if(ls.getMeetingApproval().equalsIgnoreCase("No"))
						{
							studentVisibleStatus = "yes";
						}
					}
				}
				else
				{
					String empid=DBJ.employeeIdbyEmployeeName(username,schid,conn);
					email = DBJ.employeeContactById(empid, "email", conn).trim();
					studentVisibleStatus = "no";
					if(ls.getMeetingApproval().equalsIgnoreCase("No"))
					{
						studentVisibleStatus = "yes";
					}

				}
				
				
				String[] splitter = sectionsId.split(",");
				for (int i = 0; i < splitter.length; i++) {
					sectionList.add(splitter[i]);
				}
				
				addDate = formatter1.parse(addDateStr);
			
				startTime = formatter2.parse(addDateStr+" "+startTimeStr);
			
				endTime = formatter2.parse(addDateStr+" "+endTimeStr);
				
				if(startTime.after(endTime)) {
					status = "Start Time should be before End Time";
				}
				else if(email == null || email.equalsIgnoreCase(""))
				{
					status = "Email ID is Missing. Please contact administrator to update your Email Id.";
				}
				else
				{
					long difference_In_Time = endTime.getTime() - startTime.getTime();
					long difference_In_Minutes = (difference_In_Time / (1000 * 60));
					duration = Integer.parseInt(String.valueOf(difference_In_Minutes));
					if(duration>40)
					{
						duration = 40;
					}
					
					String addDateString = formatter4.format(startTime);
					String subjectName = dbm.subjectNameFromid(subjectId, conn);
					topic = subjectName + " - " + addDateString;
					
					String apiResult = createZoomMeetingAPI(topic, startTime, duration);
				
					if(apiResult.equalsIgnoreCase("success"))
					{
						if(zoomId.trim().equalsIgnoreCase(""))
						{
							status = "Unable to create Zoom meeting at the moment. Please Try Again Later!";
						}
						else
						{
							int k = dbm.addMeeting(classId,sectionList,subjectId,addDate,startTime,endTime,zoomId.trim(),schid,username,studentVisibleStatus,zoomMeetingId,addTeacher,teacherAdded,conn);
							if(k>0) 
							{
								
								status = "success";
								
								String message = "Meeting fixed for "+subjectName+" on "+addDateString+"("+startTimeStr+"-"+endTimeStr+") on zoom having id "+zoomId.trim();
								String concernnnn = "Dear Parent,\n"+message+"\nRegards,"+ls.getSmsSchoolName();
//								for(String cc : sectionList)
//								{
//									dbm.notification(schid,"Meeting",concernnnn,cc.toString()+"-"+classId+"-"+schid,conn);
//								}
							
							 }
							 else
							 {
							      status = "Some Went Terribly Wrong. Please Try Again Later!";
							 }	
						}
					}
					else
					{
						status = "Please contact administrator to configure your zoom account.";
					}
				}
				
			}
			else
			{
				status = "Authentication error";
				
			}
			

			obj.put("status",status);
			arr.add(obj);

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
	
	public String createZoomMeetingAPI(String topic, Date startDate, int duration)
	{
		String status = "error";
		String strStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate).replace(" ", "T");
		TreeMap<String, Object> paramMap = new TreeMap<>();
		paramMap.put("topic" ,topic);
		paramMap.put("type" ,"2");
		paramMap.put("start_time" ,strStartDate);
		paramMap.put("duration", duration);
		paramMap.put("password" ,"123456");
		org.primefaces.shaded.json.JSONObject obj = new org.primefaces.shaded.json.JSONObject(paramMap);
		String params = obj.toString();
		
		String jwt = ls.getJwt();
		
		try 
		{
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, params);
			Request request = new Request.Builder()
			  .url("https://api.zoom.us/v2/users/"+email+"/meetings")
			  .method("POST", body)
			  .addHeader("Authorization", "Bearer "+jwt)
			  .addHeader("Content-Type", "application/json")
			  .build();
			Response response = client.newCall(request).execute();
			ResponseBody respBody = response.body();
			org.primefaces.shaded.json.JSONObject robj = new org.primefaces.shaded.json.JSONObject(respBody.string());
			String msg = robj.optString("message");
			
			zoomId=zoomMeetingId="";
			status = msg;
			if(msg == null || msg.equals(""))
			{
				status = "success";
				zoomId = robj.getString("join_url");
				zoomMeetingId = String.valueOf(robj.getInt("id"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
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

