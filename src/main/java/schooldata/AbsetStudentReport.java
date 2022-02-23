package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
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

@ManagedBean(name="absetStudentReport")
@ViewScoped
public class AbsetStudentReport implements Serializable{

	ArrayList<StudentInfo> studentList;
	boolean b;
	Date date=new Date();
	String total,strDate,balMsg,userType,staff;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> sectionList;
	String selectedClass="";
	String selectedSection="";
	String className,section;
	double smsLimit;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	DatabaseMethods1 DBM= new DatabaseMethods1();
	String schoolId,session;
	DataBaseMeathodJson objJson = new DataBaseMeathodJson();


	public AbsetStudentReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolDetails =DBM.fullSchoolInfo(conn);
		userType = (String) httpSession.getAttribute("type");
		staff=(String) httpSession.getAttribute("username");
        schoolId = DBM.schoolId();
        session = DBM.selectedSessionDetails(schoolId,conn);

		date=new Date();
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
			si.setValue("all");
			classList.add(si);

			ArrayList<SelectItem> temp = DBM.allClass(conn);

			if(temp.size()>0)
			{
				classList.addAll(temp);
			}
			selectedClass="all";
			searchData();
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
 					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolId,conn);
			classList = DBM.cordinatorClassList(empid, schoolId, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolId,conn);
			classList = DBM.allClassListForClassTeacher(empid, schoolId, conn);
		}
		smsLimit = DBM.smsLimitReminder(schoolId, conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		b=false;
		Connection conn=DataBaseConnection.javaConnection();
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			sectionList = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			sectionList.add(si);

			ArrayList<SelectItem> temp = DBM.allSection(selectedClass,conn);

			if(temp.size()>0)
			{
				sectionList.addAll(temp);
			}
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(staff,schoolId,conn);
			sectionList = DBM.allSectionListForClassTeacher(empid, selectedClass, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void check()
	{
		b=false;
	}


	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();

		strDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
		if(selectedClass.equalsIgnoreCase("all"))
		{
			className="All";
			section="";
			studentList=DBM.allbasentAttendance(date,conn);
			if(studentList.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No record Found"));
				b=false;
			}
			else
			{
				b=true;
			}
		}
		else
		{
			className=DBM.classNameFromidSchid(schoolId,selectedClass, session, conn);
			if(selectedSection.equalsIgnoreCase("all"))
			{
				section="All";
			}
			else
			{
				section = DBM.sectionNameByIdSchid(schoolId,selectedSection, conn);
			}

			section=" - "+section;

			studentList=DBM.allbasentAttendanceClassWise(date,selectedSection,selectedClass,conn);
			if(studentList.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No record Found"));
				b=false;
			}
			else
			{
				b=true;
			}
		}


		PrimeFaces.current().ajax().update(":form");

		total=String.valueOf(studentList.size());
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String sendAbsentMessage()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList ls=DBM.fullSchoolInfo(conn);
		
		if(ls.getCountry().equalsIgnoreCase("India"))
		{
			double balance = DBM.smsBalance(schoolId, conn);
			if(balance >0 && balance <= smsLimit)
			{
				balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
						+ "We suggest you to top-up your account today to ensure uninterrupted activity";
				if(userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
					PrimeFaces.current().ajax().update("MsgLimitForm");
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return "";
				}
			}
			else if(balance <= 0)
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
				if(userType.equalsIgnoreCase("admin"))
				{
					PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
					PrimeFaces.current().ajax().update("MsgOverForm");
				}
				else
				{
					PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
					PrimeFaces.current().ajax().update("MsgOtherForm");
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}

		String dateee=new SimpleDateFormat("dd/MM/yyyy").format(date);
		String tdy  = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		boolean flag=false;

		if(dateee.equals(tdy))
		{
			for(StudentInfo info : studentList)
			{
				if(info.getMessagesend().equalsIgnoreCase("no") && info.getAttendance().equalsIgnoreCase("A"))
				{
					flag=true;
					String mess="Dear Parent,\nYour Ward "+info.getFullName()+" is absent on date:"+dateee+" from school.\nRegards,"+ ls.getSmsSchoolName();
					if(ls.getCountry().equalsIgnoreCase("India"))
					{
						if (String.valueOf(info.getFathersPhone()).length() == 10
								&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
						{
							String templateId=objJson.templateId(ls.getKey(),"ABSENT",conn);
							objJson.messageUrlWithTemplate(String.valueOf(info.getFathersPhone()), mess,info.getAddNumber(),schoolId,conn,templateId);
							
						//	DBM.messageurl1(String.valueOf(info.getFathersPhone()), mess,info.getAddNumber(),conn,schoolId);
							//obj.updateMessageStatusInAttendance(info.getAttId(), conn);
							objJson.addMessageStatus(info.getAddNumber(), date,schoolId, conn);
						}
					}
					else
					{
						if(info.getFathersPhone()==info.getMothersPhone())
						{
							objJson.notification("ABSENT",mess,info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
						}
						else
						{
							objJson.notification("ABSENT",mess,info.getFathersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
							objJson.notification("ABSENT",mess,info.getMothersPhone()+"-"+info.getAddNumber()+"-"+schoolId,schoolId,"",conn);
						}
						objJson.addMessageStatus(info.getAddNumber(), date,schoolId, conn);
					}
				}
			}

			if(flag==true)
			{
				searchData();

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Messages sent to students."));
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("You have already sent the absent alert."));
			}

		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Sorry , You Cannot Send Alerts for Previous Date."));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void sendMsg()
	{
		Connection conn=DataBaseConnection.javaConnection();
		

		SchoolInfoList ls=DBM.fullSchoolInfo(conn);

		String dateee=new SimpleDateFormat("dd/MM/yyyy").format(date);
		String tdy  = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		boolean flag=false;

		if(dateee.equals(tdy))
		{
			for(StudentInfo info : studentList)
			{
				if(info.getMessagesend().equalsIgnoreCase("no") && info.getAttendance().equalsIgnoreCase("A"))
				{
					flag=true;
					String mess="Dear Parent,\nYour Ward "+info.getFullName()+" is absent on date:"+dateee+" from school. Please contact in case of any problem.\nRegards,\n"+ ls.getSmsSchoolName();
					if (String.valueOf(info.getFathersPhone()).length() == 10
							&& !String.valueOf(info.getFathersPhone()).equals("2222222222")
							&& !String.valueOf(info.getFathersPhone()).equals("9999999999")
							&& !String.valueOf(info.getFathersPhone()).equals("1111111111")
							&& !String.valueOf(info.getFathersPhone()).equals("1234567890")
							&& !String.valueOf(info.getFathersPhone()).equals("0123456789"))
					{
						DBM.messageurl1(String.valueOf(info.getFathersPhone()), mess,info.getAddNumber(),conn,schoolId,"");
						//obj.updateMessageStatusInAttendance(info.getAttId(), conn);
						objJson.addMessageStatus(info.getAddNumber(), date,schoolId, conn);
					}
				}
			}

			if(flag==true)
			{
				searchData();

				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Messages sent to students."));
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("You have already sent the absent alert."));
			}

		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Sorry , You Cannot Send Alerts for Previous Date."));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void print() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("absentList", studentList);
		ss.setAttribute("total", total);
		ss.setAttribute("class", className);
		ss.setAttribute("section", section);
		ss.setAttribute("strDate", strDate);
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getExternalContext().redirect("printAbsentReport.xhtml");
	}

	public  void exportAbsentStudentPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=DBM.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();




		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		//Header
		try {

			String src =ls.getDownloadpath()+ls.getImagePath();
			Image im =null;
			try {
				im =Image.getInstance(src);
				im.setAlignment(Element.ALIGN_LEFT);

				im.scaleAbsoluteHeight(60);
				im.scaleAbsoluteWidth(85);
			} catch (Exception e) {
				e.printStackTrace();
			}
			


			Chunk c = new Chunk(schoolDetails.schoolName  +"\n",fo );
            
			Chunk c3 =null;
			try {
				c3 = new Chunk(im, -250, 15);
			} catch (Exception e) {
				e.printStackTrace();
			}
			  

			Chunk c1 = new Chunk(  "              "+schoolDetails.add1+ " " +schoolDetails.add2+"                "+"\n",fo);

			Chunk c4 = new Chunk(  "Absent Student Report "+strDate,fo);
           

			
			Paragraph p1 = new Paragraph();

			//  String[] det = studentName.split("/");

			p1.add(c);
			p1.add(c1); 
			p1.add(c3);
			p1.add(c4);
			p1.setAlignment(Element.ALIGN_CENTER);
			document.add(p1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Chunk c8 = new Chunk("\n                                                                    Absent Student Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);



			Chunk c6 = new Chunk(  "Total Student "+total,fo);
	           
			Paragraph p2 = new Paragraph();
			p2.add(c6);
			document.add(p2);
			
			Chunk c66 = new Chunk( "",fo);
	           
			Paragraph p3 = new Paragraph();
			p3.add(c66);
			document.add(p3);

			
			
			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sr. no.");
			table.addCell("Class");
			table.addCell("Student Name");
			table.addCell("Father's Name");
			table.addCell("Attendence");
			table.addCell("Phone Number");
			table.addCell("Message Sent");




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
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getSrNo()),font));
				table.addCell(new Phrase(studentList.get(i).getClassName(),font));
				table.addCell(new Phrase(studentList.get(i).getFullName(),font));
				table.addCell(new Phrase(studentList.get(i).getFathersName(),font));
				table.addCell(new Phrase(studentList.get(i).getAttendance(),font));
				table.addCell(new Phrase(String.valueOf(studentList.get(i).getFathersPhone()),font));
				table.addCell(new Phrase(studentList.get(i).getMessagesend(),font));





			}


			table.setWidthPercentage(110);
			document.add(table);





		}catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Absent_Student_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Absemt_Student_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public String getTotal() {
		return total;
	}


	public void setTotal(String total) {
		this.total = total;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
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

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
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

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
