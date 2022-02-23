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

@ManagedBean(name="studentModBlockReport")
@ViewScoped

public class StudentModBlockReportBean implements Serializable
{
	ArrayList<StudentInfo> list = new ArrayList<StudentInfo>();
	StudentInfo selected = new StudentInfo();
	
	ArrayList<SelectItem> classList, sectionList;
	String selectedClass, selectedSection;
	
	String userType,schoolid,staff,header;
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();

	transient StreamedContent file;
	
	public StudentModBlockReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		ls=new DatabaseMethods1().fullSchoolInfo(conn);
		
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		
		String empid = "";
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			classList=obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
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
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp = obj.allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		String className=obj.classNameFromidSchid(schoolid, selectedClass, new DatabaseMethods1().selectedSessionDetails(schoolid, conn), conn);
		String sectioname= selectedSection.equals("-1") ? "All" : obj.sectionNameByIdSchid(schoolid, selectedSection, conn);
		String selectedClassName=className+"-"+sectioname;
		
		header = "Student App Blocked Modules \n\n"+"Class : "+selectedClassName ;
		list = new DatabaseMethods1().modBlockReport(selectedClass, selectedSection, schoolid, conn);
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

			Chunk c1 = new Chunk("              "+ls.add1+ " " +ls.add2+"                \n\n",fo);

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

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("S.No.");
			table.addCell("Student Name");
			table.addCell("Father Name");
			table.addCell("Contact No.");
			table.addCell("Class");
			table.addCell("Type");
			table.addCell("Due Amount");
			table.addCell("Modules Blocked");

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
				table.addCell(new Phrase(list.get(i).getFname(),font));
				table.addCell(new Phrase(list.get(i).getFatherName(), font));
				table.addCell(new Phrase(list.get(i).getContactNo(), font));
				table.addCell(new Phrase(list.get(i).getClassName()+"-"+list.get(i).getSectionName(), font));
				
				table.addCell(new Phrase(list.get(i).getStatus(), font));
				table.addCell(new Phrase(list.get(i).getDueAmount(), font));
				table.addCell(new Phrase(list.get(i).getModules(), font));
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","StudentAppModulesBlockedReport.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("StudentAppModulesBlockedReport.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}



	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
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

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	
}
