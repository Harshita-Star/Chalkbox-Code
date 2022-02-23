package reports_module;

import java.io.Serializable;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
import exam_module.ExamInfo;
import exam_module.SubExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;
import session_work.QueryConstants;

@ManagedBean(name="fullMarksheetReport")
@ViewScoped
public class StudentFullMarkSheetReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<StudentInfo> studentList=new ArrayList<>();

	boolean show,showSubExamRow=false,showExam;
	ArrayList<SelectItem> classSection,sectionList,termNewList,subjectTypeList,allExamList;
	ArrayList<String> scholasticColumnsList = new ArrayList<>(),selectedTerm=new ArrayList<>();
	ArrayList<String> selectdExam;
	String selectedClassSection,selectedSection,subjectType,examType,session,className,sectionName,termId,
	schid,termName, examId, examName, examID, username, userType;
	ArrayList<TermInfo> termList;
	ArrayList<SubjectInfo> subjectList;
	int termSize,subjectListSize;
	SchoolInfoList schoolDetails;
	ExamSettingInfo examSetting=new ExamSettingInfo();
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termNewList=obj1.selectedTermOfClass(selectedClassSection,conn,session,schid);
		subjectType="";
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
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			sectionList=obj1.allSectionForTeacher(selectedClassSection,empid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void chk() {
		Connection conn=DataBaseConnection.javaConnection();
		examType = subjectType;
		selectedTerm = new ArrayList<>();
		selectdExam =  new ArrayList<>();
		allExamList =  new ArrayList<>();
		showExam = false;
		termNewList=obj1.selectedTermOfClass(selectedClassSection,conn,session,schid);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public StudentFullMarkSheetReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolDetails =obj1.fullSchoolInfo(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		boolean check=(boolean) ss.getAttribute("checkstu");
		String addNumber=(String) ss.getAttribute("username");
		schid=obj1.schoolId();
		session=obj1.selectedSessionDetails(schid, conn);
		
		subjectTypeList=obj1.subjectTypeList();
		if(check==false)
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
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection=obj1.cordinatorClassList(empid, schid, conn);
			}
			else
			{
				String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
				classSection=obj1.allClassListForClassTeacher(empid,schid,conn);
			}
		}
		else
		{
			ArrayList<String> list=objStd.basicFieldsForStudentList();
			StudentInfo sn=objStd.studentDetail(addNumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, list, conn).get(0);
			studentList.add(sn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void fullExam() {
		Connection conn = DataBaseConnection.javaConnection();
		int count = 0;
		int on = 0;
		if(examType.equalsIgnoreCase("scholastic")||examType.equalsIgnoreCase("additional")) {
			allExamList = new ArrayList<>();
			if (selectedTerm.size() > 0) {
				for (String term : selectedTerm) {
					count = 0;
					termId = term;
					termName = new DatabaseMethods1().semesterNameFromid(term, conn);
					ArrayList<ExamInfo> examInformation = objExam.getAllExamsForClass(selectedClassSection, examType,
							schid, session, termId, conn);
					if (examInformation.size() > 0) {
						StringBuilder str = new StringBuilder();
						for (ExamInfo exam : examInformation) {
							if (exam.getExamTaken().equalsIgnoreCase("periodicTest")) {
								if (exam.getInclude().equalsIgnoreCase("yes")) {
									on = 1;
									count = 1;
									str.append(exam.getId() + "','");
								}
							}
						}
						if(count==1) {
							examId = str.toString().substring(0, str.toString().length() - 3);
							SelectItem ll = new SelectItem();
							ExamSettingInfo examInfo = objExam.examSettingDetail(selectedClassSection, conn);
							examName = examInfo.getExamName();
							ll.setLabel(termName + " - " + examName);
							ll.setValue(termId + " - " + examId);
							allExamList.add(ll);
						}
						for (ExamInfo exam : examInformation) {
							examId = String.valueOf(exam.getId());
							if (exam.getId() > 0) {
								if (!exam.getExamTaken().equalsIgnoreCase("periodicTest")) {
									if (exam.getInclude().equalsIgnoreCase("yes")) {
										on = 1;
										examName = exam.getExamName();
										SelectItem al = new SelectItem();
										al.setLabel(termName + " - " + examName);
										al.setValue(termId + " - " + examId);
										allExamList.add(al);
									}
								}
							}
						}
					} 
				}
			} else {
				showExam = false;
				allExamList = new ArrayList<>();
			}
		}else {
			showExam = false;
			allExamList = new ArrayList<>();
		}
		
		if(on==0) {
			showExam = false;
		}else {
			showExam = true;
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchReport()
	{
		int method = 0;
		String msg = "";
		Connection conn=DataBaseConnection.javaConnection();
		studentList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		examSetting=objExam.examSettingDetail(selectedClassSection,conn);
		if(examType.equalsIgnoreCase("scholastic")||examType.equalsIgnoreCase("additional")) {
			if(selectedTerm.size()>0) {
				if(selectdExam.size()>0) {
					method = 1;
				}else {
					msg = "Please Select atleast one Exam.";
					method=0;
				}
			}else {
				msg = "Please Select atleast one term.";
				method = 0;
			}
		}else {
			if(selectedTerm.size()>0) {
				method = 1;
			}else {
				msg = "Please Select atleast one term.";
				method=0;
			}
		}
		
		if(method==1) {

			if(studentList.size()>0)
			{
				className=studentList.get(0).getClassName();
				sectionName=studentList.get(0).getSectionName();
				
				subjectList=objExam.subjectListBySubjectTypeForExam(selectedClassSection, examType, conn);
				subjectListSize=subjectList.size();
				
				termId="";examID="";
				
				if(examType.equalsIgnoreCase("scholastic")||examType.equalsIgnoreCase("additional")) {
					for (String term : selectdExam) {
						String[] temp = term.split("-");
						if (!termId.contains(temp[0].trim())) {
							termId += temp[0].trim() + "','";
						}
						examID += temp[1].trim() + "','";
					}
					termId = termId.substring(0, termId.lastIndexOf("','"));
					examID = examID.substring(0, examID.lastIndexOf("','"));
					
					
					termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, examType, conn, "all","","","","","yes","FINAL MARKS","","","yes","PERCENT",examSetting.getExamMarks(),examSetting.getExamName(),"",examID);
					
				}else {
					for(String term:selectedTerm)
					{
						termId+=term+"','";
					}
					termId=termId.substring(0,termId.lastIndexOf("','"));
					termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, examType, conn, "all","","","","","yes","FINAL MARKS","","","yes","PERCENT",examSetting.getExamMarks(),examSetting.getExamName(),"",schid);
					
				}
//				for(String term:selectedTerm)
//				{
//					termId+=term+"','";
//				}
//				termId=termId.substring(0,termId.lastIndexOf("','"));
//				termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, subjectType, conn, "all","","","","","yes","FINAL MARKS","","","yes","PERCENT",examSetting.getExamMarks(),examSetting.getExamName(),"");
//				//termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, subjectType, conn, "all","","","","","","","","","","","","");

				makeScholasticColumnList();
				
				String comeTestType="all";
				for(StudentInfo info: studentList)
				{
					if(examType.equals("scholastic") ||examType.equalsIgnoreCase("additional"))
					{
						objExam.fillStudentMarks(info.getSectionid(),info.getAddNumber(),subjectList,session,termList,conn,info,scholasticColumnsList,termSize,examType,subjectListSize,examSetting,"fullreport",comeTestType,schid);
					}
					else
					{
						objBLM.fillMarksForCoscholasticSubject(selectedSection, subjectList, info.getAddNumber(), session, termList,examType, conn, info,"fullreport");
					}
				}
				selectedTerm = null;
				selectdExam = null;
				allExamList = null;
				show=true;
				showExam = false;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
			}
		}else {
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
		}

		try
		{
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}


	public void makeScholasticColumnList()
	{
		int ptFlag=0,count=0;
		scholasticColumnsList.clear();
		ArrayList<SubjectInfo> tempSubjectList=new ArrayList<>();
		for(TermInfo term : termList)
		{
			ptFlag=0;
			if(Integer.parseInt(term.getTermId())>0)
			{
				termSize++;
				term.setSubjectList(subjectList);
				for(ExamInfo exam : term.getExamInfoList())
				{
					if(exam.getInclude().equals("Yes"))
					{
						if(exam.getSubExamList()!=null)
						{
							if(exam.getExamTaken().equals("other"))
							{
								for(SubExamInfo subExam : exam.getSubExamList())
								{
									if(!subExam.getSubExamUpperCase().trim().equals(""))
										count++;
									for(SubjectInfo subject:subjectList)
										scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam-"+subExam.getMainExamId()+"-subExam-"+subExam.getSubExamName()+"-subject-"+subject.getSubjectId());
								}
							}
							else
							{
								if(ptFlag==0)
								{
									for(SubjectInfo subject:subjectList)
									{
										scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam-P.T.-subExam-"+examSetting.getExamName()+"-subject-"+subject.getSubjectId());
										ptFlag=1;
									}
								}
							}
						}
						else
						{
							for(SubjectInfo subject:subjectList)
							{
								scholasticColumnsList.add("term-"+term.getTermId()+"-mainExam--subExam--subject-"+subject.getSubjectId());
							}
						}
					}
				}
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
				scholasticColumnsList.add("-9-finalTotal");
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
				scholasticColumnsList.add("-11-finalPercent");
			}
			
		}
		if(count!=0)
			showSubExamRow=true;
	}
	
	
	public void toNum(Object doc)
	{
	if(showSubExamRow==false)
	{	
	//if subExam Not Present	
	if(subjectType.equalsIgnoreCase("scholastic") || subjectType.equalsIgnoreCase("additional"))
	{	

	   XSSFWorkbook book = (XSSFWorkbook)doc;
	   XSSFSheet sheet = book.getSheetAt(0);

	   sheet.createFreezePane(0,5);
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
	     
	      
	     
	   
	      int sizeFirst = 0;
	   
	     
	   
	   
	      XSSFRow first = sheet.createRow(2);
	      CellStyle styleF5 = book.createCellStyle();
//	      styleF5.setVerticalAlignment((short)0.5);
	     
	 
	     
	     sheet.addMergedRegion(new CellRangeAddress(2,5,0,0));  
	     XSSFCell firstCell = first.createCell(0);
	     firstCell.setCellValue("S.No.");
	     firstCell.setCellStyle(styleF5);
	   
	     
	     
    	 sheet.addMergedRegion(new CellRangeAddress(2,5,1,1));  
	     XSSFCell secondCell = first.createCell(1);	   
	     secondCell.setCellValue("Add.No.");
	     secondCell.setCellStyle(styleF5);
	     
	     
	     
	     sheet.addMergedRegion(new CellRangeAddress(2,5,2,2));  
	     XSSFCell thirdCell = first.createCell(2);
	     thirdCell.setCellValue("Student Name");
	     thirdCell.setCellStyle(styleF5);
	   
	     
	     
	     sheet.addMergedRegion(new CellRangeAddress(2,5,3,3));  
	     XSSFCell forthCell = first.createCell(3);
	     forthCell.setCellValue("Father Name");
	     forthCell.setCellStyle(styleF5);
	   
	     
	     
	     //For Printing Term Names
	     for(int i=0;i<termList.size()-2;i++) {
	     sizeFirst +=termList.get(i).getExamListSize()*termList.get(i).getSubjectList().size();
	                int sizlatest = termList.get(i).getExamListSize()*termList.get(i).getSubjectList().size();
	                

	               
	                CellStyle styleTerm = book.createCellStyle();
	                if(i%2==0) {
	               XSSFColor color0561 = new XSSFColor(new java.awt.Color(100,225,170));
	                 ((XSSFCellStyle) styleTerm).setFillForegroundColor(color0561);
	               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	                 }
	                 else {
	                      XSSFColor color201 = new XSSFColor(new java.awt.Color(195,200,250));
	                       ((XSSFCellStyle) styleTerm).setFillForegroundColor(color201);
	                     styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	                 }
	                styleTerm.setVerticalAlignment(VerticalAlignment.CENTER);
	                sheet.addMergedRegion(new CellRangeAddress(2,2,4+sizeFirst-sizlatest,4+sizeFirst-1));
	     XSSFCell fifthCell = first.createCell(4+sizeFirst-sizlatest);
	     fifthCell.setCellValue(termList.get(i).getTermName());
	     fifthCell.setCellStyle(styleTerm);
	     }
	   
	    	
	     
	     
	     sheet.addMergedRegion(new CellRangeAddress(2,4,sizeFirst+4,sizeFirst+4));
	     XSSFCell sixCell = first.createCell(sizeFirst+4);
	     sixCell.setCellValue("Final Marks");
	     sixCell.setCellStyle(styleF5);
	   
	     sheet.addMergedRegion(new CellRangeAddress(2,5,sizeFirst+5,sizeFirst+5));
	     XSSFCell sevenCell = first.createCell(sizeFirst+5);
	     sevenCell.setCellValue("Percentage");
	     sevenCell.setCellStyle(styleF5);
	    
	     
	     
	     
	     
	     //For Exam Name Printing
	   if(subjectType.equalsIgnoreCase("scholastic")||subjectType.equalsIgnoreCase("additional"))
	   {	 
	     int sizexam=0;
	     XSSFRow third = sheet.createRow(3);
	     for(int k=0;k<termList.size()-2;k++)
	     {
	    	 //fetching Exam Dummy List whwre exam count == 0
	    	 ArrayList<ExamInfo> examListDummy = new ArrayList<>();
		     for(ExamInfo ppp : termList.get(k).getExamInfoList())
		     {
		    	 if(ppp.getCount()==0)
			     {
		    		examListDummy.add(ppp);
			     } 
		     }
	 
	     for(int i=0;i<examListDummy.size();i++)
	     {
 
	    	 
	       CellStyle styleExam = book.createCellStyle();
	       if(i%2==0)
	       {
	        XSSFColor color0562 = new XSSFColor(new java.awt.Color(120,225,020));
	        ((XSSFCellStyle) styleExam).setFillForegroundColor(color0562);
	        styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	       }
	       else
	       {
	         XSSFColor color056 = new XSSFColor(new java.awt.Color(255,020,250));
	         ((XSSFCellStyle) styleExam).setFillForegroundColor(color056);
	         styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	       }
	       
	     styleExam.setVerticalAlignment(VerticalAlignment.CENTER);
	     sheet.addMergedRegion(new CellRangeAddress(3,3,4+sizexam+(termList.get(k).getSubjectList().size()*i),termList.get(k).getSubjectList().size()+3+sizexam+(termList.get(k).getSubjectList().size()*i)));
	     XSSFCell f1cell = third.createCell(4+sizexam+(termList.get(k).getSubjectList().size()*i));
	     

	     f1cell.setCellValue(examListDummy.get(i).getExamName());
	     f1cell.setCellStyle(styleExam);
		     
	     }
	     sizexam += examListDummy.size()*termList.get(k).getSubjectList().size();
	     }
	    }
	     
	     
	     
	     
	     //For Subject Name Printing
	     int siz=0;
	     XSSFRow four = sheet.createRow(5);
	     for(int k=0;k<termList.size()-2;k++)
	     {
	     for(int i=0;i<termList.get(k).getExamListSize();i++)
	     {
	     for(int j=0;j<termList.get(k).getSubjectList().size();j++)
	     {
	     XSSFCell f2cell = four.createCell(4+siz+i*termList.get(k).getSubjectList().size()+j);
	           
	     f2cell.setCellValue(termList.get(k).getSubjectList().get(j).getSubjectName());
	     f2cell.setCellStyle(style);
	     }
	        }  
	    siz+= termList.get(k).getExamListSize()*termList.get(k).getSubjectList().size();
	     }  
	   
	   

	     
	  //For Styling Data  
	  XSSFCellStyle intStyle = book.createCellStyle();
	  intStyle.setDataFormat((short)1);

	  XSSFCellStyle decStyle = book.createCellStyle();
	  decStyle.setDataFormat((short)2);

	  XSSFCellStyle dollarStyle = book.createCellStyle();
	  dollarStyle.setDataFormat((short)5);
	 


	
	}
	else
	{
		 XSSFWorkbook book = (XSSFWorkbook)doc;
		   XSSFSheet sheet = book.getSheetAt(0);

		   sheet.createFreezePane(0,5);
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
		     
		      
		     
		   
		      int sizeFirst = 0;
		   
		     
		   
		   
		      XSSFRow first = sheet.createRow(2);
		      CellStyle styleF5 = book.createCellStyle();
		    //  styleF5.setVerticalAlignment(XSSFCellStyle.TOP);
//		      styleF5.setVerticalAlignment((short)0.5);
		     
		 
		     
		     sheet.addMergedRegion(new CellRangeAddress(2,5,0,0));  
		     XSSFCell firstCell = first.createCell(0);
		     firstCell.setCellValue("S.No.");
		     firstCell.setCellStyle(styleF5);
		   
		     
		     
	    	 sheet.addMergedRegion(new CellRangeAddress(2,5,1,1));  
		     XSSFCell secondCell = first.createCell(1);	   
		     secondCell.setCellValue("Add.No.");
		     secondCell.setCellStyle(styleF5);
		     
		     
		     
		     sheet.addMergedRegion(new CellRangeAddress(2,5,2,2));  
		     XSSFCell thirdCell = first.createCell(2);
		     thirdCell.setCellValue("Student Name");
		     thirdCell.setCellStyle(styleF5);
		   
		     
		     
		     sheet.addMergedRegion(new CellRangeAddress(2,5,3,3));  
		     XSSFCell forthCell = first.createCell(3);
		     forthCell.setCellValue("Father Name");
		     forthCell.setCellStyle(styleF5);
		   
		     for(int i=0;i<termList.size();i++) {
		     sizeFirst +=termList.get(i).getExamListSize()*termList.get(i).getSubjectList().size();
		                int sizlatest = termList.get(i).getExamListSize()*termList.get(i).getSubjectList().size();
		              
		                
		                CellStyle styleTerm = book.createCellStyle();
		                if(i%2==0) {
		               XSSFColor color0561 = new XSSFColor(new java.awt.Color(100,225,170));
		                 ((XSSFCellStyle) styleTerm).setFillForegroundColor(color0561);
		               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		                 }
		                 else {
		                      XSSFColor color201 = new XSSFColor(new java.awt.Color(195,200,250));
		                       ((XSSFCellStyle) styleTerm).setFillForegroundColor(color201);
		                     styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		                 }
		                styleTerm.setAlignment(HorizontalAlignment.CENTER);
		                sheet.addMergedRegion(new CellRangeAddress(2,2,4+sizeFirst-sizlatest,4+sizeFirst-1));
		     XSSFCell fifthCell = first.createCell(4+sizeFirst-sizlatest);
		     fifthCell.setCellValue(termList.get(i).getTermName());
		     fifthCell.setCellStyle(styleTerm);
		     }
		   
		   
		   
		     
		     //For Exam Name Printing
		    if(subjectType.equalsIgnoreCase("additional"))
		    {	 
		     int sizexam=0;
		     XSSFRow third = sheet.createRow(3);
		     for(int k=0;k<termList.size();k++)
		     {
		    	 
		    	 ArrayList<ExamInfo> examListDummy = new ArrayList<>();
			     for(ExamInfo ppp : termList.get(k).getExamInfoList())
			     {
			    	 if(ppp.getCount()==0)
				     {
			    		examListDummy.add(ppp);
				     } 
			     }	 
		    	 
		     for(int i=0;i<examListDummy.size();i++)
		     {	
		    

		      	 
		     CellStyle styleExam = book.createCellStyle();
		     if(i%2==0)
		     {
		        XSSFColor color0562 = new XSSFColor(new java.awt.Color(120,225,020));
		        ((XSSFCellStyle) styleExam).setFillForegroundColor(color0562);
		        styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		     }
		     else
		     {
		           XSSFColor color056 = new XSSFColor(new java.awt.Color(255,020,250));
		           ((XSSFCellStyle) styleExam).setFillForegroundColor(color056);
		           styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		     }
		     
		     styleExam.setAlignment(HorizontalAlignment.CENTER);
		     sheet.addMergedRegion(new CellRangeAddress(3,3,4+sizexam+(termList.get(k).getSubjectList().size()*i),termList.get(k).getSubjectList().size()+3+sizexam+(termList.get(k).getSubjectList().size()*i)));
		     XSSFCell f1cell = third.createCell(4+sizexam+(termList.get(k).getSubjectList().size()*i));
		     
		     
		     f1cell.setCellValue(examListDummy.get(i).getExamName());
		     f1cell.setCellStyle(styleExam);
		     
		    
		     }
		     sizexam += examListDummy.size()*termList.get(k).getSubjectList().size();
		     }
		    }
		     
		     
		     
		     
		     //For Subject Name Printing
		     int siz=0;
		     XSSFRow four = sheet.createRow(5);
		     for(int k=0;k<termList.size();k++)
		     {
		      for(int i=0;i<termList.get(k).getExamListSize();i++)
		      {
		       for(int j=0;j<termList.get(k).getSubjectList().size();j++)
		       {
		         XSSFCell f2cell = four.createCell(4+siz+i*termList.get(k).getSubjectList().size()+j);
		           
		         f2cell.setCellValue(termList.get(k).getSubjectList().get(j).getSubjectName());
		         f2cell.setCellStyle(style);
		       }
		      }  
		       siz+= termList.get(k).getExamListSize()*termList.get(k).getSubjectList().size();
		    }  
		   
		   
	

		     
		  //For Styling Data  
		  XSSFCellStyle intStyle = book.createCellStyle();
		  intStyle.setDataFormat((short)1);

		  XSSFCellStyle decStyle = book.createCellStyle();
		  decStyle.setDataFormat((short)2);

		  XSSFCellStyle dollarStyle = book.createCellStyle();
		  dollarStyle.setDataFormat((short)5);
		 


		

	}
	}
	else
	{
//		if(subjectType.equalsIgnoreCase("scholastic"))
//		{		
		XSSFWorkbook book = (XSSFWorkbook)doc;
	    XSSFSheet sheet = book.getSheetAt(0);

	    sheet.createFreezePane(0,5);
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
	     
	 
	     sheet.addMergedRegion(new CellRangeAddress(2,5,0,0));
	     XSSFCell firstCell = first.createCell(0);
	     firstCell.setCellValue("S.No.");
	   
	     firstCell.setCellStyle(styleF5);
	   
	     sheet.addMergedRegion(new CellRangeAddress(2,5,1,1));
	     XSSFCell secondCell = first.createCell(1);
	   
	     secondCell.setCellValue("Add.No.");
	     secondCell.setCellStyle(styleF5);
	   
	     sheet.addMergedRegion(new CellRangeAddress(2,5,2,2));
	     XSSFCell thirdCell = first.createCell(2);
	     thirdCell.setCellValue("Student Name");
	     thirdCell.setCellStyle(styleF5);
	   
	     sheet.addMergedRegion(new CellRangeAddress(2,5,3,3));
	     XSSFCell forthCell = first.createCell(3);
	     forthCell.setCellValue("Father Name");
	     forthCell.setCellStyle(styleF5);
	   
	     
	     
	     
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
	           
	      styleTerm.setAlignment(HorizontalAlignment.CENTER);
	     
	      int holdSizeTermValue = 0;
	      holdSizeTermValue = sizTermlatest;
	 
	     XSSFCell fifthCell = first.createCell(4+sizTermlatest);
	     fifthCell.setCellValue(termList.get(i).getTermName());
	     fifthCell.setCellStyle(styleTerm);
	     
	     for(int a=0;a<termList.get(i).getExamInfoList().size();a++)
	     {
	     if(termList.get(i).getExamInfoList().get(a).getCount()!=0)
	     {
	    	 
	     }
	     else
	     {	 
	       sizTermlatest += termList.get(i).getSubjectList().size()* termList.get(i).getExamInfoList().get(a).getSubExamListSize();
	     }
	     }
	     
	   
	       sheet.addMergedRegion(new CellRangeAddress(2,2,4+holdSizeTermValue,4+sizTermlatest-1));
	   
	     }  
	     
	     
	     
	     
	     //For Printing Final mark and percntage Label
	     sheet.addMergedRegion(new CellRangeAddress(2,5,sizTermlatest+4,sizTermlatest+4));
	     XSSFCell sixCell = first.createCell(sizTermlatest+4);
	     sixCell.setCellValue("Final Marks");
	     sixCell.setCellStyle(styleF5);
	   
	     sheet.addMergedRegion(new CellRangeAddress(2,5,sizTermlatest+5,sizTermlatest+5));
	     XSSFCell sevenCell = first.createCell(sizTermlatest+5);
	     sevenCell.setCellValue("Percentage");
	     sevenCell.setCellStyle(styleF5);
	     
	     
	     
	     
	     
	     //For Printing Exam Name
	     int sizexamLatest=0;
	     XSSFRow third = sheet.createRow(3);
	     XSSFRow fourthh = sheet.createRow(4);
	     for(int k=0;k<termList.size()-2;k++)
	     {
	    	 ArrayList<ExamInfo> examListDummy = new ArrayList<>();
		     for(ExamInfo ppp : termList.get(k).getExamInfoList())
		     {
		    	 if(ppp.getCount()==0)
			     {
		    		examListDummy.add(ppp);
			     } 
		     }	 
	    	 
	     for(int i=0;i<examListDummy.size();i++)
	     {
	 
	     	 
	     CellStyle styleExam = book.createCellStyle();
	     if(i%2==0)
	     {
	       XSSFColor color0562 = new XSSFColor(new java.awt.Color(120,225,020));
	       ((XSSFCellStyle) styleExam).setFillForegroundColor(color0562);
	       styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	     }
	     else
	     {
	         XSSFColor color056 = new XSSFColor(new java.awt.Color(255,020,250));
	         ((XSSFCellStyle) styleExam).setFillForegroundColor(color056);
	          styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	     }
	     
	     styleExam.setAlignment(HorizontalAlignment.CENTER);
	     
	     int holdSizeExamValue = 0;
	        holdSizeExamValue = sizexamLatest;
	     
	     XSSFCell f1cell = third.createCell(4+sizexamLatest);
	  
	     f1cell.setCellValue(examListDummy.get(i).getExamName());
	     int subExamCellindex = sizexamLatest;
	     
	     
	     //Loop for Printing SubExam Names
	     for(int p=0;p<examListDummy.get(i).getSubExamListSize();p++)
	     {
	     CellStyle styleSubExam = book.createCellStyle();
	     
	     int subSizeeExam = examListDummy.get(i).getSubExamListSize();
	     if(subSizeeExam==1)
	     {
	     XSSFColor color0562 = new XSSFColor(new java.awt.Color(180,175,020));
	     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color0562);
	     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	     }
	     
	     else
	     {
	      if(p==0)
	      {
	     XSSFColor color0562 = new XSSFColor(new java.awt.Color(180,175,020));
	     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color0562);
	     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	              }
	      else if(p==1)
	      {
	     XSSFColor color05624 = new XSSFColor(new java.awt.Color(220,135,120));
	     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color05624);
	     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	      }
	      else
	      {
	                 XSSFColor color056 = new XSSFColor(new java.awt.Color(255,120,200));
	                 ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color056);
	                 styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	      }
	     }
	     styleSubExam.setAlignment(HorizontalAlignment.CENTER);
	     
	     int holdSubExamCellValue =0;
	     holdSubExamCellValue = subExamCellindex;
	     XSSFCell f1cellForthh = fourthh.createCell(4+subExamCellindex);
	     f1cellForthh.setCellValue(examListDummy.get(i).getSubExamList().get(p).getSubExamUpperCase());
	     f1cellForthh.setCellStyle(styleSubExam);
	     
	     subExamCellindex += termList.get(k).getSubjectList().size();
	     
	         sheet.addMergedRegion(new CellRangeAddress(4,4,4+holdSubExamCellValue,4+subExamCellindex-1));

	     
	     }
	     
	     f1cell.setCellStyle(styleExam);
	     
	     sizexamLatest += termList.get(k).getSubjectList().size()*examListDummy.get(i).getSubExamListSize();
	   
	         sheet.addMergedRegion(new CellRangeAddress(3,3,4+holdSizeExamValue,4+sizexamLatest-1));

	     }
	   	
	   
	     }
	   
	     
	     
	     
	     
	     //For Printing Subject List
	     int counterrrr=0;
	     XSSFRow fifthh = sheet.createRow(5);
	     for(int k=0;k<termList.size()-2;k++)
	     {
	       for(int i=0;i<termList.get(k).getExamInfoList().size();i++)
	       {
	         if(termList.get(k).getExamInfoList().get(i).getCount()==0)
	         {	
	           for (int q = 0; q <termList.get(k).getExamInfoList().get(i).getSubExamListSize(); q++)
	           {

       	        for(int j=0;j<termList.get(k).getSubjectList().size();j++)
	            {
	              XSSFCell f2cell = fifthh.createCell(4+counterrrr);
	                 counterrrr ++;
	              f2cell.setCellValue(termList.get(k).getSubjectList().get(j).getSubjectName());
	              f2cell.setCellStyle(style);
	            }
	           }
	         }
	        }  
	     }  
	   
	   
	    
	     
	   

	     
	  //For Data Coloring  
	  XSSFCellStyle intStyle = book.createCellStyle();
	  intStyle.setDataFormat((short)1);

	  XSSFCellStyle decStyle = book.createCellStyle();
	  decStyle.setDataFormat((short)2);

	  XSSFCellStyle dollarStyle = book.createCellStyle();
	  dollarStyle.setDataFormat((short)5);
	 
//		}
//		else
//		{
//			XSSFWorkbook book = (XSSFWorkbook)doc;
//		    XSSFSheet sheet = book.getSheetAt(0);
//
//		    sheet.createFreezePane(0,5);
//		    XSSFRow headerRow = sheet.getRow(0);
//		     
//		    headerRow.setHeight((short)1200);
//		     
//		  CellStyle style = book.createCellStyle();
//		  XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
//		  ((XSSFCellStyle) style).setFillForegroundColor(color);
//		  style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//		  style.setVerticalAlignment(XSSFCellStyle.TOP);
//		  style.setBorderLeft(BorderStyle.THIN);            
//		  style.setBorderRight(BorderStyle.THIN);            
//		  style.setBorderTop(BorderStyle.THIN);              
//		  style.setBorderBottom(BorderStyle.NONE);
//		 
//		  style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
//		  style.setTopBorderColor(IndexedColors.RED.getIndex());
//		  style.setLeftBorderColor(IndexedColors.RED.getIndex());
//		  style.setRightBorderColor(IndexedColors.RED.getIndex());
//		 
//		
//		     
//		           //Second Row Labels Printing
//		      XSSFRow first = sheet.createRow(2);
//		      CellStyle styleF5 = book.createCellStyle();
//		      styleF5.setVerticalAlignment((short)0.5);
//		     
//		 
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,0,0));
//		     XSSFCell firstCell = first.createCell(0);
//		     firstCell.setCellValue("S.No.");
//		   
//		     firstCell.setCellStyle(styleF5);
//		   
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,1,1));
//		     XSSFCell secondCell = first.createCell(1);
//		   
//		     secondCell.setCellValue("Add.No.");
//		     secondCell.setCellStyle(styleF5);
//		   
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,2,2));
//		     XSSFCell thirdCell = first.createCell(2);
//		     thirdCell.setCellValue("Student Name");
//		     thirdCell.setCellStyle(styleF5);
//		   
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,3,3));
//		     XSSFCell forthCell = first.createCell(3);
//		     forthCell.setCellValue("Father Name");
//		     forthCell.setCellStyle(styleF5);
//		   
//		     
//		     
//		     
//		     //For Printing Term names
//		     int sizTermlatest =0;
//		     for(int i=0;i<termList.size();i++)
//		     {
//		   
//		               
//		           CellStyle styleTerm = book.createCellStyle();
//		               
//		           if(i%2==0)
//		           {
//		               XSSFColor color0561 = new XSSFColor(new java.awt.Color(100,225,170));
//		               ((XSSFCellStyle) styleTerm).setFillForegroundColor(color0561);
//		               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		                 
//		           }
//		           else
//		           {
//		               XSSFColor color201 = new XSSFColor(new java.awt.Color(195,200,250));
//		               ((XSSFCellStyle) styleTerm).setFillForegroundColor(color201);
//		               styleTerm.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		               
//		           }
//		           
//		      styleTerm.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//		     
//		      int holdSizeTermValue = 0;
//		      holdSizeTermValue = sizTermlatest;
//		 
//		     XSSFCell fifthCell = first.createCell(4+sizTermlatest);
//		     fifthCell.setCellValue(termList.get(i).getTermName());
//		     fifthCell.setCellStyle(styleTerm);
//		     
//		     for(int a=0;a<termList.get(i).getExamInfoList().size();a++)
//		     {
//		     sizTermlatest += termList.get(i).getSubjectList().size()* termList.get(i).getExamInfoList().get(a).getSubExamListSize();
//		     }
//		     
//		   
//		       sheet.addMergedRegion(new CellRangeAddress(2,2,4+holdSizeTermValue,4+sizTermlatest-1));
//		   
//		     }  
//		     
//		     
//		     //For Printing Final mark and percntage Label
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,sizTermlatest+4,sizTermlatest+4));
//		     XSSFCell sixCell = first.createCell(sizTermlatest+4);
//		     sixCell.setCellValue("Final Marks");
//		     sixCell.setCellStyle(styleF5);
//		   
//		     sheet.addMergedRegion(new CellRangeAddress(2,5,sizTermlatest+5,sizTermlatest+5));
//		     XSSFCell sevenCell = first.createCell(sizTermlatest+5);
//		     sevenCell.setCellValue("Percentage");
//		     sevenCell.setCellStyle(styleF5);
//		     
//		     
//		     
//		     //For Printing Exam Name
//		     int sizexamLatest=0;
//		     XSSFRow third = sheet.createRow(3);
//		     XSSFRow fourthh = sheet.createRow(4);
//		     for(int k=0;k<termList.size();k++)
//		     {
//		     for(int i=0;i<termList.get(k).getExamInfoList().size();i++)
//		     {
//		     CellStyle styleExam = book.createCellStyle();
//		     if(i%2==0)
//		     {
//		     XSSFColor color0562 = new XSSFColor(new java.awt.Color(120,225,020));
//		     ((XSSFCellStyle) styleExam).setFillForegroundColor(color0562);
//		     styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		             }
//		     else
//		     {
//		                 XSSFColor color056 = new XSSFColor(new java.awt.Color(255,020,250));
//		                 ((XSSFCellStyle) styleExam).setFillForegroundColor(color056);
//		                 styleExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		     }
//		     
//		     styleExam.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//		     
//		     int holdSizeExamValue = 0;
//		        holdSizeExamValue = sizexamLatest;
//		     
//		     XSSFCell f1cell = third.createCell(4+sizexamLatest);
//		     
//		   
//		     f1cell.setCellValue(termList.get(k).getExamInfoList().get(i).getExamName());
//		     
//		     int subExamCellindex = sizexamLatest;
//		     
//		     
//		     //Loop for Printing SubExam Names
//		     for(int p=0;p<termList.get(k).getExamInfoList().get(i).getSubExamListSize();p++)
//		     {
//		     CellStyle styleSubExam = book.createCellStyle();
//		     
//		     int subSizeeExam = termList.get(k).getExamInfoList().get(i).getSubExamListSize();
//		     if(subSizeeExam==1)
//		     {
//		     XSSFColor color0562 = new XSSFColor(new java.awt.Color(180,175,020));
//		     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color0562);
//		     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		     }
//		     
//		     else
//		     {
//		      if(p==0)
//		      {
//		     XSSFColor color0562 = new XSSFColor(new java.awt.Color(180,175,020));
//		     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color0562);
//		     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		              }
//		      else if(p==1)
//		      {
//		     XSSFColor color05624 = new XSSFColor(new java.awt.Color(220,135,120));
//		     ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color05624);
//		     styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		      }
//		      else
//		      {
//		                 XSSFColor color056 = new XSSFColor(new java.awt.Color(255,120,200));
//		                 ((XSSFCellStyle) styleSubExam).setFillForegroundColor(color056);
//		                 styleSubExam.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		      }
//		     }
//		     styleSubExam.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//		     
//		     int holdSubExamCellValue =0;
//		     holdSubExamCellValue = subExamCellindex;
//		     XSSFCell f1cellForthh = fourthh.createCell(4+subExamCellindex);
//		     f1cellForthh.setCellValue(termList.get(k).getExamInfoList().get(i).getSubExamList().get(p).getSubExamUpperCase());
//		     f1cellForthh.setCellStyle(styleSubExam);
//		     
//		     subExamCellindex += termList.get(k).getSubjectList().size();
//		     
//		         sheet.addMergedRegion(new CellRangeAddress(4,4,4+holdSubExamCellValue,4+subExamCellindex-1));
//
//		     
//		     }
//		     
//		     f1cell.setCellStyle(styleExam);
//		     
//		     sizexamLatest += termList.get(k).getSubjectList().size()*termList.get(k).getExamInfoList().get(i).getSubExamListSize();
//		   
//		         sheet.addMergedRegion(new CellRangeAddress(3,3,4+holdSizeExamValue,4+sizexamLatest-1));
//
//		     }
//		   
//		     }
//		   
//		     
//		     
//		     
//		     
//		     //For Printing Subject List
//		     int counterrrr=0;
//		     XSSFRow fifthh = sheet.createRow(5);
//		     for(int k=0;k<termList.size();k++)
//		     {
//		       for(int i=0;i<termList.get(k).getExamInfoList().size();i++)
//		       {
//		    	 if(termList.get(k).getExamInfoList().get(i).getCount()==0)
//			     {  
//		         for (int q = 0; q <termList.get(k).getExamInfoList().get(i).getSubExamListSize(); q++)
//		         {
//        	        for(int j=0;j<termList.get(k).getSubjectList().size();j++)
//		           {
//		              XSSFCell f2cell = fifthh.createCell(4+counterrrr);
//		                 counterrrr ++;
//		             f2cell.setCellValue(termList.get(k).getSubjectList().get(j).getSubjectName());
//		             f2cell.setCellStyle(style);
//		           }
//		         }
//			     }
//		        }  
//		     }  
//		   
//		   
//		     
//		   
//
//		     
//		  //For Data Coloring  
//		  XSSFCellStyle intStyle = book.createCellStyle();
//		  intStyle.setDataFormat((short)1);
//
//		  XSSFCellStyle decStyle = book.createCellStyle();
//		  decStyle.setDataFormat((short)2);
//
//		  XSSFCellStyle dollarStyle = book.createCellStyle();
//		  dollarStyle.setDataFormat((short)5);
//		}


	 
	}
	
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

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
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

	public ArrayList<SelectItem> getTermNewList() {
		return termNewList;
	}

	public void setTermNewList(ArrayList<SelectItem> termNewList) {
		this.termNewList = termNewList;
	}

	public boolean isShowSubExamRow() {
		return showSubExamRow;
	}

	public void setShowSubExamRow(boolean showSubExamRow) {
		this.showSubExamRow = showSubExamRow;
	}

	public boolean isShowExam() {
		return showExam;
	}

	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public ArrayList<SelectItem> getAllExamList() {
		return allExamList;
	}

	public void setAllExamList(ArrayList<SelectItem> allExamList) {
		this.allExamList = allExamList;
	}

	public ArrayList<String> getSelectdExam() {
		return selectdExam;
	}

	public void setSelectdExam(ArrayList<String> selectdExam) {
		this.selectdExam = selectdExam;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public int getTermSize() {
		return termSize;
	}

	public void setTermSize(int termSize) {
		this.termSize = termSize;
	}

	public ExamSettingInfo getExamSetting() {
		return examSetting;
	}

	public void setExamSetting(ExamSettingInfo examSetting) {
		this.examSetting = examSetting;
	}
	
	

}
