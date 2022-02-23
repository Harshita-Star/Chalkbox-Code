package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.ShowAttendanceRecordBean.DayList;

@ManagedBean(name="monthWiseAttendanceReport")
@ViewScoped
public class MonthWiseAttendanceReportBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> monthList,yearList,classSection,sectionList;

	ArrayList<DayList> days;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<Attendance> dataList;
	String selectedClass,selectedMonth,selectedYear,selectedSection;
	boolean show;
	String studentName,fatherName,total,section,className,month;
	ArrayList<Attendance> list=new ArrayList<>();

	int count;

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public MonthWiseAttendanceReportBean(){
		Connection conn=DataBaseConnection.javaConnection();
		classSection=new DatabaseMethods1().allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allMonths(){
		monthList=new ArrayList<>();

		SelectItem item=new SelectItem();
		item.setLabel("January");
		item.setValue("1");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("February");
		item.setValue("2");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("March");
		item.setValue("3");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("April");
		item.setValue("4");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("May");
		item.setValue("5");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("June");
		item.setValue("6");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("July");
		item.setValue("7");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("August");
		item.setValue("8");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("September");
		item.setValue("9");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("October");
		item.setValue("10");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("November");
		item.setValue("11");
		monthList.add(item);

		item=new SelectItem();
		item.setLabel("December");
		item.setValue("12");
		monthList.add(item);
	}

	public void allYear(){
		yearList=new ArrayList<>();
		for(int i=2000;i<=2020;i++){

			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i));
			item.setValue(String.valueOf(i));
			yearList.add(item);
		}
	}


	public void monthWiseRecord(){
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 db=new DatabaseMethods1();

		String session=DatabaseMethods1.selectedSessionDetails(db.schoolId(),conn);
		studentDetails=db.searchStudentListByClassSection(selectedClass,selectedSection,conn);
		DatabaseMethods1.allAttendanceForRecord(selectedSection, selectedMonth, studentDetails, selectedYear,0,conn);

		className=studentDetails.get(0).getClassName();
		section=studentDetails.get(0).getSectionName();
		total=String.valueOf(studentDetails.size());
		month=db.monthNameByNumber(Integer.parseInt(selectedMonth));

		int count=1;
		for(StudentInfo info : studentDetails)
		{

			info.setSno(count++);
			info.setStudentName(info.getFname());
			info.setFatherName(info.getFathersName());



			int statusListSize=db.getAttendanceOfStudent(selectedMonth, info.getAddNumber(),session,conn);
			info.setPresent(String.valueOf(statusListSize));

			int totalPresentSize=db.getTotalAttendanceOfStudent(Integer.parseInt(selectedMonth),info.getAddNumber(),session,conn);
			info.setTotalPresentUpToLastMonth(String.valueOf(totalPresentSize));

			int present=totalPresentSize+statusListSize;
			info.setTotalPresent(String.valueOf(present));

			int absentListSize=db.getAbsentOfStudent(selectedMonth, info.getAddNumber(),session,conn);
			info.setAbsent(String.valueOf(absentListSize));

			int totalAbsentSize=db.getTotalAbsenteOfStudent(Integer.parseInt(selectedMonth),info.getAddNumber(),session,conn);
			info.setTotalAbsentUpToLastMonth(String.valueOf(totalAbsentSize));

			int absent=totalAbsentSize+absentListSize;
			info.setTotalAbsent(String.valueOf(absent));

			int holidayListSize=db.getHolidayOfStudent(selectedMonth, info.getAddNumber(),session,conn);
			info.setHolidays(String.valueOf(holidayListSize));

			int totalHolidaySize=db.getTotalHolidayeOfStudent(Integer.parseInt(selectedMonth),info.getAddNumber(),session,conn);
			info.setTotalHolidayUpToLastMonth(String.valueOf(totalHolidaySize));

			int holiday=totalHolidaySize+holidayListSize;
			info.setTotalHoliday(String.valueOf(holiday));

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int year=Integer.parseInt(selectedYear);
		int flag=0;
		if(year%4==0  )
		{
			if(year%100==0)
			{
				if(year %400 ==0)
				{
					flag=1;
				}
			}
			else
			{
				flag=1;
			}
		}

		int temp=31;
		if(selectedMonth.equals("4") || selectedMonth.equals("6") || selectedMonth.equals("9") || selectedMonth.equals("11"))
			temp=30;

		else if(selectedMonth.equals("2"))
		{
			if(flag==1)
			{
				temp=29;
			}
			else
			{
				temp=28;
			}
		}

		days=new ArrayList<>();
		for(int i=1;i<=temp;i++)
		{
			DayList list1=new DayList();
			list1.setDate(String.valueOf(i));

			days.add(list1);
		}
		show=true;

	}

	public String printFormat()
	{
		return "printMonthWiseAttendanceReport";
	}


	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedSection() {
		return selectedSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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

	public ArrayList<Attendance> getList() {
		return list;
	}

	public void setList(ArrayList<Attendance> list) {
		this.list = list;
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

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public String getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
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
}
