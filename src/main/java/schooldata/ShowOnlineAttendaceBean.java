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
import session_work.DatabaseMethodSession;

@ManagedBean(name="showOnlineAttendaceBean")
@ViewScoped
public class ShowOnlineAttendaceBean implements Serializable{

	Date selectDate;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<StudentInfo>list=new ArrayList<>();
	String selectedCLassSection,selectedSection;
	ArrayList<SelectItem> sectionList,classSection;
	ArrayList<StudentInfo>studentList=new ArrayList<>();
	ArrayList<StudentInfo>attendanceList=new ArrayList<>();
	Boolean renderMarkAtt = false;
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	SchoolInfoList schoolDetails;
	String userType,schoolid,staff;
	transient StreamedContent file;
	String selectedClassName;
	public ShowOnlineAttendaceBean() {
	
		Connection conn=DataBaseConnection.javaConnection();
		selectDate=new Date();
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
 		staff=(String) ses.getAttribute("username");
 		userType=(String)ses.getAttribute("type");
 		schoolid=(String) ses.getAttribute("schoolid");
 		
 		
 		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection=obj.allClass(conn);
			selectedCLassSection = "-1";
			selectedSection = "-1";

			list=new DatabaseMethods1().onlineAttendanceCheck(selectDate,selectedCLassSection,selectedSection,conn);

			selectedClassName="ALL-ALL";
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			classSection = obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			classSection=obj.allClassListForClassTeacher(empid,schoolid,conn);

		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	
	}
	
	public  void exportClasWisePdf() throws DocumentException, IOException, FileNotFoundException {

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
				// TODO: handle exception
			}


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
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
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk("\n                                                               Student Online Attendance Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass - : "+selectedClassName+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//  p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("S.No.");
			table.addCell("Student Name");
			table.addCell("Class");
			table.addCell("Father Name");
			


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
				table.addCell(new Phrase(String.valueOf(list.get(i).getSno()),font));
				table.addCell(new Phrase(list.get(i).getName(),font));

				table.addCell(new Phrase(list.get(i).getClassName()));
				table.addCell(new Phrase(list.get(i).getFatherName()));


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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Class_Wise_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Class_Wise_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	
	public void render()
	{
		renderMarkAtt = false;
	}
	
	public void markAbsentForOther()
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		studentList=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);
		
		//studentList=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
		attendanceList = new ArrayList<StudentInfo>();
		// // System.out.println(studentList.size()+"asf"); 
		for(StudentInfo stu : studentList)
		{
			int count = 1;
			for(StudentInfo lis : list)
			{
				if(lis.getAddNumber().equalsIgnoreCase(stu.getAddNumber())) {
				   
					count = 2;
				}
			}
			if(count == 1)
			{
				stu.setAttendance("A");
				attendanceList.add(stu);
			}
		}
		int i=DatabaseMethods1.attendanceSection(selectedSection, attendanceList, selectDate,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Attendance Marked"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj.allSectionWithAllOption(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);
		}
		
		
		renderMarkAtt = false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dateWiseAttendance()
	{
		Connection conn= DataBaseConnection.javaConnection();
		
		if(selectedSection.equalsIgnoreCase("all"))
		{
			selectedSection="-1";
		}
		
		
		list=new DatabaseMethods1().onlineAttendanceCheck(selectDate,selectedCLassSection,selectedSection,conn);
		
		if(selectedCLassSection.equalsIgnoreCase("-1")&&selectedSection.equalsIgnoreCase("-1"))
		{
			renderMarkAtt = false;
			selectedClassName="All-ALL";
		}
		else if(!selectedCLassSection.equalsIgnoreCase("-1")&&selectedSection.equalsIgnoreCase("-1"))
		{
			renderMarkAtt = false;
			String className=new DatabaseMethods1().classNameFromidSchid(schoolid, selectedCLassSection, new DatabaseMethods1().selectedSessionDetails(schoolid, conn), conn);
			selectedClassName=className+"-ALL";
		}
		else
		{
			
			String className=new DatabaseMethods1().classNameFromidSchid(schoolid, selectedCLassSection, new DatabaseMethods1().selectedSessionDetails(schoolid, conn), conn);
			String sectioname=new DatabaseMethods1().sectionNameByIdSchid(schoolid, selectedSection, conn);
			selectedClassName=className+"-"+sectioname;
			renderMarkAtt = true;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	
	}

	
	
	
	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
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

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public Boolean getRenderMarkAtt() {
		return renderMarkAtt;
	}

	public void setRenderMarkAtt(Boolean renderMarkAtt) {
		this.renderMarkAtt = renderMarkAtt;
	}

	public String getSelectedClassName() {
		return selectedClassName;
	}

	public void setSelectedClassName(String selectedClassName) {
		this.selectedClassName = selectedClassName;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}


	
	
}
