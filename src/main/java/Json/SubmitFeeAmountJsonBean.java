package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="submitFeeAmountJSonBean")
@ViewScoped
public class SubmitFeeAmountJsonBean implements Serializable{


	String json;
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	String neftNo="";
	String schoolid,remark,preSession,name,fathersName,className,sectionName,addmissionNumber,srNo,dobString,gender,studentStatus;
	Date dob;
	ArrayList<FeeInfo> classFeeList, selectedFees = new ArrayList<>(),newclassFeelist=new ArrayList<>();
	StudentInfo sList;
	String month="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
    String paymentStatus="",paymentId="";
	public SubmitFeeAmountJsonBean()
	{

		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();

		addmissionNumber=params.get("studentId");
		schoolid=params.get("schid");
		neftNo=params.get("orderid");
		paymentId=params.get("paymentId");
		String mm=params.get("month");
		paymentStatus=params.get("status");
		
		if(paymentId==null||paymentId.equals(""))
		{
	    	paymentId="";
		}
		
		JSONArray arr=new JSONArray();

		Map<String, String> sysParams =FacesContext.getCurrentInstance().
				getExternalContext().getRequestHeaderMap();
		String userAgent = sysParams.get("User-Agent");
		boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
		
		if(true)
		{
			String[] test=mm.split(",");

			for(int i=0;i<test.length;i++)
			{
				if(month.equals(""))
				{
					month=instalcheck1(test[i]);
				}
				else
				{
					month=month+","+instalcheck1(test[i]);
				}

			}

			if(paymentStatus==null||paymentStatus.equals(""))
			{
				paymentStatus="active";
			}
			else
			{
				paymentStatus="Payment_In_Process";
			}
			
			newGenerateFee();
			submit();
			
			JSONObject obj = new JSONObject();
			obj.put("status","1");
			arr.add(obj);
		}
		else
		{
			JSONObject obj = new JSONObject();
			obj.put("status","0");
			arr.add(obj);
		}

		json=arr.toString();


	}

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


