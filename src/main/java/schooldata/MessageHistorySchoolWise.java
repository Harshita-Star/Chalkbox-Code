package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

@ManagedBean(name="MessageHistorySchoolWise")
@ViewScoped
public class MessageHistorySchoolWise implements Serializable
{
	ArrayList<MessageHistory>allDetails=new ArrayList<>();
	double debit,credit;
	double sumaryBalance;
	int i;
	transient StreamedContent file;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList schoolDetails;
	public MessageHistorySchoolWise()
	{

		Connection conn=DataBaseConnection.javaConnection();

		schoolDetails =obj.fullSchoolInfo(conn);
		i=1;
		debit=0;
		credit=0;
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schoolid=(String) ss.getAttribute("schoolid");

		ArrayList<MessageHistory>list=obj.messageHistory(schoolid,conn);
		ArrayList<MessageHistory>message=obj.messageDebitWise(schoolid,conn);

		allDetails.addAll(list);
		allDetails.addAll(message);

		Collections.sort(allDetails);
		for(MessageHistory ss1:allDetails)
		{
			if(ss1.getDebit()!=null&&!ss1.getDebit().equals(""))
			{
				debit+=Double.parseDouble(ss1.getDebit());

			}

			if(ss1.getCredit()!=null&&!ss1.getCredit().equals(""))
			{
				credit+=Double.parseDouble(ss1.getCredit());
			}

			ss1.setSno(i++);
		}

		sumaryBalance=credit-debit;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportSmsPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//   String home = System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		//Header
		try {

			
			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im = null ;
			try {
				im =Image.getInstance(src);
			} catch (Exception e) {
				e.printStackTrace();
			}
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				 c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
			//   Date dtf = new Date();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Chunk c7 = new Chunk("                  Message History         \n\n",fo);

			Paragraph p2 = new Paragraph();

			//  String[] det = studentName.split("/");

			p2.add(c7);
			//p2.add(c1);
			// p1.add(c3);
			p2.setAlignment(Element.ALIGN_CENTER);
			document.add(p2);
			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("Sno");
			table.addCell("Date");
			table.addCell("Description");
			table.addCell("Credit");
			table.addCell("Debit");


			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<allDetails.size();i++){
				table.addCell(new Phrase(String.valueOf(allDetails.get(i).getSno())));
				table.addCell(new Phrase(allDetails.get(i).strDate,font));

				table.addCell(new Phrase(allDetails.get(i).getDescription()));
				table.addCell(new Phrase(allDetails.get(i).getCredit()));

				table.addCell(new Phrase(allDetails.get(i).getDebit()));

			}
			table.addCell("");
			table.addCell("");
			table.addCell("");
			table.addCell(String.valueOf(credit));
			table.addCell(String.valueOf(debit));
			table.addCell("");
			table.addCell("");
			table.addCell("");
			table.addCell("Balance Left");
			table.addCell(String.valueOf(sumaryBalance));

			table.setWidthPercentage(110);
			document.add(table);





		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","SMS_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("SMS_Report_Report.pdf").stream(()->isFromFirstData).build();
	}
	
	
	
	public void toNum(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);
		
		XSSFRow headerR = sheet.getRow(0);
		 headerR.setHeight((short)1200);

	
		int rowCount = sheet.getPhysicalNumberOfRows();
		try {
			sheet.shiftRows(rowCount,rowCount , -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		XSSFRow header = sheet.createRow(rowCount);
		XSSFCell celRow7 = header.createCell(3);
		
		try {
			celRow7.setCellValue("Total Credit : "+String.valueOf(credit));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		XSSFCell celRow6 = header.createCell(4);
		try {
			celRow6.setCellValue("Total Debit : "+String.valueOf(debit));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		XSSFRow header2 = sheet.createRow(rowCount+1);
		XSSFCell celRow5 = header2.createCell(3);
		celRow5.setCellValue("Balance Left");
		XSSFCell celRow4 = header2.createCell(4);
		try {
			celRow4.setCellValue(String.valueOf(sumaryBalance));
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		try
		{

			URL url = new URL(schoolDetails.getDownloadpath()+schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(0);
			my_anchor.setCol2(6);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);






		} catch (IOException ioex) {
           ioex.printStackTrace();
		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);


	}
	
	
	

	public ArrayList<MessageHistory> getAllDetails() {
		return allDetails;
	}

	public void setAllDetails(ArrayList<MessageHistory> allDetails) {
		this.allDetails = allDetails;
	}

	public double getDebit() {
		return debit;
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getSumaryBalance() {
		return sumaryBalance;
	}

	public void setSumaryBalance(double sumaryBalance) {
		this.sumaryBalance = sumaryBalance;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}







}
