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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import exam_module.DataBaseMethodsBLMExam;
import reports_module.DataBaseMethodsReports;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name = "SCPBean")
@SessionScoped
public class SeveralCertificatePrintingBean implements Serializable 
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String addNumber,date1,prefix1,prefix2, year, headerImage, session, website, studentName, startSession, endSession, affNo, board,heShe, cls,previousClass;
	StudentInfo studentInfo;
	String currentDate = new SimpleDateFormat("dd MMM yyyy").format(new Date());
	boolean showPrint, showBirth, showBank, showBonafied, showPic, showStatic, showCharacter, showAffiliation, showFee,showPassportApply, showLOC,showBirthnorthwood;
	SchoolInfoList schinfo;
	String sy[];
	ArrayList<FeeInfo> list, feeList;
	int totalPaidAmount;
	String checkGender="";
	transient StreamedContent file;
	String subject1,subject2,subject3,subject4,subject5,subject6,hisHer = "",totalAmountInWords,oldOrActive,remark,studentTypeCharacterCertificate;
	Date date=new Date();
	ArrayList<String> selectedQuarter=new ArrayList<>(),selectedFee=new ArrayList<>();
	ArrayList<SelectItem> classFeeList;
	String prefix,childOf,gender,monthName,fatherName,motherName,amountinWord,sessionString,principalNameSS,feeIncludeInCertificate;
	int totalAmount;
	ArrayList<FeeInfo> feeDetailList;
	boolean showCertificate,showCertificatePer=false;
	ArrayList<SelectItem> feelist;
	ArrayList<String> selectedFees = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		Connection conn = DataBaseConnection.javaConnection();

		schinfo = new DatabaseMethods1().fullSchoolInfo(conn);
		affNo = schinfo.getAdd4();
		board = schinfo.getBoardType().toUpperCase();
		studentName = "";
		classFeeList=new DataBaseMethodsReports().allFeeList(conn);
		selectedFee.clear();selectedQuarter.clear();
		
		feelist=new DatabaseMethods1().viewFeesList(conn);

		try {
			conn.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void dobInWords() {

		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Date dob = studentInfo.getDob();
		String date1 = "";
		if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
		{
		   date1 = transformSS(dob.getDate());
		}
		else
		{	
		   date1 = transform(dob.getDate());
		}
		String wordmonth = new DatabaseMethods1().monthNameByNumber(dob.getMonth() + 1);
		String year1 = year(dob.getYear() + 1900);
		String dobInWord = date1 + " " + wordmonth + " " + year1;
		studentInfo.setDobInWord(dobInWord);
	}

	String check = "";

	public ArrayList<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<StudentInfo> studentList = new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName", query, "full", conn, "", "");
//		ArrayList<StudentInfo> studentList = new DatabaseMethods1().searchStudentListForCertificate(query, conn);
		ArrayList<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList) {
			studentListt.add(info.getFname() + " / " + info.getFathersName() + " / " + info.getSrNo() + "-"
					+ info.getClassName() + "-" + info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void birthCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		String savePath = "";
		
		if (info.getProjecttype().equals("online")) {
			savePath = info.getDownloadpath();
		}
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" son";
				
				hisHer = "his"; 
				check = "His particulars as per school records are";
			} else {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" daughter";
				
				hisHer = "her";
				
				check = "Her particulars as per school records are";

			}
			dobInWords();
			if (studentInfo.getStudent_image() == null || studentInfo.getStudent_image().equals("")) {
				showPic = false;
				showStatic = true;
			} else {
				showPic = true;
				showStatic = false;
			}

			if(new DatabaseMethods1().schoolId().equals("4"))
			{
				showBirthnorthwood = showPrint = true;
				showBirth=false;
			}
			else
			{
				showBirth = showPrint = true;
				showBirthnorthwood=false;
					
			}
			showCertificatePer = showBonafied = showBank = showCharacter = showAffiliation = showPassportApply = showLOC = showFee  = false;

			headerImage = savePath + info.getMarksheetHeader();
			// //// // System.out.println(headerImage);
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			website = info.getWebsite();

			
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{
				currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
					
			  if(studentInfo.getStatus().equalsIgnoreCase("ACTIVE"))
			  {
				  oldOrActive = "is";
			  }
			  else
			  {
				  oldOrActive = "was";
			  }
			  principalNameSS = "";
			  if(info.getBranch_id().equalsIgnoreCase("99"))
			  {
				  principalNameSS = "Dr. (Mrs.) Babita Agarwal";
			  }
			   
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
			
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void bankCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			showBank = showPrint = true;
			showCertificatePer = showBonafied = showBirth = showBirthnorthwood = showCharacter = showAffiliation = showPassportApply = showLOC = showFee = false;
			dobInWords();
			
			
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" son";
				check = "His particulars as per school records are";
			} else {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" daughter";
				check = "Her particulars as per school records are";

			}
			
			
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			website = info.getWebsite();
			
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void bonafiedCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			showBonafied = showPrint = true;
			showCertificatePer = showBank = showBirth=showBirthnorthwood = showCharacter = showAffiliation = showPassportApply = showLOC = showFee = false;
			startSession = studentInfo.getStartingDateStr().split("/")[2];
			endSession = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn).split("-")[1];
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			website = info.getWebsite();
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{
			  currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
			} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void characterCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			showCharacter = showPrint = true;
			showCertificatePer = showBonafied = showBirth=showBirthnorthwood = showBank = showAffiliation = showPassportApply = showLOC = showFee = false;
			endSession = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn).split("-")[1];
			
			
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname());
				check = "His particulars as per school records are";
			} else {
				studentInfo.setFname(studentInfo.getFname());
				check = "Her particulars as per school records are";

			}
			dobInWords();
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			website = info.getWebsite();
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
				 studentInfo.setClassName(studentInfo.getClassName().split("-")[0]);
				  currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				principalNameSS = "";
				if(info.getBranch_id().equalsIgnoreCase("99"))
				{
				 principalNameSS = "Dr. (Mrs.) Babita Agarwal";
				}
				PrimeFaces.current().executeInitScript("PF('dlgrem').show()");
			}
			else
			{
				if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
				{	
				  studentInfo.setClassName(studentInfo.getClassName().split("-")[0]);
				  currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
				}
				else
				{
				  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

				}
			}
			
			
			
			
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void giveRemark()
	{
			
		  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
		
	}
	
	public void calculateFee()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

//		  if(feeIncludeInCertificate.equalsIgnoreCase("Tution Fee"))	
//		  {
//			  SchoolInfoList info = obj.fullSchoolInfo(conn);	
//				
//					ArrayList<FeeInfo> tempFeeList = new ArrayList<FeeInfo>();
//					for (FeeInfo ls : list) {
//						if(ls.getFeeId().equalsIgnoreCase("618")&&(info.getBranch_id().equalsIgnoreCase("99"))) {
//							tempFeeList.add(ls);
//							break;
//						}
//						if(ls.getFeeId().equalsIgnoreCase("684")&&(info.getBranch_id().equalsIgnoreCase("101"))) {
//							tempFeeList.add(ls);
//							break;
//						}
//					}
//					list = new ArrayList<FeeInfo>();
//					list = tempFeeList;
//			
//				
//			  
//		  }
		  
		ArrayList<FeeInfo> tempFeeList = new ArrayList<FeeInfo>();
		for(String fee : selectedFees) {
			for (FeeInfo ls : list) {
				
				if(fee.equals(ls.getFeeId()))
				{
			      tempFeeList.add(ls);		
				}
		   }
		}
		list=new ArrayList<>();
		list=tempFeeList;
		  int i = 1;
			for (FeeInfo ls : list) {
				int feepaidAmount = new DatabaseMethods1().FeePaidRecordForSS(new DatabaseMethods1().schoolId(), studentInfo,
						DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), ls.getFeeId(), conn);
				if (feepaidAmount > 0) {
					FeeInfo ns = new FeeInfo();
					ns.setFeeName(ls.getFeeName());
					ns.setPayAmount(feepaidAmount);
					ns.setSno(i);
					i++;
					feeList.add(ns);
				}
				totalPaidAmount = totalPaidAmount + feepaidAmount;

			}	
		  totalAmountInWords= obj.numberToWords(totalPaidAmount);
		  try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		   
		  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
		
		
		
