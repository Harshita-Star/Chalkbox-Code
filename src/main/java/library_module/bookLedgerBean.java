package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean (name="bookLedger")
@ViewScoped
public class bookLedgerBean implements Serializable {

	String bookname,author,publication,stock,opening_stock,assignedbook,finalstock;

	ArrayList<BookInfo> bookList;
	ArrayList<BookInfo> viewtable;
	boolean showpanel=false;
	boolean showtable=false;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}

	public ArrayList<String> autoCompleteBook(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookList=objLibrary.bookForAutoComplete(query,conn);
		ArrayList<String> tempList=new ArrayList<>();

		for(BookInfo ll : bookList)
		{
			tempList.add(ll.getBookName()+ " - "+ll.getAuthorName()+" - "+ll.getPublicationName()+" - "+ll.getStockInHand()+" - "+ll.getOpeningStock()+" - "+ll.getFinalstock()+" - "+ll.getArticleId());
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempList;
	}


	public void search() {

		Connection conn=DataBaseConnection.javaConnection();
		showpanel=true;
		showtable=true;


		bookname.substring(bookname.lastIndexOf("-")+1);
		String []code=bookname.split(" - ");

		author=code[1];
		opening_stock=code[4];
		stock=code[3];
		publication=code[2];
		finalstock=code[5];
		/*viewtable=objLibrary.quantitydetails(bookId,conn);

	String res= objLibrary.assbook(bookId,conn);
	setAssignedbook(res);
		 */

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//bookledger=new DataBaseMethodsLibraryModule().allbookledger(bookId,code[3],code[1],code[2],code[4]);
	}


	public bookLedgerBean() {
		// TODO Auto-generated constructor stub
	}



	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
	}


	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}


	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublication() {
		return publication;
	}

	public void setPublication(String publication) {
		this.publication = publication;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getOpening_stock() {
		return opening_stock;
	}

	public void setOpening_stock(String opening_stock) {
		this.opening_stock = opening_stock;
	}

	public boolean isShowpanel() {
		return showpanel;
	}

	public void setShowpanel(boolean showpanel) {
		this.showpanel = showpanel;
	}

	public String getAssignedbook() {
		return assignedbook;
	}

	public void setAssignedbook(String assignedbook) {
		this.assignedbook = assignedbook;
	}

	public boolean isShowtable() {
		return showtable;
	}

	public void setShowtable(boolean showtable) {
		this.showtable = showtable;
	}

	public ArrayList<BookInfo> getViewtable() {
		return viewtable;
	}

	public void setViewtable(ArrayList<BookInfo> viewtable) {
		this.viewtable = viewtable;
	}

	public String getFinalstock() {
		return finalstock;
	}

	public void setFinalstock(String finalstock) {
		this.finalstock = finalstock;
	}


}
