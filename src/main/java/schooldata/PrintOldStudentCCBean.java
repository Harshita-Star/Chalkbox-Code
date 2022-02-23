
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
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@ManagedBean(name = "printOldStdCC")
@ViewScoped
public class PrintOldStudentCCBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String headerImage, session, website, studentName, endSession, affNo, board,lastYear,address,place,motherName,passFail,admDate,removalDate,srno;
	String currentDate = new SimpleDateFormat("dd MMM yyyy").format(new Date());
	String fatherName,gender,lastClass, studentid;
	SchoolInfoList schinfo;
	StudentInfo studentInfo;
	transient StreamedContent file;
	String check="",dobString,dobInWord,remark,result,lastSchoolExam;

	public PrintOldStudentCCBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		
		schinfo = new DatabaseMethods1().fullSchoolInfo(conn);
		affNo = schinfo.getAdd4();
		board = schinfo.getBoardType().toUpperCase();
		studentName = "";

		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		gender=(String) ss.getAttribute("gender");
		studentName=(String) ss.getAttribute("studentName");
		fatherName=(String) ss.getAttribute("fatherName");
		lastClass=(String) ss.getAttribute("lastClass");
		lastYear=(String) ss.getAttribute("lastYear");
		address=(String) ss.getAttribute("address");
		session=(String) ss.getAttribute("lastYear");
		
		place=schinfo.getAdd2();
		motherName=(String) ss.getAttribute("motherName");
		passFail=(String) ss.getAttribute("passFail");
		srno=(String) ss.getAttribute("srno");
		admDate=(String) ss.getAttribute("admDate");
		removalDate=(String) ss.getAttribute("removalDate");
		studentid = (String) ss.getAttribute("studentid");
		result = (String) ss.getAttribute("result");
		remark = (String) ss.getAttribute("remark");
		lastSchoolExam = (String) ss.getAttribute("lastSchoolExam");
		endSession = DatabaseMethods1.selectedSessionDetails(obj.schoolId(),conn).split("-")[1];
		
		if (gender.equalsIgnoreCase("male")) 
		{
			//studentName="Master " + studentName;
			check = "His particulars as per school records are";
		}
		else 
		{
			//studentName="Miss " + studentName;
			check = "Her particulars as per school records are";

		}
		SchoolInfoList info = obj.fullSchoolInfo(conn);
		String savePath = "";
		if (info.getProjecttype().equals("online")) {
			savePath = info.getDownloadpath();
		}
		headerImage = savePath + info.getMarksheetHeader();
		//session = DatabaseMethods1.selectedSessionDetails();
		website = info.getWebsite();
		Date dob;
		String date1 = "";
		studentInfo = obj.studentDetailslistByAddNoAllStatus(obj.schoolId(), studentid, conn);
		if(studentid!=null) {
			dob = studentInfo.getDob();
			if(info.getBranch_id().equalsIgnoreCase("99")||info.getBranch_id().equalsIgnoreCase("101"))
			{
			   date1 = transformSS(dob.getDate());
			}
			else
			{	
			   date1 = transform(dob.getDate());
			}
			String wordmonth = new DatabaseMethods1().monthNameByNumber(dob.getMonth() + 1);
			String year1 = year(dob.getYear() + 1900);
			dobInWord = date1 + " " + wordmonth + " " + year1;
			dobString = studentInfo.getDobString();
		}
	
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String transform(int num) {
		String date1="";
		String one[] = { " ", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
				" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
				" Nineteen" };
		String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
				" Ninety" };
		if (num <= 99) {
			if (num <= 19) {
				date1 = one[num];
			} else {
				int num1 = num / 10;
				int num2 = num % 10;
				date1 = ten[num1] + one[num2];
			}
		}
		return date1;
	}
	
	public String transformSS(int num) {
		String date1="";
		String one[] = { " ", " First", " Second", " Third", " Fourth", " Fifth", " Sixth", " Seventh", " Eighth", " Ninth", " Tenth",
				" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
				" Nineteen" };
		String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
				" Ninety" };
		if (num <= 99) {
			if (num <= 19) {
				date1 = one[num];
			} else {
				int num1 = num / 10;
				int num2 = num % 10;
				date1 = ten[num1] + one[num2];
			}
		}
		return date1;
	}
	
	public String year(int num) {
		String year="";
		String one[] = { " ", " One ", " Two ", " Three ", " Four ", " Five ", " Six ", " Seven ", " Eight ", " Nine ",
				" Ten ", " Eleven ", " Twelve ", " Thirteen ", " Fourteen ", " Fifteen ", " Sixteen ", " Seventeen ",
				" Eighteen ", " Nineteen " };
		String ten[] = { " ", " ", " Twenty ", " Thirty ", " Forty ", " Fifty ", " Sixty ", "Seventy ", " Eighty ",
				" Ninety " };

		if (num >= 1000 && num <= 9999) {
			int num1 = num / 1000;
			int num2 = num % 1000;
			if (num2 <= 99) {
				if (num2 <= 19) {
					year = one[num1] + "Thousand" + one[num2];

				} else {
					int num4 = num2 / 10;
					int num5 = num2 % 10;
					year = one[num1] + "Thousand" + ten[num4] + one[num5];
				}
			} else {
				int num6 = num2 / 100;
				int num7 = num2 % 100;
				if (num7 <= 19) {
					String num8 = one[num7];
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + num8;
				} else {
					int num0 = num7 / 10;
					int num9 = num7 % 10;
					year = one[num1] + "Thousand" + one[num6] + "Hundred" + ten[num0] + one[num9];
				}
			}
		}
		return year;
	}
	
	public SchoolInfoList getSchinfo() {
		return schinfo;
	}

	public void setSchinfo(SchoolInfoList schinfo) {
		this.schinfo = schinfo;
	}

		public void exportCharacterPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(con);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String home = System.getProperty("user.home");

		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();

		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// cb.roundRectangle(30,30,536,790, 10);

		cb.rectangle(45, 712, 510, 0);
		cb.rectangle(190, 659, 222, 0);
		cb.stroke();
		cb.restoreState();

		Font fom = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
		Font fomNor = new Font(FontFamily.HELVETICA, 11, Font.NORMAL);
		Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
		// Header
		try {
	
			String src ="";
			
			  try {
	                if(!ls.getMarksheetHeader().equalsIgnoreCase(""))
	                {
	                	src =ls.getDownloadpath()+ls.getMarksheetHeader();
	                }
	                else
	                {
	                	src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
	                }
			  }
			  catch (Exception e) {
				// TODO: handle exception
			}
			Image im =null;
			try {
				im  = Image.getInstance(src);
				im.setAlignment(Element.ALIGN_CENTER);

				im.scaleAbsoluteHeight(120);
				im.scaleAbsoluteWidth(520);
			} catch (Exception e) {
				// TODO: handle exception
			}
			

			Chunk c = new Chunk(schinfo.schoolName + "\n", fo);

			Chunk c3 =null;
			try {
				c3  = new Chunk(im, 0, -78);
			} catch (Exception e) {
				// TODO: handle exception
			}
			 

			Chunk c1 = new Chunk(schinfo.add1 + " " + schinfo.add2 + "                \n\n", fo);

			Paragraph p1 = new Paragraph();

			// p1.add(c);
			// p1.add(c1);
			p1.add(c3);
			p1.setAlignment(Element.ALIGN_CENTER);

			document.add(p1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		try {
			
			// String[] det1 = studentName.split("/");
			String chec, gender1,sd = "",sondg="",vrb="", opt="";
			if (gender.equalsIgnoreCase("male")) {
				// studentInfo.setFname("Master "+studentInfo.getFname()+" son");
				chec = "he";
				gender1 = "He";
				sd = "Master";
				sondg="son";
				vrb="him";
				opt="his";
			} else if (gender.equalsIgnoreCase("female")) {
				// studentInfo.setFname("Miss "+studentInfo.getFname()+" daughter");
				chec = "she";
				gender1 = "She";
				sd = "Miss";
				sondg="daughter";
				vrb="her";
				opt="her";
			}
			else
			{
				chec = "he/she";
				gender1 = "He/She";
				sd = "Ma./Ms.";
				sondg="son/daughter";
				vrb = "him/her";
				opt="his/her";
			}

			Chunk c6 = new Chunk("\n\n\n\n\n CHARACTER CERTIFICATE\n\nTO WHOMSOEVER IT MAY CONCERN", fo);
			Paragraph p4 = new Paragraph();
			p4.add(c6);
			p4.setAlignment(Element.ALIGN_CENTER);
			document.add(p4);

			Chunk c4 = new Chunk("\nThis is to certify that "+sd+" ", fomNor);
			Paragraph p2 = new Paragraph();
			Chunk c41 = new Chunk(studentName, fom);
			Chunk c42 = new Chunk(" "+sondg+" of Mr. ", fomNor);
			Chunk c43 = new Chunk(fatherName, fom);
			Chunk c44 = new Chunk(" (Father) and Mrs. ", fomNor);
			Chunk c45 = new Chunk(motherName, fom);
			Chunk c46 = new Chunk("(Mother) was a student of class ", fomNor);
			Chunk c47 = new Chunk(lastClass, fom);
			Chunk c471 = new Chunk(" with Admission No. ", fomNor);
			Chunk c472 = new Chunk(srno, fom);
			Chunk c48 = new Chunk(" in session ", fomNor);
			Chunk c49 = new Chunk(lastYear, fom);
			Chunk c491 = new Chunk(".\n\n\n\nDuring "+opt+" academic period from ", fomNor);
			Chunk c492 = new Chunk(admDate, fom);
			Chunk c493 = new Chunk(" to ", fomNor);
			Chunk c494 = new Chunk(removalDate, fom);
			Chunk c50 = new Chunk(
					" , "+chec+" bears a good moral character and has normal behavioural pattern. "+gender1+" has not displayed persistent violent or aggressive behaviour or any desire to harm others. ",
					fomNor);
			Chunk c51 = new Chunk(
					"\n\nI wish "+vrb+" a great future ahead.", fomNor);
			p2.add(c4);
			p2.add(c41);
			p2.add(c42);
			p2.add(c43);
			p2.add(c44);
			p2.add(c45);
			p2.add(c46);
			p2.add(c47);
			p2.add(c471);
			p2.add(c472);
			p2.add(c48);
			p2.add(c49);
			p2.add(c491);
			p2.add(c492);
			p2.add(c493);
			p2.add(c494);
			p2.add(c50);
			p2.add(c51);

			document.add(p2);

			Chunk c9 = new Chunk("\n\n\n\n\n\n\n\n\n\nDate : " + currentDate
					+ "                                                                                                                         Principal",
					fom);
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);
			
			Chunk c10 = new Chunk("\nPlace : " + schinfo.add2
					+ "                                                                                                                         ",
					fom);
			Paragraph p10 = new Paragraph();
			p10.add(c10);
			document.add(p10);

			chec = "";
			gender1 = "";

		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);

		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf", "Character_Certificate.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Character_Certificate.pdf").stream(()->isFromFirstData).build();

		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}

	}
	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
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

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getEndSession() {
		return endSession;
	}

	public void setEndSession(String endSession) {
		this.endSession = endSession;
	}

	public String getAffNo() {
		return affNo;
	}

	public void setAffNo(String affNo) {
		this.affNo = affNo;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}
	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getFatherName() {
		return fatherName;
	}


	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getLastClass() {
		return lastClass;
	}


	public void setLastClass(String lastClass) {
		this.lastClass = lastClass;
	}


	public String getLastYear() {
		return lastYear;
	}


	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPlace() {
		return place;
	}


	public void setPlace(String place) {
		this.place = place;
	}


	public String getMotherName() {
		return motherName;
	}


	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}


	public String getPassFail() {
		return passFail;
	}


	public void setPassFail(String passFail) {
		this.passFail = passFail;
	}


	public String getAdmDate() {
		return admDate;
	}


	public void setAdmDate(String admDate) {
		this.admDate = admDate;
	}


	public String getRemovalDate() {
		return removalDate;
	}


	public void setRemovalDate(String removalDate) {
		this.removalDate = removalDate;
	}


	public String getSrno() {
		return srno;
	}


	public void setSrno(String srno) {
		this.srno = srno;
	}


	public String getStudentid() {
		return studentid;
	}


	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}


	public StudentInfo getStudentInfo() {
		return studentInfo;
	}


	public void setStudentInfo(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}

	public String getDobInWord() {
		return dobInWord;
	}

	public void setDobInWord(String dobInWord) {
		this.dobInWord = dobInWord;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getLastSchoolExam() {
		return lastSchoolExam;
	}


	public void setLastSchoolExam(String lastSchoolExam) {
		this.lastSchoolExam = lastSchoolExam;
	}

}
