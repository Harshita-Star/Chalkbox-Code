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

@ManagedBean(name="printbonafideCertificate")
@ViewScoped
public class printbonafideCertificateBean implements Serializable
{
	StudentInfo ccList;
	String studentName,addNo,admissionDate,address, fatherName,dobString, struckOffDate, dobInHindi, issueDate, ccNumber,totalFees,className,session;
	String admissionNo;
	Date dob, tDate;
	String dobInWord,totalFeesInWords;
	String relation,typeofcir;
	String motherName,gender,student_image;
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	public printbonafideCertificateBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ccList=(StudentInfo) ss.getAttribute("CCDetail");
		tDate=(Date) ss.getAttribute("ccIssueDate");
		typeofcir=(String)ss.getAttribute("typeofcir");
		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		if(ccList.getCurrentAddress()==null || ccList.getCurrentAddress().trim().equals(""))
		{
			address=ccList.getPermanentAddress();
		}
		else
		{
			address=ccList.getCurrentAddress();
		}

		className=ccList.getClassName();
		admissionNo=ccList.getSrNo();
		student_image = ccList.getStudent_image();
		if(admissionNo==null || admissionNo.equals(""))
		{
			admissionNo=ccList.getAddNumber();
		}
		studentName=ccList.getFullName();
		fatherName=ccList.getFathersName().replace("Mr.", "").trim();
		motherName=ccList.getMotherName().replace("Mrs.", "").trim();
		if(ccList.getTotalFees()!=null){
			totalFees=ccList.getTotalFees();

			int totalPaidFees=Integer.parseInt(totalFees);
			totalFeesInWords=new DatabaseMethods1().numberToWords(totalPaidFees);
		}
		admissionDate=sdf.format(ccList.getStartingDate());
		issueDate=sdf.format(tDate);
		dobString=sdf.format(ccList.getDob());
		struckOffDate=issueDate;
		if(struckOffDate.equals("null"))
		{
			struckOffDate=sdf.format(new Date());
		}

		dobInWord=new DatabaseMethods1().dateOfBirthInWords(ccList.getDob());
		dob=ccList.getDob();
		if(ccList.getGender()!=null)
		{
			if(ccList.getGender().equalsIgnoreCase("male"))
			{
				relation="Son";
			}
			else
			{
				relation="Daughter";
			}
		}
		else
		{
			gender=(String)ss.getAttribute("gender");
			if(gender.equalsIgnoreCase("male"))
			{
				relation="Son";
			}
			else
			{
				relation="Daughter";
			}
		}

		/*int checkCcNo=new DatabaseMethods1().checkCcNo(admissionNo);
		if(checkCcNo==0)
		{
			int number=new DatabaseMethods1().ccSerialNo();
			ccNumber=String.valueOf(number);
		}
		else
		{
			ccNumber=String.valueOf(checkCcNo);
		}
		 */
		
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



	public String getTotalFees() {
		return totalFees;
	}



	public void setTotalFees(String totalFees) {
		this.totalFees = totalFees;
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



	public String getTotalFeesInWords() {
		return totalFeesInWords;
	}



	public void setTotalFeesInWords(String totalFeesInWords) {
		this.totalFeesInWords = totalFeesInWords;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}






	public String getAddNo() {
		return addNo;
	}



	public void setAddNo(String addNo) {
		this.addNo = addNo;
	}



	public String getAdmissionNo() {
		return admissionNo;
	}



	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}



	public String getRelation() {
		return relation;
	}



	public void setRelation(String relation) {
		this.relation = relation;
	}



	public String getTypeofcir() {
		return typeofcir;
	}



	public void setTypeofcir(String typeofcir) {
		this.typeofcir = typeofcir;
	}



	public String getMotherName() {
		return motherName;
	}



	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public String getStudent_image() {
		return student_image;
	}



	public void setStudent_image(String student_image) {
		this.student_image = student_image;
	}


}
