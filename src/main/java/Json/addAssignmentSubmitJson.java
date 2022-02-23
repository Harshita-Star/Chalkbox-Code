package Json;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import schooldata.DataBaseConnection;


public class addAssignmentSubmitJson implements Serializable {
	

	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();



	public void addOnlineLacture(String userid,String lectureId,String desc,String schid,String image,String addedBy,String type) 
	{
		
		java.sql.Connection conn=DataBaseConnection.javaConnection();
		
		try {
			Date pDate=new Date();
			
			int i=DBJ.submitAssigment(userid,lectureId ,pDate,image, desc,schid,addedBy,type,conn);
		} catch (Exception e) {
			e.printStackTrace();
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
