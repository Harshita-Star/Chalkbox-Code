package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeDynamicList;
import schooldata.FeeInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="onlineFeePaymentJsonBean")
@ViewScoped
public class OnlineFeePaymentJsonBean implements Serializable
{

	String json="";
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	String addmissionNumber,schoolid,neftNo;
	
	StudentInfo ss;
	String status="",paymentId="";
	String selectedMonths;
	ArrayList<FeeInfo> classFeeList, selectedFees = new ArrayList<>(),newclassFeelist=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public OnlineFeePaymentJsonBean() {

		Connection conn= DataBaseConnection.javaConnection();
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			
			

			addmissionNumber=params.get("studentId");
			schoolid=params.get("schid");
			neftNo=params.get("orderid");
			paymentId=params.get("paymentId");
			String mm=params.get("month");
			 status=params.get("status");
			 String type=params.get("type");
			 selectedMonths=mm;
			 
			 
			    if(type==null||type.equals(""))
				{
					type="old";
				}
			    
			    if(paymentId==null||paymentId.equals(""))
				{
			    	paymentId="";
				}

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(type.equals("old"))
				{
					ss=DBJ.studentDetailslistByAddNo(addmissionNumber,schoolid,conn);
					String dd="";
					String selectedSession[]=DBM.selectedSessionDetails(schoolid,conn).split("-");
					if(Integer.parseInt(mm)<4)
					{
						dd="25/"+mm+"/"+selectedSession[1];
					}
					else
					{
						dd="25/"+mm+"/"+selectedSession[0];
					}
					
					
					if(status==null||status.equals(""))
					{
						status="active";
					}
					else
					{
						status="Payment_In_Process";
					}

					ArrayList<StudentInfo>studentList=DBJ.searchStudentListForDueFeeReportforjson(ss.getAddmisssionNumber(),new SimpleDateFormat("dd/MM/yyyy").parse(dd)
							,DBM.selectedSessionDetails(schoolid,conn),schoolid,conn);

					HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

					classFeeList = DBJ.classFeesForStudentforjson(ss.getClassId(),
							DBM.selectedSessionDetails(schoolid,conn),ss.getStudentStatus(),ss.getConcession(),schoolid,conn);


					ArrayList<FeeInfo>lst=DBM.listFeeInfoForStudentWiseWithschoolid(schoolid,conn);
					if(lst.size()>0)
					{
						classFeeList.addAll(lst);
					}

					classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
		            dueAmount = 0;
					for (FeeInfo ls : classFeeList) {
						if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {

							String amount="0";
							if(map.get(ls.getFeeName())==null)
							{
								amount="0";
							}
							else
							{
								amount=map.get(ls.getFeeName());
							}

							ls.setDueamount(amount);

							ls.setPayAmount(Integer.parseInt(amount));
							dueAmount += Integer.parseInt(amount);

						}
						else
						{
							ls.setDueamount("0");
							ls.setPayAmount(0);

						}

						ls.setSelectCheckBox(false);

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

					int xy = 1;
					for(FeeInfo fi : classFeeList)
					{
						fi.setSelectCheckBox(true);
						fi.setSno(xy++);
					}

					submit();

					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("status","1");
					arr.add(obj);


					json=arr.toString();

				}
				else
				{

					if(status==null||status.equals(""))
					{
						status="active";
					}
					else
					{
						status="Payment_In_Process";
					}
					
					newGenerateFee();
					JSONArray arr=new JSONArray();

					JSONObject obj = new JSONObject();
					obj.put("status","1");
					arr.add(obj);


					json=arr.toString();
				}
			}
			else
			{

				JSONArray arr=new JSONArray();

				JSONObject obj = new JSONObject();
				obj.put("status","0");
				arr.add(obj);


				json=arr.toString();
			}
		} catch (Exception e) {
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

	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}


