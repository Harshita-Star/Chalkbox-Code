package student_module;

import java.io.Serializable;

public class StudentPhotoInfo implements Serializable{
	String studentId,schid,sPhoto,fPhoto,mPhoto,sStatus,fStatus,mStatus,studentName,className,id;
	int sno;
	boolean showSphoto,showFphoto,showMphoto,showSstatus,showFstatus,showMstatus,photo1,photo2,photo3;
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}
	public String getsPhoto() {
		return sPhoto;
	}
	public void setsPhoto(String sPhoto) {
		this.sPhoto = sPhoto;
	}
	public String getfPhoto() {
		return fPhoto;
	}
	public void setfPhoto(String fPhoto) {
		this.fPhoto = fPhoto;
	}
	public String getmPhoto() {
		return mPhoto;
	}
	public void setmPhoto(String mPhoto) {
		this.mPhoto = mPhoto;
	}
	public String getsStatus() {
		return sStatus;
	}
	public void setsStatus(String sStatus) {
		this.sStatus = sStatus;
	}
	public String getfStatus() {
		return fStatus;
	}
	public void setfStatus(String fStatus) {
		this.fStatus = fStatus;
	}
	public String getmStatus() {
		return mStatus;
	}
	public void setmStatus(String mStatus) {
		this.mStatus = mStatus;
	}
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
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
	public boolean isShowSphoto() {
		return showSphoto;
	}
	public void setShowSphoto(boolean showSphoto) {
		this.showSphoto = showSphoto;
	}
	public boolean isShowFphoto() {
		return showFphoto;
	}
	public void setShowFphoto(boolean showFphoto) {
		this.showFphoto = showFphoto;
	}
	public boolean isShowMphoto() {
		return showMphoto;
	}
	public void setShowMphoto(boolean showMphoto) {
		this.showMphoto = showMphoto;
	}
	public boolean isShowSstatus() {
		return showSstatus;
	}
	public void setShowSstatus(boolean showSstatus) {
		this.showSstatus = showSstatus;
	}
	public boolean isShowFstatus() {
		return showFstatus;
	}
	public void setShowFstatus(boolean showFstatus) {
		this.showFstatus = showFstatus;
	}
	public boolean isShowMstatus() {
		return showMstatus;
	}
	public void setShowMstatus(boolean showMstatus) {
		this.showMstatus = showMstatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isPhoto1() {
		return photo1;
	}
	public void setPhoto1(boolean photo1) {
		this.photo1 = photo1;
	}
	public boolean isPhoto2() {
		return photo2;
	}
	public void setPhoto2(boolean photo2) {
		this.photo2 = photo2;
	}
	public boolean isPhoto3() {
		return photo3;
	}
	public void setPhoto3(boolean photo3) {
		this.photo3 = photo3;
	}

}
