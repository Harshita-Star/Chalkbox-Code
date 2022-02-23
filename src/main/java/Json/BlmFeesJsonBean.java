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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.StudentInfo;

@ManagedBean(name="blmFeesJson")
@ViewScoped
public class BlmFeesJsonBean implements Serializable
{
	String json="";

	int month;
	boolean waveOffLateFee=true;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();

	public BlmFeesJsonBean()
	{

		Connection conn= DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONArray arr=new JSONArray();
			
			if(checkRequest)
			{
				String[] feemonths= {"4","6","8","9","11","12","1","3","-1"};
				String[] allfeemonths= {"APRIL","May-June","Jul-Aug","September","Oct-Nov","December","January","Feb-Mar","Previous_Fee"};
				String addmissionNumber=params.get("studentId");
				String schoolId=params.get("schid");
				String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				String preSession=DBM.selectedSessionDetails(schoolId,conn);
				StudentInfo sList=DBJ.studentDetailslistByAddNo(addmissionNumber, schoolId, conn);
				String studentStatus = sList.getStudentStatus();
				String connsessioncategory = sList.getConcession();
				ArrayList<FeeInfo> classFeeList;
				
				String[] session=preSession.split("-");
				double previousDueAmount=0;
				for(int i=0;i<feemonths.length;i++)
				{

					double dueAmount = 0,actuladueAmount=0;
					String findDate1="";
					if(Integer.parseInt(feemonths[i])>3)
					{
						findDate1="01/"+feemonths[i]+"/"+session[0];
					}
					else
					{
						findDate1="01/"+feemonths[i]+"/"+session[1];
					}


					if(i > 4)
					{
						month = Integer.parseInt(feemonths[i])+12;
					}
					else
					{
						month = Integer.parseInt(feemonths[i]);
					}


					Date cDate=null;
					try {
						new SimpleDateFormat("dd/MM/yyyy").parse(findDate1);
						cDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					if(i==8)
					{
						classFeeList=new ArrayList<>();
						classFeeList = DBM.addPreviousFee(schoolId,classFeeList, addmissionNumber, conn);

						for(FeeInfo lss:classFeeList)
						{
							if(lss.getFeeId().equals("-1"))
							{
								int mainamount=lss.getFeeAmount();
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, lss.getFeeId(),feemonths[i],conn);
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
						int count=1;
						if(feemonths[i].equals("6")||feemonths[i].equals("8")||feemonths[i].equals("11")||feemonths[i].equals("3"))
						{
							count=2;
						}




						ArrayList<StudentInfo> studentList = DBM.searchStudentListForDueFeeReportForMasterForFees(schoolId,addmissionNumber, Integer.parseInt(feemonths[i]),
								preSession, conn, "feeCollection",count);

						HashMap<String, String> map = (HashMap<String, String>) studentList.get(0).getFeesMap();

						classFeeList = DBM.classFeesForStudent(schoolId,sList.getClassId(), preSession, studentStatus, connsessioncategory,
								conn);
						//classFeeList = DBM.addPreviousFee(schoolId,classFeeList, addmissionNumber, conn);
						
						for (FeeInfo ls : classFeeList) {
							if (!ls.getFeeId().equals("-2") && !ls.getFeeId().equals("-3") && !ls.getFeeId().equals("-4")) {
								int mainamount=Integer.parseInt(map.get(ls.getFeeName()));
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, ls.getFeeId(),feemonths[i],conn);
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
					ff.setFeeMonth(allfeemonths[i]);
					ff.setFeeMonthInt(Integer.parseInt(feemonths[i]));
					//		 obj.put("amount",String.valueOf(actuladueAmount));
					//		 obj.put("Fee_month",allfeemonths[i]);

					int latefee=0;
					if(actuladueAmount>0)
					{
						JSONObject ss=new JSONObject();

						if(waveOffLateFee==false)
						{
							if(i==0)
							{
								try {


									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("30/04/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									if(duedate.before(cDate) && month>=4)
									{
										latefee=200;
									}
								} catch (ParseException e) {
									
									e.printStackTrace();
								}
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==1)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/05/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==2)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/07/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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

								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==3)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/09/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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

								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==4)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/10/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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

								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==5)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/12/"+session[0]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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
								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==6)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/01/"+session[1]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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

								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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
							else if(i==7)
							{
								try {
									Date duedate=new SimpleDateFormat("dd/MM/yyyy").parse("10/02/"+session[1]);
									//obj.put("dueDate",new SimpleDateFormat("dd/MM/yyyy").format(duedate));
									ff.setStdate(new SimpleDateFormat("dd/MM/yyyy").format(duedate));
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

								int totalpaidamount = DBM.FeePaidRecordForCheck(sList.getSchid(),sList, preSession, "-2",feemonths[i],conn);
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

						}

						//totalLateFee += latefee;

						//ff.setLateFee(latefee);
						ff.setTotalFee(ff.getAmount()+latefee);

						//  list.add(ff);

						ss.put("amount",String.valueOf(ff.getAmount()));
						ss.put("Fee_month",allfeemonths[i] );
						ss.put("dueDate","");
						ss.put("latefee", latefee);

						arr.add(ss);


					}


				}
			}
			
			json=arr.toString();

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


	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}

}
