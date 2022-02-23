package correction_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class ChangeStudentResultDeclareInPerformanceTable implements Serializable{
	
	
	public static void main(String[] args) {
	
		
        Connection conn= DataBaseConnection.javaConnection();
		
		int k=0;
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select * from exam_table_cbse where declareResult='no' and status='ACTIVE'";
			rr = st.executeQuery(query);
			while (rr.next()) {
					
				 // System.out.println(rr.getString("id"));
				java.sql.PreparedStatement stt =null;
				try {
					String query2 = "update student_performance_cbse set declareResult='no' where mainExamId='"+rr.getString("id")+"' and session='"+rr.getString("session")+"' and schId='"+rr.getString("schId")+"'";
					 // System.out.println(query2);
					
					stt=conn.prepareStatement(query2);
					
					
					
				k =stt.executeUpdate();
				 // System.out.println(k);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (stt != null) {
						try {
							stt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
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
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(k>0)
		{
			 // System.out.println("Done");	
		}
		else
		{
			 // System.out.println("Error Occured");	
		}
		
		
	}

}
