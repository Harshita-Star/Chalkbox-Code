package library_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import session_work.RegexPattern;

@ManagedBean (name="addCoursewareQuant")
@ViewScoped

public class AddCoursewareQuantBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String bookName,quantity="0";
	Date addDate=new Date();
	ArrayList<BookInfo> bookList;
	ArrayList<BookInfo> subBookList;
	boolean checkDuplicate,show;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();

	public ArrayList<String> autoCompleteBook(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		bookList=objLibrary.coursewareForAutoComplete(query,conn);
		ArrayList<String> tempList=new ArrayList<>();

		for(BookInfo ll : bookList)
		{
			tempList.add(ll.getBookName()+ " - "+ll.getSubjectName()+" - "+ll.getArticleId());
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempList;
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

	public void addBookQuantity()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String bookId=bookName.substring(bookName.lastIndexOf("-")+2);
		if(Integer.parseInt(quantity)>0)
		{
			int i=objLibrary.addCoursewareQuantity(bookId,quantity,addDate,subBookList,conn);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course Quantity Added Sucessfully"));
				bookId="";bookName="";quantity="0";addDate=new Date();
				show=false;
				subBookList=new ArrayList<>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Quantity Must Be Greater Than Zero"));
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public ArrayList<BookInfo> getSubBookList() {
		return subBookList;
	}
	public void setSubBookList(ArrayList<BookInfo> subBookList) {
		this.subBookList = subBookList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
