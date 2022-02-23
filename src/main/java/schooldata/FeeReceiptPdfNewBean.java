package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Json.DataBaseMeathodJson;
import session_work.QueryConstants;

@ManagedBean(name = "feeReceiptPdfNew")
@ViewScoped
public class FeeReceiptPdfNewBean implements Serializable {

	private static final long serialVersionUID = 1L;
	boolean show = false;
	Date todaysdate, addmisssiondate, receiptDate;
	ArrayList<FeeInfo> selectedFees;
	ArrayList<StudentInfo> serialno;
	int total, totalDiscount, totalDue, dueInReceipt, payableAmt;
	String feeUpto, transportfeerupee, amountWords, session, totalAmountInWords, addnumber, sno, paymentmode, bankName,
			chequeno, studentclass, regNo, studentname, fathername, mothername, date, schoolName, add1, add2, add3,
			add4, phoneNo, mobileNo, emailId, website, imagePath, a, b, c, d;
	String width = "", fontSize = "", marginTop = "", marginBetween = "", discountMargin = "";
	String chequeDate, srNo;
	boolean r1 = false;
	boolean r2 = false, showDiscount = false;
	String checqustatus, cheueDateStatus, bankStatus, remark;
	String schRegNo;
	String schid = "";
	String type = "";
	transient StreamedContent file;
	DatabaseMethods1 db = new DatabaseMethods1();
	SchoolInfoList info1;

