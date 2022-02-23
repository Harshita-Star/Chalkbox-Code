package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MeetingInfo;
import schooldata.PendingTranctionList;
import schooldata.SchoolInfoList;
import trigger_module.DataBaseMethods;


@ManagedBean(name="AutoMeetingNotificationBean")
@ViewScoped
public class AutoMeetingNotificationBean implements Serializable{

	ArrayList<PendingTranctionList>list=new ArrayList<>();

	String json="";
	ArrayList<MeetingInfo> meetingList = new ArrayList<MeetingInfo>();
	DataBaseMethods obj=new DataBaseMethods();
	DatabaseMethods1 db = new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	public AutoMeetingNotificationBean() 
	{
		// TODO Auto-generated method stub
		
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
 
		  meetingList =allMeetingsWithinThirtyMinutes(conn);
		  
		  for(MeetingInfo mi : meetingList) {
		
			String message = "Your Virtual Class for Subject - "+mi.getSubjectName()+" will start from("+mi.getStartTimeStr()+") on "+mi.getAddDateStr()+". Kindly join your class by time."; 
			
			ls=db.fullSchoolInfo(mi.getSchid(), conn);
			
			String concernnnn = "";
			if(mi.getSchid().equalsIgnoreCase("216")||mi.getSchid().equalsIgnoreCase("221")||mi.getSchid().equalsIgnoreCase("302")) {
				
				
				String userType = db.userTypeOfUser(mi.getAddedbyUsername(),mi.getSchid(),conn);
				
				if(userType.equalsIgnoreCase("admin")){
					concernnnn = "Dear Student,\n"+message+"\nRegards\n"+ls.getSmsSchoolName();
				}
				else {
					String empNameByUsername = db.employeeNameByuserNameWithSchid(mi.getAddedbyUsername(),mi.getSchid(), conn);
					concernnnn = "Dear Student,\n"+message+"\nRegards\n"+empNameByUsername;
				}
				
			}
			else {
				concernnnn = "Dear Parent,\n"+message+"\nRegards\n"+ls.getSmsSchoolName();
			}
			
			
			db.notification(mi.getSchid(),"Meeting",concernnnn,mi.getSectionId()+"-"+mi.getClassId()+"-"+mi.getSchid(),conn);
			
			obj.changeStatusofMeetingNotification("off",mi.getId(),mi.getSchid(),conn);
			
		  }
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			json="done";
		
	}
	
protected ArrayList<MeetingInfo> allMeetingsWithinThirtyMinutes(Connection conn) {
		
		DatabaseMethods1 objDM = new DatabaseMethods1();
		int count = 1;
		ArrayList<MeetingInfo> list = new ArrayList<>();
		

		Statement st = null;
		ResultSet rr = null;
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		Date todayDate = new Date();
		String todayDateStr = formatter3.format(todayDate);
        String startTimeFilter = formatter4.format(todayDate);
	
		try {
			st = conn.createStatement();
			

			String qq = "select * from meeting where status='active' and trigger_status='on' and add_date='"+todayDateStr+"' and start_time>'"+startTimeFilter+"' and TIMESTAMPDIFF(MINUTE,NOW(),start_time) <= 30";
			

			rr = st.executeQuery(qq);
			while (rr.next()) {
				MeetingInfo ii = new MeetingInfo();
				ii.setSno(String.valueOf(count++));
				ii.setId(rr.getString("id"));
				ii.setAddDate(rr.getDate("add_date"));
				ii.setAddDateStr(formatter1.format(ii.getAddDate()));
				ii.setStartTime(rr.getTimestamp("start_time"));
				ii.setStartTimeStr(formatter2.format(ii.getStartTime()));
				ii.setEndTime(rr.getTimestamp("end_time"));
				ii.setEndTimeStr(formatter2.format(ii.getEndTime()));
				ii.setZoomId(rr.getString("zoom_id"));
				ii.setClassId(rr.getString("class_id"));
				ii.setSubjectId(rr.getString("subject_id"));
				ii.setSectionId(rr.getString("section_id"));
			    ii.setSubjectName(objDM.subjectNameFromid(rr.getString("subject_id"), conn));
	            ii.setSectionName(objDM.sectionNameByIdSchid(rr.getString("schid"), ii.getSectionId(), conn));
	            ii.setAddedbyUsername(rr.getString("addedby_username"));
	            ii.setEditedbyUsername(rr.getString("editedby_username"));
	            ii.setSchid(rr.getString("schid"));


				list.add(ii);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
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
		public String getJson() {
			return json;
		}
		public void setJson(String json) {
			this.json = json;
		}
	
}
