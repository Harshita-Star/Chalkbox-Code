
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
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name = "stdPromoteStatus")
@ViewScoped
public class StudentPromotionStatusReport implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList;
	ArrayList<StudentInfo> selectedStudentList;
	int totalStudent;
	boolean shwReport = false,preClass=false,postCLass=false;;
	StudentInfo studentInfo;
	ArrayList<SelectItem> sessionList;
	String selectedSession, showSession;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	String selectedCLassSection, selectedSection, schid, username, userType;
	ArrayList<SelectItem> sectionList, classSection;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports obj = new DataBaseMethodsReports();

	public StudentPromotionStatusReport() {
		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolDetails = dbm.fullSchoolInfo(conn);
		selectedSession = dbm.selectedSessionDetails(schid, conn);
		try {
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

				ArrayList<SelectItem> temp = dbm.allClass(conn);

				if(temp.size()>0)
				{
					classSection.addAll(temp);
				}
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
						|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection = dbm.cordinatorClassList(empid, schid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection=dbm.allClassListForClassTeacher(empid,schid,conn);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void showPromoted() {
		Connection conn = DataBaseConnection.javaConnection();

		if (selectedCLassSection.equals("0") && selectedSection.equals("0")) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Any Class and Section", ""));

		} else if (selectedSection.equals("0")) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Section", ""));

		} else {
			ArrayList<String> list = basicFieldsForStudentList();

			studentList = new DataBaseMethodStudent().studentDetail("", selectedSection, selectedCLassSection,
					QueryConstants.BY_CLASS_SECTION, QueryConstants.PROMOTED_STUDENTS, null, null, "",
					QueryConstants.KEYWORD, "", "", selectedSession, schid, list, conn);
			totalStudent = studentList.size();
			if (totalStudent > 0) {
				shwReport = true;
				postCLass = false;
				preClass = true;
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"There is not Promoted Students in this class-section", ""));
				shwReport = false;
				postCLass = false;
				preClass = false;
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showNotPromoted() {
		Connection conn = DataBaseConnection.javaConnection();

		if (selectedCLassSection.equals("0") && selectedSection.equals("0")) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Any Class and Section", ""));

		} else if (selectedSection.equals("0")) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Section", ""));

		} else {
			ArrayList<String> list = basicFieldsForStudentFields();
			studentList = new DataBaseMethodStudent().studentDetail("", selectedSection, selectedCLassSection,
					QueryConstants.BY_CLASS_SECTION, QueryConstants.NOT_PROMOTEDSTUDENTS, null, null, "",
					QueryConstants.KEYWORD, "", "", selectedSession, schid, list, conn);
			totalStudent = studentList.size();
			if (totalStudent > 0) {
				shwReport = true;
				postCLass = true;
				preClass = false;
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"There is not Promoted Students in this class-section", ""));
				shwReport = false;
				postCLass = false;
				preClass = false;
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
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

			ArrayList<SelectItem> temp = dbm.allSection(selectedCLassSection,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=dbm.allSectionListForClassTeacher(empid,selectedCLassSection,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public void exportPromPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = dbm.fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Header
		try {

			String src = ls.getDownloadpath() + ls.getImagePath();
			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Chunk c = new Chunk(schoolDetails.schoolName + "\n", fo);

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Chunk c1 = new Chunk(
					"              " + schoolDetails.add1 + " " + schoolDetails.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                              Promoted Students Report\n\n", fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Class From");

			table.addCell("Class To");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < studentList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()), font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(), font));
				table.addCell(new Phrase(studentList.get(i).getFname(), font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(), font));
				table.addCell(new Phrase(studentList.get(i).getClassName(), font));
				table.addCell(new Phrase(studentList.get(i).getAdmitClass(), font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Promoted_Students_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Promoted_Student_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}
	}

	public ArrayList<String> basicFieldsForStudentList() {
		ArrayList<String> list = new ArrayList<String>();
		String keyword = QueryConstants.KEYWORD;
		list.add(keyword + RegistrationColumnName.ID);
		list.add(keyword + RegistrationColumnName.ADMISSION_NUMBER);
		list.add(keyword + RegistrationColumnName.SERIAL_NUMBER);
		list.add(keyword + RegistrationColumnName.FATHERS_NAME);
		list.add(keyword + RegistrationColumnName.FATHERS_PHONE);
		list.add(keyword + RegistrationColumnName.SECTION_ID);
		list.add(RegistrationColumnName.CLASS_FROM);
		list.add(RegistrationColumnName.CLASS_TO);
		list.add(keyword + RegistrationColumnName.SESSION);
		list.add(keyword + RegistrationColumnName.STUDENT_NAME);
		return list;
	}
	
	public ArrayList<String> basicFieldsForStudentFields() {
		ArrayList<String> list = new ArrayList<String>();
		String keyword = QueryConstants.KEYWORD;
		list.add(keyword + RegistrationColumnName.ID);
		list.add(keyword + RegistrationColumnName.ADMISSION_NUMBER);
		list.add(keyword + RegistrationColumnName.SERIAL_NUMBER);
		list.add(keyword + RegistrationColumnName.FATHERS_PHONE);
		list.add(keyword + RegistrationColumnName.FATHERS_NAME);
		list.add(keyword + RegistrationColumnName.SECTION_ID);
		list.add(keyword + RegistrationColumnName.SESSION);
		list.add(keyword + RegistrationColumnName.STUDENT_NAME);
		list.add(keyword + RegistrationColumnName.ADMISSION_DATE);
		return list;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public int getTotalStudent() {
		return totalStudent;
	}

	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public String getSelectedSession() {
		return selectedSession;
	}

	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}

	public String getShowSession() {
		return showSession;
	}

	public void setShowSession(String showSession) {
		this.showSession = showSession;
	}

	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
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

	public boolean isShwReport() {
		return shwReport;
	}

	public void setShwReport(boolean shwReport) {
		this.shwReport = shwReport;
	}

	public boolean isPreClass() {
		return preClass;
	}

	public void setPreClass(boolean preClass) {
		this.preClass = preClass;
	}

	public boolean isPostCLass() {
		return postCLass;
	}

	public void setPostCLass(boolean postCLass) {
		this.postCLass = postCLass;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}
