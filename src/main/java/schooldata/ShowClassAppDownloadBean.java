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

import reports_module.DataBaseMethodsReports;
import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name="showClassAppDownloadBean")
@ViewScoped
public class ShowClassAppDownloadBean implements Serializable
{

	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section, total;
	ArrayList<ClassInfo> list;
	boolean b;
	String sectionName;
	ArrayList<StudentInfo> studentList;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;


	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		
//		// // System.out.println(schoolDetails.getSchoolName()+"dgesf");
		sectionList = new DatabaseMethods1().allSection(selectedClass, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ShowClassAppDownloadBean() {
		Connection conn = DataBaseConnection.javaConnection();
		
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		classList = new DatabaseMethods1().allClass(conn);
		selectedSection="-1";
		selectedClass="-1";
		getStudentStrength();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		String session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		if (selectedSection.equals("-1")) {
			studentList = new DataBaseMethodsReports().newRegistrationReportInSession(selectedClass,"all",conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = "All";
			sectionName = "All";
		} else {

			studentList = obj.getAllStudentStrentgth(selectedSection, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = obj.classNameFromidSchid(obj.schoolId(),selectedClass, session, conn);
			sectionName = obj.sectionNameByIdSchid(obj.schoolId(),selectedSection, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void InactiveStudentStrength() {
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMethodsReports objReport=new DataBaseMethodsReports();
		DatabaseMethods1 obj=new DatabaseMethods1();

		String session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
		ArrayList<String> stdColList=makeStdColumnList();
		studentList =new DataBaseMethodStudent().studentDetail("", selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION, QueryConstants.INACTIVE_STRENGTH, null, null,QueryConstants.IMAGE_WITH_PATH, QueryConstants.KEYWORD, "", "", session, obj.schoolId(),stdColList, conn);
		
		if (studentList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
			b = false;
		} else
			b = true;

		total = String.valueOf(studentList.size());
		if (selectedSection.equals("-1")) {
			className = "All";
			sectionName = "All";
		} else {

			className = obj.classNameFromidSchid(obj.schoolId(),selectedClass, session, conn);
			sectionName = obj.sectionNameByIdSchid(obj.schoolId(),selectedSection, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportAppDownloadPdf() throws DocumentException, IOException, FileNotFoundException {

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
			Image im = null ;
			try {
				im =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				 c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c1 = new Chunk(  "              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

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
			
		try 
	       {
			Chunk c8 = new Chunk("\n                                                         App Download Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);

			Chunk c7 = new Chunk("\n Class : "+className+"                                        Section : "+sectionName+"                                              Total : "+total+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("Sno");
			table.addCell("Sr. No.");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");
			table.addCell("Class");
			table.addCell("Address");
			table.addCell("Phone No.");
			table.addCell("App Download Status");


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
				table.addCell(new Phrase(studentList.get(i).srNo,font));

				table.addCell(new Phrase(studentList.get(i).getFullName()));
				table.addCell(new Phrase(studentList.get(i).getFathersName()));

				table.addCell(new Phrase(studentList.get(i).getMotherName()));
				table.addCell(new Phrase(studentList.get(i).getClassName()+"-"+studentList.get(i).getSectionName() ,font));

				table.addCell(new Phrase(studentList.get(i).getCurrentAddress()));
				table.addCell(new Phrase(studentList.get(i).getFathersPhone()));

				table.addCell(new Phrase(studentList.get(i).getApp_download()));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","App_Download_Status_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("App_Download_Status_Report.pdf").stream(()->isFromFirstData).build();






	}
	
	public ArrayList<String> makeStdColumnList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ADMISSION_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SERIAL_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SECTION_ID);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHERS_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.MOTHERS_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHERS_PHONE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ADMISSION_DATE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.DOB);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.CATEGORY_ID);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.GENDER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.CURRENT_ADDRESS);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.PERMANENT_ADDRESS);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.MOTHERS_PHONE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.AADHAR_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.BLOOD_GROUP);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.RELIGION);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.CASTE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.LAST_SCHOOL_NAME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHER_SCHOOL_EMP);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.MOTHER_SCHOOL_EMP);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.FATHER_INCOME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.MOTHER_INCOME);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.TRANSPORT_ROUTE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_TYPE);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ROLL_NUMBER);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_IMAGE_PATH);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.ADMISSION_REMARK);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.STUDENT_STATUS);
		list.add(QueryConstants.KEYWORD+RegistrationColumnName.SINGLE_CHILD);
		
		return list;
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

	public ArrayList<ClassInfo> getList() {
		return list;
	}

	public void setList(ArrayList<ClassInfo> list) {
		this.list = list;
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

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}



	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}




}
