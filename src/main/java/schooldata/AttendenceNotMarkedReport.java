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

import Json.DataBaseMeathodJson;

@ManagedBean(name="attendenceNotMarkedReport")
@ViewScoped
public class AttendenceNotMarkedReport implements Serializable
{
	ArrayList<ClassInfo> classList,allClassList;
	boolean b;
	Date date=new Date();
	String total,strDate;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	public AttendenceNotMarkedReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		search();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		int count = 1;
		DataBaseMeathodJson dbj = new DataBaseMeathodJson();
		boolean checkAtt,check;
		classList = new ArrayList<>();
		strDate=new SimpleDateFormat("yyyy-MM-dd").format(date);
		Connection conn = DataBaseConnection.javaConnection();
		b=true;
		String schoolid = new DatabaseMethods1().schoolId();
		allClassList=new DataBaseMeathodJson().schoolSectionList(schoolid,conn);
		if(allClassList.size()>0)
		{
			for(ClassInfo cc : allClassList)
			{
				checkAtt = new DatabaseMethods1().checkWhetherAttendanceMarked(cc.getSectionId(),strDate,schoolid,conn);
				if(checkAtt==false)
				{
					check = dbj.checkSectionHolidayForAttendanceJSON(strDate, conn, schoolid, cc.getSectionId());
					if(check==false)
					{
						classList.add(cc);
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No record Found"));
		}

		for(ClassInfo ci : classList)
		{
			ci.setSerialNumber(String.valueOf(count++));
		}

		total=String.valueOf(classList.size());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportNotMarkedPdf() throws DocumentException, IOException, FileNotFoundException {

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
				// TODO: handle exception
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
			Chunk c8 = new Chunk("\n                                                    Attendence Not Marked Report("+strDate+")\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");


			table.addCell("Class");
			table.addCell("Section");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<classList.size();i++){
				table.addCell(new Phrase(String.valueOf(classList.get(i).getSerialNumber()),font));
				table.addCell(new Phrase(classList.get(i).getClassName(),font));
				table.addCell(new Phrase(classList.get(i).getSectionName(),font));
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Attendence_Not_Marked_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Attendence_Not_Marked_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}


	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}


	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public String getTotal() {
		return total;
	}


	public void setTotal(String total) {
		this.total = total;
	}


	public String getStrDate() {
		return strDate;
	}


	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
