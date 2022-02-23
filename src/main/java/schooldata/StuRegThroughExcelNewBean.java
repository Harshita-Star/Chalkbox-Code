package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;


@ManagedBean(name="stuRegThroughExcelNew")
@ViewScoped
public class StuRegThroughExcelNewBean implements Serializable
{
	String filePath;
	transient
	UploadedFile excelFile;
	ArrayList<StudentInfo> mainList=new ArrayList<>();
	boolean show=false,showAll=false,renderClass=false,renderAll=false;
	String savePath="",fileName="";
	ArrayList<SelectItem> classSection,sectionList;
	String selectedCLassSection,selectedSection;
	DatabaseMethods1 obj=new DatabaseMethods1();
	int sheetnumber;
	String registrationType,schid,session;
	
	public StuRegThroughExcelNewBean()
	{
		  Connection conn=DataBaseConnection.javaConnection();
		  schid=obj.schoolId();
			session=obj.selectedSessionDetails(schid, conn);
		   classSection=obj.allClass(conn);
	      
		   
		   try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public String goToClasSec()
	{
		PrimeFaces.current().executeInitScript("window.open('classSectionIdReport.xhtml')");
		return "";
	}
	
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void checkRegType()
	{
		if(registrationType.equalsIgnoreCase("All Classes"))
		{
			renderClass = false;
			renderAll = true;
			show =false;
			selectedCLassSection = "";
			selectedSection ="";
		}
		else
		{
			renderClass = true;
			renderAll = false;
			showAll = false;
		}
		mainList = new ArrayList<StudentInfo>();
		
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
						show = false;
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
	
	
	
	private  void showExelDataAll(List sheetData) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
		Connection conn=DataBaseConnection.javaConnection();
		
		ArrayList<StudentInfo> sList=new ArrayList<>();

		outer:for (int i =3;i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);

			StudentInfo info=new StudentInfo();
			inner:for (int j=0;j<list.size(); j++) {
				String temp="";
				Cell cell = (Cell) list.get(j);
				
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					//cell.setCellType(Cell.CELL_TYPE_STRING);
					DataFormatter fmt = new DataFormatter();
					// Once per cell
					String valueAsSeenInExcel = fmt.formatCellValue(cell);
					temp=valueAsSeenInExcel;

				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					temp=String.valueOf(cell.getRichStringCellValue());

				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
				}
				if(j==0)
				{
					temp.trim();
					try
					{
						temp=String.valueOf(Math.round(Double.parseDouble(temp)));
					}
					catch(Exception e)
					{
						temp=String.valueOf(temp);
					}
					info.setSrno(temp);
				}
				if(j==1)
				{
					temp.trim();
					Date date=null;
					try
					{
						date = (Date)formatter.parse(temp);
					}
					catch(Exception e)
					{
						try
						{
							date = (Date)formatter1.parse(temp);
						}
						catch(Exception e1)
						{
							try
							{
								date = (Date)sdf1.parse(temp);
							}
							catch(Exception e2)
							{
								date=null;
							}
						}
					}
					
					info.setAddDate(date);
					if(date!=null)
					{
						info.setAdmissionDate(sdf1.format(date));
					}
				}
				if(j==2)
				{
					String stName = temp.trim();
					if(stName.equals("")||stName == null) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Name is blank."));
						break outer;
					}else {
						info.setStudentName(stName);
					}
				}
				
				//FOR DATE OF BIRTH
				
				if(j==3)
				{
					temp.trim();
					if(temp.equals("") || temp ==null) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Date of birth can not be null for register a student."));
						break outer;
					}else {
//						System.out.println("0---->"+temp);
						Date date=null;
						try
						{
							date = (Date)formatter.parse(temp);
//							System.out.println("1---->"+date);
						}
						catch(Exception e)
						{
							try
							{
								date = (Date)formatter1.parse(temp);
//								System.out.println("2---->"+date);
							}
							catch(Exception e1)
							{
								try
								{
									date = (Date)sdf1.parse(temp);
//									System.out.println("3---->"+date);
								}
								catch(Exception e2)
								{
									date=null;
								}
							}
						}
						info.setDob(date);
						if(date!=null)
							info.setDobStr(sdf1.format(date));
					}
				}
				if(j==4)
				{
					temp.trim();
					try
					{
						temp=String.valueOf(Math.round(Double.parseDouble(temp)));
					}
					catch(Exception e)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data is Invaid in cell no. "+(j+1)+" of row "+(i-2)+" in your excel file"));
						break outer;
					}
					

