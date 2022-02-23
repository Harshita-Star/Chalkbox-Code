package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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

@ManagedBean(name = "pendingLeaveOfStudentReport")
public class PendingLeaveOfStudentReportBean implements Serializable {

	ArrayList<StudentInfo> studentlist;
	int totalStudent;
	StudentInfo selectedPendingLeaveStudent;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public PendingLeaveOfStudentReportBean() {
		Connection conn=DataBaseConnection.javaConnection();
		studentlist = new DatabaseMethods1().pendingLeavesofStudent(conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);

		totalStudent = studentlist.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String approvedLeaveStudent() {

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i = obj
				.approveLeaveStudent(selectedPendingLeaveStudent.addNumber,new SimpleDateFormat("yyyy-MM-dd").format(selectedPendingLeaveStudent.startingDate),conn);
		if (i == 1) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Leave Approved  Successfully"));

			studentlist = obj.pendingLeavesofStudent(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String leaveDeniedStudent() {

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i = obj
				.deniedLeaveStudent(selectedPendingLeaveStudent.addNumber,new SimpleDateFormat("yyyy-MM-dd").format(selectedPendingLeaveStudent.startingDate),conn);
		if (i == 1) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Leave Denied Successfully"));

			// show=false;
			studentlist = obj.pendingLeavesofStudent(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public  void exportLeavesPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);


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
			Chunk c8 = new Chunk("\n                                                              Pending Leaves Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);







			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			//     table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Class");

			table.addCell("Section");
			table.addCell("Starting Date of Leave");
			table.addCell("Ending Date of Leave");
			//  table.addCell("Current Address");

			table.addCell("Father's Phone");





			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentlist.size();i++){
				table.addCell(new Phrase(String.valueOf(studentlist.get(i).getSno()),font));
				//   table.addCell(new Phrase(String.valueOf(studentlist.get(i).getSrNo()),font));
				table.addCell(new Phrase(studentlist.get(i).getStudentName(),font));
				table.addCell(new Phrase(studentlist.get(i).getFathersName(),font));
				table.addCell(new Phrase(studentlist.get(i).getClassName(),font));
				table.addCell(new Phrase(studentlist.get(i).getSectionName(),font));
				table.addCell(new Phrase(studentlist.get(i).getStartDate(),font));
				table.addCell(new Phrase(studentlist.get(i).getEndDate(),font));

				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getCategory()),font));
				//     table.addCell(new Phrase(String.valueOf(studentList.get(i).getGender()),font));
				table.addCell(new Phrase(studentlist.get(i).getPhone(),font));
				//    table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getAadharNo()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getReligion()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getCaste()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getLastSchoolName()),font));






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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Pending_Leave_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Pending_Leave_Report.pdf").stream(()->isFromFirstData).build();






	}

	public ArrayList<StudentInfo> getStudentlist() {
		return studentlist;
	}

	public void setStudentlist(ArrayList<StudentInfo> studentlist) {
		this.studentlist = studentlist;
	}

	public int getTotalStudent() {
		return totalStudent;
	}

	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}

	public StudentInfo getSelectedPendingLeaveStudent() {
		return selectedPendingLeaveStudent;
	}

	public void setSelectedPendingLeaveStudent(
			StudentInfo selectedPendingLeaveStudent) {
		this.selectedPendingLeaveStudent = selectedPendingLeaveStudent;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
