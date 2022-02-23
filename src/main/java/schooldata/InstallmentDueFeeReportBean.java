package schooldata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="installmentDueFeeReportBean")
@ViewScoped
public class InstallmentDueFeeReportBean implements Serializable
{
	String regex=RegexPattern.REGEX;
    String name,firstMonthTutionFee,lastMonthTutionFee,selectedCLassSection,selectedSection,dateString;
	int totalStudent;
	ArrayList<SelectItem> classSection,sectionList,sessionList,branchList;
	ArrayList<StudentInfo> studentList,selectedStudentList,studentList1;
	String regFee,tutionFee,admissionFee,examFee,annualFee,transFee,artFee,activityFee,termFee,totalFee,tutionFeePeriod,studentName,fathersName;
	StudentInfo selectedStudent;
	static int count=1;
	String selectedSession,selectedSession2,month,sectionName,className,typeMessage;
	Date date;
	String selectedMonth;
	boolean show, showBlock;
	int totalAmount;
	ArrayList<SelectItem> concessionlist = new ArrayList<>();
	String selectedConcession;
	ArrayList<ClassInfo> classFeeList;
	ArrayList<FeeStatus> feeStatus;
	ArrayList<FeeInfo> feelist = new ArrayList<>();
	String message="",blockMsg="",totalamountString;
	double totalDueAmount;
	ArrayList<StudentInfo>list;
	String[] checkMonthSelected;
	DatabaseMethods1 obj=new DatabaseMethods1();
    boolean showClass,showSchool;
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches;
	String newschid;
	
	transient StreamedContent file;
	ArrayList<SelectItem> installmentList,selectedInstallList;
	String schoolid,userType,balMsg;
	double smsLimit;
	String session;
	ArrayList<FeeDynamicList>feelist1,feeListTest;
	FeeDynamicList selectedInstallmentList;
	SchoolInfoList schoolDetails;
	String[] selectedModule;
	ArrayList<SelectItem> allMods = new ArrayList<SelectItem>();
	
   public InstallmentDueFeeReportBean() {
	        selectedCLassSection=selectedSection="-1";
	        Connection conn= DataBaseConnection.javaConnection();
	        allMods = obj.appPermissions(obj.schoolId(), "school_student_permission", conn);
	        schoolDetails =obj.fullSchoolInfo(conn);
	        schoolid=new DatabaseMethods1().schoolId();
	   		session=DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);
	        schid=schoolid;
	        smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
	        feelist1=new DatabaseMethods1().getAllInstallment(schoolid,conn);
            installmentList=new DatabaseMethods1().getAllSelectedInstallment(schoolid,conn);
			classSection=new DatabaseMethods1().allClass(schoolid,conn);
			concessionlist = new DatabaseMethods1().allConnsessionType(conn);
			HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			userType = (String) httpSession.getAttribute("type");
			
			String defSession = new DatabaseMethods1().checkDefaultSession(schid, conn);
			if(session.equals(defSession))
			{
				showBlock = true;
			}
			else
			{
				showBlock = true;
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	   
   }
   
