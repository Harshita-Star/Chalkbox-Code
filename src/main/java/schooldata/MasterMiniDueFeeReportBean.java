package schooldata;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@ManagedBean(name="masterMiniDueFeeReport")
@ViewScoped
public class   MasterMiniDueFeeReportBean   implements Serializable
{
	String name,firstMonthTutionFee,lastMonthTutionFee,selectedCLassSection,selectedSection,dateString;
	int totalStudent;
	ArrayList<SelectItem> classSection,sectionList,sessionList,branchList;
	ArrayList<StudentInfo> studentList,selectedStudentList;
	String regFee,tutionFee,admissionFee,examFee,annualFee,transFee,artFee,activityFee,termFee,totalFee,tutionFeePeriod,studentName,fathersName;
	StudentInfo selectedStudent;
	static int count=1;
	String selectedSession,selectedSession2,month,sectionName,className,typeMessage;
	Date date;
	String selectedMonth;
	boolean show;
	int totalAmount;
	ArrayList<SelectItem> installmentList,selectedInstallList;
	ArrayList<ClassInfo> classFeeList;
	ArrayList<FeeStatus> feeStatus;
	ArrayList<FeeInfo> feelist = new ArrayList<>();
	String message="",totalamountString;
	double totalDueAmount;
	ArrayList<StudentInfo>list;
	String[] checkMonthSelected;
	DatabaseMethods1 obj=new DatabaseMethods1();

	boolean showClass,showSchool;
	String schname,address1,address2,address3,address4,phoneno,mobileno,website,logo,finalAddress,affiliationNo,type,headerImagePath;
	String regno="",schid,branches;
	String newschid;

