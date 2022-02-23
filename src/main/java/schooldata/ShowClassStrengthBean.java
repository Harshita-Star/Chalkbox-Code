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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
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

import Json.DataBaseMeathodJson;
import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name = "showClassStrengthBean")
@ViewScoped
public class ShowClassStrengthBean implements Serializable {

	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection, selectedType, typeName;
	String className, section, total;
	ArrayList<ClassInfo> list;
	boolean b;
	String sectionName;
	ArrayList<StudentInfo> studentList;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DataBaseMethodStudent objStd = new DataBaseMethodStudent();
	StudentInfo selected;
	DatabaseMethods1 obj = new DatabaseMethods1();
	String schid, session, username, userType;

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		if (userType.equalsIgnoreCase("admin") || userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal") || userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("Administrative Officer") || userType.equalsIgnoreCase("Accounts")) {
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp = obj.allSection(selectedClass, conn);

			if (temp.size() > 0) {
				sectionList.addAll(temp);
			}
		} else {
			String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(username, schid, conn);
			sectionList = obj.allSectionListForClassTeacher(empid, selectedClass, conn);
		}
		schoolDetails = new DatabaseMethods1().fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ShowClassStrengthBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = new DatabaseMethods1().schoolId();
		session = DatabaseMethods1.selectedSessionDetails(schid, conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username = (String) ss.getAttribute("username");
		userType = (String) ss.getAttribute("type");

		if (userType.equalsIgnoreCase("admin") || userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal") || userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts")) {
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classList.add(si);

			ArrayList<SelectItem> temp = obj.allClass(conn);

			if (temp.size() > 0) {
				classList.addAll(temp);
			}
		} else if (userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("Administrative Officer")) {
			String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(username, schid, conn);
			classList = obj.cordinatorClassList(empid, schid, conn);
		} else {
			String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(username, schid, conn);
			classList = obj.allClassListForClassTeacher(empid, schid, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj1 = new DatabaseMethods1();
		ArrayList<String> stdColList = makeStdColumnList();

		studentList = objStd.studentDetail("", selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION,
				QueryConstants.CLASS_STRENGTH, null, null, QueryConstants.IMAGE_WITH_PATH, QueryConstants.KEYWORD, "",
				"", session, schid, stdColList, conn);
		if (studentList.isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		} else
			for (StudentInfo a : studentList) {
				a.setStudentStatus(a.getStudentStatus().toUpperCase());
			}
		b = true;
		total = String.valueOf(studentList.size());

		if (selectedSection.equals("-1")) {
			className = "All";
			sectionName = "All";
		} else {

			className = obj1.classNameFromidSchid(schid, selectedClass, session, conn);
			sectionName = obj1.sectionNameByIdSchid(schid, selectedSection, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void InactiveStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		String session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(), conn);

		ArrayList<String> stdColList = makeStdColumnList();

		studentList = objStd.studentDetail("", selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION,
				QueryConstants.INACTIVE_STRENGTH, null, null, QueryConstants.IMAGE_WITH_PATH, QueryConstants.KEYWORD,
				"", "", session, schid, stdColList, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		} else
			b = true;

		total = String.valueOf(studentList.size());

		if (selectedSection.equals("-1")) {
			className = "All";
			sectionName = "All";
		} else {
			className = obj.classNameFromidSchid(obj.schoolId(), selectedClass, session, conn);
			sectionName = obj.sectionNameByIdSchid(obj.schoolId(), selectedSection, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void studentTypeStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<String> list = objStd.basicFieldsForStudentList();
		studentList = objStd.studentDetail(selectedType, "", "", QueryConstants.STD_TYPE, QueryConstants.STD_TYPE, null,
				null, QueryConstants.IMAGE_WITH_PATH, "", "", "", session, schid, list, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
			total = "0";
		} else {
			b = true;
			total = String.valueOf(studentList.size());
		}

		if (selectedType.equalsIgnoreCase("Regular")) {
			typeName = "Day Scholar";
		} else if (selectedType.equalsIgnoreCase("Hosteler")) {
			typeName = "Hosteler";
		} else if (selectedType.equalsIgnoreCase("Day Brd")) {
			typeName = "Day Boarding";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> makeStdColumnList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.ADMISSION_NUMBER);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.SERIAL_NUMBER);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.STUDENT_NAME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.SECTION_ID);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.FATHERS_NAME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.MOTHERS_NAME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.FATHERS_PHONE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.ADMISSION_DATE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.DOB);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.CATEGORY_ID);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.GENDER);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.CURRENT_ADDRESS);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.PERMANENT_ADDRESS);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.MOTHERS_PHONE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.AADHAR_NUMBER);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.BLOOD_GROUP);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.RELIGION);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.CASTE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.LAST_SCHOOL_NAME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.FATHER_SCHOOL_EMP);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.MOTHER_SCHOOL_EMP);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.FATHER_INCOME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.MOTHER_INCOME);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.TRANSPORT_ROUTE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.STUDENT_TYPE);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.ROLL_NUMBER);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.STUDENT_IMAGE_PATH);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.ADMISSION_REMARK);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.STUDENT_STATUS);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.SINGLE_CHILD);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.LEDGER_NO);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.TEN_ROLL);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.TEN_YEAR_OF_PASS);
		list.add(QueryConstants.KEYWORD + RegistrationColumnName.TEN_BOARD);

		return list;
	}

	public void toNum(Object doc) {
		XSSFWorkbook book = (XSSFWorkbook) doc;
		XSSFSheet sheet = book.getSheetAt(0);

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		sheet.createFreezePane(0, 4);

		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow = sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow + 1, 1);
		XSSFRow r0 = sheet.createRow(0);
		XSSFRow r1 = sheet.createRow(1);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244, 237, 232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		// styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());

		XSSFCell clee = r1.createCell(0);

		clee.setCellValue("Class : " + className);

		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Section : " + sectionName);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total : " + total);
		clee4.setCellStyle(styleb);

		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (int o = 3; o < colCount; o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}
