package schooldata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Connection;
import java.sql.SQLException;

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
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean(name = "printTC")
@ViewScoped
public class PrintTransferCertificateBean implements Serializable {
	private static final long serialVersionUID = 1L;
	TCInfo selectedTC= new TCInfo();
	String headerImage = "", session, website = "", startYear = "", affNo = "", schNo = "", previosClassWord = "",
			promotedClassWord = "", studentImage = "",rollNo="";
	boolean showmarksheet = false, showBlmMarksheet = false;
	String schid, subjectStudiedWithSpace, schoolCategory, schoolAffilationNo,schoolExamWord;
	transient StreamedContent file;
	boolean showpdfbtn = false;
	boolean sharewoodmarksheet = false, showSSChildrenGirlTc = false, showMapleBearTc = false, northwoodTc = false,
			showSSChildrenTc = false, showCbseDraftTc = false, checkPromtionRender = false, showimmortal = false,
			showPNFTc = false;

	public PrintTransferCertificateBean() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedTC = (TCInfo) ss.getAttribute("tcInfo");
		schid = (String) ss.getAttribute("schoolid");
		SchoolInfoList info = new DatabaseMethods1().fullSchoolInfo(conn);

		if (info.getTcType().equalsIgnoreCase("cbse")) {
			if (info.getBranch_id().equals("99")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				northwoodTc = false;
				showMapleBearTc = false;
				showSSChildrenGirlTc = false;
				showSSChildrenTc = true;
				showPNFTc = false;

			} else if (info.getBranch_id().equals("101")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				northwoodTc = false;
				showMapleBearTc = false;
				showSSChildrenTc = false;
				showSSChildrenGirlTc = true;
				showPNFTc = false;
			} else if (info.getBranch_id().equals("78")) {
				showPNFTc = true;
				showCbseDraftTc = false;
				northwoodTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				showMapleBearTc = false;
				showSSChildrenTc = false;
			} else {
				showCbseDraftTc = true;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				showMapleBearTc = false;
				northwoodTc = false;
				showSSChildrenTc = false;
				showPNFTc = false;
			}
		} else {

			if (info.getBranch_id().equals("93")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = true;
				showBlmMarksheet = false;
				showmarksheet = false;
				showMapleBearTc = false;
				northwoodTc = false;
				showSSChildrenTc = false;
				showPNFTc = false;
			} else if (info.getBranch_id().equals("99")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				northwoodTc = false;
				showMapleBearTc = false;
				showSSChildrenTc = true;
				showSSChildrenGirlTc = false;
				showPNFTc = false;

			} else if (info.getBranch_id().equals("101")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				northwoodTc = false;
				showMapleBearTc = false;
				showSSChildrenTc = false;
				showSSChildrenGirlTc = true;
				showPNFTc = false;
			} else if (info.getBranch_id().equals("88")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				showMapleBearTc = true;
				northwoodTc = false;
				showSSChildrenTc = false;
				showPNFTc = false;
			} else if (info.getBranch_id().equals("4")) {
				showCbseDraftTc = false;
				sharewoodmarksheet = false;
				showBlmMarksheet = false;
				showmarksheet = false;
				showMapleBearTc = false;
				northwoodTc = true;
				showSSChildrenTc = false;
				showPNFTc = false;
			}

			else {
				if (info.getTcType().equalsIgnoreCase("new")) {
					showCbseDraftTc = false;
					showBlmMarksheet = true;
					showmarksheet = false;
					sharewoodmarksheet = false;
					showMapleBearTc = false;
					northwoodTc = false;
					showSSChildrenTc = false;
					showPNFTc = false;
					if (info.getBranch_id().equals("27")) {
						showimmortal = false;
					} else {
						showimmortal = true;
					}
				} else {
					showCbseDraftTc = false;
					showBlmMarksheet = false;
					showmarksheet = true;
					sharewoodmarksheet = false;
					showMapleBearTc = false;
					northwoodTc = false;
					showSSChildrenTc = false;
					showPNFTc = false;
				}
			}
		}

		if (info.getSchid().equals("216") || info.getSchid().equals("221")) {
			showpdfbtn = true;
		}

		String savePath = "";
		if (info.getProjecttype().equals("online")) {
			savePath = info.getDownloadpath();
		}
		headerImage = savePath + info.getTcHeader();
		schoolCategory = info.getSchoolCategory();
		schoolAffilationNo = info.getAdd4();
		session = DatabaseMethods1.selectedSessionDetails(new DatabaseMethods1().schoolId(), conn);
		affNo = info.getAdd4();
		schNo = info.getRegNo();
		website = info.getWebsite();
		previousClassInWord(selectedTC.getClassName());
		promotedClassInWord(selectedTC.getQualifiedPromotion());
		studentImage = savePath + selectedTC.getStudentImage();
		subjectStudiedWithSpace = selectedTC.getSubjectStudied();
		rollNo = selectedTC.getRollNo();

		if (!subjectStudiedWithSpace.equalsIgnoreCase("")) {
			String[] spliter = subjectStudiedWithSpace.split(",");

			subjectStudiedWithSpace = "";
			for (int i = 0; i < spliter.length; i++) {
				subjectStudiedWithSpace += spliter[i] + ", ";
			}

			subjectStudiedWithSpace = subjectStudiedWithSpace.substring(0, subjectStudiedWithSpace.length() - 2);
		}

