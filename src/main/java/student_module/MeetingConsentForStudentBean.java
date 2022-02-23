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
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="meetingConsentForStudent")
@ViewScoped
public class MeetingConsentForStudentBean implements Serializable {
	int approved,pending,rejected;
	String remark,status,schoolId,username,type,session;
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	AppointmentInfo selected;
	boolean showRemark,showApproved,showDeny;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	public MeetingConsentForStudentBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		approved=obj.countAllAppointment(conn,"approved",schoolId,"consent_table");
		pending=obj.countAllAppointment(conn,"pending",schoolId,"consent_table");
		rejected=obj.countAllAppointment(conn,"rejected",schoolId,"consent_table");
		session = obj2.selectedSessionDetails(schoolId,conn);
		
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

		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"pending",schoolId,"student",username);
		showApproved=true;
		showDeny=true;
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

		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"approved",schoolId,"student",username);
		showApproved=false;
		showDeny=false;
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
		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"rejected",schoolId,"student",username);
		showApproved=false;
		showDeny=false;
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
		int i=obj.updateStatusByAdmin(conn,selected.getId(),"approved",selected.getRemark(),username,"consent_table");
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request approved successfully!"));
			StudentInfo info=DBJ.studentDetailslistByAddNo(username,schoolId,conn);

			String msg="";
			if(selected.getType().equalsIgnoreCase("Consent"))
			{
				msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has approved the consent. Please open the app to check the details.";
			}
			else
			{
				msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has accepted the meeting request for date "+selected.getStrAppointmentdate()+ " "+selected.getAppointment_time()+ ". Please open the app to check the details.";
			}
			DBJ.adminnotification(selected.getType(),msg,"admin-"+schoolId,schoolId,selected.getType()+"Approve-"+username,conn);


			allPendingList();
			approved=obj.countAllAppointment(conn,"approved",schoolId,"consent_table");
			pending=obj.countAllAppointment(conn,"pending",schoolId,"consent_table");
			rejected=obj.countAllAppointment(conn,"rejected",schoolId,"consent_table");
			
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
	public void rejectedStatus()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=obj.updateStatusByAdmin(conn,selected.getId(),"rejected",selected.getRemark(),username,"consent_table");
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Request denied successfully!"));
			StudentInfo info=DBJ.studentDetailslistByAddNo(username,schoolId,conn);

			String msg="";
			if(selected.getType().equalsIgnoreCase("Consent"))
			{
				msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has rejected the consent. Please open the app to check the details.";
			}
			else
			{
				msg="Parent of "+info.getFname()+", class "+info.getClassName()+" has rejected the meeting request for date "+selected.getStrAppointmentdate()+ " "+selected.getAppointment_time()+ ". Please open the app to check the details.";
			}
			DBJ.adminnotification(selected.getType(),msg,"admin-"+schoolId,schoolId,selected.getType()+"Reject-"+username,conn);


			allPendingList();
			approved=obj.countAllAppointment(conn,"approved",schoolId,"consent_table");
			pending=obj.countAllAppointment(conn,"pending",schoolId,"consent_table");
			rejected=obj.countAllAppointment(conn,"rejected",schoolId,"consent_table");
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public boolean isShowDeny() {
		return showDeny;
	}
	public void setShowDeny(boolean showDeny) {
		this.showDeny = showDeny;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public DatabaseMethods1 getObj2() {
		return obj2;
	}
	public void setObj2(DatabaseMethods1 obj2) {
		this.obj2 = obj2;
	}
}