	public FeeReceiptPdfNewBean() {
		Connection conn = DataBaseConnection.javaConnection();

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String schid = params.get("Schoolid");
		sno = params.get("recipietNo");
		session=DatabaseMethods1.selectedSessionDetails(schid, conn);
		// // System.out.println("schid---------------- " + schid);

		
		   info1=new DataBaseMeathodJson().fullSchoolInfo(schid,conn);
		   String receiptDetailss = db.findReceiptNoDetailsOfFee(sno,schid,conn);
			
	        String[] spliteer = receiptDetailss.split("--");
			
			String payModee = spliteer[0];
			String bankNameee = spliteer[1];
			String chequeNumberr = spliteer[2];
			String receipeitDatee = spliteer[3];
			String chequeeDatee = spliteer[4];
			String remarrk = spliteer[5];
			String stidentIdd = spliteer[6];
			String dueDateString = spliteer[7];
			String feeUptoString = "";
			String feeType1 ="origanal";
			
			 selectedFees=new ArrayList<>();
			
			int i=1;
			
			
			DatabaseMethods1 obj=new DatabaseMethods1();
			ArrayList<Feecollectionc>feesSelected=obj.studetFeeCollectionByRecipietNo(stidentIdd,sno,schid,conn);
			
			
			
			

			for(Feecollectionc ff:feesSelected)
			{
				

					FeeInfo info=new FeeInfo();
					info.setSno(i);
					info.setFeeName(ff.getFeeName());
					info.setPayAmount(ff.getFeeamunt());
					info.setPayDiscount(ff.getDiscount());
					info.setDueamount(String.valueOf(ff.getTotalAmount()));

					selectedFees.add(info);
					i=i+1;
				
			}
			
			// // System.out.println(selectedFees.size()+"sffsf" );

			
			if(feesSelected.get(0).getIntallment().equals(""))
			{
				feeUptoString =  dueDateString;
				
			}
			else
			{
				feeUptoString = feesSelected.get(0).getIntallment();
				
			}

			
			
			
			
			
			
			
			
			schoolName=info1.getSchoolName();
			add1=info1.getAdd1();
			add2=info1.getAdd2();
			add3=info1.getAdd3();
			add4=info1.getAdd4();
			phoneNo=info1.getPhoneNo();
			mobileNo=info1.getMobileNo();
			emailId=info1.getEmailId();
			website=info1.getWebsite();
			regNo=info1.getRegNo();
			imagePath=info1.getDownloadpath()+info1.getImagePath();
			schRegNo=info1.getSchRegNo();
			a= info1.getRname1();
			b= info1.getRname2();
			c= info1.getRname3();
			int ad=info1.getReciept();
			//ad=2;
			if(ad==2)
			{

				width="48%";
				fontSize="1.08em";
				r1=true;
				r2=false;
				marginTop = "50px";
				marginBetween = "2%";
				discountMargin = "20%";
			}
			else if(ad==3)
			{
				width="32%";
				fontSize="1.08em";
				r1=true;
				r2=true;
				marginTop = "50px";
				marginBetween = "1.5%";
				discountMargin = "0%";
			}

			if(regNo==null || regNo.equals(""))
			{
				regNo="";
			}
			else
			{
				regNo="School Code- "+regNo;
			}

			if(schRegNo==null || schRegNo.equals(""))
			{
				schRegNo="";
			}
			else
			{
				schRegNo="Reg.No.- "+schRegNo;
			}

			if(add4==null || add4.equals(""))
			{
				add4="";
			}
			else
			{
				add4="Affiliation No.- "+add4;
			}

			

			paymentmode=payModee;
			
			
			remark=remarrk;
			feeUpto=feeUptoString;
			type="Installment Name";
			if(feeUpto.contains("/"))
			{
				type="Fee Upto";
				
				String tempArr[] = feeUpto.split("/");
				String month = db.monthNameByNumber(Integer.valueOf(tempArr[1]));
				String year = tempArr[2];
				feeUpto = month + "-" + year;
					
			}
			
			//session=DatabaseMethods1.selectedSessionDetails(schid,conn);
			try {
				receiptDate=new SimpleDateFormat("yyyy-MM-dd").parse(receipeitDatee);
			} catch (ParseException e1) {
				
				e1.printStackTrace();
			}
			date=new SimpleDateFormat("dd-MM-yyyy").format(receiptDate);
			
			if(paymentmode.equals("Cash"))
			{
				show=true;
			}
			else
			{
				bankName=bankNameee;
				chequeno=chequeNumberr;
				Date cqDate = null;
				if(chequeeDatee==null || chequeeDatee.equalsIgnoreCase("null"))
				{
					chequeDate = "";
				}
				else
				{	
				 try {
					cqDate = new SimpleDateFormat("yyyy-MM-dd").parse(chequeeDatee);
				 } catch (ParseException e) {
					
					e.printStackTrace();
				 }
				 chequeDate=new SimpleDateFormat("dd/MM/yyyy").format(cqDate);
				}
				show=false;
			}
			if(paymentmode.equals("Cheque"))
			{
				checqustatus="Cheque No";
				cheueDateStatus="Cheque Date";
				bankStatus = "Bank Name";
			}
			else if(paymentmode.equals("Challan"))
			{
				checqustatus="Challan No";
				cheueDateStatus="Challan Date";
				bankStatus = "Bank Name";
			}
			else if(paymentmode.equals("Net Banking"))
			{
				checqustatus="NEFT/IMPS No";
				cheueDateStatus="NEFT/IMPS Date";
				bankStatus = "Bank Name";
			}
			else
			{
				checqustatus="";
				cheueDateStatus="";
				bankStatus = "";
			}
			
			
		
			StudentInfo ls = db.studentDetailslistByAddNoAllStatus(schid,stidentIdd , conn);
		
			
//			if(feeType1.equals("origanal"))
//			{
			
				addnumber=stidentIdd;
				srNo=ls.getSrNo();
				studentclass=ls.getClassName();
				studentname=ls.getFullName();
				fathername=ls.getFathersName();
				mothername=ls.getMotherName();
//			}
//			else if(feeType1.equals("duplicate"))
//			{
//				DailyFeeCollectionBean info=(DailyFeeCollectionBean) rr.getAttribute("selectedStudent");
//				String addmision=info.getStudentid();
//				addnumber=String.valueOf(addmision);
//				srNo=info.getSrNo();
//				if(info.getSection()==null)
//				{
//					studentclass=info.getClassname();
//				}
//				else
//				{
//					studentclass=info.getClassname()+" - "+info.getSection();
//				}
	//
//				studentname=info.getStudentname();
//				fathername=info.getFathername();
//				mothername=info.getMothername();
//				a=a+"(D)";
//				b=b+"(D)";
//				c=c+"(D)";
//			}

				ArrayList<String> list=new DataBaseMethodStudent().verifyConcessionFieldList();
				addmisssiondate=new DataBaseMethodStudent().studentDetail(addnumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC, null,null,"","","","",session, schid, list, conn).get(0).getAddDate();

			total=totalDiscount=totalDue=dueInReceipt=0;
			for(FeeInfo ff: selectedFees)
			{
				totalDue+=Integer.valueOf(ff.getDueamount());
				total+=ff.getPayAmount();
				totalDiscount+=ff.getPayDiscount();
			}
			totalAmountInWords=db.numberToWords(total);
			if(info1.getCountry().equalsIgnoreCase("UAE"))
			{
				totalAmountInWords="AED "+totalAmountInWords+" Only";
			}
			else
			{
				totalAmountInWords="Rs."+totalAmountInWords+" Only";
			}

			payableAmt = totalDue - totalDiscount;
			dueInReceipt = totalDue - (total+totalDiscount);
			if(dueInReceipt<0)
			{
				dueInReceipt=0;
			}


			if(schid.equals("229"))
			{
				showDiscount = false;
			}
			else
			{
				showDiscount = true;
			}

			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		 
		 
	}

