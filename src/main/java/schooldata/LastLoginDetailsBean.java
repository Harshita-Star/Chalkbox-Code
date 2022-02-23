package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="lastLoginbean")
@ViewScoped
public class LastLoginDetailsBean implements Serializable
{

	ArrayList<SelectItem> schoolList;
	ArrayList<SchoolInfo> schList = new ArrayList<>();
	ArrayList<MessageHistory> allDetails = new ArrayList<>();

	Date currentDate;
	public LastLoginDetailsBean()
	{
		currentDate=new Date();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		schList = obj.allSchoolListLastLogin(conn);
		// schoolList = new DatabaseMethods1().getAllSchool(conn);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		Date sd=cal.getTime();
		allDetails = obj.LastSevenDaysSchoolNotLogin(schList,currentDate,sd, conn);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}



	}
	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}
	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}
	public ArrayList<MessageHistory> getAllDetails() {
		return allDetails;
	}
	public void setAllDetails(ArrayList<MessageHistory> allDetails) {
		this.allDetails = allDetails;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}



}
