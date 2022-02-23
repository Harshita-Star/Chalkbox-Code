package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.ShowAttendanceRecordBean.DayList;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
@ManagedBean(name="viewAttendenceInCalenderFormat")
@ViewScoped
public class ViewAttendenceInCalenderFormat implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name;
	ArrayList<StudentInfo> studentList,studentDetails;
	Date date=new Date();
	boolean show;
	ArrayList<DayList> days;
	String studentName,fatherName,total,section,className,month,session;
	int count,present,absent,notMarked,holiday,leave,medical,prepleave;
	ArrayList<Sum> sum;
	ArrayList<SelectItem>months;
	ArrayList<Week>weeks;
	int year,monthId;
	StudentInfo stuinfo = new StudentInfo();
	DatabaseMethodSession objSession=new DatabaseMethodSession();
    Date startingDate;
    String id="";
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		//studentList=new DatabaseMethods1().searchStudentList(query,conn);
		studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");

		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}
	public ViewAttendenceInCalenderFormat()
	{
		DatabaseMethods1 obj=new DatabaseMethods1();
		year = date.getYear()+1900;
		monthId = date.getMonth()+1;

		////// // System.out.println(weeks.size());
		months=obj.allMonths();

		month=obj.monthNameByNumber(monthId)+"  "+year;
	}
	

	public void searchStudentByName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			
			int index=name.lastIndexOf("-")+1;
			id=name.substring(index);
			 ArrayList<StudentInfo> tempList= new DatabaseMethodSession().searchStudentListWithPreSessionStudent("singlestudent", id, "full", conn, "", new DatabaseMethods1().schoolId());
					//new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),id, conn);
			 
			 stuinfo =tempList.get(0);
	
			startingDate=stuinfo.getStartingDate();
			weeks=new DatabaseMethods1().allWeekDay(year,monthId,id,stuinfo.getSectionid(),startingDate,conn);

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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void backward()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		if(monthId<2)
		{

			monthId=12;
			year--;
			month=DBM.monthNameByNumber(monthId)+"  "+year;


		}
		else
		{

			monthId--;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}
		weeks=DBM.allWeekDay(year,monthId,id, stuinfo.getSectionid(),startingDate,conn);
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
		DatabaseMethods1 DBM=new DatabaseMethods1();
		if(monthId>11)
		{

			monthId=1;
			year++;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}
		else
		{

			monthId++;
			month=DBM.monthNameByNumber(monthId)+"  "+year;

		}
		weeks=DBM.allWeekDay(year,monthId,id, stuinfo.getSectionid(),startingDate,conn);
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
