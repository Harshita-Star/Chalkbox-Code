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
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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

@ManagedBean(name="libFeeCollectReport")
@ViewScoped
public class LibraryFeeCollectionReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate,endDate;
	ArrayList<BookInfo> bookList;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();
	String schId;

	public LibraryFeeCollectionReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schId=obj.schoolId();
		schoolDetails =obj.fullSchoolInfo(conn);
		startDate=endDate=new Date();
		searchData();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		bookList=dbl.libraryFeeCollectionReport(schId,startDate,endDate,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  void exportLibPdf() throws DocumentException, IOException, FileNotFoundException {

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
	            c3 = new Chunk(im, -250, 15);
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

		try{
			Chunk c8 = new Chunk("\n                                                           Library Collection Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");

			table.addCell("Type ");
			table.addCell("Name");
			table.addCell("Class ");
			table.addCell("Date");
			table.addCell("Penalty(Amount)");
			table.addCell("Damage/Discard Charges");
			table.addCell("Discount");




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
				table.addCell(new Phrase(String.valueOf(bookList.get(i).getsNo()),font));


				table.addCell(new Phrase(bookList.get(i).getType()));
				table.addCell(new Phrase(bookList.get(i).getStudentName()));
				table.addCell(new Phrase(bookList.get(i).getClassSection()));
				table.addCell(new Phrase(bookList.get(i).getReceiveDateStr()));
				table.addCell(new Phrase(bookList.get(i).getPenaltyAmount()));
				table.addCell(new Phrase(bookList.get(i).getDamageAmount()));
				table.addCell(new Phrase(bookList.get(i).getDiscount(),font));




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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Library_Collection_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Library_Collection_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
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

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
