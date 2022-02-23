package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

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
import schooldata.FeeInfo;
import schooldata.FeeStatus;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.Transport;
@ManagedBean(name="feeViewjson")
@ViewScoped
public class FeeViewJson implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo,studentStatus;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	StudentInfo selectedStudent, selectedAss;
	int flag,transportFeeLeft;
	ArrayList<FeeStatus> transportfeeStatus;
	String firstMonthTransportPeriod,lastMonthTransportPeriod;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public FeeViewJson(){

		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String studentid[] = params.get("studentid").split("/");
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
				String schoolid= params.get("Schoolid");
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
					if(!rr.getFeeName().equals("Transport") && !rr.getFeeName().equals("Late Fee")&& !rr.getFeeName().equals("Previous Fee"))
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

							int feepaidAmount=DBJ.FeePaidRecord(ss,DBM.selectedSessionDetails(schoolid,conn),rr.getFeeId(),schoolid,conn);
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
										mn="13";
									}


									int fee = 0;
									if(rr.getFeeCheckType().equals("studentWise"))
									{
										fee=DBJ.viewStudentWiseFee(ss.getAddNumber(),rr.getFeeId(),mn,schoolid,conn);
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

				int transportFeePaid=DBJ.FeePaidRecord(ss,DatabaseMethods1.selectedSessionDetails(schoolid,conn),"0",schoolid,conn);  //already paid student transport fee

				transportfeeStatus=new ArrayList<>();
				ArrayList<Transport> transportFeeDetails=DBJ.transportListDetails11(ss.getAddNumber(),
						DBM.selectedSessionDetails(schoolid,conn),schoolid,conn);
				for(Transport t: transportFeeDetails)
				{
					FeeStatus fs=new FeeStatus();
					fs.setTransportRouteName(DBJ.transportRouteNameFromId(String.valueOf(t.getRouteId()),
							DBM.selectedSessionDetails(schoolid,conn),schoolid,conn));
					fs.setMonth(t.getMonth());
					fs.setStatus(t.getStatus());

					ArrayList<ClassInfo> transportFeeList = new ArrayList<ClassInfo>();
					if(t.getStatus().equalsIgnoreCase("Yes") && t.getRouteId()!=0)
					{
						transportFeeList = DBJ.transportRouteDetailsWithFee(
								t.getRouteId(), DBM.selectedSessionDetails(schoolid,conn),schoolid, conn);
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
				
				int i=0;
				for(FeeInfo ss1:feeStructureList)
				{
					JSONObject obj = new JSONObject();
					obj.put("Feename", ss1.getFeeName());
					obj.put("Feemonth", ss1.getFeePeriod());
					if(userAgent==null)
					{
						obj.put("FeeAmount", String.valueOf(ss1.getTotalFeeAmount()));
						obj.put("FeepaidAmount", String.valueOf(ss1.getTotalFeePaidAmount()));
						obj.put("FeeLeftAmount", String.valueOf(ss1.getTotalFeeLeftAmount()));
					}
					else if(userAgent.toLowerCase().contains("schooladmin/"))
					{
						obj.put("FeeAmount", ss1.getTotalFeeAmount());
						obj.put("FeepaidAmount",ss1.getTotalFeePaidAmount());
						obj.put("FeeLeftAmount",ss1.getTotalFeeLeftAmount());
					}
					else
					{
						obj.put("FeeAmount", String.valueOf(ss1.getTotalFeeAmount()));
						obj.put("FeepaidAmount", String.valueOf(ss1.getTotalFeePaidAmount()));
						obj.put("FeeLeftAmount", String.valueOf(ss1.getTotalFeeLeftAmount()));
					}
					
					
					
					 // System.out.println(ss1.getFeePeriod());
					
					/*if(schoolid.equals("299")||schoolid.equals("298"))
					{
						if(ss1.getFeePeriod().equalsIgnoreCase("April")||ss1.getFeePeriod().equalsIgnoreCase("May")||ss1.getFeePeriod().equalsIgnoreCase("June")||ss1.getFeePeriod().equalsIgnoreCase("July"))
						{
							
						}
						else
						{
							if(i==0)
							{
								obj.put("upcomingamount",0);
								i++;
							}
							arr.add(obj);
						}
					}
					else
					{*/
						if(i==0)
						{
							obj.put("upcomingamount",0);
							i++;
						}
						arr.add(obj);
									
						/* } */
					
				}

				if(!schoolid.equals("1"))
				{
					for(FeeStatus ss1:transportfeeStatus)
					{
						JSONObject obj = new JSONObject();
						obj.put("Feename", ss1.getTransportRouteName());
						obj.put("Feemonth", ss1.getMonth());
						if(userAgent==null)
						{
							obj.put("FeeAmount", String.valueOf((int)Double.parseDouble(ss1.getTransportFee())));
							obj.put("FeepaidAmount",String.valueOf((int)Double.parseDouble(ss1.getStatus())));
							obj.put("FeeLeftAmount",String.valueOf((int)Double.parseDouble( ss1.getBalanceLeft())));
						}
						else if(userAgent.toLowerCase().contains("schooladmin/"))
						{
							obj.put("FeeAmount", Double.parseDouble(ss1.getTransportFee()));
							obj.put("FeepaidAmount",Double.parseDouble(ss1.getStatus()));
							obj.put("FeeLeftAmount",Double.parseDouble( ss1.getBalanceLeft()));
						}
						else
						{
							obj.put("FeeAmount", String.valueOf((int)Double.parseDouble(ss1.getTransportFee())));
							obj.put("FeepaidAmount",String.valueOf((int)Double.parseDouble(ss1.getStatus())));
							obj.put("FeeLeftAmount",String.valueOf((int)Double.parseDouble( ss1.getBalanceLeft())));
						}
						

						/*
						if(schoolid.equals("299")||schoolid.equals("298"))
						{
							if(ss1.getMonth().equalsIgnoreCase("April")||ss1.getMonth().equalsIgnoreCase("May")||ss1.getMonth().equalsIgnoreCase("June")||ss1.getMonth().equalsIgnoreCase("July"))
							{
								
							}
							else
							{
								if(i==0)
								{
									obj.put("upcomingamount",0);
									i++;
								}
								arr.add(obj);
							}
						}
						else
						{*/
							if(i==0)
							{
								obj.put("upcomingamount",0);
								i++;
							}
							arr.add(obj);
										
							/* } */
					}
				}
			}

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}
}
