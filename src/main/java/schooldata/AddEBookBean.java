package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.file.UploadedFile;

import library_module.BookInfo;

@ManagedBean (name="e_book")
@ViewScoped
public class AddEBookBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<BookInfo> bookList;
	Date currentDate;
	BookInfo selectedBook;
	String bookName,bookPdfPath;
	ArrayList<SelectItem> classList;
	ArrayList<String> selectedClass;
	UploadedFile bookImage;

	public AddEBookBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		currentDate=new Date();
		classList=obj.allClass(conn);
		bookList=obj.allE_BookList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deleteBook()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int i=obj.deleteE_Book(selectedBook.getId(), conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Deleted Sucessfully"));
			bookList=obj.allE_BookList(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}


		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj= new DatabaseMethods1();
		int i=0;

		if (bookImage !=null && bookImage.getSize() > 0)
		{
			bookPdfPath = bookImage.getFileName();
			obj.makeProfileSchid(obj.schoolId(),bookImage, bookPdfPath, conn);
		}

		for(String classId:selectedClass)
		{
			i=obj.addE_Book(classId, bookName, bookPdfPath, currentDate, conn);
		}
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Added Sucessfully"));
			bookList=obj.allE_BookList(conn);
			selectedClass.clear();bookName=bookPdfPath="";bookImage=null;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}

	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public BookInfo getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(BookInfo selectedBook) {
		this.selectedBook = selectedBook;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookPdfPath() {
		return bookPdfPath;
	}

	public void setBookPdfPath(String bookPdfPath) {
		this.bookPdfPath = bookPdfPath;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<String> getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(ArrayList<String> selectedClass) {
		this.selectedClass = selectedClass;
	}

	public UploadedFile getBookImage() {
		return bookImage;
	}

	public void setBookImage(UploadedFile bookImage) {
		this.bookImage = bookImage;
	}


}
