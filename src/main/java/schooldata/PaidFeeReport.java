package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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

@ManagedBean(name="paidFeeReport")
@ViewScoped
public class PaidFeeReport implements Serializable
{
	ArrayList<SelectItem> classList,sectionList;
	String selectedSection,selectedClass;
	int total;
	ArrayList<StudentInfo> studentList;
	boolean b;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	 String studentType;
	 double totalFee,totalPaid,totalDiscount,totalLeft;
	 double fee,transportFee;
	 
	 String feeString,transportfeeString,totalfeeString,totalPaidString,totalDiscountString,totalLeftString;
	 ArrayList<SelectItem> concessionlist = new ArrayList<>();
	String selectedConcession;
	 
	public PaidFeeReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		classList=new DatabaseMethods1().allClass(conn);
		concessionlist = new DatabaseMethods1().allConnsessionType(conn);
		selectedClass="-1";
		selectedSection="-1";
		studentType="All";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void showPaidFeeList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentListByClassSectionForPaidRecord(selectedClass,selectedSection,studentType,selectedConcession,conn);

		if(studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b=false;
		}
		else
		{
		
			fee=0;
			transportFee=0;
			totalFee=0;
			totalPaid=0;
			totalDiscount=0;
			totalLeft=0;
			
			for(StudentInfo ls:studentList)
			{
				fee=fee+ls.getFee();
				transportFee=transportFee+ls.getTranportFee();
				totalFee=totalFee+ls.getTotalFee();
				totalPaid=totalPaid+ls.getPaidFee();
				totalDiscount=totalDiscount+Double.parseDouble(ls.getDiscountfee());
				totalLeft=totalLeft+ls.getLeftFee();
			}
			
			
			String s=
			
			feeString= BigDecimal.valueOf(fee).toPlainString();
			transportfeeString= BigDecimal.valueOf(transportFee).toPlainString();
			totalfeeString= BigDecimal.valueOf(totalFee).toPlainString();
			totalPaidString= BigDecimal.valueOf(totalPaid).toPlainString();
			totalDiscountString= BigDecimal.valueOf(totalDiscount).toPlainString();
			totalLeftString= BigDecimal.valueOf(totalLeft).toPlainString();
			
			
			
			b=true;
		}
		total=studentList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public  void exportPaidFeePdf() throws DocumentException, IOException, FileNotFoundException {

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
			Image im = null;
			try {
				im  =Image.getInstance(src);
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
			Chunk c8 = new Chunk("\n                                                                     Paid Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c7 = new Chunk("\n Total Students: "+total+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno.");
			table.addCell("Sr. No.");
			table.addCell("Student Name");
			//  table.addCell("Spouse Name");
			table.addCell("Father's Phone");
			table.addCell("Mother's Phone");
			table.addCell("Fees");
			table.addCell("Transport Fee");
			table.addCell("Total Fee");
			table.addCell("Total Paid");
			table.addCell("Total Discount");
			table.addCell("Total Left");



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
				//("fs");
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSrNo()),font));

				table.addCell(new Phrase(studentList.get(i).getFname()+"/"+studentList.get(i).getFathersName()));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSendMessageMobileNo())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getMothersPhone()),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFee())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getTranportFee())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getTotalFee()),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getPaidFee())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getDiscountFees())));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getLeftFee()),font));


			}


			table.setWidthPercentage(110);
			document.add(table);





		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Paid_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Paid_Fee_Report.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}


	public void toNum(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		XSSFRow header = sheet.getRow(3);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();

		try
		{

			URL url = new URL(schoolDetails.getDownloadpath()+schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(3);
			my_anchor.setCol1(0);
			my_anchor.setCol2(12);
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

		XSSFRow headerRow = sheet.getRow(3);

		// headerRow.setHeight((short)1200);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//  Font font = book.createFont();
		//       font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.NONE);

		style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<=11;i++) {
			XSSFCell ce1 = headerRow.getCell(i);
			if(ce1 != null) {
				ce1.setCellStyle(style);
			}


		}
		
		XSSFRow headerRow342 = sheet.getRow(4);

		// headerRow.setHeight((short)1200);
		CellStyle style342 = book.createCellStyle();
		XSSFColor color342 = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//  Font font = book.createFont();
		//       font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.NONE);

		style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<=11;i++) {
			XSSFCell ce1342 = headerRow342.getCell(i);
			if(ce1342 != null) {
				ce1342.setCellStyle(style);
			}


		}

//		XSSFRow headerRow2 = sheet.getRow(5);
//		CellStyle style22 = book.createCellStyle();
//		XSSFColor color11 = new XSSFColor(new java.awt.Color(243,236,221));
//		((XSSFCellStyle) style22).setFillForegroundColor(color11);
//		style22.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		//  Font font = book.createFont();
//		//       font.setColor(IndexedColors.BLACK.getIndex());
//
//		//   style.setFont(font);
//		// style.setVerticalAlignment(XSSFCellStyle.TOP);
//		style22.setBorderLeft(BorderStyle.THIN);
//		style22.setBorderRight(BorderStyle.THIN);
//		style22.setBorderTop(BorderStyle.NONE);
//		style22.setBorderBottom(BorderStyle.THIN);
//
//		style22.setBottomBorderColor(IndexedColors.RED.getIndex());
//		style22.setTopBorderColor(IndexedColors.WHITE.getIndex());
//		style22.setLeftBorderColor(IndexedColors.RED.getIndex());
//		style22.setRightBorderColor(IndexedColors.RED.getIndex());
//		for(int i=0;i<11;i++) {
//			XSSFCell ce111 = headerRow2.createCell(i);
//			System.out.println(ce111);
//			ce111.setCellStyle(style22);
//			//ce111.setCellValue("");
//
//
//		}

		for(int rowInd = 5; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <=11 ; cellInd++) {
				XSSFCell cell = row.getCell(cellInd);
				if(cell!=null) {
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
		}


		sheet.getRow(4);
		double total=0.0;
		double[] sum = new double[colCount+1];
		for(int rowInd = 5; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 5; cellInd <colCount ; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);
				int status=0,counter=0,dot=0;
				Character ch[] = new Character[3000];

				String strVal = cell.getStringCellValue();
				if(cellInd!=3){


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


					CellStyle sty = book.createCellStyle();
					DataFormat fmt = book.createDataFormat();
					sty.setDataFormat(fmt.getFormat("#,##0.00"));

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
					sty.setBorderLeft(BorderStyle.THIN);
					sty.setBorderRight(BorderStyle.THIN);
					sty.setBorderTop(BorderStyle.THIN);
					sty.setBorderBottom(BorderStyle.THIN);

					sty.setBottomBorderColor(IndexedColors.RED.getIndex());
					sty.setTopBorderColor(IndexedColors.RED.getIndex());
					sty.setLeftBorderColor(IndexedColors.RED.getIndex());
					sty.setRightBorderColor(IndexedColors.RED.getIndex());




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



						cell.setCellStyle(st2);
						cell.setCellValue(strVal);
					}
					else if((dot+counter)==strVal.length())
					{
						try {
							if(cellInd==4) {
								cell.setCellStyle(sty);
								cell.setCellValue(strVal);
							}
							else {
								Double ds = Double.valueOf(strVal);

								if((cellInd>=5)&&(cellInd<=colCount-1))
								{
									total+=ds;
								}
								//("total"+total);
								if(rowInd==5)
								{
									sum[cellInd] = total;

								}
								else
								{
									sum[cellInd] =sum[cellInd]+total;

								}
								total=0.0;

								cell.setCellType(Cell.CELL_TYPE_NUMERIC);



								cell.setCellStyle(sty);
								// cell.setCellStyle(sty);
								cell.setCellValue(ds);
							}
						}
						catch (Exception e) {
							// TODO: handle exception
						}

					}
					else
					{


						cell.setCellStyle(st2);
						cell.setCellValue(strVal);
					}
				}
			}
		}

		for(int rowInd = 5; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);

			for(int cellInd = 3; cellInd <4 ; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);





				// cell.getN
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



		XSSFRow roo=sheet.createRow(rowCount);

		//("sum series");
		for(int h=0;h<sum.length;h++) {
			//(h+"-"+sum[h]);
		}



		for(int f=5;f<colCount;f++)
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
		XSSFCell cell222=roo.createCell(4);
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
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public double getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}
	public double getTotalPaid() {
		return totalPaid;
	}
	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}
	public double getTotalDiscount() {
		return totalDiscount;
	}
	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}
	public double getTotalLeft() {
		return totalLeft;
	}
	public void setTotalLeft(double totalLeft) {
		this.totalLeft = totalLeft;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public double getTransportFee() {
		return transportFee;
	}
	public void setTransportFee(double transportFee) {
		this.transportFee = transportFee;
	}
	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}
	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}
	public String getFeeString() {
		return feeString;
	}
	public void setFeeString(String feeString) {
		this.feeString = feeString;
	}
	public String getTransportfeeString() {
		return transportfeeString;
	}
	public void setTransportfeeString(String transportfeeString) {
		this.transportfeeString = transportfeeString;
	}
	public String getTotalfeeString() {
		return totalfeeString;
	}
	public void setTotalfeeString(String totalfeeString) {
		this.totalfeeString = totalfeeString;
	}
	public String getTotalPaidString() {
		return totalPaidString;
	}
	public void setTotalPaidString(String totalPaidString) {
		this.totalPaidString = totalPaidString;
	}
	public String getTotalDiscountString() {
		return totalDiscountString;
	}
	public void setTotalDiscountString(String totalDiscountString) {
		this.totalDiscountString = totalDiscountString;
	}
	public String getTotalLeftString() {
		return totalLeftString;
	}
	public void setTotalLeftString(String totalLeftString) {
		this.totalLeftString = totalLeftString;
	}
	public ArrayList<SelectItem> getConcessionlist() {
		return concessionlist;
	}
	public void setConcessionlist(ArrayList<SelectItem> concessionlist) {
		this.concessionlist = concessionlist;
	}
	public String getSelectedConcession() {
		return selectedConcession;
	}
	public void setSelectedConcession(String selectedConcession) {
		this.selectedConcession = selectedConcession;
	}
	


}
