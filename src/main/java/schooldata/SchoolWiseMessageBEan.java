package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="schoolWiseMessageCheck")
@ViewScoped
public class SchoolWiseMessageBEan implements Serializable
{

	ArrayList<SelectItem> schoolList;
	ArrayList<MessageHistory> allDetails = new ArrayList<>();
	Date currentDate,toDate;
	int total=0;
	public SchoolWiseMessageBEan()
	{
		toDate=new Date();
		currentDate=new Date();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		schoolList = new DatabaseMethods1().getAllSchool(conn);
		allDetails = obj.messageDebitWiseSchool(schoolList,currentDate,toDate,conn);
		total = 0;
		for(MessageHistory mm : allDetails)
		{
			total += mm.getCount();
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}


	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		allDetails = obj.messageDebitWiseSchool(schoolList,currentDate,toDate, conn);
		total = 0;
		for(MessageHistory mm : allDetails)
		{
			total += mm.getCount();
		}


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


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}


	public Date getToDate() {
		return toDate;
	}


	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}


}
