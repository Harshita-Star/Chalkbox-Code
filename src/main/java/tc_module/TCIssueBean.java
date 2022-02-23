package tc_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.SendingNotifications;
import schooldata.SideMenuBean;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import session_work.RegexPattern;

@ManagedBean(name="tcIssue")
@ViewScoped
public class TCIssueBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	transient StreamedContent file;
	ArrayList<String> reasons = new ArrayList<>();
	String ncc,reason,lastClass,tcNumber,proofOfBirth,text,srNo,previousClass,schoolExamWord,previosClassWord,website,headerImage,perfom,classname2,dob1,startingdate1,dobinword,fullName,classname1,addmissionnumber;
	Date date2,applicationDate,issueDate, dob,startingDate;
	boolean showTextBox,showTC,showMainForm;
	String session,rollNo,fatherName,motherName,nationality,category, date1,year,className,currentAddress,permanentAddress,issuDateStr,lastDateStr,startYear="",affNo="",schNo="";
	String schoolExam,failedOrNot,subjectStudied,qualifiedPromotion="",proofDob="",schoolAffilationNo,schoolCategory,promotedClassWord,applicationDateString,qualifiedPromotionCheck,monthOfFeePaid,workingDays,workingDayPresent,feeConcession,gamesPlayed,extraActivity,otherRemark,stadhar,fadhar,madhar;
	ArrayList<StudentInfo> studentList;
	StudentInfo tcList;
	ArrayList<StudentInfo> serial;
	String serialType="continue",transferCertificatedFrom,studentImage,result;
	boolean showmarksheet=false,showBlmMarksheet=false,showBookNumber=false,showimmortal=false;
	boolean sharewoodmarksheet=false,northwoodTc=false,showMapleBearTc=false,showSSChildrenTc=false,showSSChildrenGirlTc=false,showCbseDraftTc=false,showPNFTc=false;
	boolean renTransferCertificateFrom=false,checkPromtionRender=false,showpdfbtn=false;
	DateToWords dtw = new DateToWords();
	ArrayList<String> subjectList = new ArrayList<String>();
	ArrayList<String> selectedSubjects = new ArrayList<String>();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public void getOtherReason()
	{
		if(reason.equalsIgnoreCase("others")){
			showTextBox=true;
		}
		else{
			showTextBox=false;
			text=null;
		}
	}

	public void update()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		SchoolInfoList ls = DBM.fullSchoolInfo(conn);
		/*if(schid.equalsIgnoreCase("251")
        		|| schid.equalsIgnoreCase("252")
        		|| schid.equalsIgnoreCase("243")
        		|| schid.equalsIgnoreCase("3")
        		|| schid.equalsIgnoreCase("287"))*/
	
		
		if(ls.getTcType().equalsIgnoreCase("cbse"))
		{
			 if(ls.getBranch_id().equals("99"))
			 {
				showCbseDraftTc =false;
				sharewoodmarksheet=false;
				showBlmMarksheet=false;
				showmarksheet=false;
				northwoodTc=false;
				showMapleBearTc=false;
				showSSChildrenGirlTc = false;
				showSSChildrenTc=true;
				 showPNFTc=false;
				
			  }
			 else if(ls.getBranch_id().equals("101")){
				 showCbseDraftTc =false;
					sharewoodmarksheet=false;
					showBlmMarksheet=false;
					showmarksheet=false;
					northwoodTc=false;
					showMapleBearTc=false;
					showSSChildrenTc=false;
					showSSChildrenGirlTc = true;
					 showPNFTc=false;
			 }
			 else if(ls.getBranch_id().equals("78"))
			 {
				 showPNFTc=true;
				 showCbseDraftTc =false;
					northwoodTc=false;
					sharewoodmarksheet=false;
					showBlmMarksheet=false;
					showmarksheet=false;
					showMapleBearTc=false;
					showSSChildrenTc=false;
			 }
			 else
			 {
				    showCbseDraftTc = true;
					sharewoodmarksheet=false;
					showBlmMarksheet=false;
					showmarksheet=false;
					northwoodTc=false;
					showMapleBearTc=false;
					showSSChildrenTc=false;
					 showPNFTc=false;
			 }
		}
		else
		{
		if(ls.getBranch_id().equals("93"))
		  {
			showCbseDraftTc =false;
			sharewoodmarksheet=true;
			showBlmMarksheet=false;
			showmarksheet=false;
			northwoodTc=false;
			showMapleBearTc=false;
			showSSChildrenTc=false;
			 showPNFTc=false;
		  }
		  else if(ls.getBranch_id().equals("99"))
		 {
			showCbseDraftTc =false;
			sharewoodmarksheet=false;
			showBlmMarksheet=false;
			showmarksheet=false;
			northwoodTc=false;
			showMapleBearTc=false;
			showSSChildrenTc=true;
			showSSChildrenGirlTc = false;
			 showPNFTc=false;
			
		  } else if(ls.getBranch_id().equals("101")){
				 showCbseDraftTc =false;
					sharewoodmarksheet=false;
					showBlmMarksheet=false;
					showmarksheet=false;
					northwoodTc=false;
					showMapleBearTc=false;
					showSSChildrenTc=false;
					showSSChildrenGirlTc = true;
					 showPNFTc=false;
			 }
		  else if(ls.getBranch_id().equals("4"))
		  {
			showCbseDraftTc =false;
			northwoodTc=true;
			sharewoodmarksheet=false;
			showBlmMarksheet=false;
			showmarksheet=false;
			showMapleBearTc=false;
			 showPNFTc=false;
			showSSChildrenTc=false;
		 }
		 else if(ls.getBranch_id().equals("88"))
		 {
			showCbseDraftTc =false;
			northwoodTc=false;
			sharewoodmarksheet=false;
			showBlmMarksheet=false;
			showmarksheet=false;
			showMapleBearTc=true;
			 showPNFTc=false;
			showSSChildrenTc=false;
		 }
		
		else
		 {
			if(ls.getTcType().equalsIgnoreCase("new"))
			{
				showCbseDraftTc =false;
				showBlmMarksheet=true;
				showmarksheet=false;
				 showPNFTc=false;
				northwoodTc=false;
				sharewoodmarksheet=false;
				showMapleBearTc=false;
				showSSChildrenTc=false;
				if(ls.getBranch_id().equals("27"))
				{
					showimmortal=false;
				}
				else
				{
					showimmortal=true;
				}
			}
			else
			{
				showCbseDraftTc =false;
				showBlmMarksheet=false;
				showmarksheet=true;
				northwoodTc=false;
				sharewoodmarksheet=false;
				showMapleBearTc=false;
				showSSChildrenTc=false;
				 showPNFTc=false;
			}
		
		}
		}
		
		if(ls.getSchid().equals("216") || ls.getSchid().equals("221")) {
			showpdfbtn = true;
		}

		
		for(StudentInfo st:studentList)
		{
			srNo=String.valueOf(st.getSrNo());
			addmissionnumber=String.valueOf(st.getAddNumber());

			fullName=st.getFname();
			startingDate=st.getStartingDate();
			startingdate1= sdf.format(startingDate);
			fatherName=st.getFathersName();
			motherName=st.getMotherName();
			className=st.getClassName();
			dob=st.getDob();
			

//			String date1= transform(dob.getDate());
//			String wordmonth=DBM.monthNameByNumber(dob.getMonth()+1);
//			String year1= year(dob.getYear()+1900);
//
//			dobinword=date1+" "+wordmonth+" "+year1;
			
			dobinword = dtw.DateToWordsConvert(sdf.format(dob));

			dob1=sdf.format(dob);
			if(schid.equals("306"))
			{
				nationality=st.getReligion();
				
			}
			else
			{
				nationality=st.getNationality();
				
				
			}
			
			currentAddress=st.getCurrentAddress();
			permanentAddress=st.getPermanentAddress();
			category=st.getCategory();

			stadhar=st.getAadharNo();
			fadhar=st.getFatherAadhaar();
			madhar=st.getMotherAadhaar();
			studentImage = st.getStudentImage();
		}

		serial=DBM.getpromostionclass1(addmissionnumber,conn);

		int number =0;
		if(ls.getBranch_id().equals("88"))
		{
		   number = objTc.finfMapleBearTcNumber(serialType,conn);
			tcNumber=String.valueOf(number)+"/"+session.substring(0,5)+session.substring(7, 9);
			objTc.inserserialnoMapleBear(addmissionnumber,tcNumber.substring(0, tcNumber.indexOf('/')),conn);
			String refNo;
			try {
				refNo=addWorkLog(conn,addmissionnumber,tcNumber);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			serialType = objTc.checkTcNoType(ls.getSchid(),conn);
			 number=objTc.getseraialno(serialType,conn);
		      
			tcNumber=String.valueOf(number);
			objTc.inserserialno(addmissionnumber,number,conn);
			String refNo4;
			try {
				refNo4=addWorkLog4(conn,addmissionnumber,number);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		subjectStudied = "";
		for(String ssc : selectedSubjects)
		{
			subjectStudied += ssc+", ";
		}
		if(!(selectedSubjects.size()==0))
		{
			subjectStudied = subjectStudied.substring(0, subjectStudied.length()-2);
		}
		
		if(qualifiedPromotionCheck.equalsIgnoreCase("YES")&&!qualifiedPromotion.trim().equalsIgnoreCase(""))
		{
			checkPromtionRender= true;	
		}
		else
		{
			checkPromtionRender = false;
		}


//		if(reason.equalsIgnoreCase("others"))
//		{
//			reason=text;
//			reason = reason;
//		}
		issuDateStr=sdf.format(issueDate);
		lastDateStr=sdf.format(date2);
		applicationDateString = sdf.format(applicationDate);
		
		int i=objTc.updateTCInformation1(reason, date2, applicationDate, issueDate, tcNumber, "issue",addmissionnumber
				,perfom,text,schoolExam,failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays
				,workingDayPresent,feeConcession,gamesPlayed,extraActivity,otherRemark,applicationDate,ncc,transferCertificatedFrom,qualifiedPromotionCheck,result,proofDob,startYear,conn);
		if(i==1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(schid.equals("216")||schid.equals("302")||schid.equals("221")) {
				for(StudentInfo stdinf: studentList) {
					String classTeacherId = new DatabaseMethods1().getClassTeacherId(stdinf.getClassId(), stdinf.getSectionid(), schid, session, conn);
					String classTeacherName = new DatabaseMethods1().employeeNameById(classTeacherId, conn);
					
					String msg = "Dear "+classTeacherName+",\r\n" + 
							"\r\n" + 
							"Following student of your class "+stdinf.getClassName()+" is terminated on request of parents.\r\n" + 
							"\r\n" + 
							"1. "+stdinf.getFname()+"\r\n" + 
							" \r\n" + 
							"Please remove name from your class records\r\n" + 
							"\r\n" + 
							"Regards\r\n" + 
							"\r\n" + 
							ls.getSmsSchoolName();
					
					new SendingNotifications().sendNotifi(msg, schid, "", "", session, "Teacher", classTeacherId,"Termination");
				}
			}
			
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TC generated successfully"));
			DBM.updateAdmitClassInReg(addmissionnumber,classname1,conn);
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			selectedSubjects = new ArrayList<String>();
			subjectList = new ArrayList<String>();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}
		SchoolInfoList info=DBM.fullSchoolInfo(conn);
		String savePath="";
		if(info.getProjecttype().equals("online"))
		{
			savePath=info.getDownloadpath();
		}
		headerImage=savePath+info.getTcHeader();
		website=info.getWebsite();
        studentImage = savePath+studentImage; 
        promotedClassInWord(qualifiedPromotion);
		affNo=info.getAdd4();
		schNo=info.getRegNo();
		
		schoolAffilationNo = ls.getAdd4();
		schoolCategory = ls.getSchoolCategory();

		showTC=true;showMainForm=false;
		
		
		if(!subjectStudied.equalsIgnoreCase(""))
		{
			String[] spliter = subjectStudied.split(",");
			
			subjectStudied = "";
			for(int k=0;k<spliter.length;k++) {
				subjectStudied += spliter[k].trim()+", ";
			}
			
			subjectStudied = subjectStudied.substring(0, subjectStudied.length()-2);
		}
		if(ls.getBranch_id().equals("99")||ls.getBranch_id().equals("101"))
		{
			dobinword=dobinword.replaceAll("of", " ");
			dobinword=dobinword.toUpperCase();
			nationality=nationality.toUpperCase();
			category=category.toUpperCase();
			previosClassWord=previosClassWord.toUpperCase();
			if(!previosClassWord.equals("")) {
				previosClassWord="("+previosClassWord.toUpperCase()+")";
			}
			
			promotedClassWord=promotedClassWord.toUpperCase();
			if(!promotedClassWord.equals("")) {
				promotedClassWord="("+promotedClassWord.toUpperCase()+")";
				
			}
			
			schoolExamWord(schoolExam);
			schoolExam = schoolExam.toUpperCase();
			schoolExamWord = schoolExamWord.toUpperCase();
			if(!schoolExamWord.equals("")) {
				schoolExamWord = "("+schoolExamWord.toUpperCase()+")";
			}
			subjectStudied=subjectStudied.toUpperCase();
			proofDob = proofDob.toUpperCase();
			failedOrNot = failedOrNot.toUpperCase();
			qualifiedPromotion= qualifiedPromotion.toUpperCase();
			monthOfFeePaid=monthOfFeePaid.toUpperCase();
			feeConcession=feeConcession.toUpperCase();
//			schoolCategory=schoolCategory.toUpperCase();
			
			if(gamesPlayed.equals("N.A.")&& extraActivity.equals("N.A."))
			{
				gamesPlayed="N.A.";
			}else if ((gamesPlayed.equals("") && extraActivity.equals("N.A."))||(gamesPlayed.equals("N.A.") && extraActivity.equals(""))) {
				gamesPlayed="N.A.";
			} 
			else
			{
				if(gamesPlayed.equals("")||gamesPlayed.equals("N.A.")) {
					gamesPlayed = extraActivity.toUpperCase();
				}else if(extraActivity.equals("")||extraActivity.equals("N.A.")){
					gamesPlayed = gamesPlayed.toUpperCase();
				}else {
					gamesPlayed=gamesPlayed.toUpperCase()+","+extraActivity.toUpperCase();
				}
			}
			
			otherRemark=otherRemark.toUpperCase();
		}
		else
		{
			startYear = session.split("-")[0];
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getReasons() {
		reasons = new ArrayList<>();
		if (schid.equals("313") || schid.equals("315")) {
			reasons.add("Due to long absent".toUpperCase());
			reasons.add("PARENT’S DESIRE".toUpperCase());
			reasons.add("FURTHER STUDIES".toUpperCase());
			reasons.add("To study else where".toUpperCase());
			reasons.add("To study for higher classes".toUpperCase());
			reasons.add("Parents transfer to other city".toUpperCase());
			reasons.add("Transfer of father".toUpperCase());
			reasons.add("Others".toUpperCase());
		} else {
			reasons.add("Due to long absent");
			reasons.add("PARENT'S DESIRE");
			reasons.add("FURTHER STUDIES");
			reasons.add("To study else where");
			reasons.add("To study for higher classes");
			reasons.add("Parents transfer to other city");
			reasons.add("Others");
		}
		return reasons;
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = value = addmissionnumber+" -- "+classname1; 

		
		String refNo = workLg.saveWorkLogMehod(language,"Update admit Class","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog(Connection conn,String addmissionnumber,String number)
	{
	    String value = "";
		String language= "";
		
		language = value = addmissionnumber+" -- "+number; 

		
		String refNo = workLg.saveWorkLogMehod(language,"Insert Serial No","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog4(Connection conn,String addmissionnumber,int number)
	{
	    String value = "";
		String language= "";
		
		language = value = addmissionnumber+" -- "+number;

		
		String refNo = workLg.saveWorkLogMehod(language,"Insert Serial No","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "Reason-"+reason+" -- Text-"+text+" -- TcNo-"+tcNumber+" -- Date of Struck off-"+date2+" -- First Class-"+classname1+
				" -- last Class-"+previousClass+" --Conduct-"+perfom+" --Date of leaving-"+applicationDate+" --Date Issue"+issueDate+" --Last School-"+transferCertificatedFrom+
				" --Promotion Check-"+qualifiedPromotionCheck+" --promotion-"+qualifiedPromotion+" --Failed-"+failedOrNot+" --Subject-"+subjectStudied+" --Month Fee paid-"+monthOfFeePaid+" --Working days-"+workingDays+" --WorkingDaysPresent-"+workingDayPresent+
				" --Fee Concession-"+feeConcession+" --NCC-"+ncc+" --Games Played-"+gamesPlayed+" --ExtraActivity-"+extraActivity+" --Other Remark-"+otherRemark;
		
		value = reason+" -- "+date2+" -- "+applicationDate+" -- "+issueDate+" -- "+tcNumber+" -- "+"issue"+" -- "+addmissionnumber+" -- "+
				perfom+" -- "+text+" -- "+schoolExam+" -- "+failedOrNot+" -- "+subjectStudied+" -- "+qualifiedPromotion+" -- "+monthOfFeePaid+" -- "+workingDays+" -- "+
				workingDayPresent+" -- "+feeConcession+" -- "+gamesPlayed+" -- "+extraActivity+" -- "+otherRemark+" -- "+applicationDate+" -- "+ncc+" -- "+transferCertificatedFrom+" -- "+qualifiedPromotionCheck; 

		
		String refNo = workLg.saveWorkLogMehod(language,"Update Tc Info","WEB",value,conn);
		return refNo;
	}
	

	String schid ;
	public TCIssueBean()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		tcList=(StudentInfo) ss.getAttribute("TCDetail");
		
		schid = (String) ss.getAttribute("schoolid");
		session=DBM.selectedSessionDetails(DBM.schoolId(),conn);
		String studentId=tcList.getAddNumber();
		String sectionID = tcList.getSectionid();
		SchoolInfoList ls = DBM.fullSchoolInfo(conn);
		perfom=tcList.getGrade();
		reason=tcList.getReason();
		date2=tcList.getDate();
		previousClass=tcList.getClassName();
		previousClassInWord(previousClass);
		rollNo = tcList.getRollNo();
		text=tcList.getOtherReason();
		if(reason.equalsIgnoreCase("others"))
		{
			
			showTextBox=true;
		}
		else
		{
			showTextBox=false;
			text=null;
		}
		
		ArrayList<String> list=new DataBaseMethodStudent().basicFieldsForStudentList();
		studentList= new DataBaseMethodStudent().studentDetail(studentId, "", "",QueryConstants.ADD_NUMBER,QueryConstants.STRUCK_OFF, null, null,"","","","",session,schid, list, conn);
		for(StudentInfo st:studentList)
		{
			classname1=st.getAdmitClassName();
		}
		showMainForm=true;

		if(ls.getBranch_id().equals("88"))
		{
			int number = objTc.finfMapleBearTcNumber(serialType,conn);
			tcNumber=String.valueOf(number)+"/"+session.substring(0,5)+session.substring(7, 9);;
			renTransferCertificateFrom = true;
		}
		else
		{
			serialType = objTc.checkTcNoType(ls.getSchid(),conn);
			int number=objTc.getseraialno(serialType,conn);
		      
			tcNumber=String.valueOf(number);
			renTransferCertificateFrom = false;
		}
	
		if(ls.getBranch_id().equals("99")||ls.getBranch_id().equals("101"))
		{
		  showBookNumber=true;
		}
		else
		{
			showBookNumber=false;
		}
		
		subjectList = DBM.allSubjectsClassWise(ls.getSchid(),tcList.getClassId(),session,conn);
		ArrayList<String> tempSubList = new ArrayList<>();
		for(String kk : subjectList)
		{
			tempSubList.add(DBM.toTitleCase(kk));
			
		}
		subjectList = new ArrayList<>();
		subjectList.addAll(tempSubList);
		
		getReasons();
		
		try {
            if(!ls.getBranch_id().equals("99")&&!ls.getBranch_id().equals("101")) {
            	 workingDays = DBM.totalMeetingOfStudent( conn, sectionID,schid,session);
                 workingDayPresent = DBM.totalMeetingPresentOfStudent( conn, sectionID, studentId,schid,session);
                 workingDayPresent = String.valueOf(Integer.valueOf(workingDays)-Integer.valueOf(workingDayPresent));
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
	
 
	
	
	public void promotedClassInWord(String promotedCls)
	{
		if(promotedCls.equalsIgnoreCase("I") || promotedCls.equalsIgnoreCase("1") || promotedCls.equalsIgnoreCase("1st"))
		{
			promotedClassWord="First";
		}
		else if(promotedCls.equalsIgnoreCase("II") || promotedCls.equalsIgnoreCase("2") || promotedCls.equalsIgnoreCase("2nd"))
		{
			promotedClassWord="Second";
		}
		else if(promotedCls.equalsIgnoreCase("III") || promotedCls.equalsIgnoreCase("3") || promotedCls.equalsIgnoreCase("3rd"))
		{
			promotedClassWord="Third";
		}
		else if(promotedCls.equalsIgnoreCase("IV") || promotedCls.equalsIgnoreCase("4") || promotedCls.equalsIgnoreCase("4th"))
		{
			promotedClassWord="Fourth";
		}
		else if(promotedCls.equalsIgnoreCase("V") || promotedCls.equalsIgnoreCase("5") || promotedCls.equalsIgnoreCase("5th"))
		{
			promotedClassWord="Fifth";
		}
		else if(promotedCls.equalsIgnoreCase("VI") || promotedCls.equalsIgnoreCase("6") || promotedCls.equalsIgnoreCase("6th"))
		{
			promotedClassWord="Sixth";
		}
		else if(promotedCls.equalsIgnoreCase("VII") || promotedCls.equalsIgnoreCase("7") || promotedCls.equalsIgnoreCase("7th"))
		{
			promotedClassWord="Seventh";
		}
		else if(promotedCls.equalsIgnoreCase("VIII") || promotedCls.equalsIgnoreCase("8") || promotedCls.equalsIgnoreCase("8th"))
		{
			promotedClassWord="Eighth";
		}
		else if(promotedCls.equalsIgnoreCase("IX") || promotedCls.equalsIgnoreCase("9") || promotedCls.equalsIgnoreCase("9th"))
		{
			promotedClassWord="Ninth";
		}
		else if(promotedCls.equalsIgnoreCase("X") || promotedCls.equalsIgnoreCase("10") || promotedCls.equalsIgnoreCase("10th"))
		{
			promotedClassWord="Tenth";
		}
		else if(promotedCls.equalsIgnoreCase("XI") || promotedCls.equalsIgnoreCase("11") || promotedCls.equalsIgnoreCase("11th"))
		{
			promotedClassWord="Eleventh";
		}
		else if(promotedCls.equalsIgnoreCase("XII") || promotedCls.equalsIgnoreCase("12") || promotedCls.equalsIgnoreCase("12th"))
		{
			promotedClassWord="Twelfth";
		}
		else
		{
			promotedClassWord=promotedCls;
		}

	}
	
	public void schoolExamWord(String schoolExam)
	{
		if(schoolExam.equalsIgnoreCase("I") || schoolExam.equalsIgnoreCase("1") || schoolExam.equalsIgnoreCase("1st"))
		{
			schoolExamWord="First";
		}
		else if(schoolExam.equalsIgnoreCase("II") || schoolExam.equalsIgnoreCase("2") || schoolExam.equalsIgnoreCase("2nd"))
		{
			schoolExamWord="Second";
		}
		else if(schoolExam.equalsIgnoreCase("III") || schoolExam.equalsIgnoreCase("3") || schoolExam.equalsIgnoreCase("3rd"))
		{
			schoolExamWord="Third";
		}
		else if(schoolExam.equalsIgnoreCase("IV") || schoolExam.equalsIgnoreCase("4") || schoolExam.equalsIgnoreCase("4th"))
		{
			schoolExamWord="Fourth";
		}
		else if(schoolExam.equalsIgnoreCase("V") || schoolExam.equalsIgnoreCase("5") || schoolExam.equalsIgnoreCase("5th"))
		{
			schoolExamWord="Fifth";
		}
		else if(schoolExam.equalsIgnoreCase("VI") || schoolExam.equalsIgnoreCase("6") || schoolExam.equalsIgnoreCase("6th"))
		{
			schoolExamWord="Sixth";
		}
		else if(schoolExam.equalsIgnoreCase("VII") || schoolExam.equalsIgnoreCase("7") || schoolExam.equalsIgnoreCase("7th"))
		{
			schoolExamWord="Seventh";
		}
		else if(schoolExam.equalsIgnoreCase("VIII") || schoolExam.equalsIgnoreCase("8") || schoolExam.equalsIgnoreCase("8th"))
		{
			schoolExamWord="Eighth";
		}
		else if(schoolExam.equalsIgnoreCase("IX") || schoolExam.equalsIgnoreCase("9") || schoolExam.equalsIgnoreCase("9th"))
		{
			schoolExamWord="Ninth";
		}
		else if(schoolExam.equalsIgnoreCase("X") || schoolExam.equalsIgnoreCase("10") || schoolExam.equalsIgnoreCase("10th"))
		{
			schoolExamWord="Tenth";
		}
		else if(schoolExam.equalsIgnoreCase("XI") || schoolExam.equalsIgnoreCase("11") || schoolExam.equalsIgnoreCase("11th"))
		{
			schoolExamWord="Eleventh";
		}
		else if(schoolExam.equalsIgnoreCase("XII") || schoolExam.equalsIgnoreCase("12") || schoolExam.equalsIgnoreCase("12th"))
		{
			schoolExamWord="Twelfth";
		}
		else
		{
			schoolExamWord=schoolExam;
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
	
	
	public  void exportClasWiseLessPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);
        SideMenuBean smb = new SideMenuBean();

		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();


		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();



		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30,30,536,780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba,fed);
		cb.lineTo(cba + 72f*7.45, fed);

		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font foRed = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		foRed.setColor(BaseColor.RED);
		//Header
		try {

			
			String src = ls.getDownloadpath()+ls.getTcHeader()+"?pfdrid_c=true";
			//String src = "https://tinyjpg.com/images/social/website.jpg";
			// // System.out.println(src+"sfdc");
			Image im = null;
			
			try {
				 im =Image.getInstance(src);			
			} catch (Exception e) {
	          e.printStackTrace();
			}
			try {
				im.setAlignment(Element.ALIGN_RIGHT);
				im.scaleAbsoluteHeight(100);
				im.scaleAbsoluteWidth(520);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Chunk c999 =null;
			try {
				c999 = new Chunk(im, 0, -84);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
            Paragraph p999 = new Paragraph();
            p999.add(c999);
            document.add(p999);

//			PdfPTable tabe;
//
//			tabe = new PdfPTable(new float[] {1});
//			PdfPCell cellll = new PdfPCell();
//			cellll.setImage(im);
//			cellll.setFixedHeight(100f);
//			cellll.setBorder(Rectangle.NO_BORDER);
//			tabe.addCell(cellll);
//			
//			tabe.setWidthPercentage(100);
//		
//			tabe.setHorizontalAlignment(Element.ALIGN_CENTER);
//			document.add(tabe);
			
        	Font fnt = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);
			Font ftt = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);
			

			Font font = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
			Chunk c8 = null;
			
				 c8 = new Chunk("\n\n\n\n\n\n                                                     \n",font );	
			
			
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] {1});
			PdfPCell cellTop = new PdfPCell(new Phrase("TRANSFER CERTIFICATE",foRed));
			cellTop.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cellTop);
			table.setWidthPercentage(40);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA,10, Font.BOLD);
			Chunk c40 = new Chunk("\n",fonthead );
			Paragraph p11 = new Paragraph();
			p11.add(c40);

			document.add(p11);  
			
			PdfPTable tableCe;
			tableCe = new PdfPTable(new float[] {1,1,1});
			tableCe.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			
			PdfPCell cell2 = new PdfPCell(new Phrase("             School Code : "+schNo,fnt));
			cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell2.setBorder(0);
			tableCe.addCell(cell2);
			
			PdfPCell cell4 = new PdfPCell(new Phrase("Book No. : 1/"+startYear,fnt));
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(0);
			tableCe.addCell(cell4);
			PdfPCell cell5 = new PdfPCell(new Phrase("Sr No. : "+tcNumber,fnt));
			cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell5.setBorder(0);
			tableCe.addCell(cell5);
			
			PdfPCell cell6 = new PdfPCell(new Phrase("Admission No. :"+srNo,fnt));
			cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell6.setBorder(0);
			tableCe.addCell(cell6);
			
		
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Affiliation No. : "+schoolAffilationNo,fnt));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(0);
			tableCe.addCell(cell1);
			
			SideMenuBean s = new SideMenuBean();
			
			PdfPCell cell3 = new PdfPCell(new Phrase(" School Type : "+s.getList().getSchType(),fnt));
			cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell3.setBorder(0);
			tableCe.addCell(cell3);
		
			
			tableCe.setWidthPercentage(100);
			tableCe.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(tableCe);
			
			Chunk c45 = new Chunk("\n",fonthead );
			Paragraph p15 = new Paragraph();
			p15.add(c45);

			document.add(p15); 

			


			PdfPTable table2;
		

			table2 = new PdfPTable(new float[] {1.7f,1});
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f,1.3f);


			//           for(int i=1;i<=48;i++) {
				//               if(i%2==0) {
			//                   //// // System.out.println(i);
			//               table2.getDefaultCell().setBorderWidthBottom(0.5f);
			//              table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			//           }
			//               else {
			//                  table2.getDefaultCell().setBorderWidthBottom(0.5f);
			//                  table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			//               }
			//           }

			Paragraph pa6 = new Paragraph();
			Chunk cp62 = new Chunk("1. Name of Pupil",fnt);
			pa6.add(cp62);
			
			table2.addCell(pa6);

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(fullName,ftt));
			//cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(0);
			table2.addCell(cee1);

			Paragraph pa7 = new Paragraph();
			Chunk cp72 = new Chunk(" Adhar Card No.",fnt);
			pa7.add(cp72);
			table2.addCell(pa7);
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase(stadhar,ftt));
			//cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(0);
			table2.addCell(cee5);
			
			Paragraph pa8 = new Paragraph();
			Chunk cp82 = new Chunk("2 Mother's Name",fnt);
			pa8.add(cp82);
			table2.addCell(pa8);
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase(motherName,ftt));
			//cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(0);
			table2.addCell(cee3);

			table2.addCell(pa7);
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(madhar,ftt));
			//cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(0);
			table2.addCell(cee7);

			
			Paragraph pa9 = new Paragraph();
			Chunk cp92 = new Chunk("3. Father/Guardian's Name",fnt);
			pa9.add(cp92);
			
			table2.addCell(pa9);
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(fatherName,ftt));
			//cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(0);
			table2.addCell(cee8);

			table2.addCell(pa7);
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(fadhar,ftt));
			//ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(0);
			table2.addCell(ceeh);

			
			Paragraph pa10 = new Paragraph();
			Chunk cp12 = new Chunk("4. Nationality",fnt);
			pa10.add(cp12);
			table2.addCell(pa10);
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(nationality,ftt));
			//cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(0);
			table2.addCell(cee9);

			Paragraph pa11 = new Paragraph();
			Chunk cp112 = new Chunk("5. Whether the candidate belongs to SC/ST/OBC Category",fnt);
			pa11.add(cp112);
			table2.addCell(pa11);
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(category,ftt));
			//cee9.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(0);
			table2.addCell(cee10);
			
			Paragraph pa12 = new Paragraph();
			Chunk cp122 = new Chunk("6. Date of Birth according to admission Register(in Figures)",fnt);
			pa12.add(cp122);
			table2.addCell(pa12);
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(dob1,ftt));
			//cee9.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(0);
			table2.addCell(cee11);

			Paragraph pa13 = new Paragraph();
			Chunk cp132 = new Chunk(" (in Words)",fnt);
			pa13.add(cp132);
			table2.addCell(pa13);
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(dobinword,ftt));
			//cee9.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(0);
			table2.addCell(cee12);
			
			Paragraph pa14 = new Paragraph();
			Chunk cp142 = new Chunk("7. Whether the student is failed ?",fnt);
			pa14.add(cp142);
			table2.addCell(pa14);
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(failedOrNot,ftt));
			//ce9.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(0);
			table2.addCell(cee13);
			
			Paragraph pa15 = new Paragraph();
			Chunk cp152 = new Chunk("8. Subject Offered",fnt);
			pa15.add(cp152);
			table2.addCell(pa15);
			PdfPCell cee14 = new PdfPCell();
			cee14.addElement(new Phrase(subjectStudied,ftt));
			//cee9.setBorder(2);
			cee14.setBorderWidthTop(0);
			cee14.setBorderWidthLeft(0);
			cee14.setBorderWidthRight(0);
			cee14.setBorderWidthBottom(0);
			table2.addCell(cee14);
			
			Paragraph pa16 = new Paragraph();
			Chunk cp162 = new Chunk("9. Class in which the pupil last studied(in figures)",fnt);
			pa16.add(cp162);
			table2.addCell(pa16);
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(previousClass,ftt));
			//cee9.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(0);
			table2.addCell(cee15);
			
			
			table2.addCell(pa13);
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(previosClassWord,ftt));
			//cee9.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(0);
			table2.addCell(cee16);
			
			Paragraph pa17 = new Paragraph();
			Chunk cp172 = new Chunk("10. School/Board Annual examination last taken with result",fnt);
			pa17.add(cp172);
			table2.addCell(pa17);
			PdfPCell cee17 = new PdfPCell();
			cee17.addElement(new Phrase(schoolExam,ftt));
			//cee9.setBorder(2);
			cee17.setBorderWidthTop(0);
			cee17.setBorderWidthLeft(0);
			cee17.setBorderWidthRight(0);
			cee17.setBorderWidthBottom(0);
			table2.addCell(cee17);
			
			Paragraph pa18 = new Paragraph();
			Chunk cp182 = new Chunk("11. Whether qualified for promotion to the higher class",fnt);
			pa18.add(cp182);
			table2.addCell(pa18);
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(qualifiedPromotionCheck,ftt));
			//cee9.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(0);
			table2.addCell(cee18);
			
			Paragraph pa19 = new Paragraph();
			Chunk cp192 = new Chunk("12. Whether the pupil has paid all dues to the School ?",fnt);
			pa19.add(cp192);
			table2.addCell(pa19);
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(monthOfFeePaid,ftt));
			//cee9.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(0);
			table2.addCell(cee19);
			
			Paragraph pa20 = new Paragraph();
			Chunk cp202 = new Chunk("13. Whether the pupil was in receipt of any fee concession, if so the nature of such concession ?",fnt);
			pa20.add(cp202);
			table2.addCell(pa20);
			PdfPCell cee20 = new PdfPCell();
			cee20.addElement(new Phrase(feeConcession,ftt));
			//cee9.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(0);
			table2.addCell(cee20);
			
			
			Paragraph pa21 = new Paragraph();
			Chunk cp212 = new Chunk("14. Whether the pupil is NCC Cadet/Boy Scout/ Girl Guide ( give details) ?",fnt);
			pa21.add(cp212);
			table2.addCell(pa21);
			PdfPCell cee21 = new PdfPCell();
			cee21.addElement(new Phrase(ncc,ftt));
			//cee9.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(0);
			table2.addCell(cee21);
			
			Paragraph pa22 = new Paragraph();
			Chunk cp222 = new Chunk("15. Date on which pupil name was struck off the rolls of the School",fnt);
			pa22.add(cp222);
			table2.addCell(pa22);
			PdfPCell cee22 = new PdfPCell();
			cee22.addElement(new Phrase(lastDateStr,ftt));
			//cee9.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(0);
			table2.addCell(cee22);
			
			Paragraph pa23 = new Paragraph();
			Chunk cp232 = new Chunk("16. No. of meetings up to date",fnt);
			pa23.add(cp232);
			table2.addCell(pa23);
			PdfPCell cee23 = new PdfPCell();
			cee22.addElement(new Phrase(workingDays,ftt));
			//cee9.setBorder(2);
			cee23.setBorderWidthTop(0);
			cee23.setBorderWidthLeft(0);
			cee23.setBorderWidthRight(0);
			cee23.setBorderWidthBottom(0);
			table2.addCell(cee23);
			
			Paragraph pa24 = new Paragraph();
			Chunk cp242 = new Chunk("17. No. of school days the pupil attended",fnt);
			pa23.add(cp242);
			table2.addCell(pa24);
			PdfPCell cee24 = new PdfPCell();
			cee22.addElement(new Phrase(workingDayPresent,ftt));
			//cee9.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(0);
			table2.addCell(cee24);

			Paragraph pa25 = new Paragraph();
			Chunk cp252 = new Chunk("18. General Conduct",fnt);
			pa25.add(cp252);
			table2.addCell(pa25);
			PdfPCell cee25 = new PdfPCell();
			cee22.addElement(new Phrase(perfom,ftt));
			//cee9.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(0);
			table2.addCell(cee25);
			
			Paragraph pa26 = new Paragraph();
			Chunk cp262 = new Chunk("19. Reason for leaving the school",fnt);
			pa26.add(cp262);
			table2.addCell(pa26);
			PdfPCell cee26 = new PdfPCell();
			cee22.addElement(new Phrase(reason,ftt));
			//cee9.setBorder(2);
			cee26.setBorderWidthTop(0);
			cee26.setBorderWidthLeft(0);
			cee26.setBorderWidthRight(0);
			cee26.setBorderWidthBottom(0);
			table2.addCell(cee26);
			
			Paragraph pa27 = new Paragraph();
			Chunk cp272 = new Chunk("20. Any other remarks",fnt);
			pa27.add(cp272);
			table2.addCell(pa27);
			PdfPCell cee27 = new PdfPCell();
			cee22.addElement(new Phrase(otherRemark,ftt));
			//cee9.setBorder(2);
			cee27.setBorderWidthTop(0);
			cee27.setBorderWidthLeft(0);
			cee27.setBorderWidthRight(0);
			cee27.setBorderWidthBottom(0);
			table2.addCell(cee27);
			
			Paragraph pa28 = new Paragraph();
			Chunk cp282 = new Chunk("21. Date of issue of certificate",fnt);
			pa28.add(cp282);
			table2.addCell(pa28);
			PdfPCell cee28 = new PdfPCell();
			cee22.addElement(new Phrase(issuDateStr,ftt));
			//cee9.setBorder(2);
			cee28.setBorderWidthTop(0);
			cee28.setBorderWidthLeft(0);
			cee28.setBorderWidthRight(0);
			cee28.setBorderWidthBottom(0);
			table2.addCell(cee28);
			
			
			table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

		

			Chunk c36 = new Chunk("\n Prepared By___________                              Checked By_________________						Signature of the Principal _________________ ",fo );
		
			
			Chunk c38 = new Chunk("\n Note: This T.C. must be invariably countersigned by the Manager- S.M.C., if is issued by the officiating / Incharge Principal.",fo );

			Paragraph p10 = new Paragraph();
			p10.add(c36);
			p10.add(c38);
			
			document.add(p10);

		}  catch (Exception e) {

			e.printStackTrace();
		}
		

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",fullName.replaceAll(" ", "_")+"_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name(fullName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();

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

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
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

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getSchNo() {
		return schNo;
	}

	public void setSchNo(String schNo) {
		this.schNo = schNo;
	}

	public String getPreviosClassWord() {
		return previosClassWord;
	}

	public void setPreviosClassWord(String previosClassWord) {
		this.previosClassWord = previosClassWord;
	}

	public String getNcc() {
		return ncc;
	}

	public void setNcc(String ncc) {
		this.ncc = ncc;
	}

	public boolean isShowmarksheet() {
		return showmarksheet;
	}

	public void setShowmarksheet(boolean showmarksheet) {
		this.showmarksheet = showmarksheet;
	}

	public boolean isShowBlmMarksheet() {
		return showBlmMarksheet;
	}

	public void setShowBlmMarksheet(boolean showBlmMarksheet) {
		this.showBlmMarksheet = showBlmMarksheet;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getStadhar() {
		return stadhar;
	}

	public void setStadhar(String stadhar) {
		this.stadhar = stadhar;
	}

	public String getFadhar() {
		return fadhar;
	}

	public void setFadhar(String fadhar) {
		this.fadhar = fadhar;
	}

	public String getMadhar() {
		return madhar;
	}

	public void setMadhar(String madhar) {
		this.madhar = madhar;
	}

	public boolean isSharewoodmarksheet() {
		return sharewoodmarksheet;
	}

	public void setSharewoodmarksheet(boolean sharewoodmarksheet) {
		this.sharewoodmarksheet = sharewoodmarksheet;
	}

	public boolean isNorthwoodTc() {
		return northwoodTc;
	}

	public void setNorthwoodTc(boolean northwoodTc) {
		this.northwoodTc = northwoodTc;
	}

	public boolean isShowMapleBearTc() {
		return showMapleBearTc;
	}

	public void setShowMapleBearTc(boolean showMapleBearTc) {
		this.showMapleBearTc = showMapleBearTc;
	}

	public String getTransferCertificatedFrom() {
		return transferCertificatedFrom;
	}

	public void setTransferCertificatedFrom(String transferCertificatedFrom) {
		this.transferCertificatedFrom = transferCertificatedFrom;
	}

	public boolean isRenTransferCertificateFrom() {
		return renTransferCertificateFrom;
	}

	public void setRenTransferCertificateFrom(boolean renTransferCertificateFrom) {
		this.renTransferCertificateFrom = renTransferCertificateFrom;
	}

	public String getQualifiedPromotionCheck() {
		return qualifiedPromotionCheck;
	}

	public void setQualifiedPromotionCheck(String qualifiedPromotionCheck) {
		this.qualifiedPromotionCheck = qualifiedPromotionCheck;
	}

	public String getPromotedClassWord() {
		return promotedClassWord;
	}

	public void setPromotedClassWord(String promotedClassWord) {
		this.promotedClassWord = promotedClassWord;
	}

	public String getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}

	public ArrayList<String> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<String> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSelectedSubjects() {
		return selectedSubjects;
	}

	public void setSelectedSubjects(ArrayList<String> selectedSubjects) {
		this.selectedSubjects = selectedSubjects;
	}

	public boolean isShowSSChildrenTc() {
		return showSSChildrenTc;
	}

	public void setShowSSChildrenTc(boolean showSSChildrenTc) {
		this.showSSChildrenTc = showSSChildrenTc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isShowCbseDraftTc() {
		return showCbseDraftTc;
	}

	public void setShowCbseDraftTc(boolean showCbseDraftTc) {
		this.showCbseDraftTc = showCbseDraftTc;
	}

	public String getProofDob() {
		return proofDob;
	}

	public void setProofDob(String proofDob) {
		this.proofDob = proofDob;
	}

	public String getApplicationDateString() {
		return applicationDateString;
	}

	public void setApplicationDateString(String applicationDateString) {
		this.applicationDateString = applicationDateString;
	}

	public String getSchoolAffilationNo() {
		return schoolAffilationNo;
	}

	public void setSchoolAffilationNo(String schoolAffilationNo) {
		this.schoolAffilationNo = schoolAffilationNo;
	}

	public String getSchoolCategory() {
		return schoolCategory;
	}

	public void setSchoolCategory(String schoolCategory) {
		this.schoolCategory = schoolCategory;
	}

	public boolean isCheckPromtionRender() {
		return checkPromtionRender;
	}

	public void setCheckPromtionRender(boolean checkPromtionRender) {
		this.checkPromtionRender = checkPromtionRender;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean isShowBookNumber() {
		return showBookNumber;
	}

	public void setShowBookNumber(boolean showBookNumber) {
		this.showBookNumber = showBookNumber;
	}

	public boolean isShowimmortal() {
		return showimmortal;
	}

	public void setShowimmortal(boolean showimmortal) {
		this.showimmortal = showimmortal;
	}

	public boolean isShowPNFTc() {
		return showPNFTc;
	}

	public void setShowPNFTc(boolean showPNFTc) {
		this.showPNFTc = showPNFTc;
	}

	public boolean isShowpdfbtn() {
		return showpdfbtn;
	}

	public void setShowpdfbtn(boolean showpdfbtn) {
		this.showpdfbtn = showpdfbtn;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isShowSSChildrenGirlTc() {
		return showSSChildrenGirlTc;
	}

	public void setShowSSChildrenGirlTc(boolean showSSChildrenGirlTc) {
		this.showSSChildrenGirlTc = showSSChildrenGirlTc;
	}

	public String getSchoolExamWord() {
		return schoolExamWord;
	}

	public void setSchoolExamWord(String schoolExamWord) {
		this.schoolExamWord = schoolExamWord;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public String getProofOfBirth() {
		return proofOfBirth;
	}

	public void setProofOfBirth(String proofOfBirth) {
		this.proofOfBirth = proofOfBirth;
	}

	public void setReasons(ArrayList<String> reasons) {
		this.reasons = reasons;
	}
	
}
