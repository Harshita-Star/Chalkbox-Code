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
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="ageWiseStrengthReport")
@ViewScoped
public class AgeWiseStrengthReportBean implements Serializable
{
	ArrayList<SelectItem> categoryList;
	ArrayList<SelectItem> religionList;
	ArrayList<String>  genderList,columnsList=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	int grandTotaloFBoys,grandTotalOfGirls,grandTotalNumberOfStudent;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	Date searchDate;
	String schoolId,sessionValue;
    DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();


   
	public AgeWiseStrengthReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		religionList=obj.allAgeList(conn);
		
		SelectItem item = new SelectItem();
		item.setLabel("0");
		item.setValue(String.valueOf("0"));
		religionList.add(item);
		
		schoolDetails =obj.fullSchoolInfo(conn);
		
		//// // System.out.println("Testing git");

		genderList=new ArrayList<>();
		genderList.add("Boys");
		genderList.add("Girls");
		genderList.add("Transgender");
		searchDate=new Date();

		submit();
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
	      XSSFRow h = sheet.createRow(0);

	 sheet.createFreezePane(0,2);
	      XSSFRow headerRow = sheet.getRow(0);
	     
	      headerRow.setHeight((short)1200);
	     
	      XSSFRow firstRow = sheet.createRow(1);
	     
	 
	      XSSFCell firstCellv = firstRow.createCell(0);
	     firstCellv.setCellValue("Class");
	     int  counter =0;
	     CellStyle styleF5 = book.createCellStyle();
	      styleF5.setVerticalAlignment(VerticalAlignment.CENTER);
	     styleF5.setAlignment(HorizontalAlignment.CENTER);
	     
	     for(int i=0;i<religionList.size();i++)
	     {
	     XSSFCell celldynamic = firstRow.createCell(1+(3*i));
	     celldynamic.setCellValue(religionList.get(i).getValue().toString());
	    // // // System.out.println(religionList.get(i).getValue().toString());
	     sheet.addMergedRegion(new CellRangeAddress(1,1,1+(3*i),3+(3*i)));
	     celldynamic.setCellStyle(styleF5);
	     counter = 2+(3*i);
	     }
	     
	    
	     
	     
	     
	     XSSFCell celldynamicL = firstRow.createCell(counter+2);
	     sheet.addMergedRegion(new CellRangeAddress(1,1,counter+2,counter+4));
	     celldynamicL.setCellValue("Total");
	     celldynamicL.setCellStyle(styleF5);
	     
	     XSSFCell celldynamicLast = firstRow.createCell(counter+5);
	     celldynamicLast.setCellValue("Grand");
	     
	     
	     
	     
	     XSSFRow secondRow = sheet.createRow(2);
	     XSSFCell secondCell = secondRow.createCell(0);
	     secondCell.setCellValue("");
	     
	     for(int i=1;i<=religionList.size();i++)
	     {
	     XSSFCell celldynamic2 = secondRow.createCell((3*i)-2);
	     celldynamic2.setCellValue("Boys");
	     XSSFCell celldynamic222 = secondRow.createCell((3*i)-1);
	     celldynamic222.setCellValue("Girls");
	     XSSFCell celldynamic223 = secondRow.createCell(3*i);
	     celldynamic223.setCellValue("Transgender");
	     }
	     XSSFCell celldynamicLSec = secondRow.createCell(counter+2);
	     celldynamicLSec.setCellValue("Boys");
	     
	     XSSFCell celldynamicLSec2 = secondRow.createCell(counter+3);
	     celldynamicLSec2.setCellValue("Girls");
	     
	     XSSFCell celldynamicLSec23 = secondRow.createCell(counter+4);
	     celldynamicLSec23.setCellValue("Transgender");
	     
	     XSSFCell celldynamicLastSec = secondRow.createCell(counter+5);
	     celldynamicLastSec.setCellValue("Total");
	     
	     
	     

	   
	   
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
	              my_anchor.setCol2(7);
	              int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	       
	              my_banner_image.close();                
	       
	             XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	           
	       
	             

	           

	     } catch (IOException ioex) {
	               
	             }
	     


	 

	}
	

	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
        studentList=dbr.ageWiseStudentList(religionList,searchDate,conn);
		columnsList.clear();
		for(SelectItem religion : religionList)
		{
			for(String gender :genderList)
			{
				columnsList.add(religion.getValue() + gender);
			}
		}
