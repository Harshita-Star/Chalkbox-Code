package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import session_work.QueryConstants;

@ManagedBean(name="partStdFeeRcptList")
@ViewScoped
public class ParticularStudentFeeReceiptList implements Serializable
{
	String autoname,discoutnNo,otpInput,totalPayAmunt,totalDiscountAmount,cancelremark="",receiptNo;
	String totalCollection="0",otp,branches,schoolid = "",date,name, fathersName, className;
	ArrayList<DailyFeeCollectionBean> dailyfee=new ArrayList<>();
	ArrayList<Feecollectionc> feesSelected=new ArrayList<>();
	ArrayList<StudentInfo> studentList;
	ArrayList<SelectItem> branchList;
	boolean showAdminAuth=true,check;
	Date changeDate,recipietDate = new Date();;
	double totalamount = 0.0;
	DailyFeeCollectionBean selectedstudent;
	StudentInfo sList;

	DatabaseMethods1 obj=new DatabaseMethods1();


	public ParticularStudentFeeReceiptList()
	{
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branchList = (ArrayList<SelectItem>) ss.getAttribute("branchList");

		try
		{
			recipietDate=(Date)ss.getAttribute("rDate");
			////// // System.out.println("rcpiet"+recipietDate);
			if(recipietDate==null)
			{
				recipietDate=new Date();
			}
		} catch (Exception e) {
			if(recipietDate==null)
			{
				recipietDate=new Date();
			}

		}

		branches="";
		if(branchList.size()>0)
		{
			for(SelectItem in : branchList)
			{
				if(branches.equals(""))
				{
					branches = String.valueOf(in.getValue());
				}
				else
				{
					branches = branches+"','"+String.valueOf(in.getValue());
				}
			}
		}

		totalCollection=new DatabaseMethods1().todaysCollectionDateWise(branches,recipietDate,conn);

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(branches,query,conn);
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ "/ "+info.getSrNo()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public List<String> autoCompleteReceiptNo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		ArrayList<String> studentList=new DatabaseMethods1().searchByReceiptNo(branches,query,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentList;
	}

	public void searchStudentByAutoName()
	{
		int index=autoname.lastIndexOf(":")+1;
		String id=autoname.substring(index);

		if(index!=0)
		{
			for(StudentInfo info : studentList)
			{
				if(String.valueOf(info.getAddNumber()).equals(id))
				{
					try
					{
						studentList=new ArrayList<>();
						studentList.add(info);
						sList=info;

						searchStudentByName();
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
	}

	public void searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		dailyfee=new ArrayList<>();
		schoolid=sList.getSchid();
		String id=sList.getAddNumber();
		name=sList.getFname();
		fathersName=sList.getFathersName();
		className=sList.getClassName();
		dailyfee=DBM.getstudentWisFeesCollection(schoolid, id, DatabaseMethods1.selectedSessionDetails(schoolid,conn), conn);

		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void searchStudentByReceiptNo()
	{
		String id[]=receiptNo.split("-");


		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();

		dailyfee=new ArrayList<>();
		
		ArrayList<String> list=new DataBaseMethodStudent().verifyConcessionFieldList();
		sList=new DataBaseMethodStudent().studentDetail(id[1],"","",QueryConstants.ADD_NUMBER,QueryConstants.BASIC, null,null,"","","","",DatabaseMethods1.selectedSessionDetails(branches,conn), branches, list, conn).get(0);
		schoolid=sList.getSchid();
		name=sList.getFname();
		fathersName=sList.getFathersName();
		className=sList.getClassName();
		dailyfee=DBM.receiptWiseStudentFeesCollection(schoolid, id[0], DatabaseMethods1.selectedSessionDetails(schoolid,conn), conn);

		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editFee()
	{
		feesSelected=new ArrayList<>();
		Connection conn = DataBaseConnection.javaConnection();
		feesSelected=obj.studetFeeCollectionByRecipietNo(sList.getAddNumber(),selectedstudent.getReciptno(),sList.getSchid(),conn);
		changeDate=feesSelected.get(0).getFeedate();
		double totalPAmount=0,totalPdiscount=0;
		for(Feecollectionc ls:feesSelected)
		{
			totalPAmount +=ls.getFeeamunt();
			totalPdiscount +=ls.getDiscount();
		}
		totalPayAmunt=String.valueOf(totalPAmount);
		totalDiscountAmount=String.valueOf(totalPdiscount);
		////// // System.out.println(feesSelected.size());
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkcancelOTP()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String schoolid = selectedstudent.getSchid();
		SchoolInfoList lst=obj.fullSchoolInfo(schoolid,conn);
		if(lst.getCancalfee().equalsIgnoreCase("yes"))
		{
			int randomPIN = (int) (Math.random() * 9000) + 1000;
			otp = String.valueOf(randomPIN);
			discoutnNo = lst.getDiscountOtpNo();
			String typemessage = "Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
					+ randomPIN + ") to allow for Cancel Fee.Treat this as confidential.Thank You.  \n"
					+ lst.getSmsSchoolName();
			obj.messageurlStaff(lst.getDiscountOtpNo(), typemessage, "admin", conn,schoolid,"");
			check=true;
		}
		else
		{
			check=false;
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updatelistner()
	{
		double totalPAmount=0,totalPdiscount=0;
		for(Feecollectionc ls:feesSelected)
		{
			totalPAmount +=ls.getFeeamunt();
			totalPdiscount +=ls.getDiscount();
		}
		totalPayAmunt=String.valueOf(totalPAmount);
		totalDiscountAmount=String.valueOf(totalPdiscount);
	}

	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		new DatabaseMethods1().udpatefees(feesSelected,changeDate,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		searchStudentByName();
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

			int i=obj.cancelReceipt(schoolid,selectedstudent.getReciptno(),cancelremark,conn);
			if(i>=1)
			{
				cancelremark="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Receipt Cancelled Sucessfully"));
				searchStudentByName();
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

	public String getAutoname() {
		return autoname;
	}

	public void setAutoname(String autoname) {
		this.autoname = autoname;
	}

	public String getDiscoutnNo() {
		return discoutnNo;
	}

	public void setDiscoutnNo(String discoutnNo) {
		this.discoutnNo = discoutnNo;
	}

	public String getOtpInput() {
		return otpInput;
	}

	public void setOtpInput(String otpInput) {
		this.otpInput = otpInput;
	}

	public String getTotalPayAmunt() {
		return totalPayAmunt;
	}

	public void setTotalPayAmunt(String totalPayAmunt) {
		this.totalPayAmunt = totalPayAmunt;
	}

	public String getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(String totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public String getCancelremark() {
		return cancelremark;
	}

	public void setCancelremark(String cancelremark) {
		this.cancelremark = cancelremark;
	}

	public String getTotalCollection() {
		return totalCollection;
	}

	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}

	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}

	public ArrayList<Feecollectionc> getFeesSelected() {
		return feesSelected;
	}

	public void setFeesSelected(ArrayList<Feecollectionc> feesSelected) {
		this.feesSelected = feesSelected;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<SelectItem> getBranchList() {
		return branchList;
	}

	public void setBranchList(ArrayList<SelectItem> branchList) {
		this.branchList = branchList;
	}

	public boolean isShowAdminAuth() {
		return showAdminAuth;
	}

	public void setShowAdminAuth(boolean showAdminAuth) {
		this.showAdminAuth = showAdminAuth;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getRecipietDate() {
		return recipietDate;
	}

	public void setRecipietDate(Date recipietDate) {
		this.recipietDate = recipietDate;
	}

	public double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(double totalamount) {
		this.totalamount = totalamount;
	}

	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}

	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}

	public StudentInfo getsList() {
		return sList;
	}

	public void setsList(StudentInfo sList) {
		this.sList = sList;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
}
