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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="viewAllAppointmentRequest")
@ViewScoped
public class ViewAllAppointmentRequestBean implements Serializable {
	int approved,pending,rejected;
	String remark,status,schoolId,username,type;
	ArrayList<AppointmentInfo>appointmentList = new ArrayList<>();
	AppointmentInfo selected;
	boolean showDelete,showRemark;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	DatabaseMethods1 obj2=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public ViewAllAppointmentRequestBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId = (String) ss.getAttribute("schoolid");
		username=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");
		approved=obj.countAllAppointment(conn,"approved",obj2.schoolId(),"consent_table");
		pending=obj.countAllAppointment(conn,"pending",obj2.schoolId(),"consent_table");
		rejected=obj.countAllAppointment(conn,"rejected",obj2.schoolId(),"consent_table");

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

		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"pending",schoolId,type,username);
		showRemark=false;
		showDelete=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void allApprovedList()
	{
		Connection conn = DataBaseConnection.javaConnection();

		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"approved",schoolId,type,username);
		showRemark=true;
		showDelete=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allRejectedList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		appointmentList=obj.selectAllTypeTeacherAppointmentList(conn,"rejected",schoolId,type,username);
		showRemark=true;
		showDelete=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = obj.deleteAppointmentRequest(conn,selected.getId(),"consent_table");
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meeting request deleted successfully!"));
			approved=obj.countAllAppointment(conn,"approved",schoolId,"consent_table");
			pending=obj.countAllAppointment(conn,"pending",schoolId,"consent_table");
			rejected=obj.countAllAppointment(conn,"rejected",schoolId,"consent_table");
			allPendingList();
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
		
		value = "Id-"+selected.getId();
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Appointment Request","WEB",value,conn);
		return refNo;
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
	public boolean isShowDelete() {
		return showDelete;
	}
	public void setShowDelete(boolean showDelete) {
		this.showDelete = showDelete;
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
	public boolean isShowRemark() {
		return showRemark;
	}
	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}
}