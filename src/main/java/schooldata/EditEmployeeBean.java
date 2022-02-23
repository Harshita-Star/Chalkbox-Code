package schooldata;

import java.io.File;
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

import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="editEmp")
@ViewScoped
public class EditEmployeeBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> categoryList;
	Date leavingDate;
	boolean showSpouse;
	UploadedFile employeeImage;
	String previousImage,mobile2,spouseName;
	int id,salary;
	double leave,daamt,hraamt;
	String fname,lname,gender,category,email,marital,address,comment,qualification,subCateg;
	String empImage,aadharImage,panImage,voteridImage,ugImage,pgImage,bedImage,policeImage,expImage,otherImage,dlImage,dlNo;
	String strEmpImage,strAadharImage,strPanImage,strVoteridImage,strUgImage,strPgImage,strBedImage,strPoliceImage,strExpImage,
	strOtherImage,strDlImage;
	UploadedFile uEmpImage,uAadharImage,uPanImage,uVoteridImage,uUgImage,uPgImage,uBedImage,uPoliceImage,uExpImage,uOtherImage,uDlImage;
	Date joiningDate,dob,anniDate;
	long mobile;
	String userid,employeeUniqueId,aadhaarNo,panNo;
	String bankAccountNo,bankName,bankBranch,ifscCode,epfUanNo,esiNo,platform;

	SchoolInfoList ls = new SchoolInfoList();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public void showSpouseMethod()
	{

		if(marital.equals("Married"))
		{
			showSpouse=true;
		}
		else
		{
			showSpouse=false;
			spouseName=null;
		}
	}

	public EditEmployeeBean()
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		if(!FacesContext.getCurrentInstance().isPostback())
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			EmployeeInfo list=(EmployeeInfo) ss.getAttribute("selectedEmployee");

			id=list.getId();
 
			previousImage=list.getEmpImageTemp();
			aadharImage=list.getAadharImage();
			panImage=list.getPanImage();
			voteridImage=list.getVoteridImage();
			ugImage=list.getUgImage();
			pgImage=list.getPgImage();
			bedImage=list.getBedImage();
			policeImage=list.getPoliceImage();
			expImage=list.getExpImage();
			otherImage=list.getOtherImage();
			dlImage=list.getDlImage();
			subCateg=list.getSubCateg();
			strEmpImage=list.getEmpImageTemp();
			strAadharImage=list.getAadharImage();
			strPanImage=list.getPanImage();
			strVoteridImage=list.getVoteridImage();
			strUgImage=list.getUgImage();
			strPgImage=list.getPgImage();
			strBedImage=list.getBedImage();
			strPoliceImage=list.getPoliceImage();
			strExpImage=list.getExpImage();
			strOtherImage=list.getOtherImage();
			strDlImage=list.getDlImage();
			employeeUniqueId = list.getEmployeeUniqueId();
			panNo = list.getPanNo();
			aadhaarNo = list.getAadhaarNo();
			bankAccountNo = list.getBankAccountNo();
			bankName = list.getBankName();
			bankBranch = list.getBankBranch();
			ifscCode = list.getIfscCode();
			epfUanNo = list.getEpfUanNo();
			esiNo = list.getEsiNo();
			platform = list.getPlatform();
			
			ls=DBM.fullSchoolInfo(conn);
			if(ls.getProjecttype().equals("online"))
			{
				String folderName=ls.getDownloadpath();
				previousImage=folderName+previousImage;
				aadharImage=folderName+aadharImage;
				panImage=folderName+panImage;
				voteridImage=folderName+voteridImage;
				ugImage=folderName+ugImage;
				pgImage=folderName+pgImage;
				bedImage=folderName+bedImage;
				policeImage=folderName+policeImage;
				expImage=folderName+expImage;
				otherImage=folderName+otherImage;
				dlImage=folderName+dlImage;
			}

			fname=list.getFname();
			lname=list.getLname();
			dob=list.getDob();
			gender=list.getGender();
			category=DBM.employeeCategoryIdByName(list.getCategory(),conn);
			salary=list.getSalary();
			qualification=list.getQualification();
			joiningDate=list.getStartingDate();
			mobile=list.getMobile();
			email=list.getEmail();
			dlNo=list.getDlNo();
			leave=list.getLeave();
			daamt = list.getDaamt();
			hraamt = list.getHraamt();
			marital=list.getMarital();
			userid=list.getEmplyeeuserid();
			address=list.getAddress();
			comment=list.getComment();
			leavingDate=list.getLeavingDate();
			mobile2=list.getMobile2();
           if(marital.equals("Married"))
			{
				showSpouse=true;
				spouseName=list.getSpouseName();
				anniDate=list.getAnniDate();
			}
			else
			{
				showSpouse=false;
			}
			categoryList=DBM.allEmployeeCategory(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String updateNow()
	{

		DatabaseMethods1 DBM = new DatabaseMethods1();
		int flag=0;

		if(flag==0)
		{
		  Connection conn = DataBaseConnection.javaConnection();
		
	     // boolean duplicateId = DBM.checkEmployeUniqueIdDuplicasy(employeeUniqueId,conn,String.valueOf(id) );
				
		  
			
			try
			{
				if(employeeImage != null && employeeImage.getSize() > 0)
				{
					strEmpImage=employeeImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=employeeImage.getFileName().split("\\.");
					strEmpImage="empphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),employeeImage, strEmpImage,conn);
				}
				if(uAadharImage != null && uAadharImage.getSize() > 0)
				{
					strAadharImage=uAadharImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uAadharImage.getFileName().split("\\.");
					strAadharImage="empuidphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uAadharImage, strAadharImage,conn);
				}
				if(uPanImage != null && uPanImage.getSize() > 0)
				{
					strPanImage=uPanImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uPanImage.getFileName().split("\\.");
					strPanImage="emppanphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uPanImage, strPanImage,conn);
				}
				if(uVoteridImage != null && uVoteridImage.getSize() > 0)
				{
					strVoteridImage=uVoteridImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uVoteridImage.getFileName().split("\\.");
					strVoteridImage="empvidphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uVoteridImage, strVoteridImage,conn);
				}

				if(uUgImage != null && uUgImage.getSize() > 0)
				{
					strUgImage=uUgImage.getFileName();
					DBM.makeProfileSchid(DBM.schoolId(),uUgImage, strUgImage,conn);
				}
				if(uPgImage != null && uPgImage.getSize() > 0)
				{
					strPgImage=uPgImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uPgImage.getFileName().split("\\.");
					strPgImage="emppgphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uPgImage, strPgImage,conn);
				}
				if(uBedImage != null && uBedImage.getSize() > 0)
				{
					strBedImage=uBedImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uBedImage.getFileName().split("\\.");
					strBedImage="empbedphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uBedImage, strBedImage,conn);
				}

				if(uPoliceImage != null && uPoliceImage.getSize() > 0)
				{
					strPoliceImage=uPoliceImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uPoliceImage.getFileName().split("\\.");
					strPoliceImage="emppolicephoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uPoliceImage, strPoliceImage,conn);
				}
				if(uExpImage != null && uExpImage.getSize() > 0)
				{
					strExpImage=uExpImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uExpImage.getFileName().split("\\.");
					strExpImage="empexpphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uExpImage, strExpImage,conn);
				}
				if(uOtherImage != null && uOtherImage.getSize() > 0)
				{
					strOtherImage=uOtherImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uOtherImage.getFileName().split("\\.");
					strOtherImage="empotherphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uOtherImage, strOtherImage,conn);
				}
				if(uDlImage != null && uDlImage.getSize() > 0)
				{
					strDlImage=uDlImage.getFileName();
					String dt = new SimpleDateFormat("yMdhms").format(new Date());
					int rendomNumber=(int)(Math.random()*9000)+1000;
					String exten[]=uDlImage.getFileName().split("\\.");
					strDlImage="empdlphoto_"+ls.getSchid()+dt+String.valueOf(rendomNumber)+"."+exten[exten.length-1];
					DBM.makeProfileSchid(DBM.schoolId(),uDlImage, strDlImage,conn);
				}

				int i=DBM.updateEmployee(id, fname, lname, dob, gender, category, salary, qualification,
						joiningDate, mobile, marital, address, comment, email,mobile2,spouseName,leavingDate,
						strEmpImage,strAadharImage,strPanImage,strVoteridImage,strUgImage,strPgImage,strBedImage,strPoliceImage
						,strExpImage,strOtherImage,conn,dlNo,strDlImage,leave,subCateg,anniDate,daamt,hraamt,employeeUniqueId,
						panNo,aadhaarNo,bankAccountNo,bankName,bankBranch,ifscCode,epfUanNo,esiNo,platform);
				if(i==1)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						// TODO: handle exception
					}
					DBM.updateEmployeeCategoryInAllUser(userid, category, conn,mobile,platform);
					
					String refNo2;
					try {
						refNo2=addWorkLog2(conn);
					} catch (Exception e) {
						// TODO: handle exception
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employee details updated successfully"));
					return "searchEmployee";
				}
				else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
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
		
		}
		return null;
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value = "username-"+userid+" --Category-"+category+" --Mobile-"+mobile ; 
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Update Employee Category","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		String joinDt ="";
		try {
			joinDt = formatter.format(joiningDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String dobDt ="";
		try {
			dobDt = formatter.format(dob);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String leavingDatee ="";
		try {
			leavingDatee = formatter.format(leavingDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		language = "Salary-"+salary+" --Name-"+fname+" --join Date-"+joinDt+" --Dob-"+dobDt+" --Father-"+lname+" --Qualification-"+qualification+" --Marital-"+
				marital+" --Gender-"+gender+" --Email-"+email+" --Comment-"+comment+" --Category-"+category+" --Address-"+address+" --Mobile-"+mobile+" --Mobile2-"+mobile2
				+" --Spouse-"+spouseName+" --Dl No.-"+dlNo+" --Leave-"+leave+" --Sub Categ-"+subCateg+" --Leave Date-"+leavingDatee+" --DA-"+daamt+" --HRA-"+hraamt+" --EmployeeUid-"+employeeUniqueId+" --Pan No-"+panNo+" --Aadhar No-"+
						aadhaarNo+" --Bank Acnt No.-"+bankAccountNo+" --Bank name-"+bankName+" --Bank Branch-"+bankBranch+" --IfSc Code-"+ifscCode+" --EpF Uan No-"+epfUanNo+" --Esi No-"+esiNo;
		
	
	    value = " Emp Image-"+strEmpImage+" --Aadhar Image-"+strAadharImage+" --PAN IMAGE-"+strPanImage+" --VoterId-"+strVoteridImage+" --Ug Image-"+strUgImage+" --PgImage-"+strPgImage+" --Bed. Image-"+strBedImage+" --Police Image-"+strPoliceImage
				+" --Exp Image-"+strExpImage+" --Other Image-"+strOtherImage+" --Dl Image-"+strDlImage;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Employee Admission","WEB",value,conn);
		return refNo;
	}
	
	
	 public void removeProfilePhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"profile",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Profile");
					} catch (Exception e) {
						e.printStackTrace();
					}  
		    	File file = new File(path+previousImage);
				file.delete();
				

				previousImage= ls.getDownloadpath()+"";
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Profile Photo Deleted"));
		 		
		 	
		 		
		    	
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
	 
		public String addWorkLog4(Connection conn,String photo)
		{
		    String value = "";
			String language= "";
			
		    value = "Id-"+id; 
			
			String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,photo+" Photo Remove","WEB",value,conn);
			return refNo;
		}
	 
	 
	 public void removePanPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"pan",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"PAN");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+panImage);
				file.delete();
				

				panImage= ls.getDownloadpath()+"";
				
				
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("PAN Card Photo Deleted"));
		 		
		 	
		 		
		    	
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
	 
	 
	 public void removeVoterPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"voter",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Voter Id");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+voteridImage);
				file.delete();
				

				voteridImage= ls.getDownloadpath()+"";
				
				
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Voter Id Photo Deleted"));
		 		
		 		
		 		
		    	
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
	 
	 public void removeDlPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"dl",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"DL");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+dlImage);
				file.delete();
				

				dlImage= ls.getDownloadpath()+"";
				
				
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("D/L Photo Deleted"));
		 		
		 
		 		
		    	
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
	 
	 
	 
	 public void removeUgPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"ug",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"UG");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+ugImage);
				file.delete();
				

				ugImage= ls.getDownloadpath()+"";
				
				
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("UG Certificate Photo Deleted"));
		 		
		 		
		 		
		    	
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
	 
	 
	 
	 public void removePgPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"pg",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
				try {
					refNo4=addWorkLog4(conn,"PG");
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	File file = new File(path+pgImage);
				file.delete();
				

			pgImage= ls.getDownloadpath()+"";
				
				
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("PG Certificate Photo Deleted"));
		 		
		 	
		 		
		    	
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
	 
	 public void removeAadharPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"aadhar",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Aadhar");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+aadharImage);
				file.delete();
				

			aadharImage= ls.getDownloadpath()+"";
				
				String cert = "Aadhar Card";
				SideMenuBean smb = new SideMenuBean();
				
				if(smb.getList().getCountry().toLowerCase().equalsIgnoreCase("uae"))
				{
					cert = "Emirates ID";
				}
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage(cert+" Photo Deleted"));
		 		
		 	
		 		
		    	
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
	 
	 
	 public void removeBedPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"bed",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"BED");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+bedImage);
				file.delete();
				

				bedImage= ls.getDownloadpath()+"";
				
				String cert = "B.Ed.";
				SideMenuBean smb = new SideMenuBean();
				
				if(smb.getList().getCountry().toLowerCase().equalsIgnoreCase("uae"))
				{
					cert = "PGCE";
				}
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage(cert+" Certificate Photo Deleted"));
		 		
		 	
		 		
		    	
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
	 
	 
	 public void removePolicePhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"police",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Police");
					} catch (Exception e) {
						e.printStackTrace();
					}   
		    	File file = new File(path+policeImage);
				file.delete();
				

				
				policeImage= ls.getDownloadpath()+"";
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Police Verification Photo Deleted"));
		 		
		 		
		 		
		    	
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
	 
	 public void removeExpPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
		 
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"experience",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Mature");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+expImage);
				file.delete();
				

				
				expImage= ls.getDownloadpath()+"";
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Experience Cert. Photo Deleted"));
		 		
		 		
		 		
		    	
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
	 
	
	 public void removeOtherPhoto()
	 {
		 Connection conn = DataBaseConnection.javaConnection();
			
			String schidd = DBM.schoolId();
			SchoolInfoList ls = DBM.fullSchoolInfo(schidd, conn);
			String path = ls.getUploadpath();
			 
		
		       int status = DBM.deleteEmployeePhotos(id,schidd,"other",conn);
			
		       if(status>=1)
		      {
		    	   String refNo4;
					try {
						refNo4=addWorkLog4(conn,"Other");
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	File file = new File(path+otherImage);
				file.delete();
			
				
				otherImage= ls.getDownloadpath()+"";
				
		    	FacesContext context1 = FacesContext.getCurrentInstance();
		 		context1.addMessage(null, new FacesMessage("Other Photo Deleted"));
		 		
		 		
		 		
		    	
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
	 
	 
	
	public void setShowSpouse(boolean showSpouse) {
		this.showSpouse = showSpouse;
	}
	public String getSpouseName() {
		return spouseName;
	}
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	public String getMobile2() {
		return mobile2;
	}
	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	public Date getLeavingDate() {
		return leavingDate;
	}
	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public Date getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(Date joiningDate) {
		this.joiningDate = joiningDate;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMarital() {
		return marital;
	}
	public void setMarital(String marital) {
		this.marital = marital;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public UploadedFile getEmployeeImage() {
		return employeeImage;
	}
	public void setEmployeeImage(UploadedFile employeeImage) {
		this.employeeImage = employeeImage;
	}
	/*	public StreamedContent getPreviousImage() {
		return previousImage;
	}
	public void setPreviousImage(StreamedContent previousImage) {
		this.previousImage = previousImage;
	}
	 */
	public boolean isShowSpouse() {
		return showSpouse;
	}
	public String getPreviousImage() {
		return previousImage;
	}
	public void setPreviousImage(String previousImage) {
		this.previousImage = previousImage;
	}

	public String getEmpImage() {
		return empImage;
	}

	public void setEmpImage(String empImage) {
		this.empImage = empImage;
	}

	public String getAadharImage() {
		return aadharImage;
	}

	public void setAadharImage(String aadharImage) {
		this.aadharImage = aadharImage;
	}

	public String getPanImage() {
		return panImage;
	}

	public void setPanImage(String panImage) {
		this.panImage = panImage;
	}

	public String getVoteridImage() {
		return voteridImage;
	}

	public void setVoteridImage(String voteridImage) {
		this.voteridImage = voteridImage;
	}

	public String getUgImage() {
		return ugImage;
	}

	public void setUgImage(String ugImage) {
		this.ugImage = ugImage;
	}

	public String getPgImage() {
		return pgImage;
	}

	public void setPgImage(String pgImage) {
		this.pgImage = pgImage;
	}

	public String getBedImage() {
		return bedImage;
	}

	public void setBedImage(String bedImage) {
		this.bedImage = bedImage;
	}

	public String getPoliceImage() {
		return policeImage;
	}

	public void setPoliceImage(String policeImage) {
		this.policeImage = policeImage;
	}

	public UploadedFile getuEmpImage() {
		return uEmpImage;
	}

	public void setuEmpImage(UploadedFile uEmpImage) {
		this.uEmpImage = uEmpImage;
	}

	public UploadedFile getuAadharImage() {
		return uAadharImage;
	}

	public void setuAadharImage(UploadedFile uAadharImage) {
		this.uAadharImage = uAadharImage;
	}

	public UploadedFile getuPanImage() {
		return uPanImage;
	}

	public void setuPanImage(UploadedFile uPanImage) {
		this.uPanImage = uPanImage;
	}

	public UploadedFile getuVoteridImage() {
		return uVoteridImage;
	}

	public void setuVoteridImage(UploadedFile uVoteridImage) {
		this.uVoteridImage = uVoteridImage;
	}

	public UploadedFile getuUgImage() {
		return uUgImage;
	}

	public void setuUgImage(UploadedFile uUgImage) {
		this.uUgImage = uUgImage;
	}

	public UploadedFile getuPgImage() {
		return uPgImage;
	}

	public void setuPgImage(UploadedFile uPgImage) {
		this.uPgImage = uPgImage;
	}

	public UploadedFile getuBedImage() {
		return uBedImage;
	}

	public void setuBedImage(UploadedFile uBedImage) {
		this.uBedImage = uBedImage;
	}

	public UploadedFile getuPoliceImage() {
		return uPoliceImage;
	}

	public void setuPoliceImage(UploadedFile uPoliceImage) {
		this.uPoliceImage = uPoliceImage;
	}

	public UploadedFile getuExpImage() {
		return uExpImage;
	}

	public void setuExpImage(UploadedFile uExpImage) {
		this.uExpImage = uExpImage;
	}

	public UploadedFile getuOtherImage() {
		return uOtherImage;
	}

	public void setuOtherImage(UploadedFile uOtherImage) {
		this.uOtherImage = uOtherImage;
	}

	public String getExpImage() {
		return expImage;
	}

	public void setExpImage(String expImage) {
		this.expImage = expImage;
	}

	public String getOtherImage() {
		return otherImage;
	}

	public void setOtherImage(String otherImage) {
		this.otherImage = otherImage;
	}

	public String getDlImage() {
		return dlImage;
	}

	public void setDlImage(String dlImage) {
		this.dlImage = dlImage;
	}

	public String getStrDlImage() {
		return strDlImage;
	}

	public void setStrDlImage(String strDlImage) {
		this.strDlImage = strDlImage;
	}

	public UploadedFile getuDlImage() {
		return uDlImage;
	}

	public void setuDlImage(UploadedFile uDlImage) {
		this.uDlImage = uDlImage;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public double getLeave() {
		return leave;
	}

	public void setLeave(double leave) {
		this.leave = leave;
	}

	public String getSubCateg() {
		return subCateg;
	}

	public void setSubCateg(String subCateg) {
		this.subCateg = subCateg;
	}

	public Date getAnniDate() {
		return anniDate;
	}

	public void setAnniDate(Date anniDate) {
		this.anniDate = anniDate;
	}

	public double getDaamt() {
		return daamt;
	}

	public void setDaamt(double daamt) {
		this.daamt = daamt;
	}

	public double getHraamt() {
		return hraamt;
	}

	public void setHraamt(double hraamt) {
		this.hraamt = hraamt;
	}

	public String getEmployeeUniqueId() {
		return employeeUniqueId;
	}

	public void setEmployeeUniqueId(String employeeUniqueId) {
		this.employeeUniqueId = employeeUniqueId;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getEpfUanNo() {
		return epfUanNo;
	}

	public void setEpfUanNo(String epfUanNo) {
		this.epfUanNo = epfUanNo;
	}

	public String getEsiNo() {
		return esiNo;
	}

	public void setEsiNo(String esiNo) {
		this.esiNo = esiNo;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	
}
