package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean(name="MasterMainNewDueFeeReportBean")
@ViewScoped
public class MasterMainNewDueFeeReportBean implements Serializable
{

	String name,firstMonthTutionFee,lastMonthTutionFee,selectedCLassSection,selectedSection,dateString;
	int totalStudent;
	ArrayList<SelectItem> classSection,sectionList,sessionList,branchList;
	ArrayList<StudentInfo> studentList,selectedStudentList;
	String regFee,tutionFee,admissionFee,examFee,annualFee,transFee,artFee,activityFee,termFee,totalFee,tutionFeePeriod,studentName,fathersName;
	StudentInfo selectedStudent;
	static int count=1;
	String selectedSession,selectedSession2,month,sectionName,className,typeMessage;
	Date date;
	String selectedMonth;
	boolean show;
	int totalAmount;
	ArrayList<SelectItem> installmentList,selectedInstallList;
	ArrayList<ClassInfo> classFeeList;
	ArrayList<FeeStatus> feeStatus;
	ArrayList<FeeInfo> feelist = new ArrayList<>();
	String message="",totalamountString;
	double totalDueAmount;
	ArrayList<StudentInfo>list;
	String[] checkMonthSelected;
	DatabaseMethods1 obj=new DatabaseMethods1();
    boolean showClass,showSchool;
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches;
	String newschid;
	String lateFeeCheck;
	transient StreamedContent file;
    public  MasterMainNewDueFeeReportBean()
	{
		selectedCLassSection=selectedSection="-1";
		//Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		lateFeeCheck="yes";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
					newschid=String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
					newschid=newschid+","+String.valueOf(in.getValue());
				}
			}
		}
		checkMonthSelected=new String[0];
		installment();
		/*try
		{
			sessionList=obj.sessionDetail();
			classSection=obj.allClass(conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
	}


	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("1");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("3");
		installmentList.add(ss8);
	}

	public String monthName(int i)
	{
		String month="";
		if(i==4)
		{
			month="April";
		}
		else if(i==6)
		{
			month="May-June";
		}
		else if(i==8)
		{
			month="Jul-Aug";
		}
		else if(i==9)
		{
			month="September";
		}
		else if(i==11)
		{
			month="Oct-Nov";
		}
		else if(i==12)
		{
			month="December";
		}
		else if(i==1)
		{
			month="January";
		}
		else if(i==3)
		{
			month="Feb-Mar";
		}
		return month;
	}


	public void toNum(Object doc)
	{ XSSFWorkbook book = (XSSFWorkbook)doc;
	XSSFSheet sheet = book.getSheetAt(0);

	XSSFRow header = sheet.getRow(4);
	int colCount = header.getPhysicalNumberOfCells();

	int rowCount = sheet.getPhysicalNumberOfRows();



	XSSFCellStyle intStyle = book.createCellStyle();
	intStyle.setDataFormat((short)1);

	XSSFCellStyle decStyle = book.createCellStyle();
	decStyle.setDataFormat((short)2);

	XSSFCellStyle dollarStyle = book.createCellStyle();
	dollarStyle.setDataFormat((short)5);




	sheet.getRow(4);
	double total=0.0;
	for(int rowInd = 4; rowInd < rowCount; rowInd++) {
		XSSFRow row = sheet.getRow(rowInd);
		//(rowCount);
		//(colCount);
		for(int cellInd = 6; cellInd <colCount ; cellInd++) {


			XSSFCell cell = row.getCell(cellInd);




			int status=0,counter=0,dot=0;
			Character ch[] = new Character[3000];

			String strVal = cell.getStringCellValue();

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
				cell.setCellValue(strVal);
			}
			else if((dot+counter)==strVal.length())
			{
				try {
					Double ds = Double.valueOf(strVal);

					if(cellInd==colCount-1)
					{
						total+=ds;
					}

					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					DataFormat fmt = book.createDataFormat();
					CellStyle sty = book.createCellStyle();
					sty.setDataFormat(fmt.getFormat("#,##0.00"));
					cell.setCellStyle(sty);
					cell.setCellValue(ds);
				}
				catch (Exception e) {
					// TODO: handle exception
				}

			}
			else
			{
				cell.setCellValue(strVal);
			}

		}
	}

	XSSFRow roo=sheet.createRow(rowCount);
	XSSFCell cell1=roo.createCell(colCount-1);
	XSSFCell cell2=roo.createCell(colCount-2);
	cell2.setCellValue("Total");
	cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
	DataFormat fmt = book.createDataFormat();
	CellStyle sty = book.createCellStyle();
	sty.setDataFormat(fmt.getFormat("#,##0.00"));
	cell1.setCellStyle(sty);
	cell1.setCellValue(total);


	}

	public void toNumHead(Object doc)
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
		//  XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue(" Due  Fees ");
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Collection Report :");
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue(" ");
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue("");
		clee6.setCellStyle(styleb);

		for(int o=4;o<100;o++) {
			XSSFCell clee7 = r1.createCell(o);
			clee7.setCellValue("");
			clee7.setCellStyle(styleb);
		}



		XSSFRow he = sheet.getRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



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
			//("fgg");
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
			//	            //(rowCount);
			//	            //(colCount);
			for(int cellInd = 0; cellInd <6 ; cellInd++) {


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

				if(cellInd!=3) {
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

		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
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


		XSSFRow roo=sheet.createRow(rowCount+1);

		//("sum series");
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


	}


	public  void exportDueFeeHeadPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo("251", con);


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//    String home = System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();





		//Header
		try {

			//   Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk("\nBLM Academy Sr. Secondary School                                 \n" );

			Chunk c3 = new Chunk(im, -400, 15);

			Chunk c1 = new Chunk("Padampur Devaliya, Gora Parao, Haldwani, Distt. Nainital                  \n\n");

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);
			document.add(p1);


			Chunk c8 = new Chunk("\n                                                        Due Fee Collection HeadWise \n\n");
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);


			Chunk c9 = new Chunk("\nTotal Amount:- "+totalamountString+"\n\n\n");
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			p9.setAlignment(Element.ALIGN_CENTER);




			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(6+selectedInstallList.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("SR.No.",font));
			table.addCell(new Phrase("Name",font));

			table.addCell(new Phrase("Father Name",font));
			table.addCell(new Phrase("Contact No",font));
			table.addCell(new Phrase("Class",font));

			for(int i=0;i<selectedInstallList.size();i++) {
				table.addCell(new Phrase(String.valueOf(selectedInstallList.get(i).getValue()),font));
			}

			table.addCell(new Phrase("Total Left Amount",font));






			table.setHeaderRows(1);

			new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			//  double totAmt =0.0;
			double arr[] = new double[40];

			for (int i=0;i<list.size();i++){

				table.addCell(new Phrase(list.get(i).getSrNo(),font));
				table.addCell(new Phrase(list.get(i).getFname(),font));

				table.addCell(new Phrase(list.get(i).getFathersName(),font));
				table.addCell(new Phrase(list.get(i).getContactNo(),font));
				table.addCell(new Phrase(list.get(i).getClassName()+"-"+list.get(i).getSectionName(),font));

				for(int j=0;j<selectedInstallList.size();j++) {
					table.addCell(new Phrase(list.get(i).getFeesMap().get(selectedInstallList.get(j).getValue()),font));

					try {
						arr[j] += Double.valueOf(list.get(i).getFeesMap().get(selectedInstallList.get(j).getValue()));
					}
					catch (Exception e) {
						// TODO: handle exception
					}

				}

				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalFee()),font));



			}


			table.addCell(new Phrase("TOTAL",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));

			for(int i=0;i<selectedInstallList.size();i++) {
				table.addCell(new Phrase(String.valueOf(arr[i]),font));
			}

			table.addCell(new Phrase(totalamountString,font));


			for (int i=1;i<=list.size()+1;i++){
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Due_Fee_Head_Wise_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Due_Fee_Head_Wise_Report.pdf").stream(()->isFromFirstData).build();






	}


	public void toNumDue(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		XSSFRow header = sheet.getRow(5);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();

		try
		{
			URL url = new URL("http://www.chalkboxerp.in/BLMSRS/hp.jpg");
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(3);
			my_anchor.setCol1(0);
			my_anchor.setCol2(6);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);






		} catch (IOException ioex) {
			//("fgg");
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
		for(int i=0;i<7;i++) {
			XSSFCell ce1 = headerRow.getCell(i);

			ce1.setCellStyle(style);


		}

		XSSFRow headerRow2 = sheet.getRow(4);
		CellStyle style22 = book.createCellStyle();
		XSSFColor color11 = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style22).setFillForegroundColor(color11);
		style22.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//  Font font = book.createFont();
		//       font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		// style.setVerticalAlignment(XSSFCellStyle.TOP);
		style22.setBorderLeft(BorderStyle.THIN);
		style22.setBorderRight(BorderStyle.THIN);
		style22.setBorderTop(BorderStyle.NONE);
		style22.setBorderBottom(BorderStyle.THIN);

		style22.setBottomBorderColor(IndexedColors.RED.getIndex());
		style22.setTopBorderColor(IndexedColors.WHITE.getIndex());
		style22.setLeftBorderColor(IndexedColors.RED.getIndex());
		style22.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<6;i++) {
			XSSFCell ce111 = headerRow2.createCell(i);

			ce111.setCellStyle(style22);
			ce111.setCellValue("");


		}


		sheet.getRow(4);
		double total=0.0;
		for(int rowInd = 5; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 5; cellInd <colCount ; cellInd++) {

				if(cellInd!=3) {
					XSSFCell cell = row.getCell(cellInd);

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


					int status=0,counter=0,dot=0;
					Character ch[] = new Character[3000];

					String strVal = cell.getStringCellValue();

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
							Double ds = Double.valueOf(strVal);

							if(cellInd==colCount-1)
							{
								total+=ds;
							}

							cell.setCellType(Cell.CELL_TYPE_NUMERIC);



							cell.setCellStyle(sty);
							// cell.setCellStyle(sty);
							cell.setCellValue(ds);
						}
						catch (Exception e) {
							e.printStackTrace();
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


		//        for(int rowInd = 5; rowInd < rowCount; rowInd++) {
		//            XSSFRow row = sheet.getRow(rowInd);
		//
		//            for(int cellInd = 3; cellInd <4 ; cellInd++) {
		//
		//                 XSSFCell cell = row.getCell(cellInd);
		//
		//
		//
		//
		//
		//                // cell.getN
		//                    String strVal = cell.getStringCellValue();
		//                    CellStyle st2 = book.createCellStyle();
		//                         if(rowInd%2==0) {
		//                             XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
		//                               ((XSSFCellStyle) st2).setFillForegroundColor(color6);
		//                              st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//                               }
		//                               else {
		//                                    XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
		//                                     ((XSSFCellStyle) st2).setFillForegroundColor(color2);
		//                                  st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//                               }
		//                         st2.setBorderLeft(BorderStyle.THIN);
		//                         st2.setBorderRight(BorderStyle.THIN);
		//                         st2.setBorderTop(BorderStyle.THIN);
		//                         st2.setBorderBottom(BorderStyle.THIN);
		//
		//                         st2.setBottomBorderColor(IndexedColors.RED.getIndex());
		//                         st2.setTopBorderColor(IndexedColors.RED.getIndex());
		//                         st2.setLeftBorderColor(IndexedColors.RED.getIndex());
		//                         st2.setRightBorderColor(IndexedColors.RED.getIndex());
		//                         cell.setCellStyle(st2);
		//                  cell.setCellValue(strVal);
		//            }
		//            }



		XSSFRow roo=sheet.createRow(rowCount);
		XSSFCell cell1=roo.createCell(colCount-1);
		XSSFCell cell2=roo.createCell(colCount-2);
		CellStyle sty = book.createCellStyle();
		XSSFColor color8 = new XSSFColor(new java.awt.Color(246,233,217));
		((XSSFCellStyle) sty).setFillForegroundColor(color8);
		sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sty.setBorderLeft(BorderStyle.THIN);
		sty.setBorderRight(BorderStyle.THIN);
		sty.setBorderTop(BorderStyle.THIN);
		sty.setBorderBottom(BorderStyle.THIN);

		sty.setBottomBorderColor(IndexedColors.RED.getIndex());
		sty.setTopBorderColor(IndexedColors.RED.getIndex());
		sty.setLeftBorderColor(IndexedColors.RED.getIndex());
		sty.setRightBorderColor(IndexedColors.RED.getIndex());
		cell2.setCellStyle(sty);
		cell2.setCellValue("Total");
		cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
		DataFormat fmt = book.createDataFormat();

		sty.setDataFormat(fmt.getFormat("#,##0.00"));
		cell1.setCellStyle(sty);

		cell1.setCellValue(total);

	}



	public  void exportDueFeeColPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo("251", con);


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//    String home = System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();





		//Header
		try {

			//   Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk("\nBLM Academy Sr. Secondary School                                 \n" );

			Chunk c3 = new Chunk(im, -400, 15);

			Chunk c1 = new Chunk("Padampur Devaliya, Gora Parao, Haldwani, Distt. Nainital                  \n\n");

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);
			document.add(p1);


			Chunk c8 = new Chunk("\n                                                        Due Fee Collection \n\n");
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);


			Chunk c9 = new Chunk("\nTotal Amount:- "+totalamountString+"\n\n\n");
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			p9.setAlignment(Element.ALIGN_CENTER);




			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN,10);
			PdfPTable table = new PdfPTable(7);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(new Phrase("SNo.",font));
			table.addCell(new Phrase("SR.No.",font));

			table.addCell(new Phrase("Student Name",font));
			table.addCell(new Phrase("Father's Name",font));
			table.addCell(new Phrase("Contact No.",font));
			table.addCell(new Phrase("Class",font));


			table.addCell(new Phrase("Total Left Amount",font));






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			//  double totAmt =0.0;


			for (int i=0;i<list.size();i++){
				table.addCell(new Phrase(list.get(i).getsNo(),font));
				table.addCell(new Phrase(list.get(i).getSrNo(),font));
				table.addCell(new Phrase(list.get(i).getFname(),font));
				table.addCell(new Phrase(list.get(i).getFathersName(),font));
				table.addCell(new Phrase(list.get(i).getContactNo(),font));
				table.addCell(new Phrase(list.get(i).getClassName()+"-"+list.get(i).getSectionName(),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getTotalFee()),font));


			}


			table.addCell(new Phrase("TOTAL",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase(totalamountString,font));




			for (int i=1;i<=list.size()+1;i++){
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Due_Fee_Collection_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Due_Fee_Collection_Report.pdf").stream(()->isFromFirstData).build();






	}



	//	public void toNum1(Object doc)
	//	{
	//		XSSFWorkbook book = (XSSFWorkbook)doc;
	//    XSSFSheet sheet = book.getSheetAt(0);
	//
	//    XSSFRow header = sheet.getRow(2);
	//    int colCount = header.getPhysicalNumberOfCells();
	//
	//    int rowCount = sheet.getPhysicalNumberOfRows();
	//
	//
	//
	//XSSFCellStyle intStyle = book.createCellStyle();
	//intStyle.setDataFormat((short)1);
	//
	//XSSFCellStyle decStyle = book.createCellStyle();
	//decStyle.setDataFormat((short)2);
	//
	//XSSFCellStyle dollarStyle = book.createCellStyle();
	//dollarStyle.setDataFormat((short)5);
	//
	//
	//
	//
	//XSSFRow ro =sheet.getRow(2);
	//double total=0.0;
	//for(int rowInd = 2; rowInd < rowCount; rowInd++) {
	//    XSSFRow row = sheet.getRow(rowInd);
	//    //(rowCount);
	//    //(colCount);
	//    for(int cellInd = 6; cellInd <colCount ; cellInd++) {
	//
	//
	//        XSSFCell cell = row.getCell(cellInd);
	//
	//
	//
	//
	//        int status=0,counter=0,dot=0;
	//        Character ch[] = new Character[3000];
	//
	//        String strVal = cell.getStringCellValue();
	//
	//        for(int i=0;i<strVal.length();i++)
	//        {
	//          ch[i] = strVal.charAt(i);
	//          String s =Character.toString(ch[i]);
	//
	//
	//          if(Character.isDigit(ch[i]))
	//          {
	//          counter++;
	//          }
	//          if(s.equals("."))
	//          {
	//          dot++;
	//          }
	//          if(s.equals(""))
	//          {
	//          status=1;
	//          }
	//        }
	//
	//
	//        if(status==1)
	//        {
	//        cell.setCellValue(strVal);
	//        }
	//        else if((dot+counter)==strVal.length())
	//       {
	//        try {
	//       Double ds = Double.valueOf(strVal);
	//
	//       if(cellInd==colCount-1)
	//       {
	//    	  total+=ds;
	//       }
	//
	//       cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	//
	//       DataFormat fmt = book.createDataFormat();
	//       CellStyle sty = book.createCellStyle();
	//       sty.setDataFormat(fmt.getFormat("#,##0.00"));
	//      cell.setCellStyle(sty);
	//         cell.setCellValue(ds);
	//        }
	//        catch (Exception e) {
	//// TODO: handle exception
	//}
	//
	//       }
	//        else
	//        {
	//          cell.setCellValue(strVal);
	//        }
	//
	//    }
	//}
	//
	//XSSFRow roo=sheet.createRow(rowCount);
	//XSSFCell cell1=roo.createCell(colCount-1);
	//XSSFCell cell2=roo.createCell(colCount-2);
	//cell2.setCellValue("Total");
	//cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
	//DataFormat fmt = book.createDataFormat();
	//CellStyle sty = book.createCellStyle();
	//sty.setDataFormat(fmt.getFormat("#,##0.00"));
	//cell1.setCellStyle(sty);
	//cell1.setCellValue(total);
	//
	//
	//	}



	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedCLassSection="-1";
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

			classSection=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessageToSelectedStudentsCustome() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{
			String cnt="",addNumber="";
			for(StudentInfo list : selectedStudentList)
			{
				if(!list.getSendMessageMobileNo().equals("0")  && !list.getSendMessageMobileNo().equals("") && String.valueOf(list.getSendMessageMobileNo()).length()==10)
				{
					if(cnt.equals(""))
					{
						cnt=list.getSendMessageMobileNo();

					}
					else
					{
						cnt=cnt+","+list.getSendMessageMobileNo();

					}

					if (addNumber.equals("")) {
						addNumber = list.getAddNumber();

					} else {
						addNumber = addNumber + "," + list.getAddNumber();
					}

				}
				//Date date=new Date();
				//DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), message, list.getFathersPhone(), date,list.getClassId(),"Student");
			}

			obj.messageurl1(cnt, message, addNumber,conn,obj.schoolId(),"");
			message="";

			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		sectionList = new ArrayList<>();
		selectedSection="-1";
		if(!selectedCLassSection.equals("-1"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			sectionList=obj.allSection(schid,selectedCLassSection,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void searchStudent()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			
			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);

			String schoolid = DBM.schoolIdByStudentId(id,conn);
			String session=DatabaseMethods1.selectedSessionDetails(schoolid,conn);

			//SchoolInfoList list1=DBM.fullSchoolInfo(schoolid,conn);
			schoolInfo(schoolid);

			studentList=DBM.searchStudentListForDueFeeReport(schoolid,id,date,session,conn,"dueReport");

			if(studentList.size()>0)
			{
				for(StudentInfo ll:studentList)
				{

					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}
				selectedCLassSection=studentList.get(0).getClassId();
				selectedSection=studentList.get(0).getSectionid();
				feelist=DBM.classFeesForStudent(schoolid,selectedCLassSection,session,studentList.get(0).getStatus(),studentList.get(0).getConcession(),conn);
				feelist=DBM.addPreviousFee(schoolid,feelist,studentList.get(0).getAddNumber(),conn);

				className=DBM.classNameFromidSchid(schoolid,selectedCLassSection,session,conn);
				sectionName=DBM.sectionNameByIdSchid(schoolid,selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).month;
				show=true;
				String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
				stdate.split("/");
				Date extraDate=date;
				extraDate.setDate(15);
				new SimpleDateFormat("dd-MM-yy").format(extraDate);

				//message="Dear Parent, \n\nThis is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+". Kindly ignore if already paid. \n\nRegards, \n"+list1.getSchoolName()  ;
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String printFeeDetails()
	{
		studentName=selectedStudent.getFname();
		fathersName=selectedStudent.getFathersName();
		className=selectedStudent.getClassName();
		sectionName=selectedStudent.getSectionName();

		/*regFee=selectedStudent.getAdmissionFeeDueAmount();
		tutionFee=selectedStudent.getSchoolFeeDueAmount();
		annualFee=selectedStudent.getAnnualFeeDueAmount();
		transFee=selectedStudent.getTransportFeeDueAmount();
		examFee=selectedStudent.getExaminationFeeDueAmount();
		artFee=selectedStudent.getArtFeeDueAmount();
		termFee=selectedStudent.getTermFeeDueAmount();
		activityFee=selectedStudent.getActivityFeeDueAmount();
		totalFee=selectedStudent.getTutionFeeDueAmount();
		 */
		dateString=new SimpleDateFormat("dd-MM-yyyy").format(date);

		tutionFeePeriod=selectedStudent.getPeriod1();

		return "printDueFeeReport";
	}

	public void sendMessageToSelectedStudents() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{

			SchoolInfoList list1=obj.fullSchoolInfo(conn);
			String message="";
			String msg="";
			for(StudentInfo list : selectedStudentList)
			{

				if(list.getSendMessageMobileNo().length()==10)
				{
					message= "Dear "+list.getFname() + ", \n\nYour fee is due for month "+month+" is as - "+list.getTutionFeeDueAmount()+ ". \n\nRegards, \n"+list1.getSchoolName();

					msg=message;
					obj.messageurl1(list.getSendMessageMobileNo(), msg,list.getAddNumber(),conn,obj.schoolId(),"");

				}
			}
			//Date date=new Date();
			//	DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), msg, list.getFathersPhone(), date,list.getClassId(),"Student");
			message="";


			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showReport()
	{
		if(schid.equals("-1"))
		{
			searchStudentByClassSection(newschid);
		}
		else
		{
			searchStudentByClassSection(schid);
		}
	}
	
	
	public void showReportTransport()
	{
		if(schid.equals("-1"))
		{
			searchStudentByClassSectionTransport(newschid);
		}
		else
		{
			searchStudentByClassSectionTransport(schid);
		}
	}

	public void searchStudentByClassSection(String branches)
	{
		
		totalDueAmount=0;
		studentList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			//SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			//	String session=DatabaseMethods1.selectedSessionDetails();

			////("started : "+new Date());

			String sectionid="";
			if(selectedCLassSection.equals("-1"))
			{
				sectionid="-1";
			}
			else if(selectedSection.equals("-1"))
			{
				for(SelectItem ii : sectionList)
				{
					if(sectionid.equals(""))
					{
						sectionid = String.valueOf(ii.getValue());
					}
					else
					{
						sectionid = sectionid + "','" + String.valueOf(ii.getValue());
					}
				}
				sectionid="('"+sectionid+"')";
			}
			else
			{
				sectionid = "('"+selectedSection+"')";
			}
			
		

			list=DBM.dueFeesForblmNew(branches,date, sectionid,checkMonthSelected,lateFeeCheck,conn);
			
			double totalAmt = 0;
			int p=1;
			for(StudentInfo tot : list)
			{
				tot.setsNo(String.valueOf(p++));
				totalAmt += tot.getTotalFee(); 
			}
			totalamountString = String.valueOf(totalAmt);

			selectedInstallList=new ArrayList<>();

			for(int i=0;i<checkMonthSelected.length;i++)
			{
				SelectItem ll=new SelectItem();
				ll.setLabel(checkMonthSelected[i]);
				ll.setValue(monthName(Integer.parseInt(checkMonthSelected[i])));
				selectedInstallList.add(ll);
			}

			/*if(schid.equals("-1"))
			{
				ArrayList<StudentInfo> tempList = new ArrayList<>();
				for(SelectItem in : branchList)
				{
					//("Branch : "+in.getValue());
					if(Integer.parseInt(selectedMonth)>9)
					{
						tempList=DBM.searchStudentListByClassSectionForDueFeeReport11(String.valueOf(in.getValue()),sectionid,date,session,conn);
						studentList.addAll(tempList);
					}
					else
					{
						tempList=DBM.DefaulterListForMasteradmin(String.valueOf(in.getValue()),sectionid,selectedMonth,session,conn);
						studentList.addAll(tempList);

					}

				}

			}
			else
			{
				if(Integer.parseInt(selectedMonth)>9)
				{
					studentList=DBM.searchStudentListByClassSectionForDueFeeReport11(schid,sectionid,date,session,conn);
				}
				else
				{

					studentList=DBM.DefaulterListForMasteradmin(schid,sectionid,selectedMonth,session,conn);

				}

			}*/


			if(list.size()>0)
			{

				/*for(StudentInfo ll:list)
				{
					totalDueAmount+=ll.getTotalFee();
				}

				totalamountString=String.format ("%.0f", totalDueAmount);

				if(schid.equals("-1"))
				{
					className="All";
					sectionName="All";
				}
				else
				{
					className=DBM.classNameFromidSchid(branches,selectedCLassSection,session,conn);
					sectionName=DBM.sectionNameByIdSchid(branches,selectedSection,conn);
				}*/

				/*feelist=DBM.viewFeeList(conn);
				FeeInfo ff = new FeeInfo();
				ff.setFeeName("Previous Fee");
				ff.setFeeId("-1");
				ff.setFeeType("year");
				feelist.add(ff);*/ /// Can be uncommented later

				//feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);   // only for adding previous fee label






				show=true;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void searchStudentByClassSectionTransport(String branches)
	{
		
		totalDueAmount=0;
		studentList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			//SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			//	String session=DatabaseMethods1.selectedSessionDetails();

			////("started : "+new Date());

			String sectionid="";
			if(selectedCLassSection.equals("-1"))
			{
				sectionid="-1";
			}
			else if(selectedSection.equals("-1"))
			{
				for(SelectItem ii : sectionList)
				{
					if(sectionid.equals(""))
					{
						sectionid = String.valueOf(ii.getValue());
					}
					else
					{
						sectionid = sectionid + "','" + String.valueOf(ii.getValue());
					}
				}
				sectionid="('"+sectionid+"')";
			}
			else
			{
				sectionid = "('"+selectedSection+"')";
			}
			
		

			list=DBM.dueFeesForblmNewTransport(branches,date, sectionid,checkMonthSelected,lateFeeCheck,conn);
			
			double totalAmt = 0;
			int p=1;
			for(StudentInfo tot : list)
			{
				tot.setsNo(String.valueOf(p++));
				totalAmt += tot.getTotalFee(); 
			}
			totalamountString = String.valueOf(totalAmt);

			selectedInstallList=new ArrayList<>();

			for(int i=0;i<checkMonthSelected.length;i++)
			{
				SelectItem ll=new SelectItem();
				ll.setLabel(checkMonthSelected[i]);
				ll.setValue(monthName(Integer.parseInt(checkMonthSelected[i])));
				selectedInstallList.add(ll);
			}

			/*if(schid.equals("-1"))
			{
				ArrayList<StudentInfo> tempList = new ArrayList<>();
				for(SelectItem in : branchList)
				{
					//("Branch : "+in.getValue());
					if(Integer.parseInt(selectedMonth)>9)
					{
						tempList=DBM.searchStudentListByClassSectionForDueFeeReport11(String.valueOf(in.getValue()),sectionid,date,session,conn);
						studentList.addAll(tempList);
					}
					else
					{
						tempList=DBM.DefaulterListForMasteradmin(String.valueOf(in.getValue()),sectionid,selectedMonth,session,conn);
						studentList.addAll(tempList);

					}

				}

			}
			else
			{
				if(Integer.parseInt(selectedMonth)>9)
				{
					studentList=DBM.searchStudentListByClassSectionForDueFeeReport11(schid,sectionid,date,session,conn);
				}
				else
				{

					studentList=DBM.DefaulterListForMasteradmin(schid,sectionid,selectedMonth,session,conn);

				}

			}*/


			if(list.size()>0)
			{

				/*for(StudentInfo ll:list)
				{
					totalDueAmount+=ll.getTotalFee();
				}

				totalamountString=String.format ("%.0f", totalDueAmount);

				if(schid.equals("-1"))
				{
					className="All";
					sectionName="All";
				}
				else
				{
					className=DBM.classNameFromidSchid(branches,selectedCLassSection,session,conn);
					sectionName=DBM.sectionNameByIdSchid(branches,selectedSection,conn);
				}*/

				/*feelist=DBM.viewFeeList(conn);
				FeeInfo ff = new FeeInfo();
				ff.setFeeName("Previous Fee");
				ff.setFeeId("-1");
				ff.setFeeType("year");
				feelist.add(ff);*/ /// Can be uncommented later

				//feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);   // only for adding previous fee label






				show=true;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection con = DataBaseConnection.javaConnection();
		studentList=obj.searchStudentList(branches,query,con);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
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



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
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
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getFathersName() {
		return fathersName;
	}
	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}
	public String getTutionFeePeriod() {
		return tutionFeePeriod;
	}
	public void setTutionFeePeriod(String tutionFeePeriod) {
		this.tutionFeePeriod = tutionFeePeriod;
	}
	public String getRegFee() {
		return regFee;
	}
	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}
	public String getTutionFee() {
		return tutionFee;
	}
	public void setTutionFee(String tutionFee) {
		this.tutionFee = tutionFee;
	}
	public String getAdmissionFee() {
		return admissionFee;
	}
	public void setAdmissionFee(String admissionFee) {
		this.admissionFee = admissionFee;
	}
	public String getExamFee() {
		return examFee;
	}
	public void setExamFee(String examFee) {
		this.examFee = examFee;
	}
	public String getAnnualFee() {
		return annualFee;
	}
	public void setAnnualFee(String annualFee) {
		this.annualFee = annualFee;
	}
	public String getTransFee() {
		return transFee;
	}
	public void setTransFee(String transFee) {
		this.transFee = transFee;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSelectedSession2() {
		return selectedSession2;
	}
	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}
	public void setSelectedSession2(String selectedSession2) {
		this.selectedSession2 = selectedSession2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getArtFee() {
		return artFee;
	}

	public void setArtFee(String artFee) {
		this.artFee = artFee;
	}

	public String getActivityFee() {
		return activityFee;
	}

	public void setActivityFee(String activityFee) {
		this.activityFee = activityFee;
	}

	public String getTermFee() {
		return termFee;
	}

	public void setTermFee(String termFee) {
		this.termFee = termFee;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}
	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowSchool() {
		return showSchool;
	}

	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
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

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
	}


	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}


	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}


	public String getSelectedMonth() {
		return selectedMonth;
	}


	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}


	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}


	public ArrayList<SelectItem> getSelectedInstallList() {
		return selectedInstallList;
	}


	public void setSelectedInstallList(ArrayList<SelectItem> selectedInstallList) {
		this.selectedInstallList = selectedInstallList;
	}


	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}


	public ArrayList<StudentInfo> getList() {
		return list;
	}


	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}


	public StreamedContent getFile() {
		return file;
	}


	public void setFile(StreamedContent file) {
		this.file = file;
	}


	public String getLateFeeCheck() {
		return lateFeeCheck;
	}


	public void setLateFeeCheck(String lateFeeCheck) {
		this.lateFeeCheck = lateFeeCheck;
	}



}
