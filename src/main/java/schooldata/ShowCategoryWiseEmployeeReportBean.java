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
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

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

@ManagedBean(name = "showCategoryWiseEmployeeReport")
@ViewScoped

public class ShowCategoryWiseEmployeeReportBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	String category, session;
	int total;
	String selectedCategory;
	ArrayList<EmployeeInfo> employeeList;
	boolean show;
	ArrayList<SelectItem> categoryList;
	EmployeeInfo selectedEmployee;
	String name;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;

	public ShowCategoryWiseEmployeeReportBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		categoryList = new DatabaseMethods1().allEmployeeCategory(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<String> autoCompleteStudentInfo(String query) {
		Connection conn = DataBaseConnection.javaConnection();
		employeeList = new DatabaseMethods1().searchEmployeebyName(query, conn);
		List<String> studentListt = new ArrayList<>();

		for (EmployeeInfo info : employeeList) {
			studentListt.add(info.getFname() + " " + info.getLname() + "-" + info.getId());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}


	public void searchByCategory() {
		Connection conn = DataBaseConnection.javaConnection();
		try {
			employeeList = new DatabaseMethods1().searchEmployeebyCategorySchidd(new DatabaseMethods1().schoolId(), selectedCategory, conn);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}


		show = true;
		if(selectedCategory.equalsIgnoreCase("All"))
		{
			category="All";
		}
		else
		{
			category=new DatabaseMethods1().employeeCategoryByIdSchid(new DatabaseMethods1().schoolId(),selectedCategory,conn);
		}
		total = employeeList.size();
		session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),conn);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public  void exportEmpPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		try {
			employeeList = new DatabaseMethods1().searchEmployeebyCategorySchidd(new DatabaseMethods1().schoolId(), selectedCategory, con);
		} catch (Exception ex) {
			ex.printStackTrace();
		}


		show = true;
		for (EmployeeInfo info : employeeList) {
			category = info.getCategory();

		}
		total = employeeList.size();
		session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(),con);
		if(selectedCategory.equalsIgnoreCase("All"))
		{
			category="All";
		}
		else
		{
			category=new DatabaseMethods1().employeeCategoryByIdSchid(new DatabaseMethods1().schoolId(),selectedCategory,con);
		}
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(con);


		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();



		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			
			new Font(FontFamily.HELVETICA, 12, Font.UNDERLINE);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

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
 
		try
		{
			Chunk c8 = new Chunk("\n                                                        Category Wise Employee Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			//           PdfPTable tableHead = new PdfPTable(new float[] { 0.3f});
			//
			//              tableHead.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			//
			//              PdfPCell celHead = new PdfPCell();
			//              celHead.addElement(new Phrase("Category Wise Employee Report"));
			//              celHead.setBorderWidthTop(0);
			//              celHead.setBorderWidthLeft(0);
			//              celHead.setBorderWidthRight(0);
			//                  tableHead.addCell(celHead);
			//
			//                  tableHead.getDefaultCell().setBorderWidthTop(0);
			//                  document.add(tableHead);

			Chunk c7 = new Chunk("\n Category : "+category+"                           Section : "+session+"                            Total : "+total+"\n\n",fo );
			Paragraph p7 = new Paragraph();
			p7.add(c7);
			document.add(p7);
			//   Date dtf = new Date();



			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
			PdfPTable table = new PdfPTable(new float[] { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Emp.ID");
			table.addCell("Emp. Name");
			table.addCell("Sub Category");
			table.addCell("Spouse Name");
			table.addCell("Father Name");
			table.addCell("Salary");
			table.addCell("Address");
			table.addCell("Date of joining");
			table.addCell("Date of Quit");
			table.addCell("Birth Date");
			table.addCell("Qualification");
			table.addCell("Contact(1)");
			table.addCell("Contact(2)");
			table.addCell("Email");
			table.addCell("Pan No.");
			table.addCell("Aadhar No.");


			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<employeeList.size();i++){


				table.addCell(new Phrase(String.valueOf(employeeList.get(i).getId()),font));

				table.addCell(new Phrase(employeeList.get(i).getFname(),font));
				table.addCell(new Phrase(employeeList.get(i).getSubCateg(),font));

				table.addCell(new Phrase(employeeList.get(i).getSpouseName(),font));
				table.addCell(new Phrase(employeeList.get(i).getLname() ,font));

				table.addCell(new Phrase(String.valueOf(employeeList.get(i).getSalary()),font));
				table.addCell(new Phrase(employeeList.get(i).getAddress(),font));

				table.addCell(new Phrase(employeeList.get(i).getJoiningDateStr(),font));
				table.addCell(new Phrase(employeeList.get(i).getLeavingDateStr() ,font));


				table.addCell(new Phrase(employeeList.get(i).getDobStr(),font));
				table.addCell(new Phrase(employeeList.get(i).getQualification(),font));

				table.addCell(new Phrase(String.valueOf(employeeList.get(i).getMobile()),font));
				table.addCell(new Phrase(employeeList.get(i).getMobile2(),font));
				table.addCell(new Phrase(employeeList.get(i).getEmail(),font));
				table.addCell(new Phrase(employeeList.get(i).getPanNo(),font));
				table.addCell(new Phrase(employeeList.get(i).getAadhaarNo(),font));
			}


			table.setWidthPercentage(110);
			document.add(table);





		}
		catch (Exception e) {
			// TODO: handle exception
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Category_Wise_Employee_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Category_Wise_Employee_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}


	}



	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}


	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
