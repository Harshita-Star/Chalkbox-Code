package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="projectModule")
@ViewScoped

public class ProjectModuleBean implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	boolean studentType,classType,employeeType,economyType,transportType,attendenceType,performanceType,messageType,tcType,promotionType,
	previousType,feeattType=false,feeregistrationType=false,feeenquType=false,feesubmitType=false,complaintType=false,birthdayType=false,
	eventNotify=false,marksheetNotify=false,resultNotify=false,elearningNotify=false,meetingNotify=false;

	ArrayList<SelectItem> list,showList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String schoolId,sessionValue;
	String feeTemplate="", enqTemplate="", attTemplate="", regTemplate="", complaintTemplate="", birthTemplate="";

	public ProjectModuleBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		ArrayList<String>message= obj.checkmessageSetting(conn);
		ArrayList<String>template= obj.checktemplateSetting(conn);
		if(message.get(0).equals("true"))
		{
			feesubmitType=true;
		}
		if(message.get(1).equals("true"))
		{
			feeregistrationType=true;
		}
		if(message.get(2).equals("true"))
		{
			feeenquType=true;
		}
		if(message.get(3).equals("true"))
		{
			feeattType=true;
		}
		if(message.get(4).equals("true"))
		{
			complaintType=true;
		}
		if(message.get(5).equals("true"))
		{
			birthdayType=true;
		}
		if(message.get(6).equals("true"))
		{
			eventNotify=true;
		}
		if(message.get(7).equals("true"))
		{
			marksheetNotify=true;
		}
		if(message.get(8).equals("true"))
		{
			resultNotify=true;
		}
		if(message.get(9).equals("true"))
		{
			elearningNotify=true;
		}
		if(message.get(10).equals("true"))
		{
			meetingNotify=true;
		}

		feeTemplate = template.get(0);
		regTemplate = template.get(1);
		enqTemplate = template.get(2);
		attTemplate = template.get(3);
		complaintTemplate = template.get(4);
		birthTemplate = template.get(5);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public void operation()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(showList.size()>0)
		{
			int i=obj.deleteAllModules(conn);
			if(i>=1)
			{
				insert(conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed!!!Try Again"));
			}
		}
		else
		{
			onlyInsert(conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void onlyInsert(Connection conn)
	{
		list=new ArrayList<>();
		if(studentType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("student");
			ii.setLabel("main");
			list.add(ii);
		}
		if(classType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("class");
			ii.setLabel("main");
			list.add(ii);
		}
		if(employeeType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("employee");
			ii.setLabel("main");
			list.add(ii);
		}
		if(economyType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("finance");
			ii.setLabel("main");
			list.add(ii);
		}
		if(transportType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("transport");
			ii.setLabel("main");
			list.add(ii);
		}
		if(attendenceType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("attendence");
			ii.setLabel("main");
			list.add(ii);
		}
		if(performanceType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("performance");
			ii.setLabel("main");
			list.add(ii);
		}
		if(messageType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("message");
			ii.setLabel("main");
			list.add(ii);
		}
		if(tcType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("tc");
			ii.setLabel("main");
			list.add(ii);
		}
		if(promotionType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("promotion");
			ii.setLabel("main");
			list.add(ii);
		}
		if(previousType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("previous");
			ii.setLabel("main");
			list.add(ii);
		}

		if(list.size()>0)
		{
			int n=obj.insertProjectModules(list,conn);
			if(n>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Modules Added Succefully! Please logout and Proceed!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Module Addition Failed!!!Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select modules to add!"));
		}

	}

	public void insert(Connection conn)
	{
		list=new ArrayList<>();
		if(studentType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("student");
			ii.setLabel("main");
			list.add(ii);
		}
		if(classType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("class");
			ii.setLabel("main");
			list.add(ii);
		}
		if(employeeType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("employee");
			ii.setLabel("main");
			list.add(ii);
		}
		if(economyType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("finance");
			ii.setLabel("main");
			list.add(ii);
		}
		if(transportType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("transport");
			ii.setLabel("main");
			list.add(ii);
		}
		if(attendenceType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("attendence");
			ii.setLabel("main");
			list.add(ii);
		}
		if(performanceType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("performance");
			ii.setLabel("main");
			list.add(ii);
		}
		if(messageType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("message");
			ii.setLabel("main");
			list.add(ii);
		}
		if(tcType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("tc");
			ii.setLabel("main");
			list.add(ii);
		}
		if(promotionType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("promotion");
			ii.setLabel("main");
			list.add(ii);
		}
		if(previousType==true)
		{
			SelectItem ii=new SelectItem();
			ii.setValue("previous");
			ii.setLabel("main");
			list.add(ii);
		}


		if(list.size()>0)
		{
			int n=obj.insertProjectModules(list,conn);
			if(n>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Modules Added Succefully! Please logout and Proceed!"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Module Addition Failed!!!Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("All Modules Are Deleted!!"));
		}

	}


	public void messagedefination()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i =obj.updateMessage(feeenquType,feeattType,feesubmitType,feeregistrationType,complaintType,birthdayType,conn,feeTemplate,regTemplate,enqTemplate,attTemplate,complaintTemplate,birthTemplate,eventNotify,marksheetNotify,resultNotify,elearningNotify,meetingNotify);
		if(i>0)
		{
			String refNo;
		     try {
				  refNo=addWorkLog(conn);
				}
		     catch (Exception e) {
				}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Your Message Setting Update Successfully"));
			ArrayList<String>message=obj.checkmessageSetting(conn);
			HttpSession ss= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("feesubmit", message.get(0));
			ss.setAttribute("registration", message.get(1));
			ss.setAttribute("enquiry", message.get(2));
			ss.setAttribute("attendance", message.get(3));
			ss.setAttribute("complaint", message.get(4));
			ss.setAttribute("birthday", message.get(5));
			ss.setAttribute("eventNotify", message.get(6));
			ss.setAttribute("marksheetNotify", message.get(7));
			ss.setAttribute("resultNotify", message.get(8));
			ss.setAttribute("elearningNotify", message.get(9));
			ss.setAttribute("meetingNotify", message.get(10));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = feeTemplate+"---"+regTemplate+"---"+enqTemplate+"---"+attTemplate+"---"+complaintTemplate+"---"+birthTemplate;
		String language= feeenquType+"---"+feeattType+"---"+feesubmitType+"---"+feeregistrationType+"---"+complaintType+"---"+birthdayType+"---"+eventNotify+"---"+marksheetNotify+"---"+resultNotify+"---"+elearningNotify+"---"+meetingNotify;
		
		String refNo = workLg.saveWorkLogMehod(language,"Update Message Settings","WEB",value,conn);
		return refNo;
	
	}

	public boolean isStudentType() {
		return studentType;
	}

	public void setStudentType(boolean studentType) {
		this.studentType = studentType;
	}

	public boolean isClassType() {
		return classType;
	}

	public void setClassType(boolean classType) {
		this.classType = classType;
	}

	public boolean isEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(boolean employeeType) {
		this.employeeType = employeeType;
	}

	public boolean isEconomyType() {
		return economyType;
	}

	public void setEconomyType(boolean economyType) {
		this.economyType = economyType;
	}

	public boolean isTransportType() {
		return transportType;
	}

	public void setTransportType(boolean transportType) {
		this.transportType = transportType;
	}

	public boolean isAttendenceType() {
		return attendenceType;
	}

	public void setAttendenceType(boolean attendenceType) {
		this.attendenceType = attendenceType;
	}

	public boolean isPerformanceType() {
		return performanceType;
	}

	public void setPerformanceType(boolean performanceType) {
		this.performanceType = performanceType;
	}

	public boolean isMessageType() {
		return messageType;
	}

	public void setMessageType(boolean messageType) {
		this.messageType = messageType;
	}

	public boolean isTcType() {
		return tcType;
	}

	public void setTcType(boolean tcType) {
		this.tcType = tcType;
	}

	public boolean isPromotionType() {
		return promotionType;
	}

	public void setPromotionType(boolean promotionType) {
		this.promotionType = promotionType;
	}

	public boolean isPreviousType() {
		return previousType;
	}

	public void setPreviousType(boolean previousType) {
		this.previousType = previousType;
	}

	public boolean isFeeattType() {
		return feeattType;
	}

	public void setFeeattType(boolean feeattType) {
		this.feeattType = feeattType;
	}

	public boolean isFeeregistrationType() {
		return feeregistrationType;
	}

	public void setFeeregistrationType(boolean feeregistrationType) {
		this.feeregistrationType = feeregistrationType;
	}

	public boolean isFeeenquType() {
		return feeenquType;
	}

	public void setFeeenquType(boolean feeenquType) {
		this.feeenquType = feeenquType;
	}

	public boolean isFeesubmitType() {
		return feesubmitType;
	}

	public void setFeesubmitType(boolean feesubmitType) {
		this.feesubmitType = feesubmitType;
	}

	public boolean isComplaintType() {
		return complaintType;
	}

	public void setComplaintType(boolean complaintType) {
		this.complaintType = complaintType;
	}

	public boolean isBirthdayType() {
		return birthdayType;
	}

	public void setBirthdayType(boolean birthdayType) {
		this.birthdayType = birthdayType;
	}

	public ArrayList<SelectItem> getList() {
		return list;
	}

	public void setList(ArrayList<SelectItem> list) {
		this.list = list;
	}

	public ArrayList<SelectItem> getShowList() {
		return showList;
	}

	public void setShowList(ArrayList<SelectItem> showList) {
		this.showList = showList;
	}

	public String getFeeTemplate() {
		return feeTemplate;
	}

	public void setFeeTemplate(String feeTemplate) {
		this.feeTemplate = feeTemplate;
	}

	public String getEnqTemplate() {
		return enqTemplate;
	}

	public void setEnqTemplate(String enqTemplate) {
		this.enqTemplate = enqTemplate;
	}

	public String getAttTemplate() {
		return attTemplate;
	}

	public void setAttTemplate(String attTemplate) {
		this.attTemplate = attTemplate;
	}

	public String getRegTemplate() {
		return regTemplate;
	}

	public void setRegTemplate(String regTemplate) {
		this.regTemplate = regTemplate;
	}

	public String getComplaintTemplate() {
		return complaintTemplate;
	}

	public void setComplaintTemplate(String complaintTemplate) {
		this.complaintTemplate = complaintTemplate;
	}

	public String getBirthTemplate() {
		return birthTemplate;
	}

	public void setBirthTemplate(String birthTemplate) {
		this.birthTemplate = birthTemplate;
	}

	public boolean isEventNotify() {
		return eventNotify;
	}

	public void setEventNotify(boolean eventNotify) {
		this.eventNotify = eventNotify;
	}

	public boolean isMarksheetNotify() {
		return marksheetNotify;
	}

	public void setMarksheetNotify(boolean marksheetNotify) {
		this.marksheetNotify = marksheetNotify;
	}

	public boolean isResultNotify() {
		return resultNotify;
	}

	public void setResultNotify(boolean resultNotify) {
		this.resultNotify = resultNotify;
	}

	public boolean isElearningNotify() {
		return elearningNotify;
	}

	public void setElearningNotify(boolean elearningNotify) {
		this.elearningNotify = elearningNotify;
	}

	public boolean isMeetingNotify() {
		return meetingNotify;
	}

	public void setMeetingNotify(boolean meetingNotify) {
		this.meetingNotify = meetingNotify;
	}
	



}
