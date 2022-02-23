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
import schooldata.SchoolInfoList;

@ManagedBean(name="printFeeEstimate")
@ViewScoped
public class PrintFeeEstimateBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String srNo,concessionName,schoolName,add1,add2,selectedClass,selectedSection,selectedRoute,session,totalAmountInWords,studentType,studentName,imagePath;
	String className,routeName,totalFee,totalAmountToBePaid,addNumber,parentName,areaCode,concessionCategory;
	ArrayList<FeeEstimateInfo> feeList;
	String aravali1,aravali2,aravali3,aravali4,aravali5,aravali6,aravali7,aravali8,aravali9,aravali10,aravali11,aravali12,aravali13,aravali14;
	boolean newStudent,oldStudent;
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	String schoolId;
	DatabaseMethods1 dbm=new DatabaseMethods1();
	DataBaseMethodsReports obj =new DataBaseMethodsReports();


	public PrintFeeEstimateBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId,conn);
		
		schoolDetails =dbm.fullSchoolInfo(conn);

		if(schoolId.equals("213"))
		{
			aravali2="NOTE:- MODE OF PAYMENT";
			aravali1 = "Fees has to be paid by the bank cheques or to be deposited (Non-Cash) directly into school bank\r\n" +
					"								 account from 01 st of every quarter i.e.April/July/Oct/Jan to 10 th of every quarter. After failing\r\n" +
					"								 it, late fee will be charged as per norms. Demand Draft/Cheque shall be in favour of ARAVALI PUBLIC SCHOOL,BANDIKUI.\r\n" +
					"								 If cheque is not honoured Rs.300.00 will be charged extra as bank service charges. OUTSTATION CHEQUES\r\n" +
					"								 WILL NOT BE ACCEPTED.";
			aravali3="(Please write the Student's AdmissionNo.,Name,Class,Section and Contact No.in the rear side of the Demand Draft/Cheque).";
			aravali4="**No Admission / Annual / Tuition Fee will be charged from the students who are enrolled under RTE.";
			aravali5="***Day Boarding Fee depends on commencement of Day Boarding Classes.";
			aravali6="Outstation persons can pay the school fee through Net banking/IMPS/NEFT/RTGS directly to our school\r\n" +
					"								 bank account. Cash deposit will not be allowed.";
			aravali7="If you are paying fee through Net banking/IMPS/NEFT/RTGS,must send the transaction details with the\r\n" +
					"								 snapshot/image of transaction through our School's Mobile App by clicking on 'Send Fee Details' option.\r\n" +
					"								 Do not forget to mention the transaction no. and student's details. If you are not using School's Mobile\r\n" +
					"								 App then you could send the transaction details through sms to 7891030403.";
			aravali8="School Bank A/c Details are:";
			aravali9="A/c Name: Aravali Public School, Bandikui ";
			aravali10="Bank: Axis Bank Ltd. Branch- Bandikui ";

			aravali11="A/c Type: Current Bank Account";
			aravali12="A/c No.: 917010054058266";
			aravali13="IFS Code: UTIB0001250";
			aravali14="MICR : 303211201";
		}
		else
		{
			aravali2="";
			aravali1 = "";
			aravali3="";
			aravali4="";
			aravali5="";
			aravali6="";
			aravali7="";
			aravali8="";
			aravali9="";
			aravali10="";

			aravali11="";
			aravali12="";
			aravali13="";
			aravali14="";
		}



		
		SchoolInfoList info1=dbm.fullSchoolInfo(conn);
		schoolName=info1.getSchoolName().toUpperCase();
		add1=info1.getAdd1().toUpperCase();
		add2=info1.getAdd2().toUpperCase();
		imagePath=info1.getImagePath();

		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		selectedClass=(String) rr.getAttribute("selectedClass");
		selectedSection=(String )rr.getAttribute("selectedSection");
		session=DatabaseMethods1.selectedSessionDetails(schoolId,conn);
		selectedRoute=(String) rr.getAttribute("selectedRoute");
		studentType=(String) rr.getAttribute("studentType");
		studentName=(String) rr.getAttribute("studentName");
		concessionCategory=(String) rr.getAttribute("concessionCategory");
		srNo=(String) rr.getAttribute("srno");
		concessionName = dbm.concessionCategoryNameById(schoolId,concessionCategory, conn);
		if(studentType.equalsIgnoreCase("New"))
		{
			newStudent=true;oldStudent=false;
			className=dbm.classNameFromidSchid(schoolId,selectedClass, session,conn);
			routeName=dbm.transportRouteNameFromId(schoolId,selectedRoute,session,conn);
		}
		else
		{
			addNumber=(String )rr.getAttribute("addNumber");
			parentName=(String) rr.getAttribute("parentName");
			areaCode=(String) rr.getAttribute("areaCode");

			newStudent=false;oldStudent=true;
			className=dbm.classSectionNameFromidSchid(schoolId,selectedSection,conn);
			String classId=dbm.classSectionNameFromidSchid(schoolId,selectedSection,conn);
			className=dbm.classNameFromidSchid(schoolId,classId,session,conn);
			routeName=dbm.transportRouteNameFromId(schoolId,selectedRoute,session,conn);

		}
		feeList=obj.feeEstimateList(selectedClass, session, selectedRoute, studentType,conn, concessionCategory);
		int amount1=0,amount2=0,amount3=0,amount4=0;
		for(FeeEstimateInfo info:feeList)
		{
			amount1+=Integer.parseInt(info.getFirstQuarter());
			amount2+=Integer.parseInt(info.getSecondQuarter());
			amount3+=Integer.parseInt(info.getThirdQuarter());
			amount4+=Integer.parseInt(info.getFourthQuarter());
		}
		totalFee=String.valueOf(amount1+amount2+amount3+amount4);
		totalAmountToBePaid=String.valueOf(Integer.parseInt(totalFee));

		FeeEstimateInfo info=new FeeEstimateInfo();
		info.setFeeName("TOTAL");
		info.setFirstQuarter(String.valueOf(amount1));
		info.setSecondQuarter(String.valueOf(amount2));
		info.setThirdQuarter(String.valueOf(amount3));
		info.setFourthQuarter(String.valueOf(amount4));
		feeList.add(info);

		if(Integer.parseInt(totalAmountToBePaid)<0)
		{
			totalAmountToBePaid="0";
		}
		if(Integer.parseInt(totalAmountToBePaid)>0)
		{
			totalAmountInWords=dbm.numberToWords(Integer.parseInt(totalAmountToBePaid));
			totalAmountInWords=totalAmountInWords+" Rupees. Only.";
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public  void exportFeeEstPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=dbm.fullSchoolInfo(con);




		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.getProperty("user.home");


		PdfWriter.getInstance(document, baos);
		document.open();



		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		Font fom = new Font(FontFamily.HELVETICA, 9, Font.BOLD);

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
			 

			Chunk c1 = new Chunk(  schoolDetails.add1+ " " +schoolDetails.add2+"                \n\n",fo);

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
			Chunk c8 = new Chunk("\n                                                                 Student  Fee Estimate Report\n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\nAdm. No. : "+srNo+"                                                                                                                 Student Name :"+studentName+"\nClass : "+className+"                                                                               Parents Name : "+parentName+" \nIf School Transportation Facility required, Stop for Pick/Drop:- "+routeName+"                   \nConcession Cat.:- "+concessionName+"\n\n",fom );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			//  p8.setAlignment(Element.ALIGN_CENTER);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(new float[] {1,1,1,1,1});

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Installment");
			table.addCell("(Apr-Jun)I");

			table.addCell("(July-Sept)II");
			table.addCell("(Oct-Dec)III");
			table.addCell("(Jan-Mar)IV");






			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});


			for (int i=0;i<feeList.size();i++){
				table.addCell(new Phrase(String.valueOf(feeList.get(i).getFeeName()),font));
				table.addCell(new Phrase(String.valueOf(feeList.get(i).getFirstQuarter()),font));

				table.addCell(new Phrase(String.valueOf(feeList.get(i).getSecondQuarter()),font));

				table.addCell(new Phrase(String.valueOf(feeList.get(i).getThirdQuarter()),font));

				table.addCell(new Phrase(String.valueOf(feeList.get(i).getFourthQuarter()),font));






			}


			table.setWidthPercentage(110);
			document.add(table);

			Chunk c10 = new Chunk("\nTotal Fee :                                                                                                                                                      "+totalFee+"\nTotal Amount to be Paid :                                                                                                                              "+totalAmountToBePaid+"\n In Words : "+totalAmountInWords+"\n",fom );
			Paragraph p10 = new Paragraph();
			p10.add(c10);
			document.add(p10);





		}  catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Fee_Estimate_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Fee_Estimate_Report_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}




	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAdd1() {
		return add1;
	}
	public void setAdd1(String add1) {
		this.add1 = add1;
	}
	public String getAdd2() {
		return add2;
	}
	public void setAdd2(String add2) {
		this.add2 = add2;
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
	public String getSelectedRoute() {
		return selectedRoute;
	}
	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getTotalAmountInWords() {
		return totalAmountInWords;
	}
	public void setTotalAmountInWords(String totalAmountInWords) {
		this.totalAmountInWords = totalAmountInWords;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public ArrayList<FeeEstimateInfo> getFeeList() {
		return feeList;
	}
	public void setFeeList(ArrayList<FeeEstimateInfo> feeList) {
		this.feeList = feeList;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public String getTotalAmountToBePaid() {
		return totalAmountToBePaid;
	}
	public void setTotalAmountToBePaid(String totalAmountToBePaid) {
		this.totalAmountToBePaid = totalAmountToBePaid;
	}
	public boolean isNewStudent() {
		return newStudent;
	}
	public void setNewStudent(boolean newStudent) {
		this.newStudent = newStudent;
	}
	public boolean isOldStudent() {
		return oldStudent;
	}
	public void setOldStudent(boolean oldStudent) {
		this.oldStudent = oldStudent;
	}
	public String getAddNumber() {
		return addNumber;
	}
	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getConcessionCategory() {
		return concessionCategory;
	}
	public void setConcessionCategory(String concessionCategory) {
		this.concessionCategory = concessionCategory;
	}

	public String getAravali1() {
		return aravali1;
	}

	public void setAravali1(String aravali1) {
		this.aravali1 = aravali1;
	}

	public String getAravali2() {
		return aravali2;
	}

	public void setAravali2(String aravali2) {
		this.aravali2 = aravali2;
	}

	public String getAravali3() {
		return aravali3;
	}

	public void setAravali3(String aravali3) {
		this.aravali3 = aravali3;
	}

	public String getAravali4() {
		return aravali4;
	}

	public void setAravali4(String aravali4) {
		this.aravali4 = aravali4;
	}

	public String getAravali5() {
		return aravali5;
	}

	public void setAravali5(String aravali5) {
		this.aravali5 = aravali5;
	}

	public String getAravali6() {
		return aravali6;
	}

	public void setAravali6(String aravali6) {
		this.aravali6 = aravali6;
	}

	public String getAravali7() {
		return aravali7;
	}

	public void setAravali7(String aravali7) {
		this.aravali7 = aravali7;
	}

	public String getAravali8() {
		return aravali8;
	}

	public void setAravali8(String aravali8) {
		this.aravali8 = aravali8;
	}

	public String getAravali9() {
		return aravali9;
	}

	public void setAravali9(String aravali9) {
		this.aravali9 = aravali9;
	}

	public String getAravali10() {
		return aravali10;
	}

	public void setAravali10(String aravali10) {
		this.aravali10 = aravali10;
	}

	public String getAravali11() {
		return aravali11;
	}

	public void setAravali11(String aravali11) {
		this.aravali11 = aravali11;
	}

	public String getAravali12() {
		return aravali12;
	}

	public void setAravali12(String aravali12) {
		this.aravali12 = aravali12;
	}

	public String getAravali13() {
		return aravali13;
	}

	public void setAravali13(String aravali13) {
		this.aravali13 = aravali13;
	}

	public String getAravali14() {
		return aravali14;
	}

	public void setAravali14(String aravali14) {
		this.aravali14 = aravali14;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getConcessionName() {
		return concessionName;
	}

	public void setConcessionName(String concessionName) {
		this.concessionName = concessionName;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
