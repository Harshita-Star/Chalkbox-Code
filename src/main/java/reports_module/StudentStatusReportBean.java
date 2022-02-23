package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="studentStatusReport")
@ViewScoped

public class StudentStatusReportBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> studentList,studentStatusList;
	boolean show=false;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String name,schoolID;
	
	public StudentStatusReportBean()
	{
		schoolID = dbm.schoolId();
	}
	

	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=dbr.searchStudentListForStatus(schoolID,query,conn);

		
		schoolDetails =dbm.fullSchoolInfo(conn);

		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName()
	{
		show = false;
		Connection conn=DataBaseConnection.javaConnection();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentStatusList=new ArrayList<>();
						studentStatusList=dbr.studentListForStatusReport(info.getAddNumber(),schoolID,conn);
						
						if(studentStatusList.size()>0)
						{	
						 show=true;
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Detail Found"));
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public  void exportStudentStatusPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbm.fullSchoolInfo(con);

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


			Chunk c8 = new Chunk("\n                                                            Student Status Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);






			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Class");
			table.addCell("Section");
			table.addCell("Session");
			table.addCell("Status");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentStatusList.size();i++){
				table.addCell(new Phrase(String.valueOf(studentStatusList.get(i).getSno()),font));
				table.addCell(new Phrase(studentStatusList.get(i).getFullName(),font));
				table.addCell(new Phrase(studentStatusList.get(i).getFathersName(),font));
				table.addCell(new Phrase(studentStatusList.get(i).getClassName(),font));
				table.addCell(new Phrase(studentStatusList.get(i).getSectionName(),font));
				table.addCell(new Phrase(studentStatusList.get(i).getSession(),font));
				table.addCell(new Phrase(studentStatusList.get(i).getStatus(),font));
			
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Student_Status_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Student_Status_Report.pdf").stream(()->isFromFirstData).build();







	}




	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}


	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}


	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public ArrayList<StudentInfo> getStudentStatusList() {
		return studentStatusList;
	}

	public void setStudentStatusList(ArrayList<StudentInfo> studentStatusList) {
		this.studentStatusList = studentStatusList;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	

}

