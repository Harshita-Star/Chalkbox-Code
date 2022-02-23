package schooldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import session_work.QueryConstants;
import session_work.RegexPattern;

@ManagedBean(name="installmentWiseFeeReportBean")
@ViewScoped
public class InstallmentWiseFeeReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	Date feedate=new Date(),endDate;
	String date;
	boolean show,checkLock=false;
	String remark,selectedOperator,session;
	String feetype,studentclass,studentname,fathername,selectedClass,selectedSection;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	double cashAmount,chequeAmount,paymentGateway;
	String cashAmountString,checkAmountString,totalamountString,paymentGatewayString;
	static int count=1;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<SelectItem> classList,sectionList,operatorList;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<StudentInfo> studentList;
	String name;
	double tamount,tdiscount;
	ArrayList<FeeInfo>feelist;
	String otp, otpInput;
	String discoutnNo,schid;
	boolean check,showExcel;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	transient StreamedContent file;
	SchoolInfoList schoolDetails;
	ArrayList<SelectItem> concessionlist = new ArrayList<>();
	String selectedConcession;
	String paymentModeSelected;
	ArrayList<String> paymentModeList = new ArrayList<>();
	ArrayList<SelectItem> installmentList;
	String[] checkMonthSelected = {};

	public InstallmentWiseFeeReportBean()
	{
		selectedOperator = "all";
		Connection conn = DataBaseConnection.javaConnection();
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);
		feedate=new Date();
		endDate=new Date();
		classList=obj.allClass(conn);
		concessionlist = new DatabaseMethods1().allConnsessionType(conn);
		selectedConcession = "All";
		paymentModeList = new DatabaseMethods1().allPaymentType(conn);
		paymentModeSelected = "All";
		installmentList=new DatabaseMethods1().getAllSelectedInstallment(new DatabaseMethods1().schoolId(),conn);
			

		try {
			operatorList = obj.allOperatorList(conn);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		selectedClass = "-1";
		selectedSection="-1";
		
		schid=new DatabaseMethods1().schoolId();
		session=new DatabaseMethods1().selectedSessionDetails(schid, conn);
		
		showdailyfeelist();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showdailyfeelist()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//int registfee = 0,annualfee=0,tutionfee=0,transportfee=0,eaxmfee =0,artFee=0,termfee=0;
		count=1;cashAmount=0.0;chequeAmount=0;tamount=tdiscount=0;
		cashAmountString=checkAmountString=totalamountString="0";
		dailyfee=new ArrayList<>();

		feelist=obj.viewFeeList1(obj.schoolId(),conn);
		ArrayList<String>tempdeatils;
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String	schid = (String) ss.getAttribute("schoolid");
		
		String sectionid="";
		if(selectedClass.equals("-1"))
		{
			sectionid="-1";
		}
		else if(selectedSection.equals("-1"))
		{
			for(SelectItem ii : sectionList)
			{
				if(sectionid.equals(""))
				{
					sectionid = String.valueOf(ii.getValue());
				}
				else
				{
					sectionid = sectionid + "," + String.valueOf(ii.getValue());
				}
			}
		}
		else
		{
			sectionid = selectedSection;
		}

		
		
		String allInstallments = "-1";
		for(int i =0; i<checkMonthSelected.length; i++)
		{
		 if(i==0)
		 {

			 allInstallments = "" ;
			 allInstallments = allInstallments + checkMonthSelected[i];
				
		 }
		 else
		 {
			allInstallments = allInstallments + "," + checkMonthSelected[i];

		 }
		}
		
		if(!allInstallments.equals("-1"))
		{
			if(checkMonthSelected.length==installmentList.size())
			{
				allInstallments="-1";
			}
				
		}
		
		/*if(schid.equals("2") || schid.equals("8") || schid.equals("4") || schid.equals("5") || schid.equals("221") )
		{*/
			HashMap<String, String> tempMap=new HashMap<>();
			//(new Date());
			dailyfee=obj.dailyFeeReportHeaderWiseForinstallment(schid, feedate, sectionid, endDate, tempMap, conn, selectedOperator,selectedConcession,paymentModeSelected,allInstallments);

			
		/*}
		else
		{*//*
			HashMap<String, String> tempMap=new HashMap<>();
			getdailyfeecollection=obj.tempGetfeedailydetail(feedate,selectedSection,endDate,tempMap,conn,selectedOperator);
			ArrayList<String> temp=new ArrayList<>();
			for(Feecollectionc mm:getdailyfeecollection)
			{
				temp.add(mm.getRecipietNo());
			}
			String a[]=new String[temp.size()];
			for(int i=0;i<temp.size();i++)
			{
				a[i]=temp.get(i);
			}
			a=removeDuplicates(a);

			String value="";
			
			Date lockDate = obj.checkLockDate(schid,conn);
			for(int i=0;i<a.length;i++)
			{
				tempdeatils=new ArrayList<>();
				HashMap<String, String> feecllection=new HashMap<>();
				HashMap<String, String> totalAmoutMap=new HashMap<>();
				HashMap<String, String> discountMap=new HashMap<>();
				DailyFeeCollectionBean ll=new DailyFeeCollectionBean();
				int totalamuont=0,totalDiscount=0;

				for(Feecollectionc list : getdailyfeecollection)
				{

					if(list.getRecipietNo().equals(String.valueOf(a[i])))
					{
						ll.setSchid(list.getSchid());
						ll.setStudentname(list.getStudentname());
						ll.setClassname(list.getClassName());
						ll.setFathername(list.getFathername());
						ll.setStudentid(list.getStudentid());
						ll.setUser(list.getUser());
						ll.setSrNo(list.getSrNo());
						ll.setMothername(list.getMotherName());
						ll.setSection(list.getSectionName());
						ll.setFeeRemark(list.getFeeRemark());
						ll.setFeedate(list.getFeedate());
						ll.setChallanDate(list.getChallanDate());
						ll.setDueDateStr(list.getDueDateStr());
						ll.setReciptno(list.getRecipietNo());
						ll.setFeeDateStr(sdf.format(list.getFeedate()));

						if(lockDate == null)
						{
							ll.setDisableCancel(false);
						}
						else
						{
							if(list.getFeedate().before(lockDate) || list.getFeedate().equals(lockDate))
							{
								ll.setDisableCancel(true);
							}
							else
							{
								ll.setDisableCancel(false);
							}
						}

						String feetype=list.getFeeName();
						feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
						discountMap.put(feetype, String.valueOf(list.getDiscount()));
						totalAmoutMap.put(feetype, String.valueOf(list.getTotalAmount()));
						ll.setAllFess(feecllection);
						ll.setAllDiscount(discountMap);
						ll.setAllTotalAmount(totalAmoutMap);
						ll.setBankname(list.getBankname());
						ll.setChequenumber(list.getChequeno());
						ll.setPaymentmode(list.getPaymentmode());
						if(tempdeatils.size()==0)
						{
							value="orignal";
						}
						else
						{
							for(String ls:tempdeatils)
							{
								if(ls.equals(feetype))
								{
									value="duplicate";
									break;
								}
								else
								{
									value="orignal";
								}
							}
						}

						if(value.equals("orignal"))
						{
							tempdeatils.add(feetype);
							totalamuont+=list.getFeeamunt();
							totalDiscount+=list.getDiscount();
						}

						ll.setAmount(String.valueOf(totalamuont));
						ll.setDiscount(String.valueOf(totalDiscount));
						ll.setPaymentmode(list.getPaymentmode());
						ll.setSrno(count);

					}



				}
				dailyfee.add(ll);
				// registfee = 0;annualfee=0;tutionfee=0;transportfee=0;eaxmfee = 0;artFee=0;termfee=0;
				count++;
				show=true;
			}			
	*//*}*/
		

		if(dailyfee.size()>0)
		{
			for(DailyFeeCollectionBean info:dailyfee)
			{
				if(info.getPaymentmode().equalsIgnoreCase("Cash"))
				{
					cashAmount+=Integer.parseInt(info.getAmount());
				}
				else if(info.getPaymentmode().equalsIgnoreCase("PAYTM"))
				{
					paymentGateway+=Integer.parseInt(info.getAmount());
				}
				else
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}

				tdiscount+=Integer.parseInt(info.getDiscount());
			}
			tamount=cashAmount+chequeAmount+paymentGateway;

			cashAmountString=String.format ("%.0f", cashAmount);
			checkAmountString=String.format ("%.0f", chequeAmount);
			paymentGatewayString=String.format ("%.0f", paymentGateway);
			totalamountString=String.format ("%.0f", tamount);
            showExcel =true;  
		}
		else
		{
			 showExcel =false;
		}

		date=sdf.format(new Timestamp(feedate.getTime()))+"-"+sdf.format(new Timestamp(endDate.getTime()));
		show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String checkLock()
	{
		String messageForLock="";
		Connection conn = DataBaseConnection.javaConnection();
		Date lockDate = new DatabaseMethods1().checkLockDate(schid,conn);
		if(lockDate == null)
		{

			checkLock=false;
		}
		else
		{
			if(new Date().after(lockDate))
			{
				checkLock=false;
			}
			else
			{
				checkLock=true;
				String strDt = new SimpleDateFormat("dd-MM-yyyy").format(lockDate);
				messageForLock = "Note : You have locked your fees modifications upto Date : "+strDt;

			}
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageForLock;
	}

	public void checkcancelOTP()
	{
		String messageForLock=checkLock();
		Connection conn=DataBaseConnection.javaConnection();
		if(checkLock==false)
		{
			SchoolInfoList lst=obj.fullSchoolInfo(conn);
			////(lst.getCancalfee());
			if(lst.getCancalfee().equalsIgnoreCase("yes"))
			{
				int randomPIN = (int) (Math.random() * 9000) + 1000;
				otp = String.valueOf(randomPIN);
				discoutnNo = lst.getCancelOtpNo();
				String typemessage = "Hello Sir,\nSomeone wants to cancel a fee receipt.Use given OTP ("
						+ randomPIN + ") to allow for receipt cancellation.Treat this as confidential.Thank You.\n"
						+ lst.getSmsSchoolName();
				obj.messageurlStaff(lst.getCancelOtpNo(), typemessage, "admin", conn,obj.schoolId(),"");
				check=true;
				PrimeFaces.current().executeInitScript("PF('cancelfee').show();");
				PrimeFaces.current().ajax().update("form");
				PrimeFaces.current().ajax().update("form2");
			}
			else
			{
				check=false;
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
			PrimeFaces.current().ajax().update("form");
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList = obj.searchStudentList(query,conn);
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList)
		{
			//studentListt.add(info.getFname() + "-" + info.getAddNumber());
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void duplicateFee()
	{
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		ArrayList<FeeInfo> selectedFees=new ArrayList<>();
		int i=1;
		Connection conn=DataBaseConnection.javaConnection();
		
		DatabaseMethods1 obj=new DatabaseMethods1();
		ArrayList<Feecollectionc>feesSelected=obj.studetFeeCollectionByRecipietNo(selectedstudent.getStudentid(),selectedstudent.getReciptno(),selectedstudent.getSchid(),conn);
		
		
		
		

		for(Feecollectionc ff:feesSelected)
		{
			/*String fee=selectedstudent.getAllFess().get(ff.getFeeId());
			String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
			String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
			if(fee==null)
			{

			}
			else
			{*/

				FeeInfo info=new FeeInfo();
				info.setSno(i);
				info.setFeeName(ff.getFeeName());
				info.setPayAmount(ff.getFeeamunt());
				info.setPayDiscount(ff.getDiscount());
				info.setDueamount(String.valueOf(ff.getTotalAmount()));

				selectedFees.add(info);
				i=i+1;
			/*}*/
		}

		
		if(feesSelected.get(0).getIntallment().equals(""))
		{
			rr.setAttribute("feeupto", selectedstudent.getDueDateStr());
			
		}
		else
		{
			rr.setAttribute("feeupto",feesSelected.get(0).getIntallment() );
			
		}

		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=obj;

		feelist=DBM.viewFeeList1(DBM.schoolId(),conn);

		dailyfee=new ArrayList<>();
		//int registfee = 0,annualfee=0,tutionfee=0,transportfee=0,eaxmfee =0,artFee=0,termfee=0;
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		/*int index = name.indexOf("-") + 1;
		String id = name.substring(index);*/
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);

		getdailyfeecollection = DBM.getduplicatefeedetail(id,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),conn);
		ArrayList<String> temp = new ArrayList<>();
		for (Feecollectionc mm : getdailyfeecollection)
		{
			temp.add(mm.getRecipietNo());
		}

		String a[] = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++)
		{
			a[i] = temp.get(i);
		}
		a = removeDuplicates(a);

		String value="";
		ArrayList<String>tempdeatils;

		for (int i = 0; i < a.length; i++)
		{
			tempdeatils=new ArrayList<>();
			HashMap<String, String> feecllection=new HashMap<>();
			HashMap<String, String> discountMap=new HashMap<>();
			HashMap<String, String> totalAmountMap=new HashMap<>();

			int totalamuont=0,totalDiscount=0;
			DailyFeeCollectionBean ll = new DailyFeeCollectionBean();
			ArrayList<String> tempList=objStd.basicFieldsForStudentList();
			
			for (Feecollectionc list : getdailyfeecollection)
			{
				if (list.getRecipietNo().equals(String.valueOf(a[i])))
				{

					ll.setFeedate(list.getFeedate());
					ll.setReciptno(list.getRecipietNo());
					ll.setFeeDateStr(sdf.format(list.getFeedate()));
					String feetype=list.getFeeName();
					ll.setDueDateStr(list.getDueDateStr());
					ll.setBankname(list.getBankname());
					ll.setChequenumber(list.getChequeno());
					feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
					discountMap.put(feetype, String.valueOf(list.getDiscount()));
					totalAmountMap.put(feetype, String.valueOf(list.getTotalAmount()));
					ll.setAllFess(feecllection);
					ll.setAllDiscount(discountMap);
					ll.setAllTotalAmount(totalAmountMap);
					
					StudentInfo info=objStd.studentDetail(id,"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC,null,null,"","","","", session, schid, tempList, conn).get(0);
					
					ll.setSrNo(info.getSrNo());
					ll.setStudentname(info.getFname());
					ll.setUser(list.getUser());
					ll.setClassname(info.getClassName().substring(0,info.getClassName().indexOf("-")));
					ll.setSection(info.getSectionName());
					ll.setFathername(info.getFathersName());
					ll.setFeeRemark(list.getFeeRemark());
					ll.setMothername(info.getMotherName());
					ll.setChallanDate(list.getChallanDate());
					if(tempdeatils.size()==0)
					{

						value="orignal";
					}
					else
					{
						for(String ls:tempdeatils)
						{
							if(ls.equals(feetype))
							{
								value="duplicate";
								break;
							}
							else
							{
								value="orignal";
							}
						}

					}

					if(value.equals("orignal"))
					{
						tempdeatils.add(feetype);
						totalamuont+=list.getFeeamunt();
						totalDiscount+=list.getDiscount();
					}

					ll.setAmount(String.valueOf(totalamuont));
					ll.setDiscount(String.valueOf(totalDiscount));
					ll.setPaymentmode(list.getPaymentmode());
					ll.setSrno(count);

				}


			}
			dailyfee.add(ll);
			count++;
			//registfee = 0; annualfee = 0; transportfee = 0; eaxmfee = 0;termfee = 0; artFee = 0;
			show = true;
		}
		/*Collections.sort(dailyfee);
		 for(int i=0;i<dailyfee.size();i++)
		 {
			dailyfee.get(i).setSrno(i+1);
		 }*/
		if(dailyfee.size()>0)
		{
			for(DailyFeeCollectionBean info:dailyfee)
			{
				if(info.getPaymentmode().equalsIgnoreCase("Cash"))
				{
					cashAmount+=Integer.parseInt(info.getAmount());
				}
				else
				{
					chequeAmount+=Integer.parseInt(info.getAmount());
				}
				tdiscount+=Integer.parseInt(info.getDiscount());
			}
			tamount=cashAmount+chequeAmount;
		}

		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
	}

	public void cancelReceipt()
	{
		Connection conn = DataBaseConnection.javaConnection();
		boolean checkotp=false;
		if(check==true)
		{
			if(otp.equalsIgnoreCase(otpInput))
			{
				checkotp=false;
			}
			else
			{
				checkotp=true;
			}
		}

		if(checkotp==false) {

			int i=obj.cancelReceipt(selectedstudent.getReciptno(),remark,conn);
			if(i>=1)
			{
				if(selectedstudent.getAllFess().get("-1")==null || selectedstudent.getAllFess().get("-1").equals(""))
				{

				}
				else
				{
					int prevFee = Integer.valueOf(selectedstudent.getAllFess().get("-1"));
					StudentInfo info=new StudentInfo();
					info.setAddNumber(selectedstudent.getStudentid());
					int paidPrevFee = obj.FeePaidRecord(obj.schoolId(),info, session, "-1",conn);
					//int paidPrevFee = obj.previousFeePaidRecord(selectedstudent.getStudentid(), conn);

					//obj.cancelPaidPreviousFee(selectedstudent.getStudentid(),newPrevFee,conn);
				}

				////("Prev Fee Paid : "+prevFee);
				showdailyfeelist();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Receipt Cancelled Sucessfully"));
				//show=false;
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured"));
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Not Matched"));

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public  void exportDailyPdf() throws DocumentException, IOException, FileNotFoundException {

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
			Chunk c3 =null;
			try {
				c3 = new Chunk(im, -250, 15);
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
			Chunk c8 = new Chunk("\n                                                Daily Fee Collection Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n Date : "+date+"           Total Amount : " +       totalamountString+"          Total Discount : "+tdiscount+" \n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5);
			PdfPTable table = new PdfPTable(14+feelist.size());

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("Sno",font));
			table.addCell(new Phrase("Sr. No.",font));
			table.addCell(new Phrase("Student Name",font));
			table.addCell(new Phrase("Father Name",font));
			table.addCell(new Phrase("Class",font));
			table.addCell(new Phrase("Section",font));
			table.addCell(new Phrase("Receipt No.",font));
			table.addCell(new Phrase("Installment.",font));
			table.addCell(new Phrase("Session",font));

			for(int i=0;i<feelist.size();i++) {
				table.addCell(new Phrase(String.valueOf(feelist.get(i).getFeeName()),font));

			}
			table.addCell(new Phrase("Total Amount",font));
			table.addCell(new Phrase("Total Discount",font));
			table.addCell(new Phrase("Payment Mode",font));
			table.addCell(new Phrase("Date",font));
			table.addCell(new Phrase("User",font));

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});
            int pt = 1;
			for (int i=0;i<dailyfee.size();i++){

				table.addCell(new Phrase(String.valueOf(pt++),font));
				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getSrNo()),font));
				table.addCell(new Phrase(dailyfee.get(i).getStudentname(),font));
				table.addCell(new Phrase(dailyfee.get(i).getFathername(),font));
				table.addCell(new Phrase(dailyfee.get(i).getClassname(),font));
				table.addCell(new Phrase(dailyfee.get(i).getSection(),font));
				table.addCell(new Phrase(dailyfee.get(i).getReciptno(),font));
				table.addCell(new Phrase(dailyfee.get(i).getInstallmentName(),font));
				table.addCell(new Phrase(dailyfee.get(i).getSession(),font));
				for(int j=0;j<feelist.size();j++) {
					table.addCell(new Phrase(dailyfee.get(i).getAllFess().get(feelist.get(j).getFeeId()),font));

				}
				table.addCell(new Phrase(dailyfee.get(i).getAmount(),font));
				table.addCell(new Phrase(dailyfee.get(i).getDiscount(),font));
				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getPaymentmode()+"-"+dailyfee.get(i).getBankname()+"-"+dailyfee.get(i).getChequenumber()),font));
				table.addCell(new Phrase(dailyfee.get(i).getFeeDateStr(),font));
				table.addCell(new Phrase(dailyfee.get(i).getUser(),font));
			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Daily_Fee_Collection_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Daily_Fee_Collection_Report.pdf").stream(()->isFromFirstData).build();



		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}



	}

	public  void exportMiniDailyPdf() throws DocumentException, IOException, FileNotFoundException {

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

			String src ="";
			try {
				src =ls.getDownloadpath()+ls.getImagePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
			 
			
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
				c3 = new Chunk(im, -250, 15);
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
			Chunk c8 = new Chunk("\n                                                   Daily Fee Collection Report \n\n",fo );
			Paragraph p8 = new Paragraph();
			p8.add(c8);
			document.add(p8);
			p8.setAlignment(Element.ALIGN_CENTER);

			Chunk c9 = new Chunk("\n Date : "+date+"               Total Amount : "+totalamountString+"                 Total Discount : "+tdiscount+" \n\n",fo );
			Paragraph p9 = new Paragraph();
			p9.add(c9);
			document.add(p9);





			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
			PdfPTable table = new PdfPTable(11);

			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell("Sno");

			table.addCell("Student Name");
			table.addCell("Father Name");
			table.addCell("Class");
			table.addCell("Section");
			table.addCell("Receipt No.");


			table.addCell("Total Amount");
			table.addCell("Total Discount");
			table.addCell("Payment Mode");
			table.addCell("Date");
			table.addCell("User");

			table.setHeaderRows(1);

			// BaseColor bs = new BaseColor(45, 20, 35);
			PdfPCell[] cells = table.getRow(0).getCells();
			for(int i=0;i<cells.length;i++)
			{
				cells[i].setBackgroundColor(new BaseColor(242, 234, 221));

				cells[i].setBorderWidth(2);

			}
			//table.setWidths(new int[]{1, 1 ,1,1,1,1,1,1,1,1,1});

			for (int i=0;i<dailyfee.size();i++){

				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getSrno()),font));

				table.addCell(new Phrase(dailyfee.get(i).getStudentname(),font));
				table.addCell(new Phrase(dailyfee.get(i).getFathername(),font));
				table.addCell(new Phrase(dailyfee.get(i).getClassname(),font));
				table.addCell(new Phrase(dailyfee.get(i).getSection(),font));
				table.addCell(new Phrase(dailyfee.get(i).getReciptno(),font));

				table.addCell(new Phrase(dailyfee.get(i).getAmount(),font));
				table.addCell(new Phrase(dailyfee.get(i).getDiscount(),font));
				table.addCell(new Phrase(String.valueOf(dailyfee.get(i).getPaymentmode()+"-"+dailyfee.get(i).getBankname()+"-"+dailyfee.get(i).getChequenumber()),font));
				table.addCell(new Phrase(dailyfee.get(i).getFeeDateStr(),font));
				table.addCell(new Phrase(dailyfee.get(i).getUser(),font));
			}


			table.setWidthPercentage(110);
			document.add(table);





		} catch (Exception e) {

			e.printStackTrace();
		}
		Paragraph p2 = new Paragraph("\n");
		document.add(p2);



		document.close();

		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		file = new DefaultStreamedContent(isFromFirstData, "application/pdf","Mini_Daily_Fee_Collection_Report.pdf");
		file = new DefaultStreamedContent().builder().contentType("application/pdf").name("Mini_Daily_Fee_Collection_Report.pdf").stream(()->isFromFirstData).build();


		try {
			con.close();
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}




	}

	public void toNumDailyMiniFee(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		new SimpleDateFormat("dd/MM/yyyy");


		sheet.createFreezePane(0, 3);


		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow= sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow+1, 1);
		XSSFRow r1 =     sheet.createRow(1);
		r1.setHeight((short) 600);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//     styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		//    styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue("Date :-");
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue(date);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total Amount(by Cash):- ");
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue(cashAmountString);
		clee6.setCellStyle(styleb);

		XSSFCell clee7 = r1.createCell(4);
		clee7.setCellValue("Total Amount(by Cheque):-");
		clee7.setCellStyle(styleb);
		XSSFCell clee8 = r1.createCell(5);
		clee8.setCellValue(checkAmountString);
		clee8.setCellStyle(styleb);
		XSSFCell clee9 = r1.createCell(6);
		clee9.setCellValue("Total Amount");
		clee9.setCellStyle(styleb);
		XSSFCell clee10 = r1.createCell(7);
		clee10.setCellValue(totalamountString);
		clee10.setCellStyle(styleb);


