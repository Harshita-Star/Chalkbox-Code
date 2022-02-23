package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import library_module.BookInfo;
import library_module.DataBaseMethodsLibraryModule;
@ManagedBean(name="dashboardLibrary")
@ViewScoped
public class DashboardLibraryBean implements Serializable{

	String allBooks,allAssigned,allOverdue,penalty;
	ArrayList<StudentInfo>allStudent = new ArrayList<>();
	ArrayList<BookInfo> bookList = new ArrayList<BookInfo>();
	DataBaseMethodsLibraryModule DBM = new DataBaseMethodsLibraryModule();

	public ArrayList<StudentInfo> getAllStudent() {
		return allStudent;
	}
	public void setAllStudent(ArrayList<StudentInfo> allStudent) {
		this.allStudent = allStudent;
	}
	public DashboardLibraryBean()
	{

		String schId=new DatabaseMethods1().schoolId();
		Connection conn  =  DataBaseConnection.javaConnection();
		allBooks = DBM.totalBooks(schId,conn);
		allAssigned = DBM.totalAssignedBook(schId,conn);
		allOverdue = DBM.totalOverdueBooks(schId,conn);
		
		bookList=new DataBaseMethodsLibraryModule().libraryFeeCollectionReportSessionWise(schId,conn);
		double penAmt =0;
		try {
			
			for(BookInfo bi : bookList)
			{
				penAmt += Double.valueOf(bi.getPenaltyAmount()) + Double.valueOf(bi.getDamageAmount()) - Double.valueOf(bi.getDiscount()); 
			}

			penalty = String.valueOf(penAmt);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//penalty = DBM.sumOfAllPenaltyInThisSession(schId,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getAllBooks() {
		return allBooks;
	}
	public void setAllBooks(String allBooks) {
		this.allBooks = allBooks;
	}
	public String getAllAssigned() {
		return allAssigned;
	}
	public void setAllAssigned(String allAssigned) {
		this.allAssigned = allAssigned;
	}
	public String getAllOverdue() {
		return allOverdue;
	}
	public void setAllOverdue(String allOverdue) {
		this.allOverdue = allOverdue;
	}
	public String getPenalty() {
		return penalty;
	}
	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}
}