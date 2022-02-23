package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="printProsRegReceiptBean")
@ViewScoped

public class PrintProsRegReceiptBean implements Serializable
{
	boolean showPros,showReg,showAdm,showEver;
	Date chqDate;
	String type,receiptNo,enqNo,name,fname,mobile,address,amtWords,mode,date,className,headerImage,prosRegNo,bankName,subBankName,chqNo;
	int amount;
	double totAmount;
	DatabaseMethods1 db=new DatabaseMethods1();
	ArrayList<FeeInfo> feeList = new ArrayList<>();
	String strAmt,strChqDate,strTestDate;

	public PrintProsRegReceiptBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		type = (String) ss.getAttribute("enqPaymentType");
		receiptNo = (String) ss.getAttribute("enqReceiptNo");
		enqNo = (String) ss.getAttribute("enqNo");
		name = (String) ss.getAttribute("enqStudentName");
		fname = (String) ss.getAttribute("enqFatherName");
		mobile = (String) ss.getAttribute("enqMobile");
		address = (String) ss.getAttribute("enqAddress");
		mode = (String) ss.getAttribute("enqPaymentMode");
		date = (String) ss.getAttribute("paymentDate");
		className = (String) ss.getAttribute("enqClassName");
		//amount = (int) ss.getAttribute("amount");
		totAmount = (double) ss.getAttribute("amount");
		prosRegNo = (String) ss.getAttribute("prosRegNo");
		strTestDate = (String) ss.getAttribute("testDate");


		bankName = (String) ss.getAttribute("bankName");
		subBankName = (String) ss.getAttribute("subBankName");
		chqNo = (String) ss.getAttribute("chqNo");
		chqDate= (Date) ss.getAttribute("chqDate");
		if(chqDate!=null)
			strChqDate=new SimpleDateFormat("dd/MM/yyyy").format(chqDate);

		String schid = (String) ss.getAttribute("schoolid");

		strAmt = new DecimalFormat("##").format(totAmount);
		//String amt = new DecimalFormat("##").format(totAmount);
		amount = Integer.parseInt(strAmt);

		amtWords=db.numberToWords(amount);
		amtWords=amtWords+" Only";

		SchoolInfoList info=db.fullSchoolInfo(schid,conn);
		String savePath="";
		if(info.getProjecttype().equals("online"))
		{
			savePath=info.getDownloadpath();
		}
		headerImage=savePath+info.getMarksheetHeader();

		if(type.equalsIgnoreCase("PROSPECTUS"))
		{
			showPros = true;
			showReg = false;
			showAdm=false;
		}
		else if(type.equalsIgnoreCase("REGISTRATION"))
		{
			showAdm = false;
			showPros = false;
			if(info.getBranch_id().equalsIgnoreCase("22") || info.getBranch_id().equalsIgnoreCase("27"))
			{
				showReg = false;
				showEver = true;
			}
			else
			{
				showReg = true;
				showEver = false;
			}

		}
		else if(type.equalsIgnoreCase("ADMISSION"))
		{
			showAdm = true;
			showPros = false;
			showReg = false;
			feeList = (ArrayList<FeeInfo>) ss.getAttribute("feelist");
			FeeInfo si = new FeeInfo();
			si.setSrno("");
			si.setFeeName("G.Total");
			si.setAmount(totAmount);
			feeList.add(si);

			for(FeeInfo ff : feeList)
			{
				ff.setStrAmount(new DecimalFormat("##").format(ff.getAmount()));
			}

		}

	}

	public boolean isShowPros() {
		return showPros;
	}

	public void setShowPros(boolean showPros) {
		this.showPros = showPros;
	}

	public boolean isShowReg() {
		return showReg;
	}

	public void setShowReg(boolean showReg) {
		this.showReg = showReg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getEnqNo() {
		return enqNo;
	}

	public void setEnqNo(String enqNo) {
		this.enqNo = enqNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAmtWords() {
		return amtWords;
	}

	public void setAmtWords(String amtWords) {
		this.amtWords = amtWords;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getTotAmount() {
		return totAmount;
	}

	public void setTotAmount(double totAmount) {
		this.totAmount = totAmount;
	}

	public DatabaseMethods1 getDb() {
		return db;
	}

	public void setDb(DatabaseMethods1 db) {
		this.db = db;
	}

	public String getProsRegNo() {
		return prosRegNo;
	}

	public void setProsRegNo(String prosRegNo) {
		this.prosRegNo = prosRegNo;
	}

	public boolean isShowAdm() {
		return showAdm;
	}

	public void setShowAdm(boolean showAdm) {
		this.showAdm = showAdm;
	}

	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}

	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}

	public String getStrAmt() {
		return strAmt;
	}

	public void setStrAmt(String strAmt) {
		this.strAmt = strAmt;
	}

	public Date getChqDate() {
		return chqDate;
	}

	public void setChqDate(Date chqDate) {
		this.chqDate = chqDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSubBankName() {
		return subBankName;
	}

	public void setSubBankName(String subBankName) {
		this.subBankName = subBankName;
	}

	public String getChqNo() {
		return chqNo;
	}

	public void setChqNo(String chqNo) {
		this.chqNo = chqNo;
	}

	public String getStrChqDate() {
		return strChqDate;
	}

	public void setStrChqDate(String strChqDate) {
		this.strChqDate = strChqDate;
	}

	public String getStrTestDate() {
		return strTestDate;
	}

	public void setStrTestDate(String strTestDate) {
		this.strTestDate = strTestDate;
	}

	public boolean isShowEver() {
		return showEver;
	}

	public void setShowEver(boolean showEver) {
		this.showEver = showEver;
	}
}
