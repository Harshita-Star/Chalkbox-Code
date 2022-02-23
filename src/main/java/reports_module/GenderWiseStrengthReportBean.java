package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="genderWiseStrengthReport")
@ViewScoped
public class GenderWiseStrengthReportBean implements Serializable
{
	ArrayList<SelectItem> categoryList;
	ArrayList<SelectItem> religionList;
	ArrayList<String> columnsList=new ArrayList<>();
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	int grandTotalNumberOfStudent;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsReports objReport = new DataBaseMethodsReports();
	String schoolId,session;

	public GenderWiseStrengthReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =obj1.fullSchoolInfo(conn);
		schoolId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schoolId,conn);
		religionList=new ArrayList<>();
		SelectItem ll=new SelectItem();
		ll.setLabel("Boys");
		ll.setValue("Male");
		religionList.add(ll);

		ll=new SelectItem();
		ll.setLabel("Girls");
		ll.setValue("Female");
		religionList.add(ll);
		
		ll=new SelectItem();
		ll.setLabel("Transgender");
		ll.setValue("Transgender");
		religionList.add(ll);

		studentList=objReport.genderWiseStudentList(religionList,conn);
		columnsList.clear();
		for(SelectItem religion : religionList)
		{
			columnsList.add(String.valueOf(religion.getValue()));
		}
		grandTotalNumberOfStudent=0;
		for(StudentInfo mm:studentList)
		{
			grandTotalNumberOfStudent +=mm.getGrandTotalBoysAndGirls();

		}
		setGrandTotalNumberOfStudent(grandTotalNumberOfStudent);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportGenderPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj1.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im =null;
			try {
				im  =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
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
			Chunk c8 = new Chunk("\n                                                    Gender Wise Strength Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);







			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(2+columnsList.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Class");
			for(int i=0;i<religionList.size();i++) {
				table.addCell(religionList.get(i).getLabel());
			}
			table.addCell("Total");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int k=0;k<studentList.size();k++){

				table.addCell(new Phrase(String.valueOf(studentList.get(k).getClassName()),font));
				for(int i=0;i<religionList.size();i++) {
					table.addCell(new Phrase(studentList.get(k).getAttendanceMap().get(religionList.get(i).getValue()),font));
				}

				table.addCell(new Phrase(String.valueOf(studentList.get(k).getGrandTotalBoysAndGirls()),font));






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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Gender_Wise_Strength_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Gender_Wise_Strength_Report.pdf").stream(()->isFromFirstData).build();



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


}

