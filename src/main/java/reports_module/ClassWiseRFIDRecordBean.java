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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

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
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name = "classWiseRFIDRecord")
@ViewScoped

public class ClassWiseRFIDRecordBean implements Serializable {
	Date date = new Date();
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section, dateStr, username, userType;
	SchoolInfoList schoolDetails;
	ArrayList<StudentInfo> slist;
	transient StreamedContent file;
	String inBus1 = "", outBus1 = "", inSch = "", outSch = "", inBus2 = "", outBus2 = "";
	String inBusMorn = "", notInBusMorn = "", outBusMorn = "", notOutBusMorn = "", inSchool = "", notInSchool = "",
			outSchool = "", notOutSchool = "", inBusEven = "", notInBusEven = "", outBusEven = "", notOutBusEven = "";
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	String schoolId, sessionValue;

	public ClassWiseRFIDRecordBean() {
		date = new Date();
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId, conn);
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			classList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList = obj1.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList=obj1.allClassListForClassTeacher(empid,schoolId,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			sectionList.add(si);

			ArrayList<SelectItem> temp = obj1.allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=obj1.allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		schoolDetails = obj1.fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showRFID() {
		inBus1 = outBus1 = inSch = outSch = inBus2 = outBus2 = "";
		inBusMorn = notInBusMorn = outBusMorn = notOutBusMorn = inSchool = notInSchool = outSchool = notOutSchool = inBusEven = notInBusEven = outBusEven = notOutBusEven = "";

		Connection conn = DataBaseConnection.javaConnection();
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

		ArrayList<String> list = new DataBaseMethodStudent().basicFieldsForStudentList();
		slist = new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass,
				QueryConstants.BY_CLASS_SECTION, QueryConstants.RFID_TRANSPORT, null, null, strDate, "", "", "",
				sessionValue, schoolId, list, conn);
		ArrayList<StudentInfo> sAll = new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass,
				QueryConstants.BY_CLASS_SECTION, QueryConstants.RFID_NO_TRANSPORT, null, null, strDate, "", "", "",
				sessionValue, schoolId, list, conn);
		
		slist.addAll(sAll);

		if (selectedClass.equalsIgnoreCase("All")) {
			className = "all";
		} else {
			className = obj1.classNameFromidSchid(schoolId, selectedClass, sessionValue, conn);
		}

		if (selectedSection.equalsIgnoreCase("all")) {
			section = "All";
		} else {
			section = obj1.sectionNameByIdSchid(schoolId, selectedSection, conn);
		}

		int ib1 = 0, ob1 = 0, isch = 0, osch = 0, ib2 = 0, ob2 = 0;

		for (StudentInfo tt : slist) {
			if (!tt.getRfidDataInfo().getInBusMorn().equals("")) {
				ib1 += 1;
			}

			if (!tt.getRfidDataInfo().getOutBusMorn().equals("")) {
				ob1 += 1;
			}

			if (!tt.getRfidDataInfo().getInBusEven().equals("")) {
				ib2 += 1;
			}

			if (!tt.getRfidDataInfo().getOutBusEven().equals("")) {
				ob2 += 1;
			}

			if (!tt.getRfidDataInfo().getInSchool().equals("")) {
				isch += 1;
			}

			if (!tt.getRfidDataInfo().getOutSchool().equals("")) {
				osch += 1;
			}

		}

