package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeDynamicList;
import schooldata.FeeInfo;
import schooldata.FeeStatus;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.Transport;

@ManagedBean(name="pendingFeesPaymentJsonBean")
@ViewScoped
public class PendingFeesPaymentJsonBean implements Serializable {


	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo,studentStatus;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	int flag,transportFeeLeft;
	ArrayList<FeeStatus> transportfeeStatus;
	String firstMonthTransportPeriod,lastMonthTransportPeriod;
	String schoolid="";
	ArrayList<FeeInfo> list = new ArrayList<>();
	String studentiduser="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public PendingFeesPaymentJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid[] = params.get("studentid").split("/");
			studentiduser=studentid[0];
			 schoolid= params.get("Schoolid");
			String type=params.get("type");
			
			
			if(type==null||type.equals(""))
			{
				type="old";
			}
			
			
			
			
			JSONArray arr=new JSONArray();
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				
				if(type.equals("old"))
				{

					String ii="";
					for(int i=0;i<studentid.length-1;i++)
					{
						if(ii.equals(""))
						{
							ii=studentid[i];
						}
						else
						{
							ii=ii+"/"+studentid[i];
						}
					}
					
					StudentInfo ss=DBJ.studentDetailslistByAddNo(ii,schoolid,conn);
					DateFormat df = new SimpleDateFormat("MM");


					SchoolInfoList schoolInfo=DBJ.fullSchoolInfo(schoolid,conn);
					/*     ArrayList<StudentInfo>studentList=DBJ.searchStudentListForDueFeeReport(ss.getAddmisssionNumber(),new Date()
			        		  ,DBM.selectedSessionDetails(),conn,schoolid);

			          double dueAmount=0;

			          if(studentList.size()>0)
			          {
			        	  HashMap<String,String>map=(HashMap<String, String>) studentList.get(0).getFeesMap();

			      		ArrayList<FeeInfo>classFeeList1=DBJ.classFeesForStudentforjson(ss.getClassId(),
			      				DBM.selectedSessionDetails(),ss.getStudentStatus(),ss.getConcession(),schoolid,conn);
			      		classFeeList1=DBJ.addPreviousFee(classFeeList1,ss.getAddmisssionNumber(),schoolid,conn);

			          for(FeeInfo ls:classFeeList1)
			      		{

			        	  if(!ls.getFeeName().equals("Late Fee"))
			        	  {
			        		  dueAmount+=Integer.parseInt(map.get(ls.getFeeName()));

			        	  }
			      		 }

			          }

					 */
					studentStatus=ss.getStudentStatus();
					String reportDate = df.format(ss.getStartingDate());
					int startingMonth=0;

					int startSessionMonth=Integer.valueOf(schoolInfo.getSchoolSession().split("-")[0]);
					int endSessionMonth=Integer.valueOf(schoolInfo.getSchoolSession().split("-")[1])+12;

					int session=DBJ.getpromostionclass(String.valueOf(ss.getAddNumber()),conn,schoolid);
					if(session==1||studentStatus.equalsIgnoreCase("old"))
					{
						/*if(schoolInfo.getSchoolSession().equals("4-3"))
					{
						startingMonth=4;
					}
					else
					{*/
						startingMonth=startSessionMonth;
						//}
					}
					else
					{
						if(schoolInfo.getFeeStart().equalsIgnoreCase("session_date"))
						{
							/*if(schoolInfo.getSchoolSession().equals("4-3"))
						{
							startingMonth=4;
						}
						else
						{
							startingMonth=5;
						}*/

							startingMonth=startSessionMonth;
						}
						else
						{
							startingMonth=Integer.parseInt(reportDate);
						}
					}

					ArrayList<FeeInfo>classFeeList=new DatabaseMethods1(schoolid).classFeesForStudentforjson(ss.getClassId(),
							DBM.selectedSessionDetails(schoolid,conn),ss.getStudentStatus(),ss.getConcession(),schoolid,conn);

					classFeeList=DBM.addPreviousFee(schoolid,classFeeList,ss.getAddmisssionNumber(),conn);

					ArrayList<FeeInfo> feeStructureList=new ArrayList<>();
					for(FeeInfo rr:classFeeList)
					{
						if(!rr.getFeeName().equals("Transport") && !rr.getFeeName().equals("Late Fee") && !rr.getFeeName().equals("Previous Fee"))
						{
							if(rr.getFeeType().equals("year"))
							{
								int fee = 0;
								if(rr.getFeeCheckType().equals("studentWise"))
								{
									fee=DBJ.viewStudentWiseFee(ss.getAddNumber(),rr.getFeeId(),rr.getFeeMonth(),schoolid,conn);
								}
								else
								{
									fee = rr.getFeeAmount();
								}

								int feepaidAmount=DBJ.FeePaidRecord(ss,DatabaseMethods1.selectedSessionDetails(schoolid,conn),rr.getFeeId(),schoolid,conn);
								int leftamount=fee-feepaidAmount;
								FeeInfo info=new FeeInfo();
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
								info.setFeeType(rr.getFeeType());
								info.setTotalFeePaidAmount(feepaidAmount);
								info.setTotalFeeLeftAmount(leftamount);
								feeStructureList.add(info);
								fee=feepaidAmount=leftamount=0;
							}
							else if(rr.getFeeType().equals("month"))
							{
								int totalpaidamount=0;
								totalpaidamount=DBJ.FeePaidRecord(ss,DBM.selectedSessionDetails(schoolid,conn),rr.getFeeId(),schoolid,conn);
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

								for(int i=monthly;i<=endSessionMonth;i++)
								{
									int month = i;
									if(i>12)
										month=i-12;

									int fee = 0;
									if(rr.getFeeCheckType().equals("studentWise"))
									{
										fee=DBJ.viewStudentWiseFee(ss.getAddNumber(),rr.getFeeId(),String.valueOf(i),schoolid,conn);
									}
									else
									{
										fee = rr.getFeeAmount();
									}

									if(fee<=totalpaidamount)
									{
										FeeInfo info=new FeeInfo();
										String month1=DBJ.monthList(month);
										info.setFeePeriod(month1);
										info.setFeeMonth(rr.getFeeMonth());
										info.setFeeMonth(String.valueOf(month));
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(fee);
										info.setFeeType(rr.getFeeType());
										info.setTotalFeeLeftAmount(0);
										feeStructureList.add(info);
										totalpaidamount=totalpaidamount-fee;
									}
									else
									{
										int leftamount=fee-totalpaidamount;
										FeeInfo info=new FeeInfo();
										info.setFeePeriod(DBJ.monthList(month));
										info.setFeeMonth(String.valueOf(month));
										info.setFeeName(rr.getFeeName());
										info.setTotalFeeAmount(fee);
										info.setTotalFeePaidAmount(totalpaidamount);
										info.setTotalFeeLeftAmount(leftamount);
										info.setFeeType(rr.getFeeType());
										feeStructureList.add(info);
										totalpaidamount=0;
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
								totalpaidamount=DBJ.FeePaidRecord(ss,DBM.selectedSessionDetails(schoolid,conn),rr.getFeeId(),schoolid,conn);

								if(schoolInfo.getSchoolSession().equals("4-3"))
								{
									for(int i=startmont;i<=4;i++)
									{
										String month="";
										String mn="";

										if(i==1)
										{
											month=DBJ.monthList(4)+"-"+DBJ.monthList(6);
											mn="4";
										}
										else if(i==2)
										{

											month=DBJ.monthList(7)+"-"+DBJ.monthList(9);
											mn="7";
										}
										else if(i==3)
										{
											month=DBJ.monthList(10)+"-"+DBJ.monthList(12);
											mn="10";
										}
										else if(i==4)
										{
											month=DBJ.monthList(1)+"-"+DBJ.monthList(3);
											mn="1";
										}


										int fee = 0;
										if(rr.getFeeCheckType().equals("studentWise"))
										{
											fee=DBJ.viewStudentWiseFee(ss.getAddNumber(),rr.getFeeId(),String.valueOf(i),schoolid,conn);
										}
										else
										{
											fee = rr.getFeeAmount();
										}
										if(fee<=totalpaidamount)
										{
											FeeInfo info=new FeeInfo();

											info.setFeePeriod(month);
											info.setFeeName(rr.getFeeName());
											info.setFeeType(rr.getFeeType());
											info.setTotalFeeAmount(fee);
											info.setTotalFeePaidAmount(fee);
											info.setFeeMonth(mn);
											info.setTotalFeeLeftAmount(0);
											feeStructureList.add(info);
											totalpaidamount=totalpaidamount-fee;
										}
										else
										{

											int leftamount=fee-totalpaidamount;
											FeeInfo info=new FeeInfo();
											info.setFeePeriod(month);
											info.setFeeType(rr.getFeeType());
											info.setFeeName(rr.getFeeName());
											info.setFeeMonth(mn);
											info.setTotalFeeAmount(fee);
											info.setTotalFeePaidAmount(totalpaidamount);
											info.setTotalFeeLeftAmount(leftamount);
											feeStructureList.add(info);
											totalpaidamount = 0;
										}
									}
								}
								else
								{
									for(int i=startmont;i<=4;i++)
									{
										String month="";
										String mn="";

										if(i==1)
										{
											month=DBJ.monthList(5)+"-"+DBJ.monthList(7);
											mn="5";
										}
										else if(i==2)
										{

											month=DBJ.monthList(8)+"-"+DBJ.monthList(10);
											mn="8";
										}
										else if(i==3)
										{
											month=DBJ.monthList(11)+"-"+DBJ.monthList(1);
											mn="11";
										}
										else if(i==4)
										{
											month=DBJ.monthList(2)+"-"+DBJ.monthList(4);
											mn="2";
										}

										int fee = 0;
										if(rr.getFeeCheckType().equals("studentWise"))
										{
											fee=DBJ.viewStudentWiseFee(ss.getAddNumber(),rr.getFeeId(),String.valueOf(i),schoolid,conn);
										}
										else
										{
											fee = rr.getFeeAmount();
										}
										if(fee<=totalpaidamount)
										{
											FeeInfo info=new FeeInfo();

											info.setFeePeriod(month);
											info.setFeeName(rr.getFeeName());
											info.setFeeType(rr.getFeeType());
											info.setFeeMonth(mn);

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
											info.setFeeType(rr.getFeeType());
											info.setFeeName(rr.getFeeName());
											info.setFeeMonth(mn);

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
							int totalpaidamount=0;
							totalpaidamount=DBJ.FeePaidRecord(ss,DBM.selectedSessionDetails(schoolid,conn),rr.getFeeId(),schoolid,conn);
							//int feepaidAmount = DBM.previousFeePaidRecord(sList.getAddNumber(), conn);
							int leftamount = fee - totalpaidamount;
							FeeInfo info = new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setFeePeriod("-");
							info.setFeeMonth("4");
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(totalpaidamount);
							info.setTotalFeeLeftAmount(leftamount);
							feeStructureList.add(info);

						}
					}


					int transportFeePaid=DBJ.FeePaidRecord(ss,DBM.selectedSessionDetails(schoolid,conn),"0",schoolid,conn);  //already paid student transport fee

					transportfeeStatus=new ArrayList<>();
					ArrayList<Transport> transportFeeDetails=DBJ.transportListDetails11(ss.getAddNumber(),
							DBM.selectedSessionDetails(schoolid,conn),schoolid,conn);
					for(Transport t: transportFeeDetails)
					{
						FeeStatus fs=new FeeStatus();
						fs.setTransportRouteName(DBJ.transportRouteNameFromId(String.valueOf(t.getRouteId()),
								DBM.selectedSessionDetails(schoolid,conn),schoolid,conn));
						fs.setMonth(t.getMonth());
						fs.setFeeMonth(t.getFeeMonth());
						fs.setStatus(t.getStatus());

						ArrayList<ClassInfo> transportFeeList = new ArrayList<ClassInfo>();
						if(t.getStatus().equalsIgnoreCase("Yes") && t.getRouteId()!=0)
						{
							transportFeeList = DBJ.transportRouteDetailsWithFee(
									t.getRouteId(), DBM.selectedSessionDetails(schoolid, conn),schoolid, conn);
						}
						//int transportConcession=DBM.transportConcession(ss.getAddNumber(), ss.getClassId());
						for(ClassInfo ll:transportFeeList)
						{
							ll.setTransportFee(ll.getTransportFee());
						}
						for(int j=0;j<transportFeeList.size();j++)
						{
							ClassInfo info1=transportFeeList.get(j);
							if(info1.getMonth() <= t.getMonthInt())
							{
								if(j == transportFeeList.size()-1)
								{
									fs.setTransportFee(String.valueOf(info1.getTransportFee()- ss.getDiscountFees()));
									transportfeeStatus.add(fs);

									break;
								}
								else if(transportFeeList.get(j+1).getMonth() > t.getMonthInt())
								{
									fs.setTransportFee(String.valueOf(info1.getTransportFee()- ss.getDiscountFees()));
									transportfeeStatus.add(fs);

									break;
								}
							}
						}
					}
					flag=0;
					transportFeeLeft=0;
					for(FeeStatus info : transportfeeStatus)
					{
						int transportFee=(int)Double.parseDouble(info.getTransportFee());
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
								info.setStatus(String.valueOf(transportFeePaid));
								info.setBalanceLeft(String.valueOf(transportFee-transportFeePaid));

								firstMonthTransportPeriod=info.getMonth();

								transportFeeLeft+=transportFee-transportFeePaid;
								transportFeePaid=0;
							}
							else
							{
								if(flag==0)
								{
									info.setStatus("0");
									info.setBalanceLeft(String.valueOf(transportFee));

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

					ArrayList<FeeInfo>ls=new ArrayList<>();
					for(int i=4;i<=15;i++)
					{
						FeeInfo lss=new FeeInfo();
						if(i>12)
						{
							lss.setFeeMonth(DBJ.monthList(i-12));
							lss.setFeeMonthInt(i-12);
						}
						else
						{
							lss.setFeeMonth(DBJ.monthList(i));
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
					for(FeeInfo lst:ls)
					{
						JSONObject obj = new JSONObject();
						obj.put("feemonth", lst.getFeeMonth());
						obj.put("month", String.valueOf(lst.getFeeMonthInt()));
						obj.put("feeLeftAmount", String.valueOf(lst.getAmount()));
						obj.put("totalFee", String.valueOf(lst.getTotalFee()));
						obj.put("totalPaid", String.valueOf(lst.getTotalFeePaidAmount()));

						if(lst.getAmount()>0)
						{
							arr.add(obj);
						}
					}


				}
				else
				{
					
					try {
						lateFeeCalculation();
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					for(FeeInfo lst:list)
					{
					
						
						JSONObject obj = new JSONObject();
						obj.put("feemonth", lst.getFeeMonth());
						obj.put("month", String.valueOf(lst.getFeeMonthInt()));
						obj.put("feeLeftAmount", String.valueOf(lst.getAmount()));
						obj.put("totalFee", String.valueOf(lst.getTotalFee()));
						obj.put("totalPaid", String.valueOf(lst.getTotalFeePaidAmount()));

						if(lst.getAmount()>0)
						{
							arr.add(obj);
						}

					}
				}
				

				
			}

			json=arr.toJSONString();	
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

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
	
	public int lateFeeCalculation() throws ParseException
	{
		double previousDueAmount=0; int totalLateFee=0;
		Connection conn= DataBaseConnection.javaConnection();
		try {
			DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
			DatabaseMethods1 DBM=new DatabaseMethods1();
			Date recipietDate=new Date();
			list = new ArrayList<>();
			String addmissionNumber=studentiduser;
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

				double dueAmount = 0,actuladueAmount=0,maintotalamount=0,mainpaidAmount=0;
				
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
						mainpaidAmount=mainpaidAmount+totalpaidamount;
						maintotalamount=maintotalamount+mainamount;
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

					classFeeList = DBM.classFeesForStudent11(schoolid,sList.getClassId(), preSession, studentStatus, connsessioncategory,
							conn);
					//classFeeList = DBM.addPreviousFee(schoolid,classFeeList, addmissionNumber, conn);
					
					for (FeeInfo ls : classFeeList) {
						if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
							int mainamount=Integer.parseInt(map.get(ls.getFeeName()));
							if(Integer.parseInt(ls.getTax_percn())>0)
							{
								mainamount=mainamount+((mainamount*Integer.parseInt(ls.getTax_percn()))/100);
							}
	  
							int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),feelist.get(i).getInsatllmentValue(),conn);
							mainpaidAmount=mainpaidAmount+totalpaidamount;
							maintotalamount=maintotalamount+mainamount;
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

				if(actuladueAmount>0)
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
	                ff.setTotalFee(maintotalamount+latefee);
				    ff.setTotalFeePaidAmount((int)(mainpaidAmount));
				    ff.setAmount(ff.getAmount()+latefee);
				//	ff.setTotalFee(ff.getAmount()+latefee);
					list.add(ff);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return totalLateFee;
	}
}
