package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="receiveBookReport")
@ViewScoped
public class ReceiveBookReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate,endDate;
	ArrayList<BookInfo> bookList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();

	public ReceiveBookReportBean()
	{
		startDate=endDate=new Date();
		searchData();
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookList=dbl.receiveBookReport(startDate,endDate,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}
	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
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
}
