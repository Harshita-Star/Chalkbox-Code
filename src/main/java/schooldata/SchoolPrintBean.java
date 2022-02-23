package schooldata;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
@ManagedBean(name="schoolPrint")
@ViewScoped

public class SchoolPrintBean implements Serializable {

	String schoolCode,boardNumber,tcNumber,amountPaid,totalBillAmount, name,leavingClassName,fathersName,SchoolName,SchoolNumber,schoolAddress,admitClass,oldSchoolName,oldSchoolLastClassName,session,leavingSchoolReason;
	String Srno,mothersName,exam,examResult,category,description,behaviour,schoolContact,strtcDate,strDob,strExamResultDate,strAdmitDate;
	Date tcDate,dob,examResultDate,admitDate;
	int failureTimes,studentPresent;
	String workingDays;
	TCInfo list;
	public SchoolPrintBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		list=(TCInfo) ss.getAttribute("List");
		schoolCode=list.getSchoolCode();
		boardNumber=list.getBoardNumber();
		tcNumber=list.getTcNumber();
		name=list.getName();
		leavingClassName=list.getLeavingClassName();
		fathersName=list.getFathersName();
		SchoolName=list.getSchoolName();
		SchoolNumber=list.getSchoolNumber();
		schoolAddress=list.getSchoolAddress();
		admitClass=list.getAdmitClass();
		oldSchoolName=list.getOldSchoolName();
		oldSchoolLastClassName=list.getOldSchoolLastClassName();
		leavingSchoolReason=list.getLeavingSchoolReason();
		Srno=list.getSrno();
		mothersName=list.getMothersName();
		examResult=list.getExamResult();
		category=list.getCategory();
		description=list.getDescription();
		behaviour=list.getBehaviour();
		schoolContact=list.getSchoolContact();
		strtcDate=list.getStrtcDate();
		strDob=list.getStrDob();
		strExamResultDate=list.getStrExamResultDate();
		strAdmitDate=list.getStrAdmitDate();
		failureTimes=list.getFailureTimes();
		workingDays=list.getWorkingDays();
		studentPresent=list.getStudentPresent();
	}


	public String getSchoolContact() {
		return schoolContact;
	}
	public void setSchoolContact(String schoolContact) {
		this.schoolContact = schoolContact;
	}
	public String getBehaviour() {
		return behaviour;
	}
	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	public int getStudentPresent() {
		return studentPresent;
	}
	public void setStudentPresent(int studentPresent) {
		this.studentPresent = studentPresent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWorkingDays() {
		return workingDays;
	}
	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}
	public int getFailureTimes() {
		return failureTimes;
	}
	public void setFailureTimes(int failureTimes) {
		this.failureTimes = failureTimes;
	}
	public String getSchoolCode() {
		return schoolCode;
	}
	public void setSchoolCode(String schoolCode) {
		this.schoolCode = schoolCode;
	}
	public String getBoardNumber() {
		return boardNumber;
	}
	public void setBoardNumber(String boardNumber) {
		this.boardNumber = boardNumber;
	}
	public String getTcNumber() {
		return tcNumber;
	}
	public void setTcNumber(String tcNumber) {
		this.tcNumber = tcNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFathersName() {
		return fathersName;
	}
	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}
	public String getSchoolName() {
		return SchoolName;
	}
	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}
	public String getSchoolNumber() {
		return SchoolNumber;
	}
	public void setSchoolNumber(String schoolNumber) {
		SchoolNumber = schoolNumber;
	}
	public String getSchoolAddress() {
		return schoolAddress;
	}
	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}

	public String getAdmitClass() {
		return admitClass;
	}
	public void setAdmitClass(String admitClass) {
		this.admitClass = admitClass;
	}
	public String getOldSchoolName() {
		return oldSchoolName;
	}
	public void setOldSchoolName(String oldSchoolName) {
		this.oldSchoolName = oldSchoolName;
	}
	public String getOldSchoolLastClassName() {
		return oldSchoolLastClassName;
	}
	public void setOldSchoolLastClassName(String oldSchoolLastClassName) {
		this.oldSchoolLastClassName = oldSchoolLastClassName;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getLeavingSchoolReason() {
		return leavingSchoolReason;
	}
	public void setLeavingSchoolReason(String leavingSchoolReason) {
		this.leavingSchoolReason = leavingSchoolReason;
	}
	public String getSrno() {
		return Srno;
	}
	public void setSrno(String srno) {
		Srno = srno;
	}
	public String getMothersName() {
		return mothersName;
	}
	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}
	public String getExam() {
		return exam;
	}
	public void setExam(String exam) {
		this.exam = exam;
	}
	public String getExamResult() {
		return examResult;
	}
	public void setExamResult(String examResult) {
		this.examResult = examResult;
	}
	public Date getTcDate() {
		return tcDate;
	}
	public void setTcDate(Date tcDate) {
		this.tcDate = tcDate;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getExamResultDate() {
		return examResultDate;
	}
	public void setExamResultDate(Date examResultDate) {
		this.examResultDate = examResultDate;
	}

	public Date getAdmitDate() {
		return admitDate;
	}
	public void setAdmitDate(Date admitDate) {
		this.admitDate = admitDate;
	}
	public String getLeavingClassName() {
		return leavingClassName;
	}
	public void setLeavingClassName(String leavingClassName) {
		this.leavingClassName = leavingClassName;
	}

	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getTotalBillAmount() {
		return totalBillAmount;
	}
	public void setTotalBillAmount(String totalBillAmount) {
		this.totalBillAmount = totalBillAmount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}


	public String getStrtcDate() {
		return strtcDate;
	}


	public void setStrtcDate(String strtcDate) {
		this.strtcDate = strtcDate;
	}


	public String getStrDob() {
		return strDob;
	}


	public void setStrDob(String strDob) {
		this.strDob = strDob;
	}


	public String getStrExamResultDate() {
		return strExamResultDate;
	}


	public void setStrExamResultDate(String strExamResultDate) {
		this.strExamResultDate = strExamResultDate;
	}


	public String getStrAdmitDate() {
		return strAdmitDate;
	}


	public void setStrAdmitDate(String strAdmitDate) {
		this.strAdmitDate = strAdmitDate;
	}


	public TCInfo getList() {
		return list;
	}


	public void setList(TCInfo list) {
		this.list = list;
	}

}
