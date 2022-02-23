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

import org.primefaces.model.chart.PieChartModel;

import schooldata.ShowAttendanceRecordBean.DayList;

@ManagedBean(name="viewStudent")
@ViewScoped
public class ViewStudentBean implements Serializable {

	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	public PieChartModel pieModel1;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<FeeInfo> feelist;
	ArrayList<FeeStatus> transportfeeStatus;
	ArrayList<ClassTest> list;
	ArrayList<FeeInfo> transportfeelist;
	ArrayList<ComplaintInfo> complaintList = new ArrayList<>();
	Date date=new Date();
	Boolean Showtenthperm=false,showNinthPerm=false,showEleventhPerm=false,showtwelthPerm=false,showsixPerm=false,showeaightPerm=false;
	ArrayList<SelectItem> classList,categoryList;
	Date lastUpdatingDate,dob,startingDate,tcD;
	int previousDiscount,tutionDiscount,admissionDiscount,examDiscount,id,pincode,redCount,blueCount,greenCount;
	String nationality,fname,lname,fathersName,motherName,className,sectionName,transportRoute,gender,religion,currentAddress,
	permanentAddress,country,sclassName1,sclassName2,ssectionName1,ssectionName2,relation,phone,lastSchoolName,occupation,
	fatherFname,fatherLname,passedClass,medium,sname1,sname2,sclassid1,sclassid2,previousCategory,admitClass,category,
	fathersOccupation,residencePhone,studentImage,schoolName,add1,addNumber,handicap;
	long fathersPhone,mothersPhone;
	String sDate;
	ArrayList<SelectItem> routeList;
	ArrayList<StudentInfo> completeList = new ArrayList<StudentInfo>();
	String bpl,bplCardNo,concession,caste,singleChild,bloodGroup,aadharNo,SingleParent,livingWith,fatherEmail,motherEmail;
	String pResult,p_percent,pReason,height,weight,eyes,fatherQualification,motherQualification,motherOccupation,fatherDesignation;
	String motherDesignation,fatherOrganization,motherOrganization,fatherOfficeAdd,motherOfficeAdd,fatherIncome,motherIncome,FatherAadhaar,motherAadhaar,fatherSchoolEmp,motherSchoolEmp;
	String address,fname1,fname2,lname2,relation2,occupation2,phone2,address2,classid,d;
	String tcDate,month,admissionRemark,ledgerNo;
	ArrayList<DayList> days;
	int count,present,absent,notMarked,holiday,leave,medical,prepleave;
	ArrayList<Sum> sum;
	ArrayList<SelectItem>months;
	ArrayList<Week>weeks;
	int year;
	int monthId;
	String tenRoll,tenYearOfPass,tenBoard;
	String schid,village;


	public ViewStudentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		schid = DBM.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userType = (String) ss.getAttribute("type");
		String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		studentList=(StudentInfo) ss.getAttribute("selectedStudent");
		String preSession=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		studentImage=DBM.studentImage(DBM.schoolId(),"imageWithPath",studentList.getAddNumber(),conn);
		//studentImage="profile.png";

		complaintList = new DatabaseMethods1().studentComplaintDiary(studentList.getAddNumber(), "","all", conn);
		redCount=greenCount=blueCount=0;
		for(ComplaintInfo ss1:complaintList)
		{
			if(ss1.getType().equalsIgnoreCase("complaint"))
			{
				redCount=redCount+1;
			}
			else if(ss1.getType().equalsIgnoreCase("appreciation"))
			{
				greenCount=greenCount+1;
			}
			else if(ss1.getType().equalsIgnoreCase("neutral"))
			{
				blueCount=blueCount+1;
			}
		}

		addNumber=studentList.getAddNumber();

		createPieModels();

