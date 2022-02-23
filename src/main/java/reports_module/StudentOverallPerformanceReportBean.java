package reports_module;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;
import session_work.QueryConstants;

@ManagedBean(name="overallPerReport")
@ViewScoped
public class StudentOverallPerformanceReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	int subjectListSize=0;
	boolean show;
	ArrayList<SelectItem> classSection,sectionList;
	ArrayList<SubjectInfo> subjectList;
	ArrayList<String> selectedTerm;
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();

	ArrayList<String> scholasticColumnsList = new ArrayList<>();
	String selectedClassSection,selectedSection,subjectType;
	String className,sectionName,schId,session,username,userType;
	StudentInfo selectedStudent;
	ArrayList<TermInfo> termList;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	ExamSettingInfo examSetting=new ExamSettingInfo();
    ArrayList<SelectItem>termNewList,subjectTypeList;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termNewList=obj1.selectedTermOfClass(selectedClassSection,conn,session,schId);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=obj1.allSection(selectedClassSection,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
			sectionList=obj1.allSectionForTeacher(selectedClassSection,empid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public StudentOverallPerformanceReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		String addNumber=(String) ss.getAttribute("username");
		String type=(String) ss.getAttribute("type");
		subjectTypeList=obj1.subjectTypeList();
		schId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schId, conn);
		
		if(!type.equalsIgnoreCase("student"))
		{
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff"))
			{
				classSection=obj1.allClass(conn);
			}
			else if(userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
				classSection=obj1.cordinatorClassList(empid, schId, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
				classSection=obj1.allClassListForClassTeacher(empid,schId,conn);
			}
		}
		else
		{
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schId, list, conn).get(0);
			studentList.add(sn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void searchReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int termSize=0;show=true;
		studentList=objExam.studentBasicDetailsForMarksheet(schId,"", conn,"byClass",selectedSection);
		examSetting=objExam.examSettingDetail(selectedClassSection, conn);
		if(studentList.size()>0)
		{
			className=studentList.get(0).getClassName();
			sectionName=studentList.get(0).getSectionName();
			String termId="";
			for(String term:selectedTerm)
			{
				termId+=term+"','";
			}
			termId=termId.substring(0,termId.lastIndexOf("','"));
			termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, subjectType, conn, "all","","","","","yes","FINAL MARKS","","","yes","PERCENT","","","",schId);

			scholasticColumnsList.clear();
			ArrayList<SubjectInfo> tempSubjectList=new ArrayList<>(); 
			ArrayList<SubjectInfo> subjectList=objExam.subjectListBySubjectTypeForExam(selectedClassSection,subjectType,conn);
			subjectListSize=subjectList.size();
			for(TermInfo term:termList)
			{
				if(Integer.parseInt(term.getTermId())>0)
				{
					termSize++;
					term.setSubjectList(subjectList);
				}
				else if(term.getTermId().equals("-9"))
				{
					tempSubjectList.clear();
					SubjectInfo sub=new SubjectInfo();
					sub.setSubjectName("");
					sub.setDescription("");
					sub.setHeader(true);
					sub.setSubjectId("finalTotal");
					tempSubjectList.add(sub);
					term.setSubjectList(tempSubjectList);
				}
				else if(term.getTermId().equals("-11"))
				{
					tempSubjectList.clear();
					SubjectInfo sub=new SubjectInfo();
					sub.setSubjectName("");
					sub.setDescription("");
					sub.setHeader(true);
					sub.setSubjectId("finalPercent");
					tempSubjectList.add(sub);
					term.setSubjectList(tempSubjectList);
				}
					
				for(SubjectInfo subject:term.getSubjectList())
				{
					scholasticColumnsList.add(term.getTermId()+"-"+subject.getSubjectId());
				}
			}
			
			String comeTestType="all";
			for(StudentInfo allInfo: studentList)
			{
				if(subjectType.equals("scholastic") ||subjectType.equalsIgnoreCase("additional"))
				{
					objExam.fillStudentMarks(allInfo.getSectionid(),allInfo.getAddNumber(),subjectList,session,termList,conn,allInfo,scholasticColumnsList,termSize,subjectType,subjectListSize,examSetting,"overallReport",comeTestType,schId);
				}
				else
				{
					objBLM.fillMarksForCoscholasticSubject(/*classid, */allInfo.getSectionid(),subjectList, allInfo.getAddNumber(), session, termList,subjectType,conn,allInfo,"overallReport");
				}
				
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}

		try
		{
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void printReport() throws IOException, URISyntaxException
	{
		
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("className",className);
		ss.setAttribute("studentList", studentList);
		ss.setAttribute("sectionName", sectionName);
		ss.setAttribute("termList", termList);
		ss.setAttribute("scholasticColumnsList", scholasticColumnsList);
		ss.setAttribute("show", show);
		FacesContext.getCurrentInstance().getExternalContext().redirect("printStudentOverallPerformanceReport.xhtml");
		//PrimeFaces.current().executeInitScript("window.open('printStudentOverallPerformanceReport.xhtml ')");
	}
	
	
	
	public void toNum(Object doc)
	{
	





	//Workbook adjustmnent
	XSSFWorkbook book = (XSSFWorkbook)doc;
	XSSFSheet sheet = book.getSheetAt(0);
	XSSFRow h1 = sheet.createRow(0);

	sheet.createFreezePane(0,4);
	XSSFRow headerRow = sheet.getRow(0);
	     
	    headerRow.setHeight((short)1200);
	     
	  CellStyle style = book.createCellStyle();
	  XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
	  ((XSSFCellStyle) style).setFillForegroundColor(color);
	  style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	  style.setVerticalAlignment(VerticalAlignment.TOP);
	  style.setBorderLeft(BorderStyle.THIN);            
	  style.setBorderRight(BorderStyle.THIN);            
	  style.setBorderTop(BorderStyle.THIN);              
	  style.setBorderBottom(BorderStyle.NONE);
	 
	  style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
	  style.setTopBorderColor(IndexedColors.RED.getIndex());
	  style.setLeftBorderColor(IndexedColors.RED.getIndex());
	  style.setRightBorderColor(IndexedColors.RED.getIndex());

	     
	   
	     
	     
	     
	     
	           //Second Row Labels Printing
	      XSSFRow first = sheet.createRow(2);
	      CellStyle styleF5 = book.createCellStyle();
//	      styleF5.setVerticalAlignment((short)0.5);
	     
	 
	     
	     XSSFCell firstCell = first.createCell(0);
	     firstCell.setCellValue("S.No.");
	     firstCell.setCellStyle(styleF5);
	   
	   
	     
	     XSSFCell thirdCell = first.createCell(1);
	     thirdCell.setCellValue("Student Name");
	     thirdCell.setCellStyle(styleF5);
	   
	   
	     
	     
	     
	     //For Printing Term names
	     int sizTermlatest =0;
	     for(int i=0;i<termList.size()-2;i++)
	     {
	   
	               
	           CellStyle styleTerm = book.createCellStyle();
	               
	           if(i%2==0)
	           {
	               XSSFColor color0561 = new XSSFColor(new java.awt.Color(100,225,170));
	               ((XSSFCellStyle) styleTerm).setFillForegroundColor(color0561);
	               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	                 
	           }
	           else
	           {
	               XSSFColor color201 = new XSSFColor(new java.awt.Color(195,200,250));
	               ((XSSFCellStyle) styleTerm).setFillForegroundColor(color201);
	               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	               
	           }
	           
	      styleTerm.setVerticalAlignment(VerticalAlignment.CENTER);
	     
	      int holdSizeTermValue = 0;
	      holdSizeTermValue = sizTermlatest;
	 
	     XSSFCell fifthCell = first.createCell(2+sizTermlatest);
	     fifthCell.setCellValue(termList.get(i).getTermName());
	     fifthCell.setCellStyle(styleTerm);
	     
	    
	     sizTermlatest += termList.get(i).getSubjectList().size();
	     
	     
	   
	       sheet.addMergedRegion(new CellRangeAddress(2,2,2+holdSizeTermValue,2+sizTermlatest-1));
	   
	     }  
	     XSSFCell totalCell = first.createCell(2+sizTermlatest);
	     totalCell.setCellValue("FINAL MARKS");
	     
	     XSSFCell percentCell = first.createCell(3+sizTermlatest);
	     percentCell.setCellValue("PERCENT");
	   
	     
	     
	     
	     
	     //For Printing Subject List
	     int counterrrr=0;
	     XSSFRow fifthh = sheet.createRow(3);
	     for(int k=0;k<termList.size()-2;k++)
	     {
	       for(int j=0;j<termList.get(k).getSubjectList().size();j++)
	       {
	         XSSFCell f2cell = fifthh.createCell(2+counterrrr);
	         counterrrr ++;
	         f2cell.setCellValue(termList.get(k).getSubjectList().get(j).getSubjectName());
	         f2cell.setCellStyle(style);
	       }
	     
	          
	     }  

	 //DATA 
	  XSSFCellStyle intStyle = book.createCellStyle();
	  intStyle.setDataFormat((short)1);

	  XSSFCellStyle decStyle = book.createCellStyle();
	  decStyle.setDataFormat((short)2);

	  XSSFCellStyle dollarStyle = book.createCellStyle();
	  dollarStyle.setDataFormat((short)5);
	 
//	try {
//System.out.println("size->"+studentList.size());
//	  for(int rowInd = 4; rowInd < studentList.size(); rowInd++) {
//	      XSSFRow row = sheet.getRow(rowInd);
//
//	      for(int cellInd = 0; cellInd <=4+counterrrr+1 ; cellInd++) {
//	         
//	    	  System.out.println(rowInd);
//	          XSSFCell cell = row.getCell(cellInd);
//	          try {
//	        	  String strVal = cell.getStringCellValue();
//	        	  cell.setCellValue(strVal);
//			} catch (Exception e) {
////				cell.setCellValue("");
//			}
//	          
//	        
//	          
//	      }
//	 }
//
//	} catch (Exception e) {
//	   e.printStackTrace();
//	}
	
	}
	
	
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public ArrayList<SelectItem> getTermNewList() {
		return termNewList;
	}

	public void setTermNewList(ArrayList<SelectItem> termNewList) {
		this.termNewList = termNewList;
	}
	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}

	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public ArrayList<String> getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(ArrayList<String> selectedTerm) {
		this.selectedTerm = selectedTerm;
	}
	
}
