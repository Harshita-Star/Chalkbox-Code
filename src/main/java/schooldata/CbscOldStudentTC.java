package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import session_work.RegexPattern;
import tc_module.DataBaseMethodsTcModule;
@ManagedBean(name="cbscOldStudentTC")
@SessionScoped
public class CbscOldStudentTC implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String reason,lastClass,tcNumber,text,previousClass,schoolExamWord,promotedClassWord,result,schoolAffilationNo,schNo,session,headerImage,website,perfom,classname2,dob1,startingdate1,dobinword,fullName,classname1,addmissionnumber;
	Date struckOffDate,applicationDate,issueDate, dob,startingDate;
	boolean showTextBox,showTC,showMainForm;
	String fatherName,motherName,nationality,category, date1,year,className,currentAddress,permanentAddress,issuDateStr,lastDateStr;
	String schoolExam,failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays,workingDayPresent,feeConcession,gamesPlayed,extraActivity,otherRemark;
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem>categoryList=new ArrayList<>();
	StudentInfo tcList;
	boolean showpdfbtn = false;
	transient StreamedContent file;
	ArrayList<StudentInfo> serial;
	String stadhar="",madhar="",fadhar="",ncc="",previosClassWord="", lastSession="";
	public void getOtherReason()
	{
		if(reason.equals("others"))
		{
			showTextBox=true;
		}
		else
		{
			showTextBox=false;
			text=null;
		}
	}

	public void update() throws IOException
	{
		DatabaseMethods1 DBM=new DatabaseMethods1();
		DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
		
		Connection conn=DataBaseConnection.javaConnection();
		
		SchoolInfoList info = DBM.fullSchoolInfo(conn);
		
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		String date1= transform(dob.getDate());
		String wordmonth=DBM.monthNameByNumber(dob.getMonth()+1);
		String year1= year(dob.getYear()+1900);
		dobinword=date1+" "+wordmonth+" "+year1;
		dob1=new SimpleDateFormat("dd/MM/yyyy").format(dob);
		issuDateStr=sdf.format(issueDate);
		lastDateStr=sdf.format(struckOffDate);
		startingdate1=sdf.format(startingDate);
		session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		if(reason.equalsIgnoreCase("others"))
		{
			reason=text;
		}
		
		if(info.getBranch_id().equals("22") || info.getBranch_id().equalsIgnoreCase("27"))
		{
			int number=objTc.getseraialno("continue",conn);
			tcNumber=String.valueOf(number);
			objTc.inserserialno(addmissionnumber+"_old",number,conn);
			String refNo;
			try {
				refNo=addWorkLog(conn,addmissionnumber,number);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		int i=DBM.insertCbscStudentTcInforation(reason,text,tcNumber,addmissionnumber,fullName,fatherName,motherName,
				nationality,category,startingDate,className,dob,struckOffDate,previousClass,perfom,applicationDate,issueDate,
				schoolExam,failedOrNot,subjectStudied,qualifiedPromotion,monthOfFeePaid,workingDays,workingDayPresent,
				feeConcession,gamesPlayed,extraActivity,otherRemark,stadhar,fadhar,madhar,ncc,lastSession,conn);
		if(i==1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TC generated successfully"));
			category=DBM.studentCategoryById(category, conn);
		
				FacesContext.getCurrentInstance().getExternalContext().redirect("printCBSEOldTC.xhtml");
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
		}
		previousClassInWord(previousClass);
		previosClassWord=previosClassWord.toUpperCase();
		if(!previosClassWord.equals("")) {
			previosClassWord="("+previosClassWord.toUpperCase()+")";
		}
		if(qualifiedPromotion.contains("Yes")||qualifiedPromotion.contains("yes")||qualifiedPromotion.contains("YES")) {
			String [] re = qualifiedPromotion.split(" ");
			qualifiedPromotion = re[0];
			promotedClassInWord(re[1]);
			promotedClassWord=promotedClassWord.toUpperCase();
			if(!promotedClassWord.equals("")) {
				promotedClassWord="("+promotedClassWord.toUpperCase()+")";
			}
		}
		
		result = "";
		
		if(schoolExam.contains(" ")) {
			String [] exams = schoolExam.split(" ");
			schoolExam = exams[0];
			result = exams[1];
			schoolExamWord(schoolExam);
			schoolExam = schoolExam.toUpperCase();
			schoolExamWord = schoolExamWord.toUpperCase();
			if(!schoolExamWord.equals("")) {
				schoolExamWord = "("+schoolExamWord.toUpperCase()+")";
			}
		}else {
			schoolExamWord(schoolExam);
			schoolExam = schoolExam.toUpperCase();
			schoolExamWord = schoolExamWord.toUpperCase();
			if(!schoolExamWord.equals("")) {
				schoolExamWord = "("+schoolExamWord.toUpperCase()+")";
			}
		}
		
		
		
		
		
		
		String savePath="";
		if(info.getProjecttype().equals("online"))
		{
			savePath=info.getDownloadpath();
		}
		headerImage=savePath+info.getTcHeader();
		if(gamesPlayed.equals("N.A.")&& extraActivity.equals("N.A.")){
			gamesPlayed="N.A.";
		}else if ((gamesPlayed.equals("") && extraActivity.equals("N.A."))||(gamesPlayed.equals("N.A.") && extraActivity.equals(""))) {
			gamesPlayed="N.A.";
		} 
		else
		{
			if(gamesPlayed.equals("")||gamesPlayed.equals("N.A.")) {
				gamesPlayed = extraActivity.toUpperCase();
			}else if(extraActivity.equals("")||extraActivity.equals("N.A.")){
				gamesPlayed = gamesPlayed.toUpperCase();
			}else if(gamesPlayed.equalsIgnoreCase(extraActivity)) {
				gamesPlayed = gamesPlayed.toUpperCase();
			}else{
				gamesPlayed=gamesPlayed.toUpperCase()+","+extraActivity.toUpperCase();
			}
		}
		
		if(info.getSchid().equals("216") || info.getSchid().equals("221")) {
			showpdfbtn = true;
		}
		
		
		website=info.getWebsite();
		showTC=true;showMainForm=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void schoolExamWord(String schoolExam)
	{
		if(schoolExam.equalsIgnoreCase("I") || schoolExam.equalsIgnoreCase("1") || schoolExam.equalsIgnoreCase("1st"))
		{
			schoolExamWord="First";
		}
		else if(schoolExam.equalsIgnoreCase("II") || schoolExam.equalsIgnoreCase("2") || schoolExam.equalsIgnoreCase("2nd"))
		{
			schoolExamWord="Second";
		}
		else if(schoolExam.equalsIgnoreCase("III") || schoolExam.equalsIgnoreCase("3") || schoolExam.equalsIgnoreCase("3rd"))
		{
			schoolExamWord="Third";
		}
		else if(schoolExam.equalsIgnoreCase("IV") || schoolExam.equalsIgnoreCase("4") || schoolExam.equalsIgnoreCase("4th"))
		{
			schoolExamWord="Fourth";
		}
		else if(schoolExam.equalsIgnoreCase("V") || schoolExam.equalsIgnoreCase("5") || schoolExam.equalsIgnoreCase("5th"))
		{
			schoolExamWord="Fifth";
		}
		else if(schoolExam.equalsIgnoreCase("VI") || schoolExam.equalsIgnoreCase("6") || schoolExam.equalsIgnoreCase("6th"))
		{
			schoolExamWord="Sixth";
		}
		else if(schoolExam.equalsIgnoreCase("VII") || schoolExam.equalsIgnoreCase("7") || schoolExam.equalsIgnoreCase("7th"))
		{
			schoolExamWord="Seventh";
		}
		else if(schoolExam.equalsIgnoreCase("VIII") || schoolExam.equalsIgnoreCase("8") || schoolExam.equalsIgnoreCase("8th"))
		{
			schoolExamWord="Eighth";
		}
		else if(schoolExam.equalsIgnoreCase("IX") || schoolExam.equalsIgnoreCase("9") || schoolExam.equalsIgnoreCase("9th"))
		{
			schoolExamWord="Ninth";
		}
		else if(schoolExam.equalsIgnoreCase("X") || schoolExam.equalsIgnoreCase("10") || schoolExam.equalsIgnoreCase("10th"))
		{
			schoolExamWord="Tenth";
		}
		else if(schoolExam.equalsIgnoreCase("XI") || schoolExam.equalsIgnoreCase("11") || schoolExam.equalsIgnoreCase("11th"))
		{
			schoolExamWord="Eleventh";
		}
		else if(schoolExam.equalsIgnoreCase("XII") || schoolExam.equalsIgnoreCase("12") || schoolExam.equalsIgnoreCase("12th"))
		{
			schoolExamWord="Twelfth";
		}
		else
		{
			schoolExamWord=schoolExam;
		}

	}

	public void promotedClassInWord(String promotedCls) {
		if (promotedCls.equalsIgnoreCase("I") || promotedCls.equalsIgnoreCase("1")
				|| promotedCls.equalsIgnoreCase("1st")) {
			promotedClassWord = "First";
		} else if (promotedCls.equalsIgnoreCase("II") || promotedCls.equalsIgnoreCase("2")
				|| promotedCls.equalsIgnoreCase("2nd")) {
			promotedClassWord = "Second";
		} else if (promotedCls.equalsIgnoreCase("III") || promotedCls.equalsIgnoreCase("3")
				|| promotedCls.equalsIgnoreCase("3rd")) {
			promotedClassWord = "Third";
		} else if (promotedCls.equalsIgnoreCase("IV") || promotedCls.equalsIgnoreCase("4")
				|| promotedCls.equalsIgnoreCase("4th")) {
			promotedClassWord = "Fourth";
		} else if (promotedCls.equalsIgnoreCase("V") || promotedCls.equalsIgnoreCase("5")
				|| promotedCls.equalsIgnoreCase("5th")) {
			promotedClassWord = "Fifth";
		} else if (promotedCls.equalsIgnoreCase("VI") || promotedCls.equalsIgnoreCase("6")
				|| promotedCls.equalsIgnoreCase("6th")) {
			promotedClassWord = "Sixth";
		} else if (promotedCls.equalsIgnoreCase("VII") || promotedCls.equalsIgnoreCase("7")
				|| promotedCls.equalsIgnoreCase("7th")) {
			promotedClassWord = "Seventh";
		} else if (promotedCls.equalsIgnoreCase("VIII") || promotedCls.equalsIgnoreCase("8")
				|| promotedCls.equalsIgnoreCase("8th")) {
			promotedClassWord = "Eighth";
		} else if (promotedCls.equalsIgnoreCase("IX") || promotedCls.equalsIgnoreCase("9")
				|| promotedCls.equalsIgnoreCase("9th")) {
			promotedClassWord = "Ninth";
		} else if (promotedCls.equalsIgnoreCase("X") || promotedCls.equalsIgnoreCase("10")
				|| promotedCls.equalsIgnoreCase("10th")) {
			promotedClassWord = "Tenth";
		} else if (promotedCls.equalsIgnoreCase("XI") || promotedCls.equalsIgnoreCase("11")
				|| promotedCls.equalsIgnoreCase("11th")) {
			promotedClassWord = "Eleventh";
		} else if (promotedCls.equalsIgnoreCase("XII") || promotedCls.equalsIgnoreCase("12")
				|| promotedCls.equalsIgnoreCase("12th")) {
			promotedClassWord = "Twelfth";
		} else {
			promotedClassWord = promotedCls;
		}

	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Reason-"+reason+" -- Text-"+text+" -- TcNumber-"+tcNumber+" -- AdmNo-"+addmissionnumber+" -- FullName-"+fullName+" -- FatherName-"+fatherName+" -- MotherName-"+motherName+" -- Nationality-"+
				nationality+" -- Category-"+category+" -- StartDate-"+startingDate+" -- Class-"+className+" -- DOB-"+dob+" -- StruckDate-"+struckOffDate+" -- PreviousClass-"+previousClass+" -- Perform-"+perfom+" -- AplDate-"+applicationDate+" -- issuedAte-"+issueDate+" -- School-"+
				schoolExam+" -- Pass/Fail-"+failedOrNot+" -- Subject-"+subjectStudied+" -- Qualified-"+qualifiedPromotion+" -- MonthOfFee-"+monthOfFeePaid+" -- Working Days-"+workingDays+" -- Workingdaypresent-"+workingDayPresent+" -- FeeConcession-"+
				feeConcession+" -- Games-"+gamesPlayed+" -- ExtaActivity-"+extraActivity+" -- otherRemark-"+otherRemark+" -- StdAdhaar-"+stadhar+" -- Faadhar-"+fadhar+" -- Maadhar-"+madhar+" -- Ncc-"+ncc; 

		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Insert Cbsc Old Tc","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog(Connection conn,String addmissionnumber,int number)
	{
	    String value = "";
		String language= "";
		
		language = value = addmissionnumber+" -- "+number; 

		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Insert Serial No Old Tc","WEB",value,conn);
		return refNo;
	}
	
	
	

	public void previousClassInWord(String prevclass)
	{
		if(prevclass.equalsIgnoreCase("I") || prevclass.equalsIgnoreCase("1") || prevclass.equalsIgnoreCase("1st"))
		{
			previosClassWord="First";
		}
		else if(prevclass.equalsIgnoreCase("II") || prevclass.equalsIgnoreCase("2") || prevclass.equalsIgnoreCase("2nd"))
		{
			previosClassWord="Second";
		}
		else if(prevclass.equalsIgnoreCase("III") || prevclass.equalsIgnoreCase("3") || prevclass.equalsIgnoreCase("3rd"))
		{
			previosClassWord="Third";
		}
		else if(prevclass.equalsIgnoreCase("IV") || prevclass.equalsIgnoreCase("4") || prevclass.equalsIgnoreCase("4th"))
		{
			previosClassWord="Fourth";
		}
		else if(prevclass.equalsIgnoreCase("V") || prevclass.equalsIgnoreCase("5") || prevclass.equalsIgnoreCase("5th"))
		{
			previosClassWord="Fifth";
		}
		else if(prevclass.equalsIgnoreCase("VI") || prevclass.equalsIgnoreCase("6") || prevclass.equalsIgnoreCase("6th"))
		{
			previosClassWord="Sixth";
		}
		else if(prevclass.equalsIgnoreCase("VII") || prevclass.equalsIgnoreCase("7") || prevclass.equalsIgnoreCase("7th"))
		{
			previosClassWord="Seventh";
		}
		else if(prevclass.equalsIgnoreCase("VIII") || prevclass.equalsIgnoreCase("8") || prevclass.equalsIgnoreCase("8th"))
		{
			previosClassWord="Eighth";
		}
		else if(prevclass.equalsIgnoreCase("IX") || prevclass.equalsIgnoreCase("9") || prevclass.equalsIgnoreCase("9th"))
		{
			previosClassWord="Ninth";
		}
		else if(prevclass.equalsIgnoreCase("X") || prevclass.equalsIgnoreCase("10") || prevclass.equalsIgnoreCase("10th"))
		{
			previosClassWord="Tenth";
		}
		else if(prevclass.equalsIgnoreCase("XI") || prevclass.equalsIgnoreCase("11") || prevclass.equalsIgnoreCase("11th"))
		{
			previosClassWord="Eleventh";
		}
		else if(prevclass.equalsIgnoreCase("XII") || prevclass.equalsIgnoreCase("12") || prevclass.equalsIgnoreCase("12th"))
		{
			previosClassWord="Twelfth";
		}
		else
		{
			previosClassWord=prevclass;
		}

	}

	@PostConstruct
	public void init()
	{
		schoolExam=failedOrNot=subjectStudied=qualifiedPromotion=monthOfFeePaid=workingDays=workingDayPresent=feeConcession=gamesPlayed=extraActivity=otherRemark="";
		fatherName=motherName=nationality=category= date1=year=className=currentAddress=permanentAddress=issuDateStr=lastDateStr="";
		struckOffDate=applicationDate=issueDate=dob=startingDate=null;
		showTextBox=false;
		reason=lastClass=tcNumber=text=previousClass=session=headerImage=website=perfom=classname2=dob1=startingdate1=dobinword=fullName=classname1=addmissionnumber="";
		Connection conn=DataBaseConnection.javaConnection();
		DataBaseMethodsTcModule objTc=new DataBaseMethodsTcModule();
		categoryList=new DatabaseMethods1().studentCategoryList(conn);
		showMainForm=true;
		showTC=false;
		
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);
		
		if(info.getBranch_id().equals("22") || info.getBranch_id().equalsIgnoreCase("27"))
		{
			int number=objTc.getseraialno("continue",conn);
			tcNumber=String.valueOf(number);
		}
		
		schoolAffilationNo = info.getAdd4();
		schNo = info.getRegNo();
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public String transform(int num)
	{
		String  one[]={" "," First"," Second"," Third"," Fourth"," Fifth"," Sixth"," Seventh"," Eighth"," Nineth"," Tenth"," Eleventh"," Twelveth"," Thirteen"," Fourteen","Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};String ten[]={" "," "," Twenty"," Thirty"," Forty"," Fifty"," Sixty","Seventy"," Eighty"," Ninety"};
		if(num<=99)
		{
			if(num<=19) {
				date1=one[num];
			}
			else {
				int num1=num/10;
				int num2=num%10;
				date1=ten[num1]+one[num2];
			}
		}
		return date1;
	}

	public String year(int num)
	{
		String  one[]={" "," One"," Two"," Three"," Four"," five"," Six"," Seven"," Eight"," Nine"," Ten"," Eleven"," Twelve"," Thirteen"," Fourteen","Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};
		String ten[]={" "," "," Twenty "," Thirty "," Forty "," Fifty "," Sixty ","Seventy "," Eighty "," Ninety "};

		if(num>=1000 && num<=9999){
			int num1=num/1000;
			int num2=num%1000;
			if(num2<=99){
				if(num2<=19) {
					year=one[num1]+" Thousand "+one[num2];

				}
				else {
					int num4=num2/10;
					int num5=num2%10;
					year=one[num1]+" Thousand "+ten[num4]+one[num5];
				}
			}
			else{
				int num6=num2/100;
				int num7=num2%100;
				if(num7<=19){
					String num8=one[num7];
					year=one[num1]+"Thousand "+one[num6]+" Hundred "+num8;
				}
				else {
					int num0=num7/10;
					int num9=num7%10;
					year=one[num1]+" Thousand "+one[num6]+" Hundred "+ten[num0]+one[num9];
				}
			}
		}
		return year;
	}
	

	public  void exportClasWiseLessPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con=DataBaseConnection.javaConnection();
		SchoolInfoList ls=new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);
        SideMenuBean smb = new SideMenuBean();

		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Document  document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();


		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();



		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30,30,536,780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba,fed);
		cb.lineTo(cba + 72f*7.45, fed);

		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font foRed = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		foRed.setColor(BaseColor.RED);
		//Header
		try {

			
			String src = ls.getDownloadpath()+ls.getTcHeader()+"?pfdrid_c=true";
			//String src = "https://tinyjpg.com/images/social/website.jpg";
			// // System.out.println(src+"sfdc");
			Image im = null;
			
			try {
				 im =Image.getInstance(src);			
			} catch (Exception e) {
	          e.printStackTrace();
			}
			try {
				im.setAlignment(Element.ALIGN_RIGHT);
				im.scaleAbsoluteHeight(100);
				im.scaleAbsoluteWidth(520);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Chunk c999 =null;
			try {
				c999 = new Chunk(im, 0, -84);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
            Paragraph p999 = new Paragraph();
            p999.add(c999);
            document.add(p999);

//			PdfPTable tabe;
//
//			tabe = new PdfPTable(new float[] {1});
//			PdfPCell cellll = new PdfPCell();
//			cellll.setImage(im);
//			cellll.setFixedHeight(100f);
//			cellll.setBorder(Rectangle.NO_BORDER);
//			tabe.addCell(cellll);
//			
//			tabe.setWidthPercentage(100);
//		
//			tabe.setHorizontalAlignment(Element.ALIGN_CENTER);
//			document.add(tabe);
			
        	Font fnt = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);
			Font ftt = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);
			

			Font font = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
			Chunk c8 = null;
			
				 c8 = new Chunk("\n\n\n\n\n\n                                                     \n",font );	
			
			
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] {1});
			PdfPCell cellTop = new PdfPCell(new Phrase("TRANSFER CERTIFICATE",foRed));
			cellTop.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cellTop);
			table.setWidthPercentage(40);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA,10, Font.BOLD);
			Chunk c40 = new Chunk("\n",fonthead );
			Paragraph p11 = new Paragraph();
			p11.add(c40);

			document.add(p11);  
			
			PdfPTable tableCe;
			tableCe = new PdfPTable(new float[] {1,1,1});
			tableCe.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			
			PdfPCell cell2 = new PdfPCell(new Phrase("             School Code : "+smb.list.regNo,fnt));
			cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell2.setBorder(0);
			tableCe.addCell(cell2);
			
			PdfPCell cell4 = new PdfPCell(new Phrase("Book No. : 1/"+smb.startSession,fnt));
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(0);
			tableCe.addCell(cell4);
			PdfPCell cell5 = new PdfPCell(new Phrase("Sr No. : "+tcNumber,fnt));
			cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell5.setBorder(0);
			tableCe.addCell(cell5);
			
			PdfPCell cell6 = new PdfPCell(new Phrase("Admission No. :"+addmissionnumber,fnt));
			cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell6.setBorder(0);
			tableCe.addCell(cell6);
			
		
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Affiliation No. : "+smb.list.add4,fnt));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(0);
			tableCe.addCell(cell1);
			
			SideMenuBean s = new SideMenuBean();
			
			PdfPCell cell3 = new PdfPCell(new Phrase(" School Type : "+smb.list.schType,fnt));
			cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell3.setBorder(0);
			tableCe.addCell(cell3);
		
			
			tableCe.setWidthPercentage(100);
			tableCe.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(tableCe);
			
			Chunk c45 = new Chunk("\n",fonthead );
			Paragraph p15 = new Paragraph();
			p15.add(c45);

			document.add(p15); 

			


			PdfPTable table2;
		

			table2 = new PdfPTable(new float[] {1.7f,1});
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f,1.3f);


			//           for(int i=1;i<=48;i++) {
				//               if(i%2==0) {
			//                   //// // System.out.println(i);
			//               table2.getDefaultCell().setBorderWidthBottom(0.5f);
			//              table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			//           }
			//               else {
			//                  table2.getDefaultCell().setBorderWidthBottom(0.5f);
			//                  table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			//               }
			//           }

			table2.addCell(new Phrase("1. Name of the Student",fnt));

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(fullName,ftt));
			//cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(0);
			table2.addCell(cee1);

			Paragraph pa7 = new Paragraph();
			Chunk cp72 = new Chunk(" Adhar Card No.",fnt);
			pa7.add(cp72);
			table2.addCell(pa7);
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase(stadhar,ftt));
			//cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(0);
			table2.addCell(cee5);
			
			Paragraph pa8 = new Paragraph();
			Chunk cp82 = new Chunk("2 Mother's Name",fnt);
			pa8.add(cp82);
			table2.addCell(pa8);
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase(motherName,ftt));
			//cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(0);
			table2.addCell(cee3);

			table2.addCell(pa7);
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(madhar,ftt));
			//cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(0);
			table2.addCell(cee7);

			
			Paragraph pa9 = new Paragraph();
			Chunk cp92 = new Chunk("3. Father/Guardian's Name",fnt);
			pa9.add(cp92);
			
			table2.addCell(pa9);
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(fatherName,ftt));
			//cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(0);
			table2.addCell(cee8);

			table2.addCell(pa7);
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(fadhar,ftt));
			//ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(0);
			table2.addCell(ceeh);

			
			Paragraph pa10 = new Paragraph();
			Chunk cp12 = new Chunk("4. Nationality",fnt);
			pa10.add(cp12);
			table2.addCell(pa10);
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(nationality,ftt));
			//cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(0);
			table2.addCell(cee9);

			Paragraph pa11 = new Paragraph();
			Chunk cp112 = new Chunk("5. Whether the candidate belongs to SC/ST/OBC Category",fnt);
			pa11.add(cp112);
			table2.addCell(pa11);
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(category,ftt));
			//cee9.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(0);
			table2.addCell(cee10);
			
			Paragraph pa12 = new Paragraph();
			Chunk cp122 = new Chunk("6. Date of Birth according to admission Register(in Figures)",fnt);
			pa12.add(cp122);
			table2.addCell(pa12);
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(dob1,ftt));
			//cee9.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(0);
			table2.addCell(cee11);

			Paragraph pa13 = new Paragraph();
			Chunk cp132 = new Chunk(" (in Words)",fnt);
			pa13.add(cp132);
			table2.addCell(pa13);
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(dobinword,ftt));
			//cee9.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(0);
			table2.addCell(cee12);
			
			Paragraph pa14 = new Paragraph();
			Chunk cp142 = new Chunk("7. Whether the student is failed ?",fnt);
			pa14.add(cp142);
			table2.addCell(pa14);
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(failedOrNot,ftt));
			//ce9.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(0);
			table2.addCell(cee13);
			
			Paragraph pa15 = new Paragraph();
			Chunk cp152 = new Chunk("8. Subject Offered",fnt);
			pa15.add(cp152);
			table2.addCell(pa15);
			PdfPCell cee14 = new PdfPCell();
			cee14.addElement(new Phrase(subjectStudied,ftt));
			//cee9.setBorder(2);
			cee14.setBorderWidthTop(0);
			cee14.setBorderWidthLeft(0);
			cee14.setBorderWidthRight(0);
			cee14.setBorderWidthBottom(0);
			table2.addCell(cee14);
			
			Paragraph pa16 = new Paragraph();
			Chunk cp162 = new Chunk("9. Class in which the pupil last studied(in figures)",fnt);
			pa16.add(cp162);
			table2.addCell(pa16);
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(previousClass,ftt));
			//cee9.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(0);
			table2.addCell(cee15);
			
			
			table2.addCell(pa13);
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(previosClassWord,ftt));
			//cee9.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(0);
			table2.addCell(cee16);
			
			Paragraph pa17 = new Paragraph();
			Chunk cp172 = new Chunk("10. School/Board Annual examination last taken with result",fnt);
			pa17.add(cp172);
			table2.addCell(pa17);
			PdfPCell cee17 = new PdfPCell();
			cee17.addElement(new Phrase(schoolExam,ftt));
			//cee9.setBorder(2);
			cee17.setBorderWidthTop(0);
			cee17.setBorderWidthLeft(0);
			cee17.setBorderWidthRight(0);
			cee17.setBorderWidthBottom(0);
			table2.addCell(cee17);


			table2.addCell(new Phrase("11. Whether qualified for promotion to the higher class?",fnt));
			PdfPCell cee106 = new PdfPCell();
			cee106.addElement(new Phrase(qualifiedPromotion,ftt));
			//cee16.setBorder(2);
			cee106.setBorderWidthTop(0);
			cee106.setBorderWidthLeft(0);
			cee106.setBorderWidthRight(0);
			cee106.setBorderWidthBottom(0);
			table2.addCell(cee106);
			
			table2.addCell(new Phrase("12. Whether the pupil has paid all dues to the School ?",fnt));
			PdfPCell cee166 = new PdfPCell();
			cee166.addElement(new Phrase(monthOfFeePaid,ftt));
			//cee16.setBorder(2);
			cee166.setBorderWidthTop(0);
			cee166.setBorderWidthLeft(0);
			cee166.setBorderWidthRight(0);
			cee166.setBorderWidthBottom(0);
			table2.addCell(cee166);
			
			table2.addCell(new Phrase("13. Whether the pupil was in receipt of any fee concession, if so the nature of such concession ?",fnt));
			PdfPCell cee1666 = new PdfPCell();
			cee1666.addElement(new Phrase(feeConcession,ftt));
			//cee16.setBorder(2);
			cee1666.setBorderWidthTop(0);
			cee1666.setBorderWidthLeft(0);
			cee1666.setBorderWidthRight(0);
			cee1666.setBorderWidthBottom(0);
			table2.addCell(cee1666);
			
			table2.addCell(new Phrase("14. Whether the pupil is NCC Cadet/Boy Scout/ Girl Guide ( give details)",fnt));
			PdfPCell cee20= new PdfPCell();
			cee20.addElement(new Phrase(ncc,ftt));
			//cee20.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(0);
			table2.addCell(cee20);
			
			table2.addCell(new Phrase("15. Date on which pupil name was struck off the rolls of the school",fnt));
			PdfPCell cee24= new PdfPCell();
			cee24.addElement(new Phrase(lastDateStr,ftt));
			//cee24.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(0);
			table2.addCell(cee24);
			
			

			table2.addCell(new Phrase("16. No. of meetings up to date",fnt));
			PdfPCell cee22= new PdfPCell();
			cee22.addElement(new Phrase(workingDays,ftt));
			//cee22.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(0);
			table2.addCell(cee22);
			

			table2.addCell(new Phrase("17. No. of school days the pupil attended",fnt));
			PdfPCell cee21= new PdfPCell();
			cee21.addElement(new Phrase(workingDayPresent,ftt));
			//cee21.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(0);
			table2.addCell(cee21);
			
			table2.addCell(new Phrase("18. General Conduct",fnt));
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(perfom,ftt));
			//cee18.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(0);
			table2.addCell(cee18);

			table2.addCell(new Phrase("19. Reason for leaving the school",fnt));
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(reason,ftt));
			//cee19.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(0);
			table2.addCell(cee19);
			
			

			table2.addCell(new Phrase("20. Any other remarks",fnt));
			PdfPCell cee26= new PdfPCell();
			cee26.addElement(new Phrase(otherRemark,ftt));
			//cee25.setBorder(2);
			cee26.setBorderWidthTop(0);
			cee26.setBorderWidthLeft(0);
			cee26.setBorderWidthRight(0);
			cee26.setBorderWidthBottom(0);
			table2.addCell(cee26);

			

			table2.addCell(new Phrase("21. Date of issue of certificate",fnt));
			PdfPCell cee25= new PdfPCell();
			cee25.addElement(new Phrase(issuDateStr,ftt));
			//cee25.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(0);
			table2.addCell(cee25);


			// table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

		

			Chunk c36 = new Chunk("\n Prepared By___________                              Checked By_________________						Signature of the Principal _________________ ",fo );
		
			
			Chunk c38 = new Chunk("\n Note: This T.C. must be invariably countersigned by the Manager- S.M.C., if is issued by the officiating / Incharge Principal.",fo );

			Paragraph p10 = new Paragraph();
			p10.add(c36);
			p10.add(c38);
			
			document.add(p10);

		}  catch (Exception e) {

			e.printStackTrace();
		}
		

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",fullName.replaceAll(" ", "_")+"_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name(fullName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();


	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLastClass() {
		return lastClass;
	}

	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}

	public String getTcNumber() {
		return tcNumber;
	}

	public void setTcNumber(String tcNumber) {
		this.tcNumber = tcNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPreviousClass() {
		return previousClass;
	}

	public void setPreviousClass(String previousClass) {
		this.previousClass = previousClass;
	}

	public String getPerfom() {
		return perfom;
	}

	public void setPerfom(String perfom) {
		this.perfom = perfom;
	}

	public String getClassname2() {
		return classname2;
	}

	public void setClassname2(String classname2) {
		this.classname2 = classname2;
	}

	public String getDob1() {
		return dob1;
	}

	public void setDob1(String dob1) {
		this.dob1 = dob1;
	}

	public String getStartingdate1() {
		return startingdate1;
	}

	public void setStartingdate1(String startingdate1) {
		this.startingdate1 = startingdate1;
	}

	public String getDobinword() {
		return dobinword;
	}

	public void setDobinword(String dobinword) {
		this.dobinword = dobinword;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getClassname1() {
		return classname1;
	}

	public void setClassname1(String classname1) {
		this.classname1 = classname1;
	}

	public String getAddmissionnumber() {
		return addmissionnumber;
	}

	public void setAddmissionnumber(String addmissionnumber) {
		this.addmissionnumber = addmissionnumber;
	}


	public Date getStruckOffDate() {
		return struckOffDate;
	}

	public void setStruckOffDate(Date struckOffDate) {
		this.struckOffDate = struckOffDate;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public boolean isShowTextBox() {
		return showTextBox;
	}

	public void setShowTextBox(boolean showTextBox) {
		this.showTextBox = showTextBox;
	}

	public boolean isShowTC() {
		return showTC;
	}

	public void setShowTC(boolean showTC) {
		this.showTC = showTC;
	}

	public boolean isShowMainForm() {
		return showMainForm;
	}

	public void setShowMainForm(boolean showMainForm) {
		this.showMainForm = showMainForm;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public StudentInfo getTcList() {
		return tcList;
	}

	public void setTcList(StudentInfo tcList) {
		this.tcList = tcList;
	}

	public ArrayList<StudentInfo> getSerial() {
		return serial;
	}

	public void setSerial(ArrayList<StudentInfo> serial) {
		this.serial = serial;
	}

	public String getIssuDateStr() {
		return issuDateStr;
	}

	public void setIssuDateStr(String issuDateStr) {
		this.issuDateStr = issuDateStr;
	}

	public String getLastDateStr() {
		return lastDateStr;
	}

	public void setLastDateStr(String lastDateStr) {
		this.lastDateStr = lastDateStr;
	}

	public String getSchoolExam() {
		return schoolExam;
	}

	public void setSchoolExam(String schoolExam) {
		this.schoolExam = schoolExam;
	}

	public String getFailedOrNot() {
		return failedOrNot;
	}

	public void setFailedOrNot(String failedOrNot) {
		this.failedOrNot = failedOrNot;
	}

	public String getSubjectStudied() {
		return subjectStudied;
	}

	public void setSubjectStudied(String subjectStudied) {
		this.subjectStudied = subjectStudied;
	}

	public String getQualifiedPromotion() {
		return qualifiedPromotion;
	}

	public void setQualifiedPromotion(String qualifiedPromotion) {
		this.qualifiedPromotion = qualifiedPromotion;
	}

	public String getMonthOfFeePaid() {
		return monthOfFeePaid;
	}

	public void setMonthOfFeePaid(String monthOfFeePaid) {
		this.monthOfFeePaid = monthOfFeePaid;
	}

	public String getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}

	public String getWorkingDayPresent() {
		return workingDayPresent;
	}

	public void setWorkingDayPresent(String workingDayPresent) {
		this.workingDayPresent = workingDayPresent;
	}

	public String getFeeConcession() {
		return feeConcession;
	}

	public void setFeeConcession(String feeConcession) {
		this.feeConcession = feeConcession;
	}

	public String getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(String gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public String getExtraActivity() {
		return extraActivity;
	}

	public void setExtraActivity(String extraActivity) {
		this.extraActivity = extraActivity;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getStadhar() {
		return stadhar;
	}

	public void setStadhar(String stadhar) {
		this.stadhar = stadhar;
	}

	public String getMadhar() {
		return madhar;
	}

	public void setMadhar(String madhar) {
		this.madhar = madhar;
	}

	public String getFadhar() {
		return fadhar;
	}

	public void setFadhar(String fadhar) {
		this.fadhar = fadhar;
	}

	public String getNcc() {
		return ncc;
	}

	public void setNcc(String ncc) {
		this.ncc = ncc;
	}

	public String getPreviosClassWord() {
		return previosClassWord;
	}

	public void setPreviosClassWord(String previosClassWord) {
		this.previosClassWord = previosClassWord;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean isShowpdfbtn() {
		return showpdfbtn;
	}

	public void setShowpdfbtn(boolean showpdfbtn) {
		this.showpdfbtn = showpdfbtn;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getSchoolAffilationNo() {
		return schoolAffilationNo;
	}

	public void setSchoolAffilationNo(String schoolAffilationNo) {
		this.schoolAffilationNo = schoolAffilationNo;
	}

	public String getSchNo() {
		return schNo;
	}

	public void setSchNo(String schNo) {
		this.schNo = schNo;
	}

	public String getSchoolExamWord() {
		return schoolExamWord;
	}

	public void setSchoolExamWord(String schoolExamWord) {
		this.schoolExamWord = schoolExamWord;
	}

	public String getPromotedClassWord() {
		return promotedClassWord;
	}

	public void setPromotedClassWord(String promotedClassWord) {
		this.promotedClassWord = promotedClassWord;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getLastSession() {
		return lastSession;
	}

	public void setLastSession(String lastSession) {
		this.lastSession = lastSession;
	}
	

}