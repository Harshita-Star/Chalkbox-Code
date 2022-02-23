package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

import org.primefaces.PrimeFaces;
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

@ManagedBean(name="newRegReport")
@ViewScoped
public class NewRegistrationReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList,selectedList = new ArrayList<>();
	int totalStudent;
	Date startDate,endDate;
	String balMsg,userType,typeMessage;
	double smsLimit;
	StudentInfo studentInfo;
	DatabaseMethods1 obj=new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String schoolId,session;
	String selectedCLassSection,selectedSection, username;
	ArrayList<SelectItem> sectionList,classSection;

	public  NewRegistrationReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		session=obj.selectedSessionDetails(schoolId,conn);
		smsLimit = obj.smsLimitReminder(schoolId, conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		username=(String) httpSession.getAttribute("username");
		selectedList = new ArrayList<>();
		studentList = new ArrayList<>();
		schoolDetails =obj.fullSchoolInfo(conn);
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
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection = obj.cordinatorClassList(empid, schoolId, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
				classSection=obj.allClassListForClassTeacher(empid,schoolId,conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		selectedCLassSection = "";
		selectedSection = "";

		searchAllReport();
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
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			sectionList=obj.allSectionListForClassTeacher(empid,selectedCLassSection,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void searchAllReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<String> stdColList=makeStdColumnList();
		studentList =new DataBaseMethodStudent().studentDetail("", selectedSection, selectedCLassSection, QueryConstants.BY_CLASS_SECTION, QueryConstants.NEW_REGISTRATION, startDate,endDate,QueryConstants.IMAGE_WITH_PATH, QueryConstants.KEYWORD, "", "", session, schoolId,stdColList, conn);
		
		totalStudent=studentList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printStudentDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=obj.fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("addNumber",studentInfo.getAddNumber());
		PrimeFaces.current().executeInitScript("window.open('printNewRegistrationInfo.xhtml')");
		if(info.getBranch_id().equalsIgnoreCase("22") || info.getBranch_id().equalsIgnoreCase("27"))
		{
			PrimeFaces.current().executeInitScript("window.open('printWelcomeLetter.xhtml')");
		}


	}

	public String sendMessage() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		double balance = obj.smsBalance(schoolId, conn);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
				PrimeFaces.current().ajax().update("MsgLimitForm");
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}
		else if(balance <= 0)
		{
			balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
				PrimeFaces.current().ajax().update("MsgOverForm");
			}
			else
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

				PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
				PrimeFaces.current().ajax().update("MsgOtherForm");
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
		else
		{
			sendMsg();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedList.size()>0)
		{
			if(typeMessage.trim().equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Write Your Message First Then Try To Send Again."));
			}
			else
			{
				SchoolInfoList info = obj.fullSchoolInfo(conn);
				String message="";
				String msg="";
				for(StudentInfo list : selectedList)
				{

					if(String.valueOf(list.getFathersPhone()).length()==10
							&& !String.valueOf(list.getFathersPhone()).equals("9999999999")
							&& !String.valueOf(list.getFathersPhone()).equals("1111111111")
							&& !String.valueOf(list.getFathersPhone()).equals("1234567890")
							&& !String.valueOf(list.getFathersPhone()).equals("0123456789"))
					{
						message= "Dear Parent,\n"+typeMessage+"\nRegards,\n"+info.getSmsSchoolName();

						msg=message;
						obj.messageurl1(String.valueOf(list.getFathersPhone()), msg,list.getAddNumber(),conn,schoolId,"");

					}
				}
				message="";

				FacesContext fc=FacesContext.getCurrentInstance();
				if(selectedList.size()>0)
				{
					fc.addMessage(null, new FacesMessage("Message Sent to "+selectedList.size()+" Students"));
				}
				selectedList.clear();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Student is Selected,Select Atleast One", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public  void exportnewPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);


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
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                             New Registration Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n Total :"+totalStudent+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			p9.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Admission No.");

			table.addCell("Admission Date");
			table.addCell(" Name");
			table.addCell("Father's Name");
			table.addCell("Address");
			table.addCell("Contact No.");
			table.addCell("Class");
			table.addCell("Section");
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
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(),font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionDate(),font));
				table.addCell(new Phrase(studentList.get(i).getFname(),font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(),font));
				table.addCell(new Phrase(studentList.get(i).getCurrentAddress(),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				table.addCell(new Phrase(studentList.get(i).getClassName(),font));
				table.addCell(new Phrase(studentList.get(i).getSectionName(),font));
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","New_Registration_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("New_Registration_Report.pdf").stream(()->isFromFirstData).build();

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public double getSmsLimit() {
		return smsLimit;
	}

	public void setSmsLimit(double smsLimit) {
		this.smsLimit = smsLimit;
	}



	public String getTypeMessage() {
		return typeMessage;
	}

	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}

	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
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

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
