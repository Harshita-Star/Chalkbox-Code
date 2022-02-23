package student_module;

import java.io.Serializable;

public class AppointmentInfo implements Serializable{
	String strAppointmentdate,studentName,className,schId,studentId,description,status,toMeet,strAdddate,appointment_time,
	id,remark,modify_status,type,userId,addedBy;
	int sno;
	boolean showDelete,showModified,showApproved,showRejected,showRemark;
	public String getStrAppointmentdate() {
		return strAppointmentdate;
	}
	public void setStrAppointmentdate(String strAppointmentdate) {
		this.strAppointmentdate = strAppointmentdate;
	}
	public String getSchId() {
		return schId;
	}
	public void setSchId(String schId) {
		this.schId = schId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToMeet() {
		return toMeet;
	}
	public void setToMeet(String toMeet) {
		this.toMeet = toMeet;
	}
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public String getStrAdddate() {
		return strAdddate;
	}
	public void setStrAdddate(String strAdddate) {
		this.strAdddate = strAdddate;
	}
	public String getAppointment_time() {
		return appointment_time;
	}
	public void setAppointment_time(String appointment_time) {
		this.appointment_time = appointment_time;
	}
	public boolean isShowDelete() {
		return showDelete;
	}
	public void setShowDelete(boolean showDelete) {
		this.showDelete = showDelete;
	}
	public boolean isShowModified() {
		return showModified;
	}
	public void setShowModified(boolean showModified) {
		this.showModified = showModified;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public boolean isShowRemark() {
		return showRemark;
	}
	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getModify_status() {
		return modify_status;
	}
	public void setModify_status(String modify_status) {
		this.modify_status = modify_status;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
}
