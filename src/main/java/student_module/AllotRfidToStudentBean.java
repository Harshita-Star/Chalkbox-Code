package student_module;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.RegexPattern;

@ManagedBean(name="allotRfidToStudent")
@ViewScoped

public class AllotRfidToStudentBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> list=new ArrayList<>(), selectStudent=new ArrayList<>(),stList = new ArrayList<>();
	boolean b;
	String total,schid;
	StudentInfo selectedStudent;
	String selectedSection,sectionName;
	String selectedCLassSection,className;
    ArrayList<SelectItem> sectionList,classSection;
	String filePath;
	transient
	UploadedFile excelFile;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public AllotRfidToStudentBean()
	{
		selectStudent=new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		schid = dbm.schoolId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		String userType=(String) ss.getAttribute("type");
		schid = dbm.schoolId();

		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classSection.add(si);

			ArrayList<SelectItem> temp = dbm.allClass(conn);

			if(temp.size()>0)
			{
				classSection.addAll(temp);
			}
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classSection = dbm.cordinatorClassList(empid, schid, conn);
		}
		b=false;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(selectedCLassSection.equals("-1"))
		{
			sectionList =new ArrayList<>();
			SelectItem it=new SelectItem();
			it.setLabel("All");
			it.setValue("-1");
			sectionList.add(it);
		}
		else
		{
			sectionList=dbm.allSection(selectedCLassSection,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			className = dbm.classNameFromidSchid(schid,selectedCLassSection, dbm.selectedSessionDetails(schid,conn), conn);
			sectionName = dbm.sectionNameByIdSchid(schid,selectedSection, conn);
			
			list=dbm.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
			if(list.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				b=false;
			}
			else
			{
				selectStudent=new ArrayList<>();
				b=true;
			}
			total=String.valueOf(list.size());
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
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int i=0;
			if(selectStudent.size()>0)
			{
				i=dbm.updateStudentBasicDetails(selectStudent,"RFID",conn);
				if(i>0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("RFID No Updated Successfully"));
					b=false;
					list = new ArrayList<>();
					selectStudent = new ArrayList<>();
				}
				else	
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate RFID No."));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Student(s) to Update RFID No."));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void importFromExcel() throws IOException, ParseException
	{
		if (excelFile != null && excelFile.getSize() > 0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			filePath = excelFile.getFileName();
			dbm.makeScanPath(excelFile,filePath,conn);

			SchoolInfoList info = dbm.fullSchoolInfo(conn);
			String realPath = "";
			if (info.getProjecttype().equals("online"))
			{
				realPath = info.getUploadpath();
			} 
			else
			{
				ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
				String savepath = ctx.getRealPath("/");
				realPath = savepath;

			}
			

			filePath=realPath+filePath;
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if(filePath!=null || !filePath.equals(""))
		{
			if(filePath.endsWith(".xlsx"))
			{
				String filename = filePath;
				List sheetData = new ArrayList<List<Cell>>();

				//FileInputStream fis = null;
				try
				{
					//fis = new FileInputStream(filename);
					
					XSSFWorkbook workbook = new XSSFWorkbook(filename);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					while (rowIterator.hasNext()) 
					{
						Row row = rowIterator.next();
						
						Iterator<Cell> cellIterator = row.cellIterator();	
						List data = new ArrayList<Cell>();
						
						while (cellIterator.hasNext())
						{ 
							Cell cell = cellIterator.next(); 
							data.add(cell);
						}
						
						sheetData.add(data);
					}
				}

				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				showExelData(sheetData);
			}
			else 
			{
				String filename = filePath;
				List sheetData = new ArrayList<List<Cell>>();

				FileInputStream fis = null;
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

						List data = new ArrayList<Cell>();
						while (cells.hasNext())
						{
							HSSFCell cell = (HSSFCell) cells.next();
							data.add(cell);
						}

						sheetData.add(data);
					}
				}

				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (fis != null) {
						fis.close();
					}
				}
				
				showExelData(sheetData);
			}

			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
		}

	}

	private void showExelData(List sheetData) throws ParseException {

		Connection conn=DataBaseConnection.javaConnection();
		//ExcelRead read=new ExcelRead();

		ArrayList<StudentInfo> stList=new ArrayList<>();

		for (int i =1;i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);

			StudentInfo info=new StudentInfo();
			String temp="";
			for (int j=0;j<list.size(); j++)
			{
				temp="";
				Cell cell = (Cell) list.get(j);

				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DataFormatter fmt = new DataFormatter();
					// Once per cell
					int j1=(int) cell.getNumericCellValue();
					/*//// // System.out.println("temp numeric1414"+j1);
					String valueAsSeenInExcel = fmt.formatCellValue(cell);
					*/
					temp=String.valueOf(j1);
				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					temp=String.valueOf(cell.getRichStringCellValue());
				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
				}

				if(j==0)
				{
					// S.No.
				}
				if(j==1)
				{
					temp.trim();
					if(temp==null || temp.equals(""))
					{
						info.setAddNumber("");
					}
					else
					{
						info.setAddNumber(temp);
					}
				}
				if(j==2)
				{
					// Sr. No.
				}
				if(j==3)
				{
					//Student Name
				}
				if(j==4)
				{
					// Father Name
				}
				if(j==5)
				{
					// Mother Name
				}
				if(j==6)
				{
					temp.trim();
					if(temp==null || temp.equals(""))
					{
						info.setRfidNo("");
					}
					else
					{
						info.setRfidNo(temp);
					}
				}
			}

			stList.add(info);
		}

		//(sList.size());
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		insertData(stList);
		
	}
	
	public void insertData(ArrayList<StudentInfo> list) throws ParseException
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			if(list.size()>0)
			{
				int i=new DatabaseMethods1().updateStudentBasicDetails(list,"RFID",conn);
				if(i>0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("RFID No. Updated Successfully"));
					//list=new DatabaseMethods1().searchStudentListByClassSection(selectedSection,conn);
					b=false;
					list = new ArrayList<>();
				}
				else	
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate RFID No."));
				}
			}
			else	
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Excel file is empty."));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public ArrayList<StudentInfo> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<StudentInfo> getSelectStudent() {
		return selectStudent;
	}
	public void setSelectStudent(ArrayList<StudentInfo> selectStudent) {
		this.selectStudent = selectStudent;
	}
	public ArrayList<StudentInfo> getStList() {
		return stList;
	}
	public void setStList(ArrayList<StudentInfo> stList) {
		this.stList = stList;
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
