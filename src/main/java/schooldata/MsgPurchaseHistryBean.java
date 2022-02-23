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
import java.util.Date;

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

@ManagedBean(name="msgPurchaseHistry")
@ViewScoped
public class MsgPurchaseHistryBean implements Serializable{
	ArrayList<MessagePackInfo>packList = new ArrayList<>();
	String type;
	int randomOtp,count1,count2,count3,count4;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	Date startDate,endDate;

	public MsgPurchaseHistryBean()
	{
		type="approved";
		startDate = new Date();
		endDate = new Date();
		check1();
		Connection conn = DataBaseConnection.javaConnection();



		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);


		try {
			conn.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}



	public  void exportmsghisPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		//Header
		try {

			
			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im =null;
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
			Chunk c8 = new Chunk("\n                                                        Message Purchase History Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Pack Name");
			table.addCell("Quantity ");
			table.addCell("Rate");
			table.addCell("Amount");
			table.addCell("Tax");
			table.addCell("Total Amount");
			table.addCell("Date");
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


			for (int i=0;i<packList.size();i++){
				table.addCell(new Phrase(packList.get(i).getPackName(),font));
				table.addCell(new Phrase(packList.get(i).getQuantity(),font));
				table.addCell(new Phrase(packList.get(i).getRate(),font));
				table.addCell(new Phrase(packList.get(i).getAmount(),font));
				table.addCell(new Phrase(packList.get(i).getTax(),font));
				table.addCell(new Phrase(packList.get(i).getTotalAmount(),font));
				table.addCell(new Phrase(packList.get(i).getDate(),font));
				table.addCell(new Phrase(packList.get(i).getPaidStatus(),font));







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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Message_Purchase_History_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Message_Purchase_History_Report.pdf").stream(()->isFromFirstData).build();






	}



	public void check1()
	{
		Connection conn = DataBaseConnection.javaConnection();

		if(endDate.before(startDate))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Dates Properly"));
		}
		else
		{	

		  String strDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		  String endStrDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);	
			
		  packList = new DatabaseMethods1().allMsgPackHistory("all",strDate,endStrDate,conn);

		 try {
			conn.close();
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
		}	 
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRandomOtp() {
		return randomOtp;
	}

	public void setRandomOtp(int randomOtp) {
		this.randomOtp = randomOtp;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}

	public int getCount4() {
		return count4;
	}

	public void setCount4(int count4) {
		this.count4 = count4;
	}

	public ArrayList<MessagePackInfo> getPackList() {
		return packList;
	}

	public void setPackList(ArrayList<MessagePackInfo> packList) {
		this.packList = packList;
	}



	public StreamedContent getFile() {
		return file;
	}



	public void setFile(StreamedContent file) {
		this.file = file;
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
	

}