		if (selectedTC.getQualifiedPromotionCheck().equalsIgnoreCase("YES")
				&& !selectedTC.qualifiedPromotion.trim().equalsIgnoreCase("")) {
			checkPromtionRender = true;
		}
		
		
		if (info.getBranch_id().equals("99")||info.getBranch_id().equals("101")) {
			selectedTC.setDobInWord(selectedTC.getDobInWord().replaceAll("of", " ").toUpperCase());
			selectedTC.setNationality(selectedTC.getNationality().toUpperCase());
			selectedTC.setCategory(selectedTC.getCategory().toUpperCase());
			previosClassWord=previosClassWord.toUpperCase();
			if(!previosClassWord.equals("")) {
				if(previosClassWord.equalsIgnoreCase(selectedTC.getClassName()))
				{
					previosClassWord="";
				}
				else
				{
					previosClassWord="("+previosClassWord.toUpperCase()+")";
				}
			}
			
			
			promotedClassWord=promotedClassWord.toUpperCase();
			if(!promotedClassWord.equals("")) {
				if(promotedClassWord.equalsIgnoreCase(selectedTC.getQualifiedPromotion()))
				{
					promotedClassWord = "";
				}
				else
				{
					promotedClassWord="("+promotedClassWord.toUpperCase()+")";
				}
			}	
			selectedTC.setSubjectStudied(selectedTC.getSubjectStudied().toUpperCase());
			selectedTC.setProofDob(selectedTC.getProofDob().toUpperCase());
			
			// schoolCategory=schoolCategory.toUpperCase();
			
			schoolExamWord(selectedTC.getSchoolExam());
			selectedTC.setSchoolExam(selectedTC.getSchoolExam().toUpperCase());
			schoolExamWord = schoolExamWord.toUpperCase();
			if(!schoolExamWord.equals("")) {
				if(schoolExamWord.equalsIgnoreCase(selectedTC.getSchoolExam()))
				{
					schoolExamWord = "";
				}
				else
				{
					schoolExamWord = "("+schoolExamWord.toUpperCase()+")";
				}
				
			}
			
			selectedTC.setFailedOrNot(selectedTC.getFailedOrNot().toUpperCase());
			selectedTC.setQualifiedPromotion(selectedTC.getQualifiedPromotion().toUpperCase());
			selectedTC.setMonthOfFeePaid(selectedTC.getMonthOfFeePaid().toUpperCase());
			selectedTC.setFeeConcession(selectedTC.getFeeConcession().toUpperCase());
			selectedTC.setOtherRemark(selectedTC.getOtherRemark().toUpperCase());

			
			if (selectedTC.getGamesPlayed().equals("N.A.") && selectedTC.getExtraActivity().equals("N.A.")) {
				selectedTC.setGamesPlayed("N.A.");
			} else if ((selectedTC.getGamesPlayed().equals("") && selectedTC.getExtraActivity().equals("N.A."))||(selectedTC.getGamesPlayed().equals("N.A.") && selectedTC.getExtraActivity().equals(""))) {
				selectedTC.setGamesPlayed("N.A.");
			} else {
				if(selectedTC.getGamesPlayed().equals("")||selectedTC.getGamesPlayed().equals("N.A.")) {
					selectedTC.setGamesPlayed(selectedTC.getExtraActivity().toUpperCase());
				}else if(selectedTC.getExtraActivity().equals("")||selectedTC.getExtraActivity().equals("N.A.")) {
					selectedTC.setGamesPlayed(selectedTC.getGamesPlayed().toUpperCase());
				}
				else {
					selectedTC.setGamesPlayed(
							selectedTC.getGamesPlayed().toUpperCase() + "," + selectedTC.getExtraActivity().toUpperCase());
				}
			}
			startYear = selectedTC.getBookNo();

			
		} else {
			startYear = session.split("-")[0];
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
	


	public void previousClassInWord(String prevclass) {
		if (prevclass.equalsIgnoreCase("I") || prevclass.equalsIgnoreCase("1") || prevclass.equalsIgnoreCase("1st")) {
			previosClassWord = "First";
		} else if (prevclass.equalsIgnoreCase("II") || prevclass.equalsIgnoreCase("2")
				|| prevclass.equalsIgnoreCase("2nd")) {
			previosClassWord = "Second";
		} else if (prevclass.equalsIgnoreCase("III") || prevclass.equalsIgnoreCase("3")
				|| prevclass.equalsIgnoreCase("3rd")) {
			previosClassWord = "Third";
		} else if (prevclass.equalsIgnoreCase("IV") || prevclass.equalsIgnoreCase("4")
				|| prevclass.equalsIgnoreCase("4th")) {
			previosClassWord = "Fourth";
		} else if (prevclass.equalsIgnoreCase("V") || prevclass.equalsIgnoreCase("5")
				|| prevclass.equalsIgnoreCase("5th")) {
			previosClassWord = "Fifth";
		} else if (prevclass.equalsIgnoreCase("VI") || prevclass.equalsIgnoreCase("6")
				|| prevclass.equalsIgnoreCase("6th")) {
			previosClassWord = "Sixth";
		} else if (prevclass.equalsIgnoreCase("VII") || prevclass.equalsIgnoreCase("7")
				|| prevclass.equalsIgnoreCase("7th")) {
			previosClassWord = "Seventh";
		} else if (prevclass.equalsIgnoreCase("VIII") || prevclass.equalsIgnoreCase("8")
				|| prevclass.equalsIgnoreCase("8th")) {
			previosClassWord = "Eighth";
		} else if (prevclass.equalsIgnoreCase("IX") || prevclass.equalsIgnoreCase("9")
				|| prevclass.equalsIgnoreCase("9th")) {
			previosClassWord = "Ninth";
		} else if (prevclass.equalsIgnoreCase("X") || prevclass.equalsIgnoreCase("10")
				|| prevclass.equalsIgnoreCase("10th")) {
			previosClassWord = "Tenth";
		} else if (prevclass.equalsIgnoreCase("XI") || prevclass.equalsIgnoreCase("11")
				|| prevclass.equalsIgnoreCase("11th")) {
			previosClassWord = "Eleventh";
		} else if (prevclass.equalsIgnoreCase("XII") || prevclass.equalsIgnoreCase("12")
				|| prevclass.equalsIgnoreCase("12th")) {
			previosClassWord = "Twelfth";
		} else {
			previosClassWord = prevclass;
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

	public void exportTCAravaliPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);
		SideMenuBean smb = new SideMenuBean();

		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30, 30, 536, 780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba, fed);
		cb.lineTo(cba + 72f * 7.45, fed);

		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font foRed = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		foRed.setColor(BaseColor.RED);
		// Header
		try {

			String src = ls.getDownloadpath() + ls.getTcHeader() + "?pfdrid_c=true";
			// String src = "https://tinyjpg.com/images/social/website.jpg";
//			 // System.out.println(src+"sfdc");
			Image im = null;

			try {
				im = Image.getInstance(src);
			} catch (Exception e) {
				src = "https://coolbackgrounds.io/images/backgrounds/white/pure-white-background-85a2a7fd.jpg";
				im = Image.getInstance(src);
				e.printStackTrace();
			}
			try {
				im.setAlignment(Element.ALIGN_RIGHT);
				im.scaleAbsoluteHeight(80);
				im.scaleAbsoluteWidth(520);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Chunk c999 = null;
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

			Font font = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
			Chunk c8 = null;

			c8 = new Chunk("\n\n\n\n\n                                                     \n", font);

			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] { 1 });
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table.addCell(new Phrase("                                                TRANSFER CERTIFICATE", font));
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Chunk c40 = new Chunk("\n", fonthead);
			Paragraph p11 = new Paragraph();
			p11.add(c40);

//			document.add(p11);  

			PdfPTable tableCe;
			tableCe = new PdfPTable(new float[] { 1, 1 });
			tableCe.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			PdfPCell cell1 = new PdfPCell(new Phrase("TC No.:" + session + "/" + selectedTC.tcNo, fonthead));
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBorder(0);
			tableCe.addCell(cell1);
			PdfPCell cell2 = new PdfPCell(new Phrase("Admission No. : " + selectedTC.srno, fonthead));
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell2.setBorder(0);
			tableCe.addCell(cell2);
			tableCe.setWidthPercentage(100);
			tableCe.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(tableCe);

			Chunk c45 = new Chunk("\n", fonthead);
			Paragraph p15 = new Paragraph();
			p15.add(c45);

			document.add(p15);

			PdfPTable table2;
			Font fnt = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			Font ftt = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);