//		styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
//		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int o=8;o<colCount;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}



		XSSFRow he = sheet.createRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



		try
		{
			URL url = new URL(schoolDetails.downloadpath+schoolDetails.marksheetHeader);
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			// FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(8);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();




		} catch (IOException ioex) {
			//("fgg");
		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);

		XSSFRow headerRow = sheet.getRow(2);
		headerRow.setHeight((short)400);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//   Font font = book.createFont();
		//  font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
//			if(i==0) {
//				ce1.setCellValue(" ");
//			}
			ce1.setCellStyle(style);
		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) st).setFillForegroundColor(color1);
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setBorderLeft(BorderStyle.THIN);
		st.setBorderRight(BorderStyle.THIN);
		st.setBorderTop(BorderStyle.THIN);
		st.setBorderBottom(BorderStyle.THIN);

		st.setBottomBorderColor(IndexedColors.RED.getIndex());
		st.setTopBorderColor(IndexedColors.RED.getIndex());
		st.setLeftBorderColor(IndexedColors.RED.getIndex());
		st.setRightBorderColor(IndexedColors.RED.getIndex());




		sheet.getRow(5);
		double total=0.0;
		double[] sum = new double[colCount+1];


		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <6 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 8; cellInd <11 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 6; cellInd <8 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);

				String strVal = cell.getStringCellValue();


				try {
					Double ds = Double.valueOf(strVal);

					if((cellInd>=6)&&(cellInd<8))
					{
						total+=ds;
					}
					//("total"+total);
					if(rowInd==3)
					{
						sum[cellInd] = total;

					}
					else
					{
						sum[cellInd] =sum[cellInd]+total;

					}
					total=0.0;

					cell.setCellType(Cell.CELL_TYPE_NUMERIC);

					DataFormat fmt = book.createDataFormat();
					CellStyle sty = book.createCellStyle();

					if(rowInd%2==0) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
						((XSSFCellStyle) sty).setFillForegroundColor(color6);
						sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					else {
						XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
						((XSSFCellStyle) sty).setFillForegroundColor(color2);
						sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}


					sty.setDataFormat(fmt.getFormat("#,##0.00"));
					sty.setBorderLeft(BorderStyle.THIN);
					sty.setBorderRight(BorderStyle.THIN);
					sty.setBorderTop(BorderStyle.THIN);
					sty.setBorderBottom(BorderStyle.THIN);



					sty.setBottomBorderColor(IndexedColors.RED.getIndex());
					sty.setTopBorderColor(IndexedColors.RED.getIndex());
					sty.setLeftBorderColor(IndexedColors.RED.getIndex());
					sty.setRightBorderColor(IndexedColors.RED.getIndex());
					cell.setCellStyle(sty);

					cell.setCellValue(ds);

				}
				catch (Exception e) {
					// TODO: handle exception
				}

			}

		}


		XSSFRow roo=sheet.createRow(rowCount+2);

		//("sum series");
		for(int h=0;h<sum.length;h++) {
			//(h+"-"+sum[h]);
		}



		for(int f=6;f<8;f++)
		{
			XSSFCell cell1=roo.createCell(f);

			cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
			DataFormat fmt = book.createDataFormat();
			CellStyle sty = book.createCellStyle();
			XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
			((XSSFCellStyle) sty).setFillForegroundColor(color7);
			sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			sty.setBorderLeft(BorderStyle.THIN);
			sty.setBorderRight(BorderStyle.THIN);
			sty.setBorderTop(BorderStyle.THIN);
			sty.setBorderBottom(BorderStyle.THIN);

			sty.setBottomBorderColor(IndexedColors.RED.getIndex());
			sty.setTopBorderColor(IndexedColors.RED.getIndex());
			sty.setLeftBorderColor(IndexedColors.RED.getIndex());
			sty.setRightBorderColor(IndexedColors.RED.getIndex());
			sty.setDataFormat(fmt.getFormat("#,##0.00"));
			cell1.setCellStyle(sty);
			cell1.setCellValue(sum[f]);
		}
		XSSFCell cell222=roo.createCell(5);
		CellStyle sty = book.createCellStyle();
		XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
		((XSSFCellStyle) sty).setFillForegroundColor(color7);
		sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sty.setBorderLeft(BorderStyle.THIN);
		sty.setBorderRight(BorderStyle.THIN);
		sty.setBorderTop(BorderStyle.THIN);
		sty.setBorderBottom(BorderStyle.THIN);

		sty.setBottomBorderColor(IndexedColors.RED.getIndex());
		sty.setTopBorderColor(IndexedColors.RED.getIndex());
		sty.setLeftBorderColor(IndexedColors.RED.getIndex());
		sty.setRightBorderColor(IndexedColors.RED.getIndex());

		cell222.setCellStyle(sty);
		cell222.setCellValue("Total");

	}

	public void toNumDailyFee(Object doc)
	{
		XSSFWorkbook book = (XSSFWorkbook)doc;
		XSSFSheet sheet = book.getSheetAt(0);

		new SimpleDateFormat("dd/MM/yyyy");


		sheet.createFreezePane(0, 3);


		XSSFRow header = sheet.getRow(2);
		int colCount = header.getPhysicalNumberOfCells();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int lastRow= sheet.getLastRowNum();
		sheet.shiftRows(1, lastRow+1, 1);
		XSSFRow r1 =     sheet.createRow(1);
		r1.setHeight((short)600);
		CellStyle styleb = book.createCellStyle();
		XSSFColor color10 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) styleb).setFillForegroundColor(color10);
		styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//     styleb.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		//    styleb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleb.setBorderLeft(BorderStyle.NONE);
		styleb.setBorderRight(BorderStyle.NONE);
		styleb.setBorderTop(BorderStyle.NONE);
		styleb.setBorderBottom(BorderStyle.NONE);

		styleb.setBottomBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setTopBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setLeftBorderColor(IndexedColors.WHITE.getIndex());
		styleb.setRightBorderColor(IndexedColors.WHITE.getIndex());
		XSSFCell clee = r1.createCell(0);
		clee.setCellValue("Date :-");
		clee.setCellStyle(styleb);
		XSSFCell clee3 = r1.createCell(1);
		clee3.setCellValue(date);
		clee3.setCellStyle(styleb);
		XSSFCell clee4 = r1.createCell(2);
		clee4.setCellValue("Total Amount(by Cash):- ");
		clee4.setCellStyle(styleb);

		XSSFCell clee6 = r1.createCell(3);
		clee6.setCellValue(cashAmountString);
		clee6.setCellStyle(styleb);

		XSSFCell clee7 = r1.createCell(4);
		clee7.setCellValue("Total Amount(by Cheque):-");
		clee7.setCellStyle(styleb);
		XSSFCell clee8 = r1.createCell(5);
		clee8.setCellValue(checkAmountString);
		clee8.setCellStyle(styleb);
		XSSFCell clee9 = r1.createCell(6);
		clee9.setCellValue("Total Amount");
		clee9.setCellStyle(styleb);
		XSSFCell clee10 = r1.createCell(7);
		clee10.setCellValue(totalamountString);
		clee10.setCellStyle(styleb);

		for(int o=8;o<100;o++) {
			XSSFCell clee12 = r1.createCell(o);
			clee12.setCellValue("");
			clee12.setCellStyle(styleb);
		}



		XSSFRow he = sheet.createRow(0);
		he.setHeight((short)1400);
		book.createCellStyle();

		//    XSSFRow rowOne = sheet.getRow(0);
		//  XSSFCell cellOne = rowOne.getCell(7);
		//  cellOne.setCellValue("");



		try
		{
			URL url = new URL(schoolDetails.downloadpath+schoolDetails.marksheetHeader);
			InputStream my_banner_image = new BufferedInputStream(url.openStream());
			//   FileInputStream my_banner_image = new FileInputStream("C://Users//HP//Pictures//Screenshots//bl.png");
			byte[] bytes = IOUtils.toByteArray(my_banner_image);
			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			ClientAnchor my_anchor = new XSSFClientAnchor();
			my_anchor.setRow1(0);
			my_anchor.setRow2(1);
			my_anchor.setCol1(1);
			my_anchor.setCol2(8);
			int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			//   cellll.setCellValue(my_picture_id);
			my_banner_image.close();

			drawing.createPicture(my_anchor, my_picture_id);

			//   my_picture.resize();
			//     FileOutputStream out = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\exc.xlsx"));
			//     book.write(out);
			//       out.close();




		} catch (IOException ioex) {
			//("fgg");
		}


		XSSFCellStyle intStyle = book.createCellStyle();
		intStyle.setDataFormat((short)1);

		XSSFCellStyle decStyle = book.createCellStyle();
		decStyle.setDataFormat((short)2);

		XSSFCellStyle dollarStyle = book.createCellStyle();
		dollarStyle.setDataFormat((short)5);

		XSSFRow headerRow = sheet.getRow(2);
		headerRow.setHeight((short)400);
		CellStyle style = book.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(243,236,221));
		((XSSFCellStyle) style).setFillForegroundColor(color);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//   Font font = book.createFont();
		//  font.setColor(IndexedColors.BLACK.getIndex());

		//   style.setFont(font);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		
		

		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		for(int i=0;i<colCount;i++) {
			XSSFCell ce1 = headerRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if(i==0) {
				ce1.setCellValue(" ");
			}
			ce1.setCellStyle(style);


		}
		CellStyle st = book.createCellStyle();
		XSSFColor color1 = new XSSFColor(new java.awt.Color(244,237,232));
		((XSSFCellStyle) st).setFillForegroundColor(color1);
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		st.setBorderLeft(BorderStyle.THIN);
		st.setBorderRight(BorderStyle.THIN);
		st.setBorderTop(BorderStyle.THIN);
		st.setBorderBottom(BorderStyle.THIN);

		st.setBottomBorderColor(IndexedColors.RED.getIndex());
		st.setTopBorderColor(IndexedColors.RED.getIndex());
		st.setLeftBorderColor(IndexedColors.RED.getIndex());
		st.setRightBorderColor(IndexedColors.RED.getIndex());


		for(int rowInd = 3; rowInd <rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);
			//        //(rowCount);
			//        //(colCount);
			for(int cellInd = 0; cellInd <7 ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);
				String strVal = cell.getStringCellValue();
				CellStyle st2 = book.createCellStyle();
				if(rowInd%2==0) {
					XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
					((XSSFCellStyle) st2).setFillForegroundColor(color6);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else {
					XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
					((XSSFCellStyle) st2).setFillForegroundColor(color2);
					st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}

				st2.setBorderLeft(BorderStyle.THIN);
				st2.setBorderRight(BorderStyle.THIN);
				st2.setBorderTop(BorderStyle.THIN);
				st2.setBorderBottom(BorderStyle.THIN);

				st2.setBottomBorderColor(IndexedColors.RED.getIndex());
				st2.setTopBorderColor(IndexedColors.RED.getIndex());
				st2.setLeftBorderColor(IndexedColors.RED.getIndex());
				st2.setRightBorderColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(st2);
				cell.setCellValue(strVal);
			}
		}

		sheet.getRow(5);
		// double total=0.0;
		// double[] sum = new double[colCount+1];
		for(int rowInd = 3; rowInd <= rowCount; rowInd++) {
			XSSFRow row = sheet.getRow(rowInd);

			for(int cellInd = 7; cellInd <colCount ; cellInd++) {


				XSSFCell cell = row.getCell(cellInd);






				int status=0,counter=0,dot=0;
				Character ch[] = new Character[3000];

				String strVal = cell.getStringCellValue();
				if(strVal.equalsIgnoreCase("")) {
					status=5;
				}

				for(int i=0;i<strVal.length();i++)
				{
					ch[i] = strVal.charAt(i);
					String s =Character.toString(ch[i]);


					if(Character.isDigit(ch[i]))
					{
						counter++;
					}
					if(s.equals("."))
					{
						dot++;
					}
					if(s.equals(""))
					{
						status=1;
					}
				}


				if(status==1)
				{
					CellStyle st2 = book.createCellStyle();

					if(rowInd%2==0) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					else {
						XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
						((XSSFCellStyle) st2).setFillForegroundColor(color2);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					st2.setBorderLeft(BorderStyle.THIN);
					st2.setBorderRight(BorderStyle.THIN);
					st2.setBorderTop(BorderStyle.THIN);
					st2.setBorderBottom(BorderStyle.THIN);

					st2.setBottomBorderColor(IndexedColors.RED.getIndex());
					st2.setTopBorderColor(IndexedColors.RED.getIndex());
					st2.setLeftBorderColor(IndexedColors.RED.getIndex());
					st2.setRightBorderColor(IndexedColors.RED.getIndex());
					cell.setCellStyle(st2);
					cell.setCellValue(strVal);

				}
				else if(status==5) {
					CellStyle st2 = book.createCellStyle();
					if(rowInd%2==0) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					else {
						XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
						((XSSFCellStyle) st2).setFillForegroundColor(color2);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					st2.setBorderLeft(BorderStyle.THIN);
					st2.setBorderRight(BorderStyle.THIN);
					st2.setBorderTop(BorderStyle.THIN);
					st2.setBorderBottom(BorderStyle.THIN);

					st2.setBottomBorderColor(IndexedColors.RED.getIndex());
					st2.setTopBorderColor(IndexedColors.RED.getIndex());
					st2.setLeftBorderColor(IndexedColors.RED.getIndex());
					st2.setRightBorderColor(IndexedColors.RED.getIndex());
					cell.setCellStyle(st2);
					cell.setCellValue("");
				}
				else if((dot+counter)==strVal.length())
				{
					try {
						Double ds = Double.valueOf(strVal);

						//           if((cellInd>=5)&&(cellInd<=colCount-1))
						//           {
						//              total+=ds;
						//           }
						//           //("total"+total);
						//           if(rowInd==3)
						//           {
						//               sum[cellInd] = total;
						//
						//           }
						//           else
						//           {
						//               sum[cellInd] =sum[cellInd]+total;
						//
						//           }
						//           total=0.0;

						cell.setCellType(Cell.CELL_TYPE_NUMERIC);

						DataFormat fmt = book.createDataFormat();
						CellStyle sty = book.createCellStyle();

						if(rowInd%2==0) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
							((XSSFCellStyle) sty).setFillForegroundColor(color6);
							sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						else {
							XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
							((XSSFCellStyle) sty).setFillForegroundColor(color2);
							sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}
						if(rowInd==rowCount) {
							XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
							((XSSFCellStyle) sty).setFillForegroundColor(color6);
							sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						}

						sty.setDataFormat(fmt.getFormat("#,##0.00"));
						sty.setBorderLeft(BorderStyle.THIN);
						sty.setBorderRight(BorderStyle.THIN);
						sty.setBorderTop(BorderStyle.THIN);
						sty.setBorderBottom(BorderStyle.THIN);



						sty.setBottomBorderColor(IndexedColors.RED.getIndex());
						sty.setTopBorderColor(IndexedColors.RED.getIndex());
						sty.setLeftBorderColor(IndexedColors.RED.getIndex());
						sty.setRightBorderColor(IndexedColors.RED.getIndex());
						cell.setCellStyle(sty);

						cell.setCellValue(ds);

					}
					catch (Exception e) {
						// TODO: handle exception
					}

				}
				else
				{
					CellStyle st2 = book.createCellStyle();
					if(rowInd%2==0) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(254,254,251));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					else {
						XSSFColor color2 = new XSSFColor(new java.awt.Color(241,235,234));
						((XSSFCellStyle) st2).setFillForegroundColor(color2);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					if(rowInd==rowCount) {
						XSSFColor color6 = new XSSFColor(new java.awt.Color(246,233,217));
						((XSSFCellStyle) st2).setFillForegroundColor(color6);
						st2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					st2.setBorderLeft(BorderStyle.THIN);
					st2.setBorderRight(BorderStyle.THIN);
					st2.setBorderTop(BorderStyle.THIN);
					st2.setBorderBottom(BorderStyle.THIN);

					st2.setBottomBorderColor(IndexedColors.RED.getIndex());
					st2.setTopBorderColor(IndexedColors.RED.getIndex());
					st2.setLeftBorderColor(IndexedColors.RED.getIndex());
					st2.setRightBorderColor(IndexedColors.RED.getIndex());
					cell.setCellStyle(st2);
					cell.setCellValue(strVal);

				}

			}

		}


		//
		//    XSSFRow roo=sheet.createRow(rowCount+1);
		//
		//    //("sum series");
		//    for(int h=0;h<sum.length;h++) {
		//        //(h+"-"+sum[h]);
		//    }
		//
		//    for(int f=5;f<colCount;f++)
		//    {
		//      XSSFCell cell1=roo.createCell(f);
		//
		//      cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
		//      DataFormat fmt = book.createDataFormat();
		//      CellStyle sty = book.createCellStyle();
		//      XSSFColor color7 = new XSSFColor(new java.awt.Color(246,233,217));
		//        ((XSSFCellStyle) sty).setFillForegroundColor(color7);
		//       sty.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//      sty.setBorderLeft(XSSFBorderStyle.THIN);
		//      sty.setBorderRight(XSSFBorderStyle.THIN);
		//      sty.setBorderTop(XSSFBorderStyle.THIN);
		//      sty.setBorderBottom(XSSFBorderStyle.THIN);
		//
		//      sty.setBottomBorderColor(IndexedColors.RED.getIndex());
		//      sty.setTopBorderColor(IndexedColors.RED.getIndex());
		//      sty.setLeftBorderColor(IndexedColors.RED.getIndex());
		//      sty.setRightBorderColor(IndexedColors.RED.getIndex());
		//      sty.setDataFormat(fmt.getFormat("#,##0.00"));
		//      cell1.setCellStyle(sty);
		//      cell1.setCellValue(sum[f]);
		//    }


	}


	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public Date getFeedate() {
		return feedate;
	}
	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}
	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}
	public void setGetdailyfeecollection(
			ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailycollection() {
		return dailycollection;
	}
	public void setDailycollection(ArrayList<DailyFeeCollectionBean> dailycollection) {
		this.dailycollection = dailycollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}
	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}
	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
	}
	public String getDate() {
		return date;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}
	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getTamount() {
		return tamount;
	}

	public void setTamount(double tamount) {
		this.tamount = tamount;
	}

	public double getTdiscount() {
		return tdiscount;
	}

	public void setTdiscount(double tdiscount) {
		this.tdiscount = tdiscount;
	}

	public String getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(String selectedOperator) {
		this.selectedOperator = selectedOperator;
	}

	public ArrayList<SelectItem> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(ArrayList<SelectItem> operatorList) {
		this.operatorList = operatorList;
	}

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public double getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getCashAmountString() {
		return cashAmountString;
	}

	public void setCashAmountString(String cashAmountString) {
		this.cashAmountString = cashAmountString;
	}

	public String getCheckAmountString() {
		return checkAmountString;
	}

	public void setCheckAmountString(String checkAmountString) {
		this.checkAmountString = checkAmountString;
	}

	public String getTotalamountString() {
		return totalamountString;
	}

	public void setTotalamountString(String totalamountString) {
		this.totalamountString = totalamountString;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtpInput() {
		return otpInput;
	}

	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}

	public String getDiscoutnNo() {
		return discoutnNo;
	}

	public void setDiscoutnNo(String discoutnNo) {
		this.discoutnNo = discoutnNo;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public boolean isShowExcel() {
		return showExcel;
	}

	public void setShowExcel(boolean showExcel) {
		this.showExcel = showExcel;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public ArrayList<SelectItem> getConcessionlist() {
		return concessionlist;
	}

	public void setConcessionlist(ArrayList<SelectItem> concessionlist) {
		this.concessionlist = concessionlist;
	}

	public String getSelectedConcession() {
		return selectedConcession;
	}

	public void setSelectedConcession(String selectedConcession) {
		this.selectedConcession = selectedConcession;
	}

	public String getPaymentModeSelected() {
		return paymentModeSelected;
	}

	public void setPaymentModeSelected(String paymentModeSelected) {
		this.paymentModeSelected = paymentModeSelected;
	}

	public ArrayList<String> getPaymentModeList() {
		return paymentModeList;
	}

	public void setPaymentModeList(ArrayList<String> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getFeetype() {
		return feetype;
	}

	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}

	public String getStudentclass() {
		return studentclass;
	}

	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
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

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		InstallmentWiseFeeReportBean.count = count;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public DatabaseMethods1 getObj() {
		return obj;
	}

	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}

	public DataBaseMethodStudent getObjStd() {
		return objStd;
	}

	public void setObjStd(DataBaseMethodStudent objStd) {
		this.objStd = objStd;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public SchoolInfoList getSchoolDetails() {
		return schoolDetails;
	}

	public void setSchoolDetails(SchoolInfoList schoolDetails) {
		this.schoolDetails = schoolDetails;
	}

	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}

	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(double paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public String getPaymentGatewayString() {
		return paymentGatewayString;
	}

	public void setPaymentGatewayString(String paymentGatewayString) {
		this.paymentGatewayString = paymentGatewayString;
	}
}
