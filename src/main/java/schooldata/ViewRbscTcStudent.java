package schooldata;

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
@ManagedBean(name="viewRbscTcStudent")
@ViewScoped
public class ViewRbscTcStudent implements Serializable
{
	String regex=RegexPattern.REGEX;
	String addNumber;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	String status,character,activity1,activity2;
	String classStudent,session,tyepofstud;
	Date ccIssueDate;
	public ViewRbscTcStudent()
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().rbscTcIssuedStudentList(conn);
		tyepofstud="old";
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public void printDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("rbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());
		ss.setAttribute("tcType", "Duplicate Copy");
			PrimeFaces.current().executeInitScript("window.open('printRbscTc.xhtml')");
			
		
	}
 
	
	public void printDetails1()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("rbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());
		ss.setAttribute("tcType", "Original Copy");
		PrimeFaces.current().executeInitScript("window.open('printRbscTc.xhtml')");
		
		
	}
	
	
	public void printccDeatils()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classStudent=selectedStudent.getClassName()+"-"+selectedStudent.getSectionName();
		session=new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		int i=DBM.deleteRbscStruckOffStudent(selectedStudent.addNumber,conn);
		if(i>=1)
		{
			DBM.activeRbscStruckOffStudent(selectedStudent.addNumber,conn);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Student Active Again Successfully"));
			studentList=DBM.rbscTcIssuedStudentList(conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	public void issueCC()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedStudent!= null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("RbscCCDetail", selectedStudent);
			ss.setAttribute("RbscccIssueDate", ccIssueDate);
			ss.setAttribute("character", character);
			ss.setAttribute("act1", activity1);
			ss.setAttribute("act2", activity2);
			ss.setAttribute("status", status);
			ss.setAttribute("typeofcc",tyepofstud);
			ss.setAttribute("class",classStudent);
			ss.setAttribute("session",session);
			ss.setAttribute("session",session);
			
			ss.setAttribute("pageFrom","viewRbscTc");

			//int number=new DatabaseMethods1().ccSerialNo(conn);
			//new DatabaseMethods1().insertRbscCcSnoDetail(selectedStudent.getAddNumber(),number,conn,character,activity1,activity2);
			PrimeFaces.current().executeInitScript("window.open('rbseCharacterCertificate.xhtml')");
			PrimeFaces.current().ajax().update("form");
			PrimeFaces.current().ajax().update("form:up");


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

		//return "viewRbscTcStudent.xhtml";
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
	public String getClassStudent() {
		return classStudent;
	}
	public void setClassStudent(String classStudent) {
		this.classStudent = classStudent;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getTyepofstud() {
		return tyepofstud;
	}
	public void setTyepofstud(String tyepofstud) {
		this.tyepofstud = tyepofstud;
	}
	public Date getCcIssueDate() {
		return ccIssueDate;
	}
	public void setCcIssueDate(Date ccIssueDate) {
		this.ccIssueDate = ccIssueDate;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
