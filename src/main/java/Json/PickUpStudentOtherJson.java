 package Json;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.model.SelectItem;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

public class PickUpStudentOtherJson
{
	SchoolInfoList ls = new SchoolInfoList();
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	ArrayList<ClassInfo> selectedClassList;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public void pickup(String gType,String gRelation,String gName,String gMobileNo,String gImage,String addNo,String remark, String schid, String userid)
	{
		Connection conn=DataBaseConnection.javaConnection();
        try {
        	StudentInfo selectedGuardian=new StudentInfo();
    		selectedGuardian.setName(gType);
    		selectedGuardian.setRelation(gRelation);
    		selectedGuardian.setFname(gName);
    		selectedGuardian.setContactNo(gMobileNo);
    		selectedGuardian.setSignImage(gImage);
    		
    		StudentInfo ls = DBJ.studentDetailslistByAddNo(addNo, schid, conn);

    		DBM.pickUpStudent(addNo,remark,selectedGuardian,schid,conn);

    		if(ls.getFathersPhone()==ls.getMothersPhone())
    		{
    			DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    					ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
    		}
    		else
    		{
    			DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    					ls.getFathersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
    			DBJ.notification("Security", "Your ward "+ls.getFname()+" has been picked up from school by "+gName+" ("+gRelation+").",
    					ls.getMothersPhone()+"-"+ls.getAddNumber() + "-" + schid, schid,"", conn);
    		}
    		
    		String currentStop = DBJ.studentCurrentStop(addNo,schid,conn);
    		if(!currentStop.equalsIgnoreCase("no"))
    		{
    			String currentRoute = DBJ.routeIdFromStopGroupId(currentStop, DBM.selectedSessionDetails(schid,conn), conn, schid);
    			String rem = "Left With Parents";
    			String pickTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
    			DBJ.insertStudentPickDetail(addNo,schid,currentStop,currentRoute,rem,new Date(),"no","yes","schoolpick",userid,pickTime,conn);
    			DBJ.insertStudentDropDetail(addNo, schid, currentStop, currentRoute, rem, new Date(),"no","yes","drop",userid,pickTime, conn);
    		}
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


}