//		
	}

	public void affiliationCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			showAffiliation = showPrint = true;
			showCertificatePer = showBonafied = showBirth=showBirthnorthwood = showCharacter = showBank = showPassportApply = showLOC = showFee = false;
			dobInWords();
			
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" son";
				check = "His particulars as per school records are";
			} else {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" daughter";
				check = "Her particulars as per school records are";

			}
			
		
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			website = info.getWebsite();

			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void feeCertificate() {
		
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		SchoolInfoList info = obj.fullSchoolInfo(conn);	
		prefix1 = "Mr.";
		prefix2 = "Mrs.";
	   if(info.getBranch_id().equalsIgnoreCase("19"))
	   {
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			sessionString="(01-04-"+session.substring(0,session.indexOf("-"))+" to 31-03-"+session.substring(session.indexOf("-")+1)+")";

			PrimeFaces.current().executeInitScript("PF('tutionAravali').show()");

	   }
	   else
	   {	
	
		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {

			feeList = new ArrayList<>();
			studentInfo= new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName", addNumber, "full", conn, "", "").get(0);
			//studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			totalPaidAmount = 0;
			list = obj.viewFeeList(conn);
			showPrint = true;
			showBonafied = showBirth=showBirthnorthwood = showCharacter = showBank = showAffiliation = showPassportApply = showLOC = false;
			
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" son";
				if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101")) {
					checkGender=" S/O";
//					prefix1 = "";
//					prefix2 = "";
				}
				hisHer = "him";
				check = "His particulars as per school records are";
			} else {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" daughter";
				if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101")) {
					checkGender=" D/O";
//					prefix1 = "";
//					prefix2 = "";
				}
				hisHer = "her";
				check = "Her particulars as per school records are";

			}
			
			
			
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
	
			startSession = session.split("-")[0];
			endSession = session.split("-")[1];
			website = info.getWebsite();
			if(info.getBranch_id().equalsIgnoreCase("22") || info.getBranch_id().equalsIgnoreCase("27"))
			{
				showCertificatePer=true;
				showFee=false;
				previousClass = new DBMethodsNew().previousClassbyAddNumber(addNumber,obj.schoolId(),session,conn);
				if(previousClass == null)
				{
					previousClass=studentInfo.getClassName();
				}
			}
			else
			{
				showCertificatePer=false;
				showFee=true;
			}
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
				  currentDate =  new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				  
				  principalNameSS = "";
				  if(info.getBranch_id().equalsIgnoreCase("99"))
				  {
					  principalNameSS = "Dr. (Mrs.) Babita Agarwal";
				  }
				  PrimeFaces.current().executeInitScript("PF('feeDlg').show()");			}
			else
			{
				int i = 1;
				for (FeeInfo ls : list) {
					int feepaidAmount = new DatabaseMethods1().FeePaidRecord(new DatabaseMethods1().schoolId(), studentInfo,
							DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), ls.getFeeId(), conn);
					if (feepaidAmount > 0) {
						FeeInfo ns = new FeeInfo();
						ns.setFeeName(ls.getFeeName());
						ns.setPayAmount(feepaidAmount);
						ns.setSno(i);
						i++;
						feeList.add(ns);
					}
					totalPaidAmount = totalPaidAmount + feepaidAmount;

				}	
			  totalAmountInWords= obj.numberToWords(totalPaidAmount);
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
			
			
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}
	   }
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void passportApplyCertificate() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			showPassportApply = showPrint = true;
			showCertificatePer = showBonafied = showBirth=showBirthnorthwood = showCharacter = showBank = showAffiliation = showLOC = showFee = false;
			dobInWords();
			

			
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" S/o";
				check = "His";
			} else {
				studentInfo.setFname(studentInfo.getFname() );
				checkGender=" D/o";
				check = "Her";

			}
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			endSession = session.split("-")[1];
			website = info.getWebsite();

			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void locCertificate() {
		DataBaseMethodsBLMExam objBLM= new DataBaseMethodsBLMExam();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
        subject1="";
        subject2="";
        subject3="";
        
        
        subject4="";subject5="";subject6="";
		int index = studentName.lastIndexOf("-") + 1;
		addNumber = studentName.substring(index);
		/*
		 * String tempSrNo=new DatabaseMethods1().tempAddNumberFromReal(addNumber,conn);
		 * studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(tempSrNo,conn);
		 * if(tempSrNo.equals("0")) { FacesContext.getCurrentInstance().addMessage(null,
		 * new FacesMessage("No Student Found With This Admission Number")); }
		 */
		if (index != 0) {
			studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), addNumber, conn);
			//studentInfo = obj.studentDetailslistByAddNo(obj.schoolId(),addNumber, conn);
			ArrayList<SubjectInfo>subjList = new ArrayList<>();
			ArrayList<SubjectInfo>manSubList=obj.manadatorySubjectListForStudent(studentInfo.getClassId(), session, studentInfo.getAddNumber(), studentInfo.getSectionid(), "scholastic", conn);
			ArrayList<SubjectInfo>optSubList=obj.optionalSubjectListForStudent(studentInfo.getClassId(), session, studentInfo.getAddNumber(), studentInfo.getSectionid(), "scholastic", conn);
			subjList.addAll(manSubList);
			subjList.addAll(optSubList);
			
			
			
			sy = new String[subjList.size()];
			
			for(int i=0;i<subjList.size();i++)
			{
				
				sy[i] = subjList.get(i).getSubName();
				
				
//				if(i==0)
//				{
//					subject1=subjList.get(i).getSubName();
//				}
//				else if(i==1)
//				{
//					subject2=subjList.get(i).getSubName();
//							
//				}
//				else if(i==2)
//				{
//					subject3=subjList.get(i).getSubName();
//					
//				}
//				else if(i==3)
//				{
//					subject4=subjList.get(i).getSubName();
//					
//				}
//				else if(i==4)
//				{
//					subject5=subjList.get(i).getSubName();
//					
//				}
//				else if(i==5) {
//					subject6 = subjList.get(i).getSubName();
//				}
				
				
			}
			
			
			cls = studentInfo.getClassName().split("-")[0];
			showLOC = showPrint = true;
			showCertificatePer = showBonafied = showBirth=showBirthnorthwood = showCharacter = showBank = showAffiliation = showPassportApply = showFee = false;
			dobInWords();
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" S/o");
				check = "His";
			} else {
				// studentInfo.setFname("Miss "+studentInfo.getFname()+" D/o");
				check = "Her";

			}
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String savePath = "";
			if (info.getProjecttype().equals("online")) {
				savePath = info.getDownloadpath();
			}
			headerImage = savePath + info.getMarksheetHeader();
			session = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn);
			endSession = session.split("-")[1];
			website = info.getWebsite();
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{	
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificatesSSChilren.xhtml')");
			}
			else
			{
			  PrimeFaces.current().executeInitScript("window.open('printSeveralCertificates.xhtml')");

			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String transform(int num) {
		String one[] = { " ", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
				" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
				" Nineteen" };
		String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
				" Ninety" };
		if (num <= 99) {
			if (num <= 19) {
				date1 = one[num];
			} else {
				int num1 = num / 10;
				int num2 = num % 10;
				date1 = ten[num1] + one[num2];
			}
		}
		return date1;
	}
	
	public String transformSS(int num) {
		String one[] = { " ", " First", " Second", " Third", " Fourth", " Fifth", " Sixth", " Seventh", " Eighth", " Ninth", " Tenth",
				" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
				" Nineteen" };
		String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
				" Ninety" };
		if (num <= 99) {
			if (num <= 19) {
				date1 = one[num];
			} else {
				int num1 = num / 10;
				int num2 = num % 10;
				date1 = ten[num1] + one[num2];
			}
		}
		return date1;
	}
	
	
	public void searchByName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int index=studentName.lastIndexOf("-")+1;
		addNumber=studentName.substring(index);
		if(index!=0)
		{
			studentInfo=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(), addNumber,conn);
			commonMethod();

			
			PrimeFaces.current().executeInitScript("window.open('printTutionFeeCertificate.xhtml')");
			PrimeFaces.current().executeInitScript("PF('tutionAravali').hide()");

			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void commonMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		totalAmount=0;
		showCertificate=true;
		feeDetailList=new ArrayList<>();
		ArrayList<FeeInfo> tempFeeList=new ArrayList<>();

		fatherName=studentInfo.getFathersName().replace("Mr.", "").trim();
		// // System.out.println(fatherName+" f");
		motherName=studentInfo.getMotherName().replace("Mrs.", "").trim();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("TRANSPORT",true);

		tempFeeList=DBM.feeListForStudentWithConcession(studentInfo.getClassId(),conn,studentInfo.getConcession());
		for(String fee:selectedFee)
		{
			for(FeeInfo tempFee:tempFeeList)
			{
				if(fee.equals(tempFee.getFeeId()))
				{
					feeDetailList.add(tempFee);
				}
			}
		}

		int size=selectedQuarter.size();
		for(FeeInfo fee:feeDetailList)
		{
			
			fee.setSchId(new DatabaseMethods1().schoolId());
		   
			if(!fee.getFeeName().equals("Transport"))
			{
				if(fee.getFeeType().equals("year"))
				{
					fee.setFeePeriod("1");
					if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
					{
						fee.setFeeAmount(fee.getOldfeeAmount());
					}
					fee.setPayAmount(fee.getFeeAmount()*Integer.parseInt(fee.getFeePeriod()));

				}
				else if(fee.getFeeType().equals("month"))
				{
					
					if(new DatabaseMethods1().schoolId().equals("287"))
					{
						String className=studentInfo.getClassName();
						
						if(className.contains("XI Sci")||(className.contains("XI Com"))||(className.contains("XI Art"))||className.contains("XII Sci")||(className.contains("XII Com"))||(className.contains("XII Art")))
						{
							if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
							{
								
								fee.setFeePeriod(String.valueOf(size*3));
								fee.setFeeAmount(3870);
								fee.setPayAmount(3870*Integer.parseInt(fee.getFeePeriod()));
								
							}
							else
							{
								fee.setFeePeriod(String.valueOf(size*3));
								fee.setFeeAmount(3950);
								fee.setPayAmount(3950*Integer.parseInt(fee.getFeePeriod()));
							}
						}
						else if(className.contains("IX")||className.contains("X"))
						{
							if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
							{
								fee.setFeePeriod(String.valueOf(size*3));
								fee.setFeeAmount(3060);
								fee.setPayAmount(3060*Integer.parseInt(fee.getFeePeriod()));
							}
							else
							{
								fee.setFeePeriod(String.valueOf(size*3));
								fee.setFeeAmount(3150);
								fee.setPayAmount(3150*Integer.parseInt(fee.getFeePeriod()));
							}
						}
						else
						{
							fee.setFeePeriod(String.valueOf(size*3));
							if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
							{
								fee.setFeeAmount(fee.getOldfeeAmount());
							}
							fee.setPayAmount(fee.getFeeAmount()*Integer.parseInt(fee.getFeePeriod()));
			
						}
						
				
					}
					else
					{
						fee.setFeePeriod(String.valueOf(size*3));
						if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
						{
							fee.setFeeAmount(fee.getOldfeeAmount());
						}
						fee.setPayAmount(fee.getFeeAmount()*Integer.parseInt(fee.getFeePeriod()));
		
					}
					
					
				
				}
				else if(fee.getFeeType().equals("quarter"))
				{
					fee.setFeePeriod(String.valueOf(size));
					if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
					{
						fee.setFeeAmount(fee.getOldfeeAmount());
					}
					fee.setPayAmount(fee.getFeeAmount()*Integer.parseInt(fee.getFeePeriod()));
					
					

				}
			}
			else
			{
				String routeId=DBM.presentRouteStatus(DBM.schoolId(),addNumber,conn);
				String className=studentInfo.getClassName();
				if( routeId==null ||routeId.equals("null") || routeId.equals(""))
				{
					fee.setFeeAmount(0);
					fee.setOldfeeAmount(0);
				}
				else
				{
					fee.setFeeAmount(DBM.routeFee(Integer.parseInt(routeId), session,conn));
					fee.setOldfeeAmount(DBM.routeFee(Integer.parseInt(routeId), session,conn));
				}

				if(selectedQuarter.contains("1"))
				{
					if(className.contains("IX") || className.contains("X") || className.contains("XI") || className.contains("XII"))
					{
						fee.setFeePeriod(String.valueOf(size*3));
					}
					else
					{
						fee.setFeePeriod(String.valueOf(size*3-1.5));
					}
					if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
					{
						fee.setFeeAmount(fee.getOldfeeAmount());
					}
					fee.setPayAmount((int) (fee.getFeeAmount()* Double.parseDouble(fee.getFeePeriod())));

				}
				else
				{
					fee.setFeePeriod(String.valueOf(size*3));
					if(studentInfo.getStudentStatus().equalsIgnoreCase("old"))
					{
						fee.setFeeAmount(fee.getOldfeeAmount());
					}
					fee.setPayAmount((int) (fee.getFeeAmount()* Double.parseDouble(fee.getFeePeriod())));

				}
			}
			totalAmount+= fee.getPayAmount();

		}
		amountinWord=DBM.numberToWords(totalAmount);
		amountinWord=amountinWord+" Rupees Only";
		currentDate=new SimpleDateFormat("dd-MM-yyyy").format(date);

		String sex=studentInfo.getGender();
		if(sex.equalsIgnoreCase("Male"))
		{
			gender="He";
			childOf="S/O";
			prefix="Master";
		}
		else if(sex.equalsIgnoreCase("Female"))
		{
			gender="She";
			childOf="D/O";
			prefix="Miss";
		}

		String sMonth="",eMonth="";
		
		if(new DatabaseMethods1().schoolId().equals("287"))
		{
			for(int i=0;i<selectedQuarter.size();i++)
			{
				sMonth="";eMonth="";
				int end= Integer.parseInt(selectedQuarter.get(i));
				int start=Integer.parseInt(selectedQuarter.get(i));
				if(start==1)
				{
					sMonth="APR "+session.substring(0,session.indexOf("-"));
				}
				else if(start==2)
				{
					sMonth="JUL "+session.substring(0,session.indexOf("-"));
				}
				else if(start==3)
				{
					sMonth="OCT "+session.substring(0,session.indexOf("-"));
				}
				else if(start==4)
				{
					sMonth="JAN "+session.substring(session.indexOf("-")+1);
				}

				if(end==1)
				{
					eMonth="JUN "+session.substring(0,session.indexOf("-"));
				}
				else if(end==2)
				{
					eMonth="SEP "+session.substring(0,session.indexOf("-"));
				}
				else if(end==3)
				{
					eMonth="DEC "+session.substring(0,session.indexOf("-"));
				}
				else if(end==4)
				{
					eMonth="MAR "+session.substring(session.indexOf("-")+1);
				}
				
				if(i==0)
				{
					monthName="("+sMonth+" - "+eMonth+")";
				}
				else
				{
					monthName=monthName+",("+sMonth+" - "+eMonth+")";
							
				}
			}
		}
		else
		{
			sortMonth();
			int end= Integer.parseInt(selectedQuarter.get(0));
			int start=Integer.parseInt(selectedQuarter.get(selectedQuarter.size()-1));
			if(start==1)
			{
				sMonth="APR "+session.substring(0,session.indexOf("-"));
			}
			else if(start==2)
			{
				sMonth="JUL "+session.substring(0,session.indexOf("-"));
			}
			else if(start==3)
			{
				sMonth="OCT "+session.substring(0,session.indexOf("-"));
			}
			else if(start==4)
			{
				sMonth="JAN "+session.substring(session.indexOf("-")+1);
			}

			if(end==1)
			{
				eMonth="JUN "+session.substring(0,session.indexOf("-"));
			}
			else if(end==2)
			{
				eMonth="SEP "+session.substring(0,session.indexOf("-"));
			}
			else if(end==3)
			{
				eMonth="DEC "+session.substring(0,session.indexOf("-"));
			}
			else if(end==4)
			{
				eMonth="MAR "+session.substring(session.indexOf("-")+1);
			}
			monthName=sMonth+" - "+eMonth;
		}
		
		

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void sortMonth()
	{
		int a=0,b=0;String temp=new String();
		for(int k=0;k<selectedQuarter.size();k++)
		{
			a=Integer.parseInt(selectedQuarter.get(k));
			for(int j=k+1;j<selectedQuarter.size();j++)
			{
				b=Integer.parseInt(selectedQuarter.get(j));
				if(a<b)
				{
					temp=selectedQuarter.get(k);
					selectedQuarter.set(k,selectedQuarter.get(j));
					selectedQuarter.set(j,temp);
				}
			}
		}
	}
	

	public SchoolInfoList getSchinfo() {
		return schinfo;
	}

	public void setSchinfo(SchoolInfoList schinfo) {
		this.schinfo = schinfo;
	}

	public String year(int num) {
		String one[] = { " ", " One ", " Two ", " Three ", " Four ", " Five ", " Six ", " Seven ", " Eight ", " Nine ",
				" Ten ", " Eleven ", " Twelve ", " Thirteen ", " Fourteen ", " Fifteen ", " Sixteen ", " Seventeen ",
				" Eighteen ", " Nineteen " };
		String ten[] = { " ", " ", " Twenty ", " Thirty ", " Forty ", " Fifty ", " Sixty ", "Seventy ", " Eighty ",
				" Ninety " };

		if (num >= 1000 && num <= 9999) {
			int num1 = num / 1000;
			int num2 = num % 1000;
			if (num2 <= 99) {
				if (num2 <= 19) {
					year = one[num1] + "Thousand" + one[num2];

				} else {
					int num4 = num2 / 10;
					int num5 = num2 % 10;
					year = one[num1] + "Thousand" + ten[num4] + one[num5];
				}
			} else {
				int num6 = num2 / 100;
				int num7 = num2 % 100;
				if (num7 <= 19) {
					String num8 = one[num7];
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + num8;
				} else {
					int num0 = num7 / 10;
					int num9 = num7 % 10;
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + ten[num0] + one[num9];
				}
			}
		}
		return year;
	}

	public  void exportBirthPdf() throws DocumentException, IOException, FileNotFoundException {
        
        Connection con=DataBaseConnection.javaConnection();
        SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);
      
    
      
         Document  document = new Document();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
         String home = System.getProperty("user.home");  
      
          String gender="",chec="",sd="";
          PdfWriter writer = PdfWriter.getInstance(document, baos);  
           document.open();
          
           PdfContentByte cb = writer.getDirectContent();
           cb.saveState();
       // cb.roundRectangle(30,30,536,790, 10);
           cb.rectangle(42,563,228,0);
           cb.rectangle(45,712,510,0);
           cb.rectangle(190,660,222,0);
           cb.stroke();
           cb.restoreState();
      
           if(studentInfo.gender.equalsIgnoreCase("male"))
           {
              // studentInfo.setFname("Master "+studentInfo.getFname()+" son");
           //    check="His particulars as per school records are";
               chec="His";
               gender="Master";
               sd="son";
           }
           else
           {
            //   studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
             //  check="Her particulars as per school records are";
               chec ="Her";
               gender="Miss";
               sd="daughter";
                  
           }
                                   
          
           String src;
           Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
           Font fomNor= new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
           Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
           Font fontAddress = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
           String[] det = studentName.split("/");
           //Header
           try {
                if(!ls.getMarksheetHeader().equalsIgnoreCase(""))
                {
                	src =ls.getDownloadpath()+ls.getMarksheetHeader();
                }
                else
                {
                	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
                }
                
               Image im =Image.getInstance(src);
               im.setAlignment(Element.ALIGN_CENTER);
      
             im.scaleAbsoluteHeight(120);
             im.scaleAbsoluteWidth(520);
            
        
   
           Chunk c = new Chunk("\n\n\n\n\n\n\n\n\n\n\n"+schinfo.schoolName  +"\n",fo );
          
           Chunk c3 = new Chunk(im, 0, -73);
      
           Chunk c1 = new Chunk(  schinfo.add1+ " " +schinfo.add2+"                \n\n",fo);
      
            Paragraph p1 = new Paragraph();

       
          
         //  // p1.add(c);
          //  // p1.add(c1);
            p1.add(c3);
           p1.setAlignment(Element.ALIGN_CENTER);
      
              document.add(p1);
           }
           catch (Exception e) {
			// TODO: handle exception
		}
              
              
           try 
           {
              Chunk c6 = new Chunk("\n\n\n\n\n BIRTH CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN",fo );
              Paragraph p4 = new Paragraph();
              p4.add(c6);
              p4.setAlignment(Element.ALIGN_CENTER);
              document.add(p4);
              
              Chunk c4 = new Chunk("\nThis is certified that "+gender,fomNor);
            
              Chunk c41 = new Chunk(" "+det[0]+" ",fom);
              Chunk c42 = new Chunk(sd+" of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "),fomNor);
              Chunk c43 = new Chunk(det[1],fom);
               Chunk c44 = new Chunk("and "+(ls.getCountry().equalsIgnoreCase("India") ? "Smt. " : "Mrs. "),fomNor);
               Chunk c45 = new Chunk(studentInfo.getMotherName(),fom);
               Chunk c46 = new Chunk(" is a bonafide student of Class ",fomNor);
               Chunk c47 = new Chunk(studentInfo.getClassName(),fom);
               Chunk c48 = new Chunk(" of our school in Session ",fomNor);
               Chunk c49 = new Chunk(session+".",fom);
              Paragraph p2 = new Paragraph();
              p2.add(c4);
              p2.add(c41);
              p2.add(c42);
              p2.add(c43);
              p2.add(c44);
              p2.add(c45);
              p2.add(c46);
              p2.add(c47);
              p2.add(c48);
              p2.add(c49);
              
              document.add(p2);
              
              Chunk c5 = new Chunk("\n\n "+chec+" Particulars as per school records are :",fo );
              Paragraph p3 = new Paragraph();
              p3.add(c5);
              document.add(p3);
              
              Chunk c7 = new Chunk("\n\nDate of birth :    ",fom );
              Chunk c71 = new Chunk(studentInfo.getDobString(),fomNor);
              Chunk c72 = new Chunk("\nAdmission No : ",fom);
              Chunk c73 = new Chunk(studentInfo.srNo,fomNor);
               Chunk c74 = new Chunk("\nAddress :          ",fom);
               Chunk c75 = new Chunk(studentInfo.getCurrentAddress(),fontAddress);
              Paragraph p5 = new Paragraph();
              p5.add(c7);
              p5.add(c71);
              p5.add(c72);
              p5.add(c73);
              p5.add(c74);
              p5.add(c75);
            
              document.add(p5);
            

              
              try {
            
              String srcc =studentInfo.getStudent_image();
             String ul ="";String[] sp;
                     if(srcc.contains(" "));
                     {
                     sp = srcc.split(" ");
                     for(int j=0;j<sp.length-1;j++) {
                     ul += sp[j]+"%20";
                     }
                     ul = ul+ sp[sp.length-1];
                     // //// // System.out.println(ul);
                     }
                     srcc = ul;
              //   //// // System.out.println(srcc);
                   Image imgh;
                
                   imgh =Image.getInstance(srcc);
      
                  // imgh.setAlignment(Element.ALIGN_RIGHT);
             imgh.scaleAbsoluteHeight(60);
                  imgh.scaleAbsoluteWidth(65);
                  Chunk c9  = new Chunk(imgh,10, 20);
                  Paragraph p17 = new Paragraph();
                  p17.setAlignment(Element.ALIGN_RIGHT);
                  p17.add(c9);
                  document.add(p17);
       } catch (Exception e) {
       // TODO: handle exception
       }
        
              
              Chunk c10 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : "+currentDate+"                                                                                                              Principal",fom);
              Paragraph p9 = new Paragraph();
              p9.add(c10);
              document.add(p9);
                
              chec ="";
              gender="";
              sd="";
              
           }catch (Exception e) {
          
               e.printStackTrace();
           }
           Paragraph p2 = new Paragraph("\n");
           document.add(p2);
          
        
              
               document.close();
    
               InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//               file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Birth_Certificate.pdf");
               file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Birthday_Certificate.pdf").stream(()->isFromFirstData).build();
              
            
              
               try {
                con.close();
            } catch (SQLException e1) {
                
                e1.printStackTrace();
            }
            
        
          
           }


