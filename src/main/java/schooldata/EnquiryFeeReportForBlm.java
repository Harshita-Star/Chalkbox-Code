package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ViewScoped
@ManagedBean(name="enquiryFeeReportForBlm")
public class EnquiryFeeReportForBlm implements Serializable
{
	Date startDate=new Date(),endDate=new Date();
	ArrayList<StudentInfo1>list=new ArrayList<>();
	public EnquiryFeeReportForBlm()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//endDate.setDate(startDate.getDate()+1);
		list=new DatabaseMethods1().enquiryFeeReportForBlm(startDate,endDate,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().enquiryFeeReportForBlm(startDate,endDate,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public ArrayList<StudentInfo1> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo1> list) {
		this.list = list;
	}



}
