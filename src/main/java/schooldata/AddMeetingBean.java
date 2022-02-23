package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.shaded.json.JSONObject;

import Json.DataBaseMeathodJson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@ManagedBean(name="addMeeting")
@ViewScoped
public class AddMeetingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList, subjectList, teacherList;
	ArrayList<String> selectedSectionList;
	String selectedCLassSection,userType,schoolid,studentVisibleStatus,
			 subject, type,staff,zoomId,email,zoomMeetingId, addTeacher;
	Date addDate,startTime,endTime;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	
	SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");

	public AddMeetingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		addDate = new Date();
		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		
		addTeacher = "";
		
		try
		{
			String categid = obj.employeeCategoryIdByName("Teacher", conn);
			teacherList = obj.allteacherOnly(categid,schoolid,conn);
			String empid = "";
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff") 
					|| userType.equalsIgnoreCase("Accounts"))
			{
				if(userType.equalsIgnoreCase("admin"))
				{
					classSection=obj.allClass(conn);
					studentVisibleStatus = "yes";
					email = ls.getEmailId().trim();
				}
				else
				{
					empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
					email = new DataBaseMeathodJson().employeeContactById(empid, "email", conn).trim();
					classSection=obj.allClass(conn);
					studentVisibleStatus = "no";
					if(ls.getMeetingApproval().equalsIgnoreCase("No"))
					{
						studentVisibleStatus = "yes";
					}
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				email = new DataBaseMeathodJson().employeeContactById(empid, "email", conn).trim();
 				classSection = obj.cordinatorClassList(empid, schoolid, conn);
 				studentVisibleStatus = "no";
				if(ls.getMeetingApproval().equalsIgnoreCase("No"))
				{
					studentVisibleStatus = "yes";
				}
 			}
			else
			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				email = new DataBaseMeathodJson().employeeContactById(empid, "email", conn).trim();
				classSection=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
				studentVisibleStatus = "no";
//				if(schoolid.equalsIgnoreCase("7")) {
//					studentVisibleStatus = "yes";
//				}
				if(ls.getMeetingApproval().equalsIgnoreCase("No"))
				{
					studentVisibleStatus = "yes";
				}

			}
			
			if(!userType.equalsIgnoreCase("admin"))
			{
				SelectItem ss = new SelectItem();
				loop:for(SelectItem si : teacherList)
				{
					if(si.getValue().equals(empid))
					{
						ss = si;
						break loop;
					}
				}
				
				if(teacherList.contains(ss))
				{
					teacherList.remove(ss);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		sectionList = new ArrayList<SelectItem>();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj.allSection(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedCLassSection, empid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public void checkType() {
//		
//		if(meetingType.equalsIgnoreCase("Youtube")) {
//			showYoutube =true;
//			showZoom =false;
//		}
//		else
//		{
//			showYoutube = false;
//			showZoom = true;
//		}
//		
//	}
	
	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection, conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(staff,schoolid,conn);
			subjectList = new ArrayList<SelectItem>();
			for(String ss : selectedSectionList) 
			{
				ArrayList<SelectItem> subList= new ArrayList<SelectItem>();	
				subList=objJson.AllEMployeeSubject(empid,ss,schoolid,conn);
				
				for(SelectItem jj: subList) {
					
					boolean check = false ;
					for(SelectItem pp: subjectList) {
						if(pp.getValue().equals(jj.getValue()))
							check = true;
					}
					
					if(check==false)
					subjectList.add(jj);	
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	


	public String addMeeting()
	{
	
		if(startTime.after(endTime)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Enter Start Time and End Time Properly", "Validation error"));
		}	
		else
		{
			String addDateStr = formatter1.format(addDate);
			String startDateStr = formatter3.format(startTime);
			startDateStr = addDateStr+" "+startDateStr;
			
			String endDateStr = formatter3.format(endTime);
			endDateStr = addDateStr+" "+endDateStr;
			
			int duration = 40;
			
			try {
			  startTime = formatter2.parse(startDateStr);
			  endTime = formatter2.parse(endDateStr);
			  
			  long difference_In_Time = endTime.getTime() - startTime.getTime();
			  long difference_In_Minutes = (difference_In_Time / (1000 * 60));
			  duration = Integer.parseInt(String.valueOf(difference_In_Minutes));
			  if(duration>40)
			  {
				  duration = 40;
			  }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if(email == null || email.equalsIgnoreCase(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Email ID is Missing. Please contact administrator to update your Email Id.", "Validation error"));
			}
			else if(selectedSectionList.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Select atleast 1 section", "Validation error"));
			}
			else 
			{
				Connection conn=DataBaseConnection.javaConnection();
				
				String topic = obj.subjectNameFromid(subject, conn) + " - " + startDateStr;
				
				String apiResult = createZoomMeetingAPI(topic, startTime, duration);
				
				if(apiResult.equalsIgnoreCase("success"))
				{
					if(zoomId.trim().equalsIgnoreCase(""))
					{
						try {
							conn.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Unable to create Zoom meeting at the moment. Please Try Again Later!", "Validation error"));
						return ""; 
					}
					else
					{
						String teacherAdded = "no";
						if(!addTeacher.equals(""))
						{
							teacherAdded = "yes";
						}
						int k = obj.addMeeting(selectedCLassSection,selectedSectionList,subject,addDate,startTime,endTime,zoomId.trim(),schoolid,staff,studentVisibleStatus,zoomMeetingId, addTeacher, teacherAdded, conn);
						if(k>0)
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meeting Added Successfully"));
							
							boolean checkMeetingNotificationStatus = obj.checkMeetingNotificationStatus(schoolid,conn);
							if(checkMeetingNotificationStatus) 
							{
								String startTimeStr = formatter3.format(startTime);
								String endTimeStr = formatter3.format(endTime);
								String addDateString = formatter1.format(addDate);
								String subjectName = obj.subjectNameFromid(subject, conn);
								
								String message = "Meeting fixed for "+subjectName+" on "+addDateString+"("+startTimeStr+"-"+endTimeStr+").";
								String concernnnn = "";
								if(schoolid.equalsIgnoreCase("216")||schoolid.equalsIgnoreCase("221")||schoolid.equalsIgnoreCase("302")) {
									
									if(userType.equalsIgnoreCase("admin")){
										concernnnn = "Dear Student,\n"+message+"\nRegards\n"+ls.getSmsSchoolName();
									}
									else {
										String empNameByUsername = obj.employeeNameByuserName(staff, conn);
										concernnnn = "Dear Student,\n"+message+"\nRegards\n"+empNameByUsername;
									}
									
								}
								else {
									concernnnn = "Dear Parent,\n"+message+"\nRegards\n"+ls.getSmsSchoolName();
								}
								
								/*for(String cc : selectedSectionList)
								{
									obj.notification(schoolid,"Meeting",concernnnn,cc.toString()+"-"+selectedCLassSection+"-"+schoolid,conn);
								}*/
							}
						
							 try {
								conn.close();
							 } catch (Exception e) {
								e.printStackTrace();
							 } 
						
							 return "addMeeting.xhtml";
						}
						else
						{
						   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Some Went Terribly Wrong. Please Try Again Later!", "Validation error"));
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							return ""; 
						}
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,apiResult+" Please contact administrator to configure your zoom account.", "Validation error"));
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return ""; 
				}
				
				
			}
		}
		return "";
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
		JSONObject obj = new JSONObject(paramMap);
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
			String strResponse = respBody.string();
			JSONObject robj = new JSONObject(strResponse);
			String msg = robj.optString("message");
			
			zoomId=zoomMeetingId="";
			status = msg;
			if(msg == null || msg.equals(""))
			{
				status = "success";
				zoomId = robj.getString("join_url");
				zoomMeetingId = String.valueOf(robj.getLong("id"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
	

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}




	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSelectedSectionList() {
		return selectedSectionList;
	}

	public void setSelectedSectionList(ArrayList<String> selectedSectionList) {
		this.selectedSectionList = selectedSectionList;
	}



	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}


	public String getZoomId() {
		return zoomId;
	}

	public void setZoomId(String zoomId) {
		this.zoomId = zoomId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStudentVisibleStatus() {
		return studentVisibleStatus;
	}

	public void setStudentVisibleStatus(String studentVisibleStatus) {
		this.studentVisibleStatus = studentVisibleStatus;
	}

	public ArrayList<SelectItem> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<SelectItem> teacherList) {
		this.teacherList = teacherList;
	}

	public String getAddTeacher() {
		return addTeacher;
	}

	public void setAddTeacher(String addTeacher) {
		this.addTeacher = addTeacher;
	}
	
	



}

