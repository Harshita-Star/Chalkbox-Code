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
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
import session_work.QueryConstants;
import student_module.RegistrationColumnName;
@ManagedBean(name="ageWiseReport")
@SessionScoped
public class AgeWiseReport implements Serializable
{
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	ArrayList<StudentInfo> arrayList=new ArrayList<>();
	String selectedClass;
	ArrayList<Age>ag=new ArrayList<>();
	Age age;

	String selectedSection;
	String className,section,total,sectionName,username, userType, schoolid;
	ArrayList<StudentInfo> studentList;
	boolean b;
	Date date=new Date();
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	/*public AgeWiseReport()
	   {
		   Connection conn = DataBaseConnection.javaConnection();
		   classList=new DatabaseMethods1().allClass(conn);
		   try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	   }*/

	@PostConstruct
	public void init()
	{
		className=sectionName=total=selectedClass=selectedSection="";
		date=null;
		b=false;
		studentList = new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=new DatabaseMethods1().allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=new DatabaseMethods1().cordinatorClassList(empid, schoolid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=new DatabaseMethods1().allClassListForClassTeacher(empid,schoolid,conn);

		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
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
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void getStudentStrength()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		String schid=DBM.schoolId();

		ArrayList<String> list=basicFieldsForStudentList();
		studentList=new DataBaseMethodStudent().studentDetail("",selectedSection,selectedClass,QueryConstants.BY_CLASS_SECTION,QueryConstants.CLASS_STRENGTH,null,null,"",QueryConstants.KEYWORD,"","",session, schid, list, conn);

		if (studentList.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			b = false;
		}
		else
		{
			for(StudentInfo ss:studentList)
			{
				if(ss.getDob()!=null)
				{
					age=ageCalculator(ss.getDob(),date);
					ss.setAge(String.valueOf(age.getYears()+" year "+age.getMonths()+" months "+age.days+" days "));
				}
			}
			b = true;
		}
		total = String.valueOf(studentList.size());
		if (selectedSection.equals("-1"))
		{
			className = "All";
			sectionName = "All";
		}
		else
		{
			className=DBM.classNameFromidSchid(DBM.schoolId(),selectedClass,session,conn);
			sectionName=DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection,conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Age ageCalculator(Date dob, Date date)
	{
		int years = 0;
		int months = 0;
		int days = 0;

		Calendar birthDay = Calendar.getInstance();
		birthDay.setTimeInMillis(dob.getTime());

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(date.getTime());

		years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
		int currMonth = now.get(Calendar.MONTH) + 1;
		int birthMonth = birthDay.get(Calendar.MONTH) + 1;

		months = currMonth - birthMonth;

		if (months < 0)
		{
			years--;
			months = 12 - birthMonth + currMonth;
			if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
				months--;
		} else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
		{
			years--;
			months = 11;
		}

		if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
			days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
		else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
		{
			int today = now.get(Calendar.DAY_OF_MONTH);
			now.add(Calendar.MONTH, -1);
			days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
		} else
		{
			days = 0;
			if (months == 12)
			{
				years++;
				months = 0;
			}
		}

		return new Age(days, months, years);


	}

	public  void exportAgeWisePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();



		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);


		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			
			Image im = null;
			try {
				 im  =Image.getInstance(src);
					im.setAlignment(Element.ALIGN_LEFT);

					im.scaleAbsoluteHeight(60);
					im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = null;
			try {
				c3  = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                               Age Wise Class Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : "+className+"                                   Section : "+sectionName+"                                 Total : "+total+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//  p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");

			table.addCell("Add. Date");
			table.addCell("Dob");
			table.addCell("Category");
			table.addCell("Age");

			table.addCell("Address");

			table.addCell("Phone");





			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<studentList.size();i++){
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSno()),font));
				table.addCell(new Phrase(studentList.get(i).getSrNo(),font));
				table.addCell(new Phrase(studentList.get(i).getFullName(),font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(),font));
				table.addCell(new Phrase(studentList.get(i).getMotherName(),font));
				table.addCell(new Phrase(studentList.get(i).getAdmissionDate(),font));
				table.addCell(new Phrase(studentList.get(i).getDobString(),font));
				table.addCell(new Phrase(studentList.get(i).getCategory(),font));

				table.addCell(new Phrase(studentList.get(i).getAge(),font));
				table.addCell(new Phrase(studentList.get(i).getCurrentAddress(),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				//    table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getAadharNo()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getReligion()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getCaste()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getLastSchoolName()),font));






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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Age_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Age_Report.pdf").stream(()->isFromFirstData).build();






	}
	
	public ArrayList<String> basicFieldsForStudentList()
	{
		String keyword=QueryConstants.KEYWORD;
		ArrayList<String> list = new ArrayList<String>();
		list.add(keyword+RegistrationColumnName.ADMISSION_NUMBER);
		list.add(keyword+RegistrationColumnName.SERIAL_NUMBER);
		list.add(keyword+RegistrationColumnName.FATHERS_NAME);
		list.add(keyword+RegistrationColumnName.STUDENT_NAME);
		list.add(keyword+RegistrationColumnName.DOB);
		list.add(keyword+RegistrationColumnName.MOTHERS_NAME);
		list.add(keyword+RegistrationColumnName.CATEGORY_ID);
		list.add(keyword+RegistrationColumnName.CURRENT_ADDRESS);
		list.add(keyword+RegistrationColumnName.FATHERS_PHONE);
		list.add(keyword+RegistrationColumnName.ADMISSION_DATE);
		
		return list;
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
	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}
	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}


	public ArrayList<Age> getAg() {
		return ag;
	}
	public void setAg(ArrayList<Age> ag) {
		this.ag = ag;
	}
	public Age getAge() {
		return age;
	}
	public void setAge(Age age) {
		this.age = age;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
