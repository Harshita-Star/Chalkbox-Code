package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ShowAttendanceRecordBean.DayList;
import schooldata.StudentInfo;
import schooldata.Sum;
import schooldata.Week;

@ManagedBean(name="studentAttendanceCalendar")
@ViewScoped

public class StudentAttendanceCalendarBean implements Serializable
{
	String name;
	String id="";
	ArrayList<StudentInfo> studentList,studentDetails;
	Date date=new Date();
	boolean show;
	ArrayList<DayList> days;
	String studentName,fatherName,total,section,className,month,session,schoolId;
	int count,present,absent,notMarked,holiday,leave,medical,prepleave;
	ArrayList<Sum> sum;
	ArrayList<SelectItem>months;
	ArrayList<Week>weeks;
	int year;
	int monthId;
	StudentInfo stuinfo = new StudentInfo();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public StudentAttendanceCalendarBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		year = date.getYear()+1900;
		monthId = date.getMonth()+1;

		////// // System.out.println(weeks.size());
		months=obj.allMonths();

		month=obj.monthNameByNumber(monthId)+"  "+year;

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		id = (String) ss.getAttribute("username");
		schoolId = obj.schoolId();
		stuinfo = obj.studentDetailslistByAddNo(schoolId,id, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		searchStudentByName();
	}


	public void searchStudentByName()
	{

		Connection conn=DataBaseConnection.javaConnection();

		weeks=obj.allWeekDay(year,monthId,id,stuinfo.getSectionid(),stuinfo.getStartingDate(),conn);

		for(Week ww:weeks)
		{
			present=ww.getPresent();
			absent=ww.getAbsent();
			notMarked=ww.getNotMarked();
			holiday=ww.getHoliday();
			leave=ww.getLeave();
			medical=ww.getMedical();
			prepleave=ww.getPrepleave();
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void backward()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(monthId<2)
		{

			monthId=12;
			year--;
			month=obj.monthNameByNumber(monthId)+"  "+year;


		}
		else
		{

			monthId--;
			month=obj.monthNameByNumber(monthId)+"  "+year;

		}

		weeks=obj.allWeekDay(year,monthId,id, stuinfo.getSectionid(),stuinfo.getStartingDate(),conn);
		for(Week ww:weeks)
		{
			present=ww.getPresent();
			absent=ww.getAbsent();
			notMarked=ww.getNotMarked();
			holiday=ww.getHoliday();
			leave=ww.getLeave();
			medical=ww.getMedical();
			prepleave=ww.getPrepleave();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//return month;

	}

	public void forward()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(monthId>11)
		{

			monthId=1;
			year++;
			month=obj.monthNameByNumber(monthId)+"  "+year;

		}
		else
		{

			monthId++;
			month=obj.monthNameByNumber(monthId)+"  "+year;

		}

		weeks=obj.allWeekDay(year,monthId,id, stuinfo.getSectionid(),stuinfo.getStartingDate(),conn);
		for(Week ww:weeks)
		{
			present=ww.getPresent();
			absent=ww.getAbsent();
			notMarked=ww.getNotMarked();
			holiday=ww.getHoliday();
			leave=ww.getLeave();
			medical=ww.getMedical();
			prepleave=ww.getPrepleave();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//return month;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<DayList> getDays() {
		return days;
	}
	public void setDays(ArrayList<DayList> days) {
		this.days = days;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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

	public int getNotMarked() {
		return notMarked;
	}
	public void setNotMarked(int notMarked) {
		this.notMarked = notMarked;
	}
	public int getHoliday() {
		return holiday;
	}
	public void setHoliday(int holiday) {
		this.holiday = holiday;
	}
	public ArrayList<Sum> getSum() {
		return sum;
	}
	public void setSum(ArrayList<Sum> sum) {
		this.sum = sum;
	}
	public ArrayList<SelectItem> getMonths() {
		return months;
	}
	public void setMonths(ArrayList<SelectItem> months) {
		this.months = months;
	}
	public ArrayList<Week> getWeeks() {
		return weeks;
	}
	public void setWeeks(ArrayList<Week> weeks) {
		this.weeks = weeks;
	}


	public int getLeave() {
		return leave;
	}


	public void setLeave(int leave) {
		this.leave = leave;
	}


	public int getMedical() {
		return medical;
	}


	public void setMedical(int medical) {
		this.medical = medical;
	}


	public int getPrepleave() {
		return prepleave;
	}


	public void setPrepleave(int prepleave) {
		this.prepleave = prepleave;
	}
}
