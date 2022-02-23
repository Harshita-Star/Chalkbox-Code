package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import exam_module.ExamInfo;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;
@ManagedBean(name="viewInstituteEnquiry")
@ViewScoped
public class ViewInstituteEnquiry implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo1> leadList, historyInfo, overdueLeadList,allEnquiry;
	ArrayList<SelectItem> empList, connsessionList, sectionList, classList;
	StudentInfo1 selectedEnquiry;
	String searchType, description, totalOverdue, userId, type, status, selectedEmp, message="false",cbNumber,clsName,balMsg;
	Date searchDate = new Date(), followDate, todayDate = new Date(), addmissionDate = new Date(),dob;
	boolean check, showEmp, testStatus = false, examStatus = false;
	DatabaseMethods1 obj = new DatabaseMethods1();
	UploadedFile fatherImage, motherImage, g1Image, g2Image, studentImage;
	String country, addmissionNumber, className1, selectedSection, routeName1 = "", routeFees = "0", discountFee = "0",
			totalFees = "0";
	ArrayList<String> documentsSubmitted = new ArrayList<>();
	ArrayList<ClassTest> classTestList;
	ArrayList<ExamInfo> examList;
	double smsLimit;
	String schid,session;

	public ViewInstituteEnquiry()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId = (String) ss.getAttribute("username");
		type = (String) ss.getAttribute("type");
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		if (type.equalsIgnoreCase("master admin") || type.equalsIgnoreCase("admin"))
		{
			selectedEmp = "all";
			showEmp = true;
		} else {
			selectedEmp = userId;
			showEmp = false;
		}
		empList = obj.allEmployeeList(conn);

		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		classList = obj.allClass(conn);
		connsessionList = obj.allConnsessionType(conn);
        allEnquiry=obj.enquiryForFollowUpInstituteALL(selectedEmp, conn);
		searchData();

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		sectionList = obj.allSection(className1, conn);
		selectedSection = (String) sectionList.get(0).getValue();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addLead() {
		PrimeFaces.current().executeInitScript("window.open('enquiryForm.xhtml')");
	}

	public void searchData() {
		searchType = "particular";
		if (selectedEmp.equals("Self")) {
			selectedEmp = userId;
		}
		search();
	}

	@SuppressWarnings("deprecation")
	public void search() {
		Connection conn = DataBaseConnection.javaConnection();
		Timestamp ss1 = new Timestamp(searchDate.getTime());
		ss1.setHours(0);
		ss1.setMinutes(0);
		ss1.setSeconds(0);
		ss1.setNanos(0);
		Date dd = new Date();
		Timestamp ss2 = new Timestamp(dd.getTime());
		ss2.setHours(0);
		ss2.setMinutes(0);
		ss2.setSeconds(0);
		ss2.setNanos(0);

		if (ss2.equals(ss1)) {
			check = true;
		} else {
			check = false;
		}
		leadList = obj.enquiryForFollowUpInstitute(searchDate, selectedEmp, conn);
		overdueLeadList = obj.overDueEnquiryInstitute(searchDate, selectedEmp, conn);
		totalOverdue = String.valueOf(overdueLeadList.size());
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void checkHistory() {
		Connection conn = DataBaseConnection.javaConnection();
		historyInfo = obj.historyOfEnquiryInstitute(selectedEnquiry.getEnquiryId(), conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void addFollowUp() {
		Connection conn = DataBaseConnection.javaConnection();
		String id = selectedEnquiry.getId();


		Timestamp fDate = new Timestamp(followDate.getTime());
		int i = obj.addFollowUpInstitute(selectedEnquiry.getEnquiryId(), fDate, description, status, userId, id,"pending", conn);
		if (i > 0) {
			search();
			description = "";
			followDate = null;
			status = "";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Follow Up Added successfully"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error occur Please try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void acceptEnquiry() {
		Connection conn = DataBaseConnection.javaConnection();

		int agreement = obj.checkAgreementFor(schid, conn);
		int currentStrength = Integer.parseInt(obj.allStudentcount(schid,"", "",session,"", conn));

		if(agreement<500)
		{
			if(currentStrength>=(agreement+25))
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.","You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
			}
			else
			{
				addStudent();
			}
		}
		else
		{
			if(currentStrength>=(agreement+50))
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.","You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
			}
			else
			{
				addStudent();
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void addStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyy");

		SchoolInfoList info = obj.fullSchoolInfo(conn);
		String concession = (String) connsessionList.get(0).getValue();
		String studentstatus = "new";

		boolean check = true;
		if (studentstatus.equalsIgnoreCase("New")) {
			String[] sesion = (DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn)).split("-");

			Date startdate = null;
			Date endDate = null;
			try {
				if (info.getSchoolSession().equals("4-3")) {
					startdate = sdf.parse("31/03/" + sesion[0]);
					endDate = sdf.parse("01/04/" + sesion[1]);
				} else {
					startdate = sdf.parse("30/04/" + sesion[0]);
					endDate = sdf.parse("01/05/" + sesion[1]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (addmissionDate.after(startdate) && addmissionDate.before(endDate)) {
				check = true;
			} else {
				check = false;
			}

		}
		
		int checker = obj.checkingForDuplAdmNoAllowed(conn);
		int status = 0;
		if (checker == 1) {
			if (!addmissionNumber.trim().equalsIgnoreCase(""))
				status = obj.duplicateStudentEntry(obj.schoolId(), addmissionNumber, conn);
		}
		
		if (status == 1) {
			studentImage=null;
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate admission Number found,try a different one",
							"Duplicate admission Number found,try a different one"));
		}
		else
		{
			if (check == true) {
				int i = obj.studentRegistration(" ",obj.schoolId(),addmissionDate, selectedEnquiry.getStudentName(), dob,
						selectedSection, "", Long.valueOf(selectedEnquiry.getMobno()), selectedEnquiry.getAddress(),
						selectedEnquiry.getAddress(), selectedEnquiry.getGender(), "Indian", "", "", "", "", 0, "", "", "",
						"India", selectedEnquiry.getFatherName(), selectedEnquiry.getMotherName(), "", "", "",
						Long.valueOf(0), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", routeName1, concession, "", "", "", "", "", "",
						documentsSubmitted, studentImage, fatherImage, motherImage, g1Image, g2Image, "", "", "", "",
						studentstatus, "", discountFee, "", "", conn, "", addmissionNumber,"accepted","temp",className1,"","","","","","","","","","","","","","");

				/*int i = obj.studentRegistration(addmissionDate, selectedEnquiry.getStudentName(),dob,
						selectedSection, "", Long.valueOf(selectedEnquiry.getMobno()), selectedEnquiry.getAddress(),
						selectedEnquiry.getAddress(), selectedEnquiry.getGender(), "Indian", "", "", "", "", 0, "", "", "",
						"India", selectedEnquiry.getFatherName(), selectedEnquiry.getMotherName(), "", "", "",
						Long.valueOf(0), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", routeName1, concession, "", "", "", "", "", "",
						documentsSubmitted, studentImage, fatherImage, motherImage, g1Image, g2Image, "", "", "", "",
						studentstatus, "", discountFee, "", "", conn, "", addmissionNumber);*/
				if (i >= 1) {
					int maxnumber = i;
					cbNumber = String.valueOf(maxnumber);
					obj.updateStudentId("CB" + String.valueOf(maxnumber), i, conn);
					if (routeName1.equals("")) {
						obj.transportDataEntry(obj.schoolId(),addmissionDate, "CB" + String.valueOf(maxnumber), routeName1, "No", className1, conn);
					} else {
						obj.transportDataEntry(obj.schoolId(),addmissionDate, "CB" + String.valueOf(maxnumber), routeName1, "Yes", className1, conn);
					}

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Student Added Successfully",
							"Student Added Successfully"));

					obj.increaseStudentInAddSchool(obj.schoolId(),conn);
					String className = obj.classNameFromidSchid(obj.schoolId(),className1, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn);
					classTestList = obj.selectedClassTestList(obj.schoolId(),className1, selectedSection, conn);
					examList = obj.selectedClassExamList(obj.schoolId(),selectedSection, conn);
					clsName = className;
					for (ClassTest ct : classTestList) {
						testStatus = obj.checkClassTestPerformanceStatus(ct.getId(), conn);
						if (testStatus == true) {
							new DataBaseOnlineAdm().entryOfNewStudentInClassTestPerformanceSession(obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),"CB" + String.valueOf(maxnumber), ct.getId(), conn);
						} else {

						}
					}
					for (ExamInfo ee : examList) {
						new DataBaseOnlineAdm().entryOfNewStudentInExamPerformanceSession(obj.schoolId(),DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn),"CB" + String.valueOf(maxnumber), ee.getClassid(),
								ee.getSubjectid(), ee.getSemesterid(), ee.getExamid(), ee.getExamType(), conn, ee.getMaxMark(), ee.getExamName());
					}


					String id = selectedEnquiry.getId();
					obj.acceptEnquiryInstitute(id, selectedEnquiry.getEnquiryId(),userId, conn);
					search();

					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					ss.setAttribute("addNo", "CB" + String.valueOf(maxnumber));
					ss.setAttribute("name", selectedEnquiry.getStudentName());
					ss.setAttribute("selectedClass", selectedSection);

					if (message.equals("true"))
					{
						String typeMessage = "";
						double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);

						if(balance >0 && balance <= smsLimit)
						{

							balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
									+ "We suggest you to top-up your account today to ensure uninterrupted activity";
							if (type.equalsIgnoreCase("admin"))
							{
								PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
								PrimeFaces.current().ajax().update("MsgLimitForm");
							}
							else
							{
								sendMsg();
							}
						}
						else if(balance <= 0)
						{
							balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
							if (type.equalsIgnoreCase("admin"))
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

						}
						else
						{
							if(selectedEnquiry.getMobno().length()==10
									&& !selectedEnquiry.getMobno().equals("2222222222")
									&& !selectedEnquiry.getMobno().equals("9999999999")
									&& !selectedEnquiry.getMobno().equals("1111111111")
									&& !selectedEnquiry.getMobno().equals("1234567890")
									&& !selectedEnquiry.getMobno().equals("0123456789"))
							{
								if (info.getSchoolAppName().equalsIgnoreCase("N/A")) {
									typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
											+ selectedEnquiry.getStudentName() + " in class " + className
											+ ". Now you can access your ward's information on your mobile." + "\n" + "Regards\n"
											+ info.getSmsSchoolName();

								} else {
									typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
											+ selectedEnquiry.getStudentName() + " in class " + className
											+ ". Now you can access your ward's information on your mobile. Please search "
											+ info.getSchoolAppName()
											+ " on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"
											+ "\n" + "Regards\n" + info.getSmsSchoolName();

								}
								obj.messageurl1(selectedEnquiry.getMobno(), typeMessage, "CB" + String.valueOf(maxnumber), conn,obj.schoolId(),"");
							}
						}
					}

				}
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "If Student New Please Date Must Be In This Session",
								"If Student New Please Date Must Be In This Session "));
			}
		}


		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(message.equals("true"))
		{
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			String typeMessage="";
			if(selectedEnquiry.getMobno().length()==10
					&& !selectedEnquiry.getMobno().equals("2222222222")
					&& !selectedEnquiry.getMobno().equals("9999999999")
					&& !selectedEnquiry.getMobno().equals("1111111111")
					&& !selectedEnquiry.getMobno().equals("1234567890")
					&& !selectedEnquiry.getMobno().equals("0123456789"))
			{
				if (info.getSchoolAppName().equalsIgnoreCase("N/A")) {
					typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
							+ selectedEnquiry.getStudentName() + " in class " + clsName
							+ ". Now you can access your ward's information on your mobile." + "\n" + "Regards\n"
							+ info.getSmsSchoolName();

				} else {
					typeMessage = "Dear Parent," + "\n" + "Thank You for admission of your ward "
							+ selectedEnquiry.getStudentName() + " in class " + clsName
							+ ". Now you can access your ward's information on your mobile. Please search "
							+ info.getSchoolAppName()
							+ " on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"
							+ "\n" + "Regards\n" + info.getSmsSchoolName();

				}
				obj.messageurl1(selectedEnquiry.getMobno(), typeMessage, "CB" + cbNumber, conn,obj.schoolId(),"");
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void denyEnquiry() {
		Connection conn = DataBaseConnection.javaConnection();
		String id = selectedEnquiry.getId();
		int i = obj.denyEnquiryInstitute(id, selectedEnquiry.getEnquiryId(),description,userId,conn);
		if (i > 0) {
			search();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Denied successfully"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error occur Please try Again"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo1> getLeadList() {
		return leadList;
	}

	public void setLeadList(ArrayList<StudentInfo1> leadList) {
		this.leadList = leadList;
	}

	public ArrayList<StudentInfo1> getHistoryInfo() {
		return historyInfo;
	}

	public void setHistoryInfo(ArrayList<StudentInfo1> historyInfo) {
		this.historyInfo = historyInfo;
	}

	public ArrayList<StudentInfo1> getOverdueLeadList() {
		return overdueLeadList;
	}

	public void setOverdueLeadList(ArrayList<StudentInfo1> overdueLeadList) {
		this.overdueLeadList = overdueLeadList;
	}

	public ArrayList<SelectItem> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<SelectItem> empList) {
		this.empList = empList;
	}

	public StudentInfo1 getSelectedEnquiry() {
		return selectedEnquiry;
	}

	public void setSelectedEnquiry(StudentInfo1 selectedEnquiry) {
		this.selectedEnquiry = selectedEnquiry;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTotalOverdue() {
		return totalOverdue;
	}

	public void setTotalOverdue(String totalOverdue) {
		this.totalOverdue = totalOverdue;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSelectedEmp() {
		return selectedEmp;
	}

	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	public Date getFollowDate() {
		return followDate;
	}

	public void setFollowDate(Date followDate) {
		this.followDate = followDate;
	}

	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(Date todayDate) {
		this.todayDate = todayDate;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isShowEmp() {
		return showEmp;
	}

	public void setShowEmp(boolean showEmp) {
		this.showEmp = showEmp;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public Date getAddmissionDate() {
		return addmissionDate;
	}

	public void setAddmissionDate(Date addmissionDate) {
		this.addmissionDate = addmissionDate;
	}

	public String getAddmissionNumber() {
		return addmissionNumber;
	}

	public void setAddmissionNumber(String addmissionNumber) {
		this.addmissionNumber = addmissionNumber;
	}

	public String getClassName1() {
		return className1;
	}

	public void setClassName1(String className1) {
		this.className1 = className1;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public ArrayList<StudentInfo1> getAllEnquiry() {
		return allEnquiry;
	}

	public void setAllEnquiry(ArrayList<StudentInfo1> allEnquiry) {
		this.allEnquiry = allEnquiry;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
	

}
