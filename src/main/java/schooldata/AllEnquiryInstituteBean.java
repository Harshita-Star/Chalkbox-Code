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

@ManagedBean(name="allEnquiryInst")
@ViewScoped
public class AllEnquiryInstituteBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo1>list,historyInfo;
	String studentName,gender,address,mobno,email,fatherName,admissionNo,addmissionclass;
	ArrayList<String>information=new ArrayList<>();
	String instituteInfo,medium,preSchool,selectedClasses,remark,studentMobno;
	Date dob,admissionDate;
	ArrayList<SelectItem> empList,classList,classes=new ArrayList<>();;
	String searchType="All",userId,type,selectedEmp="All",searchStatus="All";
	boolean showType,showEmp;
	StudentInfo1 selectedEnquiry;
	int totalLead;
	DatabaseMethods1 obj=new DatabaseMethods1();


	public  AllEnquiryInstituteBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=obj.allClass11(conn);
		classes=obj.instituteClass(conn);
		empList=obj.allEmployeeList(conn);
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

		list=obj.allEnquiryListInstitute(searchStatus,selectedEmp,conn);
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
		historyInfo=obj.historyOfEnquiryInstitute(selectedEnquiry.getId(),conn);

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
		admissionNo=selectedEnquiry.getAdmissionNo();
		addmissionclass=selectedEnquiry.getAdmissionclass();
		dob=selectedEnquiry.getDob();
		admissionDate=selectedEnquiry.getAdmissionDate();
	}

	public void updateLead()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		String informationInstitute="";
		for(String ss:information)
		{
			if(informationInstitute.equalsIgnoreCase(""))
			{
				informationInstitute=ss;
			}
			else
			{
				informationInstitute=informationInstitute+","+ss;
			}
		}
		int i=obj.updateEnquiryInstitute(admissionDate,studentName,fatherName,mobno,studentMobno,addmissionclass,medium,instituteInfo,
				address,selectedClasses,informationInstitute,preSchool,remark,selectedEnquiry.getId(),conn);
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

	public String getInstituteInfo() {
		return instituteInfo;
	}

	public void setInstituteInfo(String instituteInfo) {
		this.instituteInfo = instituteInfo;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getPreSchool() {
		return preSchool;
	}

	public void setPreSchool(String preSchool) {
		this.preSchool = preSchool;
	}

	public String getSelectedClasses() {
		return selectedClasses;
	}

	public void setSelectedClasses(String selectedClasses) {
		this.selectedClasses = selectedClasses;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ArrayList<String> getInformation() {
		return information;
	}

	public void setInformation(ArrayList<String> information) {
		this.information = information;
	}

	public String getStudentMobno() {
		return studentMobno;
	}

	public void setStudentMobno(String studentMobno) {
		this.studentMobno = studentMobno;
	}

	public ArrayList<SelectItem> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<SelectItem> classes) {
		this.classes = classes;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