	public void newGenerateFee()
	{
	
		Connection conn= DataBaseConnection.javaConnection();
		try
		{
			sList = new DatabaseMethods1().studentDetailslistByAddNo(schoolid,addmissionNumber, conn);

			DatabaseMethods1 DBM = new DatabaseMethods1();
			DBM.fullSchoolInfo(schoolid,conn);

			String[] selectedCheckFees=month.split(",");
			for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
			{
				if(jjj==0)
				{

					remark=instalcheck(selectedCheckFees[jjj]);
				}
				else
				{

					remark=remark+","+instalcheck(selectedCheckFees[jjj]);
				}
			}


			////// // System.out.println(schoolInfo.getBranch_id());



			////// // System.out.println(showDisabled);
			preSession = DatabaseMethods1.selectedSessionDetails(schoolid,conn);
			String[] session=preSession.split("-");

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
			////// // System.out.println(connsessioncategory);
			String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());

			Date cDate=null;
				cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
			//discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);

			newclassFeelist=new ArrayList<>();
			dueAmount = 0;
			for(int i =0;i<selectedCheckFees.length;i++)
			{
				
				
				if(selectedCheckFees[i].equals("-1"))
				{
					   classFeeList=new ArrayList<>();
						classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
						for(FeeInfo lss:classFeeList)
						{
							if(lss.getFeeId().equals("-1"))
							{
								int mainamount=lss.getFeeAmount();
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, lss.getFeeId(),selectedCheckFees[i],conn);
								int dAmount=mainamount-totalpaidamount;
								if(dAmount>0)
								{
									dueAmount += dAmount;
									lss.setFeeInstallment(instalcheck(selectedCheckFees[i]));
									lss.setFeeInstallMonth(selectedCheckFees[i]);
									lss.setDueamount(String.valueOf(dAmount));
									lss.setPayAmount(dAmount);
									lss.setSelectCheckBox(false);



								}
							}


						}
						
				}
				else
				{
					classFeeList = DBM.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
							conn);
					int count=1;
					if(selectedCheckFees[i].equals("6")||selectedCheckFees[i].equals("8")||selectedCheckFees[i].equals("11")||selectedCheckFees[i].equals("3"))
					{
						count=2;
					}


					int month=0;
					if(Integer.parseInt(selectedCheckFees[i]) < 4)
					{
						month = Integer.parseInt(selectedCheckFees[i])+12;
					}
					else
					{
						month = Integer.parseInt(selectedCheckFees[i]);
					}


					ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReportForMasterForFees(schoolid,addmissionNumber, Integer.parseInt(selectedCheckFees[i]),
							preSession, conn, "feeCollection",count);

					HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

					for (FeeInfo ls : classFeeList) {

						ls.setFeeInstallment(instalcheck(selectedCheckFees[i]));
						ls.setFeeInstallMonth(selectedCheckFees[i]);
						if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
							if(i==0)
							{

								int dueamountcheck=Integer.parseInt(map.get(ls.getFeeName()));
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
								int leftAmount=dueamountcheck-totalpaidamount;
								if(leftAmount>=0)
								{
									ls.setDueamount(String.valueOf(leftAmount));
									ls.setPayAmount(leftAmount);
									dueAmount +=leftAmount;

								}
								else
								{
									ls.setDueamount(String.valueOf(0));
									ls.setPayAmount(0);
									dueAmount +=0;

								}

							}
							else
							{
								int dueamountcheck=Integer.parseInt(map.get(ls.getFeeName()));
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
								int leftAmount=dueamountcheck-totalpaidamount;
								if(leftAmount>=0)
								{
									ls.setDueamount(String.valueOf(ls.getPayAmount()+leftAmount));
									ls.setPayAmount(ls.getPayAmount()+leftAmount);
									dueAmount += leftAmount;
								}
								else
								{
									ls.setDueamount(String.valueOf(ls.getPayAmount()+0));
									ls.setPayAmount(ls.getPayAmount()+0);
									dueAmount += 0;
								}

							}




						}



						if(ls.getFeeId().equals("-2"))
						{
							int latefee=0;
							if(selectedCheckFees[i].equals("4"))
							{
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("30/04/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									//ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									if(duedate.before(cDate) && month>=4)
									{
										latefee=200;
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

							}
							else if(selectedCheckFees[i].equals("6"))
							{
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
							}
							else if(selectedCheckFees[i].equals("8"))
							{
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
							}
							else if(selectedCheckFees[i].equals("9"))
							{
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
							}
							else if(selectedCheckFees[i].equals("11"))
							{
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
							}
							else if(selectedCheckFees[i].equals("12"))
							{
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
							}
							else if(selectedCheckFees[i].equals("1"))
							{
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
							}
							else if(selectedCheckFees[i].equals("3"))
							{
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
							}

							ls.setDueamount(String.valueOf(ls.getPayAmount()+0));
							ls.setPayAmount(ls.getPayAmount()+0);
							dueAmount += 0;
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
				else if(Integer.parseInt(ls.getDueamount())<=0)
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
					fi.setDisabledDiscountFee(false);
				}

				fi.setSelectCheckBox(true);
				fi.setSno(xy++);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        finally {
        	try {
    			conn.close();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
		}
	}


	public String submit() {

		schoolid = sList.getSchid();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

		discountAmount = 0;

		selectedFees = new ArrayList<>();

		for(FeeInfo ff : newclassFeelist)
		{
			if(ff.isSelectCheckBox())
			{
				selectedFees.add(ff);
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


				return "";
			} else {
				int amoutnt = 0;
				boolean flag = false, flag1 = false;
				int i = 0;
				int balLeft = 0;
				/*for (FeeInfo ff : selectedFees) {
						if (ff.getPayAmount() > 0 || ff.getPayDiscount() > 0) {
							int totalfee = 0;
							int totalFeepaid = 0;
							if (ff.getFeeName().equals("Transport")) {
								totalFeepaid = DBM.FeePaidRecord(schoolid,sList, preSession, "0", conn);
								for (FeeStatus nn : transportfeeStatus) {
									totalfee += Integer.parseInt(nn.getTransportFee());
								}
							}
							if (ff.getFeeName().equals("Previous Fee")) {

								totalFeepaid = DBM.previousFeePaidRecord(schoolid,sList.getAddNumber(), conn);
								for (FeeInfo ss : feeStructureList) {
									if (ss.getFeeName().equals(ff.getFeeName())) {
										totalfee += ss.getTotalFeeAmount();
									}
								}
							} else {
								totalFeepaid = DBM.FeePaidRecord(schoolid,sList, preSession, ff.getFeeId(), conn);
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
						} else {
							flag = true;

							break;
						}
						i = i + 1;
					}*/

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
						String num = "";
						
						if(paymentStatus.equals("active"))
						{
							int number = DBM.feeserailno(schoolid,conn);
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

						}
						else
						{
							num="temp"+new SimpleDateFormat("yMdhms").format((new Date()));

						}

						int ii = 0;
						for (FeeInfo ff : selectedFees) {
							if (ff.getFeeName().equalsIgnoreCase("Late Fee") || ff.getFeeName().equals("Any Other Charges")) {
								ff.setDueamount(String.valueOf(ff.getPayAmount() + ff.getPayDiscount()));
							}
							ii = DBM.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), "Payment Gateway", "",
									"", num, ff.getPayDiscount(), preSession, new Date(), "", neftNo,
									new Date(), new Date(), conn, remark, new Date(), ff.getDueamount(), "current",ff.getFeeInstallMonth(),"0","0","N/A",paymentStatus, paymentId);
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
									DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
											(ff.getPayAmount() + ff.getPayDiscount()), conn);
								}*/
							amoutnt += ff.getPayAmount();
						}

						if (ii >= 1) {

							String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by Payment Gateway via Receipt No. " + num
									+ "\n\nRegards, \n"
									+ info.getSmsSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
										|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
									if (paymentMode.equalsIgnoreCase("Cash")) {
										DBM.increaseCompanyCapitalAmount(schoolid,Double.valueOf(amount), conn);
									}
								}*/

							/*if (message.equals("true")) {*/
						/*	DBM.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
									sList.getAddNumber(), conn,schoolid);
						*/	/*}

								DBM.notification(schoolid,"Fees", typeMessage,
										String.valueOf(sList.getFathersPhone()) + "-" + schoolid, conn);
								DBM.notification(schoolid,"Fees", typeMessage,
										String.valueOf(sList.getMothersPhone()) + "-" + schoolid, conn);
							 */
							/*HttpSession rr = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
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
								rr.setAttribute("feeupto", new SimpleDateFormat("dd/MM/yyyy").format(dueDate));
							 */
							/*if (!schoolid.equals("206")) {
									PrimeFaces.current().executeInitScript("window.open('masterFeeReceipt.xhtml')");

								}
							 */
							return "masterStudentFeeCollection";
						} else {
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
		else if(month.equals("-1"))
		{
			smonth="Previous_Fee";
		}

		return smonth;

	}




	public String instalcheck1(String month)
	{

		String smonth="";

		if(month.equalsIgnoreCase("April"))
		{
			smonth="4";
		}
		else if(month.equalsIgnoreCase("May-June"))
		{
			smonth="6";
		}
		else if(month.equalsIgnoreCase("Jul-Aug"))
		{
			smonth="8";
		}
		else if(month.equalsIgnoreCase("September"))
		{
			smonth="9";
		}
		else if(month.equalsIgnoreCase("Oct-Nov"))
		{
			smonth="11";
		}
		else if(month.equalsIgnoreCase("December"))
		{
			smonth="12";
		}
		else if(month.equalsIgnoreCase("January"))
		{
			smonth="1";
		}
		else if(month.equalsIgnoreCase("Feb-Mar"))
		{
			smonth="3";
		}
		else if(month.equalsIgnoreCase("Previous_Fee"))
		{
			smonth="-1";
		}

		return smonth;

	}


}

