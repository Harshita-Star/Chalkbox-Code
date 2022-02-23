package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import dlt_template.DltDatabaseMethod;
import dlt_template.DltTemplateInfo;

@ManagedBean(name="enqComm")
@ViewScoped

public class EnquiryCommunicationBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> leadList,selectedList;
	StudentInfo1 selectedEnquiry;
	String userId, type, status,balMsg;
	String lang,uType,typemessage;
	boolean showEnglish,showHindi;
	DatabaseMethods1 obj = new DatabaseMethods1();
	SchoolInfoList info;
	double smsLimit;
	ArrayList<SelectItem>sessionList;
	String selectedSession,enqType="Pending";
	
	

	String keyWord, dltId;
	ArrayList<SelectItem> allKeywords = new ArrayList<>();
	ArrayList<DltTemplateInfo> allTemplates = new ArrayList<>();
	DltTemplateInfo selectedTemp;
	boolean editable;
	
	
	public void checklang() {
		Connection conn = DataBaseConnection.javaConnection();
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", lang, new DatabaseMethods1().schoolId(), conn);
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

		allTemplates = new DltDatabaseMethod().getAllTemplates(keyWord, lang, new DatabaseMethods1().schoolId(),
				conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkGroupSeelcted() {

		Connection conn = DataBaseConnection.javaConnection();
		typemessage = selectedTemp.getContent();
		dltId = selectedTemp.getDltId();
		if (selectedTemp.getType().equalsIgnoreCase("dynamic")) {
			editable = true;
		} else {
			editable = false;
		}

		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public EnquiryCommunicationBean() {
		showEnglish = true;
		showHindi = false;
		lang="english";
		typemessage="";
		//selectedList=new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId = (String) ss.getAttribute("username");
		type = (String) ss.getAttribute("type");
		//SchoolInfoList list=new DatabaseMethods1().fullSchoolInfo(conn);
		
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


		
		
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		//info = obj.fullSchoolInfo(conn);
		
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", lang, new DatabaseMethods1().schoolId(), conn);
		
		if(enqType.equalsIgnoreCase("Pending"))
		{
			leadList = new DataBaseMeathodJson().pendingEnquiryList(new DatabaseMethods1().schoolId(),selectedSession, conn);

		}
		else
		{
			leadList = new DataBaseMeathodJson().deniedEnquiryList(new DatabaseMethods1().schoolId(),selectedSession, conn);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkLanguage()
	{
		typemessage = "";
		if(lang.equalsIgnoreCase("english"))
		{
			showEnglish = true;
			showHindi = false;
		}
		else
		{
		//	lang = "english";
			showEnglish = false;
			showHindi = true;
			
			//PrimeFaces.current().ajax().update("translator");
			//PrimeFaces.current().executeInitScript("PF('hindiDi').show();");

		}
	}
	
	
	public void closeConfirmDialog()
	{
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		
	}
	
	public void checkEnqSelected()
	{
		if(selectedList.size()>0)
		{
			checkGroupSeelcted();
		 // PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select student(s) to send SMS alert."));
		}
	}

	
	public void sessionWiseList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		leadList = new ArrayList<StudentInfo>();
		
		if(enqType.equalsIgnoreCase("Pending"))
		{
			leadList = new DataBaseMeathodJson().pendingEnquiryList(new DatabaseMethods1().schoolId(),selectedSession, conn);
              
		}
		else
		{
			leadList = new DataBaseMeathodJson().deniedEnquiryList(new DatabaseMethods1().schoolId(),selectedSession, conn);
		
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public String sendMessage() throws UnsupportedEncodingException
	{
		if(selectedList.size()>0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (type.equalsIgnoreCase("admin"))
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
				else
				{
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sendEnqSms();
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
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
				return "";
			}
			else
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sendEnqSms();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select student(s) to send SMS alert."));
			return "";
		}
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		return "";
	}

	public void sendEnqSms() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int check = 0;
		SchoolInfoList ls = obj.fullSchoolInfo(conn);
		String contactno = "", addNumber = "";
//		String msg = "Dear Parent,\n" + typemessage + "\nRegards,\n" + ls.getSmsSchoolName();
//		if(lang.equalsIgnoreCase("hindi"))
//		{
//			msg="प्रिय अभिभावक,\n"+typemessage+"\nसादर,\n"+ls.getHindiName();
//		}
		
		//DLt Template Message
		
		String msg =  typemessage + "\nRegards,\n" + ls.getSmsSchoolName();
		if(lang.equalsIgnoreCase("hindi"))
		{
			msg=typemessage+"\nसादर,\n"+ls.getHindiName();
		}

		for (StudentInfo ls11 : selectedList) {
			check++;
			if (String.valueOf(ls11.getContactNo()).length() == 10
					&& !String.valueOf(ls11.getContactNo()).equals("2222222222")
					&& !String.valueOf(ls11.getContactNo()).equals("9999999999")
					&& !String.valueOf(ls11.getContactNo()).equals("1111111111")
					&& !String.valueOf(ls11.getContactNo()).equals("1234567890")
					&& !String.valueOf(ls11.getContactNo()).equals("0123456789"))
			{
				if (contactno.equals("")) {
					contactno = String.valueOf(ls11.getContactNo());
				} else {
					contactno = contactno + "," + String.valueOf(ls11.getContactNo());
				}

				if (addNumber.equals("")) {
					addNumber = ls11.getFname();

				} else {
					addNumber = addNumber + "," + ls11.getFname();
				}
			}
		}


		if (check > 0) {
//			msg = "Dear Parent,\n" + typemessage + "\nRegards,\n" + ls.getSmsSchoolName();
//			if(lang.equalsIgnoreCase("hindi"))
//			{
//				msg="प्रिय अभिभावक,\n"+typemessage+"\nसादर,\n"+ls.getHindiName();
			
			//Dlt Templapte Message
			
			msg = typemessage + "\nRegards,\n" + ls.getSmsSchoolName();
			if(lang.equalsIgnoreCase("hindi"))
			{
				msg=typemessage+"\nसादर,\n"+ls.getHindiName();
				//// // System.out.println(msg.length());
				obj.messageurlHindiEnq(contactno, msg,addNumber,conn,dltId);
			}
			else
			{
				obj.messageurlenq(contactno, msg,addNumber,conn,dltId);
			}


			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message sent successfully!"));
			showEnglish = true;
			showHindi = false;
			lang="english";
			typemessage="";
			selectedList=new ArrayList<>();
			//obj.messageurl1(contactno, msg, addNumber, conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to send SMS alert!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getLeadList() {
		return leadList;
	}

	public void setLeadList(ArrayList<StudentInfo> leadList) {
		this.leadList = leadList;
	}

	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public StudentInfo1 getSelectedEnquiry() {
		return selectedEnquiry;
	}

	public void setSelectedEnquiry(StudentInfo1 selectedEnquiry) {
		this.selectedEnquiry = selectedEnquiry;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getuType() {
		return uType;
	}

	public void setuType(String uType) {
		this.uType = uType;
	}

	public String getTypemessage() {
		return typemessage;
	}

	public void setTypemessage(String typemessage) {
		this.typemessage = typemessage;
	}

	public boolean isShowEnglish() {
		return showEnglish;
	}

	public void setShowEnglish(boolean showEnglish) {
		this.showEnglish = showEnglish;
	}

	public boolean isShowHindi() {
		return showHindi;
	}

	public void setShowHindi(boolean showHindi) {
		this.showHindi = showHindi;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public double getSmsLimit() {
		return smsLimit;
	}

	public void setSmsLimit(double smsLimit) {
		this.smsLimit = smsLimit;
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

	public String getEnqType() {
		return enqType;
	}

	public void setEnqType(String enqType) {
		this.enqType = enqType;
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
