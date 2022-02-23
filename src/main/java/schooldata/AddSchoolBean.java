package schooldata;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="addSchool")
@ViewScoped
public class AddSchoolBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String folder,adminAppKey,commonAppKey,schoolName,aliasName,contactNo,contactName,type,gps,empusername,password,session,schoolSession,feeStart,clientType,timetable;
	Date startDate=new Date(),expireDate=new Date(),renewalDate=new Date(),date=new Date();
	ArrayList<SelectItem>staffCategoryList=new ArrayList<>(), branchList = new ArrayList<>();
	double chalkBoxAmount,imgAmount=0,chalkBoxRenewal=0,imgRenewalAmount;
	int noOfStudents=0,msz;
	ArrayList<SelectItem> sessionList;
	String senderid="",appType,strRandomOtp,otpInput,country,state,district,zone,branchid,admNoDupl;
	int randomOtp,noOfStudent;
	boolean adminLogin=false,teacherLogin=false,studentApp=false,academicLogin=false,authorityLogin=false,principalLogin=false;
	boolean frontLogin=false,libraryLogin=false,attendantLogin=false,transportLogin=false,securityLogin=false,otherLogin=false;
	public AddSchoolBean()
	{
		renewalDate.setYear(new Date().getYear()+1);
		expireDate.setYear(new Date().getYear()+1);

		adminAppKey = "AAAAaoFmDCc:APA91bFSCXCDvpPPN70s8v_d83f2p6EgJVkGOs_JIsg6muf5RUx1YQNFRxgNUP8jZjCwePaDEsx3KEDX3tynYMHq_ZI0qsGCZOoMipS2nc5C42JzeWkyCIu2rd5iTKN-elmUBH3ggDnt";
		commonAppKey = "AAAAhq4pdFc:APA91bFT4x0BUzBn6TnCCxNyMUTy34ZFIajgUAhXzyvySMbZ-_xiDGm_UqiQm1Ldt2D92DDzA4A93rXHq7ymwSMxKyKuUGZT2TscxcXailPqzAJewWGpwnpUOP4X0fGB25lrocSYJGhP";
		sessionList=new ArrayList<>();
		for(int i=2015;i<=2050;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;

		if(month>=4)
		{
			session=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			session=String.valueOf(year-1)+"-"+String.valueOf(year);
		}

		branchid = "";
		gps="no";
		type="novice";
		schoolSession="4-3";
		feeStart="admission_date";
		clientType="school";
		timetable="Image";
		appType="";
		Connection conn=DataBaseConnection.javaConnection();
		branchList = new ArrayList<>();
		staffCategoryList=new DatabaseMethods1().allStaffCategory(conn);
		branchList = new DatabaseMethods1().allBranch(conn);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendOTP()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		boolean check = DBM.checkLoginDuplicacy(empusername, conn);
		if(check==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Admin User Name Found"));
		}
		else
		{
			boolean duplicate=DBM.checkDuplicateSchool(aliasName,conn);
			if(duplicate==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Alias Name Found"));
			}
			else
			{
				if(studentApp && appType.equals(""))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select App Type"));
				}
				else if(expireDate.after(renewalDate))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Expiry Date Must Be Before Renewal Date"));
				}
				else if(startDate.after(expireDate) || startDate.after(renewalDate))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Start Date Must Be Before Expiry/Renewal Date"));
				}
				else if(senderid.length()!=6)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sender ID Must Be of 6 Characters"));
				}
				else if(senderid.contains(" "))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Spaces Are Not Allowed in Sender ID"));
				}
				else if(folder.contains(" "))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Spaces Are Not Allowed in Directory Name"));
				}
				else
				{
					PrimeFaces.current().executeInitScript("PF('addSchool').show()");
					PrimeFaces.current().ajax().update("form2");
					strRandomOtp = "";
					randomOtp = (int)(Math.random()*9000)+1000;
					strRandomOtp = String.valueOf(randomOtp);
                     // System.out.println(strRandomOtp);
					String typemessage="Hello Admin,\n"
							+String.valueOf(randomOtp)+ " is  your OTP to ADD NEW SCHOOL - "+schoolName+".\nRegards.\nCB";
					String tempcontactNo=new DatabaseMethods1().contactNo("Add School",conn);
					new DatabaseMethods1().messageurlMasterAdmin(tempcontactNo, typemessage/*,"masteradmin",conn*/);
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Sent. Please Enter OTP."));
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void addSchool()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(otpInput.equals(strRandomOtp))
		{
			Path path = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder);
			//Path path = Paths.get("/Users/neerajrajput/Documents/"+folder);
			//Path path = Paths.get("D:/Chalk/"+folder);

			if(!Files.exists(path))
			{
				try
				{
					Files.createDirectories(path);
					Set<PosixFilePermission> perms = Files.readAttributes(path,PosixFileAttributes.class).permissions();

					// // System.out.format("Permissions before: %s%n",  PosixFilePermissions.toString(perms));

					perms.add(PosixFilePermission.OWNER_WRITE);
					perms.add(PosixFilePermission.OWNER_READ);
					perms.add(PosixFilePermission.OWNER_EXECUTE);
					perms.add(PosixFilePermission.GROUP_WRITE);
					perms.add(PosixFilePermission.GROUP_READ);
					perms.add(PosixFilePermission.GROUP_EXECUTE);
					perms.add(PosixFilePermission.OTHERS_WRITE);
					perms.add(PosixFilePermission.OTHERS_READ);
					perms.add(PosixFilePermission.OTHERS_EXECUTE);
					Files.setPosixFilePermissions(path, perms);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				
//				Path pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/SchoolDocuments");
//				//Path pathFolder = Paths.get("D:/Chalk/"+folder+"/SchoolDocuments");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/StudentDocuments");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/StudentDocuments");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Homework");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Homework");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/News");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/News");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Gallery");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Gallery");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Syllabus");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Syllabus");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Employee");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Employee");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/AdmitCard");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/AdmitCard");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Marksheet");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Marksheet");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/ELearning");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/ELearning");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/PostConsent");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/PostConsent");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/TimeTable");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/TimeTable");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/SchoolInfo");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/SchoolInfo");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/StudentPhoto");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/StudentPhoto");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Library");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Library");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				pathFolder = Paths.get("/var/sentora/hostdata/chalkbox/public_html/"+folder+"/Visitor");
//				//pathFolder = Paths.get("D:/Chalk/"+folder+"/Visitor");
//
//				if(!Files.exists(pathFolder))
//				{
//					try
//					{
//						Files.createDirectories(pathFolder);
//						Set<PosixFilePermission> perms = Files.readAttributes(pathFolder,PosixFileAttributes.class).permissions();
//
//						perms.add(PosixFilePermission.OWNER_WRITE);
//						perms.add(PosixFilePermission.OWNER_READ);
//						perms.add(PosixFilePermission.OWNER_EXECUTE);
//						perms.add(PosixFilePermission.GROUP_WRITE);
//						perms.add(PosixFilePermission.GROUP_READ);
//						perms.add(PosixFilePermission.GROUP_EXECUTE);
//						perms.add(PosixFilePermission.OTHERS_WRITE);
//						perms.add(PosixFilePermission.OTHERS_READ);
//						perms.add(PosixFilePermission.OTHERS_EXECUTE);
//						Files.setPosixFilePermissions(pathFolder, perms);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
				

				if(branchid.equalsIgnoreCase("new"))
				{
					int id  = DBM.createBranch(schoolName,conn);
					if(id>=1)
					{
						String bid = String.valueOf(id);
						update(bid);
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong in New Branch Creation. Please Try Again."));
					}
				}
				else
				{
					update(branchid);
				}



			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Directory already present. Please Try Another Directory Name"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid OTP. Please Try Again."));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void update(String bid)
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String uploadPath = "/var/sentora/hostdata/chalkbox/public_html/"+folder+"/";
		String downloadPath = "http://www.chalkboxerp.in/"+folder+"/";

		if(!appType.equalsIgnoreCase("common"))
		{
			commonAppKey="";
		}
		
		int i=DBM.addSchool(schoolName,aliasName,startDate,expireDate,renewalDate,chalkBoxAmount,imgAmount,chalkBoxRenewal,
				imgRenewalAmount,contactNo,contactName,noOfStudents,msz,type,conn,gps,empusername,password,session,
				clientType,timetable,adminLogin,teacherLogin,appType,studentApp,country,state,district,zone,noOfStudent,bid,
				academicLogin,authorityLogin,principalLogin,frontLogin,libraryLogin,attendantLogin,transportLogin,securityLogin,otherLogin);
		if(i>0 )
		{
			DBM.addAdminInAllUser(empusername,password,"admin",conn,contactNo,String.valueOf(i));
			DBM.addPayment(String.valueOf(i),"Initial",date,chalkBoxAmount,"debit",conn);
			DBM.addMessage(String.valueOf(i),date,msz,session,conn);
			DBM.insertNewSchool(schoolName, "", "", "","", contactNo, "","", "", "","", 3,"SCHOOL COPY", "OFFICE COPY",
					"PARENT COPY",String.valueOf(i),type,conn,"no","",schoolSession,feeStart,senderid,"",studentApp,
					adminLogin,teacherLogin,adminAppKey,commonAppKey,uploadPath,downloadPath,country,session,admNoDupl);

			DBM.InsertMessageMessage(String.valueOf(i),conn);
			//DBM.checkConnsessiontype("GENERAL",String.valueOf(i),conn);
			DBM.insertConnsessionType("GENERAL",String.valueOf(i),conn);
			DBM.insertTuitionFee(String.valueOf(i),conn);
			DBM.insertStaffCategory(staffCategoryList,String.valueOf(i),conn);

//			DataBaseMethodsReports dbr = new DataBaseMethodsReports();
//			ArrayList<Transport> nlist = new ArrayList<>();
//			for(int x=1;x<=12;x++)
//			{
//				Transport tt = new Transport();
//				tt.setMonth(String.valueOf(x));
//				tt.setStatus("Yes");
//				nlist.add(tt);
//			}
//
//			dbr.copyTransportWaiveEntry(nlist,String.valueOf(i),session,conn);

			branchList = new DatabaseMethods1().allBranch(conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Added Successfully"));
			schoolName=aliasName=contactName=contactNo=empusername=password=appType="";chalkBoxAmount=chalkBoxRenewal=imgAmount=imgRenewalAmount=0;
			startDate=expireDate=renewalDate=new Date();
			noOfStudents=msz=0;
			gps="no";
			type="novice";
			schoolSession="4-3";
			feeStart="admission_date";
			clientType="school";
			senderid="";
			adminLogin=teacherLogin=studentApp=false;
			otpInput="";
			branchid="";
			admNoDupl="";
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
	}

	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
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
	public double getChalkBoxAmount() {
		return chalkBoxAmount;
	}
	public void setChalkBoxAmount(double chalkBoxAmount) {
		this.chalkBoxAmount = chalkBoxAmount;
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


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getGps() {
		return gps;
	}


	public void setGps(String gps) {
		this.gps = gps;
	}


	public String getEmpusername() {
		return empusername;
	}


	public void setEmpusername(String empusername) {
		this.empusername = empusername;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getSession() {
		return session;
	}


	public void setSession(String session) {
		this.session = session;
	}


	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}


	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
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


	public String getClientType() {
		return clientType;
	}


	public void setClientType(String clientType) {
		this.clientType = clientType;
	}


	public String getTimetable() {
		return timetable;
	}


	public void setTimetable(String timetable) {
		this.timetable = timetable;
	}


	public String getSenderid() {
		return senderid;
	}


	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}


	public boolean isAdminLogin() {
		return adminLogin;
	}


	public void setAdminLogin(boolean adminLogin) {
		this.adminLogin = adminLogin;
	}


	public boolean isTeacherLogin() {
		return teacherLogin;
	}


	public void setTeacherLogin(boolean teacherLogin) {
		this.teacherLogin = teacherLogin;
	}


	public boolean isStudentApp() {
		return studentApp;
	}


	public void setStudentApp(boolean studentApp) {
		this.studentApp = studentApp;
	}


	public String getAppType() {
		return appType;
	}


	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getOtpInput() {
		return otpInput;
	}

	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
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

	public int getNoOfStudent() {
		return noOfStudent;
	}

	public void setNoOfStudent(int noOfStudent) {
		this.noOfStudent = noOfStudent;
	}

	public String getBranchid() {
		return branchid;
	}

	public void setBranchid(String branchid) {
		this.branchid = branchid;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public boolean isAcademicLogin() {
		return academicLogin;
	}

	public void setAcademicLogin(boolean academicLogin) {
		this.academicLogin = academicLogin;
	}

	public boolean isAuthorityLogin() {
		return authorityLogin;
	}

	public void setAuthorityLogin(boolean authorityLogin) {
		this.authorityLogin = authorityLogin;
	}

	public boolean isPrincipalLogin() {
		return principalLogin;
	}

	public void setPrincipalLogin(boolean principalLogin) {
		this.principalLogin = principalLogin;
	}

	public boolean isFrontLogin() {
		return frontLogin;
	}

	public void setFrontLogin(boolean frontLogin) {
		this.frontLogin = frontLogin;
	}

	public boolean isLibraryLogin() {
		return libraryLogin;
	}

	public void setLibraryLogin(boolean libraryLogin) {
		this.libraryLogin = libraryLogin;
	}

	public boolean isAttendantLogin() {
		return attendantLogin;
	}

	public void setAttendantLogin(boolean attendantLogin) {
		this.attendantLogin = attendantLogin;
	}

	public boolean isTransportLogin() {
		return transportLogin;
	}

	public void setTransportLogin(boolean transportLogin) {
		this.transportLogin = transportLogin;
	}

	public boolean isSecurityLogin() {
		return securityLogin;
	}

	public void setSecurityLogin(boolean securityLogin) {
		this.securityLogin = securityLogin;
	}

	public boolean isOtherLogin() {
		return otherLogin;
	}

	public void setOtherLogin(boolean otherLogin) {
		this.otherLogin = otherLogin;
	}

	public String getAdmNoDupl() {
		return admNoDupl;
	}

	public void setAdmNoDupl(String admNoDupl) {
		this.admNoDupl = admNoDupl;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


	
	
	
}
