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
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

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

import Json.DataBaseMeathodJson;

@ManagedBean(name="meetingAttendanceReport")
@ViewScoped

public class MeetingAttendanceReportBean implements Serializable
{
	Date addDate = new Date();
	ArrayList<SelectItem> classSection,sectionList,classList,subjectList;
	String selectedCLassSection,selectedSection,username,schoolid,type,subject,selectedClassName, header, subjectName;
	ArrayList<StudentInfo> list = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	
	public MeetingAttendanceReportBean() 
	{
		selectedClassName = subjectName = "";
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			username=(String) ss.getAttribute("username");
			schoolid=(String) ss.getAttribute("schoolid");
			type=(String) ss.getAttribute("type");
			if(type.equalsIgnoreCase("admin")
					|| type.equalsIgnoreCase("authority")
					|| type.equalsIgnoreCase("principal")
					|| type.equalsIgnoreCase("vice principal")
					|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
					|| type.equalsIgnoreCase("Accounts"))
			{
				classSection=obj.allClass(conn);
			}
			else if(type.equalsIgnoreCase("academic coordinator") 
					|| type.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classSection=obj.cordinatorClassList(empid, schoolid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);

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
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");

		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer")
				|| type.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj.allSection(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subjectList=obj.allSubjectClassWise(selectedCLassSection, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn= DataBaseConnection.javaConnection();
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(addDate);
		String strDateShow = new SimpleDateFormat("dd-MM-yyyy").format(addDate);
		
		String className=obj.classNameFromidSchid(schoolid, selectedCLassSection, new DatabaseMethods1().selectedSessionDetails(schoolid, conn), conn);
		String sectioname=obj.sectionNameByIdSchid(schoolid, selectedSection, conn);
		selectedClassName=className+"-"+sectioname;
		subjectName = obj.subjectNameFromid(subject, conn);
		
		header = "Meeting Attendance Report \n\n"+"Class : "+selectedClassName+" , Subject : "+subjectName+" , "+"Date : "+strDateShow;
		boolean checkMeeting = obj.checkMeeting(strDate, selectedCLassSection, selectedSection, subject, schoolid, conn);
		if(checkMeeting)
		{
			list=new DataBaseMeathodJson().meetingAttendanceReport(strDate, selectedCLassSection, selectedSection, subject, schoolid, conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Meeting Found For The Applied Filters. Try Again With Different Filter Combinaton."));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}

	public  void exportPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im = null;
			try {
				im  =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
			}

			Chunk c = new Chunk(ls.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
			}

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
		}

		try {
			Chunk c8 = new Chunk("\n"+header+"\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

//			Chunk c9 = new Chunk("\nClass - : "+selectedClassName+"\n\n",fo );
//			Paragraph p9 = new Paragraph();
//			p9.add(c9);
//			document.add(p9);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("S.No.");
			table.addCell("Student Name");
			table.addCell("Father Name");
			table.addCell("Contact No.");
			table.addCell("Class");
			table.addCell("Class Start Time");
			table.addCell("Class End Time");
			table.addCell("Class Joining Time");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<list.size();i++){
				table.addCell(new Phrase(String.valueOf(i+1),font));
				table.addCell(new Phrase(list.get(i).getName(),font));
				table.addCell(new Phrase(list.get(i).getFatherName(), font));
				table.addCell(new Phrase(list.get(i).getContactNo(), font));
				table.addCell(new Phrase(list.get(i).getClassName(), font));
				
				table.addCell(new Phrase(list.get(i).getClassStartTimeStr(), font));
				table.addCell(new Phrase(list.get(i).getClassEndTimeStr(), font));
				table.addCell(new Phrase(list.get(i).getJoinDateStr(), font));
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Meeting_attendance_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Meeting_attendence_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}



	}
	
	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSelectedClassName() {
		return selectedClassName;
	}

	public void setSelectedClassName(String selectedClassName) {
		this.selectedClassName = selectedClassName;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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
}
