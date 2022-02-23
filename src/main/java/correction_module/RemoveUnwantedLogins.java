package correction_module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class RemoveUnwantedLogins {
	
	public static void main(String[] args) {
		
Connection conn= DataBaseConnection.javaConnection();
		
		
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select id,name,user_type from all_user where schid='302' and user_type!='admin'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String user_type = rr.getString("user_type");
				String name = rr.getString("name");
				String id = rr.getString("id");
				
				boolean checkPresent = false; 
				if(user_type.equalsIgnoreCase("student"))
				{
					checkPresent = findStudentPresent(name,conn);
				}
				else
				{
					checkPresent = findEmployeePresent(name,conn);
				}
			
				if(checkPresent)
				{
					
				}
				else
				{	
				 java.sql.PreparedStatement stt =null;
				 try {
					String query2 = "delete from all_user where id=?";
					
					stt=conn.prepareStatement(query2);
					
					stt.setString(1, id);
					
					
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
	
	private static boolean findStudentPresent(String studentid,Connection conn) {
		
		boolean check= false;
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select * from registration1 where addNumber='"+studentid+"' and schid='302'";
			rr = st.executeQuery(query);
			if(rr.next()) {
			
			    check = true;
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
		return check;
		
	}
	
private static boolean findEmployeePresent(String id,Connection conn) {
		
		boolean check= false;
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select * from employeeaddmission where empusername='"+id+"' and schid='302'";
			rr = st.executeQuery(query);
			if(rr.next()) {
			
			    check = true;
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
		return check;
		
	}

}
