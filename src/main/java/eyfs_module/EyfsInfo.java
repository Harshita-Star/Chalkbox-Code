package eyfs_module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.model.SelectItem;

public class EyfsInfo implements Serializable
{
	String id,gradeParameterId,gradeParameterName,schid,session,weightage;
	String mainParameterName,mainParameterId,description;
	String subParaId,subParaName;
	String classId,className,ageGrade,classMainPara;
	String termId,termName,totalMark,totalAchieved;
	ArrayList<SelectItem> classMainParaList,classSubParaList,ageGroupGradeList;
	ArrayList<SubParameterInfo> subParaList;
	String comment,stdId,name,fatherName,attachment,ageGroupId,ageGroupName;
	Map<String,String> marksMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGradeParameterId() {
		return gradeParameterId;
	}

	public void setGradeParameterId(String gradeParameterId) {
		this.gradeParameterId = gradeParameterId;
	}

	public String getGradeParameterName() {
		return gradeParameterName;
	}

	public void setGradeParameterName(String gradeParameterName) {
		this.gradeParameterName = gradeParameterName;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getWeightage() {
		return weightage;
	}

	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}

	public String getMainParameterName() {
		return mainParameterName;
	}

	public void setMainParameterName(String mainParameterName) {
		this.mainParameterName = mainParameterName;
	}

	public String getMainParameterId() {
		return mainParameterId;
	}

	public void setMainParameterId(String mainParameterId) {
		this.mainParameterId = mainParameterId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubParaId() {
		return subParaId;
	}

	public void setSubParaId(String subParaId) {
		this.subParaId = subParaId;
	}

	public String getSubParaName() {
		return subParaName;
	}

	public void setSubParaName(String subParaName) {
		this.subParaName = subParaName;
	}

	public ArrayList<SelectItem> getClassMainParaList() {
		return classMainParaList;
	}

	public void setClassMainParaList(ArrayList<SelectItem> classMainParaList) {
		this.classMainParaList = classMainParaList;
	}

	public ArrayList<SelectItem> getClassSubParaList() {
		return classSubParaList;
	}

	public void setClassSubParaList(ArrayList<SelectItem> classSubParaList) {
		this.classSubParaList = classSubParaList;
	}

	public ArrayList<SelectItem> getAgeGroupGradeList() {
		return ageGroupGradeList;
	}

	public void setAgeGroupGradeList(ArrayList<SelectItem> ageGroupGradeList) {
		this.ageGroupGradeList = ageGroupGradeList;
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

	public String getAgeGrade() {
		return ageGrade;
	}

	public void setAgeGrade(String ageGrade) {
		this.ageGrade = ageGrade;
	}

	public String getClassMainPara() {
		return classMainPara;
	}

	public void setClassMainPara(String classMainPara) {
		this.classMainPara = classMainPara;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStdId() {
		return stdId;
	}

	public void setStdId(String stdId) {
		this.stdId = stdId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getTotalMark() {
		return totalMark;
	}

	public void setTotalMark(String totalMark) {
		this.totalMark = totalMark;
	}

	public ArrayList<SubParameterInfo> getSubParaList() {
		return subParaList;
	}

	public void setSubParaList(ArrayList<SubParameterInfo> subParaList) {
		this.subParaList = subParaList;
	}

	public String getTotalAchieved() {
		return totalAchieved;
	}

	public void setTotalAchieved(String totalAchieved) {
		this.totalAchieved = totalAchieved;
	}

	public Map<String, String> getMarksMap() {
		return marksMap;
	}

	public void setMarksMap(Map<String, String> marksMap) {
		this.marksMap = marksMap;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getAgeGroupId() {
		return ageGroupId;
	}

	public void setAgeGroupId(String ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	public String getAgeGroupName() {
		return ageGroupName;
	}

	public void setAgeGroupName(String ageGroupName) {
		this.ageGroupName = ageGroupName;
	}
}