			table2 = new PdfPTable(new float[] { 1.7f, 1 });
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f, 1.3f);

			// for(int i=1;i<=48;i++) {
			// if(i%2==0) {
			// //// // System.out.println(i);
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			// }
			// else {
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			// }
			// }

			table2.addCell(new Phrase("1. Name of Pupil", fnt));

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(selectedTC.studentName, ftt));
			// cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(0);
			table2.addCell(cee1);

			table2.addCell(new Phrase("2. Father's/Guardian's Name", fnt));
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase("Mr. "+selectedTC.fatherName, ftt));
			// cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(0);
			table2.addCell(cee5);

			table2.addCell(new Phrase("3. Mother's Name", fnt));
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase("Mrs. "+selectedTC.motherName, ftt));
			// cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(0);
			table2.addCell(cee3);

			table2.addCell(new Phrase("4. Nationality", fnt));
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(selectedTC.nationality, ftt));
			// cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(0);
			table2.addCell(cee7);

			table2.addCell(new Phrase("5. Whether the candidate belongs to Schedule Caste or Schedule Tribe", fnt));
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(selectedTC.category, ftt));
			// cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(0);
			table2.addCell(cee8);

			table2.addCell(new Phrase("6. Date of First admission in the school with Class", fnt));
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(selectedTC.joinDateStr + "           Class :- " + selectedTC.addClass, ftt));
			// ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(0);
			table2.addCell(ceeh);

			table2.addCell(
					new Phrase("7. Date of Birth(in Christian Era) according to admission Register(in Fig.)", fnt));
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(selectedTC.dobStr, ftt));
			// cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(0);
			table2.addCell(cee9);

			table2.addCell(new Phrase("   (in Words)", fnt));
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(selectedTC.dobInWord, ftt));
			// cee10.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(0);
			table2.addCell(cee10);

			table2.addCell(new Phrase("8. Class in which the pupil last studied(in figures)", fnt));
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(selectedTC.className, ftt));
			// cee13.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(0);
			table2.addCell(cee13);

			table2.addCell(new Phrase("9. School/Board Annual examination last taken with result", fnt));
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(selectedTC.schoolExam, ftt));
			// cee15.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(0);
			table2.addCell(cee15);

			table2.addCell(new Phrase("10. Whether failed, if so once/twice in the same class  ", fnt));
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(selectedTC.failedOrNot, ftt));
			// cee11.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(0);
			table2.addCell(cee11);

			table2.addCell(new Phrase("11. Subjects Studied  ", fnt));
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(selectedTC.subjectStudied, ftt));
			// cee12.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(0);
			table2.addCell(cee12);

			// table2.addCell(new Phrase(" (in Words) :",fnt));
			// PdfPCell cee14 = new PdfPCell();
			// cee14.addElement(new Phrase(previosClassWord,ftt));
			// cee14.setBorder(2);
			// cee14.setBorderWidthTop(0);
			// table2.addCell(cee14);

			table2.addCell(new Phrase("12. Whether qualified for promotion to the higher class", fnt));
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(selectedTC.qualifiedPromotion, ftt));
			// cee16.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(0);
			table2.addCell(cee16);

			if (smb.getList().getBranch_id().equalsIgnoreCase("98")) {
				table2.addCell(new Phrase("13. Month upto which the school dues paid", fnt));
			} else {
				table2.addCell(new Phrase("13. Month upto which the (pupil has paid ) school dues/paid", fnt));
			}

			PdfPCell cee17 = new PdfPCell();
			cee17.addElement(new Phrase(selectedTC.monthOfFeePaid, ftt));
			// cee17.setBorder(2);
			cee17.setBorderWidthTop(0);
			cee17.setBorderWidthLeft(0);
			cee17.setBorderWidthRight(0);
			cee17.setBorderWidthBottom(0);
			table2.addCell(cee17);

			table2.addCell(new Phrase("14. Total No. of working Days/Meetings", fnt));
			PdfPCell cee21 = new PdfPCell();
			cee21.addElement(new Phrase(selectedTC.workingDays, ftt));
			// cee21.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(0);
			table2.addCell(cee21);

			table2.addCell(new Phrase("15. Total No. of working days/Meetings present", fnt));
			PdfPCell cee22 = new PdfPCell();
			cee22.addElement(new Phrase(selectedTC.workingDayPresent, ftt));
			// cee22.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(0);
			table2.addCell(cee22);

			table2.addCell(new Phrase("16. Any fee concession availed of : if so, the nature of such concession", fnt));
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(selectedTC.feeConcession, ftt));
			// cee18.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(0);
			table2.addCell(cee18);

			table2.addCell(new Phrase("17. Games Played", fnt));
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(selectedTC.gamesPlayed, ftt));
			// cee19.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(0);
			table2.addCell(cee19);

			table2.addCell(new Phrase("18. Extra-curricular activities", fnt));
			PdfPCell cee20 = new PdfPCell();
			cee20.addElement(new Phrase(selectedTC.extraActivity, ftt));
			// cee20.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(0);
			table2.addCell(cee20);

			table2.addCell(new Phrase("19. General Conduct", fnt));
			PdfPCell cee23 = new PdfPCell();
			cee23.addElement(new Phrase(selectedTC.performance, ftt));
			// cee23.setBorder(2);
			cee23.setBorderWidthTop(0);
			cee23.setBorderWidthLeft(0);
			cee23.setBorderWidthRight(0);
			cee23.setBorderWidthBottom(0);
			table2.addCell(cee23);

			table2.addCell(
					new Phrase(ls.getSchid().equalsIgnoreCase("213") ? "20. Date of Leaving/ last attended the class"
							: "20. Date of Application for Certificate", fnt));
			PdfPCell cee50 = new PdfPCell();
			
			if(ls.getSchid().equalsIgnoreCase("213")) {
				cee50.addElement(new Phrase(selectedTC.applicationDateStr, ftt));
			}else {
				cee50.addElement(new Phrase(selectedTC.leavingDateStr, ftt));
			}
			
			// cee50.setBorder(2);
			cee50.setBorderWidthTop(0);
			cee50.setBorderWidthLeft(0);
			cee50.setBorderWidthRight(0);
			cee50.setBorderWidthBottom(0);
			table2.addCell(cee50);

			table2.addCell(new Phrase("21. Date of issue of certificate", fnt));
			PdfPCell cee51 = new PdfPCell();
			cee51.addElement(new Phrase(selectedTC.issueDateStr, ftt));
			// cee51.setBorder(2);
			cee51.setBorderWidthTop(0);
			cee51.setBorderWidthLeft(0);
			cee51.setBorderWidthRight(0);
			cee51.setBorderWidthBottom(0);
			table2.addCell(cee51);

			table2.addCell(new Phrase("22. Reason for leaving the school", fnt));
			PdfPCell cee24 = new PdfPCell();
			cee24.addElement(new Phrase(selectedTC.reason, ftt));
			// cee24.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(0);
			table2.addCell(cee24);

			table2.addCell(new Phrase("23. Any other remarks", fnt));
			PdfPCell cee25 = new PdfPCell();
			cee25.addElement(new Phrase(selectedTC.otherRemark, ftt));
			// cee25.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(0);
			table2.addCell(cee25);

			// table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

			Chunk c36 = new Chunk(
					"\n Prepared By___________                                                                                                       Checked By ",
					fo);

			Chunk c41 = new Chunk(
					"\n I hereby declare that the above information including Name of the candidate,Father's Name,Mother's Name and Date of Birth furnished above is correct as per school records.",
					foRed);

			Chunk c39 = new Chunk(
					"\n\n Date___________                                                                                    Signature of the Principal _________________ \n",
					fo);

			Chunk c38 = new Chunk(" Note:- To Verify Transfer Certificate log on at " + website + "       ", fo);

			Paragraph p10 = new Paragraph();
			p10.add(c36);
			p10.add(c41);
			p10.add(c39);
			p10.add(c38);

			document.add(p10);

		} catch (Exception e) {

			e.printStackTrace();
		}

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				selectedTC.studentName.replaceAll(" ", "_") + "_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name(selectedTC.studentName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();

	}

	public void exportTCCBSEFormatPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);
		SideMenuBean smb = new SideMenuBean();

		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30, 30, 536, 780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba, fed);
		cb.lineTo(cba + 72f * 7.45, fed);

		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font foRed = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		foRed.setColor(BaseColor.RED);
		// Header
		try {

			String src = ls.getDownloadpath() + ls.getTcHeader() + "?pfdrid_c=true";
			// String src = "https://tinyjpg.com/images/social/website.jpg";
			//  // System.out.println(src+"sfdc");
			Image im = null;

			try {
				im = Image.getInstance(src);
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

			Chunk c999 = null;
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

			c8 = new Chunk("\n\n\n\n\n\n                                                     \n", font);

			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] { 1 });
			PdfPCell cellTop = new PdfPCell(new Phrase("TRANSFER CERTIFICATE", foRed));
			cellTop.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cellTop);
			table.setWidthPercentage(40);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Chunk c40 = new Chunk("\n", fonthead);
			Paragraph p11 = new Paragraph();
			p11.add(c40);

			document.add(p11);

			PdfPTable tableCe;
			tableCe = new PdfPTable(new float[] { 1, 1, 1 });
			tableCe.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			PdfPCell cell1 = new PdfPCell(new Phrase("Affiliation No. : " + schoolAffilationNo, fnt));
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBorder(0);
			tableCe.addCell(cell1);
			PdfPCell cell2 = new PdfPCell(new Phrase("             School Code : " + schNo, fnt));
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBorder(0);
			tableCe.addCell(cell2);
			PdfPCell cell3 = new PdfPCell(new Phrase("", fnt));
			cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell3.setBorder(0);
			tableCe.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase("Book No. : 1/" + startYear, fnt));
			cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell4.setBorder(0);
			tableCe.addCell(cell4);
			PdfPCell cell5 = new PdfPCell(new Phrase("Sl No. : " + selectedTC.getTcNo(), fnt));
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell5.setBorder(0);
			tableCe.addCell(cell5);
			PdfPCell cell6 = new PdfPCell(new Phrase("Admission No. :" + selectedTC.srno, fnt));
			cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell6.setBorder(0);
			tableCe.addCell(cell6);

			tableCe.setWidthPercentage(100);
			tableCe.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(tableCe);

			Chunk c45 = new Chunk("\n", fonthead);
			Paragraph p15 = new Paragraph();
			p15.add(c45);

			document.add(p15);

			PdfPTable table2;

			table2 = new PdfPTable(new float[] { 1.7f, 1 });
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f, 1.3f);

			// for(int i=1;i<=48;i++) {
			// if(i%2==0) {
			// //// // System.out.println(i);
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			// }
			// else {
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			// }
			// }

			table2.addCell(new Phrase("1. Name of the Student", fnt));

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(selectedTC.studentName, ftt));
			// cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(1);
			table2.addCell(cee1);

			table2.addCell(new Phrase("2. Mother's Name", fnt));
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase("Mrs. " + selectedTC.motherName, ftt));
			// cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(1);
			table2.addCell(cee5);

			table2.addCell(new Phrase("3. Father's/Guardian's Name", fnt));
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase("Mr. " + selectedTC.fatherName, ftt));
			// cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(1);
			table2.addCell(cee3);

			table2.addCell(new Phrase(
					"4. Date of Birth(in Christian Era) according to Admission & Withdrawal Register(in Figures)",
					fnt));
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(selectedTC.dobStr, ftt));
			// cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(1);
			table2.addCell(cee7);

			table2.addCell(new Phrase("  (in Words)", fnt));
			PdfPCell cee77 = new PdfPCell();
			cee77.addElement(new Phrase(selectedTC.dobInWord, ftt));
			// cee7.setBorder(2);
			cee77.setBorderWidthTop(0);
			cee77.setBorderWidthLeft(0);
			cee77.setBorderWidthRight(0);
			cee77.setBorderWidthBottom(1);
			table2.addCell(cee77);

			table2.addCell(new Phrase("5. Proof for Date of Birth submitted at the time of admission", fnt));
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(selectedTC.proofDob, ftt));
			// cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(1);
			table2.addCell(cee8);

			table2.addCell(new Phrase("6. Nationality", fnt));
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(selectedTC.nationality, ftt));
			// ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(1);
			table2.addCell(ceeh);

			table2.addCell(
					new Phrase("7. Whether the candidate belongs to Schedule Caste or Schedule Tribe or OBC", fnt));
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(selectedTC.category, ftt));
			// cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(1);
			table2.addCell(cee9);

			table2.addCell(new Phrase("8. Date of First admission in the school with Class", fnt));
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(selectedTC.joinDateStr + "       Class :-" + selectedTC.addClass, ftt));
			// cee10.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(1);
			table2.addCell(cee10);

			table2.addCell(new Phrase("9. Class in which the pupil last studied(in figures)", fnt));
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(selectedTC.className, ftt));
			// cee13.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(1);
			table2.addCell(cee13);

			table2.addCell(new Phrase("   (in Words)", fnt));
			PdfPCell cee133 = new PdfPCell();
			cee133.addElement(new Phrase(previosClassWord, ftt));
			// cee13.setBorder(2);
			cee133.setBorderWidthTop(0);
			cee133.setBorderWidthLeft(0);
			cee133.setBorderWidthRight(0);
			cee133.setBorderWidthBottom(1);
			table2.addCell(cee133);

			table2.addCell(new Phrase("10. School/Board Annual examination last taken with result", fnt));
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(selectedTC.schoolExam, ftt));
			// cee15.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(1);
			table2.addCell(cee15);

			table2.addCell(new Phrase("11. Whether failed, if so once/twice in the same class  ", fnt));
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(selectedTC.failedOrNot, ftt));
			// cee11.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(1);
			table2.addCell(cee11);

			table2.addCell(new Phrase("12. Subjects Studied  ", fnt));
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(selectedTC.subjectStudied, ftt));
			// cee12.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(1);
			table2.addCell(cee12);

			// table2.addCell(new Phrase(" (in Words) :",fnt));
			// PdfPCell cee14 = new PdfPCell();
			// cee14.addElement(new Phrase(previosClassWord,ftt));
			// cee14.setBorder(2);
			// cee14.setBorderWidthTop(0);
			// table2.addCell(cee14);

			table2.addCell(new Phrase("13. Whether qualified for promotion to the higher class?", fnt));
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(selectedTC.qualifiedPromotionCheck, ftt));
			// cee16.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(1);
			table2.addCell(cee16);

			table2.addCell(new Phrase("    If so, to which class(in fig)", fnt));
			PdfPCell cee166 = new PdfPCell();
			cee166.addElement(new Phrase(selectedTC.qualifiedPromotion, ftt));
			// cee16.setBorder(2);
			cee166.setBorderWidthTop(0);
			cee166.setBorderWidthLeft(0);
			cee166.setBorderWidthRight(0);
			cee166.setBorderWidthBottom(1);
			table2.addCell(cee166);

			table2.addCell(new Phrase("    (in words)", fnt));
			PdfPCell cee1666 = new PdfPCell();
			cee1666.addElement(new Phrase(promotedClassWord, ftt));
			// cee16.setBorder(2);
			cee1666.setBorderWidthTop(0);
			cee1666.setBorderWidthLeft(0);
			cee1666.setBorderWidthRight(0);
			cee1666.setBorderWidthBottom(1);
			table2.addCell(cee1666);

			table2.addCell(new Phrase("14. Total No. of working days in the academic session", fnt));
			PdfPCell cee21 = new PdfPCell();
			cee21.addElement(new Phrase(selectedTC.workingDays, ftt));
			// cee21.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(1);
			table2.addCell(cee21);

			table2.addCell(new Phrase("15. Total No. of presence in the academic session", fnt));
			PdfPCell cee22 = new PdfPCell();
			cee22.addElement(new Phrase(selectedTC.workingDayPresent, ftt));
			// cee22.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(1);
			table2.addCell(cee22);

			table2.addCell(new Phrase("16. Month upto which the pupil has paid school dues", fnt));
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(selectedTC.monthOfFeePaid, ftt));
			// cee18.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(1);
			table2.addCell(cee18);

			table2.addCell(new Phrase("17. Any fee concession availed of : if so, the nature of such concession", fnt));
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(selectedTC.feeConcession, ftt));
			// cee19.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(1);
			table2.addCell(cee19);

			table2.addCell(new Phrase("18. Whether NCC Cadet/Boy Scout/ Girl Guide ( details may be given)", fnt));
			PdfPCell cee20 = new PdfPCell();
			cee20.addElement(new Phrase(selectedTC.ncc, ftt));
			// cee20.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(1);
			table2.addCell(cee20);

			table2.addCell(new Phrase("19. Whether school is under Govt./Minority/Independent Category", fnt));
			PdfPCell cee23 = new PdfPCell();
			cee23.addElement(new Phrase(schoolCategory, ftt));
			// cee23.setBorder(2);
			cee23.setBorderWidthTop(0);
			cee23.setBorderWidthLeft(0);
			cee23.setBorderWidthRight(0);
			cee23.setBorderWidthBottom(1);
			table2.addCell(cee23);

			table2.addCell(new Phrase(
					"20. Games Played on extra curricular activities in which the pupil usually took part(mention achievement level therein)",
					fnt));
			PdfPCell cee50 = new PdfPCell();
			cee50.addElement(new Phrase(selectedTC.gamesPlayed + " , " + selectedTC.extraActivity, ftt));
			// cee50.setBorder(2);
			cee50.setBorderWidthTop(0);
			cee50.setBorderWidthLeft(0);
			cee50.setBorderWidthRight(0);
			cee50.setBorderWidthBottom(1);
			table2.addCell(cee50);

			table2.addCell(new Phrase("21. Date of application for certificate", fnt));
			PdfPCell cee51 = new PdfPCell();
			cee51.addElement(new Phrase(selectedTC.applicationDateStr, ftt));
			// cee51.setBorder(2);
			cee51.setBorderWidthTop(0);
			cee51.setBorderWidthLeft(0);
			cee51.setBorderWidthRight(0);
			cee51.setBorderWidthBottom(1);
			table2.addCell(cee51);

			table2.addCell(new Phrase("22. Date on which pupil name was struck off the rolls of the school", fnt));
			PdfPCell cee24 = new PdfPCell();
			cee24.addElement(new Phrase(selectedTC.leavingDateStr, ftt));
			// cee24.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(1);
			table2.addCell(cee24);

			table2.addCell(new Phrase("23. Date of issue of certificate", fnt));
			PdfPCell cee25 = new PdfPCell();
			cee25.addElement(new Phrase(selectedTC.issueDateStr, ftt));
			// cee25.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(1);
			table2.addCell(cee25);

			table2.addCell(new Phrase("24. Any other remarks", fnt));
			PdfPCell cee26 = new PdfPCell();
			cee26.addElement(new Phrase(selectedTC.otherRemark, ftt));
			// cee25.setBorder(2);
			cee26.setBorderWidthTop(0);
			cee26.setBorderWidthLeft(0);
			cee26.setBorderWidthRight(0);
			cee26.setBorderWidthBottom(1);
			table2.addCell(cee26);

			// table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

			Chunk c36 = new Chunk(
					"\n\n Date ___________                                                                                                 Signature of the Principal_________________ \n",
					fo);
			Chunk c38 = new Chunk(
					"\nI hereby declare that the above information including Name of the candidate,Father's Name,Mother's Name and Date of Birth furnished above is correct as per school records.",
					foRed);

			Paragraph p10 = new Paragraph();

			p10.add(c38);
			p10.add(c36);

			document.add(p10);

		} catch (Exception e) {

			e.printStackTrace();
		}

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				selectedTC.studentName.replaceAll(" ", "_") + "_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name(selectedTC.studentName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();

	}

	public void exportTCHindiPdf()
			throws DocumentException, IOException, FileNotFoundException, UnsupportedCharsetException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);

		// /Users/neerajrajput/Library/Fonts/Akshar Unicode.ttf

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		// Rectangle rect = new Rectangle(50,50, 300, 300);
		// rect.setBorder(2);
		// rect.setBorderColor(BaseColor.BLACK);
		// document.add(rect);

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30, 30, 536, 780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba, fed);
		cb.lineTo(cba + 72f * 7.45, fed);

		cb.stroke();
		cb.restoreState();

		// Header
		try {

			Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			String src = ls.getDownloadpath() + ls.getMarksheetHeader() + "?pfdrid_c=true";
			// String src ="http://www.chalkboxerp.in/DEMOSCHOOL/header.png?pfdrid_c=true";

			Image im = null;
			try {
				im = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_RIGHT);

				im.scaleAbsoluteHeight(70);
				im.scaleAbsoluteWidth(520);
			} catch (Exception e) {

			}

			Chunk c3 = null;
			try {
				c3 = new Chunk(im, -79, -30);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// Chunk c1 = new Chunk("Padampur Devaliya, Gora Parao, Haldwani, Distt.
			// Nainital \n\n",fo);

			Paragraph p1 = new Paragraph();

			// String[] det = studentName.split("/");

			// p1.add(c);
			// p1.add(c1);

			PdfPTable tabe;

			tabe = new PdfPTable(new float[] { 1 });
			tabe.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tabe.addCell(im);
			tabe.setWidthPercentage(100);
			tabe.setHorizontalAlignment(Element.ALIGN_CENTER);

			p1.add(c3);
			p1.setAlignment(Element.ALIGN_RIGHT);
			document.add(tabe);

			Font font = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
			Chunk c8 = null;

			c8 = new Chunk("\n                                                        \n", font);

			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] { 1 });
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table.addCell(new Phrase("                                                TRANSFER CERTIFICATE", font));
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Chunk c40 = new Chunk("\n  TC No.:" + session + "/" + selectedTC.tcNo
					+ "                                                                                                           Admission No. : "
					+ selectedTC.srno + "\n\n", fonthead);

			Paragraph p11 = new Paragraph();
			p11.add(c40);

			document.add(p11);

			PdfPTable table2;
			Font fnt = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			Font ftt = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);

			BaseFont unicode = BaseFont.createFont("/Users/neerajrajput/Library/Fonts/Akshar Unicode.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			Font hnft = new Font(unicode, 9, Font.NORMAL);

			table2 = new PdfPTable(new float[] { 1.7f, 1 });
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f, 1.3f);

			// for(int i=1;i<=48;i++) {
			// if(i%2==0) {
			// //// // System.out.println(i);
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			// }
			// else {
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			// }
			// }

			table2.addCell(new Phrase("1. à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤•à¤¾ à¤¨à¤¾à¤®", hnft));

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(selectedTC.studentName, ftt));
			// cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(0);
			table2.addCell(cee1);

			table2.addCell(new Phrase("2. à¤ªà¤¿à¤¤à¤¾ /à¤…à¤­à¤¿à¤­à¤¾à¤µà¤• à¤•à¤¾ à¤¨à¤¾à¤®", hnft));
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase(selectedTC.fatherName, ftt));
			// cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(0);
			table2.addCell(cee5);

			table2.addCell(new Phrase("3. à¤®à¤¾à¤¤à¤¾ à¤•à¤¾ à¤¨à¤¾à¤®", hnft));
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase(selectedTC.motherName, ftt));
			// cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(0);
			table2.addCell(cee3);

			table2.addCell(new Phrase("4. à¤°à¤¾à¤·à¥�à¤Ÿà¥�à¤°à¥€à¤¯à¤¤à¤¾", hnft));
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(selectedTC.nationality, ftt));
			// cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(0);
			table2.addCell(cee7);

			table2.addCell(new Phrase(
					"5. à¤•à¥�à¤¯à¤¾ à¤…à¤­à¥�à¤¯à¤°à¥�à¤¥à¥€ à¤…à¤¨à¥�à¤¸à¥‚à¤šà¤¿à¤¤ à¤œà¤¾à¤¤à¤¿ à¤¯à¤¾ à¤…à¤¨à¥�à¤¸à¥‚à¤šà¤¿à¤¤ à¤œà¤¨à¤œà¤¾à¤¤à¤¿ à¤¯à¤¾ à¤…à¤¨à¥�à¤¯ à¤ªà¤¿à¤›à¤¼à¤¡à¥‡ à¤µà¤°à¥�à¤— à¤¸à¥‡ à¤¸à¤®à¥�à¤¬à¤¨à¥�à¤§à¤¿à¤¤ à¤¹à¥ˆ ?", hnft));
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(selectedTC.category, ftt));
			// cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(0);
			table2.addCell(cee8);

			table2.addCell(new Phrase("6. Date of First admission in the school with Class", fnt));
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(selectedTC.joinDateStr + "           Class :- " + selectedTC.addClass, ftt));
			// ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(0);
			table2.addCell(ceeh);

			table2.addCell(new Phrase("7. à¤ªà¥�à¤°à¤µà¥‡à¤¶ à¤ªà¥�à¤¸à¥�à¤¤à¤¿à¤•à¤¾ à¤•à¥‡ à¤…à¤¨à¥�à¤¸à¤¾à¤° à¤œà¤¨à¥�à¤® à¤¤à¤¿à¤¥à¤¿ (à¤…à¤‚à¤•à¥‹ à¤®à¥‡à¤‚)", hnft));
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(selectedTC.dobStr, ftt));
			// cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(0);
			table2.addCell(cee9);

			table2.addCell(new Phrase("   (à¤¶à¤¬à¥�à¤¦à¥‹à¤‚ à¤®à¥‡à¤‚)", hnft));
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(selectedTC.dobInWord, ftt));
			// cee10.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(0);
			table2.addCell(cee10);

			table2.addCell(new Phrase("8. à¤ªà¤¿à¤›à¤²à¥€ à¤•à¤•à¥�à¤·à¤¾ à¤œà¤¿à¤¸à¤®à¥‡ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤…à¤§à¥�à¤¯à¤¯à¤¨à¤°à¤¤ à¤¥à¤¾ (à¤…à¤‚à¤•à¥‹ à¤®à¥‡à¤‚)", hnft));
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(selectedTC.className, ftt));
			// cee13.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(0);
			table2.addCell(cee13);

			table2.addCell(new Phrase("9. à¤ªà¤¿à¤›à¤²à¥‡ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤²à¤¯ / à¤¬à¥‹à¤°à¥�à¤¡ à¤ªà¤°à¥€à¤•à¥�à¤·à¤¾ à¤�à¤µà¤‚ à¤ªà¤°à¤¿à¤£à¤¾à¤®", hnft));
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(selectedTC.schoolExam, ftt));
			// cee15.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(0);
			table2.addCell(cee15);

			table2.addCell(new Phrase("10. à¤•à¥�à¤¯à¤¾ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤•à¤¾ à¤ªà¤°à¥€à¤•à¥�à¤·à¤¾ à¤ªà¤°à¤¿à¤£à¤¾à¤® à¤…à¤¨à¥�à¤¤à¥�à¤¤à¥€à¤°à¥�à¤£ à¤¹à¥ˆ ?", hnft));
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(selectedTC.failedOrNot, ftt));
			// cee11.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(0);
			table2.addCell(cee11);

			table2.addCell(new Phrase("11. à¤ªà¥�à¤°à¤¸à¥�à¤¤à¤¾à¤µà¤¿à¤¤ à¤µà¤¿à¤·à¤¯", hnft));
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(selectedTC.subjectStudied, ftt));
			// cee12.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(0);
			table2.addCell(cee12);

			// table2.addCell(new Phrase(" (in Words) :",fnt));
			// PdfPCell cee14 = new PdfPCell();
			// cee14.addElement(new Phrase(previosClassWord,ftt));
			// cee14.setBorder(2);
			// cee14.setBorderWidthTop(0);
			// table2.addCell(cee14);

			table2.addCell(new Phrase("12. à¤•à¥�à¤¯à¤¾ à¤‰à¤šà¥�à¤š à¤•à¤•à¥�à¤·à¤¾ à¤®à¥‡à¤‚ à¤ªà¤¦à¥‹à¤¨à¥�à¤¨à¤¤à¤¿ à¤•à¤¾ à¤…à¤§à¤¿à¤•à¤¾à¤°à¥€ à¤¹à¥ˆ ? (à¤…à¤‚à¤•à¥‹ à¤®à¥‡à¤‚)", hnft));
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(selectedTC.qualifiedPromotion, ftt));
			// cee16.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(0);
			table2.addCell(cee16);

			table2.addCell(new Phrase("13. à¤•à¥�à¤¯à¤¾ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤¨à¥‡ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤²à¤¯ à¤•à¥€ à¤¸à¤­à¥€ à¤¦à¥‡à¤¯ à¤°à¤¾à¤¶à¤¿ à¤•à¤¾ à¤­à¥�à¤—à¤¤à¤¾à¤¨ à¤•à¤° à¤¦à¤¿à¤¯à¤¾ à¤¹à¥ˆ ?", hnft));
			PdfPCell cee17 = new PdfPCell();
			cee17.addElement(new Phrase(selectedTC.monthOfFeePaid, ftt));
			// cee17.setBorder(2);
			cee17.setBorderWidthTop(0);
			cee17.setBorderWidthLeft(0);
			cee17.setBorderWidthRight(0);
			cee17.setBorderWidthBottom(0);
			table2.addCell(cee17);

			table2.addCell(new Phrase("14. à¤…à¤‚à¤¤à¤¿à¤® à¤¤à¤¿à¤¥à¤¿ à¤¤à¤• à¤‰à¤ªà¤¸à¥�à¤¥à¤¿à¤¤à¤¿à¤¯à¥‹à¤‚ à¤•à¥€ à¤•à¥�à¤² à¤¸à¤‚à¤–à¥�à¤¯à¤¾", hnft));
			PdfPCell cee21 = new PdfPCell();
			cee21.addElement(new Phrase(selectedTC.workingDays, ftt));
			// cee21.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(0);
			table2.addCell(cee21);

			table2.addCell(new Phrase("15. à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤•à¥€ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤²à¤¯ à¤¦à¤¿à¤µà¤¸à¥‹ à¤•à¥€ à¤•à¥�à¤² à¤‰à¤ªà¤¸à¥�à¤¥à¤¿à¤¤à¤¿à¤¯à¤¾à¤‚", hnft));
			PdfPCell cee22 = new PdfPCell();
			cee22.addElement(new Phrase(selectedTC.workingDayPresent, ftt));
			// cee22.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(0);
			table2.addCell(cee22);

			table2.addCell(new Phrase(
					"16. à¤•à¥�à¤¯à¤¾ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤•à¥‹ à¤•à¥‹à¤ˆ à¤¶à¥�à¤²à¥�à¤• à¤°à¤¿à¤¯à¤¾à¤¯à¤¤ à¤ªà¥�à¤°à¤¦à¤¾à¤¨ à¤•à¥€ à¤—à¤¯à¥€ à¤¥à¥€ à¤¯à¤¦à¤¿ à¤¹à¤¾à¤� à¤¤à¥‹ à¤‰à¤¸à¤•à¥€ à¤ªà¥�à¤°à¤•à¥ƒà¤¤à¤¿ ?", hnft));
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(selectedTC.feeConcession, ftt));
			// cee18.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(0);
			table2.addCell(cee18);

			table2.addCell(new Phrase("17. Games Played", fnt));
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(selectedTC.gamesPlayed, ftt));
			// cee19.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(0);
			table2.addCell(cee19);

			table2.addCell(new Phrase("18. Extra-curricular activities", fnt));
			PdfPCell cee20 = new PdfPCell();
			cee20.addElement(new Phrase(selectedTC.extraActivity, ftt));
			// cee20.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(0);
			table2.addCell(cee20);

			table2.addCell(new Phrase("19. à¤¸à¤¾à¤®à¤¾à¤¨à¥�à¤¯ à¤†à¤šà¤°à¤£", hnft));
			PdfPCell cee23 = new PdfPCell();
			cee23.addElement(new Phrase(selectedTC.performance, ftt));
			// cee23.setBorder(2);
			cee23.setBorderWidthTop(0);
			cee23.setBorderWidthLeft(0);
			cee23.setBorderWidthRight(0);
			cee23.setBorderWidthBottom(0);
			table2.addCell(cee23);

			table2.addCell(new Phrase("20. à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤²à¤¯ à¤¸à¥‡ à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤°à¥�à¤¥à¥€ à¤•à¤¾ à¤¨à¤¾à¤® à¤•à¤¾à¤Ÿà¥‡ à¤œà¤¾à¤¨à¥‡ à¤•à¥€ à¤¤à¤¿à¤¥à¤¿", hnft));
			PdfPCell cee50 = new PdfPCell();
			cee50.addElement(new Phrase(selectedTC.leavingDateStr, ftt));
			// cee50.setBorder(2);
			cee50.setBorderWidthTop(0);
			cee50.setBorderWidthLeft(0);
			cee50.setBorderWidthRight(0);
			cee50.setBorderWidthBottom(0);
			table2.addCell(cee50);

			table2.addCell(new Phrase("21. à¤ªà¥�à¤°à¤®à¤¾à¤£ à¤ªà¤¤à¥�à¤° à¤œà¤¾à¤°à¥€ à¤•à¤°à¤¨à¥‡ à¤•à¥€ à¤¤à¤¿à¤¥à¤¿", hnft));
			PdfPCell cee51 = new PdfPCell();
			cee51.addElement(new Phrase(selectedTC.issueDateStr, ftt));
			// cee51.setBorder(2);
			cee51.setBorderWidthTop(0);
			cee51.setBorderWidthLeft(0);
			cee51.setBorderWidthRight(0);
			cee51.setBorderWidthBottom(0);
			table2.addCell(cee51);

			table2.addCell(new Phrase("22. à¤µà¤¿à¤¦à¥�à¤¯à¤¾à¤²à¤¯ à¤›à¥‹à¤¡à¤¼à¤¨à¥‡ à¤•à¤¾ à¤•à¤¾à¤°à¤£", hnft));
			PdfPCell cee24 = new PdfPCell();
			cee24.addElement(new Phrase(selectedTC.reason, ftt));
			// cee24.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(0);
			table2.addCell(cee24);

			table2.addCell(new Phrase("23. à¤•à¥‹à¤ˆ à¤…à¤¨à¥�à¤¯ à¤Ÿà¤¿à¤ªà¥�à¤ªà¤£à¥€", hnft));
			PdfPCell cee25 = new PdfPCell();
			cee25.addElement(new Phrase(selectedTC.otherRemark, ftt));
			// cee25.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(0);
			table2.addCell(cee25);

			// table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

			Chunk c39 = new Chunk("                                       ", font);

			Chunk c36 = new Chunk(
					"\n\n\n\n à¤¤à¥ˆà¤¯à¤¼à¤¾à¤°à¤•à¤°à¥�à¤¤à¤¾ / Prepared By___________                                           à¤œà¥‰à¤‚à¤šà¤•à¤°à¥�à¤¤à¤¾ / Checked By                                                  à¤¹.à¤ªà¥�à¤°à¤§à¤¾à¤¨à¤¾à¤šà¤¾à¤°à¥�à¤¯ / Principal \n",
					hnft);
			Chunk c38 = new Chunk("\nNote:- To Verify Transfer Certificate log on at " + website + "       \n", fo);

			Paragraph p10 = new Paragraph();
			p10.add(c36);

			p10.add(c38);
			p10.add(c39);
			document.add(p10);

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				selectedTC.studentName.replaceAll(" ", "_") + "_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name(selectedTC.studentName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();

	}

	public void exportClasWiseLessPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(new DatabaseMethods1().schoolId(), con);
		SideMenuBean smb = new SideMenuBean();

		try {
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		cb.roundRectangle(30, 30, 536, 780, 10);
		// cb.rectangle(30,30,530,790);

		float cba = 30f;
		float fed = 700f;
		cb.moveTo(cba, fed);
		cb.lineTo(cba + 72f * 7.45, fed);

		cb.stroke();
		cb.restoreState();

		Font fo = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		Font foRed = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
		foRed.setColor(BaseColor.RED);
		// Header
		try {

			String src = ls.getDownloadpath() + ls.getTcHeader() + "?pfdrid_c=true";
			// String src = "https://tinyjpg.com/images/social/website.jpg";
			//  // System.out.println(src+"sfdc");
			Image im = null;

			try {
				im = Image.getInstance(src);
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

			Chunk c999 = null;
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

			c8 = new Chunk("\n\n\n\n\n\n                                                     \n", font);

			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);
			PdfPTable table;
			table = new PdfPTable(new float[] { 1 });
			PdfPCell cellTop = new PdfPCell(new Phrase("TRANSFER CERTIFICATE", foRed));
			cellTop.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cellTop);
			table.setWidthPercentage(40);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(table);

			Font fonthead = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Chunk c40 = new Chunk("\n", fonthead);
			Paragraph p11 = new Paragraph();
			p11.add(c40);

			document.add(p11);

			PdfPTable tableCe;
			tableCe = new PdfPTable(new float[] { 1, 1, 1 });
			tableCe.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			PdfPCell cell2 = new PdfPCell(new Phrase("             School Code : " + smb.list.regNo, fnt));
			cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell2.setBorder(0);
			tableCe.addCell(cell2);

			PdfPCell cell4 = new PdfPCell(new Phrase("Book No. : 1/" + smb.startSession, fnt));
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(0);
			tableCe.addCell(cell4);
			PdfPCell cell5 = new PdfPCell(new Phrase("Sr No. : " + selectedTC.tcNo, fnt));
			cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell5.setBorder(0);
			tableCe.addCell(cell5);

			PdfPCell cell6 = new PdfPCell(new Phrase("Admission No. :" + selectedTC.srno, fnt));
			cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell6.setBorder(0);
			tableCe.addCell(cell6);

			PdfPCell cell1 = new PdfPCell(new Phrase("Affiliation No. : " + affNo, fnt));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBorder(0);
			tableCe.addCell(cell1);

			SideMenuBean s = new SideMenuBean();

			PdfPCell cell3 = new PdfPCell(new Phrase(" School Type : " + smb.list.schType, fnt));
			cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell3.setBorder(0);
			tableCe.addCell(cell3);

			tableCe.setWidthPercentage(100);
			tableCe.setHorizontalAlignment(Element.ALIGN_CENTER);

			document.add(tableCe);

			Chunk c45 = new Chunk("\n", fonthead);
			Paragraph p15 = new Paragraph();
			p15.add(c45);

			document.add(p15);

			PdfPTable table2;

			table2 = new PdfPTable(new float[] { 1.7f, 1 });
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			table2.getDefaultCell().setPaddingTop(2);
			table2.getDefaultCell().setIndent(1.3f);
			table2.getDefaultCell().setLeading(1.3f, 1.3f);

			// for(int i=1;i<=48;i++) {
			// if(i%2==0) {
			// //// // System.out.println(i);
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.BLACK);
			// }
			// else {
			// table2.getDefaultCell().setBorderWidthBottom(0.5f);
			// table2.getDefaultCell().setBorderColorBottom(BaseColor.WHITE);
			// }
			// }

			table2.addCell(new Phrase("1. Name of the Student", fnt));

			PdfPCell cee1 = new PdfPCell();
			cee1.addElement(new Phrase(selectedTC.studentName, ftt));
			// cee1.setBorder(2);
			cee1.setBorderWidthTop(0);
			cee1.setBorderWidthLeft(0);
			cee1.setBorderWidthRight(0);
			cee1.setBorderWidthBottom(0);
			table2.addCell(cee1);

			Paragraph pa7 = new Paragraph();
			Chunk cp72 = new Chunk(" Adhar Card No.", fnt);
			pa7.add(cp72);
			table2.addCell(pa7);
			PdfPCell cee5 = new PdfPCell();
			cee5.addElement(new Phrase(selectedTC.stadhar, ftt));
			// cee5.setBorder(2);
			cee5.setBorderWidthTop(0);
			cee5.setBorderWidthLeft(0);
			cee5.setBorderWidthRight(0);
			cee5.setBorderWidthBottom(0);
			table2.addCell(cee5);

			Paragraph pa8 = new Paragraph();
			Chunk cp82 = new Chunk("2 Mother's Name", fnt);
			pa8.add(cp82);
			table2.addCell(pa8);
			PdfPCell cee3 = new PdfPCell();
			cee3.addElement(new Phrase(selectedTC.motherName, ftt));
			// cee3.setBorder(2);
			cee3.setBorderWidthTop(0);
			cee3.setBorderWidthLeft(0);
			cee3.setBorderWidthRight(0);
			cee3.setBorderWidthBottom(0);
			table2.addCell(cee3);

			table2.addCell(pa7);
			PdfPCell cee7 = new PdfPCell();
			cee7.addElement(new Phrase(selectedTC.madhar, ftt));
			// cee7.setBorder(2);
			cee7.setBorderWidthTop(0);
			cee7.setBorderWidthLeft(0);
			cee7.setBorderWidthRight(0);
			cee7.setBorderWidthBottom(0);
			table2.addCell(cee7);

			Paragraph pa9 = new Paragraph();
			Chunk cp92 = new Chunk("3. Father/Guardian's Name", fnt);
			pa9.add(cp92);

			table2.addCell(pa9);
			PdfPCell cee8 = new PdfPCell();
			cee8.addElement(new Phrase(selectedTC.fatherName, ftt));
			// cee8.setBorder(2);
			cee8.setBorderWidthTop(0);
			cee8.setBorderWidthLeft(0);
			cee8.setBorderWidthRight(0);
			cee8.setBorderWidthBottom(0);
			table2.addCell(cee8);

			table2.addCell(pa7);
			PdfPCell ceeh = new PdfPCell();
			ceeh.addElement(new Phrase(selectedTC.fadhar, ftt));
			// ceeh.setBorder(2);
			ceeh.setBorderWidthTop(0);
			ceeh.setBorderWidthLeft(0);
			ceeh.setBorderWidthRight(0);
			ceeh.setBorderWidthBottom(0);
			table2.addCell(ceeh);

			Paragraph pa10 = new Paragraph();
			Chunk cp12 = new Chunk("4. Nationality", fnt);
			pa10.add(cp12);
			table2.addCell(pa10);
			PdfPCell cee9 = new PdfPCell();
			cee9.addElement(new Phrase(selectedTC.nationality, ftt));
			// cee9.setBorder(2);
			cee9.setBorderWidthTop(0);
			cee9.setBorderWidthLeft(0);
			cee9.setBorderWidthRight(0);
			cee9.setBorderWidthBottom(0);
			table2.addCell(cee9);

			Paragraph pa11 = new Paragraph();
			Chunk cp112 = new Chunk("5. Whether the candidate belongs to SC/ST/OBC Category", fnt);
			pa11.add(cp112);
			table2.addCell(pa11);
			PdfPCell cee10 = new PdfPCell();
			cee10.addElement(new Phrase(selectedTC.category, ftt));
			// cee9.setBorder(2);
			cee10.setBorderWidthTop(0);
			cee10.setBorderWidthLeft(0);
			cee10.setBorderWidthRight(0);
			cee10.setBorderWidthBottom(0);
			table2.addCell(cee10);

			Paragraph pa12 = new Paragraph();
			Chunk cp122 = new Chunk("6. Date of Birth according to admission Register(in Figures)", fnt);
			pa12.add(cp122);
			table2.addCell(pa12);
			PdfPCell cee11 = new PdfPCell();
			cee11.addElement(new Phrase(selectedTC.dobStr, ftt));
			// cee9.setBorder(2);
			cee11.setBorderWidthTop(0);
			cee11.setBorderWidthLeft(0);
			cee11.setBorderWidthRight(0);
			cee11.setBorderWidthBottom(0);
			table2.addCell(cee11);

			Paragraph pa13 = new Paragraph();
			Chunk cp132 = new Chunk(" (in Words)", fnt);
			pa13.add(cp132);
			table2.addCell(pa13);
			PdfPCell cee12 = new PdfPCell();
			cee12.addElement(new Phrase(selectedTC.dobInWord, ftt));
			// cee9.setBorder(2);
			cee12.setBorderWidthTop(0);
			cee12.setBorderWidthLeft(0);
			cee12.setBorderWidthRight(0);
			cee12.setBorderWidthBottom(0);
			table2.addCell(cee12);

			Paragraph pa14 = new Paragraph();
			Chunk cp142 = new Chunk("7. Whether the student is failed ?", fnt);
			pa14.add(cp142);
			table2.addCell(pa14);
			PdfPCell cee13 = new PdfPCell();
			cee13.addElement(new Phrase(selectedTC.failedOrNot, ftt));
			// ce9.setBorder(2);
			cee13.setBorderWidthTop(0);
			cee13.setBorderWidthLeft(0);
			cee13.setBorderWidthRight(0);
			cee13.setBorderWidthBottom(0);
			table2.addCell(cee13);

			Paragraph pa15 = new Paragraph();
			Chunk cp152 = new Chunk("8. Subject Offered", fnt);
			pa15.add(cp152);
			table2.addCell(pa15);
			PdfPCell cee14 = new PdfPCell();
			cee14.addElement(new Phrase(selectedTC.subjectStudied, ftt));
			// cee9.setBorder(2);
			cee14.setBorderWidthTop(0);
			cee14.setBorderWidthLeft(0);
			cee14.setBorderWidthRight(0);
			cee14.setBorderWidthBottom(0);
			table2.addCell(cee14);

			Paragraph pa16 = new Paragraph();
			Chunk cp162 = new Chunk("9. Class in which the pupil last studied(in figures)", fnt);
			pa16.add(cp162);
			table2.addCell(pa16);
			PdfPCell cee15 = new PdfPCell();
			cee15.addElement(new Phrase(selectedTC.className, ftt));
			// cee9.setBorder(2);
			cee15.setBorderWidthTop(0);
			cee15.setBorderWidthLeft(0);
			cee15.setBorderWidthRight(0);
			cee15.setBorderWidthBottom(0);
			table2.addCell(cee15);

			table2.addCell(pa13);
			PdfPCell cee16 = new PdfPCell();
			cee16.addElement(new Phrase(previosClassWord, ftt));
			// cee9.setBorder(2);
			cee16.setBorderWidthTop(0);
			cee16.setBorderWidthLeft(0);
			cee16.setBorderWidthRight(0);
			cee16.setBorderWidthBottom(0);
			table2.addCell(cee16);

			Paragraph pa17 = new Paragraph();
			Chunk cp172 = new Chunk("10. School/Board Annual examination last taken with result", fnt);
			pa17.add(cp172);
			table2.addCell(pa17);
			PdfPCell cee17 = new PdfPCell();
			cee17.addElement(new Phrase(selectedTC.schoolExam, ftt));
			// cee9.setBorder(2);
			cee17.setBorderWidthTop(0);
			cee17.setBorderWidthLeft(0);
			cee17.setBorderWidthRight(0);
			cee17.setBorderWidthBottom(0);
			table2.addCell(cee17);

			table2.addCell(new Phrase("11. Whether qualified for promotion to the higher class?", fnt));
			PdfPCell cee106 = new PdfPCell();
			cee106.addElement(new Phrase(selectedTC.qualifiedPromotionCheck, ftt));
			// cee16.setBorder(2);
			cee106.setBorderWidthTop(0);
			cee106.setBorderWidthLeft(0);
			cee106.setBorderWidthRight(0);
			cee106.setBorderWidthBottom(0);
			table2.addCell(cee106);

			table2.addCell(new Phrase("12. Whether the pupil has paid all dues to the School ?", fnt));
			PdfPCell cee166 = new PdfPCell();
			cee166.addElement(new Phrase(selectedTC.monthOfFeePaid, ftt));
			// cee16.setBorder(2);
			cee166.setBorderWidthTop(0);
			cee166.setBorderWidthLeft(0);
			cee166.setBorderWidthRight(0);
			cee166.setBorderWidthBottom(0);
			table2.addCell(cee166);

			table2.addCell(new Phrase(
					"13. Whether the pupil was in receipt of any fee concession, if so the nature of such concession ?",
					fnt));
			PdfPCell cee1666 = new PdfPCell();
			cee1666.addElement(new Phrase(selectedTC.feeConcession, ftt));
			// cee16.setBorder(2);
			cee1666.setBorderWidthTop(0);
			cee1666.setBorderWidthLeft(0);
			cee1666.setBorderWidthRight(0);
			cee1666.setBorderWidthBottom(0);
			table2.addCell(cee1666);

			table2.addCell(new Phrase("14. Whether the pupil is NCC Cadet/Boy Scout/ Girl Guide ( give details)", fnt));
			PdfPCell cee20 = new PdfPCell();
			cee20.addElement(new Phrase(selectedTC.ncc, ftt));
			// cee20.setBorder(2);
			cee20.setBorderWidthTop(0);
			cee20.setBorderWidthLeft(0);
			cee20.setBorderWidthRight(0);
			cee20.setBorderWidthBottom(0);
			table2.addCell(cee20);

			table2.addCell(new Phrase("15. Date on which pupil name was struck off the rolls of the school", fnt));
			PdfPCell cee24 = new PdfPCell();
			cee24.addElement(new Phrase(selectedTC.leavingDateStr, ftt));
			// cee24.setBorder(2);
			cee24.setBorderWidthTop(0);
			cee24.setBorderWidthLeft(0);
			cee24.setBorderWidthRight(0);
			cee24.setBorderWidthBottom(0);
			table2.addCell(cee24);

			table2.addCell(new Phrase("16. No. of meetings up to date", fnt));
			PdfPCell cee22 = new PdfPCell();
			cee22.addElement(new Phrase(selectedTC.workingDays, ftt));
			// cee22.setBorder(2);
			cee22.setBorderWidthTop(0);
			cee22.setBorderWidthLeft(0);
			cee22.setBorderWidthRight(0);
			cee22.setBorderWidthBottom(0);
			table2.addCell(cee22);

			table2.addCell(new Phrase("17. No. of school days the pupil attended", fnt));
			PdfPCell cee21 = new PdfPCell();
			cee21.addElement(new Phrase(selectedTC.workingDayPresent, ftt));
			// cee21.setBorder(2);
			cee21.setBorderWidthTop(0);
			cee21.setBorderWidthLeft(0);
			cee21.setBorderWidthRight(0);
			cee21.setBorderWidthBottom(0);
			table2.addCell(cee21);

			table2.addCell(new Phrase("18. General Conduct", fnt));
			PdfPCell cee18 = new PdfPCell();
			cee18.addElement(new Phrase(selectedTC.performance, ftt));
			// cee18.setBorder(2);
			cee18.setBorderWidthTop(0);
			cee18.setBorderWidthLeft(0);
			cee18.setBorderWidthRight(0);
			cee18.setBorderWidthBottom(0);
			table2.addCell(cee18);

			table2.addCell(new Phrase("19. Reason for leaving the school", fnt));
			PdfPCell cee19 = new PdfPCell();
			cee19.addElement(new Phrase(selectedTC.reason, ftt));
			// cee19.setBorder(2);
			cee19.setBorderWidthTop(0);
			cee19.setBorderWidthLeft(0);
			cee19.setBorderWidthRight(0);
			cee19.setBorderWidthBottom(0);
			table2.addCell(cee19);

			table2.addCell(new Phrase("20. Any other remarks", fnt));
			PdfPCell cee26 = new PdfPCell();
			cee26.addElement(new Phrase(selectedTC.otherRemark, ftt));
			// cee25.setBorder(2);
			cee26.setBorderWidthTop(0);
			cee26.setBorderWidthLeft(0);
			cee26.setBorderWidthRight(0);
			cee26.setBorderWidthBottom(0);
			table2.addCell(cee26);

			table2.addCell(new Phrase("21. Date of issue of certificate", fnt));
			PdfPCell cee25 = new PdfPCell();
			cee25.addElement(new Phrase(selectedTC.issueDateStr, ftt));
			// cee25.setBorder(2);
			cee25.setBorderWidthTop(0);
			cee25.setBorderWidthLeft(0);
			cee25.setBorderWidthRight(0);
			cee25.setBorderWidthBottom(0);
			table2.addCell(cee25);

			// table2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.setWidthPercentage(100);

			document.add(table2);

			Chunk c36 = new Chunk(
					"\n Prepared By___________                              Checked By_________________						Signature of the Principal _________________ ",
					fo);

			Chunk c38 = new Chunk(
					"\n Note: This T.C. must be invariably countersigned by the Manager- S.M.C., if is issued by the officiating / Incharge Principal.",
					fo);

			Paragraph p10 = new Paragraph();
			p10.add(c36);
			p10.add(c38);

			document.add(p10);

		} catch (Exception e) {

			e.printStackTrace();
		}

		document.close();

		ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf",