	public String submit() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		try {
			schoolid = ss.getSchid();
			
			
			SchoolInfoList info = DBM.fullSchoolInfo(schoolid,conn);

			discountAmount = 0;

			ArrayList<FeeInfo>selectedFees = new ArrayList<>();

			for(FeeInfo ff : classFeeList)
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
							
							if(status.equals("active"))
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
								ii = DBM.submitFeeSchidForBlm(schoolid,ss, ff.getPayAmount(), ff.getFeeId(), "Payment Gateway", "",
										"", num, ff.getPayDiscount(),DBM.selectedSessionDetails(schoolid,conn), new Date(), "", neftNo,
										new Date(), new Date(), conn, "", new Date(), ff.getDueamount(), "current","","0","0","N/A",status, paymentId);
								/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
									DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
											(ff.getPayAmount() + ff.getPayDiscount()), conn);
								}*/
								amoutnt += ff.getPayAmount();
							}

							if (ii >= 1) {
								if(status==null||status.equals("")||status.equalsIgnoreCase("active"))
								{
									String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
											+ " in favour of fee by Payment Gateway via Receipt No. " + num
											+ "\n\nRegards, \n"
											+ info.getSmsSchoolName();
									DBJ.messageurl1(String.valueOf(ss.getFathersPhone()), typeMessage,
											ss.getAddNumber(), schoolid,conn);
								

								}

							
								/*if (info.getCtype().equalsIgnoreCase("foster")
										|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
									if (paymentMode.equalsIgnoreCase("Cash")) {
										DBM.increaseCompanyCapitalAmount(schoolid,Double.valueOf(amount), conn);
									}
								}*/

								/*if (message.equals("true")) {*/
								/*}

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
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		 finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}
	
	public void newGenerateFee()
	{
	

		
  
		String feeRemark="";


		Connection conn = DataBaseConnection.javaConnection();
		StudentInfo sList = DBJ.studentDetailslistByAddNo(addmissionNumber,schoolid,conn);
		
		schoolid = sList.getSchid();
		SchoolInfoList schoolInfo = DBM.fullSchoolInfo(schoolid,conn);
        String studentStatus=sList.getStudentStatus();
		String[] selectedCheckFees=selectedMonths.split(",");

		
		for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
		{
			if(jjj==0)
			{

				feeRemark=DBM.installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
			else
			{

				feeRemark=feeRemark+","+DBM.installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
		}


		//////// // System.out.println(schoolInfo.getBranch_id());
		


		//////// // System.out.println(showDisabled);
		String preSession = DBM.selectedSessionDetails(schoolid,conn);
		preSession.split("-");

		addmissionNumber = sList.getAddNumber();
		
		String connsessioncategory = sList.getConcession();
		//////// // System.out.println(connsessioncategory);
		//ss.setAttribute("admisionnumber", addmissionNumber);
		String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		
		//discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);

		
		FeeDynamicList lateFees=DBM.getlatefeecalculation(schoolid,conn);
		
		
		newclassFeelist=new ArrayList<>();
		dueAmount = 0;
		for(int i =0;i<selectedCheckFees.length;i++)
		{
			
			Date findDate=null;
			Date cDate=null;
			
			
			FeeDynamicList list = null;
			
			if((schoolid.equals("287") && Integer.parseInt(sList.getClassId())<13 ) ||(!schoolid.equals("287")))
			{
				 list=   DBM.getAllinstallmentdetails(schoolid, selectedCheckFees[i], conn);
						
			}
			else
			{
				 try {
					list=  DBM.getAllInstallmentforAjmaniDetails(schoolid, selectedCheckFees[i], conn);
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
			else if(Integer.parseInt(ls.getDueamount())<=0)
			{
				newclassFeelist.remove(ls);
			}
		}
		
		String num = "";
		
		if(status.equals("active"))
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

		for (FeeInfo ff : newclassFeelist) {
			
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



			

                 //// // System.out.println(ff.getFeeName()+"....."+ff.getFeeId());
			int ii = DBM.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), "Payment Gateway", "",
					"", num, ff.getPayDiscount(), preSession, new Date(), "", neftNo,
					new Date(), new Date(), conn, feeRemark, new Date(), ff.getDueamount(), "current",ff.getFeeInstallMonth(),ff.getMainAmount(),ff.getTaxAmount(),"",status, paymentId);
			/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
				DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
						(ff.getPayAmount() + ff.getPayDiscount()), conn);
			}*/
			
		}
		
		
		

	}




}
