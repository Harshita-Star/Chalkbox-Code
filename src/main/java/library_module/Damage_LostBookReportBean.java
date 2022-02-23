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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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

@ManagedBean(name="damageBookReport")
@ViewScoped
public class Damage_LostBookReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<BookInfo> bookList;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();



	public Damage_LostBookReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =obj.fullSchoolInfo(conn);
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
		bookList=dbl.damageBookReport(conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public  String exportDamageBookPdf() throws DocumentException, IOException, FileNotFoundException 
	{
		
       if(bookList.size()>0)
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

   		try {
   			Chunk c8 = new Chunk("\n                                                               Damage Books Report \n\n",fo );
   			Paragraph p8 = new Paragraph();
   			p8.add(c8);
   			document.add(p8);
   			p8.setAlignment(Element.ALIGN_CENTER);





   			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
   			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1});

   			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

   			table.addCell("SNo.");
   			table.addCell("Book Name");
   			table.addCell("Accession No.");
   			table.addCell("Author Name");
   			table.addCell("Publication Name");
   			table.addCell("Subject Name");
   			table.addCell("Type ");
   			table.addCell("Damaged By");




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
   				table.addCell(new Phrase(bookList.get(i).getsNo(),font));
   				table.addCell(new Phrase(bookList.get(i).getBookName(),font));

   				table.addCell(new Phrase(bookList.get(i).getBookId(),font));
   				table.addCell(new Phrase(bookList.get(i).getAuthorName(),font));
   				table.addCell(new Phrase(bookList.get(i).getPublicationName(),font));
   				table.addCell(new Phrase(bookList.get(i).getSubjectName(),font));





   			}


   			table.setWidthPercentage(110);
   			document.add(table);





   		}  catch (Exception e) {

   			e.printStackTrace();
   		}
   		Paragraph p2 = new Paragraph("\n");
   		document.add(p2);



   		document.close();

   		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//   		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Damage_Book_Report.pdf");
   		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Damage_Book_Report.pdf").stream(()->isFromFirstData).build();


   		try {
   			con.close();
   		} catch (SQLException e1) {
   			
   			e1.printStackTrace();
   		}
       }
       else
       {
    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Damage / Discard Books found "));
       }


         return "damage_lostBookReport.xhtml";
	}

	public ArrayList<BookInfo> getBookList() {
		return bookList;
	}
	public void setBookList(ArrayList<BookInfo> bookList) {
		this.bookList = bookList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
