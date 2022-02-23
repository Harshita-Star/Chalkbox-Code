package schooldata;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="schoolInfo")
@ViewScoped
public class SchoolInformationBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String schoolName,smsSchoolName,hindiName,emailId, website,add1,add2,add3,add4,phoneNo,mobileNo, regNo, imagePath,rname1,rname2,rname3,type, permitAttendance,permitLeave;
	transient UploadedFile image,marksheetHeader,tcHeader;
	Boolean show=false;
	int reciept;
	boolean r1=false;
	boolean r2=false;
	boolean r3=false,showAlias=false;
	String discountotp,discountOtpNo,cancelOtpNo,collectionNo;
	String aliasName,contactNo,contactName,marksheetHeaderImagePath,tcHeaderImagePath,schoolSession,feeStart,schRegNo;
	Date startDate=new Date(),expireDate,renewalDate,date=new Date();
	double chalkBoxAmount,imgAmount,chalkBoxRenewal,imgRenewalAmount;
	int noOfStudents,msz;
	String previousImage,previousHeader,previousTcHeader,concessionRequest;
	String cancelfee,autoCollection,country,state,district,zone,attendComm,rfidComm,jwt;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,sessionValue,udiseNo,schoolCategory, meetingApproval;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public SchoolInformationBean()
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);

		boolean i=obj.checkSchoolInfo(conn);
		if(i==true)
		{
			SchoolInfoList info=obj.fullSchoolInfo(conn);
			
			
			if(info.getParentAppType().equalsIgnoreCase("common"))
			{
				showAlias=true;
			}
			else
			{
				showAlias=false;
			}
			
			schoolName=info.getSchoolName();
			permitLeave=info.getPermitLeave();
			add1=info.getAdd1();
			add2=info.getAdd2();
			add3=info.getAdd3();
			add4=info.getAdd4();
			phoneNo=info.getPhoneNo();
			schoolSession=info.getSchoolSession();
			feeStart=info.getFeeStart();
			schRegNo=info.getSchRegNo();
			aliasName=info.getAliasName();
			mobileNo=info.getMobileNo();
			emailId=info.getEmailId();
			website=info.getWebsite();
			regNo=info.getRegNo();
			if(info.getProjecttype().equals("online"))
			{
				savePath=info.getDownloadpath();
			}
			previousImage=info.getImagePath();
			imagePath=savePath+info.getImagePath();
			previousHeader=info.getMarksheetHeader();
			marksheetHeaderImagePath=savePath+info.getMarksheetHeader();
			previousTcHeader = info.getTcHeader();
			tcHeaderImagePath=savePath+info.getTcHeader();
			discountotp=info.getDiscountotp();
			discountOtpNo=info.getDiscountOtpNo();
			cancelOtpNo = info.getCancelOtpNo();
			collectionNo=info.getAutoCollectionNo();
			concessionRequest=info.getConcessionRequest();
			reciept=info.getReciept();
			permitAttendance=info.getPermitAttendance();
			cancelfee=info.getCancalfee();
			autoCollection=info.getAutoCollection();
			smsSchoolName = info.getSmsSchoolName().split("\\n")[0];
			hindiName = info.getHindiName().split("\\n")[0];
			meetingApproval = info.getMeetingApproval();
			jwt = info.getJwt();
			
			
			attendComm = info.getAttendantComm();
			rfidComm = info.getRfidComm();
			udiseNo = info.getUdise();

			country=info.getCountry();
			state=info.getState();
			district=info.getDistrict();
			zone=info.getZone();
			schoolCategory = info.getSchoolCategory();

			rname1=info.getRname1();
			rname2=info.getRname2();
			rname3=info.getRname3();
			if(reciept==1){
				r1=true;
				r2=false;
				r3=false;
			}

			else if(reciept==2){
				r1=true;
				r2=true;
				r3=false;
			}
			else
			{
				r1=true;
				r2=true;
				r3=true;
			}
			show=true;
		}
		else
		{
			show=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void recieptno() {

		if(reciept==1){
			r1=true;
			r2=false;
			r3=false;
		}
		else if(reciept==2){
			r1=true;
			r2=true;
			r3=false;
		}
		else
		{
			r1=true;
			r2=true;
			r3=true;
		}

	}


	public String insert() throws UnsupportedEncodingException
	{

		Connection conn=DataBaseConnection.javaConnection();
		
		//String dt=new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		
		if (image != null && image.getSize() > 0)
		{
			String exten[]=image.getFileName().split("\\.");
			imagePath="schoolLogo"+schoolId+"."+exten[exten.length-1];
			obj.makeScanPath(image, imagePath,conn);
		}
		else
		{
			imagePath=previousImage;
		}

		if (marksheetHeader != null && marksheetHeader.getSize() > 0)
		{
			String exten[]=marksheetHeader.getFileName().split("\\.");
			marksheetHeaderImagePath="marksheetHeader"+schoolId+"."+exten[exten.length-1];
			obj.makeScanPath(marksheetHeader,marksheetHeaderImagePath,conn);
		}
		else
		{
			marksheetHeaderImagePath=previousHeader;
		}
		
		if (tcHeader != null && tcHeader.getSize() > 0)
		{
			String exten[]=tcHeader.getFileName().split("\\.");
			tcHeaderImagePath="tcHeader"+schoolId+"."+exten[exten.length-1];
			obj.makeScanPath(tcHeader,tcHeaderImagePath,conn);
		}
		else
		{
			tcHeaderImagePath=previousTcHeader;
		}

		if(reciept<=3)
		{


			if(discountotp.equalsIgnoreCase("yes"))
			{
				if(discountOtpNo.length()!=10)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Discount Otp No must Be 10 Digit","Discount Otp No must Be 10 Digit"));

					return "";
				}
			}


			if(cancelfee.equalsIgnoreCase("yes"))
			{
				if(cancelOtpNo.length()!=10)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Cancel Fee Otp No must Be 10 Digit","Cancel Fee Otp No must Be 10 Digit"));

					return "";
				}
			}


			if(autoCollection.equalsIgnoreCase("yes"))
			{
				if(collectionNo.length()!=10)
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Auto Collection No must Be 10 Digit","Auto Collection No must Be 10 Digit"));

					return "";
				}
			}


			boolean i=obj.checkSchoolInfo(conn);
			if(i==true)
			{
				obj.upateNewSchool(schoolName,add1,add2,add3,add4,phoneNo,mobileNo, emailId, website, regNo,
						imagePath,reciept,rname1,rname2,rname3,conn,permitAttendance,	marksheetHeaderImagePath,
						schoolSession,feeStart,schRegNo,discountotp,discountOtpNo,cancelfee,autoCollection,cancelOtpNo
						,collectionNo,concessionRequest,country,state,district,zone,smsSchoolName,hindiName,
						permitLeave,attendComm,rfidComm,tcHeaderImagePath,udiseNo,schoolCategory, meetingApproval, jwt);
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Update Successfully Your School Details","Update Successfully Your School Details"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "schoolInformation";
			}
			/*else
		{
		   int j=obj.insertNewSchool(schoolName,add1,add2,add3,add4,phoneNo,mobileNo, emailId, website,
				   regNo, imagePath,reciept,rname1,rname2,rname3,schid,"",conn,permitAttendance,marksheetHeaderImagePath,schoolSession,feeStart,"","");
		    if(j==1)
		    {
		    	try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	return "selectSession";
		    }

		}*/
		}
		else  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reciept cannot be greater than 3"));

		}

		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public String updateHindi() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();

		obj.updateSMSNameHindi(schoolId, hindiName, conn);
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Hindi Name Updated Successfully","Hindi Name Updated Successfully"));
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "schoolInformation";
	}
	
	
	public void removeSchoolLogo()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls = obj.fullSchoolInfo(schoolId, conn);
		String path = ls.getUploadpath();
		 
		
	       int status = obj.deleteSchoolLogo(schoolId,conn);
		
	       if(status>=1)
	      {
	    	File file = new File(path+ls.getImagePath());
			file.delete();
			
			SchoolInfoList ls2 = obj.fullSchoolInfo(schoolId, conn);
			imagePath=ls2.getDownloadpath()+ls2.getImagePath();
			
	    	FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("School Logo Deleted"));
	 		
	    	
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
	
	public void removeMarksheetHeader()
	{
		Connection conn = DataBaseConnection.javaConnection();

		SchoolInfoList ls = obj.fullSchoolInfo(schoolId, conn);
		String path = ls.getUploadpath();
		 
		
	       int status = obj.deleteHeaderOfMarksheet(schoolId,conn);
		
	       if(status>=1)
	      {
	    	File file = new File(path+ls.getMarksheetHeader());
			file.delete();
			
			SchoolInfoList ls2 = obj.fullSchoolInfo(schoolId, conn);
			marksheetHeaderImagePath=ls2.getDownloadpath()+ls2.getMarksheetHeader();
			
	    	FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Marksheet Header Removed"));
	 		
	    	
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
	
	public void removeTCHeader()
	{
		Connection conn = DataBaseConnection.javaConnection();
	
		SchoolInfoList ls = obj.fullSchoolInfo(schoolId, conn);
		String path = ls.getUploadpath();
		 
		
	       int status = obj.deleteHeaderOfTC(schoolId,conn);
		
	       if(status>=1)
	      {
	    	File file = new File(path+ls.getTcHeader());
			file.delete();
			
			SchoolInfoList ls2 = obj.fullSchoolInfo(schoolId, conn);
			tcHeaderImagePath=ls2.getDownloadpath()+ls2.getTcHeader();
			
	    	FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("TC Header Removed"));
	 		
	    	
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
	

	public Boolean getShow() {
		return show;
	}
	public void setShow(Boolean show) {
		this.show = show;
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
	public String getAdd2() {
		return add2;
	}
	public void setAdd2(String add2) {
		this.add2 = add2;
	}
	public String getAdd3() {
		return add3;
	}
	public void setAdd3(String add3) {
		this.add3 = add3;
	}
	public String getAdd4() {
		return add4;
	}
	public void setAdd4(String add4) {
		this.add4 = add4;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}




	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
	}




	public int getReciept() {
		return reciept;
	}




	public void setReciept(int reciept) {
		this.reciept = reciept;
	}




	public String getRname1() {
		return rname1;
	}




	public void setRname1(String rname1) {
		this.rname1 = rname1;
	}




	public String getRname2() {
		return rname2;
	}




	public String getDiscountotp() {
		return discountotp;
	}

	public void setDiscountotp(String discountotp) {
		this.discountotp = discountotp;
	}

	public String getDiscountOtpNo() {
		return discountOtpNo;
	}

	public void setDiscountOtpNo(String discountOtpNo) {
		this.discountOtpNo = discountOtpNo;
	}

	public void setRname2(String rname2) {
		this.rname2 = rname2;
	}




	public String getRname3() {
		return rname3;
	}




	public void setRname3(String rname3) {
		this.rname3 = rname3;
	}

	public boolean isR1() {
		return r1;
	}

	public void setR1(boolean r1) {
		this.r1 = r1;
	}

	public boolean isR2() {
		return r2;
	}

	public void setR2(boolean r2) {
		this.r2 = r2;
	}

	public boolean isR3() {
		return r3;
	}

	public void setR3(boolean r3) {
		this.r3 = r3;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Date getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getChalkBoxAmount() {
		return chalkBoxAmount;
	}

	public void setChalkBoxAmount(double chalkBoxAmount) {
		this.chalkBoxAmount = chalkBoxAmount;
	}

	public double getImgAmount() {
		return imgAmount;
	}

	public void setImgAmount(double imgAmount) {
		this.imgAmount = imgAmount;
	}

	public double getChalkBoxRenewal() {
		return chalkBoxRenewal;
	}

	public void setChalkBoxRenewal(double chalkBoxRenewal) {
		this.chalkBoxRenewal = chalkBoxRenewal;
	}

	public double getImgRenewalAmount() {
		return imgRenewalAmount;
	}

	public void setImgRenewalAmount(double imgRenewalAmount) {
		this.imgRenewalAmount = imgRenewalAmount;
	}

	public int getNoOfStudents() {
		return noOfStudents;
	}

	public void setNoOfStudents(int noOfStudents) {
		this.noOfStudents = noOfStudents;
	}

	public int getMsz() {
		return msz;
	}

	public void setMsz(int msz) {
		this.msz = msz;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPermitAttendance() {
		return permitAttendance;
	}

	public void setPermitAttendance(String permitAttendance) {
		this.permitAttendance = permitAttendance;
	}

	public UploadedFile getMarksheetHeader() {
		return marksheetHeader;
	}

	public void setMarksheetHeader(UploadedFile marksheetHeader) {
		this.marksheetHeader = marksheetHeader;
	}

	public String getMarksheetHeaderImagePath() {
		return marksheetHeaderImagePath;
	}

	public void setMarksheetHeaderImagePath(String marksheetHeaderImagePath) {
		this.marksheetHeaderImagePath = marksheetHeaderImagePath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSchoolSession() {
		return schoolSession;
	}

	public void setSchoolSession(String schoolSession) {
		this.schoolSession = schoolSession;
	}

	public String getFeeStart() {
		return feeStart;
	}

	public void setFeeStart(String feeStart) {
		this.feeStart = feeStart;
	}

	public String getSchRegNo() {
		return schRegNo;
	}

	public void setSchRegNo(String schRegNo) {
		this.schRegNo = schRegNo;
	}

	public String getCancelfee() {
		return cancelfee;
	}

	public void setCancelfee(String cancelfee) {
		this.cancelfee = cancelfee;
	}

	public String getAutoCollection() {
		return autoCollection;
	}

	public void setAutoCollection(String autoCollection) {
		this.autoCollection = autoCollection;
	}

	public String getCancelOtpNo() {
		return cancelOtpNo;
	}

	public void setCancelOtpNo(String cancelOtpNo) {
		this.cancelOtpNo = cancelOtpNo;
	}

	public String getCollectionNo() {
		return collectionNo;
	}

	public void setCollectionNo(String collectionNo) {
		this.collectionNo = collectionNo;
	}

	public String getConcessionRequest() {
		return concessionRequest;
	}

	public void setConcessionRequest(String concessionRequest) {
		this.concessionRequest = concessionRequest;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getSmsSchoolName() {
		return smsSchoolName;
	}

	public void setSmsSchoolName(String smsSchoolName) {
		this.smsSchoolName = smsSchoolName;
	}

	public String getHindiName() {
		return hindiName;
	}

	public void setHindiName(String hindiName) {
		this.hindiName = hindiName;
	}

	public String getPermitLeave() {
		return permitLeave;
	}

	public void setPermitLeave(String permitLeave) {
		this.permitLeave = permitLeave;
	}

	public String getAttendComm() {
		return attendComm;
	}

	public void setAttendComm(String attendComm) {
		this.attendComm = attendComm;
	}

	public String getRfidComm() {
		return rfidComm;
	}

	public void setRfidComm(String rfidComm) {
		this.rfidComm = rfidComm;
	}

	public boolean isShowAlias() {
		return showAlias;
	}

	public void setShowAlias(boolean showAlias) {
		this.showAlias = showAlias;
	}

	public UploadedFile getTcHeader() {
		return tcHeader;
	}

	public void setTcHeader(UploadedFile tcHeader) {
		this.tcHeader = tcHeader;
	}

	public String getTcHeaderImagePath() {
		return tcHeaderImagePath;
	}

	public void setTcHeaderImagePath(String tcHeaderImagePath) {
		this.tcHeaderImagePath = tcHeaderImagePath;
	}

	public String getUdiseNo() {
		return udiseNo;
	}

	public void setUdiseNo(String udiseNo) {
		this.udiseNo = udiseNo;
	}

	public String getSchoolCategory() {
		return schoolCategory;
	}

	public void setSchoolCategory(String schoolCategory) {
		this.schoolCategory = schoolCategory;
	}

	public String getMeetingApproval() {
		return meetingApproval;
	}

	public void setMeetingApproval(String meetingApproval) {
		this.meetingApproval = meetingApproval;
	}
	
	
}
