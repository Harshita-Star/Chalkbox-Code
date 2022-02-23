package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name = "addTest")
@ViewScoped

public class AddTest implements Serializable {
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem> classSection;
	ArrayList<SelectItem> sectionList, subjectListt;
	ArrayList<SubjectInfo> subjectList,subjectList2;
	String selectedCLassSection,schid,session;
	String selectedSection;
	String selectedSubject,subTest1,subTest2,noOfTest;
	String testName, testMarks, idTest,username,userType,clsTch;
	ArrayList<ClassTest> list;
	ClassTest selectedTest;
	String type;
	Date selectedDay;
	boolean sectionField = false,showSub1,showSub2,showName1;
	boolean showType,showSectionBut;
	SchoolInfoList schoolInfo;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseValidator dbValidate=new DataBaseValidator();

	public AddTest() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolInfo = obj.fullSchoolInfo(conn);
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		showType = false;
		showSectionBut = true;
		if (schoolInfo.getClient_type().equalsIgnoreCase("institute")) {	
			/*
			 * if(schoolInfo.getBranch_id().equals("13")) { sectionField = false; } else {
			 */
				sectionField = true;
				/* } */

			selectedSection = "";
		} else {
			sectionField = true;
		}
		selectedDay = new Date();
		list = obj.viewTestList(conn);
		
