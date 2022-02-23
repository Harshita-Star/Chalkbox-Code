package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Json.DataBaseMeathodJson;
import exam_module.DataBaseMethodsExam;
import exam_module.DataBaseMethodsExamReport;

@ManagedBean(name="onlineExamReport")
@ViewScoped

public class OnlineExamReportBean implements Serializable 
{
	ArrayList<SelectItem> classList, sectionList, subjectList, examList, termList, examTypeList, subExamList;
	ArrayList<StudentInfo> studentList, selectedList;
	
	String selectedClass, selectedSection, selectedSubject, selectedExam, schoolid, username,
	userType, session, selectedTerm, selectedType, selectedCopyExam, subExam, subExamTemp, marksFormat,
	maxMarks, message;
	
	double marks;
	transient StreamedContent file;
	
	boolean showExam, showSubExam;
	boolean b;
	SchoolInfoList schoolDetails;
	String sectionName,className,total,subjectName,examName;
	
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsExamReport objExam=new DataBaseMethodsExamReport();
	DataBaseMeathodJson objJson=new DataBaseMeathodJson();
	
	public OnlineExamReportBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		session=DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		userType=(String) ss.getAttribute("type");
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal"))
		{
			classList=obj.allClass(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			classList = obj.cordinatorClassList(username, schoolid, conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schoolid,conn);
		}
		b = false;
		schoolDetails = new DatabaseMethods1().fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSubject()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMeathodJson objJson=new DataBaseMeathodJson();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			subjectList=new DatabaseMethods1().allSubjectClassWise(selectedClass,conn);
		}
		else
		{
			String empid=objJson.employeeIdbyEmployeeName(username,schoolid,conn);
			subjectList=objJson.AllEMployeeSubject(empid,selectedSection,schoolid,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allExams()
	{
		Connection examconn=DataBaseConnectionExam.javaConnection();
		examList = objExam.onlineExamList(selectedClass, selectedSubject, schoolid, examconn);

		try {
			examconn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		Connection examconn = DataBaseConnectionExam.javaConnection();
		String subjectType=obj.checkSubjectType(selectedSubject,conn);
		if(subjectType.equalsIgnoreCase("Mandatory"))
		{
			studentList=new DataBaseMethodsExam().studentBasicDetailsForMarksheet(schoolid,"", conn,"byClass",selectedSection);
		}
		else
		{
			studentList=obj.searchStudentListBySubject(selectedClass,selectedSection,selectedSubject,conn);
		}
		
		DataBaseMethodsExamReport.allOnlineExamResults(selectedClass, selectedExam, studentList, selectedSubject, examconn);
		
		if(studentList.size()>0 )
		{
			
			SchoolInfoList info = obj.fullSchoolInfo(conn);
			if(info.getBranch_id().equals("54"))
				Collections.sort(studentList, new MySalaryComp());
			else 
			{
				if(!info.getBranch_id().equals("52"))
					Collections.sort(studentList, new MyRollNoComp());
			}
			
			className = obj.classNameFromidSchid(schoolid, selectedClass, session, conn);
			sectionName = obj.sectionNameByIdSchid(schoolid, selectedSection, conn);
			total = Integer.toString(studentList.size());
			subjectName = obj.subjectNameFromid(selectedSubject, conn);
			for(SelectItem x : examList) {
				if (selectedExam.equals(x.getValue())) {
					examName = x.getLabel().toString();
				}
			}
			b = true;
			
			
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void copyDetails()
	{
		selectedTerm = selectedType = selectedCopyExam = subExam = subExamTemp = "";
		showExam = false;
		showSubExam = false;
		
		if(selectedList.size()>0)
		{
			Connection conn=DataBaseConnection.javaConnection();
			boolean checkExamSetting = obj.checkExamSettingMade(selectedClass,session,schoolid,conn);
			if(checkExamSetting)
			{
				termList=obj.selectedTermOfClass(selectedClass,conn,session,schoolid);
				selectedType = objExam.checkMainSubjectType(selectedSubject, schoolid, conn);
				
				PrimeFaces.current().executeInitScript("PF('copyDialog').show()");
				PrimeFaces.current().ajax().update("copyForm");
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add Exam Setting for selected class to proceed further")); 
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one student to proceed further")); 
		}
		
		
	}

	public void method()
	{
		showExam=showSubExam=false;
		selectedCopyExam=null;
		allExamType();
	}
	
	public void allExamType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		examTypeList=new DataBaseMethodsExam().mainExamListOfClassSection(selectedClass,selectedType,selectedTerm,conn);
		
		if(selectedType.equals("scholastic") || selectedType.equals("additional") )
		{
			showExam=true;
		}
		else
		{
			showSubExam=showExam=false;
			
			try {
				
				if(examTypeList.size()>0)
				{
					selectedCopyExam=examTypeList.get(0).getValue().toString();	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
					
			
			createSubExamList();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createSubExamList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subExamList=new DataBaseMethodsExam().subExamListOfClassSection(selectedClass,selectedType,selectedCopyExam,selectedTerm,session,conn);
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			if(value.contains("sub"))
			{
				showSubExam=true;
			}
			else
			{
				showSubExam=false;
				subExamValue();
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void subExamValue()
	{
		if(subExamList.size()>0)
		{
			String value=(String) subExamList.get(0).getValue();
			if(value.contains("sub"))
			{
				subExamTemp=subExam.substring(0,subExam.lastIndexOf("/"));
			}
			else
			{
				subExamTemp=value.substring(0,value.lastIndexOf("/"));
			}
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();

		String examAdded=new DataBaseMethodsExam().checkSubjectAddedInExamOrNot(selectedClass, selectedType, selectedCopyExam, selectedTerm, schoolid, selectedSubject, session, conn);
		if(examAdded.equals("notadded"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dear User,Subject not added in exam. Please add this subject in selected exam in EDIT EXAM option"));
		}
		else
		{
			marksFormat=new DataBaseMethodsExam().examSettingDetail(selectedClass, conn).getMarks_grade();
			String status="Unlock";
			if(!userType.equalsIgnoreCase("admin"))
			{
				status=obj.checkExamLockStatus(selectedClass, selectedTerm, selectedCopyExam, selectedType, conn);
			}
			if(status.equalsIgnoreCase("Unlock"))
			{
				subExamValue();
				marks=new DataBaseMethodsExam().subjectMaxMarksFromId(selectedClass,selectedCopyExam,subExamTemp,selectedSubject,selectedType,session,selectedTerm,conn);
				maxMarks=String.valueOf(marks);
				saveData();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Exam Has Been locked By Admin. Please Contact Admin For Editing in Marks!"));
			}
		}
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String saveData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		
	
			for(StudentInfo list: selectedList)
			{
				if(selectedType.equalsIgnoreCase("scholastic") || selectedType.equals("additional"))
				{
					if(marksFormat.equals("marks"))
					{
						if(list.getMarksObtained() != null && !list.getMarksObtained().equalsIgnoreCase("Ab") && !list.getMarksObtained().equalsIgnoreCase("ML") && !list.getMarksObtained().equals(""))
						{
							try 
							{
								if(Double.parseDouble(list.getMarksObtained())>marks)
								{
	                                message="Dear User, \n Marks obtained can't be greater than total marks.";
									flag=1;
									break;
								}else {
									list.setMarksType("");
									list.setMarks(list.getMarksObtained());
								}	
							} 
							catch (Exception e) 
							{
							     message="Dear User, \n Please Enter only Numeric Value or 'Ab' or 'ML'.";
							}
						}else {
							if(list.getMarksObtained() == null || list.getMarksObtained().equals("")) {
								list.setMarks("");
							}else {
								list.setMarksType(list.getMarksObtained());
							}
						}
					}
				}
			}
			if(flag==0)
			{
				boolean ls=new DataBaseMethodsExam().addStudentPerformance(selectedSection,selectedCopyExam,subExamTemp, selectedList, /*subjectList,*/selectedType,selectedSubject,maxMarks,selectedTerm,conn,session);
				if(ls==true)
				{
					String refNo=addWorkLog(conn);
					if(refNo.equals(""))
						message="Dear User, \n Some Error Occur Please Try Again";
					else
						message="Dear User, \n Performance record updated successfully. Please Note Down Your Reference No : "+refNo+" For Any Further Query.";
						
				}
				else
				{
					message="Dear User, \n Some Error Occur Please Try Again"	;
				}
				
				
			}
	
			openAndCloseDia();
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void openAndCloseDia() {
		PrimeFaces.current().executeInitScript("PF('copyDialog').hide()");
		PrimeFaces.current().ajax().update("copyForm");
		PrimeFaces.current().executeInitScript("PF('messageDialog').show()");
		PrimeFaces.current().ajax().update("messageDia");
	}
	
	public String addWorkLog(Connection conn)
	{
		String className=obj.classname(selectedClass, schoolid, conn);
		String sectionname =obj.sectionNameByIdSchid(schoolid,selectedSection, conn);
		String termName=obj.semesterNameFromid(selectedTerm, conn);
		String typeOfArea =selectedType;
		String subjectName=obj.subjectNameFromid(selectedSubject, conn);
		String examName=obj.examNameFromid(selectedCopyExam, conn);
		String subExamName=subExamTemp;
		String language=""+className+"-"+sectionname+"\n,"+""+termName+"\n,"+typeOfArea ;
		String description = "Class-"+selectedClass+",Section-"+selectedSection+",Term-"+selectedTerm+","+typeOfArea;
		
		if(!examName.equals(""))
		{
			language=language+"\n,"+examName;
			description += ",Exam-"+selectedCopyExam;
		}
		
		if(!subExamName.equals(""))
		{
		   if(!examName.equals(subExamName))
		   {
			   language=language+"\n,"+subExamName;
			   description += ",SubExam-"+subExamTemp;
		   }
		}
		
		language=language+"\n,"+subjectName;
		description += ",Subject-"+selectedSubject;
		String addedBy=obj.employeeNameByuserName(username, conn);
		language=language+"\n,"+addedBy+"("+username+"-"+userType+")" +"\n"+" Marks Submitted Successfully";
		
		String value="";
		for(StudentInfo lss:studentList)
		{
			if(value.equals(""))
			{
				if(lss.getMarks().equals(""))
				{
					value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
				}
				else
				{
					value="("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
				}
			}
			else
			{
				if(lss.getMarks().equals(""))
				{
					value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:0/"+maxMarks+")";
				}
				else
				{
					value=value+"("+lss.getAddNumber()+"----"+lss.getFullName()+"--Marks---:"+lss.getMarks()+"/"+maxMarks+")";
				}
			}
		}
		String refNo =obj.saveWorkLog(schoolid,language, username, "MARKS FEEDING",userType, session, "WEB",value,description, conn);
		return refNo;
	}
	
	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}


	class MyRollNoComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			//	String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			//String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			if(e1.getRollNo()==null||e2.getRollNo()==null||e2.getRollNo().equals("")||e1.getRollNo().equals(""))
			{
				return 0;
			}
			else
			{
				if(e2.getRollNo().matches("-?\\d+(\\.\\d+)?")||e1.getRollNo().matches("-?\\d+(\\.\\d+)?"))
				{
					try
					{
						int sr1 = Integer.parseInt(e1.getRollNo());
						int sr2 = Integer.parseInt(e2.getRollNo());

						if(sr1 >= sr2)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					catch (Exception e)
					{
						return 0;
					}


				}
				else
				{
					return 0;


				}

			}




		}
	}
	
	public void exportClasWisePdf() throws DocumentException, IOException, FileNotFoundException {
		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");

		PdfWriter.getInstance(document, baos);
		document.open();

		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Header
		try {

			String src = ls.getDownloadpath() + ls.getImagePath();

			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c = new Chunk(schoolDetails.schoolName + "\n", fo);

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}

			Chunk c1 = new Chunk(
					"              " + schoolDetails.add1 + " " + schoolDetails.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			Chunk c8 = new Chunk(
					"\n                                                                 Online Exam Marks Report\n\n",
					fo);
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : " + className + "                                          Section : " + sectionName     
					+ "                                         Total : " + total +"\nSubject Name : "+subjectName+"            Exam Name : "+examName+"\n\n", fo);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			// p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(
					new float[] { 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Roll No.");
			table.addCell("Add. Number");
			table.addCell("Student Name");
			table.addCell("Max. Marks");
			table.addCell("Obtained Marks");
			// table.addCell("Message Sent");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < studentList.size(); i++) {
				table.addCell(new Phrase(String.valueOf(i+1), font));
				table.addCell(new Phrase(studentList.get(i).getRollNo(), font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(), font));
				table.addCell(new Phrase(studentList.get(i).getFullName(), font));
				table.addCell(new Phrase(studentList.get(i).getMaxMarks(), font));
				table.addCell(new Phrase(studentList.get(i).getMarksObtained(), font));
				
			}

			table.setWidthPercentage(110);
			document.add(table);

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Class_Wise_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name("Online Exam Marks.pdf").stream(() -> isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
	}
	
	public void toNumWithoutPhoto(Object doc) {
		XSSFWorkbook book = (XSSFWorkbook) doc;
		XSSFSheet sheet = book.getSheetAt(0);

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		sheet.createFreezePane(2, 3);

		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow = sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow + 1, 1);

		XSSFRow r0 = sheet.createRow(0);
		XSSFRow r1 = sheet.createRow(1);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244, 237, 232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		// styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		
		CellStyle styleb1 = book.createCellStyle();
		XSSFColor color11 = new XSSFColor(new java.awt.Color(244, 237, 232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		// styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		
		
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());

		XSSFCell clee = r1.createCell(0);

		clee.setCellValue("Class : " + className);

		clee.setCellStyle(styleb1);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue("Section : " + sectionName);
		clee3.setCellStyle(styleb1);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total : " + total);
		clee4.setCellStyle(styleb1);
		
		XSSFCell clee5 = r1.createCell(3);
		clee5.setCellValue("Subject Name : " + subjectName);
		clee5.setCellStyle(styleb1);
		
		XSSFCell clee6 = r1.createCell(4);
		clee6.setCellValue("Exam Name : " + examName);
		clee6.setCellStyle(styleb1);

		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		for (int o = 3; o < colCount; o++) {
//			XSSFCell clee12 = r1.createCell(o);
//			clee12.setCellValue("");
//			clee12.setCellStyle(styleb);
//		}
//        
//    

		XSSFRow he = sheet.getRow(0);
		XSSFCell cel = he.createCell(4);
		cel.setCellValue("Online Exam Marks Copy Report");
		//cel.setCellStyle(styleb);
		
		he.setHeight((short) 400);

		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short) 1);
		XSSFRow headerRow = sheet.getRow(2);
		headerRow.setHeight((short) 400);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243, 236, 221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// Font font = book.createFont();
		// font.setColor(IndexedColors.BLACK.getIndex());

		// style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		XSSFCell cle1 = headerRow.createCell(colCount);

		for (int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
//            //// // System.out.println(rowCount);
//            //// // System.out.println(colCount);
			for (int cellInd = 0; cellInd < colCount; cellInd++) {

				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if (rowInd % 2 == 0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254, 254, 251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				} else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241, 235, 234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				st2.setVerticalAlignment(VerticalAlignment.CENTER);
				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

	}


	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<SelectItem> getExamList() {
		return examList;
	}

	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
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

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public String getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(String selectedExam) {
		this.selectedExam = selectedExam;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<StudentInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<StudentInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public ArrayList<SelectItem> getExamTypeList() {
		return examTypeList;
	}

	public void setExamTypeList(ArrayList<SelectItem> examTypeList) {
		this.examTypeList = examTypeList;
	}

	public ArrayList<SelectItem> getSubExamList() {
		return subExamList;
	}

	public void setSubExamList(ArrayList<SelectItem> subExamList) {
		this.subExamList = subExamList;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public String getSelectedCopyExam() {
		return selectedCopyExam;
	}

	public void setSelectedCopyExam(String selectedCopyExam) {
		this.selectedCopyExam = selectedCopyExam;
	}

	public String getSubExam() {
		return subExam;
	}

	public void setSubExam(String subExam) {
		this.subExam = subExam;
	}

	public String getSubExamTemp() {
		return subExamTemp;
	}

	public void setSubExamTemp(String subExamTemp) {
		this.subExamTemp = subExamTemp;
	}

	public String getMarksFormat() {
		return marksFormat;
	}

	public void setMarksFormat(String marksFormat) {
		this.marksFormat = marksFormat;
	}

	public String getMaxMarks() {
		return maxMarks;
	}

	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



	public double getMarks() {
		return marks;
	}

	public void setMarks(double marks) {
		this.marks = marks;
	}

	public boolean isShowExam() {
		return showExam;
	}

	public void setShowExam(boolean showExam) {
		this.showExam = showExam;
	}

	public boolean isShowSubExam() {
		return showSubExam;
	}

	public void setShowSubExam(boolean showSubExam) {
		this.showSubExam = showSubExam;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}
	
}
