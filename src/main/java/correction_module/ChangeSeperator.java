package correction_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import schooldata.DataBaseConnection;

public class ChangeSeperator {

	public static void main(String[] args) {
		Connection conn = DataBaseConnection.javaConnection();
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		try {
			st = conn.createStatement();
			String query = "select otherValues,id from exam_setting";
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (rs.getString("otherValues") != null && !rs.getString("otherValues").equals("")) {
					if (rs.getString("otherValues").contains(",")) {
						String a = rs.getString("otherValues").replace(",", "%@%");
						String sql = "update exam_setting set otherValues = ? where id = ?";
						ps = conn.prepareStatement(sql);
						ps.setString(1, a);
						ps.setString(2, rs.getString("id"));
						i = ps.executeUpdate();
					}
				}
			}
			if (i > 0) {
				 // System.out.println("UPDATED");
			}else {
				 // System.out.println("SOME ERROR OR ALREADY UPDATED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
