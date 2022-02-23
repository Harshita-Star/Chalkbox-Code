package tc_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.StreamedContent;

import exam.DatabaseMethodExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="randomTcView")
@ViewScoped
public class RandomTcGenerate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<StudentInfo> studentDetails;
	SchoolInfoList info;
	DatabaseMethods1 dbm  = new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	String json="";
	String reason,lastClass,branchId,tcNumber,schid,text,previousClass,session,headerImage,website,perfom,classname2,dob1,startingdate1,dobinword,fullName,classname1,addmissionnumber;
	Date struckOffDate,applicationDate,issueDate, dob,startingDate;
	boolean showTextBox,showTC,showMainForm;
	String fatherName,motherName,nationality,category, date1,year,className,currentAddress,permanentAddress,issuDateStr,lastDateStr,tcType="New";
	String schoolExam,failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays,workingDayPresent,feeConcession,gamesPlayed,extraActivity,otherRemark;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem>categoryList=new ArrayList<>();
	StudentInfo tcList;
	boolean showpdfbtn = false;
	String dateOfBirth;
	int startSessionMonth,endSessionMonth;
	transient StreamedContent file;
	ArrayList<StudentInfo> serial;
	String previosClassWord="",stadhar="",madhar="",fadhar="",ncc="";
	public RandomTcGenerate()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		tcNumber =params.get("tcNumber");
		schid = params.get("Schoolid");
		
		//------>Date Format--"yyyy-MM-dd"
		
		dateOfBirth = params.get("dob");
		
		//for data table cbsc_old_student_tc
		studentDetails = dbExam.getOldStudentDetailsForTcGenerte(tcNumber,schid,conn,dateOfBirth);
		if(studentDetails.isEmpty()==true) {
			//for data table transfer_certificate
			// dob format for this is  "yyyy-MM-dd HH:MM:SS"
			studentDetails = dbExam.getStudentDetailsFOrTcGenertae(tcNumber, schid, studentDetails, conn,dateOfBirth+" 00:00:00");
			
		}
		
		if(studentDetails.isEmpty()==true) {
			tcType="new";
		}else {
			tcType="Duplicate Copy";
			tcList=studentDetails.get(0);
			addmissionnumber=studentDetails.get(0).getAddNumber();
			//schid = schid;
			showMainForm=false;
			showTC=true;
			reason=tcList.getReason();
			text=tcList.getOtherReason();
			tcNumber=tcList.getTcNo();
			fullName=tcList.getStudentName();
			fatherName=tcList.getFatherName();
			motherName=tcList.getMotherName();
			category=tcList.getCategory();
			startingdate1=tcList.getJoinDateStr();
			dob1=tcList.getDobStr();
			dobinword=tcList.getDobInWord();
			className=tcList.getAdmitClass();
			perfom=tcList.getPerformance();
			nationality=tcList.getNationality();
			previousClass=tcList.getLastClass();
			previousClassInWord(previousClass);
			schoolExam=tcList.getSchoolExam();
			failedOrNot=tcList.getFailedOrNot();
			subjectStudied=tcList.getSubjectStudied();
			qualifiedPromotion=tcList.getQualifiedPromotion();
			monthOfFeePaid=tcList.getMonthOfFeePaid();
			workingDays=tcList.getWorkingDays();
			workingDayPresent=tcList.getWorkingDayPresent();
			feeConcession=tcList.getFeeConcession();
			gamesPlayed=tcList.getGamesPlayed();
			extraActivity=tcList.getExtraActivity();
			otherRemark=tcList.getOtherRemark();
			lastDateStr=tcList.getLeavingDateStr();
			issuDateStr=tcList.getIssueDateStr();
			stadhar=tcList.getAadharNo();
			fadhar=tcList.getFatherAadhaar();
			madhar=tcList.getMotherAadhaar();
			ncc=tcList.getNcc();
			info=new DatabaseMethods1().fullSchoolInfo(schid, conn);

			startSessionMonth = Integer.valueOf(info.getSchoolSession().split("-")[0]);
			endSessionMonth = Integer.valueOf(info.getSchoolSession().split("-")[1]) + 12;
			branchId = info.getBranch_id();
			String savePath="";
			if(info.getProjecttype().equals("online"))
			{
				savePath=info.getDownloadpath();
			}
			headerImage=savePath+info.getTcHeader();
			website=info.getWebsite();

			if(info.getSchid().equals("216") || info.getSchid().equals("221")) {
				showpdfbtn = true;
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void previousClassInWord(String prevclass)
	{
		if(prevclass.equalsIgnoreCase("I") || prevclass.equalsIgnoreCase("1") || prevclass.equalsIgnoreCase("1st"))
		{
			previosClassWord="First";
		}
		else if(prevclass.equalsIgnoreCase("II") || prevclass.equalsIgnoreCase("2") || prevclass.equalsIgnoreCase("2nd"))
		{
			previosClassWord="Second";
		}
		else if(prevclass.equalsIgnoreCase("III") || prevclass.equalsIgnoreCase("3") || prevclass.equalsIgnoreCase("3rd"))
		{
			previosClassWord="Third";
		}
		else if(prevclass.equalsIgnoreCase("IV") || prevclass.equalsIgnoreCase("4") || prevclass.equalsIgnoreCase("4th"))
		{
			previosClassWord="Fourth";
		}
		else if(prevclass.equalsIgnoreCase("V") || prevclass.equalsIgnoreCase("5") || prevclass.equalsIgnoreCase("5th"))
		{
			previosClassWord="Fifth";
		}
		else if(prevclass.equalsIgnoreCase("VI") || prevclass.equalsIgnoreCase("6") || prevclass.equalsIgnoreCase("6th"))
		{
			previosClassWord="Sixth";
		}
		else if(prevclass.equalsIgnoreCase("VII") || prevclass.equalsIgnoreCase("7") || prevclass.equalsIgnoreCase("7th"))
		{
			previosClassWord="Seventh";
		}
		else if(prevclass.equalsIgnoreCase("VIII") || prevclass.equalsIgnoreCase("8") || prevclass.equalsIgnoreCase("8th"))
		{
			previosClassWord="Eighth";
		}
		else if(prevclass.equalsIgnoreCase("IX") || prevclass.equalsIgnoreCase("9") || prevclass.equalsIgnoreCase("9th"))
		{
			previosClassWord="Ninth";
		}
		else if(prevclass.equalsIgnoreCase("X") || prevclass.equalsIgnoreCase("10") || prevclass.equalsIgnoreCase("10th"))
		{
			previosClassWord="Tenth";
		}
		else if(prevclass.equalsIgnoreCase("XI") || prevclass.equalsIgnoreCase("11") || prevclass.equalsIgnoreCase("11th"))
		{
			previosClassWord="Eleventh";
		}
		else if(prevclass.equalsIgnoreCase("XII") || prevclass.equalsIgnoreCase("12") || prevclass.equalsIgnoreCase("12th"))
		{
			previosClassWord="Twelfth";
		}
		else
		{
			previosClassWord=prevclass;
		}

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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPreviousClass() {
		return previousClass;
	}
	public void setPreviousClass(String previousClass) {
		this.previousClass = previousClass;
	}
	public String getPerfom() {
		return perfom;
	}
	public void setPerfom(String perfom) {
		this.perfom = perfom;
	}
	public String getClassname2() {
		return classname2;
	}
	public void setClassname2(String classname2) {
		this.classname2 = classname2;
	}
	public String getDob1() {
		return dob1;
	}
	public void setDob1(String dob1) {
		this.dob1 = dob1;
	}
	public String getStartingdate1() {
		return startingdate1;
	}
	public void setStartingdate1(String startingdate1) {
		this.startingdate1 = startingdate1;
	}
	public String getDobinword() {
		return dobinword;
	}
	public void setDobinword(String dobinword) {
		this.dobinword = dobinword;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getClassname1() {
		return classname1;
	}
	public void setClassname1(String classname1) {
		this.classname1 = classname1;
	}
	public String getAddmissionnumber() {
		return addmissionnumber;
	}
	public void setAddmissionnumber(String addmissionnumber) {
		this.addmissionnumber = addmissionnumber;
	}
	public Date getStruckOffDate() {
		return struckOffDate;
	}
	public void setStruckOffDate(Date struckOffDate) {
		this.struckOffDate = struckOffDate;
	}
	public Date getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
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
	public boolean isShowTextBox() {
		return showTextBox;
	}
	public void setShowTextBox(boolean showTextBox) {
		this.showTextBox = showTextBox;
	}
	public boolean isShowTC() {
		return showTC;
	}
	public void setShowTC(boolean showTC) {
		this.showTC = showTC;
	}
	public boolean isShowMainForm() {
		return showMainForm;
	}
	public void setShowMainForm(boolean showMainForm) {
		this.showMainForm = showMainForm;
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
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDate1() {
		return date1;
	}
	public void setDate1(String date1) {
		this.date1 = date1;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
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
	public String getIssuDateStr() {
		return issuDateStr;
	}
	public void setIssuDateStr(String issuDateStr) {
		this.issuDateStr = issuDateStr;
	}
	public String getLastDateStr() {
		return lastDateStr;
	}
	public void setLastDateStr(String lastDateStr) {
		this.lastDateStr = lastDateStr;
	}
	public String getTcType() {
		return tcType;
	}
	public void setTcType(String tcType) {
		this.tcType = tcType;
	}
	public String getSchoolExam() {
		return schoolExam;
	}
	public void setSchoolExam(String schoolExam) {
		this.schoolExam = schoolExam;
	}
	public String getFailedOrNot() {
		return failedOrNot;
	}
	public void setFailedOrNot(String failedOrNot) {
		this.failedOrNot = failedOrNot;
	}
	public String getSubjectStudied() {
		return subjectStudied;
	}
	public void setSubjectStudied(String subjectStudied) {
		this.subjectStudied = subjectStudied;
	}
	public String getQualifiedPromotion() {
		return qualifiedPromotion;
	}
	public void setQualifiedPromotion(String qualifiedPromotion) {
		this.qualifiedPromotion = qualifiedPromotion;
	}
	public String getMonthOfFeePaid() {
		return monthOfFeePaid;
	}
	public void setMonthOfFeePaid(String monthOfFeePaid) {
		this.monthOfFeePaid = monthOfFeePaid;
	}
	public String getWorkingDays() {
		return workingDays;
	}
	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}
	public String getWorkingDayPresent() {
		return workingDayPresent;
	}
	public void setWorkingDayPresent(String workingDayPresent) {
		this.workingDayPresent = workingDayPresent;
	}
	public String getFeeConcession() {
		return feeConcession;
	}
	public void setFeeConcession(String feeConcession) {
		this.feeConcession = feeConcession;
	}
	public String getGamesPlayed() {
		return gamesPlayed;
	}
	public void setGamesPlayed(String gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	public String getExtraActivity() {
		return extraActivity;
	}
	public void setExtraActivity(String extraActivity) {
		this.extraActivity = extraActivity;
	}
	public String getOtherRemark() {
		return otherRemark;
	}
	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public StudentInfo getTcList() {
		return tcList;
	}
	public void setTcList(StudentInfo tcList) {
		this.tcList = tcList;
	}
	public ArrayList<StudentInfo> getSerial() {
		return serial;
	}
	public void setSerial(ArrayList<StudentInfo> serial) {
		this.serial = serial;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getHeaderImage() {
		return headerImage;
	}
	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPreviosClassWord() {
		return previosClassWord;
	}

	public void setPreviosClassWord(String previosClassWord) {
		this.previosClassWord = previosClassWord;
	}

	public String getStadhar() {
		return stadhar;
	}

	public void setStadhar(String stadhar) {
		this.stadhar = stadhar;
	}

	public String getMadhar() {
		return madhar;
	}

	public void setMadhar(String madhar) {
		this.madhar = madhar;
	}

	public String getFadhar() {
		return fadhar;
	}

	public void setFadhar(String fadhar) {
		this.fadhar = fadhar;
	}

	public String getNcc() {
		return ncc;
	}

	public void setNcc(String ncc) {
		this.ncc = ncc;
	}

	
	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isShowpdfbtn() {
		return showpdfbtn;
	}

	public void setShowpdfbtn(boolean showpdfbtn) {
		this.showpdfbtn = showpdfbtn;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public int getStartSessionMonth() {
		return startSessionMonth;
	}

	public void setStartSessionMonth(int startSessionMonth) {
		this.startSessionMonth = startSessionMonth;
	}

	public int getEndSessionMonth() {
		return endSessionMonth;
	}

	public void setEndSessionMonth(int endSessionMonth) {
		this.endSessionMonth = endSessionMonth;
	}
	

}
