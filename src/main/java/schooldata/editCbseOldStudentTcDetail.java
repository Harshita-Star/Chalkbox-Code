package schooldata;

import java.io.IOException;
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
@ManagedBean(name="editCbseOldStudentTcDetail")
@ViewScoped
public class editCbseOldStudentTcDetail implements Serializable
{
	private static final long serialVersionUID = 1L;
	String reason,lastClass,lastSession,tcNumber,text,previousClass,perfom,classname2,dob1,startingdate1,dobinword,fullName,classname1,addmissionnumber;
	Date struckOffDate,applicationDate,issueDate, dob,startingDate;
	boolean showTextBox,showTC=false,showMainForm;
	String fatherName,motherName,nationality,category, date1,year,className,currentAddress,permanentAddress,issuDateStr,lastDateStr,tcType="New";
	String schoolExam,failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays,workingDayPresent,feeConcession,gamesPlayed,extraActivity,otherRemark;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem>categoryList=new ArrayList<>();
	StudentInfo tcList;
	ArrayList<StudentInfo> serial;
	String stadhar="",madhar="",fadhar="",ncc="";
	public editCbseOldStudentTcDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=new DatabaseMethods1().studentCategoryList(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		showMainForm=true;
		tcList=(StudentInfo) ss.getAttribute("cbscTCDetail");
		reason=tcList.getReason();
		text=tcList.getOtherReason();
		tcNumber=tcList.getTcNo();
		fullName=tcList.getStudentName();
		addmissionnumber=tcList.getAddNumber();
		fatherName=tcList.getFatherName();
		motherName=tcList.getMotherName();
		category=tcList.getCaste();
		startingDate=tcList.getStartingDate();
		startingdate1=tcList.getJoinDateStr();
		dob=tcList.getDob();
		dob1=tcList.getDobStr();
		dobinword=tcList.getDobInWord();
		className=tcList.getAdmitClass();
		perfom=tcList.getPerformance();
		nationality=tcList.getNationality();
		previousClass=tcList.getLastClass();
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
		issueDate=tcList.getIssueDate();
		applicationDate=tcList.getLeavingDate();
		struckOffDate=tcList.getStruckOffDate();
		lastDateStr=tcList.getLeavingDateStr();
		issuDateStr=tcList.getIssueDateStr();
		stadhar=tcList.getAadharNo();
		fadhar=tcList.getFatherAadhaar();
		madhar=tcList.getMotherAadhaar();
		ncc=tcList.getNcc();
		lastSession=tcList.getSession();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getOtherReason()
	{
		if(reason.equals("others"))
		{
			showTextBox=true;
		}
		else
		{
			showTextBox=false;
			text=null;
		}
	}

	public String transform(int num)
	{
		String  one[]={" "," First"," Second"," Third"," Fourth"," Fifth"," Sixth"," Seventh"," Eighth"," Nineth"," Tenth"," Eleventh"," Twelveth"," Thirteen"," Fourteen","Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};String ten[]={" "," "," Twenty"," Thirty"," Forty"," Fifty"," Sixty","Seventy"," Eighty"," Ninety"};
		if(num<=99)
		{
			if(num<=19) {
				date1=one[num];
			}
			else {
				int num1=num/10;
				int num2=num%10;
				date1=ten[num1]+one[num2];
			}
		}
		return date1;
	}
	public String year(int num)
	{
		String  one[]={" "," One"," Two"," Three"," Four"," five"," Six"," Seven"," Eight"," Nine"," Ten"," Eleven"," Twelve"," Thirteen"," Fourteen","Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};
		String ten[]={" "," "," Twenty "," Thirty "," Forty "," Fifty "," Sixty ","Seventy "," Eighty "," Ninety "};

		if(num>=1000 && num<=9999){
			int num1=num/1000;
			int num2=num%1000;
			if(num2<=99){
				if(num2<=19) {
					year=one[num1]+" Thousand "+one[num2];

				}
				else {
					int num4=num2/10;
					int num5=num2%10;
					year=one[num1]+" Thousand "+ten[num4]+one[num5];
				}
			}
			else{
				int num6=num2/100;
				int num7=num2%100;
				if(num7<=19){
					String num8=one[num7];
					year=one[num1]+"Thousand "+one[num6]+" Hundred "+num8;
				}
				else {
					int num0=num7/10;
					int num9=num7%10;
					year=one[num1]+" Thousand "+one[num6]+" Hundred "+ten[num0]+one[num9];
				}
			}
		}
		return year;
	}
	public void update()
	{
		DatabaseMethods1 DBM=new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		String date1= transform(dob.getDate());
		String wordmonth=DBM.monthNameByNumber(dob.getMonth()+1);
		String year1= year(dob.getYear()+1900);
		dobinword=date1+" "+wordmonth+" "+year1;
		dob1=new SimpleDateFormat("dd/MM/yyyy").format(dob);
		issuDateStr=sdf.format(issueDate);
		lastDateStr=sdf.format(struckOffDate);
		startingdate1=sdf.format(startingDate);
		if(reason.equalsIgnoreCase("others"))
		{
			reason=text;
		}

		int i=DBM.updateCbscStudentTcInforation(reason,text,tcNumber,addmissionnumber,fullName,fatherName,motherName,nationality,
				category,startingDate,className,dob,struckOffDate,previousClass,perfom,applicationDate,issueDate,schoolExam,
				failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays,workingDayPresent,feeConcession,gamesPlayed,
				extraActivity,otherRemark,stadhar,fadhar,madhar,ncc,lastSession,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TC Information Updated Successfully"));
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("viewOldStudentCbscTc.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Reason-"+reason+" -- Text-"+text+" -- TcNumber-"+tcNumber+" -- AdmNo-"+addmissionnumber+" -- FullName-"+fullName+" -- FatherName-"+fatherName+" -- MotherName-"+motherName+" -- Nationality-"+
				nationality+" -- Category-"+category+" -- StartDate-"+startingDate+" -- Class-"+className+" -- DOB-"+dob+" -- StruckDate-"+struckOffDate+" -- PreviousClass-"+previousClass+" -- Perform-"+perfom+" -- AplDate-"+applicationDate+" -- issuedAte-"+issueDate+" -- School-"+
				schoolExam+" -- Pass/Fail-"+failedOrNot+" -- Subject-"+subjectStudied+" -- Qualified-"+qualifiedPromotion+" -- MonthOfFee-"+monthOfFeePaid+" -- Working Days-"+workingDays+" -- Workingdaypresent-"+workingDayPresent+" -- FeeConcession-"+
				feeConcession+" -- Games-"+gamesPlayed+" -- ExtaActivity-"+extraActivity+" -- otherRemark-"+otherRemark+" -- StdAdhaar-"+stadhar+" -- Faadhar-"+fadhar+" -- Maadhar-"+madhar+" -- Ncc-"+ncc; 

		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Cbsc Old Tc","WEB",value,conn);
		return refNo;
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

	public String getLastSession() {
		return lastSession;
	}

	public void setLastSession(String lastSession) {
		this.lastSession = lastSession;
	}

}
