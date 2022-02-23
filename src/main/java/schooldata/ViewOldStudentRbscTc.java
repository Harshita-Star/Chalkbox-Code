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
@ManagedBean(name="viewOldStudentRbscTc")
@ViewScoped
public class ViewOldStudentRbscTc implements Serializable
{
	String regex=RegexPattern.REGEX;
	String addNumber;
	ArrayList<StudentInfo> studentList;
	StudentInfo selectedStudent;
	Date ccIssueDate;
	String typeofCir,tyepofstud,gender;
	String status,character,activity1,activity2;
	String classStudent,session;
	public ViewOldStudentRbscTc()
	{
		Connection conn=DataBaseConnection.javaConnection();
		tyepofstud="old";
		gender="Male";
		ccIssueDate=new Date();
		studentList=new DatabaseMethods1().oldRbscTcStudentList(conn);
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

			new DatabaseMethods1().ccSerialNo(conn);
			PrimeFaces.current().executeInitScript("window.open('printOldStudentDobStudy.xhtml')");
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

		return "viewOldStudentRbscTc.xhtml";
	}
	public String issueCC()
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
			ss.setAttribute("pageFrom","viewOldStudentTc");

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

		return "viewOldStudentRbscTc.xhtml";
	}
	public void printDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("rbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());
		ss.setAttribute("tcType", "Duplicate Copy");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("printRbscTc.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void editDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("rbscTCDetail", selectedStudent);
		ss.setAttribute("addNumber", selectedStudent.getAddNumber());

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("editRbscOldStudentDetails.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void deleteDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int i=0;
		try
		{
			i=obj.deleteRbscStruckStudent(selectedStudent.getAddNumber(),conn);
			if(i>=1)
			{
				obj.deleteTCDetail(selectedStudent.getAddNumber(),conn);

				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("viewOldStudentRbscTc.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured..!"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
