package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
@ViewScoped
@ManagedBean(name="tcGeneration")
public class TcGeneration implements Serializable
{
	String schoolCode,boardNumber,tcNumber,name,leavingClassName,fathersName,SchoolName,SchoolNumber,schoolAddress,admitClass,oldSchoolName,oldSchoolLastClassName,session,leavingSchoolReason;
	String srno,mothersName,exam,examResult,category,description,behaviour,schoolContact;
	Date tcDate,dob,examResultDate,admitDate;
	int failureTimes,workingDays,studentPresent;
	TCInfo list=new TCInfo();

	public String addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		try
		{
			int status =obj.addDetails(schoolCode,boardNumber,tcDate,tcNumber,name,srno,fathersName,mothersName,
					dob,category,admitClass,admitDate,oldSchoolName,oldSchoolLastClassName,leavingClassName,examResult,
					examResultDate,failureTimes,workingDays,studentPresent,behaviour,leavingSchoolReason,description,conn);

			if(status>=1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null,new FacesMessage("Details added successfully"));
				list=obj.getTcDetails(conn);
				HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("List",list);
				PrimeFaces.current().executeInitScript("window.open('govtCollegePrintFormat.xhtml')");
				return "tcGeneration.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again ", "Validation error"));
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	public String getBehaviour() {
		return behaviour;
	}
	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	public String getAdmitClass() {
		return admitClass;
	}
	public void setAdmitClass(String admitClass) {
		this.admitClass = admitClass;
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
	public String getLeavingClassName() {
		return leavingClassName;
	}
	public void setLeavingClassName(String leavingClassName) {
		this.leavingClassName = leavingClassName;
	}

	public int getStudentPresent() {
		return studentPresent;
	}
	public void setStudentPresent(int studentPresent) {
		this.studentPresent = studentPresent;
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

	public String getLeavingSchoolReason() {
		return leavingSchoolReason;
	}
	public void setLeavingSchoolReason(String leavingSchoolReason) {
		this.leavingSchoolReason = leavingSchoolReason;
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
	public String getSrno() {
		return srno;
	}
	public void setSrno(String srno) {
		this.srno = srno;
	}
	public String getSchoolContact() {
		return schoolContact;
	}
	public void setSchoolContact(String schoolContact) {
		this.schoolContact = schoolContact;
	}
	public String getMothersName() {
		return mothersName;
	}
	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public String getExamResult() {
		return examResult;
	}
	public void setExamResult(String examResult) {
		this.examResult = examResult;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public int getFailureTimes() {
		return failureTimes;
	}
	public void setFailureTimes(int failureTimes) {
		this.failureTimes = failureTimes;
	}
	public int getWorkingDays() {
		return workingDays;
	}
	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}


}
