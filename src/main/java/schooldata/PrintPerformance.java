package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
@ManagedBean(name="printPerformance")
@ViewScoped
public class PrintPerformance implements Serializable{

	ArrayList<SelectItem> test, sectionList,classList;
	String selectedTest,testName,testInfo,selectedClass,selectedSection,tempTestName;
	ArrayList<String> listSub;
	String test1,test2,balMsg;
	ArrayList<StudentInfo> studentDetails;
	boolean showStudentRecord,showStudentRecordButton,showSection;
	boolean renderShowTable=false;
	boolean showRollSr = false;
	String totalmarks;
	String section,subjectDetail;
	ArrayList<ClassTest> list;
	SchoolInfoList schoolInfo;
	String type,username,schoolid;
	DatabaseMethods1 obj=new DatabaseMethods1();
	double smsLimit;

	public PrintPerformance()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		smsLimit = obj.smsLimitReminder(obj.schoolId(), conn);
		schoolInfo=obj.fullSchoolInfo(conn);

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		if(type.equalsIgnoreCase("admin"))
		{
			classList=obj.allClass(conn);
			
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
		 	classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
		}
		
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if (type.equalsIgnoreCase("academic coordinator") 
					|| type.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList = obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
		}

		if(schoolInfo.getBranch_id().equals("13"))
		{
			showSection=false;
			showRollSr = true;
		}
		else
		{
			showSection=true;
			showRollSr = false;
		}

