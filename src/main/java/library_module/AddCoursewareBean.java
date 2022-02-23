package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="addCourseware")
@ViewScoped

public class AddCoursewareBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String bookName,subject,subjectName,quantity="0",bookPrice,keyword_book;
	String bookfor,booktype,bookCatgoryName;
	boolean otherBook,checkDuplicate,show;
	ArrayList<SelectItem>bookForList;
	ArrayList<SelectItem> autoList;
	Date addDate=new Date();
	ArrayList<BookInfo> subBookList;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	DatabaseMethods1 db=new DatabaseMethods1();

	public void checkBookFor()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(bookfor.equalsIgnoreCase("class"))
		{
			otherBook=false;
			bookForList=db.allClass(conn);
		}
		else
		{
			otherBook=true;
			bookForList=objLibrary.coursewareCatgeoryList(conn);
		}
		try {

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompleteSubject(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		autoList=objLibrary.subjectCoursewareAutoComplete(query,conn);
		ArrayList<String> tempList=new ArrayList<>();

		for(SelectItem ll : autoList)
		{
			tempList.add(ll.getLabel()+ " - "+ll.getValue());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tempList;
	}

	public void addCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int duplicate=objLibrary.checkDuplicateCoursewareCategory(bookCatgoryName,conn);
		if(duplicate==0)
		{
			int i=objLibrary.addCoursewareCategory(bookCatgoryName,conn);
			if(i==1)
			{


				bookForList=objLibrary.coursewareCatgeoryList(conn);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Courseware Catgeory Added Sucessfully"));
				bookCatgoryName="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Author Found"));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int duplicate=objLibrary.checkDuplicateCoursewareSubject(subjectName,conn);
		if(duplicate==0)
		{
			int i=objLibrary.addCoursewareSubject(subjectName,conn);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Added Sucessfully"));
				subjectName="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Subject Found"));
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createBookList()
	{
		subBookList=new ArrayList<>();
		for(int i=1;i<=Integer.parseInt(quantity);i++)
		{
			BookInfo info=new BookInfo();
			info.setsNo(String.valueOf(i));
			info.setBarCode(null);
			info.setBookId("");
			subBookList.add(info);
		}
		show=true;
	}

	public void checkDuplicateAccessionNo()
	{
		Connection conn=DataBaseConnection.javaConnection();


		FacesContext context = FacesContext.getCurrentInstance();
		String index = (String) UIComponent.getCurrentComponent(context).getAttributes().get("filter1");
		for(BookInfo info:subBookList)
		{
			if(info.getsNo().equals(index))
			{
				checkDuplicate=objLibrary.checkDuplicateUniqueNo(info.getBookId(),conn);
				if(checkDuplicate==true)
				{
					info.setBookId("");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Unique No Found"));
					break;
				}
			}
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

	public void addBook()
	{
		Connection conn=DataBaseConnection.javaConnection();

		String subjectId=subject.substring(subject.lastIndexOf("-")+2);
		int duplicate=objLibrary.checkDuplicateCoursewareBook(bookName,subjectId,conn);
		if(duplicate==0)
		{
			int i=objLibrary.addCourseware(bookName,subjectId,quantity,bookfor,booktype,bookPrice,keyword_book,conn);
			if(i>0)
			{
				objLibrary.addCoursewareQuantity(String.valueOf(""+i),quantity,addDate,subBookList,conn);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Courseware Added Sucessfully"));
				show=false;subBookList=new ArrayList<>();
				bookName=subject=subjectId="";
				quantity=bookfor=booktype=bookPrice=keyword_book="";

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Courseware Found"));
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public ArrayList<SelectItem> getAutoList() {
		return autoList;
	}
	public void setAutoList(ArrayList<SelectItem> autoList) {
		this.autoList = autoList;
	}

	public String getBookfor() {
		return bookfor;
	}

	public void setBookfor(String bookfor) {
		this.bookfor = bookfor;
	}

	public boolean isOtherBook() {
		return otherBook;
	}

	public void setOtherBook(boolean otherBook) {
		this.otherBook = otherBook;
	}

	public ArrayList<SelectItem> getBookForList() {
		return bookForList;
	}

	public void setBookForList(ArrayList<SelectItem> bookForList) {
		this.bookForList = bookForList;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}


	public String getBooktype() {
		return booktype;
	}


	public void setBooktype(String booktype) {
		this.booktype = booktype;
	}


	public String getBookCatgoryName() {
		return bookCatgoryName;
	}


	public void setBookCatgoryName(String bookCatgoryName) {
		this.bookCatgoryName = bookCatgoryName;
	}


	public String getBookPrice() {
		return bookPrice;
	}


	public void setBookPrice(String bookPrice) {
		this.bookPrice = bookPrice;
	}


	public ArrayList<BookInfo> getSubBookList() {
		return subBookList;
	}


	public void setSubBookList(ArrayList<BookInfo> subBookList) {
		this.subBookList = subBookList;
	}



	public boolean isCheckDuplicate() {
		return checkDuplicate;
	}


	public void setCheckDuplicate(boolean checkDuplicate) {
		this.checkDuplicate = checkDuplicate;
	}


	public boolean isShow() {
		return show;
	}


	public void setShow(boolean show) {
		this.show = show;
	}


	public String getKeyword_book() {
		return keyword_book;
	}


	public void setKeyword_book(String keyword_book) {
		this.keyword_book = keyword_book;
	}
}
