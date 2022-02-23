package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="chootuRechargeReport")
@ViewScoped
public class ChhotuRechargeReportBean implements Serializable{
	ArrayList<MessagePackInfo> schoolList = new ArrayList<>();

	public ChhotuRechargeReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().selectChhotuReport(conn);

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