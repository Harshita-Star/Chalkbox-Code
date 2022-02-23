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
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="allEnquiry")
@ViewScoped
public class AllEnquiryBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo1>list,historyInfo;
	String studentName,gender,address,mobno,email,fatherName,motherName,admissionNo,addmissionclass;
	Date dob,admissionDate;
	ArrayList<SelectItem> empList,classList;
	String searchType="All",userId,type,selectedEmp="All",searchStatus="All";
	boolean showType,showEmp;
	StudentInfo1 selectedEnquiry;
	int totalLead;

	public  AllEnquiryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=new DatabaseMethods1().allClass11(conn);
		empList=new DatabaseMethods1().allEmployeeList(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");
		if (type.equalsIgnoreCase("master admin") || type.equalsIgnoreCase("admin"))
		{
			//showType=true;
			showType=true;
			search();
		}
		else
		{
			selectedEmp=userId;
			showType=false;
			search();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkSearchType()
	{
		searchStatus="";
		if(searchType.equalsIgnoreCase("employee"))
		{
			showEmp=true;
		}
		else
		{
			showEmp=false;
		}
	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();

		list=new DatabaseMethods1().allEnquiryList(searchStatus,selectedEmp,conn);
		totalLead=list.size();


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkHistory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		historyInfo=new DatabaseMethods1().historyOfEnquiry(selectedEnquiry.getId(),conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editData()
	{

		studentName=selectedEnquiry.getStudentName();
		gender=selectedEnquiry.getGender();
		address=selectedEnquiry.getAddress();
		mobno=selectedEnquiry.getMobno();
		email=selectedEnquiry.getEmail();
		fatherName=selectedEnquiry.getFatherName();
		motherName=selectedEnquiry.getMotherName();
		admissionNo=selectedEnquiry.getAdmissionNo();
		addmissionclass=selectedEnquiry.getAdmissionclass();
		dob=selectedEnquiry.getDob();
		admissionDate=selectedEnquiry.getAdmissionDate();
	}

	public void updateLead()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		int i=obj.updateEnquiry(selectedEnquiry.getId(),studentName,gender,address,mobno,email,fatherName,motherName,admissionNo,addmissionclass,admissionDate,dob,conn);
		if(i>=1)
		{


			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Enquiry Updated SucessFully"));
			search();
			totalLead=list.size();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.... Please Try Again"));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<SelectItem> getEmpList() {
		return empList;
	}
	public void setEmpList(ArrayList<SelectItem> empList) {
		this.empList = empList;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSelectedEmp() {
		return selectedEmp;
	}
	public void setSelectedEmp(String selectedEmp) {
		this.selectedEmp = selectedEmp;
	}
	public String getSearchStatus() {
		return searchStatus;
	}
	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}
	public boolean isShowType() {
		return showType;
	}
	public void setShowType(boolean showType) {
		this.showType = showType;
	}
	public boolean isShowEmp() {
		return showEmp;
	}
	public void setShowEmp(boolean showEmp) {
		this.showEmp = showEmp;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getTotalLead() {
		return totalLead;
	}
	public void setTotalLead(int totalLead) {
		this.totalLead = totalLead;
	}

	public ArrayList<StudentInfo1> getList() {
		return list;
	}

	public void setList(ArrayList<StudentInfo1> list) {
		this.list = list;
	}

	public ArrayList<StudentInfo1> getHistoryInfo() {
		return historyInfo;
	}

	public void setHistoryInfo(ArrayList<StudentInfo1> historyInfo) {
		this.historyInfo = historyInfo;
	}

	public StudentInfo1 getSelectedEnquiry() {
		return selectedEnquiry;
	}

	public void setSelectedEnquiry(StudentInfo1 selectedEnquiry) {
		this.selectedEnquiry = selectedEnquiry;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobno() {
		return mobno;
	}

	public void setMobno(String mobno) {
		this.mobno = mobno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getAdmissionNo() {
		return admissionNo;
	}

	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}

	public String getAddmissionclass() {
		return addmissionclass;
	}

	public void setAddmissionclass(String addmissionclass) {
		this.addmissionclass = addmissionclass;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
