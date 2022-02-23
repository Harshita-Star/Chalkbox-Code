package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ManagedBean(name = "addPerformance")
@ViewScoped
public class TestPerformance implements Serializable {

	ArrayList<SelectItem> test, subjectList, sectionList,classList;
	String selectedTest, testName, testMarks,selectedClass,selectedSection,balMsg,filePath;
	ArrayList<StudentInfo> studentDetails;
	ArrayList<StudentInfo> selectedabsentStudent = new ArrayList<>();
	StudentInfo selected;
	String section, classId,subject, subjectDetail,type,username,schoolid,uploadType="1";
	boolean showStudentRecord, showStudentRecordButton,showSection;
	boolean renderShowTable = false,showExcelUpload=false,showUploadFile=false;
	SchoolInfoList schoolInfo;
	Date testDate=new Date();
	int maxmarks=0;
	transient UploadedFile excelFile;
	String savePath="",fileName="",clsTch;
	ArrayList<StudentInfo> finalList;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public TestPerformance() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		schoolInfo = obj.fullSchoolInfo(conn);
		uploadType="0";
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
				|| type.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			clsTch = new DataBaseMeathodJson().checkClassTeacherById(empid, schoolid, conn);
			classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
		}

//		if(schoolInfo.getBranch_id().equals("13"))
//		{
//			showSection=false;
//		}
//		else
//		{
//			showSection=true;
//		}

		//test = obj.allTest(schoolInfo.getClient_type(), conn);

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
//		if(schoolInfo.getBranch_id().equals("13"))
//		{
//
//			selectedTest = "";
//			test = obj.allTest(schoolInfo.getClient_type(),selectedClass,selectedSection,testDate, conn);
//		}
//		else
//		{
			if(type.equalsIgnoreCase("admin")
					|| type.equalsIgnoreCase("academic coordinator")
					|| type.equalsIgnoreCase("authority")
					|| type.equalsIgnoreCase("principal")
					|| type.equalsIgnoreCase("vice principal")
					|| type.equalsIgnoreCase("front office") || type.equalsIgnoreCase("office staff")
					|| type.equalsIgnoreCase("Administrative Officer")
					|| type.equalsIgnoreCase("Accounts"))
			{
				sectionList=obj.allSection(selectedClass,conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
				
				sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
			}
			test = new ArrayList<>();
		//}
		allTest();
		

		selectedTest = "";
		selectedSection="";
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
		
		if(selectedSection == null || selectedSection.equals("")) {
			selectedSection = "";
		}
		test = obj.allTest(schoolInfo.getClient_type(),selectedClass,selectedSection,testDate, conn);

		showStudentRecord=false;
		showStudentRecordButton=false;renderShowTable = false;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public void allTestIIT()
	{
		Connection conn=DataBaseConnection.javaConnection();
		test = new ArrayList<>();
		selectedTest = "";
		if(schoolInfo.getBranch_id().equals("13"))
		{
			test = obj.allTest(schoolInfo.getClient_type(),selectedClass,selectedSection, conn);

			showStudentRecord=false;
			showStudentRecordButton=false;renderShowTable = false;
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public void sessionDetails()
	{
		studentDetail();
	}
	
	public void showExcelOption()
	{
		if(uploadType.equals("0"))
		{
			renderShowTable = false;
			showUploadFile=true;
			
		}
		else
		{
			renderShowTable = true;
			showUploadFile=false;
			
		}
		sessionDetails();
	}
	

	public void studentDetail() 
	{
		studentDetails= new ArrayList<StudentInfo>();
		selectedabsentStudent = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		testName = DBM.searchTest(selectedTest, conn);
		subject = DBM.searchSubject(selectedTest, conn);
		classId = DBM.searchClass(selectedTest, conn);
		subjectDetail = DBM.subjectDetailsForClassTest(selectedTest, conn);
		String[] temp = subjectDetail.split(",");
//		if (schoolInfo.getClient_type().equalsIgnoreCase("institute"))
//		{
			sectionList = DBM.sectionNameAndIdListByid(classId, conn);
			if (temp[1].equalsIgnoreCase("Mandatory"))
			{
				if(selectedSection.equals(""))
				{
					studentDetails = DBM.searchStudentListByClass(sectionList, conn);
				}
				else
				{
					studentDetails = DBM.searchStudentListbysection(selectedSection, conn);
				}
			}
			else
			{
				section = DBM.searchSection(selectedTest, conn);
				if(selectedSection.equals(""))
				{
					studentDetails = DBM.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],"","","subjectwise", conn);
				}
				else
				{
					studentDetails = DBM.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],selectedSection,selectedClass,"classwise", conn);
				}

			}