//        
//    

		XSSFRow he = sheet.getRow(0);
		he.setHeight((short) 1550);

		try {

			URL url = new URL(schoolDetails.getDownloadpath() + schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			// FileInputStream my_banner_image = new
			// FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(9);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			// cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			XSSFPicture my_picture = drawing.createPicture(my_anchor, my_picture_id);

			// my_picture.resize();
			// FileOutputStream out = new FileOutputStream(new
			// File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			// book.write(out);
			// out.close();

		} catch (IOException ioex) {

		}

		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short) 1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short) 2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short) 5);

		XSSFRow headerRow = sheet.getRow(3);
		headerRow.setHeight((short) 900);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243, 236, 221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// Font font = book.createFont();
		// font.setColor(IndexedColors.BLACK.getIndex());

		// style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		XSSFCell cle1 = headerRow.createCell(colCount);

		cle1.setCellValue("Student Image");

		cle1.setCellStyle(style);
		for (int i = 0; i < colCount; i++) {
			XSSFCell ce1 = headerRow.getCell(i);
//              if(i==0) {
//                  ce1.setCellValue("Sno");
//              }
//              if(i==4) {
//                  ce1.setCellValue("Concession Assign");
//              }
//              if(i==5) {
//                  ce1.setCellValue("Student Type");
//              }
//              if(i==6) {
//                  ce1.setCellValue("Total Amount");
//              }
			ce1.setCellStyle(style);

		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244, 237, 232));
		((XSSFCellStyle) st).setFillForegroundColor(color1);
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setBorderLeft(BorderStyle.THIN);
		st.setBorderRight(BorderStyle.THIN);
		st.setBorderTop(BorderStyle.THIN);
		st.setBorderBottom(BorderStyle.THIN);

		st.setBottomBorderColor(IndexedColors.RED.getIndex());
		st.setTopBorderColor(IndexedColors.RED.getIndex());
		st.setLeftBorderColor(IndexedColors.RED.getIndex());
		st.setRightBorderColor(IndexedColors.RED.getIndex());

		for (int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
//            //// // System.out.println(rowCount);
//            //// // System.out.println(colCount);
			for (int cellInd = 0; cellInd < colCount; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if (rowInd % 2 == 0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254, 254, 251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				} else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241, 235, 234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				st2.setVerticalAlignment(VerticalAlignment.CENTER);
				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

		for (int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row22 = sheet.getRow(rowInd);
			row22.setHeight((short) 1550);

			for (int cellInd = colCount; cellInd < colCount + 1; cellInd++) {

				try {

					String ul = "";
					String[] sp;
					
					int index = rowInd - 3;
					if (index < studentList.size() && index != studentList.size()) {

					try {
						studentList.get(index).setStudent_image(
								schoolDetails.getDownloadpath() + studentList.get(index).getStdImageWithoutPath());
						if (studentList.get(index).getStudent_image().contains(" "))
							;
						{
							sp = studentList.get(index).getStudent_image().split(" ");

							for (int j = 0; j < sp.length - 1; j++) {
								ul += sp[j] + "%20";
							}

							ul = ul + sp[sp.length - 1];

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					URL url;

						if (studentList.get(rowInd - 3).getStudent_image().contains(" ")) {
							url = new URL(ul);
						} else {
							url = new URL(studentList.get(rowInd - 3).getStudent_image());
						}

						// //// //
						// System.out.println((rowInd-3)+"-"+studentList.get(rowInd-3).getStudent_image());
						// String paramString =
						// URLEncodedUtils.format(studentList.get(rowInd-3).getStudent_image(),"utf-8"
						// );

						InputStream my_banner_image = new BufferedInputStream(url.openStream());
						// //// // System.out.println(my_banner_image.read());
						// FileInputStream my_banner_image = new
						// FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
						byte[] bytes = IOUtils.toByteArray(my_banner_image);
						// //// // System.out.println(bytes.length);
						XSSFDrawing drawing = sheet.createDrawingPatriarch();

						ClientAnchor my_anch = new XSSFClientAnchor();
						my_anch.setRow1(rowInd+1);
						my_anch.setRow2(rowInd + 2);
						my_anch.setCol1(colCount);
						my_anch.setCol2(colCount + 1);
						int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
						// cellll.setCellValue(my_picture_id);
						my_banner_image.close();

						XSSFPicture my_picture = drawing.createPicture(my_anch, my_picture_id);

						// my_picture.resize();
						// FileOutputStream out = new FileOutputStream(new
						// File("C:\\Users\\user\\Desktop\\exc.xlsx"));
						// book.write(out);
						// out.close();

					}

				} catch (IOException ioex) {

				}
			}
		}

	}

	public void toNumWithoutPhoto(Object doc) {
		XSSFWorkbook book = (XSSFWorkbook) doc;
		XSSFSheet sheet = book.getSheetAt(0);

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		sheet.createFreezePane(0, 4);

		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow = sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow + 1, 1);

		XSSFRow r0 = sheet.createRow(0);
		XSSFRow r1 = sheet.createRow(1);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244, 237, 232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		// styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());

		XSSFCell clee = r1.createCell(0);

		clee.setCellValue("Class : " + className);

		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Section : " + sectionName);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total : " + total);
		clee4.setCellStyle(styleb);

		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (int o = 3; o < colCount; o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}
//        
//    

		XSSFRow he = sheet.getRow(0);
		he.setHeight((short) 1550);

		try {

			URL url = new URL(schoolDetails.getDownloadpath() + schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			// FileInputStream my_banner_image = new
			// FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(9);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			// cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			XSSFPicture my_picture = drawing.createPicture(my_anchor, my_picture_id);

			// my_picture.resize();
			// FileOutputStream out = new FileOutputStream(new
			// File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			// book.write(out);
			// out.close();

		} catch (IOException ioex) {

		}

		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short) 1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short) 2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short) 5);

		XSSFRow headerRow = sheet.getRow(3);
		headerRow.setHeight((short) 900);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243, 236, 221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// Font font = book.createFont();
		// font.setColor(IndexedColors.BLACK.getIndex());

		// style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		XSSFCell cle1 = headerRow.createCell(colCount);

		for (int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
//            //// // System.out.println(rowCount);
//            //// // System.out.println(colCount);
			for (int cellInd = 0; cellInd < colCount; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if (rowInd % 2 == 0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254, 254, 251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				} else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241, 235, 234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				st2.setVerticalAlignment(VerticalAlignment.CENTER);
				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

	}

	public void exportClasWisePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Header
		try {

			String src = ls.getDownloadpath() + ls.getImagePath();

			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c = new Chunk(schoolDetails.schoolName + "\n", fo);

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c1 = new Chunk(
					"              " + schoolDetails.add1 + " " + schoolDetails.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                                 Class Wise Student Report\n\n",
					fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : " + className + "                           Section : " + sectionName
					+ "                           Total : " + total + "\n\n", fo);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			// p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(
					new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");
			table.addCell("Class");
			table.addCell("Add. Date");
			table.addCell("Dob");
			table.addCell("Category");
			table.addCell("Gender");
			table.addCell("Address");
			table.addCell("Phone No");
			table.addCell("Aadhar Number");
			table.addCell("Religion ");
			table.addCell("Cast");
			table.addCell("Last School");
			table.addCell("Father Annual Income");
			table.addCell("Mother Annual Income");
			table.addCell("Father School Emp.");
			table.addCell("Mother School Emp.");
			table.addCell("Admission Remark");
			table.addCell("Single Child");
			table.addCell("Ledger No.");
			// table.addCell("Message Sent");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < studentList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()), font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(), font));
				table.addCell(new Phrase(studentList.get(i).getFullName(), font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(), font));
				table.addCell(new Phrase(studentList.get(i).getMotherName(), font));
				table.addCell(new Phrase(
						String.valueOf(studentList.get(i).getClassName() + "-" + studentList.get(i).getSectionName()),
						font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionDate(), font));
				table.addCell(new Phrase(studentList.get(i).getDobString(), font));
				table.addCell(new Phrase(studentList.get(i).getCategory(), font));
				table.addCell(new Phrase(studentList.get(i).getGender(), font));
				table.addCell(new Phrase(studentList.get(i).getCurrentAddress(), font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()), font));
				table.addCell(new Phrase(studentList.get(i).getAadharNo(), font));
				table.addCell(new Phrase(studentList.get(i).getReligion(), font));
				table.addCell(new Phrase(studentList.get(i).getCaste(), font));
				table.addCell(new Phrase(studentList.get(i).getLastSchoolName(), font));
				table.addCell(new Phrase(studentList.get(i).getFatherAnnIncome(), font));
				table.addCell(new Phrase(studentList.get(i).getMotherAnnIncome(), font));
				table.addCell(new Phrase(studentList.get(i).getFatherSchoolEmp(), font));
				table.addCell(new Phrase(studentList.get(i).getMotherSchoolEmp(), font));
				table.addCell(new Phrase(studentList.get(i).getAdmRemark(), font));
				table.addCell(new Phrase(studentList.get(i).getSingleChild(), font));
				table.addCell(new Phrase(studentList.get(i).getLedgerNo(), font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Class_Wise_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name("Class_Wise_Report_Report.pdf").stream(() -> isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

	}

	public void exportClasWiseLessPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Header
		try {

			String src = ls.getDownloadpath() + ls.getImagePath();

			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c = new Chunk(schoolDetails.schoolName + "\n", fo);

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c1 = new Chunk(
					"              " + schoolDetails.add1 + " " + schoolDetails.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                                 Class Wise Student Report\n\n",
					fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : " + className + "                         Section : " + sectionName
					+ "                                Total : " + total + "\n\n", fo);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			// p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Roll No.");
			table.addCell("Sr. No.");
			table.addCell("Student Type");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Contact No.");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < studentList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()), font));
				table.addCell(new Phrase(studentList.get(i).getRollNo(), font));

				table.addCell(new Phrase(studentList.get(i).getSrNo(), font));
				table.addCell(new Phrase(studentList.get(i).getStudentStatus().toUpperCase(), font));

				table.addCell(new Phrase(studentList.get(i).getFullName(), font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(), font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()), font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Class_Wise_Less_Details_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name("Class_Wise_Less_Details_Report.pdf").stream(() -> isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

	}

	public void activeStudent() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		try {
			int i = obj.deleteStudent(selected.getAddNumber(), "ACTIVE", "INACTIVE", conn);
			if (i >= 1) {
				obj.increaseStudentInAddSchool(obj.schoolId(), conn);
				obj.activateUser(selected.getAddNumber(), conn);

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Activated Successfully"));

				InactiveStudentStrength();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void exportInactivePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Header
		try {

			String src = ls.getDownloadpath() + ls.getImagePath();

			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c = new Chunk(schoolDetails.schoolName + "\n", fo);

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c1 = new Chunk(
					"              " + schoolDetails.add1 + " " + schoolDetails.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                                 Inactive Student Report\n\n",
					fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : " + className + "                    Section : " + sectionName
					+ "                    Total : " + total + "\n\n", fo);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			// p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Class Name");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Occupation");
			table.addCell("Mother's Name");

			table.addCell("Add. Date");
			table.addCell("Dob");
			table.addCell("Category");
			table.addCell("Gender");
			table.addCell("Address");
			table.addCell("Phone No");

			// table.addCell("Message Sent");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < studentList.size(); i++) {

				table.addCell(new Phrase(studentList.get(i).getSrNo(), font));
				table.addCell(new Phrase(studentList.get(i).getClassName(), font));

				table.addCell(new Phrase(studentList.get(i).getFullName(), font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(), font));
				table.addCell(new Phrase(studentList.get(i).getFathersOccupation(), font));

				table.addCell(new Phrase(studentList.get(i).getMotherName(), font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionDate(), font));
				table.addCell(new Phrase(studentList.get(i).getDobString(), font));
				table.addCell(new Phrase(studentList.get(i).getCategory(), font));
				table.addCell(new Phrase(studentList.get(i).getGender(), font));
				table.addCell(new Phrase(studentList.get(i).getCurrentAddress(), font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()), font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Inactive_Student_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Inactive_Student_Report.pdf")
				.stream(() -> isFromFirstData).build();

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

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
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

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
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
