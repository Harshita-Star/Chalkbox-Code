package library_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="allBookReport")
@ViewScoped
public class AllBookReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<BookInfo> bookList,selectedBookList=new ArrayList<>();
	BookInfo selectedBook;
	String id,bookName,author,publication,subject,bookPrice,keyword_book;
	String subTitle,page,yearOfPublish,callNo,bookNo,isbnNo,source,accNo,bookImage;
	String bookfor,booktype,bookCatgoryName;
	boolean otherBook,checkDuplicate,show,showTable;
	ArrayList<SelectItem>bookForList;
	ArrayList<SelectItem> autoList;
	Date addDate=new Date();
	transient UploadedFile newImage;
	DataBaseMethodsLibraryModule objLibrary=new DataBaseMethodsLibraryModule();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,selectedAlpha;
	ArrayList<String> list;
	
	public AllBookReportBean()
	{
		list=new ArrayList<>();
		String str="A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String arr[]=str.split(",");
		for(String alpha:arr)
		{
			list.add(alpha);
		}
	}
	
	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolDetails =obj.fullSchoolInfo(conn);
		schoolId=obj.schoolId();
		
		bookList=objLibrary.allBookReport(schoolId,"report",conn,selectedAlpha);
		if(bookList.size()>0)
		{
			if(!schoolId.equals("238"))
			{
				Collections.sort(bookList, new MySalaryComp());
			}
			showTable=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("No Record Found."));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void editDetails()
	{
		id=selectedBook.getArticleId();
		bookName=selectedBook.getBookName();
		subTitle=selectedBook.getSubTitle();
		keyword_book=selectedBook.getKeyword_book();
		bookfor=selectedBook.getBookFor();
		checkBookFor();
		booktype=selectedBook.getBookCategoryId();
		author=selectedBook.getAuthorName()+" - "+selectedBook.getAuhtorId();
		publication=selectedBook.getPublicationName()+" - "+selectedBook.getPublicationId();
		subject=selectedBook.getSubjectName()+" - "+selectedBook.getSubjectId();
		yearOfPublish=selectedBook.getYearOfPublish();
		page=selectedBook.getPage();
		source=selectedBook.getSource();
		callNo=selectedBook.getCallNo();
		bookNo=selectedBook.getBookNo();
		isbnNo=selectedBook.getIsbnNo();
		bookPrice=selectedBook.getBookPrice();
		bookImage=selectedBook.getBarCodePath();
		accNo=selectedBook.getBookId();
	}

	public void deleteBook()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int i=objLibrary.deleteBook(schoolId, selectedBook.getBookId(), conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book deleted successfully"));
			searchData();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An error occured. Please try again."));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void deleteMultipleBook()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedBookList.size()>0)
		{
			int i=0;
			for(BookInfo book:selectedBookList)
			{
				if(book.isEnableDelete()==false)
					i=objLibrary.deleteBook(schoolId, book.getBookId(), conn);
			}
			if(i>0)
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book deleted successfully"));
			searchData();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one book"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
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

	public void updateBook()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		String authorId=author.substring(author.lastIndexOf("-")+2);
		String publicationId=publication.substring(publication.lastIndexOf("-")+2);
		String subjectId=subject.substring(subject.lastIndexOf("-")+2);
		//// // System.out.println(selectedBook.getBookId()+"....."+accNo);
		if(!selectedBook.getBookId().equals(accNo))
		{
			boolean check=objLibrary.checkAccNoAssignedOrNotYet(selectedBook.getBookId(),accNo,conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Accession No. is Used.. So can not be changed"));
				accNo=selectedBook.getBookId();
			}
			else
			{
				i=objLibrary.updateBookDetails(bookName,subTitle,keyword_book,accNo,bookfor,booktype,authorId,publicationId,subjectId,
						yearOfPublish,page,source,callNo,bookNo,isbnNo,bookPrice,bookImage,newImage,selectedBook.getBookId(),selectedBook.getArticleId(),conn);
				PrimeFaces.current().ajax().update(":editForm");
				PrimeFaces.current().ajax().update(":form");
			}
		}
		else
		{
			i=objLibrary.updateBookDetails(bookName,subTitle,keyword_book,accNo,bookfor,booktype,authorId,publicationId,subjectId,
					yearOfPublish,page,source,callNo,bookNo,isbnNo,bookPrice,bookImage,newImage,selectedBook.getBookId(),selectedBook.getArticleId(),conn);
			PrimeFaces.current().ajax().update(":editForm");
			PrimeFaces.current().ajax().update(":form");
		}
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Book Updated SuccessFully"));
			
			bookList=objLibrary.allBookReport(schoolId,"report",conn,selectedAlpha);
			Collections.sort(bookList, new MySalaryComp());
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
	}

	public  void exportbookPdf() throws DocumentException, IOException, FileNotFoundException 
	{

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);
		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im=null;
			try {
				im =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );
			Chunk c3 =null;
			
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			Chunk c1 = new Chunk(  schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		try {
			//   Date dtf = new Date();
			Chunk c8 = new Chunk("\n                                                         All Book Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);


			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		//	table.addCell("Sno");
			table.addCell("Accession No.");
			table.addCell("Author");
			table.addCell("Book Name");
			table.addCell("Keyword");
			table.addCell("Sub Title");
			table.addCell("Publisher / Place");
			table.addCell("Year Of Publication");
			table.addCell("Page");
			table.addCell("Source");
			table.addCell("Call No.");
			table.addCell("Book No.");
			table.addCell("Price");
			table.addCell("ISBN No.");
			table.addCell("Category");
			table.addCell("Subject Name");
			table.addCell("Date of Entry");
			table.addCell("Status");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<bookList.size();i++){
				//table.addCell(new Phrase(String.valueOf(bookList.get(i).getsNo()),font));
				table.addCell(new Phrase(bookList.get(i).getBookId(),font));

				table.addCell(new Phrase(bookList.get(i).getAuthorName(),font));
				table.addCell(new Phrase(bookList.get(i).getBookName(),font));

				table.addCell(new Phrase(bookList.get(i).getKeyword_book(),font));

				table.addCell(new Phrase(bookList.get(i).getSubTitle(),font));
				table.addCell(new Phrase(bookList.get(i).getPublicationName(),font));

				table.addCell(new Phrase(bookList.get(i).getYearOfPublish(),font));
				table.addCell(new Phrase(bookList.get(i).getPage(),font));

				table.addCell(new Phrase(bookList.get(i).getSource(),font));

				table.addCell(new Phrase(bookList.get(i).getCallNo(),font));
				table.addCell(new Phrase(bookList.get(i).getBookNo(),font));

				table.addCell(new Phrase(bookList.get(i).getBookPrice(),font));
				table.addCell(new Phrase(bookList.get(i).getIsbnNo(),font));

				table.addCell(new Phrase(bookList.get(i).getBookCategoryName(),font));

				table.addCell(new Phrase(bookList.get(i).getSubjectName(),font));
				table.addCell(new Phrase(bookList.get(i).getAddDateStr(),font));

				table.addCell(new Phrase(bookList.get(i).getReceivedStatus(),font));

			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","All_Book_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("All_Book_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}
	
	class MySalaryComp implements Comparator<BookInfo>
	{
		@Override
		public int compare(BookInfo e1, BookInfo e2)
		{

			String srno1 =  e1.getBookId().trim();
			String srno2 = e2.getBookId().trim();

//			long sr1 = Long.parseLong(srno1);
//			long sr2 = Long.parseLong(srno2);
			
			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}

	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
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

	public String getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(String bookPrice) {
		this.bookPrice = bookPrice;
	}

	public String getKeyword_book() {
		return keyword_book;
	}

	public void setKeyword_book(String keyword_book) {
		this.keyword_book = keyword_book;
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

	public void setYearOfPublish(String yearOfPublish) {
		this.yearOfPublish = yearOfPublish;
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

	public String getBookfor() {
		return bookfor;
	}

	public void setBookfor(String bookfor) {
		this.bookfor = bookfor;
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

	public boolean isOtherBook() {
		return otherBook;
	}

	public void setOtherBook(boolean otherBook) {
		this.otherBook = otherBook;
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

	public ArrayList<SelectItem> getBookForList() {
		return bookForList;
	}

	public void setBookForList(ArrayList<SelectItem> bookForList) {
		this.bookForList = bookForList;
	}

	public ArrayList<SelectItem> getAutoList() {
		return autoList;
	}

	public void setAutoList(ArrayList<SelectItem> autoList) {
		this.autoList = autoList;
	}


	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getBookImage() {
		return bookImage;
	}

	public void setBookImage(String bookImage) {
		this.bookImage = bookImage;
	}

	public UploadedFile getNewImage() {
		return newImage;
	}

	public void setNewImage(UploadedFile newImage) {
		this.newImage = newImage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public String getSelectedAlpha() {
		return selectedAlpha;
	}

	public void setSelectedAlpha(String selectedAlpha) {
		this.selectedAlpha = selectedAlpha;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public ArrayList<BookInfo> getSelectedBookList() {
		return selectedBookList;
	}

	public void setSelectedBookList(ArrayList<BookInfo> selectedBookList) {
		this.selectedBookList = selectedBookList;
	}

}
