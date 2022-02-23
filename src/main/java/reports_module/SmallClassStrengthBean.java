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
import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.studentCategoryInfo;

@ManagedBean(name = "smallClassStrength")
@ViewScoped
public class SmallClassStrengthBean implements Serializable {
	ArrayList<ClassInfo> strenghtList = new ArrayList<>();
	ArrayList<SelectItem> sectionList = new ArrayList<>();
	ArrayList<SelectItem> classlist = new ArrayList<>();
	ArrayList<studentCategoryInfo> finallist = new ArrayList<>();
	int total=0,general=0,obc=0,sc=0,st=0,sbc=0;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	int totalStudents=0;
	String schoolId,sessionValue,username,userType;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DataBaseMethodsReports objReport =new DataBaseMethodsReports();


	public SmallClassStrengthBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schoolId=obj.schoolId();
		sessionValue=obj.selectedSessionDetails(schoolId,conn);
		schoolDetails =obj.fullSchoolInfo(conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classlist=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classlist=obj.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classlist=obj.allClassListForClassTeacher(empid,schoolId,conn);

		}
		
		String classid="";
		total=0;
		int count=0;
		for(SelectItem cls:classlist)
		{
			classid= String.valueOf(cls.getValue());

			sectionList=obj.sectionNameAndIdListByid(classid,conn);
			for(SelectItem section:sectionList)
			{
				studentCategoryInfo info =new studentCategoryInfo();
				info.setClassid(classid);
				info.setClassName(cls.getLabel());
				info.setSection(section.getLabel());
				info.setSectionid(String.valueOf(section.getValue()));

				finallist.add(info);


			}
		}

		/////////////////////////////////////////////////////////////////////
		int i=1;
		for(studentCategoryInfo sc : finallist)
		{
			count=0;
			count = objReport.fetchAllValuesFromregistration(sc.getSectionid(),"all", conn);
			sc.setTotal(count);
			totalStudents += count; 
			sc.setSno(i++);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public  void exportClassStrnPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=obj.fullSchoolInfo(con);




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
			Chunk c8 = new Chunk("\n                                                             Class Strength Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);







			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");


			table.addCell("Class Name");
			table.addCell("Section");
			table.addCell("Total");








			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<finallist.size();i++){
				table.addCell(new Phrase(String.valueOf(finallist.get(i).getSno()),font));
				table.addCell(new Phrase(finallist.get(i).getClassName(),font));
				table.addCell(new Phrase(finallist.get(i).getSection(),font));
				table.addCell(new Phrase(String.valueOf(finallist.get(i).getTotal()),font));







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
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf"," Class_Strength_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Class_Strength_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}


	public ArrayList<ClassInfo> getStrenghtList() {
		return strenghtList;
	}



	public void setStrenghtList(ArrayList<ClassInfo> strenghtList) {
		this.strenghtList = strenghtList;
	}



	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}



	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}



	public ArrayList<SelectItem> getClasslist() {
		return classlist;
	}



	public void setClasslist(ArrayList<SelectItem> classlist) {
		this.classlist = classlist;
	}



	public ArrayList<studentCategoryInfo> getFinallist() {
		return finallist;
	}



	public void setFinallist(ArrayList<studentCategoryInfo> finallist) {
		this.finallist = finallist;
	}



	public int getGeneral() {
		return general;
	}



	public void setGeneral(int general) {
		this.general = general;
	}



	public int getObc() {
		return obc;
	}



	public void setObc(int obc) {
		this.obc = obc;
	}



	public int getSc() {
		return sc;
	}



	public void setSc(int sc) {
		this.sc = sc;
	}



	public int getSt() {
		return st;
	}



	public void setSt(int st) {
		this.st = st;
	}



	public int getSbc() {
		return sbc;
	}



	public void setSbc(int sbc) {
		this.sbc = sbc;
	}



	public DatabaseMethods1 getObj() {
		return obj;
	}



	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}



	public StreamedContent getFile() {
		return file;
	}



	public void setFile(StreamedContent file) {
		this.file = file;
	}



	public int getTotalStudents() {
		return totalStudents;
	}



	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}
	public int getTotal() {
		return total;
	}



	public void setTotal(int total) {
		this.total = total;
	}



	

}