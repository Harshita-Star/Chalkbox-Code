package schooldata;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import dlt_template.DltDatabaseMethod;
import dlt_template.DltTemplateInfo;
import student_module.DataBaseOnlineAdm;

@ManagedBean(name="sendMessage")
@ViewScoped
public class SendMessageBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String messageCategory,className1,typeMessage,selectedSection,language,balMsg,userType;
	ArrayList<SelectItem> sectionList,classListWithSecetion;
	SchoolInfoList ls;
	ArrayList<StudentInfo> studentList,selectedStudentList = new ArrayList<>();
	boolean showClassList,showclassName,show,hindiShow,englishShow,messageParents=false,messageStudents=false,showInstitute=false,sms,notification;
	ArrayList<ClassInfo> classList,selectedClassList;
	double smsLimit;
	ArrayList<String> selectedSectionArr = new ArrayList<String>();
	
	String keyWord,dltId,lang;
	ArrayList<SelectItem> allKeywords = new ArrayList<>();
	ArrayList<DltTemplateInfo> allTemplates = new ArrayList<>();
	DltTemplateInfo selectedTemp;
	boolean editable;

	public SendMessageBean()
	{
		sms=true;
		notification=false;
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		ls=DBM.fullSchoolInfo(conn);
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		String ctype=DBM.checkClientType(conn);
		
		if(ctype.equalsIgnoreCase("institute"))
		{
			showInstitute=true;
		}
		else
		{
			showInstitute=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSelectedSection()
	{
		return selectedSection;
	}

	public void checkBalance()
	{

		String output  = getUrlContents("http://sms.imgglobalinfotech.com/api/balance.php?authkey=76d23f5f80ac88287012832a08c6d4bc&route=B");
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("You have "+output+" message left "));

	}
	private static String getUrlContents(String theUrl)
	{
		StringBuilder content = new StringBuilder();
		try
		{
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				content.append(line + "\n");
			}
			bufferedReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return content.toString();
	}

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(className1,conn);
		selectedSection=null;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showGeneralCategoryList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		hindiShow=false;
		language="english";
		englishShow=true;
		sms=true;
		notification=false;
		if(messageCategory.equals("All"))
		{
			showclassName=false;
			show=false;
			showClassList=true;

			classList=obj.allSectionList(conn);
			className1=null;
			studentList=null;
			selectedSection=null;
		}
		else if(messageCategory.equals("ClassWise"))
		{
			showClassList=false;
			showclassName=true;
			classListWithSecetion=obj.allClass(conn);
			className1=null;
			selectedSection=null;
		}
		
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(language,conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1",language,new DatabaseMethods1().schoolId(),conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageStudentByClassSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		hindiShow=false;
		language="english";
		englishShow=true;
		studentList = new ArrayList<StudentInfo>();
		try
		{
			for(String sec : selectedSectionArr)
			{
				ArrayList<StudentInfo> list = new DatabaseMethods1().searchStudentListByClassSection(className1,sec,conn);
				studentList.addAll(list);
			}
			
			show=true;
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

	public void checkLanguage()
	{
		 // System.out.println("hello");
		typeMessage="";
		if(language.equalsIgnoreCase("english"))
		{
			englishShow=true;hindiShow=false;
		}
		else
		{
			englishShow=false;hindiShow=true;
		//	PrimeFaces.current().executeInitScript("PF('hindiDi').show();");
		}
	}

	public String sendMessageAllClass() throws UnsupportedEncodingException
	{
		if(sms==false && notification==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		}
		else
		{

			Connection conn = DataBaseConnection.javaConnection();
			if(ls.getCountry().equalsIgnoreCase("India"))
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
			}

			if(showInstitute==false)
			{
				messageAllClassSchool();
			}
			else
			{
				if(messageParents==false && messageStudents==false)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Receivers : Parents/Students"));
				}
				else
				{
					messageAllClassInstitute();
				}
			}
		}

		return "";
	}
	
	public void closeConfirmDialog()
	{
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		
	}

	public void allClassMsg() throws UnsupportedEncodingException
	{
		if(sms==false && notification==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		}
		else
		{
			if(showInstitute==false)
			{
				messageAllClassSchool();
			}
			else
			{
				if(messageParents==false && messageStudents==false)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Receivers : Parents/Students"));
				}
				else
				{
					messageAllClassInstitute();
				}
			}
		}
	}

	public void messageAllClassSchool() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		ls=DBM.fullSchoolInfo(conn);
		int counterStudentInClass=0;
		int x=0;
		if(selectedClassList.size()>0)
		{
			if(sms)
			{
				studentList=DBM.searchStudentListByClassSection(selectedClassList,conn);

				if(studentList.size()>0)
				{
					counterStudentInClass++;
				}
				//int counter=1;
				//int secondCounter=0;
				String contactNumber = "";
				String addNumber="";
				//Date date=new Date();
				if(ls.getCountry().equalsIgnoreCase("India"))
				{
					for(StudentInfo info : studentList)
					{
						x++;
						
						//secondCounter++;
						if(String.valueOf(info.getFathersPhone()).length()==10
								&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
						{
							if(contactNumber.equals(""))
							{
								contactNumber=String.valueOf(info.getFathersPhone());
								addNumber=info.getAddmisssionNumber();
							}
							else
							{
								if (!contactNumber.contains(String.valueOf(info
										.getFathersPhone())))
								{
								contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
								addNumber=addNumber+","+info.getAddmisssionNumber();
								}
							}

						}

					}
				}
				else
				{
					String tp = typeMessage;
					ArrayList<StudentInfo> newList = studentList;
					Runnable r = new Runnable()
					{
						public void run()
						{
							String heading = "<center class=\"red\">Message From "+ls.getSchoolName()+"!</center>";
							String subject = "Message From "+ls.getSchoolName();
							String msg="<center>Dear Parent,<br></br>"+tp+" <br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
							for(StudentInfo info : newList)
							{
								
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
						}
					};
					new Thread(r).start();
				}
				//	typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
				if(ls.getCountry().equalsIgnoreCase("India"))
				{
					if(x>0)
					{
//						String messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
//						if(language.equalsIgnoreCase("hindi"))
//						{
//							messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						
						String messageTemp= typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
						if(language.equalsIgnoreCase("hindi"))
						{
							messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
						
							//(messageTemp.length());
							
							   //Pattern for showing milliseconds in the time "SSS"
						       
							DBM.messageurlHindi(contactNumber, messageTemp,addNumber,conn,dltId);
							  
								
							
							
						}
						else
						{
						 
						
							DBM.messageurl1(contactNumber, messageTemp,addNumber,conn,DBM.schoolId(),dltId);
							
							
							
						}
						//DBM.messageurl1(contactNumber, messageTemp,addNumber,conn);
					}
				}
			}

			if(notification)
			{
				String messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
				if(language.equalsIgnoreCase("hindi"))
				{
					messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
				}

				String schid = DBM.schoolId();

				for(ClassInfo cc : selectedClassList)
				{
					counterStudentInClass++;
					DBM.notification(schid,"Message",messageTemp,cc.getSectionId()+"-"+cc.getClassid()+"-"+schid,conn);
				}
			}


			FacesContext fc=FacesContext.getCurrentInstance();
			if(counterStudentInClass>0)
			{	
			 fc.addMessage(null, new FacesMessage("Message Sent Successfully"));
			}
			else
			{
				 fc.addMessage(null, new FacesMessage("No Student In Class Found"));	
			}
			classList=selectedClassList=null;
			studentList=selectedStudentList=null;
			typeMessage=messageCategory=className1=null;
			show=showClassList=showclassName=false;
			sms=true;notification=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No class selected, please select atleast one", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void messageAllClassInstitute() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		ls=DBM.fullSchoolInfo(conn);
		int x=0;
		if(selectedClassList.size()>0)
		{
			if(sms)
			{
				studentList=DBM.searchStudentListByClassSection(selectedClassList,conn);

				//int counter=1;
				//int secondCounter=0;
				String contactNumber = "";
				String addNumber="";
				//Date date=new Date();

				//	typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
				for(StudentInfo info : studentList)
				{
					x++;
					//secondCounter++;
					if(messageParents==true)
					{
						if(String.valueOf(info.getFathersPhone()).length()==10
								&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
						{
							if(contactNumber.equals(""))
							{
								contactNumber=String.valueOf(info.getFathersPhone());
								addNumber=info.getAddmisssionNumber();
							}
							else
							{
								if (!contactNumber.contains(String.valueOf(info
										.getFathersPhone())))
								{
								contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
								addNumber=addNumber+","+info.getAddmisssionNumber();
								}
							}

						}
					}

					if(messageStudents==true)
					{
						if(info.getStudentPhone().length()==10
								&& !String.valueOf(info.getStudentPhone()).equals("2222222222")
								&& !String.valueOf(info.getStudentPhone()).equals("9999999999")
								&& !String.valueOf(info.getStudentPhone()).equals("1111111111")
								&& !String.valueOf(info.getStudentPhone()).equals("1234567890")
								&& !String.valueOf(info.getStudentPhone()).equals("0123456789"))
						{
							if(contactNumber.equals(""))
							{
								contactNumber=info.getStudentPhone();
								addNumber=String.valueOf(info.getAddmisssionNumber());
							}
							else
							{
								if (!contactNumber.contains(String.valueOf(info
										.getStudentPhone())))
								{
								contactNumber=contactNumber+","+info.getStudentPhone();
								addNumber=addNumber+","+String.valueOf(info.getAddmisssionNumber());
								}
							}
						}
					}



				}

				if(x>0)
				{
					String messageTemp="";
//					if(messageParents==true && messageStudents==false)
//					{
//						messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//						if(language.equalsIgnoreCase("hindi"))
//						{
//							messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//						}
//					}
//					else if(messageParents==false && messageStudents==true)
//					{
//						messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//						if(language.equalsIgnoreCase("hindi"))
//						{
//							messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//						}
//					}
//					else if(messageParents==true && messageStudents==true)
//					{
//						messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//						if(language.equalsIgnoreCase("hindi"))
//						{
//							messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//						}
//					}
					
					
					if(messageParents==true && messageStudents==false)
					{
						messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(language.equalsIgnoreCase("hindi"))
						{
							messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}
					else if(messageParents==false && messageStudents==true)
					{
						messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(language.equalsIgnoreCase("hindi"))
						{
							messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}
					else if(messageParents==true && messageStudents==true)
					{
						messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
						if(language.equalsIgnoreCase("hindi"))
						{
							messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
						}
					}
					

					//					//(addNumber);
					//					//(contactNumber.split(",").length);
					if(language.equalsIgnoreCase("hindi"))
					{
					
						
						DBM.messageurlHindi(contactNumber, messageTemp,addNumber,conn,dltId);
						
						
					}
					else
					{
						
						DBM.messageurl1(contactNumber, messageTemp,addNumber,conn,DBM.schoolId(),dltId);
						
					}
				}
			}

			if(notification)
			{
				String messageTemp="";
//				if(messageParents==true && messageStudents==false)
//				{
//					messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//					if(language.equalsIgnoreCase("hindi"))
//					{
//						messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//					}
//				}
//				else if(messageParents==false && messageStudents==true)
//				{
//					messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//					if(language.equalsIgnoreCase("hindi"))
//					{
//						messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//					}
//				}
//				else if(messageParents==true && messageStudents==true)
//				{
//					messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//					if(language.equalsIgnoreCase("hindi"))
//					{
//						messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//					}
//				}
				
				
				if(messageParents==true && messageStudents==false)
				{
					messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(language.equalsIgnoreCase("hindi"))
					{
						messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}
				else if(messageParents==false && messageStudents==true)
				{
					messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(language.equalsIgnoreCase("hindi"))
					{
						messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}
				else if(messageParents==true && messageStudents==true)
				{
					messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
					if(language.equalsIgnoreCase("hindi"))
					{
						messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
					}
				}

				String schid = DBM.schoolId();

				for(ClassInfo cc : selectedClassList)
				{
					DBM.notification(schid,"Message",messageTemp,cc.getSectionId()+"-"+cc.getClassid()+"-"+schid,conn);
				}
			}

			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Message Sent Successfully"));

			classList=selectedClassList=null;
			studentList=selectedStudentList=null;
			typeMessage=messageCategory=className1=null;
			show=showClassList=showclassName=messageParents=messageStudents=false;
			sms=true;notification=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No class selected, please select atleast one", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String sendMessageClassWise() throws UnsupportedEncodingException
	{
		if(sms==false && notification==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
			return "";
		}
		else
		{
			Connection conn = DataBaseConnection.javaConnection();
			if(ls.getCountry().equalsIgnoreCase("India"))
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
						PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");

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
					PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");

					return "";
				}
			}

			if(showInstitute==false)
			{
				messageClassWiseSchool();
			}
			else
			{
				if(messageParents==false && messageStudents==false)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Receivers : Parents/Students"));
				}
				else
				{
					messageClassWiseInstitute();
				}
			}
		}
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");

		return "";
	}
	
	public void checkClassesStuSelected()
	{
		if(sms==false && notification==false)
	 {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		
	 }
	  else
	  {
		if(selectedStudentList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student!"));	
		}
		else
		{
			checkGroupSeelcted();
			//PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");	
		}
		
	  }
	}
	

	public void classWiseMsg() throws UnsupportedEncodingException
	{
		if(sms==false && notification==false)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select SMS/Notification/Both"));
		}
		else
		{
			if(showInstitute==false)
			{
				messageClassWiseSchool();
			}
			else
			{
				if(messageParents==false && messageStudents==false)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Receivers : Parents/Students"));
				}
				else
				{
					messageClassWiseInstitute();
				}
			}
		}
	}

	public void messageClassWiseSchool() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		ls=DBM.fullSchoolInfo(conn);
		int x=0;
		//		int counter=1;
		//		int secondCounter=0;
		String contactNumber = "";
		//Date date=new Date();
		String addNumber="";

//		String messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
		String messageTemp= typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
		if(language.equalsIgnoreCase("hindi"))
		{
			//messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
		}
		//typeMessage=URLEncoder.encode(typeMessage,"UTF-8");
		if(ls.getCountry().equalsIgnoreCase("India"))
		{
			for(StudentInfo info : selectedStudentList)
			{
				x++;
				//secondCounter++;
				if(String.valueOf(info.getFathersPhone()).length()==10
						&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
						&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
						&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
						&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
						&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
				{
					if(contactNumber.equals(""))
					{
						contactNumber=String.valueOf(info.getFathersPhone());
						addNumber=info.getAddNumber();
					}
					else
					{
						if (!contactNumber.contains(String.valueOf(info
								.getFathersPhone())))
						{
						
						contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
						addNumber=addNumber+","+info.getAddNumber();
						}
					}

				}

				if(notification)
				{
					DBM.notification(ls.getSchid(),"Message",messageTemp, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),conn);
					DBM.notification(ls.getSchid(),"Message",messageTemp, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),conn);
				}

			}
		}
		else
		{
			//(selectedStudentList.size());
			x++;
			String tp = typeMessage;
			ArrayList<StudentInfo> newList = selectedStudentList;
			Runnable r = new Runnable()
			{
				public void run()
				{
					Connection con=DataBaseConnection.javaConnection();
					String heading = "<center class=\"red\">Message From "+ls.getSchoolName()+"!</center>";
					String subject = "Message From "+ls.getSchoolName();
					String msg="<center>Dear Parent,<br></br>"+tp+" <br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
					String msgn="Dear Parent,\n"+tp+"\nRegards,\n"+ls.getSmsSchoolName();
					//(newList.size());

					for(StudentInfo info : newList)
					{
						if(sms)
						{
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

						if(notification)
						{
							DBM.notification(ls.getSchid(),"Message",msgn, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),con);
							DBM.notification(ls.getSchid(),"Message",msgn, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),con);
						}
					}

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			};
			new Thread(r).start();
		}


		if(x>0)
		{
			if(ls.getCountry().equalsIgnoreCase("India"))
			{
				if(sms)
				{
					//messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
					messageTemp= typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
					if(language.equalsIgnoreCase("hindi"))
					{
						//messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
						//(messageTemp.length());
						
						
						
						DBM.messageurlHindi(contactNumber, messageTemp,addNumber,conn,dltId);
						 
						
						
					}
					else
					{
						
						 // System.out.println(typeMessage);
						 // System.out.println(messageTemp);
						
						DBM.messageurl1(contactNumber, messageTemp,addNumber,conn,DBM.schoolId(),dltId);
						
						
					}
				}
			}

			//DBM.messageurl1(contactNumber, messageTemp,addNumber,conn);
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Please select atleast one student!"));
		}



		classList=selectedClassList=null;
		studentList=selectedStudentList=null;
		typeMessage=messageCategory=className1=null;
		show=showClassList=showclassName=false;
		sms=true;notification=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void messageClassWiseInstitute() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		ls=DBM.fullSchoolInfo(conn);
		int x=0;
		//		int counter=1;
		//		int secondCounter=0;
		String contactNumber = "";
		//Date date=new Date();
		String addNumber="";
		String messageTemp="";

		if(messageParents==true && messageStudents==false)
		{
//			messageTemp="Dear Parent,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
			
			messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
			if(language.equalsIgnoreCase("hindi"))
			{
				messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
			}
			
		}
		else if(messageParents==false && messageStudents==true)
		{
//			messageTemp="Dear Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
			
			messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
			if(language.equalsIgnoreCase("hindi"))
			{
				messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
			}
		}
		else if(messageParents==true && messageStudents==true)
		{
//			messageTemp="Dear Parent/Student,\n"+typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
//			if(language.equalsIgnoreCase("hindi"))
//			{
//				messageTemp="प्रिय अभिभावक/विद्यार्थी,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
//			}
			
			messageTemp= typeMessage+" \nRegards,\n"+ls.getSmsSchoolName();
			if(language.equalsIgnoreCase("hindi"))
			{
				messageTemp= typeMessage+"\nसादर,\n"+ls.getHindiName();
			}
			
		}
		//typeMessage=URLEncoder.encode(typeMessage,"UTF-8");

		for(StudentInfo info : selectedStudentList)
		{
			x++;
			//secondCounter++;
			if(messageParents==true)
			{
				if(String.valueOf(info.getFathersPhone()).length()==10
						&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
						&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
						&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
						&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
						&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
				{
					if(contactNumber.equals(""))
					{
						contactNumber=String.valueOf(info.getFathersPhone());
						addNumber=info.getAddNumber();
					}
					else
					{
						if (!contactNumber.contains(String.valueOf(info
								.getFathersPhone())))
						{
						contactNumber=contactNumber+","+String.valueOf(info.getFathersPhone());
						addNumber=addNumber+","+info.getAddNumber();
						}
					}

				}
			}

			if(messageStudents==true)
			{
				if(info.getStudentPhone().length()==10
						&& !String.valueOf(info.getStudentPhone()).equals("2222222222")
						&& !String.valueOf(info.getStudentPhone()).equals("9999999999")
						&& !String.valueOf(info.getStudentPhone()).equals("1111111111")
						&& !String.valueOf(info.getStudentPhone()).equals("1234567890")
						&& !String.valueOf(info.getStudentPhone()).equals("0123456789"))
				{
					if(contactNumber.equals(""))
					{
						contactNumber=info.getStudentPhone();
						addNumber=String.valueOf(info.getAddNumber());
					}
					else
					{
						if (!contactNumber.contains(String.valueOf(info
								.getStudentPhone())))
						{
						contactNumber=contactNumber+","+info.getStudentPhone();
						addNumber=addNumber+","+String.valueOf(info.getAddNumber());
						}
					}
				}
			}

			if(notification)
			{
				DBM.notification(ls.getSchid(),"Message",messageTemp, info.getFathersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),conn);
				DBM.notification(ls.getSchid(),"Message",messageTemp, info.getMothersPhone()+"-"+info.getAddNumber()+"-"+ls.getSchid(),conn);
			}
		}

		if(x>0)
		{


			//			//(addNumber);
			//			//(contactNumber.split(",").length);
			if(sms)
			{
				if(language.equalsIgnoreCase("hindi"))
				{
					DBM.messageurlHindi(contactNumber, messageTemp,addNumber,conn,dltId);
					
					
					
				}
				else
				{
					DBM.messageurl1(contactNumber, messageTemp,addNumber,conn,DBM.schoolId(),dltId);
					
					
				}
			}


			/*if(messageParents==true && messageStudents==false)
			{
				messageTemp="Dear Parent,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			}
			else if(messageParents==false && messageStudents==true)
			{
				messageTemp="Dear Student,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			}
			else if(messageParents==true && messageStudents==true)
			{
				messageTemp="Dear Parent/Student,\n"+typeMessage+"\nRegards,\n"+ls.getSmsSchoolName();
			}

//			//(contactNumber);
//			//(messageTemp);
			DBM.messageurl1(contactNumber, messageTemp,addNumber,conn);*/


			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Please select atleast one student!"));
		}

		language = "english";
		englishShow=true;
		hindiShow=false;
		classList=selectedClassList=null;
		studentList=selectedStudentList=null;
		typeMessage=messageCategory=className1=null;
		show=showClassList=showclassName=messageParents=messageStudents=false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checklang() {
		Connection conn = DataBaseConnection.javaConnection();
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(language, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", language, new DatabaseMethods1().schoolId(), conn);
		keyWord = "-1";
		checkLanguage();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showData() {
		Connection conn = DataBaseConnection.javaConnection();

		allTemplates = new DltDatabaseMethod().getAllTemplates(keyWord, language, new DatabaseMethods1().schoolId(),conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public void checkGroupSeelcted() {
		
		Connection conn = DataBaseConnection.javaConnection();
		typeMessage = selectedTemp.getContent();
		dltId = selectedTemp.getDltId();
		if(selectedTemp.getType().equalsIgnoreCase("dynamic")) {
			editable=true;
		}else {
			editable= false;
		}
		
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public String getClassName1() {
		return className1;
	}
	public void setClassName1(String className1) {
		this.className1 = className1;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShowclassName() {
		return showclassName;
	}
	public void setShowclassName(boolean showclassName) {
		this.showclassName = showclassName;
	}
	public ArrayList<SelectItem> getClassListWithSecetion() {
		return classListWithSecetion;
	}
	public void setClassListWithSecetion(ArrayList<SelectItem> classListWithSecetion) {
		this.classListWithSecetion = classListWithSecetion;
	}
	public String getMessageCategory() {
		return messageCategory;
	}
	public void setMessageCategory(String messageCategory) {
		this.messageCategory = messageCategory;
	}
	public boolean isShowClassList() {
		return showClassList;
	}
	public void setShowClassList(boolean showClassList) {
		this.showClassList = showClassList;
	}
	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}
	public ArrayList<ClassInfo> getSelectedClassList() {
		return selectedClassList;
	}
	public void setSelectedClassList(ArrayList<ClassInfo> selectedClassList) {
		this.selectedClassList = selectedClassList;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public boolean isHindiShow() {
		return hindiShow;
	}
	public void setHindiShow(boolean hindiShow) {
		this.hindiShow = hindiShow;
	}
	public boolean isEnglishShow() {
		return englishShow;
	}
	public void setEnglishShow(boolean englishShow) {
		this.englishShow = englishShow;
	}

	public boolean isMessageParents() {
		return messageParents;
	}

	public void setMessageParents(boolean messageParents) {
		this.messageParents = messageParents;
	}

	public boolean isMessageStudents() {
		return messageStudents;
	}

	public void setMessageStudents(boolean messageStudents) {
		this.messageStudents = messageStudents;
	}

	public boolean isShowInstitute() {
		return showInstitute;
	}

	public void setShowInstitute(boolean showInstitute) {
		this.showInstitute = showInstitute;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public ArrayList<String> getSelectedSectionArr() {
		return selectedSectionArr;
	}

	public void setSelectedSectionArr(ArrayList<String> selectedSectionArr) {
		this.selectedSectionArr = selectedSectionArr;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public ArrayList<SelectItem> getAllKeywords() {
		return allKeywords;
	}

	public void setAllKeywords(ArrayList<SelectItem> allKeywords) {
		this.allKeywords = allKeywords;
	}

	public ArrayList<DltTemplateInfo> getAllTemplates() {
		return allTemplates;
	}

	public void setAllTemplates(ArrayList<DltTemplateInfo> allTemplates) {
		this.allTemplates = allTemplates;
	}

	public DltTemplateInfo getSelectedTemp() {
		return selectedTemp;
	}

	public void setSelectedTemp(DltTemplateInfo selectedTemp) {
		this.selectedTemp = selectedTemp;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	
}
