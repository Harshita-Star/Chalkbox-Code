package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="addBook")
@ViewScoped
public class AddBookBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String bookName,author,publication,subject,authorName,subjectName,publicationName,quantity="0",bookPrice,keyword_book;
	String subTitle,page,yearOfPublish,callNo,bookNo,isbnNo,source;
	String bookfor,booktype,bookCatgoryName;
	boolean otherBook,checkDuplicate,show;
	ArrayList<SelectItem>bookForList;
	ArrayList<SelectItem> autoList;
	Date addDate=new Date();
	ArrayList<BookInfo> subBookList;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();



	public ArrayList<String> autoCompleteAuthor(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		autoList=objLibrary.authorForAutoComplete(query,conn);
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


	public void checkBookFor()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(bookfor.equalsIgnoreCase("class"))
		{
			otherBook=false;
			bookForList=obj.allClass(conn);
		}
		else
		{
			otherBook=true;
			bookForList=objLibrary.bookCatgeoryList(conn);
		}
		try {

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> autoCompletePublication(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		autoList=objLibrary.publicationForAutoComplete(query,conn);
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

	public ArrayList<String> autoCompleteSubject(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		autoList=objLibrary.subjectForAutoComplete(query,conn);
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

	public void addAuthor()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			int duplicate=objLibrary.checkDuplicateAuthor(authorName,conn);
			if(duplicate==0)
			{
				int i=objLibrary.addAuthor(authorName,conn);
				if(i==1)
				{
				    addWorkLog(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Author Added Sucessfully"));
					authorName="";
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

		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Author Name-"+authorName;
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Author","WEB",value,conn);
		return refNo;
	}

	public void addCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int duplicate=objLibrary.checkDuplicateCategory(bookCatgoryName,conn);
		if(duplicate==0)
		{
			int i=objLibrary.addBookCategory(bookCatgoryName,conn);
			if(i==1)
			{


				bookForList=objLibrary.bookCatgeoryList(conn);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Catgeory Added Sucessfully"));
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

	public void addPublication()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			int duplicate=objLibrary.checkDuplicatePublication(publicationName,conn);
			if(duplicate==0)
			{
				int i=objLibrary.addPublication(publicationName,conn);
				if(i==1)
				{
					addWorkLog2(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Publication Added Sucessfully"));
					publicationName="";
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Publication Found"));
			}

		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Publication Name-"+publicationName;
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Publication","WEB",value,conn);
		return refNo;
	}

	public void addSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			int duplicate=objLibrary.checkDuplicateSubject(subjectName,conn);
			if(duplicate==0)
			{
				int i=objLibrary.addSubject(subjectName,conn);
				if(i==1)
				{
					addWorkLog3(conn);
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
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Subject Name-"+subjectName;
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Subject","WEB",value,conn);
		return refNo;
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
		String tempBookIdByIndex = subBookList.get(Integer.valueOf(index)-1).getBookId();
		
		for(BookInfo info:subBookList)
		{
			if(info.getsNo().equals(index))
			{
				checkDuplicate=objLibrary.checkDuplicateAccessionNo(info.getBookId(),conn);
				if(checkDuplicate==true)
				{
					info.setBookId("");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Accession No Found"));
					break;
				}
			}
			else
			{
				if(info.getBookId().equals(tempBookIdByIndex))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Accession No Found"));
					 subBookList.get(Integer.valueOf(index)-1).setBookId("");
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
		
		try 
		{
			String authorId=author.substring(author.lastIndexOf("-")+2);
			String publicationId=publication.substring(publication.lastIndexOf("-")+2);
			String subjectId=subject.substring(subject.lastIndexOf("-")+2);
			int duplicate=objLibrary.checkDuplicateBook(bookName,authorId,publicationId,subjectId,conn);
			if(duplicate==0)
			{
				int i=objLibrary.addBook(bookName,authorId,publicationId,subjectId,quantity,bookfor,booktype,bookPrice,subTitle,callNo,isbnNo,bookNo,yearOfPublish,page,source,keyword_book,conn);
				if(i>0)
				{
					addWorkLog4(conn,authorId,publicationId,subjectId);
					objLibrary.addBookQuantity(String.valueOf(""+i),quantity,addDate,subBookList,conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Added Sucessfully"));
					show=false;subBookList=new ArrayList<>();
					bookName=author=publication=subject=authorId=publicationId=subjectId="";
					quantity=bookfor=booktype=bookPrice=subTitle=keyword_book=callNo=isbnNo=bookNo=yearOfPublish=page=source="";

				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Book Found"));
			}

		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public String addWorkLog4(Connection conn,String authorId,String publicationId,String subjectId)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addDate);
		
		language = "Date-"+addDt+" --Book-"+bookName+" -- AuthorId-"+authorId+" -- PublicationId-"+publicationId+" -- Subject-"+subjectId+" -- Quantity-"+quantity+" -- BookFor-"+bookfor+" -- BookType-"+booktype+" -- Price-"+bookPrice+" -- SubTitle-"+subTitle+" -- CallNo-"+callNo+" -- IsbnNo-"+isbnNo+" -- BookNo-"+bookNo+" -- Year-"+yearOfPublish+" -- Pages-"+page+" -- Source-"+source+" -- Keyword-"+keyword_book;
		
		for(BookInfo bi :subBookList)
		{
			String namee = "";
	    	try {
				namee = bi.getBarCode().getFileName();
				//// // System.out.println(namee);
			} catch (Exception e) {
				// TODO: handle exception
			}
			value += "("+bi.getBookId()+" - "+namee+")";
		}
		
		String refNo =workLg.saveWorkLogMehod(language,"Add Book","WEB",value,conn);
		return refNo;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getPublicationName() {
		return publicationName;
	}
	public void setPublicationName(String publicationName) {
		this.publicationName = publicationName;
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


	public String getSubTitle() {
		return subTitle;
	}


	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}


	public String getPage() {
		return page;
	}


	public void setPage(String page) {
		this.page = page;
	}


	public String getYearOfPublish() {
		return yearOfPublish;
	}

	public String getCallNo() {
		return callNo;
	}


	public void setCallNo(String callNo) {
		this.callNo = callNo;
	}


	public String getBookNo() {
		return bookNo;
	}


	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}


	public String getIsbnNo() {
		return isbnNo;
	}


	public void setIsbnNo(String isbnNo) {
		this.isbnNo = isbnNo;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public ArrayList<BookInfo> getSubBookList() {
		return subBookList;
	}


	public void setSubBookList(ArrayList<BookInfo> subBookList) {
		this.subBookList = subBookList;
	}


	public void setYearOfPublish(String yearOfPublish) {
		this.yearOfPublish = yearOfPublish;
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


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
