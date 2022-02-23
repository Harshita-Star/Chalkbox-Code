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
@ManagedBean(name="studentCompleteDetail")
@ViewScoped
public class StudentCompleteDetail implements Serializable
{
	ArrayList<StudentInfo>list=new ArrayList<>();
	boolean b;
	String total;
	StudentInfo selectedStudent;
	String selectedSection;
	String selectedCLassSection, schoolid, username, userType;
	ArrayList<SelectItem> sectionList,classSection;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public StudentCompleteDetail()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("-1");
				classSection.add(si);

				ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

				if(temp.size()>0)
				{
					classSection.addAll(temp);
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
						|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
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
		list=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,"-1","-1");
		if(list.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b=false;
		}
		else
		{
			b=true;
		}
		total=String.valueOf(list.size());
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

			ArrayList<SelectItem> temp = obj.allSection(selectedCLassSection,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
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
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			//list=new DatabaseMethods1().searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			list=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);

			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
			{
				b=true;
			}
			total=String.valueOf(list.size());
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

	public  void exportDetailPdf() throws DocumentException, IOException, FileNotFoundException {

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




		Font foHead = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		new Font(FontFamily.HELVETICA, 7, Font.BOLD);
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

			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",foHead );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}
			

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",foHead);

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
			Chunk c8 = new Chunk("\n                                                                  Student Basic Report\n\n",foHead );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("Total : "+total+"\n\n",foHead );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			p9.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");
			table.addCell("Class");
			table.addCell("Gender");

			table.addCell("Add. Date");
			table.addCell("Admit Class");
			table.addCell("Aadhar No.");
			table.addCell("Blood Group");

			table.addCell("Dob");
			table.addCell("Category");
			table.addCell("Address");

			table.addCell("Concession");

			table.addCell("Stop");

			table.addCell("Father's Phone");
			table.addCell("Mother's Phone");



			table.addCell("G1 Phone");
			table.addCell("G2 Phone");
			table.addCell("Email");
			table.addCell("Father Annual Income");
			table.addCell("Mother Annual Income");





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
				table.addCell(new Phrase(list.get(i).getSrNo(),font));
				table.addCell(new Phrase(list.get(i).getFname(),font));
				table.addCell(new Phrase(list.get(i).getFathersName(),font));
				table.addCell(new Phrase(list.get(i).getMotherName(),font));
				table.addCell(new Phrase(list.get(i).getClassName(),font));
				table.addCell(new Phrase(list.get(i).getGender(),font));
				table.addCell(new Phrase(list.get(i).getAdmissionDate(),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getDobString()),font));
				table.addCell(new Phrase(list.get(i).getAdmitClassName(),font));
				table.addCell(new Phrase(list.get(i).getAadharNo(),font));
				table.addCell(new Phrase(list.get(i).getBloodGroup(),font));
				table.addCell(new Phrase(list.get(i).getDobString(),font));
				table.addCell(new Phrase(list.get(i).getCategory(),font));
				table.addCell(new Phrase(list.get(i).getCurrentAddress(),font));
				table.addCell(new Phrase(list.get(i).getConcessionName(),font));
				table.addCell(new Phrase(list.get(i).getTransportRoute(),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getFathersPhone()),font));
				table.addCell(new Phrase(String.valueOf(list.get(i).getMothersPhone()),font));
				table.addCell(new Phrase(list.get(i).getPhone(),font));
				table.addCell(new Phrase(list.get(i).getPhone1(),font));
				table.addCell(new Phrase(list.get(i).getFatherEmail(),font));
				table.addCell(new Phrase(list.get(i).getFatherAnnIncome(),font));
				table.addCell(new Phrase(list.get(i).getMotherAnnIncome(),font));







			}


			table.setWidthPercentage(110);
			document.add(table);





		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Student_Details_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Student_Details_Report.pdf").stream(()->isFromFirstData).build();
      //  // // System.out.println("file"+file.getContentLength());





	}




	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
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
	public StreamedContent getFile() {
		return file;
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}


}
