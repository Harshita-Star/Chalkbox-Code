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

@ManagedBean(name="noRFIDPunchReport")
@ViewScoped

public class NoRFIDPunchReportBean implements Serializable
{
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass;
	String selectedSection;
	String className, section,dateStr, username, userType;
	SchoolInfoList schoolDetails;
	ArrayList<StudentInfo> slist;
	transient StreamedContent file;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,session;
	DatabaseMethods1 obj=new DatabaseMethods1();


	
	public NoRFIDPunchReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		session=obj.selectedSessionDetails(schoolId,conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		session=obj.selectedSessionDetails(schoolId,conn);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			classList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList = obj.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolId,conn);
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
			si.setValue("all");
			sectionList.add(si);

			ArrayList<SelectItem> temp = obj.allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		schoolDetails =obj.fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showRFID()
	{
		Connection conn = DataBaseConnection.javaConnection();
	
		
		ArrayList<String> list=new DataBaseMethodStudent().basicFieldsForStudentList();
		slist.addAll(new DataBaseMethodStudent().studentDetail("",selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION,QueryConstants.NO_RFID_PUNCH_TRANSPORT, null, null,"","","","", session, schoolId, list, conn));
		slist.addAll(new DataBaseMethodStudent().studentDetail("",selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION,QueryConstants.NO_RFID_PUNCH_NO_TRANSPORT, null, null,"","","","", session, schoolId, list, conn));
		if(selectedClass.equalsIgnoreCase("All"))
		{
			className = "all";
		}
		else
		{
			className =obj.classNameFromidSchid(schoolId, selectedClass, session, conn);
		}
		
		if(selectedSection.equalsIgnoreCase("all"))
		{
			section = "All";
		}
		else
		{
			section = obj.sectionNameByIdSchid(schoolId, selectedSection, conn);
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public  void exportLibPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);

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

			Chunk c = new Chunk(ls.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  ls.add1+ " " +ls.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();
			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);

			Chunk c8 = new Chunk("\n                                                           No RFID Punch Report "+className+" "+section+" \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(8);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Adm.No.");
			table.addCell("Class");
			table.addCell("Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");
			table.addCell("Transport Status");
			table.addCell("RFID No.");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<slist.size();i++)
			{
				table.addCell(new Phrase(String.valueOf(i+1),font));
				table.addCell(new Phrase(slist.get(i).getSrNo(),font));
				table.addCell(new Phrase(slist.get(i).getClassName()+" - "+slist.get(i).getSectionName(),font));
				table.addCell(new Phrase(slist.get(i).getFullName(),font));
				table.addCell(new Phrase(slist.get(i).getFathersName(),font));
				table.addCell(new Phrase(slist.get(i).getMotherName(),font));
				table.addCell(new Phrase(slist.get(i).getTransStatus(),font));
				table.addCell(new Phrase(slist.get(i).getRfidNo(),font));
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","No_RFID_Punch_Report_"+className+"_"+section+".pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("No_RFID_Punch_Report_"+className+"_"+section+".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

	public ArrayList<StudentInfo> getSlist() {
		return slist;
	}

	public void setSlist(ArrayList<StudentInfo> slist) {
		this.slist = slist;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
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

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	
}
