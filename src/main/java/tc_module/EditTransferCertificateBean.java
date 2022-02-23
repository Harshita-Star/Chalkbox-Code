package tc_module;

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
import javax.faces.context.ExternalContext;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.TCInfo;
import session_work.RegexPattern;

@ManagedBean(name = "editTc")
@ViewScoped
public class EditTransferCertificateBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String regex = RegexPattern.REGEX;
	ArrayList<TCInfo> allStudentTCList;
	transient StreamedContent file;
	TCInfo selectedTC;
	boolean sortAscending = true;
	StudentInfo studentInfo = new StudentInfo();
	boolean showTextBox, renTransferCertificateFrom, showBookNo = false, ssDetails = false;
	String schoolExam, ncc, failedOrNot, subjectStudied, qualifiedPromotion, monthOfFeePaid, workingDays,
			workingDayPresent, feeConcession;
	String gamesPlayed, extraActivity, otherRemark, reason, tcNumber, lastClass, perform, otherReason, admitClass,
			stadhar, fadhar, madhar;
	Date issueDate, lastDate, applicationDate;
	String studentName, fatherName, motherName, nationality, categoryId, srno, transferCertificatedFrom,
			qualifiedPromotionCheck, result;
	Date dob, doa;
	ArrayList<SelectItem> categoryList = new ArrayList<>();
	DataBaseMethodsTcModule objTc = new DataBaseMethodsTcModule();
	ArrayList<String> subjectList = new ArrayList<String>();
	ArrayList<String> selectedSubjects = new ArrayList<String>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	ArrayList<String> reasons = new ArrayList<>();
	String schoolId, sessionValue, proofDob, bookNo = null;

	public EditTransferCertificateBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = DBM.schoolId();
		sessionValue = DBM.selectedSessionDetails(schoolId, conn);
		categoryList = DBM.studentCategoryList(conn);
		allStudentTCList = objTc.allStudentTcList(conn);

		System.out.println(allStudentTCList.size());
		SchoolInfoList ls = DBM.fullSchoolInfo(conn);
		if (ls.getBranch_id().equals("88")) {
			renTransferCertificateFrom = true;
		} else {
			renTransferCertificateFrom = false;
		}

		getReasons();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getReasons() {
		reasons = new ArrayList<>();
		if (schoolId.equals("313") || schoolId.equals("315")) {
			reasons.add("Due to long absent".toUpperCase());
			reasons.add("PARENT’S DESIRE".toUpperCase());
			reasons.add("FURTHER STUDIES".toUpperCase());
			reasons.add("To study else where".toUpperCase());
			reasons.add("To study for higher classes".toUpperCase());
			reasons.add("Parents transfer to other city".toUpperCase());
			reasons.add("Transfer of father".toUpperCase());
			reasons.add("Others".toUpperCase());
		} else {
			reasons.add("Due to long absent");
			reasons.add("PARENT'S DESIRE");
			reasons.add("FURTHER STUDIES");
			reasons.add("To study else where");
			reasons.add("To study for higher classes");
			reasons.add("Parents transfer to other city");
			reasons.add("Others");
		}
		return reasons;
		
		
	}

	public void editDetails() {
		Connection conn = DataBaseConnection.javaConnection();
		
		reason = selectedTC.getReason();
		 // System.out.println(reason);
		otherReason = selectedTC.getOtherReason();
		tcNumber =selectedTC.getTcNo();
		lastClass = selectedTC.getLastClass();
		issueDate = selectedTC.getIssueDate();
		lastDate = selectedTC.getLeavingDate();
		applicationDate = selectedTC.getApplicationDate();
		transferCertificatedFrom = selectedTC.getTransferCertificatedFrom();
		qualifiedPromotionCheck = selectedTC.getQualifiedPromotionCheck();
		schoolExam = selectedTC.getSchoolExam();
		failedOrNot = selectedTC.getFailedOrNot();
		subjectStudied = selectedTC.getSubjectStudied();
		qualifiedPromotion = selectedTC.getQualifiedPromotion();
		monthOfFeePaid = selectedTC.getMonthOfFeePaid();
		workingDays = selectedTC.getWorkingDays();
		workingDayPresent = selectedTC.getWorkingDayPresent();
		feeConcession = selectedTC.getFeeConcession();
		gamesPlayed = selectedTC.getGamesPlayed();
		extraActivity = selectedTC.getExtraActivity();
		otherRemark = selectedTC.getOtherRemark();
		perform = selectedTC.getPerformance();
		admitClass = selectedTC.getAddClass();
		ncc = selectedTC.getNcc();
		result = selectedTC.getResult();
		proofDob = selectedTC.getProofDob();

		if (selectedTC.getSchoolCode().equals("315") || selectedTC.getSchoolCode().equals("313")) {
			bookNo = selectedTC.getBookNo();
			showBookNo = true;
			ssDetails = false;
		} else {
			showBookNo = false;
			ssDetails = true;
		}
		subjectList = new ArrayList<String>();
		selectedSubjects = new ArrayList<String>();
		subjectList = DBM.allSubjectsClassWise(schoolId, selectedTC.getClassId(), sessionValue, conn);

		//  // System.out.println("as"+subjectList.size());
		ArrayList<String> tempSubList = new ArrayList<>();

		if (subjectList.size() > 0) {
			for (String kk : subjectList) {
				//  // System.out.println(kk);
				tempSubList.add(DBM.toTitleCase(kk.trim()));

			}
		}
		subjectList = new ArrayList<>();
		subjectList.addAll(tempSubList);
		try {

			String[] spliter = selectedTC.getSubjectStudied().split(",");
			
			for (int p = 0; p < spliter.length; p++) {
				// // System.out.println("strubg ->"+spliter[p]);
				selectedSubjects.add(spliter[p].trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		checkOtherReason();
	}

	public void editBasicDetails() {
		studentName = selectedTC.getStudentName();
		fatherName = selectedTC.getFatherName();
		motherName = selectedTC.getMotherName();
		nationality = selectedTC.getNationality();
		categoryId = selectedTC.getCategoryId();
		doa = selectedTC.getJoinDate();
		dob = selectedTC.getDob();
		srno = selectedTC.getSrno();
		stadhar = selectedTC.getStadhar();
		fadhar = selectedTC.getFadhar();
		madhar = selectedTC.getMadhar();
	}

	public void editPersonalDetails() {
		Connection conn = DataBaseConnection.javaConnection();

		studentInfo.setAddNumber(selectedTC.getAddNumber());
		DBM.studentDetailsByAddNo(studentInfo, conn);

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", studentInfo);
		ss.setAttribute("pageName", "From TC");

		ExternalContext cc = FacesContext.getCurrentInstance().getExternalContext();
		try {
			cc.redirect("editStudentDetails.xhtml");
			conn.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void checkOtherReason() {
		if (reason.equalsIgnoreCase("others")) {
			showTextBox = true;
		} else {
			showTextBox = false;
			otherReason = null;
		}
	}

	public void updateTCDetails() {
		Connection conn = DataBaseConnection.javaConnection();

		subjectStudied = "";
		for (String ssc : selectedSubjects) {
			subjectStudied += ssc + " , ";
		}
		if (!(selectedSubjects.size() == 0)) {
			subjectStudied = subjectStudied.substring(0, subjectStudied.length() - 3);
		}

//		if (reason.equalsIgnoreCase("others")) {
//			reason = otherReason;
//		}
		int i = objTc.updateTCInformation1(reason, lastDate, applicationDate, issueDate, tcNumber,
				selectedTC.getStatus(), selectedTC.getAddNumber(), perform, otherReason, schoolExam, failedOrNot,
				subjectStudied, qualifiedPromotion, monthOfFeePaid, workingDays, workingDayPresent, feeConcession,
				gamesPlayed, extraActivity, otherRemark, lastDate, ncc, transferCertificatedFrom,
				qualifiedPromotionCheck, result, proofDob, bookNo, conn);
		if (i == 1) {
			String refNo4;
			try {
				refNo4 = addWorkLog4(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Transfer Certificate Updated Sucessfully"));
			DBM.updateAdmitClassInReg(selectedTC.getAddNumber(), admitClass, conn);
			String refNo5;
			try {
				refNo5 = addWorkLog5(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			allStudentTCList = objTc.allStudentTcList(conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public String addWorkLog5(Connection conn) {
		String value = "Add Number-" + selectedTC.getAddNumber() + " --Admit Class-" + admitClass;
		String language = "";

		String refNo = workLg.saveWorkLogMehod(language, "update Tc Admit Class Update", "WEB", value, conn);
		return refNo;
	}

	public String addWorkLog4(Connection conn) {
		String value = reason + " -- " + lastDate + " -- " + applicationDate + " -- " + issueDate + " -- " + tcNumber
				+ " -- " + selectedTC.getStatus() + " -- " + selectedTC.getAddNumber() + " -- " + perform + " -- "
				+ otherReason + " -- " + schoolExam + " -- " + failedOrNot + " -- " + subjectStudied + " -- "
				+ qualifiedPromotion + " -- " + monthOfFeePaid + " -- " + workingDays + " -- " + workingDayPresent
				+ " -- " + feeConcession + " -- " + gamesPlayed + " -- " + extraActivity + " -- " + otherRemark + " -- "
				+ lastDate + " -- " + ncc + " -- " + transferCertificatedFrom + " -- " + qualifiedPromotionCheck;
		String language = "";

		String refNo = workLg.saveWorkLogMehod(language, "update Tc info", "WEB", value, conn);
		return refNo;
	}

	public void cancelTC() {
		Connection conn = DataBaseConnection.javaConnection();
		int i = objTc.cancelTransferCertificate(selectedTC.getAddNumber(), selectedTC.getId(), conn);
		if (i == 1) {
			String refNo;
			try {
				refNo = addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			DBM.activateUser(selectedTC.getAddNumber(), conn);
			String refNo2;
			try {
				refNo2 = addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Transfer Certificate Cancelled Sucessfully"));
			allStudentTCList = objTc.allStudentTcList(conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addWorkLog(Connection conn) {
		String value = "Student-" + selectedTC.getAddNumber() + " -- Id-" + selectedTC.getId();
		String language = "";

		String refNo = workLg.saveWorkLogMehod(language, "Cancel Tc", "WEB", value, conn);
		return refNo;
	}

	public String addWorkLog2(Connection conn) {
		String value = "Student-" + selectedTC.getAddNumber();
		String language = "";

		String refNo = workLg.saveWorkLogMehod(language, "Activate User", "WEB", value, conn);
		return refNo;
	}

	public void updateBasicDetails() {
		Connection conn = DataBaseConnection.javaConnection();
		int i = DBM.updateBasicDetailFromTc(studentName, fatherName, motherName, nationality, categoryId, srno, doa,
				dob, selectedTC.getAddNumber(), stadhar, fadhar, madhar, conn);
		if (i >= 1) {
			String refNo3;
			try {
				refNo3 = addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Basic Details Updated Successfully"));
			allStudentTCList = objTc.allStudentTcList(conn);

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addWorkLog3(Connection conn) {
		String value = "";
		String language = "";

		language = "SrNo-" + srno + " -- Student-" + studentName + " --Aadhar-" + stadhar + " --Father-" + fatherName
				+ " --Father aadhar-" + fadhar + " --Mother-" + motherName + " --Mother Aadhar-" + madhar + " --DOA-"
				+ doa + " --DOB-" + dob + " --Category-" + categoryId;

		value = studentName + " -- " + fatherName + " -- " + motherName + " -- " + nationality + " -- " + categoryId
				+ " -- " + srno + " -- " + doa + " -- " + dob + " -- " + selectedTC.getAddNumber() + " -- " + stadhar
				+ " -- " + fadhar + " -- " + madhar;

		String refNo = workLg.saveWorkLogMehod(language, "edit Basic Details In Tc", "WEB", value, conn);
		return refNo;
	}

	public void feeStatement()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("studentid", selectedTC.getAddNumber());
		ss.setAttribute("leavingDate", selectedTC.getLeavingDate());
		PrimeFaces.current().executeInitScript("window.open('tcStudentFeeStatement.xhtml')");
	}
	
	public String printDetails() {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("tcInfo", selectedTC);
		PrimeFaces.current().executeInitScript("window.open('printTransferCertificate.xhtml')");
		return "";
	}

	public void printCCDetails() {
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);

		String arr[] = selectedTC.getLeavingDateStr().split("/");
		int month = Integer.parseInt(arr[1]);
		int year = Integer.parseInt(arr[2]);
		String selectedSession = "";
//		if(ls.getBranch_id().equalsIgnoreCase("22") || ls.getBranch_id().equalsIgnoreCase("27"))
//		{
//			selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
//		}
//		else if(ls.getBranch_id().equalsIgnoreCase("23"))
//		{
		selectedSession = selectedTC.getSession();
//		}
//		else
//		{
//			if(month>=4)
//			{
//				selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
//			}
//			else
//			{
//				selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
//			}
//		}

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("gender", selectedTC.getGender());
		ss.setAttribute("studentName", selectedTC.getStudentName());
		ss.setAttribute("fatherName", selectedTC.getFatherName());
		ss.setAttribute("lastClass", selectedTC.getClassName());
		ss.setAttribute("lastYear", selectedSession);

		ss.setAttribute("srno", selectedTC.getSrno());
		ss.setAttribute("address", selectedTC.getAddress());
		ss.setAttribute("place", " ");
		ss.setAttribute("motherName", selectedTC.getMotherName());
		ss.setAttribute("passFail", selectedTC.getFailedOrNot());
		ss.setAttribute("admDate", selectedTC.getJoinDateStr());
		ss.setAttribute("removalDate", selectedTC.getLeavingDateStr());

		ss.setAttribute("studentid", selectedTC.getAddNumber());
		ss.setAttribute("result", selectedTC.getResult());
		ss.setAttribute("remark", selectedTC.getOtherRemark());
		ss.setAttribute("lastSchoolExam", selectedTC.getSchoolExam());
		try {
			if (ls.getBranch_id().equalsIgnoreCase("99") || ls.getBranch_id().equalsIgnoreCase("101")) {
				PrimeFaces.current().executeInitScript("window.open('printOldStudentCCSSChildren.xhtml')");
			} else {
				PrimeFaces.current().executeInitScript("window.open('printOldStudentCC.xhtml')");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportTc() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = DBM.fullSchoolInfo(con);

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

			Chunk c = new Chunk(ls.schoolName + "\n", fo);

			Chunk c1 = new Chunk("              " + ls.add1 + " " + ls.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);

			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                             All Student Tc Table\n\n", fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("S No.");
			table.addCell("Adm. No.");
			table.addCell("TC No.");
			table.addCell("Student Name");
			table.addCell("Father Name");
			table.addCell("Admission Class");
			table.addCell("Last Class");
			table.addCell("Issue Date");
			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < allStudentTCList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(allStudentTCList.get(i).getsNo()), font));
				table.addCell(new Phrase(allStudentTCList.get(i).getSrno(), font));
				table.addCell(new Phrase(allStudentTCList.get(i).getTcNo(), font));
				table.addCell(new Phrase(String.valueOf(allStudentTCList.get(i).getStudentName()), font));
				table.addCell(new Phrase(String.valueOf(allStudentTCList.get(i).getFatherName()), font));
				table.addCell(new Phrase(allStudentTCList.get(i).getAddClass(), font));
				table.addCell(new Phrase(allStudentTCList.get(i).getLastClass(), font));
				table.addCell(new Phrase(String.valueOf(allStudentTCList.get(i).getIssueDateStr()), font));

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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", " All Student Tc Table.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("ALl Student Tc Table.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
	}

	public ArrayList<TCInfo> getAllStudentTCList() {
		return allStudentTCList;
	}

	public void setAllStudentTCList(ArrayList<TCInfo> allStudentTCList) {
		this.allStudentTCList = allStudentTCList;
	}

	public TCInfo getSelectedTC() {
		return selectedTC;
	}

	public void setSelectedTC(TCInfo selectedTC) {
		this.selectedTC = selectedTC;
	}

	public boolean isShowTextBox() {
		return showTextBox;
	}

	public void setShowTextBox(boolean showTextBox) {
		this.showTextBox = showTextBox;
	}

	public String getSchoolExam() {
		return schoolExam;
	}

	public void setSchoolExam(String schoolExam) {
		this.schoolExam = schoolExam;
	}

	public String getFailedOrNot() {
		return failedOrNot;
	}

	public void setFailedOrNot(String failedOrNot) {
		this.failedOrNot = failedOrNot;
	}

	public String getSubjectStudied() {
		return subjectStudied;
	}

	public void setSubjectStudied(String subjectStudied) {
		this.subjectStudied = subjectStudied;
	}

	public String getQualifiedPromotion() {
		return qualifiedPromotion;
	}

	public void setQualifiedPromotion(String qualifiedPromotion) {
		this.qualifiedPromotion = qualifiedPromotion;
	}

	public String getMonthOfFeePaid() {
		return monthOfFeePaid;
	}

	public void setMonthOfFeePaid(String monthOfFeePaid) {
		this.monthOfFeePaid = monthOfFeePaid;
	}

	public String getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}

	public String getWorkingDayPresent() {
		return workingDayPresent;
	}

	public void setWorkingDayPresent(String workingDayPresent) {
		this.workingDayPresent = workingDayPresent;
	}

	public String getFeeConcession() {
		return feeConcession;
	}

	public void setFeeConcession(String feeConcession) {
		this.feeConcession = feeConcession;
	}

	public String getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(String gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public String getExtraActivity() {
		return extraActivity;
	}

	public void setExtraActivity(String extraActivity) {
		this.extraActivity = extraActivity;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLastClass() {
		return lastClass;
	}

	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}

	public String getTcNumber() {
		return tcNumber;
	}

	public void setTcNumber(String tcNumber) {
		this.tcNumber = tcNumber;
	}

	public String getPerform() {
		return perform;
	}

	public void setPerform(String perform) {
		this.perform = perform;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}

	public String getOtherReason() {
		return otherReason;
	}

	public String getAdmitClass() {
		return admitClass;
	}

	public void setAdmitClass(String admitClass) {
		this.admitClass = admitClass;
	}

	public StudentInfo getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
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

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSrno() {
		return srno;
	}

	public void setSrno(String srno) {
		this.srno = srno;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getDoa() {
		return doa;
	}

	public void setDoa(Date doa) {
		this.doa = doa;
	}

	public boolean isShowBookNo() {
		return showBookNo;
	}

	public void setShowBookNo(boolean showBookNo) {
		this.showBookNo = showBookNo;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getNcc() {
		return ncc;
	}

	public void setNcc(String ncc) {
		this.ncc = ncc;
	}

	public String getStadhar() {
		return stadhar;
	}

	public void setStadhar(String stadhar) {
		this.stadhar = stadhar;
	}

	public String getFadhar() {
		return fadhar;
	}

	public void setFadhar(String fadhar) {
		this.fadhar = fadhar;
	}

	public String getMadhar() {
		return madhar;
	}

	public void setMadhar(String madhar) {
		this.madhar = madhar;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getTransferCertificatedFrom() {
		return transferCertificatedFrom;
	}

	public void setTransferCertificatedFrom(String transferCertificatedFrom) {
		this.transferCertificatedFrom = transferCertificatedFrom;
	}

	public boolean isRenTransferCertificateFrom() {
		return renTransferCertificateFrom;
	}

	public void setRenTransferCertificateFrom(boolean renTransferCertificateFrom) {
		this.renTransferCertificateFrom = renTransferCertificateFrom;
	}

	public String getQualifiedPromotionCheck() {
		return qualifiedPromotionCheck;
	}

	public void setQualifiedPromotionCheck(String qualifiedPromotionCheck) {
		this.qualifiedPromotionCheck = qualifiedPromotionCheck;
	}

	public ArrayList<String> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<String> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSelectedSubjects() {
		return selectedSubjects;
	}

	public void setSelectedSubjects(ArrayList<String> selectedSubjects) {
		this.selectedSubjects = selectedSubjects;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getProofDob() {
		return proofDob;
	}

	public void setProofDob(String proofDob) {
		this.proofDob = proofDob;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public boolean isSsDetails() {
		return ssDetails;
	}

	public void setSsDetails(boolean ssDetails) {
		this.ssDetails = ssDetails;
	}

	public void setReasons(ArrayList<String> reasons) {
		this.reasons = reasons;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