   public void allSections()
	{
		sectionList = new ArrayList<>();
		selectedSection="-1";
		if(!selectedCLassSection.equals("-1"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			sectionList=obj.allSection(schid,selectedCLassSection,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
   
   public void copyBlockStuff() 
   {
	   for(StudentInfo ss : list)
	   {
		   ss.setBlockModList(selectedModule);
		   ss.setMsg(blockMsg);
	   }
	}

   public String sendMessageToSelectedStudents() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList scinfo=new DatabaseMethods1().fullSchoolInfo(conn);
		DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
		if(scinfo.getCountry().equalsIgnoreCase("India"))
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin"))
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
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}

			if(selectedStudentList.size()>0)
			{

				//SchoolInfoList list1=obj.fullSchoolInfo(conn);
				String message="";
				String msg="";
				String notifyMsg = "";
				for(StudentInfo list : selectedStudentList)
				{
					 
					 studentList1=new DatabaseMethods1().searchStudentListForDueFeeReport(list.getAddNumber(),date,session,conn,"dueReport","active");
					
					notifyMsg="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTotalFee()+" upto  the month of "+studentList1.get(0).month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
					//notifyMsg= "Dear "+list.getFname() + ",\nYour fee is due for month "+month+" is as - "+list.getTutionFeeDueAmount()+ ".\nRegards,\n"+scinfo.getSmsSchoolName();
					DBJ.notification("Fees Due",notifyMsg, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
					DBJ.notification("Fees Due",notifyMsg, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

					if(String.valueOf(list.getSendMessageMobileNo()).length()==10
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
					{
							
							message="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTotalFee()+" upto  the month of "+studentList1.get(0).month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
							
						msg=message;
						
						obj.messageurl1(list.getSendMessageMobileNo(), msg,list.getAddNumber(),conn,obj.schoolId(),"");

					}
				}
				//Date date=new Date();
				//	DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), msg, list.getFathersPhone(), date,list.getClassId(),"Student");
				message="";


				FacesContext fc=FacesContext.getCurrentInstance();
				if(selectedStudentList.size()>1)
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" students "));
				}
				else
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" student"));
				}

				name=null;date=null;selectedSection=null;selectedCLassSection=null;
				sectionList=null;show=false;
				selectedStudentList=new ArrayList<>();studentList=new ArrayList<>();
				className=sectionName=month="";
				
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			if(selectedStudentList.size()>0)
			{
				ArrayList<StudentInfo> newList = selectedStudentList;
				Runnable r = new Runnable()
				{
					public void run()
					{
						String heading = "<center class=\"red\">Message From "+scinfo.getSchoolName()+"!</center>";
						String subject = "Message From "+scinfo.getSchoolName();
						String msg = "";
						String notifyMsg = "";
						for(StudentInfo info : newList)
						{
							 studentList1=new DatabaseMethods1().searchStudentListForDueFeeReport(info.getAddNumber(),date,session,conn,"dueReport","active");
								
								notifyMsg="Dear Parents,\nThe pending fee of your ward "+info.getFname()+" of class "+info.getClassName()+" is Rs. "+info.getTotalFee()+" upto  the month of "+studentList1.get(0).month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
								DBJ.notification("Fees Due",notifyMsg, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
							DBJ.notification("Fees Due",notifyMsg, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

							msg="<center>Dear "+info.getFname() + ",<br></br>Your fee is due for month "+month+" is as - "+info.getTotalFee()+ ". <br></br>Regards,<br></br>"+scinfo.getSmsSchoolName()+"</center>";

							if(info.getActionBy().equalsIgnoreCase("father"))
							{
								new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
							}
							else if(info.getActionBy().equalsIgnoreCase("mother"))
							{
								new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
							}
							else if(info.getActionBy().equalsIgnoreCase("both"))
							{
								if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
								else
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
								}
							}
							else
							{
								new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
							}
						}
						
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				};
				new Thread(r).start();

				FacesContext fc=FacesContext.getCurrentInstance();
				if(newList.size()>1)
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+newList.size()+" students "));
				}
				else
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+newList.size()+" student"));
				}

				name=null;date=null;selectedSection=null;selectedCLassSection=null;
				sectionList=null;show=false;
				selectedStudentList=new ArrayList<>();studentList=new ArrayList<>();
				className=sectionName=month="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return "";
	}

   public String sendMessageToSelectedStudentsCustome() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
		SchoolInfoList scinfo=new DatabaseMethods1().fullSchoolInfo(conn);
		if(scinfo.getCountry().equalsIgnoreCase("India"))
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg1').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm1");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (userType.equalsIgnoreCase("admin"))
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
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}

			if(selectedStudentList.size()>0)
				
			{
				String cnt="",addNumber="";
				for(StudentInfo list : selectedStudentList)
				{

					DBJ.notification("Fees Due",message, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
					DBJ.notification("Fees Due",message, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

					if(String.valueOf(list.getSendMessageMobileNo()).length()==10
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
					{
						if(cnt.equals(""))
						{
							cnt=list.getSendMessageMobileNo();

						}
						else
						{
							cnt=cnt+","+list.getSendMessageMobileNo();

						}

						if (addNumber.equals("")) {
							addNumber = list.getAddNumber();

						} else {
							addNumber = addNumber + "," + list.getAddNumber();
						}

					}
					//Date date=new Date();
					//DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), message, list.getFathersPhone(), date,list.getClassId(),"Student");
				}

				obj.messageurl1(cnt, message, addNumber,conn,obj.schoolId(),"");
				message="";

				FacesContext fc=FacesContext.getCurrentInstance();
				if(selectedStudentList.size()>1)
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" students "));
				}
				else
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" student"));
				}

				name=null;date=null;selectedSection=null;selectedCLassSection=null;
				sectionList=null;show=false;
				selectedStudentList=null;studentList=null;
				className=sectionName=month="";
				
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			if(selectedStudentList.size()>0)
			{
				String tp = message;
				ArrayList<StudentInfo> newList = selectedStudentList;
				Runnable r = new Runnable()
				{
					public void run()
					{
						String heading = "<center class=\"red\">Message From "+scinfo.getSchoolName()+"!</center>";
						String subject = "Message From "+scinfo.getSchoolName();
						String msg="<center>"+tp+"</center>";
						for(StudentInfo info : newList)
						{
							DBJ.notification("Fees Due",tp, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
							DBJ.notification("Fees Due",tp, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

							if(info.getActionBy().equalsIgnoreCase("father"))
							{
								new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
							}
							else if(info.getActionBy().equalsIgnoreCase("mother"))
							{
								new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
							}
							else if(info.getActionBy().equalsIgnoreCase("both"))
							{
								if(info.getFatherEmail().equalsIgnoreCase(info.getMotherEmail()))
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
								}
								else
								{
									new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
									new DataBaseOnlineAdm().doMail(info.getMotherEmail(), subject, heading, msg);
								}
							}
							else
							{
								new DataBaseOnlineAdm().doMail(info.getFatherEmail(), subject, heading, msg);
							}
						}
						
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					}
				};
				new Thread(r).start();

				message="";

				FacesContext fc=FacesContext.getCurrentInstance();
				if(newList.size()>1)
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+newList.size()+" students "));
				}
				else
				{
					fc.addMessage(null, new FacesMessage("You have sent message to "+newList.size()+" student"));
				}

				name=null;date=null;selectedSection=null;selectedCLassSection=null;
				sectionList=null;show=false;
				selectedStudentList=new ArrayList<>();studentList=new ArrayList<>();
				className=sectionName=month="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return "";
	}

   public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
		if(selectedStudentList.size()>0)
		{

			SchoolInfoList list1=obj.fullSchoolInfo(conn);
			String message="";
			String msg="";
			String notifyMsg = "";
			for(StudentInfo list : selectedStudentList)
			{
				 studentList1=new DatabaseMethods1().searchStudentListForDueFeeReport(list.getAddNumber(),date,session,conn,"dueReport","active");
					
					notifyMsg="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTotalFee()+" upto  the month of "+studentList1.get(0).month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+list1.getSmsSchoolName();
					
				DBJ.notification("Fees Due",notifyMsg, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+list1.getSchid(),list1.getSchid(),"",conn);
				DBJ.notification("Fees Due",notifyMsg, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+list1.getSchid(),list1.getSchid(),"",conn);

				if(String.valueOf(list.getSendMessageMobileNo()).length()==10
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
				{
						
					 message="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTotalFee()+" upto  the month of "+studentList1.get(0).month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+list1.getSmsSchoolName();
						
					
					msg=message;
					obj.messageurl1(list.getSendMessageMobileNo(), msg,list.getAddNumber(),conn,obj.schoolId(),"");

				}
			}
			//Date date=new Date();
			//	DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), msg, list.getFathersPhone(), date,list.getClassId(),"Student");
			message="";


			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
   
   public void sendMsgCustom()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
		DatabaseMethods1 dbm = new DatabaseMethods1();
		if(selectedStudentList.size()>0)
		{
			String cnt="",addNumber="";
			for(StudentInfo list : selectedStudentList)
			{
				DBJ.notification("Fees Due",message, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+dbm.schoolId(),dbm.schoolId(),"",conn);
				DBJ.notification("Fees Due",message, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+dbm.schoolId(),dbm.schoolId(),"",conn);

				if(String.valueOf(list.getSendMessageMobileNo()).length()==10
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
				{
					if(cnt.equals(""))
					{
						cnt=list.getSendMessageMobileNo();

					}
					else
					{
						cnt=cnt+","+list.getSendMessageMobileNo();

					}

					if (addNumber.equals("")) {
						addNumber = list.getAddNumber();

					} else {
						addNumber = addNumber + "," + list.getAddNumber();
					}

				}
				//Date date=new Date();
				//DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), message, list.getFathersPhone(), date,list.getClassId(),"Student");
			}

			obj.messageurl1(cnt, message, addNumber,conn,obj.schoolId(),"");
			message="";

			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

   public void showReport()
   {
	   
	   
	   Connection conn = DataBaseConnection.javaConnection();
	
	   feeListTest=new ArrayList<>();
	   selectedStudentList = new ArrayList<StudentInfo>();
	   
	   
	   for(int i=0;i<checkMonthSelected.length;i++)
	   {
		   for(FeeDynamicList ls:feelist1)
		   {
			   if(ls.getInsatllmentValue().equals(checkMonthSelected[i]))
			   {
				   feeListTest.add(ls);
				   break;
			   }
		   }	   
	   }
	   
	  
	   
	   
	   String sectionid="";
		if(selectedCLassSection.equals("-1"))
		{
			sectionid="-1";
		}
		else if(selectedSection.equals("-1"))
		{
			for(SelectItem ii : sectionList)
			{
				if(sectionid.equals(""))
				{
					sectionid = String.valueOf(ii.getValue());
				}
				else
				{
					sectionid = sectionid + "','" + String.valueOf(ii.getValue());
				}
			}
			
			sectionid="('"+sectionid+"')";
			
		}
		else  
		{
			sectionid = "('"+selectedSection+"')";
		}
		
	   list=new DatabaseMethods1().dueFeesForOtherForSession(obj.schoolId(), date, selectedSection, feeListTest,selectedConcession,selectedCLassSection,conn);

	   double total = 0.0;	   
	   for(StudentInfo ss: list)
	   {
		   total += ss.getTotalFee();
	   }
	   totalamountString = String.valueOf(total);
	   String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
		String[] dd=stdate.split("/");
		Date extraDate=date;
		extraDate.setDate(15);
		String date11=new SimpleDateFormat("dd-MM-yy").format(extraDate);

		message="Dear Parent \n This is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+".Kindly ignore if already paid \n Thanks \n"+schoolDetails.getSmsSchoolName()  ;

	   
	   if(list.size()>0)
	   {
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
   
   public void updateAction()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String schoolId = obj.schoolId();
		if(selectedStudentList.size()>0)
		{
			int i = 0,amount=0,x=0;
			for(StudentInfo ls:selectedStudentList)
			{
				amount = Integer.valueOf(new DecimalFormat("##").format(ls.getTotalFee()));
				
				i=obj.blockStudentAppMods(ls.getAddNumber(),"Fees Block",ls.getBlockModList(),schoolId,amount,ls.getMsg(),"manual",conn);
				if(i>=1)
				{
					x+=1;
				}
			}
			
			if(x>=1)
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Block/UnBlock Modules Updated Successfully!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Something went wrong. Please try again! ", "Validation error"));
			}
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
   
   public void toNum(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);
		
		XSSFRow r2 =     sheet.createRow(0);
		XSSFRow headerR = sheet.getRow(0);
		 headerR.setHeight((short)1200);
		

		try
		{

			URL url = new URL(schoolDetails.getDownloadpath()+schoolDetails.getMarksheetHeader());
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(0);
			my_anchor.setCol2(6);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);






		} catch (IOException ioex) {

		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);


	}
   
   
public String getUserType() {
	return userType;
}

public void setUserType(String userType) {
	this.userType = userType;
}

public String getBalMsg() {
	return balMsg;
}

public void setBalMsg(String balMsg) {
	this.balMsg = balMsg;
}

public double getSmsLimit() {
	return smsLimit;
}

public void setSmsLimit(double smsLimit) {
	this.smsLimit = smsLimit;
}

public FeeDynamicList getSelectedInstallmentList() {
	return selectedInstallmentList;
}

public void setSelectedInstallmentList(FeeDynamicList selectedInstallmentList) {
	this.selectedInstallmentList = selectedInstallmentList;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getFirstMonthTutionFee() {
	return firstMonthTutionFee;
}

public void setFirstMonthTutionFee(String firstMonthTutionFee) {
	this.firstMonthTutionFee = firstMonthTutionFee;
}

public String getLastMonthTutionFee() {
	return lastMonthTutionFee;
}

public void setLastMonthTutionFee(String lastMonthTutionFee) {
	this.lastMonthTutionFee = lastMonthTutionFee;
}

public String getSelectedCLassSection() {
	return selectedCLassSection;
}

public void setSelectedCLassSection(String selectedCLassSection) {
	this.selectedCLassSection = selectedCLassSection;
}

public String getSelectedSection() {
	return selectedSection;
}

public void setSelectedSection(String selectedSection) {
	this.selectedSection = selectedSection;
}

public String getDateString() {
	return dateString;
}

public void setDateString(String dateString) {
	this.dateString = dateString;
}

public int getTotalStudent() {
	return totalStudent;
}

public void setTotalStudent(int totalStudent) {
	this.totalStudent = totalStudent;
}

public ArrayList<SelectItem> getClassSection() {
	return classSection;
}

public void setClassSection(ArrayList<SelectItem> classSection) {
	this.classSection = classSection;
}

public ArrayList<SelectItem> getSectionList() {
	return sectionList;
}

public void setSectionList(ArrayList<SelectItem> sectionList) {
	this.sectionList = sectionList;
}

public ArrayList<SelectItem> getSessionList() {
	return sessionList;
}

public void setSessionList(ArrayList<SelectItem> sessionList) {
	this.sessionList = sessionList;
}

public ArrayList<SelectItem> getBranchList() {
	return branchList;
}

public void setBranchList(ArrayList<SelectItem> branchList) {
	this.branchList = branchList;
}

public ArrayList<StudentInfo> getStudentList() {
	return studentList;
}

public void setStudentList(ArrayList<StudentInfo> studentList) {
	this.studentList = studentList;
}

public ArrayList<StudentInfo> getSelectedStudentList() {
	return selectedStudentList;
}

public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
	this.selectedStudentList = selectedStudentList;
}

public String getRegFee() {
	return regFee;
}

public void setRegFee(String regFee) {
	this.regFee = regFee;
}

public String getTutionFee() {
	return tutionFee;
}

public void setTutionFee(String tutionFee) {
	this.tutionFee = tutionFee;
}

public String getAdmissionFee() {
	return admissionFee;
}

public void setAdmissionFee(String admissionFee) {
	this.admissionFee = admissionFee;
}

public String getExamFee() {
	return examFee;
}

public void setExamFee(String examFee) {
	this.examFee = examFee;
}

public String getAnnualFee() {
	return annualFee;
}

public void setAnnualFee(String annualFee) {
	this.annualFee = annualFee;
}

public String getTransFee() {
	return transFee;
}

public void setTransFee(String transFee) {
	this.transFee = transFee;
}

public String getArtFee() {
	return artFee;
}

public void setArtFee(String artFee) {
	this.artFee = artFee;
}

public String getActivityFee() {
	return activityFee;
}

public void setActivityFee(String activityFee) {
	this.activityFee = activityFee;
}

public String getTermFee() {
	return termFee;
}

public void setTermFee(String termFee) {
	this.termFee = termFee;
}

public String getTotalFee() {
	return totalFee;
}

public void setTotalFee(String totalFee) {
	this.totalFee = totalFee;
}

public String getTutionFeePeriod() {
	return tutionFeePeriod;
}

public void setTutionFeePeriod(String tutionFeePeriod) {
	this.tutionFeePeriod = tutionFeePeriod;
}

public String getStudentName() {
	return studentName;
}

public void setStudentName(String studentName) {
	this.studentName = studentName;
}

public String getFathersName() {
	return fathersName;
}

public void setFathersName(String fathersName) {
	this.fathersName = fathersName;
}

public StudentInfo getSelectedStudent() {
	return selectedStudent;
}

public void setSelectedStudent(StudentInfo selectedStudent) {
	this.selectedStudent = selectedStudent;
}

public static int getCount() {
	return count;
}

public static void setCount(int count) {
	InstallmentDueFeeReportBean.count = count;
}

public String getSelectedSession() {
	return selectedSession;
}

public void setSelectedSession(String selectedSession) {
	this.selectedSession = selectedSession;
}

public String getSelectedSession2() {
	return selectedSession2;
}

public void setSelectedSession2(String selectedSession2) {
	this.selectedSession2 = selectedSession2;
}

public String getMonth() {
	return month;
}

public void setMonth(String month) {
	this.month = month;
}

public String getSectionName() {
	return sectionName;
}

public void setSectionName(String sectionName) {
	this.sectionName = sectionName;
}

public String getClassName() {
	return className;
}

public void setClassName(String className) {
	this.className = className;
}

public String getTypeMessage() {
	return typeMessage;
}

public void setTypeMessage(String typeMessage) {
	this.typeMessage = typeMessage;
}

public Date getDate() {
	return date;
}

public void setDate(Date date) {
	this.date = date;
}

public String getSelectedMonth() {
	return selectedMonth;
}

public void setSelectedMonth(String selectedMonth) {
	this.selectedMonth = selectedMonth;
}

public boolean isShow() {
	return show;
}

public void setShow(boolean show) {
	this.show = show;
}

public int getTotalAmount() {
	return totalAmount;
}

public void setTotalAmount(int totalAmount) {
	this.totalAmount = totalAmount;
}

public ArrayList<ClassInfo> getClassFeeList() {
	return classFeeList;
}

public void setClassFeeList(ArrayList<ClassInfo> classFeeList) {
	this.classFeeList = classFeeList;
}

public ArrayList<FeeStatus> getFeeStatus() {
	return feeStatus;
}

public void setFeeStatus(ArrayList<FeeStatus> feeStatus) {
	this.feeStatus = feeStatus;
}

public ArrayList<FeeInfo> getFeelist() {
	return feelist;
}

public void setFeelist(ArrayList<FeeInfo> feelist) {
	this.feelist = feelist;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public String getTotalamountString() {
	return totalamountString;
}

public void setTotalamountString(String totalamountString) {
	this.totalamountString = totalamountString;
}

public double getTotalDueAmount() {
	return totalDueAmount;
}

public void setTotalDueAmount(double totalDueAmount) {
	this.totalDueAmount = totalDueAmount;
}

public ArrayList<StudentInfo> getList() {
	return list;
}

public void setList(ArrayList<StudentInfo> list) {
	this.list = list;
}

public String[] getCheckMonthSelected() {
	return checkMonthSelected;
}

public void setCheckMonthSelected(String[] checkMonthSelected) {
	this.checkMonthSelected = checkMonthSelected;
}

public DatabaseMethods1 getObj() {
	return obj;
}

public void setObj(DatabaseMethods1 obj) {
	this.obj = obj;
}

public boolean isShowClass() {
	return showClass;
}

public void setShowClass(boolean showClass) {
	this.showClass = showClass;
}

public boolean isShowSchool() {
	return showSchool;
}

public void setShowSchool(boolean showSchool) {
	this.showSchool = showSchool;
}

public String getSchname() {
	return schname;
}

public void setSchname(String schname) {
	this.schname = schname;
}

public String getAddress1() {
	return address1;
}

public void setAddress1(String address1) {
	this.address1 = address1;
}

public String getAddress2() {
	return address2;
}

public void setAddress2(String address2) {
	this.address2 = address2;
}

public String getAddress3() {
	return address3;
}

public void setAddress3(String address3) {
	this.address3 = address3;
}

public String getAddress4() {
	return address4;
}

public void setAddress4(String address4) {
	this.address4 = address4;
}

public String getPhoneno() {
	return phoneno;
}

public void setPhoneno(String phoneno) {
	this.phoneno = phoneno;
}

public String getMobileno() {
	return mobileno;
}

public void setMobileno(String mobileno) {
	this.mobileno = mobileno;
}

public String getWebsite() {
	return website;
}

public void setWebsite(String website) {
	this.website = website;
}

public String getLogo() {
	return logo;
}

public void setLogo(String logo) {
	this.logo = logo;
}

public String getFinalAddress() {
	return finalAddress;
}

public void setFinalAddress(String finalAddress) {
	this.finalAddress = finalAddress;
}

public String getAffiliationNo() {
	return affiliationNo;
}

public void setAffiliationNo(String affiliationNo) {
	this.affiliationNo = affiliationNo;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getHeaderImagePath() {
	return headerImagePath;
}

public void setHeaderImagePath(String headerImagePath) {
	this.headerImagePath = headerImagePath;
}

public String getRegno() {
	return regno;
}

public void setRegno(String regno) {
	this.regno = regno;
}

public String getSchid() {
	return schid;
}

public void setSchid(String schid) {
	this.schid = schid;
}

public String getBranches() {
	return branches;
}

public void setBranches(String branches) {
	this.branches = branches;
}

public String getNewschid() {
	return newschid;
}

public void setNewschid(String newschid) {
	this.newschid = newschid;
}

public StreamedContent getFile() {
	return file;
}

public void setFile(StreamedContent file) {
	this.file = file;
}

public ArrayList<SelectItem> getInstallmentList() {
	return installmentList;
}

public void setInstallmentList(ArrayList<SelectItem> installmentList) {
	this.installmentList = installmentList;
}

public ArrayList<SelectItem> getSelectedInstallList() {
	return selectedInstallList;
}

public void setSelectedInstallList(ArrayList<SelectItem> selectedInstallList) {
	this.selectedInstallList = selectedInstallList;
}

public String getSchoolid() {
	return schoolid;
}

public void setSchoolid(String schoolid) {
	this.schoolid = schoolid;
}

public ArrayList<FeeDynamicList> getFeelist1() {
	return feelist1;
}

public void setFeelist1(ArrayList<FeeDynamicList> feelist1) {
	this.feelist1 = feelist1;
}

public ArrayList<FeeDynamicList> getFeeListTest() {
	return feeListTest;
}

public void setFeeListTest(ArrayList<FeeDynamicList> feeListTest) {
	this.feeListTest = feeListTest;
}

public SchoolInfoList getSchoolDetails() {
	return schoolDetails;
}

public void setSchoolDetails(SchoolInfoList schoolDetails) {
	this.schoolDetails = schoolDetails;
}

public ArrayList<SelectItem> getConcessionlist() {
	return concessionlist;
}

public void setConcessionlist(ArrayList<SelectItem> concessionlist) {
	this.concessionlist = concessionlist;
}

public String getSelectedConcession() {
	return selectedConcession;
}

public void setSelectedConcession(String selectedConcession) {
	this.selectedConcession = selectedConcession;
}

public String getBlockMsg() {
	return blockMsg;
}

public void setBlockMsg(String blockMsg) {
	this.blockMsg = blockMsg;
}

public String[] getSelectedModule() {
	return selectedModule;
}

public void setSelectedModule(String[] selectedModule) {
	this.selectedModule = selectedModule;
}

public ArrayList<SelectItem> getAllMods() {
	return allMods;
}

public void setAllMods(ArrayList<SelectItem> allMods) {
	this.allMods = allMods;
}

public String getRegex() {
	return regex;
}

public void setRegex(String regex) {
	this.regex = regex;
}

public boolean isShowBlock() {
	return showBlock;
}

public void setShowBlock(boolean showBlock) {
	this.showBlock = showBlock;
}

   
   
   
   
}
