package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="dueFeeReport")
@SessionScoped
public class DueFeeReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,firstMonthTutionFee,lastMonthTutionFee,selectedCLassSection,selectedSection,dateString,balMsg,userType;
	int totalStudent;
	ArrayList<SelectItem> classSection,sectionList,sessionList;
	ArrayList<StudentInfo> studentList,selectedStudentList,noticeStudentList;
	String regFee,tutionFee,admissionFee,examFee,annualFee,transFee,artFee,activityFee,termFee,totalFee,tutionFeePeriod,studentName,fathersName;
	StudentInfo selectedStudent;
	static int count=1;
	String selectedSession,selectedSession2,month,sectionName,className,typeMessage;
	Date date;
	boolean show;
	String studentType;
	int totalAmount;
	ArrayList<ClassInfo> classFeeList;
	ArrayList<FeeStatus> feeStatus;
	ArrayList<FeeInfo> feelist = new ArrayList<>();
	String message="", blockMsg = "";
	double totalDueAmount,smsLimit;
	DatabaseMethods1 obj=new DatabaseMethods1();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	ArrayList<SelectItem> concessionlist = new ArrayList<>();
	String selectedConcession;
	String[] selectedModule;
	ArrayList<SelectItem> allMods = new ArrayList<SelectItem>();

	public  DueFeeReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails = obj.fullSchoolInfo(conn);
		smsLimit = obj.smsLimitReminder(obj.schoolId(), conn);
		concessionlist = obj.allConnsessionType(conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		studentType="All";
		
		allMods = obj.appPermissions(obj.schoolId(), "school_student_permission", conn);
		
		try
		{
			sessionList=obj.sessionDetail();
			classSection=obj.allClass(conn);
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

	public void copyBlockStuff() 
	{
		for(StudentInfo ss : studentList)
		{
			ss.setBlockModList(selectedModule);
			ss.setMsg(blockMsg);
		}
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

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudent()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);

			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);

			studentList=DBM.searchStudentListForDueFeeReport(id,date,session,conn,"dueReport","active");
			SchoolInfoList info = DBM.fullSchoolInfo(conn);
			if(info.getBranch_id().equals("54"))
			{
				Collections.sort(studentList, new MySalaryComp());
			}
			if(studentList.size()>0)
			{
				for(StudentInfo ll:studentList)
				{
					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}
				selectedCLassSection=studentList.get(0).getClassId();
				selectedSection=studentList.get(0).getSectionid();
				feelist=DBM.classFeesForStudentForDues(selectedCLassSection,session,studentList.get(0).getStudentStatus(),studentList.get(0).getConcession(),conn);
				feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);

				className=DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection,session,conn);
				sectionName=DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).month;
				show=true;
				String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
				String[] dd=stdate.split("/");
				Date extraDate=date;
				extraDate.setDate(15);
				String date11=new SimpleDateFormat("dd-MM-yy").format(extraDate);

				message="Dear Parent, \n\nThis is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+". Kindly ignore if already paid. \n\nRegards, \n"+list1.getSmsSchoolName()  ;
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
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
	
	public void printNotice()
	{
		noticeStudentList = new ArrayList<>();
		String name="",name1="",cls="",cls1="",sec="",sec1="",amt="",amt1="";
		if(selectedStudentList.size()>0)
		{
			int i = 0;
			
			while(i<selectedStudentList.size())
			{
				name=name1=cls=cls1=sec=sec1=amt=amt1="";
				StudentInfo ii = new StudentInfo();
				name = selectedStudentList.get(i).getFname();
				cls = selectedStudentList.get(i).getClassStrName();
				sec = selectedStudentList.get(i).getSectionName();
				amt = selectedStudentList.get(i).getTutionFeeDueAmount();
				
				if((i+1)<selectedStudentList.size())
				{
					name1 = selectedStudentList.get(i+1).getFname();
					cls1 = selectedStudentList.get(i+1).getClassStrName();
					sec1 = selectedStudentList.get(i+1).getSectionName();
					amt1 = selectedStudentList.get(i+1).getTutionFeeDueAmount();
				}
				
				ii.setFname(name);
				ii.setFname1(name1);
				ii.setClassName(cls);
				ii.setClassToName(cls1);
				ii.setSectionName(sec);
				ii.setSectionTo(sec1);
				ii.setTotalFees(amt);
				ii.setTotalDiscount(amt1);
				
				noticeStudentList.add(ii);
				
				i = i+2;
			}
			
			PrimeFaces.current().executeInitScript("window.open('dueFeeNotice.xhtml')");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one student to print notice", "Validation error"));
		}
	}
	
	public String printFeeDetails()
	{
		studentName=selectedStudent.getFname();
		fathersName=selectedStudent.getFathersName();
		className=selectedStudent.getClassName();
		sectionName=selectedStudent.getSectionName();

		/*regFee=selectedStudent.getAdmissionFeeDueAmount();
		tutionFee=selectedStudent.getSchoolFeeDueAmount();
		annualFee=selectedStudent.getAnnualFeeDueAmount();
		transFee=selectedStudent.getTransportFeeDueAmount();
		examFee=selectedStudent.getExaminationFeeDueAmount();
		artFee=selectedStudent.getArtFeeDueAmount();
		termFee=selectedStudent.getTermFeeDueAmount();
		activityFee=selectedStudent.getActivityFeeDueAmount();
		totalFee=selectedStudent.getTutionFeeDueAmount();
		 */
		dateString=new SimpleDateFormat("dd-MM-yyyy").format(date);

		tutionFeePeriod=selectedStudent.getPeriod1();

		return "printDueFeeReport";
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
					if(obj.schoolId().equals("290") || obj.schoolId().equals("302"))
					{
						notifyMsg="Dear Parent,\nFee pending for your ward "+list.getFname()+","+list.getClassName()+" is Rs."+list.getTutionFeeDueAmount()+".\nKindly deposit to avoid late fine.\nAccounts\nTeam Altus\n- CB";
					}
					else
					{
						notifyMsg="Dear Parent,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTutionFeeDueAmount()+" upto  the month of "+month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
					}//notifyMsg= "Dear "+list.getFname() + ",\nYour fee is due for month "+month+" is as - "+list.getTutionFeeDueAmount()+ ".\nRegards,\n"+scinfo.getSmsSchoolName();
					DBJ.notification("Fees Due",notifyMsg, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
					DBJ.notification("Fees Due",notifyMsg, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

					if(String.valueOf(list.getSendMessageMobileNo()).length()==10
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
							&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
					{
						if(obj.schoolId().equals("290") || obj.schoolId().equals("302"))
						{
							message="Dear Parent,\nFee pending for your ward "+list.getFname()+","+list.getClassName()+" is Rs."+list.getTutionFeeDueAmount()+".\nKindly deposit to avoid late fine.\nAccounts\nTeam Altus\n- CB";
						}
						else
						{
							message="Dear Parent,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTutionFeeDueAmount()+" upto  the month of "+month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
						}
						
						msg=message;
						String templateId=DBJ.templateId(scinfo.getKey(),"DUEFEE",conn);
						//System.out.println(msg);
						DBJ.messageUrlWithTemplate(String.valueOf(list.getSendMessageMobileNo()), msg,list.getAddNumber(),obj.schoolId(),conn,templateId);
				
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
							notifyMsg="Dear Parent,\nThe pending fee of your ward "+info.getFname()+" of class "+info.getClassName()+" is Rs. "+info.getTutionFeeDueAmount()+" upto  the month of "+month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+scinfo.getSmsSchoolName();
							DBJ.notification("Fees Due",notifyMsg, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);
							DBJ.notification("Fees Due",notifyMsg, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+scinfo.getSchid(),scinfo.getSchid(),"",conn);

							msg="<center>Dear "+info.getFname() + ",<br></br>Your fee is due for month "+month+" is as - "+info.getTutionFeeDueAmount()+ ". <br></br>Regards,<br></br>"+scinfo.getSmsSchoolName()+"</center>";

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
				if(obj.schoolId().equals("290") || obj.schoolId().equals("302"))
				{
					notifyMsg="Dear Parent,\nFee pending for your ward "+list.getFname()+","+list.getClassName()+" is Rs."+list.getTutionFeeDueAmount()+".\nKindly deposit to avoid late fine.\nAccounts\nTeam Altus\n- CB";
				}
				else
				{
					notifyMsg="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTutionFeeDueAmount()+" upto  the month of "+month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+list1.getSmsSchoolName();
				}
				DBJ.notification("Fees Due",notifyMsg, list.getFathersPhone()+"-"+list.getAddNumber()+"-"+list1.getSchid(),list1.getSchid(),"",conn);
				DBJ.notification("Fees Due",notifyMsg, list.getMothersPhone()+"-"+list.getAddNumber()+"-"+list1.getSchid(),list1.getSchid(),"",conn);

				if(String.valueOf(list.getSendMessageMobileNo()).length()==10
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("9999999999")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1111111111")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("1234567890")
						&& !String.valueOf(list.getSendMessageMobileNo()).equals("0123456789"))
				{
					if(obj.schoolId().equals("290") || obj.schoolId().equals("302"))
					{
						message="Dear Parent,\nFee pending for your ward "+list.getFname()+","+list.getClassName()+" is Rs."+list.getTutionFeeDueAmount()+".\nKindly deposit to avoid late fine.\nAccounts\nTeam Altus\n- CB";
					}
					else
					{
						message="Dear Parents,\nThe pending fee of your ward "+list.getFname()+" of class "+list.getClassName()+" is Rs. "+list.getTutionFeeDueAmount()+" upto  the month of "+month+". \nKindly Deposit it as soon as possible.\nRegards,\n"+list1.getSmsSchoolName();
					}
					
					msg=message;
					String templateId=DBJ.templateId(list1.getKey(),"DUEFEE",conn);
					DBJ.messageUrlWithTemplate(String.valueOf(list.getSendMessageMobileNo()), msg,list.getAddNumber(),obj.schoolId(),conn,templateId);
					

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

	public void searchStudentByClassSection()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);

			//("started : "+new Date());
			studentList = new ArrayList<>();
			studentList=DBM.searchStudentListByClassSectionForDueFeeReport11session(selectedCLassSection,selectedSection,date,session,studentType,selectedConcession,conn);
			
			
/*			if(selectedSection.equalsIgnoreCase("-1"))
			{
				studentList=DBM.searchStudentListByClassSectionForDueFeeReportNew(selectedCLassSection,date,session,studentType,conn);

			}
			else
			{
				studentList=DBM.searchStudentListByClassSectionForDueFeeReport11(selectedSection,date,session,studentType,conn);

			}*/
			//studentList=obj.myDemoMethod2(selectedSection,date,session);
			SchoolInfoList info = DBM.fullSchoolInfo(conn);
			if(info.getBranch_id().equals("54"))
			{
				Collections.sort(studentList, new MySalaryComp());
			}
			//("finished : "+new Date());


			if(studentList.size()>0)
			{

				for(StudentInfo ll:studentList)
				{
					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}

				feelist=DBM.viewFeeListForDues(DBM.schoolId(),conn);
				FeeInfo ff = new FeeInfo();
				ff.setFeeName("Previous Fee");
				ff.setFeeId("-1");
				ff.setFeeType("year");
				feelist.add(ff);
				//feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);   // only for adding previous fee label

				className=selectedCLassSection.equalsIgnoreCase("-1") ? "All" : DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection,session,conn);
				sectionName=selectedSection.equalsIgnoreCase("-1") ? "All" : DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).month;
				String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
				String[] dd=stdate.split("/");
				Date extraDate=date;
				extraDate.setDate(15);
				String date11=new SimpleDateFormat("dd-MM-yy").format(extraDate);

				message="Dear Parent \n This is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+".Kindly ignore if already paid \n Thanks \n"+list1.getSmsSchoolName()  ;

				show=true;
			}
			else
			{
				totalStudent = 0;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection con = DataBaseConnection.javaConnection();
		//studentList=obj.searchStudentList(query,con);
		studentList=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName",query, "full", con,"","");
		
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
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
				amount = Integer.valueOf(ls.getTutionFeeDueAmount());
				
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
	
	public  void exportDueFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);

		schoolDetails =new DatabaseMethods1().fullSchoolInfo(con);


		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

		//Header
		try {
			//   Font fon = new Font(FontFamily.HELVETICA, 4, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im =null;
			try {
				im  =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 =null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                              Due Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c7 = new Chunk("\n Class : "+className+"          Section : "+sectionName +"            Up To Month : "+month+"           Total Amount : "+totalDueAmount+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5);
			PdfPTable table = new PdfPTable(6+feelist.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("Sno.",font));
			table.addCell(new Phrase("Student Name",font));

			table.addCell(new Phrase("Father Name",font));
			table.addCell(new Phrase("Contact No.",font));
			table.addCell(new Phrase("Class",font));
			for(int j=0;j<feelist.size();j++) {
				table.addCell(new Phrase(String.valueOf(feelist.get(j).getFeeName()),font));

			}

			table.addCell(new Phrase("Total Left Amount",font));




			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentList.size();i++){

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSrNo()),font));
				table.addCell(new Phrase(studentList.get(i).getFname(),font));

				table.addCell(new Phrase(studentList.get(i).getFathersName(),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));

				table.addCell(new Phrase(studentList.get(i).getClassName(),font));
				for(int j=0;j<feelist.size();j++) {
					table.addCell(new Phrase(studentList.get(i).getFeesMap().get(feelist.get(j).getFeeName()),font));

				}



				table.addCell(new Phrase(studentList.get(i).getTutionFeeDueAmount(),font));




			}


			table.setWidthPercentage(110);
			document.add(table);





		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Due_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Due_Fee_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}


	public  void exportMiniDueFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);



		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im = null;
			try {
				 im  =Image.getInstance(src);
					im.setAlignment(Element.ALIGN_LEFT);

					im.scaleAbsoluteHeight(60);
					im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );
			Chunk c3 = null;
			try {
				 c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                    Due Fee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c7 = new Chunk("\n Class : "+className+"          Section : "+sectionName +"          Up To Month : "+month+"        Total Amount : "+totalDueAmount+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();


			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(6);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno.");
			table.addCell("Student Name");

			table.addCell("Father Name");
			table.addCell("Contact No.");
			table.addCell("Class");

			table.addCell("Total Left Amount");




			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentList.size();i++){

				table.addCell(new Phrase(studentList.get(i).getSrNo()));
				table.addCell(new Phrase(studentList.get(i).getFname(),font));

				table.addCell(new Phrase(studentList.get(i).getFathersName()));
				table.addCell(new Phrase(studentList.get(i).getFathersPhone()));

				table.addCell(new Phrase(studentList.get(i).getClassName()));




				table.addCell(new Phrase(studentList.get(i).getTutionFeeDueAmount()));




			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf"," Mini_Due_Fee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Mini_Due_Fee_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public void toNumDueMiniFee(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		new SimpleDateFormat("dd/MM/yyyy");


		sheet.createFreezePane(0, 3);


		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow= sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow+1, 1);
		XSSFRow r1 =     sheet.createRow(1);
		r1.setHeight((short) 600);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//     styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		//    styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue("Class:-"+className);
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Section:-"+sectionName);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total Student:-"+totalStudent);
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue("Up To Month : "+month);
		clee6.setCellStyle(styleb);

		XSSFCell clee7 = r1.createCell(4);
		clee7.setCellValue("Total Amount : "+totalDueAmount);
		clee7.setCellStyle(styleb);

//		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
//		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=5;o<colCount;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}



		XSSFRow he = sheet.createRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



		try
		{

			URL url = new URL(schoolDetails.downloadpath+schoolDetails.marksheetHeader);
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(7);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();




		} catch (IOException ioex) {
             ioex.printStackTrace();
		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);

		XSSFRow headerRow = sheet.getRow(2);
		headerRow.setHeight((short)400);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//   Font font = book.createFont();
		//  font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i,MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if(i==0) {
				ce1.setCellValue(" ");//Sr. No.
			}
			ce1.setCellStyle(style);


		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) st).setFillForegroundColor(color1);
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setBorderLeft(BorderStyle.THIN);
		st.setBorderRight(BorderStyle.THIN);
		st.setBorderTop(BorderStyle.THIN);
		st.setBorderBottom(BorderStyle.THIN);

		st.setBottomBorderColor(IndexedColors.RED.getIndex());
		st.setTopBorderColor(IndexedColors.RED.getIndex());
		st.setLeftBorderColor(IndexedColors.RED.getIndex());
		st.setRightBorderColor(IndexedColors.RED.getIndex());

		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <5 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}



		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 5; cellInd <6 ; cellInd++) {

				//(rowInd);
				XSSFCell cell = row.getCell(cellInd);

				String strVal = cell.getStringCellValue();


				try {
					Double ds = Double.valueOf(strVal);


					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					DataFormat fmt = book.createDataFormat();
					CellStyle sty = book.createCellStyle();

					if(rowInd%2==0) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
						((XSSFCellStyle) sty).setFillForegroundColor(color6);
						sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					else {
						XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
						((XSSFCellStyle) sty).setFillForegroundColor(color2);
						sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}


					sty.setDataFormat(fmt.getFormat("#,##0.00"));
					sty.setBorderLeft(BorderStyle.THIN);
					sty.setBorderRight(BorderStyle.THIN);
					sty.setBorderTop(BorderStyle.THIN);
					sty.setBorderBottom(BorderStyle.THIN);



					sty.setBottomBorderColor(IndexedColors.RED.getIndex());
					sty.setTopBorderColor(IndexedColors.RED.getIndex());
					sty.setLeftBorderColor(IndexedColors.RED.getIndex());
					sty.setRightBorderColor(IndexedColors.RED.getIndex());
					cell.setCellStyle(sty);

					cell.setCellValue(ds);

				}
				catch (Exception e) {
					e.printStackTrace();
				}

			}

		}




	}

	public void toNumDueFee(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		new SimpleDateFormat("dd/MM/yyyy");


		sheet.createFreezePane(0, 3);


		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow= sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow+1, 1);
		XSSFRow r1 =     sheet.createRow(1);
		r1.setHeight((short) 600);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//     styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		//    styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue("Class:-"+className);
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Section:-"+sectionName);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total Student:-"+totalStudent);
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue("Up To Month : "+month);
		clee6.setCellStyle(styleb);

		XSSFCell clee7 = r1.createCell(4);
		clee7.setCellValue("Total Amount : "+totalDueAmount);
		clee7.setCellStyle(styleb);


		for(int o=5;o<colCount;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}



		XSSFRow he = sheet.createRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



		try
		{
			URL url = new URL(schoolDetails.downloadpath+schoolDetails.marksheetHeader);
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//  FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(8);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();




		} catch (IOException ioex) {

		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);

		XSSFRow headerRow = sheet.getRow(2);
		headerRow.setHeight((short)400);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//   Font font = book.createFont();
		//  font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		//XSSFRow hr = sheet.getRow(3);
		
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
//			if(i==0) {
//				ce1.setCellValue(" ");//Sr. No.
//			}
//			if(i==colCount-1) {
//				ce1.setCellValue(" "); //Total Left Amount
//			}
			ce1.setCellStyle(style);


		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) st).setFillForegroundColor(color1);
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setBorderLeft(BorderStyle.THIN);
		st.setBorderRight(BorderStyle.THIN);
		st.setBorderTop(BorderStyle.THIN);
		st.setBorderBottom(BorderStyle.THIN);

		st.setBottomBorderColor(IndexedColors.RED.getIndex());
		st.setTopBorderColor(IndexedColors.RED.getIndex());
		st.setLeftBorderColor(IndexedColors.RED.getIndex());
		st.setRightBorderColor(IndexedColors.RED.getIndex());


		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd < 1 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

		sheet.getRow(5);
		double total=0.0;
		double[] sum = new double[colCount+1];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);

			for(int cellInd = 1; cellInd <colCount ; cellInd++) {

				if(cellInd!=3) {
					XSSFCell cell = row.getCell(cellInd);

					int status=0,counter=0,dot=0;
					Character ch[] = new Character[3000];

					String strVal = cell.getStringCellValue();
					if(strVal.equalsIgnoreCase("")) {
						status=5;
					}

					for(int i=0;i<strVal.length();i++)
					{
						ch[i] = strVal.charAt(i);
						String s =Character.toString(ch[i]);


						if(Character.isDigit(ch[i]))
						{
							counter++;
						}
						if(s.equals("."))
						{
							dot++;
						}
						if(s.equals(""))
						{
							status=1;
						}
					}


					if(status==1)
					{
						CellStyle st2 = book.createCellStyle();

						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue(strVal);

					}
					else if(status==5) {
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue("");
					}
					else if((dot+counter)==strVal.length())
					{
						try {
							Double ds = Double.valueOf(strVal);

							if((cellInd>=5)&&(cellInd<=colCount-1))
							{
								total+=ds;
							}
							//("total"+total);
							if(rowInd==3)
							{
								sum[cellInd] = total;

							}
							else
							{
								sum[cellInd] =sum[cellInd]+total;


							}
							total=0.0;

							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							DataFormat fmt = book.createDataFormat();
							CellStyle sty = book.createCellStyle();

							if(rowInd%2==0) {
								XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
								((XSSFCellStyle) sty).setFillForegroundColor(color6);
								sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}
							else {
								XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
								((XSSFCellStyle) sty).setFillForegroundColor(color2);
								sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							}


							sty.setDataFormat(fmt.getFormat("#,##0.00"));
							sty.setBorderLeft(BorderStyle.THIN);
							sty.setBorderRight(BorderStyle.THIN);
							sty.setBorderTop(BorderStyle.THIN);
							sty.setBorderBottom(BorderStyle.THIN);



							sty.setBottomBorderColor(IndexedColors.RED.getIndex());
							sty.setTopBorderColor(IndexedColors.RED.getIndex());
							sty.setLeftBorderColor(IndexedColors.RED.getIndex());
							sty.setRightBorderColor(IndexedColors.RED.getIndex());
							cell.setCellStyle(sty);

							cell.setCellValue(ds);

						}
						catch (Exception e) {
							// TODO: handle exception
						}

					}
					else
					{
						CellStyle st2 = book.createCellStyle();
						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) st2).setFillForegroundColor(color6);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) st2).setFillForegroundColor(color2);
							st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

						st2.setBorderLeft(BorderStyle.THIN);
						st2.setBorderRight(BorderStyle.THIN);
						st2.setBorderTop(BorderStyle.THIN);
						st2.setBorderBottom(BorderStyle.THIN);

						st2.setBottomBorderColor(IndexedColors.RED.getIndex());
						st2.setTopBorderColor(IndexedColors.RED.getIndex());
						st2.setLeftBorderColor(IndexedColors.RED.getIndex());
						st2.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(st2);
						cell.setCellValue(strVal);

					}

				}

			}
		}

		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);

			for(int cellInd = 3; cellInd <4 ; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);





				// cell.getN
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}



		XSSFRow roo=sheet.createRow(rowCount+2);

		//("sum series");
		for(int h=0;h<sum.length;h++) {
			//(h+"-"+sum[h]);
		}

		for(int f=5;f<colCount;f++)
		{
			XSSFCell cell1=roo.createCell(f);

			cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
			DataFormat fmt = book.createDataFormat();
			CellStyle sty = book.createCellStyle();
			XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
			((XSSFCellStyle) sty).setFillForegroundColor(color7);
			sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			sty.setBorderLeft(BorderStyle.THIN);
			sty.setBorderRight(BorderStyle.THIN);
			sty.setBorderTop(BorderStyle.THIN);
			sty.setBorderBottom(BorderStyle.THIN);

			sty.setBottomBorderColor(IndexedColors.RED.getIndex());
			sty.setTopBorderColor(IndexedColors.RED.getIndex());
			sty.setLeftBorderColor(IndexedColors.RED.getIndex());
			sty.setRightBorderColor(IndexedColors.RED.getIndex());
			sty.setDataFormat(fmt.getFormat("#,##0.00"));
			cell1.setCellStyle(sty);
			cell1.setCellValue(sum[f]);
		}
		XSSFCell cell222=roo.createCell(4);
		CellStyle sty = book.createCellStyle();
		XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
		((XSSFCellStyle) sty).setFillForegroundColor(color7);
		sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sty.setBorderLeft(BorderStyle.THIN);
		sty.setBorderRight(BorderStyle.THIN);
		sty.setBorderTop(BorderStyle.THIN);
		sty.setBorderBottom(BorderStyle.THIN);

		sty.setBottomBorderColor(IndexedColors.RED.getIndex());
		sty.setTopBorderColor(IndexedColors.RED.getIndex());
		sty.setLeftBorderColor(IndexedColors.RED.getIndex());
		sty.setRightBorderColor(IndexedColors.RED.getIndex());
		//  sty.setDataFormat(fmt.getFormat("#,##0.00"));
		cell222.setCellStyle(sty);
		cell222.setCellValue("Total");
	}



	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
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
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
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
	public String getTutionFeePeriod() {
		return tutionFeePeriod;
	}
	public void setTutionFeePeriod(String tutionFeePeriod) {
		this.tutionFeePeriod = tutionFeePeriod;
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
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
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
	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSelectedSession2() {
		return selectedSession2;
	}
	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}
	public void setSelectedSession2(String selectedSession2) {
		this.selectedSession2 = selectedSession2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
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
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
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

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}
	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}
	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public ArrayList<StudentInfo> getNoticeStudentList() {
		return noticeStudentList;
	}

	public void setNoticeStudentList(ArrayList<StudentInfo> noticeStudentList) {
		this.noticeStudentList = noticeStudentList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
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
	
	


}
