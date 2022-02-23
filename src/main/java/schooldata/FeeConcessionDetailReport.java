package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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
@ManagedBean(name="feeConcessionDetailReport")
@ViewScoped
public class FeeConcessionDetailReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>(),multipleStudentList=new ArrayList<>();
	ArrayList<SelectItem> classList=new ArrayList<>();
	ArrayList<SelectItem> sectionList=new ArrayList<>(),concessionCategoryList=new ArrayList<>();
	String selectedClass,conceesionCategory,concessionStatus,selectedCategory;
	String sectionName;
	String selectedSection;
	String className,section,total;
	boolean b;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public FeeConcessionDetailReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		concessionCategoryList=new DatabaseMethods1().allConnsessionType(conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		conceesionCategory=String.valueOf(concessionCategoryList.get(0).getValue());
		concessionCategoryList.remove(0);
		classList=new DatabaseMethods1().allClass(conn);
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
	public void getStudentStrength()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList = new DatabaseMethods1().feeConcessionDetailReport(selectedClass,selectedSection,selectedCategory,conceesionCategory,conn);
		if (studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b = false;
		}
		else
		{
			b = true;
		}
		total = String.valueOf(studentList.size());
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportConFeePdf() throws DocumentException, IOException, FileNotFoundException {

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
			

			Chunk c1 = new Chunk(  schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

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
			Chunk c8 = new Chunk("\n                                                               Concession Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(7);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno.");
			table.addCell("Sr. No.");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Concession Align");
			table.addCell("Student Type");
			//   table.addCell("Receipt No.");
			table.addCell("Amount");
			//   table.addCell("Payment Mode");
			//  table.addCell("Remark");
			//  table.addCell("Date");



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
				// //// // System.out.println("fs");
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno())));
				table.addCell(new Phrase(studentList.get(i).getSrNo(),font));
				table.addCell(new Phrase(studentList.get(i).getFullName()));

				table.addCell(new Phrase(studentList.get(i).getFathersName()));

				table.addCell(new Phrase(studentList.get(i).getCurrentConcessionAssign()));


				table.addCell(new Phrase(studentList.get(i).getStudentType() ,font));

				table.addCell(new Phrase(studentList.get(i).getFeeConcession()));



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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Concession_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Concession_Fee_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<StudentInfo> getMultipleStudentList() {
		return multipleStudentList;
	}

	public void setMultipleStudentList(ArrayList<StudentInfo> multipleStudentList) {
		this.multipleStudentList = multipleStudentList;
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

	public ArrayList<SelectItem> getConcessionCategoryList() {
		return concessionCategoryList;
	}

	public void setConcessionCategoryList(
			ArrayList<SelectItem> concessionCategoryList) {
		this.concessionCategoryList = concessionCategoryList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getConceesionCategory() {
		return conceesionCategory;
	}

	public void setConceesionCategory(String conceesionCategory) {
		this.conceesionCategory = conceesionCategory;
	}

	public String getConcessionStatus() {
		return concessionStatus;
	}

	public void setConcessionStatus(String concessionStatus) {
		this.concessionStatus = concessionStatus;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
