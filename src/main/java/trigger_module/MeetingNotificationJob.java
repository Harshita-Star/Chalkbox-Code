package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.MeetingInfo;
import schooldata.SchoolInfoList;

public class MeetingNotificationJob implements Job{
	
	ArrayList<MeetingInfo> meetingList = new ArrayList<MeetingInfo>();
	DataBaseMethods obj = new DataBaseMethods();
	DatabaseMethods1 db = new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
 
		  meetingList = obj.allMeetingsWithinThirtyMinutes(conn);
		  
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
		
	}



}
