package schooldata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.primefaces.model.file.UploadedFile;

public class HomeWorkInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	String sNo,id, teacherId,day,teacherName, subjectId,subjectName,uploadDateStr, openingDateStr,type,classId,className,sectionId,sectionName,description,assignmentName,studentName;
	String assignment_photo1, assignment_photo2, assignment_photo3, assignment_photo4, assignment_photo5,session,tag,imageName,photo;
	Date uploadDate,openingDate;
	String ass1,ass2,ass3,ass4,ass5; 
	boolean showFile;
	transient
	UploadedFile file;
	int image;
	ArrayList<SelectItem> imageList;
	public String getsNo()
	{
		return sNo;
	}
	public void setsNo(String sNo) {
		this.sNo = sNo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getUploadDateStr() {
		return uploadDateStr;
	}
	public void setUploadDateStr(String uploadDateStr) {
		this.uploadDateStr = uploadDateStr;
	}
	public String getOpeningDateStr() {
		return openingDateStr;
	}
	public void setOpeningDateStr(String openingDateStr) {
		this.openingDateStr = openingDateStr;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAssignmentName() {
		return assignmentName;
	}
	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}
	public String getAssignment_photo1() {
		return assignment_photo1;
	}
	public void setAssignment_photo1(String assignment_photo1) {
		this.assignment_photo1 = assignment_photo1;
	}
	public String getAssignment_photo2() {
		return assignment_photo2;
	}
	public void setAssignment_photo2(String assignment_photo2) {
		this.assignment_photo2 = assignment_photo2;
	}
	public String getAssignment_photo3() {
		return assignment_photo3;
	}
	public void setAssignment_photo3(String assignment_photo3) {
		this.assignment_photo3 = assignment_photo3;
	}
	public String getAssignment_photo4() {
		return assignment_photo4;
	}
	public void setAssignment_photo4(String assignment_photo4) {
		this.assignment_photo4 = assignment_photo4;
	}
	public String getAssignment_photo5() {
		return assignment_photo5;
	}
	public void setAssignment_photo5(String assignment_photo5) {
		this.assignment_photo5 = assignment_photo5;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Date getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public ArrayList<SelectItem> getImageList() {
		return imageList;
	}
	public void setImageList(ArrayList<SelectItem> imageList) {
		this.imageList = imageList;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getAss1() {
		return ass1;
	}
	public void setAss1(String ass1) {
		this.ass1 = ass1;
	}
	public String getAss2() {
		return ass2;
	}
	public void setAss2(String ass2) {
		this.ass2 = ass2;
	}
	public String getAss3() {
		return ass3;
	}
	public void setAss3(String ass3) {
		this.ass3 = ass3;
	}
	public String getAss4() {
		return ass4;
	}
	public void setAss4(String ass4) {
		this.ass4 = ass4;
	}
	public String getAss5() {
		return ass5;
	}
	public void setAss5(String ass5) {
		this.ass5 = ass5;
	}
	public boolean isShowFile() {
		return showFile;
	}
	public void setShowFile(boolean showFile) {
		this.showFile = showFile;
	}
	

}