	public  MasterMiniDueFeeReportBean()
	{
		selectedCLassSection=selectedSection="-1";
		//Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");
		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
					newschid=String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
					newschid=newschid+","+String.valueOf(in.getValue());
				}
			}
		}
		checkMonthSelected=new String[0];
		installment();
		/*try
		{
			sessionList=obj.sessionDetail();
			classSection=obj.allClass(conn);
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
		}*/
	}


	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("13");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("15");
		installmentList.add(ss8);
	}

	public void toNum(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		XSSFRow header = sheet.getRow(4);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();



		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);




		sheet.getRow(4);
		double total=0.0;
		for(int rowInd = 4; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 5; cellInd <colCount ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);




				int status=0,counter=0,dot=0;
				Character ch[] = new Character[3000];

				String strVal = cell.getStringCellValue();

				for(int i=0;i<strVal.length();i++)
				{
					ch[i] = strVal.charAt(i);
					String s =Character.toString(ch[i]);


					if(Character.isDigit(ch[i]))
					{
						counter++;
					}
					if(s.equals("."))
					{
						dot++;
					}
					if(s.equals(""))
					{
						status=1;
					}
				}


				if(status==1)
				{
					cell.setCellValue(strVal);
				}
				else if((dot+counter)==strVal.length())
				{
					try {
						Double ds = Double.valueOf(strVal);

						if(cellInd==colCount-1)
						{
							total+=ds;
						}

						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						DataFormat fmt = book.createDataFormat();
						CellStyle sty = book.createCellStyle();
						sty.setDataFormat(fmt.getFormat("#,##0.00"));
						cell.setCellStyle(sty);
						cell.setCellValue(ds);
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}
				else
				{
					cell.setCellValue(strVal);
				}

			}
		}

		XSSFRow roo=sheet.createRow(rowCount);
		XSSFCell cell1=roo.createCell(colCount-1);
		XSSFCell cell2=roo.createCell(colCount-2);
		cell2.setCellValue("Total");
		cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
		DataFormat fmt = book.createDataFormat();
		CellStyle sty = book.createCellStyle();
		sty.setDataFormat(fmt.getFormat("#,##0.00"));
		cell1.setCellStyle(sty);
		cell1.setCellValue(total);

	}


	public void toNum1(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		XSSFRow header = sheet.getRow(4);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();



		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);




		sheet.getRow(4);
		double total=0.0;
		double[] sum = new double[colCount+1];
		for(int rowInd = 2; rowInd < rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//(rowCount);
			//(colCount);
			for(int cellInd = 5; cellInd <colCount ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);




				int status=0,counter=0,dot=0;
				Character ch[] = new Character[3000];

				String strVal = cell.getStringCellValue();

				for(int i=0;i<strVal.length();i++)
				{
					ch[i] = strVal.charAt(i);
					String s =Character.toString(ch[i]);


					if(Character.isDigit(ch[i]))
					{
						counter++;
					}
					if(s.equals("."))
					{
						dot++;
					}
					if(s.equals(""))
					{
						status=1;
					}
				}


				if(status==1)
				{
					cell.setCellValue(strVal);
				}
				else if((dot+counter)==strVal.length())
				{
					try {
						Double ds = Double.valueOf(strVal);

						if((cellInd>=5)&&(cellInd<=colCount-1))
						{
							total+=ds;
						}
						//("total"+total);
						if(rowInd==2)
						{
							sum[cellInd] = total;

						}
						else
						{
							sum[cellInd] =sum[cellInd]+total;

						}
						total=0.0;
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						DataFormat fmt = book.createDataFormat();
						CellStyle sty = book.createCellStyle();
						sty.setDataFormat(fmt.getFormat("#,##0.00"));
						cell.setCellStyle(sty);
						cell.setCellValue(ds);
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}
				else
				{
					cell.setCellValue(strVal);
				}

			}
		}


		XSSFRow roo=sheet.createRow(rowCount);

		for(int f=5;f<colCount;f++)
		{
			XSSFCell cell1=roo.createCell(f);

			cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
			DataFormat fmt = book.createDataFormat();
			CellStyle sty = book.createCellStyle();
			sty.setDataFormat(fmt.getFormat("#,##0.00"));
			cell1.setCellStyle(sty);
			cell1.setCellValue(sum[f]);
		}

	}

	public String monthName(int i)
	{
		String month="";
		if(i==4)
		{
			month="April";
		}
		else if(i==6)
		{
			month="May-June";
		}
		else if(i==8)
		{
			month="Jul-Aug";
		}
		else if(i==9)
		{
			month="September";
		}
		else if(i==11)
		{
			month="Oct-Nov";
		}
		else if(i==12)
		{
			month="December";
		}
		else if(i==1)
		{
			month="January";
		}
		else if(i==3)
		{
			month="Feb-Mar";
		}
		return month;
	}

	public void branchWiseWork()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedSection="-1";
		selectedCLassSection="-1";
		sectionList = new ArrayList<>();
		show=false;

		if(schid.equals("-1"))
		{
			showClass = false;
			showSchool=true;
			schname="B.L.M Academy";
			finalAddress="Haldwani";
			affiliationNo="";
			phoneno="";
		}
		else
		{
			showClass = true;
			showSchool=false;
			schoolInfo(schid);

			classSection=new DatabaseMethods1().allClass(schid,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessageToSelectedStudentsCustome() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{
			String cnt="",addNumber="";
			for(StudentInfo list : selectedStudentList)
			{
				if(!list.getSendMessageMobileNo().equals("0")  && !list.getSendMessageMobileNo().equals("") && String.valueOf(list.getSendMessageMobileNo()).length()==10)
				{
					if(cnt.equals(""))
					{
						cnt=list.getSendMessageMobileNo();

					}
					else
					{
						cnt=cnt+","+list.getSendMessageMobileNo();

					}

					if (addNumber.equals("")) {
						addNumber = list.getAddNumber();

					} else {
						addNumber = addNumber + "," + list.getAddNumber();
					}

				}
				//Date date=new Date();
				//DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), message, list.getFathersPhone(), date,list.getClassId(),"Student");
			}

			obj.messageurl1(cnt, message, addNumber,conn,obj.schoolId(),"");
			message="";

			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		sectionList = new ArrayList<>();
		selectedSection="-1";
		if(!selectedCLassSection.equals("-1"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			sectionList=obj.allSection(schid,selectedCLassSection,conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void searchStudent()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			
			int index=name.lastIndexOf(":")+1;
			String id=name.substring(index);

			String schoolid = DBM.schoolIdByStudentId(id,conn);
			String session=DatabaseMethods1.selectedSessionDetails(schoolid,conn);

			//SchoolInfoList list1=DBM.fullSchoolInfo(schoolid,conn);
			schoolInfo(schoolid);

			studentList=DBM.searchStudentListForDueFeeReport(schoolid,id,date,session,conn,"dueReport");

			if(studentList.size()>0)
			{
				for(StudentInfo ll:studentList)
				{

					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}
				selectedCLassSection=studentList.get(0).getClassId();
				selectedSection=studentList.get(0).getSectionid();
				feelist=DBM.classFeesForStudent(schoolid,selectedCLassSection,session,studentList.get(0).getStatus(),studentList.get(0).getConcession(),conn);
				feelist=DBM.addPreviousFee(schoolid,feelist,studentList.get(0).getAddNumber(),conn);

				className=DBM.classNameFromidSchid(schoolid,selectedCLassSection,session,conn);
				sectionName=DBM.sectionNameByIdSchid(schoolid,selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).month;
				show=true;
				String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
				stdate.split("/");
				Date extraDate=date;
				extraDate.setDate(15);
				new SimpleDateFormat("dd-MM-yy").format(extraDate);

				//message="Dear Parent, \n\nThis is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+". Kindly ignore if already paid. \n\nRegards, \n"+list1.getSchoolName()  ;
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}
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
	public String printFeeDetails()
	{
		studentName=selectedStudent.getFname();
		fathersName=selectedStudent.getFathersName();
		className=selectedStudent.getClassName();
		sectionName=selectedStudent.getSectionName();

		/*regFee=selectedStudent.getAdmissionFeeDueAmount();
		tutionFee=selectedStudent.getSchoolFeeDueAmount();
		annualFee=selectedStudent.getAnnualFeeDueAmount();
		transFee=selectedStudent.getTransportFeeDueAmount();
		examFee=selectedStudent.getExaminationFeeDueAmount();
		artFee=selectedStudent.getArtFeeDueAmount();
		termFee=selectedStudent.getTermFeeDueAmount();
		activityFee=selectedStudent.getActivityFeeDueAmount();
		totalFee=selectedStudent.getTutionFeeDueAmount();
		 */
		dateString=new SimpleDateFormat("dd-MM-yyyy").format(date);

		tutionFeePeriod=selectedStudent.getPeriod1();

		return "printDueFeeReport";
	}

	public void sendMessageToSelectedStudents() throws UnsupportedEncodingException
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(selectedStudentList.size()>0)
		{

			SchoolInfoList list1=obj.fullSchoolInfo(conn);
			String message="";
			String msg="";
			for(StudentInfo list : selectedStudentList)
			{

				if(list.getSendMessageMobileNo().length()==10)
				{
					message= "Dear "+list.getFname() + ", \n\nYour fee is due for month "+month+" is as - "+list.getTutionFeeDueAmount()+ ". \n\nRegards, \n"+list1.getSchoolName();

					msg=message;
					obj.messageurl1(list.getSendMessageMobileNo(), msg,list.getAddNumber(),conn,obj.schoolId(),"");

				}
			}
			//Date date=new Date();
			//	DataBaseMethodsSMSModule.sentMessage(list.getAddNumber(), msg, list.getFathersPhone(), date,list.getClassId(),"Student");
			message="";


			FacesContext fc=FacesContext.getCurrentInstance();
			if(selectedStudentList.size()>1)
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduents "));
			}
			else
			{
				fc.addMessage(null, new FacesMessage("You have sent message to "+selectedStudentList.size()+" stduent"));
			}

			name=null;date=null;selectedSection=null;selectedCLassSection=null;
			sectionList=null;show=false;
			selectedStudentList=null;studentList=null;
			className=sectionName=month="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No student selected,select atleast one student ", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showReport()
	{
		if(schid.equals("-1"))
		{
			searchStudentByClassSection(newschid);
		}
		else
		{
			searchStudentByClassSection(schid);
		}
	}

	public void searchStudentByClassSection(String branches)
	{
		totalDueAmount=0;
		studentList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = obj;
		try
		{
			//SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			String session=DatabaseMethods1.selectedSessionDetails(branches,conn);

			////("started : "+new Date());

			String sectionid="";
			if(selectedCLassSection.equals("-1"))
			{
				sectionid="-1";
			}
			else if(selectedSection.equals("-1"))
			{
				for(SelectItem ii : sectionList)
				{
					if(sectionid.equals(""))
					{
						sectionid = String.valueOf(ii.getValue());
					}
					else
					{
						sectionid = sectionid + "','" + String.valueOf(ii.getValue());
					}
				}
				sectionid="('"+sectionid+"')";
			}
			else
			{
				sectionid = "('"+selectedSection+"')";
			}


			list=DBM.dueFeesForblm(branches, date, sectionid, checkMonthSelected, conn);
			selectedInstallList=new ArrayList<>();

			for(int i=0;i<checkMonthSelected.length;i++)
			{
				SelectItem ll=new SelectItem();
				ll.setLabel(checkMonthSelected[i]);
				ll.setValue(monthName(Integer.parseInt(checkMonthSelected[i])));
				selectedInstallList.add(ll);
			}

			/*if(schid.equals("-1"))
			{
				ArrayList<StudentInfo> tempList = new ArrayList<>();
				for(SelectItem in : branchList)
				{
					//("Branch : "+in.getValue());
					if(Integer.parseInt(selectedMonth)>9)
					{
						tempList=DBM.searchStudentListByClassSectionForDueFeeReport11(String.valueOf(in.getValue()),sectionid,date,session,conn);
						studentList.addAll(tempList);
					}
					else
					{
						tempList=DBM.DefaulterListForMasteradmin(String.valueOf(in.getValue()),sectionid,selectedMonth,session,conn);
						studentList.addAll(tempList);

					}

				}

			}
			else
			{
				if(Integer.parseInt(selectedMonth)>9)
				{
					studentList=DBM.searchStudentListByClassSectionForDueFeeReport11(schid,sectionid,date,session,conn);
				}
				else
				{

					studentList=DBM.DefaulterListForMasteradmin(schid,sectionid,selectedMonth,session,conn);

				}

			}*/


			if(list.size()>0)
			{

				for(StudentInfo ll:list)
				{
					totalDueAmount+=ll.getTotalFee();
				}

				totalamountString=String.format ("%.0f", totalDueAmount);

				if(schid.equals("-1"))
				{
					className="All";
					sectionName="All";
				}
				else
				{
					className=DBM.classNameFromidSchid(branches,selectedCLassSection,session,conn);
					sectionName=DBM.sectionNameByIdSchid(branches,selectedSection,conn);
				}

				/*feelist=DBM.viewFeeList(conn);
				FeeInfo ff = new FeeInfo();
				ff.setFeeName("Previous Fee");
				ff.setFeeId("-1");
				ff.setFeeType("year");
				feelist.add(ff);*/ /// Can be uncommented later

				//feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);   // only for adding previous fee label




				/*totalStudent=studentList.size();
				month=studentList.get(0).month;
				String stdate=new SimpleDateFormat("dd/MMM/yy").format(date);
				String[] dd=stdate.split("/");
				Date extraDate=date;
				extraDate.setDate(15);
				String date11=new SimpleDateFormat("dd-MM-yy").format(extraDate);
				 */
				//message="Dear Parent \n This is a gentle reminder that the due date to pay the "+dd[1]+"'"+dd[2]+" fee without late fee is "+date11+".Kindly ignore if already paid \n Thanks \n"+list1.getSchoolName()  ;

				show=true;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection con = DataBaseConnection.javaConnection();
		studentList=obj.searchStudentList(branches,query,con);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void schoolInfo(String schid)
	{
		String savePath="";
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList info=new DatabaseMethods1().fullSchoolInfo(schid,conn);
		schname=info.getSchoolName();
		address1=info.getAdd1();
		address2=info.getAdd2();
		address3=info.getAdd3();
		address4=info.getAdd4();
		phoneno=info.getPhoneNo();
		mobileno=info.getMobileNo();
		website=info.getWebsite();
		type=info.getClient_type();
		if(type.equalsIgnoreCase("institute"))
		{
			type="Institute";
		}
		else if(type.equalsIgnoreCase("school"))
		{
			type="School";
		}


		if(info.getProjecttype().equals("online"))
		{
			String folderName=info.getDownloadpath();
			savePath=folderName;
		}

		logo=savePath+info.getImagePath();
		headerImagePath=savePath+info.getMarksheetHeader();
		regno=info.getRegNo();
		finalAddress=address1;

		if(address2==null || address2.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address2;
		}

		if(address3==null || address3.equals(""))
		{
		}
		else
		{
			finalAddress=finalAddress+", "+address3;
		}

		affiliationNo=address4;



		/*name="Dynamic Public School";
		address1="Alwar 301001";
		address2="Govt. Recognized";
		//address3="Run By : Smart Education & Social welfare Society (Reg. No. 143 Alwar 2009-10)";
		address4="IMG GLOBAL INFOTECH,Jai Complex, Alwar";*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
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
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getFathersName() {
		return fathersName;
	}
	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}
	public String getTutionFeePeriod() {
		return tutionFeePeriod;
	}
	public void setTutionFeePeriod(String tutionFeePeriod) {
		this.tutionFeePeriod = tutionFeePeriod;
	}
	public String getRegFee() {
		return regFee;
	}
	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}
	public String getTutionFee() {
		return tutionFee;
	}
	public void setTutionFee(String tutionFee) {
		this.tutionFee = tutionFee;
	}
	public String getAdmissionFee() {
		return admissionFee;
	}
	public void setAdmissionFee(String admissionFee) {
		this.admissionFee = admissionFee;
	}
	public String getExamFee() {
		return examFee;
	}
	public void setExamFee(String examFee) {
		this.examFee = examFee;
	}
	public String getAnnualFee() {
		return annualFee;
	}
	public void setAnnualFee(String annualFee) {
		this.annualFee = annualFee;
	}
	public String getTransFee() {
		return transFee;
	}
	public void setTransFee(String transFee) {
		this.transFee = transFee;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public ArrayList<StudentInfo> getSelectedStudentList() {
		return selectedStudentList;
	}
	public void setSelectedStudentList(ArrayList<StudentInfo> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
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
	public int getTotalStudent() {
		return totalStudent;
	}
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}
	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}
	public String getSelectedSession2() {
		return selectedSession2;
	}
	public String getSelectedSession() {
		return selectedSession;
	}
	public void setSelectedSession(String selectedSession) {
		this.selectedSession = selectedSession;
	}
	public void setSelectedSession2(String selectedSession2) {
		this.selectedSession2 = selectedSession2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public String getArtFee() {
		return artFee;
	}

	public void setArtFee(String artFee) {
		this.artFee = artFee;
	}

	public String getActivityFee() {
		return activityFee;
	}

	public void setActivityFee(String activityFee) {
		this.activityFee = activityFee;
	}

	public String getTermFee() {
		return termFee;
	}

	public void setTermFee(String termFee) {
		this.termFee = termFee;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}
	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}
	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public boolean isShowClass() {
		return showClass;
	}

	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}

	public boolean isShowSchool() {
		return showSchool;
	}

	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
	}

	public String getSchname() {
		return schname;
	}

	public void setSchname(String schname) {
		this.schname = schname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFinalAddress() {
		return finalAddress;
	}

	public void setFinalAddress(String finalAddress) {
		this.finalAddress = finalAddress;
	}

	public String getAffiliationNo() {
		return affiliationNo;
	}

	public void setAffiliationNo(String affiliationNo) {
		this.affiliationNo = affiliationNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
	}


	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}


	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}


	public String getSelectedMonth() {
		return selectedMonth;
	}


	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}


	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}


	public ArrayList<SelectItem> getSelectedInstallList() {
		return selectedInstallList;
	}


	public void setSelectedInstallList(ArrayList<SelectItem> selectedInstallList) {
		this.selectedInstallList = selectedInstallList;
	}


	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}


	public ArrayList<StudentInfo> getList() {
		return list;
	}


	public void setList(ArrayList<StudentInfo> list) {
		this.list = list;
	}

}
