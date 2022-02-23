package lecture_plan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="viewInitLecPlan")
@ViewScoped

public class ViewInitialLecturePlanBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String username,subject,schid,type;
	String filePath,session,selectedClass,selectedSection,empid,lessonNo,lessonName,description;
	ArrayList<SelectItem> subjectList,classList,sectionList,lessonNoList,lessonNameList;
	ArrayList<LecturePlanInfo> planList;
	LecturePlanInfo selLecture;
	static UploadedFile excelFile;
	boolean show=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson objJSON=new DataBaseMeathodJson();
	exam_module.DataBaseMethodsExam objExam=new exam_module.DataBaseMethodsExam();
	DBMethodsLecturePlan objLecture=new DBMethodsLecturePlan();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public ViewInitialLecturePlanBean() 
	{
		Connection conn= DataBaseConnection.javaConnection();
		subjectList=new ArrayList<>();
		
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		
		session=obj.selectedSessionDetails(schid,conn);
		
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office")
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
			if(type.equalsIgnoreCase("admin"))
			{
				empid=username;
			}
			else
			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			}
		}
		else if(type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassDeatilsForTeacher(empid,schid,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allSection()
	{
		Connection conn= DataBaseConnection.javaConnection();
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office") 
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			sectionList=obj.allSection(selectedClass,conn);
		}
		else
		{
			sectionList=obj.allSectionForTeacher(selectedClass, empid,conn);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allSubjects()
	{
		Connection conn= DataBaseConnection.javaConnection();
		
		if(type.equalsIgnoreCase("admin")
				|| type.equalsIgnoreCase("authority")
				|| type.equalsIgnoreCase("principal")
				|| type.equalsIgnoreCase("vice principal")
				|| type.equalsIgnoreCase("front office")
				|| type.equalsIgnoreCase("office staff") 
				|| type.equalsIgnoreCase("Accounts")
				|| type.equalsIgnoreCase("academic coordinator") 
				|| type.equalsIgnoreCase("Administrative Officer"))
		{
			subjectList=obj.selectedSubjectTypeofClassSection(selectedClass,"NA",conn);
		}
		else
		{
			subjectList=objExam.AllEMployeeSubjectByType(empid,selectedSection,schid,"NA",conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void searchData()
	{
		Connection con= DataBaseConnection.javaConnection();
		show=false;
		planList=objLecture.lecturePlanDetailOfSubject(empid, selectedClass, selectedSection, subject, session, schid, con);
		if(planList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Upload Lesson Plan For This Subject First"));
			show=false;
		}
		else
		{
			show=true;
			
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editDetails()
	{
		Connection con=DataBaseConnection.javaConnection();
		
		lessonName=selLecture.getUnitName();
		lessonNo=selLecture.getUnitNo();
		description=selLecture.getDescription();
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteLecturePlan()
	{
		Connection con=DataBaseConnection.javaConnection();
		ArrayList<LecturePlanInfo> list=objLecture.dailyLectureDetailOfSubject(null, null, empid, selectedClass, selectedSection, subject, session, schid, con);
		if(list.size()>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Can Not Delete This Lesson Plan Because It Is Already In Use. Please Remove All Entries From Daily Lesson Plan Of Related Subejct And Class"));
		}
		else
		{
			int i=objLecture.deleteLecturePlan(empid, selectedClass, selectedSection, subject, session, schid, con);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(con);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deleted Sucessfully"));
				selectedClass=selectedSection=subject="";show=false;
				subjectList=sectionList=new ArrayList<SelectItem>();
				planList=new ArrayList<LecturePlanInfo>();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again."));
			}
		}
		
		selectedClass=selectedSection=subject="";
		sectionList=subjectList=null;
		show=false;
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		value = " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject+" --EmpId-"+empid;
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Initial Lecture Plan","WEB",value,conn);
		return refNo;
	}
	
	
	
	
	public void importFromExcel() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=obj.fullSchoolInfo(schid, conn);
		if (excelFile != null && excelFile.getSize() > 0) 
		{
			filePath = excelFile.getFileName();
			obj.makeProfileSchid(schid, excelFile, filePath, conn);
			/*ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String realPath = ctx.getRealPath("/");
			String savePath = realPath;*/
			
			filePath=info.getUploadpath()+filePath;  //online
			//filePath=savePath+filePath;  //online
		}
		
		if(filePath!=null || !filePath.equals(""))
		{
			String filename = filePath;
			List sheetData = new ArrayList();
			
			FileInputStream fis = null;
			
			//2007
			if (filename.endsWith("xlsx"))
			{
			//	try 
			//	{
			//		fis = new FileInputStream(filename);
					
			//		XSSFWorkbook workbook = new XSSFWorkbook(fis);
			//		XSSFSheet sheet = workbook.getSheetAt(0);
			//		Iterator rows = sheet.rowIterator();
			//		while (rows.hasNext()) 
			//		{
			//			XSSFRow row = (XSSFRow) rows.next();
			//			Iterator cells = row.cellIterator();

			//			List data = new ArrayList();
			//			while (cells.hasNext()) 
			//			{
			//				XSSFCell cell = (XSSFCell) cells.next();
			//				data.add(cell);
			//			}
						
			//			sheetData.add(data);
			//		}
			//	} 
			//	catch (IOException e) {
			//		e.printStackTrace();
			//	} 
			//	finally {
			//		if (fis != null) {
			//			fis.close();
			//		}
			//	}
				
				
				
				//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dear User You can upload excel file with extension .xls (97-2003)."));
			}
			
			
			//2003
			else
			{
				try 
				{
					fis = new FileInputStream(filename);

					HSSFWorkbook workbook = new HSSFWorkbook(fis);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					while (rows.hasNext()) 
					{
						HSSFRow row = (HSSFRow) rows.next();
						Iterator cells = row.cellIterator();

						List data = new ArrayList();
						while (cells.hasNext()) 
						{
							HSSFCell cell = (HSSFCell) cells.next();
							data.add(cell);
						}
						
						sheetData.add(data);
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
				finally {
					if (fis != null) {
						fis.close();
					}
				}
			}
			

			showExelData(sheetData);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void showExelData(List sheetData) 
	{
		Connection conn=DataBaseConnection.javaConnection();
		//ExcelRead read=new ExcelRead();
		
		ArrayList<LecturePlanInfo> lecturePlan=new ArrayList<>();
		int k=objLecture.totalLectureOfLecturePlan(selectedClass, selectedSection, subject, schid, session,empid,conn)+1;
		for (int i = 1; i < sheetData.size(); i++) 
		{
			List list = (List) sheetData.get(i);
			LecturePlanInfo info=new LecturePlanInfo();
			String temp="";
			for (int j = 0; j <= 2; j++) {
				Cell cell = (Cell) list.get(j);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) 
				{
					DataFormatter fmt = new DataFormatter();
					String valueAsSeenInExcel = fmt.formatCellValue(cell);
					temp=valueAsSeenInExcel;
				
				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					
					temp=String.valueOf(cell.getRichStringCellValue());

				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					
					temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
				}
					
				if(j==0)
				{
					info.setUnitNo(temp);
				}
				if(j==1)
				{
					info.setUnitName(temp);
				}
				if(j==2)
				{
					info.setDescription(temp);
				}
			}
			info.setLectureNo(String.valueOf(k++));
			lecturePlan.add(info);
		}
		
		insertData(lecturePlan);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertData( ArrayList<LecturePlanInfo> list)
	{
		Connection con=DataBaseConnection.javaConnection();
		int i=0;
		i=objLecture.addLecturePlan(empid, selectedClass, selectedSection, subject, schid, session, con,list);
		if(i>=1)
		{
			String refNo3;
			try {
				refNo3=addWorkLog3(con,list);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lesson Plan Updated Successfully"));
			show=false;selectedClass=selectedSection=subject="";
			sectionList=null;subjectList=null;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error in Uploading Lesson Plan."));
			excelFile=null;
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog3(Connection conn,ArrayList<LecturePlanInfo> list)
	{
	    String value = "";
		String language= "";
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language= " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject;
	
		for(LecturePlanInfo lf : list)
		{	
		  value += "( Lecture no-"+lf.getLectureNo()+" --Unit No-"+lf.getUnitNo()+" --Unit Name-"+lf.getUnitName()+" --Description-"+lf.getDescription()+") ";
		}
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Initial Lecture Plan","WEB",value,conn);
		return refNo;
	}
	
	public void updateNow()
	{
		Connection con=DataBaseConnection.javaConnection();
		int i=0;
		i=objLecture.updateLecturePlan(empid, selectedClass, selectedSection, subject, schid, session, con,lessonName,lessonNo,description,selLecture.getLectureNo());
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lesson Plan Updated Successfully"));
			searchData();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error in Updating Lesson Plan."));
			excelFile=null;
		}
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		String className=obj.classname(selectedClass, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		language = " Class-"+className+" --Section-"+sectionname+" --Subject-"+subject;
		
		value = "Selected Lecture No-"+selLecture.getLectureNo()+" --Lession Name-"+lessonName+" --Lession No-"+lessonNo+" --Description-"+description;
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Initial Lecture Plan","WEB",value,conn);
		return refNo;
	}
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public UploadedFile getExcelFile() {
		return excelFile;
	}
	public void setExcelFile(UploadedFile excelFile) {
		this.excelFile = excelFile;
	}
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public LecturePlanInfo getSelLecture() {
		return selLecture;
	}

	public void setSelLecture(LecturePlanInfo selLecture) {
		this.selLecture = selLecture;
	}

	public String getLessonNo() {
		return lessonNo;
	}

	public void setLessonNo(String lessonNo) {
		this.lessonNo = lessonNo;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<SelectItem> getLessonNoList() {
		return lessonNoList;
	}

	public void setLessonNoList(ArrayList<SelectItem> lessonNoList) {
		this.lessonNoList = lessonNoList;
	}

	public ArrayList<SelectItem> getLessonNameList() {
		return lessonNameList;
	}

	public void setLessonNameList(ArrayList<SelectItem> lessonNameList) {
		this.lessonNameList = lessonNameList;
	}

	public ArrayList<LecturePlanInfo> getPlanList() {
		return planList;
	}

	public void setPlanList(ArrayList<LecturePlanInfo> planList) {
		this.planList = planList;
	}
}