//		grandTotalNumberOfStudent=0;
//		grandTotaloFBoys=0;
//		grandTotalOfGirls=0;
//		for(StudentInfo mm:studentList)
//		{
//			grandTotalNumberOfStudent +=mm.getGrandTotalBoysAndGirls();
//			grandTotaloFBoys +=mm.getBoysTotal();
//			grandTotalOfGirls +=mm.getGirlTotal();
//
//		}
//		setGrandTotalNumberOfStudent(grandTotalNumberOfStudent);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	public  void exportAgedPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		//Header
		try {

			
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

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
			Chunk c8 = new Chunk("\n                                                                   Age Wise Strength Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);



			Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable tableq = new PdfPTable(5+(religionList.size()*3));

			tableq.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			tableq.addCell(new Phrase("Class",fontq));
			for(int i=0;i<religionList.size();i++) {
				tableq.addCell(new Phrase(String.valueOf(religionList.get(i).getValue()),fontq));
				tableq.addCell(new Phrase(" ",fontq));
				tableq.addCell(new Phrase(" ",fontq));


			}
			tableq.addCell(new Phrase("Total",fontq));
			tableq.addCell(new Phrase("",fontq));
			tableq.addCell(new Phrase("",fontq));
			tableq.addCell(new Phrase("Grand",fontq));

			tableq.addCell(new Phrase("",fontq));
			for(int i=0;i<religionList.size();i++) {
				tableq.addCell(new Phrase("Boys",fontq));
				tableq.addCell(new Phrase("Girls",fontq));
				tableq.addCell(new Phrase("Transgender",fontq));


			}
			tableq.addCell(new Phrase("Boys",fontq));
			tableq.addCell(new Phrase("Girls",fontq));
			tableq.addCell(new Phrase("Transgender",fontq));
			tableq.addCell(new Phrase("Total",fontq));


			tableq.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cellsq = tableq.getRow(0).getCells();
			for(int i=0;i<cellsq.length;i++)
			{
				cellsq[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cellsq[i].setBorderWidth(2);

			}

			PdfPCell[] cellsqq = tableq.getRow(1).getCells();
			for(int i=0;i<cellsqq.length;i++)
			{
				cellsqq[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cellsqq[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int k=0;k<studentList.size();k++){
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getClassName()),fontq));
				for(int i=0;i<religionList.size();i++) {
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(religionList.get(i).getValue()+"Boys"),fontq));
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(religionList.get(i).getValue()+"Girls"),fontq));
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(religionList.get(i).getValue()+"Transgendernew"),fontq));


				}
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getBoysTotal()),fontq));
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getGirlTotal()),fontq));
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getTransTotal()),fontq));
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getGrandTotalBoysAndGirls()),fontq));

			}
//			tableq.addCell(new Phrase("Total",fontq));
//			for(int i=0;i<religionList.size();i++) {
//				tableq.addCell(new Phrase("",fontq));
//				tableq.addCell(new Phrase("",fontq));
//				tableq.addCell(new Phrase("",fontq));
//
//
//			}
//			tableq.addCell(new Phrase(String.valueOf(grandTotaloFBoys),fontq));
//			tableq.addCell(new Phrase(String.valueOf(grandTotalOfGirls),fontq));
//			
//			tableq.addCell(new Phrase(String.valueOf(grandTotalNumberOfStudent),fontq));


			tableq.setWidthPercentage(110);
			document.add(tableq);

		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Age_Wise_Strength_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Age_Wise_Strength_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList<String> getGenderList() {
		return genderList;
	}

	public void setGenderList(ArrayList<String> genderList) {
		this.genderList = genderList;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList<String> columnsList) {
		this.columnsList = columnsList;
	}

	public ArrayList<SelectItem> getReligionList() {
		return religionList;
	}

	public void setReligionList(ArrayList<SelectItem> religionList) {
		this.religionList = religionList;
	}

	public int getGrandTotaloFBoys() {
		return grandTotaloFBoys;
	}

	public void setGrandTotaloFBoys(int grandTotaloFBoys) {
		this.grandTotaloFBoys = grandTotaloFBoys;
	}

	public int getGrandTotalOfGirls() {
		return grandTotalOfGirls;
	}

	public void setGrandTotalOfGirls(int grandTotalOfGirls) {
		this.grandTotalOfGirls = grandTotalOfGirls;
	}

	public int getGrandTotalNumberOfStudent() {
		return grandTotalNumberOfStudent;
	}

	public void setGrandTotalNumberOfStudent(int grandTotalNumberOfStudent) {
		this.grandTotalNumberOfStudent = grandTotalNumberOfStudent;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	

}

