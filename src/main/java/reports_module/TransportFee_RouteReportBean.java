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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.SideMenuBean;
import schooldata.StudentInfo;

@ManagedBean(name="transFee_RouteReport")
@ViewScoped
public class TransportFee_RouteReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedClass,selectedSection,branches,newschid,routeName,session;
	String type="";
	ArrayList<SelectItem> classList,sectionList,monthList,branchList,monthListHeader;
	ArrayList<StudentInfo> studentList;
	String sectionName,className,userType, username;
	String schid="",schoolid;
	boolean show,showTotal,showClass,showSchool;
	int totalStudent;
	DatabaseMethods1 obj=new DatabaseMethods1();

	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,headerImagePath;
	String regno="";
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DataBaseMethodsReports objReport=new DataBaseMethodsReports();



	public TransportFee_RouteReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolid = (String) ss.getAttribute("schoolid");
		schid = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolDetails =obj.fullSchoolInfo(conn);
		session=obj.selectedSessionDetails(schid, conn);
		
		if(schoolid.equals("NA"))
		{
			routeName="noStop";
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
		}
		else
		{
			routeName="withStop";
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts")
					|| userType.equalsIgnoreCase("Transport Manager"))
			{
				classList = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("-1");
				classList.add(si);

				ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(schoolid, conn);

				if(temp.size()>0)
				{
					classList.addAll(temp);
				}
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classList=obj.cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classList=obj.allClassListForClassTeacher(empid,schoolid,conn);

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
		sectionList=obj.allSection(schoolid,selectedClass,conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts")
				|| userType.equalsIgnoreCase("Transport Manager")
				)
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allSection(schoolid, selectedClass, conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String session="";

		if(type.equalsIgnoreCase("amount"))
		{
			if(schoolid.contains(","))
			{
				monthList=obj.allMonthsTransport("251",conn);
				monthListHeader=obj.allMonthsTransportHeader("251",conn);
				session=obj.selectedSessionDetails("251",conn);
			}
			else
			{
				monthList=obj.allMonthsTransport(schoolid,conn);
				monthListHeader=obj.allMonthsTransportHeader(schoolid,conn);
				session=obj.selectedSessionDetails(schoolid,conn);
			}
			SelectItem ll=new SelectItem();
			ll.setLabel("Total");
			ll.setValue("-1");
			monthList.add(ll);
		}
		else
		{
			
			if(schoolid.contains(","))
			{
				monthList=obj.allMonthsTransport("251",conn);
				monthListHeader=obj.allMonthsTransportHeader("251",conn);
				session=obj.selectedSessionDetails("251",conn);
			}
			else
			{
				monthList=obj.allMonthsTransport(schoolid,conn);
				monthListHeader=obj.allMonthsTransportHeader(schoolid,conn);
				session=obj.selectedSessionDetails(schoolid,conn);
			}
		}

		studentList=objReport.transportFee_RouteReport(selectedClass,type,selectedSection,monthList,schoolid,session,routeName,conn);

		if(type.equals("amount"))
		{
			showTotal=true;
			totalStudent=studentList.size()-1;
		}
		else
		{
			totalStudent=studentList.size();
			showTotal=false;
		}
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

	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedClass="-1";
		sectionList = new ArrayList<>();
		show=false;

		if(schid.equals("-1"))
		{
			schoolid=branches;
			showClass = false;
			showSchool=true;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
		}
		else
		{
			schoolid=schid;
			showClass = true;
			showSchool=false;
			schoolInfo(schid);
			classList=obj.allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=obj.fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();

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
	
	public  void exportTransFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//System.getProperty("user.home");


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
			Chunk c8 = new Chunk("\nTransport Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			p8.setAlignment(Element.ALIGN_CENTER);

			document.add(p8);

			Chunk c7 = new Chunk("\nTotal Students : "+totalStudent+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			p7.setAlignment(Element.ALIGN_CENTER);

			document.add(p7);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(6+monthList.size()*2);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			
			table.addCell(new Phrase("S.No.",font));
			table.addCell(new Phrase("Sr. No.",font));
			table.addCell(new Phrase("Student Name",font));
			table.addCell(new Phrase("Father's Name",font));
			table.addCell(new Phrase("Class",font));
			table.addCell(new Phrase("Date of Admission",font));
			
			SideMenuBean smb = new SideMenuBean();
			String countryName="";
			if(smb.getList().getCountry().equalsIgnoreCase("UAE")) {
				countryName ="AED";
			}
			else {
				countryName ="Rs.";
			}
			
			for(int j=0;j<monthList.size();j++) {
				
				 PdfPCell c1111 = new PdfPCell(new Phrase(String.valueOf(monthList.get(j).getLabel()),font));
				    c1111.setColspan(2);
				    c1111.setHorizontalAlignment(Element.ALIGN_MIDDLE);
				    c1111.setVerticalAlignment(Element.ALIGN_MIDDLE);
				    table.addCell(c1111);
			}

			table.setHeaderRows(1);
			
			PdfPCell[] cells = table.getRow(0).getCells();
//			for(int i=0;i<cells.length;i++)
//			{
//				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));
//				cells[i].setBorderWidth(2);
//			}
			
			table.addCell(new Phrase((""),font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			table.addCell(new Phrase("",font));
			
			for(int j=0;j<monthList.size();j++) {
				table.addCell(new Phrase("Route",font));
				table.addCell(new Phrase("Amount("+countryName+")",font));
			}
			
//			PdfPCell[] cells2 = table.getRow(0).getCells();
//			for(int i=0;i<cells2.length;i++)
//			{
//				cells2[i].setBackgroundColor(new BaseColor(242, 234, 221));
//				cells2[i].setBorderWidth(2);
//			}


			for (int i=0;i<studentList.size();i++){

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(),font));
				table.addCell(new Phrase(studentList.get(i).getFname(),font));
				table.addCell(new Phrase(studentList.get(i).getFatherName(),font));
				table.addCell(new Phrase(studentList.get(i).getClassName(),font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionDate(),font));
				for(int j=0;j<monthList.size();j++) {
					table.addCell(new Phrase(studentList.get(i).getFeesMap().get(monthList.get(j).getValue()+"route"),font));
                    table.addCell(new Phrase(studentList.get(i).getFeesMap().get(monthList.get(j).getValue()+"amount"),font));
				}





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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Transport_Fee_Route_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Transport_Fee_Route_Report.pdf").stream(()->isFromFirstData).build();


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
		XSSFRow h0 = sheet.createRow(0);
		XSSFRow h1 = sheet.createRow(1);
		XSSFRow h = sheet.createRow(2);
		
		XSSFRow headerR = sheet.getRow(0);
		 headerR.setHeight((short)1200);

		XSSFRow header = sheet.getRow(1);
		XSSFRow header2 = sheet.getRow(2);
		
		
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
	     style.setVerticalAlignment(VerticalAlignment.CENTER);
	      style.setAlignment(HorizontalAlignment.CENTER);
	      
		XSSFCell celRow1 = header.createCell(0);
		celRow1.setCellValue("S.No.");
		celRow1.setCellStyle(style);
		XSSFCell celRow2 = header.createCell(1);
		celRow2.setCellValue("Sr. No.");
		celRow2.setCellStyle(style);
		XSSFCell celRow3 = header.createCell(2);
		celRow3.setCellValue("Student Name");
		celRow3.setCellStyle(style);
		XSSFCell celRow4 = header.createCell(3);
		celRow4.setCellValue("Father's Name");
		celRow4.setCellStyle(style);
		XSSFCell celRow5 = header.createCell(4);
		celRow5.setCellValue("Class");
		celRow5.setCellStyle(style);
		XSSFCell celRow6 = header.createCell(5);
		celRow6.setCellValue("Date of Admission");
		celRow6.setCellStyle(style);
		
		SideMenuBean smb = new SideMenuBean();
		String countryName="";
		if(smb.getList().getCountry().equalsIgnoreCase("UAE")) {
			countryName ="AED";
		}
		else {
			countryName ="Rs.";
		}
		
		int k=6;
		for(int j=0;j<monthList.size();j++) 
		{
			XSSFCell celRow7 = header.createCell(k);
			celRow7.setCellValue(String.valueOf(monthList.get(j).getLabel()));
		     sheet.addMergedRegion(new CellRangeAddress(1,1,6+(2*j),6+((2*j)+1)));
		     XSSFCell celRow8 = header2.createCell(k);
				celRow8.setCellValue("Route");
				  XSSFCell celRow9 = header2.createCell(k+1);	
				celRow9.setCellValue("Amount("+countryName+")");
		     
		     
			
			celRow7.setCellStyle(style);
			celRow8.setCellStyle(style);
			celRow9.setCellStyle(style);
			
			k+=2;
		}
		
		int colCount = (monthList.size()*2)+6;

		int rowCount = studentList.size();

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
			my_anchor.setCol2(7);
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

	


		CellStyle style22 = book.createCellStyle();
		XSSFColor color11 = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style22).setFillForegroundColor(color11);
		style22.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	
		style22.setBorderLeft(BorderStyle.THIN);
		style22.setBorderRight(BorderStyle.THIN);
		style22.setBorderTop(BorderStyle.NONE);
		style22.setBorderBottom(BorderStyle.THIN);

		style22.setBottomBorderColor(IndexedColors.RED.getIndex());
		style22.setTopBorderColor(IndexedColors.RED.getIndex());
		style22.setLeftBorderColor(IndexedColors.RED.getIndex());
		style22.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int rowInd = 3; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <colCount ; cellInd++) {
				
				
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
		for(int rowInd = 3; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			for(int cellInd = 6; cellInd <colCount ; cellInd++) {
				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
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
						try {
							
								Double ds = Double.valueOf(strVal);

							

								cell.setCellType(Cell.CELL_TYPE_NUMERIC);



								cell.setCellStyle(sty);
								
								cell.setCellValue(ds);
							
						}
						catch (Exception e) {
							// TODO: handle exception
						}

				
				}
			
		}
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


	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isShowTotal() {
		return showTotal;
	}
	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}
	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
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

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
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

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public ArrayList<SelectItem> getMonthListHeader() {
		return monthListHeader;
	}

	public void setMonthListHeader(ArrayList<SelectItem> monthListHeader) {
		this.monthListHeader = monthListHeader;
	}
	
}
