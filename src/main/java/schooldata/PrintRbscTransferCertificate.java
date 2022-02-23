package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
@ManagedBean(name="printRbscTransferCertificate")
@ViewScoped
public class PrintRbscTransferCertificate implements Serializable
{
	StudentInfo studentList;
	String registerNo,addNumber,studentName,fatherName,dobInWord,type,srno;
	ArrayList<TCInfo> tcDetail;
	int j=0;
	public PrintRbscTransferCertificate()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentList=(StudentInfo) ss.getAttribute("rbscTCDetail");
		addNumber=(String) ss.getAttribute("addNumber");
		srno=studentList.getSrno();
		type=(String) ss.getAttribute("tcType");
		////// // System.out.println(type);
		studentName=studentList.getFname().toUpperCase();
		tcDetail=obj.rbscTCDetail(addNumber,conn);
		dobInWord=obj.dateOfBirthInWords(studentList.getDob());
		for(int i=(tcDetail.size()+1);i<=15;i++)
		{
			TCInfo in=new TCInfo();
			in.setsNo(String.valueOf(i));
			tcDetail.add(in);
			j=i;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public StudentInfo getStudentList() {
		return studentList;
	}

	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}

	public String getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
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

	public ArrayList<TCInfo> getTcDetail() {
		return tcDetail;
	}

	public void setTcDetail(ArrayList<TCInfo> tcDetail) {
		this.tcDetail = tcDetail;
	}

	public String getDobInWord() {
		return dobInWord;
	}

	public void setDobInWord(String dobInWord) {
		this.dobInWord = dobInWord;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSrno() {
		return srno;
	}

	public void setSrno(String srno) {
		this.srno = srno;
	}

}
