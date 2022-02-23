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

import session_work.RegexPattern;
@ManagedBean(name="rbscTC")
@ViewScoped
public class RbscTC implements Serializable
{
	String regex=RegexPattern.REGEX;
	StudentInfo studentList;
	String  perfom;
	String text;
	String reason,lastClass,tcNumber;
	boolean showTextBox;
	Date date2;
	static String perform1;
	int number;

	public RbscTC()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(StudentInfo) ss.getAttribute("selectedStudent");
		lastClass=(studentList.getClassName());

	}
	public void getOtherReason()
	{
		if(reason.equals("others")){
			showTextBox=true;
		}
		else{
			showTextBox=false;
		}
	}
	public String struckOff()
	{
		Connection conn=DataBaseConnection.javaConnection();
		perform1=perfom;

		String id=String.valueOf(studentList.getAddNumber());
		String classId=studentList.getSectionid();
		//   int number=new DatabaseMethods1().tcSerialNo(conn);
		int i=DatabaseMethods1.addRbscTC(reason, date2,lastClass,id,perfom,text,classId,conn,number);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Struck Off Successfully"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "rbscStudentSearchStruckOff.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
		}

		reason=null;
		date2=null;
		lastClass=null;
		perfom=null;
		text=null;
		classId=null;
		number=0;
		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public String getPerfom() {
		return perfom;
	}
	public void setPerfom(String perfom) {
		this.perfom = perfom;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getLastClass() {
		return lastClass;
	}
	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}
	public String getTcNumber() {
		return tcNumber;
	}
	public void setTcNumber(String tcNumber) {
		this.tcNumber = tcNumber;
	}
	public boolean isShowTextBox() {
		return showTextBox;
	}
	public void setShowTextBox(boolean showTextBox) {
		this.showTextBox = showTextBox;
	}
	public Date getDate2() {
		return date2;
	}
	public void setDate2(Date date2) {
		this.date2 = date2;
	}
	public static String getPerform1() {
		return perform1;
	}
	public static void setPerform1(String perform1) {
		RbscTC.perform1 = perform1;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
