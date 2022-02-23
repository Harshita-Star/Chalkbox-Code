package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import schooldata.ShowAttendanceRecordBean.DayList;
@ManagedBean(name="studentAttBean")
@ViewScoped
public class StudentAttBean implements Serializable
{

	ArrayList<StudentInfo> studentDetails;
	ArrayList<SelectItem> monthList;
	boolean showStudentRecord;
	String selectedSection,selectedMonth,selectedYear;
	ArrayList<SelectItem> yearList;
	ArrayList<DayList> days;


	public void allYear(){
		yearList=new ArrayList<>();
		for(int i=2000;i<=2020;i++){

			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i));
			item.setValue(String.valueOf(i));
			yearList.add(item);
		}
	}

	public StudentAttBean() {
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession SS=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username =(String) SS.getAttribute("username");

		//studentImage=new DatabaseMethods1().studentImage(Integer.parseInt(username));
		StudentInfo	info=new DatabaseMethods1().studentDetailslistByAddNo(new DatabaseMethods1().schoolId(),username,conn);

		studentDetails=new ArrayList<>();
		studentDetails.add(info);

		selectedSection=info.getSectionid();
		allYear();allMonths();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void studentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{

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

			DatabaseMethods1.allAttendanceForRecord(selectedSection, selectedMonth, studentDetails, selectedYear, temp,conn);

			showStudentRecord=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void allMonths()
	{
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

	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
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
	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}
	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
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
}
