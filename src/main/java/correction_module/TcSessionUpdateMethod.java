package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

public class TcSessionUpdateMethod {
	
	DatabaseMethods1 dbm = new DatabaseMethods1();
	
	public static void main(String[] args) {
		
       Connection conn= DataBaseConnection.javaConnection();
		
		
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select id,studentid,schid,status from transfer_certificate where session=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
			
				
				String id = rr.getString("id");
				
				
				String session = "";
				if(rr.getString("status").equalsIgnoreCase("cancel"))
				{
					
					session = findSessionOfStudentCancel(rr.getString("studentid"),rr.getString("schid"),conn);
					
					if(session.equalsIgnoreCase(""))
					{
						session = findSessionOfStudent(rr.getString("studentid"),rr.getString("schid"),conn);	
					}
				}
				else
				{
				  session = findSessionOfStudent(rr.getString("studentid"),rr.getString("schid"),conn);

				}
				
				
				java.sql.PreparedStatement stt =null;
				try {
					String query2 = "update transfer_certificate set session=? where id=?";
					
					stt=conn.prepareStatement(query2);
					stt.setString(1, session);
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

	private static String findSessionOfStudentCancel(String studentId, String schid, Connection conn) {
		
		String sesssion="";
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select session from promotion where studentId='"+studentId+"' and schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
			 
				sesssion=rr.getString("session");
			    
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
		return sesssion;
	}

	private static String findSessionOfStudent(String studentid,String schid,Connection conn) {
		
		String sesssion="";
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select session from registration1 where addNumber='"+studentid+"' and schid='"+schid+"'";
			rr = st.executeQuery(query);
			while (rr.next()) {
			
			    sesssion = rr.getString("session");
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
		return sesssion;
		
	}

}