					try 
				    {
	                    boolean checkClassPresent = obj.checkValidClass(temp,conn);
					    if(checkClassPresent==false)
						{
						   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Wrong Class Id in cell no. "+(j+1)+" of row "+(i-2)+" in your excel file"));
						   sList = new ArrayList<StudentInfo>();
						   break outer;
						}
					    } catch (Exception e) {
							// TODO: handle exception
						}

					info.setClassId(temp);
				}
				if(j==5)
				{
					temp.trim();
					try
					{
						temp=String.valueOf(Math.round(Double.parseDouble(temp)));
					}
					catch(Exception e)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data is Invaid in cell no. "+(j+1)+" of row "+(i-2)+" in your excel file"));
							break outer;
					}
					
					try
					{
						 boolean checkSectionPresent = obj.checkSectionpresent(temp,info.getClassId(),conn);
				         if(checkSectionPresent==false)
				         {
				            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Wrong Section Id in cell no. "+(j+1)+" of row "+(i-2)+" in your excel file"));
								sList = new ArrayList<StudentInfo>();
				            	break outer;
				         }
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					info.setSectionid(temp);
				}
				if(j==6)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFathersPhone(0);
					}
					else
					{
						try
						{
							info.setFathersPhone(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							info.setFathersPhone(0);
						}
                    }
				}
				if(j==7)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setStudentStatus("");
					}
					else
						info.setStudentStatus(temp);
				}
				if(j==8)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setAadharNo("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setAadharNo(temp);
					}
				}
				if(j==9)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setCurrentAddress("");
					}
					else
						info.setCurrentAddress(temp);
				}
				if(j==10)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setGender("");
					}
					else
						info.setGender(temp);
				}
				if(j==11)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFatherName("");
					}
					else
						info.setFatherName(temp);
				}
				if(j==12)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setMotherName("");
					}
					else
						info.setMotherName(temp);
				}
				if(j==13)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFatherAadhaar("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setFatherAadhaar(temp);
					}
				}
				if(j==14)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setMotherAadhaar("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setMotherAadhaar(temp);
					}
				}
				if(j==15)
				{
					temp.trim();
					if(temp.equals(""))
						info.setMothersPhone(0);
					else
					{
						try
						{
							info.setMothersPhone(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							info.setMothersPhone(0);
						}
                    }
				}
			}

			sList.add(info);
		
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mainList=new ArrayList<>();
		mainList.addAll(sList);
       // regstrationform( sList);
		
		if(mainList.size()>0)
		{
			showAll=true;
		}

		
	}


	
	

	public void importFromExcelInRegTable() throws IOException, ParseException
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
					showExelData(sheetData);
					showAll = false;
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

	private  void showExelData(List sheetData) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
		//SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		Connection conn=DataBaseConnection.javaConnection();
		
		ArrayList<StudentInfo> sList=new ArrayList<>();

		outer : for (int i =3;i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);

			StudentInfo info=new StudentInfo();
			for (int j=0;j<list.size(); j++) {
				String temp="";
				Cell cell = (Cell) list.get(j);
				
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					//cell.setCellType(Cell.CELL_TYPE_STRING);
					DataFormatter fmt = new DataFormatter();
					// Once per cell
					String valueAsSeenInExcel = fmt.formatCellValue(cell);
					temp=valueAsSeenInExcel;

				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					temp=String.valueOf(cell.getRichStringCellValue());

				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
				}
				if(j==0)
				{
					temp.trim();
					try
					{
						temp=String.valueOf(Math.round(Double.parseDouble(temp)));
					}
					catch(Exception e)
					{
						temp=String.valueOf(temp);
					}
					info.setSrno(temp);
				}
				if(j==1)
				{
					temp.trim();
					Date date=null;
					try
					{
						date = (Date)formatter.parse(temp);
					}
					catch(Exception e)
					{
						try
						{
							date = (Date)formatter1.parse(temp);
						}
						catch(Exception e1)
						{
							try
							{
								date = (Date)sdf1.parse(temp);
							}
							catch(Exception e2)
							{
								date=null;
							}
						}
					}
					
					info.setAddDate(date);
					if(date!=null)
					{
						info.setAdmissionDate(sdf1.format(date));
					}
				}
				if(j==2)
				{
					String stName = temp.trim();
					if(stName.equals("")||stName == null) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Name is blank."));
						break outer;
					}else {
						info.setStudentName(stName);
					}
					//info.setStudentName(temp);
				}
				
				
				//FOR DATE OF BIRTH
				
				if(j==3)
				{
					temp.trim();
					if(temp.equals("")||temp == null) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Date of birth can not be null for register a student."));
						break outer;
					}else {
						Date date=null;
//						System.out.println(temp);
						try
						{
							date = (Date)formatter.parse(temp);
//							 System.out.println("Date...1...."+date);
						}
						catch(Exception e)
						{
							try
							{
								date = (Date)formatter1.parse(temp);
//								 System.out.println("Date...2..."+date);
							}
							catch(Exception e1)
							{
								try
								{
									date = (Date)sdf1.parse(temp);
//									 System.out.println("Date...3/...."+date);
								}
								catch(Exception e2)
								{
									date=null;
								}
							}
						}
						info.setDob(date);
						if(date!=null)
							info.setDobStr(sdf1.format(date));
					}
				}
				if(j==4)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFathersPhone(0);
					}
					else
					{
						try
						{
							info.setFathersPhone(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							info.setFathersPhone(0);
						}
                    }
				}
				if(j==5)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setStudentStatus("");
					}
					else
						info.setStudentStatus(temp);
				}
				if(j==6)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setAadharNo("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setAadharNo(temp);
					}
				}
				if(j==7)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setCurrentAddress("");
					}
					else
						info.setCurrentAddress(temp);
				}
				if(j==8)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setGender("");
					}
					else
						info.setGender(temp);
				}
				if(j==9)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFatherName("");
					}
					else
						info.setFatherName(temp);
				}
				if(j==10)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setMotherName("");
					}
					else
						info.setMotherName(temp);
				}
				if(j==11)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setFatherAadhaar("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setFatherAadhaar(temp);
					}
				}
				if(j==12)
				{
					temp.trim();
					if(temp.equals(""))
					{
						info.setMotherAadhaar("");
					}
					else
					{
						try
						{
							temp=String.valueOf(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							temp="";
						}
						info.setMotherAadhaar(temp);
					}
				}
				if(j==13)
				{
					temp.trim();
					if(temp.equals(""))
						info.setMothersPhone(0);
					else
					{
						try
						{
							info.setMothersPhone(Double.valueOf(temp).longValue());
						}
						catch(Exception e)
						{
							info.setMothersPhone(0);
						}
                    }
				}
			}

			sList.add(info);
		
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mainList=new ArrayList<>();
		mainList.addAll(sList);
       // regstrationform( sList);
		
		if(mainList.size()>0)
		{
			show=true;
		}
		
	}/*

	public StudentInfo studentDetailsByAddNo(String srno, Connection conn) {

		Statement st = null;
		ResultSet rr = null;
		StudentInfo list=null;
		try {
			st = conn.createStatement();
			String query = "select * from registration1 where sr_no='"+srno+ "'";
			rr = st.executeQuery(query);
			if (rr.next()) {
				list=new StudentInfo();
				list.setAddNumber(rr.getString("addNumber"));
				list.setSchid(rr.getString("schid"));			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	*/
	public void regstrationform()
	{
	    Connection conn=DataBaseConnection.javaConnection();

	    int agreement = obj.checkAgreementFor(schid, conn);
	    int currentStrength = Integer.parseInt(obj.allStudentcount(schid,"", "",session,"", conn));
		
		int sizeList = mainList.size();
		int flag = 0;

		if(agreement<500)
		{
			if((currentStrength+sizeList)>=(agreement+25))
			{
				flag = 1;		
			}
		}
		else
		{
			if((currentStrength+sizeList)>=(agreement+50))
			{
				flag = 1;
			}
		}
	    
	    if(flag == 1)
	    {
	    	FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have agreement limit of "+agreement+" students and current Strength is "+currentStrength+".Uploading This Excel will exceed agreement limit, Please contact Chalkbox Administrator for new registration.","You have agreement limit of "+agreement+" students and current Strength is "+currentStrength+".Uploading This Excel will exceed agreement limit, Please contact Chalkbox Administrator for new registration."));
	    }
	    else
	    {	
	    
	    obj.addExcelFileData(fileName,savePath,selectedCLassSection,selectedSection,conn);
		String refN6;
		try {
			refN6=addWorkLog6(conn);
		} catch (Exception e) {
			// TODO: handle exception
		}
		for(StudentInfo ls:mainList)
		{
			Date addmissionDate=null;
			if(ls.getAdmissionDate()==null||ls.getAdmissionDate().equalsIgnoreCase(""))
			{
				addmissionDate=new Date();
			}
			else
			{
				addmissionDate=ls.getAddDate();

			}
			Date dob=null;
			if(ls.getDobStr()==null||ls.getDobStr().equalsIgnoreCase(""))
			{
				dob=new Date();
			}
			else
			{
				dob=ls.getDob();
			}
			
			
			ArrayList<String> lss=new ArrayList<>();
			
			ArrayList<ConnsessionList>list1=new DatabaseMethods1().allCategoryList(conn);
			UploadedFile studentImage=null;
			//int i=1;
			int i=obj.studentRegistration("",obj.schoolId(),addmissionDate,ls.getStudentName(),dob,selectedSection,ls.getAadharNo(),ls.getFathersPhone(),
					ls.getCurrentAddress(),ls.getCurrentAddress(),ls.getGender(),"INDIAN","","","","",0
					,"","","","INDIA",ls.getFatherName(),ls.getMotherName()
					,"",ls.getFatherAadhaar(),ls.getMotherAadhaar(),ls.getMothersPhone(),""
					,"","","","","","","","","","","","","","","","",""
					,"","","","","","","",
					"",""
					,"","","","",list1.get(0).getId(),"","","","","","",
					lss,studentImage,studentImage,studentImage,studentImage,studentImage,"",""
					,"","",ls.getStudentStatus(),"","0","","",conn,"0",ls.getSrno(),"","temp", selectedCLassSection,"","","","","","","","","","","","","","");
			
			
			if(i>=1)
			{
				String refNo7;
				try {
					refNo7=addWorkLog7(conn,addmissionDate,ls.getStudentName(),dob,selectedSection,ls.getAadharNo(),ls.getFathersPhone(),
							ls.getCurrentAddress(),ls.getCurrentAddress(),ls.getGender(),ls.getFatherName(),ls.getMotherName()
							,ls.getFatherAadhaar(),ls.getMotherAadhaar(),ls.getMothersPhone(),ls.getStudentStatus(),ls.getSrno(),selectedCLassSection);
				} catch (Exception e) {
					// TODO: handle exception
				}
				int maxnumber=i;
				String cbNumber = String.valueOf(maxnumber);
				obj.updateStudentId("CB"+String.valueOf(maxnumber),i,conn);
				String refNo8;
				try {
					refNo8=addWorkLog2(conn,"CB"+String.valueOf(maxnumber));
				} catch (Exception e) {
					// TODO: handle exception
				}
				obj.transportDataEntry(obj.schoolId(),addmissionDate,"CB"+String.valueOf(maxnumber),"", "No",selectedCLassSection,conn);
				String refNo9;
				try {
					refNo9=addWorkLog9(conn,"CB"+String.valueOf(maxnumber),addmissionDate);
				} catch (Exception e) {
					// TODO: handle exception
				} 			
 				
				String password = obj.randomAlphaNumeric(8);
				obj.insertEmployeeInLogin("CB"+String.valueOf(maxnumber), password, "student", obj.schoolId(), conn);
				String refNo10;
				try {
					refNo10=addWorkLog10(conn,"CB"+String.valueOf(maxnumber),password);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
			
		
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Student Added Successfully","Student Added Successfully"));
		
		
		mainList=new ArrayList<>();
		show=false;
		registrationType ="";
		renderClass= false;
		
	    }
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public String addWorkLog6(Connection conn)
	{
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
	    String value = "Filename-"+fileName+" --SavePath-"+savePath;
		String language = "Class - "+className+"  Section-"+sectionname;

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Excel Data(Registration)","WEB",value,conn);
		return refNo;
	}
	
	private String addWorkLog7(Connection conn, Date addmissionDate, String studentName, Date dob, String sectionid,
			String aadharNo, long fathersPhone, String currentAddress, String currentAddress2, String gender,
			String fatherName, String motherName, String fatherAadhaar, String motherAadhaar, long mothersPhone,
			String studentStatus, String srno, String classId) {
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedCLassSection, schid, conn);
		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		String language = "Class-"+className+" Section-"+sectionname;
		SimpleDateFormat formateer = new SimpleDateFormat("dd/MM/yyyy");
		String datee = formateer.format(addmissionDate);
		String dobb= formateer.format(dob);
		
		String value = "Adm. Date-"+datee+" --Student-"+studentName+" --Dob-"+dobb+" --SectionId-"+sectionid+" --Aadhar-"+aadharNo+" --Father phone-"+fathersPhone+" --Address-"+
				currentAddress+" --Gender-"+gender+" --Father-"+fatherName+" --Mother-"+motherName+" --Father aadhar-"+" --FatherAadhar-"+
				fatherAadhaar+" --MotherAadhar-"+motherAadhaar+" --mother Phone-"+mothersPhone+" --Student Status-"+studentStatus+" --SrNo-"+srno+" --FatherAadhar-"+classId;


		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Excel(Registration)","WEB",value,conn);
		return refNo;
		
	}
	
private String addWorkLog9(Connection conn, String studentId, Date addmissionDate) {
	
	    DatabaseMethods1 obj = new DatabaseMethods1(); 
	    String schid = obj.schoolId();
	    String className=obj.classname(selectedCLassSection, schid, conn);
	    String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
		
		String language = "Class-"+className+" section-"+sectionname;
		SimpleDateFormat formateer = new SimpleDateFormat("dd/MM/yyyy");
		String value = "Student Id-"+studentId +" --Admission Date-"+formateer.format(addmissionDate);


		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"transport Data entry Excel(Registration)","WEB",value,conn);
		return refNo;
	}

  private String addWorkLog10(Connection conn, String StudentId, String password) {
	
	String value = "StudentId-"+StudentId+" --Password-"+password;
	
	DatabaseMethods1 obj = new DatabaseMethods1(); 
	String schid = obj.schoolId();
	String className=obj.classname(selectedCLassSection, schid, conn);
	String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
	String language = "Class-"+className+" Section-"+sectionname;

	String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"insert Login Excel (Registration)","WEB",value,conn);
	return refNo;
  }
	
	
	
	
	public void regstrationformAll()
	{
	    Connection conn=DataBaseConnection.javaConnection();
	    
	    DatabaseMethods1 obj = new DatabaseMethods1();

	    int agreement = obj.checkAgreementFor(obj.schoolId(), conn);
	    int currentStrength = Integer.parseInt(obj.allStudentcount(obj.schoolId(),"", "",session,"", conn));
		
		int sizeList = mainList.size();
		int flag = 0;

		if(agreement<500)
		{
			if((currentStrength+sizeList)>=(agreement+25))
			{
				flag = 1;		
			}
		}
		else
		{
			if((currentStrength+sizeList)>=(agreement+50))
			{
				flag = 1;
			}
		}
	    
	    if(flag == 1)
	    {
	    	FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"You have agreement limit of "+agreement+" students and current Strength is "+currentStrength+".Uploading This Excel will exceed agreement limit, Please contact Chalkbox Administrator for new registration.","You have agreement limit of "+agreement+" students and current Strength is "+currentStrength+".Uploading This Excel will exceed agreement limit, Please contact Chalkbox Administrator for new registration."));
	    }
	    else
	    {
	    
		new DatabaseMethods1().addExcelFileData(fileName,savePath,"all","all",conn);
		 String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
		for(StudentInfo ls:mainList)
		{
			Date addmissionDate=null;
			if(ls.getAdmissionDate()==null||ls.getAdmissionDate().equalsIgnoreCase(""))
			{
				addmissionDate=new Date();
			}
			else
			{
				addmissionDate=ls.getAddDate();

			}
			Date dob=null;
			if(ls.getDobStr()==null||ls.getDobStr().equalsIgnoreCase(""))
			{
				dob=new Date();
			}
			else
			{
				dob=ls.getDob();
			}
			
			
			ArrayList<String> lss=new ArrayList<>();
			ArrayList<ConnsessionList>list1=new DatabaseMethods1().allCategoryList(conn);
			UploadedFile studentImage=null;
			//int i=1;
			int i=obj.studentRegistration("",obj.schoolId(),addmissionDate,ls.getStudentName(),dob,ls.getSectionid(),ls.getAadharNo(),ls.getFathersPhone(),
					ls.getCurrentAddress(),ls.getCurrentAddress(),ls.getGender(),"INDIAN","","","","",0
					,"","","","INDIA",ls.getFatherName(),ls.getMotherName()
					,"",ls.getFatherAadhaar(),ls.getMotherAadhaar(),ls.getMothersPhone(),""
					,"","","","","","","","","","","","","","","","",""
					,"","","","","","","",
					"",""
					,"","","","",list1.get(0).getId(),"","","","","","",
					lss,studentImage,studentImage,studentImage,studentImage,studentImage,"",""
					,"","",ls.getStudentStatus(),"","0","","",conn,"0",ls.getSrno(),"","temp",ls.getClassId(),"","","","","","","","","","","","","","");
			
			
			if(i>=1)
			{
				String refNo5;
				try {
					refNo5=addWorkLog5(conn,addmissionDate,ls.getStudentName(),dob,ls.getSectionid(),ls.getAadharNo(),ls.getFathersPhone(),
							ls.getCurrentAddress(),ls.getCurrentAddress(),ls.getGender(),ls.getFatherName(),ls.getMotherName()
							,ls.getFatherAadhaar(),ls.getMotherAadhaar(),ls.getMothersPhone(),ls.getStudentStatus(),ls.getSrno(),ls.getClassId());
				} catch (Exception e) {
					// TODO: handle exception
				}	
				int maxnumber=i;
				String cbNumber = String.valueOf(maxnumber);
				obj.updateStudentId("CB"+String.valueOf(maxnumber),i,conn);
				String refNo2;
				try {
					refNo2=addWorkLog2(conn,"CB"+String.valueOf(maxnumber));
				} catch (Exception e) {
					// TODO: handle exception
				}
				obj.transportDataEntry(obj.schoolId(),addmissionDate,"CB"+String.valueOf(maxnumber),"", "No", ls.getClassId(),conn);
				String refNo3;
				try {
					refNo3=addWorkLog3(conn,"CB"+String.valueOf(maxnumber),addmissionDate);
				} catch (Exception e) {
					// TODO: handle exception
				}
				String password = obj.randomAlphaNumeric(8);
				obj.insertEmployeeInLogin("CB"+String.valueOf(maxnumber), password, "student", obj.schoolId(), conn);
				String refNo4;
				try {
					refNo4=addWorkLog4(conn,"CB"+String.valueOf(maxnumber),password);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
			
		
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Student Added Successfully","Student Added Successfully"));
		
		
		mainList=new ArrayList<>();
		
		showAll=false;
		registrationType ="";
		renderAll=false;
		
	    }
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private String addWorkLog5(Connection conn, Date addmissionDate, String studentName, Date dob, String sectionid,
			String aadharNo, long fathersPhone, String currentAddress, String currentAddress2, String gender,
			String fatherName, String motherName, String fatherAadhaar, String motherAadhaar, long mothersPhone,
			String studentStatus, String srno, String classId) {
		
		String language = "Class/Section - All";
		SimpleDateFormat formateer = new SimpleDateFormat("dd/MM/yyyy");
		String datee = formateer.format(addmissionDate);
		String dobb= formateer.format(dob);
		
		String value = "Adm. Date-"+datee+" --Student-"+studentName+" --Dob-"+dobb+" --SectionId-"+sectionid+" --Aadhar-"+aadharNo+" --Father phone-"+fathersPhone+" --Address-"+
				currentAddress+" --Gender-"+gender+" --Father-"+fatherName+" --Mother-"+motherName+" --Father aadhar-"+" --FatherAadhar-"+
				fatherAadhaar+" --MotherAadhar-"+motherAadhaar+" --mother Phone-"+mothersPhone+" --Student Status-"+studentStatus+" --SrNo-"+srno+" --FatherAadhar-"+classId;


		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Student Excel(Registration)","WEB",value,conn);
		return refNo;
		
	}


	private String addWorkLog4(Connection conn, String StudentId, String password) {
		
		String value = "StudentId-"+StudentId+" --Password-"+password;
		String language = "Class/Section - All";

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"insert Login Excel (Registration)","WEB",value,conn);
		return refNo;
	}


	private String addWorkLog3(Connection conn, String studentId, Date addmissionDate) {
		
		String language = "Class/Section - All";
		SimpleDateFormat formateer = new SimpleDateFormat("dd/MM/yyyy");
		String value = "Student Id-"+studentId +" --Admission Date-"+formateer.format(addmissionDate);


		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"transport Data entry Excel(Registration)","WEB",value,conn);
		return refNo;
	}


	public String addWorkLog(Connection conn)
	{
	    String value = "Filename-"+fileName+" --SavePath-"+savePath;
		String language = "Class/Section - All";

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Excel Data(Registration)","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn,String id)
	{
	    String value = "";
		String language = "Student Id-"+id;

		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Update Student Id Excel(Registration)","WEB",value,conn);
		return refNo;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public UploadedFile getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(UploadedFile excelFile) {
		this.excelFile = excelFile;
	}

	public ArrayList<StudentInfo> getMainList() {
		return mainList;
	}

	public void setMainList(ArrayList<StudentInfo> mainList) {
		this.mainList = mainList;
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


	public boolean isRenderClass() {
		return renderClass;
	}


	public void setRenderClass(boolean renderClass) {
		this.renderClass = renderClass;
	}


	public String getRegistrationType() {
		return registrationType;
	}


	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}


	public boolean isRenderAll() {
		return renderAll;
	}


	public void setRenderAll(boolean renderAll) {
		this.renderAll = renderAll;
	}


	public boolean isShowAll() {
		return showAll;
	}


	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}






}





	
