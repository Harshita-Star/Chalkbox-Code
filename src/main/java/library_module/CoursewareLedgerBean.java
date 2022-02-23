package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="coursewareLedger")
@ViewScoped

public class CoursewareLedgerBean implements Serializable
{
	ArrayList<SelectItem> bookList;
	ArrayList<BookInfo> ledgerList;
	String courseware;
	int balance = 0;
	int totalIn = 0;
	int totalOut = 0;
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();


	public CoursewareLedgerBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		bookList = dbl.coursewareList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void show()
	{
		balance = 0;
		totalIn = 0;
		totalOut = 0;
		Connection conn = DataBaseConnection.javaConnection();
		ledgerList = dbl.coursewareLedger(courseware,conn);
		Collections.sort(ledgerList);

		for(BookInfo bb : ledgerList)
		{
			totalIn = totalIn + bb.getInQuant();
			totalOut = totalOut + bb.getOutQuant();
		}

		balance = totalIn - totalOut;
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getBookList() {
		return bookList;
	}

	public void setBookList(ArrayList<SelectItem> bookList) {
		this.bookList = bookList;
	}

	public ArrayList<BookInfo> getLedgerList() {
		return ledgerList;
	}

	public void setLedgerList(ArrayList<BookInfo> ledgerList) {
		this.ledgerList = ledgerList;
	}

	public String getCourseware() {
		return courseware;
	}

	public void setCourseware(String courseware) {
		this.courseware = courseware;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(int totalIn) {
		this.totalIn = totalIn;
	}

	public int getTotalOut() {
		return totalOut;
	}

	public void setTotalOut(int totalOut) {
		this.totalOut = totalOut;
	}

}
