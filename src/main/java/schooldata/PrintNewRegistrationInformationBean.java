package schooldata;

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
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean(name = "printNewRegInfo")
@ViewScoped
public class PrintNewRegistrationInformationBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	StudentInfo studentInfo;
	String addNumber;
	transient StreamedContent file;

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public PrintNewRegistrationInformationBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		addNumber = (String) ss.getAttribute("addNumber");
		studentInfo = new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),addNumber, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void exportWelcomePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(253, 719, 125, 26, 10);
		// cb.rectangle(30,30,530,790);
		cb.stroke();
		cb.restoreState();

		SideMenuBean smb = new SideMenuBean();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font fol = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
			Font fom = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			Font fotable = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			String src = smb.getHeaderImage();
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_RIGHT);

			im.scaleAbsoluteHeight(70);
			im.scaleAbsoluteWidth(510);

			// Chunk c = new Chunk("\nBLM Academy Sr. Secondary School \n",fo );

			Chunk c3 = new Chunk(im, -11, -30);

			// Chunk c1 = new Chunk( schoolDetails.add1+ " " +schoolDetails.add2+"
			// \n\n",fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);
			document.add(p1);

			String schANme = "";
			if (smb.list.getBranch_id().equalsIgnoreCase("22")) {
				schANme = "Ever Green";
			} else {
				schANme = "Immortal";
			}

			Chunk c8 = new Chunk(
					"\n\n\n                                                                   WELCOME LETTER\n", fo);

			Chunk c81 = new Chunk(
					"\nDear Parent/Guardian (s),\nWe welcome you to be a part of our esteemed institution in the city.As the new session has started, we request you to send your ward to school from next working day.We at "
							+ schANme + " are entitled to serve the best.\n",
							fol);
			Chunk c82 = new Chunk(
					"Communication is the key to success. We request our parent(s) to please co-ordinate with class teacher and subject teacher during PTM every month.Regular meetings will definitely improve the performance of your ward.\n",
					fol);
			Chunk c83 = new Chunk(
					"Please feel free to contact us during school hours for any complaints and suggestions.\n", fol);
			Chunk c84 = new Chunk(
					"Please check these details furnished by you to our office during the admission of your ward. Also ask if any amendments are required in these details :\n\n",
					fol);

			Paragraph p8 = new Paragraph();
			p8.add(c8);
			p8.add(c81);
			p8.add(c82);
			p8.add(c83);
			p8.add(c84);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n", fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			// p8.setAlignment(Element.ALIGN_CENTER);

			FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1 });

			// table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("ADM. NO.", fotable));
			table.addCell(new Phrase(studentInfo.srNo, fotable));
			table.addCell(new Phrase("DATE OF ADM.", fotable));
			table.addCell(new Phrase(studentInfo.startingDateStr, fotable));
			table.addCell(new Phrase("NAME OF STUDENT", fotable));
			table.addCell(new Phrase(studentInfo.fname, fotable));
			table.addCell(new Phrase("AADHAR NO.", fotable));
			table.addCell(new Phrase(studentInfo.aadharNo, fotable));
			table.addCell(new Phrase("FATHER'S NAME", fotable));
			table.addCell(new Phrase(studentInfo.fathersName, fotable));
			table.addCell(new Phrase("AADHAR NO.", fotable));
			table.addCell(new Phrase(studentInfo.FatherAadhaar, fotable));
			table.addCell(new Phrase("MOTHER'S NAME", fotable));
			table.addCell(new Phrase(studentInfo.motherName, fotable));
			table.addCell(new Phrase("AADHAR NO.", fotable));
			table.addCell(new Phrase(studentInfo.motherAadhaar, fotable));
			table.addCell(new Phrase("DOB", fotable));
			table.addCell(new Phrase(studentInfo.dobString, fotable));
			table.addCell(new Phrase("CLASS & SEC.", fotable));
			table.addCell(new Phrase(studentInfo.className, fotable));
			table.addCell(new Phrase("ADDRESS", fotable));

			PdfPCell cel = new PdfPCell(new Phrase(studentInfo.currentAddress, fotable));
			cel.setColspan(3);
			// .setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cel);

			table.addCell(new Phrase("SOCIAL CATEGORY", fotable));
			table.addCell(new Phrase(studentInfo.category, fotable));
			PdfPCell celll = new PdfPCell(new Phrase(""));
			celll.setColspan(2);
			// .setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(celll);
			table.addCell(new Phrase("FATHER'S MOB.", fotable));

			String fatherPh = "";
			try {
				fatherPh = String.valueOf(studentInfo.getFathersPhone());
			} catch (Exception e) {
				e.printStackTrace();
			}

			table.addCell(new Phrase(fatherPh, fotable));
			table.addCell(new Phrase("MOTHER'S MOB.", fotable));
			if (studentInfo.getMothersPhone() == 0) {
				table.addCell(new Phrase(""));
			} else {
				String motherPh = "";
				try {
					motherPh = String.valueOf(studentInfo.getMothersPhone());
				} catch (Exception e) {
					e.printStackTrace();
				}
				table.addCell(new Phrase(motherPh, fotable));
			}

			table.setWidthPercentage(103);
			document.add(table);

			Chunk c12 = new Chunk(
					"\n\n                                                       DETAILS OF TRANSPORT (IF AVAILING)\n\n",
					fo);
			Paragraph p12 = new Paragraph();
			p12.add(c12);
			document.add(p12);

			PdfPTable tabl = new PdfPTable(new float[] { 1, 1, 1, 1 });

			// tabl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			tabl.addCell(new Phrase("BUS NO.", fotable));
			tabl.addCell(new Phrase(studentInfo.vehicleNo, fotable));
			tabl.addCell(new Phrase("STOPAGE", fotable));
			if(studentInfo.getTransportRoute().equalsIgnoreCase("")) {
				tabl.addCell(new Phrase(""));
			} else {
				String spl[] = studentInfo.getTransportRoute().split("School to");
				tabl.addCell(new Phrase(spl[1], fotable));
			}
			tabl.addCell(new Phrase("DRIVER'S NAME", fotable));
			tabl.addCell(new Phrase(studentInfo.driverName, fotable));
			tabl.addCell(new Phrase("DRIVER'S NO.", fotable));
			if (studentInfo.getDriverNo() == "0") {
				tabl.addCell(new Phrase(""));
			} else {
				tabl.addCell(new Phrase(studentInfo.driverNo, fotable));
			}
			tabl.addCell(new Phrase("CLEANER'S NAME", fotable));
			tabl.addCell(new Phrase(""));
			tabl.addCell(new Phrase("CLEANER'S NO.", fotable));
			tabl.addCell(new Phrase(""));
			tabl.addCell(new Phrase("LADY ATTENDANT", fotable));
			tabl.addCell(new Phrase(studentInfo.attendantName, fotable));
			tabl.addCell(new Phrase("TRANSPORT FEE", fotable));
			tabl.addCell(new Phrase(studentInfo.transfee, fotable));

			tabl.setWidthPercentage(103);
			document.add(tabl);

			Chunk c10 = new Chunk(
					"\n\n                                                                                                                                                                                                ADMIN",
					fom);
			Paragraph p10 = new Paragraph();
			p10.add(c10);
			document.add(p10);

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Welcome.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Welcome.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}
	public  void exportConsentPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();



		PdfWriter writer =  PdfWriter.getInstance(document, baos);
		document.open();
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30,510,550,292, 10);
		cb.rectangle(10,498,572,1);
		// cb.rectangle(10,165,572,1);
		cb.roundRectangle(30,165,555,302, 10);
		// cb.roundRectangle(30,50,555,120, 10);
		// cb.rectangle(30,30,530,790);
		cb.stroke();
		cb.restoreState();

		Font fbo = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fnt = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font f11 = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
		Chunk r1 = new Chunk("                                                                   Consent Letter To The Class Teacher",fbo);
		Chunk c1 = new Chunk("\n\nTo,\n                        The Class Teacher,\nPlease enroll this student in your class:- ",f11 );
		Paragraph p1 = new Paragraph();
		p1.add(r1);
		p1.add(c1);
		document.add(p1);





		PdfPTable table2;


		table2 = new PdfPTable(new float[] {2f,1});
		table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table2.getDefaultCell().setPaddingTop(1);
		table2.getDefaultCell().setIndent(1.1f);
		table2.getDefaultCell().setLeading(1.1f,1.1f);



		table2.addCell(new Phrase("1. Admission No :"+studentInfo.srNo,fnt));

		table2.addCell(new Phrase("Date Of Admission :"+studentInfo.startingDateStr,fnt));
		table2.addCell(new Phrase("2. Pupil's Name :"+studentInfo.fname,fnt));

		table2.addCell(new Phrase("Aadhar No.:"+studentInfo.aadharNo,fnt));
		table2.addCell(new Phrase("3. Father's Name :"+studentInfo.fathersName,fnt));

		table2.addCell(new Phrase("Aadhar No.:"+studentInfo.FatherAadhaar,fnt));
		table2.addCell(new Phrase("4.  Mother's Name :"+studentInfo.motherName,fnt));
		table2.addCell(new Phrase("Aadhar No.:"+studentInfo.motherAadhaar,fnt));

		table2.addCell(new Phrase("5.  Address :"+studentInfo.currentAddress,fnt));
		table2.addCell(new Phrase(""));

		table2.addCell(new Phrase("6.  Date of Birth as recorded in the Admission Register :"+studentInfo.dobString,fnt));
		table2.addCell(new Phrase(""));
		table2.addCell(new Phrase("7.  Class   Section :"+studentInfo.className,fnt));
		table2.addCell(new Phrase(""));

		table2.addCell(new Phrase("8.  Contact No. :Father's:"+studentInfo.fathersPhone+"  Mother's:"+studentInfo.mothersPhone+"  Guardian's:"+studentInfo.residenceMobile ,fnt));
		table2.addCell(new Phrase(""));
		SideMenuBean sbm = new SideMenuBean();

		if((sbm.list.getBranch_id().equalsIgnoreCase("22"))&&(sbm.list.getBranch_id().equalsIgnoreCase("27"))){
			table2.addCell(new Phrase("9.  Fee Details : Concession Category:"+studentInfo.concessionName,fnt));
			table2.addCell(new Phrase(""));

		}
		if((sbm.list.getBranch_id().equalsIgnoreCase("22"))||(sbm.list.getBranch_id().equalsIgnoreCase("27"))){


			table2.addCell(new Phrase("9.  Social Category :"+studentInfo.category,fnt));
			table2.addCell(new Phrase(""));
		}
		if((!studentInfo.getTransportRoute().equalsIgnoreCase("")) && ((sbm.list.getBranch_id().equalsIgnoreCase("22"))||(sbm.list.getBranch_id().equalsIgnoreCase("27")))){

			table2.addCell(new Phrase("10.  Bus No :"+studentInfo.vehicleNo+"       Transport Route :"+studentInfo.transportRoute ,fnt));
			table2.addCell(new Phrase(""));
		}




		table2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.setWidthPercentage(100);

		document.add(table2);

		Chunk c6 = new Chunk("\nDate Of Issue "+currentDate+"                                                                                                             Principal",fnt);
		Paragraph p6 = new Paragraph();
		p6.add(c6);
		document.add(p6);

		Chunk r2 = new Chunk("\n\n\n \n\n\n\n                                                                Consent Letter To The Clerical Department",fbo);

		Chunk c3 = new Chunk("\nTo,\n                        The Clerical Department,\nPlease enter the following details of the below mentioned student in your records:-  ",f11 );
		Paragraph p3 = new Paragraph();
		p3.add(r2);
		p3.add(c3);
		document.add(p3);





		PdfPTable table;


		table = new PdfPTable(new float[] {1});
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.getDefaultCell().setPaddingTop(1);
		table.getDefaultCell().setIndent(1.1f);
		table.getDefaultCell().setLeading(1.1f,1.1f);


		table.addCell(new Phrase("1. Date Of Admission :"+studentInfo.startingDateStr,fnt));
		table.addCell(new Phrase("2. Admission No :"+studentInfo.srNo,fnt));


		table.addCell(new Phrase("3. Pupil's Name :"+studentInfo.fname+"       4. Class   Section : "+studentInfo.className,fnt));


		table.addCell(new Phrase("5. Father's Name :"+studentInfo.fathersName,fnt));

		table.addCell(new Phrase("6.  Mother's Name :"+studentInfo.motherName,fnt));


		table.addCell(new Phrase("7.  Address :"+studentInfo.currentAddress,fnt));


		table.addCell(new Phrase("8.  Date of Birth as recorded in the Admission Register :"+studentInfo.dobString,fnt));


		table.addCell(new Phrase("9.  Contact No. :Father's:"+studentInfo.fathersPhone+"  Mother's:"+studentInfo.mothersPhone+"  Guardian's:"+studentInfo.residenceMobile ,fnt));

		SideMenuBean sbmj = new SideMenuBean();


		table.addCell(new Phrase("10.  Fee Details : Concession Category:"+studentInfo.concessionName,fnt));
		if(!studentInfo.getTransportRoute().equalsIgnoreCase("")){


			table.addCell(new Phrase("11.  Bus No :"+studentInfo.vehicleNo+"       Transport Route :"+studentInfo.transportRoute+"         Transport Fee :"+studentInfo.transfee ,fnt));
			table.addCell(new Phrase(""));
		}

		if((sbmj.list.getBranch_id().equalsIgnoreCase("22"))||(sbmj.list.getBranch_id().equalsIgnoreCase("27"))){

			if(!studentInfo.getTransportRoute().equalsIgnoreCase("")) {
				table.addCell(new Phrase("12. Aadhar No. :"+studentInfo.aadharNo,fnt));

			}
			else {
				table.addCell(new Phrase("11. Aadhar No. :"+studentInfo.aadharNo,fnt));
			}

			if(!studentInfo.getTransportRoute().equalsIgnoreCase("")) {
				table.addCell(new Phrase("13. Social Category :"+studentInfo.category,fnt));

			}
			else {
				table.addCell(new Phrase("12. Social Category :"+studentInfo.category,fnt));
			}

		}





		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100);

		document.add(table);

		Chunk c11 = new Chunk("Date Of Issue "+currentDate+"                                                                                                             Principal",fnt);
		Paragraph p11 = new Paragraph();
		p11.add(c11);
		document.add(p11);

		//	         Chunk r3 = new Chunk("\n  \n\n\n                                                                 Consent Letter To The Transporter",fbo);
		//	         Chunk c14 = new Chunk("\n                  "+studentInfo.driverName+"\n                                    "+studentInfo.fname+"                                         "+studentInfo.fathersName,fnt);
		//	         Chunk c15 = new Chunk("\n                      "+studentInfo.className+"                                    "+studentInfo.transportRoute+"\n                             "+studentInfo.fathersPhone+"      Date ofIssue : "+currentDate+"                                                Principal",fnt);
		//
		//	         Paragraph p12 = new Paragraph();
		//	         p12.add(r3);
		//	         p12.add(c14);
		//	         p12.add(c15);
		//	         document.add(p12);




		//	       Paragraph p10 = new Paragraph("\n");
		//	       document.add(p10);

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Consent.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Consent.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
}
