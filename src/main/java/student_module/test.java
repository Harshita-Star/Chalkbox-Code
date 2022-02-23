package student_module;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.paytm.pg.merchant.CheckSumServiceHelper;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeDynamicList;
import schooldata.FeeInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;



public class test implements Serializable{

	URL url;
	int dueAmount = 0, submitAmount = 0, discountAmount = 0;
	String neftNo="",schid;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	String session, rzpPaymentId, txnStatus;
	SchoolInfoList schinfo = new SchoolInfoList();
	public void values(String paymentId,String paytmOrderid,ArrayList<FeeInfo>list,String addnumb,String schid,String session1,String payStatus) {
			Connection con = DataBaseConnection.javaConnection();
			String checksum;
			HttpURLConnection connection = null;
			TreeMap<String, String> tmap= new TreeMap<>();
			session=session1;
			schinfo = DBJ.fullSchoolInfo(schid, con);
			txnStatus = payStatus;
			System.out.println("receipt status : "+payStatus+" ....."+txnStatus);
			rzpPaymentId = paymentId;
			neftNo=paytmOrderid;
			if(paymentId == null || paymentId.equals(""))
			{
				rzpPaymentId = "";
			}
			
			try{
				
				if(schinfo.getPg_type().equalsIgnoreCase("paytm"))
				{
					String urlParameters="";
					if(schid.equals("251"))
					{
						tmap.put("MID", "BLMSrS53643455483506");
						String orderid[]=paytmOrderid.split("=");
						tmap.put("ORDERID", orderid[1]);
						neftNo=orderid[1];

						checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("YaaAReat0ymrB2O4",tmap);
						tmap.put("CHECKSUMHASH", checksum);
						JSONObject obj = new JSONObject(tmap);
						urlParameters=obj.toString();

						JSONObject obj1 = new JSONObject();
						obj1.put("MID", "BLMSrS53643455483506");
						obj1.put("ORDERID", orderid[1]);
						obj1.put("CHECKSUMHASH", checksum);
						//("https://securegw.paytm.in/merchant-status/getTxnStatus?JsonData="+obj1.toString());


						urlParameters=URLEncoder.encode(urlParameters);

					}
					else if(schid.equals("252"))
					{
						tmap.put("MID", "BLMBlo43887663064832");
						String orderid[]=paytmOrderid.split("=");
						tmap.put("ORDERID", orderid[1]);
						neftNo=orderid[1];
						checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("3_@O9w5vWSb!I%jg",tmap);
						tmap.put("CHECKSUMHASH", checksum);
						JSONObject obj = new JSONObject(tmap);
						urlParameters=obj.toString();

						JSONObject obj1 = new JSONObject();
						obj1.put("MID", "BLMBlo43887663064832");
						obj1.put("ORDERID", orderid[1]);
						obj1.put("CHECKSUMHASH", checksum);
						//("https://securegw.paytm.in/merchant-status/getTxnStatus?JsonData="+obj1.toString());


						urlParameters=URLEncoder.encode(urlParameters);
					}
					else
					{
//						Connection con=DataBaseConnection.javaConnection();
//						SchoolInfoList list1=DBJ.fullSchoolInfo(schid, con);
						
						tmap.put("MID", schinfo.getPaytm_mid());
						String orderid[]=paytmOrderid.split("=");
						tmap.put("ORDERID", orderid[1]);
						neftNo=orderid[1];
						checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(schinfo.getPaytm_marchent_key(),tmap);
						tmap.put("CHECKSUMHASH", checksum);
						JSONObject obj = new JSONObject(tmap);
						urlParameters=obj.toString();
	                    JSONObject obj1 = new JSONObject();
						obj1.put("MID", "BLMBlo43887663064832");
						obj1.put("ORDERID", orderid[1]);
						obj1.put("CHECKSUMHASH", checksum);
	                    urlParameters=URLEncoder.encode(urlParameters);
					}


					URL url = new URL("https://securegw-stage.paytm.in/merchant-status/getTxnStatus");

					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("contentType","application/json");
					connection.setUseCaches(false);
					connection.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
					wr.writeBytes("JsonData=");
					wr.writeBytes(urlParameters);
					wr.close();
					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					//("test");
					/*while((line = rd.readLine()) != null) {
			

							          }*/
					rd.close();
				}
				
				try {
        			con.close();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
				
				selectedList = list;
				addmissionNumber=addnumb;
				schoolid = schid;


                if(schid.equals("251")||schid.equals("252"))
                {
                	newGenerateFee();
    				submit();
	
                }
                else
                {
                	newGenerateFee1();
                }
				
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
	public void newGenerateFee1()
	{
  
		String feeRemark="";


		Connection conn = DataBaseConnection.javaConnection();
		StudentInfo sList = DBJ.studentDetailslistByAddNosessionWIse(addmissionNumber,schoolid,session,conn);
		
		schoolid = sList.getSchid();
		SchoolInfoList schoolInfo = obj.fullSchoolInfo(schoolid,conn);
        String studentStatus=sList.getStudentStatus();
        String[] selectedCheckFees=new String[selectedList.size()];
		int ij=0;
		for(FeeInfo ls:selectedList)
		{
			selectedCheckFees[ij]=String.valueOf(ls.getFeeMonthInt());
			ij=ij+1;
		}

		
		for(int jjj=0;jjj<selectedCheckFees.length;jjj++)
		{
			if(jjj==0)
			{

				feeRemark=obj.installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
			else
			{

				feeRemark=feeRemark+","+obj.installmentName(schoolid,selectedCheckFees[jjj],conn);
			}
		}


		//////// // System.out.println(schoolInfo.getBranch_id());
		


		//////// // System.out.println(showDisabled);
		String preSession = session;
		preSession.split("-");

		addmissionNumber = sList.getAddNumber();
		
		String connsessioncategory = sList.getConcession();
		//////// // System.out.println(connsessioncategory);
		//ss.setAttribute("admisionnumber", addmissionNumber);
		String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		
		//discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);

		
		FeeDynamicList lateFees=obj.getlatefeecalculation(schoolid,conn);
		
		
		newclassFeelist=new ArrayList<>();
		dueAmount = 0;
		for(int i =0;i<selectedCheckFees.length;i++)
		{
			
			Date findDate=null;
			Date cDate=null;
			
			
			FeeDynamicList list = null;
			
			if((schoolid.equals("287") && Integer.parseInt(sList.getClassId())<13 ) ||(!schoolid.equals("287")))
			{
				 list=   obj.getAllinstallmentdetails(schoolid, selectedCheckFees[i], conn);
						
			}
			else
			{
				 try {
					list=   obj.getAllInstallmentforAjmaniDetails(schoolid, selectedCheckFees[i], conn);
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
				classFeeList = obj.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
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
					int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
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
				classFeeList = obj.classFeesForStudent11(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
						conn);
				
				



				int count=Integer.parseInt(list.getMonth());



				ArrayList<StudentInfo> studentList = obj.searchStudentListForDueFeeReportForMasterForStkabeerwithSession(schoolid,addmissionNumber, Integer.parseInt(selectedCheckFees[i]),
						preSession, conn, "feeCollection",count,list.getInstallment_Month_Include(),session);

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
							int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
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
							int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
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

							int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
		System.out.println("receipt status 1 : "+txnStatus);
		String status=txnStatus;
		
		System.out.println("receipt status 2 : "+status);
		
		if(status.equalsIgnoreCase("active"))
		{
			int number = obj.feeserailno(schoolid,conn);
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

             
			 // System.out.println("hello in payment");

			

                 //// // System.out.println(ff.getFeeName()+"....."+ff.getFeeId());
			int ii = obj.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), "Payment Gateway", "",
					"", num, ff.getPayDiscount(), preSession, new Date(), "", neftNo,
					new Date(), new Date(), conn, feeRemark, new Date(), ff.getDueamount(), "current",ff.getFeeInstallMonth(),"0","0","",status,rzpPaymentId);
			/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
				DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
						(ff.getPayAmount() + ff.getPayDiscount()), conn);
			}*/
			
		}
		
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	String schoolid,remark,preSession,name,fathersName,className,sectionName,addmissionNumber,srNo,dobString,gender,studentStatus;
	Date dob;
	ArrayList<FeeInfo> classFeeList, selectedFees = new ArrayList<>(),newclassFeelist=new ArrayList<>();
	ArrayList<FeeInfo> selectedList = new ArrayList<>();
	StudentInfo sList;
	public void newGenerateFee()
	{

		Connection conn= DataBaseConnection.javaConnection();

		sList = obj.studentDetailslistByAddNo(schoolid,addmissionNumber, conn);

		obj.fullSchoolInfo(schoolid,conn);

		String[] selectedCheckFees=new String[selectedList.size()];
		int ij=0;
		for(FeeInfo ls:selectedList)
		{
			selectedCheckFees[ij]=String.valueOf(ls.getFeeMonthInt());
			ij=ij+1;
		}


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


		////(schoolInfo.getBranch_id());



		////(showDisabled);
		preSession = obj.selectedSessionDetails(schoolid,conn);
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
		////(connsessioncategory);
		String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		Date cDate=null;
		try {
			//findDate= new SimpleDateFormat("dd/MM/yyyy").parse(findDate1);
			cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//discountFeeList = DBM.feeDiscountForSelectedStudent(schoolid,addmissionNumber, conn);

		newclassFeelist=new ArrayList<>();
		dueAmount = 0;
		for(int i =0;i<selectedCheckFees.length;i++)
		{
			classFeeList = obj.classFeesForStudent(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
					conn);
			//classFeeList = obj.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);

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


			ArrayList<StudentInfo> studentList = obj.searchStudentListForDueFeeReportForMasterForFees(schoolid,addmissionNumber, Integer.parseInt(selectedCheckFees[i]),
					preSession, conn, "feeCollection",count);

			HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

			for (FeeInfo ls : classFeeList) {

				//(selectedCheckFees[i]+"............."+ls.getFeeName()+"............."+map.get(ls.getFeeName()));
				ls.setFeeInstallment(instalcheck(selectedCheckFees[i]));
				ls.setFeeInstallMonth(selectedCheckFees[i]);
				if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
					if(i==0)
					{

						int dueamountcheck=Integer.parseInt(map.get(ls.getFeeName()));
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),selectedCheckFees[i],conn);
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
					//("late fee");
					int latefee=0;
					if(selectedCheckFees[i].equals("4"))
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
						int totalpaidamount = obj.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",selectedCheckFees[i],conn);
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
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String submit() {
		schoolid = sList.getSchid();
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList info = obj.fullSchoolInfo(schoolid,conn);

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
						int number = obj.feeserailno(schoolid,conn);
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
							ii = obj.submitFeeSchidForBlm(schoolid,sList, ff.getPayAmount(), ff.getFeeId(), "Payment Gateway", "",
									"", num, ff.getPayDiscount(), preSession, new Date(), "", neftNo,
									new Date(), new Date(), conn, remark, new Date(), ff.getDueamount(), "current",ff.getFeeInstallMonth(),"0","0","N/A",txnStatus,rzpPaymentId);
							/*if (ii >= 1 && ff.getFeeName().equals("Previous Fee")) {
								DBM.updatePaidAmountOfPreviousFee(schoolid,sList.getAddNumber(),
										(ff.getPayAmount() + ff.getPayDiscount()), conn);
							}*/
							amoutnt += ff.getPayAmount();
						}

						if (ii >= 1) {

							String typeMessage = "Dear Parent, \n\nReceived payment of Rs." + amoutnt
									+ " in favour of fee by "+"Payment Gateway"+" via Receipt No. " + num
									+ "\n\nRegards, \n"
									+ info.getSmsSchoolName();

							/*if (info.getCtype().equalsIgnoreCase("foster")
									|| info.getCtype().equalsIgnoreCase("fosterCBSE")) {
								if (paymentMode.equalsIgnoreCase("Cash")) {
									DBM.increaseCompanyCapitalAmount(schoolid,Double.valueOf(amount), conn);
								}
							}*/

							/*if (message.equals("true")) {*/
							obj.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,
									sList.getAddNumber(), conn,schoolid,"");
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

}