public  void exportBankPdf() throws DocumentException, IOException, FileNotFoundException {
    
    Connection con=DataBaseConnection.javaConnection();
    SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);
  
  

  
     Document  document = new Document();
     ByteArrayOutputStream baos = new ByteArrayOutputStream();

     PdfWriter writer = PdfWriter.getInstance(document, baos);  
     document.open();
    
     PdfContentByte cb = writer.getDirectContent();
     cb.saveState();
 // cb.roundRectangle(30,30,536,790, 10);
     cb.rectangle(42,547,228,0);
     cb.rectangle(45,712,510,0);
     cb.rectangle(190,660,222,0);
     cb.stroke();
     cb.restoreState();
  
    
    
      
     Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
     Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
     Font fomNor= new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
       //Header
       try {
           
           String src = "";
           if(!ls.getMarksheetHeader().equalsIgnoreCase(""))
           {
           	src =ls.getDownloadpath()+ls.getMarksheetHeader();
           }
           else
           {
           	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
           }
           Image im =Image.getInstance(src);
           im.setAlignment(Element.ALIGN_CENTER);
  
           im.scaleAbsoluteHeight(120);
           im.scaleAbsoluteWidth(520);
        
          
           Chunk c = new Chunk(schinfo.schoolName  +"\n",fo );
        
           Chunk c3 = new Chunk(im, 0, -73);
    
           Chunk c1 = new Chunk(  schinfo.add1+ " " +schinfo.add2+"                \n\n",fo);
  
           Paragraph p1 = new Paragraph();

           p1.add(c3);
           p1.setAlignment(Element.ALIGN_CENTER);
  
           document.add(p1);
       }
       catch (Exception e) {
		// TODO: handle exception
	}
          
         try
         {
          String chec,gender="",sd="";String name = studentInfo.getFname();
          if(studentInfo.gender.equalsIgnoreCase("male"))
           {
           
                
               chec="His";
               gender="son";
               sd="Master";
           }
           else
           {
           
               chec ="Her";
               sd = "Miss";
            
               
               gender="daughter";
           }

          Chunk c6 = new Chunk("\n\n\n\n\n BANK CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN",fo );
          Paragraph p4 = new Paragraph();
          p4.add(c6);
          p4.setAlignment(Element.ALIGN_CENTER);
          document.add(p4);
          
          Chunk c4 = new Chunk("\nThis is certified that "+sd+" ",fomNor);
        
          Chunk c41 = new Chunk(name,fom);
          Chunk c42 = new Chunk(" "+gender+" of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "),fomNor);
          Chunk c43 = new Chunk(studentInfo.getFathersName(),fom);
           Chunk c44 = new Chunk(" regular student of ",fomNor);
           Chunk c45 = new Chunk(schinfo.schoolName,fom);
           Chunk c46 = new Chunk(" from ",fomNor);
           Chunk c47 = new Chunk(studentInfo.getStartingDateStr(),fom);
           Chunk c48 = new Chunk(" to ",fomNor);
           Chunk c49 = new Chunk(currentDate+".",fom);
           Chunk c50 = new Chunk(chec+" Mother's name is "+(ls.getCountry().equalsIgnoreCase("India") ? "Smt. " : "Mrs. "),fomNor);
           Chunk c51 = new Chunk(studentInfo.getMotherName(),fom);
           Chunk c52 = new Chunk(". He/She has appeared in Class ",fomNor);
           Chunk c53 = new Chunk(studentInfo.getClassName(),fom);
           Chunk c54 = new Chunk(" examination in the session ",fomNor);
           Chunk c55 = new Chunk(session+".",fom);
          Paragraph p2 = new Paragraph();
      
        
        
          p2.add(c4);
          p2.add(c41);
          p2.add(c42);
          p2.add(c43);
          p2.add(c44);
          p2.add(c45);
          p2.add(c46);
          p2.add(c47);
          p2.add(c48);
          p2.add(c49);
          p2.add(c50);
          p2.add(c51);
          p2.add(c52);
          p2.add(c53);
          p2.add(c54);
          p2.add(c55);
          document.add(p2);
          
          Chunk c5 = new Chunk("\n\n His Particulars as per school records are :",fo );
          Paragraph p3 = new Paragraph();
          p3.add(c5);
          document.add(p3);
          
        
          Chunk c7 = new Chunk("\n\nDate of birth :    ",fom );
          Chunk c71 = new Chunk(studentInfo.getDobString(),fomNor);
          Chunk c72 = new Chunk("\nIn Words :        ",fom);
          Chunk c73 = new Chunk(studentInfo.getDobInWord(),fomNor);
           Chunk c74 = new Chunk("\nAddress :         ",fom);
           Chunk c75 = new Chunk(studentInfo.getCurrentAddress(),fomNor);
          Paragraph p5 = new Paragraph();
          p5.add(c7);
          p5.add(c71);
          p5.add(c72);
          p5.add(c73);
          p5.add(c74);
          p5.add(c75);
        
          document.add(p5);
          
          Chunk c8 = new Chunk("\n\nHe/She bears a good moral Character." );
          Paragraph p6 = new Paragraph();
          p6.add(c8);
          document.add(p6);
          
          Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : "+currentDate+"                                                                                                                     Principal",fom);
          Paragraph p9 = new Paragraph();
          p9.add(c9);
          document.add(p9);
            
       } catch (Exception e) {
      
           e.printStackTrace();
       }
       Paragraph p2 = new Paragraph("\n");
       document.add(p2);
      
    
          
           document.close();

           InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//           file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Bank_Certificate.pdf");
           file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Bank_Certificate.pdf").stream(()->isFromFirstData).build();
          
           try {
               con.close();
           } catch (SQLException e1) {
               
               e1.printStackTrace();
           }

        
    
      
       }


