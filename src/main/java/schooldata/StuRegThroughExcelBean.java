package schooldata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
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
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.primefaces.model.file.UploadedFile;

import session_work.QueryConstants;



@ManagedBean(name="stuRegThroughExcel")
@ViewScoped
public class StuRegThroughExcelBean implements Serializable
{
	String filePath;
	transient
	UploadedFile excelFile;
	ArrayList<StudentInfo> slist, admlist;
	static DataBaseMethodStudent objStd=new DataBaseMethodStudent();

	/*	public void show()
	{
		int count=1;
		admlist=new DatabaseMethods1().listOfAllAdmissionNo();
		//("Size: "+admlist.size());
		for(StudentInfo ii: admlist)
		{
			//("count: "+count++ +"= " +ii.getAddNumber());
		}
	}*/

	public void importFromExcelInRegTable() throws IOException, ParseException
	{
		if (excelFile != null && excelFile.getSize() > 0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			filePath = excelFile.getFileName();
			new DatabaseMethods1().makeScanPath(excelFile,filePath,conn);//Offline

			filePath = excelFile.getFileName();
			new DatabaseMethods1().makeScanPath(excelFile,filePath,conn);	//Online

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String realPath = ctx.getRealPath("/");

			filePath=realPath+filePath;



			//	String folderName="/home/wwwindtiedu/public_html/Scan_images/";
			//filePath=folderName+filePath;
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if(filePath!=null || !filePath.equals(""))
		{
			String filename = filePath;
			List sheetData = new ArrayList();

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

					List data = new ArrayList();
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
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
		}

	}

	private static void showExelData(List sheetData) throws ParseException {

		Connection conn=DataBaseConnection.javaConnection();
		//ExcelRead read=new ExcelRead();

		ArrayList<StudentInfo> sList=new ArrayList<>();

		for (int i =0;i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);

			StudentInfo info=new StudentInfo();
		
			for (int j=0;j<list.size(); j++) {
				String temp="";
				Cell cell = (Cell) list.get(j);

				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

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
					//// // System.out.println("sr_no"+temp);
					info.setSrno(temp);
				}
				if(j==1)
				{
					temp.trim();
					//// // System.out.println("studentName"+temp);
					info.setStudentName(temp);


				}
				if(j==2)
				{
					temp.trim();
					//// // System.out.println("fathername"+temp);
					info.setFatherName(temp);


				}
				if(j==3)
				{
					temp.trim();
					//// // System.out.println("mothername"+temp);
					info.setMothersName(temp);


				}
				if(j==4)
				{
					temp.trim();
					//// // System.out.println("classid"+temp);
					info.setClassId(temp);


				}
				if(j==5)
				{
					temp.trim();
					//// // System.out.println("sectionid"+temp);
					info.setSectionid(temp);


				}
				if(j==6)
				{
					temp.trim();
					//// // System.out.println("gender"+temp);
					info.setGender(temp);


				}
				if(j==7)
				{
					temp.trim();
					//// // System.out.println("dob"+temp);
					info.setDobStr(temp);


				}
				if(j==8)
				{
					temp.trim();
					//// // System.out.println("fathersphone"+temp);
					if(temp.equals(""))
					{
						info.setFathersPhone(0);
	
					}
					else
					{
						info.setFathersPhone(Long.parseLong(temp));
                    }
					
					

				}if(j==9)
				{
					temp.trim();
					//// // System.out.println("mothersphone"+temp);
					if(temp.equals(""))
					{
						info.setMothersPhone(0);
	                }
					else
					{
					    info.setMothersPhone(Long.parseLong(temp));
                    }
				}if(j==10)
				{
					temp.trim();
					//// // System.out.println("stadhar"+temp);
					info.setAadharNo(temp);


				}if(j==11)
				{
					temp.trim();
					//// // System.out.println("fadhar"+temp);
					info.setFatherAadhaar(temp);


				}
				if(j==12)
				{
					temp.trim();
					//// // System.out.println("madhar"+temp);
					info.setMotherAadhaar(temp);


				}if(j==13)
				{
					temp.trim();
					//// // System.out.println("add"+temp);
					info.setPermanentAddress(temp);


				}if(j==14)
				{
					temp.trim();
					//// // System.out.println("cateid "+temp);
					info.setCategId(temp);


				}
				


			}

			sList.add(info);
		}

