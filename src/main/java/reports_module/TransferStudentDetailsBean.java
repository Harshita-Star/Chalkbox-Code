package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ParentsInfo;
import schooldata.StudentInfo;

@ManagedBean(name="tStudentDetails")
@ViewScoped
public class TransferStudentDetailsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	String fname,lname,fathersName,motherName,className,sectionName,transportRoute,gender,religion,currentAddress,permanentAddress;
	String relation,phone,lastSchoolName,occupation,fatherFname,fatherLname,passedClass,medium,sname1,sname2,sclassid1,sclassid2;
	String sclassName1,sclassName2,ssectionName1,ssectionName2,country,nationality,previousCategory,category,fathersOccupation;
	int previousDiscount, tutionDiscount,admissionDiscount,examDiscount,id,pincode;
	StudentInfo studentList;
	String studentImage,addNumber;
	ArrayList<SelectItem> categoryList,classList,routeList;
	Date lastUpdatingDate,dob,startingDate;
	long fathersPhone,mothersPhone;
	String admitClass,residencePhone;
	String session,schoolId;
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public TransferStudentDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			schoolId=DBM.schoolId();
			session=DBM.selectedSessionDetails(schoolId, conn);

			studentList=(StudentInfo) ss.getAttribute("selectedStudent");

			studentImage=DBM.studentImage(schoolId,"imageWithPath",studentList.getAddNumber(),conn);

			String temp=DBM.admitClass(String.valueOf(studentList.getAddNumber()),conn);
			if(temp.equals("new"))
			{
				admitClass=studentList.getClassName();
			}
			else
			{
				String classid1=DBM.classSectionNameFromidSchid(schoolId,temp,conn);
				admitClass=(DBM.classNameFromidSchid(schoolId,classid1,session,conn));
			}

			addNumber=studentList.getAddNumber();
			fname=studentList.getFname();
			startingDate=studentList.getStartingDate();
			fathersName=studentList.getFathersName();
			motherName=studentList.getMotherName();
			transportRoute=studentList.getTransportRoute();
			dob=studentList.getDob();
			gender=studentList.getGender();
			nationality=studentList.getNationality();
			religion=studentList.getReligion();
			currentAddress=studentList.getCurrentAddress();
			permanentAddress=studentList.getPermanentAddress();
			pincode=studentList.getPincode();
			country=studentList.getCountry();
			fathersPhone=studentList.getFathersPhone();
			mothersPhone=studentList.getMothersPhone();
			category=studentList.getCategory();
			tutionDiscount=studentList.getTutionDiscount();
			admissionDiscount=studentList.getAdmissionDiscount();
			examDiscount=studentList.getExamDiscount();
			residencePhone=studentList.getResidenceMobile();
			fathersOccupation=studentList.getFathersOccupation();

			className=(studentList.getClassName())+" - "+studentList.getSectionName();
			transportRoute=DBM.presentRouteStatus(schoolId, addNumber,conn);
			transportRoute=DBM.transportRouteNameFromId(schoolId,transportRoute,session,conn);

			ParentsInfo list=DBM.editParentsDetail(String.valueOf(addNumber),conn);

			fatherFname=list.getFname();
			fatherLname=list.getLname();
			relation=list.getRelation();
			phone=list.getPhone();
			lastSchoolName=list.getLastSchoolName();
			occupation=list.getOccupation();
			passedClass=list.getPassedClass();
			medium=list.getMedium();
			sname1=list.getSname1();
			sname2=list.getSname2();
			sclassName1=DBM.classNameFromidSchid(schoolId,list.getSclassid1(),session,conn);
			sclassName2=DBM.classNameFromidSchid(schoolId,list.getSclassid2(),session,conn);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String getSclassName1() {
		return sclassName1;
	}
	public void setSclassName1(String sclassName1) {
		this.sclassName1 = sclassName1;
	}
	public String getSclassName2() {
		return sclassName2;
	}
	public void setSclassName2(String sclassName2) {
		this.sclassName2 = sclassName2;
	}
	public String getSsectionName1() {
		return ssectionName1;
	}
	public void setSsectionName1(String ssectionName1) {
		this.ssectionName1 = ssectionName1;
	}
	public String getSsectionName2() {
		return ssectionName2;
	}
	public void setSsectionName2(String ssectionName2) {
		this.ssectionName2 = ssectionName2;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLastSchoolName() {
		return lastSchoolName;
	}
	public void setLastSchoolName(String lastSchoolName) {
		this.lastSchoolName = lastSchoolName;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getFatherFname() {
		return fatherFname;
	}
	public void setFatherFname(String fatherFname) {
		this.fatherFname = fatherFname;
	}
	public String getFatherLname() {
		return fatherLname;
	}
	public void setFatherLname(String fatherLname) {
		this.fatherLname = fatherLname;
	}
	public String getPassedClass() {
		return passedClass;
	}
	public void setPassedClass(String passedClass) {
		this.passedClass = passedClass;
	}
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	public String getSname1() {
		return sname1;
	}
	public void setSname1(String sname1) {
		this.sname1 = sname1;
	}
	public String getSname2() {
		return sname2;
	}
	public void setSname2(String sname2) {
		this.sname2 = sname2;
	}
	public String getSclassid1() {
		return sclassid1;
	}
	public void setSclassid1(String sclassid1) {
		this.sclassid1 = sclassid1;
	}
	public String getSclassid2() {
		return sclassid2;
	}
	public void setSclassid2(String sclassid2) {
		this.sclassid2 = sclassid2;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public StudentInfo getStudentList() {
		return studentList;
	}
	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getFathersName() {
		return fathersName;
	}
	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getTransportRoute() {
		return transportRoute;
	}
	public void setTransportRoute(String transportRoute) {
		this.transportRoute = transportRoute;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public int getPincode() {
		return pincode;
	}
	public void setPincode(int pincode) {
		this.pincode = pincode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public long getFathersPhone() {
		return fathersPhone;
	}
	public void setFathersPhone(long fathersPhone) {
		this.fathersPhone = fathersPhone;
	}
	public long getMothersPhone() {
		return mothersPhone;
	}
	public void setMothersPhone(long mothersPhone) {
		this.mothersPhone = mothersPhone;
	}

	public String getStudentImage() {
		return studentImage;
	}
	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}
	public String getFathersOccupation() {
		return fathersOccupation;
	}
	public void setFathersOccupation(String fathersOccupation) {
		this.fathersOccupation = fathersOccupation;
	}
	public String getResidencePhone() {
		return residencePhone;
	}
	public void setResidencePhone(String residencePhone) {
		this.residencePhone = residencePhone;
	}
	public int getTutionDiscount() {
		return tutionDiscount;
	}
	public void setTutionDiscount(int tutionDiscount) {
		this.tutionDiscount = tutionDiscount;
	}
	public int getAdmissionDiscount() {
		return admissionDiscount;
	}
	public void setAdmissionDiscount(int admissionDiscount) {
		this.admissionDiscount = admissionDiscount;
	}
	public int getExamDiscount() {
		return examDiscount;
	}
	public void setExamDiscount(int examDiscount) {
		this.examDiscount = examDiscount;
	}
	public ArrayList<SelectItem> getClassList()
	{
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public Date getLastUpdatingDate() {
		return lastUpdatingDate;
	}
	public void setLastUpdatingDate(Date lastUpdatingDate) {
		this.lastUpdatingDate = lastUpdatingDate;
	}
	public int getPreviousDiscount() {
		return previousDiscount;
	}
	public void setPreviousDiscount(int previousDiscount) {
		this.previousDiscount = previousDiscount;
	}
	public String getPreviousCategory() {
		return previousCategory;
	}
	public void setPreviousCategory(String previousCategory) {
		this.previousCategory = previousCategory;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}
	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public String getAdmitClass() {
		return admitClass;
	}
	public void setAdmitClass(String admitClass) {
		this.admitClass = admitClass;
	}
}
