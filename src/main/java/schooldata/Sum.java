package schooldata;

import java.io.Serializable;
import java.util.Date;

public class Sum implements Serializable {
	String totMarks,examName;
	Date feeDate;
	String date,collection,className,classId,month,monthId,routeName,routeId,stopName,stopId;
	int present,absent,leave,notMarked,feeAmount,strength,medical,prepLeave,holiday;
	String absentPercent, presentPercent,leavePercent,
	notMarkedPercent,medicalPercent,prepLeavePercent,
	holidatPercent;
	public String getTotMarks() {
		return totMarks;
	}

	public void setTotMarks(String totMarks) {
		this.totMarks = totMarks;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getDate() {
		return date;
	}

	public int getPresent() {
		return present;
	}

	public void setPresent(int present) {
		this.present = present;
	}

	public int getAbsent() {
		return absent;
	}

	public void setAbsent(int absent) {
		this.absent = absent;
	}

	public int getLeave() {
		return leave;
	}

	public void setLeave(int leave) {
		this.leave = leave;
	}

	public int getNotMarked() {
		return notMarked;
	}

	public void setNotMarked(int notMarked) {
		this.notMarked = notMarked;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public Date getFeeDate() {
		return feeDate;
	}

	public void setFeeDate(Date feeDate) {
		this.feeDate = feeDate;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonthId() {
		return monthId;
	}

	public void setMonthId(String monthId) {
		this.monthId = monthId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getStopId() {
		return stopId;
	}

	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	public int getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(int feeAmount) {
		this.feeAmount = feeAmount;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getMedical() {
		return medical;
	}

	public void setMedical(int medical) {
		this.medical = medical;
	}

	public int getPrepLeave() {
		return prepLeave;
	}

	public void setPrepLeave(int prepLeave) {
		this.prepLeave = prepLeave;
	}

	public int getHoliday() {
		return holiday;
	}

	public void setHoliday(int holiday) {
		this.holiday = holiday;
	}

	public String getAbsentPercent() {
		return absentPercent;
	}

	public void setAbsentPercent(String absentPercent) {
		this.absentPercent = absentPercent;
	}

	public String getPresentPercent() {
		return presentPercent;
	}

	public void setPresentPercent(String presentPercent) {
		this.presentPercent = presentPercent;
	}

	public String getLeavePercent() {
		return leavePercent;
	}

	public void setLeavePercent(String leavePercent) {
		this.leavePercent = leavePercent;
	}

	public String getNotMarkedPercent() {
		return notMarkedPercent;
	}

	public void setNotMarkedPercent(String notMarkedPercent) {
		this.notMarkedPercent = notMarkedPercent;
	}

	public String getMedicalPercent() {
		return medicalPercent;
	}

	public void setMedicalPercent(String medicalPercent) {
		this.medicalPercent = medicalPercent;
	}

	public String getPrepLeavePercent() {
		return prepLeavePercent;
	}

	public void setPrepLeavePercent(String prepLeavePercent) {
		this.prepLeavePercent = prepLeavePercent;
	}

	public String getHolidatPercent() {
		return holidatPercent;
	}

	public void setHolidatPercent(String holidatPercent) {
		this.holidatPercent = holidatPercent;
	}

	


}
