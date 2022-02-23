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
@ManagedBean(name="printRbscCC")
@ViewScoped
public class PrintRbscCC implements Serializable
{
	StudentInfo ccList;
	String studentName,admissionDate, fatherName,dobString, struckOffDate, dobInHindi, session,issueDate, ccNumber,motherName,character,activity1,activity2;
	String admissionNo,className;
	Date dob, tDate;
	String dobInWord,status,page,headerImage;
	Boolean headerPresent,headerAbsent;

	public PrintRbscCC()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ccList=(StudentInfo) ss.getAttribute("RbscCCDetail");
		tDate=(Date) ss.getAttribute("RbscccIssueDate");
		character=(String) ss.getAttribute("character");
		activity1=(String) ss.getAttribute("act1");
		activity2=(String) ss.getAttribute("act2");
		status= (String) ss.getAttribute("status");
		page = (String) ss.getAttribute("pageFrom");
		String type= (String) ss.getAttribute("typeofcc");

		admissionNo=ccList.getSrNo();
		fatherName=ccList.getFathersName().toUpperCase();
		motherName=ccList.getMotherName().toUpperCase();
		admissionDate=sdf.format(ccList.getStartingDate());
		issueDate=sdf.format(tDate);
		dobString=sdf.format(ccList.getDob());
		struckOffDate=issueDate;
		
		SchoolInfoList ls = obj.fullSchoolInfo(conn);
		if(ls.getSchid().equalsIgnoreCase("336"))
		{
			if(ls.getTcHeader()==null)
			{
				headerAbsent = true;
				
				
			}
		    else if(!ls.getTcHeader().equalsIgnoreCase(""))
			{
				String savePath="";
				if(ls.getProjecttype().equals("online"))
				{
					savePath=ls.getDownloadpath();
				}
				headerImage=savePath+ls.getTcHeader();
				headerPresent = true;		
			}
			else
			{
				headerAbsent = true;
				
			}
		}
		else
		{
			headerAbsent = true;
			
		}
		
		if(type.equalsIgnoreCase("current"))
		{
			className=ccList.getClassName();
			session=(String) ss.getAttribute("session");
			admissionNo=ccList.getSrNo();
		}
		else
		{
			className=(String) ss.getAttribute("class");
			session=(String) ss.getAttribute("session");
			admissionNo=ccList.getAddNumber();
		}

		if(struckOffDate.equals("null"))
		{
			struckOffDate=sdf.format(new Date());
		}

		dobInWord=obj.dateOfBirthInWords(ccList.getDob());
		dob=ccList.getDob();

		int checkCcNo=obj.rbseCheckCcNo(admissionNo,conn);
		if(checkCcNo==0)
		{
			int number=obj.ccSerialNo(conn);
			ccNumber=String.valueOf(number);
		}
		else
		{
			ccNumber=String.valueOf(checkCcNo);
		}

		if(page.equalsIgnoreCase("viewRbscTc"))
		{

			studentName = ccList.getFname().toUpperCase();

		}
		else
		{
			studentName=ccList.getFullName().toUpperCase();
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


	public String getStudentName() {
		return studentName;
	}


	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}


	public String getAdmissionDate() {
		return admissionDate;
	}


	public void setAdmissionDate(String admissionDate) {
		this.admissionDate = admissionDate;
	}


	public String getFatherName() {
		return fatherName;
	}


	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}


	public String getDobString() {
		return dobString;
	}


	public void setDobString(String dobString) {
		this.dobString = dobString;
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


	public String getAdmissionNo() {
		return admissionNo;
	}


	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}


	public Date getDob() {
		return dob;
	}


	public void setDob(Date dob) {
		this.dob = dob;
	}


	public Date gettDate() {
		return tDate;
	}


	public void settDate(Date tDate) {
		this.tDate = tDate;
	}


	public String getDobInWord() {
		return dobInWord;
	}


	public void setDobInWord(String dobInWord) {
		this.dobInWord = dobInWord;
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


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getHeaderImage() {
		return headerImage;
	}


	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}


	public Boolean getHeaderPresent() {
		return headerPresent;
	}


	public void setHeaderPresent(Boolean headerPresent) {
		this.headerPresent = headerPresent;
	}


	public Boolean getHeaderAbsent() {
		return headerAbsent;
	}


	public void setHeaderAbsent(Boolean headerAbsent) {
		this.headerAbsent = headerAbsent;
	}


}
