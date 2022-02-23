package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.FeeStatus;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.Transport;

@ManagedBean(name="collectFeeNowJSON")
@ViewScoped

public class CollectFeeNowJsonBean implements Serializable
{
	String json;
	StudentInfo sList;
	ArrayList<FeeInfo> classFeeList,selectedFees=new ArrayList<>();
	int dueAmount=0;
	int flag,transportFeeLeft;
	String firstMonthTransportPeriod,studentStatus,message;
	String paymentMode,bankName,chequeNumber,challanNo,neftNo;
	Date dob,challanDate,neftDate;
	ArrayList<FeeStatus> transportfeeStatus;
	ArrayList<FeeInfo> feeStructureList=new ArrayList<>();
	Date recipietDate=new Date();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();

	public CollectFeeNowJsonBean()
	{
		

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			selectedFees=new ArrayList<>();
			feeStructureList=new ArrayList<>();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String addmissionNumber = params.get("studentid");
			String schid=params.get("Schoolid");
			String feesId=params.get("feesId");
			String feesName=params.get("feesName");
			String payAmount=params.get("payAmount");
			String payDiscount=params.get("payDiscount");

			paymentMode=params.get("paymentMode");
			bankName=params.get("bankName");
			chequeNumber=params.get("chequeNo");

			String chlDate=params.get("challanDate");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(chlDate==null || chlDate.equals(""))
				{
					challanDate=null;
				}
				else
				{
					try
					{
						challanDate=new Date();
						challanDate=new SimpleDateFormat("dd/MM/yyyy").parse(chlDate);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				String feeIdArr[]=feesId.split(",");
				String feeNameArr[]=feesName.split(",");
				String payAmtArr[]=payAmount.split(",");
				String payDiscArr[]=payDiscount.split(",");

				for(int x=0;x<feeIdArr.length;x++)
				{
					FeeInfo ii=new FeeInfo();

					ii.setFeeId(feeIdArr[x]);
					ii.setFeeName(feeNameArr[x]);
					ii.setPayAmount(Integer.valueOf(payAmtArr[x]));
					ii.setPayDiscount(Integer.valueOf(payDiscArr[x]));

					selectedFees.add(ii);
				}

				ArrayList<String> messageList=DBJ.checkmessageSetting(conn,schid);
				message=messageList.get(0);


				String preSession=DBM.selectedSessionDetails(schid,conn);
				SchoolInfoList schoolInfo=DBJ.fullSchoolInfo(schid,conn);

				sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schid, conn);

				ArrayList<StudentInfo>studentList=DBJ.searchStudentListForDueFeeReport(addmissionNumber,new Date(),preSession,conn,schid);

				HashMap<String,String>map=(HashMap<String, String>) studentList.get(0).getFeesMap();

				classFeeList=DBJ.classFeesForStudent(sList.getClassId(),preSession,sList.getStudentStatus(),sList.getConcession(),conn,schid);
				classFeeList=DBM.addPreviousFee(schid,classFeeList,addmissionNumber,conn);
				dueAmount=0;
				for(FeeInfo ls:classFeeList)
				{
					if(!ls.getFeeId().equals("-2")&&!ls.getFeeId().equals("-3"))
					{
						ls.setDueamount(map.get(ls.getFeeName()));
						ls.setPayAmount(Integer.parseInt(map.get(ls.getFeeName())));
						dueAmount+=Integer.parseInt(map.get(ls.getFeeName()));

					}
				}

				if(DBM.schoolId().equals("206"))
				{
					int i =DBJ.totalStudentExpense(addmissionNumber,conn,schid);
					classFeeList.get(classFeeList.size()-1).setFeeAmount(i);

				}

				studentStatus=sList.getStudentStatus();
				DateFormat df = new SimpleDateFormat("MM");
				String reportDate = df.format(sList.getStartingDate());
				int startingMonth=0;
				int session=DBJ.getpromostionclass(String.valueOf(sList.getAddNumber()),conn,schid);
				if(session==1||studentStatus.equalsIgnoreCase("old"))
				{
					if(schoolInfo.getSchoolSession().equals("4-3"))
					{
						startingMonth=4;
					}
					else
					{
						startingMonth=5;
					}
				}
				else
				{
					if(schoolInfo.getFeeStart().equalsIgnoreCase("session_date"))
					{
						if(schoolInfo.getSchoolSession().equals("4-3"))
						{
							startingMonth=4;
						}
						else
						{
							startingMonth=5;
						}
					}
					else
					{
						startingMonth=Integer.parseInt(reportDate);
					}
				}
				//totalAmount=new DatabaseMethods1().tutionFeePaid(sList,preSession)+DatabaseMethods1.getTutionDiscount(sList.getAddNumber(),preSession);

				feeStructureList=new ArrayList<>();

				for(FeeInfo rr:classFeeList)
				{
					if(!rr.getFeeName().equals("Transport") && !rr.getFeeName().equals("Previous Fee"))
					{
						if(rr.getFeeType().equals("year"))
						{
							int fee=rr.getFeeAmount();
							int feepaidAmount=DBJ.FeePaidRecord(sList,preSession,rr.getFeeId(),conn,schid);
							int leftamount=fee-feepaidAmount;
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setFeePeriod("-");
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(feepaidAmount);
							info.setTotalFeeLeftAmount(leftamount);
							feeStructureList.add(info);
							fee=feepaidAmount=leftamount=0;
						}
						else if(rr.getFeeType().equals("month"))
						{
							int totalpaidamount=0;
							totalpaidamount=DBJ.FeePaidRecord(sList,preSession,rr.getFeeId(),conn,schid);
							int monthly=0;
							if(schoolInfo.getSchoolSession().equals("4-3"))
							{
								if(startingMonth<4)
								{
									monthly=startingMonth+12;
								}
								else
								{
									monthly=startingMonth;
								}
							}
							else
							{
								if(startingMonth<5)
								{
									monthly=startingMonth+12;
								}
								else
								{
									monthly=startingMonth;
								}
							}

							////// // System.out.println("hellooooo........."+monthly);

							if(schoolInfo.getSchoolSession().equals("4-3"))
							{
								for(int i=monthly;i<=15;i++)
								{
									int month = i;
									if(i>12)
										month=i-12;

									int fee=rr.getFeeAmount();

									if(fee<=totalpaidamount)
									{
										FeeInfo info=new FeeInfo();
										String month1=DBJ.monthList(month);
										info.setFeePeriod(month1);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(fee);
										info.setTotalFeeLeftAmount(0);
										feeStructureList.add(info);
										totalpaidamount=totalpaidamount-fee;
									}
									else
									{

										int leftamount=fee-totalpaidamount;
										FeeInfo info=new FeeInfo();
										info.setFeePeriod(DBJ.monthList(month));

										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(totalpaidamount);
										info.setTotalFeeLeftAmount(leftamount);
										feeStructureList.add(info);
										totalpaidamount=0;
									}
								}
							}
							else
							{
								for(int i=monthly;i<=16;i++)
								{
									int month = i;
									if(i>12)
										month=i-12;

									int fee=rr.getFeeAmount();

									if(fee<=totalpaidamount)
									{
										FeeInfo info=new FeeInfo();
										String month1=DBJ.monthList(month);
										info.setFeePeriod(month1);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(fee);
										info.setTotalFeeLeftAmount(0);
										feeStructureList.add(info);
										totalpaidamount=totalpaidamount-fee;
									}
									else
									{
										int leftamount=fee-totalpaidamount;
										FeeInfo info=new FeeInfo();
										info.setFeePeriod(DBJ.monthList(month));

										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(totalpaidamount);
										info.setTotalFeeLeftAmount(leftamount);
										feeStructureList.add(info);
										totalpaidamount=0;
									}
								}
							}
						}
						else if(rr.getFeeType().equals("quarter"))
						{

							int startmont=0;
							if(schoolInfo.getSchoolSession().equals("4-3"))
							{
								if(startingMonth>=4&&startingMonth<=6)
								{
									startmont=1;
								}
								else if(startingMonth>=7&&startingMonth<=9)
								{
									startmont=2;
								}
								else if(startingMonth>=10&&startingMonth<=12)
								{
									startmont=3;
								}
								else if(startingMonth>=1&&startingMonth<=3)
								{
									startmont=4;
								}

							}
							else
							{
								int tempStartingMonth=startingMonth;
								if(startingMonth==1)
								{
									tempStartingMonth=13;
								}

								if(tempStartingMonth>=5&&tempStartingMonth<=7)
								{
									startmont=1;
								}
								else if(tempStartingMonth>=8&&tempStartingMonth<=10)
								{
									startmont=2;
								}
								else if(tempStartingMonth>=11&&tempStartingMonth<=13)
								{
									startmont=3;
									////// // System.out.println("sds : "+startingMonth);
								}
								else if(tempStartingMonth>=2&&tempStartingMonth<=4)
								{
									startmont=4;
								}

							}


							int totalpaidamount=0;
							totalpaidamount=DBJ.FeePaidRecord(sList,preSession,rr.getFeeId(),conn,schid);


							if(schoolInfo.getSchoolSession().equals("4-3"))
							{
								for(int i=startmont;i<=4;i++)
								{
									String month="";

									if(i==1)
									{
										month=DBJ.monthList(4)+"-"+DBJ.monthList(6);
									}
									else if(i==2)
									{

										month=DBJ.monthList(7)+"-"+DBJ.monthList(9);
									}
									else if(i==3)
									{
										month=DBJ.monthList(10)+"-"+DBJ.monthList(12);
									}
									else if(i==4)
									{
										month=DBJ.monthList(1)+"-"+DBJ.monthList(3);
									}


									int fee=rr.getFeeAmount();

									if(fee<=totalpaidamount)
									{


										FeeInfo info=new FeeInfo();

										info.setFeePeriod(month);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(fee);
										info.setTotalFeeLeftAmount(0);
										feeStructureList.add(info);
										totalpaidamount=totalpaidamount-fee;
									}
									else
									{

										int leftamount=fee-totalpaidamount;
										FeeInfo info=new FeeInfo();
										info.setFeePeriod(month);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(totalpaidamount);
										info.setTotalFeeLeftAmount(leftamount);
										feeStructureList.add(info);
										totalpaidamount=0;
									}
								}
							}
							else
							{
								for(int i=startmont;i<=4;i++)
								{
									String month="";

									if(i==1)
									{
										month=DBJ.monthList(5)+"-"+DBJ.monthList(7);
									}
									else if(i==2)
									{

										month=DBJ.monthList(8)+"-"+DBJ.monthList(10);
									}
									else if(i==3)
									{
										month=DBJ.monthList(11)+"-"+DBJ.monthList(1);
									}
									else if(i==4)
									{
										month=DBJ.monthList(2)+"-"+DBJ.monthList(4);
									}


									int fee=rr.getFeeAmount();

									if(fee<=totalpaidamount)
									{
										FeeInfo info=new FeeInfo();

										info.setFeePeriod(month);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(fee);
										info.setTotalFeeLeftAmount(0);
										feeStructureList.add(info);
										totalpaidamount=totalpaidamount-fee;
									}
									else
									{

										int leftamount=fee-totalpaidamount;
										FeeInfo info=new FeeInfo();
										info.setFeePeriod(month);
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(totalpaidamount);
										info.setTotalFeeLeftAmount(leftamount);
										feeStructureList.add(info);
										totalpaidamount=0;
									}
								}
							}
						}
					}
				}

				for(FeeInfo rr:classFeeList)
				{
					if(rr.getFeeName().equals("Previous Fee"))
					{
						int fee=rr.getFeeAmount();
						int feepaidAmount=DBM.FeePaidRecord(schid,sList, preSession, rr.getFeeId(), conn);
						//int feepaidAmount=DBJ.previousFeePaidRecord(sList.getAddNumber(),conn,schid);
						int leftamount=fee-feepaidAmount;
						FeeInfo info=new FeeInfo();
						info.setFeeName(rr.getFeeName());
						info.setFeePeriod("-");
						info.setTotalFeeAmount(fee);
						info.setTotalFeePaidAmount(feepaidAmount);
						info.setTotalFeeLeftAmount(leftamount);
						feeStructureList.add(info);
						fee=feepaidAmount=leftamount=0;

					}
				}


				int transportFeePaid=DBJ.FeePaidRecord(sList,preSession,"0",conn,schid);  //already paid student transport fee

				transportfeeStatus=new ArrayList<>();
				ArrayList<Transport> transportFeeDetails=DBJ.transportListDetails(sList.getAddNumber(),preSession,conn,schid);

				for(Transport t: transportFeeDetails)
				{
					FeeStatus fs=new FeeStatus();
					fs.setTransportRouteName(DBJ.transportRouteNameFromId(String.valueOf(t.getRouteId()),preSession,schid,conn));
					fs.setMonth(t.getMonth());
					fs.setStatus(t.getStatus());

					ArrayList<ClassInfo> transportFeeList = new ArrayList<ClassInfo>();
					if(t.getStatus().equalsIgnoreCase("Yes") && t.getRouteId()!=0)
					{
						transportFeeList = DBJ.transportRouteDetailsWithFee(
								t.getRouteId(), preSession, conn, schid);
					}
					for(int j=0;j<transportFeeList.size();j++)
					{
						ClassInfo info1=transportFeeList.get(j);
						if(info1.getMonth() <= t.getMonthInt())
						{

							if(j == transportFeeList.size()-1)
							{

								fs.setTransportFee(String.valueOf(info1.getTransportFee()-Integer.parseInt(sList.getDiscountfee())));
								transportfeeStatus.add(fs);

								break;
							}
							else if(transportFeeList.get(j+1).getMonth() > t.getMonthInt())
							{
								fs.setTransportFee(String.valueOf(info1.getTransportFee()-Integer.parseInt(sList.getDiscountfee())));
								transportfeeStatus.add(fs);

								break;
							}
						}
					}
				}

				flag=0;
				transportFeeLeft=0;
				int transportFee=0;
				for(FeeStatus info : transportfeeStatus)
				{

					transportFee=Integer.parseInt(info.getTransportFee());
					if(transportFeePaid >= transportFee)
					{
						info.setStatus(String.valueOf(transportFee));
						info.setBalanceLeft("0");

						transportFeePaid-=transportFee;
					}
					else
					{
						if(transportFeePaid>0)
						{
							info.setStatus(String.valueOf(transportFeePaid)+" paid");
							info.setBalanceLeft(String.valueOf(transportFee-transportFeePaid)+" left");

							firstMonthTransportPeriod=info.getMonth();

							transportFeeLeft+=transportFee-transportFeePaid;
							transportFeePaid=0;
						}
						else
						{
							if(flag==0)
							{
								info.setStatus("0 paid");
								info.setBalanceLeft(String.valueOf(transportFee)+" left");

								firstMonthTransportPeriod=info.getMonth();
							}
							else
							{
								info.setStatus("0");
								info.setBalanceLeft(String.valueOf(transportFee));
							}
							transportFeeLeft+=transportFee;
						}
						flag++;
					}
				}

				submit(schid, preSession);
			}
			else
			{
				JSONObject obj = new JSONObject();
				obj.put("status","Something Went Wrong. Please Try Again!");

				json=obj.toJSONString();
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
		
		
	}

	public void submit(String schoolid,String preSession)
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try
		{
			int amoutnt=0;
			boolean flag=false,flag1=false;
			int i=0;
			int balLeft=0;
			for(FeeInfo ff:selectedFees )
			{

				if(ff.getPayAmount()>0 || ff.getPayDiscount()>0)
				{
					int totalfee=0;
					int totalFeepaid=0;
					if(ff.getFeeName().equals("Transport"))
					{
						totalFeepaid=DBJ.FeePaidRecord(sList,preSession,"0",conn,schoolid);
						for(FeeStatus nn:transportfeeStatus)
						{
							totalfee+=Integer.parseInt(nn.getTransportFee());
						}
					}
					if(ff.getFeeName().equals("Previous Fee"))
					{
						totalFeepaid=DBM.FeePaidRecord(schoolid,sList,preSession,ff.getFeeId(),conn);
						for(FeeInfo ss:feeStructureList)
						{
							if(ss.getFeeName().equals(ff.getFeeName()))
							{
								totalfee+=ss.getTotalFeeAmount();
							}
						}
					}
					else
					{
						totalFeepaid=DBJ.FeePaidRecord(sList,preSession,ff.getFeeId(),conn,schoolid);
						for(FeeInfo ss:feeStructureList)
						{
							if(ss.getFeeName().equals(ff.getFeeName()))
							{
								totalfee+=ss.getTotalFeeAmount();
							}
						}
					}


					if(!ff.getFeeName().equals("Late Fee"))
					{
						if(ff.getPayAmount()+ff.getPayDiscount()>totalfee-totalFeepaid)
						{
							flag1=true;
							balLeft=totalfee-totalFeepaid;
							break;
						}
						else
						{
							flag1=false;
						}

					}
				}
				else
				{
					flag=true;

					break;
				}
				i=i+1;
			}



			if(flag==true)
			{
				String feename=selectedFees.get(i).getFeeName();

				JSONObject obj = new JSONObject();
				obj.put("status",feename+" must Be Greater Than Zero");

				json=obj.toJSONString();

				//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(feename+" must Be Greater Than Zero"));
				//return "";
			}
			else
			{
				if(flag1==true)
				{
					String feename=selectedFees.get(i).getFeeName();

					JSONObject obj = new JSONObject();
					obj.put("status",feename + " Only "+balLeft+" Rs. Left");

					json=obj.toJSONString();
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(feename + " Only "+balLeft+" Rs. Left"));
					//return "";
				}
				else
				{
					//new DatabaseMethods1().insertfee(name,className);
					int	number=DBJ.feeserailno(conn,schoolid);
					String num="";
					if(String.valueOf(number).length()==1)
					{
						num="0000"+String.valueOf(number);
					}
					else if(String.valueOf(number).length()==2)
					{
						num="000"+String.valueOf(number);
					}
					else if(String.valueOf(number).length()==3)
					{
						num="00"+String.valueOf(number);
					}
					else if(String.valueOf(number).length()==4)
					{
						num="0"+String.valueOf(number);
					}
					else if(String.valueOf(number).length()==5)
					{
						num=String.valueOf(number);
					}

					int ii=0;
					for(FeeInfo ff:selectedFees)
					{
						ii=DBJ.submitFee(sList,ff.getPayAmount(),ff.getFeeId(),paymentMode,bankName,chequeNumber
								,num,ff.getPayDiscount(),preSession,recipietDate,challanNo,neftNo,challanDate,neftDate,conn,schoolid);
						/*if(ii>=1 && ff.getFeeName().equals("Previous Fee"))
						{
							DBJ.updatePaidAmountOfPreviousFee(sList.getAddNumber(),(ff.getPayAmount()+ff.getPayDiscount()),conn,schoolid);
						}*/
						amoutnt+=ff.getPayAmount();
					}

					if(ii>=1)
					{
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Added Successfully"));

						SchoolInfoList info=DBJ.fullSchoolInfo(schoolid,conn);
						String typeMessage="Dear Parent, \n\nReceived payment of Rs."+amoutnt+" in favour of fee by "+paymentMode+" via Receipt No. "+num+" .Cheque payments are subject to clearance. \n\nRegards, \n"+info.getSchoolName();

						if(info.getCtype().equalsIgnoreCase("foster") || info.getCtype().equalsIgnoreCase("fosterCBSE"))
						{
							if(paymentMode.equalsIgnoreCase("Cash"))
							{
								DBJ.increaseCompanyCapitalAmount(Double.valueOf(amoutnt),conn,schoolid);
							}
						}


						if(message.equals("true"))
						{
							////// // System.out.println("adm : "+sList.getAddNumber());
							if (String.valueOf(sList.getFathersPhone()).length() == 10
									&& !String.valueOf(sList.getFathersPhone()).equals("2222222222")
									&& !String.valueOf(sList.getFathersPhone()).equals("9999999999")
									&& !String.valueOf(sList.getFathersPhone()).equals("1111111111")
									&& !String.valueOf(sList.getFathersPhone()).equals("1234567890")
									&& !String.valueOf(sList.getFathersPhone()).equals("0123456789"))
							{
								DBJ.messageurl1(String.valueOf(sList.getFathersPhone()), typeMessage,sList.getAddNumber(),schoolid,conn);
							}
						}

						DBJ.notification("Fees", typeMessage,String.valueOf(sList.getFathersPhone())+"-"+sList.getAddNumber()+"-"+schoolid,schoolid,"",conn);
						DBJ.notification("Fees", typeMessage,String.valueOf(sList.getMothersPhone())+"-"+sList.getAddNumber()+"-"+schoolid,schoolid,"",conn);

						JSONObject obj = new JSONObject();
						obj.put("status","Fees Added Successfully");

						json=obj.toJSONString();
						/*
						HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
						rr.setAttribute("type1", "origanal");
						rr.setAttribute("paymentmode", paymentMode);
						rr.setAttribute("bankname", bankName);
						rr.setAttribute("chequeno", chequeNumber);
						rr.setAttribute("receiptNumber", num);
						rr.setAttribute("selectedFee", selectedFees);
						rr.setAttribute("receiptDate", recipietDate);
						rr.setAttribute("chaqueDate", challanDate);

						if(!schoolid.equals("206"))
						{
							PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

						}

						return "studentFeeCollection";*/
					}
					else
					{
						JSONObject obj = new JSONObject();
						obj.put("status","Something Went Wrong. Please Try Again!");

						json=obj.toJSONString();
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select At Least One Fee Type"));
					}
				}
			}
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//return "";
	}



	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
