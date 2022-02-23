package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printCCDetail")
@ViewScoped
public class printCCDetailBean implements Serializable
{
	StudentInfo ccList;
	String studentName,admissionDate, fatherName,dobString, struckOffDate, dobInHindi, issueDate, ccNumber,motherName,character,activity1,activity2,status;
	String admissionNo,className,session;
	Date dob, tDate;
	String dobInWord;

	public printCCDetailBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ccList=(StudentInfo) ss.getAttribute("CCDetail");
		tDate=(Date) ss.getAttribute("ccIssueDate");
		character=(String) ss.getAttribute("character");
		activity1=(String) ss.getAttribute("act1");
		activity2=(String) ss.getAttribute("act2");
		status= (String) ss.getAttribute("status");
		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		admissionNo=ccList.getSrNo();
		if(admissionNo==null || admissionNo.equals(""))
		{
			admissionNo=ccList.getSrNo();
		}
		studentName=ccList.getFullName();
		fatherName=ccList.getFathersName().replace("Mr.", "").trim();
		motherName=ccList.getMotherName().replace("Mrs.", "").trim();

		admissionDate=sdf.format(ccList.getStartingDate());
		issueDate=sdf.format(tDate);
		className=ccList.getClassName();
		dobString=sdf.format(ccList.getDob());
		struckOffDate=issueDate;
		if(struckOffDate.equals("null"))
		{
			struckOffDate=sdf.format(new Date());
		}

		dobInWord=obj.dateOfBirthInWords(ccList.getDob());
		dob=ccList.getDob();

		int checkCcNo=obj.checkCcNo(admissionNo,conn);
		if(checkCcNo==0)
		{
			int number=obj.ccSerialNo(conn);
			ccNumber=String.valueOf(number);
		}
		else
		{
			ccNumber=String.valueOf(checkCcNo);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public StudentInfo getCcList() {
		return ccList;
	}

	public void setCcList(StudentInfo ccList) {
		this.ccList = ccList;
	}

	public Date gettDate() {
		return tDate;
	}

	public void settDate(Date tDate) {
		this.tDate = tDate;
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



	public String getStruckOffDate() {
		return struckOffDate;
	}

	public void setStruckOffDate(String struckOffDate) {
		this.struckOffDate = struckOffDate;
	}



	public String getDobInHindi() {
		return dobInHindi;
	}

	public void setDobInHindi(String dobInHindi) {
		this.dobInHindi = dobInHindi;
	}


	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(String admissionDate) {
		this.admissionDate = admissionDate;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}



	public String getIssueDate() {
		return issueDate;
	}



	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}



	public String getCcNumber() {
		return ccNumber;
	}



	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}



	public String getDobInWord() {
		return dobInWord;
	}



	public void setDobInWord(String dobInWord) {
		this.dobInWord = dobInWord;
	}



	public String getAdmissionNo() {
		return admissionNo;
	}



	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}



	public String getMotherName() {
		return motherName;
	}



	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}



	public String getCharacter() {
		return character;
	}



	public void setCharacter(String character) {
		this.character = character;
	}



	public String getActivity1() {
		return activity1;
	}



	public void setActivity1(String activity1) {
		this.activity1 = activity1;
	}



	public String getActivity2() {
		return activity2;
	}



	public void setActivity2(String activity2) {
		this.activity2 = activity2;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getClassName() {
		return className;
	}



	public void setClassName(String className) {
		this.className = className;
	}



	public String getSession() {
		return session;
	}



	public void setSession(String session) {
		this.session = session;
	}


}
