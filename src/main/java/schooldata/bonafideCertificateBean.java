package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="bonafideCertificate")
@ViewScoped
public class bonafideCertificateBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name;
	ArrayList<StudentInfo> studentList;
	ArrayList<StudentInfo> list;
	boolean show;
	StudentInfo selectedStudent;
	String selectedClass, addNumber;
	int addNo;
	ArrayList<SelectItem> classSection;
	Date ccIssueDate;
	String typeofCir,tyepofstud;

	public bonafideCertificateBean()
	{
		typeofCir="DOB";
		tyepofstud="Current";
		Connection conn = DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentListForCCByName(query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+" / "+info.getSrNo()+ "-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName()
	{
		list=new ArrayList<>();
		int index=name.indexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						list.add(info);

						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}

		show=true;
	}


	public void searchStudentByAdmissionNo()
	{
		addNo=Integer.parseInt(addNumber);
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().searchStudentListByAdmissionNo1(addNo,conn);
		if(list.size()!=0)
		{
			show=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found Of This Admission No."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String issueCC()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedStudent!= null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("CCDetail", selectedStudent);
			ss.setAttribute("ccIssueDate", ccIssueDate);
			ss.setAttribute("typeofcir",typeofCir );
			show=false;
			name=null;addNumber="";
			if(!tyepofstud.equalsIgnoreCase("Old"))
			{
				PrimeFaces.current().executeInitScript("window.open('printbonafideCertificate.xhtml')");
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("form:up");


			}
			else
			{

				PrimeFaces.current().executeInitScript("window.open('printOldStudentDobStudy.xhtml')");
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("form:up");



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

		return "bonafideCertificate.xhtml";
	}



	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	public int getAddNo() {
		return addNo;
	}

	public void setAddNo(int addNo) {
		this.addNo = addNo;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}



}