		//test=obj.allTestPerformancePrint(schoolInfo.getClient_type(),conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allSection()
	{
		Connection conn=DataBaseConnection.javaConnection();
		test = new ArrayList<>();
		selectedSection=null;
		if(schoolInfo.getBranch_id().equals("13"))
		{
			selectedTest = "";
			test = obj.allTestPerformancePrint(/*schoolInfo.getClient_type(),*/selectedClass,selectedSection, conn);
			//test = obj.allTest(schoolInfo.getClient_type(),selectedClass,selectedSection, conn);
		}

		if(type.equalsIgnoreCase("admin") 
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionListForClassTeacher(empid, selectedClass,conn);
		}

		selectedTest = "";
		selectedSection=null;
		showStudentRecord=false;
		showStudentRecordButton=false;renderShowTable = false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allTest()
	{
		Connection conn=DataBaseConnection.javaConnection();
		test = new ArrayList<>();
		selectedTest = "";

		test = obj.allTestPerformancePrint(/*schoolInfo.getClient_type(),*/selectedClass,selectedSection, conn);

		showStudentRecord=false;
		showStudentRecordButton=false;renderShowTable = false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String sendMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		boolean check=obj.checkTestPerformance(testInfo,conn);
		if(check==true)
		{
			PrimeFaces.current().ajax().update("confirmForm");
			PrimeFaces.current().executeInitScript("PF('confirmDialog').show()");
		}
		else
		{
			double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if (type.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if (type.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				}
				else
				{
					balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}

			commonMethod();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String sendMsg()
	{
		Connection conn=DataBaseConnection.javaConnection();
		double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (type.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
				PrimeFaces.current().ajax().update("MsgLimitForm");
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}
		else if(balance <= 0)
		{
			balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
			if (type.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
				PrimeFaces.current().ajax().update("MsgOverForm");
			}
			else
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

				PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
				PrimeFaces.current().ajax().update("MsgOtherForm");
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}

		commonMethod();

		return "";
	}

	public void commonMethod()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		SchoolInfoList sl = obj.fullSchoolInfo(conn);

		String message="",testIdMap="",marks="",subName="",maxMarks="",totalObtained="";
		for(StudentInfo info:studentDetails)
		{
			message="";
			message = "Dear parent,\nyour ward " + info.getFullName() + " has secured marks in ";
			Map<String, String> map = info.getStudentPerformnaceMap();

			for(Map.Entry<String, String> mapInfo: map.entrySet())
			{
				testIdMap=mapInfo.getKey();
				marks=mapInfo.getValue();
				loop:for (ClassTest ls : list)
				{
					if(ls.getId().equals(testIdMap))
					{
						subName=ls.getSubject();
						maxMarks=ls.getTestMarks();
						break loop;
					}
				}
				if(marks.equals("AB"))
				{
					message+=subName+":- "+marks+" , ";
				}
				else if(marks.equals("NA"))
				{
					message+="";
				}
				else
				{
					message+=subName+":- "+marks+"/"+maxMarks+" , ";
				}
			}
			totalObtained=info.getTotalTestNo();
			message=message.substring(0,message.lastIndexOf(","));
			message+="\nTotal Marks:- "+totalObtained+"/"+totalmarks+" and Rank is:- "+info.getRank()+", in Test:- "+testName;
			message+=". \n\nRegards, \n" + sl.getSmsSchoolName();
		   if(!marks.equalsIgnoreCase("NA"))
		   {
			   obj.messageurlReport(String.valueOf(info.getFathersPhone()), message, info.getAddNumber(), testInfo, conn);
   
		   }
			
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message Sent Sucessfully"));
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sessionDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		loop:for(SelectItem ss : test)
		{
			if(selectedTest.equals(ss.getValue()))
			{
				testInfo=ss.getLabel();
				tempTestName = ss.getLabel();
				break loop;
			}
		}

		studentDetail();

		String arr[]=testInfo.split("/");

		if(schoolInfo.getClient_type().equalsIgnoreCase("institute"))
		{
			if(schoolInfo.getBranch_id().equals("13"))
			{
				testInfo = arr[0] + "/" + obj.classNameFromidSchid(obj.schoolId(),selectedClass, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn);
			}
			else
			{
				testInfo = arr[0] + "/" + obj.classNameFromidSchid(obj.schoolId(),selectedClass, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn) + "/" + obj.sectionNameByIdSchid(obj.schoolId(),selectedSection, conn);
			}
		}
		else
		{
			testInfo = arr[0] + "/" + obj.classNameFromidSchid(obj.schoolId(),selectedClass, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn) + "/" + obj.sectionNameByIdSchid(obj.schoolId(),selectedSection, conn);
		}


		/*String arr[]=testInfo.split("/");
		if(arr.length==4)
		{
			testInfo=arr[0]+"/"+arr[2]+"/"+arr[3];
		}*/
	}

	public void studentDetail()
	{

		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		/*String arr[]=testInfo.split("/");
			if(arr.length==4)
			{
				listSub=obj.subTestNameByTest(selectedTest,conn);
				test1=listSub.get(0);
				test2=listSub.get(1);

			}*/

		String arr[]=tempTestName.split("/");
		if(arr.length==2)
		{
			listSub=obj.subTestNameByTest(selectedTest,conn);
			test1=listSub.get(0);
			test2=listSub.get(1);

		}
		else
		{
			test1="";
			test2="";
		}

		list=obj.viewTestListForTest(conn,selectedTest);
		testName = list.get(0).getTestName();
		subjectDetail=obj.subjectDetailsForClassTest(list.get(0).getId(), conn);
		String[] temp=subjectDetail.split(",");
		if(schoolInfo.getClient_type().equalsIgnoreCase("institute"))
		{
			//sectionList=obj.sectionNameAndIdListByid(list.get(0).getClassid(),conn);
			if(temp[1].equalsIgnoreCase("Mandatory"))
			{
				studentDetails=obj.searchStudentListByClass(sectionList,conn);

				if(schoolInfo.getBranch_id().equals("13"))
				{
					studentDetails = obj.searchStudentListByClass(sectionList, conn);
				}
				else
				{
					studentDetails = obj.searchStudentListbysection(selectedSection, conn);
				}

			}
			else
			{
				if(schoolInfo.getBranch_id().equals("13"))
				{
					studentDetails=obj.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],"","","subjectwise", conn);
				}
				else
				{
					studentDetails = obj.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],selectedSection,selectedClass,"classwise", conn);
				}
			}
		}
		else
		{
			if(temp[1].equalsIgnoreCase("Mandatory"))
			{
				studentDetails=obj.searchStudentListbysection(selectedSection,conn);
			}
			else
			{
				//studentDetails=obj.getAllStudentStrentgthForOptional(temp[2], conn);
				studentDetails = obj.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],selectedSection,selectedClass,"classwise", conn);
			}

		}
		/*section=new DatabaseMethods1().searchSection(selectedTest,conn);
			testName=new DatabaseMethods1().searchTest(selectedTest,conn);*/

		DatabaseMethods1.testPerformanceForRank(studentDetails,/*selectedTest,*/conn,list);
		Collections.sort(studentDetails,new MySalaryComp());
		totalmarks=list.get(list.size()-1).getTotalmarks();
		int counter=1;
		if(studentDetails.size()>0 )
		{
			for(int i=0;i<studentDetails.size();i++)
			{

				if(studentDetails.get(i).getTotalTestNo().equals("NA"))
				{
					studentDetails.get(i).setRank("NA");
					studentDetails.get(i).setPercentage("0");
				}
				else if(i==0 || (studentDetails.get(i).getTotalTestNo().equals(studentDetails.get(i-1).getTotalTestNo())))
				{
					studentDetails.get(i).setRank(String.valueOf(counter));
					if(studentDetails.get(i).getTotalTestNo().equalsIgnoreCase("0.0"))
					{
						studentDetails.get(i).setPercentage("0");
					}
					else
					{
						double totalOfMarks = 0.0;
						double totalMaxMrks = 0.0;
						for(ClassTest ct : list)
						{
							
							if(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId())==null)
							{
								totalOfMarks += 0;
							}
							else if(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()).equalsIgnoreCase("AB") || studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()).equalsIgnoreCase("NA"))
							{
								totalOfMarks += 0;
								totalMaxMrks += Double.valueOf(ct.getTestMarks());
							}
							else
							{	
							  totalOfMarks += Double.valueOf(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()));
							  totalMaxMrks += Double.valueOf(ct.getTestMarks());
							} 
							
							
						}
						
						double percent = (totalOfMarks/totalMaxMrks)*100;
						percent =Double.parseDouble(new DecimalFormat("##.##").format(percent));
						studentDetails.get(i).setPercentage(String.valueOf(percent));
						
					}
				}
				else
				{
					++counter;
					
					studentDetails.get(i).setRank(String.valueOf(counter));
					if(studentDetails.get(i).getTotalTestNo().equalsIgnoreCase("0.0"))
					{
						studentDetails.get(i).setPercentage("0");
					}
					else
					{
						double totalMaxMrks = 0.0;
						double totalOfMarks = 0.0;
						for(ClassTest ct : list)
						{
							
							if(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId())==null)
							{
								totalOfMarks += 0;
							}
							else if(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()).equalsIgnoreCase("AB") || studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()).equalsIgnoreCase("NA"))
							{
								totalOfMarks += 0;
								totalMaxMrks += Double.valueOf(ct.getTestMarks());
							}
							else
							{	
							  totalOfMarks += Double.valueOf(studentDetails.get(i).getStudentPerformnaceMap().get(ct.getId()));
							  totalMaxMrks += Double.valueOf(ct.getTestMarks());
							}
							
						}
						
						double percent = (totalOfMarks/totalMaxMrks)*100;
						percent =Double.parseDouble(new DecimalFormat("##.##").format(percent));
						studentDetails.get(i).setPercentage(String.valueOf(percent));
					}
					
				}
				
				studentDetails.get(i).setSno(i+1);
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

			if(!e1.getTotalTestNo().equals("NA") && !e2.getTotalTestNo().equals("NA")) {

				return (int) (Float.parseFloat(e2.getTotalTestNo()) - Float.parseFloat(e1.getTotalTestNo()));
			}
			if(e2.getTotalTestNo().equals("NA")) {
				return -1;
			}
			if(e1.getTotalTestNo().equals("NA")) {
				return 1;
			}

			return 0;


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
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
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

	public ArrayList<ClassTest> getList() {
		return list;
	}

	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}

	public String getTotalmarks() {
		return totalmarks;
	}

	public void setTotalmarks(String totalmarks) {
		this.totalmarks = totalmarks;
	}

	public String getTest1() {
		return test1;
	}

	public void setTest1(String test1) {
		this.test1 = test1;
	}

	public String getTest2() {
		return test2;
	}

	public void setTest2(String test2) {
		this.test2 = test2;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public boolean isShowSection() {
		return showSection;
	}

	public void setShowSection(boolean showSection) {
		this.showSection = showSection;
	}

	public boolean isShowRollSr() {
		return showRollSr;
	}

	public void setShowRollSr(boolean showRollSr) {
		this.showRollSr = showRollSr;
	}


}
