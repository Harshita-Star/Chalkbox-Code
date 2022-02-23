package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schooldata.DataBaseConnection;


public class CorrectStudentImagePath {
	
	public static void main(String[] args) {
		
		Connection conn= DataBaseConnection.javaConnection();
		
		
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select id,student_image_path from registration1 where student_image_path like  'http%' ";
			rr = st.executeQuery(query);
			while (rr.next()) {
			
				String check = rr.getString("student_image_path");
				int point = check.lastIndexOf("/");
				
				String id = rr.getString("id");
				
				String fin = check.substring(point+1, check.length());
				
				java.sql.PreparedStatement stt =null;
				try {
					String query2 = "update registration1 set student_image_path=? where id=?";
					
					stt=conn.prepareStatement(query2);
					stt.setString(1, fin);
					stt.setString(2, id);
					
					stt.executeUpdate();
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
		
	}

}
