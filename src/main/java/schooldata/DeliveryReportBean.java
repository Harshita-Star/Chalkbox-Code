package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
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

import Json.DataBaseMeathodJson;
import Json.DeliveryReport;

@ManagedBean(name="deliveryReportBean")
@ViewScoped
public class DeliveryReportBean implements Serializable
{

	ArrayList<DeliveryReport>list;
	DeliveryReport selectedList;
	Date searchdate,endSearchdate;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public DeliveryReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();

		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		

		searchdate = new Date();
		endSearchdate = new Date();
		searchData();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void searchData()
	{
		
		list = new ArrayList<DeliveryReport>();
		if(endSearchdate.before(searchdate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Dates Properly"));
		}
		else
		{	
		 HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 String schid = (String) ss.getAttribute("schoolid");	
		 
		 
		 Calendar c = Calendar.getInstance(); 
		 c.setTime(endSearchdate); 
		 c.add(Calendar.DATE, 1);
		 Date dtt = c.getTime();
		 	
		 String strDate = new SimpleDateFormat("yyyy-MM-dd").format(searchdate);
		 String endStrDate = new SimpleDateFormat("yyyy-MM-dd").format(dtt);


		 Connection conn=DataBaseConnection.javaConnection();
		 list=new DataBaseMeathodJson().allDeliveryreportForDate(schid,"student','staff','enq",conn,strDate,endStrDate);


		 try {
			conn.close();
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		}
	}


	public void selelectedListMessage()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("messsagelist", selectedList);

		PrimeFaces.current().executeInitScript("window.open('deliveryStatusReport.xhtml')");

	}

	public  void exportDelivPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);




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

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  "              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

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
			Chunk c8 = new Chunk("\n                                                                 Message Delivery Report \n\n",fo );
			Paragraph p8 = new Paragraph();

			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Message");
			table.addCell("Student Count");
			table.addCell("Message Count");
			table.addCell("Date");


			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<list.size();i++){
				table.addCell(new Phrase(list.get(i).getMessage(),font));
				table.addCell(new Phrase(list.get(i).getCount(),font));

				table.addCell(new Phrase(list.get(i).getMcount()));
				table.addCell(new Phrase(list.get(i).getDate()));


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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Message_Delivery_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Message_Delivery_Report.pdf").stream(()->isFromFirstData).build();




		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}


	}

	public ArrayList<DeliveryReport> getList() {
		return list;
	}
	public void setList(ArrayList<DeliveryReport> list) {
		this.list = list;
	}
	public DeliveryReport getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(DeliveryReport selectedList) {
		this.selectedList = selectedList;
	}


	public Date getSearchdate() {
		return searchdate;
	}


	public void setSearchdate(Date searchdate) {
		this.searchdate = searchdate;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public Date getEndSearchdate() {
		return endSearchdate;
	}

	public void setEndSearchdate(Date endSearchdate) {
		this.endSearchdate = endSearchdate;
	}





}
