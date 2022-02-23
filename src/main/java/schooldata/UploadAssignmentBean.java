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

import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name="uploadAssignment")
@ViewScoped
public class UploadAssignmentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	String selectedCLassSection,selectedSection,userType,schoolid,assignmentPhotoPath1="",sms="",notification="",
			assignmentPhotoPath2="",assignmentPhotoPath3="",assignmentPhotoPath4="",assignmentPhotoPath5="",assignmentName,
			staff, subject, type, label, des,balMsg;
	Date assShowDate=new Date();
	Date pDate,minDateAssignment;
	double smsLimit;
	transient
	UploadedFile assignmentPhoto1,assignmentPhoto2,assignmentPhoto3,assignmentPhoto4,assignmentPhoto5;
 
     boolean sendMessageBoolean=false;
	public UploadAssignmentBean()
	{
		type="homework";
		sms="no";
		notification="yes";
		minDateAssignment = new Date();
		pDate = new Date();
		
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList info=obj.fullSchoolInfo(conn);
		
		if(info.getHomeworkMessage().equalsIgnoreCase("no"))
		{
			sendMessageBoolean=false;
			sms="no";
		}
		else
		{
			sendMessageBoolean=true;
			sms="yes";
		}
		
		smsLimit = obj.smsLimitReminder(obj.schoolId(), conn);
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		try
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection=obj.allClass(conn);

			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
 			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection=obj.allClassDeatilsForTeacher(empid,schoolid,conn);

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

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=obj.allSectionWithAllOption(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedCLassSection, empid,conn);
		}
		//subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		//sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			subjectList=new DatabaseMethods1().allSubjectClassWise(selectedCLassSection,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(staff,schoolid,conn);
			subjectList=objJson.AllEMployeeSubject(empid,selectedSection,schoolid,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkType()
	{
		label=type;
	}

	public String upload()
	{
		boolean extensionChecker = false;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		try
		{
			
			if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
			{
				String ext1 = FilenameUtils.getExtension(assignmentPhoto1.getFileName());
				if(ext1.equalsIgnoreCase("pdf")||ext1.equalsIgnoreCase("jpg")||ext1.equalsIgnoreCase("jpeg")||ext1.equalsIgnoreCase("gif")||ext1.equalsIgnoreCase("doc")||ext1.equalsIgnoreCase("docx")) {
					extensionChecker = false;  
				}
				else
				{
					extensionChecker = true;
				}
			}
			
			if (assignmentPhoto2 != null && assignmentPhoto2.getSize() > 0)
			{
				String ext2 = FilenameUtils.getExtension(assignmentPhoto2.getFileName());
				if(ext2.equalsIgnoreCase("pdf")||ext2.equalsIgnoreCase("jpg")||ext2.equalsIgnoreCase("jpeg")||ext2.equalsIgnoreCase("gif")||ext2.equalsIgnoreCase("doc")||ext2.equalsIgnoreCase("docx")) {
					extensionChecker = false;  
				}
				else
				{
					extensionChecker = true;
				}
			}
			
			if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
			{
				String ext3 = FilenameUtils.getExtension(assignmentPhoto3.getFileName());
				if(ext3.equalsIgnoreCase("pdf")||ext3.equalsIgnoreCase("jpg")||ext3.equalsIgnoreCase("jpeg")||ext3.equalsIgnoreCase("gif")||ext3.equalsIgnoreCase("doc")||ext3.equalsIgnoreCase("docx")) {
					extensionChecker = false;  
				}
				else
				{
					extensionChecker = true;
				}
			}
			
			if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
			{
				String ext4 = FilenameUtils.getExtension(assignmentPhoto4.getFileName());
				if(ext4.equalsIgnoreCase("pdf")||ext4.equalsIgnoreCase("jpg")||ext4.equalsIgnoreCase("jpeg")||ext4.equalsIgnoreCase("gif")||ext4.equalsIgnoreCase("doc")||ext4.equalsIgnoreCase("docx")) {
					extensionChecker = false;  
				}
				else
				{
					extensionChecker = true;
				}
			}
			
			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
			{
				String ext5 = FilenameUtils.getExtension(assignmentPhoto5.getFileName());
				if(ext5.equalsIgnoreCase("pdf")||ext5.equalsIgnoreCase("jpg")||ext5.equalsIgnoreCase("jpeg")||ext5.equalsIgnoreCase("gif")||ext5.equalsIgnoreCase("doc")||ext5.equalsIgnoreCase("docx")) {
					extensionChecker = false;  
				}
				else
				{
					extensionChecker = true;
				}
			}
			
			if(extensionChecker == true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Specified File Types"));
			}
			else
			{	
			assShowDate = pDate;
			Date dtf = new Date();
			String dt=new SimpleDateFormat("yMdhms").format(dtf);
			if (assignmentPhoto1 != null && assignmentPhoto1.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(dtf);
				assignmentPhotoPath1 = assignmentPhoto1.getFileName();
				String exten[]=assignmentPhotoPath1.split("\\.");
				assignmentPhotoPath1=staff+"_"+subject+"_"+dt+"_1_"+selectedSection+"."+exten[exten.length-1];
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto1,assignmentPhotoPath1,conn);
			}

			if (assignmentPhoto2 != null && assignmentPhoto2.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(dtf);
				assignmentPhotoPath2 = assignmentPhoto2.getFileName();
				String exten[]=assignmentPhotoPath2.split("\\.");
				assignmentPhotoPath2=staff+"_"+subject+"_"+dt+"_2_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath2;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto2,assignmentPhotoPath2,conn);
			}

			if (assignmentPhoto3 != null && assignmentPhoto3.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(dtf);
				assignmentPhotoPath3 = assignmentPhoto3.getFileName();
				String exten[]=assignmentPhotoPath3.split("\\.");
				assignmentPhotoPath3=staff+"_"+subject+"_"+dt+"_3_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath3;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto3,assignmentPhotoPath3,conn);
			}

			if (assignmentPhoto4 != null && assignmentPhoto4.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(dtf);
				assignmentPhotoPath4 = assignmentPhoto4.getFileName();
				String exten[]=assignmentPhotoPath4.split("\\.");
				assignmentPhotoPath4=staff+"_"+subject+"_"+dt+"_4_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath4;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto4,assignmentPhotoPath4,conn);
			}

			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
			{
				dt=new SimpleDateFormat("yMdhms").format(dtf);
				assignmentPhotoPath5 = assignmentPhoto5.getFileName();
				String exten[]=assignmentPhotoPath5.split("\\.");
				assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten[exten.length-1];
				assignmentPhotoPath1=assignmentPhotoPath1+","+assignmentPhotoPath5;
				DBM.makeProfileSchid(DBM.schoolId(),assignmentPhoto5,assignmentPhotoPath5,conn);

			}

			HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			staff=(String) ses.getAttribute("username");

//			 // System.out.println(assignmentPhotoPath1);
		
			int i=DBM.submitAssignment(selectedCLassSection, selectedSection, subject, staff, pDate, assShowDate, assignmentPhotoPath1, "", "",
					"", "", assignmentName, type, des,conn);
			if(i>=1)
			{

				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
				String subjectName = "";
				if(subject.equalsIgnoreCase("All"))
				{
					subjectName = "All";
				}
				else
				{
					subjectName = DBM.subjectNameFromid(subject, conn);
				}
				String className = DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection, session, conn);
				String sectionName = "";
				String clsSection = "";
				if(selectedSection.equalsIgnoreCase("All"))
				{
					sectionName = "All";
					clsSection = className;
				}
				else
				{
					sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
					clsSection = className+"-"+sectionName;
				}


				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Upload Sucessfully"));
				ArrayList<StudentInfo> studentList = new ArrayList<>();
				String subType = "";
				if(subject.equalsIgnoreCase("All"))
				{
					subType = "All,Mandatory,All";
				}
				else
				{
					subType = DBM.subjectNameAndTypeFromid(subject, conn);
				}

				String schid = DBM.schoolId();
				
				String[] temp=subType.split(",");
			
				
				String empName = DBM.teacherInfoByUserName(staff, conn).getFname();
				String empType = DBM.userTypeOfUser(staff, schid, conn);
				if(empType.equalsIgnoreCase("admin")) {
					empName = "ADMIN";
				}
				if(temp[1].equalsIgnoreCase("Mandatory"))
				{
					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						
					
						if(subjectName.equalsIgnoreCase("All")) {
							subjectName = "All Subjets";
						}
						else {
							subjectName = "Subject "+subjectName;
						}
						
						
						if(schid.equals("221") || schid.equals("216") || schid.equals("302")) {
							String sid = selectedSection;
							if(selectedSection.equalsIgnoreCase("All")) {
								sid = "-1";
							}
							
							ArrayList<StudentInfo> studentDetails = DBM.searchStudentListByClassSectionSchidWise(schid, selectedCLassSection, sid, conn);
							
							for(StudentInfo ss : studentDetails) {
								
								hw = "Dear "+ss.getFullName()+"\r\n" + 
										"Home work of "+subjectName+" Chapter/ Topic name "+assignmentName+""
										+ " of class "+clsSection+" is being uploaded by "+empName+".\r\n" + 
										"Kindly check and complete your work in time.\r\n" +
										"Team Chalkbox";
								DBM.notification(DBM.schoolId(),"Home Work",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
								DBM.notification(DBM.schoolId(),"Home Work",hw, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
								
							}
							
							
						}else {
							if(selectedSection.equalsIgnoreCase("All"))
							{
								DBM.notification(DBM.schoolId(),"Home Work",hw,selectedCLassSection+"-"+DBM.schoolId(),conn);
							}
							else
							{
								DBM.notification(DBM.schoolId(),"Home Work",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);
							}
						}
					}

				}
				else
				{
					studentList=DBM.getAllStudentStrentgthForOptional(DBM.schoolId(),subject,selectedSection,selectedCLassSection,"fromJson",conn);
					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						//DBM.notification("Home Work",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);

						for(StudentInfo ss : studentList)
						{
							if(schid.equals("221") || schid.equals("216") || schid.equals("302")) {
								
								
								if(subjectName.equalsIgnoreCase("All")) {
									subjectName = "All Subjets";
								}else {
									subjectName = "Subject "+subjectName;
								}
								
									hw = "Dear "+ss.getFullName()+"\r\n" + 
											"Home work of "+subjectName+" Chapter/ Topic name "+assignmentName+""
											+ " of class "+clsSection+" is being uploaded by "+empName+".\r\n" + 
											"Kindly check and complete your work in time.";
								}
							DBM.notification(DBM.schoolId(),"Home Work",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
							DBM.notification(DBM.schoolId(),"Home Work",hw, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
						}
					}

				}

				if(sms.equalsIgnoreCase("yes"))
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

					sendMsg();
				}
				else
				{
					return "uploadAssignment.xhtml";
				}


				/*String session = DatabaseMethods1.selectedSessionDetails();
				String subjectName = DBM.subjectNameFromid(subject, conn);
				String className = DBM.classNameFromid(selectedCLassSection, session, conn);
				String sectionName = DBM.sectionNameById(selectedSection, conn);
				String clsSection = className+"-"+sectionName;

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Upload Sucessfully"));
				ArrayList<StudentInfo> studentList = new ArrayList<>();
				String subType = DBM.subjectNameAndTypeFromid(subject, conn);
				String[] temp=subType.split(",");
				if(temp[1].equalsIgnoreCase("Mandatory"))
				{
					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						DBM.notification("Home Work",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);
					}



					if(sms.equalsIgnoreCase("yes"))
					{
						SchoolInfoList info=DBM.fullSchoolInfo(conn);
						studentList=DBM.searchStudentListByClassSection(selectedSection,conn);

						if(studentList.size()>0)
						{
							String contactNumber = "";
							String addNumber="";
							int x=0;

							String typeMessage="Dear Parent,"+"\n"+"Home work for class:"+clsSection+" subject:"+subjectName+" is: "+"\n"+des+"\n"+"Regards\n"+info.getSchoolName();
							for(StudentInfo ss : studentList)
							{

								//secondCounter++;
								if(String.valueOf(ss.getFathersPhone()).length()==10)
							    {
									x++;
									if(contactNumber.equals(""))
								    {
								    	    contactNumber=String.valueOf(ss.getFathersPhone());
								        addNumber=ss.getAddNumber();
								    }
								    else
								    {
									    	contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
									    	addNumber=addNumber+","+ss.getAddNumber();
								    }
								}

							}


							if(x>0)
							{
								DBM.messageurl1(contactNumber, typeMessage,addNumber,conn);
							}
						}

					}
				}
				else
				{
					studentList=new DataBaseMeathodJson().getAllStudentStrentgthForOptional(subject,DBM.schoolId(), conn);

					if(notification.equalsIgnoreCase("yes"))
					{
						String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
						String hw = cls+"\n"+des;
						//DBM.notification("Home Work",hw,selectedSection+"-"+selectedCLassSection+"-"+DBM.schoolId(),conn);

						for(StudentInfo ss : studentList)
						{
							DBM.notification("Home Work",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
		        				DBM.notification("Home Work",hw, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+DBM.schoolId(),conn);
						}
					}

					if(sms.equalsIgnoreCase("yes"))
					{
						SchoolInfoList info=DBM.fullSchoolInfo(conn);

						if(studentList.size()>0)
						{
							String contactNumber = "";
							String addNumber="";
							int x=0;

							String typeMessage="Dear Parent,"+"\n"+"Home work for class:"+clsSection+" subject:"+subjectName+" is: "+"\n"+des+"\n"+"Regards\n"+info.getSchoolName();
							for(StudentInfo ss : studentList)
							{

								//secondCounter++;
								if(String.valueOf(ss.getFathersPhone()).length()==10)
							    {
									x++;
									if(contactNumber.equals(""))
								    {
								    	    contactNumber=String.valueOf(ss.getFathersPhone());
								        addNumber=ss.getAddNumber();
								    }
								    else
								    {
									    	contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
									    	addNumber=addNumber+","+ss.getAddNumber();
								    }
								}

							}


							if(x>0)
							{
								DBM.messageurl1(contactNumber, typeMessage,addNumber,conn);
							}
						}

					}

				}*/

				//return "uploadAssignment.xhtml";
			}
			
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
			}
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
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String dt = formater.format(pDate);
		String asShowDt = formater.format(assShowDate);
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class-"+className+" --Section-"+sectionname+" --Date-"+dt+" --Ass Show Date-"+asShowDt;
		value = "Subject-"+subject+" --Staff"+staff+" --Files-"+assignmentPhotoPath1+" --Assignmnet Name-"+assignmentName+" --Type-"+type+" --Description-"+des;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Upload Assignment","WEB",value,conn);
		return refNo;
	}
	
	

	public String sendMsg()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		String subjectName = "";
		if(subject.equalsIgnoreCase("All"))
		{
			subjectName = "All";
		}
		else
		{
			subjectName = DBM.subjectNameFromid(subject, conn);
		}
		String className = DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection, session, conn);
		String sectionName = "";
		String clsSection = "";
		if(selectedSection.equalsIgnoreCase("All"))
		{
			sectionName = "All";
			clsSection = className;
		}
		else
		{
			sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
			clsSection = className+"-"+sectionName;
		}


		ArrayList<StudentInfo> studentList = new ArrayList<>();
		String subType = "";
		if(subject.equalsIgnoreCase("All"))
		{
			subType = "All,Mandatory,All";
		}
		else
		{
			subType = DBM.subjectNameAndTypeFromid(subject, conn);
		}

		String[] temp=subType.split(",");
		if(temp[1].equalsIgnoreCase("Mandatory"))
		{

			if(sms.equalsIgnoreCase("yes"))
			{
				SchoolInfoList info=DBM.fullSchoolInfo(conn);
				if(selectedSection.equalsIgnoreCase("All"))
				{
					studentList=DBM.searchStudentListByClassOnly(selectedCLassSection,conn);
				}
				else
				{
					studentList=DBM.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
				}


				if(studentList.size()>0)
				{
					String contactNumber = "";
					String addNumber="";
					int x=0;

					String typeMessage="Dear Parent,"+"\n"+"Home work for class:"+clsSection+" subject:"+subjectName+" is: "+"\n"+des+"\n"+"Regards\n"+info.getSmsSchoolName();
					for(StudentInfo ss : studentList)
					{

						//secondCounter++;
						if (String.valueOf(ss.getFathersPhone()).length() == 10
								&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
						{
							x++;
							if(contactNumber.equals(""))
							{
								contactNumber=String.valueOf(ss.getFathersPhone());
								addNumber=ss.getAddNumber();
							}
							else
							{
								contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
								addNumber=addNumber+","+ss.getAddNumber();
							}
						}

					}


					if(x>0)
					{
						DBM.messageurl1(contactNumber, typeMessage,addNumber,conn,DBM.schoolId(),"");
					}
				}

			}
		}
		else
		{
			studentList=DBM.getAllStudentStrentgthForOptional(DBM.schoolId(),subject,selectedSection,selectedCLassSection,"fromJson",conn);

			if(sms.equalsIgnoreCase("yes"))
			{
				SchoolInfoList info=DBM.fullSchoolInfo(conn);

				if(studentList.size()>0)
				{
					String contactNumber = "";
					String addNumber="";
					int x=0;

					String typeMessage="Dear Parent,"+"\n"+"Home work for class:"+clsSection+" subject:"+subjectName+" is: "+"\n"+des+"\n"+"Regards\n"+info.getSmsSchoolName();
					for(StudentInfo ss : studentList)
					{

						//secondCounter++;
						if (String.valueOf(ss.getFathersPhone()).length() == 10
								&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
						{
							x++;
							if(contactNumber.equals(""))
							{
								contactNumber=String.valueOf(ss.getFathersPhone());
								addNumber=ss.getAddNumber();
							}
							else
							{
								contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
								addNumber=addNumber+","+ss.getAddNumber();
							}
						}

					}


					if(x>0)
					{
						DBM.messageurl1(contactNumber, typeMessage,addNumber,conn,DBM.schoolId(),"");
					}
				}

			}

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "uploadAssignment.xhtml";
	}

	/*public String uploadSyllabus()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		try
		{
	     	pDate=new Date();
			String dt=new SimpleDateFormat("dd-MM-yyyy").format(pDate);

			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0)
			{
				assignmentPhotoPath5 = assignmentPhoto5.getFileName();
				String exten[]=assignmentPhotoPath5.split("\\.");
				assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten[exten.length-1];
				DBM.makeProfile(assignmentPhoto5,assignmentPhotoPath5,conn);
			}

			int i=DBM.uploadSyallbus(selectedCLassSection,assignmentPhotoPath5,conn);

			if(i>=1)
			{
				DBM.notification("Syallbus","Your Class Syllabus Is Added",selectedCLassSection+"-"+new DatabaseMethods1().schoolId(),conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Assignment Upload Sucessfully"));
				return "addSyllabus.xhtml";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}*/





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

	public String getAssignmentPhotoPath1() {
		return assignmentPhotoPath1;
	}

	public void setAssignmentPhotoPath1(String assignmentPhotoPath1) {
		this.assignmentPhotoPath1 = assignmentPhotoPath1;
	}

	public String getAssignmentPhotoPath2() {
		return assignmentPhotoPath2;
	}

	public void setAssignmentPhotoPath2(String assignmentPhotoPath2) {
		this.assignmentPhotoPath2 = assignmentPhotoPath2;
	}

	public String getAssignmentPhotoPath3() {
		return assignmentPhotoPath3;
	}

	public void setAssignmentPhotoPath3(String assignmentPhotoPath3) {
		this.assignmentPhotoPath3 = assignmentPhotoPath3;
	}

	public String getAssignmentPhotoPath4() {
		return assignmentPhotoPath4;
	}

	public void setAssignmentPhotoPath4(String assignmentPhotoPath4) {
		this.assignmentPhotoPath4 = assignmentPhotoPath4;
	}

	public String getAssignmentPhotoPath5() {
		return assignmentPhotoPath5;
	}

	public void setAssignmentPhotoPath5(String assignmentPhotoPath5) {
		this.assignmentPhotoPath5 = assignmentPhotoPath5;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getAssShowDate() {
		return assShowDate;
	}

	public void setAssShowDate(Date assShowDate) {
		this.assShowDate = assShowDate;
	}

	public Date getpDate() {
		return pDate;
	}

	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}

	public UploadedFile getAssignmentPhoto1() {
		return assignmentPhoto1;
	}

	public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
		this.assignmentPhoto1 = assignmentPhoto1;
	}

	public UploadedFile getAssignmentPhoto2() {
		return assignmentPhoto2;
	}

	public void setAssignmentPhoto2(UploadedFile assignmentPhoto2) {
		this.assignmentPhoto2 = assignmentPhoto2;
	}

	public UploadedFile getAssignmentPhoto3() {
		return assignmentPhoto3;
	}

	public void setAssignmentPhoto3(UploadedFile assignmentPhoto3) {
		this.assignmentPhoto3 = assignmentPhoto3;
	}

	public UploadedFile getAssignmentPhoto4() {
		return assignmentPhoto4;
	}

	public void setAssignmentPhoto4(UploadedFile assignmentPhoto4) {
		this.assignmentPhoto4 = assignmentPhoto4;
	}

	public UploadedFile getAssignmentPhoto5() {
		return assignmentPhoto5;
	}

	public void setAssignmentPhoto5(UploadedFile assignmentPhoto5) {
		this.assignmentPhoto5 = assignmentPhoto5;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public boolean isSendMessageBoolean() {
		return sendMessageBoolean;
	}

	public void setSendMessageBoolean(boolean sendMessageBoolean) {
		this.sendMessageBoolean = sendMessageBoolean;
	}

	public Date getMinDateAssignment() {
		return minDateAssignment;
	}

	public void setMinDateAssignment(Date minDateAssignment) {
		this.minDateAssignment = minDateAssignment;
	}







}
