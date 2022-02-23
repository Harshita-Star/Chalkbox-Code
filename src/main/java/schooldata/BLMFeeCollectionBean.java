package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;

@ManagedBean(name="blmFeeCollection")
@ViewScoped

public class BLMFeeCollectionBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name, fathersName, className, feeModuleType, sectionName, preSession, dobString;
	String firstMonthTransportPeriod, lastMonthTransportPeriod, feeType, gender, bankName, chequeNumber, remark = "",
			challanNo, neftNo, paymentMode = "Cash", lastMonthTransportPeriodTemp, addmissionNumber,submittedBankName;
	int amount, mn, totalAmount, admissionDiscount, examinationDiscount, tutionDiscount, annualDiscount,
	transportDiscount, termDiscount, artDiscount, activityDiscount;
	StudentInfo sList;
	ArrayList<StudentInfo> serialno;
	ArrayList<FeeStatus> transportfeeStatus;
	Date dob, challanDate, neftDate;
	int flag, transportFeeLeft;
	ArrayList<FeeInfo> classFeeList, selectedFees = new ArrayList<>();
	boolean showPaymentMode, showFeeList, showChallan, showNeft, showCheque,showDisabled,showTransfer;
	Date recipietDate = new Date(), dueDate = new Date();
	ArrayList<FeeInfo> feeStructureList = new ArrayList<>();
	String studentStatus = "", srNo;
	boolean showtwsb;
	String billtype = "Ist Bill";
	String message = "";
	String otp, otpInput,selectedMonth,selectedYear;
	String discoutnNo;
	double totalamount = 0.0;
	String schoolid = "";
	ArrayList<FeeInfo> discountFeeList = new ArrayList<>();
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	ArrayList<SelectItem> yearList,bankList,installmentList;
	ArrayList<SelectItem> monthList;
	ArrayList<FeeInfo> list = new ArrayList<>();

	ArrayList<String> selectedInstallment = new ArrayList<>();

	String receipetNo="";

	SchoolInfoList fullSchInfo = new SchoolInfoList();
	/*
	 * public void calculateTotal() {
	 *
	 * }
	 */

	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
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
				////(ff.getFeeName());
				if (k == ff.getSno())
				{
					if (ff.getFeeName().equalsIgnoreCase("Late Fee"))
					{
						// ff.setDueamount(String.valueOf(ff.getPayAmount()+ff.getPayDiscount()));
					}
					else
					{
						if (ff.getDueamount().equals("0"))
						{
							ff.setPayAmount(0);
							ff.setPayDiscount(0);
						}
						else
						{
							int due = Integer.valueOf(ff.getDueamount());
							int disc = ff.getPayDiscount();

							ff.setPayAmount(due - disc);
						}
					}

				}

				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}

		}
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

	public void checkInstallment()
	{
		/*for(int i=0;i<installmentList.size();i++)
		{
			if(!(i==0) && !(i==(installmentList.size()-1)))
			{
				if(selectedInstallment.contains(installmentList.get(i).getValue()))
				{

				}
			}
			else
			{

			}
		}*/

		for(int i=0;i<selectedInstallment.size();i++)
		{
			if(i>0)
			{


				//int index1 = installmentList.in
			}
		}
	}

	public BLMFeeCollectionBean() {

		installment();

		Connection conn=DataBaseConnection.javaConnection();

		Date dt = new Date();

		allMonths();
		allYear();

		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
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
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) ss.getAttribute("feesubmit");
		sList = (StudentInfo) ss.getAttribute("selectedStudent");
		addmissionNumber = sList.getAddNumber();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		schoolid = DBM.schoolId();

		Runnable r = new Runnable()
		{
			public void run()
			{
				duehandler(selectedMonth, addmissionNumber, schoolid);
			}
		};
		new Thread(r).start();*/

		////(new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

		generateFeeCollectionTables(dueDate);
	}

	public void findDueFees() {

		if(!selectedMonth.equals(""))
		{
			if (dueDate == null) {
				dueDate = new Date();
			}

			submitAmount = 0;
			discountAmount = 0;
			selectedFees = new ArrayList<>();

			//selectedMonth = selectedInstallment.get(selectedInstallment.size()-1);

			/*Date dt = new Date();
			selectedMonth = String.valueOf(dt.getMonth() + 1);
			selectedYear = String.valueOf(dt.getYear() + 1900);

			allMonths();
			allYear();*/

			String strDate = "01/"+selectedMonth+"/"+selectedYear;

			try {
				dueDate= new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			////(new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

			generateFeeCollectionTables(dueDate);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please first select installment(s) to pay!"));
		}



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

	public void generateFeeCollectionTables(Date findDate) {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		schoolid = DBM.schoolId();
		SchoolInfoList schoolInfo = DBM.fullSchoolInfo(conn);
		fullSchInfo = schoolInfo;

		if (schoolid.equals("206")) {
			billtype = "Ist Bill";
			showtwsb = true;
		} else {
			billtype = "";
			showtwsb = false;
		}


		////(schoolInfo.getBranch_id());
		if(schoolInfo.getBranch_id().equalsIgnoreCase("54"))
		{
			showDisabled=false;
		}
		else
		{
			showDisabled=true;
		}


		//(showDisabled);
		preSession = DatabaseMethods1.selectedSessionDetails(schoolid,conn);

		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		message = (String) ss.getAttribute("feesubmit");
		sList = (StudentInfo) ss.getAttribute("selectedStudent");
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
		////(connsessioncategory);
		ss.setAttribute("admisionnumber", addmissionNumber);

		Runnable r = new Runnable()
		{
			public void run()
			{
				duehandler(selectedMonth, addmissionNumber, schoolid);
			}
		};
		new Thread(r).start();

		discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);
		ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReport(addmissionNumber, findDate,
				preSession, conn, "feeCollection", "active");

		HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

		classFeeList = DBM.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
				conn);
		classFeeList = DBM.addPreviousFee(classFeeList, addmissionNumber, conn);
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
			int i = DBM.totalStudentExpense(DBM.schoolId(),addmissionNumber, conn);
			classFeeList.get(classFeeList.size() - 1).setFeeAmount(i);

		}



		// ClassInfo list=new ClassInfo();
		// list=classFeeList.get(classFeeList.size()-1);
		//
		// //("session - "+schoolInfo.getSchoolSession());
		DateFormat df = new SimpleDateFormat("MM");
		String reportDate = df.format(sList.getStartingDate());
		// //("reportDate -
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
					int feepaidAmount = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, rr.getFeeId(), conn);
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
					totalpaidamount = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, rr.getFeeId(), conn);
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
					////("monthly....." + monthly + ".....startmonth....." + startingMonth);

					// //("hellooooo........."+monthly);

					if (schoolInfo.getSchoolSession().equals("4-3")) {
						for (int i = monthly; i <= 15; i++) {
							int month = i;
							if (i > 12)
								month = i - 12;

							int fee = rr.getFeeAmount();

							if (fee <= totalpaidamount) {
								FeeInfo info = new FeeInfo();
								String month1 = DBM.monthNameByNumber(month);
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
								info.setFeePeriod(DBM.monthNameByNumber(month));

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
								String month1 = DBM.monthNameByNumber(month);
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
								info.setFeePeriod(DBM.monthNameByNumber(month));

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
							// //("sds : "+startingMonth);
						} else if (tempStartingMonth >= 2 && tempStartingMonth <= 4) {
							startmont = 4;
						}

					}

					int totalpaidamount = 0;
					totalpaidamount = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, rr.getFeeId(), conn);

					if (schoolInfo.getSchoolSession().equals("4-3")) {
						for (int i = startmont; i <= 4; i++) {
							String month = "";

							if (i == 1) {
								month = DBM.monthNameByNumber(4) + "-" + DBM.monthNameByNumber(6);
							} else if (i == 2) {

								month = DBM.monthNameByNumber(7) + "-" + DBM.monthNameByNumber(9);
							} else if (i == 3) {
								month = DBM.monthNameByNumber(10) + "-" + DBM.monthNameByNumber(12);
							} else if (i == 4) {
								month = DBM.monthNameByNumber(1) + "-" + DBM.monthNameByNumber(3);
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
						////("xx : " + startmont);
						for (int i = startmont; i <= 4; i++) {
							String month = "";

							if (i == 1) {
								month = DBM.monthNameByNumber(5) + "-" + DBM.monthNameByNumber(7);
							} else if (i == 2) {

								month = DBM.monthNameByNumber(8) + "-" + DBM.monthNameByNumber(10);
							} else if (i == 3) {
								month = DBM.monthNameByNumber(11) + "-" + DBM.monthNameByNumber(1);
							} else if (i == 4) {
								month = DBM.monthNameByNumber(2) + "-" + DBM.monthNameByNumber(4);
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
				int feepaidAmount = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, rr.getFeeId(), conn);
				//int feepaidAmount = DBM.previousFeePaidRecord(sList.getAddNumber(), conn);
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

		int transportFeePaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, "0", conn); // already paid student transport fee

		transportfeeStatus = new ArrayList<>();
		ArrayList<Transport> transportFeeDetails = DBM.transportListDetails(DBM.schoolId(),sList.getAddNumber(), preSession, conn);

		for (Transport t : transportFeeDetails) {
			FeeStatus fs = new FeeStatus();
			fs.setTransportRouteName(DBM.transportRouteNameFromId(DBM.schoolId(),String.valueOf(t.getRouteId()), preSession, conn));
			fs.setMonth(t.getMonth());
			fs.setStatus(t.getStatus());

			ArrayList<ClassInfo> transportFeeList = DBM.transportRouteDetailsWithFee(DBM.schoolId(),t.getRouteId(), preSession, conn);

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
		}
		int lateFee = lateFeeCalculation();
		int xy = 1;
		for(FeeInfo fi : classFeeList)
		{
			if(fi.getFeeId().equals("-2") && lateFee > 0)
			{
				fi.setDueamount(String.valueOf(lateFee));
				fi.setPayAmount(lateFee);

			}
			fi.setSno(xy++);
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

	public void checkSiblingFee() {
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("siblingStudent", sList);
		PrimeFaces.current().executeInitScript("window.open('siblingFeeCollection.xhtml')");
	}

	public String submit() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(conn);

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss1.getAttribute("schoolid");


		Date lockDate = DBM.checkLockDate(schid,conn);

		submitAmount = 0;
		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : classFeeList)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}

		if(lockDate == null)
		{

		}
		else
		{
			String strLDT = new SimpleDateFormat("yyyy-MM-dd").format(lockDate);
			String strRDT = new SimpleDateFormat("yyyy-MM-dd").format(recipietDate);
			if(recipietDate.before(lockDate) || strLDT.equals(strRDT))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("You can not receive fees in selected date, as the fees is locked upto : "+strLDT));
				return "";
			}
		}

		if(selectedFees.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please select At Least One Fee Type"));
			return "";
		}
		else
		{
			for (FeeInfo ff : selectedFees) {
				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}
		}

		try {
			if (info.getDiscountotp().equalsIgnoreCase("yes") && discountAmount > 0) {

				int randomPIN = (int) (Math.random() * 9000) + 1000;
				otp = String.valueOf(randomPIN);
				discoutnNo = info.getDiscountOtpNo();
				String typemessage = "Hello Sir,\nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
						+ randomPIN + ") to allow for giving discount.Treat this as confidential.Thank You.\n"
						+ info.getSchoolName();
				DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,DBM.schoolId(),"");
				PrimeFaces.current().ajax().update("otpdialog");
				PrimeFaces.current().executeInitScript("PF('otp').show()");

				return "";
			} else {
				int amoutnt = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;
				for (FeeInfo ff : selectedFees) {

					int totalfee = 0;
					int totalFeepaid = 0;
					if (ff.getFeeName().equals("Transport")) {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, "0", conn);
						for (FeeStatus nn : transportfeeStatus) {
							totalfee += Integer.parseInt(nn.getTransportFee());
						}
					}
					if (ff.getFeeName().equals("Previous Fee")) {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
						//totalFeepaid = DBM.previousFeePaidRecord(sList.getAddNumber(), conn);
						for (FeeInfo ss : feeStructureList) {
							if (ss.getFeeName().equals(ff.getFeeName())) {
								totalfee += ss.getTotalFeeAmount();
							}
						}
					} else {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
						for (FeeInfo ss : feeStructureList) {
							if (ss.getFeeName().equals(ff.getFeeName())) {
								totalfee += ss.getTotalFeeAmount();
							}
						}
					}

					if (!ff.getFeeName().equals("Late Fee") && !ff.getFeeName().equals("Any Other Charges")) {
						if (ff.getPayAmount() + ff.getPayDiscount() > totalfee - totalFeepaid) {
							flag1 = true;
							balLeft = totalfee - totalFeepaid;
							break;
						} else {
							flag1 = false;
						}

					}

					i = i + 1;
				}

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
						int number = DBM.feeserailno(DBM.schoolId(),conn);
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
						remark=findRemark();
						//("remark : "+remark);
						for (FeeInfo ff : selectedFees) {
							if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
								ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
							}

							/*ii = DBM.submitFee(sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
									chequeNumber, num, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
									challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",submittedBankName);*/
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
						}

						if (ii >= 1) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Fees Added Successfully"));

							info.getSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/

							if (message.equals("true")) {
								/*DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
										sList.getAddNumber(), conn);*/
							}

							/*DBM.notification("Fees", typeMessage,
									String.valueOf(sList.getFathersPhone()) + "-" + DBM.schoolId(), conn);
							DBM.notification("Fees", typeMessage,
									String.valueOf(sList.getMothersPhone()) + "-" + DBM.schoolId(), conn);*/

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
							rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

							if (!schoolid.equals("206")) {
								//PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

							}

							return "blmFeeCollection";
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




	public String submitWithNumber() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(conn);
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss1.getAttribute("schoolid");

		Date lockDate = DBM.checkLockDate(schid,conn);

		submitAmount = 0;
		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : classFeeList)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}

		if(lockDate == null)
		{

		}
		else
		{
			String strLDT = new SimpleDateFormat("yyyy-MM-dd").format(lockDate);
			String strRDT = new SimpleDateFormat("yyyy-MM-dd").format(recipietDate);
			if(recipietDate.before(lockDate) || strLDT.equals(strRDT))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("You can not receive fees in selected date, as the fees is locked upto : "+strLDT));
				return "";
			}
		}

		if(selectedFees.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please select At Least One Fee Type"));
			return "";
		}
		else
		{
			for (FeeInfo ff : selectedFees) {
				submitAmount += ff.getPayAmount();
				discountAmount += ff.getPayDiscount();
			}
		}

		try {
			if (info.getDiscountotp().equalsIgnoreCase("yes") && discountAmount > 0) {

				int randomPIN = (int) (Math.random() * 9000) + 1000;
				otp = String.valueOf(randomPIN);
				discoutnNo = info.getDiscountOtpNo();
				String typemessage = "Hello Sir,\nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
						+ randomPIN + ") to allow for giving discount.Treat this as confidential.Thank You.\n"
						+ info.getSchoolName();
				DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,DBM.schoolId(),"");
				PrimeFaces.current().ajax().update("otpdialog");
				PrimeFaces.current().executeInitScript("PF('otp').show()");

				return "";
			} else {
				int amoutnt = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;
				for (FeeInfo ff : selectedFees) {

					int totalfee = 0;
					int totalFeepaid = 0;
					if (ff.getFeeName().equals("Transport")) {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, "0", conn);
						for (FeeStatus nn : transportfeeStatus) {
							totalfee += Integer.parseInt(nn.getTransportFee());
						}
					}
					if (ff.getFeeName().equals("Previous Fee")) {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
						//totalFeepaid = DBM.previousFeePaidRecord(sList.getAddNumber(), conn);
						for (FeeInfo ss : feeStructureList) {
							if (ss.getFeeName().equals(ff.getFeeName())) {
								totalfee += ss.getTotalFeeAmount();
							}
						}
					} else {
						totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
						for (FeeInfo ss : feeStructureList) {
							if (ss.getFeeName().equals(ff.getFeeName())) {
								totalfee += ss.getTotalFeeAmount();
							}
						}
					}

					if (!ff.getFeeName().equals("Late Fee") && !ff.getFeeName().equals("Any Other Charges")) {
						if (ff.getPayAmount() + ff.getPayDiscount() > totalfee - totalFeepaid) {
							flag1 = true;
							balLeft = totalfee - totalFeepaid;
							break;
						} else {
							flag1 = false;
						}

					}

					i = i + 1;
				}

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
						boolean number = DBM.feeCheckserailno(conn, receipetNo);
						/*String num = "";
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
						if(number==false)
						{
							int ii = 0;
							for (FeeInfo ff : selectedFees) {
								if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
									ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
								}

								////(recipietDate);
								ii = DBM.submitFeeSchid(DBM.schoolId(),sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
										chequeNumber, receipetNo, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
										challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",submittedBankName);
								/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
   								DBM.updatePaidAmountOfPreviousFee(sList.getAddNumber(),
   										(ff.getPayAmount() + ff.getPayDiscount()), conn);
   							}*/
								amoutnt += ff.getPayAmount();
							}

							if (ii >= 1) {
								FacesContext.getCurrentInstance().addMessage(null,
										new FacesMessage("Fees Added Successfully"));

								String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
										+ " in favour of fee by " + paymentMode + " via Receipt No. " + receipetNo
										+ ". Cheque payments are subjected to clearance. \n\nRegards, \n"
										+ info.getSchoolName();

								/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/

								if (message.equals("true")) {
									DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
											sList.getAddNumber(), conn,DBM.schoolId(),"");
								}

								DBM.notification(DBM.schoolId(),"Fees", typeMessage,
										String.valueOf(sList.getFathersPhone()) + "-" + DBM.schoolId(), conn);
								DBM.notification(DBM.schoolId(),"Fees", typeMessage,
										String.valueOf(sList.getMothersPhone()) + "-" + DBM.schoolId(), conn);

								HttpSession rr = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
										.getSession(false);
								rr.setAttribute("type1", "origanal");
								rr.setAttribute("paymentmode", paymentMode);
								rr.setAttribute("bankname", bankName);
								rr.setAttribute("chequeno", chequeNumber);
								rr.setAttribute("receiptNumber", receipetNo);
								rr.setAttribute("selectedFee", selectedFees);
								rr.setAttribute("receiptDate", recipietDate);
								rr.setAttribute("chaqueDate", challanDate);
								rr.setAttribute("remark", remark);
								rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

								if (!schoolid.equals("206")) {
									PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

								}

								return "studentFeeCollection";
							} else {
								FacesContext.getCurrentInstance().addMessage(null,
										new FacesMessage("Please select At Least One Fee Type"));
							}

						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Recipiet No Duplicate"));
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


	public String findRemark()
	{
		int month = Integer.parseInt(selectedMonth);
		if(month==1)
		{
			month=13;
		}
		else if(month==2)
		{
			month=14;
		}
		else if(month==3)
		{
			month=15;
		}
		String rem = "";
		//("Size : "+list.size());
		for(FeeInfo ff : list)
		{
			if(month<=ff.getFeeMonthInt())
			{
				if(rem.equals(""))
				{
					rem = ff.getFeeMonth();
				}
				else
				{
					rem = rem + "," + ff.getFeeMonth();
				}
			}
		}

		return rem;
	}


	public String discountSave() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		/*selectedFees = new ArrayList<>();

		for(FeeInfo ff : classFeeList)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
			}
		}*/

		if(selectedFees.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Please select At Least One Fee Type"));
			return "";
		}
		try {
			if (!otp.equals(otpInput)) {
				/*
				 * SchoolInfoList info =DBM.fullSchoolInfo(conn);
				 *
				 * int randomPIN = (int)(Math.random()*9000)+1000;
				 * otp=String.valueOf(randomPIN); discoutnNo=info.getDiscountOtpNo(); String
				 * typemessage="Hello Sir, \nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
				 * +randomPIN+") to allow for discount.Treat this as confidential.Thank You.  \n"
				 * +info.getSchoolName(); new
				 * DatabaseMethods1().messageurlStaff(info.getDiscountOtpNo(),
				 * typemessage,"admin",conn);
				 * PrimeFaces.current().ajax().update("otpdialog");
				 * PrimeFaces.current().executeInitScript("PF('otp').show()");
				 */
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("OTP Not Matched"));

				return "";
			} else {
				int amoutnt = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;
				for (FeeInfo ff : selectedFees) {

					if (ff.getPayAmount() > 0 || ff.getPayDiscount() > 0)
					{
						int totalfee = 0;
						int totalFeepaid = 0;
						if (ff.getFeeName().equals("Transport"))
						{
							totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, "0", conn);
							for (FeeStatus nn : transportfeeStatus)
							{
								totalfee += Integer.parseInt(nn.getTransportFee());
							}
						}
						if (ff.getFeeName().equals("Previous Fee"))
						{
							totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
							//totalFeepaid = DBM.previousFeePaidRecord(sList.getAddNumber(), conn);
							for (FeeInfo ss : feeStructureList) {
								if (ss.getFeeName().equals(ff.getFeeName()))
								{
									totalfee += ss.getTotalFeeAmount();
								}
							}
						}
						else
						{
							totalFeepaid = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
							for (FeeInfo ss : feeStructureList)
							{
								if (ss.getFeeName().equals(ff.getFeeName()))
								{
									totalfee += ss.getTotalFeeAmount();
								}
							}
						}

						if (!ff.getFeeName().equals("Late Fee") && !ff.getFeeName().equals("Any Other Charges"))
						{
							if (ff.getPayAmount() + ff.getPayDiscount() > totalfee - totalFeepaid)
							{
								flag1 = true;
								balLeft = totalfee - totalFeepaid;
								break;
							}
							else
							{
								flag1 = false;
							}

						}
					}
					else
					{
						flag = true;

						break;
					}
					i = i + 1;
				}

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
						int number = DBM.feeserailno(DBM.schoolId(),conn);
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

							//(recipietDate);
							ii = DBM.submitFeeSchid(DBM.schoolId(),sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
									chequeNumber, num, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
									challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",submittedBankName);
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
						}

						if (ii >= 1) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Fees Added Successfully"));

							SchoolInfoList info = DBM.fullSchoolInfo(conn);
							String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by " + paymentMode + " via Receipt No. " + num
									+ ". Cheque payments are subjected to clearance. \n\nRegards, \n"
									+ info.getSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/

							if (message.equals("true")) {
								DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
										sList.getAddNumber(), conn,DBM.schoolId(),"");
							}

							DBM.notification(DBM.schoolId(),"Fees", typeMessage,
									String.valueOf(sList.getFathersPhone()) + "-" + DBM.schoolId(), conn);
							DBM.notification(DBM.schoolId(),"Fees", typeMessage,
									String.valueOf(sList.getMothersPhone()) + "-" + DBM.schoolId(), conn);

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
							rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

							if (!schoolid.equals("206")) {
								PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

							}
							PrimeFaces.current().ajax().update("otpdialog");

							PrimeFaces.current().ajax().update("form");

							PrimeFaces.current().executeInitScript("PF('otp').hide()");

							return "studentFeeCollection";
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
		if (paymentMode.equals("Cheque"))
		{
			showPaymentMode = true;
			showCheque = true;
			showChallan = false;
			showNeft = false;
			showTransfer=false;
			if (fullSchInfo.getType().equalsIgnoreCase("foster") || fullSchInfo.getType().equalsIgnoreCase("fosterCBSE"))
			{
				showTransfer=true;
			}

		}
		else if (paymentMode.equals("Challan"))
		{
			showPaymentMode = showChallan = true;
			showNeft = showCheque = false;
			showTransfer=false;
			if (fullSchInfo.getType().equalsIgnoreCase("foster") || fullSchInfo.getType().equalsIgnoreCase("fosterCBSE"))
			{
				showTransfer=true;
			}
		}
		else if (paymentMode.equals("Net Banking"))
		{
			showPaymentMode = showNeft = true;
			showChallan = showCheque = false;
			showTransfer=false;
			if (fullSchInfo.getType().equalsIgnoreCase("foster") || fullSchInfo.getType().equalsIgnoreCase("fosterCBSE"))
			{
				showTransfer=true;
			}
		}
		else
		{
			showTransfer = showPaymentMode = showChallan = showNeft = showCheque = false;
		}
	}


	public void duehandler(String month,String admNo, String schid)
	{

		int smonth=0;
		if(Integer.parseInt(month)<4)
		{
			smonth=Integer.parseInt(month)+12;
		}
		else
		{
			smonth=Integer.parseInt(month);
		}
		//("thread start : "+month+"...."+admNo+"...."+schid);
		//HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		list = new ArrayList<>();
		String[] feemonths= {"4","6","8","9","11","12","13","15"};
		String[] allfeemonths= {"April","May-June","Jul-Aug","September","Oct-Nov","December","January","Feb-Mar"};
		String addmissionNumber=admNo;
		String schoolId=schid;
		String preSession=DatabaseMethods1.selectedSessionDetails(schoolId,conn);
		StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolId, conn);
		String studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		ArrayList<FeeInfo> classFeeList;
		//JSONArray arr=new JSONArray();
		String[] session=preSession.split("-");
		double previousDueAmount=0;
		for(int i=0;i<feemonths.length;i++)
		{

			if(Integer.parseInt(feemonths[i])<=smonth)
			{
				String findDate1="";
				if(Integer.parseInt(feemonths[i])<13)
				{
					findDate1="01/"+feemonths[i]+"/"+session[0];
				}
				else
				{
					findDate1="01/"+feemonths[i]+"/"+session[1];
				}

				Date findDate=null;
				//Date cDate=null;
				try {
					findDate= new SimpleDateFormat("dd/MM/yyyy").parse(findDate1);
					//cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReport(schoolId,addmissionNumber, findDate,
						preSession, conn, "feeCollection");

				HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

				classFeeList = DBM.classFeesForStudent(schoolId,sList.getClassId(), preSession, studentStatus, connsessioncategory,
						conn);
				classFeeList = DBM.addPreviousFee(schoolId,classFeeList, addmissionNumber, conn);
				double dueAmount = 0,actuladueAmount=0;
				for (FeeInfo ls : classFeeList) {
					if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {

						dueAmount += Integer.parseInt(map.get(ls.getFeeName()));

					}


				}
				actuladueAmount=dueAmount-previousDueAmount;
				previousDueAmount =dueAmount;
				//JSONObject obj = new JSONObject();
				FeeInfo ff = new FeeInfo();
				ff.setAmount(actuladueAmount);
				ff.setFeeMonth(allfeemonths[i]);
				ff.setFeeMonthInt(Integer.parseInt(feemonths[i]));
				if(ff.getFeeMonthInt()==1)
				{
					ff.setFeeMonthInt(13);
				}
				else if(ff.getFeeMonthInt()==2)
				{
					ff.setFeeMonthInt(14);
				}
				else if(ff.getFeeMonthInt()==3)
				{
					ff.setFeeMonthInt(15);
				}
				//			 obj.put("amount",String.valueOf(actuladueAmount));
				//			 obj.put("Fee_month",allfeemonths[i]);




				ff.setTotalFee(ff.getAmount()+ff.getLateFee());
				//obj.put("latefee", latefee);
				//arr.add(obj);
				list.add(ff);

			}
		}

		//("thread size - "+list.size());
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int lateFeeCalculation()
	{

		Connection conn= DataBaseConnection.javaConnection();
		DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		list = new ArrayList<>();
		int month = 0;
		if(Integer.parseInt(selectedMonth) < 4)
		{
			month = Integer.parseInt(selectedMonth)+12;
		}
		else
		{
			month = Integer.parseInt(selectedMonth);
		}
		String[] feemonths= {"4","6","8","9","11","12","1","3"};
		String addmissionNumber=sList.getAddNumber();
		//String schoolId=sList.getSchid();
		String date=new SimpleDateFormat("dd/MM/yyyy").format(recipietDate);
		String preSession=DatabaseMethods1.selectedSessionDetails(schoolid, conn);
		StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolid, conn);
		String studentStatus = sList.getStudentStatus();
		String connsessioncategory = sList.getConcession();
		ArrayList<FeeInfo> classFeeList;
		//JSONArray arr=new JSONArray();
		String[] session=preSession.split("-");
		double previousDueAmount=0; int totalLateFee=0;
		for(int i=0;i<feemonths.length;i++)
		{
			String findDate1="";
			if(Integer.parseInt(feemonths[i])>3)
			{
				findDate1="01/"+feemonths[i]+"/"+session[0];
			}
			else
			{
				findDate1="01/"+feemonths[i]+"/"+session[1];
			}

			Date findDate=null;
			Date cDate=null;
			try {
				findDate= new SimpleDateFormat("dd/MM/yyyy").parse(findDate1);
				cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReport(schoolid,addmissionNumber, findDate,
					preSession, conn, "feeCollection");

			HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

			classFeeList = DBM.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
					conn);
			classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
			double dueAmount = 0,actuladueAmount=0;
			for (FeeInfo ls : classFeeList) {
				if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {

					dueAmount += Integer.parseInt(map.get(ls.getFeeName()));

				}


			}
			actuladueAmount=dueAmount-previousDueAmount;
			previousDueAmount =dueAmount;
			//JSONObject obj = new JSONObject();
			//FeeInfo ff = new FeeInfo();
			//ff.setAmount(actuladueAmount);
			//ff.setFeeMonth(allfeemonths[i]);
			//		 obj.put("amount",String.valueOf(actuladueAmount));
			//		 obj.put("Fee_month",allfeemonths[i]);

			int latefee=0;

			if(actuladueAmount>0)
			{

				if(i==0)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("30/04/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(cDate) && month>=4)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==1)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/05/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/05/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=5)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1)  && month>=5)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==2)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/07/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/07/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=7)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=7)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==3)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/09/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/09/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=9)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=9)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==4)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/10/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/10/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=10)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=10)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==5)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/12/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/12/"+session[0]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=12)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=12)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==6)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/01/"+session[1]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/01/"+session[1]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=13)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=13)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}
				else if(i==7)
				{
					try {
						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/02/"+session[1]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("15/02/"+session[1]);
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));

						if(duedate.before(cDate)&&!cDate.after(duedate1) && month>=14)
						{
							latefee=100;
						}
						else if(cDate.after(duedate1) && month>=14)
						{
							latefee=200;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
				}

				totalLateFee += latefee;

				//ff.setLateFee(latefee);
				/*ff.setTotalFee(ff.getAmount()+ff.getLateFee());
			   //obj.put("latefee", latefee);
			   //arr.add(obj);
			   list.add(ff);*/
			}


		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalLateFee;
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

	public String getSubmittedBankName() {
		return submittedBankName;
	}

	public void setSubmittedBankName(String submittedBankName) {
		this.submittedBankName = submittedBankName;
	}

	public ArrayList<SelectItem> getBankList() {
		return bankList;
	}

	public void setBankList(ArrayList<SelectItem> bankList) {
		this.bankList = bankList;
	}

	public boolean isShowTransfer() {
		return showTransfer;
	}

	public void setShowTransfer(boolean showTransfer) {
		this.showTransfer = showTransfer;
	}

	public String getReceipetNo() {
		return receipetNo;
	}

	public void setReceipetNo(String receipetNo) {
		this.receipetNo = receipetNo;
	}

	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public ArrayList<String> getSelectedInstallment() {
		return selectedInstallment;
	}

	public void setSelectedInstallment(ArrayList<String> selectedInstallment) {
		this.selectedInstallment = selectedInstallment;
	}
}
