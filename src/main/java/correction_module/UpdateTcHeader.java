package correction_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;

@ManagedBean(name="updateTCHeader")
@ViewScoped
public class UpdateTcHeader implements Serializable{
	
	public void generate() {
		
Connection conn= DataBaseConnection.javaConnection();
		
		
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			
			String query = "select marksheet_header,id from schoolinfo where marksheet_header!=''";
			rr = st.executeQuery(query);
			while (rr.next()) {
				
				String tcHeader = rr.getString("marksheet_header");
				String id = rr.getString("id");
			
				java.sql.PreparedStatement stt =null;
				try {
					String query2 = "update schoolinfo set tc_header=? where id=?";
					
					stt=conn.prepareStatement(query2);
					stt.setString(1, tcHeader);
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
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TC headers Updated"));
		
	}

}
