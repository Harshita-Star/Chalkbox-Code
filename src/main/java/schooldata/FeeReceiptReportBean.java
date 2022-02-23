package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="feereceiptBean")
@ViewScoped
public class FeeReceiptReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	boolean show=false;
	Date todaysdate,addmisssiondate, receiptDate;
	ArrayList<FeeInfo> selectedFees;
	ArrayList<StudentInfo>serialno;
	int total,totalDiscount,totalDue,dueInReceipt,payableAmt;
	String feeUpto,transportfeerupee,amountWords,session,totalAmountInWords,addnumber,sno,paymentmode,bankName,chequeno,studentclass,regNo,
	studentname,fathername,mothername,date,schoolName,add1,add2,add3,add4,phoneNo,mobileNo, emailId, website, imagePath,a,b,c,d;
	String width="",fontSize="",marginTop="",marginBetween="",discountMargin="";
	String chequeDate,srNo;
	boolean r1=false;
	boolean r2=false,showDiscount=false;
	String checqustatus,cheueDateStatus,bankStatus,remark;
	String schRegNo;
	String schid="";
	String type="";
	DatabaseMethods1 db=new DatabaseMethods1();

	public FeeReceiptReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info1=db.fullSchoolInfo(conn);
		schid=info1.getSchid();
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

		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		paymentmode=(String) rr.getAttribute("paymentmode");
		//	tutionFirstMonth=(String) rr.getAttribute("tutionFirstMonth");
		//	tutionLastMonth=(String) rr.getAttribute("tutionLastMonth");
		//	transportFirstMonth=(String) rr.getAttribute("transportFirstMonth");
		//	transportLastMonth=(String) rr.getAttribute("transportLastMonth");
		sno=(String )rr.getAttribute("receiptNumber");
		remark=(String )rr.getAttribute("remark");
		feeUpto=(String )rr.getAttribute("feeupto");
		type="Installment Name";
		if(feeUpto.contains("/"))
		{
			type="Fee Upto";
			
			String tempArr[] = feeUpto.split("/");
			String month = db.monthNameByNumber(Integer.valueOf(tempArr[1]));
			String year = tempArr[2];
			feeUpto = month + "-" + year;
				
		}
		
		session=DatabaseMethods1.selectedSessionDetails(schid,conn);
		receiptDate=(Date) rr.getAttribute("receiptDate");
		date=new SimpleDateFormat("dd-MM-yyyy").format(receiptDate);
		selectedFees=(ArrayList<FeeInfo>) rr.getAttribute("selectedFee");

		if(paymentmode.equals("Cash") || paymentmode.equalsIgnoreCase("Paytm"))
		{
			show=true;
		}
		else
		{
			bankName=(String) rr.getAttribute("bankname");
			chequeno=(String) rr.getAttribute("chequeno");
			Date cqDate=(Date) rr.getAttribute("chaqueDate");
			chequeDate=new SimpleDateFormat("dd/MM/yyyy").format(cqDate);
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
		if(rr.getAttribute("type1").equals("origanal"))
		{
			StudentInfo info=(StudentInfo) rr.getAttribute("selectedStudent");

			String addmision=info.getAddNumber();
			addnumber=String.valueOf(addmision);
			srNo=info.getSrNo();
			studentclass=info.getClassName();
			studentname=info.getFname();
			fathername=info.getFathersName();
			mothername=info.getMotherName();
		}
		else if(rr.getAttribute("type1").equals("duplicate"))
		{
			DailyFeeCollectionBean info=(DailyFeeCollectionBean) rr.getAttribute("selectedStudent");
			String addmision=info.getStudentid();
			addnumber=String.valueOf(addmision);
			srNo=info.getSrNo();
			if(info.getSection()==null)
			{
				studentclass=info.getClassname();
			}
			else
			{
				studentclass=info.getClassname()+" - "+info.getSection();
			}

			studentname=info.getStudentname();
			fathername=info.getFathername();
			mothername=info.getMothername();
			a=a+"(D)";
			b=b+"(D)";
			c=c+"(D)";
		}

		ArrayList<String> list=new DataBaseMethodStudent().verifyConcessionFieldList();
		//addmisssiondate=new DataBaseMethodStudent().studentDetail(addnumber,"","",QueryConstants.ADD_NUMBER,QueryConstants.DAILY_FEE_COLLECTION, null,null,"","","","",session, schid, list, conn).get(0).getAddDate();
		/*allFeePaidList=db.feeOfStudentByReceiptNo(sno);
		for(FeeStatus ft: allFeePaidList)
		{
			if(ft.getFeeType().equalsIgnoreCase("Tution Fee"))
			{
				if(tutionFirstMonth.equals(tutionLastMonth))
				{
					tutionPeriod=tutionFirstMonth;
				}
				else
				{
					tutionPeriod=tutionFirstMonth+" - "+tutionLastMonth;
				}
				ft.setFeeDateStr(tutionPeriod);

			}
			else if(ft.getFeeType().equalsIgnoreCase("Transport Fee"))
			{
				if(transportFirstMonth.equals(transportLastMonth))
				{
					transportPeriod=transportFirstMonth;
				}
				else
				{
					transportPeriod=transportFirstMonth+" - "+transportLastMonth;
				}
				ft.setFeeDateStr(transportPeriod);
			}
			else
			{
				ft.setFeeDateStr("");
			}
		}
		//int totalfee=tutionamount+admissionamount+annualfeeamount+examamount+transportamount;

		int totalfee=0;
		for(FeeStatus ff: allFeePaidList)
		{
			totalfee+=Integer.parseInt(ff.getAmount());
		}*/
		//total=String.valueOf(totalfee);
		/*admissionfeeRupee=String.valueOf(admissionamount);
		annualfeeRupee=String.valueOf(annualfeeamount);
		examfeerupee=String.valueOf(examamount);
		tutuionfeerupee=String.valueOf(tutionamount);
		transportfeerupee=String.valueOf(transportamount);*/

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


		/*if(tutionamount!=0){

			if(tutionFirstMonth.equals(tutionLastMonth))
				tutionPeriod=tutionFirstMonth;
			else
				tutionPeriod=tutionFirstMonth+" - "+tutionLastMonth;

		}
		if(transportamount!=0){

			if(transportFirstMonth.equals(transportLastMonth))
				transportPeriod=transportFirstMonth;
			else
				transportPeriod=transportFirstMonth+" - "+transportLastMonth;
			}
		}*/

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
	

}
