package Json;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

public class AddVisitorImageJsonBean
{
	DatabaseMethods1 DBM=new DatabaseMethods1();
	
	public void addVisitor(Date addDate,String visitorNo,String name,String mobileNo,String address,String toMeet,String purpose,String otherDetails,String noOfPerson,String personName,String image,String inTime,String toMeetName,String schId)
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			if(otherDetails==null || otherDetails.equalsIgnoreCase(""))
			{
				otherDetails = "";
			}
			DBM.addVisitor(addDate,visitorNo,name,mobileNo,address,toMeet,purpose,otherDetails,noOfPerson,personName,image,conn,inTime,toMeetName,schId);
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
}
