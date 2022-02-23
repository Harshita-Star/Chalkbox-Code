package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import session_work.DatabaseMethodSession;
import session_work.RegexPattern;

@ManagedBean(name="installmentWiseFeeCollection")
@ViewScoped
public class InstallmentWiseFeeCollection implements Serializable
{

	String regex=RegexPattern.REGEX;
	Date feedate=new Date(),endDate;
	String autoname,name, fathersName, className, feeModuleType, sectionName, preSession, dobString;
	ArrayList<StudentInfo> studentList;
	String firstMonthTransportPeriod, lastMonthTransportPeriod, feeType, gender, bankName, chequeNumber, remark = "",
			challanNo, neftNo, paymentMode = "Cash", lastMonthTransportPeriodTemp, addmissionNumber;
	int amount, mn, totalAmount, admissionDiscount, examinationDiscount, tutionDiscount, annualDiscount,
	transportDiscount, termDiscount, artDiscount, activityDiscount;
	StudentInfo sList,selectedStudent;
	String date;
	DatabaseMethodSession objSession=new DatabaseMethodSession();
	String cancelremark="";
	boolean showAdminAuth=true;
	String recepietNo="";
	String[] selectedCheckFees,checkMonthSelected;
	boolean disabledfees=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<Feecollectionc> feesSelected=new ArrayList<>();
	ArrayList<FeeInfo>feelist1;
	ArrayList<StudentInfo> serialno;
	ArrayList<FeeStatus> transportfeeStatus;
	Date dob, challanDate, neftDate;
	int flag, transportFeeLeft;
	String totalPayAmunt,totalDiscountAmount;
	ArrayList<FeeInfo> classFeeList, selectedFees = new ArrayList<>(),newclassFeelist=new ArrayList<>();
	boolean showPaymentMode, showFeeList, showChallan, showNeft, showCheque,showDisabled;
	Date recipietDate = new Date(), dueDate = new Date();
	ArrayList<FeeInfo> feeStructureList = new ArrayList<>();
	ArrayList<Feecollectionc> getdailyfeecollection;
	String studentStatus = "", srNo;
	boolean showtwsb;
	String billtype = "Ist Bill";
	ArrayList<SelectItem> installmentList,installmentList2,branchList;
	String message = "";
	String otp, otpInput,selectedMonth,selectedYear;
	String discoutnNo,branches;
	double totalamount = 0.0;
	String schoolid = "";
	ArrayList<FeeInfo> discountFeeList = new ArrayList<>();
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	ArrayList<SelectItem> yearList;
	ArrayList<SelectItem> monthList;
	ArrayList<FeeInfo> list = new ArrayList<>();
	double tamount,tdiscount;
	Date changeDate;
	String reason;
	String totalseletedAMT="0";
   String feeRemark="";
   boolean showFees=false,showDirectSubmitFee=false,checkLock=false;
	
   Date maxDate;
   
   
	boolean directSubmitFee,nonDirectFeeSubmit;

	/*
	 * public void calculateTotal() {
	 *
	 * }
	 */
	boolean waveOffLateFee=false;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	int cashAmount,chequeAmount;
	static int count=1;
	String selectedFeeRemarkNew;

	

	public void checkingInstallment()
	{
		
		int i=0;
        double amount=0;
		for(FeeInfo ls:list)
		{
            for(int j=0;j<selectedCheckFees.length;j++)
            {
            	if(selectedCheckFees[j].equals(String.valueOf(ls.getFeeMonthInt())))
            	{
            		i=i+1;
                	amount=amount+ls.getTotalFee();
                	break;	
            	}
            	
            }
            
            if(i==selectedCheckFees.length)
            {
            	break;
            }
            
		}
		
		
		totalseletedAMT=String.valueOf(amount);
		
	}
	
	
	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentListNormalSchool(branches,query,conn);
	//	studentList=objSession.searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		
		List<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+"/ "+info.getFathersName()+"/ "+info.getClassName()+ "/ "+info.getSrNo()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public String submitNow()
	{	
		String messageForLock=checkLock();
		String sub="";
		if(checkLock==false)
		{
			newGenerateFee();
			sub=submit();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
		}
		return sub;
	}
	
	public String submitNowWithRecipietNO()
	{
		String messageForLock=checkLock();
		String sub="";
		if(checkLock==false)
		{
			newGenerateFee();
			sub=submitWithReceipietNO();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
		}
		return sub;
	}

	public String submitNowChallan()
	{
		String messageForLock=checkLock();
		String sub="";
		if(checkLock==false)
		{
			newGenerateFee();
			sub=submitFeeChallan();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
		}

		return sub;
	}
	
	public String checkLock()
	{
		String messageForLock="";
		Connection conn = DataBaseConnection.javaConnection();
		Date lockDate = new DatabaseMethods1().checkLockDate(branches,conn);
		if(lockDate == null)
		{

			checkLock=false;
		}
		else
		{
			if(recipietDate.after(lockDate))
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
	
	public void searchStudentByAutoName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String messageForLock=checkLock();
		if(checkLock==false)
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
							selectedStudent=info;

							HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
							ss.setAttribute("selectedStudent", selectedStudent);


							general();
							
							
							if(installmentList.size()>0)
							{
								showFees=true;
								showDirectSubmitFee=true;
							}
							else
							{

								showFees=false;
								showDirectSubmitFee=false;
							}
							

							/*FacesContext fc = FacesContext.getCurrentInstance();
							ExternalContext ec = fc.getExternalContext();

							try {
								ec.redirect("masterStudentFeeCollection.xhtml");
							} catch (IOException e) {
								
								e.printStackTrace();
							}
							return "masterStudentFeeCollection";*/
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
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return "";
	}

	public void calculateamt() {
		FacesContext context = FacesContext.getCurrentInstance();
		int k = (int) UIComponent.getCurrentComponent(context).getAttributes().get("auto");
		submitAmount = 0;
		discountAmount = 0;

		if (selectedFees.size() > 0)
		{
			for (FeeInfo ff : selectedFees)
			{
				//////// // System.out.println(ff.getFeeName());
				if (k == ff.getSno())
				{
					if (ff.getFeeName().equalsIgnoreCase("Late Fee"))
					{
						// ff.setDueamount(String.valueOf(ff.getPayAmount()+ff.getPayDiscount()));
					}
					else
					{
						/*if (ff.getDueamount().equals("0"))
						{
							ff.setPayAmount(0);
							ff.setPayDiscount(0);
						}
						else
						{*/
						int due = ff.getPayAmount();
						////// // System.out.println(due);
						int disc = ff.getPayDiscount();
						ff.setPayAmount(due - disc);

						/*}*/
				}

				}

				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}

		}
	}

	
	
	public String duplicateFee()
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
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml');");
		return "installmentWiseFeeCollection";
		
	}



	public void searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		new SimpleDateFormat("dd/MM/yyyy");
		feelist1=DBM.viewFeeList1(DBM.schoolId(),conn);

		dailyfee=new ArrayList<>();
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		/*int index = name.indexOf("-") + 1;
		String id = name.substring(index);*/
		schoolid=sList.getSchid();
		String id=sList.getAddNumber();
		dailyfee=DBM.getstudentWisFeesCollection(schoolid, id, DatabaseMethods1.selectedSessionDetails(schoolid,conn), conn);
		
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
					if(info.getPaymentmode().equalsIgnoreCase("Payment Gateway"))
					{
						info.setBankname("");
					}
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





	public void calculatePayAmount() {
		submitAmount = 0;
		discountAmount = 0;

		if (selectedFees.size() > 0) {
			for (FeeInfo ff : selectedFees) {
				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}

		}
	}

	String totalCollection="0";

