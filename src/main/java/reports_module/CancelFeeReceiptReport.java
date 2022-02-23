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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

import schooldata.DailyFeeCollectionBean;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.Feecollectionc;
import schooldata.SchoolInfoList;

@ManagedBean(name="cancelFee")
@ViewScoped
public class CancelFeeReceiptReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	String date;
	boolean show,showBranch;
	String usertype,feetype,studentclass,studentname,fathername,selectedClass,selectedSection,schoolId;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	int cashAmount,chequeAmount;
	ArrayList<FeeInfo>feelist;
	static int count=1;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<SelectItem> branchList = new ArrayList<>();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 dbm = new DatabaseMethods1();
    String sessionValue;

	public CancelFeeReceiptReport()
	{
		/*HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 schoolId=(String) ss.getAttribute("schoolid");
		 usertype=(String) ss.getAttribute("type");

		 if(!usertype.equalsIgnoreCase("madmin"))
		 {

		 }*/
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		schoolDetails =dbm.fullSchoolInfo(conn);
		search();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		

		feelist=dbm.viewFeeList1(schoolId,conn);

		count=1;cashAmount=0;chequeAmount=0;
		dailyfee=new ArrayList<>();

		HashMap<String, String> tempMap=new HashMap<>();
		getdailyfeecollection=dbm.cancelReceiptReport(tempMap,conn);
		ArrayList<String> temp=new ArrayList<>();
		for(Feecollectionc mm:getdailyfeecollection)
		{
			temp.add(mm.getRecipietNo());
		}
		String a[]=new String[temp.size()];
		for(int i=0;i<temp.size();i++)
		{
			a[i]=temp.get(i);
		}
		a=removeDuplicates(a);

		String value="";
		ArrayList<String>tempdeatils;
		for(int i=0;i<a.length;i++)
		{
			tempdeatils=new ArrayList<>();
			HashMap<String, String> feecllection=new HashMap<>();
			DailyFeeCollectionBean ll=new DailyFeeCollectionBean();
			int totalamuont=0;

			for(Feecollectionc list : getdailyfeecollection)
			{
				if(list.getRecipietNo().equals(String.valueOf(a[i])))
				{
					ll.setStudentname(list.getStudentname());
					ll.setClassname(list.getClassName());
					ll.setFathername(list.getFathername());
					ll.setStudentid(list.getStudentid());
					ll.setMothername(list.getMotherName());
					ll.setSection(list.getSectionName());
					ll.setFeedate(list.getFeedate());
					ll.setReciptno(list.getRecipietNo());
					ll.setFeeDateStr(sdf.format(list.getFeedate()));
					String feetype=list.getFeeName();
					feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
					ll.setAllFess(feecllection);
					ll.setBankname(list.getBankname());
					ll.setRemark(list.getRemark());
					ll.setChequenumber(list.getChequeno());
					ll.setPaymentmode(list.getPaymentmode());
					ll.setClassname(list.getClassName());
					ll.setSection(list.getSectionName());
					if(tempdeatils.size()==0)
					{

						value="orignal";
					}
					else
					{
						for(String ls:tempdeatils)
						{
							if(ls.equals(feetype))
							{
								value="duplicate";
								break;
							}
							else
							{
								value="orignal";
							}
						}

					}

					if(value.equals("orignal"))
					{
						tempdeatils.add(feetype);
						totalamuont+=list.getFeeamunt();
					}

					ll.setAmount(String.valueOf(totalamuont));
					ll.setPaymentmode(list.getPaymentmode());
					ll.setSrno(count);

				}



			}
			dailyfee.add(ll);
			count++;
			show=true;
		}
		if(dailyfee.size()>0)
		{
			for(DailyFeeCollectionBean info:dailyfee)
			{
				if(info.getPaymentmode().equalsIgnoreCase("Cash"))
				{
					cashAmount+=Integer.parseInt(info.getAmount());
				}
				else
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}
			}
		}


		// date=new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(feedate.getTime()))+"-"+new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(endDate.getTime()));
		show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
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
		XSSFRow r2 =     sheet.createRow(0);
		XSSFRow r1 = sheet.createRow(1);
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

		clee.setCellValue("Total Amount(by Cash):-");

		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue(String.valueOf(cashAmount));
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total Amount(by Cheque):-");
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue(String.valueOf(chequeAmount));
		clee6.setCellStyle(styleb);

		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=4;o<colCount;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}
		//
		//

		XSSFRow he = sheet.getRow(0);
		he.setHeight((short)1550);
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
			my_anchor.setCol1(0);
			my_anchor.setCol2(9);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();

		} catch (IOException ioex) {

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
//			              if(i==0) {
//			                  ce1.setCellValue("Sno");
//			              }
//			              if(i==4) {
//			                  ce1.setCellValue("Concession Assign");
//			              }
//			              if(i==5) {
//			                  ce1.setCellValue("Student Type");
//			              }
//			              if(i==6) {
//			                  ce1.setCellValue("Total Amount");
//			              }
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
			System.out.println(rowCount);
			System.out.println(colCount);
			for(int cellInd = 0; cellInd <5 ; cellInd++) {


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


		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//            //// // System.out.println(rowCount);
			//            //// // System.out.println(colCount);
			for(int cellInd = colCount-4; cellInd <colCount ; cellInd++) {


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


		double total=0.0;
		double[] sum = new double[colCount+1];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//            //// // System.out.println(rowCount);
			//            //// // System.out.println(colCount);
			for(int cellInd = 5; cellInd <colCount-5; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);

				String strVal = cell.getStringCellValue();
				if(strVal.equalsIgnoreCase("")) {
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
				else {
					try {
						Double ds = Double.valueOf(strVal);

						if((cellInd>=5)&&(cellInd<=colCount-1))
						{
							total+=ds;
						}

						if(rowInd==2)
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
						if(strVal.equalsIgnoreCase("")) {
							cell.setCellValue(0);
						}
						else {
							cell.setCellValue(ds);
						}


					}
					catch (Exception e) {
					   e.printStackTrace();
					}
				}
			}

		}

		XSSFRow roo=sheet.createRow(rowCount+1);




		for(int f=5;f<colCount-5;f++)
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
		//  sty.setDataFormat(fmt.getFormat("#,##0.00"));
		cell222.setCellStyle(sty);
		cell222.setCellValue("Total");


	}





	public  void exportCancelFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbm.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font foH = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		new Font(FontFamily.HELVETICA, 7, Font.BOLD);

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
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",foH);
			Chunk c3 =null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",foH);

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
			Chunk c8 = new Chunk("\n                                                                 Cancel Fee Report \n\n",foH );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c7 = new Chunk("\n Total Amount(By Cash) : "+cashAmount+"                                       Total Amount(By Cheque): "+chequeAmount +"\n\n",foH );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(10+feelist.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("Sno.",font));
			table.addCell(new Phrase("Student Name",font));

			table.addCell(new Phrase("Father Name",font));
			table.addCell(new Phrase("Class",font));
			table.addCell(new Phrase("Section",font));
			for(int j=0;j<feelist.size();j++) {
				table.addCell(new Phrase(feelist.get(j).getFeeName()));

			}

			table.addCell(new Phrase("Receipt No.",font));
			table.addCell(new Phrase("Amount",font));
			table.addCell(new Phrase("Payment Mode",font));
			table.addCell(new Phrase("Remark",font));
			table.addCell(new Phrase("Date",font));



			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<dailyfee.size();i++){
				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getSrno()),font));
				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getStudentname()),font));

				table.addCell(new Phrase(dailyfee.get(i).getFathername(),font));
				table.addCell(new Phrase(dailyfee.get(i).getClassname(),font));

				table.addCell(new Phrase(dailyfee.get(i).getSection(),font));
				for(int j=0;j<feelist.size();j++) {
					table.addCell(new Phrase(dailyfee.get(i).getAllFess().get(feelist.get(j).getFeeName()),font));

				}

				table.addCell(new Phrase(dailyfee.get(i).getReciptno() ,font));

				table.addCell(new Phrase(dailyfee.get(i).getAmount(),font));
				table.addCell(new Phrase(dailyfee.get(i).getPaymentmode(),font));

				table.addCell(new Phrase(dailyfee.get(i).getRemark(),font));
				table.addCell(new Phrase(dailyfee.get(i).getFeeDateStr() ,font));



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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Cancel_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Cancel_Fee_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}
	public void setGetdailyfeecollection(
			ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailycollection() {
		return dailycollection;
	}
	public void setDailycollection(ArrayList<DailyFeeCollectionBean> dailycollection) {
		this.dailycollection = dailycollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}
	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}
	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}
	public int getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(int cashAmount) {
		this.cashAmount = cashAmount;
	}
	public int getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(int chequeAmount) {
		this.chequeAmount = chequeAmount;
	}
	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
	}
	public String getDate() {
		return date;
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
	public void setDate(String date) {
		this.date = date;
	}
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}


	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public boolean isShowBranch() {
		return showBranch;
	}

	public void setShowBranch(boolean showBranch) {
		this.showBranch = showBranch;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
