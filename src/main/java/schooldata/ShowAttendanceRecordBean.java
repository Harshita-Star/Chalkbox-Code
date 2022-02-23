package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;

@ManagedBean(name = "showAttendance")
@ViewScoped
public class ShowAttendanceRecordBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection, sectionList;
	String selectedSection, monthName, className, sectionName;
	boolean showAttendanceDetail;
	StudentInfo studentName;
	String allPresentsInString, userType,schoolid,staff;
	String allAbsentsInString;
	String allLeavesInString;
	StudentInfo info;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethodSession objSession=new DatabaseMethodSession();


	String totalpresent, totalabsent, totalholidays;

	boolean showStudentRecord;
	ArrayList<DayList> days = new ArrayList<>();

	ArrayList<SelectItem> yearList;
	ArrayList<SelectItem> classList;
	String selectedClass;
	String selectedYear;

	ArrayList<StudentInfo> studentDetails;

	ArrayList<SelectItem> monthList;
	String selectedMonth;

	public void allSections() {
		Connection conn = DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ShowAttendanceRecordBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		HttpSession ses=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff=(String) ses.getAttribute("username");
		userType=(String)ses.getAttribute("type");
		schoolid=(String) ses.getAttribute("schoolid");
		
		try 
		{
			String empid = "";
			if(userType.equalsIgnoreCase("admin")
					|| userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff") 
					|| userType.equalsIgnoreCase("Accounts"))
			{
				classSection = new DatabaseMethods1().allClass(conn);
			}
			else if (userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
 			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
 				classSection = new DatabaseMethods1().cordinatorClassList(empid, schoolid, conn);
 			}
			else
			{
				empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolid,conn);
				classSection=new DatabaseMethods1().allClassListForClassTeacher(empid,schoolid,conn);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		allYear();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<SelectItem> getMonths(int val,String pre){
		ArrayList<SelectItem> months = new ArrayList<>();
		ArrayList<SelectItem> finalMonths = new ArrayList<>();

		SelectItem item = new SelectItem();
		item.setLabel("January");
		item.setValue("1");
		months.add(item);

		item = new SelectItem();
		item.setLabel("February");
		item.setValue("2");
		months.add(item);

		item = new SelectItem();
		item.setLabel("March");
		item.setValue("3");
		months.add(item);

		item = new SelectItem();
		item.setLabel("April");
		item.setValue("4");
		months.add(item);

		item = new SelectItem();
		item.setLabel("May");
		item.setValue("5");
		months.add(item);

		item = new SelectItem();
		item.setLabel("June");
		item.setValue("6");
		months.add(item);

		item = new SelectItem();
		item.setLabel("July");
		item.setValue("7");
		months.add(item);

		item = new SelectItem();
		item.setLabel("August");
		item.setValue("8");
		months.add(item);

		item = new SelectItem();
		item.setLabel("September");
		item.setValue("9");
		months.add(item);

		item = new SelectItem();
		item.setLabel("October");
		item.setValue("10");
		months.add(item);

		item = new SelectItem();
		item.setLabel("November");
		item.setValue("11");
		months.add(item);

		item = new SelectItem();
		item.setLabel("December");
		item.setValue("12");
		months.add(item);

		if(pre.equalsIgnoreCase("first")) {
			for(int i = val ;i<=12;i++) {
				for(SelectItem x:months) 
				{
					if(Integer.valueOf(x.getValue().toString())==i) {
						finalMonths.add(x);
					}
				}
			}
		}else if(pre.equalsIgnoreCase("end")) {
			for(int i = 1 ;i<=val;i++) {
				for(SelectItem x:months) 
				{
					if(Integer.valueOf(x.getValue().toString())==i) {
						finalMonths.add(x);
					}
				}
			}
		}
		
		return finalMonths;
	}
	
	
	public void allMonths() {
		Connection conn = DataBaseConnection.javaConnection();
		String sessionMonths = new DatabaseMethods1().fullSchoolInfo(conn).getSchoolSession();
		String session = new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(), conn);
		String []arr = session.split("-");
		String mn[] = sessionMonths.split("-");
		if(selectedYear.equalsIgnoreCase(arr[0].trim())) {
			monthList = getMonths(Integer.valueOf(mn[0].trim()),"first");
		}else {
			monthList = getMonths(Integer.valueOf(mn[1].trim()),"end");
		}
	}
	
	public void allYear() {
		Connection conn = DataBaseConnection.javaConnection();
		String session = new DatabaseMethods1().selectedSessionDetails(new DatabaseMethods1().schoolId(), conn);
		String []ar = session.split("-");
		Date dt = new Date();
		yearList = new ArrayList<>();
		for (int i = 0; i <= (ar.length)-1; i++) {
			SelectItem item = new SelectItem();
			item.setLabel(String.valueOf(ar[i]).trim());
			item.setValue(String.valueOf(ar[i]).trim());
			yearList.add(item);
		}
	}
	
	

	public void studentDetail() {
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		String session = DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		monthName = DBM.monthNameByNumber(Integer.valueOf(selectedMonth));
		className = DBM.classNameFromidSchid(DBM.schoolId(),selectedClass, session, conn);
		sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
		try {
			//studentDetails = DBM.searchStudentListByClassSection(selectedSection, conn);
			studentDetails=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedClass,selectedSection);

			int year = Integer.parseInt(selectedYear);
			int flag = 0;
			if (year % 4 == 0) {
				if (year % 100 == 0) {
					if (year % 400 == 0) {
						flag = 1;
					}
				} else {
					flag = 1;
				}
			}

			int temp = 31;
			if (selectedMonth.equals("4") || selectedMonth.equals("6") || selectedMonth.equals("9")
					|| selectedMonth.equals("11"))
				temp = 30;

			else if (selectedMonth.equals("2")) {
				if (flag == 1) {
					temp = 29;
				} else {
					temp = 28;
				}
			}

			days = new ArrayList<>();
			for (int i = 1; i <= temp; i++) {
				DayList list1 = new DayList();
				list1.setDate(String.valueOf(i));

				days.add(list1);
			}
			DatabaseMethods1.allAttendanceForRecord(selectedSection, selectedMonth, studentDetails, selectedYear,temp, conn);
			

			showStudentRecord = true;
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

	public void studentDetailWise() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String addNumber = (String) ss.getAttribute("username");
			studentDetails = new DatabaseMethods1().allStudentListbyAddNumber(addNumber, conn);

			int year = Integer.parseInt(selectedYear);
			int flag = 0;
			if (year % 4 == 0) {
				if (year % 100 == 0) {
					if (year % 400 == 0) {
						flag = 1;
					}
				} else {
					flag = 1;
				}
			}

			int temp = 31;
			if (selectedMonth.equals("4") || selectedMonth.equals("6") || selectedMonth.equals("9")
					|| selectedMonth.equals("11"))
				temp = 30;

			else if (selectedMonth.equals("2")) {
				if (flag == 1) {
					temp = 29;
				} else {
					temp = 28;
				}
			}

			days = new ArrayList<>();
			for (int i = 1; i <= temp; i++) {
				DayList list1 = new DayList();
				list1.setDate(String.valueOf(i));

				days.add(list1);
			}

			DatabaseMethods1.allAttendanceForRecord(selectedSection, selectedMonth, studentDetails, selectedYear, temp, conn);

			showStudentRecord = true;
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

	public  void exportShowAttPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im=null;
			try {
				im =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				// TODO: handle exception
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 =null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				// TODO: handle exception
			}
			 

			Chunk c1 = new Chunk(  "              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		try {
			
			Chunk c8 = new Chunk("\n                                                          Monthly Attendence Report-"+monthName+""+selectedYear+"\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n                Class : "+className+"                                                                    Section : "+sectionName+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			p8.setAlignment(Element.ALIGN_CENTER);



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5);
			PdfPTable table = new PdfPTable(3+days.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("Sno.",font));
			table.addCell(new Phrase("Ad. No.",font));

			table.addCell(new Phrase("Student Name",font));

			for(int i=0;i<days.size();i++) {
				table.addCell(new Phrase(String.valueOf(days.get(i).getDate()),font));

			}



			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentDetails.size();i++){

				table.addCell(new Phrase(String.valueOf(studentDetails.get(i).getSno()),font));
				table.addCell(new Phrase(String.valueOf(studentDetails.get(i).getSrNo()),font));
				table.addCell(new Phrase(studentDetails.get(i).getFullName(),font));

				for(int j=0;j<days.size();j++) {
					table.addCell(new Phrase(studentDetails.get(i).attendanceMap.get(days.get(j).getDate()),font));

				}

			}


			table.setWidthPercentage(110);
			document.add(table);





		} 
catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Monthly_Attendence_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Monthly_Attendence_Report.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}

	public boolean isShowAttendanceDetail() {
		return showAttendanceDetail;
	}

	public void setShowAttendanceDetail(boolean showAttendanceDetail) {
		this.showAttendanceDetail = showAttendanceDetail;
	}

	public StudentInfo getStudentName() {
		return studentName;
	}

	public void setStudentName(StudentInfo studentName) {
		this.studentName = studentName;
	}

	public String getAllPresentsInString() {
		return allPresentsInString;
	}

	public void setAllPresentsInString(String allPresentsInString) {
		this.allPresentsInString = allPresentsInString;
	}

	public String getAllAbsentsInString() {
		return allAbsentsInString;
	}

	public void setAllAbsentsInString(String allAbsentsInString) {
		this.allAbsentsInString = allAbsentsInString;
	}

	public String getAllLeavesInString() {
		return allLeavesInString;
	}

	public void setAllLeavesInString(String allLeavesInString) {
		this.allLeavesInString = allLeavesInString;
	}

	public StudentInfo getInfo() {
		return info;
	}

	public void setInfo(StudentInfo info) {
		this.info = info;
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

	public ArrayList<DayList> getDays() {

		return days;
	}

	public void setDays(ArrayList<DayList> days) {
		this.days = days;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public String getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	public boolean isShowStudentRecord() {
		return showStudentRecord;
	}

	public void setShowStudentRecord(boolean showStudentRecord) {
		this.showStudentRecord = showStudentRecord;
	}
	public ArrayList<StudentInfo> getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(ArrayList<StudentInfo> studentDetails) {
		this.studentDetails = studentDetails;
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

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public String getTotalpresent() {
		return totalpresent;
	}

	public void setTotalpresent(String totalpresent) {
		this.totalpresent = totalpresent;
	}

	public String getTotalabsent() {
		return totalabsent;
	}

	public void setTotalabsent(String totalabsent) {
		this.totalabsent = totalabsent;
	}

	public String getTotalholidays() {
		return totalholidays;
	}

	public void setTotalholidays(String totalholidays) {
		this.totalholidays = totalholidays;
	}


	static public class DayList implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		String date, value;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
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

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

}
