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

import javax.faces.application.FacesMessage;
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
import session_work.QueryConstants;

@ManagedBean(name = "categoryWiseReport")
@ViewScoped
public class CategoryWiseReport implements Serializable {
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	ArrayList<SelectItem> categoryList;
	ArrayList<StudentInfo> arrayList = new ArrayList<>();
	String selectedClass, category, userType, username;
	String selectedSection, selectedCategory;
	String className, section, total, sectionName,schId,session;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	ArrayList<StudentInfo> studentList;
	boolean b;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public CategoryWiseReport() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schId=new DatabaseMethods1().schoolId();
		session=DatabaseMethods1.selectedSessionDetails(schId, conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
			classList=obj.cordinatorClassList(empid, schId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
			classList=obj.allClassListForClassTeacher(empid,schId,conn);

		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections() {
		DatabaseMethods1 DBM = new DatabaseMethods1();
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
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			sectionList.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schId,conn);
			sectionList=new DatabaseMethods1().allSectionListForClassTeacher(empid,selectedClass,conn);
		}
		categoryList = DBM.allCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getStudentStrength() {
		DatabaseMethods1 DBM = new DatabaseMethods1();
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<String> stdColList=objStd.basicFieldsForStudentListForPromotion();

		for (SelectItem ss : categoryList) {
			if (ss.getValue().equals(selectedCategory)) {
				category = ss.getLabel();
				break;
			}
		}

		if (selectedSection.equals("-1"))
		{
			studentList =objStd.studentDetail(selectedCategory, selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION, QueryConstants.CATEGORY_WISE_STUDENT_WPS, null, null,"", QueryConstants.KEYWORD, "", "", session, schId,stdColList, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = "All";
			sectionName = "All";
		} else {
			studentList =objStd.studentDetail(selectedCategory, selectedSection, selectedClass, QueryConstants.BY_CLASS_SECTION, QueryConstants.CATEGORY_WISE_STUDENT_WPS, null, null, "", QueryConstants.KEYWORD, "", "", session, schId,stdColList, conn);
			if (studentList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No record Found", "Validation Error"));
				b = false;
			} else
				b = true;

			total = String.valueOf(studentList.size());
			className = DBM.classNameFromidSchid(DBM.schoolId(),selectedClass, session, conn);
			sectionName = DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection, conn);
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public  void exportCatWisePdf() throws DocumentException, IOException, FileNotFoundException {

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

			Chunk c3 =  null;
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
			Chunk c8 = new Chunk("\n                                                           Category Wise Class Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : "+className+"                   Section : "+sectionName+"                      Category : "+category+"                Total : "+total+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//  p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Sr. No.");

			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Mother's Name");

			table.addCell("Add. Date");
			table.addCell("Dob");
			table.addCell("Class");
			table.addCell("Current Address");

			table.addCell("Phone No.");





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
				table.addCell(new Phrase(studentList.get(i).getCurrentAddress(),font));

				table.addCell(new Phrase(String.valueOf(studentList.get(i).getClassName()+"-"+studentList.get(i).getSectionName()),font));

				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getCategory()),font));
				//     table.addCell(new Phrase(String.valueOf(studentList.get(i).getGender()),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				//    table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getAadharNo()),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getReligion()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getCaste()),font));
				//  table.addCell(new Phrase(String.valueOf(studentList.get(i).getLastSchoolName()),font));






			}


			table.setWidthPercentage(110);
			document.add(table);





		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Category_Wise_Class_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Category_Wise_Class_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
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

	public String getSelectedClass() {
		return selectedClass;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList<StudentInfo> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<StudentInfo> arrayList) {
		this.arrayList = arrayList;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