		/*year = date.getYear()+1900;
		monthId = date.getMonth()+1;

		months=DBM.allMonths();

		month=DBM.monthName(monthId)+"  "+year;

	weeks=new DatabaseMethods1().allWeekDay(year,monthId,studentList.getAddNumber(),studentList.getSectionid(),conn);

		for(Week ww:weeks)
		{
		present=ww.getPresent();
		absent=ww.getAbsent();
		notMarked=ww.getNotMarked();
		holiday=ww.getHoliday();
		leave=ww.getLeave();
		}*/


		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(studentImage));
		String temp=DBM.admitClass(String.valueOf(studentList.getAddNumber()),conn);
		if(temp.equals("new"))
		{
			admitClass=studentList.getClassName();
		}
		else
		{
			String classid1=DBM.classSectionNameFromidSchid(DBM.schoolId(),temp,conn);
			admitClass=(DBM.classNameFromidSchid(DBM.schoolId(),classid1,session,conn));
		}

		fname=studentList.getFname();
		startingDate=studentList.getStartingDate();
		if(startingDate!=null)
		{
			sDate=new SimpleDateFormat("dd/MM/yyyy").format(startingDate);
		}

		fathersName=studentList.getFathersName();
		if((studentList.getMotherName()==null)||(studentList.getMotherName().equals(""))||(studentList.getMotherName().equals("0"))){
			motherName="NA";
		}else{
			motherName=studentList.getMotherName();
		}



		dob=studentList.getDob();
		if(dob!=null)
		{
			d=new SimpleDateFormat("dd/MM/yyyy").format(dob);
		}
		gender=studentList.getGender();
		nationality=studentList.getNationality();
		religion=studentList.getReligion();
		currentAddress=studentList.getCurrentAddress();

		if((studentList.getPermanentAddress()==null)||(studentList.getPermanentAddress().equals("0"))||(studentList.getPermanentAddress().equals(""))){

			permanentAddress="NA";
		}else{
			permanentAddress=studentList.getPermanentAddress();
		}
		pincode=studentList.getPincode();
		if((studentList.getCountry()==null)||(studentList.getCountry().equals("0"))||(studentList.getCountry().equals(""))){
			country="NA";
		}else{
			country=studentList.getCountry();}
		fathersPhone=studentList.getFathersPhone();
		mothersPhone=studentList.getMothersPhone();
		category=studentList.getCategory();
		tutionDiscount=studentList.getTutionDiscount();
		admissionDiscount=studentList.getAdmissionDiscount();
		examDiscount=studentList.getExamDiscount();
		if((studentList.getDisability()==null)||(studentList.getDisability().equalsIgnoreCase("No"))||(studentList.getDisability().equals("")))
		{
			handicap="No";
		}
		else
		{
			handicap=studentList.getHandicap();
		}
		if((studentList.getResidenceMobile()==null)||(studentList.getResidenceMobile().equals(""))||(studentList.getResidenceMobile().equals("0"))){
			residencePhone="NA";
		}else{
			residencePhone=studentList.getResidenceMobile();}

		if((studentList.getFathersOccupation()==null)||(studentList.getFathersOccupation().equals("0"))||(studentList.getFathersOccupation().equals(""))){
			fathersOccupation="NA";
		}else{
			fathersOccupation=studentList.getFathersOccupation();
		}
		if((studentList.getBpl()==null)||(studentList.getBpl().equals("0"))||(studentList.getBpl().equals(""))){
			bpl="NA";
		}else{
			bpl=studentList.getBpl();
		}
		if((studentList.getGname()==null)||(studentList.getGname().equals("0"))||(studentList.getGname().equals(""))){
			fname1="NA";
		}else{
			fname1=studentList.getGname();
		}
		if((studentList.getAdmRemark()==null)||(studentList.getAdmRemark().equals("0"))||(studentList.getAdmRemark().equals(""))){
			admissionRemark="NA";
		}else{
			admissionRemark=studentList.getAdmRemark();
		}
		if((studentList.getLedgerNo()==null)||(studentList.getLedgerNo().equals("0"))||(studentList.getLedgerNo().equals(""))){
			ledgerNo="NA";
		}else{
			ledgerNo=studentList.getLedgerNo();
		}

		if((studentList.getBplCardNo()==null)||(studentList.getBplCardNo().equals("0"))||(studentList.getBplCardNo().equals(""))){
			bplCardNo="NA";
		}else{
			bplCardNo=studentList.getBplCardNo();}

		concession=studentList.getConcession();

		if((studentList.getCaste()==null)||(studentList.getCaste().equals("0"))||(studentList.getCaste().equals(""))){
			caste="NA";
		}else{
			caste=studentList.getCaste();
		}

		singleChild=studentList.getSingleChild();
		if((studentList.getSingleChild()==null)||(studentList.getSingleChild().equals("0"))||(studentList.getSingleChild().equals(""))){
			singleChild="NA";
		}else{
			singleChild=studentList.getSingleChild();
		}
		if((studentList.getBloodGroup()==null)||(studentList.getBloodGroup().equals("0"))||(studentList.getBloodGroup().equals(""))){
			bloodGroup="NA";
		}else{
			bloodGroup=studentList.getBloodGroup();}
		if((studentList.getAadharNo()==null)||(studentList.getAadharNo().equals("0"))||(studentList.getAadharNo().equals(""))){
			aadharNo="NA";
		}
		else{
			aadharNo=studentList.getAadharNo();
		}
		if((studentList.getSingleParent()==null)||(studentList.getSingleParent().equals("0"))||(studentList.getSingleParent().equals(""))){
			SingleParent="NA";
		}else{
			SingleParent=studentList.getSingleParent();}
		if((studentList.getLivingWith()==null)||(studentList.getLivingWith().equals("0"))||(studentList.getLivingWith().equals(""))){
			livingWith="NA";
		}else{
			livingWith=studentList.getLivingWith();
		}
		if((studentList.getFatherEmail()==null)||(studentList.getFatherEmail().equals("0"))||(studentList.getFatherEmail().equals(""))){
			fatherEmail="NA";
		}else{
			fatherEmail=studentList.getFatherEmail();
		}
		
		if((studentList.getVillage()==null)||(studentList.getVillage().equals("0"))||(studentList.getVillage().equals(""))){
			village="NA";
		}else{
			village=studentList.getVillage();
		}

		if((studentList.getMotherEmail()==null)||(studentList.getMotherEmail().equals("0"))||(studentList.getMotherEmail().equals(""))){
			motherEmail="NA";
		}else{
			motherEmail=studentList.getMotherEmail();
		}
		
		
		
		
		
		
		
		classid=studentList.getClassId();
		className=studentList.getClassName();
		//	feelist=new DatabaseMethods1().classALLFeesList(classid,studentList.getStudentStatus());
		feelist=DBM.classFeesForStudentforView(classid,preSession,studentList.getStudentStatus(),studentList.getConcession(),conn);
		transportfeeStatus=new ArrayList<>();
		ArrayList<Transport> transportFeeDetails=DBM.transportListDetails(DBM.schoolId(),studentList.getAddNumber(),preSession, conn);
		list=DBM.allTestReport(studentList.getAddNumber(),conn,userType);
		studentDetails=new ArrayList<>();
		for(int i=1;i<=12;i++)
		{
			studentList.setMonth(String.valueOf(i));
			try {
				studentDetails.add((StudentInfo) studentList.clone());
			} catch (CloneNotSupportedException e) {
				
				e.printStackTrace();
			}
		}
		//	String session=DatabaseMethods1.selectedSessionDetails();
		//		int currentMonth=Calendar.getInstance().get(Calendar.MONTH)+1;
		/*int cc=4;
			DatabaseMethods1 db=new DatabaseMethods1();
			for(StudentInfo info : studentDetails)
			{
				if(cc==13)
					cc=1;

				DataBaseMethodsReports.allAttendanceForRecord(info,cc,session,conn);

				info.setStudentName(info.getFname());
				info.setFatherName(info.getFathersName());

				int statusListSize=db.getAttendanceOfStudent(String.valueOf(cc), info.getStudentid(),session,conn);
				info.setPresent(String.valueOf(statusListSize));

				int totalPresentSize=db.getTotalAttendanceOfStudent(cc,info.getStudentid(),session,conn);
				info.setTotalPresentUpToLastMonth(String.valueOf(totalPresentSize));

				int present=totalPresentSize+statusListSize;
				info.setTotalPresent(String.valueOf(present));

				int absentListSize=db.getAbsentOfStudent(String.valueOf(cc), info.getStudentid(),session,conn);
				info.setAbsent(String.valueOf(absentListSize));

				int totalAbsentSize=db.getTotalAbsenteOfStudent(cc,info.getStudentid(),session,conn);
				info.setTotalAbsentUpToLastMonth(String.valueOf(totalAbsentSize));

				int absent=totalAbsentSize+absentListSize;
				info.setTotalAbsent(String.valueOf(String.valueOf(absent)));

				int holidayListSize=db.getHolidayOfStudent(String.valueOf(cc), info.getStudentid(),session,conn);
				info.setHolidays(String.valueOf(holidayListSize));

				int totalHolidaySize=db.getTotalHolidayeOfStudent(cc,info.getStudentid(),session,conn);
				info.setTotalHolidayUpToLastMonth(String.valueOf(totalHolidaySize));

				 int holiday=totalHolidaySize+holidayListSize;
				 info.setTotalHoliday(String.valueOf(holiday));

				info.setMonth(db.monthList(cc));
				cc++;
			}*/



		if(transportFeeDetails.size()==0){
			FeeStatus sk=new FeeStatus();
			sk.setTransportRouteName("not Availiable");
			sk.setTransportFee("not Availiable");
			sk.setMonth("not Availiable");
			transportfeeStatus.add(sk);
		}else{
			for(Transport t: transportFeeDetails)
			{

				FeeStatus fs=new FeeStatus();

				fs.setTransportRouteName(DBM.transportRouteNameFromId(DBM.schoolId(),String.valueOf(t.getRouteId()),preSession,conn));

				fs.setMonth(t.getMonth());

				fs.setStatus(t.getStatus());

				ArrayList<ClassInfo> transportFeeList = DBM.transportRouteDetailsWithFee(DBM.schoolId(),t.getRouteId(),preSession,conn);

				for(int j=0;j<transportFeeList.size();j++)
				{
					ClassInfo info1=transportFeeList.get(j);
					if(info1.getMonth() <= t.getMonthInt())
					{

						if(j == transportFeeList.size()-1)
						{
							if(info1.getTransportFee()==0){
								fs.setTransportFee("-");
							}else{
								if(studentList.getDiscountfee()==null || studentList.getDiscountfee().equals(""))
								{
									fs.setTransportFee(String.valueOf(info1.getTransportFee()-0));
								}
								else
								{
									fs.setTransportFee(String.valueOf(info1.getTransportFee()-Integer.parseInt(studentList.getDiscountfee())));
								}

							}
							transportfeeStatus.add(fs);

							break;
						}}}}}
		/*if(className.equalsIgnoreCase("First")||className.equalsIgnoreCase("Second"))
		{
			showtwelthPerm=false;
			showEleventhPerm=true;
			showNinthPerm=false;
			Showtenthperm=false;
			showsixPerm=false;
			showeaightPerm=false;
		}
		else if(className.equalsIgnoreCase("Third")||className.equalsIgnoreCase("Fourth")||className.equalsIgnoreCase("Fifth"))
		{
			showtwelthPerm=false;
			showEleventhPerm=false;
			showNinthPerm=true;
			Showtenthperm=false;
			showsixPerm=false;
			showeaightPerm=false;
		}
		else if(className.equalsIgnoreCase("Sixth")||className.equalsIgnoreCase("Seventh")||className.equalsIgnoreCase("Eight"))
		{

			showtwelthPerm=false;
			showEleventhPerm=false;
			showNinthPerm=false;
			Showtenthperm=true;
			showsixPerm=false;
			showeaightPerm=false;
		}*/
		transportRoute=DBM.presentRouteStatus(DBM.schoolId(),addNumber,conn);
		transportRoute=DBM.transportRouteNameFromId(DBM.schoolId(),transportRoute,session,conn);
		if((transportRoute==null)||(transportRoute.equals(""))||(transportRoute.equals("0")))
		{
			transportRoute="NA";
		}
		//ParentsInfo list=new DatabaseMethods1().editParentsDetail(String.valueOf(addNumber));
		fatherFname=studentList.getGname();
		//fatherLname=studentList.getLname();
		relation=studentList.getRelation();
		phone=studentList.getPhone();
		address=studentList.getAddress();
		if((studentList.getFname1()==null)||(studentList.getFname1().equals("0"))||(studentList.getFname1().equals(""))){
			fname2="NA";
		}else{
			fname2=studentList.getFname1();
		}
		//lname2=studentList.getLname1();
		relation2=studentList.getRelation1();
		occupation2=studentList.getOccupation1();
		phone2=studentList.getPhone1();
		address2=studentList.getAddress1();
		if((studentList.getLastSchoolName()==null)||(studentList.getLastSchoolName().equals("0"))||(studentList.getLastSchoolName().equals(""))){
			lastSchoolName="NA";
		}else{
			lastSchoolName=studentList.getLastSchoolName();
		}
		if((studentList.getPassedClass()==null)||(studentList.getPassedClass().equals("0"))||(studentList.getPassedClass().equals(""))){
			passedClass="NA";
		}else{
			passedClass=studentList.getPassedClass();
		}
		if((studentList.getMedium()==null)||(studentList.getMedium().equals("0"))||(studentList.getMedium().equals(""))){
			medium="NA";
		}else{
			medium=studentList.getMedium();
		}

		//lastSchoolName=studentList.getLastSchoolName();
		occupation=studentList.getOccupation();
		//passedClass=studentList.getPassedClass();
		//medium=studentList.getMedium();
		if((studentList.getSname1()==null)||(studentList.getSname1().equals("0"))||(studentList.getSname1().equals(""))){
			sname1="NA";
		}else{
			sname1=studentList.getSname1();
		}
		if((studentList.getSname2()==null)||(studentList.getSname2().equals("0"))||(studentList.getSname2().equals(""))){
			sname2="NA";
		}else{
			sname2=studentList.getSname2();
		}
		/*sname1=list.getSname1();
		sname2=studentList.getSname2();*/
		sclassName1=DBM.classNameFromidSchid(DBM.schoolId(),studentList.getSclassid1(),session,conn);
		sclassName2=DBM.classNameFromidSchid(DBM.schoolId(),studentList.getSclassid2(),session,conn);
		if((sclassName1==null)||(sclassName1.equals("0"))||(sclassName1.equals(""))){
			sclassName1="NA";
		}
		if((sclassName2==null)||(sclassName2.equals("0"))||(sclassName2.equals(""))){
			sclassName2="NA";
		}



		if((studentList.getpResult()==null)||(studentList.getpResult().equals("0"))||(studentList.getpResult().equals(""))){
			pResult="NA";
		}else{
			pResult=studentList.getpResult();
		}
		if((studentList.getP_percent()==null)||(studentList.getP_percent().equals("0"))||(studentList.getP_percent().equals(""))){
			p_percent="NA";
		}else{
			p_percent=studentList.getP_percent();
		}
		//pResult=studentList.getpResult();
		//p_percent=studentList.getP_percent();
		if((studentList.getpReason()==null)||(studentList.getpReason().equals("0"))||(studentList.getpReason().equals(""))){
			pReason="NA";
		}else{
			pReason=studentList.getpReason();
		}
		//pReason=studentList.getpReason();
		if((studentList.getHeight()==null)||(studentList.getHeight().equals("0"))||(studentList.getHeight().equals(""))){
			height="NA";
		}else{
			height=studentList.getHeight();
		}
		if((studentList.getWeight()==null)||(studentList.getWeight().equals("0"))||(studentList.getWeight().equals(""))){
			weight="NA";
		}else{
			weight=studentList.getWeight();
		}
		if((studentList.getEyes()==null)||(studentList.getEyes().equals("0"))||(studentList.getEyes().equals(""))){
			eyes="NA";
		}else{
			eyes=studentList.getEyes();
		}
		/*height=list.getHeight();
		weight=list.getWeight();
		eyes=list.getEyes();*/
		if((studentList.getFatherQualification()==null)||(studentList.getFatherQualification().equals("0"))||(studentList.getFatherQualification().equals(""))){
			fatherQualification="NA";
		}else{
			fatherQualification=studentList.getFatherQualification();
		}
		//fatherQualification=list.getFatherQualification();
		if((studentList.getMotherQualification()==null)||(studentList.getMotherQualification().equals("0"))||(studentList.getMotherQualification().equals(""))){
			motherQualification="NA";
		}else{
			motherQualification=studentList.getMotherQualification();
		}
		//motherQualification=studentList.getMotherQualification();
		if((studentList.getMotherOccupation()==null)||(studentList.getMotherOccupation().equals("0"))||(studentList.getMotherOccupation().equals(""))){
			motherOccupation="NA";
		}else{
			motherOccupation=studentList.getMotherOccupation();
		}
		//motherOccupation=studentList.getMotherOccupation();
		if((studentList.getFatherDesignation()==null)||(studentList.getFatherDesignation().equals("0"))||(studentList.getFatherDesignation().equals(""))){
			fatherDesignation="NA";
		}else{
			fatherDesignation=studentList.getFatherDesignation();
		}
		//fatherDesignation=studentList.getFatherDesignation();
		if((studentList.getMotherDesignation()==null)||(studentList.getMotherDesignation().equals("0"))||(studentList.getMotherDesignation().equals(""))){
			motherDesignation="NA";
		}else{
			motherDesignation=studentList.getMotherDesignation();
		}
		//motherDesignation=studentList.getMotherDesignation();
		if((studentList.getFatherOrganization()==null)||(studentList.getFatherOrganization().equals("0"))||(studentList.getFatherOrganization().equals(""))){
			fatherOrganization="NA";
		}else{
			fatherOrganization=studentList.getFatherOrganization();
		}
		//fatherOrganization=studentList.getFatherOrganization();
		if((studentList.getMotherOrganization()==null)||(studentList.getMotherOrganization().equals("0"))||(studentList.getMotherOrganization().equals(""))){
			motherOrganization="NA";
		}else{
			motherOrganization=studentList.getMotherOrganization();
		}
		//motherOrganization=studentList.getMotherOrganization();
		if((studentList.getFatherOfficeAdd()==null)||(studentList.getFatherOfficeAdd().equals("0"))||(studentList.getFatherOfficeAdd().equals(""))){
			fatherOfficeAdd="NA";
		}else{
			fatherOfficeAdd=studentList.getFatherOfficeAdd();
		}
		//fatherOfficeAdd=studentList.getFatherOfficeAdd();
		if((studentList.getMotherOfficeAdd()==null)||(studentList.getMotherOfficeAdd().equals("0"))||(studentList.getMotherOfficeAdd().equals(""))){
			motherOfficeAdd="NA";
		}else{
			motherOfficeAdd=studentList.getMotherOfficeAdd();
		}
		//motherOfficeAdd=studentList.getMotherOfficeAdd();
		if((studentList.getFatherAnnIncome()==null)||(studentList.getFatherAnnIncome().equals("0"))||(studentList.getFatherAnnIncome().equals(""))){
			fatherIncome="NA";
		}else{
			fatherIncome=studentList.getFatherAnnIncome();
		}
		//fatherIncome=list.getFatherIncome();
		if((studentList.getMotherAnnIncome()==null)||(studentList.getMotherAnnIncome().equals("0"))||(studentList.getMotherAnnIncome().equals(""))){
			motherIncome="NA";
		}else{
			motherIncome=studentList.getMotherAnnIncome();
		}
		//motherIncome=studentList.getMotherIncome();
		if((studentList.getFatherAadhaar()==null)||(studentList.getFatherAadhaar().equals("0"))||(studentList.getFatherAadhaar().equals(""))){
			FatherAadhaar="NA";
		}else{
			FatherAadhaar=studentList.getFatherAadhaar();
		}
		//FatherAadhaar=studentList.getFatherAadhaar();
		if((studentList.getMotherAadhaar()==null)||(studentList.getMotherAadhaar().equals("0"))||(studentList.getMotherAadhaar().equals(""))){
			motherAadhaar="NA";
		}else{
			motherAadhaar=studentList.getMotherAadhaar();
		}
		//motherAadhaar=studentList.getMotherAadhaar();
		if((studentList.getFatherSchoolEmp()==null)||(studentList.getFatherSchoolEmp().equals("0"))||(studentList.getFatherSchoolEmp().equals(""))){
			fatherSchoolEmp="NA";
		}else{
			fatherSchoolEmp=studentList.getFatherSchoolEmp();
		}
		if((studentList.getMotherSchoolEmp()==null)||(studentList.getMotherSchoolEmp().equals("0"))||(studentList.getMotherSchoolEmp().equals(""))){
			motherSchoolEmp="NA";
		}else{
			motherSchoolEmp=studentList.getMotherSchoolEmp();
		}
		
		
		
		
		if((studentList.getTenRoll()==null)||(studentList.getTenRoll().equals("0"))||(studentList.getTenRoll().equals(""))){
			tenRoll="NA";
		}else{
			tenRoll=studentList.getTenRoll();
		}
		
		if((studentList.getTenYearOfPass()==null)||(studentList.getTenYearOfPass().equals("0"))||(studentList.getTenYearOfPass().equals(""))){
			tenYearOfPass="NA";
		}else{
			tenYearOfPass=studentList.getTenYearOfPass();
		}
		
		if((studentList.getTenBoard()==null)||(studentList.getTenBoard().equals("0"))||(studentList.getTenBoard().equals(""))){
			tenBoard="NA";
		}else{
			tenBoard=studentList.getTenBoard();
		}
		
		
		
		//fatherSchoolEmp=studentList.getFatherSchoolEmp();
		//motherSchoolEmp=studentList.getMotherSchoolEmp();
		tcD=studentList.getTcDate();
		if((tcD==null)||(tcD.equals("0"))||(tcD.equals(""))){
			tcDate="NA";
		}else{
			tcDate=new SimpleDateFormat("dd/MM/yyyy").format(tcD);
		}
		//tcDate=list.getTcDate();

		completeList=DBM.siblingListOfStudent(studentList.getAddNumber(),conn);
		
		try {
			
			for(StudentInfo cvb : completeList)
			{
				String[] spliter =  cvb.getFname().split("-");
				String id = spliter[1];
				cvb.setFname(spliter[0]);
				String classandSection = DBM.studentClassSectionNameById(DBM.schoolId(), id, conn);
				String[] split2 = classandSection.split(" - ");
				cvb.setClassName(split2[0]);
				cvb.setSectionName(split2[1]);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createPieModels() {
		createPieModel1();
	}
	private void createPieModel1() {

		Connection conn=DataBaseConnection.javaConnection();


		String sectionId=studentList.getSectionid();
		//int  sum2=new DatabaseMethods1().totalStudent(conn,studentId);

		int b=new DatabaseMethods1().TotalPresentDays(conn,sectionId);
		int a=new DatabaseMethods1().totalStudentAbsent(conn,addNumber,"A");
		int l=new DatabaseMethods1().totalStudentAbsent(conn,addNumber,"L");
		int ml=new DatabaseMethods1().totalStudentAbsent(conn,addNumber,"ML");
		int pl=new DatabaseMethods1().totalStudentAbsent(conn,addNumber,"PL");
		int h=new DatabaseMethods1().totalStudentAbsent(conn,addNumber,"H");
		int d=b-(a+l+ml+pl+h);
		//sum=new DatabaseMethods1().alltAttendance(conn);
		pieModel1 = new PieChartModel();
		pieModel1.set("Present - "+d, d);
		pieModel1.set("Absent - "+a, a);
		pieModel1.set("Leave - "+l, l);
		pieModel1.set("Holiday - "+h, h);
		pieModel1.set("Medical Leave - "+ml, ml);
		pieModel1.set("Preparation Leave - "+pl, pl);
		/* pieModel1.set("Leave - "+sum.get(0).getLeave(), sum.get(0).getLeave());
   pieModel1.set("Not Marked - "+sum.get(0).getNotMarked(), sum.get(0).getNotMarked());*/
		pieModel1.setShowDataLabels(true);
		pieModel1.setTitle("Total Meetings - "+b);
		pieModel1.setLegendPosition("w");
		//pieModel1.setSeriesColors("00A65A,DD4B39,F39C12,00C0EF");
		pieModel1.setSeriesColors("93F2A3,F79BA3,FFBF79,FF512DA8,FFFF5722,FF0097A7");
		pieModel1.setExtender("skinPie");
		try
		{
			conn.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public String backward()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		if(monthId<2)
		{

			monthId=12;
			year--;
			month=DBM.monthNameByNumber(monthId)+"  "+year;


		}
		else
		{

			monthId--;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}

		weeks=new DatabaseMethods1().allWeekDay(year,monthId,studentList.getAddNumber(),studentList.getSectionid(),studentList.getStartingDate(),conn);
		for(Week ww:weeks)
		{
			present=ww.getPresent();
			absent=ww.getAbsent();
			notMarked=ww.getNotMarked();
			holiday=ww.getHoliday();
			leave=ww.getLeave();
			medical=ww.getMedical();
			prepleave=ww.getPrepleave();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return month;

	}

	public String forward()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		if(monthId>11)
		{

			monthId=1;
			year++;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}
		else
		{

			monthId++;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}

		weeks=new DatabaseMethods1().allWeekDay(year,monthId,studentList.getAddNumber(),studentList.getSectionid(),studentList.getStartingDate(),conn);
		for(Week ww:weeks)
		{
			present=ww.getPresent();
			absent=ww.getAbsent();
			notMarked=ww.getNotMarked();
			holiday=ww.getHoliday();
			leave=ww.getLeave();
			medical=ww.getMedical();
			prepleave=ww.getPrepleave();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return month;
	}
	public Boolean getShowsixPerm() {
		return showsixPerm;
	}

	public void setShowsixPerm(Boolean showsixPerm) {
		this.showsixPerm = showsixPerm;
	}

	public Boolean getShoweaightPerm() {
		return showeaightPerm;
	}

	public void setShoweaightPerm(Boolean showeaightPerm) {
		this.showeaightPerm = showeaightPerm;
	}

	public Boolean getShowEleventhPerm() {
		return showEleventhPerm;
	}

	public void setShowEleventhPerm(Boolean showEleventhPerm) {
		this.showEleventhPerm = showEleventhPerm;
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
	public Boolean getShowtwelthPerm() {
		return showtwelthPerm;
	}

	public void setShowtwelthPerm(Boolean showtwelthPerm) {
		this.showtwelthPerm = showtwelthPerm;
	}

	public Boolean getShowtenthperm() {
		return Showtenthperm;
	}

	public void setShowtenthperm(Boolean showtenthperm) {
		Showtenthperm = showtenthperm;
	}

	public Boolean getShowNinthPerm() {
		return showNinthPerm;
	}

	public void setShowNinthPerm(Boolean showNinthPerm) {
		this.showNinthPerm = showNinthPerm;
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
	public ArrayList<SelectItem> getClassList() {
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
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAdd1() {
		return add1;
	}
	public void setAdd1(String add1) {
		this.add1 = add1;
	}
	public String getBpl() {
		return bpl;
	}
	public void setBpl(String bpl) {
		this.bpl = bpl;
	}
	public String getBplCardNo() {
		return bplCardNo;
	}
	public void setBplCardNo(String bplCardNo) {
		this.bplCardNo = bplCardNo;
	}
	public String getConcession() {
		return concession;
	}
	public void setConcession(String concession) {
		this.concession = concession;
	}
	public String getCaste() {
		return caste;
	}
	public void setCaste(String caste) {
		this.caste = caste;
	}
	public String getSingleChild() {
		return singleChild;
	}
	public void setSingleChild(String singleChild) {
		this.singleChild = singleChild;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}
	public String getSingleParent() {
		return SingleParent;
	}
	public void setSingleParent(String singleParent) {
		SingleParent = singleParent;
	}
	public String getLivingWith() {
		return livingWith;
	}
	public void setLivingWith(String livingWith) {
		this.livingWith = livingWith;
	}
	public String getFatherEmail() {
		return fatherEmail;
	}
	public void setFatherEmail(String fatherEmail) {
		this.fatherEmail = fatherEmail;
	}
	public String getMotherEmail() {
		return motherEmail;
	}
	public void setMotherEmail(String motherEmail) {
		this.motherEmail = motherEmail;
	}
	public String getpResult() {
		return pResult;
	}
	public void setpResult(String pResult) {
		this.pResult = pResult;
	}
	public String getP_percent() {
		return p_percent;
	}
	public void setP_percent(String p_percent) {
		this.p_percent = p_percent;
	}
	public String getpReason() {
		return pReason;
	}
	public void setpReason(String pReason) {
		this.pReason = pReason;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getEyes() {
		return eyes;
	}
	public void setEyes(String eyes) {
		this.eyes = eyes;
	}
	public String getFatherQualification() {
		return fatherQualification;
	}
	public void setFatherQualification(String fatherQualification) {
		this.fatherQualification = fatherQualification;
	}
	public String getMotherQualification() {
		return motherQualification;
	}
	public void setMotherQualification(String motherQualification) {
		this.motherQualification = motherQualification;
	}
	public String getMotherOccupation() {
		return motherOccupation;
	}
	public void setMotherOccupation(String motherOccupation) {
		this.motherOccupation = motherOccupation;
	}
	public String getFatherDesignation() {
		return fatherDesignation;
	}
	public void setFatherDesignation(String fatherDesignation) {
		this.fatherDesignation = fatherDesignation;
	}
	public String getMotherDesignation() {
		return motherDesignation;
	}
	public void setMotherDesignation(String motherDesignation) {
		this.motherDesignation = motherDesignation;
	}
	public String getFatherOrganization() {
		return fatherOrganization;
	}
	public void setFatherOrganization(String fatherOrganization) {
		this.fatherOrganization = fatherOrganization;
	}
	public String getMotherOrganization() {
		return motherOrganization;
	}
	public void setMotherOrganization(String motherOrganization) {
		this.motherOrganization = motherOrganization;
	}
	public String getFatherOfficeAdd() {
		return fatherOfficeAdd;
	}
	public void setFatherOfficeAdd(String fatherOfficeAdd) {
		this.fatherOfficeAdd = fatherOfficeAdd;
	}
	public String getMotherOfficeAdd() {
		return motherOfficeAdd;
	}
	public void setMotherOfficeAdd(String motherOfficeAdd) {
		this.motherOfficeAdd = motherOfficeAdd;
	}
	public String getFatherIncome() {
		return fatherIncome;
	}
	public void setFatherIncome(String fatherIncome) {
		this.fatherIncome = fatherIncome;
	}
	public String getMotherIncome() {
		return motherIncome;
	}
	public void setMotherIncome(String motherIncome) {
		this.motherIncome = motherIncome;
	}
	public String getFatherAadhaar() {
		return FatherAadhaar;
	}
	public void setFatherAadhaar(String fatherAadhaar) {
		FatherAadhaar = fatherAadhaar;
	}
	public String getMotherAadhaar() {
		return motherAadhaar;
	}
	public void setMotherAadhaar(String motherAadhaar) {
		this.motherAadhaar = motherAadhaar;
	}
	public String getFatherSchoolEmp() {
		return fatherSchoolEmp;
	}
	public void setFatherSchoolEmp(String fatherSchoolEmp) {
		this.fatherSchoolEmp = fatherSchoolEmp;
	}
	public String getMotherSchoolEmp() {
		return motherSchoolEmp;
	}
	public void setMotherSchoolEmp(String motherSchoolEmp) {
		this.motherSchoolEmp = motherSchoolEmp;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFname2() {
		return fname2;
	}
	public void setFname2(String fname2) {
		this.fname2 = fname2;
	}
	public String getLname2() {
		return lname2;
	}
	public void setLname2(String lname2) {
		this.lname2 = lname2;
	}
	public String getRelation2() {
		return relation2;
	}
	public void setRelation2(String relation2) {
		this.relation2 = relation2;
	}
	public String getOccupation2() {
		return occupation2;
	}
	public void setOccupation2(String occupation2) {
		this.occupation2 = occupation2;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Date getTcD() {
		return tcD;
	}
	public void setTcD(Date tcD) {
		this.tcD = tcD;
	}
	public String getTcDate() {
		return tcDate;
	}
	public void setTcDate(String tcDate) {
		this.tcDate = tcDate;
	}
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}
	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}
	public ArrayList<FeeInfo> getTransportfeelist() {
		return transportfeelist;
	}
	public void setTransportfeelist(ArrayList<FeeInfo> transportfeelist) {
		this.transportfeelist = transportfeelist;
	}
	public ArrayList<FeeStatus> getTransportfeeStatus() {
		return transportfeeStatus;
	}
	public void setTransportfeeStatus(ArrayList<FeeStatus> transportfeeStatus) {
		this.transportfeeStatus = transportfeeStatus;
	}
	public String getClassid() {
		return classid;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getsDate() {
		return sDate;
	}
	public void setsDate(String sDate) {
		this.sDate = sDate;
	}
	public String getFname1() {
		return fname1;
	}
	public void setFname1(String fname1) {
		this.fname1 = fname1;
	}
	public String getHandicap() {
		return handicap;
	}
	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}
	public ArrayList<ClassTest> getList() {
		return list;
	}
	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public ArrayList<DayList> getDays() {
		return days;
	}

	public void setDays(ArrayList<DayList> days) {
		this.days = days;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPresent() {
		return present;
	}

	public void setPresent(int present) {
		this.present = present;
	}

	public int getAbsent() {
		return absent;
	}

	public void setAbsent(int absent) {
		this.absent = absent;
	}

	public int getNotMarked() {
		return notMarked;
	}

	public void setNotMarked(int notMarked) {
		this.notMarked = notMarked;
	}

	public int getHoliday() {
		return holiday;
	}

	public void setHoliday(int holiday) {
		this.holiday = holiday;
	}

	public ArrayList<Sum> getSum() {
		return sum;
	}

	public void setSum(ArrayList<Sum> sum) {
		this.sum = sum;
	}

	public ArrayList<SelectItem> getMonths() {
		return months;
	}

	public void setMonths(ArrayList<SelectItem> months) {
		this.months = months;
	}

	public ArrayList<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(ArrayList<Week> weeks) {
		this.weeks = weeks;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonthId() {
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public int getLeave() {
		return leave;
	}

	public void setLeave(int leave) {
		this.leave = leave;
	}

	public ArrayList<ComplaintInfo> getComplaintList() {
		return complaintList;
	}

	public void setComplaintList(ArrayList<ComplaintInfo> complaintList) {
		this.complaintList = complaintList;
	}

	public int getRedCount() {
		return redCount;
	}

	public void setRedCount(int redCount) {
		this.redCount = redCount;
	}

	public int getBlueCount() {
		return blueCount;
	}

	public void setBlueCount(int blueCount) {
		this.blueCount = blueCount;
	}

	public int getGreenCount() {
		return greenCount;
	}

	public void setGreenCount(int greenCount) {
		this.greenCount = greenCount;
	}

	public PieChartModel getPieModel1() {
		return pieModel1;
	}

	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}

	public int getMedical() {
		return medical;
	}

	public void setMedical(int medical) {
		this.medical = medical;
	}

	public int getPrepleave() {
		return prepleave;
	}

	public void setPrepleave(int prepleave) {
		this.prepleave = prepleave;
	}

	public ArrayList<StudentInfo> getCompleteList() {
		return completeList;
	}

	public void setCompleteList(ArrayList<StudentInfo> completeList) {
		this.completeList = completeList;
	}

	public String getAdmissionRemark() {
		return admissionRemark;
	}

	public void setAdmissionRemark(String admissionRemark) {
		this.admissionRemark = admissionRemark;
	}

	public String getLedgerNo() {
		return ledgerNo;
	}

	public void setLedgerNo(String ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	public String getTenRoll() {
		return tenRoll;
	}

	public void setTenRoll(String tenRoll) {
		this.tenRoll = tenRoll;
	}

	public String getTenYearOfPass() {
		return tenYearOfPass;
	}

	public void setTenYearOfPass(String tenYearOfPass) {
		this.tenYearOfPass = tenYearOfPass;
	}

	public String getTenBoard() {
		return tenBoard;
	}

	public void setTenBoard(String tenBoard) {
		this.tenBoard = tenBoard;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}
	
	

}