//				selectedTC.studentName.replaceAll(" ", "_") + "_TC.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf")
				.name(selectedTC.studentName.replaceAll(" ", "_")+"_TC.pdf").stream(()->isFromFirstData).build();

	}

	public TCInfo getSelectedTC() {
		return selectedTC;
	}

	public void setSelectedTC(TCInfo selectedTC) {
		this.selectedTC = selectedTC;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getSchNo() {
		return schNo;
	}

	public void setSchNo(String schNo) {
		this.schNo = schNo;
	}

	public String getPreviosClassWord() {
		return previosClassWord;
	}

	public void setPreviosClassWord(String previosClassWord) {
		this.previosClassWord = previosClassWord;
	}

	public boolean isShowmarksheet() {
		return showmarksheet;
	}

	public void setShowmarksheet(boolean showmarksheet) {
		this.showmarksheet = showmarksheet;
	}

	public boolean isShowBlmMarksheet() {
		return showBlmMarksheet;
	}

	public void setShowBlmMarksheet(boolean showBlmMarksheet) {
		this.showBlmMarksheet = showBlmMarksheet;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isSharewoodmarksheet() {
		return sharewoodmarksheet;
	}

	public void setSharewoodmarksheet(boolean sharewoodmarksheet) {
		this.sharewoodmarksheet = sharewoodmarksheet;
	}

	public boolean isShowMapleBearTc() {
		return showMapleBearTc;
	}

	public void setShowMapleBearTc(boolean showMapleBearTc) {
		this.showMapleBearTc = showMapleBearTc;
	}

	public boolean isNorthwoodTc() {
		return northwoodTc;
	}

	public void setNorthwoodTc(boolean northwoodTc) {
		this.northwoodTc = northwoodTc;
	}

	public String getPromotedClassWord() {
		return promotedClassWord;
	}

	public void setPromotedClassWord(String promotedClassWord) {
		this.promotedClassWord = promotedClassWord;
	}

	public String getStudentImage() {
		return studentImage;
	}

	public void setStudentImage(String studentImage) {
		this.studentImage = studentImage;
	}

	public boolean isShowSSChildrenTc() {
		return showSSChildrenTc;
	}

	public void setShowSSChildrenTc(boolean showSSChildrenTc) {
		this.showSSChildrenTc = showSSChildrenTc;
	}

	public String getSubjectStudiedWithSpace() {
		return subjectStudiedWithSpace;
	}

	public void setSubjectStudiedWithSpace(String subjectStudiedWithSpace) {
		this.subjectStudiedWithSpace = subjectStudiedWithSpace;
	}

	public boolean isShowCbseDraftTc() {
		return showCbseDraftTc;
	}

	public void setShowCbseDraftTc(boolean showCbseDraftTc) {
		this.showCbseDraftTc = showCbseDraftTc;
	}

	public String getSchoolCategory() {
		return schoolCategory;
	}

	public void setSchoolCategory(String schoolCategory) {
		this.schoolCategory = schoolCategory;
	}

	public String getSchoolAffilationNo() {
		return schoolAffilationNo;
	}

	public void setSchoolAffilationNo(String schoolAffilationNo) {
		this.schoolAffilationNo = schoolAffilationNo;
	}

	public boolean isCheckPromtionRender() {
		return checkPromtionRender;
	}

	public void setCheckPromtionRender(boolean checkPromtionRender) {
		this.checkPromtionRender = checkPromtionRender;
	}

	public boolean isShowimmortal() {
		return showimmortal;
	}

	public void setShowimmortal(boolean showimmortal) {
		this.showimmortal = showimmortal;
	}

	public boolean isShowPNFTc() {
		return showPNFTc;
	}

	public void setShowPNFTc(boolean showPNFTc) {
		this.showPNFTc = showPNFTc;
	}

	public boolean isShowpdfbtn() {
		return showpdfbtn;
	}

	public void setShowpdfbtn(boolean showpdfbtn) {
		this.showpdfbtn = showpdfbtn;
	}

	public boolean isShowSSChildrenGirlTc() {
		return showSSChildrenGirlTc;
	}

	public void setShowSSChildrenGirlTc(boolean showSSChildrenGirlTc) {
		this.showSSChildrenGirlTc = showSSChildrenGirlTc;
	}
	public String getSchoolExamWord() {
		return schoolExamWord;
	}
	public void setSchoolExamWord(String schoolExamWord) {
		this.schoolExamWord = schoolExamWord;
	}
	public String getRollNo() {
		return rollNo;
	}
	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

}
