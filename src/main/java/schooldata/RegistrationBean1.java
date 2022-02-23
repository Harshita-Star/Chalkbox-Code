
package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import exam_module.ExamInfo;
import reports_module.DataBaseMethodsReports;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name = "registrationBean1")
@ViewScoped
public class RegistrationBean1 implements Serializable {
	private static final long serialVersionUID = 1L;
	String residencePhone, studentPhone = "0", fathersOccupation, selectedSection, selectedSectionForAdmitClass,
			country, route, className, fname, lname, ffname, mname, className1, routeName1 = "", category = " ", gender,
			nationality, religion = " ", currAdd = " ", perAdd = " ", addmissionNumber, sendMessageMobileNo,
			admRemark = "";
	String doctype, regNo_IX, regNo_XI, ledgNo, admnFileNo;
	String newHouse, balMsg;
	String discountFee = "0";
	String TotalFess = "0";
	String minority = "No";
	ArrayList<ClassTest> classTestList;
	ArrayList<ExamInfo> examList;
	boolean testStatus = false, examStatus = false, showInstitute = false;
	ArrayList<SelectItem> categoryList, discountList, sectionList, religionList, handicaplist;
	List<SelectItem> classList, routeList, connsessionList;
	long fathersPhone, mothersPhone;
	int pincode = 0;
	double hostelfees, smsLimit;
	UploadedFile studentImage;
	Date addmissionDate = new Date(), dob = new Date();
	String fatherAadharNo, boardName, hostal, houseName, disability, handicap;
	String bpl, bplCardNo, concession, caste, singleChild, bloodGroup, aadharNo, SingleParent, livingWith, fatherEmail,
			motherEmail;
	UploadedFile fatherImage, motherImage, g1Image, g2Image;
	boolean showBpl, showDis;
	String message = "", clsName, schName, cbNumber;
	ArrayList<SelectItem> houseCategorylist, doctypelist;
	String fname1, lname1, relation1, occupation1, phone1, address1, fname2, lname2, relation2, occupation2, phone2,
			address2, lastSchoolName, passedClass, medium, sname1, sname2, sclassid1, sclassid2, sClassName1,
			sClassName2;
	ArrayList<String> documentsSubmitted;
	boolean completed;
	String routeFees = "0";
	ArrayList<StudentInfo> studentList, completeList = new ArrayList<>();
	String pResult, p_percent, pReason, height, weight, eyes, fatherQualification, motherQualification,
			motherOccupation, fatherDesignation;
	String motherDesignation, fatherOrganization, motherOrganization, fatherOfficeAdd, motherOfficeAdd, fatherIncome,
			motherIncome, FatherAadhaar, motherAadhaar, fatherSchoolEmp, motherSchoolEmp;
	Date tcDate;
	String motherofficecontactno, fatherofficecontcatno;
	String rollNo, studentstatus = "new";
	String conceesionCategory, concessionStatus, type;

	String srnoType, srnoPrefix, srnoStart, schid, session, fahterAnnualIncome, motherAnnualIncome;
	boolean disableSrNo; // with getter - setter
	SchoolInfoList info;
	SelectItem selectedDoc = new SelectItem();
	String tenRoll,tenYearOfPass,tenBoard;
	boolean showminority=false;
	String village = "";

