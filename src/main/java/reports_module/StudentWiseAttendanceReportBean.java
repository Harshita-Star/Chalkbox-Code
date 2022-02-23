package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import schooldata.Attendance;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ShowAttendanceRecordBean.DayList;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="studentWiseAttendanceReport")
@SessionScoped
public class StudentWiseAttendanceReportBean implements Serializable{
	/**
	 *
	 */
	String regex=RegexPattern.REGEX;
	ArrayList<Attendance> dataList;
	boolean show;
	String studentName,fatherName,total,section,className,month,session1;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<DayList> days;
	ArrayList<StudentInfo> studentList;
	String name;
	int count;

	@PostConstruct
	public void init()
	{
		name="";
		show=false;
		studentName=fatherName=className=section="";
		studentDetails=new ArrayList<>();
	}

	public String printFormat()
	{
		return "printStudentWiseAttendanceReport";
	}



	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
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

	public void monthWiseRecord(){

		int index=name.lastIndexOf("-")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentDetails=new ArrayList<>();
						for(int i=1;i<=12;i++)
						{
							info.setMonth(String.valueOf(i));
							studentDetails.add((StudentInfo) info.clone());
						}

						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}

		DatabaseMethods1 db = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		String schoolId = db.schoolId();
		String session=db.selectedSessionDetails(schoolId,conn);
		
		int cc=4;
		for(StudentInfo info : studentDetails)
		{
			if(cc==13)
				cc=1;

			DataBaseMethodsReports.allAttendanceForRecord(info,cc,session,conn);

			info.setStudentName(info.getFname());
			info.setFatherName(info.getFathersName());

			

			int statusListSize=db.getAttendanceOfStudent(String.valueOf(cc), info.getAddNumber(),session,conn);
			info.setPresent(String.valueOf(statusListSize));

			int totalPresentSize=db.getTotalAttendanceOfStudent(cc,info.getAddNumber(),session,conn);
			info.setTotalPresentUpToLastMonth(String.valueOf(totalPresentSize));

			int present=totalPresentSize+statusListSize;
			info.setTotalPresent(String.valueOf(present));

			int absentListSize=db.getAbsentOfStudent(String.valueOf(cc), info.getAddNumber(),session,conn);
			info.setAbsent(String.valueOf(absentListSize));

			int totalAbsentSize=db.getTotalAbsenteOfStudent(cc,info.getAddNumber(),session,conn);
			info.setTotalAbsentUpToLastMonth(String.valueOf(totalAbsentSize));

			int absent=totalAbsentSize+absentListSize;
			info.setTotalAbsent(String.valueOf(String.valueOf(absent)));

			int holidayListSize=db.getHolidayOfStudent(String.valueOf(cc), info.getAddNumber(),session,conn);
			info.setHolidays(String.valueOf(holidayListSize));

			int totalHolidaySize=db.getTotalHolidayeOfStudent(cc,info.getAddNumber(),session,conn);
			info.setTotalHolidayUpToLastMonth(String.valueOf(totalHolidaySize));

			int holiday=totalHolidaySize+holidayListSize;
			info.setTotalHoliday(String.valueOf(holiday));

			info.setMonth(db.monthNameByNumber(cc));
			cc++;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		days=new ArrayList<>();
		for(int i=1;i<=31;i++)
		{
			DayList list1=new DayList();
			list1.setDate(String.valueOf(i));

			days.add(list1);
		}

		count=1;

		studentName=studentDetails.get(0).getStudentName();
		section=studentDetails.get(0).getSectionName();
		className=studentDetails.get(0).getClassName();
		fatherName=studentDetails.get(0).getFathersName();
		session1=studentDetails.get(0).getSession();

		show=true;

	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public ArrayList<DayList> getDays() {
		return days;
	}
	public void setDays(ArrayList<DayList> days) {
		this.days = days;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}

	public String getSession1() {
		return session1;
	}

	public void setSession1(String session1) {
		this.session1 = session1;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
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

	public ArrayList<Attendance> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<Attendance> dataList) {
		this.dataList = dataList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
