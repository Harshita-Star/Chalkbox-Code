import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.StudentInfo;


public class MAINCLASS {

	public static void main(String[] args) {
	
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<StudentInfo>list=new DataBaseMeathodJson().transportList("216","2021-2022",conn);
		
		for(StudentInfo ls:list)
		{
			new DataBaseMeathodJson().updateTransport(ls.getAddNumber(),ls.getTransport(),"216","2021-2022",conn);
		    // System.out.println(ls.getAddNumber());
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

}