		//(sList.size());
	regstrationform( sList);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void insertData(Connection conn, ArrayList<BLMFEEEXCEL> list) throws ParseException
	{
		try
		{
			DatabaseMethods1 DBM=new DatabaseMethods1();

			for(BLMFEEEXCEL ss:list)
			{
				StudentInfo in=new StuRegThroughExcelBean().studentDetailsByAddNo(ss.getAddno(), conn);


				if(in==null)
				{

				}
				else
				{
					Date d=null;

					d=new SimpleDateFormat("dd-MMM-yyyy").parse(ss.getRdate());

					if(in.getSchid().equals("251"))
					{

						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getA_charges()), "277", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getTuition_Fee()), "263", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");

						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getTrns_amount()), "0", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");

						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getFine()), "-2", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getH_class()), "278", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getLab_fee()), "279", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getEcare()), "280", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getRefreshment()), "281", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getAbcus()), "282", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getRobotics()), "283", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getPd_class()), "284", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getImo()), "285", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getMis_amt()), "286", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


					}
					else
					{
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getA_charges()), "287", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getTuition_Fee()), "264", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");

						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getTrns_amount()), "0", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");

						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getFine()), "-2", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getH_class()), "288", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getLab_fee()), "289", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getEcare()), "290", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getRefreshment()), "291", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getAbcus()), "292", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("251",in,Integer.parseInt(ss.getRobotics()), "293", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getPd_class()), "294", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getImo()), "295", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");
						DBM.submitFeeSchid("252",in,Integer.parseInt(ss.getMis_amt()), "296", ss.getPaymode(),"",
								"", ss.getRno(), 0, "2018-2019", d, "", "",
								null, null, conn, ss.getInstallement(), d, "0", "current","N/A");


					}

				}


				/*for(BLMFEEEXCEL ss:list)
			{

				String query="insert into packing_info(combination, type)values('"+ss.getRdate()+"','"+ss.getRno()+"')  ";
				int i=st.executeUpdate(query);
				//(query);
			}*/
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static void updateinfo(String addnumber,/* String updatenum, String studename, String fathesname, String mname, String classid, */long phone/*, String transport,String status,String stustatus*/)
	{
		Connection conn=DataBaseConnection.javaConnection();
		Statement st=null;
		try{
			st=conn.createStatement();
			String ww="update registration1 set fathersPhone='"+phone+"',sendMessageMobileNo='"+phone+"' where addNumber='"+addnumber+"' and schid='1'";

			st.executeUpdate(ww);

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



	public StudentInfo studentDetailsByAddNo(String srno, Connection conn) {

		Statement st = null;
		ResultSet rr = null;
		StudentInfo list=null;
		try {
			st = conn.createStatement();
			String query = "select * from registration1 where sr_no='"+srno
					+ "'";
			//(query);


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





	public void importFromExcelInPromotionTable() throws IOException, ParseException
	{
		if (excelFile != null && excelFile.getSize() > 0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			filePath = excelFile.getFileName();
			new DatabaseMethods1().makeScanPath(excelFile,filePath,conn);

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String realPath = ctx.getRealPath("/");

			filePath=realPath+filePath;
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if(filePath!=null || !filePath.equals(""))
		{
			String filename = filePath;
			List sheetData = new ArrayList();

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

					List data = new ArrayList();
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

			showExelData1(sheetData);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
		}

	}
	
	
	
	public static void regstrationform(ArrayList<StudentInfo>list)
	{
	
		
		for(StudentInfo ls:list)
		{
			
			
			Date addmissionDate=null;
			
			addmissionDate=new Date();
		
			
			
			DateFormat formatter1 = null ;
			Date dob=null;
			if(ls.getDobStr()==null||ls.getDobStr().equalsIgnoreCase(""))
			{
				dob=new Date();
			}
			else
			{
				if(ls.getDobStr().contains("-"))
				{
					formatter1 = new SimpleDateFormat("dd-MM-yyyy") ;
				}
				else if(ls.getDobStr().contains("/"))
				{
					formatter1 = new SimpleDateFormat("dd/MM/yyyy") ;
				}
				else if(ls.getDobStr().contains("."))
				{
					formatter1 = new SimpleDateFormat("dd.MM.yyyy") ;
				}
				try {
					dob = formatter1.parse(ls.getDobStr());
				} catch (ParseException e) {
					
					e.printStackTrace();
				}

			}
			
			ArrayList<String> lss=new ArrayList<>();
			Connection conn=DataBaseConnection.javaConnection();
			DatabaseMethods1 DBM=new DatabaseMethods1();
		
			UploadedFile studentImage=null;
			int i=	DBM.studentRegistration("",DBM.schoolId(),addmissionDate,ls.getStudentName(),dob,/*className1,*/ls.getSectionid(),ls.getAadharNo(),ls.getFathersPhone(),
					ls.getPermanentAddress(),ls.getPermanentAddress(),ls.getGender(),"INDIAN","",ls.getCategId(),"","",0
					,"","","","INDIA",ls.getFatherName(),ls.getMothersName()
					,"",ls.getFatherAadhaar(),ls.getMotherAadhaar(),ls.getMothersPhone(),""
					,"","","","","","","","","","","","","","","","",""
					,"","","","","","","",
					"",""
					,"","","","","321","","","","","","",
					lss,studentImage,studentImage,studentImage,studentImage,studentImage,"",""
					,"","","","","0","","",conn,"0",ls.getSrno(),"","temp", "","","","","","","","","","","","","","","");
			if(i>=1)
			{
				int maxnumber=i;
				String cbNumber = String.valueOf(maxnumber);
				DBM.updateStudentId("CB"+String.valueOf(maxnumber),i,conn);
				DBM.transportDataEntry(DBM.schoolId(),addmissionDate,"CB"+String.valueOf(maxnumber), "", "No","",conn);
				
			}
		}
			
		
		
		
	}

	private static void showExelData1(List sheetData) throws ParseException {

		Connection conn=DataBaseConnection.javaConnection();
		//ExcelRead read=new ExcelRead();

		ArrayList<StudentInfo> sList=new ArrayList<>();

		for (int i = 1; i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);

			StudentInfo info=new StudentInfo();

			String temp="";
			for (int j = 0; j <= 4; j++) {
				Cell cell = (Cell) list.get(j);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

					DataFormatter fmt = new DataFormatter();

					String valueAsSeenInExcel = fmt.formatCellValue(cell);
					temp=valueAsSeenInExcel;

				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

					temp=String.valueOf(cell.getRichStringCellValue());

				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

					temp=String.valueOf(Cell.CELL_TYPE_BOOLEAN);
				}
				else
				{
					temp="";
				}

				if(j==0)
				{
					info.setAddNumber(temp);
				}
				if(j==1)
				{
					info.setPreviousClass(temp);
				}
				if(j==2)
				{
					info.setPromotedClass(temp);
				}
				if(j==3)
				{
					info.setPromotedDateStr(temp);
				}
				if(j==4)
				{
					info.setSession(temp);
				}



			}
			sList.add(info);
		}

		insertData1(conn, sList);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void insertData1(Connection conn, ArrayList<StudentInfo> list) throws ParseException
	{
		int i=0;
		for(StudentInfo info : list)
		{
			//GPS//i=insertQuestion(info.getAddNumber(), info.getStudentName(), info.getFatherName(), info.getMotherName(), info.getClassName(), info.getDobString(), info.getAdmissionDate());
			i=insertQuestion(conn,info.getAddNumber(), info.getPreviousClass(), info.getPromotedClass(), info.getPromotedDateStr(), info.getSession());
		}

		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Excel Upload Successfully done."));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
		}
	}

	private static int insertQuestion(Connection conn,String addNumber, String preClass, String pClass,String pDateStr, String session) throws ParseException
	{
		//Connection conn=DataBaseConnection.javaConnection();
		Statement st=null;
		String classId = null, pclassId = null;
		if(preClass.equalsIgnoreCase("Nursery"))
		{
			classId="1";
		}
		else if(preClass.equalsIgnoreCase("Play Group"))
		{
			classId="2";
		}
		else if(preClass.equalsIgnoreCase("L.K.G"))
		{
			classId="3";
		}

		else if(preClass.equalsIgnoreCase("U.K.G"))
		{
			classId="4";
		}

		else if(preClass.equalsIgnoreCase("1"))
		{
			classId="5";
		}

		else if(preClass.equalsIgnoreCase("2"))
		{
			classId="6";
		}

		else if(preClass.equalsIgnoreCase("3"))
		{
			classId="7";
		}

		else if(preClass.equalsIgnoreCase("4"))
		{
			classId="8";
		}
		else if(preClass.equalsIgnoreCase("5"))
		{
			classId="9";
		}

		else if(preClass.equalsIgnoreCase("6"))
		{
			classId="10";
		}

		else if(preClass.equalsIgnoreCase("7"))
		{
			classId="11";
		}

		else if(preClass.equalsIgnoreCase("8"))
		{
			classId="12";
		}
		else if(preClass.equalsIgnoreCase("9"))
		{
			classId="13";
		}

		else if(preClass.equalsIgnoreCase("10"))
		{
			classId="14";
		}

		else if(preClass.equalsIgnoreCase("11"))
		{
			classId="15";
		}

		else if(preClass.equalsIgnoreCase("12"))
		{
			classId="16";
		}


		if(pClass.equalsIgnoreCase("Nursery"))
		{
			pclassId="1";
		}
		else if(pClass.equalsIgnoreCase("Play Group"))
		{
			classId="2";
		}
		else if(pClass.equalsIgnoreCase("L.K.G"))
		{
			pclassId="3";
		}

		else if(pClass.equalsIgnoreCase("U.K.G"))
		{
			pclassId="4";
		}

		else if(pClass.equalsIgnoreCase("1"))
		{
			pclassId="5";
		}

		else if(pClass.equalsIgnoreCase("2"))
		{
			pclassId="6";
		}

		else if(pClass.equalsIgnoreCase("3"))
		{
			pclassId="7";
		}

		else if(pClass.equalsIgnoreCase("4"))
		{
			pclassId="8";
		}
		else if(pClass.equalsIgnoreCase("5"))
		{
			pclassId="9";
		}

		else if(pClass.equalsIgnoreCase("6"))
		{
			pclassId="10";
		}

		else if(pClass.equalsIgnoreCase("7"))
		{
			pclassId="11";
		}

		else if(pClass.equalsIgnoreCase("8"))
		{
			pclassId="12";
		}
		else if(pClass.equalsIgnoreCase("9"))
		{
			pclassId="13";
		}

		else if(pClass.equalsIgnoreCase("10"))
		{
			pclassId="14";
		}

		else if(pClass.equalsIgnoreCase("11"))
		{
			pclassId="15";
		}

		else if(pClass.equalsIgnoreCase("12"))
		{
			pclassId="16";
		}



		DateFormat formatter1 = null ;
		Date startDate=null;
		if(pDateStr.equalsIgnoreCase("No Data")||pDateStr.equalsIgnoreCase(""))
		{
			startDate=new Date();
		}
		else
		{
			if(pDateStr.contains("-"))
			{
				formatter1 = new SimpleDateFormat("dd-MM-yyyy") ;
			}
			else if(pDateStr.contains("/"))
			{
				formatter1 = new SimpleDateFormat("dd/MM/yyyy") ;
			}
			else if(pDateStr.contains("."))
			{
				formatter1 = new SimpleDateFormat("dd.MM.yyyy") ;
			}
			startDate = formatter1.parse(pDateStr);

		}


		Timestamp tt=new Timestamp(startDate.getTime());

		ArrayList<String> tempList=objStd.basicFieldsForStudentList();
		StudentInfo info=objStd.studentDetail(String.valueOf(addNumber),"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, new DatabaseMethods1().schoolId(), tempList, conn).get(0);
		

		try
		{
			st=conn.createStatement();
			String qq="insert into promotion (classFrom,classTo,studentId,postDate,session) values ('"+classId+"','"+pclassId+"','"+addNumber+"','"+tt+"','"+session+"')";

			int f=st.executeUpdate(qq);

			if(f==1)
			{

				String presession=session;
				if(!presession.equals("")||!presession.equalsIgnoreCase("No Data"))
				{
					String ww="update registration1 set classid='"+pclassId+"',session='"+presession+"'  where addNumber='"+addNumber+"' and status='ACTIVE'";

					st.executeUpdate(ww);
				}

			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return 1;
	}






	public void importFromExcelInTCTable() throws IOException, ParseException
	{
		if (excelFile != null && excelFile.getSize() > 0)
		{
			Connection conn = DataBaseConnection.javaConnection();
			filePath = excelFile.getFileName();
			new DatabaseMethods1().makeScanPath(excelFile,filePath,conn);

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String realPath = ctx.getRealPath("/");

			filePath=realPath+filePath;
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if(filePath!=null || !filePath.equals(""))
		{
			String filename = filePath;
			List sheetData = new ArrayList();

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

					List data = new ArrayList();
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
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error In File Uploading.Please Check!!!"));
		}

	}



	public static void insertData2(Connection conn, ArrayList<StudentInfo> list) throws ParseException
	{
		int i=0;
		for(StudentInfo info : list)
		{
			i=insertTcDetail(conn,info.getAddNumber(), info.getAdmissionDate());
		}

		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Excel Upload Successfully done."));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
		}
	}

	private static int insertTcDetail(Connection conn,String addNumber, String issueDateStr) throws ParseException
	{
		//Connection conn = DataBaseConnection.javaConnection();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		DatabaseMethods1 obj=new DatabaseMethods1();

		Date issueDt = formatter.parse(issueDateStr);
		//Timestamp issueDt=new Timestamp(startDate.getTime());


		//DatabaseMethods1.addTCInformation("to study else where", issueDt, "0",  String.valueOf(addNumber),"GOOD","NA","0",conn);

		int number=obj.getseraialno(conn);
		String tcNumber=String.valueOf(number);

		obj.inserserialno(String.valueOf(addNumber),number,conn);
		//DatabaseMethods1.updateTCInformation("to study else where", issueDt, issueDt, issueDt, tcNumber, "issue", String.valueOf(addNumber),"GOOD","NA",conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;
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






}