	public void exportFeePdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);

		document.open();

		PdfContentByte cb = writer.getDirectContent();

		cb.rectangle(15, 15, 556, 800);
		cb.setLineWidth(0.5f);
		cb.moveTo(15, 640);
		cb.lineTo(570, 640);
		cb.stroke();

		cb.moveTo(15, 539);
		cb.lineTo(570, 539);
		cb.stroke();
//           
//           cb.moveTo(15, 410);
//           cb.lineTo(570, 410);
//           cb.stroke();
//           
//           
//           
//           cb.moveTo(15, 365);
//           cb.lineTo(570, 365);
//           cb.stroke();
//           
//           cb.moveTo(15, 280);
//           cb.lineTo(570, 280);
//           cb.stroke();
//           
//           
//           cb.moveTo(15, 165);
//           cb.lineTo(570, 165);
//           cb.stroke();

		Font fontBold = new Font(FontFamily.HELVETICA, 15, Font.BOLD);
		Font fontmedium = new Font(FontFamily.HELVETICA, 13, Font.BOLD);

		Font fo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

		Chunk c1 = new Chunk(
				regNo + "                                                                                  ");
		Chunk c001 = new Chunk("Parent Copy\n", fontmedium);
		Chunk c2 = new Chunk(
				schRegNo + "                                                                                     "
						+ add4 + "\n\n");
		Paragraph p1 = new Paragraph();
		p1.add(c1);
		p1.add(c001);
		p1.add(c2);
		// p4.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);

		String src = ls.getDownloadpath() + ls.getImagePath();
		Image im = Image.getInstance(src);
		im.setAlignment(Element.ALIGN_LEFT);

		im.scaleAbsoluteHeight(100);
		im.scaleAbsoluteWidth(100);

		Chunk c3 = new Chunk(schoolName + "\n", fontBold);

		Chunk c4 = new Chunk(im, -200, -65);

		Chunk c5 = new Chunk(add1.trim() + "\n", fo);

		Chunk c6 = new Chunk(add2.trim() + "\n", fo);
		Chunk c7 = new Chunk(add3 + "\n", fo);
		Chunk c8 = new Chunk(phoneNo + "\n", fo);
		Chunk c9 = new Chunk(emailId + "\n\n\n", fo);

		Paragraph p2 = new Paragraph();

		// String[] det = studentName.split("/");

		p2.add(c3);
		p2.add(c4);
		p2.add(c5);
		p2.add(c6);
		p2.add(c7);
		p2.add(c8);
		p2.add(c9);
		p2.setAlignment(Element.ALIGN_CENTER);

		document.add(p2);

		PdfPTable tablehead = new PdfPTable(new float[] { 1, 1 });

		PdfPCell cel01 = new PdfPCell();
		cel01.addElement(new Phrase("Receipt No.:- " + sno));
		cel01.setBorderColorTop(BaseColor.WHITE);
		cel01.setBorderColorBottom(BaseColor.WHITE);
		cel01.setBorderColorLeft(BaseColor.WHITE);
		cel01.setBorderColorRight(BaseColor.WHITE);
		cel01.setBorderWidth(0);
		tablehead.addCell(cel01);

		PdfPCell cel02 = new PdfPCell();
		cel02.addElement(new Phrase("Date :-" + date));
		cel02.setBorderColorTop(BaseColor.WHITE);
		cel02.setBorderColorBottom(BaseColor.WHITE);
		cel02.setBorderColorLeft(BaseColor.WHITE);
		cel02.setBorderColorRight(BaseColor.WHITE);
		cel02.setBorderWidth(0);
		cel02.setIndent(30);
		tablehead.addCell(cel02);

		PdfPCell cel03 = new PdfPCell();
		cel03.addElement(new Phrase("Student Name:- " + studentname));
		cel03.setBorderColorTop(BaseColor.WHITE);
		cel03.setBorderColorBottom(BaseColor.WHITE);
		cel03.setBorderColorLeft(BaseColor.WHITE);
		cel03.setBorderColorRight(BaseColor.WHITE);
		cel03.setBorderWidth(0);
		tablehead.addCell(cel03);

		PdfPCell cel04 = new PdfPCell();
		cel04.addElement(new Phrase("Class :-" + studentclass));
		cel04.setBorderColorTop(BaseColor.WHITE);
		cel04.setBorderColorBottom(BaseColor.WHITE);
		cel04.setBorderColorLeft(BaseColor.WHITE);
		cel04.setBorderColorRight(BaseColor.WHITE);
		cel04.setBorderWidth(0);
		tablehead.addCell(cel04);

		PdfPCell cel05 = new PdfPCell();
		cel05.addElement(new Phrase("Father's Name:- " + fathername));
		cel05.setBorderColorTop(BaseColor.WHITE);
		cel05.setBorderColorBottom(BaseColor.WHITE);
		cel05.setBorderColorLeft(BaseColor.WHITE);
		cel05.setBorderColorRight(BaseColor.WHITE);
		cel05.setBorderWidth(0);
		tablehead.addCell(cel05);

		PdfPCell cel06 = new PdfPCell();
		cel06.addElement(new Phrase("Sr No.:-" + srNo));
		cel06.setBorderColorTop(BaseColor.WHITE);
		cel06.setBorderColorBottom(BaseColor.WHITE);
		cel06.setBorderColorLeft(BaseColor.WHITE);
		cel06.setBorderColorRight(BaseColor.WHITE);
		cel06.setBorderWidth(0);
		tablehead.addCell(cel06);

		PdfPCell cel07 = new PdfPCell();
		cel07.addElement(new Phrase("Fess Upto:- " + feeUpto));
		cel07.setBorderColorTop(BaseColor.WHITE);
		cel07.setBorderColorBottom(BaseColor.WHITE);
		cel07.setBorderColorLeft(BaseColor.WHITE);
		cel07.setBorderColorRight(BaseColor.WHITE);
		cel07.setBorderWidth(0);
		tablehead.addCell(cel07);

		PdfPCell cel08 = new PdfPCell();
		cel08.addElement(new Phrase());
		cel08.setBorderColorTop(BaseColor.WHITE);
		cel08.setBorderColorBottom(BaseColor.WHITE);
		cel08.setBorderColorLeft(BaseColor.WHITE);
		cel08.setBorderColorRight(BaseColor.WHITE);
		cel08.setBorderWidth(0);
		tablehead.addCell(cel08);

		tablehead.setWidthPercentage(105);

		document.add(tablehead);

		Chunk c14 = new Chunk("\nFee Details \n\n", fo);

		Paragraph p4 = new Paragraph();
		p4.setAlignment(Element.ALIGN_CENTER);

		p4.add(c14);

		document.add(p4);

		SideMenuBean sdb = new SideMenuBean();
		String currencyy = "Rs.";
		PdfPTable table;

		if (sdb.list.country.equalsIgnoreCase("UAE")) {
			currencyy = "AED";
			table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("Fee Type ");
			table.addCell("Amount AED");
			table.addCell("Tax Amount AED");
			table.addCell("Due Amount " + currencyy);
			table.addCell("Discount " + currencyy);
			table.addCell("Paid " + currencyy);

			table.setHeaderRows(1);

			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < selectedFees.size(); i++) {
				table.addCell(new Phrase(String.valueOf(selectedFees.get(i).getFeeName())));

				table.addCell(new Phrase(selectedFees.get(i).getMainAmount()));

				table.addCell(new Phrase(selectedFees.get(i).getTaxAmount()));
				table.addCell(new Phrase(selectedFees.get(i).getDueamount()));

				table.addCell(new Phrase(selectedFees.get(i).getPayDiscount() + ""));
				table.addCell(new Phrase(selectedFees.get(i).getPayAmount() + ""));

			}
		}

		else {
			table = new PdfPTable(new float[] { 1, 1, 1, 1 });

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell("Fee Type ");
			table.addCell("Due Amount " + currencyy);
			table.addCell("Discount " + currencyy);
			table.addCell("Paid " + currencyy);

			table.setHeaderRows(1);

			PdfPCell[] cells = table.getRow(0).getCells();
			for (int i = 0; i < cells.length; i++) {
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			// table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i = 0; i < selectedFees.size(); i++) {
				table.addCell(new Phrase(String.valueOf(selectedFees.get(i).getFeeName())));
				table.addCell(new Phrase(selectedFees.get(i).getDueamount()));
//// // System.out.println(selectedFees.get(i).getPayDiscount()+"-"+selectedFees.get(i).getPayAmount());
				table.addCell(new Phrase(selectedFees.get(i).getPayDiscount() + ""));
				table.addCell(new Phrase(selectedFees.get(i).getPayAmount() + ""));

			}
		}

		table.setWidthPercentage(100);
		document.add(table);

		Chunk c15;

		c15 = new Chunk("\n", fo);

		Paragraph p5 = new Paragraph();

		p5.add(c15);

		document.add(p5);

		PdfPTable tableThird = new PdfPTable(new float[] { 1, 1, 1 });

		PdfPCell ce301 = new PdfPCell();

		if (showDiscount == true) {
			ce301.addElement(new Phrase("Total Fees :" + currencyy + " " + totalDue));
		} else {
			ce301.addElement(new Phrase(""));
		}

		ce301.setBorderColorTop(BaseColor.WHITE);
		ce301.setBorderColorBottom(BaseColor.WHITE);
		ce301.setBorderColorLeft(BaseColor.WHITE);
		ce301.setBorderColorRight(BaseColor.WHITE);
		ce301.setBorderWidthTop(0);
		ce301.setBorderWidthLeft(0);
		ce301.setBorderWidthRight(0);
		ce301.setBorderWidthBottom(1);
		tableThird.addCell(ce301);

		PdfPCell ce302 = new PdfPCell();
		if (showDiscount == true) {
			ce302.addElement(new Phrase("Discount :" + currencyy + " " + totalDiscount));
		} else {
			ce302.addElement(new Phrase(""));
		}
		ce302.setBorderColorTop(BaseColor.WHITE);
		ce302.setBorderColorBottom(BaseColor.WHITE);
		ce302.setBorderColorLeft(BaseColor.WHITE);
		ce302.setBorderColorRight(BaseColor.WHITE);
		ce302.setBorderWidthTop(0);
		ce302.setBorderWidthLeft(0);
		ce302.setBorderWidthRight(0);
		ce302.setBorderWidthBottom(1);

		tableThird.addCell(ce302);

		PdfPCell ce303 = new PdfPCell();
		ce303.addElement(new Phrase("Payable Fees :" + currencyy + " " + payableAmt));
		ce303.setBorderColorTop(BaseColor.WHITE);
		ce303.setBorderColorBottom(BaseColor.WHITE);
		ce303.setBorderColorLeft(BaseColor.WHITE);
		ce303.setBorderColorRight(BaseColor.WHITE);
		ce303.setBorderWidthTop(0);
		ce303.setBorderWidthLeft(0);
		ce303.setBorderWidthRight(0);
		ce303.setBorderWidthBottom(1);
		tableThird.addCell(ce303);

		tableThird.setWidthPercentage(100);

		document.add(tableThird);

		Chunk c16;

		c16 = new Chunk("\n", fo);

		Paragraph p6 = new Paragraph();

		p6.add(c16);

		document.add(p6);

		PdfPTable tableForth = new PdfPTable(new float[] { 1, 1, 1 });

		PdfPCell ce401 = new PdfPCell();

		if (showDiscount == true) {
			ce401.addElement(new Phrase("Total Dues In This Receipt :" + currencyy + " " + dueInReceipt));
		} else {
			ce401.addElement(new Phrase(""));
		}

		ce401.setBorderColorTop(BaseColor.WHITE);
		ce401.setBorderColorBottom(BaseColor.WHITE);
		ce401.setBorderColorLeft(BaseColor.WHITE);
		ce401.setBorderColorRight(BaseColor.WHITE);
		ce401.setBorderWidthTop(0);
		ce401.setBorderWidthLeft(0);
		ce401.setBorderWidthRight(0);
		ce401.setBorderWidthBottom(1);
		tableForth.addCell(ce401);

		PdfPCell ce402 = new PdfPCell();

		ce402.addElement(new Phrase(""));

		ce402.setBorderColorTop(BaseColor.WHITE);
		ce402.setBorderColorBottom(BaseColor.WHITE);
		ce402.setBorderColorLeft(BaseColor.WHITE);
		ce402.setBorderColorRight(BaseColor.WHITE);
		ce402.setBorderWidthTop(0);
		ce402.setBorderWidthLeft(0);
		ce402.setBorderWidthRight(0);
		ce402.setBorderWidthBottom(1);

		tableForth.addCell(ce402);

		PdfPCell ce403 = new PdfPCell();
		ce403.addElement(new Phrase("Paid Fees :" + currencyy + " " + total));
		ce403.setBorderColorTop(BaseColor.WHITE);
		ce403.setBorderColorBottom(BaseColor.WHITE);
		ce403.setBorderColorLeft(BaseColor.WHITE);
		ce403.setBorderColorRight(BaseColor.WHITE);
		ce403.setBorderWidthTop(0);
		ce403.setBorderWidthLeft(0);
		ce403.setBorderWidthRight(0);
		ce403.setBorderWidthBottom(1);
		tableForth.addCell(ce403);

		tableForth.setWidthPercentage(100);

		document.add(tableForth);

		Chunk c18 = new Chunk("\n", fo);

		Paragraph p8 = new Paragraph();

		p8.add(c18);

		document.add(p8);

		PdfPTable tableFifth = new PdfPTable(new float[] { 1.5f, 1, 1 });

		PdfPCell ce501 = new PdfPCell();

		ce501.addElement(new Phrase("Amount In Words :" + totalAmountInWords));

		ce501.setBorderColorTop(BaseColor.WHITE);
		ce501.setBorderColorBottom(BaseColor.WHITE);
		ce501.setBorderColorLeft(BaseColor.WHITE);
		ce501.setBorderColorRight(BaseColor.WHITE);
		ce501.setBorderWidthTop(0);
		ce501.setBorderWidthLeft(0);
		ce501.setBorderWidthRight(0);
		ce501.setBorderWidthBottom(1);
		tableFifth.addCell(ce501);

		PdfPCell ce502 = new PdfPCell();

		ce502.addElement(new Phrase(""));

		ce502.setBorderColorTop(BaseColor.WHITE);
		ce502.setBorderColorBottom(BaseColor.WHITE);
		ce502.setBorderColorLeft(BaseColor.WHITE);
		ce502.setBorderColorRight(BaseColor.WHITE);
		ce502.setBorderWidthTop(0);
		ce502.setBorderWidthLeft(0);
		ce502.setBorderWidthRight(0);
		ce502.setBorderWidthBottom(1);

		tableFifth.addCell(ce502);

		PdfPCell ce503 = new PdfPCell();
		ce503.addElement(new Phrase("Paid Fees :" + currencyy + " " + total));
		ce503.setBorderColorTop(BaseColor.WHITE);
		ce503.setBorderColorBottom(BaseColor.WHITE);
		ce503.setBorderColorLeft(BaseColor.WHITE);
		ce503.setBorderColorRight(BaseColor.WHITE);
		ce503.setBorderWidthTop(0);
		ce503.setBorderWidthLeft(0);
		ce503.setBorderWidthRight(0);
		ce503.setBorderWidthBottom(1);
		tableFifth.addCell(ce503);

		tableFifth.setWidthPercentage(100);

		document.add(tableFifth);

		Chunk c17 = new Chunk("Payment Mode \n" + checqustatus + "\n" + bankStatus + "\n" + cheueDateStatus + "\n"
				+ paymentmode + "\n" + chequeno + "\n" + bankName + "\n" + chequeDate + "\n", fo);

		Paragraph p7 = new Paragraph();

		p7.add(c17);

		document.add(p7);

		Chunk c19 = new Chunk(
				"\nRemark                                                                                                                         Authorized Signature",
				fo);

		Paragraph p9 = new Paragraph();

		p9.add(c19);

		document.add(p9);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Fee_Receipt.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Fee_Receipt_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public Date getTodaysdate() {
		return todaysdate;
	}

	public void setTodaysdate(Date todaysdate) {
		this.todaysdate = todaysdate;
	}

	public Date getAddmisssiondate() {
		return addmisssiondate;
	}

	public void setAddmisssiondate(Date addmisssiondate) {
		this.addmisssiondate = addmisssiondate;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public ArrayList<FeeInfo> getSelectedFees() {
		return selectedFees;
	}

	public void setSelectedFees(ArrayList<FeeInfo> selectedFees) {
		this.selectedFees = selectedFees;
	}

	public ArrayList<StudentInfo> getSerialno() {
		return serialno;
	}

	public void setSerialno(ArrayList<StudentInfo> serialno) {
		this.serialno = serialno;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getTransportfeerupee() {
		return transportfeerupee;
	}

	public void setTransportfeerupee(String transportfeerupee) {
		this.transportfeerupee = transportfeerupee;
	}

	public String getAmountWords() {
		return amountWords;
	}

	public void setAmountWords(String amountWords) {
		this.amountWords = amountWords;
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

	public String getAddnumber() {
		return addnumber;
	}

	public void setAddnumber(String addnumber) {
		this.addnumber = addnumber;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getPaymentmode() {
		return paymentmode;
	}

	public void setPaymentmode(String paymentmode) {
		this.paymentmode = paymentmode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeno() {
		return chequeno;
	}

	public void setChequeno(String chequeno) {
		this.chequeno = chequeno;
	}

	public String getStudentclass() {
		return studentclass;
	}

	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getStudentname() {
		return studentname;
	}

	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}

	public String getFathername() {
		return fathername;
	}

	public void setFathername(String fathername) {
		this.fathername = fathername;
	}

	public String getMothername() {
		return mothername;
	}

	public void setMothername(String mothername) {
		this.mothername = mothername;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getAdd3() {
		return add3;
	}

	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	public String getAdd4() {
		return add4;
	}

	public void setAdd4(String add4) {
		this.add4 = add4;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getChecqustatus() {
		return checqustatus;
	}

	public void setChecqustatus(String checqustatus) {
		this.checqustatus = checqustatus;
	}

	public String getCheueDateStatus() {
		return cheueDateStatus;
	}

	public void setCheueDateStatus(String cheueDateStatus) {
		this.cheueDateStatus = cheueDateStatus;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public int getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(int totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public boolean isR1() {
		return r1;
	}

	public void setR1(boolean r1) {
		this.r1 = r1;
	}

	public boolean isR2() {
		return r2;
	}

	public void setR2(boolean r2) {
		this.r2 = r2;
	}

	public int getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(int totalDue) {
		this.totalDue = totalDue;
	}

	public int getDueInReceipt() {
		return dueInReceipt;
	}

	public void setDueInReceipt(int dueInReceipt) {
		this.dueInReceipt = dueInReceipt;
	}

	public String getBankStatus() {
		return bankStatus;
	}

	public void setBankStatus(String bankStatus) {
		this.bankStatus = bankStatus;
	}

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getPayableAmt() {
		return payableAmt;
	}

	public void setPayableAmt(int payableAmt) {
		this.payableAmt = payableAmt;
	}

	public String getFeeUpto() {
		return feeUpto;
	}

	public void setFeeUpto(String feeUpto) {
		this.feeUpto = feeUpto;
	}

	public boolean isShowDiscount() {
		return showDiscount;
	}

	public void setShowDiscount(boolean showDiscount) {
		this.showDiscount = showDiscount;
	}

	public String getSchRegNo() {
		return schRegNo;
	}

	public void setSchRegNo(String schRegNo) {
		this.schRegNo = schRegNo;
	}

	public String getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
	}

	public String getMarginBetween() {
		return marginBetween;
	}

	public void setMarginBetween(String marginBetween) {
		this.marginBetween = marginBetween;
	}

	public String getDiscountMargin() {
		return discountMargin;
	}

	public void setDiscountMargin(String discountMargin) {
		this.discountMargin = discountMargin;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public SchoolInfoList getInfo1() {
		return info1;
	}

	public void setInfo1(SchoolInfoList info1) {
		this.info1 = info1;
	}
	

}