		allClass(conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void section() {
		
		Connection conn = DataBaseConnection.javaConnection();
		
		testName = testMarks=noOfTest = "";
		selectedDay = new Date();
		subjectList=subjectList2 = new ArrayList<>();
		showSub1=showSub2=showName1=false;
		subTest1=subTest2="";
		subjectListt = new ArrayList<>();
		selectedCLassSection = "";
		allClass(conn);
		sectionList = new ArrayList<>();
		
		if(type.equals("0")) {
			selectedSection = "";
			showSectionBut = false;
		}else {
			showSectionBut = true;
		}
		
		showType = true;
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void allClass(Connection conn)
	{
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff"))
		{
			classSection=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection=obj.allClassDeatilsForTeacher(empid,schid,conn);
		}
	}

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=new DatabaseMethods1().allSectionForTeacher(selectedCLassSection, empid, conn);
			
		}
		
		if(type.equals("0")) {
			allSubjectsList();
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSubjectsList() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin") 
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal"))
		{
			subjectList = obj.allSubjectListClassWise(selectedCLassSection, conn);
			subjectList2 = obj.allSubjectListClassWise(selectedCLassSection, conn);
			subjectListt = obj.allSubjectClassWise(selectedCLassSection, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			subjectList=new DataBaseMeathodJson().AllEMployeeSubjectClassWise(empid,selectedSection,schid,conn);
			subjectList2=new DataBaseMeathodJson().AllEMployeeSubjectClassWise(empid,selectedSection,schid,conn);
			subjectListt=new DataBaseMeathodJson().AllEMployeeSubjectClassWiseJson(empid,selectedSection,schid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void allSubjects() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin") 
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal"))
		{
			subjectList = obj.allSubjectListClassWise(selectedCLassSection, conn);
			subjectList2 = obj.allSubjectListClassWise(selectedCLassSection, conn);
			subjectListt = obj.allSubjectClassWise(selectedCLassSection, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			subjectList=obj.AllEMployeeSubject(empid,selectedSection,schid,conn);
			subjectList2=obj.AllEMployeeSubject(empid,selectedSection,schid,conn);
			subjectListt=new DataBaseMeathodJson().AllEMployeeSubject(empid,selectedSection,schid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editSectionDetails() {
		Connection conn = DataBaseConnection.javaConnection();
		allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkNoOfTest()
	{
		if(noOfTest.equals("1"))
		{
			showSub1=true;showSub2=showName1=false;
		}
		else
		{
			showSub1=showSub2=showName1=true;
		}
	}

	public void addTest() {
		Connection conn = DataBaseConnection.javaConnection();
		idTest = getIdTest();
		int i = -1;
		if (subjectList.isEmpty()) 
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Subject Found."));
		}
		else
		{
			boolean duplicate=false;
			if(noOfTest.equals("1"))
			{
				for (SubjectInfo ss : subjectList) 
				{
					if (ss.getMarks() > 0) 
					{
						duplicate=dbValidate.checkDuplicateTest(selectedCLassSection, selectedSection,ss.getId(), testName,schid,session,conn,"");
						if(duplicate==true)
						{
							noOfTest="";
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Test Name Found For Subject "+ss.getSubjectName()+".. Please Try With Another One.", "Validation error"));
							break;
						}
						else
						{
							i = obj.addTest(selectedCLassSection, selectedSection, ss.getId(), testName,String.valueOf(ss.getMarks()), idTest, selectedDay, conn);
						
						}
					}
				}
			}
			else
			{
				for (SubjectInfo ss : subjectList)
				{
					if (ss.getMarks() > 0) 
					{
						duplicate=dbValidate.checkDuplicateTest(selectedCLassSection, selectedSection,ss.getId(), testName+"/"+subTest1,schid,session,conn,"");
						if(duplicate==true)
						{
							noOfTest="";
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Test Name Found For Subject "+ss.getSubjectName()+" for Sub Exam "+subTest1+".. Please Try With Another One.", "Validation error"));
							break;
						}
						else
						{
							i = obj.addTest(selectedCLassSection, selectedSection, ss.getId(), testName+"/"+subTest1,
								String.valueOf(ss.getMarks()), idTest, selectedDay, conn);
						}
					}
				}

				for (SubjectInfo ss : subjectList2) {
					if (ss.getMarks() > 0) 
					{
						duplicate=dbValidate.checkDuplicateTest(selectedCLassSection, selectedSection,ss.getId(), testName+"/"+subTest2,schid,session,conn,"");
						if(duplicate==true)
						{
							noOfTest="";
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Test Name Found For Subject "+ss.getSubjectName()+" for Sub Exam "+subTest2+".. Please Try With Another One.", "Validation error"));
							break;
						}
						else
						{
							i = obj.addTest(selectedCLassSection, selectedSection, ss.getId(), testName+"/"+subTest2,
								String.valueOf(ss.getMarks()), idTest, selectedDay, conn);
						}
					}
				}
			} 

			if (i > 0 && duplicate==false) 
			{
				if(noOfTest.equals("1"))
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					}
					catch (Exception e) {
					e.printStackTrace();
				    }
				}
				
				if(noOfTest.equals("2"))
				{
					String refNo;
					try {
						refNo=addWorkLog2(conn);
					}
					catch (Exception e) {
					e.printStackTrace();
				    }
				}
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Add Successfully"));
				testName = testMarks=noOfTest = "";
				selectedDay = new Date();
				subjectList=subjectList2 = new ArrayList<>();
				showSub1=showSub2=showName1=false;
				subTest1=subTest2="";
				subjectListt = new ArrayList<>();
				selectedCLassSection = "";
				allClass(conn);
				sectionList = new ArrayList<>();
				list = obj.viewTestList(conn);
			} else if (i == -1 && duplicate==false) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Marks must be greater than ZERO."));
			} else if(duplicate==false){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean table = false;
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class - "+className+" --- Section - "+sectionname+" --- TestName -"+testName+" --- Testdate -"+selectedDay+" --- No of test-"+noOfTest;
		
		for (SubjectInfo ss : subjectList)
		{
		  value += "( Id - "+ss.getId()+" -- marks - "+ss.getMarks()+")";
		}
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add  Class Test","WEB",value,conn);
		return refNo;
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "1. "+subTest1+" -- ";
		String language= "";

		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class - "+className+" --- Section - "+sectionname+" --- TestName -"+testName+" --- Testdate -"+selectedDay+" --- No of test-"+noOfTest;
		
		for (SubjectInfo ss : subjectList)
		{
		  value += "( Id - "+ss.getId()+" -- marks - "+ss.getMarks()+")";
		}
		
		value +=  " 2."+subTest2+" -- ";
		for (SubjectInfo ss : subjectList2)
		{
		  value += "( Id - "+ss.getId()+" -- marks - "+ss.getMarks()+")";
		}
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add  Class Test","WEB",value,conn);
		return refNo;
	}
	

	public void editTestDetails() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		boolean status = DBM.checkTest(selectedTest.getId(), conn);
		if (status == true) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("You Cannot Edit Details After Adding Performance"));
		} else {
			selectedCLassSection = selectedTest.getClassid();
			allSections();
			selectedSection = selectedTest.getSectionid();
			selectedSubject = selectedTest.getSubid();
			idprevoiustest = selectedTest.getId();
			testName = selectedTest.getTestName();
			testMarks = selectedTest.getTestMarks();
			if(userType.equalsIgnoreCase("admin") 
					|| userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal"))
			{
				subjectListt = obj.allSubjectClassWise(selectedCLassSection, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				subjectListt=new DataBaseMeathodJson().AllEMployeeSubject(empid,selectedSection,schid,conn);
			}
			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('editDialog').show();");
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editNow() {
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			int i=0;
			FacesContext fc = FacesContext.getCurrentInstance();
			boolean duplicate=false;
			if(Integer.parseInt(testMarks)>0)
			{
				duplicate=dbValidate.checkDuplicateTest(selectedCLassSection, selectedSection,selectedSubject, testName,schid,session,conn,selectedTest.getId());
				if(duplicate==false)
				{
					i = obj.updateTests(selectedCLassSection, selectedSection, selectedSubject, testName, testMarks,idprevoiustest, conn);
					if (i == 1) 
					{
						String refNo;
						try {
							refNo=addWorkLog5(conn);
						}
						catch (Exception e) {
						e.printStackTrace();
					    }
						fc.addMessage(null, new FacesMessage("Test details updated successfully"));
						list = obj.viewTestList(conn);

					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"An error occurred, try again  ", "Validation error"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Duplicate Entry Found.... please try with another one", "Validation error"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Marks Must Be Greater Than Zero", "Validation error"));
			}
			
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
	
	public String addWorkLog5(Connection conn)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);

		value = "Class - "+className+" -- Section - "+sectionname+" -- Subject - "+selectedSubject+" -- Test Name -"+testName+" -- Test marks -"+testMarks+" -- IdPreviousTest"+idprevoiustest;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Class Test","WEB",value,conn);
		return refNo;
	}
	

	public void deleteTest() {
		Connection conn = DataBaseConnection.javaConnection();
		
		
		int i = obj.deleteClassTest(selectedTest.getId(), conn);
		if (i == 1) {
			
			String refNo;
			try {
				refNo=addWorkLog3(conn);
			}
			catch (Exception e) {
			e.printStackTrace();
		    }
//			obj.deleteClassTestPerformance(selectedTest.getId(), conn);
//			
//			String refNo2;
//			try {
//				refNo2=addWorkLog4(conn);
//			}
//			catch (Exception e) {
//			// TODO: handle exception
//		    }
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Test Deleted Successfully"));
			list = obj.viewTestList(conn);
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("An Error Occured... Please Try Again"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = selectedTest.getId();
		String language= "";

		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class Test","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog4(Connection conn)
	{
	    String value = selectedTest.getId();
		String language= "";

		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class Test performance","WEB",value,conn);
		return refNo;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestMarks() {
		return testMarks;
	}

	public void setTestMarks(String testMarks) {
		this.testMarks = testMarks;
	}

	String idprevoiustest;

	public String getIdTest() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			String temp = String.valueOf(obj.testId(conn));
			idTest = "Test" + temp;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return idTest;
	}

	public void setIdTest(String idTest) {
		this.idTest = idTest;
	}

	public ArrayList<ClassTest> getList() {
		return list;
	}

	public void setList(ArrayList<ClassTest> list) {
		this.list = list;
	}

	public ClassTest getSelectedTest() {
		return selectedTest;
	}

	public void setSelectedTest(ClassTest selectedTest) {
		this.selectedTest = selectedTest;
	}

	public String getIdprevoiustest() {
		return idprevoiustest;
	}

	public void setIdprevoiustest(String idprevoiustest) {
		this.idprevoiustest = idprevoiustest;
	}

	public boolean isTable() {
		return table;
	}

	public void setTable(boolean table) {
		this.table = table;
	}

	public Date getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(Date selectedDay) {
		this.selectedDay = selectedDay;
	}

	public ArrayList<SelectItem> getSubjectListt() {
		return subjectListt;
	}

	public void setSubjectListt(ArrayList<SelectItem> subjectListt) {
		this.subjectListt = subjectListt;
	}

	public boolean isSectionField() {
		return sectionField;
	}

	public void setSectionField(boolean sectionField) {
		this.sectionField = sectionField;
	}

	public ArrayList<SubjectInfo> getSubjectList2() {
		return subjectList2;
	}

	public void setSubjectList2(ArrayList<SubjectInfo> subjectList2) {
		this.subjectList2 = subjectList2;
	}

	public String getSubTest1() {
		return subTest1;
	}

	public void setSubTest1(String subTest1) {
		this.subTest1 = subTest1;
	}

	public String getSubTest2() {
		return subTest2;
	}

	public void setSubTest2(String subTest2) {
		this.subTest2 = subTest2;
	}

	public boolean isShowSub1() {
		return showSub1;
	}

	public void setShowSub1(boolean showSub1) {
		this.showSub1 = showSub1;
	}

	public boolean isShowSub2() {
		return showSub2;
	}

	public void setShowSub2(boolean showSub2) {
		this.showSub2 = showSub2;
	}

	public String getNoOfTest() {
		return noOfTest;
	}

	public void setNoOfTest(String noOfTest) {
		this.noOfTest = noOfTest;
	}

	public boolean isShowName1() {
		return showName1;
	}

	public void setShowName1(boolean showName1) {
		this.showName1 = showName1;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isShowType() {
		return showType;
	}

	public void setShowType(boolean showType) {
		this.showType = showType;
	}

	public boolean isShowSectionBut() {
		return showSectionBut;
	}

	public void setShowSectionBut(boolean showSectionBut) {
		this.showSectionBut = showSectionBut;
	}
	

}
