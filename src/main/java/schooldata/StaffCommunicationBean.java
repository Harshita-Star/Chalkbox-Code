package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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

@ManagedBean(name="staffComm")
@ViewScoped

public class StaffCommunicationBean implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<EmpInfo> list;
	ArrayList<SelectItem> categoryList;
	ArrayList<EmpInfo> selected;
	SchoolInfoList ls;
	String message,lang,balMsg,userType;
	boolean showEnglish,showHindi;
	double smsLimit;
	
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
		message = selectedTemp.getContent();
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

	public StaffCommunicationBean()
	{
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		lang="english";
		showEnglish=true;
		showHindi=false;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		smsLimit = obj.smsLimitReminder(obj.schoolId(), conn);
		categoryList = obj.employeeCategoryForCommunication(conn);
		list=obj.allEmpInfo(conn);
		ls = obj.fullSchoolInfo(conn);
		
		
		
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang, conn);
		allTemplates = new DltDatabaseMethod().getAllTemplates("-1", lang, new DatabaseMethods1().schoolId(), conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkLanguage()
	{
		message = "";
		if(lang.equalsIgnoreCase("english"))
		{
			showEnglish = true;
			showHindi = false;
		}
		else
		{
			showEnglish = false;
			showHindi = true;

			//lang = "english";
			//PrimeFaces.current().executeInitScript("PF('hindiDi').show();");
			//PrimeFaces.current().ajax().update("translator");
			//PrimeFaces.current().executeInitScript("PF('translateDlg').show();");

		}
	}

	public String sendMessage() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
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

		DatabaseMethods1 obj=new DatabaseMethods1();

		ls=obj.fullSchoolInfo(conn);
		if(selected.size()>0)
		{
			String contactno="",empname="";
			if(ls.getCountry().equalsIgnoreCase("India"))
			{
				for(EmpInfo ee : selected)
				{
					if(empname.equals(""))
					{
						if (ee.getMobile().length() == 10
								&& !ee.getMobile().equals("2222222222")
								&& !ee.getMobile().equals("9999999999")
								&& !ee.getMobile().equals("1111111111")
								&& !ee.getMobile().equals("1234567890")
								&& !ee.getMobile().equals("0123456789"))
						{
							empname=ee.getFname()+"@CB@"+ee.getId();
							contactno=ee.getMobile();
						}
					}
					else
					{
						if (ee.getMobile().length() == 10
								&& !ee.getMobile().equals("2222222222")
								&& !ee.getMobile().equals("9999999999")
								&& !ee.getMobile().equals("1111111111")
								&& !ee.getMobile().equals("1234567890")
								&& !ee.getMobile().equals("0123456789"))
						{
							empname=empname+","+ ee.getFname()+"@CB@"+ee.getId();
							contactno=contactno+","+ee.getMobile();
						}
					}

				}

				if(lang.equalsIgnoreCase("hindi"))
				{
				//	message="प्रिय कर्मचारी,\n"+message+"\nसादर,\n"+ls.getHindiName();
					message= message+"\nसादर,\n"+ls.getHindiName();
					obj.messageurlStaffHindi(contactno, message,empname,conn,dltId);
				}
				else
				{
//					message="Dear Staff Member,\n"+message+"\nRegards,\n"+ls.getSmsSchoolName();
					message=message+"\nRegards,\n"+ls.getSmsSchoolName();
					obj.messageurlStaff(contactno, message,empname,conn,obj.schoolId(),dltId);
				}
			}
			else
			{
				String tp = message;
				Runnable r = new Runnable()
				{
					public void run()
					{
						String heading = "<center class=\"red\">Message From "+ls.getSchoolName()+"!</center>";
						String subject = "Message From "+ls.getSchoolName();
						String msg="<center>Dear Staff Member,<br></br>"+tp+"<br></br>Regards,<br></br>"+ls.getSmsSchoolName()+"</center>";
						for(EmpInfo ee : selected)
						{
							if(ee.getEmail()!=null && !ee.getEmail().equalsIgnoreCase("") && !ee.getEmail().equalsIgnoreCase("null"))
							{
								new DataBaseOnlineAdm().doMail(ee.getEmail(), subject, heading, msg);
							}
						}
					}
				};
				new Thread(r).start();
			}



			////// // System.out.println(message);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Message Sent SuccessFully.", "Alert"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Atleast One Receiver From Staff List.", "Alert"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "staffCommunication.xhtml";
	}

	public String sendMsg() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		ls=obj.fullSchoolInfo(conn);
		if(selected.size()>0)
		{
			String contactno="",empname="";
			for(EmpInfo ee : selected)
			{
				if(empname.equals(""))
				{
					if (ee.getMobile().length() == 10
							&& !ee.getMobile().equals("2222222222")
							&& !ee.getMobile().equals("9999999999")
							&& !ee.getMobile().equals("1111111111")
							&& !ee.getMobile().equals("1234567890")
							&& !ee.getMobile().equals("0123456789"))
					{
						empname=ee.getFname()+"@CB@"+ee.getId();
						contactno=ee.getMobile();
					}
				}
				else
				{
					if (ee.getMobile().length() == 10
							&& !ee.getMobile().equals("2222222222")
							&& !ee.getMobile().equals("9999999999")
							&& !ee.getMobile().equals("1111111111")
							&& !ee.getMobile().equals("1234567890")
							&& !ee.getMobile().equals("0123456789"))
					{
						empname=empname+","+ ee.getFname()+"@CB@"+ee.getId();
						contactno=contactno+","+ee.getMobile();
					}
				}

			}


			if(lang.equalsIgnoreCase("hindi"))
			{
//				message="प्रिय कर्मचारी,\n"+message+"\nसादर,\n"+ls.getHindiName();
				message= message+"\nसादर,\n"+ls.getHindiName();
				obj.messageurlStaffHindi(contactno, message,empname,conn,dltId);
			}
			else
			{
//				message="Dear Staff Member,\n"+message+"\nRegards,\n"+ls.getSmsSchoolName();
				message=message+"\nRegards,\n"+ls.getSmsSchoolName();
				obj.messageurlStaff(contactno, message,empname,conn,obj.schoolId(),dltId);
			}

			////// // System.out.println(message);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Message Sent SuccessFully.", "Alert"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Atleast One Receiver From Staff List.", "Alert"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		return "staffCommunication.xhtml";
	}
	
	public void closeConfirmDialog()
	{
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		
	}
	
	public void checkStaffSelected()
	{
		if(selected.size()>0)
		{
			checkGroupSeelcted();
		 // PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		}
	else
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Atleast One Receiver From Staff List.", "Alert"));
	}
	
	}

	public ArrayList<EmpInfo> getList() {
		return list;
	}

	public void setList(ArrayList<EmpInfo> list) {
		this.list = list;
	}

	public ArrayList<EmpInfo> getSelected() {
		return selected;
	}

	public void setSelected(ArrayList<EmpInfo> selected) {
		this.selected = selected;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
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
