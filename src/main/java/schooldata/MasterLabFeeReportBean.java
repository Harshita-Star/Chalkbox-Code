package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
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

@ManagedBean(name="masteLabFeeReportBean")
@ViewScoped
public class MasterLabFeeReportBean implements Serializable
{
	int totalStudent;
	ArrayList<StudentInfo> studentList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,selectedSection,selectedClass,branches;
	ArrayList<SelectItem> columnList,branchList,classList,sectionList;
	boolean show,showSchool,showClass;
	transient StreamedContent file;

	public MasterLabFeeReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(schid,selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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










		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=0;o<colCount;o++) {
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

			URL url = new URL("http://www.chalkboxerp.in/BLMSRS/hp.jpg");
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
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
	
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<=colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i);
		
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
			//            //(rowCount);
			//            //(colCount);
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





		double total=0.0;
		double[] sum = new double[colCount+1];
		double[] rowSum = new double[rowCount+5];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//            //(rowCount);
			//            //(colCount);
			rowSum[rowInd] =0.0;
			for(int cellInd = 5; cellInd <colCount; cellInd++) {

				//(rowInd);
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

						rowSum[rowInd] +=ds;

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
						if(strVal.equalsIgnoreCase("")) {
							cell.setCellValue(0);
						}
						else {
							cell.setCellValue(ds);
						}


					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			//("row"+rowInd+"-"+rowSum[rowInd]);
			CellStyle styleCou = book.createCellStyle();
			XSSFColor colorCou = new XSSFColor(new java.awt.Color(243,236,221));
			((XSSFCellStyle) styleCou).setFillForegroundColor(colorCou);
			XSSFCell cellCour = row.createCell(colCount);

			cellCour.setCellValue(rowSum[rowInd]);
			cellCour.setCellStyle(styleCou);
		}


		/*    for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
            XSSFRow row = sheet.getRow(rowInd);

            rowSum[rowInd] =0.0;
            for(int cellInd = colCount; cellInd <colCount+1; cellInd++) {


//                  cellCou.setCellType(Cell.CELL_TYPE_NUMERIC);
//
//                   DataFormat fmtCou = book.createDataFormat();
//                   CellStyle styCou = book.createCellStyle();
//
//                   if(rowInd%2==0) {
//                       XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
//                         ((XSSFCellStyle) styCou).setFillForegroundColor(color6);
//                        styCou.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                         }
//                         else {
//                              XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
//                               ((XSSFCellStyle) styCou).setFillForegroundColor(color2);
//                            styCou.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                         }
//
//
//                   styCou.setDataFormat(fmtCou.getFormat("#,##0.00"));
//                   styCou.setBorderLeft(BorderStyle.THIN);
//                   styCou.setBorderRight(BorderStyle.THIN);
//                   styCou.setBorderTop(BorderStyle.THIN);
//                   styCou.setBorderBottom(BorderStyle.THIN);
//
//
//
//                   styCou.setBottomBorderColor(IndexedColors.RED.getIndex());
//                   styCou.setTopBorderColor(IndexedColors.RED.getIndex());
//                   styCou.setLeftBorderColor(IndexedColors.RED.getIndex());
//                   styCou.setRightBorderColor(IndexedColors.RED.getIndex());
//                  cellCou.setCellStyle(styCou);

                      cellCou.setCellValue(String.valueOf(rowSum[rowInd]));
                    //  cellCou.set
            }
        }*/



		XSSFRow roo=sheet.createRow(rowCount+1);

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
		//  sty.setDataFormat(fmt.getFormat("#,##0.00"));
		cell222.setCellStyle(sty);
		cell222.setCellValue("Total");


	}


	public  void exportMasterLabPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo("251", con);


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





		//Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk("\nBLM Academy Sr. Secondary School                                 \n",fo );

			Chunk c3 = new Chunk(im, -400, 15);

			Chunk c1 = new Chunk("Padampur Devaliya, Gora Parao, Haldwani, Distt. Nainital                  \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);
			document.add(p1);


			Chunk c8 = new Chunk("\n                                                                  Lab Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);


			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(5+columnList.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno.");
			table.addCell("EBLM No");

			table.addCell("First Name");
			table.addCell("Class");
			table.addCell("School");
			for(int j=0;j<columnList.size();j++) {
				table.addCell(new Phrase(columnList.get(j).getLabel()));

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

			double arr[] = new double[40];
			for (int i=0;i<studentList.size();i++){
				//("fs");
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(),font));

				table.addCell(new Phrase(studentList.get(i).getFname(),font));
				table.addCell(new Phrase(studentList.get(i).getClassName(),font));

				table.addCell(new Phrase(studentList.get(i).getSchname(),font));
				for(int j=0;j<columnList.size();j++) {
					table.addCell(new Phrase(studentList.get(i).getFeesMap().get(columnList.get(j).getValue()),font));
					try {
						arr[j] += Double.valueOf(studentList.get(i).getFeesMap().get(columnList.get(j).getValue()));
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}





			}
			table.addCell(new Phrase("Total",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			for(int i=0;i<columnList.size();i++) {
				table.addCell(new Phrase(String.valueOf(arr[i]),font));
			}

			for (int i=1;i<=studentList.size()+1;i++){
				PdfPCell[] cell = table.getRow(i).getCells();
				for(int j=0;j<cell.length;j++)
				{

					cell[j].setBorderColor(BaseColor.RED);
				}
			}

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Lab_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Lab_Fee_Report_Report.pdf").stream(()->isFromFirstData).build();






	}

	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedClass="-1";
		sectionList = new ArrayList<>();
		show=false;

		if(schid.equals("-1"))
		{
			showClass = false;
			showSchool=true;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
		}
		else
		{
			showClass = true;
			showSchool=false;
			schoolInfo(schid);
			branches=schid;
			classList=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void searchReport()
	{
		createList();
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports objReport=new DataBaseMethodsReports();
		String session=DatabaseMethods1.selectedSessionDetails(branches,conn);

		//(new Date());
		studentList=objReport.blmLabFeeReport(selectedClass,selectedSection,branches,session,columnList,conn);
		//(new Date());
		if(studentList.size()>0)
		{
			show=true;
		}
		else
		{
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Records Found"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createList()
	{
		columnList=new ArrayList<>();
		SelectItem ll=new SelectItem();
		ll.setLabel("April");
		ll.setValue("labfee");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("May-June");
		ll.setValue("labmayjune");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("July-Aug");
		ll.setValue("labjulyaug");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Sep");
		ll.setValue("labsep");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Oct-Nov");
		ll.setValue("laboctnov");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Dec");
		ll.setValue("labdec");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Jan");
		ll.setValue("labjan");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Feb-Mar");
		ll.setValue("labfebmar");
		columnList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Total");
		ll.setValue("labTotal");
		columnList.add(ll);
	}


	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFinalAddress() {
		return finalAddress;
	}

	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}

	public String getAffiliationNo() {
		return affiliationNo;
	}

	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public ArrayList<SelectItem> getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList<SelectItem> columnList) {
		this.columnList = columnList;
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

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
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

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isShowSchool() {
		return showSchool;
	}

	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}


}
