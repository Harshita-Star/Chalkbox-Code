package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import schooldata.StudentInfo;

@ManagedBean(name="differentBusStudentReport")
@ViewScoped

public class DifferentBusStudentReportBean implements Serializable
{
		/*SELECT * FROM `rfid_report` WHERE `action_date` = '2019-09-03' AND `schid` = '251' AND 
				((in_bus_morn_imei != '' AND in_bus_even_imei != '' AND in_bus_morn_imei != in_bus_even_imei) OR 
						(out_bus_morn_imei != '' AND out_bus_even_imei != '' AND out_bus_morn_imei != out_bus_even_imei))*/
	Date date;
	String dateStr;
	ArrayList<StudentInfo> slist;
	transient StreamedContent file;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,sessionValue;
	
	public DifferentBusStudentReportBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
	
		try {
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void showRFID()
	{
		Connection conn = DataBaseConnection.javaConnection();
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
	
		
		slist = dbr.differentBusListRFID(strDate,schoolId,conn);
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportLibPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbm.fullSchoolInfo(con);

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		//Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);

			Chunk c = new Chunk(ls.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  ls.add1+ " " +ls.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();
			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                           Differnt Bus Report "+dateStr+" \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(7);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Name ");
			table.addCell("Class");
			
			table.addCell("Pick From Home Stop");
			table.addCell("Drop In School");
			table.addCell("Pick From School");
			table.addCell("Drop At Home Stop");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<slist.size();i++)
			{
				table.addCell(new Phrase(String.valueOf(i+1),font));
				table.addCell(new Phrase(slist.get(i).getFullName(),font));
				table.addCell(new Phrase(slist.get(i).getClassName(),font));
				//table.addCell(new Phrase(slist.get(i).getTransStatus()));
				
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusMornComplex(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusMornComplex(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusEvenComplex(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusEvenComplex(),font));
				
			}
		
			table.setWidthPercentage(110);
			document.add(table);

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);
		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Different_Bus_Report_"+dateStr+".pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Different_Bus_Report_"+dateStr+".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public ArrayList<StudentInfo> getSlist() {
		return slist;
	}

	public void setSlist(ArrayList<StudentInfo> slist) {
		this.slist = slist;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
	
}
