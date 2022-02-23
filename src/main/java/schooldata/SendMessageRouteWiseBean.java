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

import student_module.DataBaseOnlineAdm;

@ManagedBean(name="sendMessageRouteWise")
@ViewScoped
public class SendMessageRouteWiseBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedRoute,typeMessage,language,selectedStop,balMsg,userType;
	ArrayList<SelectItem> routeNameList;
	boolean show,hindiShow,englishShow,sms,notification;
	ArrayList<StudentInfo> studentList,selectedStudentList,list;
	ArrayList<RouteDetail> routeDetailList;
	SchoolInfoList ls;
	double smsLimit;
	ArrayList<SelectItem>stopList=new ArrayList<>();

	public void getStudentRoute()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		smsLimit = obj.smsLimitReminder(obj.schoolId(), conn);

		//hindiShow=false;englishShow=true;language="english";
		routeDetailList=obj.getAllStudentRoute(selectedRoute,conn);
		studentList=obj.getAllStudentStop1(routeDetailList,conn);
		stopList=obj.selectedRouteStopList(selectedRoute, conn);
		if(studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record found", "Validation Error"));
			show=false;
		}
		else
		{
			show=true;
		}

		ls = obj.fullSchoolInfo(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void getStudentRouteStop()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		////// // System.out.println(selectedStop);
		if(selectedStop.equalsIgnoreCase("select"))
		{
			studentList=obj.getAllStudentStop1(routeDetailList,conn);
		}
		else
		{
			studentList=obj.studentStopWiseRecord(selectedStop, conn);
		}

		if(studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record found", "Validation Error"));
			show=false;
		}
		else
		{
			show=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkLanguage()
	{
		typeMessage="";
		if(language.equalsIgnoreCase("english"))
		{
			englishShow=true;hindiShow=false;
		}
		else
		{
			//language = "english";
			//PrimeFaces.current().executeInitScript("PF('hindiDi').show();");

				englishShow=false;hindiShow=true;
		}
	}
	
	public void checkRouteSelected()
	{
		if(selectedStudentList.size()>0)
		{
			PrimeFaces.current().executeInitScript("PF('SendMessageCheck').show()");
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Atleast One Receiver From Staff List.", "Alert"));

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

		DatabaseMethods1 obj=new DatabaseMethods1();

		ls=obj.fullSchoolInfo(conn);
		String message="";
		if(language.equalsIgnoreCase("hindi"))
		{
			message="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			////// // System.out.println(message.length());
		}
		else
		{
			message="Dear Parent,\n"+typeMessage+" \nRegards, \n"+ls.getSmsSchoolName();
		}

		if(selectedStudentList.size()>0)
		{
			String contactno="",addNumber="";
			if(ls.getCountry().equalsIgnoreCase("India"))
			{
				for(StudentInfo ss : selectedStudentList)
				{
					if (String.valueOf(ss.getFathersPhone()).length() == 10
							&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
							&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
							&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
							&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
							&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
					{
						if (contactno.equals(""))
						{
							contactno = String.valueOf(ss.getFathersPhone());
						}
						else
						{
							contactno = contactno + "," + String.valueOf(ss.getFathersPhone());
						}

						if (addNumber.equals(""))
						{
							addNumber = ss.getAddNumber();
						}
						else
						{
							addNumber = addNumber + "," + ss.getAddNumber();
						}
					}

					if(notification)
					{
						obj.notification(ls.getSchid(),"Message",message, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+ls.getSchid(),conn);
						obj.notification(ls.getSchid(),"Message",message, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+ls.getSchid(),conn);
					}

				}
			}
			else
			{
				if(sms)
				{
					String tp = typeMessage;
					ArrayList<StudentInfo> newList = selectedStudentList;
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
			}

			if(sms)
			{
				if(ls.getCountry().equalsIgnoreCase("India"))
				{
					if(language.equalsIgnoreCase("hindi"))
					{
						//message="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
						////// // System.out.println(message.length());
						obj.messageurlHindi(contactno, message,addNumber,conn,"");
					}
					else
					{
						//message="Dear Parent,\n"+typeMessage+" \nRegards, \n"+ls.getSmsSchoolName();
						obj.messageurl1(contactno, message,addNumber,conn,obj.schoolId(),"");
					}
				}
			}

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
		return "sendMessageRouteWise.xhtml";
	}
	
	public void closeConfirmDialog()
	{
		PrimeFaces.current().executeInitScript("PF('SendMessageCheck').hide()");
		
	}

	public String sendMsg() throws UnsupportedEncodingException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		ls=obj.fullSchoolInfo(conn);
		String message="";
		if(language.equalsIgnoreCase("hindi"))
		{
			message="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
			////// // System.out.println(message.length());
		}
		else
		{
			message="Dear Parent,\n"+typeMessage+" \nRegards, \n"+ls.getSmsSchoolName();
		}

		if(selectedStudentList.size()>0)
		{
			String contactno="",addNumber="";
			for(StudentInfo ss : selectedStudentList)
			{

				if (String.valueOf(ss.getFathersPhone()).length() == 10
						&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
						&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
						&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
						&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
						&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
				{
					if (contactno.equals(""))
					{
						contactno = String.valueOf(ss.getFathersPhone());
					}
					else
					{
						contactno = contactno + "," + String.valueOf(ss.getFathersPhone());
					}

					if (addNumber.equals(""))
					{
						addNumber = ss.getAddNumber();
					}
					else
					{
						addNumber = addNumber + "," + ss.getAddNumber();
					}
				}

				if(notification)
				{
					obj.notification(ls.getSchid(),"Message",message, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+ls.getSchid(),conn);
					obj.notification(ls.getSchid(),"Message",message, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+ls.getSchid(),conn);
				}

			}

			if(sms)
			{
				if(language.equalsIgnoreCase("hindi"))
				{
					//message="प्रिय अभिभावक,\n"+typeMessage+"\nसादर,\n"+ls.getHindiName();
					////// // System.out.println(message.length());
					obj.messageurlHindi(contactno, message,addNumber,conn,"");
				}
				else
				{
					//message="Dear Parent,\n"+typeMessage+" \nRegards, \n"+ls.getSmsSchoolName();
					obj.messageurl1(contactno, message,addNumber,conn,obj.schoolId(),"");
				}
			}

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
		return "sendMessageRouteWise.xhtml";
	}

	public SendMessageRouteWiseBean()
	{

		language="english";
		englishShow=true;
		hindiShow=false;
		sms=true;
		notification=false;

		Connection conn=DataBaseConnection.javaConnection();
		routeNameList=new DatabaseMethods1().allTransportRoutes(conn);
		ls = new DatabaseMethods1().fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public String getSelectedRoute() {
		return selectedRoute;
	}
	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}
	public ArrayList<SelectItem> getRouteNameList() {
		return routeNameList;
	}
	public void setRouteNameList(ArrayList<SelectItem> routeNameList) {
		this.routeNameList = routeNameList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public ArrayList<RouteDetail> getRouteDetailList() {
		return routeDetailList;
	}
	public void setRouteDetailList(ArrayList<RouteDetail> routeDetailList) {
		this.routeDetailList = routeDetailList;
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
	public String getSelectedStop() {
		return selectedStop;
	}
	public void setSelectedStop(String selectedStop) {
		this.selectedStop = selectedStop;
	}
	public ArrayList<SelectItem> getStopList() {
		return stopList;
	}
	public void setStopList(ArrayList<SelectItem> stopList) {
		this.stopList = stopList;
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


}