public  void exportBonafide() throws DocumentException, IOException, FileNotFoundException {
  
   Connection con=DataBaseConnection.javaConnection();
   SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);
  
  

  
    Document  document = new Document();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    String home = System.getProperty("user.home");  

    PdfWriter writer = PdfWriter.getInstance(document, baos);  
    document.open();
    
    PdfContentByte cb = writer.getDirectContent();
    cb.saveState();
// cb.roundRectangle(30,30,536,790, 10);
  
    cb.rectangle(45,712,510,0);
    cb.rectangle(190,688,222,0);
    cb.stroke();
    cb.restoreState();
  
    
    
    
    Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
    Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    Font fomNor= new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
      //Header
      try {
     
         
          String src = "";
          if(!ls.getMarksheetHeader().equalsIgnoreCase(""))
          {
          	src =ls.getDownloadpath()+ls.getMarksheetHeader();
          }
          else
          {
          	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
          }
          Image im =Image.getInstance(src);
          im.setAlignment(Element.ALIGN_CENTER);
  
          im.scaleAbsoluteHeight(120);
          im.scaleAbsoluteWidth(520);
      
        
          Chunk c = new Chunk(schinfo.schoolName  +"\n",fo );
        
          Chunk c3 = new Chunk(im, 0, -74);
  
          Chunk c1 = new Chunk(  schinfo.add1+ " " +schinfo.add2+"                \n\n",fo);

          Paragraph p1 = new Paragraph();

          String[] det = studentName.split("/");
      
          p1.add(c3);
          p1.setAlignment(Element.ALIGN_CENTER);

           document.add(p1);
      }
      catch (Exception e) {
		// TODO: handle exception
	}
      
       try {
         Chunk c6 = new Chunk("\n\n\n\n\nTO WHOMSOEVER IT MAY CONCERN\n\n",fo );
         Paragraph p4 = new Paragraph();
         p4.add(c6);
         p4.setAlignment(Element.ALIGN_CENTER);
         document.add(p4);
        
         try {
        
          String srcc =studentInfo.getStudent_image();
          String ul ="";String[] sp;
             if(srcc.contains(" "));
             {
             sp = srcc.split(" ");
             for(int j=0;j<sp.length-1;j++) {
             ul += sp[j]+"%20";
             }
             ul = ul+ sp[sp.length-1];
             // //// // System.out.println(ul);
             }
             srcc = ul;
               // //// // System.out.println(studentInfo.getStudent_image());
                Image imgh =Image.getInstance(srcc);
                imgh.setAlignment(Element.ALIGN_CENTER);

              imgh.scaleAbsoluteHeight(60);
              imgh.scaleAbsoluteWidth(85);
              Chunk c9 = new Chunk(imgh, 235, -50);
               Paragraph p9 = new Paragraph();
               p9.add(c9);
               document.add(p9);
              
} catch (Exception e) {
//TODO: handle exception
}
    
         if(studentInfo.gender.equalsIgnoreCase("male"))
          {
            //  studentInfo.setFname("Master "+studentInfo.getFname()+" son");
            //  check="His particulars as per school records are";
          }
          else
          {
            //  studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
             // check="Her particulars as per school records are";
                  
          }
        
         Chunk c4 = new Chunk("\n\n\n\n\nThis is to certify that ",fomNor);
      
        
         Chunk c41 = new Chunk(studentInfo.getFname(),fom);
         Chunk c42 = new Chunk(" son / daughter of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "),fomNor);
         Chunk c43 = new Chunk(studentInfo.getFathersName(),fom);
          Chunk c44 = new Chunk(", whose Adm. No. is ",fomNor);
          Chunk c45 = new Chunk(studentInfo.getSrNo(),fom);
          Chunk c46 = new Chunk(" and date of birth is ",fomNor);
          Chunk c47 = new Chunk(studentInfo.getDobString(),fom);
          Chunk c48 = new Chunk(" is a bonafide student of Class ",fomNor);
          Chunk c49 = new Chunk(studentInfo.getClassName(),fom);
          Chunk c50 = new Chunk(" of our school, for the session of ",fomNor);
          Chunk c51 = new Chunk(session+".",fom);
        
         Paragraph p2 = new Paragraph();
      
      
      
         p2.add(c4);
         p2.add(c41);
         p2.add(c42);
         p2.add(c43);
         p2.add(c44);
         p2.add(c45);
         p2.add(c46);
         p2.add(c47);
         p2.add(c48);
         p2.add(c49);
         p2.add(c50);
         p2.add(c51);
      
        
         document.add(p2);
        
         Chunk c10 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : "+currentDate+"\nPlace : "+(ls.getBranch_id().equalsIgnoreCase("22") ? "Tularampur" : (ls.getBranch_id().equalsIgnoreCase("27") ? "Halduchaur" : ""))+"                                                                                                              Principal",fom);
         Paragraph p10 = new Paragraph();
         p10.add(c10);
         document.add(p10);
        
      
          
      } catch (Exception e) {
    
          e.printStackTrace();
      }
      Paragraph p2 = new Paragraph("\n");
      document.add(p2);
    
    
        
          document.close();

          InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//          file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Bonafide_Certificate.pdf");
          file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Bonafied_Certificate.pdf").stream(()->isFromFirstData).build();
          
          try {
              con.close();
          } catch (SQLException e1) {
              
              e1.printStackTrace();
          }

        
  
    
      }



	public void exportCharacterPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);

		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 659, 222, 0);
		cb.stroke();
		cb.restoreState();

		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		// Header
		try {
			
			String src = "";
			 if(!ls.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =ls.getDownloadpath()+ls.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(120);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -73);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			// String[] det1 = studentName.split("/");
			String chec, gender,sd="";
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
				chec = "he";
				gender = "He";
				sd="Master";
			} else {
				// studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
				chec = "she";
				gender = "She";
				sd="Miss";
			}

			Chunk c6 = new Chunk("\n\n\n\n\n CHARACTER CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN", fo);
			Paragraph p4 = new Paragraph();
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);

			Chunk c4 = new Chunk("\nThis is to certify that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();
			Chunk c41 = new Chunk(studentInfo.getFname(), fom);
			Chunk c42 = new Chunk(" "+(studentInfo.getGender().equalsIgnoreCase("Male") ? "son" : "daughter")+" of Mr. ", fomNor);
			Chunk c43 = new Chunk(studentInfo.getFathersName(), fom);
//			Chunk c44 = new Chunk(" resident of ", fomNor);
//			Chunk c45 = new Chunk(studentInfo.getCurrentAddress(), fom);
			Chunk c46 = new Chunk(" has passed ", fomNor);
			Chunk c47 = new Chunk(studentInfo.getClassName(), fom);
			Chunk c48 = new Chunk(" examination in the year ", fomNor);
			Chunk c49 = new Chunk(endSession, fom);
			Chunk c50 = new Chunk(
					" from this institution. To the best of my knowledge "+(studentInfo.getGender().equalsIgnoreCase("Male") ? "he" : "she")+" bears a good moral character and has normal behavioural pattern. "+(studentInfo.getGender().equalsIgnoreCase("Male") ? "He" : "She")+" has not displayed persistent violent or aggressive behaviour or any desire to harm others. ",
					fomNor);
			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
//			p2.add(c44);
//			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c48);
			p2.add(c49);
			p2.add(c50);

			document.add(p2);

			Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : " + currentDate
					+ "                                                                                                                         Principal",
					fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);

			chec = "";
			gender = "";

		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Character_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Character_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public void exportCharacterPdf2() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		schinfo = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);

		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 659, 222, 0);
		cb.stroke();
		cb.restoreState();

		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		// Header
		try {
			
			String src = "";
			 if(!schinfo.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =schinfo.getDownloadpath()+schinfo.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(120);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -78);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		try {
			// String[] det1 = studentName.split("/");
			String chec, gender1,sd = "",sondg="",vrb="", opt="";
			if (studentInfo.getGender().equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
				chec = "he";
				gender1 = "He";
				sd = "Master";
				sondg="son";
				vrb="him";
				opt="his";
			} else if (studentInfo.getGender().equalsIgnoreCase("female")) {
				// studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
				chec = "she";
				gender1 = "She";
				sd = "Miss";
				sondg="daughter";
				vrb="her";
				opt="her";
			}
			else
			{
				chec = "he/she";
				gender1 = "He/She";
				sd = "Ma./Ms.";
				sondg="son/daughter";
				vrb = "him/her";
				opt="his/her";
			}

			Chunk c6 = new Chunk("\n\n\n\n\n CHARACTER CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN", fo);
			Paragraph p4 = new Paragraph();
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);

			Chunk c4 = new Chunk("\nThis is to certify that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();
			Chunk c41 = new Chunk(studentInfo.getFname(), fom);
			Chunk c42 = new Chunk(" "+sondg+" of Mr. ", fomNor);
			Chunk c43 = new Chunk(studentInfo.getFathersName(), fom);
			Chunk c44 = new Chunk(" (Father) and Mrs. ", fomNor);
			Chunk c45 = new Chunk(studentInfo.getMotherName(), fom);
			Chunk c46 = new Chunk("(Mother) is studying in class ", fomNor);
			Chunk c47 = new Chunk(studentInfo.getClassToName(), fom);
			Chunk c471 = new Chunk(" with Admission No. ", fomNor);
			Chunk c472 = new Chunk(studentInfo.getSrNo(), fom);
			Chunk c48 = new Chunk(" in session ", fomNor);
			Chunk c49 = new Chunk(session, fom);
			Chunk c491 = new Chunk(".\n\n\n\nDuring "+opt+" academic period from ", fomNor);
			Chunk c492 = new Chunk(studentInfo.getStartingDateStr(), fom);
			Chunk c493 = new Chunk(" till today", fom);
			//Chunk c494 = new Chunk(removalDate, fom);
			Chunk c50 = new Chunk(
					" , "+chec+" bears a good moral character and has normal behavioural pattern. "+gender1+" has not displayed persistent violent or aggressive behaviour or any desire to harm others. ",
					fomNor);
			Chunk c51 = new Chunk(
					"\n\nI wish "+vrb+" a great future ahead.", fomNor);
			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
			p2.add(c44);
			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c471);
			p2.add(c472);
			p2.add(c48);
			p2.add(c49);
			p2.add(c491);
			p2.add(c492);
			p2.add(c493);
			//p2.add(c494);
			p2.add(c50);
			p2.add(c51);

			document.add(p2);

			Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : " + currentDate
					+ "                                                                                                                         Principal",
					fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			
			Chunk c10 = new Chunk("\nPlace : " + schinfo.add2
					+ "                                                                                                                         ",
					fom);
			Paragraph p10 = new Paragraph();
			p10.add(c10);
			document.add(p10);

			chec = "";
			gender1 = "";

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Character_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Character_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}
	
	public void exportAffiliationPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);
		// cb.rectangle(42,556,228,0);
		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 660, 222, 0);
		cb.stroke();
		cb.restoreState();

		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		// Header
		try {
			
			String src = "";
			 if(!schinfo.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =schinfo.getDownloadpath()+schinfo.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(120);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -73);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
			
			try {
			String gender = "",sd="";
			String name = studentInfo.getFname();
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
				// check="His particulars as per school records are";
				
				
				gender = "son";
				sd="Master";
			} else {

				
				
				gender = "daughter";
				sd="Miss";
			}

			Chunk c6 = new Chunk("\n\n\n\n\n AFFILIATION CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN", fo);
			Paragraph p4 = new Paragraph();
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);

			Chunk c4 = new Chunk("\nThis is certified that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();
			Chunk c41 = new Chunk(name, fom);
			Chunk c42 = new Chunk(" "+gender + " of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "), fomNor);
			Chunk c43 = new Chunk(studentInfo.getFathersName(), fom);
			Chunk c44 = new Chunk(" and "+(ls.getCountry().equalsIgnoreCase("India") ? "Smt. " : "Mrs. "), fomNor);
			Chunk c45 = new Chunk(studentInfo.getMotherName(), fom);
			Chunk c46 = new Chunk(" is a bonafide student of this school, studying in Class ", fomNor);
			Chunk c47 = new Chunk(studentInfo.getClassName(), fom);
			Chunk c48 = new Chunk(" during the Session ", fomNor);

			Chunk c49 = new Chunk(session + ".", fom);
			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
			p2.add(c44);
			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c48);
			p2.add(c49);

			document.add(p2);

			Chunk c5 = new Chunk("\n\n His Particulars as per school records are :", fo);
			Paragraph p3 = new Paragraph();
			p3.add(c5);
			document.add(p3);

			Chunk c7 = new Chunk("\n\nDate of birth :    ", fom);
			Chunk c71 = new Chunk(studentInfo.getDobString(), fomNor);
			Chunk c72 = new Chunk("\nIn Words :        ", fom);
			Chunk c73 = new Chunk(studentInfo.getDobInWord(), fomNor);
			Chunk c74 = new Chunk("\nAddress :         ", fom);
			Chunk c75 = new Chunk(studentInfo.getCurrentAddress(), fomNor);
			Paragraph p5 = new Paragraph();
			p5.add(c7);
			p5.add(c71);
			p5.add(c72);
			p5.add(c73);
			p5.add(c74);
			p5.add(c75);
			document.add(p5);

			Chunk c8 = new Chunk(
					"\n\nThis school is affiliated to " + board + ", New Delhi   Affiliation No. is " + affNo);
			Paragraph p6 = new Paragraph();
			p6.add(c8);
			document.add(p6);

			Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : " + currentDate
					+ "                                                                                                                        Principal",
					fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);

		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Affiliation_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Affiliation_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public void exportFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		String currencyVal = "";
		SideMenuBean smb = new SideMenuBean();
		if (smb.getList().getCountry().equalsIgnoreCase("UAE")) {
			currencyVal = "AED";
		} else {
			currencyVal = "Rs.";
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);

		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 629, 222, 0);
		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		// Header
		try {

			
			String src ="";
			 if(!schinfo.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =schinfo.getDownloadpath()+schinfo.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(120);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -73);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			String[] det = studentName.split("/");

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
			
			// Date dtf = new Date();
            try
            {
			Chunk c6 = new Chunk(
					"\n\n\n\n\n FEE CERTIFICATE\n\nRef No. :                                                                                                     Date : "
							+ currentDate + " \n\nTO WHOMSOEVER IT MAY CONCERN",fo);
			Paragraph p4 = new Paragraph();
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);
			String chec;
			String gender = "",sd="";
			String name = studentInfo.getFname();
			if (studentInfo.gender.equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
				chec = "He";
			
			
				gender = "son";
				sd="Master";
			} else {
				// studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
				chec = "She";
				sd="Miss";
			
				
				gender = "daughter";

			}

			Chunk c4 = new Chunk("\n\r\n" + "It is certified that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();

			Chunk c41 = new Chunk(name, fom);
			Chunk c42 = new Chunk(" "+gender + " of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "), fomNor);
			Chunk c43 = new Chunk(studentInfo.getFathersName(), fom);
			Chunk c44 = new Chunk(" and "+(ls.getCountry().equalsIgnoreCase("India") ? "Smt. " : "Mrs. "), fomNor);
			Chunk c45 = new Chunk(studentInfo.getMotherName(), fom);
			Chunk c46 = new Chunk(" a student of Class ", fomNor);
			Chunk c47 = new Chunk(studentInfo.getClassName() + " (" + studentInfo.getSrNo(), fom);
			Chunk c48 = new Chunk(") is a bonafide student of ", fomNor);

			Chunk c49 = new Chunk(schinfo.getSchoolName(), fom);
			Chunk c51 = new Chunk("." + chec + " has paid ", fomNor);
			Chunk c52 = new Chunk(currencyVal + " " + totalPaidAmount, fom);
			Chunk c53 = new Chunk(" under different heads of fee as mentioned below in financial year ", fomNor);

			Chunk c54 = new Chunk(session + ".\n\n ", fom);

			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
			p2.add(c44);
			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c48);
			p2.add(c49);
			p2.add(c51);
			p2.add(c52);
			p2.add(c53);
			p2.add(c54);
			document.add(p2);

			chec = "";

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("Sno");
			table.addCell("Particular");
			table.addCell("Amount Paid (" + currencyVal + ")");

			table.setHeaderRows(1);

			currencyVal = "";

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < feeList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(feeList.get(i).getSno())));
				table.addCell(new Phrase(feeList.get(i).getFeeName(), font));

				table.addCell(new Phrase(String.valueOf(feeList.get(i).getPayAmount())));
			}
			table.addCell("");
			table.addCell("TOTAL");
			table.addCell(String.valueOf(totalPaidAmount));

			table.setWidthPercentage(110);
			document.add(table);

			Chunk c8 = new Chunk(
					"\n\n\n\nChecked By :                                                                                                        Principal");
			Paragraph p6 = new Paragraph();
			p6.add(c8);
			document.add(p6);

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Fee_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Fee_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public void exportPassportPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);

		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 612, 222, 0);
		cb.stroke();
		cb.restoreState();

		String gender = "";
		String chec;
		String relation = "",sd="";
		String name = studentInfo.getFname();
		if (studentInfo.gender.equalsIgnoreCase("male")) {
			// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
			// check="His particulars as per school records are";
			gender = "His";
			heShe = "He";
		
			
			relation = "S/o";
			sd="Master";
		} else {
			// studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
			// check="Her particulars as per school records are";
			gender = "Her";
			heShe = "She";
			sd="Miss";
		
			
			relation = "D/o";
		}

		heShe = "";

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		Font fontC = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		// Header
		try {

			
			String src ="";
			 if(!schinfo.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =schinfo.getDownloadpath()+schinfo.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(120);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -73);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			String[] det = studentName.split("/");

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
           try {
			Chunk c50 = new Chunk("\n\n\n\n\nRef.No. :LFCS/PAC/" + endSession + "/" + studentInfo.srNo
					+ "                                                                                               Dated :"
					+ currentDate, fontC);
			Chunk c6 = new Chunk("\n\n PASSPORT APPLICATION CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN", fo);
			Paragraph p4 = new Paragraph();
			Paragraph p50 = new Paragraph();
			p50.add(c50);
			p50.setAlignment(Element.ALIGN_LEFT);
			document.add(p50);
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);

			Chunk c4 = new Chunk("\n\r\n" + "This is to certify that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();

			Chunk c41 = new Chunk(name, fom);
			Chunk c42 = new Chunk(" "+relation + " of "+(ls.getCountry().equalsIgnoreCase("India") ? "Shri " : "Mr. "), fomNor);
			Chunk c43 = new Chunk(studentInfo.getFathersName(), fom);
			Chunk c44 = new Chunk(" and "+(ls.getCountry().equalsIgnoreCase("India") ? "Smt. " : "Mrs. "), fomNor);
			Chunk c45 = new Chunk(studentInfo.getMotherName(), fom);
			Chunk c46 = new Chunk(" is a bonafide student of ", fomNor);
			Chunk c47 = new Chunk(schinfo.getSchoolName() + "," + schinfo.add1 + ".", fom);
			Chunk c48 = new Chunk(heShe + " is studying in this school in ", fomNor);

			Chunk c49 = new Chunk(studentInfo.getClassName(), fom);
			Chunk c51 = new Chunk(" under Adm. No. ", fomNor);
			Chunk c52 = new Chunk(studentInfo.getSrNo() + ".", fom);
			Chunk c53 = new Chunk(gender + " date of birth as per record is ", fomNor);

			Chunk c54 = new Chunk(studentInfo.getDobString(), fom);
			Chunk c55 = new Chunk(" (" + studentInfo.getDobInWord() + "). " + gender + " residential address is ",
					fomNor);

			Chunk c56 = new Chunk(studentInfo.getCurrentAddress(), fom);
			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
			p2.add(c44);
			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c48);
			p2.add(c49);

			p2.add(c51);
			p2.add(c52);
			p2.add(c53);
			p2.add(c54);
			p2.add(c55);
			p2.add(c56);
			document.add(p2);

			Chunk c8 = new Chunk("\n\nThis certificate is issued on " + gender + " parent's request.");
			Paragraph p6 = new Paragraph();
			p6.add(c8);
			document.add(p6);

			Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nPrincipal", fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);

			gender = "";
			heShe = "";
			check = "";

		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Passport_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Passport_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public void exportLocPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src = "";
			 if(!schinfo.getMarksheetHeader().equalsIgnoreCase(""))
             {
             	src =schinfo.getDownloadpath()+schinfo.getMarksheetHeader();
             }
             else
             {
             	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
             }
			Image im = Image.getInstance(src);
			im.setAlignment(Element.ALIGN_CENTER);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(520);

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 = new Chunk(im, 0, -15);

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			String[] det = studentName.split("/");

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c4 = new Chunk("\n\n\n\n");
			Paragraph p2 = new Paragraph();
			p2.add(c4);
			document.add(p2);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 3, 1 });

			// table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(new Phrase("CBSE-ONLINE LOC DATA ENTRY FORM FOR CLASS " + cls + " CANDIDATES"));
			table.addCell("");
			table.addCell(
					new Phrase("Class : " + studentInfo.getClassName() + "     Roll NO. : " + studentInfo.getRollNo()));
			table.addCell("");
			table.addCell(new Phrase("NAME OF CANDIDATE :"));
			table.addCell(new Phrase(studentInfo.getFname()));
			table.addCell(new Phrase("MOTHER'S NAME :"));
			table.addCell(new Phrase(studentInfo.getMotherName()));
			table.addCell(new Phrase("FATHER'S NAME :"));
			table.addCell(new Phrase(studentInfo.getFathersName()));
			table.addCell(new Phrase("DATE OF BIRTH :"));
			table.addCell(new Phrase(studentInfo.getDobString()));
			table.addCell(new Phrase("CASTE :"));
			table.addCell(new Phrase(studentInfo.getCaste()));
			table.addCell(new Phrase("HANDICAPPED"));
			table.addCell(new Phrase("N.A. ,BLIND ,DEAF ,HANDICAPPED ,DYLEXSIC ,SPASTIC"));
			table.addCell(new Phrase("ANNUAL INCOME OF MOTHER AND FATHER :"));
			table.addCell(new Phrase((ls.getCountry().equalsIgnoreCase("UAE") ? "AED" : "Rs.")));
			table.addCell(new Phrase("GENDER :"));
			table.addCell(new Phrase(studentInfo.getGender()));
			table.addCell(new Phrase("NAME OF THIRD LANGUAGE"));
			table.addCell(new Phrase());
			table.addCell(new Phrase("YEAR OF PASSING"));
			table.addCell(new Phrase());
			table.addCell(new Phrase("ADMITTED DIRECTLY IN CLASS IX :"));
			table.addCell(new Phrase("YES      NO   "));
			table.addCell(new Phrase("ONLY CHILD OF PARENTS :"));
			table.addCell(new Phrase("YES      NO   "));
			table.addCell(new Phrase("ADMISSION NO. : "));
			table.addCell(new Phrase(studentInfo.getSrNo()));
			table.addCell(new Phrase("DATE OF JOINGING :"));
			table.addCell(new Phrase(studentInfo.getStartingDateStr()));
			table.addCell(new Phrase(" DO YOU BELONG TO MINORITY SECTION :"));
			table.addCell(new Phrase("YES      NO   "));
			table.addCell(new Phrase(
					"THE ABOVE GIVEN DATA ARE YOUR WARD'S DETAILS AS PER SCHOOL RECORDS AND WILL BE SENT TO CBSE FOR REGISTRATION PLEASE VERIFY"));
			table.addCell(new Phrase());
			table.addCell(new Phrase());
			table.addCell(new Phrase("Parent's Signature"));

			table.setWidthPercentage(110);
			document.add(table);

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "LOC_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("LOC_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public boolean isShowPrint() {
		return showPrint;
	}

	public void setShowPrint(boolean showPrint) {
		this.showPrint = showPrint;
	}

	public boolean isShowBirth() {
		return showBirth;
	}

	public void setShowBirth(boolean showBirth) {
		this.showBirth = showBirth;
	}

	public boolean isShowBank() {
		return showBank;
	}

	public void setShowBank(boolean showBank) {
		this.showBank = showBank;
	}

	public boolean isShowBonafied() {
		return showBonafied;
	}

	public void setShowBonafied(boolean showBonafied) {
		this.showBonafied = showBonafied;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public boolean isShowPic() {
		return showPic;
	}

	public void setShowPic(boolean showPic) {
		this.showPic = showPic;
	}

	public boolean isShowStatic() {
		return showStatic;
	}

	public void setShowStatic(boolean showStatic) {
		this.showStatic = showStatic;
	}

	public String getStartSession() {
		return startSession;
	}

	public void setStartSession(String startSession) {
		this.startSession = startSession;
	}

	public String getEndSession() {
		return endSession;
	}

	public void setEndSession(String endSession) {
		this.endSession = endSession;
	}

	public boolean isShowCharacter() {
		return showCharacter;
	}

	public void setShowCharacter(boolean showCharacter) {
		this.showCharacter = showCharacter;
	}

	public boolean isShowAffiliation() {
		return showAffiliation;
	}

	public void setShowAffiliation(boolean showAffiliation) {
		this.showAffiliation = showAffiliation;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public boolean isShowFee() {
		return showFee;
	}

	public void setShowFee(boolean showFee) {
		this.showFee = showFee;
	}

	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}

	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}

	public int getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(int totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public boolean isShowPassportApply() {
		return showPassportApply;
	}

	public void setShowPassportApply(boolean showPassportApply) {
		this.showPassportApply = showPassportApply;
	}

	public String getHeShe() {
		return heShe;
	}

	public void setHeShe(String heShe) {
		this.heShe = heShe;
	}

	public boolean isShowLOC() {
		return showLOC;
	}

	public void setShowLOC(boolean showLOC) {
		this.showLOC = showLOC;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getCheckGender() {
		return checkGender;
	}

	public void setCheckGender(String checkGender) {
		this.checkGender = checkGender;
	}

	public String getSubject1() {
		return subject1;
	}

	public void setSubject1(String subject1) {
		this.subject1 = subject1;
	}



	public String getSubject3() {
		return subject3;
	}

	public void setSubject3(String subject3) {
		this.subject3 = subject3;
	}

	public String getSubject4() {
		return subject4;
	}

	public void setSubject4(String subject4) {
		this.subject4 = subject4;
	}

	public String getSubject5() {
		return subject5;
	}

	public void setSubject5(String subject5) {
		this.subject5 = subject5;
	}

	public String getSubject2() {
		return subject2;
	}

	public void setSubject2(String subject2) {
		this.subject2 = subject2;
	}

	public boolean isShowBirthnorthwood() {
		return showBirthnorthwood;
	}

	public void setShowBirthnorthwood(boolean showBirthnorthwood) {
		this.showBirthnorthwood = showBirthnorthwood;
	}

	public String getHisHer() {
		return hisHer;
	}

	public void setHisHer(String hisHer) {
		this.hisHer = hisHer;
	}



	public String getTotalAmountInWords() {
		return totalAmountInWords;
	}

	public void setTotalAmountInWords(String totalAmountInWords) {
		this.totalAmountInWords = totalAmountInWords;
	}

	public String getOldOrActive() {
		return oldOrActive;
	}

	public void setOldOrActive(String oldOrActive) {
		this.oldOrActive = oldOrActive;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStudentTypeCharacterCertificate() {
		return studentTypeCharacterCertificate;
	}

	public void setStudentTypeCharacterCertificate(String studentTypeCharacterCertificate) {
		this.studentTypeCharacterCertificate = studentTypeCharacterCertificate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList<String> getSelectedQuarter() {
		return selectedQuarter;
	}

	public void setSelectedQuarter(ArrayList<String> selectedQuarter) {
		this.selectedQuarter = selectedQuarter;
	}

	public ArrayList<String> getSelectedFee() {
		return selectedFee;
	}

	public void setSelectedFee(ArrayList<String> selectedFee) {
		this.selectedFee = selectedFee;
	}

	public ArrayList<SelectItem> getClassFeeList() {
		return classFeeList;
	}

	public void setClassFeeList(ArrayList<SelectItem> classFeeList) {
		this.classFeeList = classFeeList;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getChildOf() {
		return childOf;
	}

	public void setChildOf(String childOf) {
		this.childOf = childOf;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getAmountinWord() {
		return amountinWord;
	}

	public void setAmountinWord(String amountinWord) {
		this.amountinWord = amountinWord;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public ArrayList<FeeInfo> getFeeDetailList() {
		return feeDetailList;
	}

	public void setFeeDetailList(ArrayList<FeeInfo> feeDetailList) {
		this.feeDetailList = feeDetailList;
	}

	public boolean isShowCertificate() {
		return showCertificate;
	}

	public void setShowCertificate(boolean showCertificate) {
		this.showCertificate = showCertificate;
	}

	public String getSessionString() {
		return sessionString;
	}

	public void setSessionString(String sessionString) {
		this.sessionString = sessionString;
	}

	public String getPrincipalNameSS() {
		return principalNameSS;
	}

	public void setPrincipalNameSS(String principalNameSS) {
		this.principalNameSS = principalNameSS;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getFeeIncludeInCertificate() {
		return feeIncludeInCertificate;
	}

	public void setFeeIncludeInCertificate(String feeIncludeInCertificate) {
		this.feeIncludeInCertificate = feeIncludeInCertificate;
	}

	public boolean isShowCertificatePer() {
		return showCertificatePer;
	}

	public void setShowCertificatePer(boolean showCertificatePer) {
		this.showCertificatePer = showCertificatePer;
	}

	public String getPreviousClass() {
		return previousClass;
	}

	public void setPreviousClass(String previousClass) {
		this.previousClass = previousClass;
	}

	public String getPrefix1() {
		return prefix1;
	}

	public void setPrefix1(String prefix1) {
		this.prefix1 = prefix1;
	}

	public String getPrefix2() {
		return prefix2;
	}

	public void setPrefix2(String prefix2) {
		this.prefix2 = prefix2;
	}

	public String getSubject6() {
		return subject6;
	}

	public void setSubject6(String subject6) {
		this.subject6 = subject6;
	}

	public String[] getSy() {
		return sy;
	}

	public void setSy(String[] sy) {
		this.sy = sy;
	}

	public ArrayList<SelectItem> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<SelectItem> feelist) {
		this.feelist = feelist;
	}

	public ArrayList<String> getSelectedFees() {
		return selectedFees;
	}

	public void setSelectedFees(ArrayList<String> selectedFees) {
		this.selectedFees = selectedFees;
	}



	
	
}