	public RegistrationBean1() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		schid = DBM.schoolId();
		session = DBM.selectedSessionDetails(schid, conn);
		smsLimit = DBM.smsLimitReminder(DBM.schoolId(), conn);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) ss.getAttribute("registration");
		type = (String) ss.getAttribute("type");
		categoryList = DBM.studentCategoryList(conn);
		classList = DBM.allClass(conn);
		religionList = DBM.allReligionList(conn);
		routeList = DBM.allStops(conn);
		houseCategorylist = DBM.allHouseCategory(conn);
		doctypelist = DBM.allDocType(conn);
		connsessionList = DBM.allConnsessionType(conn);
		nationality = "Indian";
		handicaplist = DBM.studentHandicapList(conn);
		concession = (String) connsessionList.get(0).getValue();
		String ctype = DBM.checkClientType(conn);
		
		if(schid.equals("313") || schid.equals("315")) {
			showminority = true;
		}
		
		if (ctype.equalsIgnoreCase("institute")) {
			showInstitute = true;
		} else {
			showInstitute = false;
		}
		// religion="Hindu";

		country = "India";

		info = DBM.fullSchoolInfo(conn);
		srnoType = info.getSrnoType();

		if (srnoType.equalsIgnoreCase("manual")) {
			disableSrNo = false;
			addmissionNumber = "";
		} else {
			disableSrNo = true;
			if (info.getBranch_id().equalsIgnoreCase("22")) {
				addmissionNumber = "";
			} else {
				boolean check = DBM.checkStudentsInSchool(info.getSchid(), conn);
				if (check == false) {
					addmissionNumber = info.getSrnoPrefix() + info.getSrnoStart();
				} else {
					addmissionNumber = info.getSrnoPrefix()
							+ DBM.autoGeneratedSrNo(info.getSchid(), (info.getSrnoPrefix().length() + 1), conn);
				}
			}

			//
		}

		for (int i = 1; i <= 5; i++) {
			StudentInfo info = new StudentInfo();
			info.setSno(i);
			info.setAddNumber("");
			completeList.add(info);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkRoute() {
		Connection conn = DataBaseConnection.javaConnection();
		routeFees = new DatabaseMethods1().routeStopListAllAmount(new DatabaseMethods1().schoolId(), routeName1, conn);
		TotalFess = routeFees;
		discountFee = "0";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkfees() {
		if (Integer.valueOf(routeFees) >= Integer.valueOf(discountFee)) {
			TotalFess = String.valueOf(Integer.valueOf(routeFees) - Integer.valueOf(discountFee));

		} else {
			TotalFess = routeFees;
			discountFee = "0";
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Discount Fee Must Be LessThan Or Equal Of Route Fees",
							"Discount Fee Must Be LessThan Or Equal Of Route Fees"));

		}

	}

	public void houseSrno() {
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();

		String startSession = DatabaseMethods1.selectedSessionDetails(info.getSchid(), conn).split("-")[0];
		String ssn = StringUtils.right(startSession, 2);
		// String ssn = startSession.substring(startSession.length() - 2);
		boolean checkStu = DBM.checkStudentsInSchool(info.getSchid(), conn);
		if (checkStu == false) {
			if (info.getBranch_id().equalsIgnoreCase("22")) {
				String house = DBM.houseNameById(houseName, conn);
				String hsnm = StringUtils.left(house, 2);
				addmissionNumber = info.getSrnoPrefix() + hsnm + "/" + info.getSrnoStart() + "/" + ssn;
			} else {
				// addmissionNumber = "";
			}

		} else {
			if (info.getBranch_id().equalsIgnoreCase("22")) {
				String house = DBM.houseNameById(houseName, conn);
				String hsnm = StringUtils.left(house, 2);
				// addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+""+"/"+ssn;
				// EG/AS/1234/18 --> prefix=EG/ , house=AS
				String prefix = info.getSrnoPrefix() + hsnm + "/";
				addmissionNumber = prefix + DBM.autoGeneratedSrNo(info.getSchid(), (prefix.length() + 1), conn) + "/"
						+ ssn;
			} else {
				// addmissionNumber = "";
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkAdmissionNumber() {
		Connection conn = DataBaseConnection.javaConnection();
		int status = new DatabaseMethods1().duplicateStudentEntry(new DatabaseMethods1().schoolId(), addmissionNumber,
				conn);
		if (status == 1) {

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate Admission Number " + addmissionNumber + " found,try a different one",
							"Duplicate Admission Number " + addmissionNumber + " found,try a different one"));
			addmissionNumber = "";
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = new DatabaseMethods1().allSection(className1, conn);
		selectedSection = (String) sectionList.get(0).getValue();

		selectedSectionForAdmitClass = className1;

		houseCategorylist = new ArrayList<>();
		houseCategorylist = new DatabaseMethods1().allHouseCategory(conn);

		if (sectionList.size() > 0) {
			int count = 0;
			for (SelectItem ss : sectionList) {
				count = new DataBaseMethodsReports().fetchAllValuesFromregistration(String.valueOf(ss.getValue()),
						"all", conn);
				ss.setLabel(ss.getLabel() + " (" + count + " Students)");
			}
		}

		if (houseCategorylist.size() > 0) {
			int count = 0;
			for (SelectItem ss : houseCategorylist) {
				count = Integer.parseInt(
						new DatabaseMethods1().allStudentcount(new DatabaseMethods1().schoolId(), "house", className1,
								new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(), conn),
								String.valueOf(ss.getValue()), conn));
				ss.setLabel(ss.getLabel() + " (" + count + " Students)");
			}
		}
		
		checkDob();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showBPLNo() {
		if (bpl.equals("Yes"))
			showBpl = true;
		else
			showBpl = false;
	}

	public void showDisability() {
		if (disability.equals("Yes"))
			showDis = true;
		else
			showDis = false;
	}

	public void removeDoc() {
		Connection con = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		int checkDocPresentInStudentTable = DBM.checkDocPresentInStudentTableMethod(selectedDoc, con);
		int checkDocPresentInRegistrtion = DBM.checkDocPresentInRegistrtionMehod(selectedDoc, con);
		if (checkDocPresentInStudentTable == 1) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Can't Remove, Document Already Present for Student"));
		} else if (checkDocPresentInRegistrtion == 1) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Can't Remove, Document Already Present for Student In Registation"));
		} else {

			int p = DBM.removeDocumentFormTable(selectedDoc, con);

			if (p >= 1) {
				String refNo;
				try {
					refNo = addWorkLog2(con);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document Removed Successfully"));

			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured"));

			}

			doctypelist = DBM.allDocType(con);
		}

		try {
			con.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String addWorkLog2(Connection conn) {
		String value = "";
		String language = "";

		language = value = "Document Id -" + selectedDoc.getValue().toString();

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language, "Remove Document", "WEB", value, conn);
		return refNo;
	}

	public ArrayList<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		studentList = new DatabaseMethods1().searchStudentList(query, conn);
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
	
	public void checkDob() {
		Connection conn = DataBaseConnection.javaConnection();
		 if(className1 != null) {
				if(schid.equals("302")||schid.equals("216")||schid.equals("221")) {
					int currYear = new Date().getYear()+1900;
					int selYear = dob.getYear()+1900;
					int diff = currYear - selYear;
					String classNameForSwitch = new DatabaseMethods1().classname(className1, schid, conn);
					FacesContext fs = FacesContext.getCurrentInstance();
					String msg = "";
					switch(classNameForSwitch.toLowerCase()) {
					case "playgroup":
						if(diff<2) 
							msg = "Student age should be 2+ year for admission in PLAYGROUP.";
						break;
					case "nursery" :
						if(diff<3) 
							msg = "Student age should be 3+ year for admission in NURSERY.";
						break;
					case "l.k.g." :
						if(diff<4) 
							msg = "Student age should be 4+ year for admission in L.K.G.";
						break;
					case "u.k.g." :
						if(diff<5) 
							msg = "Student age should be 5+ year for admission in U.K.G.";
						break;
					case "i" :
						if(diff<6) 
							msg = "Student age should be 6+ year for admission in I.";
						break;
					case "ii" :
						if(diff<7) 
							msg = "Student age should be 7+ year for admission in II.";
						break;
					case "iii" :
						if(diff<8) 
							msg = "Student age should be 8+ year for admission in III.";
						break;
					case "iv" :
						if(diff<9) 
							msg = "Student age should be 9+ year for admission in IV.";
						break;
					case "v" :
						if(diff<10) 
							msg = "Student age should be 10+ year for admission in V.";
						break;
					case "vi" :
						if(diff<11) 
							msg = "Student age should be 11+ year for admission in VI.";
						break;
					case "vii" :
						if(diff<12) 
							msg = "Student age should be 12+ year for admission in VII.";
						break;
					case "viii" :
						if(diff<13) 
							msg = "Student age should be 13+ year for admission in VIII.";
						break;
					case "ix" :
						if(diff<14) 
							msg = "Student age should be 14+ year for admission in IX.";
						break;
					case "x" :
						if(diff<15) 
							msg = "Student age should be 15+ year for admission in X.";
						break;
					case "xi" :
						if(diff<16) 
							msg = "Student age should be 16+ year for admission in XI.";
						break;
					case "xii" :
						if(diff<17) 
							msg = "Student age should be 17+ year for admission in XII.";
						break;
					}
					
					if(!msg.equals("")) {
						fs.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,msg.trim(),""));
					}
				}
		 }
		 
		 try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkSiblingClass() {
		DatabaseMethods1 DBMS = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();

		try {

			UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
			int dummyy = (int) component.getAttributes().get("Dummyy");
			for (StudentInfo ll : completeList) {
				if (ll.getSno() == dummyy) {
					int index = ll.getFname().lastIndexOf("-") + 1;
					String id = ll.getFname().substring(index);

					String classandSection = DBMS.studentClassSectionNameById(DBMS.schoolId(), id, conn);
					String[] split2 = classandSection.split(" - ");
					ll.setClassName(split2[0]);
					ll.setSectionName(split2[1]);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void checkSiblingClass1() {
		int index = sname1.lastIndexOf("-") + 1;
		String id = sname1.substring(index);
		if (index != 0) {
			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					sclassid1 = info.getSectionid();
					sClassName1 = info.getClassId();
				}
			}
		}
	}
	


	public void addHouse() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int j = obj.checkHouseCategory(newHouse, conn);
		if (j > 0) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "duplicate House Category Found ",
					"duplicate House Category Found"));

		} else {
			int i = obj.addhouse(newHouse, conn);
			if (i > 0) {
				String refNo;
				try {
					refNo = addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "House Category Added", "House Category Added"));
				houseCategorylist = obj.allHouseCategory(conn);

				newHouse = "";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some Error Occur Please try Again",
						"Some Error Occur Please try Again"));

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addWorkLog(Connection conn) {
		String value = "";
		String language = "";

		language = value = "House - " + newHouse;

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language, "Add House", "WEB", value, conn);
		return refNo;
	}

	public void addDocType() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int j = obj.checkDoctype(doctype, conn);
		if (j > 0) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "duplicate Document  Found ",
					"duplicate Document  Found"));

		} else {
			int i = obj.addDoctype(doctype, conn);
			if (i > 0) {
				String refNo;
				try {
					refNo = addWorkLog3(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Document Added", "Document Added"));
				doctypelist = obj.allDocType(conn);

				doctype = "";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some Error Occur Please try Again",
						"Some Error Occur Please try Again"));

			}
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

		language = value = "Document - " + doctype;

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language, "Add Document", "WEB", value, conn);
		return refNo;
	}

	public void checkSiblingClass2() {
		int index = sname2.lastIndexOf("-") + 1;
		String id = sname2.substring(index);
		if (index != 0) {
			for (StudentInfo info : studentList) {
				if (String.valueOf(info.getAddNumber()).equals(id)) {
					sclassid2 = info.getSectionid();
					sClassName2 = info.getClassId();
				}
			}
		}
	}

	public String reg2() {
		DatabaseMethods1 DBM = new DatabaseMethods1();

		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info = DBM.fullSchoolInfo(conn);

		int checker = DBM.checkingForDuplAdmNoAllowed(conn);
		int status = 0;
		if (checker == 1) {
			if (!addmissionNumber.trim().equalsIgnoreCase(""))
				status = DBM.duplicateStudentEntry(DBM.schoolId(), addmissionNumber, conn);
		}
		if (status == 1) {
			studentImage=null;
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate admission Number found,try a different one",
							"Duplicate admission Number found,try a different one"));
		} else {
			int agreement = DBM.checkAgreementFor(DBM.schoolId(), conn);
			int currentStrength = Integer.parseInt(DBM.allStudentcount(schid, "", "", session, "", conn));

			if (agreement < 500) {
				if (currentStrength >= (agreement + 25)) {
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.",
							"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
					return "";
				}
			} else {
				if (currentStrength >= (agreement + 50)) {
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.",
							"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
					return "";
				}
			}

			String startSession = (new DatabaseMethods1().selectedSessionDetails(DBM.schoolId(), conn)).split("-")[0];
			boolean check = true;
			if (studentstatus.equalsIgnoreCase("New")) {
				String[] sesion = (DatabaseMethods1.selectedSessionDetails(DBM.schoolId(), conn)).split("-");

				Date startdate = null;
				Date endDate = null;
				try {
					if (info.getSchoolSession().equals("4-3")) {
						startdate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + sesion[0]);
						endDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/04/" + sesion[1]);
					} else {
						startdate = new SimpleDateFormat("dd/MM/yyyy").parse("30/04/" + sesion[0]);
						endDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/05/" + sesion[1]);
					}

				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				if (addmissionDate.after(startdate) && addmissionDate.before(endDate)) {
					check = true;
				} else {
					check = false;
				}

			}
			check = true;

			if (check == true) {

				srnoType = info.getSrnoType();

				if (srnoType.equalsIgnoreCase("auto")) {
					String ssn = StringUtils.right(startSession, 2);
					boolean checkStu = DBM.checkStudentsInSchool(info.getSchid(), conn);
					if (checkStu == false) {
						if (info.getBranch_id().equalsIgnoreCase("22")) {
							String house = DBM.houseNameById(houseName, conn);
							String hsnm = StringUtils.left(house, 2);
							addmissionNumber = info.getSrnoPrefix() + hsnm + "/" + info.getSrnoStart() + "/" + ssn;
						} else {
							addmissionNumber = info.getSrnoPrefix() + info.getSrnoStart();
						}
					} else {
						if (info.getBranch_id().equalsIgnoreCase("22")) {
							String house = DBM.houseNameById(houseName, conn);
							String hsnm = StringUtils.left(house, 2);
							// addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+""+"/"+ssn;
							String prefix = info.getSrnoPrefix() + hsnm + "/";
							addmissionNumber = prefix + new DatabaseMethods1().autoGeneratedSrNo(info.getSchid(),
									(prefix.length() + 1), conn) + "/" + ssn;
						} else {
							addmissionNumber = info.getSrnoPrefix()
									+ DBM.autoGeneratedSrNo(info.getSchid(), (info.getSrnoPrefix().length() + 1), conn);
						}
					}
				}
				conceesionCategory = DBM.concessionCategoryNameById(DBM.schoolId(), concession, conn);
				if (conceesionCategory.equalsIgnoreCase("General")) {
					concessionStatus = "accepted";
				} else {
					if (info.getConcessionRequest().equals("no")) {
						concessionStatus = "accepted";
					} else {
						concessionStatus = "pending";
					}

				}

				if (discountFee == null || discountFee.equals("")) {
					discountFee = "0";
				}

				boolean duplicate = new DataBaseValidator().checkDuplicateRollNo(rollNo, selectedSection, session,
						schid, "", conn);
				if (duplicate == true && !rollNo.equals("")) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate Roll No Found.... Please Try With Another One.", "Validation error"));
				} else {
					
					 if(village != null) {
						 village = village.toUpperCase();
					 }

					
					int i = DBM.studentRegistration(admRemark, schid, addmissionDate, fname.trim(), dob,
							/* className1, */selectedSection, aadharNo, fathersPhone, currAdd, perAdd, gender,
							nationality, religion, category, bpl, bplCardNo, pincode, singleChild, SingleParent, caste,
							country, ffname, mname, fatherEmail, fatherAadharNo, motherAadhaar, mothersPhone,
							residencePhone, livingWith, lastSchoolName, passedClass, medium, pResult, boardName,
							p_percent, pReason/* ,tcDate */, height, weight, eyes, fname1, relation1, occupation1,
							phone1, address1, fname2, relation2, occupation2, phone2, address2, fatherQualification,
							fathersOccupation, fatherOfficeAdd, fatherSchoolEmp, motherQualification, motherOccupation,
							motherOfficeAdd, motherSchoolEmp, routeName1, concession, hostal, houseName, sname1,
							sClassName1, sname2, sClassName2, documentsSubmitted, studentImage, fatherImage,
							motherImage, g1Image, g2Image, motherEmail, bloodGroup/* ,"","" */
							, fatherofficecontcatno, motherofficecontactno, studentstatus, rollNo, discountFee,
							disability, handicap, conn, studentPhone, addmissionNumber, concessionStatus, "temp",
							className1, regNo_IX, regNo_XI, admnFileNo, ledgNo, selectedSectionForAdmitClass,
							fahterAnnualIncome, motherAnnualIncome, motherDesignation, fatherDesignation,tenRoll,tenYearOfPass,tenBoard,minority,village);
					
					if (i >= 1) {
						int maxnumber = i;
						cbNumber = String.valueOf(maxnumber);
						DBM.updateStudentId("CB" + String.valueOf(maxnumber), i, conn);
						String password = DBM.randomAlphaNumeric(8);
						DBM.insertEmployeeInLogin("CB" + String.valueOf(maxnumber), password, "student", DBM.schoolId(),
								conn);

						if (routeName1.equals("")) {
							DBM.transportDataEntry(DBM.schoolId(), addmissionDate, "CB" + String.valueOf(maxnumber),
									routeName1, "No", className1,conn);
						} else {
							DBM.transportDataEntry(DBM.schoolId(), addmissionDate, "CB" + String.valueOf(maxnumber),
									routeName1, "Yes", className1,conn);
						}

						int count = 0;
						String addNumber = "", schid = DBM.schoolId();
						for (StudentInfo ll : completeList) {
							if (!ll.getFname().equals("")) {
								addNumber = ll.getFname().substring(ll.getFname().lastIndexOf("-") + 1);
								ll.setAddNumber(addNumber);
								count++;
							}
						}
						if (count > 0) {
							new DatabaseMethods1().submitSiblingValue(schid, "CB" + String.valueOf(maxnumber),
									completeList, conn);
							completeList = new ArrayList<>();
							for (int k = 1; i <= 5; i++) {
								StudentInfo ll = new StudentInfo();
								ll.setSno(k);
								ll.setAddNumber("");
								completeList.add(ll);
							}
						}

						new DataBaseMeathodJson().addclassAttendanceINNew(selectedSection, new Date(), DBM.schoolId(),
								conn);
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Student Added Successfully",
								"Student Added Successfully"));
						// new DatabaseMethods1().addUserName(addmissionNumber,"123456","student");
						DBM.increaseStudentInAddSchool(DBM.schoolId(), conn);
						String className = DBM.classNameFromidSchid(DBM.schoolId(), className1,
								DatabaseMethods1.selectedSessionDetails(DBM.schoolId(), conn), conn);
						classTestList = DBM.selectedClassTestList(DBM.schoolId(), className1, selectedSection, conn);
						examList = DBM.selectedClassExamList(DBM.schoolId(), selectedSection, conn);
						clsName = className;
						schName = info.getSchoolName();

						for (ClassTest ct : classTestList) {
							testStatus = DBM.checkClassTestPerformanceStatus(ct.getId(), conn);
							if (testStatus == true) {
								new DataBaseOnlineAdm().entryOfNewStudentInClassTestPerformanceSession(DBM.schoolId(),
										DatabaseMethods1.selectedSessionDetails(DBM.schoolId(), conn),
										"CB" + String.valueOf(maxnumber), ct.getId(), conn);
							} else {

							}
						}
						for (ExamInfo ee : examList) {
							new DataBaseOnlineAdm().entryOfNewStudentInExamPerformanceSession(DBM.schoolId(),
									DatabaseMethods1.selectedSessionDetails(DBM.schoolId(), conn),
									"CB" + String.valueOf(maxnumber), ee.getClassid(), ee.getSubjectid(),
									ee.getSemesterid(), ee.getExamid(), ee.getExamType(), conn, ee.getMaxMark(),
									ee.getExamName());
						}

						HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
								.getSession(false);
						ss.setAttribute("addNo", "CB" + String.valueOf(maxnumber));
						ss.setAttribute("addNumber", "CB" + String.valueOf(maxnumber));
						ss.setAttribute("name", fname);
						ss.setAttribute("selectedClass", selectedSection);
						PrimeFaces.current().executeInitScript("window.open('printNewRegistrationInfo.xhtml')");
						if (info.getBranch_id().equalsIgnoreCase("22") || info.getBranch_id().equalsIgnoreCase("27")) {
							PrimeFaces.current().executeInitScript("window.open('printWelcomeLetter.xhtml')");
						}
						
						if (schid.equals("216") || schid.equals("302") || schid.equals("221")) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
							String sectName = DBM.sectionNameByIdSchid(schid, selectedSection, conn);
							String clsName = DBM.classNameFromidSchid(schid, className1, session, conn);
							String notificationText = "Dear Sir/ Madam,\n" + 
									"New admission is confirmed in class "+clsName+" section "+sectName+". Details are as follows: -\n" + 
									"Name: - "+fname+"\n" + 
									"Father's Name: - "+ffname+"\n" + 
									"Father's Mobile Number: - "+fathersPhone+"\n" + 
									"Mother's Name: - "+mname+"\n" + 
									"Mother's Mobile Number: - "+mothersPhone+"\n" + 
									"Date of Admission: - "+sdf.format(addmissionDate)+"\n" + 
									"Date of Birth: - "+sdf.format(dob)+"\n" + 
									"Address: - "+perAdd+"--"+currAdd+"\n" + 
									"\n" + 
									"Kindly co- ordinate with the parents and enroll name in school records.\n" + 
									"Regards\n" + 
									"\n" + 
									info.getSmsSchoolName();

							sendNotification(notificationText, schid, className1, selectedSection, session);
						}
						
						if (message.equals("true")) {
							String typeMessage = "";
							double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
							if (balance > 0 && balance <= smsLimit) {
								balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
										+ "We suggest you to top-up your account today to ensure uninterrupted activity";
								if (type.equalsIgnoreCase("admin")) {
									PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
									PrimeFaces.current().ajax().update("MsgLimitForm");
									try {
										conn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
									return "";
								}
							} else if (balance <= 0) {
								balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
								if (type.equalsIgnoreCase("admin")) {
									PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
									PrimeFaces.current().ajax().update("MsgOverForm");

								} else {
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

							if (String.valueOf(fathersPhone).length() == 10
									&& !String.valueOf(fathersPhone).equals("2222222222")
									&& !String.valueOf(fathersPhone).equals("9999999999")
									&& !String.valueOf(fathersPhone).equals("1111111111")
									&& !String.valueOf(fathersPhone).equals("1234567890")
									&& !String.valueOf(fathersPhone).equals("0123456789")) {
								if (info.getSchoolAppName().equalsIgnoreCase("N/A")) {
									typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
											+ fname + " in class " + className
											+ ". Now you can access your ward's information on your mobile." + "\n"
											+ info.getSmsSchoolName();

								} else {
									typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
											+ fname + " in class " + className
											+ ". Now you can access your ward's information on your mobile. Please search "
											+ info.getSchoolAppName()
											+ " on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"
											+ "\n" + "Regards\n" + info.getSmsSchoolName();

								}

								String templateId = new DataBaseMeathodJson().templateId(info.getKey(), "ADDMISSION",
										conn);

								// DBM.messageurl1(String.valueOf(fathersPhone),
								// typeMessage,"CB"+String.valueOf(maxnumber),conn,DBM.schoolId());
								new DataBaseMeathodJson().messageUrlWithTemplate(String.valueOf(fathersPhone),
										typeMessage, "CB" + String.valueOf(maxnumber), DBM.schoolId(), conn,
										templateId);

							}

						}

						// new DatabaseMethods1().registrationStep2(addmissionNumber);
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return "registration1";
					}
				}
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"If Student Is New.. Then Admission Date Must Be In Currently Running Session",
								"Validation error"));
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public void sendNotification(String notificationText, String schoolId, String classNameid, String selectedSectionid,
			String sessionValue) {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 dbmnew = new DatabaseMethods1();
		dbmnew.adminnotification("Registration-Confirm", notificationText, "admin-" + schoolId,"", conn);
		String classTeacherId = dbmnew.getClassTeacherId(classNameid, selectedSectionid, schoolId, sessionValue, conn);
		ArrayList<EmployeeInfo> principleId = dbmnew.getPrincipleId(schid, conn);
		if(classTeacherId != null && !classTeacherId.equals("")) {
			dbmnew.adminnotification("Registration-Confirm", notificationText, "staff-" + classTeacherId + "-" + schoolId,"", conn);
		}
		for (EmployeeInfo a : principleId) {
			if(!Integer.toString(a.getId()).equals("") && Integer.toString(a.getId()) != null) {
				dbmnew.adminnotification("Registration-Confirm", notificationText, "staff-" + a.getId() + "-" + schoolId,"", conn);
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String sendMsg() {
		Connection conn = DataBaseConnection.javaConnection();
		if (message.equals("true")) {
			String typeMessage = "";
			if (String.valueOf(fathersPhone).length() == 10 && !String.valueOf(fathersPhone).equals("2222222222")
					&& !String.valueOf(fathersPhone).equals("9999999999")
					&& !String.valueOf(fathersPhone).equals("1111111111")
					&& !String.valueOf(fathersPhone).equals("1234567890")
					&& !String.valueOf(fathersPhone).equals("0123456789")) {
				if (info.getSchoolAppName().equalsIgnoreCase("N/A")) {
					typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward " + fname + " in class "
							+ clsName + ". Now you can access your ward's information on your mobile." + "\n"
							+ "Regards\n" + info.getSmsSchoolName();

				} else {
					typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward " + fname + " in class "
							+ clsName + ". Now you can access your ward's information on your mobile. Please search "
							+ info.getSchoolAppName()
							+ " on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"
							+ "\n" + "Regards\n" + info.getSmsSchoolName();

				}
				new DatabaseMethods1().messageurl1(String.valueOf(fathersPhone), typeMessage, "CB" + cbNumber, conn,
						new DatabaseMethods1().schoolId(),"");

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "registration1";
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList<SelectItem> getDiscountList() {
		return discountList;
	}

	public void setDiscountList(ArrayList<SelectItem> discountList) {
		this.discountList = discountList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getFathersPhone() {
		return fathersPhone;
	}

	public void setFathersPhone(long fathersPhone) {
		this.fathersPhone = fathersPhone;
	}

	public long getMothersPhone() {
		return mothersPhone;
	}

	public void setMothersPhone(long mothersPhone) {
		this.mothersPhone = mothersPhone;
	}

	public Date getAddmissionDate() {
		return addmissionDate;
	}

	public void setAddmissionDate(Date addmissionDate) {
		this.addmissionDate = addmissionDate;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getFfname() {
		return ffname;
	}

	public void setFfname(String ffname) {
		this.ffname = ffname;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getClassName1() {
		return className1;
	}

	public void setClassName1(String className1) {
		this.className1 = className1;
	}

	public String getRouteName1() {
		return routeName1;
	}

	public void setRouteName1(String routeName1) {
		this.routeName1 = routeName1;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getCurrAdd() {
		return currAdd;
	}

	public void setCurrAdd(String currAdd) {
		this.currAdd = currAdd;
	}

	public String getPerAdd() {
		return perAdd;
	}

	public void setPerAdd(String perAdd) {
		this.perAdd = perAdd;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<SelectItem> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<SelectItem> routeList) {
		this.routeList = routeList;
	}

	public List<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(List<SelectItem> classList) {
		this.classList = classList;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAddmissionNumber() {
		return addmissionNumber;
	}

	public void setAddmissionNumber(String addmissionNumber) {
		this.addmissionNumber = addmissionNumber;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public UploadedFile getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(UploadedFile studentImage) {
		this.studentImage = studentImage;
	}

	public String getResidencePhone() {
		return residencePhone;
	}

	public void setResidencePhone(String residencePhone) {
		this.residencePhone = residencePhone;
	}

	public String getFathersOccupation() {
		return fathersOccupation;
	}

	public void setFathersOccupation(String fathersOccupation) {
		this.fathersOccupation = fathersOccupation;
	}

	public String getBpl() {
		return bpl;
	}

	public void setBpl(String bpl) {
		this.bpl = bpl;
	}

	public String getBplCardNo() {
		return bplCardNo;
	}

	public void setBplCardNo(String bplCardNo) {
		this.bplCardNo = bplCardNo;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(String caste) {
		this.caste = caste;
	}

	public String getSingleChild() {
		return singleChild;
	}

	public void setSingleChild(String singleChild) {
		this.singleChild = singleChild;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getSingleParent() {
		return SingleParent;
	}

	public void setSingleParent(String singleParent) {
		SingleParent = singleParent;
	}

	public String getLivingWith() {
		return livingWith;
	}

	public void setLivingWith(String livingWith) {
		this.livingWith = livingWith;
	}

	public String getFatherEmail() {
		return fatherEmail;
	}

	public void setFatherEmail(String fatherEmail) {
		this.fatherEmail = fatherEmail;
	}

	public String getMotherEmail() {
		return motherEmail;
	}

	public void setMotherEmail(String motherEmail) {
		this.motherEmail = motherEmail;
	}

	public boolean isShowBpl() {
		return showBpl;
	}

	public void setShowBpl(boolean showBpl) {
		this.showBpl = showBpl;
	}

	public String getConcession() {
		return concession;
	}

	public void setConcession(String concession) {
		this.concession = concession;
	}

	public UploadedFile getFatherImage() {
		return fatherImage;
	}

	public void setFatherImage(UploadedFile fatherImage) {
		this.fatherImage = fatherImage;
	}

	public UploadedFile getMotherImage() {
		return motherImage;
	}

	public void setMotherImage(UploadedFile motherImage) {
		this.motherImage = motherImage;
	}

	public UploadedFile getG1Image() {
		return g1Image;
	}

	public void setG1Image(UploadedFile g1Image) {
		this.g1Image = g1Image;
	}

	public UploadedFile getG2Image() {
		return g2Image;
	}

	public void setG2Image(UploadedFile g2Image) {
		this.g2Image = g2Image;
	}

	public String getSendMessageMobileNo() {
		return sendMessageMobileNo;
	}

	public void setSendMessageMobileNo(String sendMessageMobileNo) {
		this.sendMessageMobileNo = sendMessageMobileNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFname1() {
		return fname1;
	}

	public void setFname1(String fname1) {
		this.fname1 = fname1;
	}

	public String getLname1() {
		return lname1;
	}

	public void setLname1(String lname1) {
		this.lname1 = lname1;
	}

	public String getRelation1() {
		return relation1;
	}

	public void setRelation1(String relation1) {
		this.relation1 = relation1;
	}

	public String getOccupation1() {
		return occupation1;
	}

	public void setOccupation1(String occupation1) {
		this.occupation1 = occupation1;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getFname2() {
		return fname2;
	}

	public void setFname2(String fname2) {
		this.fname2 = fname2;
	}

	public String getLname2() {
		return lname2;
	}

	public void setLname2(String lname2) {
		this.lname2 = lname2;
	}

	public String getRelation2() {
		return relation2;
	}

	public void setRelation2(String relation2) {
		this.relation2 = relation2;
	}

	public String getOccupation2() {
		return occupation2;
	}

	public void setOccupation2(String occupation2) {
		this.occupation2 = occupation2;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getLastSchoolName() {
		return lastSchoolName;
	}

	public void setLastSchoolName(String lastSchoolName) {
		this.lastSchoolName = lastSchoolName;
	}

	public String getPassedClass() {
		return passedClass;
	}

	public void setPassedClass(String passedClass) {
		this.passedClass = passedClass;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getSname1() {
		return sname1;
	}

	public void setSname1(String sname1) {
		this.sname1 = sname1;
	}

	public String getSname2() {
		return sname2;
	}

	public void setSname2(String sname2) {
		this.sname2 = sname2;
	}

	public String getSclassid1() {
		return sclassid1;
	}

	public void setSclassid1(String sclassid1) {
		this.sclassid1 = sclassid1;
	}

	public String getSclassid2() {
		return sclassid2;
	}

	public void setSclassid2(String sclassid2) {
		this.sclassid2 = sclassid2;
	}

	public String getsClassName1() {
		return sClassName1;
	}

	public void setsClassName1(String sClassName1) {
		this.sClassName1 = sClassName1;
	}

	public String getsClassName2() {
		return sClassName2;
	}

	public void setsClassName2(String sClassName2) {
		this.sClassName2 = sClassName2;
	}

	public ArrayList<String> getDocumentsSubmitted() {
		return documentsSubmitted;
	}

	public void setDocumentsSubmitted(ArrayList<String> documentsSubmitted) {
		this.documentsSubmitted = documentsSubmitted;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getpResult() {
		return pResult;
	}

	public void setpResult(String pResult) {
		this.pResult = pResult;
	}

	public String getP_percent() {
		return p_percent;
	}

	public void setP_percent(String p_percent) {
		this.p_percent = p_percent;
	}

	public String getpReason() {
		return pReason;
	}

	public void setpReason(String pReason) {
		this.pReason = pReason;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getEyes() {
		return eyes;
	}

	public void setEyes(String eyes) {
		this.eyes = eyes;
	}

	public String getFatherQualification() {
		return fatherQualification;
	}

	public void setFatherQualification(String fatherQualification) {
		this.fatherQualification = fatherQualification;
	}

	public String getMotherQualification() {
		return motherQualification;
	}

	public void setMotherQualification(String motherQualification) {
		this.motherQualification = motherQualification;
	}

	public String getMotherOccupation() {
		return motherOccupation;
	}

	public void setMotherOccupation(String motherOccupation) {
		this.motherOccupation = motherOccupation;
	}

	public String getFatherDesignation() {
		return fatherDesignation;
	}

	public void setFatherDesignation(String fatherDesignation) {
		this.fatherDesignation = fatherDesignation;
	}

	public String getMotherDesignation() {
		return motherDesignation;
	}

	public void setMotherDesignation(String motherDesignation) {
		this.motherDesignation = motherDesignation;
	}

	public String getFatherOrganization() {
		return fatherOrganization;
	}

	public void setFatherOrganization(String fatherOrganization) {
		this.fatherOrganization = fatherOrganization;
	}

	public String getMotherOrganization() {
		return motherOrganization;
	}

	public void setMotherOrganization(String motherOrganization) {
		this.motherOrganization = motherOrganization;
	}

	public String getFatherOfficeAdd() {
		return fatherOfficeAdd;
	}

	public void setFatherOfficeAdd(String fatherOfficeAdd) {
		this.fatherOfficeAdd = fatherOfficeAdd;
	}

	public String getMotherOfficeAdd() {
		return motherOfficeAdd;
	}

	public void setMotherOfficeAdd(String motherOfficeAdd) {
		this.motherOfficeAdd = motherOfficeAdd;
	}

	public String getFatherIncome() {
		return fatherIncome;
	}

	public void setFatherIncome(String fatherIncome) {
		this.fatherIncome = fatherIncome;
	}

	public String getMotherIncome() {
		return motherIncome;
	}

	public void setMotherIncome(String motherIncome) {
		this.motherIncome = motherIncome;
	}

	public String getFatherAadhaar() {
		return FatherAadhaar;
	}

	public void setFatherAadhaar(String fatherAadhaar) {
		FatherAadhaar = fatherAadhaar;
	}

	public String getMotherAadhaar() {
		return motherAadhaar;
	}

	public void setMotherAadhaar(String motherAadhaar) {
		this.motherAadhaar = motherAadhaar;
	}

	public String getFatherSchoolEmp() {
		return fatherSchoolEmp;
	}

	public void setFatherSchoolEmp(String fatherSchoolEmp) {
		this.fatherSchoolEmp = fatherSchoolEmp;
	}

	public String getMotherSchoolEmp() {
		return motherSchoolEmp;
	}

	public void setMotherSchoolEmp(String motherSchoolEmp) {
		this.motherSchoolEmp = motherSchoolEmp;
	}

	public Date getTcDate() {
		return tcDate;
	}

	public void setTcDate(Date tcDate) {
		this.tcDate = tcDate;
	}

	public String getFatherAadharNo() {
		return fatherAadharNo;
	}

	public void setFatherAadharNo(String fatherAadharNo) {
		this.fatherAadharNo = fatherAadharNo;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getHostal() {
		return hostal;
	}

	public void setHostal(String hostal) {
		this.hostal = hostal;
	}

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}

	public List<SelectItem> getConnsessionList() {
		return connsessionList;
	}

	public void setConnsessionList(List<SelectItem> connsessionList) {
		this.connsessionList = connsessionList;
	}

	public String getRouteFees() {
		return routeFees;
	}

	public void setRouteFees(String routeFees) {
		this.routeFees = routeFees;
	}

	public String getDiscountFee() {
		return discountFee;
	}

	public void setDiscountFee(String discountFee) {
		this.discountFee = discountFee;
	}

	public String getTotalFess() {
		return TotalFess;
	}

	public void setTotalFess(String totalFess) {
		TotalFess = totalFess;
	}

	public ArrayList<SelectItem> getReligionList() {
		return religionList;
	}

	public void setReligionList(ArrayList<SelectItem> religionList) {
		this.religionList = religionList;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public ArrayList<SelectItem> getHandicaplist() {
		return handicaplist;
	}

	public void setHandicaplist(ArrayList<SelectItem> handicaplist) {
		this.handicaplist = handicaplist;
	}

	public String getStudentstatus() {
		return studentstatus;
	}

	public void setStudentstatus(String studentstatus) {
		this.studentstatus = studentstatus;
	}

	public ArrayList<SelectItem> getHouseCategorylist() {
		return houseCategorylist;
	}

	public void setHouseCategorylist(ArrayList<SelectItem> houseCategorylist) {
		this.houseCategorylist = houseCategorylist;
	}

	public String getDisability() {
		return disability;
	}

	public void setDisability(String disability) {
		this.disability = disability;
	}

	public String getHandicap() {
		return handicap;
	}

	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}

	public String getNewHouse() {
		return newHouse;
	}

	public void setNewHouse(String newHouse) {
		this.newHouse = newHouse;
	}

	public String getMotherofficecontactno() {
		return motherofficecontactno;
	}

	public void setMotherofficecontactno(String motherofficecontactno) {
		this.motherofficecontactno = motherofficecontactno;
	}

	public String getFatherofficecontcatno() {
		return fatherofficecontcatno;
	}

	public void setFatherofficecontcatno(String fatherofficecontcatno) {
		this.fatherofficecontcatno = fatherofficecontcatno;
	}

	public ArrayList<SelectItem> getDoctypelist() {
		return doctypelist;
	}

	public void setDoctypelist(ArrayList<SelectItem> doctypelist) {
		this.doctypelist = doctypelist;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public boolean isShowDis() {
		return showDis;
	}

	public void setShowDis(boolean showDis) {
		this.showDis = showDis;
	}

	public double getHostelfees() {
		return hostelfees;
	}

	public void setHostelfees(double hostelfees) {
		this.hostelfees = hostelfees;
	}

	public String getStudentPhone() {
		return studentPhone;
	}

	public void setStudentPhone(String studentPhone) {
		this.studentPhone = studentPhone;
	}

	public boolean isShowInstitute() {
		return showInstitute;
	}

	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
	}

	public ArrayList<ClassTest> getClassTestList() {
		return classTestList;
	}

	public void setClassTestList(ArrayList<ClassTest> classTestList) {
		this.classTestList = classTestList;
	}

	public ArrayList<ExamInfo> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<ExamInfo> examList) {
		this.examList = examList;
	}

	public boolean isTestStatus() {
		return testStatus;
	}

	public void setTestStatus(boolean testStatus) {
		this.testStatus = testStatus;
	}

	public boolean isExamStatus() {
		return examStatus;
	}

	public void setExamStatus(boolean examStatus) {
		this.examStatus = examStatus;
	}

	public String getConceesionCategory() {
		return conceesionCategory;
	}

	public void setConceesionCategory(String conceesionCategory) {
		this.conceesionCategory = conceesionCategory;
	}

	public String getConcessionStatus() {
		return concessionStatus;
	}

	public void setConcessionStatus(String concessionStatus) {
		this.concessionStatus = concessionStatus;
	}

	public String getSrnoType() {
		return srnoType;
	}

	public void setSrnoType(String srnoType) {
		this.srnoType = srnoType;
	}

	public String getSrnoPrefix() {
		return srnoPrefix;
	}

	public void setSrnoPrefix(String srnoPrefix) {
		this.srnoPrefix = srnoPrefix;
	}

	public String getSrnoStart() {
		return srnoStart;
	}

	public void setSrnoStart(String srnoStart) {
		this.srnoStart = srnoStart;
	}

	public boolean isDisableSrNo() {
		return disableSrNo;
	}

	public void setDisableSrNo(boolean disableSrNo) {
		this.disableSrNo = disableSrNo;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getAdmRemark() {
		return admRemark;
	}

	public void setAdmRemark(String admRemark) {
		this.admRemark = admRemark;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public ArrayList<StudentInfo> getCompleteList() {
		return completeList;
	}

	public void setCompleteList(ArrayList<StudentInfo> completeList) {
		this.completeList = completeList;
	}

	public String getRegNo_IX() {
		return regNo_IX;
	}

	public void setRegNo_IX(String regNo_IX) {
		this.regNo_IX = regNo_IX;
	}

	public String getRegNo_XI() {
		return regNo_XI;
	}

	public void setRegNo_XI(String regNo_XI) {
		this.regNo_XI = regNo_XI;
	}

	public String getLedgNo() {
		return ledgNo;
	}

	public void setLedgNo(String ledgNo) {
		this.ledgNo = ledgNo;
	}

	public String getAdmnFileNo() {
		return admnFileNo;
	}

	public void setAdmnFileNo(String admnFileNo) {
		this.admnFileNo = admnFileNo;
	}

	public String getSelectedSectionForAdmitClass() {
		return selectedSectionForAdmitClass;
	}

	public void setSelectedSectionForAdmitClass(String selectedSectionForAdmitClass) {
		this.selectedSectionForAdmitClass = selectedSectionForAdmitClass;
	}

	public SelectItem getSelectedDoc() {
		return selectedDoc;
	}

	public void setSelectedDoc(SelectItem selectedDoc) {
		this.selectedDoc = selectedDoc;
	}

	public String getFahterAnnualIncome() {
		return fahterAnnualIncome;
	}

	public void setFahterAnnualIncome(String fahterAnnualIncome) {
		this.fahterAnnualIncome = fahterAnnualIncome;
	}

	public String getMotherAnnualIncome() {
		return motherAnnualIncome;
	}

	public void setMotherAnnualIncome(String motherAnnualIncome) {
		this.motherAnnualIncome = motherAnnualIncome;
	}

	public String getTenRoll() {
		return tenRoll;
	}

	public void setTenRoll(String tenRoll) {
		this.tenRoll = tenRoll;
	}

	public String getTenYearOfPass() {
		return tenYearOfPass;
	}

	public void setTenYearOfPass(String tenYearOfPass) {
		this.tenYearOfPass = tenYearOfPass;
	}

	public String getTenBoard() {
		return tenBoard;
	}

	public void setTenBoard(String tenBoard) {
		this.tenBoard = tenBoard;
	}

	public String getMinority() {
		return minority;
	}

	public void setMinority(String minority) {
		this.minority = minority;
	}

	public boolean isShowminority() {
		return showminority;
	}

	public void setShowminority(boolean showminority) {
		this.showminority = showminority;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}
	
	
	
}
