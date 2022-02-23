package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="allAppointmentRequest")
@ViewScoped
public class AllAppointmentRequestBean implements Serializable {
	int approved,pending,rejected;
	String remark,status;
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	AppointmentInfo selected;
	boolean showRemark,showApproved,showRejected;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	String schid;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	public AllAppointmentRequestBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
        schid = obj2.schoolId();
		approved=obj.countAllAppointment(conn,"approved",schid,"parent_appointment");
		pending=obj.countAllAppointment(conn,"pending",schid,"parent_appointment");
		rejected=obj.countAllAppointment(conn,"rejected",schid,"parent_appointment");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		allPendingList();
	}
	public void allPendingList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		appointmentList=obj.selectAllTypeAppointmentList(conn,"pending",schid);

		showApproved=true;
		showRejected=true;
		showRemark=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allApprovedList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		appointmentList=obj.selectAllTypeAppointmentList(conn,"approved",schid);
		showApproved=false;
		showRejected=false;
		showRemark=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allRejectedList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		appointmentList=obj.selectAllTypeAppointmentList(conn,"rejected",schid);
		showApproved=false;
		showRejected=false;
		showRemark=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void details()
	{
		status="approved";
	}
	public void details1()
	{
		status="rejected";
	}
	public void submit()
	{
		if(status.equalsIgnoreCase("approved"))
		{
			approvedStatus();
		}
		else
		{
			rejectedStatus();
		}
	}



	public void approvedStatus()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) ss.getAttribute("username");
		int i=obj.updateStatusByAdmin(conn,selected.getId(),"approved",selected.getRemark(),userid,"parent_appointment");
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment request approved successfully!"));
			allPendingList();
			approved=obj.countAllAppointment(conn,"approved",schid,"parent_appointment");
			pending=obj.countAllAppointment(conn,"pending",schid,"parent_appointment");
			rejected=obj.countAllAppointment(conn,"rejected",schid,"parent_appointment");
			StudentInfo info = DBJ.studentDetailslistByAddNo(selected.getStudentId(),schid, conn);
			DBJ.notification("Appointment","Dear Parent, your appointment request for date "+selected.getStrAppointmentdate()+" "+selected.getAppointment_time()+" has been confirmed. Please check the remark from school.",info.getFathersPhone()+"-"+selected.getStudentId()+"-"+schid,schid,"",conn);
			DBJ.notification("Appointment","Dear Parent, your appointment request for date "+selected.getStrAppointmentdate()+" "+selected.getAppointment_time()+" has been confirmed. Please check the remark from school.",info.getMothersPhone()+"-"+selected.getStudentId()+"-"+schid,schid,"",conn);

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+selected.getId()+" --Remark-"+selected.getRemark()+" --Parent Appointment"; 
		
		String refNo = workLg.saveWorkLogMehod(language,"Approve Appointment","WEB",value,conn);
		return refNo;
	}
	
	
	public void rejectedStatus()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) ss.getAttribute("username");
		int i=obj.updateStatusByAdmin(conn,selected.getId(),"rejected",selected.getRemark(),userid,"parent_appointment");
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment request rejected successfully!"));
			allPendingList();
			approved=obj.countAllAppointment(conn,"approved",schid,"parent_appointment");
			pending=obj.countAllAppointment(conn,"pending",schid,"parent_appointment");
			rejected=obj.countAllAppointment(conn,"rejected",schid,"parent_appointment");
			StudentInfo info = DBJ.studentDetailslistByAddNo(selected.getStudentId(), schid, conn);
			DBJ.notification("Appointment","Dear Parent, your appointment request for date "+selected.getStrAppointmentdate()+" "+selected.getAppointment_time()+" has been cancelled. Please check the remark from school.",info.getFathersPhone()+"-"+selected.getStudentId()+"-"+schid,schid,"",conn);
			DBJ.notification("Appointment","Dear Parent, your appointment request for date "+selected.getStrAppointmentdate()+" "+selected.getAppointment_time()+" has been cancelled. Please check the remark from school.",info.getMothersPhone()+"-"+selected.getStudentId()+"-"+schid,schid,"",conn);

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+selected.getId()+" --Remark-"+selected.getRemark()+" --Parent Appointment"; 
		
		String refNo = workLg.saveWorkLogMehod(language,"Reject Appointment","WEB",value,conn);
		return refNo;
	}
	
	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) ss.getAttribute("username");
		int i=obj.updateRemarkByAdmin(conn,selected.getId(),selected.getRemark(),userid);
		if(i>=1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Remark has been updated successfully!"));
			/*if(selected.getRemark().equalsIgnoreCase("pending"))
		{
			appointmentList=obj.selectAllTypeAppointmentList(conn,"pending");
			showApproved=true;
			showRejected=true;
			showRemark=false;
		}*/
			/* if(selected.getRemark().equalsIgnoreCase("approved"))
		{
			appointmentList=obj.selectAllTypeAppointmentList(conn,"approved");
			showApproved=false;
			showRejected=false;
			showRemark=true;
		}
		else if(selected.getRemark().equalsIgnoreCase("rejected"))
		{
			appointmentList=obj.selectAllTypeAppointmentList(conn,"rejected");
			showApproved=false;
			showRejected=false;
			showRemark=true;
		}*/



		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur!"));

		}
		//return "allApointmentRequest.xhtml";
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
		
	    value += "Selected Id-"+selected.getId()+" --Remark-"+selected.getRemark(); 
		
		String refNo = workLg.saveWorkLogMehod(language,"Update Appoitment","WEB",value,conn);
		return refNo;
	}
	
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}

	public int getApproved() {
		return approved;
	}

	public void setApproved(int approved) {
		this.approved = approved;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getRejected() {
		return rejected;
	}

	public void setRejected(int rejected) {
		this.rejected = rejected;
	}
	public ArrayList<AppointmentInfo> getAppointmentList() {
		return appointmentList;
	}
	public void setAppointmentList(ArrayList<AppointmentInfo> appointmentList) {
		this.appointmentList = appointmentList;
	}
	public AppointmentInfo getSelected() {
		return selected;
	}
	public void setSelected(AppointmentInfo selected) {
		this.selected = selected;
	}
	public boolean isShowRemark() {
		return showRemark;
	}
	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}
	public boolean isShowApproved() {
		return showApproved;
	}
	public void setShowApproved(boolean showApproved) {
		this.showApproved = showApproved;
	}
	public boolean isShowRejected() {
		return showRejected;
	}
	public void setShowRejected(boolean showRejected) {
		this.showRejected = showRejected;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}


}