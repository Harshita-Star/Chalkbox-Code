package reports_module;

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

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;

@ManagedBean(name="studentWithNoTransport")
@ViewScoped
public class StudentWithNoTransportReportBean implements Serializable{

	
	ArrayList<StudentInfo> arrayList = new ArrayList<StudentInfo>();
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection,selectedType,typeName;
	String className, section, total;
	ArrayList<ClassInfo> list;
	boolean b;
	String sectionName;
	ArrayList<StudentInfo> studentList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,session;
	
	public StudentWithNoTransportReportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=obj.schoolId();
		session=obj.selectedSessionDetails(schoolId, conn);
		schoolDetails =dbm.fullSchoolInfo(conn);
		classList = dbm.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj.allSection(selectedClass, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void noTransport() {
		Connection conn = DataBaseConnection.javaConnection();
		
		ArrayList<String> fieldList=new DataBaseMethodStudent().basicFieldsForStudentList();
		studentList=new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION, QueryConstants.NO_TRANSPORT,null,null,"","","","", session, schoolId, fieldList, conn);
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		} else
			b = true;

		total = String.valueOf(studentList.size());

		if (selectedSection.equals("-1")) 
		{
			if(selectedClass.equalsIgnoreCase("-1")) 
			{
				className = "All";
			}
			else 
			{
				 className = dbm.classNameFromidSchid(schoolId,selectedClass, session, conn);

			}
			sectionName = "All";
		} else {
			className = dbm.classNameFromidSchid(schoolId,selectedClass, session, conn);
			sectionName = dbm.sectionNameByIdSchid(schoolId,selectedSection,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	
	public  void exportPdf() throws DocumentException, IOException, FileNotFoundException {
	       
        Connection con=DataBaseConnection.javaConnection();
        SchoolInfoList ls=obj.fullSchoolInfo(con);
      
      
        try {
         con.close();
     } catch (SQLException e1) {
         
         e1.printStackTrace();
     }
      
         Document  document = new Document();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

      //   String home = System.getProperty("user.home"); 
     
         
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
           
             
               Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );
             
               Chunk c3 = new Chunk(im, -250, 15);
          
             Chunk c1 = new Chunk(  schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);
     
            Paragraph p1 = new Paragraph();

        //  String[] det = studentName.split("/");
          
           p1.add(c);
            p1.add(c1);
            p1.add(c3);
           p1.setAlignment(Element.ALIGN_CENTER);
     
              document.add(p1);
           //   Date dtf = new Date();

              Chunk c7 = new Chunk("                      Transport Not Taken \n\n",fo);
              Chunk c17 = new Chunk("Class : "+className+"             Section : "+sectionName+"              Total : "+total+" \n\n",fo);
                Paragraph p2 = new Paragraph();

            //  String[] det = studentName.split("/");
              
               p2.add(c7);
               p2.add(c17);
                //p2.add(c1);
               // p1.add(c3);
               p2.setAlignment(Element.ALIGN_CENTER);
              document.add(p2);
              Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
              PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1,1,1});
        
              table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
              table.addCell("Sno");
              table.addCell("SrNo");
                  table.addCell("Student Name");
                  table.addCell("Father's Name");
                  table.addCell("Mother's Name");
                  table.addCell("Class");
                  table.addCell("Add Date");
                  table.addCell("Dob");
                  table.addCell("Category");
                  table.addCell("Gender");
                  table.addCell("Current Address");
                
               
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
                     table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno())));
                     table.addCell(new Phrase(studentList.get(i).getSrNo(),font));
                   
                     table.addCell(new Phrase(studentList.get(i).getFullName(),font));
                     table.addCell(new Phrase(studentList.get(i).getFathersName(),font));  
                       
                     table.addCell(new Phrase(studentList.get(i).getMotherName(),font));
                     table.addCell(new Phrase(studentList.get(i).getClassName()+"-"+studentList.get(i).getSectionName(),font));

                     table.addCell(new Phrase(studentList.get(i).getAdmissionDate(),font));
                   
                     table.addCell(new Phrase(studentList.get(i).getDobString(),font));
                     table.addCell(new Phrase(studentList.get(i).getCategory(),font));  
                       
                     table.addCell(new Phrase(studentList.get(i).getGender(),font));
                     table.addCell(new Phrase(studentList.get(i).getCurrentAddress(),font));

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
//               file = new DefaultStreamedContent(isFromFirstData, "application/pdf","transport_report.pdf");
       		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("transport_Report.pdf").stream(()->isFromFirstData).build();

     }

	public void toNum(Object doc)
    {
         XSSFWorkbook book = (XSSFWorkbook)doc;
            XSSFSheet sheet = book.getSheetAt(0);
            Connection conn =DataBaseConnection.javaConnection();
            schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
          
   		

        sheet.createFreezePane(0, 4);
         
       
        XSSFRow header = sheet.getRow(1);
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
                   my_anchor.setCol2(7);
                   int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                //   cellll.setCellValue(my_picture_id);
                   my_banner_image.close();                
              
                  XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
                 
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
        
        
        
        
     
       
       
       for(int rowInd = 3; rowInd <= studentList.size()+2; rowInd++) {
            XSSFRow row = sheet.getRow(rowInd);

            for(int cellInd = 0; cellInd <11 ; cellInd++) {
                
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
                cell.setCellStyle(st2);

            }
            
       }
       
    
      
  try {
	conn.close();
} catch (Exception e) {
	e.printStackTrace();
}

        
        }

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
	
	
}
