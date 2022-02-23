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

import session_work.QueryConstants;
import student_module.RegistrationColumnName;

@ManagedBean(name = "studentBirthday")
@ViewScoped
public class StudentBirthdayListBean implements Serializable {
	Date date,endDate;
	ArrayList<StudentInfo> studentList;
	String status, template, birthdayWish, errorLabel, temp1, temp2, temp3, temp4, bdyPreview, name, schoolName,userType,balMsg;
	boolean showWishAll;
	StudentInfo selectedStaff;
	double smsLimit;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public StudentBirthdayListBean() {
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);

		userType = (String) httpSession.getAttribute("type");
		schoolName = ls.getSmsSchoolName();
		date = new Date();
		endDate = new Date();

		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		searchData();

		temp1 = "Wishing You All Great Things In Life, Hope This Day Will Bring You An Extra Share Of All That Makes You Happiest. Happy Birthday!!";
		temp2 = "Wish You A Very Happy Birthday. May Life Lead You To Great Happiness, And Success. Enjoy Your Day!!";
		temp3 = "A Prayer To Bless You, A Wish To Lighten Your Moments, A Text To Say Happy Birthday!! God Bless U..";
		//temp4="Every B'day Presents A New Page In Ur Life, Keep Doing Good Things And Filling That Page With Good Deeds. Happy Birthday!!";
		temp4="Wish you all great things in life. May you live a long meaningful & blessful life.";
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void templateMsg()
	{
		birthdayWish = template;
	}

	public void searchData() {
		Connection conn = DataBaseConnection.javaConnection();

		if(endDate.before(date))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Enter Dates Properly"));
		}
		

		String schid=new DatabaseMethods1().schoolId();
		String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
		ArrayList<String> list=columnFieldList();
		studentList=new DataBaseMethodStudent().studentDetail("","","",QueryConstants.IN_SCHOOL,QueryConstants.BIRTHDAY,date,endDate,QueryConstants.IMAGE_WITH_PATH,"","","", session, schid, list, conn);
		if(studentList.isEmpty())
		{
			showWishAll=false;
		}
		else
		{
			showWishAll=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public ArrayList<String> columnFieldList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(RegistrationColumnName.ID);
		list.add(RegistrationColumnName.SECTION_ID);
		list.add(RegistrationColumnName.ADMISSION_NUMBER);
		list.add(RegistrationColumnName.DOB);
		list.add(RegistrationColumnName.STUDENT_IMAGE_PATH);
		list.add(RegistrationColumnName.STUDENT_NAME);
		list.add(RegistrationColumnName.FATHERS_NAME);
		list.add(RegistrationColumnName.SERIAL_NUMBER);
		list.add(RegistrationColumnName.FATHERS_PHONE);
		list.add(RegistrationColumnName.STUDENT_PHONE);
		list.add(RegistrationColumnName.MOTHERS_PHONE);
		
		return list;
	}

	public void allStudentMsz() {
		status = "all";
		template = birthdayWish = "";
		if (!studentList.isEmpty()) {
			PrimeFaces.current().executeInitScript("PF('birthdayDialog').show()");
			PrimeFaces.current().ajax().update(":birthdayForm");
		} else {
			errorLabel = "No Students to Wish.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
	}

	public void selectStudentMsz()
	{
		status = "individual";
		template = birthdayWish = "";
	}

	public String wishPreview()
	{
		Connection conn=DataBaseConnection.javaConnection();
		double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlg1').show()");
				PrimeFaces.current().ajax().update("MsgLimitForm1");
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

		if (template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if (!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + template + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}


		return "";

	}

	public void allBdMsg()
	{

		if (template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if (!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + template + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Student, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}

	}


	public void sendWish() throws IOException {
		PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevForm");
		String message = "", contactNumber = "";

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		if (status.equalsIgnoreCase("all"))
		{
			for (StudentInfo info : studentList)
			{
				String.valueOf(info.getId());
				if (String.valueOf(info.getFathersPhone()).length() == 10
						&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
						&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
						&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
						&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
						&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
				{
					name=info.getFname();
					message = "Dear " + name + "," + birthdayWish + " Regards," + schoolName;
					obj.messageurl1(String.valueOf(info.getFathersPhone()), message,info.getAddNumber(),conn,obj.schoolId(),"");
				}
			}
		}
		else
		{
			contactNumber = String.valueOf(selectedStaff.getFathersPhone());
			if (contactNumber.length() == 10
					&& !contactNumber.equals("2222222222")
					&& !contactNumber.equals("9999999999")
					&& !contactNumber.equals("1111111111")
					&& !contactNumber.equals("1234567890")
					&& !contactNumber.equals("0123456789"))
			{
				message = "Dear " + selectedStaff.getFname() + "," + birthdayWish + " Regards," + schoolName;
				obj.messageurl1(contactNumber, message,selectedStaff.getAddNumber(),conn,obj.schoolId(),"");
			}

		}

		if (template.isEmpty() && birthdayWish.isEmpty()) {
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{
			////// // System.out.println("hiiii");
			errorLabel="Message Sent";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().getExternalContext().redirect("studentBirthdayList.xhtml");
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public  void exportBirthPdf() throws DocumentException, IOException, FileNotFoundException {

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
				// TODO: handle exception
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			} 
			 

			Chunk c1 = new Chunk("              "+ schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

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
			Chunk c8 = new Chunk("\n                                                              Student Birthday Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);






			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");


			table.addCell("Student Name");
			table.addCell("Date of birth");
			table.addCell("Class");





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
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFname()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getDobStr()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getClassName()),font));






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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Student_BirthDay_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Student_Birthday_Report.pdf").stream(()->isFromFirstData).build();






	}




	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getBirthdayWish() {
		return birthdayWish;
	}

	public void setBirthdayWish(String birthdayWish) {
		this.birthdayWish = birthdayWish;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public String getTemp1() {
		return temp1;
	}

	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}

	public String getTemp2() {
		return temp2;
	}

	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}

	public String getTemp3() {
		return temp3;
	}

	public void setTemp3(String temp3) {
		this.temp3 = temp3;
	}

	public String getTemp4() {
		return temp4;
	}

	public void setTemp4(String temp4) {
		this.temp4 = temp4;
	}

	public String getBdyPreview() {
		return bdyPreview;
	}

	public void setBdyPreview(String bdyPreview) {
		this.bdyPreview = bdyPreview;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public boolean isShowWishAll() {
		return showWishAll;
	}

	public void setShowWishAll(boolean showWishAll) {
		this.showWishAll = showWishAll;
	}

	public StudentInfo getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(StudentInfo selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	

}
