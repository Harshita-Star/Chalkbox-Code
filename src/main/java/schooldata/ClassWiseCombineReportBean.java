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

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="ClassWiseCombineReportBean")
@ViewScoped
public class ClassWiseCombineReportBean implements Serializable
{

	Date selectDate;
	ArrayList<ClassInfo>list;

	int totalP,totalL,totalA,totalS,totalML,totalPL,totalH;
	String strDate;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public ClassWiseCombineReportBean() {

		Connection conn=DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		totalA=0;
		totalL=0;
		totalP=0;
		totalS=0;
		totalML=0;
		totalPL=0;
		totalH=0;

		selectDate=new Date();

		strDate=new SimpleDateFormat("dd/MM/yyyy").format(selectDate);

		list=new DataBaseMethodsReports().classWiseAttendanceList(selectDate,conn);
		for(ClassInfo ss:list)
		{
			totalA=totalA+Integer.parseInt(ss.getToatalAbsent());
			totalL=totalL+Integer.parseInt(ss.getTotalLeave());
			totalP=totalP+Integer.parseInt(ss.getTotalPresent());
			totalS=totalS+Integer.parseInt(ss.getTotalStudent());

			totalH=totalH+Integer.parseInt(ss.getTotalHoliday());
			totalML=totalML+Integer.parseInt(ss.getTotalMedical());
			totalPL=totalPL+Integer.parseInt(ss.getTotalPrepleave());

		}

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}


	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();

		totalA=0;
		totalL=0;
		totalP=0;
		totalS=0;
		totalML=0;
		totalPL=0;
		totalH=0;

		strDate=new SimpleDateFormat("dd/MM/yyyy").format(selectDate);
		list=new DataBaseMethodsReports().classWiseAttendanceList(selectDate,conn);
		for(ClassInfo ss:list)
		{
			totalA=totalA+Integer.parseInt(ss.getToatalAbsent());
			totalL=totalL+Integer.parseInt(ss.getTotalLeave());
			totalP=totalP+Integer.parseInt(ss.getTotalPresent());
			totalS=totalS+Integer.parseInt(ss.getTotalStudent());

			totalH=totalH+Integer.parseInt(ss.getTotalHoliday());
			totalML=totalML+Integer.parseInt(ss.getTotalMedical());
			totalPL=totalPL+Integer.parseInt(ss.getTotalPrepleave());
		}


		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}


	public  void exportClassAttPdf() throws DocumentException, IOException, FileNotFoundException {

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

			Chunk c3 =null;
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                               Class Attendence Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);



			table.addCell("Class Name");
			table.addCell("Total Present");
			table.addCell("Total Absent");
			table.addCell("Total Leave");
			table.addCell("Total Medical Leave");
			table.addCell("Total Preparation  Leave");
			table.addCell("Total Holiday ");
			table.addCell("Total Student");
			// table.addCell("Ending Date of Leave Apply");
			// table.addCell("Father's Phone");
			//    table.addCell("Message Sent");




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
				table.addCell(new Phrase(String.valueOf(list.get(i).getClassName()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalPresent()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getToatalAbsent()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalLeave()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalMedical()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalPrepleave()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalHoliday()),font));

				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalStudent()),font));

			}
			table.addCell(new Phrase("Total",font));
			table.addCell(new Phrase(String.valueOf(totalP),font));
			table.addCell(new Phrase(String.valueOf(totalA),font));
			table.addCell(new Phrase(String.valueOf(totalL),font));
			table.addCell(new Phrase(String.valueOf(totalML),font));
			table.addCell(new Phrase(String.valueOf(totalPL),font));
			table.addCell(new Phrase(String.valueOf(totalH),font));
			table.addCell(new Phrase(String.valueOf(totalS),font));

			table.setWidthPercentage(110);
			document.add(table);

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Class_Attendence_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Class_Attendance_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public int getTotalP() {
		return totalP;
	}

	public void setTotalP(int totalP) {
		this.totalP = totalP;
	}

	public int getTotalL() {
		return totalL;
	}

	public void setTotalL(int totalL) {
		this.totalL = totalL;
	}

	public int getTotalA() {
		return totalA;
	}

	public void setTotalA(int totalA) {
		this.totalA = totalA;
	}

	public int getTotalS() {
		return totalS;
	}

	public void setTotalS(int totalS) {
		this.totalS = totalS;
	}

	public int getTotalML() {
		return totalML;
	}

	public void setTotalML(int totalML) {
		this.totalML = totalML;
	}

	public int getTotalPL() {
		return totalPL;
	}

	public void setTotalPL(int totalPL) {
		this.totalPL = totalPL;
	}

	public int getTotalH() {
		return totalH;
	}

	public void setTotalH(int totalH) {
		this.totalH = totalH;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

}
