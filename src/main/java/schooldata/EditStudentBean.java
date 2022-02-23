package schooldata;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="editStudent")
@ViewScoped
public class EditStudentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date lastUpdatingDate,dob,startingDate;
	StudentInfo studentList;
	ArrayList<SelectItem> classList,sectionList,categoryList,routeList,religionList,handicaplist;
	transient
	UploadedFile studentImage;
	String admRemark,disability,handicap,category,fathersOccupation,residencePhone,selectedSection,tempSection,previousImage,nationality,fname,lname,fathersName
	,motherName,className,sectionName,transportRoute,gender,religion,currentAddress,permanentAddress,country,addNumber,sendMessageMobileNo;
	int pincode,id;
	long fathersPhone,mothersPhone;
	String srno,regNo_IX,regNo_XI,ledgNo,admnFileNo,schid,doctype;
	String bpl="No",bplCardNo,concession,caste,singleChild,bloodGroup,aadharNo,SingleParent,livingWith,fatherEmail,motherEmail,preFatherImg,preMotherImg,preG1Img,preG2Img;
	transient
	UploadedFile fatherImage,motherImage,g1Image,g2Image;
	String rollno;
	ArrayList<StudentInfo> completeList;
	String conceesionCategory,concessionStatus,oldconcessionCtegory,oldconcessionStatus,oldroutename;
	boolean disableSrNo; //with getter-setter
	boolean showBpl,showHandicap,showDis;
	Date addmissionDate=new Date();
	String fatherAadharNo,boardName,hostal,houseName;
	String message="",studentPhone="0";
	ArrayList<SelectItem>houseCategorylist,doctypelist,connsessionList;
	String fname1,lname1,relation1,occupation1,phone1,address1,fname2,lname2,relation2,occupation2,phone2,address2,lastSchoolName,passedClass,medium,sname1,sname2,sclassid1,sclassid2,sClassName1,sClassName2;
	ArrayList<String> documentsSubmitted;
	boolean completed,showInstitute=false;
	String pResult,p_percent,pReason,height,weight,eyes,fatherQualification,motherQualification,motherOccupation,fatherDesignation;
	String motherDesignation,fatherOrganization,motherOrganization,fatherOfficeAdd,motherOfficeAdd,fatherIncome,motherIncome,FatherAadhaar,motherAadhaar,fatherSchoolEmp,motherSchoolEmp;
	Date tcDate;
	String motherofficecontactno,fatherofficecontcatno,admitClassVal,minority="No";
	String routeName1="";
    String  fahterAnnualIncome,motherAnnualIncome;
	String discountFee="0";
	String TotalFess="0";
	String routeFees="0",studentStatus;
	DatabaseMethods1 DBM=new DatabaseMethods1();
	SelectItem selectedDoc = new SelectItem();
	StudentInfo selectedSibling = new StudentInfo();
	String tenRoll,tenYearOfPass,tenBoard;
	boolean showminority = false;
	String village = "";

	public EditStudentBean() {
		Connection conn=DataBaseConnection.javaConnection();
		
		routeList = DBM.allStops(conn);
		connsessionList = DBM.allConnsessionType(conn);
		schid=DBM.schoolId();
		String ctype = DBM.checkClientType(conn);
		if(ctype.equalsIgnoreCase("institute"))
		{
			showInstitute=true;
		}
		else
		{
			showInstitute=false;
		}
		
		
		if(schid.equals("313") || schid.equals("315")) {
			showminority = true;
		}
		
		
		EditStudentDetailsBeann();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public void checkRoute()
	{
		Connection conn = DataBaseConnection.javaConnection();
		routeFees=new DatabaseMethods1().routeStopListAllAmount(new DatabaseMethods1().schoolId(), routeName1,conn);

		TotalFess=routeFees;
		discountFee="0";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeDoc()
	{
		Connection con = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		int checkDocPresentInStudentTable = DBM.checkDocPresentInStudentTableMethod(selectedDoc,con);
		int checkDocPresentInRegistrtion = DBM.checkDocPresentInRegistrtionMehod(selectedDoc,con);
		if(checkDocPresentInStudentTable==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Can't Remove, Document Already Present for Student"));
		}
		else if(checkDocPresentInRegistrtion==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Can't Remove, Document Already Present for Student In Registation"));
		} 
		else
		{
			
			int p = DBM.removeDocumentFormTable(selectedDoc,con);
			
			if(p>=1)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(con);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Document Removed Successfully"));

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured"));

			}
	
			doctypelist=DBM.allDocType(con);
		}
		
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "Selected Doc-"+selectedDoc.getValue().toString();
		String language= "";
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Document in edit Student","WEB",value,conn);
		return refNo;
	}
	
	
	public void checkfees()
	{
		if(Integer.valueOf(routeFees)>=Integer.valueOf(discountFee))
		{
			TotalFess=String.valueOf(Integer.valueOf(routeFees)-Integer.valueOf(discountFee));

		}
		else
		{
			TotalFess=routeFees;
			discountFee="0";
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Discount Fee Must Be LessThan Or Equal Of Route Fees","Discount Fee Must Be LessThan Or Equal Of Route Fees"));

		}

	}
	
	
	public void addDocType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		int j=obj.checkDoctype(doctype,conn);
		if(j>0)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"duplicate Document  Found ","duplicate Document  Found"));

		}
		else
		{
			int i=obj.addDoctype(doctype,conn);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Document Added","Document Added"));
				doctypelist=obj.allDocType(conn);

				doctype="";
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Some Error Occur Please try Again","Some Error Occur Please try Again"));

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = doctype;
		String language= doctype;
		
		
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Document in Edit Registration","WEB",value,conn);
		return refNo;
	}


	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(className,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showBPLNo()
	{
		if(bpl.equals("Yes"))
			showBpl=true;
		else
			showBpl=false;
	}
	public void showDisability()
	{
		if(disability.equals("Yes"))
			showDis=true;
		else
			showDis=false;
	}


	String currstudeimage,curfatherimage,curmotherimage,curg1image,curg2image;


	public void EditStudentDetailsBeann()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(!FacesContext.getCurrentInstance().isPostback())
		{

			
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

			studentList=(StudentInfo) ss.getAttribute("selectedStudent");
			categoryList=DBM.studentCategoryList(conn);
			classList=DBM.allClass(conn);
			houseCategorylist=DBM.allHouseCategory(conn);
			doctypelist=DBM.allDocType(conn);
			routeList=DBM.allStops(conn);
			religionList=DBM.allReligionList(conn);
			handicaplist=DBM.studentHandicapList(conn);
			previousImage=DBM.studentImage(DBM.schoolId(),"imageOnly",studentList.getAddNumber(),conn);
//			preFatherImg=DBM.fatherImage(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
//			preMotherImg=DBM.motherImage(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
			preFatherImg = studentList.getFatherImage();
			preMotherImg = studentList.getMotherImage();
			preG1Img=DBM.gr1Image(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
			preG2Img=DBM.gr2Image(DBM.schoolId(),studentList.getAddNumber(),"student",conn);

			completeList=DBM.siblingListOfStudent(studentList.getAddNumber(),conn);
			
			try {
				
				for(StudentInfo cvb : completeList)
				{
					String[] spliter =  cvb.getFname().split("-");
					String id = spliter[1];
					
					String classandSection = DBM.studentClassSectionNameById(DBM.schoolId(), id, conn);
					String[] split2 = classandSection.split(" - ");
					cvb.setClassName(split2[0]);
					cvb.setSectionName(split2[1]);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			for(int i=completeList.size()+1;i<=5;i++)
			{
				StudentInfo info=new StudentInfo();
				info.setSno(i);
				info.setAddNumber("");
				completeList.add(info);
			}


			String savePath;
			SchoolInfoList ls=DBM.fullSchoolInfo(conn);
			if(ls.getProjecttype().equals("online"))
			{
				String folderName=ls.getDownloadpath();
				savePath=folderName;
			}
			else
			{

				ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
				String realPath = ctx.getRealPath("/");
				savePath = realPath;
			}

			if(ls.getSrnoType().equalsIgnoreCase("manual"))
			{
				disableSrNo = false;
			}
			else
			{
				disableSrNo = true;
			}
			currstudeimage=savePath+previousImage;
			curfatherimage=savePath+preFatherImg;
			curmotherimage=savePath+preMotherImg;
			curg1image=savePath+preG1Img;
			curg2image=savePath+preG2Img;
			routeName1=studentList.getRouteStop();
			if(routeName1==null||routeName1.equals(""))
			{
				//routeName1
				routeFees="0";
				discountFee="0";
				TotalFess="0";
			}
			else
			{
				routeFees=DBM.routeStopListAllAmount(DBM.schoolId(),routeName1,conn);
				discountFee=studentList.getDiscountfee();
				TotalFess=String.valueOf(Integer.valueOf(routeFees)-Integer.valueOf(discountFee));

			}
			oldroutename=routeName1;
			id=studentList.getId();
			addNumber=studentList.getAddNumber();
			srno=studentList.getSrNo();
			fname=studentList.getFname();
			addmissionDate=studentList.getStartingDate();
			fathersName=studentList.getFathersName();
			motherName=studentList.getMotherName();
			className=studentList.getClassId();
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
			studentPhone=studentList.getStudentPhone();
			category=DBM.studentCategoryIdByName(studentList.getCategory(),conn);
			residencePhone=studentList.getResidenceMobile();
			fathersOccupation=studentList.getFathersOccupation();
			sectionName=studentList.getSectionid();
			sectionList=DBM.allSection(className,conn);
			rollno=studentList.getRollNo();
			disability=studentList.getDisability();
			handicap=studentList.getHandicap();
			bpl=studentList.getBpl();
			bplCardNo=studentList.getBplCardNo();
			concession=studentList.getConcession();
			oldconcessionCtegory=studentList.getConcession();
			oldconcessionStatus=studentList.getConcessionStatus();
			////// // System.out.println(oldconcessionStatus);
			caste=studentList.getCaste();
			singleChild=studentList.getSingleChild();
			bloodGroup=studentList.getBloodGroup();
			aadharNo=studentList.getAadharNo();
			SingleParent=studentList.getSingleParent();
			livingWith=studentList.getLivingWith();
			fatherEmail=studentList.getFatherEmail();
			motherEmail=studentList.getMotherEmail();
			sendMessageMobileNo =String.valueOf(studentList.getFathersPhone());
			fname1=studentList.getGname();
			relation1=studentList.getRelation();
			phone1=studentList.getPhone();
			lastSchoolName=studentList.getLastSchoolName();
			occupation1=studentList.getOccupation();
			passedClass=studentList.getPassedClass();
			medium=studentList.getMedium();
			sname1=studentList.getSname1();
			sname2=studentList.getSname2();
			
			admRemark=studentList.getAdmRemark();

			sclassid1=studentList.getSclassid1();
			//sClassName1=DBM.classSectionNameFromid(sclassid1,conn);
			sClassName1=sclassid1;
			sclassid2=studentList.getSclassid2();
			//sClassName2=DBM.classSectionNameFromid(sclassid2,conn);
			sClassName2=sclassid2;

			address1=studentList.getAddress();
			fname2=studentList.getFname1();
			//lname2=studentList.getLname2();
			relation2=studentList.getRelation1();
			occupation2=studentList.getOccupation1();
			phone2=studentList.getPhone1();
			address2=studentList.getAddress1();
			pResult=studentList.getpResult();
			p_percent=studentList.getP_percent();
			pReason=studentList.getpReason();
			height=studentList.getHeight();
			weight=studentList.getWeight();
			eyes=studentList.getEyes();
			fatherQualification=studentList.getFatherQualification();
			motherQualification=studentList.getMotherQualification();
			motherOccupation=studentList.getMotherOccupation();
			fatherDesignation=studentList.getFatherDesignation();
			motherDesignation=studentList.getMotherDesignation();
			fatherofficecontcatno=studentList.getFatherOrganization();
			motherofficecontactno=studentList.getMotherOrganization();
			fatherOfficeAdd=studentList.getFatherOfficeAdd();
			motherOfficeAdd=studentList.getMotherOfficeAdd();
			fatherIncome=studentList.getFatherIncome();
			motherIncome=studentList.getMotherIncome();
			fatherAadharNo=studentList.getFatherAadhaar();
			motherAadhaar=studentList.getMotherAadhaar();
			fatherSchoolEmp=studentList.getFatherSchoolEmp();
			motherSchoolEmp=studentList.getMotherSchoolEmp();
			tcDate=studentList.getTcDate();
			tempSection=sectionName;
			boardName=studentList.getBoard();
			hostal=studentList.getHostel();
			houseName = studentList.getHouse();
			
			ledgNo=studentList.getLedgerNo();
			regNo_IX=studentList.getRegNo_IX();
			regNo_XI=studentList.getRegNo_XI();
			admnFileNo=studentList.getAdmnFileNo();
			admitClassVal = studentList.getAdmitClassName();
			fahterAnnualIncome = studentList.getFatherAnnIncome();
			motherAnnualIncome = studentList.getMotherAnnIncome();
			studentStatus = studentList.getStudentStatus();
			tenRoll = studentList.getTenRoll();
			tenYearOfPass = studentList.getTenYearOfPass();
			tenBoard = studentList.getTenBoard();
			minority = studentList.getMinority();
			village = studentList.getVillage();
			
			
			String tempDoc = studentList.getDocumentsSubmitted();

			if(tempDoc!=null)
			{
				if(!tempDoc.equals(""))
				{
					if(tempDoc.contains(","))
					{
						documentsSubmitted = new ArrayList<>();
						String temparr[] = tempDoc.split(",");
						for(int i=0;i<temparr.length;i++)
						{
							documentsSubmitted.add(temparr[i]);
						}
					}
					else
					{
						documentsSubmitted = new ArrayList<>();
						documentsSubmitted.add(tempDoc);
					}
				}
			}

			if(bpl!=null)
			{
				showBPLNo();
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList1=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList1)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-"+info.getAddNumber());
			//studentListt.add(info.getFname()+"/"+info.getFathersName()+"-"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	
	public void checkSiblingClass()
	{
		DatabaseMethods1 DBMS=new DatabaseMethods1();	
		Connection conn=DataBaseConnection.javaConnection();
        
		try {
			
		
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
	    int dummyy = (int) component.getAttributes().get("Dummyy");
	    for(StudentInfo ll:completeList)
		{
			if(ll.getSno()==dummyy)
			{
				int index=ll.getFname().lastIndexOf("-")+1;
				String id=ll.getFname().substring(index);
				
				String classandSection = DBMS.studentClassSectionNameById(DBMS.schoolId(), id, conn);
				String[] split2 = classandSection.split(" - ");
				ll.setClassName(split2[0]);
				ll.setSectionName(split2[1]);
				
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    

	}
	

	public void checkSiblingClass1()
	{

		int index=sname1.lastIndexOf("-")+1;
		String id=sname1.substring(index);

		if(index!=0)
		{
			for(StudentInfo info : studentList1)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{

					sclassid1=info.getSectionid();
					sClassName1=info.getClassId();

				}
			}
		}
	}
	ArrayList<StudentInfo> studentList1;

	public void checkSiblingClass2()
	{
		int index=sname2.lastIndexOf("-")+1;
		String id=sname2.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList1)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					sclassid2=info.getSectionid();
					sClassName2=info.getClassId();
				}
			}
		}
	}


	public String editParentsDetail()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("addmissionNumber", String.valueOf(addNumber));
		ss.setAttribute("name", fname);
		ss.setAttribute("selectedClass", sectionName);
		return "editParentsDetail";
	}

	public String deleteStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		new DatabaseMethods1().deleteStudent(addNumber,"DELETE","ACTIVE",conn);
		DBM.decreaseStudentInAddSchool(conn);
		DBM.blockUser(addNumber,"DELETE",conn);
		new DataBaseMeathodJson().deleteStudentFromCommGroup(addNumber, "student", new DatabaseMethods1().schoolId(), conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "searchStudent";
	}

	public void updateHere()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		ArrayList<Transport> transportDetails = new ArrayList<>();
		String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		transportDetails=DBM.transportListDetails(DBM.schoolId(),addNumber,session,conn);
		SchoolInfoList info=DBM.fullSchoolInfo(conn);
		 if(village != null) {
			 village = village.toUpperCase();
		 }

//		if(transportDetails.size()<=0)
//		{
//			if(routeName1.equals(""))
//			{
//
//			}
//			else
//			{
//				DBM.transportDataEntry(DBM.schoolId(),addmissionDate,addNumber, routeName1, "Yes",conn);
//			}
//
//		}
//		else
//		{
//			if(!routeName1.equalsIgnoreCase(oldroutename))
//			{
//				new DataBaseMethodsReports().deleteStudentTransport(DatabaseMethods1.selectedSessionDetails(), addNumber, conn);
//				if(routeName1.equals(""))
//				{
//					DBM.transportDataEntry(DBM.schoolId(),addmissionDate,addNumber, routeName1, "No",conn);
//				}
//				else
//				{
//					DBM.transportDataEntry(DBM.schoolId(),addmissionDate,addNumber, routeName1, "Yes",conn);
//				}
//			}
//			
//			/*if(routeName1.equals(""))
//			{
//				DBM.updateTransport(addNumber,"No", "0",conn);
//			}
//			else
//			{
//				if(!routeName1.equalsIgnoreCase(oldroutename))
//                {
//                    DBM.updateTransport(addNumber,"Yes",routeName1,conn);
//                }
//			}*/
//		}



		if(studentPhone.equals(""))
		{
			studentPhone="0";
		}

		/*new DatabaseMethods1().updateStudentAttendance(addNumber, sectionName);
		new DatabaseMethods1().updateStudentPerformance(addNumber, sectionName);*/



		if(concession.equals(oldconcessionCtegory))
		{
			concessionStatus=oldconcessionStatus;
		}
		else
		{
			conceesionCategory=DBM.concessionCategoryNameById(DBM.schoolId(),concession, conn);
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
		}

		////// // System.out.println(concessionStatus+".........."+oldconcessionStatus);

		if(discountFee==null || discountFee.equals(""))
		{
			discountFee = "0";
		}

		boolean duplicate=new DataBaseValidator().checkDuplicateRollNo(rollno,sectionName,session,schid,addNumber,conn);
		if(duplicate==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Roll No Found For Student "+fname+".. Please Try With Another One.", "Validation error"));
		}
		else
		{
			int checker = DBM.checkingForDuplAdmNoAllowed(conn);
			int status = 0;
			if(checker == 1)
			{
				if(!srno.trim().equalsIgnoreCase(""))
				status=new DatabaseMethods1().duplicateStudentEntryInEdit(DBM.schoolId(),srno,addNumber,conn);
			}	
		 
		
		// // // System.out.println(status);
		 if(status==1)
		 {
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate admission Number found,try a different one","Duplicate admission Number found,try a different one"));
	     }
		 else
		 {
			int i=DBM.updateStudent(id,srno,fname,fathersName, motherName, addmissionDate, dob, sectionName,gender,
					nationality, religion, currentAddress, permanentAddress, pincode, country, fathersPhone,mothersPhone,category,
					fathersOccupation,residencePhone,studentImage,previousImage,bpl,bplCardNo,concession,caste,singleChild,bloodGroup,aadharNo,
					SingleParent,livingWith,fatherEmail,motherEmail,fatherImage,motherImage,preFatherImg,preMotherImg,g1Image,g2Image,preG1Img,
					preG2Img,/*sendMessageMobileNo,*/routeName1,discountFee,rollno,disability,handicap,conn,fatherAadharNo,motherAadhaar,lastSchoolName,
					passedClass,medium,pResult,boardName,p_percent,pReason,tcDate,height,weight,eyes,fname1,relation1,occupation1,phone1,address1,fname2,relation2,occupation2,phone2,address2,
					fatherQualification,fatherDesignation,fatherofficecontcatno,fatherOfficeAdd,fatherSchoolEmp,motherQualification,motherDesignation,motherofficecontactno,motherOfficeAdd,motherSchoolEmp,
					hostal,sname1,sClassName1,sname2,sClassName2,studentPhone,houseName,documentsSubmitted,concessionStatus,admRemark,regNo_IX,regNo_XI,admnFileNo,ledgNo,admitClassVal,fahterAnnualIncome,
					motherAnnualIncome,studentStatus,motherOccupation,tenRoll,tenYearOfPass,tenBoard,minority,
					village);
			if(i==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Detail Updated Successfully"));

				int count=0;
				String name="",schid=DBM.schoolId();
				for(StudentInfo ll:completeList)
				{
					if(!ll.getFname().equals(""))
					{
						name=ll.getFname().substring(ll.getFname().lastIndexOf("-")+1);
						ll.setAddNumber(name);
						count++;
					}
				}
				if(count>0)
				{
					new DatabaseMethods1().updateSiblingValue(schid,addNumber,String.valueOf(completeList.get(0).getId()),completeList,conn);
					//completeList=new ArrayList<>();
				}
				//previousImage=DBM.studentImage(DBM.schoolId(),"imageWithPath",studentList.getAddNumber(),conn);
				previousImage=DBM.studentImage(DBM.schoolId(),"imageOnly",studentList.getAddNumber(),conn);
				preFatherImg=DBM.fatherImage(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
				preFatherImg=DBM.fatherImage(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
				preMotherImg=DBM.motherImage(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
				preG1Img=DBM.gr1Image(DBM.schoolId(),studentList.getAddNumber(),"student",conn);
				preG2Img=DBM.gr2Image(DBM.schoolId(),studentList.getAddNumber(),"student",conn);

				String savePath;
				SchoolInfoList ls=DBM.fullSchoolInfo(conn);
				if(ls.getProjecttype().equals("online"))
				{
					String folderName=ls.getDownloadpath();
					savePath=folderName;
				}
				else
				{

					ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
					String realPath = ctx.getRealPath("/");
					savePath = realPath;
				}
				currstudeimage=savePath+previousImage;
				curfatherimage=savePath+preFatherImg;
				curmotherimage=savePath+preMotherImg;
				curg1image=savePath+preG1Img;
				curg2image=savePath+preG2Img;

			}
		 }
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
//
//	public String permanentDeleteStudent()
//	{
//		Connection conn = DataBaseConnection.javaConnection();
//		DatabaseMethods1 obj=new DatabaseMethods1();
//
//		try
//		{
//			int i=obj.deleteprmanentStudent(addNumber,conn);
//			if(i==1)
//			{
//				obj.decreaseStudentInAddSchool(conn);
//				FacesContext fc=FacesContext.getCurrentInstance();
//				fc.addMessage(null, new FacesMessage("Student Deleted successfully"));
//				conn.close();
//				return "searchStudent";
//			}
//
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//
//		if(conn!=null)
//		{
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return "";
//	}
	
	
	public String permanentDeleteStudent()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			//int i=obj.deleteprmanentStudent(selectedStudent.getAddNumber(),conn);
			int i=obj.deleteStudent(addNumber,"DELETE","ACTIVE",conn);
			if(i>=1)
			{
				obj.decreaseStudentInAddSchool(conn);
				
				obj.blockUser(addNumber,"DELETE",conn);
				
				
				new DataBaseMeathodJson().deleteStudentFromCommGroup(addNumber, "student", new DatabaseMethods1().schoolId(), conn);
				
				

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Student Deleted Successfully"));
				
				conn.close();
				return "searchStudent";
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}
	
	
	
	 public void removeMotherPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteStudentPhoto(schidd,addNumber,"mother",conn);
			
		       if(status>=1)
		      {
		    	   String refNo7;
				try {
					refNo7=addWorkLog7(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	File file = new File(path+preMotherImg);
				file.delete();
				
				preMotherImg=DBM.motherImage(schidd,addNumber,"student",conn);
				
				//// // System.out.println(previousImage);
				SchoolInfoList ls2 = DBM.fullSchoolInfo(schidd, conn);
				curmotherimage = ls2.getDownloadpath()+preMotherImg;
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Mother's Photo Deleted"));
		 		
		    	
		      }
		      else 
		      {
		    
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		      }
			
			

		     try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 }
	 
	 public String addWorkLog7(Connection conn)
		{
		    String value = "Student Id-"+addNumber;
			String language= "";
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Remove Mother Image","WEB",value,conn);
			return refNo;
		}
	 
	 public void removeFatherPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteStudentPhoto(schidd,addNumber,"father",conn);
			
		       if(status>=1)
		      {
		    	   String refNo6;
					try {
						refNo6=addWorkLog6(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}   
		    	File file = new File(path+preFatherImg);
				file.delete();
				preFatherImg=DBM.fatherImage(schidd,addNumber,"student",conn);
				
				
				//// // System.out.println(previousImage);
				SchoolInfoList ls2 = DBM.fullSchoolInfo(schidd, conn);
				curfatherimage = ls2.getDownloadpath()+preFatherImg;
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Father's Photo Deleted"));
		 		
		    	
		      }
		      else 
		      {
		    
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		      }
			
			

		     try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 }
	 
	 public String addWorkLog6(Connection conn)
		{
		    String value = "Student Id-"+addNumber;
			String language= "";
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Remove Father Image","WEB",value,conn);
			return refNo;
		}
	
	
	
	
	 public void removeStudentPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteStudentPhoto(schidd,addNumber,"student",conn);
			
		       if(status>=1)
		      {
		    	   String refNo5;
					try {
						refNo5=addWorkLog5(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}   
		    	File file = new File(path+previousImage);
				file.delete();
				
				previousImage=DBM.studentImage(schidd,"imageOnly",addNumber,conn);
				//// // System.out.println(previousImage);
				SchoolInfoList ls2 = DBM.fullSchoolInfo(schidd, conn);
				currstudeimage = ls2.getDownloadpath()+previousImage;
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Student Photo Deleted"));
		 		
		    	
		      }
		      else 
		      {
		    
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		      }
			
			

		     try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 }
	 
	 
	 public String addWorkLog5(Connection conn)
		{
		    String value = "Student Id-"+addNumber;
			String language= "";
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Remove Student Image","WEB",value,conn);
			return refNo;
		}
	

	 public void removeSib()
	 {
		 ArrayList<StudentInfo> tempList = new ArrayList<StudentInfo>();
		 
		 for(StudentInfo cc: completeList)
		 {
			 if(cc.getSno() != selectedSibling.getSno())
			 {
				tempList.add(cc);
			 }
		 }
		 
		 completeList = new ArrayList<StudentInfo>();
		 
		 completeList.addAll(tempList);
		 int j=1;
		 for(StudentInfo cc: completeList)
		 {
			cc.setSno(j++); 
		 }
		 
		
		 
	 }

	public String getPreviousImage() {
		return previousImage;
	}
	public void setPreviousImage(String previousImage) {
		this.previousImage = previousImage;
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


	public String getCurrstudeimage() {
		return currstudeimage;
	}

	public void setCurrstudeimage(String currstudeimage) {
		this.currstudeimage = currstudeimage;
	}

	public String getCurfatherimage() {
		return curfatherimage;
	}

	public void setCurfatherimage(String curfatherimage) {
		this.curfatherimage = curfatherimage;
	}

	public String getCurmotherimage() {
		return curmotherimage;
	}

	public void setCurmotherimage(String curmotherimage) {
		this.curmotherimage = curmotherimage;
	}

	public String getCurg1image() {
		return curg1image;
	}

	public void setCurg1image(String curg1image) {
		this.curg1image = curg1image;
	}

	public String getCurg2image() {
		return curg2image;
	}

	public void setCurg2image(String curg2image) {
		this.curg2image = curg2image;
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
	}public String getFathersOccupation() {
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
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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
	public UploadedFile getStudentImage() {
		return studentImage;
	}
	public void setStudentImage(UploadedFile studentImage) {
		this.studentImage = studentImage;
	}

	public String getTempSection() {
		return tempSection;
	}

	public void setTempSection(String tempSection) {
		this.tempSection = tempSection;
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

	public UploadedFile getFatherImage() {
		return fatherImage;
	}

	public void setFatherImage(UploadedFile fatherImage) {
		this.fatherImage = fatherImage;
	}

	public UploadedFile getMotherImage() {
		return motherImage;
	}

	public void setMotherImage(UploadedFile motherImage) {
		this.motherImage = motherImage;
	}

	public boolean isShowBpl() {
		return showBpl;
	}

	public void setShowBpl(boolean showBpl) {
		this.showBpl = showBpl;
	}

	public String getPreFatherImg() {
		return preFatherImg;
	}

	public void setPreFatherImg(String preFatherImg) {
		this.preFatherImg = preFatherImg;
	}

	public String getPreMotherImg() {
		return preMotherImg;
	}

	public void setPreMotherImg(String preMotherImg) {
		this.preMotherImg = preMotherImg;
	}

	public String getSendMessageMobileNo() {
		return sendMessageMobileNo;
	}

	public void setSendMessageMobileNo(String sendMessageMobileNo) {
		this.sendMessageMobileNo = sendMessageMobileNo;
	}

	public String getPreG1Img() {
		return preG1Img;
	}

	public void setPreG1Img(String preG1Img) {
		this.preG1Img = preG1Img;
	}

	public String getPreG2Img() {
		return preG2Img;
	}

	public void setPreG2Img(String preG2Img) {
		this.preG2Img = preG2Img;
	}

	public UploadedFile getG1Image() {
		return g1Image;
	}

	public void setG1Image(UploadedFile g1Image) {
		this.g1Image = g1Image;
	}

	public UploadedFile getG2Image() {
		return g2Image;
	}

	public void setG2Image(UploadedFile g2Image) {
		this.g2Image = g2Image;
	}

	public Date getAddmissionDate() {
		return addmissionDate;
	}

	public void setAddmissionDate(Date addmissionDate) {
		this.addmissionDate = addmissionDate;
	}

	public String getFatherAadharNo() {
		return fatherAadharNo;
	}

	public void setFatherAadharNo(String fatherAadharNo) {
		this.fatherAadharNo = fatherAadharNo;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getHostal() {
		return hostal;
	}

	public void setHostal(String hostal) {
		this.hostal = hostal;
	}

	public String getHouseName() {
		return houseName;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<SelectItem> getHouseCategorylist() {
		return houseCategorylist;
	}

	public void setHouseCategorylist(ArrayList<SelectItem> houseCategorylist) {
		this.houseCategorylist = houseCategorylist;
	}

	public ArrayList<SelectItem> getDoctypelist() {
		return doctypelist;
	}

	public void setDoctypelist(ArrayList<SelectItem> doctypelist) {
		this.doctypelist = doctypelist;
	}

	public String getFname1() {
		return fname1;
	}

	public void setFname1(String fname1) {
		this.fname1 = fname1;
	}

	public String getLname1() {
		return lname1;
	}

	public void setLname1(String lname1) {
		this.lname1 = lname1;
	}

	public String getRelation1() {
		return relation1;
	}

	public void setRelation1(String relation1) {
		this.relation1 = relation1;
	}

	public String getOccupation1() {
		return occupation1;
	}

	public void setOccupation1(String occupation1) {
		this.occupation1 = occupation1;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
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

	public String getLastSchoolName() {
		return lastSchoolName;
	}

	public void setLastSchoolName(String lastSchoolName) {
		this.lastSchoolName = lastSchoolName;
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

	public String getsClassName1() {
		return sClassName1;
	}

	public void setsClassName1(String sClassName1) {
		this.sClassName1 = sClassName1;
	}

	public String getsClassName2() {
		return sClassName2;
	}

	public void setsClassName2(String sClassName2) {
		this.sClassName2 = sClassName2;
	}

	public ArrayList<String> getDocumentsSubmitted() {
		return documentsSubmitted;
	}

	public void setDocumentsSubmitted(ArrayList<String> documentsSubmitted) {
		this.documentsSubmitted = documentsSubmitted;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
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

	public Date getTcDate() {
		return tcDate;
	}

	public void setTcDate(Date tcDate) {
		this.tcDate = tcDate;
	}

	public String getMotherofficecontactno() {
		return motherofficecontactno;
	}

	public void setMotherofficecontactno(String motherofficecontactno) {
		this.motherofficecontactno = motherofficecontactno;
	}

	public String getFatherofficecontcatno() {
		return fatherofficecontcatno;
	}

	public void setFatherofficecontcatno(String fatherofficecontcatno) {
		this.fatherofficecontcatno = fatherofficecontcatno;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getRouteName1() {
		return routeName1;
	}


	public void setRouteName1(String routeName1) {
		this.routeName1 = routeName1;
	}


	public String getDiscountFee() {
		return discountFee;
	}


	public void setDiscountFee(String discountFee) {
		this.discountFee = discountFee;
	}


	public String getTotalFess() {
		return TotalFess;
	}


	public void setTotalFess(String totalFess) {
		TotalFess = totalFess;
	}


	public String getRouteFees() {
		return routeFees;
	}


	public void setRouteFees(String routeFees) {
		this.routeFees = routeFees;
	}


	public ArrayList<StudentInfo> getStudentList1() {
		return studentList1;
	}


	public void setStudentList1(ArrayList<StudentInfo> studentList1) {
		this.studentList1 = studentList1;
	}


	public ArrayList<SelectItem> getConnsessionList() {
		return connsessionList;
	}


	public void setConnsessionList(ArrayList<SelectItem> connsessionList) {
		this.connsessionList = connsessionList;
	}


	public String getRollno() {
		return rollno;
	}


	public void setRollno(String rollno) {
		this.rollno = rollno;
	}


	public ArrayList<SelectItem> getReligionList() {
		return religionList;
	}


	public void setReligionList(ArrayList<SelectItem> religionList) {
		this.religionList = religionList;
	}


	public ArrayList<SelectItem> getHandicaplist() {
		return handicaplist;
	}


	public void setHandicaplist(ArrayList<SelectItem> handicaplist) {
		this.handicaplist = handicaplist;
	}


	public String getDisability() {
		return disability;
	}


	public void setDisability(String disability) {
		this.disability = disability;
	}


	public String getHandicap() {
		return handicap;
	}


	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}


	public boolean isShowHandicap() {
		return showHandicap;
	}


	public void setShowHandicap(boolean showHandicap) {
		this.showHandicap = showHandicap;
	}


	public boolean isShowDis() {
		return showDis;
	}


	public void setShowDis(boolean showDis) {
		this.showDis = showDis;
	}


	public String getStudentPhone() {
		return studentPhone;
	}


	public void setStudentPhone(String studentPhone) {
		this.studentPhone = studentPhone;
	}


	public boolean isShowInstitute() {
		return showInstitute;
	}


	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
	}


	public String getSrno() {
		return srno;
	}


	public void setSrno(String srno) {
		this.srno = srno;
	}


	public boolean isDisableSrNo() {
		return disableSrNo;
	}


	public void setDisableSrNo(boolean disableSrNo) {
		this.disableSrNo = disableSrNo;
	}


	public String getAdmRemark() {
		return admRemark;
	}


	public void setAdmRemark(String admRemark) {
		this.admRemark = admRemark;
	}


	public String getOldroutename() {
		return oldroutename;
	}


	public void setOldroutename(String oldroutename) {
		this.oldroutename = oldroutename;
	}


	public ArrayList<StudentInfo> getCompleteList() {
		return completeList;
	}


	public void setCompleteList(ArrayList<StudentInfo> completeList) {
		this.completeList = completeList;
	}


	public String getRegNo_IX() {
		return regNo_IX;
	}


	public void setRegNo_IX(String regNo_IX) {
		this.regNo_IX = regNo_IX;
	}


	public String getRegNo_XI() {
		return regNo_XI;
	}


	public void setRegNo_XI(String regNo_XI) {
		this.regNo_XI = regNo_XI;
	}


	public String getLedgNo() {
		return ledgNo;
	}


	public void setLedgNo(String ledgNo) {
		this.ledgNo = ledgNo;
	}


	public String getAdmnFileNo() {
		return admnFileNo;
	}


	public void setAdmnFileNo(String admnFileNo) {
		this.admnFileNo = admnFileNo;
	}


	public String getAdmitClassVal() {
		return admitClassVal;
	}


	public void setAdmitClassVal(String admitClassVal) {
		this.admitClassVal = admitClassVal;
	}


	public SelectItem getSelectedDoc() {
		return selectedDoc;
	}


	public void setSelectedDoc(SelectItem selectedDoc) {
		this.selectedDoc = selectedDoc;
	}


	public String getDoctype() {
		return doctype;
	}


	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}


	public StudentInfo getSelectedSibling() {
		return selectedSibling;
	}


	public void setSelectedSibling(StudentInfo selectedSibling) {
		this.selectedSibling = selectedSibling;
	}


	public String getFahterAnnualIncome() {
		return fahterAnnualIncome;
	}


	public void setFahterAnnualIncome(String fahterAnnualIncome) {
		this.fahterAnnualIncome = fahterAnnualIncome;
	}


	public String getMotherAnnualIncome() {
		return motherAnnualIncome;
	}


	public void setMotherAnnualIncome(String motherAnnualIncome) {
		this.motherAnnualIncome = motherAnnualIncome;
	}


	public String getStudentStatus() {
		return studentStatus;
	}


	public void setStudentStatus(String studentStatus) {
		this.studentStatus = studentStatus;
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


	public String getMinority() {
		return minority;
	}


	public void setMinority(String minority) {
		this.minority = minority;
	}


	public boolean isShowminority() {
		return showminority;
	}


	public void setShowminority(boolean showminority) {
		this.showminority = showminority;
	}


	public String getVillage() {
		return village;
	}


	public void setVillage(String village) {
		this.village = village;
	}



	
}
