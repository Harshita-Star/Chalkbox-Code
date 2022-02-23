package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import account_module.Method;
import account_module.ValueInfo;
import session_work.QueryConstants;

@ManagedBean(name = "studentFee")
@ViewScoped
public class StudentFeeCollectionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String name, fathersName, className, feeModuleType, sectionName, preSession, dobString,userType,balMsg;
	String firstMonthTransportPeriod, lastMonthTransportPeriod, feeType, gender, bankName, chequeNumber, remark = "",
			challanNo, neftNo, paymentMode = "Cash", lastMonthTransportPeriodTemp, addmissionNumber,submittedBankName;
	int amount, mn, totalAmount, admissionDiscount, examinationDiscount, tutionDiscount, annualDiscount,
	transportDiscount, termDiscount, artDiscount, activityDiscount,msgAmt;
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
	double totalamount = 0.0,smsLimit;
	String schoolid = "",session;
	ArrayList<FeeInfo> discountFeeList = new ArrayList<>();
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	ArrayList<SelectItem> yearList,bankList;
	ArrayList<SelectItem> monthList;
	boolean waveOffLateFee=false;
	Date changeDate;
	String receipetNo="";

	SchoolInfoList fullSchInfo = new SchoolInfoList();
	DailyFeeCollectionBean selectedstudent;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	ArrayList<FeeInfo>feelist;
	DataBaseMethodStudent objStd=new DataBaseMethodStudent();
	/*
	 * public void calculateTotal() {
	 *
	 * }
	 */
	
	Date maxDate;

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

	public StudentFeeCollectionBean() {
		Connection conn=DataBaseConnection.javaConnection();
        Date dt = new Date();


		allMonths();
		allYear();
		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);
		bankList=new DataBaseMethodsIncome_Expense().allBankList(conn);
		selectedMonth = String.valueOf(dt.getMonth() + 1);
		selectedYear = String.valueOf(dt.getYear() + 1900);

        schoolid=new DatabaseMethods1().schoolId();
        session=DatabaseMethods1.selectedSessionDetails(schoolid, conn);
		maxDate=new Date();
		
		String strDate = "01/"+selectedMonth+"/"+selectedYear;

		try {
			dueDate= new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		////(new SimpleDateFormat("dd/MM/yyyy").format(dueDate));

		generateFeeCollectionTables(dueDate);
		searchReceipts();
	}

	public void findDueFees() {
		if (dueDate == null) {
			dueDate = new Date();
		}

		submitAmount = 0;
		discountAmount = 0;
		selectedFees = new ArrayList<>();

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
		userType = (String) ss.getAttribute("type");
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
		ss.setAttribute("admisionnumber", addmissionNumber);
	
		discountFeeList = DBM.feeDiscountForSelectedStudent(DBM.schoolId(),addmissionNumber, conn);
		ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReport(addmissionNumber, findDate,
				preSession, conn, "feeCollection", "active");
	
		HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();
	
		classFeeList = DBM.classFeesForStudent(DBM.schoolId(), sList.getClassId(), preSession, studentStatus, connsessioncategory,
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
			if (!rr.getFeeName().equals("Transport") && !rr.getFeeName().equals("Previous Fee")&& !rr.getFeeName().equals("Late Fee")) {
				if (rr.getFeeType().equals("year")) {
	
					//(rr.getFeeName());
					int fee = 0;
					
					
					if(rr.getFeeCheckType().equals("studentWise"))
					{
						fee=DBM.viewStudentWiseFee(sList.getAddNumber(),rr.getFeeId(),rr.getFeeMonth(),DBM.schoolId(),conn);
					}
					else
					{
						fee = rr.getFeeAmount();
					}
					//	int fee = rr.getFeeAmount();
					int feepaidAmount = DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, rr.getFeeId(), conn);
					int leftamount = fee - feepaidAmount;
					FeeInfo info = new FeeInfo();
					info.setFeeName(rr.getFeeName());
					info.setFeePeriod("-");
					if(rr.getFeeMonth().equalsIgnoreCase("every"))
					{
						info.setFeeMonth("4");
					}
					else
					{
						info.setFeeMonth(rr.getFeeMonth());
					}
					info.setTotalFeeAmount(fee);
					info.setTotalFeePaidAmount(feepaidAmount);
					info.setTotalFeeLeftAmount(leftamount);
					if(fee>0)
					{
						feeStructureList.add(info);
							
					}
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
	
							int fee = 0;
							if(rr.getFeeCheckType().equals("studentWise"))
							{
								fee=DBM.viewStudentWiseFee(sList.getAddNumber(),rr.getFeeId(),String.valueOf(i),DBM.schoolId(),conn);
							}
							else
							{
								fee = rr.getFeeAmount();
							}
	
	
							if (fee <= totalpaidamount) {
								FeeInfo info = new FeeInfo();
								String month1 = DBM.monthNameByNumber(month);
								info.setFeePeriod(month1);
								info.setFeeMonth(rr.getFeeMonth());
								info.setFeeMonth(String.valueOf(month));
								
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
								totalpaidamount = totalpaidamount - fee;
							} else {
	
								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(DBM.monthNameByNumber(month));
								info.setFeeMonth(rr.getFeeMonth());
								info.setFeeMonth(String.valueOf(month));
								
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
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
								info.setFeeMonth(String.valueOf(month));
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
								totalpaidamount = totalpaidamount - fee;
							} else {
								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(DBM.monthNameByNumber(month));
								info.setFeeMonth(String.valueOf(month));
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
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
							String mn="";
	
							if (i == 1) {
								month = DBM.monthNameByNumber(4) + "-" + DBM.monthNameByNumber(6);
								mn="4";
							} else if (i == 2) {
	
								month = DBM.monthNameByNumber(7) + "-" + DBM.monthNameByNumber(9);
								mn="7";
							} else if (i == 3) {
								month = DBM.monthNameByNumber(10) + "-" + DBM.monthNameByNumber(12);
								mn="10";
							} else if (i == 4) {
								month = DBM.monthNameByNumber(1) + "-" + DBM.monthNameByNumber(3);
								mn="1";
							}
	
							int fee = 0;
							if(rr.getFeeCheckType().equals("studentWise"))
							{
								fee=DBM.viewStudentWiseFee(sList.getAddNumber(),rr.getFeeId(),String.valueOf(i),DBM.schoolId(),conn);
	
							}
							else
							{
								fee = rr.getFeeAmount();
							}
	
	
	
							//int fee = rr.getFeeAmount();
	
							if (fee <= totalpaidamount) {
	
								FeeInfo info = new FeeInfo();
	
								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setFeeMonth(String.valueOf(mn));
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
								totalpaidamount = totalpaidamount - fee;
							} else {
	
								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setFeeMonth(String.valueOf(mn));
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
								totalpaidamount = 0;
							}
						}
					} else {
						////("xx : " + startmont);
						for (int i = startmont; i <= 4; i++) {
							String month = "";
	                       String mn="";
							if (i == 1) {
								month = DBM.monthNameByNumber(5) + "-" + DBM.monthNameByNumber(7);
								mn="4";
							} else if (i == 2) {
	
								month = DBM.monthNameByNumber(8) + "-" + DBM.monthNameByNumber(10);
								mn="7";
							} else if (i == 3) {
								month = DBM.monthNameByNumber(11) + "-" + DBM.monthNameByNumber(1);
								 mn="10";
							} else if (i == 4) {
								month = DBM.monthNameByNumber(2) + "-" + DBM.monthNameByNumber(4);
								 mn="1";
							}
	
							int fee = 0;
							if(rr.getFeeCheckType().equals("studentWise"))
							{
	
								fee=DBM.viewStudentWiseFee(sList.getAddNumber(),rr.getFeeId(),String.valueOf(i),DBM.schoolId(),conn);
	
							}
							else
							{
								fee = rr.getFeeAmount();
							}
	
							if (fee <= totalpaidamount) {
	
								FeeInfo info = new FeeInfo();
	
								info.setFeePeriod(month);
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setFeeMonth(String.valueOf(mn));
								
								info.setTotalFeePaidAmount(fee);
								info.setTotalFeeLeftAmount(0);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
								totalpaidamount = totalpaidamount - fee;
							} else {
	
								int leftamount = fee - totalpaidamount;
								FeeInfo info = new FeeInfo();
								info.setFeePeriod(month);
								info.setFeeMonth(String.valueOf(mn));
								
								info.setFeeName(rr.getFeeName());
								info.setTotalFeeAmount(fee);
								info.setTotalFeePaidAmount(totalpaidamount);
								info.setTotalFeeLeftAmount(leftamount);
								if(fee>0)
								{
									feeStructureList.add(info);
										
								}
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
				info.setFeeMonth("4");
				
				info.setTotalFeeAmount(fee);
				info.setTotalFeePaidAmount(feepaidAmount);
				info.setTotalFeeLeftAmount(leftamount);
				if(fee>0)
				{
					feeStructureList.add(info);
						
				}
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
			fs.setFeeMonth(t.getFeeMonth());
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
					info.setStatus(String.valueOf(transportFeePaid));
					info.setBalanceLeft(String.valueOf(transportFee - transportFeePaid));
	
					firstMonthTransportPeriod = info.getMonth();
	
					transportFeeLeft += transportFee - transportFeePaid;
					transportFeePaid = 0;
				} else {
					if (flag == 0) {
						info.setStatus("0");
						info.setBalanceLeft(String.valueOf(transportFee));
	
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
	
	
		int lateFee=0;
		
		if(DBM.schoolId().equals("1")||DBM.schoolId().equals("295")||DBM.schoolId().equals("288"))
		{
			
			
			
			
			ArrayList<FeeInfo>ls=new ArrayList<>();
			for(int i=4;i<=15;i++)
			{
				FeeInfo lss=new FeeInfo();
				if(i>12)
				{
					lss.setFeeMonth(new DataBaseMeathodJson().monthList(i-12));
					lss.setFeeMonthInt(i-12);
				}
				else
				{
					lss.setFeeMonth(new DataBaseMeathodJson().monthList(i));
					lss.setFeeMonthInt(i);
				}
				lss.setAmount(0);
				ls.add(lss);
			}


			for(FeeInfo ss1:feeStructureList)
			{
				if(ss1.getFeeMonth().equals("4"))
				{
					double amt=ls.get(0).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(0).setAmount(amt);
					ls.get(0).setTotalFee(ls.get(0).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(0).setTotalFeePaidAmount(ls.get(0).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("5"))
				{
					double amt=ls.get(1).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(1).setAmount(amt);
					ls.get(1).setTotalFee(ls.get(1).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(1).setTotalFeePaidAmount(ls.get(1).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("6"))
				{
					double amt=ls.get(2).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(2).setAmount(amt);
					ls.get(2).setTotalFee(ls.get(2).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(2).setTotalFeePaidAmount(ls.get(2).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("7"))
				{
					double amt=ls.get(3).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(3).setAmount(amt);
					ls.get(3).setTotalFee(ls.get(3).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(3).setTotalFeePaidAmount(ls.get(3).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("8"))
				{
					double amt=ls.get(4).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(4).setAmount(amt);
					ls.get(4).setTotalFee(ls.get(4).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(4).setTotalFeePaidAmount(ls.get(4).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("9"))
				{
					double amt=ls.get(5).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(5).setAmount(amt);
					ls.get(5).setTotalFee(ls.get(5).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(5).setTotalFeePaidAmount(ls.get(5).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("10"))
				{
					double amt=ls.get(6).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(6).setAmount(amt);
					ls.get(6).setTotalFee(ls.get(6).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(6).setTotalFeePaidAmount(ls.get(6).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("11"))
				{
					double amt=ls.get(7).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(7).setAmount(amt);
					ls.get(7).setTotalFee(ls.get(7).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(7).setTotalFeePaidAmount(ls.get(7).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());
				}
				else if(ss1.getFeeMonth().equals("12"))
				{
					double amt=ls.get(8).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(8).setAmount(amt);
					ls.get(8).setTotalFee(ls.get(8).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(8).setTotalFeePaidAmount(ls.get(8).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());

				}
				else if(ss1.getFeeMonth().equals("1"))
				{
					double amt=ls.get(9).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(9).setAmount(amt);
					ls.get(9).setTotalFee(ls.get(9).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(9).setTotalFeePaidAmount(ls.get(9).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());

				}
				else if(ss1.getFeeMonth().equals("2"))
				{
					double amt=ls.get(10).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(10).setAmount(amt);
					ls.get(10).setTotalFee(ls.get(10).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(10).setTotalFeePaidAmount(ls.get(10).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());

				}
				else if(ss1.getFeeMonth().equals("3"))
				{
					double amt=ls.get(11).getAmount()+ss1.getTotalFeeLeftAmount();
					ls.get(11).setAmount(amt);
					ls.get(11).setTotalFee(ls.get(11).getTotalFee()+ss1.getTotalFeeAmount());
					ls.get(11).setTotalFeePaidAmount(ls.get(11).getTotalFeePaidAmount()+ss1.getTotalFeePaidAmount());

				}

			}

			for(FeeStatus ss1:transportfeeStatus)
			{
				if(ss1.getFeeMonth().equals("4"))
				{
					double amt=ls.get(0).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(0).setAmount(amt);
					ls.get(0).setTotalFee(ls.get(0).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(0).setTotalFeePaidAmount(ls.get(0).getTotalFeePaidAmount()+Integer.parseInt(ss1.getStatus()));


				}
				else if(ss1.getFeeMonth().equals("5"))
				{
					double amt=ls.get(1).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(1).setAmount(amt);
					ls.get(1).setTotalFee(ls.get(1).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(1).setTotalFeePaidAmount(ls.get(1).getTotalFeePaidAmount()+Integer.parseInt(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("6"))
				{
					double amt=ls.get(2).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(2).setAmount(amt);
					ls.get(2).setTotalFee(ls.get(2).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(2).setTotalFeePaidAmount(ls.get(2).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("7"))
				{
					double amt=ls.get(3).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(3).setAmount(amt);
					ls.get(3).setTotalFee(ls.get(3).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(3).setTotalFeePaidAmount(ls.get(3).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("8"))
				{
					double amt=ls.get(4).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(4).setAmount(amt);
					ls.get(4).setTotalFee(ls.get(4).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(4).setTotalFeePaidAmount(ls.get(4).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("9"))
				{
					double amt=ls.get(5).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(5).setAmount(amt);
					ls.get(5).setTotalFee(ls.get(5).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(5).setTotalFeePaidAmount(ls.get(5).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("10"))
				{
					double amt=ls.get(6).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(6).setAmount(amt);
					ls.get(6).setTotalFee(ls.get(6).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(6).setTotalFeePaidAmount(ls.get(6).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("11"))
				{
					double amt=ls.get(7).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(7).setAmount(amt);
					ls.get(7).setTotalFee(ls.get(7).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(7).setTotalFeePaidAmount(ls.get(7).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("12"))
				{
					double amt=ls.get(8).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(8).setAmount(amt);
					ls.get(8).setTotalFee(ls.get(8).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(8).setTotalFeePaidAmount(ls.get(8).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("1"))
				{
					double amt=ls.get(9).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(9).setAmount(amt);
					ls.get(9).setTotalFee(ls.get(9).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(9).setTotalFeePaidAmount(ls.get(9).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("2"))
				{
					double amt=ls.get(10).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(10).setAmount(amt);
					ls.get(10).setTotalFee(ls.get(10).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(10).setTotalFeePaidAmount(ls.get(10).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
				else if(ss1.getFeeMonth().equals("3"))
				{
					double amt=ls.get(11).getAmount()+Double.parseDouble(ss1.getBalanceLeft());
					ls.get(11).setAmount(amt);
					ls.get(11).setTotalFee(ls.get(11).getTotalFee()+Double.parseDouble(ss1.getTransportFee()));
					ls.get(11).setTotalFeePaidAmount(ls.get(11).getTotalFeePaidAmount()+Integer.valueOf(ss1.getStatus()));

				}
			}

			
			
	        
	      String[] session1=DatabaseMethods1.selectedSessionDetails(schoolid,conn).split("-");
	      
	      FeeDynamicList lateFees=new DatabaseMethods1().getlatefeecalculation(DBM.schoolId(),conn);
	      String[] latefeeamount=lateFees.getLate_fee().split(",");
			
			for(FeeInfo lst:ls)
			{
				if(lst.getFeeMonthInt()==4 && lst.getAmount()>0)
				{

					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/04/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/04/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				
				}
				else if(lst.getFeeMonthInt()==5 && lst.getAmount()>0)
				{
					
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/05/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/05/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==6 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/06/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/06/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==7 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/7/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/07/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==8 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/08/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/08/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==9 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/09/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/09/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==10 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/10/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/10/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
					
				}
				else if(lst.getFeeMonthInt()==11 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/11/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/11/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==12 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/12/"+session1[0]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/12/"+session1[0]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==1 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/01/"+session1[1]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/01/"+session1[1]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==2 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/02/"+session1[1]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/02/"+session1[1]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				else if(lst.getFeeMonthInt()==3 && lst.getAmount()>0)
				{
					try {


						Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/03/"+session1[1]);
						Date duedate1=new SimpleDateFormat("dd/MM/yyyy").parse("20/03/"+session1[1]);
						
						//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
						if(duedate.before(recipietDate)&&!recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[0]);
						}
						else if(recipietDate.after(duedate1) )
						{
							lateFee+=Integer.parseInt(latefeeamount[1]);;
						}
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
				
				
				
	          
				
			}

		}
	
		
		
		
		ArrayList<FeeInfo> tempList = new ArrayList<>();
		tempList.addAll(classFeeList);
		for (FeeInfo lsst : tempList)
		{
			if(lsst.getDueamount()==null)
			{
	
			}
			else if(lsst.getDueamount().equals("0") || lsst.getDueamount().equals("0.0"))
			{
				classFeeList.remove(lsst);
			}
		}
	
		int xy = 1;
		for(FeeInfo fi : classFeeList)
		{
	
			if(fi.getFeeId().equals("-2") && (DBM.schoolId().equals("1")||DBM.schoolId().equals("295")||DBM.schoolId().equals("288")))
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
		String	schid = (String) ss1.getAttribute("schoolid");

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
				if(String.valueOf(info.getDiscountOtpNo()).length()==10
						&& !String.valueOf(info.getDiscountOtpNo()).equals("2222222222")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("9999999999")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("1111111111")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("1234567890")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("0123456789"))
				{
					int randomPIN = (int) (Math.random() * 9000) + 1000;
					otp = String.valueOf(randomPIN);
					discoutnNo = info.getDiscountOtpNo();
					String typemessage = "Hello Sir,\nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
							+ randomPIN + ") to allow for giving discount.Treat this as confidential.Thank You.\n"
							+ info.getSmsSchoolName();
					DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,DBM.schoolId(),"");
					PrimeFaces.current().ajax().update("otpdialog");
					PrimeFaces.current().executeInitScript("PF('otp').show()");
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discout OTP No. is Invalid. Please contact administrator"));
				}


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

						totalFeepaid= DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
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
						for (FeeInfo ff : selectedFees) {
							if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
								ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
							}

							////(recipietDate);


							ii = DBM.submitFeeSchid(DBM.schoolId(),sList, ff.getPayAmount(), ff.getFeeId(), paymentMode, bankName,
									chequeNumber, num, ff.getPayDiscount(), preSession, recipietDate, challanNo, neftNo,
									challanDate, neftDate, conn, remark, dueDate, ff.getDueamount(), "current",submittedBankName);
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
						}

						
						//// // System.out.println("test"+ii);
						if (ii >= 1) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage("Fees Added Successfully"));

							if (info.getType().equalsIgnoreCase("foster") || info.getType().equalsIgnoreCase("fosterCBSE"))
							{
								ValueInfo vi = new Method().obtainHeadInfoBySeq("income,School Fees", DBM.schoolId(), DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn), conn);
								String accountId = new Method().obtainAccountId(vi.getId(),"Fee",conn);
								String dsc = "Fee Recieved - "+name+", Sr.No: "+srNo+", Receipt No:"+num;
								String dsc1 = "Fee Due - "+name+", Sr.No: "+srNo;
								new Method().insertInvoiceAndBill(accountId,"Dr",recipietDate,amoutnt,dsc1,"bill",conn);
								if(paymentMode.equalsIgnoreCase("cash"))
								{
									new Method().insertCashBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,conn);
								}
								else
								{
									new Method().insertBankBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,chequeNumber,challanDate,"",submittedBankName,conn);
								}
							}


							msgAmt = amoutnt;

							String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by " + paymentMode + " via Receipt No. " + num
									+ ".\n\nRegards,  \n"
									+ info.getSmsSchoolName();

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

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/

							
							//// // System.out.println(message);
							if (message.equals("true"))
							{
								double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
								if(balance >0 && balance <= smsLimit)
								{
									balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
											+ "We suggest you to top-up your account today to ensure uninterrupted activity";
									if (userType.equalsIgnoreCase("admin"))
									{
										PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
										PrimeFaces.current().ajax().update("MsgLimitForm");
										try {
											conn.close();
										} catch (SQLException e) {
											e.printStackTrace();
										}
										return "";
									}
									else
									{
										if(String.valueOf(sList.getFathersPhone()).length()==10
												&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
												&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
												&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
												&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
												&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
										{
											DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
													sList.getAddNumber(), conn,DBM.schoolId(),"");

										}
									}
								}
								else if(balance <= 0)
								{
									balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
									if (userType.equalsIgnoreCase("admin"))
									{
										PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
										PrimeFaces.current().ajax().update("MsgOverForm");
										return "";
									}
									else
									{
										balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

										PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
										PrimeFaces.current().ajax().update("MsgOtherForm");
									}
									try {
										conn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}

								}
								else
								{
									if(String.valueOf(sList.getFathersPhone()).length()==10
											&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
											&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
											&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
											&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
											&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
									{
										DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
												sList.getAddNumber(), conn,DBM.schoolId(),"");

									}
								}

							}



							return "feeCollection";
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
		String	schid = (String) ss1.getAttribute("schoolid");

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
				if(String.valueOf(info.getDiscountOtpNo()).length()==10
						&& !String.valueOf(info.getDiscountOtpNo()).equals("2222222222")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("9999999999")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("1111111111")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("1234567890")
						&& !String.valueOf(info.getDiscountOtpNo()).equals("0123456789"))
				{
					int randomPIN = (int) (Math.random() * 9000) + 1000;
					otp = String.valueOf(randomPIN);
					discoutnNo = info.getDiscountOtpNo();
					String typemessage = "Hello Sir,\nSomeone wants to give DISCOUNT while collecting fee.Use given OTP ("
							+ randomPIN + ") to allow for giving discount.Treat this as confidential.Thank You.\n"
							+ info.getSmsSchoolName();
					DBM.messageurlStaff(info.getDiscountOtpNo(), typemessage, "admin", conn,DBM.schoolId(),"");
					PrimeFaces.current().ajax().update("otpdialog");
					PrimeFaces.current().executeInitScript("PF('otp').show()");
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discout OTP No. is Invalid. Please contact administrator"));
				}

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

						totalFeepaid= DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
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

								if (info.getType().equalsIgnoreCase("foster") || info.getType().equalsIgnoreCase("fosterCBSE"))
								{
									ValueInfo vi = new Method().obtainHeadInfoBySeq("income,School Fees", DBM.schoolId(), DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn), conn);
									String accountId = new Method().obtainAccountId(vi.getId(),"Fee",conn);
									String dsc = "Fee Recieved - "+name+", Sr.No: "+srNo+", Receipt No:"+receipetNo;
									String dsc1 = "Fee Due - "+name+", Sr.No: "+srNo;
									new Method().insertInvoiceAndBill(accountId,"Dr",recipietDate,amoutnt,dsc1,"bill",conn);
									if(paymentMode.equalsIgnoreCase("cash"))
									{
										new Method().insertCashBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,conn);
									}
									else
									{
										new Method().insertBankBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,chequeNumber,challanDate,"",submittedBankName,conn);
									}
								}


								msgAmt = amoutnt;
								String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
										+ " in favour of fee by " + paymentMode + " via Receipt No. " + receipetNo
										+ ". \n\nRegards, \n"
										+ info.getSmsSchoolName();

								/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/


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
									//PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

								}

								if (message.equals("true"))
								{
									double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
									if(balance >0 && balance <= smsLimit)
									{
										balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
												+ "We suggest you to top-up your account today to ensure uninterrupted activity";
										if (userType.equalsIgnoreCase("admin"))
										{
											PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
											PrimeFaces.current().ajax().update("MsgLimitForm");
											try {
												conn.close();
											} catch (SQLException e) {
												e.printStackTrace();
											}
											return "";
										}
									}
									else if(balance <= 0)
									{
										balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
										if (userType.equalsIgnoreCase("admin"))
										{
											PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
											PrimeFaces.current().ajax().update("MsgOverForm");

										}
										else
										{
											balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

											PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
											PrimeFaces.current().ajax().update("MsgOtherForm");
										}
										try {
											conn.close();
										} catch (SQLException e) {
											e.printStackTrace();
										}
										return "";

									}

									if(String.valueOf(sList.getFathersPhone()).length()==10
											&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
											&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
											&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
											&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
											&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
									{
										DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
												sList.getAddNumber(), conn,DBM.schoolId(),"");
									}
								}


								return "feeCollection";
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

	public void sendMsg()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		if(String.valueOf(sList.getFathersPhone()).length()==10
				&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
				&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
				&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
				&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
				&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
		{
			SchoolInfoList info = DBM.fullSchoolInfo(conn);


			String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + msgAmt
					+ " in favour of fee by " + paymentMode + " via Receipt No. " + receipetNo
					+ ".\n\nRegards, \n"
					+ info.getSmsSchoolName();
			if(String.valueOf(sList.getFathersPhone()).length()==10)
			{
				DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
						sList.getAddNumber(), conn,DBM.schoolId(),"");
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
							totalFeepaid= DBM.FeePaidRecord(DBM.schoolId(),sList, preSession, ff.getFeeId(), conn);
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

							if (info.getType().equalsIgnoreCase("foster") || info.getType().equalsIgnoreCase("fosterCBSE"))
							{
								ValueInfo vi = new Method().obtainHeadInfoBySeq("income,School Fees", DBM.schoolId(), DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn), conn);
								String accountId = new Method().obtainAccountId(vi.getId(),"Fee",conn);
								String dsc = "Fee Recieved - "+name+", Sr.No: "+srNo+", Receipt No:"+num;
								String dsc1 = "Fee Due - "+name+", Sr.No: "+srNo;
								new Method().insertInvoiceAndBill(accountId,"Dr",recipietDate,amoutnt,dsc1,"bill",conn);
								if(paymentMode.equalsIgnoreCase("cash"))
								{
									new Method().insertCashBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,conn);
								}
								else
								{
									new Method().insertBankBokDetails(accountId,"cr",recipietDate,amoutnt,dsc,chequeNumber,challanDate,"",submittedBankName,conn);
								}
							}

							msgAmt = amoutnt;

							String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by " + paymentMode + " via Receipt No. " + num
									+ ".\n\nRegards, \n"
									+ info.getSmsSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(Double.valueOf(amount), conn);
								}
							}*/



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

							if (message.equals("true"))
							{
								double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
								if(balance >0 && balance <= smsLimit)
								{
									balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
											+ "We suggest you to top-up your account today to ensure uninterrupted activity";
									if (userType.equalsIgnoreCase("admin"))
									{
										PrimeFaces.current().executeInitScript("PF('MsgLmtDlg').show()");
										PrimeFaces.current().ajax().update("MsgLimitForm");
										try {
											conn.close();
										} catch (SQLException e) {
											e.printStackTrace();
										}
										return "";
									}
								}
								else if(balance <= 0)
								{
									balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
									if (userType.equalsIgnoreCase("admin"))
									{
										PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
										PrimeFaces.current().ajax().update("MsgOverForm");

									}
									else
									{
										balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

										PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
										PrimeFaces.current().ajax().update("MsgOtherForm");
									}
									try {
										conn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
									return "";

								}

								if(String.valueOf(sList.getFathersPhone()).length()==10
										&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
										&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
										&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
										&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
										&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
								{
									DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
											sList.getAddNumber(), conn,DBM.schoolId(),"");
								}
							}

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

	public void searchReceipts()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		feelist=DBM.viewFeeList1(DBM.schoolId(),conn);

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss1.getAttribute("schoolid");

		dailyfee=new ArrayList<>();
		int cashAmount=0,chequeAmount=0;
		int count=1;
		/*int index = name.indexOf("-") + 1;
		String id = name.substring(index);*/
		//int index=name.lastIndexOf(":")+1;
		String id=addmissionNumber;
		Date lockDate = DBM.checkLockDate(schid,conn);
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
		ArrayList<String> tempList=objStd.basicFieldsForStudentList();
		

		for (int i = 0; i < a.length; i++)
		{
			tempdeatils=new ArrayList<>();
			HashMap<String, String> feecllection=new HashMap<>();
			HashMap<String, String> discountMap=new HashMap<>();
			HashMap<String, String> totalAmountMap=new HashMap<>();

			int totalamuont=0,totalDiscount=0;
			DailyFeeCollectionBean ll = new DailyFeeCollectionBean();
			for (Feecollectionc list : getdailyfeecollection)
			{
				if (list.getRecipietNo().equals(String.valueOf(a[i])))
				{

					ll.setFeedate(list.getFeedate());
					ll.setReciptno(list.getRecipietNo());
					ll.setFeeDateStr(sdf.format(list.getFeedate()));
					String feetype=list.getFeeName();
					ll.setFeeRemark(list.getFeeRemark());
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
					ll.setUser(list.getUser());
					ll.setStudentname(info.getFname());
					ll.setDueDateStr(list.getDueDateStr());
					ll.setClassname(info.getClassName().substring(0,info.getClassName().indexOf("-")));
					ll.setSection(info.getSectionName());
					ll.setFathername(info.getFathersName());
					ll.setMothername(info.getMotherName());
					ll.setStudentid(id);
					ll.setChallanDate(list.getChallanDate());
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
				Integer.parseInt(info.getDiscount());
			}
		}

		//date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void duplicateFee()
	{
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		//(selectedstudent.getStudentid());
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());

		ArrayList<FeeInfo> selectedFees=new ArrayList<>();
		int i=1;

		for(FeeInfo ff:feelist)
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

				selectedFees.add(info);
				i=i+1;
			}
		}



		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

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

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}

	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}

	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}

	public void setGetdailyfeecollection(ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
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

	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	

}
