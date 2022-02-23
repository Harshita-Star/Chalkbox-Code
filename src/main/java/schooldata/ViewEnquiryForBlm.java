package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import exam_module.ExamInfo;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="viewEnquiryForBlm")
@ViewScoped
public class ViewEnquiryForBlm implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	boolean testStatus=false,examStatus=false,check,checkaccpect,disableSrNo,showCheque,disableAmount; //with getter - setter;
	Date dob,admissionDate,addmissionDate=new Date(),tcDate,chequeDate,testDate;
	double feeAmount,smsLimit,totalAmount;
	long mothMob;
	StudentInfo1 selectStudent;
	String strTestDate="",studentName,gender,address,mobno,email,fatherName,motherName,admissionNo,paymentMode="cash",remark,refNo,refMobNo,refName,bankName,submittedBankName,chequeNumber;
	String selecttype="pending",selectYear,typemessage,balMsg,clsName,cbNumber,uType,message,type,srnoType,srnoPrefix,srnoStart,searchType,aadhar,faadhar,maadhar,stuCateg;
	String userId,country,addmissionNumber,className1,selectedSection,routeName1="",routeFees="0",discountFee="0",totalFees="0",concession,houseName;
	ArrayList<SelectItem> sectionList,classList,routeList,connsessionList,houseCategorylist,yearlist=new ArrayList<>(),bankList,categoryList,stuCategList;
	ArrayList<String> documentsSubmitted=new ArrayList<>();
	ArrayList<FeeInfo> admFeeList = new ArrayList<>();
	ArrayList<StudentInfo1>list,selectedstudent,studentList = new ArrayList<>();
	ArrayList<ClassTest> classTestList;
	ArrayList<ExamInfo>examList;
	UploadedFile fatherImage,motherImage,g1Image,g2Image,studentImage;
	SchoolInfoList info=new SchoolInfoList();

	boolean checkPros=false,checkReg=false,checkAdm=false,checkSimple=false;

	ArrayList<SelectItem>sessionList=new ArrayList<>();
	String selectedSession;
	public ViewEnquiryForBlm()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession sss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message=(String) sss.getAttribute("registration");
		uType=(String) sss.getAttribute("type");
		userId=(String) sss.getAttribute("username");
		info = obj.fullSchoolInfo(conn);
		if(info.getBranch_id().equals("22"))
		{
			testDate = new Date();
		}
		
		int start=new DatabaseMethods1().schoolStartingSession(new DatabaseMethods1().schoolId(),conn);
		////// // System.out.println("start : "+start);
		sessionList=new ArrayList<>();

		Calendar now = Calendar.getInstance();
		int year1 = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		for(int i=start;i<=year1+1;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		if(month>=4)
		{
			selectedSession=String.valueOf(year1)+"-"+String.valueOf(year1+1);
		}
		else
		{
			selectedSession=String.valueOf(year1-1)+"-"+String.valueOf(year1);
		}

		
		
		/*if(info.getBranch_id().equalsIgnoreCase("54") || info.getBranch_id().equalsIgnoreCase("3"))
		{
			checkPros=true;
			checkReg=true;
			checkAdm=true;
		}
		else if(info.getBranch_id().equalsIgnoreCase("71"))
		{
			checkPros=true;
			checkReg=false;
			checkAdm=false;
		}
		else
		{
			checkPros=false;
			checkReg=true;
			checkAdm=false;
		}*/

		if(info.getEnqType().equalsIgnoreCase("both"))
		{
			checkPros=true;
			checkReg=true;
			checkAdm=true;
			checkSimple=false;
		}
		else if(info.getEnqType().equalsIgnoreCase("prospectus"))
		{
			checkPros=true;
			checkReg=false;
			checkAdm=false;
			checkSimple=false;
		}
		else if(info.getEnqType().equalsIgnoreCase("registration"))
		{
			checkPros=false;
			checkReg=true;
			checkAdm=false;
			checkSimple=false;
		}
		else if(info.getEnqType().equalsIgnoreCase("simple"))
		{
			checkPros=false;
			checkReg=false;
			checkAdm=false;
			checkSimple=true;
		}


		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		Date d=new  Date();
		int year=d.getYear()+1900;
		selectYear=String.valueOf(year);
		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
		for(int i=2015;i<=year;i++)
		{
			SelectItem ss=new SelectItem();
			ss.setLabel(String.valueOf(i));
			ss.setValue(String.valueOf(i));
			yearlist.add(ss);
		}
		check=false;

		classList = new DatabaseMethods1().allClass(conn);
		connsessionList=new DatabaseMethods1().allConnsessionType(conn);
		concession = (String) connsessionList.get(0).getValue();

		//houseCategorylist=new DatabaseMethods1().allHouseCategory(conn);
		categoryList=new DatabaseMethods1().allRefernceCategory(conn);
		stuCategList=new DatabaseMethods1().studentCategoryList(conn);
		stuCateg = (String) stuCategList.get(0).getValue();
		routeList = new DatabaseMethods1().allStops(conn);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	
	public void printEnquiry()
	{
		HttpSession cs=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        cs.setAttribute("enqSession", selectStudent.getSession());
        cs.setAttribute("enqAdmDate", selectStudent.getStringadmissionDate());
        cs.setAttribute("enqClass", selectStudent.getAdmissionclass());
        cs.setAttribute("enqStudent", selectStudent.getStudentName());
        cs.setAttribute("enqFather", selectStudent.getFatherName());
        cs.setAttribute("enqMother", selectStudent.getMotherName());
        cs.setAttribute("enqGender", selectStudent.getGender());
        cs.setAttribute("enqDob", selectStudent.getStringdob());
        cs.setAttribute("enqAddress", selectStudent.getAddress());
        cs.setAttribute("enqMob", selectStudent.getMobno());
        cs.setAttribute("enqMotMob", selectStudent.getMothmobno());
        cs.setAttribute("enqEmail", selectStudent.getEmail());
        cs.setAttribute("enqCategory", selectStudent.getCateg());
        cs.setAttribute("enqDescription", selectStudent.getRemark());
        
	    PrimeFaces.current().executeInitScript("window.open('printAdmissionEnquiry.xhtml')");
	}
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchEnquiryStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo1 info : studentList)
		{
			studentListt.add(info.getStudentName()+ " / "+info.getFatherName()+" / "+info.getMobno()+"-:"+info.getRefNo()+"-:"+info.getId());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchEnquiry()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="single";
		searchType="enq";
		
		int index=refName.lastIndexOf(":")+1;
		String id=refName.substring(index);
		
		if(refName.split("-:").length>=2)
		{
			refNo = refName.split("-:")[1];
		}
		else 
		{
			refNo = "";
		}
		
		list=obj.studentEnquiryByRefNo(id, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}
				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found By This Reference No. Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchAllEnquiry()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="all";
		searchType="enq";
		list=obj.studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}

				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchReg()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="all";
		searchType="reg";
		
		/*int index=refName.lastIndexOf(":")+1;
		String id=refName.substring(index);
		
		refNo = refName.split("-:")[1];*/
		
		list=obj.studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;
			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}

				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found By This Reference No. Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchAllReg()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="all";
		searchType="reg";
		list=obj.studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}

				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void searchAllPropectus()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		searchType="PROSPECTUS";
		list=obj.studentEnquiryByRefNoforblm(conn, searchType, selectedSession);
		totalAmount=0;
		if(list.size()>0)
		{
         check=true;
         
         totalAmount=0;
         for(StudentInfo1 ls:list)
         {
        	totalAmount +=ls.getAmount(); 
         }
         
         
         
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchAllRegistration()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		searchType="REGISTRATION";
		list=obj.studentEnquiryByRefNoforblm(conn, searchType, selectedSession);
		totalAmount=0;
		if(list.size()>0)
		{
         check=true;
         
         
         totalAmount=0;
         for(StudentInfo1 ls:list)
         {
        	totalAmount +=ls.getAmount(); 
         }
        
         
         
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void searchAllAdmission()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		searchType="ADMISSION";
		list=obj.studentEnquiryByRefNoforblm(conn, searchType, selectedSession);
		totalAmount=0;
		if(list.size()>0)
		{
         check=true;
         totalAmount=0;
         for(StudentInfo1 ls:list)
         {
        	totalAmount +=ls.getAmount(); 
         }
        
        }
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void searchAllDeny()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="all";
		searchType="deny";
		list=obj.studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}

				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchAllAdmit()
	{
		DatabaseMethods1 obj = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		type="all";
		searchType="admit";
		list=obj.studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}

			info = obj.fullSchoolInfo(conn);
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
				addmissionNumber = "";
			}
			else
			{
				disableSrNo = true;
				if(info.getBranch_id().equalsIgnoreCase("22"))
				{
					addmissionNumber="";
				}
				else
				{
					boolean check = obj.checkStudentsInSchool(info.getSchid(),conn);
					if(check==false)
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+obj.autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}

				//
			}
		}
		else
		{
			check=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Enquiry Found . Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(className1,conn);
		houseCategorylist=new ArrayList<>();
		houseCategorylist=new DatabaseMethods1().allHouseCategory(conn);
		selectedSection=(String) sectionList.get(0).getValue();
		admFeeList = new ArrayList<>();
		admFeeList = new DatabaseMethods1().obtainAdmissionFeeListBLM(new DatabaseMethods1().schoolId(), className1,selectStudent.getSession(),conn);
		feeAmount=0;
		paymentMode="cash";
		remark="";

		if(admFeeList.size()>0)
		{
			for(FeeInfo ss : admFeeList)
			{
				feeAmount = feeAmount + ss.getAmount();
			}
		}

		if(sectionList.size()>0)
		{
			int count = 0;
			for(SelectItem ss : sectionList)
			{
				count =Integer.parseInt(new DatabaseMethods1().allStudentcount(new DatabaseMethods1().schoolId(), "category", String.valueOf(ss.getValue()), selectStudent.getSession(), "all",conn));

				ss.setLabel(ss.getLabel()+" ("+count+" Students)");
			}
		}


		if(houseCategorylist.size()>0)
		{
			int count = 0;
			for(SelectItem ss : houseCategorylist)
			{
				count=Integer.parseInt(new DatabaseMethods1().allStudentcount(new DatabaseMethods1().schoolId(), "house", className1, new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(),conn), String.valueOf(ss.getValue()), conn));
				ss.setLabel(ss.getLabel()+" ("+count+" Students)");
			}
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void calculateAmount()
	{
		feeAmount=0;
		for(FeeInfo ss : admFeeList)
		{
			if(ss.getPayDiscount()>ss.getAmount())
			{
				ss.setPayDiscount(0);
			}
			feeAmount = feeAmount + (ss.getAmount()-ss.getPayDiscount());
		}
	}

	public void prospectusFee()
	{
		Connection conn=DataBaseConnection.javaConnection();
		double amt = new DatabaseMethods1().enqFeeAmountToPay("Prospectus Fee",selectStudent.getSession(),new DatabaseMethods1().schoolId() ,conn);
		feeAmount = amt;
		if(amt>0)
		{
			disableAmount=true;
		}
		else
		{
			disableAmount=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void registrationFee()
	{
		Connection conn=DataBaseConnection.javaConnection();
		double amt = new DatabaseMethods1().enqFeeAmountToPay("Registration Fee",selectStudent.getSession(),new DatabaseMethods1().schoolId() ,conn);
		feeAmount = amt;
		if(amt>0)
		{
			disableAmount=true;
		}
		else
		{
			disableAmount=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedstudent.size()>0)
		{
			for(StudentInfo1 ls:selectedstudent)
			{
				new DatabaseMethods1().messageurl1(ls.getMobno(), typemessage,"ENQ"+ls.getId(),conn,new DatabaseMethods1().schoolId(),"");
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message Sent!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please select student(s) to send message.","Please select student(s) to send message."));
		}


		typemessage="";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allStudent(selecttype,selectYear,conn);
		if(list.size()>0)
		{
			check=true;
			if(selecttype.equals("pending"))
			{
				checkaccpect=true;

			}
			else
			{
				checkaccpect=false;
			}
		}
		else
		{
			check=false;

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void details()
	{
		mothMob = Long.parseLong(selectStudent.getMothmobno().equals("") ? "0" : selectStudent.getMothmobno());
		if(selectStudent.getStuCateg() == null || selectStudent.getStuCateg().equals(""))
		{
			stuCateg = (String) stuCategList.get(0).getValue();
		}
		else
		{
			stuCateg = selectStudent.getStuCateg();
		}
	}
	
	public void editDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		info=DBM.fullSchoolInfo(conn);
		String admissionNo = "";

		String res = DBM.editStudentEnq(selectStudent.getAdmissionDate(), admissionNo, selectStudent.getStudentName(),
				selectStudent.getFatherName(), selectStudent.getMotherName(), selectStudent.getGender(),
				selectStudent.getDob(), selectStudent.getAddress(), selectStudent.getMobno(), selectStudent.getEmail(),
				selectStudent.getAdmissionclass(), userId, selectStudent.getRemark(), conn, selectStudent.getSession(),
				selectStudent.getCateg(), selectStudent.getId(),selectStudent.getMothmobno(),selectStudent.getStuCateg(),
				selectStudent.getLastSchool(),selectStudent.getReferredBy());

		if(res.equals("successful"))
		{
			String refNoNew2;
			try {
				refNoNew2=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Enquiry Details Editted Successfully!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please try again. Something went wrong"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void prospectusPayment()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int receiptNo=Integer.parseInt(new DatabaseMethods1().recepietNoForother(new DatabaseMethods1().schoolId(), conn))+1;
		int prosNo=Integer.parseInt(new DatabaseMethods1().prospectusNoForother(new DatabaseMethods1().schoolId(),"PROSPECTUS",conn))+1;

		String schid=new DatabaseMethods1().schoolId();
		if(schid.equals("251") && receiptNo==1)
		{
			receiptNo = 6443;
		}
		else if (schid.equals("252") && receiptNo==1)
		{
			receiptNo = 566;
		}

		if(schid.equals("251") && prosNo==1)
		{
			prosNo = 1870;
		}
		else if (schid.equals("252") && prosNo==1)
		{
			prosNo = 175;
		}

		int i=new DatabaseMethods1().insertenquiryFees(selectStudent.getSession(),selectStudent.getId(),"PROSPECTUS",feeAmount,receiptNo,paymentMode,remark,conn,String.valueOf(prosNo),bankName,submittedBankName,chequeNumber,chequeDate);
		if(i>=1)
			//if(true)
		{

			//strTestDate = new SimpleDateFormat("dd/MM/yyyy").format(testDate);

			//	new DatabaseMethods1().updateTestDateByEnqiry(selectStudent.getId(), strTestDate, conn);
			list=new DatabaseMethods1().studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Added Successfully"));


			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("enqPaymentType","PROSPECTUS");
			ss.setAttribute("enqReceiptNo",String.valueOf(receiptNo));
			ss.setAttribute("enqNo",selectStudent.getRefNo());
			ss.setAttribute("prosRegNo",String.valueOf(prosNo));
			ss.setAttribute("enqStudentName",selectStudent.getStudentName());
			ss.setAttribute("enqFatherName",selectStudent.getFatherName());
			ss.setAttribute("enqMobile",selectStudent.getMobno());
			ss.setAttribute("enqAddress",selectStudent.getAddress());
			ss.setAttribute("enqPaymentMode",paymentMode.toUpperCase());
			ss.setAttribute("testDate",strTestDate);
			ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(new Date()));
			ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
			ss.setAttribute("amount",feeAmount);
			ss.setAttribute("chqNo",chequeNumber);
			ss.setAttribute("chqDate",chequeDate);
			ss.setAttribute("bankName", bankName);
			String name="";
			try {
				name=new DatabaseMethods1().bankNameById(submittedBankName, conn);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			ss.setAttribute("subBankName",name);
			ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());
			feeAmount=0;
			paymentMode="cash";
			disableAmount=false;
			remark="";
			bankName=submittedBankName=chequeNumber="";chequeDate=null;
			PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void registrationPayment()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int receiptNo=Integer.parseInt(new DatabaseMethods1().recepietNoForother(new DatabaseMethods1().schoolId(),conn))+1;
		int regNo=Integer.parseInt(new DatabaseMethods1().prospectusNoForother(new DatabaseMethods1().schoolId(),"REGISTRATION",conn))+1;
		String schid=new DatabaseMethods1().schoolId();
		if(schid.equals("251") && receiptNo==1)
		{
			receiptNo = 6443;
		}
		else if (schid.equals("252") && receiptNo==1)
		{
			receiptNo = 566;
		}

		if(schid.equals("251") && regNo==1)
		{
			regNo = 1782;
		}
		else if (schid.equals("252") && regNo==1)
		{
			regNo = 174;
		}

		int i=new DatabaseMethods1().insertenquiryFees(selectStudent.getSession(),selectStudent.getId(),"REGISTRATION",feeAmount,receiptNo,paymentMode,remark,conn,String.valueOf(regNo),bankName,submittedBankName,chequeNumber,chequeDate);
		if(i>=1)
			//	if(true)
		{

			if(testDate==null)
			{
				strTestDate = "";
			}
			else
			{
				strTestDate = new SimpleDateFormat("dd/MM/yyyy").format(testDate);
			}

			if(info.getBranch_id().equals("22") || info.getBranch_id().equals("27"))
			{
				if(selectStudent.getMobno().length()==10
						&& !selectStudent.getMobno().equals("2222222222")
						&& !selectStudent.getMobno().equals("9999999999")
						&& !selectStudent.getMobno().equals("1111111111")
						&& !selectStudent.getMobno().equals("1234567890")
						&& !selectStudent.getMobno().equals("0123456789"))
				{
					String typemessage = "";
					if(strTestDate==null || strTestDate.equals(""))
					{
						typemessage="Dear Sir/Madam,\nRegistration of your ward "+selectStudent.getStudentName()+" is confirmed. Please fill admission form with all documents as soon as possible.\nRegards,\n"+info.getSmsSchoolName();
					}
					else
					{
						typemessage="Dear Sir/Madam,\nRegistration of your ward "+selectStudent.getStudentName()+" is confirmed. Make sure to join Screening test for admission to be held on "+strTestDate+" from 9 AM.\nRegards,\n"+info.getSmsSchoolName();
					}
					new DatabaseMethods1().messageurl1(selectStudent.getMobno(),typemessage,selectStudent.getStudentName(),conn,new DatabaseMethods1().schoolId(),"");
				}
			}

			new DatabaseMethods1().updateTestDateByEnqiry(selectStudent.getId(), strTestDate, conn);

			list=new DatabaseMethods1().studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Added Successfully"));


			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("enqPaymentType","REGISTRATION");
			ss.setAttribute("enqReceiptNo",String.valueOf(receiptNo));
			ss.setAttribute("enqNo",selectStudent.getRefNo());

			if(schid.equals("251"))
			{
				ss.setAttribute("prosRegNo","RGBLM"+String.valueOf(regNo));
			}
			else if (schid.equals("252"))
			{
				ss.setAttribute("prosRegNo","RGBBB"+String.valueOf(regNo));
			}
			else
			{
				ss.setAttribute("prosRegNo",String.valueOf(regNo));
			}


			ss.setAttribute("enqStudentName",selectStudent.getStudentName());
			ss.setAttribute("enqFatherName",selectStudent.getFatherName());
			ss.setAttribute("enqMobile",selectStudent.getMobno());
			ss.setAttribute("enqAddress",selectStudent.getAddress());
			ss.setAttribute("testDate",strTestDate);
			ss.setAttribute("enqPaymentMode",paymentMode.toUpperCase());
			ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(new Date()));
			ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
			ss.setAttribute("amount",feeAmount);
			ss.setAttribute("chqNo",chequeNumber);
			ss.setAttribute("chqDate",chequeDate);
			ss.setAttribute("bankName", bankName);
			String name="";
			try {
				name=new DatabaseMethods1().bankNameById(submittedBankName, conn);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			ss.setAttribute("subBankName",name);
			ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());

			feeAmount=0;
			paymentMode="cash";
			disableAmount=false;
			remark="";
			bankName=submittedBankName=chequeNumber="";chequeDate=null;
			PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printProspectusInv()
	{
		new DatabaseMethods1().schoolId();
		Connection conn = DataBaseConnection.javaConnection();
		StudentInfo1 info = new DatabaseMethods1().enquiryFeeDetailsByEnqId(selectStudent.getId(), "PROSPECTUS", conn);

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enqPaymentType","PROSPECTUS");
		ss.setAttribute("enqReceiptNo",String.valueOf(info.getReceiptNo()));
		ss.setAttribute("enqNo",selectStudent.getRefNo());
		ss.setAttribute("prosRegNo",info.getProsRegNo());
		ss.setAttribute("enqStudentName",selectStudent.getStudentName());
		ss.setAttribute("enqFatherName",selectStudent.getFatherName());
		ss.setAttribute("testDate",selectStudent.getStrTestDate());
		ss.setAttribute("enqMobile",selectStudent.getMobno());
		ss.setAttribute("enqAddress",selectStudent.getAddress());
		ss.setAttribute("enqPaymentMode",info.getPaymentMode().toUpperCase());
		ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(info.getFollowDate()));
		ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
		ss.setAttribute("amount",info.getAmount());
		ss.setAttribute("chqNo",info.getChqNo());
		ss.setAttribute("chqDate",info.getDate());
		ss.setAttribute("bankName", info.getBankName());
		ss.setAttribute("subBankName",info.getSubBankName());
		ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());

		PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printAdmInv()
	{
		new DatabaseMethods1().schoolId();
		Connection conn = DataBaseConnection.javaConnection();
		StudentInfo1 info = new DatabaseMethods1().enquiryFeeDetailsByEnqId(selectStudent.getId(), "ADMISSION", conn);

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enqPaymentType","ADMISSION");
		ss.setAttribute("enqReceiptNo",String.valueOf(info.getReceiptNo()));
		ss.setAttribute("enqNo",selectStudent.getRefNo());
		ss.setAttribute("prosRegNo",info.getProsRegNo());
		ss.setAttribute("enqStudentName",selectStudent.getStudentName());
		ss.setAttribute("enqFatherName",selectStudent.getFatherName());
		ss.setAttribute("enqMobile",selectStudent.getMobno());
		ss.setAttribute("enqAddress",selectStudent.getAddress());
		ss.setAttribute("enqPaymentMode",info.getPaymentMode().toUpperCase());
		ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(info.getFollowDate()));
		ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
		ss.setAttribute("amount",info.getAmount());
		ss.setAttribute("chqNo",info.getChqNo());
		ss.setAttribute("chqDate",info.getDate());
		ss.setAttribute("bankName", info.getBankName());
		ss.setAttribute("subBankName",info.getSubBankName());
		ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());
		ss.setAttribute("testDate",selectStudent.getStrTestDate());

		selectStudent.getAdmissionNo().split(":-:");

		admFeeList = new ArrayList<>();
		admFeeList = new DatabaseMethods1().obtainPaidAdmFeeListBLM(new DatabaseMethods1().schoolId(),selectStudent.getId(),conn);
		ss.setAttribute("feelist",admFeeList);
		PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printRegInv()
	{
		String schid=new DatabaseMethods1().schoolId();
		Connection conn = DataBaseConnection.javaConnection();
		StudentInfo1 info = new DatabaseMethods1().enquiryFeeDetailsByEnqId(selectStudent.getId(), "REGISTRATION", conn);

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("enqPaymentType","REGISTRATION");
		ss.setAttribute("enqReceiptNo",String.valueOf(info.getReceiptNo()));
		ss.setAttribute("enqNo",selectStudent.getRefNo());
		if(schid.equals("251"))
		{
			ss.setAttribute("prosRegNo","RGBLM"+info.getProsRegNo());
		}
		else if (schid.equals("252"))
		{
			ss.setAttribute("prosRegNo","RGBBB"+info.getProsRegNo());
		}
		else
		{
			ss.setAttribute("prosRegNo",info.getProsRegNo());
		}
		ss.setAttribute("enqStudentName",selectStudent.getStudentName());
		ss.setAttribute("enqFatherName",selectStudent.getFatherName());
		ss.setAttribute("enqMobile",selectStudent.getMobno());
		ss.setAttribute("enqAddress",selectStudent.getAddress());
		ss.setAttribute("enqPaymentMode",info.getPaymentMode().toUpperCase());
		ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(info.getFollowDate()));
		ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
		ss.setAttribute("amount",info.getAmount());
		ss.setAttribute("chqNo",info.getChqNo());
		ss.setAttribute("chqDate",info.getDate());
		ss.setAttribute("bankName", info.getBankName());
		ss.setAttribute("subBankName",info.getSubBankName());
		ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());
		ss.setAttribute("testDate",selectStudent.getStrTestDate());

		PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void acceptStudent() throws IOException
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();

		int agreement = DBM.checkAgreementFor(DBM.schoolId(), conn);
		int currentStrength =Integer.parseInt(DBM.allStudentcount(DBM.schoolId(),"","",selectStudent.getSession(),"", conn));

		if(agreement<500)
		{
			if(currentStrength>=(agreement+25))
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.","You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
			}
			else
			{
				addStudent();
			}
		}
		else
		{
			if(currentStrength>=(agreement+50))
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration.","You have crossed your agreement limit, Please contact Chalkbox Administrator for new registration."));
			}
			else
			{
				addStudent();
			}
		}

		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void houseSrno()
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();

		String startSession = selectStudent.getSession().split("-")[0];
		String ssn = StringUtils.right(startSession, 2);
		//String ssn = startSession.substring(startSession.length() - 2);
		boolean checkStu = DBM.checkStudentsInSchool(info.getSchid(),conn);
		if(checkStu==false)
		{
			if(info.getBranch_id().equalsIgnoreCase("22"))
			{
				String house = DBM.houseNameById(houseName, conn);
				String hsnm = StringUtils.left(house, 2);
				addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+info.getSrnoStart()+"/"+ssn;
			}
			else
			{
				//addmissionNumber = "";
			}

		}
		else
		{
			if(info.getBranch_id().equalsIgnoreCase("22"))
			{
				String house = DBM.houseNameById(houseName, conn);
				String hsnm = StringUtils.left(house, 2);
				//addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+""+"/"+ssn;
				//EG/AS/1234/18  --> prefix=EG/  , house=AS
				String prefix = info.getSrnoPrefix()+hsnm+"/";
				addmissionNumber = prefix+DBM.autoGeneratedSrNo(info.getSchid(),(prefix.length()+1),conn)+"/"+ssn;
			}
			else
			{
				//addmissionNumber = "";
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addStudent()
	{
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();

		SchoolInfoList info=DBM.fullSchoolInfo(conn);

		String studentstatus = "new";
		String startSession = selectStudent.getSession().split("-")[0];
		boolean check=true;
		if(studentstatus.equalsIgnoreCase("New"))
		{
			String[] sesion=selectStudent.getSession().split("-");
			Date startdate = null;
			Date endDate = null;
			try {
				if(info.getSchoolSession().equals("4-3"))
				{
					startdate=new  SimpleDateFormat("dd/MM/yyyy").parse("31/03/"+sesion[0]);
					endDate = new  SimpleDateFormat("dd/MM/yyyy").parse("01/04/"+sesion[1]);
				}
				else
				{
					startdate=new  SimpleDateFormat("dd/MM/yyyy").parse("30/04/"+sesion[0]);
					endDate = new  SimpleDateFormat("dd/MM/yyyy").parse("01/05/"+sesion[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(addmissionDate.after(startdate)&&addmissionDate.before(endDate))
			{
				check=true;
			}
			else
			{
				check=false;
			}
		}
		
		if(check==true)
		{
			srnoType = info.getSrnoType();

			if(srnoType.equalsIgnoreCase("auto"))
			{
				String ssn = StringUtils.right(startSession, 2);
				//String ssn = startSession.substring(startSession.length() - 2);
				boolean checkStu = new DatabaseMethods1().checkStudentsInSchool(info.getSchid(),conn);
				if(checkStu==false)
				{
					if(info.getBranch_id().equalsIgnoreCase("22"))
					{
						String house = DBM.houseNameById(houseName, conn);
						String hsnm = StringUtils.left(house, 2);
						addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+info.getSrnoStart()+"/"+ssn;
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+info.getSrnoStart();
					}

				}
				else
				{
					if(info.getBranch_id().equalsIgnoreCase("22"))
					{
						String house = DBM.houseNameById(houseName, conn);
						String hsnm = StringUtils.left(house, 2);
						//addmissionNumber = info.getSrnoPrefix()+hsnm+"/"+""+"/"+ssn;
						//EG/AS/1234/18  --> prefix=EG/  , house=AS
						String prefix = info.getSrnoPrefix()+hsnm+"/";
						addmissionNumber = prefix+new DatabaseMethods1().autoGeneratedSrNo(info.getSchid(),(prefix.length()+1),conn)+"/"+ssn;
					}
					else
					{
						addmissionNumber = info.getSrnoPrefix()+new DatabaseMethods1().autoGeneratedSrNo(info.getSchid(),(info.getSrnoPrefix().length()+1),conn);
					}
				}
			}
			
			int checker = DBM.checkingForDuplAdmNoAllowed(conn);
			int status = 0;
			if (checker == 1) {
				if (!addmissionNumber.trim().equalsIgnoreCase(""))
					status = DBM.duplicateStudentEntry(DBM.schoolId(), addmissionNumber, conn);
			}
			
			if (status == 1) {
				studentImage=null;
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Duplicate admission Number found,try a different one",
								"Duplicate admission Number found,try a different one"));
			}
			else
			{
				String conceesionCategory=DBM.concessionCategoryNameById(DBM.schoolId(),concession, conn);
				String concessionStatus = "";
				if(conceesionCategory.equalsIgnoreCase("General"))
				{
					concessionStatus="accepted";
				}
				else
				{
					if(info.getConcessionRequest().equals("no"))
					{
						concessionStatus="accepted";
					}
					else
					{
						concessionStatus="pending";
					}

				}

				int i = new DatabaseMethods1().studentRegistrationEnq(selectStudent.getSession()," ",addmissionDate, selectStudent.getStudentName(), selectStudent.getDob(),
						selectedSection, aadhar, Long.valueOf(selectStudent.getMobno()), selectStudent.getAddress(),
						selectStudent.getAddress(), selectStudent.getGender(), "Indian", "", stuCateg, "", "", 0, "", "", "",
						"India", selectStudent.getFatherName(), selectStudent.getMotherName(), "", faadhar, maadhar,
						mothMob, "", "", selectStudent.getLastSchool(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", routeName1, concession, "", houseName, "", "", "", "",
						documentsSubmitted, studentImage, fatherImage, motherImage, g1Image, g2Image, "", "", "", "",
						studentstatus, "", discountFee, "", "", conn, "", addmissionNumber,concessionStatus,"temp",className1);

				/*int i=	new DatabaseMethods1().studentRegistration(addmissionDate,selectStudent.getStudentName(),selectStudent.getDob(),className1,selectedSection,"",Long.valueOf(selectStudent.getMobno()),selectStudent.getAddress(),selectStudent.getAddress(),
						selectStudent.getGender(),"Indian","","","","",0,"","","","India",selectStudent.getFatherName(),selectStudent.getMotherName(),"","","",
						Long.valueOf(0),"","","","","","","","","",tcDate,"","","","","","","","","","","","","","","","","","","","","",routeName1,concession,"","",
						"","","","",documentsSubmitted,studentImage,fatherImage,motherImage,g1Image,g2Image,"","","",""
						,"","",studentstatus,"",discountFee,"","",conn,"",addmissionNumber);*/
				if(i>=1)
				{
					int maxnumber=i;
					cbNumber = String.valueOf(maxnumber);
					DBM.updateStudentId("CB"+String.valueOf(maxnumber),i,conn);
					if(routeName1.equals(""))
					{
						new DataBaseOnlineAdm().transportDataEntryAe(addmissionDate,"CB"+String.valueOf(maxnumber), routeName1, "No", className1,conn, selectStudent.getSession());
						//DBM.transportDataEntry(addmissionDate,"CB"+String.valueOf(maxnumber), routeName1, "No",conn);
					}
					else
					{
						new DataBaseOnlineAdm().transportDataEntryAe(addmissionDate,"CB"+String.valueOf(maxnumber), routeName1, "Yes", className1,conn, selectStudent.getSession());
						//DBM.transportDataEntry(addmissionDate,"CB"+String.valueOf(maxnumber), routeName1, "Yes",conn);
					}

					new DataBaseMeathodJson().addclassAttendanceINNew(selectedSection,new Date(),DBM.schoolId(),conn);
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Student Added Successfully","Student Added Successfully"));
					DBM.increaseStudentInAddSchool(DBM.schoolId(),conn);
					String className=DBM.classNameFromidSchid(DBM.schoolId(),className1,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),conn);
					classTestList=new DataBaseOnlineAdm().selectedClassTestListSession(selectStudent.getSession(),className1,selectedSection,conn);
					examList=new DataBaseOnlineAdm().selectedClassExamListSession(selectStudent.getSession(),selectedSection,conn);
					clsName = className;
					for(ClassTest ct:classTestList)
					{
						testStatus=new DataBaseOnlineAdm().checkClassTestPerformanceStatusSession(selectStudent.getSession(),ct.getId(),conn);
						if(testStatus==true)
						{
							new DataBaseOnlineAdm().entryOfNewStudentInClassTestPerformanceSession(DBM.schoolId(),selectStudent.getSession(),"CB"+String.valueOf(maxnumber),ct.getId(),conn);
						}
						else
						{

						}
					}
					for(ExamInfo ee:examList)
					{
						new DataBaseOnlineAdm().entryOfNewStudentInExamPerformanceSession(DBM.schoolId(),selectStudent.getSession(),"CB"+String.valueOf(maxnumber),ee.getClassid(),ee.getSubjectid(),ee.getSemesterid(),ee.getExamid(),ee.getExamType(),conn, ee.getMaxMark(), ee.getExamName());
					}

					String id=selectStudent.getId();
					new DatabaseMethods1().accpectStudentByEnqiry(id,"CB"+String.valueOf(maxnumber)+":-:"+addmissionNumber+":-:"+className1,conn);
					int receiptNo = 0;
					if(info.getBranch_id().equalsIgnoreCase("54"))
					{
						String schid=new DatabaseMethods1().schoolId();
						receiptNo=Integer.parseInt(new DatabaseMethods1().recepietNoForother(schid,conn))+1;
						
						if(schid.equals("251") && receiptNo==1)
						{
							receiptNo = 6443;
						}
						else if (schid.equals("252") && receiptNo==1)
						{
							receiptNo = 566;
						}

						new DatabaseMethods1().insertenquiryFees(selectStudent.getSession(),selectStudent.getId(),"ADMISSION",feeAmount,receiptNo,paymentMode,remark,conn,addmissionNumber,bankName,submittedBankName,chequeNumber,chequeDate);
						new DatabaseMethods1().insertAdmFeesSchid(schid,selectStudent.getId(),admFeeList,conn);
					}


					list=new DatabaseMethods1().studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
					//
					//			        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Added Successfully"));
					//

					HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

					if(info.getBranch_id().equalsIgnoreCase("54"))
					{
						ss.setAttribute("enqPaymentType","ADMISSION");
						ss.setAttribute("enqReceiptNo",String.valueOf(receiptNo));
						ss.setAttribute("enqNo",selectStudent.getRefNo());
						ss.setAttribute("prosRegNo",addmissionNumber);
						ss.setAttribute("enqStudentName",selectStudent.getStudentName());
						ss.setAttribute("enqFatherName",selectStudent.getFatherName());
						ss.setAttribute("enqMobile",selectStudent.getMobno());
						ss.setAttribute("enqAddress",selectStudent.getAddress());
						ss.setAttribute("enqPaymentMode",paymentMode.toUpperCase());
						ss.setAttribute("paymentDate",new SimpleDateFormat("dd MMM yyyy").format(new Date()));
						ss.setAttribute("enqClassName",selectStudent.getAdmissionclass());
						ss.setAttribute("amount",feeAmount);
						ss.setAttribute("chqNo",chequeNumber);
						ss.setAttribute("chqDate",chequeDate);
						ss.setAttribute("bankName", bankName);
						ss.setAttribute("testDate",selectStudent.getStrTestDate());
						String name="";
						try {
							name=new DatabaseMethods1().bankNameById(submittedBankName, conn);
						} catch (SQLException e) {
							
							e.printStackTrace();
						}
						ss.setAttribute("subBankName",name);
						ss.setAttribute("schoolid", new DatabaseMethods1().schoolId());
						for(FeeInfo ff : admFeeList)
						{
							ff.setAmount(ff.getAmount()-ff.getPayDiscount());
						}

						ss.setAttribute("feelist",admFeeList);

						PrimeFaces.current().executeInitScript("window.open('printProsRegReceipt.xhtml')");
					}
					else
					{
						ss.setAttribute("addNumber", "CB"+String.valueOf(maxnumber));
						PrimeFaces.current().executeInitScript("window.open('printNewRegistrationInfo.xhtml')");
						if(info.getBranch_id().equalsIgnoreCase("22") || info.getBranch_id().equalsIgnoreCase("27"))
						{
							PrimeFaces.current().executeInitScript("window.open('printWelcomeLetter.xhtml')");
						}
					}
					
					
					
					String schid = DBM.schoolId();
					 // System.out.println(schid);
					if (schid.equals("216") || schid.equals("302") || schid.equals("221")) {
						 // System.out.println("in");
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
						String sectName = DBM.sectionNameByIdSchid(schid, selectedSection, conn);
						String clsName = DBM.classNameFromidSchid(schid, className1, selectStudent.getSession(), conn);
						String notificationText = "Dear Sir/ Madam,\n" + 
								"New admission is confirmed in class "+clsName+" section "+sectName+". Details are as follows: -\n" + 
								"Name: - "+selectStudent.getStudentName()+"\n" + 
								"Father’s Name: - "+selectStudent.getFatherName()+"\n" + 
								"Father’s Mobile Number: - "+selectStudent.getMobno()+"\n" + 
								"Mother’s Name: - "+selectStudent.getMotherName()+"\n" + 
								"Mother’s Mobile Number: - "+mothMob+"\n" + 
								"Date of Admission: - "+sdf.format(addmissionDate)+"\n" + 
								"Date of Birth: - "+sdf.format(selectStudent.getDob())+"\n" + 
								"Address: - "+selectStudent.getAddress()+"\n" + 
								"\n" + 
								"Kindly co- ordinate with the parents and enroll name in school records.\n" + 
								"Regards\n" + 
								"\n" + 
								info.getSmsSchoolName();

						new RegistrationBean1().sendNotification(notificationText, schid, className1, selectedSection, selectStudent.getSession());
					}

					stuCateg = (String) stuCategList.get(0).getValue();
					concession = (String) connsessionList.get(0).getValue();
					addmissionNumber=className1=selectedSection=aadhar=houseName=remark="";
					addmissionDate=new Date();
					houseCategorylist=new ArrayList<>();
					feeAmount=0;
					paymentMode="cash";
					remark="";
					bankName=submittedBankName=chequeNumber="";chequeDate=null;


					//	HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					ss.setAttribute("addNo", "CB"+String.valueOf(maxnumber));
					ss.setAttribute("name", selectStudent.getStudentName());
					ss.setAttribute("selectedClass",selectedSection);

					if(message.equals("true"))
					{
						String typeMessage="";

						double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
						if(balance >0 && balance <= smsLimit)
						{
							balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
									+ "We suggest you to top-up your account today to ensure uninterrupted activity";
							if (type.equalsIgnoreCase("admin"))
							{
								PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
								PrimeFaces.current().ajax().update("MsgLimitForm");
							}
							else
							{
								sendMsg();
							}
						}
						else if(balance <= 0)
						{
							balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
							if (type.equalsIgnoreCase("admin"))
							{
								PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
								PrimeFaces.current().ajax().update("MsgOverForm");
							}
							else
							{
								balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

								PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
								PrimeFaces.current().ajax().update("MsgOtherForm");
							}
						}
						else
						{
							if(selectStudent.getMobno().length()==10
									&& !selectStudent.getMobno().equals("2222222222")
									&& !selectStudent.getMobno().equals("9999999999")
									&& !selectStudent.getMobno().equals("1111111111")
									&& !selectStudent.getMobno().equals("1234567890")
									&& !selectStudent.getMobno().equals("0123456789"))
							{

								if(info.getSchoolAppName().equalsIgnoreCase("N/A"))
								{
									typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "+selectStudent.getStudentName()+" in class "+className+". Now you can access your ward's information on your mobile."+"\n"+"Regards\n"+info.getSchoolName();

								}
								else
								{
									typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "+selectStudent.getStudentName()+" in class "+className+". Now you can access your ward's information on your mobile. Please search "+info.getSchoolAppName()+" on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"+"\n"+"Regards\n"+info.getSchoolName();

								}

								DBM.messageurl1(selectStudent.getMobno(), typeMessage,"CB"+String.valueOf(maxnumber),conn,DBM.schoolId(),"");
							}
						}


					}

					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					//FacesContext.getCurrentInstance().getExternalContext().redirect("viewEnquiryForBlm.xhtml");
				}
			}
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"If Student New Please Date Must Be In This Session","If Student New Please Date Must Be In This Session "));
		}

		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(message.equals("true"))
		{
			String typeMessage="";
			if(selectStudent.getMobno().length()==10
					&& !selectStudent.getMobno().equals("2222222222")
					&& !selectStudent.getMobno().equals("9999999999")
					&& !selectStudent.getMobno().equals("1111111111")
					&& !selectStudent.getMobno().equals("1234567890")
					&& !selectStudent.getMobno().equals("0123456789"))
			{
				if(info.getSchoolAppName().equalsIgnoreCase("N/A"))
				{
					typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "+selectStudent.getStudentName()+" in class "+clsName+". Now you can access your ward's information on your mobile."+"\n"+"Regards\n"+info.getSchoolName();

				}
				else
				{
					typeMessage="Dear Parent,"+"\n"+"Thank You for admission of your ward "+selectStudent.getStudentName()+" in class "+clsName+". Now you can access your ward's information on your mobile. Please search "+info.getSchoolAppName()+" on Google Playstore or Apple store. Enter your registered mobile no. and get OTP verified instantly. We welcome you to be a part of Digital India !"+"\n"+"Regards\n"+info.getSchoolName();

				}
				new DatabaseMethods1().messageurl1(selectStudent.getMobno(), typeMessage,"CB"+cbNumber,conn,new DatabaseMethods1().schoolId(),"");
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deniedStudent() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		String id=selectStudent.getId();
		int i=new DatabaseMethods1().deniedStudentByEnqiry(id,conn);
		if(i>0)
		{
			String refNoNew;
			try {
				refNoNew=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			list=new DatabaseMethods1().studentEnquiryByRefNo(refNo, conn, type,searchType,selectedSession);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Enquiry Denied successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some Error occur Please try Again"));
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
		
		value = "Id - "+selectStudent.getId();
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Deny Enquiry","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn,String admissionNo)
	{
	    String value = "";
		String language= "";
		
		Timestamp db = new Timestamp(selectStudent.getAdmissionDate().getTime());
		
		Timestamp dt = new Timestamp(selectStudent.getDob().getTime());
		
		language = selectStudent.getStudentName()+" -- "+selectStudent.getFatherName()+" -- "+selectStudent.getMotherName()
		            +" -- "+selectStudent.getGender()+" -- "+dt+" -- "+selectStudent.getAddress()+" -- "+selectStudent.getMobno()+" -- "+
				    selectStudent.getMothmobno()+" -- "+selectStudent.getEmail()+" -- "+selectStudent.getCateg()+" -- "+selectStudent.getRemark();
		
				value =  db+" -- "+admissionNo+" -- "+selectStudent.getStudentName()+" -- "+
				selectStudent.getFatherName()+" -- "+selectStudent.getMotherName()+" -- "+selectStudent.getGender()+" -- "+
				dt+" -- "+selectStudent.getAddress()+" -- "+selectStudent.getMobno()+" -- "+selectStudent.getEmail()+" -- "+
				selectStudent.getAdmissionclass()+" -- "+userId+" -- "+selectStudent.getRemark()+" -- "+selectStudent.getSession()+" -- "+
				selectStudent.getCateg()+" -- "+selectStudent.getId()+" -- "+selectStudent.getMothmobno();
	
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Enquiry","WEB",value,conn);
		return refNo;
	}


	public void paymentModeListener()
	{
		if (paymentMode.equals("cheque") || paymentMode.equals("neft"))
		{
			showCheque = true;
		}
		else
		{
			showCheque = false;
		}
		//PrimeFaces.current().executeInitScript("PF('prospectusfeeDialog').show()");
	}


	/*public String back()
	{
	return "dashboard.xhtml";
	}*/

	public ArrayList<StudentInfo1> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo1> list) {
		this.list = list;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public StudentInfo1 getSelectStudent() {
		return selectStudent;
	}
	public void setSelectStudent(StudentInfo1 selectStudent) {
		this.selectStudent = selectStudent;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddmissionNumber() {
		return addmissionNumber;
	}

	public void setAddmissionNumber(String addmissionNumber) {
		this.addmissionNumber = addmissionNumber;
	}

	public String getClassName1() {
		return className1;
	}

	public void setClassName1(String className1) {
		this.className1 = className1;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getRouteName1() {
		return routeName1;
	}

	public void setRouteName1(String routeName1) {
		this.routeName1 = routeName1;
	}

	public String getRouteFees() {
		return routeFees;
	}

	public void setRouteFees(String routeFees) {
		this.routeFees = routeFees;
	}

	public String getDiscountFee() {
		return discountFee;
	}

	public void setDiscountFee(String discountFee) {
		this.discountFee = discountFee;
	}

	public String getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(String totalFees) {
		this.totalFees = totalFees;
	}

	public Date getAddmissionDate() {
		return addmissionDate;
	}

	public void setAddmissionDate(Date addmissionDate) {
		this.addmissionDate = addmissionDate;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public ArrayList<StudentInfo1> getSelectedstudent() {
		return selectedstudent;
	}

	public void setSelectedstudent(ArrayList<StudentInfo1> selectedstudent) {
		this.selectedstudent = selectedstudent;
	}
	public String getSelectYear() {
		return selectYear;
	}

	public void setSelectYear(String selectYear) {
		this.selectYear = selectYear;
	}

	public ArrayList<SelectItem> getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList<SelectItem> yearlist) {
		this.yearlist = yearlist;
	}
	public String getTypemessage() {
		return typemessage;
	}

	public void setTypemessage(String typemessage) {
		this.typemessage = typemessage;
	}

	public String getSelecttype() {
		return selecttype;
	}

	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}

	public Boolean getCheckaccpect() {
		return checkaccpect;
	}

	public void setCheckaccpect(Boolean checkaccpect) {
		this.checkaccpect = checkaccpect;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(double feeAmount) {
		this.feeAmount = feeAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getSrnoType() {
		return srnoType;
	}

	public void setSrnoType(String srnoType) {
		this.srnoType = srnoType;
	}

	public String getSrnoPrefix() {
		return srnoPrefix;
	}

	public void setSrnoPrefix(String srnoPrefix) {
		this.srnoPrefix = srnoPrefix;
	}

	public String getSrnoStart() {
		return srnoStart;
	}

	public void setSrnoStart(String srnoStart) {
		this.srnoStart = srnoStart;
	}

	public boolean isDisableSrNo() {
		return disableSrNo;
	}

	public void setDisableSrNo(boolean disableSrNo) {
		this.disableSrNo = disableSrNo;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public ArrayList<FeeInfo> getAdmFeeList() {
		return admFeeList;
	}

	public void setAdmFeeList(ArrayList<FeeInfo> admFeeList) {
		this.admFeeList = admFeeList;
	}

	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSubmittedBankName() {
		return submittedBankName;
	}

	public void setSubmittedBankName(String submittedBankName) {
		this.submittedBankName = submittedBankName;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}

	public boolean isShowCheque() {
		return showCheque;
	}

	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}

	public boolean isDisableAmount() {
		return disableAmount;
	}

	public void setDisableAmount(boolean disableAmount) {
		this.disableAmount = disableAmount;
	}

	public boolean isCheckPros() {
		return checkPros;
	}

	public void setCheckPros(boolean checkPros) {
		this.checkPros = checkPros;
	}

	public boolean isCheckReg() {
		return checkReg;
	}

	public void setCheckReg(boolean checkReg) {
		this.checkReg = checkReg;
	}

	public boolean isCheckAdm() {
		return checkAdm;
	}

	public void setCheckAdm(boolean checkAdm) {
		this.checkAdm = checkAdm;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public String getStrTestDate() {
		return strTestDate;
	}

	public void setStrTestDate(String strTestDate) {
		this.strTestDate = strTestDate;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getConcession() {
		return concession;
	}

	public void setConcession(String concession) {
		this.concession = concession;
	}

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}

	public ArrayList<SelectItem> getConnsessionList() {
		return connsessionList;
	}

	public void setConnsessionList(ArrayList<SelectItem> connsessionList) {
		this.connsessionList = connsessionList;
	}

	public ArrayList<SelectItem> getHouseCategorylist() {
		return houseCategorylist;
	}

	public void setHouseCategorylist(ArrayList<SelectItem> houseCategorylist) {
		this.houseCategorylist = houseCategorylist;
	}

	public boolean isCheckSimple() {
		return checkSimple;
	}

	public void setCheckSimple(boolean checkSimple) {
		this.checkSimple = checkSimple;
	}

	public String getStuCateg() {
		return stuCateg;
	}

	public void setStuCateg(String stuCateg) {
		this.stuCateg = stuCateg;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList<SelectItem> getStuCategList() {
		return stuCategList;
	}

	public void setStuCategList(ArrayList<SelectItem> stuCategList) {
		this.stuCategList = stuCategList;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getFaadhar() {
		return faadhar;
	}

	public void setFaadhar(String faadhar) {
		this.faadhar = faadhar;
	}

	public String getMaadhar() {
		return maadhar;
	}

	public void setMaadhar(String maadhar) {
		this.maadhar = maadhar;
	}

	public long getMothMob() {
		return mothMob;
	}

	public void setMothMob(long mothMob) {
		this.mothMob = mothMob;
	}

	public String getRefMobNo() {
		return refMobNo;
	}

	public void setRefMobNo(String refMobNo) {
		this.refMobNo = refMobNo;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public String getSelectedSession() {
		return selectedSession;
	}

	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}



}