		inBus1 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ib1 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ib1);
		outBus1 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ob1 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ob1);

		inSch = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + isch + ",\n"
				+ "Students Not Punched = " + (slist.size() - isch);
		outSch = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + osch + ",\n"
				+ "Students Not Punched = " + (slist.size() - osch);

		inBus2 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ib2 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ib2);
		outBus2 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ob2 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ob2);

		inBusMorn = "Students Punched = " + ib1;
		notInBusMorn = "Students Not Punched = " + (slist.size() - ib1);
		outBusMorn = "Students Punched = " + ob1;
		notOutBusMorn = "Students Not Punched = " + (slist.size() - ob1);

		inSchool = "Students Punched = " + isch;
		notInSchool = "Students Not Punched = " + (slist.size() - isch);
		outSchool = "Students Punched = " + osch;
		notOutSchool = "Students Not Punched = " + (slist.size() - osch);

		inBusEven = "Students Punched = " + ib2;
		notInBusEven = "Students Not Punched = " + (slist.size() - ib2);
		outBusEven = "Students Punched = " + ob2;
		notOutBusEven = "Students Not Punched = " + (slist.size() - ob2);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showSchoolRFID() {
		inBus1 = outBus1 = inSch = outSch = inBus2 = outBus2 = "";
		inBusMorn = notInBusMorn = outBusMorn = notOutBusMorn = inSchool = notInSchool = outSchool = notOutSchool = inBusEven = notInBusEven = outBusEven = notOutBusEven = "";

		Connection conn = DataBaseConnection.javaConnection();
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

		ArrayList<String> list = new DataBaseMethodStudent().basicFieldsForStudentList();
		slist.addAll(new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass,
				QueryConstants.BY_CLASS_SECTION, QueryConstants.RFID_NO_TRANSPORT, null, null, strDate, "", "", "",
				sessionValue, schoolId, list, conn));

		if (selectedClass.equalsIgnoreCase("All")) {
			className = "all";
		} else {
			className = obj1.classNameFromidSchid(schoolId, selectedClass, sessionValue, conn);
		}

		if (selectedSection.equalsIgnoreCase("all")) {
			section = "All";
		} else {
			section = obj1.sectionNameByIdSchid(schoolId, selectedSection, conn);
		}

		int ib1 = 0, ob1 = 0, isch = 0, osch = 0, ib2 = 0, ob2 = 0;

		for (StudentInfo tt : slist) {
			if (!tt.getRfidDataInfo().getInSchool().equals("")) {
				isch += 1;
			}

			if (!tt.getRfidDataInfo().getOutSchool().equals("")) {
				osch += 1;
			}

		}

		// inBus1 = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+ib1+",\n"+"Students Not Punched = "+(slist.size()-ib1);
		// outBus1 = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+ob1+",\n"+"Students Not Punched = "+(slist.size()-ob1);

		inSch = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + isch + ",\n"
				+ "Students Not Punched = " + (slist.size() - isch);
		outSch = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + osch + ",\n"
				+ "Students Not Punched = " + (slist.size() - osch);

		// inBus2 = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+ib2+",\n"+"Students Not Punched = "+(slist.size()-ib2);
		// outBus2 = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+ob2+",\n"+"Students Not Punched = "+(slist.size()-ob2);

		inSchool = "Students Punched = " + isch;
		notInSchool = "Students Not Punched = " + (slist.size() - isch);
		outSchool = "Students Punched = " + osch;
		notOutSchool = "Students Not Punched = " + (slist.size() - osch);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showTransRFID() {
		inBus1 = outBus1 = inSch = outSch = inBus2 = outBus2 = "";
		inBusMorn = notInBusMorn = outBusMorn = notOutBusMorn = inSchool = notInSchool = outSchool = notOutSchool = inBusEven = notInBusEven = outBusEven = notOutBusEven = "";

		Connection conn = DataBaseConnection.javaConnection();
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

		ArrayList<String> list = new DataBaseMethodStudent().basicFieldsForStudentList();
		slist.addAll(new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass,
				QueryConstants.BY_CLASS_SECTION, QueryConstants.RFID_TRANSPORT, null, null, strDate, "", "", "",
				sessionValue, schoolId, list, conn));

		if (selectedClass.equalsIgnoreCase("All")) {
			className = "all";
		} else {
			className = obj1.classNameFromidSchid(schoolId, selectedClass, sessionValue, conn);
		}

		if (selectedSection.equalsIgnoreCase("all")) {
			section = "All";
		} else {
			section = obj1.sectionNameByIdSchid(schoolId, selectedSection, conn);
		}

		int ib1 = 0, ob1 = 0, isch = 0, osch = 0, ib2 = 0, ob2 = 0;

		for (StudentInfo tt : slist) {
			if (!tt.getRfidDataInfo().getInBusMorn().equals("")) {
				ib1 += 1;
			}

			if (!tt.getRfidDataInfo().getOutBusMorn().equals("")) {
				ob1 += 1;
			}

			if (!tt.getRfidDataInfo().getInBusEven().equals("")) {
				ib2 += 1;
			}

			if (!tt.getRfidDataInfo().getOutBusEven().equals("")) {
				ob2 += 1;
			}

		}

		inBus1 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ib1 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ib1);
		outBus1 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ob1 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ob1);

		// inSch = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+isch+",\n"+"Students Not Punched = "+(slist.size()-isch);
		// outSch = "Total Students = "+slist.size()+",\n"+"Students Punched =
		// "+osch+",\n"+"Students Not Punched = "+(slist.size()-osch);

		inBus2 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ib2 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ib2);
		outBus2 = "Total Students = " + slist.size() + ",\n" + "Students Punched = " + ob2 + ",\n"
				+ "Students Not Punched = " + (slist.size() - ob2);

		inBusMorn = "Students Punched = " + ib1;
		notInBusMorn = "Students Not Punched = " + (slist.size() - ib1);
		outBusMorn = "Students Punched = " + ob1;
		notOutBusMorn = "Students Not Punched = " + (slist.size() - ob1);

		inBusEven = "Students Punched = " + ib2;
		notInBusEven = "Students Not Punched = " + (slist.size() - ib2);
		outBusEven = "Students Punched = " + ob2;
		notOutBusEven = "Students Not Punched = " + (slist.size() - ob2);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void exportLibPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = obj1.fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src = ls.getDownloadpath() + ls.getImagePath();
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);

			Chunk c = new Chunk(ls.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(ls.add1 + " " + ls.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();
			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                           RFID Record " + className
					+ " " + section + " " + dateStr + " \n\n", fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(10);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Name ");
			table.addCell("Class");
			table.addCell("Transport Status ");

			table.addCell("Pick From Home Stop");
			table.addCell("Drop In School");
			table.addCell("Enter In School");
			table.addCell("Exit From School");
			table.addCell("Pick From School");
			table.addCell("Drop At Home Stop");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < slist.size(); i++) {
				table.addCell(new Phrase(String.valueOf(i + 1), font));
				table.addCell(new Phrase(slist.get(i).getFullName(), font));
				table.addCell(new Phrase(slist.get(i).getClassName() + " - " + slist.get(i).getSectionName(), font));
				table.addCell(new Phrase(slist.get(i).getTransStatus()));

				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusMorn(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusMorn(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInSchool(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutSchool(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusEven(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusEven(), font));

			}

			table.addCell("");
			table.addCell("");
			table.addCell("");
			table.addCell("");

			table.addCell(new Phrase(inBus1, font));
			table.addCell(new Phrase(outBus1, font));
			table.addCell(new Phrase(inSch, font));
			table.addCell(new Phrase(outSch, font));
			table.addCell(new Phrase(inBus2, font));
			table.addCell(new Phrase(outBus2, font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				"RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf");
		
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void exportSchPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = obj1.fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src = ls.getDownloadpath() + ls.getImagePath();
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);

			Chunk c = new Chunk(ls.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(ls.add1 + " " + ls.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();
			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                          School RFID Record "
					+ className + " " + section + " " + dateStr + " \n\n", fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(5);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Name ");
			table.addCell("Class");
			// table.addCell("Transport Status ");

//			table.addCell("Board In Bus");
//			table.addCell("Drop To School");
			table.addCell("Enter In School");
			table.addCell("Exit From School");
//			table.addCell("Board From School");
//			table.addCell("Drop To Home");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < slist.size(); i++) {
				table.addCell(new Phrase(String.valueOf(i + 1), font));
				table.addCell(new Phrase(slist.get(i).getFullName(), font));
				table.addCell(new Phrase(slist.get(i).getClassName() + " - " + slist.get(i).getSectionName(), font));
				// table.addCell(new Phrase(slist.get(i).getTransStatus()));

//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusMorn(),font));
//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusMorn(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInSchool(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutSchool(), font));
//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusEven(),font));
//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusEven(),font));

			}

			table.addCell("");
			table.addCell("");
			table.addCell("");

			table.addCell(new Phrase(inSch, font));
			table.addCell(new Phrase(outSch, font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				"School_RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf");
		
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("School_RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void exportTransPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = obj1.fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src = ls.getDownloadpath() + ls.getImagePath();
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);

			Chunk c = new Chunk(ls.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(ls.add1 + " " + ls.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();
			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                          Transport RFID Record "
					+ className + " " + section + " " + dateStr + " \n\n", fo);
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
			// table.addCell("Transport Status ");

			table.addCell("Pick From Home Stop");
			table.addCell("Drop In School");
//			table.addCell("In School");
//			table.addCell("Out School");
			table.addCell("Pcick From School");
			table.addCell("Drop At Home Stop");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < slist.size(); i++) {
				table.addCell(new Phrase(String.valueOf(i + 1), font));
				table.addCell(new Phrase(slist.get(i).getFullName(), font));
				table.addCell(new Phrase(slist.get(i).getClassName() + " - " + slist.get(i).getSectionName(), font));
				// table.addCell(new Phrase(slist.get(i).getTransStatus()));

				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusMorn(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusMorn(), font));
//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInSchool(),font));
//				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutSchool(),font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getInBusEven(), font));
				table.addCell(new Phrase(slist.get(i).getRfidDataInfo().getOutBusEven(), font));

			}

			table.addCell("");
			table.addCell("");
			table.addCell("");

			table.addCell(new Phrase(inBus1, font));
			table.addCell(new Phrase(outBus1, font));
			table.addCell(new Phrase(inBus2, font));
			table.addCell(new Phrase(outBus2, font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				"Transport_RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Transport_RFID_Record_" + className + "_" + section + "_" + dateStr + ".pdf").stream(()->isFromFirstData).build();


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

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
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

	public String getInBus1() {
		return inBus1;
	}

	public void setInBus1(String inBus1) {
		this.inBus1 = inBus1;
	}

	public String getOutBus1() {
		return outBus1;
	}

	public void setOutBus1(String outBus1) {
		this.outBus1 = outBus1;
	}

	public String getInSch() {
		return inSch;
	}

	public void setInSch(String inSch) {
		this.inSch = inSch;
	}

	public String getOutSch() {
		return outSch;
	}

	public void setOutSch(String outSch) {
		this.outSch = outSch;
	}

	public String getInBus2() {
		return inBus2;
	}

	public void setInBus2(String inBus2) {
		this.inBus2 = inBus2;
	}

	public String getOutBus2() {
		return outBus2;
	}

	public void setOutBus2(String outBus2) {
		this.outBus2 = outBus2;
	}

	public String getInBusMorn() {
		return inBusMorn;
	}

	public void setInBusMorn(String inBusMorn) {
		this.inBusMorn = inBusMorn;
	}

	public String getNotInBusMorn() {
		return notInBusMorn;
	}

	public void setNotInBusMorn(String notInBusMorn) {
		this.notInBusMorn = notInBusMorn;
	}

	public String getOutBusMorn() {
		return outBusMorn;
	}

	public void setOutBusMorn(String outBusMorn) {
		this.outBusMorn = outBusMorn;
	}

	public String getNotOutBusMorn() {
		return notOutBusMorn;
	}

	public void setNotOutBusMorn(String notOutBusMorn) {
		this.notOutBusMorn = notOutBusMorn;
	}

	public String getInSchool() {
		return inSchool;
	}

	public void setInSchool(String inSchool) {
		this.inSchool = inSchool;
	}

	public String getNotInSchool() {
		return notInSchool;
	}

	public void setNotInSchool(String notInSchool) {
		this.notInSchool = notInSchool;
	}

	public String getOutSchool() {
		return outSchool;
	}

	public void setOutSchool(String outSchool) {
		this.outSchool = outSchool;
	}

	public String getNotOutSchool() {
		return notOutSchool;
	}

	public void setNotOutSchool(String notOutSchool) {
		this.notOutSchool = notOutSchool;
	}

	public String getInBusEven() {
		return inBusEven;
	}

	public void setInBusEven(String inBusEven) {
		this.inBusEven = inBusEven;
	}

	public String getNotInBusEven() {
		return notInBusEven;
	}

	public void setNotInBusEven(String notInBusEven) {
		this.notInBusEven = notInBusEven;
	}

	public String getOutBusEven() {
		return outBusEven;
	}

	public void setOutBusEven(String outBusEven) {
		this.outBusEven = outBusEven;
	}

	public String getNotOutBusEven() {
		return notOutBusEven;
	}

	public void setNotOutBusEven(String notOutBusEven) {
		this.notOutBusEven = notOutBusEven;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
