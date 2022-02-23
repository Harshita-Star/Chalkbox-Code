package schooldata;

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

@ManagedBean(name = "showAttendenceTeacherReport")
@ViewScoped
public class ShowTeacherAttendenceBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String monthName;
	boolean showAttendanceDetail;
	boolean showStudentRecord;
	ArrayList<DayList> days;

	ArrayList<SelectItem> yearList;
	ArrayList<SelectItem> employeeList;
	String selectedEmployee;
	String selectedYear;

	ArrayList<EmpInfo> studentDetails;
	ArrayList<SelectItem> categoryList;
	ArrayList<SelectItem> empList = new ArrayList<SelectItem>();
	ArrayList<String> selEmployees= new ArrayList<String>();
	
	ArrayList<String> selectedCategory= new ArrayList<String>();
	ArrayList<SelectItem> monthList;
	String selectedMonth;


	public ShowTeacherAttendenceBean() {
		Connection conn = DataBaseConnection.javaConnection();
		
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String userid = (String) httpSession.getAttribute("username");
		
		employeeList = new DatabaseMethods1().teacherDetails(userid,conn);
		categoryList=new DatabaseMethods1().allEmployeeCategory(conn);

		
		for(SelectItem cc:employeeList)
		{
			selectedEmployee = (String) cc.getValue();
		}
		
		

		Date dt = new Date();
		selectedMonth = String.valueOf(dt.getMonth() + 1);
		selectedYear = String.valueOf(dt.getYear() + 1900);

		allMonths();
		allYear();
		studentDetail();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchByCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
        empList = new ArrayList<SelectItem>();  
		try
		{

			empList=obj.searchEmployeesbyCategoryAndSchidd(obj.schoolId(),selectedCategory,conn);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void allMonths() {
		monthList = new ArrayList<>();

		SelectItem item = new SelectItem();
		item.setLabel("January");
		item.setValue("1");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("February");
		item.setValue("2");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("March");
		item.setValue("3");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("April");
		item.setValue("4");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("May");
		item.setValue("5");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("June");
		item.setValue("6");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("July");
		item.setValue("7");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("August");
		item.setValue("8");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("September");
		item.setValue("9");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("October");
		item.setValue("10");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("November");
		item.setValue("11");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("December");
		item.setValue("12");
		monthList.add(item);

	}

	public void allYear() {
		yearList = new ArrayList<>();
		Date d = new Date();
		int year = d.getYear()+1900;
		for (int i = 2000; i <= year; i++) {

			SelectItem item = new SelectItem();
			item.setLabel(String.valueOf(i));
			item.setValue(String.valueOf(i));
			yearList.add(item);
		}
	}

	public void studentDetail()
	{
		
		//// // System.out.println(selectedEmployee);
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		monthName = DBM.monthNameByNumber(Integer.valueOf(selectedMonth));
		//System.out.println(monthName);
		try
		{
			ArrayList<EmpInfo> list=new ArrayList<>();
			list = DBM.allEmpInfo(conn);
			//System.out.println(list.size());
//			if(selectedEmployee.equals("all"))
//			{
//				studentDetails = list;
//			}
//			else
//			{
				studentDetails=new ArrayList<>();
				for(EmpInfo info:list)
				{
				//	System.out.println(info.getName());
					if(info.getId().equals(selectedEmployee))
					{
						studentDetails.add(info);
						break;
					}
				}
//			}

			int year = Integer.parseInt(selectedYear);
			int flag = 0;
			if (year % 4 == 0) {
				if (year % 100 == 0) {
					if (year % 400 == 0) {
						flag = 1;
					}
				} else {
					flag = 1;
				}
			}

			int temp = 31;
			if (selectedMonth.equals("4") || selectedMonth.equals("6") || selectedMonth.equals("9")
					|| selectedMonth.equals("11"))
				temp = 30;

			else if (selectedMonth.equals("2")) {
				if (flag == 1) {
					temp = 29;
				} else {
					temp = 28;
				}
			}

			days = new ArrayList<>();
			for (int i = 1; i <= temp; i++) {
				DayList list1 = new DayList();
				list1.setDate(String.valueOf(i));
				days.add(list1);
			}

			DatabaseMethods1.allAttendanceRecordForTeacher(selectedMonth, studentDetails, selectedYear,temp, conn);

			showStudentRecord = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void studentDetailMultiple()
	{
		
		//// // System.out.println(selectedEmployee);
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		monthName = DBM.monthNameByNumber(Integer.valueOf(selectedMonth));
		try
		{
			ArrayList<EmpInfo> list=new ArrayList<>();
			for(String ss :selEmployees)
			{
				EmpInfo epf = new EmpInfo();
				epf.setId(ss);
				list.add(epf);
			}
			

				studentDetails=new ArrayList<>();
				for(EmpInfo info:list)
				{
					
						studentDetails.add(info);
						
					
				}
//			}

			int year = Integer.parseInt(selectedYear);
			int flag = 0;
			if (year % 4 == 0) {
				if (year % 100 == 0) {
					if (year % 400 == 0) {
						flag = 1;
					}
				} else {
					flag = 1;
				}
			}

			int temp = 31;
			if (selectedMonth.equals("4") || selectedMonth.equals("6") || selectedMonth.equals("9")
					|| selectedMonth.equals("11"))
				temp = 30;

			else if (selectedMonth.equals("2")) {
				if (flag == 1) {
					temp = 29;
				} else {
					temp = 28;
				}
			}

			days = new ArrayList<>();
			for (int i = 1; i <= temp; i++) {
				DayList list1 = new DayList();
				list1.setDate(String.valueOf(i));
				days.add(list1);
			}

			DatabaseMethods1.allAttendanceRecordForTeacher(selectedMonth, studentDetails, selectedYear,temp, conn);

			showStudentRecord = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public boolean isShowAttendanceDetail() {
		return showAttendanceDetail;
	}

	public void setShowAttendanceDetail(boolean showAttendanceDetail) {
		this.showAttendanceDetail = showAttendanceDetail;
	}
	public ArrayList<DayList> getDays() {

		return days;
	}

	public void setDays(ArrayList<DayList> days) {
		this.days = days;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
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

	public String getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}

	static public class DayList implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		String date, value;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public ArrayList<SelectItem> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<SelectItem> employeeList) {
		this.employeeList = employeeList;
	}
	public String getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(String selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public ArrayList<EmpInfo> getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(ArrayList<EmpInfo> studentDetails) {
		this.studentDetails = studentDetails;
	}
	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList<String> getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(ArrayList<String> selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public ArrayList<SelectItem> getEmpList() {
		return empList;
	}

	public void setEmpList(ArrayList<SelectItem> empList) {
		this.empList = empList;
	}

	public ArrayList<String> getSelEmployees() {
		return selEmployees;
	}

	public void setSelEmployees(ArrayList<String> selEmployees) {
		this.selEmployees = selEmployees;
	}

	
	
}
