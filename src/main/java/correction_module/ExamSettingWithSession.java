package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class ExamSettingWithSession {

	public static void main(String[] args) {
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			Statement st=conn.createStatement();
			Statement st1 = conn.createStatement();
			ExamSettingCopy exam=new ExamSettingCopy();
			ResultSet rr=null,rs=null;
			String query="select schid from exam_setting where session = '2020-2021' group by schid";
			rr=st.executeQuery(query);
			while(rr.next()) {
				query = "select schid from exam_setting where session = '2021-2022' and schid = '"+rr.getString("schid")+"'";
				rs = st1.executeQuery(query);
				if(rs.next()) {
					
				}else 
				{
					 // System.out.println(rr.getString("schid"));
//					exam.copySetting(rr.getString("schid"));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
