package schooldata;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="viewOldStudentCbscTc")
@ViewScoped
public class ViewOldStudentCbscTc implements Serializable
{
	String regex=RegexPattern.REGEX;
	String addNumber;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	Date ccIssueDate;
	String typeofCir,tyepofstud,gender;
	String status,character,activity1,activity2;
	public ViewOldStudentCbscTc()
	{
		Connection conn=DataBaseConnection.javaConnection();
		tyepofstud="old";
		gender="Male";
		ccIssueDate=new Date();
		studentList=new DatabaseMethods1().oldCbscTcStudentList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String issueBonafied()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedStudent!= null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("CCDetail", selectedStudent);
			ss.setAttribute("ccIssueDate", ccIssueDate);
			ss.setAttribute("typeofcir",typeofCir);
			ss.setAttribute("gender", gender);
			PrimeFaces.current().executeInitScript("window.open('printOldStudentDobStudy.xhtml')");
			PrimeFaces.current().ajax().update("form");
			//PrimeFaces.current().ajax().update("form:up");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "viewOldStudentCbscTc.xhtml";
	}
	public void issueCC()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedStudent!= null)
		{
			/*HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("RbscCCDetail", selectedStudent);
			ss.setAttribute("RbscccIssueDate", ccIssueDate);
			ss.setAttribute("character", character);
			ss.setAttribute("act1", activity1);
			ss.setAttribute("act2", activity2);
			ss.setAttribute("status", status);
			PrimeFaces.current().executeInitScript("window.open('rbseCharacterCertificate.xhtml')");
			PrimeFaces.current().ajax().update("form");*/
			//PrimeFaces.current().ajax().update("form:up");
			
			String arr[] = selectedStudent.getStruckOffDateStr().split("/");
			int month = Integer.parseInt(arr[1]);
			int year = Integer.parseInt(arr[2]);
			String selectedSession = "";
			/*if(month>=4)
			{
				selectedSession=String.valueOf(year)+"-"+String.valueOf(year+1);
			}
			else
			{*/
				selectedSession=String.valueOf(year-1)+"-"+String.valueOf(year);
			//}
			
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("gender",selectedStudent.getGender());//
			ss.setAttribute("studentName",selectedStudent.getStudentName());//
			ss.setAttribute("fatherName",selectedStudent.getFatherName());//
			ss.setAttribute("lastClass",selectedStudent.getLastClass());//
			ss.setAttribute("lastYear",selectedSession);//
			
			ss.setAttribute("srno",selectedStudent.getAddNumber());//
			ss.setAttribute("address",selectedStudent.getAddress());//
			ss.setAttribute("place",selectedStudent.getAddress1());//
			ss.setAttribute("motherName",selectedStudent.getMotherName());//
			ss.setAttribute("passFail",selectedStudent.getFailedOrNot());//
			ss.setAttribute("admDate",selectedStudent.getJoinDateStr());//
			ss.setAttribute("removalDate",selectedStudent.getStruckOffDateStr());//
			ss.setAttribute("dateofbirth", selectedStudent.getDob());
			try
			{
				PrimeFaces.current().executeInitScript("window.open('printOldStudentCC.xhtml')");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void printDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("cbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());
		ss.setAttribute("tcType", "Duplicate Copy");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("cbscDuplicateTc.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void editDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("cbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("editCbscOldStudentTcDetails.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteDetails() throws SQLException
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		i=new DatabaseMethods1().deleteCbscOldSTudentDetail(selectedStudent.getAddNumber(),conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			conn.close();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("viewOldStudentCbscTc.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured..!"));
			conn.close();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = selectedStudent.getAddNumber();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Cbsc Old Tc","WEB",value,conn);
		return refNo;
	}
	
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
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
	public Date getCcIssueDate() {
		return ccIssueDate;
	}
	public void setCcIssueDate(Date ccIssueDate) {
		this.ccIssueDate = ccIssueDate;
	}
	public String getTypeofCir() {
		return typeofCir;
	}
	public void setTypeofCir(String typeofCir) {
		this.typeofCir = typeofCir;
	}
	public String getTyepofstud() {
		return tyepofstud;
	}
	public void setTyepofstud(String tyepofstud) {
		this.tyepofstud = tyepofstud;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