//		}
//		else
//		{
//			section = DBM.searchSection(selectedTest, conn);
//			if (temp[1].equalsIgnoreCase("Mandatory"))
//			{
//				studentDetails = DBM.searchStudentListbysection(selectedSection, conn);
//			}
//			else
//			{
//				studentDetails = DBM.getAllStudentStrentgthForOptional(schoolInfo.getSchid(),temp[2],selectedSection,selectedClass,"classwise", conn);
//			}
//		}
		DatabaseMethods1.testPerformance(studentDetails, selectedTest, conn);
		// DatabaseMethods1.allPerformance(selectedSection, term, selectedExam,
		// studentDetails, subjectList,selectedType,selectedSubject);
		if (studentDetails.size() > 0) {
			for (StudentInfo ss : studentDetails) {
				if (ss.getTestMarks().equalsIgnoreCase("AB")) {
					selectedabsentStudent.add(ss);
				}
			}
		}
		if (studentDetails.size() > 0) {
			showStudentRecordButton = true;
		} else {
			showStudentRecordButton = false;
		}

		showStudentRecord = true;
		maxmarks = 0;
		maxmarks = obj.getTestMaxMarks(selectedTest, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public void addMessage() {
//		for(StudentInfo k : studentDetails) {
//			if(k.isNaStudentsBooolean()) {
//				k.setTestMarks("NA");
//			}else if(k.isNaStudentsBooolean() == false) {
//				k.setTestMarks("00");
//			}
//		}
//	}
	
	public void saveData() {



		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int flag = 0;

		//maxmarks = obj.getTestMaxMarks(selectedTest, conn);
		for (StudentInfo rr : studentDetails) {
			try {
				if (rr.getTestMarks() == null || rr.getTestMarks().equals("")) {
					flag = 1;
				} else {

					if (!rr.getTestMarks().equalsIgnoreCase("AB") && !rr.getTestMarks().equalsIgnoreCase("NA")) {
						if (maxmarks < Double.parseDouble(rr.getTestMarks())) {
							flag = 1;
						}
					}else if(rr.getTestMarks().equalsIgnoreCase("AB")) {
						rr.setTestMarks("AB");
					}
				}
			} catch (Exception e) {
				flag = 2;
				e.printStackTrace();
			}

		}
		
		if (flag == 0) {
			try {
				obj.addTestPerformance(selectedTest, studentDetails, selectedabsentStudent, conn);
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				renderShowTable = false;
				selectedTest = null;
				studentDetails = new ArrayList<>();
				selectedabsentStudent = new ArrayList<>();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Performance record updated successfully"));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (flag == 2) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please enter only Number And AB For Absent "));

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please enter Correct Marks"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void saveDataByExcel() 
	{

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int flag = 0;

		//maxmarks = obj.getTestMaxMarks(selectedTest, conn);

		for (StudentInfo rr : studentDetails) {

			try {
				if (rr.getTestMarks() == null || rr.getTestMarks().equals("")) {
					flag = 1;
				} else {

					if (!rr.getTestMarks().equalsIgnoreCase("AB") && !rr.getTestMarks().equalsIgnoreCase("NA")) {
						if (maxmarks < Double.parseDouble(rr.getTestMarks())) {
							flag = 1;
						}
					}

				}
			} catch (Exception e) {
				flag = 2;
			}

		}

		if (flag == 0) {
			try {
				ArrayList<StudentInfo>list=new ArrayList<StudentInfo>();
				obj.addTestPerformance(selectedTest, studentDetails, list, conn);
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				renderShowTable = false;
				selectedTest = null;
				studentDetails = new ArrayList<>();
				selectedabsentStudent = new ArrayList<>();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Performance record updated successfully"));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (flag == 2) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please enter only Number And AB For Absent "));

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please enter Correct Marks"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = "Class -"+className+" -- Section -"+sectionname+" -- Date -"+testDate+" -- Test -"+selectedTest;
		
		for (StudentInfo tt : selectedabsentStudent) 
		{
		  value += "( Student -"+tt.getAddNumber()+" - AB)";	
		}
		value += " --- ";
		for (StudentInfo tt : studentDetails) 
		{
			if (tt.isNaStudentsBooolean()) {
				value += "( Student -"+tt.getAddNumber()+" - NA)";	
			}
			else
			{
				value += "( Student -"+tt.getAddNumber()+" --- Marks -"+tt.getTestMarks()+")";	
			}
		  
		}	
		
			
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Class Test Performance","WEB",value,conn);
		return refNo;
	}

	public String saveDataAndSendMessage() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();

		int flag = 0;


		for (StudentInfo rr : studentDetails) {

			try {

				if (rr.getTestMarks() == null || rr.getTestMarks().equals("")) {
					flag = 1;
				} else {

					if (!rr.getTestMarks().equalsIgnoreCase("AB") && !rr.getTestMarks().equalsIgnoreCase("NA")) {
						if (maxmarks < Double.parseDouble(rr.getTestMarks())) {
							flag = 1;
						}
					}else if(rr.getTestMarks().equalsIgnoreCase("AB")) {
						rr.setTestMarks("AB");
					}

				}
			} catch (Exception e) {
				flag = 2;
				e.printStackTrace();
			}

		}

		if (flag == 0) {
			try {
				obj.addTestPerformance1(selectedTest, studentDetails, subject, String.valueOf(maxmarks),
						selectedabsentStudent, testName, conn);
				renderShowTable = false;
				selectedTest = null;
				studentDetails = new ArrayList<>();
				selectedabsentStudent = new ArrayList<>();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Performance record updated successfully"));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (flag == 2) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please enter only Number And AB For Absent "));

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please enter Correct Marks"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void importFromExcelInRegTableAll() throws IOException, ParseException
	{
		if (excelFile != null && excelFile.getSize() > 0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			
			filePath = excelFile.getFileName();
			fileName = excelFile.getFileName();
			if(filePath!=null || !filePath.equals(""))
			{
				if(filePath.contains(".xls"))
				{
					 obj.makeScanPath(excelFile,filePath,conn);	//Online
						
						SchoolInfoList ls = obj.fullSchoolInfo(conn);
						
						if (ls.getProjecttype().equals("online")) {
							String folderName = ls.getUploadpath();
							savePath = folderName;

						} else {
							ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
							String realPath = ctx.getRealPath("/");
							savePath = realPath;
						}
						filePath=savePath+filePath;
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						String filename = filePath;
						List sheetData = new ArrayList();

						try
						{
							XSSFWorkbook workbook=null;
							try
							{
								workbook = new XSSFWorkbook(filePath);
							}
							catch(Exception e)
							{
								e.printStackTrace();
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Excel Version Must Be greater than 2007"));
							}
							
							if(workbook!=null)
							{
								XSSFSheet sheet = workbook.getSheetAt(0);
								Iterator rows = sheet.rowIterator();
								while (rows.hasNext())
								{
									XSSFRow row = (XSSFRow) rows.next();
									for (int cn=0; cn<row.getLastCellNum(); cn++) 
									{
							            Cell cell = row.getCell(cn);
							            if (cell == null) 
							            	cell = row.createCell(cn);
							         }
									Iterator cells = row.cellIterator();
									List data = new ArrayList();
									while (cells.hasNext())
									{
										XSSFCell cell = (XSSFCell) cells.next();
										data.add(cell);
									}
									sheetData.add(data);
								}
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						showExelDataAll(sheetData);
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please upload file in correct formate"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
			}
		   
		}

	}
	
	
	private  void showExelDataAll(List sheetData) throws ParseException
	{
		try 
		{
			selectedabsentStudent = new ArrayList<>();
			
			int sNo=0;
			
			outer:for (int i =1;i < sheetData.size(); i++) 
			{
				List list = (List) sheetData.get(i);
                
			  inner: for(StudentInfo ls:studentDetails)
			   {
				
			   for (int j=1;j<list.size(); j++)
				{
				    
					String temp="";
					Cell cell = (Cell) list.get(j);
					
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
					{
//						DataFormatter fmt = new DataFormatter();
//						String valueAsSeenInExcel = fmt.formatCellValue(cell);
//						temp=valueAsSeenInExcel;
						temp=String.valueOf(cell.getNumericCellValue());
						

					} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						temp=String.valueOf(cell.getRichStringCellValue());

					} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
						temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
					}
				
					
					if(j==1)
					{
						temp.trim();
						temp=String.valueOf(temp);
						if(temp.equals(ls.getAddNumber()))
						{
							
						}
						else
						{
							break;
						}
					}
					if(j==6)
					{
						temp.trim();
						temp=String.valueOf(temp);
						ls.setTestMarks(temp);
						
						
						break inner;
					}
					
					
				}
			}
			}	
		} catch (Exception e) {
		}
		
		
	}
	
	
	public void selectStudent(ArrayList<StudentInfo> sList)
	{
		
		finalList= new ArrayList<StudentInfo>();
		studentDetail();
		int count =0;
		for(StudentInfo obj : sList)
		{
			for(int i=0;i<studentDetails.size();i++)
			{
				if(studentDetails.get(i).getRollNo().equals(obj.getRollNo().substring(0, obj.getRollNo().length()-2)))
				{
					StudentInfo mObj = studentDetails.get(i);
					mObj.setSno(++count);
					mObj.setTestMarks(obj.getTestMarks());
					finalList.add(mObj);
					break;
				}
					
			}
		}
		if(finalList.size()>0)
		{
			studentDetails=finalList;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class students are different than excel file. please re-check"));
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

	public ArrayList<StudentInfo> getSelectedabsentStudent() {
		return selectedabsentStudent;
	}

	public void setSelectedabsentStudent(ArrayList<StudentInfo> selectedabsentStudent) {
		this.selectedabsentStudent = selectedabsentStudent;
	}

	public String getTestMarks() {
		return testMarks;
	}

	public void setTestMarks(String testMarks) {
		this.testMarks = testMarks;
	}

	public StudentInfo getSelected() {
		return selected;
	}

	public void setSelected(StudentInfo selected) {
		this.selected = selected;
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

	public int getMaxmarks() {
		return maxmarks;
	}

	public void setMaxmarks(int maxmarks) {
		this.maxmarks = maxmarks;
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

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public boolean isShowExcelUpload() {
		return showExcelUpload;
	}

	public void setShowExcelUpload(boolean showExcelUpload) {
		this.showExcelUpload = showExcelUpload;
	}

	public boolean isShowUploadFile() {
		return showUploadFile;
	}

	public void setShowUploadFile(boolean showUploadFile) {
		this.showUploadFile = showUploadFile;
	}

	public UploadedFile getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(UploadedFile excelFile) {
		this.excelFile = excelFile;

	}

}
