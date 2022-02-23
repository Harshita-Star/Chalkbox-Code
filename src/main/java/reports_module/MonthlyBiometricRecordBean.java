package reports_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.EmpInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="monthlyBiometricRecord")
@ViewScoped

public class MonthlyBiometricRecordBean implements Serializable
{
	ArrayList<SelectItem> yearList;
	String selectedYear,selectedMonth;
	ArrayList<SelectItem> monthList;
	String monthYear;
	ArrayList<EmpInfo> teacherDetails = new ArrayList<>();
	int monthDays,sundays,holidays;
	transient StreamedContent file;
	String basis="";
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,session;
	
	public MonthlyBiometricRecordBean() 
	{
		allMonths();
		allYear();
		Connection conn = DataBaseConnection.javaConnection();

		schoolId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Date dt = new Date();
		selectedMonth = String.valueOf(dt.getMonth() + 1);
		selectedYear = String.valueOf(dt.getYear() + 1900);
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String type = (String) ss.getAttribute("biometricReportBasis");
		if(type.equalsIgnoreCase("monthly"))
		{
			show();
		}
		else
		{
			showYearly();
		}
	}
	
	public void show()
	{
		basis = "monthYear";
		Connection conn = DataBaseConnection.javaConnection();
	
		
		monthYear = dbm.monthNameByNumber(Integer.valueOf(selectedMonth))+"-"+selectedYear;
		
		countWeekendDays(Integer.valueOf(selectedYear), Integer.valueOf(selectedMonth));
		holidays=dbr.countTotalHolidaysMonthYearWise(basis,schoolId,selectedMonth,selectedYear,conn);
		teacherDetails=dbm.allEmpInfo(conn);
		try
		{
			int dd = 0;
			for(EmpInfo tt :teacherDetails)
			{
				tt.setMonthDays(monthDays);
				tt.setSundays(sundays);
				tt.setHolidays(holidays);
				tt.setWorkingDays(monthDays-(sundays+holidays));
				dd=0;
				if(tt.getBioCode()!=null && !tt.getBioCode().equals("") && tt.getBioDeviceCode()!=null && !tt.getBioDeviceCode().equals(""))
				{
					dd = dbr.countTotalStaffPunchMonthYearWise(basis,selectedMonth,selectedYear,tt.getBioCode(),tt.getBioDeviceCode(),conn);
				}
				tt.setPresent(dd);
				tt.setAbsent(tt.getWorkingDays()-tt.getPresent());
				
			}
		}
		catch (Exception e) 
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
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showYearly()
	{
		basis="year";
		Connection conn = DataBaseConnection.javaConnection();
		
		monthYear = selectedYear;
		
		countWeekendDaysYear(Integer.valueOf(selectedYear));
		holidays=dbr.countTotalHolidaysMonthYearWise(basis,schoolId,"",selectedYear,conn);
		teacherDetails=dbm.allEmpInfo(conn);
		try
		{
			int dd = 0;
			for(EmpInfo tt :teacherDetails)
			{
				tt.setMonthDays(monthDays);
				tt.setSundays(sundays);
				tt.setHolidays(holidays);
				tt.setWorkingDays(monthDays-(sundays+holidays));
				dd=0;
				if(tt.getBioCode()!=null && !tt.getBioCode().equals("") && tt.getBioDeviceCode()!=null && !tt.getBioDeviceCode().equals(""))
				{
					dd = dbr.countTotalStaffPunchMonthYearWise(basis,"",selectedYear,tt.getBioCode(),tt.getBioDeviceCode(),conn);
				}
				tt.setPresent(dd);
				tt.setAbsent(tt.getWorkingDays()-tt.getPresent());
				
			}
		}
		catch (Exception e) 
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
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void countWeekendDays(int year, int month) {
	    Calendar calendar = Calendar.getInstance();
	    // Note that month is 0-based in calendar, bizarrely.
	    calendar.set(year, month - 1, 1);
	    monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    int count = 0;
	    for (int day = 1; day <= monthDays; day++) {
	        calendar.set(year, month - 1, day);
	        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	        if (dayOfWeek == Calendar.SUNDAY) {
	            count+=1;
	        }
	    }
	    sundays=count;
	}
	
	public void countWeekendDaysYear(int year) {
	    Calendar calendar = Calendar.getInstance();
	    // Note that month is 0-based in calendar, bizarrely.
	    calendar.set(year, 0, 1);
	    monthDays = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	    int count = 0;
	    for (int day = 1; day <= monthDays; day++) {
	        calendar.set(year, 0, day);
	        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	        if (dayOfWeek == Calendar.SUNDAY) {
	            count+=1;
	        }
	    }
	    sundays=count;
	}
	
	public void allMonths() {
		monthList = new ArrayList<>();

		SelectItem item = new SelectItem();
		item.setLabel("January");
		item.setValue("1");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("February");
		item.setValue("2");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("March");
		item.setValue("3");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("April");
		item.setValue("4");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("May");
		item.setValue("5");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("June");
		item.setValue("6");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("July");
		item.setValue("7");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("August");
		item.setValue("8");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("September");
		item.setValue("9");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("October");
		item.setValue("10");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("November");
		item.setValue("11");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("December");
		item.setValue("12");
		monthList.add(item);

	}

	public void allYear() {
		Date dt = new Date();
		yearList = new ArrayList<>();
		for (int i = 2017; i <= (dt.getYear() + 1900); i++) {

			SelectItem item = new SelectItem();
			item.setLabel(String.valueOf(i));
			item.setValue(String.valueOf(i));
			yearList.add(item);
		}
	}
	
	public  void exportPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbm.fullSchoolInfo(con);

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
				e.printStackTrace();
			}
			

			Chunk c = new Chunk(ls.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk(  ls.add1+ " " +ls.add2+"                \n\n",fo);

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
			Chunk c8 = new Chunk("\n                                                           Staff Biometric Record "+monthYear+" \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8);
			PdfPTable table = new PdfPTable(9);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("S.No.");
			table.addCell("Name");
			table.addCell("Category");
			table.addCell("Sub Category");
			
			table.addCell("Total Days");
			table.addCell("Total Sundays");
			table.addCell("Total Other Holidays");
			table.addCell("Total Working Days");
			table.addCell("Total Present");
			//table.addCell("Total Absent");

			table.setHeaderRows(1);
			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(1);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<teacherDetails.size();i++)
			{
				table.addCell(new Phrase(String.valueOf(i+1),font));
				table.addCell(new Phrase(teacherDetails.get(i).getName(),font));
				table.addCell(new Phrase(teacherDetails.get(i).getCategory(),font));
				table.addCell(new Phrase(teacherDetails.get(i).getSubCateg(),font));
				
				table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getMonthDays()),font));
				table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getSundays()),font));
				table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getHolidays()),font));
				table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getWorkingDays()),font));
				table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getPresent()),font));
				//table.addCell(new Phrase(String.valueOf(teacherDetails.get(i).getAbsent()),font));
				
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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","StaffBiometricRecord_"+monthYear+".pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("StaffBiometricRecordRecord_"+monthYear+".pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
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

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public ArrayList<EmpInfo> getTeacherDetails() {
		return teacherDetails;
	}

	public void setTeacherDetails(ArrayList<EmpInfo> teacherDetails) {
		this.teacherDetails = teacherDetails;
	}

	public String getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}
	
	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
}
