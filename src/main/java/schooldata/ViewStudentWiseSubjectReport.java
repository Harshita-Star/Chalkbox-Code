package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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

import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="viewStudentWiseSubjectReport")
@ViewScoped

public class ViewStudentWiseSubjectReport implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<StudentInfo> studentList;
	ArrayList<Subjects> subjectList,finalList;
	String name,total,className,sectionName, reportFormat;
	StudentInfo selectedStudent;
	boolean show=false,showSubTable=false,showStuTable=false;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	ArrayList<SelectItem> sectionList,classSection;
	//DatabaseMethods1 dd=new DatabaseMethods1();
	String selectedSection;
	String selectedCLassSection;
	DatabaseMethodSession objSession=new DatabaseMethodSession();

	
	public ViewStudentWiseSubjectReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			classSection=new DatabaseMethods1().allClass(conn);
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
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{
		Connection conn=DataBaseConnection.javaConnection();
		studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");

		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);

		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;
	}

	public void searchStudentByName()
	{
		showStuTable=false;
		showSubTable=true;
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						subjectList=obj.allSubjectStudentWise(info.getSectionid(),info.getClassId(),conn,id,info.getSrNo());
						total=String.valueOf(subjectList.size());
						className=subjectList.get(0).getClassName();
						sectionName=info.getSectionName();
						studentList.add(info);
						show=true;
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Note: Please select student name from Autocomplete list", "Validation error"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentByClassSection()
	{
		finalList = new ArrayList<>();
		showStuTable=true;
		showSubTable=false;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();
		try
		{
			studentList=objSession.searchStudentListWithPreSessionStudent("byClassSection","", "full", conn,selectedCLassSection,selectedSection);

			if(studentList.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
			}
			else
			{
				for(StudentInfo ss: studentList)
				{
					subjectList=obj.allSubjectStudentWise(ss.getSectionid(),ss.getClassId(),conn,ss.getAddNumber(),ss.getSrNo());
					
					if(reportFormat.equalsIgnoreCase("multiRow"))
					{
						for(Subjects sb : subjectList)
						{
							sb.setSerialNumber(ss.getAddNumber());
						}
						
						finalList.addAll(subjectList);
					}
					else if(reportFormat.equalsIgnoreCase("singleRowWithType"))
					{
						Subjects s = new Subjects();
						s.setSrNo(ss.getSrNo());
						s.setClassName(ss.getClassStrName());
						s.setSectionName(ss.getSectionName());
						s.setFullName(ss.getFullName());
						s.setSerialNumber(ss.getAddNumber());
						
						String subName = "", subType = "";
						for(Subjects sb : subjectList)
						{
							if(subName.equalsIgnoreCase(""))
							{
								subType = sb.getSubjectType();
								if(sb.getSubjectType().equalsIgnoreCase("Mandatory"))
								{
									subName = sb.getSubjectName()+" (M)";
								}
								else
								{
									subName = sb.getSubjectName()+" (O)";
								}
							}
							else
							{
								subType = subType + ", " + sb.getSubjectType();
								if(sb.getSubjectType().equalsIgnoreCase("Mandatory"))
								{
									subName = subName + ", " + sb.getSubjectName()+" (M)";
								}
								else
								{
									subName = subName + ", " + sb.getSubjectName()+" (O)";
								}
							}
						}
						
						s.setSubjectName(subName);
						s.setSubjectType(subType);
						
						finalList.add(s);
					}
					else if(reportFormat.equalsIgnoreCase("singleRowNoType"))
					{
						Subjects s = new Subjects();
						s.setSrNo(ss.getSrNo());
						s.setClassName(ss.getClassStrName());
						s.setSectionName(ss.getSectionName());
						s.setFullName(ss.getFullName());
						s.setSerialNumber(ss.getAddNumber());
						
						String subName = "";
						for(Subjects sb : subjectList)
						{
							if(subName.equalsIgnoreCase(""))
							{
								subName = sb.getSubjectName();
							}
							else
							{
								subName = subName + ", " + sb.getSubjectName();
							}
						}
						
						s.setSubjectName(subName);
						
						finalList.add(s);
					}
					
				}
			}
			
			total=String.valueOf(studentList.size());
			className=obj.classNameFromidSchid(obj.schoolId(), selectedCLassSection, DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn), conn);
			sectionName= selectedSection.equals("-1") ? "All" : obj.sectionNameByIdSchid(obj.schoolId(), selectedSection, conn);
			show=true;
			
			subjectList = new ArrayList<>();
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

	public  void exportStuWiseSubPdf() throws DocumentException, IOException, FileNotFoundException {

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





		//Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =Image.getInstance(src);
			im.setAlignment(Element.ALIGN_LEFT);

			im.scaleAbsoluteHeight(60);
			im.scaleAbsoluteWidth(85);


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );

			Chunk c3 = new Chunk(im, -250, 15);

			Chunk c1 = new Chunk("              "+schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);


			Chunk c8 = new Chunk("\n                                                            Student Wise Subject Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nClass : "+className+"                              Section : "+sectionName+"                         Total : "+total+"\n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//  p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("SNo.");
			table.addCell("Subject Name");

			table.addCell("Subject Type");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<subjectList.size();i++){
				table.addCell(new Phrase(String.valueOf(subjectList.get(i).getSerialNumber()),font));
				table.addCell(new Phrase(subjectList.get(i).getSubjectName(),font));
				table.addCell(new Phrase(subjectList.get(i).getSubjectType(),font));
				//   table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersName()),font));







			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Student_Wise_Subject_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Student_Wise_Subject_Report.pdf").stream(()->isFromFirstData).build();







	}




	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Subjects> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subjects> subjectList) {
		this.subjectList = subjectList;
	}

	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
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

	public ArrayList<Subjects> getFinalList() {
		return finalList;
	}

	public void setFinalList(ArrayList<Subjects> finalList) {
		this.finalList = finalList;
	}

	public boolean isShowSubTable() {
		return showSubTable;
	}

	public void setShowSubTable(boolean showSubTable) {
		this.showSubTable = showSubTable;
	}

	public boolean isShowStuTable() {
		return showStuTable;
	}

	public void setShowStuTable(boolean showStuTable) {
		this.showStuTable = showStuTable;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}
	

}
