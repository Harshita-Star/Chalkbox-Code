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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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

@ManagedBean(name="catWiseStdReport")
@ViewScoped
public class CategoryWiseStudentReport implements Serializable
{
	ArrayList<SelectItem> categoryList;
	ArrayList<String>  genderList,columnsList=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	String schoolId,sessionValue;
	DatabaseMethods1 obj=new DatabaseMethods1();
	int sumBoys = 0;
	int sumGirls = 0;
	int sumAll = 0;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();

	public CategoryWiseStudentReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId,conn);
		categoryList=obj.studentCategoryList(conn);
		//// // System.out.println(categoryList.size());
		schoolDetails = obj.fullSchoolInfo(conn);
		SelectItem ll=new SelectItem();
		ll.setLabel("Total");
		ll.setValue("Total");
		categoryList.add(ll);

		genderList=new ArrayList<>();
		genderList.add("Boys");
		genderList.add("Girls");
		genderList.add("Transgender");
		genderList.add("Total");

		studentList=dbr.categoryWiseStudentList(categoryList/*,genderList*/,conn);
		
//		sumBoys = 0;
//		sumGirls = 0;
//		sumAll = 0;
//		for(StudentInfo slist : studentList)
//		{
//			sumBoys += Integer.valueOf(slist.getAttendanceMap().get("TotalBoys"));
//			sumGirls +=  Integer.valueOf(slist.getAttendanceMap().get("TotalGirls"));
//			sumAll += Integer.valueOf(slist.getAttendanceMap().get("TotalTotal"));
//		}

		columnsList.clear();
		for(SelectItem category : categoryList)
		{
			for(String gender :genderList)
			{
				columnsList.add(category.getValue() + gender);
			}
		}

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

	 sheet.createFreezePane(0,5);
	      XSSFRow headerRow = sheet.getRow(0);
	     
	      headerRow.setHeight((short)1200);
	     
	      XSSFRow firstRow = sheet.createRow(1);
	     
	 
	      XSSFCell firstCellv = firstRow.createCell(0);
	     firstCellv.setCellValue("Class");
	  
	     
	     CellStyle styleF5 = book.createCellStyle();
	      styleF5.setVerticalAlignment(VerticalAlignment.CENTER);
	      styleF5.setAlignment(HorizontalAlignment.CENTER);
	     
	      int t=0;
	     for(int i=1;i<=categoryList.size();i++)
	     {
	    	 if(i>1)
	    	 {
	    		 t= (i*3)+(i-3);
	    	 }
	    	 else {
	    		 t=i;
	    	 }	 
	       XSSFCell celldynamic = firstRow.createCell(t);
	       celldynamic.setCellValue(categoryList.get(i-1).getLabel().toString());
	       sheet.addMergedRegion(new CellRangeAddress(1,1,t,t+3));
	       celldynamic.setCellStyle(styleF5);
	       
	     }
	     
	   
	     
	     
	     
	     
	     XSSFRow secondRow = sheet.createRow(2);
	     XSSFCell secondCell = secondRow.createCell(0);
	     secondCell.setCellValue("");
	     
	     int j=0;
	     
	     for(int i=1;i<=categoryList.size();i++)
	     {
	      
	    	 
	    	 if(i>1)
	    	 {
	    		 j= (i*3)+(i-3);
	    	 }
	    	 else {
	    		 j=i;
	    	 }
	    	 
	     XSSFCell celldynamic1 = secondRow.createCell(j);
	     celldynamic1.setCellValue("Boys");
	    
	     
	     XSSFCell celldynamic222 = secondRow.createCell(j+1);
	     celldynamic222.setCellValue("Girls");
	     
	     XSSFCell celldynamic223 = secondRow.createCell(j+2);
	     celldynamic223.setCellValue("Transgender");
	    
	     
	     XSSFCell celldynamic33 = secondRow.createCell(j+3);
	     celldynamic33.setCellValue("Total");
	     }
	    
	     
	     
	     

	   
	   
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
	              ioex.printStackTrace(); 
	             }
	   


	 

	}

	public  void exportCatStuPdf() throws DocumentException, IOException, FileNotFoundException {

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
			Chunk c8 = new Chunk("\n                                                              Category Wise Student Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

//			Chunk c9 = new Chunk("\nTotal Boys : "+sumBoys+"                                 Total Girls : " +sumGirls+"                                Total : "+sumAll+" \n\n",fo );
//			Paragraph p9 = new Paragraph();
//			p9.add(c9);
//			document.add(p9);







			Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable tableq = new PdfPTable(1+(categoryList.size()*4));

			tableq.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			tableq.addCell(new Phrase("Class",fontq));
			for(int i=0;i<categoryList.size();i++) {
				tableq.addCell(new Phrase(String.valueOf(categoryList.get(i).getLabel()),fontq));
				tableq.addCell(new Phrase(" ",fontq));
				tableq.addCell(new Phrase(" ",fontq));
				tableq.addCell(new Phrase(" ",fontq));

			}
			tableq.addCell(new Phrase("",fontq));
			for(int i=0;i<categoryList.size();i++) {
				tableq.addCell(new Phrase("Boys",fontq));
				tableq.addCell(new Phrase("Girls",fontq));
				tableq.addCell(new Phrase("Transgender",fontq));
				tableq.addCell(new Phrase("Total",fontq));

			}


			tableq.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cellsq = tableq.getRow(0).getCells();
			for(int i=0;i<cellsq.length;i++)
			{
				cellsq[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cellsq[i].setBorderWidth(2);

			}

//			PdfPCell[] cellsqq = tableq.getRow(1).getCells();
//			for(int i=0;i<cellsqq.length;i++)
//			{
//				cellsqq[i].setBackgroundColor(new BaseColor(242, 234, 221));
//
//				cellsqq[i].setBorderWidth(2);
//
//			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int k=0;k<studentList.size();k++){
				tableq.addCell(new Phrase(String.valueOf(studentList.get(k).getClassName()),fontq));
				for(int i=0;i<categoryList.size();i++) {
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(categoryList.get(i).getValue()+"Boys"),fontq));
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(categoryList.get(i).getValue()+"Girls"),fontq));
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(categoryList.get(i).getValue()+"Transgender"),fontq));
					tableq.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(categoryList.get(i).getValue()+"Total"),fontq));

				}

			}


			tableq.setWidthPercentage(110);
			document.add(tableq);






		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Category_Wise_Student_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Category_Wise_Student_Report.pdf").stream(()->isFromFirstData).build();



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

	public int getSumBoys() {
		return sumBoys;
	}

	public void setSumBoys(int sumBoys) {
		this.sumBoys = sumBoys;
	}

	public int getSumGirls() {
		return sumGirls;
	}

	public void setSumGirls(int sumGirls) {
		this.sumGirls = sumGirls;
	}

	public int getSumAll() {
		return sumAll;
	}

	public void setSumAll(int sumAll) {
		this.sumAll = sumAll;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}

