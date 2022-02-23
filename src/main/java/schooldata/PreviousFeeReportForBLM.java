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

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
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

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="PreviousFeeReportForBLM")
@ViewScoped
public class PreviousFeeReportForBLM implements Serializable{

	

	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	boolean showButton;
	double fees,paid;
	boolean b;
	String total;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;


	public PreviousFeeReportForBLM()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<StudentInfo> studentList1=new DataBaseMethodsReports().allPreviousFeeList("251",conn);
		ArrayList<StudentInfo> studentList11=new DataBaseMethodsReports().allPreviousFeeList("252",conn);
		studentList=new ArrayList<>();
		studentList.addAll(studentList1);
		studentList.addAll(studentList11);
		
		schoolDetails =new DatabaseMethods1().fullSchoolInfo("251",conn);
		if(studentList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No record Found"));
			b=false;
		}
		else
		{
			b=true;
		}
		total=String.valueOf(studentList.size());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		fees=Double.parseDouble(selectedStudent.getAdmissionFeeDueAmount());
		paid=Double.parseDouble(selectedStudent.getPercentage());
	}

	public void updateFeeDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(fees<paid)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees cannot be less than Paid Amount"));
		}
		else
		{
			int i=new DatabaseMethods1().updatePreviousFees(selectedStudent.getAddNumber(),fees,DatabaseMethods1.selectedSessionDetails(selectedStudent.getSchid(),conn),conn);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee Updated Sucessfully"));
				studentList=new DataBaseMethodsReports().allPreviousFeeList(new DatabaseMethods1().schoolId(),conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deleteFee()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().deletePreviousFees(selectedStudent.getAddNumber(),conn);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee Deleted Sucessfully"));
			studentList=new DataBaseMethodsReports().allPreviousFeeList(new DatabaseMethods1().schoolId(),conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportPreviousPdf() throws DocumentException, IOException, FileNotFoundException {

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

			
			
			String src ="";
			try {
				src =ls.getDownloadpath()+ls.getMarksheetHeader();
			} catch (Exception e) {
				e.printStackTrace();
			}
			 
			Image im = null;
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


			Chunk c8 = new Chunk("\n                                                                     Previous Fee Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);







			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Adm. No.");

			table.addCell("Name");
			table.addCell("Father's Name");
			table.addCell("Contact");

			table.addCell("Total Fees");
			table.addCell("Paid Amount");
			table.addCell("Discount Amount");
			table.addCell("Left Amount");
			//    table.addCell("Current Address");

			//    table.addCell("Father's Phone");





			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentList.size();i++){
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSrNo()),font));
				table.addCell(new Phrase(studentList.get(i).getFname(),font));
				table.addCell(new Phrase(studentList.get(i).getFatherName(),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionFeeDueAmount(),font));
				table.addCell(new Phrase(studentList.get(i).getPercentage(),font));
				table.addCell(new Phrase(studentList.get(i).getDiscountfee(),font));
				table.addCell(new Phrase(studentList.get(i).getAnnualFeeDueAmount(),font));







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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Previous_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Previous_Fee_Report.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}


	public void toNumPreFee(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		new SimpleDateFormat("dd/MM/yyyy");


		sheet.createFreezePane(0, 4);


		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow= sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow+1, 1);
		XSSFRow r0 =     sheet.createRow(0);
		XSSFRow r1 =     sheet.createRow(1);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//     styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		//    styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue("Total :-");
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue(total);
		clee3.setCellStyle(styleb);


		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=2;o<colCount;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}



		XSSFRow he = sheet.getRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



		try
		{
			URL url = new URL(schoolDetails.getDownloadpath()+schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//      FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(8);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();




		} catch (IOException ioex) {
			ioex.printStackTrace();
		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);

		XSSFRow headerRow = sheet.getRow(3);
		headerRow.setHeight((short)900);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//   Font font = book.createFont();
		//  font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i);
			if(i==0) {
				ce1.setCellValue("Sr.No.");
			}
			ce1.setCellStyle(style);


		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244,237,232));
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


		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <7 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

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

		sheet.getRow(5);
		double total=0.0;
		double[] sum = new double[colCount+1];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 6; cellInd <colCount ; cellInd++) {

				if(cellInd!=4) {
					XSSFCell cell = row.getCell(cellInd);






					int status=0,counter=0,dot=0;
					Character ch[] = new Character[3000];

					String strVal = cell.getStringCellValue();
					if(strVal.equalsIgnoreCase("")) {
						status=5;
					}

					for(int i=0;i<strVal.length();i++)
					{
						ch[i] = strVal.charAt(i);
						String s =Character.toString(ch[i]);


						if(Character.isDigit(ch[i]))
						{
							counter++;
						}
						if(s.equals("."))
						{
							dot++;
						}
						if(s.equals(""))
						{
							status=1;
						}
					}


					if(status==1)
					{
						CellStyle st2 = book.createCellStyle();

						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

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
					else if(status==5) {
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue("");
					}
					else if((dot+counter)==strVal.length())
					{

						if(cellInd==0) {
							CellStyle st2 = book.createCellStyle();
							if(rowInd%2==0) {
								XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
								((XSSFCellStyle) st2).setFillForegroundColor(color6);
								st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}
							else {
								XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
								((XSSFCellStyle) st2).setFillForegroundColor(color2);
								st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}

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
						else {

							try {
								Double ds = Double.valueOf(strVal);

								if((cellInd>=5)&&(cellInd<=colCount-1))
								{
									total+=ds;
								}
								//("total"+total);
								if(rowInd==3)
								{
									sum[cellInd] = total;

								}
								else
								{
									sum[cellInd] =sum[cellInd]+total;

								}
								total=0.0;

								cell.setCellType(Cell.CELL_TYPE_NUMERIC);

								DataFormat fmt = book.createDataFormat();
								CellStyle sty = book.createCellStyle();

								if(rowInd%2==0) {
									XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
									((XSSFCellStyle) sty).setFillForegroundColor(color6);
									sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								}
								else {
									XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
									((XSSFCellStyle) sty).setFillForegroundColor(color2);
									sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								}


								sty.setDataFormat(fmt.getFormat("#,##0.00"));
								sty.setBorderLeft(BorderStyle.THIN);
								sty.setBorderRight(BorderStyle.THIN);
								sty.setBorderTop(BorderStyle.THIN);
								sty.setBorderBottom(BorderStyle.THIN);



								sty.setBottomBorderColor(IndexedColors.RED.getIndex());
								sty.setTopBorderColor(IndexedColors.RED.getIndex());
								sty.setLeftBorderColor(IndexedColors.RED.getIndex());
								sty.setRightBorderColor(IndexedColors.RED.getIndex());
								cell.setCellStyle(sty);

								cell.setCellValue(ds);
							}

							catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
					else
					{
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

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
		}

		//    for(int rowInd = 3; rowInd < rowCount; rowInd++) {
		//        XSSFRow row = sheet.getRow(rowInd);
		//
		//        for(int cellInd = 4; cellInd <5 ; cellInd++) {
		//
		//             XSSFCell cell = row.getCell(cellInd);
		//
		//
		//
		//
		//
		//            // cell.getN
		//                String strVal = cell.getStringCellValue();
		//                CellStyle st2 = book.createCellStyle();
		//                     if(rowInd%2==0) {
		//                         XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
		//                           ((XSSFCellStyle) st2).setFillForegroundColor(color6);
		//                          st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//                           }
		//                           else {
		//                                XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
		//                                 ((XSSFCellStyle) st2).setFillForegroundColor(color2);
		//                              st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//                           }
		//                     st2.setBorderLeft(BorderStyle.THIN);
		//                     st2.setBorderRight(BorderStyle.THIN);
		//                     st2.setBorderTop(BorderStyle.THIN);
		//                     st2.setBorderBottom(BorderStyle.THIN);
		//
		//                     st2.setBottomBorderColor(IndexedColors.RED.getIndex());
		//                     st2.setTopBorderColor(IndexedColors.RED.getIndex());
		//                     st2.setLeftBorderColor(IndexedColors.RED.getIndex());
		//                     st2.setRightBorderColor(IndexedColors.RED.getIndex());
		//                     cell.setCellStyle(st2);
		//              cell.setCellValue(strVal);
		//        }
		//        }

		XSSFRow roo=sheet.createRow(rowCount);


		for(int h=0;h<sum.length;h++) {
			//(h+"-"+sum[h]);
		}



		for(int f=6;f<colCount;f++)
		{
			XSSFCell cell1=roo.createCell(f);

			cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
			DataFormat fmt = book.createDataFormat();
			CellStyle sty = book.createCellStyle();
			XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
			((XSSFCellStyle) sty).setFillForegroundColor(color7);
			sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			sty.setBorderLeft(BorderStyle.THIN);
			sty.setBorderRight(BorderStyle.THIN);
			sty.setBorderTop(BorderStyle.THIN);
			sty.setBorderBottom(BorderStyle.THIN);

			sty.setBottomBorderColor(IndexedColors.RED.getIndex());
			sty.setTopBorderColor(IndexedColors.RED.getIndex());
			sty.setLeftBorderColor(IndexedColors.RED.getIndex());
			sty.setRightBorderColor(IndexedColors.RED.getIndex());
			sty.setDataFormat(fmt.getFormat("#,##0.00"));
			cell1.setCellStyle(sty);
			cell1.setCellValue(sum[f]);
		}
		XSSFCell cell222=roo.createCell(5);
		CellStyle sty = book.createCellStyle();
		XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
		((XSSFCellStyle) sty).setFillForegroundColor(color7);
		sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sty.setBorderLeft(BorderStyle.THIN);
		sty.setBorderRight(BorderStyle.THIN);
		sty.setBorderTop(BorderStyle.THIN);
		sty.setBorderBottom(BorderStyle.THIN);

		sty.setBottomBorderColor(IndexedColors.RED.getIndex());
		sty.setTopBorderColor(IndexedColors.RED.getIndex());
		sty.setLeftBorderColor(IndexedColors.RED.getIndex());
		sty.setRightBorderColor(IndexedColors.RED.getIndex());

		cell222.setCellStyle(sty);
		cell222.setCellValue("Total");

	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public boolean isShowButton() {
		return showButton;
	}
	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}
	public double getFees() {
		return fees;
	}
	public void setFees(double fees) {
		this.fees = fees;
	}

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}


	
	
	
	
}
