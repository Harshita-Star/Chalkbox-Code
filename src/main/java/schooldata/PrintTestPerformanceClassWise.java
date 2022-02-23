package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;
@ManagedBean(name="printTestPerformanceClassWise")
@ViewScoped
public class PrintTestPerformanceClassWise implements Serializable
{
	ArrayList<SelectItem> test;
	String selectedTest,testName,testInfo,selectedSection;
	ArrayList<StudentInfo> studentDetails;
	boolean showStudentRecord,showStudentRecordButton;
	boolean renderShowTable=false,showSection;
	ArrayList<SelectItem> classSection,sectionList;
	String totalmarks;
	Date selectedDay;
	String selectedClass, username, userType, schoolid;
	ArrayList<ClassTest> list;
	String section,date,className,session;
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	SchoolInfoList schoolInfo;
    String month="";
    
    String monthName="";
    
	public PrintTestPerformanceClassWise()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		username=(String) httpSession.getAttribute("username");
		schoolid = (String) httpSession.getAttribute("schoolid");
		schoolInfo = DBM.fullSchoolInfo(conn);
		session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		selectedDay=new Date();
		date=sdf.format(selectedDay);

		if(schoolInfo.getBranch_id().equals("13"))
		{
			showSection=false;
		}
		else
		{
			showSection=true;
		}
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection=new DatabaseMethods1().allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classSection = DBM.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classSection=DBM.allClassListForClassTeacher(empid,schoolid,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSection()
	{
		DatabaseMethods1 obj=new DatabaseMethods1();
		Connection conn=DataBaseConnection.javaConnection();
		test = new ArrayList<>();
		showStudentRecord=false;
		showStudentRecordButton=false;renderShowTable = false;

		if(schoolInfo.getBranch_id().equals("13"))
		{
			selectedSection = "all";
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			sectionList.add(si);
		}
		else
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer")
					|| userType.equalsIgnoreCase("Accounts"))
			{
				sectionList = new ArrayList<SelectItem>();
				SelectItem si = new SelectItem();
				si.setLabel("All");
				si.setValue("all");
				sectionList.add(si);

				ArrayList<SelectItem> temp = obj.allSection(selectedClass,conn);

				if(temp.size()>0)
				{
					sectionList.addAll(temp);
				}
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				sectionList=obj.allSectionListForClassTeacher(empid,selectedClass,conn);
			}
			selectedSection=null;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		monthName=new DatabaseMethods1().monthNameByNumber(Integer.parseInt(month));
		date=sdf.format(selectedDay);
		Date stdate=null;
		Date edate=null;
		String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		String[] sst=session.split("-");
	    SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");
		
		if(Integer.parseInt(month)<4)
		{
		   	String startDate="01/"+month+"/"+sst[1];
		   	String endDate="31/"+month+"/"+sst[1];
		   	
		   	try {
				stdate=ss.parse(startDate);
				edate=ss.parse(endDate);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
		}
		else
		{
			String startDate="01/"+month+"/"+sst[0];
		   	String endDate="31/"+month+"/"+sst[0];
		   	
		   	try {
				stdate=ss.parse(startDate);
				edate=ss.parse(endDate);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
		   	
		   	
		}
		

		test=DBM.sectionNameAndIdListByid(selectedClass,conn);
		if(selectedSection.equalsIgnoreCase("all"))
		{
			className=DBM.classNameFromidSchid(DBM.schoolId(),selectedClass,session, conn);
			list=DBM.allTestListClassWiseDateWise(selectedClass,/*selectedDay,*/stdate,edate,conn);
		}
		else
		{
			className=DBM.classNameFromidSchid(DBM.schoolId(),selectedClass,session, conn) + "-" + DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
			list=DBM.allTestListSectionWiseDateWise(selectedClass,selectedSection,stdate,edate,conn);
		}
		
		if(list.size()>0)
		{
			studentDetail();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Record Found"));
			showStudentRecord=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void studentDetail()
	{
		Connection conn=DataBaseConnection.javaConnection();
		/*if(selectedSection.equalsIgnoreCase("all"))
		{
			studentDetails=new DatabaseMethods1().searchStudentListByClass(test,conn);
		}
		else
		{
			studentDetails=new DatabaseMethods1().searchStudentListBySectio(selectedSection,conn);
		}*/

		String sectionId="";
		if(selectedSection.equalsIgnoreCase("all"))
			sectionId="-1";
		else
			sectionId=selectedSection;
		
		studentDetails=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byClassSection","", "basic", conn,selectedClass,sectionId);
		ArrayList<StudentInfo> studentWithNAStudent=new ArrayList<>();
		ArrayList<StudentInfo> studentWithOutNAStudent=new ArrayList<>();

		DatabaseMethods1.testPerformanceForRank(studentDetails,/*selectedTest,*/conn,list);

		for(int j=0;j<studentDetails.size();j++)
		{

			if(studentDetails.get(j).getTotalTestNo()==null || studentDetails.get(j).getTotalTestNo().equals("NA"))
			{

				studentWithNAStudent.add(studentDetails.get(j));
			}
			else
			{
				studentWithOutNAStudent.add(studentDetails.get(j));
			}
		}

		Collections.sort(studentWithOutNAStudent,new MySalaryComp());
		totalmarks=list.get(list.size()-1).getTotalmarks();
		studentDetails=new ArrayList<>();
		studentDetails.addAll(studentWithOutNAStudent);
		studentDetails.addAll(studentWithNAStudent);
		int counter=1;
		if(studentDetails.size()>0 )
		{
			for(int i=0;i<studentDetails.size();i++)
			{

				if(studentDetails.get(i).getTotalTestNo()==null || studentDetails.get(i).getTotalTestNo().equals("NA") )
				{
					studentDetails.get(i).setRank("NA");
				}
				else if(i==0 || (studentDetails.get(i).getTotalTestNo().equals(studentDetails.get(i-1).getTotalTestNo())))
				{
					studentDetails.get(i).setRank(String.valueOf(counter));
				}
				else
				{
					++counter;
					studentDetails.get(i).setRank(String.valueOf(counter));
				}
			}
			//Collections.sort(studentDetails,new MySalaryComp1());
			showStudentRecordButton=true;
			renderShowTable=true;
		}
		else
		{
			showStudentRecordButton=false;
		}


		showStudentRecord=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	class MySalaryComp implements Comparator<StudentInfo>{

		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			if(!(e1.getTotalTestNo()==null || e1.getTotalTestNo().equals("0") ||  e2.getTotalTestNo().equals("0") || e1.getTotalTestNo().equals("NA") ||  e2.getTotalTestNo().equals("NA")))
			{
				if(Float.parseFloat(e1.getTotalTestNo()) <= Float.parseFloat(e2.getTotalTestNo())){
					return 1;
				} else {
					return -1;
				}
			}

			return -1;

		}
	}

	class MySalaryComp1 implements Comparator<StudentInfo>{

		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			return e1.getFullName().compareToIgnoreCase(e2.getFullName());
		}


	}

	public ArrayList<SelectItem> getTest() {
		return test;
	}

	public void setTest(ArrayList<SelectItem> test) {
		this.test = test;
	}

	public String getSelectedTest() {
		return selectedTest;
	}

	public void setSelectedTest(String selectedTest) {
		this.selectedTest = selectedTest;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestInfo() {
		return testInfo;
	}

	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
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

	public boolean isShowStudentRecordButton() {
		return showStudentRecordButton;
	}

	public void setShowStudentRecordButton(boolean showStudentRecordButton) {
		this.showStudentRecordButton = showStudentRecordButton;
	}

	public boolean isRenderShowTable() {
		return renderShowTable;
	}

	public void setRenderShowTable(boolean renderShowTable) {
		this.renderShowTable = renderShowTable;
	}

	public String getTotalmarks() {
		return totalmarks;
	}

	public void setTotalmarks(String totalmarks) {
		this.totalmarks = totalmarks;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public ArrayList<ClassTest> getList() {
		return list;
	}

	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public boolean isShowSection() {
		return showSection;
	}

	public void setShowSection(boolean showSection) {
		this.showSection = showSection;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}
	

}
