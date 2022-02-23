package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import session_work.QueryConstants;

public class SendingNotifications implements Serializable{
	
	
	public void sendNotifi(String msg,String schid,String classId,String section,String session,String type,String studentInfo,String title) {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 dbmnew = new DatabaseMethods1();
		if(type.equalsIgnoreCase("admin")) {
			dbmnew.adminnotification(title, msg, "admin-" + schid,"", conn);
		}else if(type.equalsIgnoreCase("teacher")) {
//			System.out.println(schid);
//			System.out.println(msg);
//			System.out.println(studentInfo);
			if(studentInfo != null && !studentInfo.equals("")) {
				dbmnew.adminnotification(title, msg, "staff-" + studentInfo + "-" + schid,"", conn);
			}
		}else if(type.equals("Students")) {
			
			StudentInfo info = dbmnew.studentDetailslistByAddNo(schid, studentInfo, conn);
			
//			System.out.println(schid);
//			System.out.println(msg);
//			System.out.println(info.getFathersPhone());
//			System.out.println(info.getMothersPhone());
//			System.out.println(studentInfo);
//			System.out.println(session);
			
			dbmnew.notification(schid,title,msg, info.getFathersPhone()+"-"+studentInfo+"-"+schid,conn);
			dbmnew.notification(schid,title,msg, info.getMothersPhone()+"-"+studentInfo+"-"+schid,conn);
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
