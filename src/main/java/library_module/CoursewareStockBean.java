package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="coursewareStock")
@ViewScoped

public class CoursewareStockBean implements Serializable
{
	ArrayList<BookInfo> stockList,assignList;
	BookInfo selected;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();

	public CoursewareStockBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		stockList = dbl.coursewareStockReport(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		assignList = dbl.coursewareAssignedReport(selected.getId(), conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BookInfo> getStockList() {
		return stockList;
	}

	public void setStockList(ArrayList<BookInfo> stockList) {
		this.stockList = stockList;
	}

	public ArrayList<BookInfo> getAssignList() {
		return assignList;
	}

	public void setAssignList(ArrayList<BookInfo> assignList) {
		this.assignList = assignList;
	}

	public BookInfo getSelected() {
		return selected;
	}

	public void setSelected(BookInfo selected) {
		this.selected = selected;
	}
}