	public InstallmentWiseFeeCollection()
	{

		branches="";
		Connection conn= DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		branches = (String) ss.getAttribute("schoolid");

		try {
			recipietDate=(Date)ss.getAttribute("rDate");
			////// // System.out.println("rcpiet"+recipietDate);
			
			maxDate=new Date();
			
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



		totalCollection=new DatabaseMethods1().todaysCollectionDateWise(branches,recipietDate,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void general()
	{
		Date dt = new Date();

		allMonths();
		allYear();

		selectedMonth = String.valueOf(dt.getMonth() + 1);
		selectedYear = String.valueOf(dt.getYear() + 1900);

		if(selectedMonth=="2")
		{
			selectedMonth="3";
		}
		else if(selectedMonth=="5")
		{
			selectedMonth="6";
		}
		else if(selectedMonth=="7")
		{
			selectedMonth="8";
		}
		else if(selectedMonth=="10")
		{
			selectedMonth="11";
		}

		if(Integer.parseInt(selectedMonth)<4)
		{
			selectedYear = String.valueOf(dt.getYear() + 1901);
		}


		String strDate = "01/"+selectedMonth+"/"+selectedYear;

		try {
			dueDate= new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//////// // System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(dueDate));


		newclassFeelist=new ArrayList<>();

		generateFeeCollectionTables(dueDate);
		installment();
		searchStudentByName();

		directSubmitFee=true;
		nonDirectFeeSubmit=false;

	}





	public void installment()
	{
		////// // System.out.println(list.size());
		installmentList = new ArrayList<>();
		for(FeeInfo ls:list)
		{
			SelectItem ss1 = new SelectItem();
			ss1.setLabel(ls.getFeeMonth()+"("+String.valueOf(ls.getTotalFee())+")");
			ss1.setValue(String.valueOf(ls.getFeeMonthInt()));
			installmentList.add(ss1);

		}

		/*	SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("1");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("3");
		installmentList.add(ss8);
		 */	}

	public void findDueFees() {
		if (dueDate == null) {
			dueDate = new Date();
		}

		submitAmount = 0;
		discountAmount = 0;
		selectedFees = new ArrayList<>();

		Date dt = new Date();
		/*selectedMonth = String.valueOf(dt.gesubmittMonth() + 1);
		selectedYear = String.valueOf(dt.getYear() + 1900);

		allMonths();
		allYear();*/


		selectedYear = String.valueOf(dt.getYear() + 1900);

		if(selectedMonth=="2")
		{
			selectedMonth="3";
		}
		else if(selectedMonth=="5")
		{
			selectedMonth="6";
		}
		else if(selectedMonth=="7")
		{
			selectedMonth="8";
		}
		else if(selectedMonth=="10")
		{
			selectedMonth="11";
		}

		if(Integer.parseInt(selectedMonth)<4)
		{
			selectedYear = String.valueOf(dt.getYear() + 1901);
		}


		String strDate = "01/"+selectedMonth+"/"+selectedYear;

		//	String strDate = "01/"+selectedMonth+"/"+selectedYear;

		try {
			dueDate= new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		////// // System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

		generateFeeCollectionTables(dueDate);

	}


	public void checkWaveOfFee()
	{
		try {
			lateFeeCalculation();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		installment();
	}



	public int lateFeeCalculation() throws ParseException
	{

		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		list = new ArrayList<>();
		
		String addmissionNumber = "";
		try {
			addmissionNumber=sList.getAddNumber();
		} catch (Exception e) {
			 // System.out.println("CB Testing Installment"+new DatabaseMethods1().schoolId());
			 // System.out.println("CB Testing Installment"+autoname);
			e.printStackTrace();
			 // System.out.println("CB Testing Installment"+sList);
		}
		
		schoolid = sList.getSchid();
		FeeDynamicList lateFees=new DatabaseMethods1().getlatefeecalculation(schoolid,conn);
		String date=new SimpleDateFormat("dd/MM/yyyy").format(recipietDate);
		String preSession=DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolid, conn);
		ArrayList<FeeDynamicList>feelist;
		if((schoolid.equals("287") && Integer.parseInt(sList.getClassId())<13 ) ||(!schoolid.equals("287")))
		{
			feelist=new DatabaseMethods1().getAllInstallment(schoolid,conn);
					
		}
		else
		{
			feelist=new DatabaseMethods1().getAllInstallmentforAjmani(schoolid,conn);
			
		}
		
		String studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		ArrayList<FeeInfo> classFeeList;
		new ArrayList<>();
		//JSONArray arr=new JSONArray();
		String[] session=preSession.split("-");
		double previousDueAmount=0; int totalLateFee=0;
		for(int i=0;i<feelist.size();i++)
		{
			

			Date findDate=null;
			Date cDate=null;
			try {
				findDate= feelist.get(i).getFee_due_date();
				cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			/*int count=1;
			if(feemonths[i].equals("6")||feemonths[i].equals("8")||feemonths[i].equals("11")||feemonths[i].equals("3"))
			{
				count=2;
			}*/

			double dueAmount = 0,actuladueAmount=0;
			
			if(feelist.get(i).getInsatllmentName().equalsIgnoreCase("Previous_Fee"))
			{
				  
				classFeeList=new ArrayList<>();
			classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);

			for(FeeInfo lss:classFeeList)
			{
				if(lss.getFeeId().equals("-1"))
				{
					int mainamount=lss.getFeeAmount();
					int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, lss.getFeeId(),feelist.get(i).getInsatllmentValue(),conn);
					int dAmount=mainamount-totalpaidamount;
					if(dAmount>0)
					{
						dueAmount += dAmount;
					}
				}


			}

				
			}
			else
			{
				ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReportForMasterForStkabeer(schoolid,addmissionNumber, Integer.parseInt(feelist.get(i).getInsatllmentValue()),
						preSession, conn, "feeCollection",Integer.parseInt(feelist.get(i).getMonth()),feelist.get(i).getInstallment_Month_Include());

				HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();
				classFeeList=new ArrayList<>();
				
				classFeeList = DBM.classFeesForStudent11(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
						conn);
				
				 // System.out.println(classFeeList.size());
				
				//classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
				
				for (FeeInfo ls : classFeeList) {
					
					 // System.out.println(ls.getFeeName());
					
					if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
						int mainamount=Integer.parseInt(map.get(ls.getFeeName()));
						if(Integer.parseInt(ls.getTax_percn())>0)
						{
							mainamount=mainamount+((mainamount*Integer.parseInt(ls.getTax_percn()))/100);
						}

						int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),feelist.get(i).getInsatllmentValue(),conn);
						int dAmount=mainamount-totalpaidamount;
						if(dAmount>0)
						{
							dueAmount += dAmount;
						}
					}
				}
			}



			
			actuladueAmount=dueAmount-previousDueAmount;

			//JSONObject obj = new JSONObject();
			FeeInfo ff = new FeeInfo();
			ff.setAmount(actuladueAmount);
			ff.setFeeMonth(feelist.get(i).getInsatllmentName());
			ff.setFeeMonthInt(Integer.parseInt(feelist.get(i).getInsatllmentValue()));
			int latefee=0;

			if(actuladueAmount>0 && waveOffLateFee==false)
			{



				long diff =  cDate.getTime()-findDate.getTime() ;
				int fff =(int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				if(lateFees.getFee_due_days().equals("")) {
					latefee=0;
				}
				else
				{
					String[] feeduedays=lateFees.getFee_due_days().split(",");
					String[] latefeeamount=lateFees.getLate_fee().split(",");
					if(feeduedays.length==1)
					{
						if(Integer.parseInt(feeduedays[0])>=fff)
						{
							latefee=Integer.parseInt(latefeeamount[0]);
						}

					}
					else if(feeduedays.length==2)
					{
						int ii=Integer.parseInt(feeduedays[0]);
						int j=Integer.parseInt(feeduedays[1]);
						if(ii <= fff && fff < j)
						{
							latefee=Integer.parseInt(latefeeamount[0]);
						}
						else if(fff >= j)
						{
							latefee=Integer.parseInt(latefeeamount[1]);
						}
						else {
							latefee=0;
						}
					}


				}

				int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feelist.get(i).getInsatllmentValue(),conn);
				int l=latefee-totalpaidamount;
				if(l>0)
				{
					latefee=l;
				}
				else
				{
					latefee=0;
				}

			}


			
			

			if(actuladueAmount>0)
			{
				totalLateFee += latefee;

				//ff.setLateFee(latefee);
				ff.setTotalFee(ff.getAmount()+latefee);
				list.add(ff);
			}

			


		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalLateFee;
	}



	public void allMonths() {
		monthList = new ArrayList<>();

		SelectItem item = new SelectItem();
		item.setLabel("January");
		item.setValue("1");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("February");
		item.setValue("2");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("March");
		item.setValue("3");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("April");
		item.setValue("4");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("May");
		item.setValue("5");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("June");
		item.setValue("06");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("July");
		item.setValue("7");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("August");
		item.setValue("8");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("September");
		item.setValue("9");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("October");
		item.setValue("10");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("November");
		item.setValue("11");
		monthList.add(item);

		item = new SelectItem();
		item.setLabel("December");
		item.setValue("12");
		monthList.add(item);

	}

	public void allYear() {
		yearList = new ArrayList<>();
		for (int i = 2010; i <= 2100; i++) {

			SelectItem item = new SelectItem();
			item.setLabel(String.valueOf(i));
			item.setValue(String.valueOf(i));
			yearList.add(item);
		}
	}
	boolean check;
	public void checkcancelOTP()
	{
		System.out.println(recipietDate);
		String messageForLock=checkLock();
		if(checkLock==false)
		{
			System.out.println("hello for if");
			Connection conn=DataBaseConnection.javaConnection();
			String schoolid = selectedstudent.getSchid();
			SchoolInfoList lst=obj.fullSchoolInfo(schoolid,conn);
			//////// // System.out.println(lst.getCancalfee());
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
				
				PrimeFaces.current().executeInitScript("PF('cancelfee').show();");
				PrimeFaces.current().ajax().update("form2");
				
			}
			else
			{
				check=false;
				PrimeFaces.current().executeInitScript("PF('cancelfee').show();");
				PrimeFaces.current().ajax().update("form2");
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
			PrimeFaces.current().ajax().update("form");
		}
		
	}

	public void generateFeeCollectionTables(Date findDate)
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) ss.getAttribute("feesubmit");
		sList = (StudentInfo) ss.getAttribute("selectedStudent");
		//String etype=(String) ss.getAttribute("etype");
		/*	if(etype.equalsIgnoreCase("madmin"))
		{*/
		disabledfees=false;
		showAdminAuth=true;
		/*}
		else
		{
			showAdminAuth=false;
			disabledfees=true;
		}*/
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		schoolid = sList.getSchid();
		SchoolInfoList schoolInfo = DBM.fullSchoolInfo(schoolid,conn);

		if (schoolid.equals("206")) {
			billtype = "Ist Bill";
			showtwsb = true;
		} else {
			billtype = "";
			showtwsb = false;
		}


		//////// // System.out.println(schoolInfo.getBranch_id());
		if(schoolInfo.getBranch_id().equalsIgnoreCase("54"))
		{
			showDisabled=false;
		}
		else
		{
			showDisabled=true;
		}


		//////// // System.out.println(showDisabled);
		preSession = DatabaseMethods1.selectedSessionDetails(schoolid,conn);


		name = sList.getFname();
		fathersName = sList.getFathersName();
		className = sList.getClassName();
		sectionName = sList.getSectionName();
		addmissionNumber = sList.getAddNumber();
		srNo = sList.getSrNo();
		dob = sList.getDob();
		dobString = sList.getDobString();
		gender = sList.getGender();
		studentStatus = sList.getStudentStatus();
		sList.getConcession();
		//////// // System.out.println(connsessioncategory);
		ss.setAttribute("admisionnumber", addmissionNumber);

		/*discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);
		ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReport(schoolid,addmissionNumber, findDate,
				preSession, conn, "feeCollection");

		HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

		classFeeList = DBM.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
				conn);
		classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
		dueAmount = 0;
		for (FeeInfo ls : classFeeList) {
			if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
				ls.setDueamount(map.get(ls.getFeeName()));
				ls.setPayAmount(Integer.parseInt(map.get(ls.getFeeName())));
				dueAmount += Integer.parseInt(map.get(ls.getFeeName()));

			}

			ls.setSelectCheckBox(false);

		}

		if (DBM.schoolId().equals("206")) {
			int i = DBM.totalStudentExpense(schoolid,addmissionNumber, conn);
			classFeeList.get(classFeeList.size() - 1).setFeeAmount(i);

		}



		// ClassInfo list=new ClassInfo();
		// list=classFeeList.get(classFeeList.size()-1);
		//
		// ////// // System.out.println("session - "+schoolInfo.getSchoolSession());
		DateFormat df = new SimpleDateFormat("MM");
		String reportDate = df.format(sList.getStartingDate());
		// ////// // System.out.println("reportDate -
		// "+sList.getStartingDate()+"...."+reportDate);
		int startingMonth = 0;
		int session = DBM.getpromostionclass(String.valueOf(sList.getAddNumber()), conn);
		if (session == 1 || studentStatus.equalsIgnoreCase("old")) {
			if (schoolInfo.getSchoolSession().equals("4-3")) {
				startingMonth = 4;
			} else {
				startingMonth = 5;
			}
		} else {
			if (schoolInfo.getFeeStart().equalsIgnoreCase("session_date")) {
				if (schoolInfo.getSchoolSession().equals("4-3")) {
					startingMonth = 4;
				} else {
					startingMonth = 5;
				}
			} else {
				startingMonth = Integer.parseInt(reportDate);
			}
		}
		// totalAmount=new
		// DatabaseMethods1().tutionFeePaid(sList,preSession)+DatabaseMethods1.getTutionDiscount(sList.getAddNumber(),preSession);

		feeStructureList = new ArrayList<>();

		for (FeeInfo rr : classFeeList) {
			if (!rr.getFeeName().equals("Transport") && !rr.getFeeName().equals("Previous Fee")) {
				if (rr.getFeeType().equals("year")) {
					int fee = rr.getFeeAmount();
					int feepaidAmount = DBM.FeePaidRecord(schoolid,sList, preSession, rr.getFeeId(), conn);
					int leftamount = fee - feepaidAmount;
					FeeInfo info = new FeeInfo();
					info.setFeeName(rr.getFeeName());
					info.setFeePeriod("-");
					info.setTotalFeeAmount(fee);
					info.setTotalFeePaidAmount(feepaidAmount);
					info.setTotalFeeLeftAmount(leftamount);
					feeStructureList.add(info);
					fee = feepaidAmount = leftamount = 0;
				} else if (rr.getFeeType().equals("month")) {
					int totalpaidamount = 0;
					totalpaidamount = DBM.FeePaidRecord(schoolid,sList, preSession, rr.getFeeId(), conn);
					int monthly = 0;
					if (schoolInfo.getSchoolSession().equals("4-3")) {
						if (startingMonth < 4) {
							monthly = startingMonth + 12;
						} else {
							monthly = startingMonth;
						}
					} else {
						if (startingMonth < 5) {
							monthly = startingMonth + 12;
						} else {
							monthly = startingMonth;
						}
					}
					////// // System.out.println("monthly....." + monthly + ".....startmonth....." + startingMonth);

					// ////// // System.out.println("hellooooo........."+monthly);

					if (schoolInfo.getSchoolSession().equals("4-3")) {
						for (int i = monthly; i <= 15; i++) {
							int month = i;
							if (i > 12)
								month = i - 12;

							int fee = rr.getFeeAmount();

							if (fee <= totalpaidamount) {
								FeeInfo info = new FeeInfo();
								String month1 = DBM.monthList(month);
								info.setFeePeriod(month1);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								feeStructureList.add(info);
								totalpaidamount = totalpaidamount - fee;
							} else {

								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(DBM.monthList(month));

								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								feeStructureList.add(info);
								totalpaidamount = 0;
							}
						}
					} else {
						for (int i = monthly; i <= 16; i++) {
							int month = i;
							if (i > 12)
								month = i - 12;

							int fee = rr.getFeeAmount();

							if (fee <= totalpaidamount) {
								FeeInfo info = new FeeInfo();
								String month1 = DBM.monthList(month);
								info.setFeePeriod(month1);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								feeStructureList.add(info);
								totalpaidamount = totalpaidamount - fee;
							} else {
								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(DBM.monthList(month));

								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								feeStructureList.add(info);
								totalpaidamount = 0;
							}
						}
					}
				} else if (rr.getFeeType().equals("quarter")) {

					int startmont = 0;

					if (schoolInfo.getSchoolSession().equals("4-3")) {
						if (startingMonth >= 4 && startingMonth <= 6) {
							startmont = 1;
						} else if (startingMonth >= 7 && startingMonth <= 9) {
							startmont = 2;
						} else if (startingMonth >= 10 && startingMonth <= 12) {
							startmont = 3;
						} else if (startingMonth >= 1 && startingMonth <= 3) {
							startmont = 4;
						}

					} else {
						int tempStartingMonth = startingMonth;
						if (startingMonth == 1) {
							tempStartingMonth = 13;
						}

						if (tempStartingMonth >= 5 && tempStartingMonth <= 7) {
							startmont = 1;
						} else if (tempStartingMonth >= 8 && tempStartingMonth <= 10) {
							startmont = 2;
						} else if (tempStartingMonth >= 11 && tempStartingMonth <= 13) {
							startmont = 3;
							// ////// // System.out.println("sds : "+startingMonth);
						} else if (tempStartingMonth >= 2 && tempStartingMonth <= 4) {
							startmont = 4;
						}

					}

					int totalpaidamount = 0;
					totalpaidamount = DBM.FeePaidRecord(schoolid,sList, preSession, rr.getFeeId(), conn);

					if (schoolInfo.getSchoolSession().equals("4-3")) {
						for (int i = startmont; i <= 4; i++) {
							String month = "";

							if (i == 1) {
								month = DBM.monthList(4) + "-" + DBM.monthList(6);
							} else if (i == 2) {

								month = DBM.monthList(7) + "-" + DBM.monthList(9);
							} else if (i == 3) {
								month = DBM.monthList(10) + "-" + DBM.monthList(12);
							} else if (i == 4) {
								month = DBM.monthList(1) + "-" + DBM.monthList(3);
							}

							int fee = rr.getFeeAmount();

							if (fee <= totalpaidamount) {

								FeeInfo info = new FeeInfo();

								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								feeStructureList.add(info);
								totalpaidamount = totalpaidamount - fee;
							} else {

								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								feeStructureList.add(info);
								totalpaidamount = 0;
							}
						}
					} else {
						////// // System.out.println("xx : " + startmont);
						for (int i = startmont; i <= 4; i++) {
							String month = "";

							if (i == 1) {
								month = DBM.monthList(5) + "-" + DBM.monthList(7);
							} else if (i == 2) {

								month = DBM.monthList(8) + "-" + DBM.monthList(10);
							} else if (i == 3) {
								month = DBM.monthList(11) + "-" + DBM.monthList(1);
							} else if (i == 4) {
								month = DBM.monthList(2) + "-" + DBM.monthList(4);
							}

							int fee = rr.getFeeAmount();

							if (fee <= totalpaidamount) {

								FeeInfo info = new FeeInfo();

								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								feeStructureList.add(info);
								totalpaidamount = totalpaidamount - fee;
							} else {

								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								feeStructureList.add(info);
								totalpaidamount = 0;
							}
						}
					}
				}
			}
		}

		for (FeeInfo rr : classFeeList) {
			if (rr.getFeeName().equals("Previous Fee")) {
				int fee = rr.getFeeAmount();
				int feepaidAmount = DBM.previousFeePaidRecord(schoolid,sList.getAddNumber(), conn);
				int leftamount = fee - feepaidAmount;
				FeeInfo info = new FeeInfo();
				info.setFeeName(rr.getFeeName());
				info.setFeePeriod("-");
				info.setTotalFeeAmount(fee);
				info.setTotalFeePaidAmount(feepaidAmount);
				info.setTotalFeeLeftAmount(leftamount);
				feeStructureList.add(info);
				fee = feepaidAmount = leftamount = 0;

			}
		}

		int transportFeePaid = DBM.FeePaidRecord(schoolid,sList, preSession, "0", conn); // already paid student transport fee

		transportfeeStatus = new ArrayList<FeeStatus>();
		ArrayList<Transport> transportFeeDetails = DBM.transportListDetails(schoolid,sList.getAddNumber(), preSession, conn);

		for (Transport t : transportFeeDetails) {
			FeeStatus fs = new FeeStatus();
			fs.setTransportRouteName(DBM.transportRouteNameFromId(schoolid,String.valueOf(t.getRouteId()), preSession, conn));
			fs.setMonth(t.getMonth());
			fs.setStatus(t.getStatus());

			ArrayList<ClassInfo> transportFeeList = DBM.transportRouteDetailsWithFee(schoolid,t.getRouteId(), preSession, conn);

			for (int j = 0; j < transportFeeList.size(); j++) {
				ClassInfo info1 = transportFeeList.get(j);
				if (info1.getMonth() <= t.getMonthInt()) {

					if (j == transportFeeList.size() - 1) {

						if (sList.getDiscountfee().equalsIgnoreCase("0.0")) {
							sList.setDiscountfee("0");
						}
						fs.setTransportFee(
								String.valueOf(info1.getTransportFee() - Integer.parseInt(sList.getDiscountfee())));
						transportfeeStatus.add(fs);

						break;
					} else if (transportFeeList.get(j + 1).getMonth() > t.getMonthInt()) {
						fs.setTransportFee(
								String.valueOf(info1.getTransportFee() - Integer.parseInt(sList.getDiscountfee())));
						transportfeeStatus.add(fs);

						break;
					}
				}
			}
		}

		flag = 0;
		transportFeeLeft = 0;
		int transportFee = 0;
		for (FeeStatus info : transportfeeStatus) {

			transportFee = Integer.parseInt(info.getTransportFee());
			if (transportFeePaid >= transportFee) {
				info.setStatus(String.valueOf(transportFee));
				info.setBalanceLeft("0");

				transportFeePaid -= transportFee;
			} else {
				if (transportFeePaid > 0) {
					info.setStatus(String.valueOf(transportFeePaid) + " paid");
					info.setBalanceLeft(String.valueOf(transportFee - transportFeePaid) + " left");

					firstMonthTransportPeriod = info.getMonth();

					transportFeeLeft += transportFee - transportFeePaid;
					transportFeePaid = 0;
				} else {
					if (flag == 0) {
						info.setStatus("0 paid");
						info.setBalanceLeft(String.valueOf(transportFee) + " left");

						firstMonthTransportPeriod = info.getMonth();
					} else {
						info.setStatus("0");
						info.setBalanceLeft(String.valueOf(transportFee));
					}
					transportFeeLeft += transportFee;
				}
				flag++;
			}
		}


		ArrayList<FeeInfo> tempList = new ArrayList<>();
		tempList.addAll(classFeeList);
		for (FeeInfo ls : tempList)
		{
			if(ls.getDueamount()==null)
			{

			}
			else if(ls.getDueamount().equals("0") || ls.getDueamount().equals("0.0"))
			{
				classFeeList.remove(ls);
			}
		}*/

		try {
			lateFeeCalculation();
		} catch (ParseException e1) {
			
			e1.printStackTrace();
		}


		/*
		 * int total=0; for(FeeStatus ff:transportfeeStatus) {
		 * total+=Integer.valueOf(ff.getTransportFee()); }
		 */

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void newGenerateFee()
	{
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) ss.getAttribute("feesubmit");
		sList = (StudentInfo) ss.getAttribute("selectedStudent");

		showDirectSubmitFee=false;
		nonDirectFeeSubmit=true;


		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		schoolid = sList.getSchid();
		SchoolInfoList schoolInfo = DBM.fullSchoolInfo(schoolid,conn);

		if (schoolid.equals("206")) {
			billtype = "Ist Bill";
			showtwsb = true;
		} else {
			billtype = "";
			showtwsb = false;
		}

		
		for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
		{
			if(jjj==0)
			{

				feeRemark=new DatabaseMethods1().installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
			else
			{

				feeRemark=feeRemark+","+new DatabaseMethods1().installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
		}


		//////// // System.out.println(schoolInfo.getBranch_id());
		if(schoolInfo.getBranch_id().equalsIgnoreCase("54"))
		{
			showDisabled=false;
		}
		else
		{
			showDisabled=true;
		}


		//////// // System.out.println(showDisabled);
		preSession = DatabaseMethods1.selectedSessionDetails(schoolid,conn);
		preSession.split("-");

		name = sList.getFname();
		fathersName = sList.getFathersName();
		className = sList.getClassName();
		sectionName = sList.getSectionName();
		addmissionNumber = sList.getAddNumber();
		srNo = sList.getSrNo();
		dob = sList.getDob();
		dobString = sList.getDobString();
		gender = sList.getGender();
		studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		//////// // System.out.println(connsessioncategory);
		ss.setAttribute("admisionnumber", addmissionNumber);
		String date=new SimpleDateFormat("dd/MM/yyyy").format(recipietDate);

		
		//discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);

		
		FeeDynamicList lateFees=new DatabaseMethods1().getlatefeecalculation(schoolid,conn);
		
		
		newclassFeelist=new ArrayList<>();
		dueAmount = 0;
		for(int i =0;i<selectedCheckFees.length;i++)
		{
			
			Date findDate=null;
			Date cDate=null;
			
			
			FeeDynamicList list = null;
			
			if((schoolid.equals("287") && Integer.parseInt(sList.getClassId())<13 ) ||(!schoolid.equals("287")))
			{
				 list=   new DatabaseMethods1().getAllinstallmentdetails(schoolid, selectedCheckFees[i], conn);
						
			}
			else
			{
				 try {
					list=   new DatabaseMethods1().getAllInstallmentforAjmaniDetails(schoolid, selectedCheckFees[i], conn);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				
			}
			
			
			
			
			
			try {
				findDate= list.getFee_due_date();
				cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(selectedCheckFees[i].equals("-1"))
			{
				classFeeList=new ArrayList<>();
				classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
				for(FeeInfo ls:classFeeList)
				{

					ls.setFeeInstallment(list.getInsatllmentName());
					ls.setFeeInstallMonth(selectedCheckFees[i]);
					int taxamount=0;
					int dueamountcheck=ls.getFeeAmount();
					if(Integer.parseInt(ls.getTax_percn())>0)
					{
						taxamount=((dueamountcheck*Integer.parseInt(ls.getTax_percn()))/100);
					}
					int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
					int leftAmount=(dueamountcheck+taxamount)-totalpaidamount;

					if(leftAmount>=0)
					{
						ls.setMainAmount(String.valueOf(dueamountcheck));
						ls.setTaxAmount(String.valueOf(taxamount));
						ls.setDueamount(String.valueOf(leftAmount));
						ls.setPayAmount(leftAmount);
						dueAmount +=leftAmount;

					}
					else
					{
						ls.setMainAmount(String.valueOf(0));
						ls.setTaxAmount(String.valueOf(0));
						ls.setDueamount(String.valueOf(0));
						ls.setPayAmount(0);
						dueAmount +=0;

					}

				
				}

			}
			else
			{
				classFeeList = DBM.classFeesForStudent11(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
						conn);
				
				



				int count=Integer.parseInt(list.getMonth());



				ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReportForMasterForStkabeer(schoolid,addmissionNumber, Integer.parseInt(selectedCheckFees[i]),
						preSession, conn, "feeCollection",count,list.getInstallment_Month_Include());

				HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

				for (FeeInfo ls : classFeeList) {

					////// // System.out.println(selectedCheckFees[i]+"............."+ls.getFeeName()+"............."+map.get(ls.getFeeName()));
					ls.setFeeInstallment(list.getInsatllmentName());
					ls.setFeeInstallMonth(selectedCheckFees[i]);
					if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
						if(i==0)
						{
							int taxamount=0;
							int dueamountcheck=Integer.parseInt(map.get(ls.getFeeName()));
							if(Integer.parseInt(ls.getTax_percn())>0)
							{
								taxamount=((dueamountcheck*Integer.parseInt(ls.getTax_percn()))/100);
							}
							int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
							int leftAmount=(dueamountcheck+taxamount)-totalpaidamount;

							if(leftAmount>=0)
							{
								ls.setMainAmount(String.valueOf(dueamountcheck));
								ls.setTaxAmount(String.valueOf(taxamount));
								ls.setDueamount(String.valueOf(leftAmount));
								ls.setPayAmount(leftAmount);
								dueAmount +=leftAmount;

							}
							else
							{
								ls.setMainAmount(String.valueOf(0));
								ls.setTaxAmount(String.valueOf(0));
								ls.setDueamount(String.valueOf(0));
								ls.setPayAmount(0);
								dueAmount +=0;

							}

						}
						else
						{
							int taxamount=0;


							int dueamountcheck=Integer.parseInt(map.get(ls.getFeeName()));
							if(Integer.parseInt(ls.getTax_percn())>0)
							{
								taxamount=((dueamountcheck*Integer.parseInt(ls.getTax_percn()))/100);
							}
							int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
							int leftAmount=(dueamountcheck+taxamount)-totalpaidamount;
							if(leftAmount>=0)
							{
								ls.setMainAmount(String.valueOf(dueamountcheck));
								ls.setTaxAmount(String.valueOf(taxamount));

								ls.setDueamount(String.valueOf(ls.getPayAmount()+leftAmount));
								ls.setPayAmount(ls.getPayAmount()+leftAmount);
								dueAmount += leftAmount;
							}
							else
							{
								ls.setMainAmount(String.valueOf(0));
								ls.setTaxAmount(String.valueOf(0));
								ls.setDueamount(String.valueOf(ls.getPayAmount()+0));
								ls.setPayAmount(ls.getPayAmount()+0);
								dueAmount += 0;
							}

						}




					}
					else if(ls.getFeeId().equals("-4"))
					{
						ls.setDueamount("0");
						ls.setPayAmount(0);
						ls.setMainAmount("0");
						ls.setTaxAmount("0");
					}

                    
					
					

					if(ls.getFeeId().equals("-2"))
					{
					 
						if(dueAmount>0)
						{
							int latefee=0;
		                    long diff =  cDate.getTime()-findDate.getTime() ;
							int fff =(int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							if(lateFees.getFee_due_days().equals("")) {
								latefee=0;
							}
							else
							{
								String[] feeduedays=lateFees.getFee_due_days().split(",");
								String[] latefeeamount=lateFees.getLate_fee().split(",");
								if(feeduedays.length==1)
								{
									if(Integer.parseInt(feeduedays[0])>=fff)
									{
										latefee=Integer.parseInt(latefeeamount[0]);
									}

								}
								else if(feeduedays.length==2)
								{
									int ii=Integer.parseInt(feeduedays[0]);
									int j=Integer.parseInt(feeduedays[1]);
									if(ii <= fff && fff < j)
									{
										latefee=Integer.parseInt(latefeeamount[0]);
									}
									else if(fff >= j)
									{
										latefee=Integer.parseInt(latefeeamount[1]);
									}
									else {
										latefee=0;
									}
								}


							}

							int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
							int l=latefee-totalpaidamount;
							if(l>0)
							{
								latefee=l;
							}
							else
							{
								latefee=0;
							}

						
							
							
							dueAmount=dueAmount+latefee;
							ls.setDueamount(String.valueOf(latefee));
							ls.setPayAmount(latefee);
							ls.setMainAmount(String.valueOf(latefee));
							ls.setTaxAmount("0");
						}
						
						
					}
	                ls.setSelectCheckBox(false);

				}
				
			}
			
			
			

			newclassFeelist.addAll(classFeeList);

		}

		submitAmount=dueAmount;

		ArrayList<FeeInfo> tempList = new ArrayList<>();
		tempList.addAll(newclassFeelist);
		for (FeeInfo ls : tempList)
		{
			if(ls.getDueamount()==null)
			{

			}
			else if(Integer.parseInt(ls.getDueamount())<=0 && !ls.getFeeId().equals("-4"))
			{
				newclassFeelist.remove(ls);
			}
		}

		int xy = 1;
		for(FeeInfo fi : newclassFeelist)
		{
			if(fi.getFeeId().equals("-2"))
			{
				fi.setDisabledDiscountFee(false);
			}
			else
			{
				fi.setDisabledDiscountFee(disabledfees);
			}

			fi.setSelectCheckBox(true);
			fi.setSno(xy++);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void checkSiblingFee() {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("siblingStudent", sList);
		PrimeFaces.current().executeInitScript("window.open('siblingFeeCollection.xhtml')");
	}

	public String previousSubmit()
	{
		String messageForLock=checkLock();
		String submit="";
		if(checkLock==false)
		{
			FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String etype="madmin";
			if(etype.equalsIgnoreCase("madmin"))
			{
				boolean check=false;
				for(FeeInfo ff : newclassFeelist)
				{
					if(!ff.getFeeId().equals("-2")&&ff.getPayDiscount()>0)
					{
						check=true;
						break;
					}
				}


				if(check==false)
				{
					reason="";
					submit=submit();
				}
				else
				{
					reason="Management Discount";
					PrimeFaces context = PrimeFaces.current();
					context.executeInitScript("PF('dis').show();");
					submit="";
				}
			}
			else
			{
				reason="";
				submit=submit();
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
		}

		

		return submit;
	}


	
	public String  check()
	{
		
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		double submitAmount1 = 0;
		double discountAmount1 = 0;
		for (FeeInfo ff : newclassFeelist) {
			submitAmount1 += ff.getPayAmount();
			discountAmount1 += ff.getPayDiscount();
		}
		
		if (info.getDiscountotp().equalsIgnoreCase("yes") && discountAmount1 > 0) {

			int randomPIN = (int) (Math.random() * 9000) + 1000;
			otp = String.valueOf(randomPIN);
			discoutnNo = info.getDiscountOtpNo();
			String typemessage = "Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
					+ randomPIN + ") to allow for discount.Treat this as confidential.Thank You.  \n"
					+ info.getSmsSchoolName();
			DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,obj.schoolId(),"");
			PrimeFaces.current().ajax().update("otpdialog");
			PrimeFaces.current().executeInitScript("PF('otp').show()");
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		} else {
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return submit();
		}
		
		
		
	}
	
	
	
	public String submit() {
		HttpSession sss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) sss.getAttribute("feesubmit");
		sList = (StudentInfo) sss.getAttribute("selectedStudent");
		schoolid = sList.getSchid();
		String userId = (String) sss.getAttribute("username");
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		submitAmount = 0;
		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : newclassFeelist)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}


		for (FeeInfo ff : selectedFees) {
			submitAmount += ff.getPayAmount();
			discountAmount += ff.getPayDiscount();
		}

		try {
		
				int amoutnt = 0, discount = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;

				if (flag == true) {
					String feename = selectedFees.get(i).getFeeName();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(feename + " must Be Greater Than Zero"));
					return "";
				} else {
					if (flag1 == true) {
						String feename = selectedFees.get(i).getFeeName();
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(feename + " Only " + balLeft + " Rs. Left"));
						return "";
					} else {
						// new DatabaseMethods1().insertfee(name,className);
						int number = DBM.feeserailno(schoolid,conn);
						String num = "";
						if (String.valueOf(number).length() == 1) {
							num = "0000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 2) {
							num = "000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 3) {
							num = "00" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 4) {
							num = "0" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 5) {
							num = String.valueOf(number);
						}

						int ii = 0;
						for (FeeInfo ff : selectedFees) {
							if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
								ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
							}
							/*for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
							{
								if(jjj==0)
								{
									//	installment=selectedCheckFees[jjj];
									remark=instalcheck(selectedCheckFees[jjj]);
								}
								else
								{

									//installment=installment+","+selectedCheckFees[jjj];
									remark=remark+","+instalcheck(selectedCheckFees[jjj]);
								}
							}



							if( (!(reason==null)) && !reason.equals(""))
							{
								remark=remark+"("+reason+")";
							}*/



							if(ff.getFeeId().equals("-2")&& waveOffLateFee==true)
							{
								ff.setPayDiscount(ff.getPayAmount());
								ff.setPayAmount(0);
							}

                                 //// // System.out.println(ff.getFeeName()+"....."+ff.getFeeId());
							ii = DBM.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
									chequeNumber, num, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
									challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",ff.getFeeInstallMonth(),ff.getMainAmount(),ff.getTaxAmount(),userId,"active", "");
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
							discount += ff.getPayDiscount();
							
							if(ii>=1)
							{
								String refNo2;
								try {
									refNo2=addWorkLog2(conn,ff.getPayAmount(),ff.getFeeId(),num,ff.getPayDiscount(),ff.getDueamount(),ff.getFeeInstallMonth(),ff.getMainAmount(),ff.getTaxAmount());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						}

						if (ii >= 1) {
							String modArr[]=new String[0];
							int totalAmt = amoutnt+discount;
							obj.blockStudentAppMods(sList.getAddNumber(),"Fees Block",modArr,schoolid,totalAmt,"","auto",conn);
							
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Fees Added Successfully"));

							info.getSmsSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(schoolid,Double.valueOf(amount), conn);
								}
							}*/
							
						/*	String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by " + paymentMode + " via Receipt No. " + num
									+ ".\n\nRegards,  \n"
									+ info.getSmsSchoolName();
						*/	
							
							String typeMessage="Dear Parent,\n"+name+" ("+sList.className+")"+" Fee Receipt No. "+num+" Rs."+amoutnt+" Via "+paymentMode+" For "+feeRemark+"\n"+info.getSmsSchoolName();
							System.out.println(typeMessage);
							
							

							if (message.equals("true")) {
								
								String templateId=new DataBaseMeathodJson().templateId(info.getKey(),"FeeCollection",conn);
								new DataBaseMeathodJson().messageUrlWithTemplate(String.valueOf(sList.getFathersPhone()), typeMessage,sList.getAddNumber(),obj.schoolId(),conn,templateId);
								//DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,sList.getAddNumber(),conn,obj.schoolId(),templateId);
								 
								/*
								 * 
								 * DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,sList.getAddNumber(),conn,obj.schoolId(),templateId);
								 * DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
								 * sList.getAddNumber(), conn,schoolid);
								 */
							}

							DBM.notification(schoolid,"Fees", typeMessage,
									String.valueOf(sList.getFathersPhone()) + "-" + schoolid, conn);
							DBM.notification(schoolid,"Fees", typeMessage,
									String.valueOf(sList.getMothersPhone()) + "-" + schoolid, conn);
							 
							HttpSession rr = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
									.getSession(false);
							rr.setAttribute("type1", "origanal");
							rr.setAttribute("paymentmode", paymentMode);
							rr.setAttribute("bankname", bankName);
							rr.setAttribute("chequeno", chequeNumber);
							rr.setAttribute("receiptNumber", num);
							rr.setAttribute("selectedFee", selectedFees);
							rr.setAttribute("receiptDate", recipietDate);
							rr.setAttribute("chaqueDate", challanDate);
							rr.setAttribute("remark", remark);
							rr.setAttribute("rDate", recipietDate);
							rr.setAttribute("feeupto", feeRemark);

							PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml');");
							try {
								FacesContext.getCurrentInstance().getExternalContext().redirect("installmentWiseFeeCollection.xhtml");
							} catch (IOException e) {
								
								e.printStackTrace();
							}
							
							return "installmentWiseFeeCollection";
						} else {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Please select At Least One Fee Type"));
						}
					}
				}
			

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	
	
	
	private String addWorkLog2(Connection conn, int payAmount, String feeId, String num, int payDiscount,
			String dueamount2, String feeInstallMonth, String mainAmount, String taxAmount) {
		
		String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String st = "";
		if(recipietDate != null)
		{
		 try {
			st = formater.format(recipietDate);
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		} 
		
		
		String ChlnDate = "";
		if(challanDate != null)
		{
		 try {
			ChlnDate = formater.format(challanDate);
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		} 
		
		String nftDate = "";
		if(neftDate != null)
		{	
		 try {
			nftDate = formater.format(neftDate);
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		}
		
		String dueDatee = "";
		if(dueDate != null)
		{
		 try {
			dueDatee = formater.format(dueDate);
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		} 
		
		language = autoname;
        value = "Pay Amt.-"+payAmount+" --FeeId-"+feeId+" --Number-"+num+" --Payment Mode-"+paymentMode+" --Bank-"+bankName+
				" --ChequeNum-"+chequeNumber+" --PayDiscount-"+payDiscount+" --PreSssion"+ preSession+" --Receipt Date-"+st+" --Challan No-"+ challanNo+" --Neft No-"+neftNo+
				" --Challan date-"+ChlnDate+" --Neft Date-"+ nftDate+" --Remark-"+remark+" --Due Date-"+ dueDatee+" --Due Amount-"+dueDatee+ " --current"+" --fee Install Month-"+feeInstallMonth+" --Main Amount-"+mainAmount+" --TaxAmount-"+taxAmount;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add  Fee Collection","WEB",value,conn);
		return refNo;
	}


	public String submitWithReceipietNO() {
		HttpSession sss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) sss.getAttribute("feesubmit");
		sList = (StudentInfo) sss.getAttribute("selectedStudent");
		schoolid = sList.getSchid();
		String userId = (String) sss.getAttribute("username");
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		submitAmount = 0;
		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : newclassFeelist)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}


		for (FeeInfo ff : selectedFees) {
			submitAmount += ff.getPayAmount();
			discountAmount += ff.getPayDiscount();
		}

		try {
			if (info.getDiscountotp().equalsIgnoreCase("yes") && discountAmount > 0) {

				int randomPIN = (int) (Math.random() * 9000) + 1000;
				otp = String.valueOf(randomPIN);
				discoutnNo = info.getDiscountOtpNo();
				String typemessage = "Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
						+ randomPIN + ") to allow for discount.Treat this as confidential.Thank You.  \n"
						+ info.getSmsSchoolName();
				DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,obj.schoolId(),"");
				PrimeFaces.current().ajax().update("otpdialog");
				PrimeFaces.current().executeInitScript("PF('otp').show()");

				return "";
			} else {
				int amoutnt = 0, discount = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;

				if (flag == true) {
					String feename = selectedFees.get(i).getFeeName();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(feename + " must Be Greater Than Zero"));
					return "";
				} else {
					if (flag1 == true) {
						String feename = selectedFees.get(i).getFeeName();
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(feename + " Only " + balLeft + " Rs. Left"));
						return "";
					} else {
						// new DatabaseMethods1().insertfee(name,className);
						/*int number = DBM.feeserailno(schoolid,conn);
						String num = "";
						if (String.valueOf(number).length() == 1) {
							num = "0000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 2) {
							num = "000" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 3) {
							num = "00" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 4) {
							num = "0" + String.valueOf(number);
						} else if (String.valueOf(number).length() == 5) {
							num = String.valueOf(number);
						}*/

						int ii = 0;
						for (FeeInfo ff : selectedFees) {
							if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
								ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
							}
							for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
							{
								if(jjj==0)
								{
									//	installment=selectedCheckFees[jjj];
									remark=instalcheck(selectedCheckFees[jjj]);
								}
								else
								{

									//installment=installment+","+selectedCheckFees[jjj];
									remark=remark+","+instalcheck(selectedCheckFees[jjj]);
								}
							}



							if( (!(reason==null)) && !reason.equals(""))
							{
								remark=remark+"("+reason+")";
							}



							if(ff.getFeeId().equals("-2")&& waveOffLateFee==true)
							{
								ff.setPayDiscount(ff.getPayAmount());
								ff.setPayAmount(0);
							}

                                 //// // System.out.println(ff.getFeeName()+"....."+ff.getFeeId());
							ii = DBM.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
									chequeNumber, recepietNo, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
									challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",ff.getFeeInstallMonth(),ff.getMainAmount(),ff.getTaxAmount(),userId,"active", "");
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
							discount += ff.getPayDiscount();
							
							if(ii>=1)
							{
								String refNo3;
								try {
									refNo3=addWorkLog3(conn,ff.getPayAmount(),ff.getFeeId(),ff.getPayDiscount(),ff.getDueamount(),ff.getFeeInstallMonth(),ff.getMainAmount(),ff.getTaxAmount());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						if (ii >= 1) {
							
							String modArr[]=new String[0];
							int totalAmt = amoutnt+discount;
							obj.blockStudentAppMods(sList.getAddNumber(),"Fees Block",modArr,schoolid,totalAmt,"","auto",conn);
							
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Fees Added Successfully"));

							info.getSmsSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(schoolid,Double.valueOf(amount), conn);
								}
							}*/

							/*if (message.equals("true")) {
								DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
										sList.getAddNumber(), conn,schoolid);
							}

							DBM.notification(schoolid,"Fees", typeMessage,
									String.valueOf(sList.getFathersPhone()) + "-" + schoolid, conn);
							DBM.notification(schoolid,"Fees", typeMessage,
									String.valueOf(sList.getMothersPhone()) + "-" + schoolid, conn);
							 */
							HttpSession rr = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
									.getSession(false);
							rr.setAttribute("type1", "origanal");
							rr.setAttribute("paymentmode", paymentMode);
							rr.setAttribute("bankname", bankName);
							rr.setAttribute("chequeno", chequeNumber);
							rr.setAttribute("receiptNumber", recepietNo);
							rr.setAttribute("selectedFee", selectedFees);
							rr.setAttribute("receiptDate", recipietDate);
							rr.setAttribute("chaqueDate", challanDate);
							rr.setAttribute("remark", remark);
							rr.setAttribute("rDate", recipietDate);
							rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

							PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");
							 
							return "InstallmentWiseFeeCollectionWithRecipiet";
						} else {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Please select At Least One Fee Type"));
						}
					}
				}
			}

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	
	private String addWorkLog3(Connection conn, int payAmount, String feeId, int payDiscount,
			String dueamount2, String feeInstallMonth, String mainAmount, String taxAmount) {
		
		String value = "";
		String language= "";
		
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String st = "";
		try {
			st = formater.format(recipietDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ChlnDate = "";
		try {
			ChlnDate = formater.format(challanDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String nftDate = "";
		try {
			nftDate = formater.format(neftDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dueDatee = "";
		try {
			dueDatee = formater.format(dueDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		language = autoname;
        value = "Pay Amt.-"+payAmount+" --FeeId-"+feeId+" --Payment Mode-"+paymentMode+" --Bank-"+bankName+
				" --ChequeNum-"+chequeNumber+" --PayDiscount-"+payDiscount+" --PreSssion"+ preSession+" --Receipt Date-"+st+" --Challan No-"+ challanNo+" --Neft No-"+neftNo+
				" --Challan date-"+ChlnDate+" --Neft Date-"+ nftDate+" --Remark-"+remark+" --Due Date-"+ dueDatee+" --Due Amount-"+dueDatee+ " --current"+" --fee Install Month-"+feeInstallMonth+" --Main Amount-"+mainAmount+" --TaxAmount-"+taxAmount;
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add  Fee Collection With Receipt No","WEB",value,conn);
		return refNo;
	}
	
	
	
	public String submitFeeChallan() {
		HttpSession sss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) sss.getAttribute("feesubmit");
		sList = (StudentInfo) sss.getAttribute("selectedStudent");
		schoolid = sList.getSchid();
		String userId = (String) sss.getAttribute("username");
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		submitAmount = 0;
		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : newclassFeelist)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}


		for (FeeInfo ff : selectedFees) {
			submitAmount += ff.getPayAmount();
			discountAmount += ff.getPayDiscount();
		}

		try {
			
							HttpSession rr = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
							.getSession(false);
							rr.setAttribute("type1", "origanal");
							rr.setAttribute("paymentmode", "Cash");
							rr.setAttribute("bankname", "");
							rr.setAttribute("chequeno", "");
							rr.setAttribute("receiptNumber", "");
							rr.setAttribute("selectedFee", selectedFees);
							rr.setAttribute("receiptDate", "");
							rr.setAttribute("chaqueDate", "");
							rr.setAttribute("remark", "");
							rr.setAttribute("rDate", "");
							rr.setAttribute("feeupto", selectedFees.get(0).getFeeInstallment());

							PrimeFaces.current().executeInitScript("window.open('FeeChallanBook.xhtml')");
							 
							
						
					
				
			

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	
	
	
	

	public String instalcheck(String month)
	{

		String smonth="";

		if(month.equals("4"))
		{
			smonth="April";
		}
		else if(month.equals("6"))
		{
			smonth="May-June";
		}
		else if(month.equals("8"))
		{
			smonth="Jul-Aug";
		}
		else if(month.equals("9"))
		{
			smonth="September";
		}
		else if(month.equals("11"))
		{
			smonth="Oct-Nov";
		}
		else if(month.equals("12"))
		{
			smonth="December";
		}
		else if(month.equals("1"))
		{
			smonth="January";
		}
		else if(month.equals("3"))
		{
			smonth="Feb-Mar";
		}

		return smonth;

	}


	public void editFee()
	{
		System.out.println(recipietDate);
		String messageForLock=checkLock();
		if(checkLock==false)
		{
			/*HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			rr.setAttribute("selectedStudent", selectedstudent);
			rr.setAttribute("type1", "duplicate");
			rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
			rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
			rr.setAttribute("bankname", selectedstudent.getBankname());
			rr.setAttribute("chequeno",selectedstudent.getChequenumber());
			rr.setAttribute("remark", selectedstudent.getFeeRemark());
			rr.setAttribute("type1","duplicate");
			rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
			rr.setAttribute("feeupto", selectedstudent.getDueDateStr());*/
			feesSelected=new ArrayList<>();
			//HashMap<String, String>hasmap=selectedstudent.getAllFess();
			Connection conn = DataBaseConnection.javaConnection();
			// feelist=obj.viewFeeList1(selectedstudent.getSchid(),conn);

			feesSelected=obj.studetFeeCollectionByRecipietNo(sList.getAddNumber(),selectedstudent.getReciptno(),sList.getSchid(),conn);
			changeDate=feesSelected.get(0).getFeedate();
			selectedFeeRemarkNew=feesSelected.get(0).getFeeRemark();
			double totalPAmount=0,totalPdiscount=0;
			for(Feecollectionc ls:feesSelected)
			{
				totalPAmount +=ls.getFeeamunt();
				totalPdiscount +=ls.getDiscount();
			}

			totalPayAmunt=String.valueOf(totalPAmount);
			totalDiscountAmount=String.valueOf(totalPdiscount);
			/*  for(FeeInfo ff:feelist)
		    {
			    	String fee=selectedstudent.getAllFess().get(ff.getFeeId());
			    	String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
			    	String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
			    	if(fee==null)
			    	{

			    	}
			    	else
			    	{

			    		FeeInfo info=new FeeInfo();
			    		info.setSno(i);
			    		info.setFeeName(ff.getFeeName());
		            	info.setPayAmount(Integer.parseInt(fee));
		            	info.setPayDiscount(Integer.valueOf(disc));
		            	info.setDueamount(totalAmt);
		            	feesSelected.add(info);
		            	i=i+1;

			    	}
		    }
			 */

			/*	rr.setAttribute("selectedFee", selectedFees);
			rr.setAttribute("receiptDate", selectedstudent.getFeedate());
			PrimeFaces.current().executeInitScript("window.open('masterFeeReceipt.xhtml')");*/
			
			PrimeFaces.current().executeInitScript("PF('editfee').show();");
			PrimeFaces.current().ajax().update("form5");
			

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,messageForLock, "Validation error"));
			PrimeFaces.current().ajax().update("form");
		}
		
	}


	public void viewFee()
	{
		/*rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());*/
		feesSelected=new ArrayList<>();
		//HashMap<String, String>hasmap=selectedstudent.getAllFess();
		Connection conn = DataBaseConnection.javaConnection();
		// feelist=obj.viewFeeList1(selectedstudent.getSchid(),conn);

		feesSelected=obj.studetFeeCollectionByRecipietNo(sList.getAddNumber(),selectedstudent.getReciptno(),sList.getSchid(),conn);
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);

		/* changeDate=feesSelected.get(0).getFeedate();
        double totalPAmount=0,totalPdiscount=0;
        for(Feecollectionc ls:feesSelected)
        {
            totalPAmount +=ls.getFeeamunt();
            totalPdiscount +=ls.getDiscount();
        }
		 */

		/* totalPayAmunt=String.valueOf(totalPAmount);
        totalDiscountAmount=String.valueOf(totalPdiscount);

		 */


		/*  for(FeeInfo ff:feelist)
	    {
		    	String fee=selectedstudent.getAllFess().get(ff.getFeeId());
		    	String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
		    	String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
		    	if(fee==null)
		    	{

		    	}
		    	else
		    	{

		    		FeeInfo info=new FeeInfo();
		    		info.setSno(i);
		    		info.setFeeName(ff.getFeeName());
	            	info.setPayAmount(Integer.parseInt(fee));
	            	info.setPayDiscount(Integer.valueOf(disc));
	            	info.setDueamount(totalAmt);
	            	feesSelected.add(info);
	            	i=i+1;

		    	}
	    }
		 */

		////// // System.out.println(feesSelected.size());


		/*	rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('masterFeeReceipt.xhtml')");*/

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


	public void classFeeList()
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

	public void calculateamtCheck() {
		FacesContext context = FacesContext.getCurrentInstance();
		int k = (int) UIComponent.getCurrentComponent(context).getAttributes().get("auto");
		submitAmount = 0;
		discountAmount = 0;

		if (newclassFeelist.size() > 0)
		{
			for (FeeInfo ff : newclassFeelist)
			{
				//////// // System.out.println(ff.getFeeName());
				if (k == ff.getSno())
				{

					if (ff.getDueamount().equals("0"))
					{
						ff.setPayAmount(0);
						ff.setPayDiscount(0);
					}
					else
					{
						int due = Integer.parseInt(ff.getDueamount());
						int disc = ff.getPayDiscount();
						ff.setPayAmount(due - disc);
					}
				}

				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}

		}
	}



	public void calculateSubmitAmt() {
		FacesContext context = FacesContext.getCurrentInstance();
		UIComponent.getCurrentComponent(context).getAttributes().get("auto");
		submitAmount = 0;
		discountAmount = 0;

		if (newclassFeelist.size() > 0)
		{
			for (FeeInfo ff : newclassFeelist)
			{
				//////// // System.out.println(ff.getFeeName());

				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}

		}
	}



	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		new DatabaseMethods1().udpatefeesWithRemark(feesSelected,changeDate,selectedFeeRemarkNew,conn);
		searchStudentByName();
	}

	public String discountSave() {
		HttpSession sss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) sss.getAttribute("feesubmit");
		sList = (StudentInfo) sss.getAttribute("selectedStudent");
		schoolid = sList.getSchid();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		/*selectedFees = new ArrayList<>();

		for(FeeInfo ff : classFeeList)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}*/

		
		try {
			if (!otp.equals(otpInput)) {
				/*
				 * SchoolInfoList info =DBM.fullSchoolInfo(conn);
				 *
				 * int randomPIN = (int)(Math.random()*9000)+1000;
				 * otp=String.valueOf(randomPIN); discoutnNo=info.getDiscountOtpNo(); String
				 * typemessage="Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
				 * +randomPIN+") to allow for discount.Treat this as confidential.Thank You.  \n"
				 * +info.getSmsSchoolName(); new
				 * DatabaseMethods1().messageurlStaff(info.getDiscountOtpNo(),
				 * typemessage,"admin",conn);
				 * PrimeFaces.current().ajax().update("otpdialog");
				 * PrimeFaces.current().executeInitScript("PF('otp').show()");
				 */
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Not Matched"));

				return "";
			} else {
				return submit();
				
			}

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
	}
	public void cancelReceipt()
	{
		//String schoolid = schoolid;;
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
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//int prevFee = Integer.valueOf(selectedstudent.getAllFess().get("-1"));
				//int paidPrevFee = obj.previousFeePaidRecord(schoolid,selectedstudent.getStudentid(), conn);

				//int newPrevFee = paidPrevFee-prevFee;

				//obj.cancelPaidPreviousFee(schoolid,selectedstudent.getStudentid(),newPrevFee,conn);
				//////// // System.out.println("Prev Fee Paid : "+prevFee);
				cancelremark="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Receipt Cancelled Sucessfully"));
				//show=false;
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
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		language = autoname;
		value = "Selected Receipt-"+selectedstudent.getReciptno()+" --Cancel Remark"+cancelremark;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Cancel Fee Receipt","WEB",value,conn);
		return refNo;
	}
	

	public String searchStudentByNameByExpense() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		/*
		 * boolean checkBillGeneration=new
		 * DataBaseValidator().checkBillGeneration(sList.getAddNumber(), billtype,
		 * conn); if(checkBillGeneration==true) {
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(billtype+" has already generated!")); } else {
		 */
		if (billtype.equalsIgnoreCase("Final Bill")) {
			boolean checkFirstBill = new DataBaseValidator().checkBillGeneration(sList.getAddNumber(), "Ist Bill",
					conn);
			if (checkFirstBill == false) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Generate Ist Bill First Then Try To Generate Final Bill!"));
			} else {
				obj.insertBillGeneration(sList.getAddNumber(), billtype, conn);
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				ss.setAttribute("selectedStudent", sList);
				ss.setAttribute("billtype", billtype);
				PrimeFaces.current().executeInitScript("window.open('studentExpenseLedger.xhtml')");
				PrimeFaces.current().executeInitScript("window.open('billPayment.xhtml')");
			}
		} else {
			obj.insertBillGeneration(sList.getAddNumber(), billtype, conn);
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", sList);
			ss.setAttribute("billtype", billtype);
			PrimeFaces.current().executeInitScript("window.open('studentExpenseLedger.xhtml')");
			PrimeFaces.current().executeInitScript("window.open('billPayment.xhtml')");
		}
		// }
		// return "billPayment";
		// show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void paymentModeListener() {
		showFeeList = true;
		if (paymentMode.equals("Cheque")) {
			showPaymentMode = true;
			showCheque = true;
			showChallan = false;
			showNeft = false;
		} else if (paymentMode.equals("Challan")) {
			showPaymentMode = showChallan = true;
			showNeft = showCheque = false;
		} else if (paymentMode.equals("Net Banking")) {
			showPaymentMode = showNeft = true;
			showChallan = showCheque = false;
		} else {
			showPaymentMode = showChallan = showNeft = showCheque = false;
		}
	}

	public void createInstallment()
	{
		installmentList2 = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList2.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("6");
		installmentList2.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("8");
		installmentList2.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList2.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("11");
		installmentList2.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList2.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("1");
		installmentList2.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("3");
		installmentList2.add(ss8);
	}

	public void printFeeCertificate()
	{
		int index=autoname.lastIndexOf(":")+1;
		String id=autoname.substring(index);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent",id);
		ss.setAttribute("months",checkMonthSelected);
		ss.setAttribute("schoolid",selectedStudent.getSchid());
		ss.setAttribute("schId", selectedStudent.getSchid());

		PrimeFaces.current().executeInitScript("window.open('printFeeCertificateForBLM.xhtml')");
	}

	public void studentLedger()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedStudent", selectedStudent);


		PrimeFaces.current().executeInitScript("window.open('StudentLedger.xhtml')");
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

	public String getFeeModuleType() {
		return feeModuleType;
	}

	public void setFeeModuleType(String feeModuleType) {
		this.feeModuleType = feeModuleType;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getPreSession() {
		return preSession;
	}

	public void setPreSession(String preSession) {
		this.preSession = preSession;
	}

	public String getFirstMonthTransportPeriod() {
		return firstMonthTransportPeriod;
	}

	public void setFirstMonthTransportPeriod(String firstMonthTransportPeriod) {
		this.firstMonthTransportPeriod = firstMonthTransportPeriod;
	}

	public String getLastMonthTransportPeriod() {
		return lastMonthTransportPeriod;
	}

	public void setLastMonthTransportPeriod(String lastMonthTransportPeriod) {
		this.lastMonthTransportPeriod = lastMonthTransportPeriod;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getLastMonthTransportPeriodTemp() {
		return lastMonthTransportPeriodTemp;
	}

	public void setLastMonthTransportPeriodTemp(String lastMonthTransportPeriodTemp) {
		this.lastMonthTransportPeriodTemp = lastMonthTransportPeriodTemp;
	}

	public String getAddmissionNumber() {
		return addmissionNumber;
	}

	public void setAddmissionNumber(String addmissionNumber) {
		this.addmissionNumber = addmissionNumber;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getMn() {
		return mn;
	}

	public void setMn(int mn) {
		this.mn = mn;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getAdmissionDiscount() {
		return admissionDiscount;
	}

	public void setAdmissionDiscount(int admissionDiscount) {
		this.admissionDiscount = admissionDiscount;
	}

	public int getExaminationDiscount() {
		return examinationDiscount;
	}

	public void setExaminationDiscount(int examinationDiscount) {
		this.examinationDiscount = examinationDiscount;
	}

	public int getTutionDiscount() {
		return tutionDiscount;
	}



	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public void setTutionDiscount(int tutionDiscount) {
		this.tutionDiscount = tutionDiscount;
	}

	public int getAnnualDiscount() {
		return annualDiscount;
	}

	public void setAnnualDiscount(int annualDiscount) {
		this.annualDiscount = annualDiscount;
	}

	public int getTransportDiscount() {
		return transportDiscount;
	}

	public void setTransportDiscount(int transportDiscount) {
		this.transportDiscount = transportDiscount;
	}

	public int getTermDiscount() {
		return termDiscount;
	}

	public void setTermDiscount(int termDiscount) {
		this.termDiscount = termDiscount;
	}

	public int getArtDiscount() {
		return artDiscount;
	}

	public void setArtDiscount(int artDiscount) {
		this.artDiscount = artDiscount;
	}

	public int getActivityDiscount() {
		return activityDiscount;
	}

	public void setActivityDiscount(int activityDiscount) {
		this.activityDiscount = activityDiscount;
	}

	public StudentInfo getsList() {
		return sList;
	}

	public void setsList(StudentInfo sList) {
		this.sList = sList;
	}

	public ArrayList<StudentInfo> getSerialno() {
		return serialno;
	}

	public void setSerialno(ArrayList<StudentInfo> serialno) {
		this.serialno = serialno;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public boolean isShowPaymentMode() {
		return showPaymentMode;
	}

	public void setShowPaymentMode(boolean showPaymentMode) {
		this.showPaymentMode = showPaymentMode;
	}

	public ArrayList<FeeInfo> getClassFeeList() {
		return classFeeList;
	}

	public void setClassFeeList(ArrayList<FeeInfo> classFeeList) {
		this.classFeeList = classFeeList;
	}

	public boolean isShowFeeList() {
		return showFeeList;
	}

	public void setShowFeeList(boolean showFeeList) {
		this.showFeeList = showFeeList;
	}

	public String getDobString() {
		return dobString;
	}

	public void setDobString(String dobString) {
		this.dobString = dobString;
	}

	public ArrayList<FeeInfo> getSelectedFees() {
		return selectedFees;
	}

	public ArrayList<FeeStatus> getTransportfeeStatus() {
		return transportfeeStatus;
	}

	public void setTransportfeeStatus(ArrayList<FeeStatus> transportfeeStatus) {
		this.transportfeeStatus = transportfeeStatus;
	}

	public void setSelectedFees(ArrayList<FeeInfo> selectedFees) {
		this.selectedFees = selectedFees;
	}

	public ArrayList<FeeInfo> getFeeStructureList() {
		return feeStructureList;
	}

	public void setFeeStructureList(ArrayList<FeeInfo> feeStructureList) {
		this.feeStructureList = feeStructureList;
	}

	public String getChallanNo() {
		return challanNo;
	}

	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}

	public String getNeftNo() {
		return neftNo;
	}

	public void setNeftNo(String neftNo) {
		this.neftNo = neftNo;
	}

	public Date getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(Date challanDate) {
		this.challanDate = challanDate;
	}

	public Date getNeftDate() {
		return neftDate;
	}

	public void setNeftDate(Date neftDate) {
		this.neftDate = neftDate;
	}

	public boolean isShowChallan() {
		return showChallan;
	}

	public void setShowChallan(boolean showChallan) {
		this.showChallan = showChallan;
	}

	public boolean isShowNeft() {
		return showNeft;
	}

	public void setShowNeft(boolean showNeft) {
		this.showNeft = showNeft;
	}

	public boolean isShowCheque() {
		return showCheque;
	}

	public void setShowCheque(boolean showCheque) {
		this.showCheque = showCheque;
	}

	public Date getRecipietDate() {
		return recipietDate;
	}

	public void setRecipietDate(Date recipietDate) {
		this.recipietDate = recipietDate;
	}

	public int getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(int dueAmount) {
		this.dueAmount = dueAmount;
	}

	public int getSubmitAmount() {
		return submitAmount;
	}

	public void setSubmitAmount(int submitAmount) {
		this.submitAmount = submitAmount;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public boolean isShowtwsb() {
		return showtwsb;
	}

	public void setShowtwsb(boolean showtwsb) {
		this.showtwsb = showtwsb;
	}

	public String getBilltype() {
		return billtype;
	}

	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}

	public ArrayList<FeeInfo> getDiscountFeeList() {
		return discountFeeList;
	}

	public void setDiscountFeeList(ArrayList<FeeInfo> discountFeeList) {
		this.discountFeeList = discountFeeList;
	}

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(double totalamount) {
		this.totalamount = totalamount;
	}

	public boolean isShowDisabled() {
		return showDisabled;
	}

	public void setShowDisabled(boolean showDisabled) {
		this.showDisabled = showDisabled;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public String getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	public ArrayList<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}



	public Date getFeedate() {
		return feedate;
	}



	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public int getFlag() {
		return flag;
	}



	public void setFlag(int flag) {
		this.flag = flag;
	}



	public int getTransportFeeLeft() {
		return transportFeeLeft;
	}



	public void setTransportFeeLeft(int transportFeeLeft) {
		this.transportFeeLeft = transportFeeLeft;
	}



	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}



	public void setGetdailyfeecollection(ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}



	public String getStudentStatus() {
		return studentStatus;
	}



	public void setStudentStatus(String studentStatus) {
		this.studentStatus = studentStatus;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String getOtp() {
		return otp;
	}



	public void setOtp(String otp) {
		this.otp = otp;
	}



	public String getSchoolid() {
		return schoolid;
	}



	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}



	public ArrayList<FeeInfo> getList() {
		return list;
	}



	public void setList(ArrayList<FeeInfo> list) {
		this.list = list;
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



	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}



	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
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



	public int getCashAmount() {
		return cashAmount;
	}



	public void setCashAmount(int cashAmount) {
		this.cashAmount = cashAmount;
	}



	public int getChequeAmount() {
		return chequeAmount;
	}



	public void setChequeAmount(int chequeAmount) {
		this.chequeAmount = chequeAmount;
	}



	public ArrayList<FeeInfo> getFeelist1() {
		return feelist1;
	}



	public void setFeelist1(ArrayList<FeeInfo> feelist1) {
		this.feelist1 = feelist1;
	}



	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}



	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}



	public ArrayList<Feecollectionc> getFeesSelected() {
		return feesSelected;
	}



	public void setFeesSelected(ArrayList<Feecollectionc> feesSelected) {
		this.feesSelected = feesSelected;
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



	public String[] getSelectedCheckFees() {
		return selectedCheckFees;
	}



	public void setSelectedCheckFees(String[] selectedCheckFees) {
		this.selectedCheckFees = selectedCheckFees;
	}
	public String getAutoname() {
		return autoname;
	}



	public void setAutoname(String autoname) {
		this.autoname = autoname;
	}

	public boolean isWaveOffLateFee() {
		return waveOffLateFee;
	}

	public void setWaveOffLateFee(boolean waveOffLateFee) {
		this.waveOffLateFee = waveOffLateFee;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public boolean isDisabledfees() {
		return disabledfees;
	}

	public void setDisabledfees(boolean disabledfees) {
		this.disabledfees = disabledfees;
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

	public ArrayList<FeeInfo> getNewclassFeelist() {
		return newclassFeelist;
	}

	public void setNewclassFeelist(ArrayList<FeeInfo> newclassFeelist) {
		this.newclassFeelist = newclassFeelist;
	}

	public String getTotalCollection() {
		return totalCollection;
	}

	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public ArrayList<SelectItem> getInstallmentList2() {
		return installmentList2;
	}

	public void setInstallmentList2(ArrayList<SelectItem> installmentList2) {
		this.installmentList2 = installmentList2;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}

	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}

	public boolean isDirectSubmitFee() {
		return directSubmitFee;
	}

	public void setDirectSubmitFee(boolean directSubmitFee) {
		this.directSubmitFee = directSubmitFee;
	}

	public boolean isNonDirectFeeSubmit() {
		return nonDirectFeeSubmit;
	}

	public void setNonDirectFeeSubmit(boolean nonDirectFeeSubmit) {
		this.nonDirectFeeSubmit = nonDirectFeeSubmit;
	}

	public String getCancelremark() {
		return cancelremark;
	}

	public void setCancelremark(String cancelremark) {
		this.cancelremark = cancelremark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


	public String getTotalseletedAMT() {
		return totalseletedAMT;
	}


	public void setTotalseletedAMT(String totalseletedAMT) {
		this.totalseletedAMT = totalseletedAMT;
	}


	public String getRecepietNo() {
		return recepietNo;
	}


	public void setRecepietNo(String recepietNo) {
		this.recepietNo = recepietNo;
	}


	public String getFeeRemark() {
		return feeRemark;
	}


	public void setFeeRemark(String feeRemark) {
		this.feeRemark = feeRemark;
	}


	public Date getMaxDate() {
		return maxDate;
	}


	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}


	public boolean isShowFees() {
		return showFees;
	}


	public void setShowFees(boolean showFees) {
		this.showFees = showFees;
	}


	public boolean isShowDirectSubmitFee() {
		return showDirectSubmitFee;
	}


	public void setShowDirectSubmitFee(boolean showDirectSubmitFee) {
		this.showDirectSubmitFee = showDirectSubmitFee;
	}


	public String getSelectedFeeRemarkNew() {
		return selectedFeeRemarkNew;
	}


	public void setSelectedFeeRemarkNew(String selectedFeeRemarkNew) {
		this.selectedFeeRemarkNew = selectedFeeRemarkNew;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}


	

	

}
