
package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="oldStudentCC")
@ViewScoped
public class OldStudentCCGenerateBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String studentName,fatherName,lastClass,gender,lastYear,motherName,address,place,passFail,srno;
	Date admDate,removalDate;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	
	public OldStudentCCGenerateBean()
	{
		admDate= new Date();
		removalDate = new Date();
		Connection conn=DataBaseConnection.javaConnection();
		studentList=DBM.oldStudentCCList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void issueCC()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String adDate = new SimpleDateFormat("yyyy-MM-dd").format(admDate);
		String rmDate = new SimpleDateFormat("yyyy-MM-dd").format(removalDate);
		int i=DBM.insertOldStudentCCInforation(studentName, fatherName, lastClass, gender, lastYear,address,place,motherName,passFail,adDate,rmDate,srno, conn);
		if(i==1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("CC generated successfully"));
			
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("gender",gender);
			ss.setAttribute("studentName",studentName);
			ss.setAttribute("fatherName",fatherName);
			ss.setAttribute("lastClass",lastClass);
			ss.setAttribute("lastYear",lastYear);
			
			ss.setAttribute("srno",srno);
			ss.setAttribute("address",address);
			ss.setAttribute("place",place);
			ss.setAttribute("motherName",motherName);
			ss.setAttribute("passFail",passFail);
			ss.setAttribute("admDate",new SimpleDateFormat("dd/MM/yyyy").format(admDate));
			ss.setAttribute("removalDate",new SimpleDateFormat("dd/MM/yyyy").format(removalDate));

			try
			{
				FacesContext.getCurrentInstance().getExternalContext().redirect("printOldStudentCC.xhtml");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String adDate = new SimpleDateFormat("yyyy-MM-dd").format(admDate);
		String rmDate = new SimpleDateFormat("yyyy-MM-dd").format(removalDate);
		
		language = "Srno-"+srno+" --Student-"+studentName+" --gender-"+gender+" --Address-"+address+" --father-"+fatherName+" --Mother-"+motherName+" --Adm date-"+adDate+" --Removl Date-"+rmDate+
				" --last Class-"+lastClass+" --Pass/Fail-"+passFail+" --LasYear-"+lastYear+" --Place-"+place;
		
		value = studentName+" -- "+fatherName+" -- "+lastClass+" -- "+gender+" -- "+lastYear+" -- "+address+" -- "+place+" -- "+motherName+" -- "+passFail+" -- "+adDate+" -- "+rmDate+" -- "+srno;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Old CC generate","WEB",value,conn);
		return refNo;
	}
	
	
	
	public void printDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("gender",selectedStudent.getGender());
		ss.setAttribute("studentName",selectedStudent.getStudentName());
		ss.setAttribute("fatherName",selectedStudent.getFatherName());
		ss.setAttribute("lastClass",selectedStudent.getLastClass());
		ss.setAttribute("lastYear",selectedStudent.getYear());
		
		ss.setAttribute("srno",selectedStudent.getSrno());
		ss.setAttribute("address",selectedStudent.getAddress());
		ss.setAttribute("place",selectedStudent.getAddress1());
		ss.setAttribute("motherName",selectedStudent.getMotherName());
		ss.setAttribute("passFail",selectedStudent.getPassedClass());
		ss.setAttribute("admDate",selectedStudent.getStrAdmitDate());
		ss.setAttribute("removalDate",selectedStudent.getStruckOffDateStr());
		try
		{
			PrimeFaces.current().executeInitScript("window.open('printOldStudentCC.xhtml')");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void editDetails()
	{
		gender=selectedStudent.getGender();
		studentName=selectedStudent.getStudentName();
		fatherName=selectedStudent.getFatherName();
		lastClass=selectedStudent.getLastClass();
		lastYear=selectedStudent.getYear();
		srno=selectedStudent.getSrno();
		
		motherName=selectedStudent.getMotherName();
		address=selectedStudent.getAddress();
		place=selectedStudent.getAddress1();
		passFail=selectedStudent.getPassedClass();
		admDate=selectedStudent.getAdmitDate();
		removalDate=selectedStudent.getStruckOffDate();
	}
	
	public void updateDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String adDate = new SimpleDateFormat("yyyy-MM-dd").format(admDate);
		String rmDate = new SimpleDateFormat("yyyy-MM-dd").format(removalDate);
		
		int i=DBM.updateOldStudentCCInforation(selectedStudent.getId(),studentName, fatherName, lastClass, gender,lastYear,address,place,motherName,passFail,adDate,rmDate,srno, conn);
		if(i==1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("CC Updated successfully"));
			studentList=DBM.oldStudentCCList(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		String adDate = new SimpleDateFormat("yyyy-MM-dd").format(admDate);
		String rmDate = new SimpleDateFormat("yyyy-MM-dd").format(removalDate);
		
		value = "Id-"+selectedStudent.getId()+" -- Student-"+studentName+" -- Father-"+fatherName+" -- LastClass-"+lastClass+" -- gender-"+gender+" -- LastYear-"+lastYear+" -- Address-"+address+" -- Place-"+place+" -- Mother-"+motherName+" -- Pass/Fail-"+passFail+" -- AdmDate-"+adDate+" -- RemoveDate-"+rmDate+" -- Srno-"+srno;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Update Old CC","WEB",value,conn);
		return refNo;
	}
	
	
	public void deleteDetails()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		int i=DBM.deleteOldStudentCCInforation(selectedStudent.getId(),conn);
		if(i==1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("CC Deleted successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = String.valueOf(selectedStudent.getId());
		String language= "";
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Old CC","WEB",value,conn);
		return refNo;
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

	public String getLastClass() {
		return lastClass;
	}

	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public String getLastYear() {
		return lastYear;
	}

	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPassFail() {
		return passFail;
	}

	public void setPassFail(String passFail) {
		this.passFail = passFail;
	}

	public Date getAdmDate() {
		return admDate;
	}

	public void setAdmDate(Date admDate) {
		this.admDate = admDate;
	}

	public Date getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Date removalDate) {
		this.removalDate = removalDate;
	}

	public String getSrno() {
		return srno;
	}

	public void setSrno(String srno) {
		this.srno = srno;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}

