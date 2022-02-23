package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="tempLicenceReport")
@ViewScoped

public class TempLicenceReportBean implements Serializable
{
	ArrayList<MessagePackInfo> schoolList = new ArrayList<>();

	public TempLicenceReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DataBaseMethodsReports().tempLicenceReport(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ArrayList<MessagePackInfo> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<MessagePackInfo> schoolList) {
		this.schoolList = schoolList;
	}
}
