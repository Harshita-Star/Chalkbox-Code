package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="schoolMasterSideMenu")
@ViewScoped

public class SchoolMasterSideMenu implements Serializable
{
	String session = "";

	public SchoolMasterSideMenu()
	{
		Connection conn = DataBaseConnection.javaConnection();
		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}



}
