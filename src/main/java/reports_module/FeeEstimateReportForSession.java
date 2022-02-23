package reports_module;

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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.SchoolInfoList;

@ManagedBean (name="feeEstimateSession")
@ViewScoped
public class FeeEstimateReportForSession implements Serializable
{
	String selectedSchool,sessionValue;
	ArrayList<FeeInfo> feeList=new ArrayList<>(),feeInfo;
	ArrayList<SelectItem> classList=new ArrayList<>(),installmentList;
	ArrayList<FeeEstimateInfo> feeEstimateList=new ArrayList<>(),feeEstimateList1=new ArrayList<>(),feeEstimateList2=new ArrayList<>();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj= new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();

	// General category For 251 ='121'     252 ='122'
	
	public FeeEstimateReportForSession()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolDetails =obj.fullSchoolInfo(conn);
		selectedSchool=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(selectedSchool, conn);

		classList=obj.allClass(selectedSchool,conn);
		feeInfo=obj.viewFeeListForDues(selectedSchool,conn);
		FeeInfo ff=new FeeInfo();
		ff.setFeeName("Total");
		ff.setFeeId("Total");
		feeInfo.add(ff);

		installment();

		String catId=obj.concessionCategoryIdByName("general", conn);
		feeEstimateList=dbr.feeEstimateReportForSession(catId,installmentList,classList,feeInfo,selectedSchool,conn);
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		
		ss1 = new SelectItem();
		ss1.setLabel("May"); 
		ss1.setValue("5");
		installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("June"); ss1.setValue("6");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("July"); ss1.setValue("7");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("August"); ss1.setValue("8");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("September"); ss1.setValue("9");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("October"); ss1.setValue("10");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("November"); ss1.setValue("11");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("December"); ss1.setValue("12");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("January"); ss1.setValue("1");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("February"); ss1.setValue("2");
		  installmentList.add(ss1);
		  
		  ss1 = new SelectItem(); ss1.setLabel("March"); ss1.setValue("3");
		  installmentList.add(ss1);
	}


	public void toNum(Object doc)
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
		XSSFRow r2 = sheet.createRow(0);
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
		clee.setCellValue("Fee Estimate");
		clee.setCellStyle(styleb);


		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=1;o<colCount;o++) {
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
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
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
				ce1.setCellValue("Month Name");
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




		sheet.getRow(5);
		// double total=0.0;
		//  double[] sum = new double[colCount+1];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 0; cellInd <colCount ; cellInd++) {


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
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
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
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
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
					try {
						Double ds = Double.valueOf(strVal);


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
						if(rowInd==rowCount) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
							((XSSFCellStyle) sty).setFillForegroundColor(color6);
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
						e.printStackTrace();
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
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
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






	public  void exportFeeEstimatePdf() throws DocumentException, IOException, FileNotFoundException {

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

			Chunk c3 = null;
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
			Chunk c8 = new Chunk("\n                                                             Fee Estimate Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(1+feeInfo.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Installment Name");
			for(int i=0;i<feeInfo.size();i++) {
				table.addCell(new Phrase(String.valueOf(feeInfo.get(i).getFeeName()),font));

			}

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<feeEstimateList.size();i++){

				table.addCell(new Phrase(feeEstimateList.get(i).getInstallmentName()));

				for(int j=0;j<feeInfo.size();j++) {
					table.addCell(new Phrase(feeEstimateList.get(i).getFeeMap().get(feeInfo.get(j).getFeeName()),font));

				}
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Fee_Estimate_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Fee_Estimate_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public String getSelectedSchool() {
		return selectedSchool;
	}

	public void setSelectedSchool(String selectedSchool) {
		this.selectedSchool = selectedSchool;
	}

	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}

	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}

	public ArrayList<FeeInfo> getFeeInfo() {
		return feeInfo;
	}

	public void setFeeInfo(ArrayList<FeeInfo> feeInfo) {
		this.feeInfo = feeInfo;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public ArrayList<FeeEstimateInfo> getFeeEstimateList() {
		return feeEstimateList;
	}

	public void setFeeEstimateList(ArrayList<FeeEstimateInfo> feeEstimateList) {
		this.feeEstimateList = feeEstimateList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
