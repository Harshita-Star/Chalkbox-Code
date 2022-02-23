/*
package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="collectFeeHere")
@ViewScoped
public class CollectFeeHereBean implements Serializable {

	private static final long serialVersionUID = 1L;
	String name,fathersName,className,feeModuleType,sectionName, preSession,firstMonthTutionFee,lastMonthTutionFee,sno,lastMonthTutionFeeTemp,firstMonthTermFee,lastMonthTermFee,lastMonthTermFeeTemp;
	String firstMonthTransportPeriod,lastMonthTransportPeriod,feeType,gender,bankName,chequeNumber,paymentMode,lastMonthTransportPeriodTemp;
	int addmissionNumber,amount,mn,totalAmount,admissionDiscount,examinationDiscount,tutionDiscount,annualDiscount,transportDiscount,termDiscount,artDiscount,activityDiscount;
	int termFeeLeft,artFeeLeft,activityFeeLeft,tutionFeeleft,admissionFeeLeft,examinationFeeLeft,annualFeeLeft,transportFeeLeft,number,admissionAmount;
	int artAmount,termAmount,activityAmount,examinationAmount,tutionAmount,annualAmount,transportAmount;
	boolean admissionFeeType,tutionFeeType,examinationFeeType,annualFeeType,transportFeeType,activityFeeType,artFeeType,termFeeType,show,showAdmissionFee,showExaminationFee;
	boolean showAnnualFee,showTransportFee,showPaymentMode,showTutionFee,showActivityFee,showArtFee,showTermFee;
	StudentInfo sList;
	ArrayList<FeeStatus> annualFeeStatus=new ArrayList<FeeStatus>();
	ArrayList<StudentInfo> serialno;
	ArrayList<FeeStatus> termStatus=new ArrayList<>(),artFeeStatus=new ArrayList<>(),activityFeeStatus=new ArrayList<>(),
			feeStatus,examinationFeeStatus,addmissionFeeStatus,transportfeeStatus;
	Date dob;
	ArrayList<ClassInfo> classFeeList;

	public void paymentModeListener()
	{
		if(paymentMode.equals("Cheque"))
		{
			showPaymentMode=true;
		}
		else
		{
			showPaymentMode=false;
		}
	}

	public void submitDiscount() throws IOException
	{
		int flag=0;
		if(admissionDiscount>0)
		{
			if(admissionDiscount>admissionFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+admissionFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Addmission Fee","Cash",bankName,chequeNumber,"-1",admissionDiscount,preSession);
			}
		}
		if(examinationDiscount>0)
		{
			if(examinationDiscount>examinationFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+examinationFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Security Fee","Cash",bankName,chequeNumber,"-1",examinationDiscount,preSession);
			}
		}

		if(tutionDiscount>0)
		{
			if(tutionDiscount>tutionFeeleft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+tutionFeeleft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"School Fee","Cash",bankName,chequeNumber,"-1",tutionDiscount,preSession);
			}
		}

		if(annualDiscount>0)
		{
			if(annualDiscount>annualFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+annualFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Registration Fee","Cash",bankName,chequeNumber,"-1",annualDiscount,preSession);
			}
		}

		if(artDiscount>0)
		{
			if(artDiscount>artFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+artFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Art Craft Fee","Cash",bankName,chequeNumber,"-1",artDiscount,preSession);
			}
		}

		if(activityDiscount>0)
		{
			if(activityDiscount>activityFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+activityFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Activity Fee","Cash",bankName,chequeNumber,"-1",activityDiscount,preSession);
			}
		}

		if(termDiscount>0)
		{
			if(termDiscount>termFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+termFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Term Fee","Cash",bankName,chequeNumber,"-1",termDiscount,preSession);
			}
		}

		if(transportDiscount>0)
		{
			if(transportDiscount>transportFeeLeft)
			{
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Only '"+transportFeeLeft+"' Fee Left"));
			}
			else
			{
				flag=1;
				new DatabaseMethods1().submitFee(sList,0,"Transport Fee","Cash",bankName,chequeNumber,"-1",transportDiscount,preSession);
			}
		}
		if(flag==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Discount Applied Successfully"));
			FacesContext.getCurrentInstance().getExternalContext().redirect("collectFeeNow.xhtml");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("At Least One Discount Must Be Greater Than Zero"));
		}
	}

	public void submitHere()
	{
		int flag=0;
		if(examinationAmount<=0 && tutionAmount<=0 && transportAmount<=0 && admissionAmount<=0 && annualAmount<=0 && tutionAmount<=0 && artAmount<=0 && activityAmount<=0 && termAmount<=0)
		{
			flag++;
			show=false;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Amount must be greater than zero", "Validation error"));
			examinationAmount=0;
			tutionAmount=0;
			transportAmount=0;
			annualAmount=0;
			admissionAmount=0;

			activityAmount=0;
			artAmount=0;
			termAmount=0;

		}

		if(flag==0)
		{
			try
			{
				int flag2=0;
				if(examinationFeeType==true)
				{
					if(examinationAmount>examinationFeeLeft || examinationDiscount>examinationFeeLeft || (examinationAmount+examinationDiscount)>examinationFeeLeft)
					{
						if(examinationFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only "+examinationFeeLeft+" amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Security fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						examinationAmount=0;
					}

				}
				if(admissionFeeType==true)
				{
					if(admissionAmount>admissionFeeLeft || admissionDiscount>admissionFeeLeft ||(admissionAmount+admissionDiscount)>admissionFeeLeft)
					{
						if(admissionFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only "+admissionFeeLeft+" amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Admission fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						admissionAmount=0;
					}

				}
				if(annualFeeType==true)
				{
					if(annualAmount>annualFeeLeft || annualDiscount>annualFeeLeft ||(annualAmount+annualDiscount)>annualFeeLeft)
					{
						if(annualFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only "+annualFeeLeft+" amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Registration fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						annualAmount=0;
					}

				}

				if(transportFeeType==true)
				{
					if(transportAmount>transportFeeLeft || transportDiscount>transportFeeLeft || (transportAmount+transportDiscount)>transportFeeLeft)
					{

						if(transportFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only "+transportFeeLeft+" amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Transport fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						transportAmount=0;
					}

				}
				if(tutionFeeType==true)
				{

					if(tutionAmount>tutionFeeleft || tutionDiscount>tutionFeeleft || (tutionAmount+tutionDiscount)>tutionFeeleft)
					{
						if(tutionFeeleft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+tutionFeeleft+"' amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Tution fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						tutionAmount=0;
					}

				}
				if(artFeeType==true)
				{

					if(artAmount>artFeeLeft || artDiscount>artFeeLeft || (artAmount+artDiscount)>artFeeLeft)
					{
						if(artFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+artFeeLeft+"' amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Art/Craft fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						artAmount=0;
					}

				}
				if(activityFeeType==true)
				{

					if(activityAmount>activityFeeLeft || activityDiscount>activityFeeLeft || (activityAmount+activityDiscount)>activityFeeLeft)
					{
						if(activityFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+activityFeeLeft+"' amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Activity fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						activityAmount=0;
					}

				}
				if(termFeeType==true)
				{

					if(termAmount>termFeeLeft || termDiscount>termFeeLeft || (termAmount+termDiscount)>termFeeLeft)
					{
						if(termFeeLeft>0){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+termFeeLeft+"' amount left ", "Validation error"));
						}
						else{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Term fee paid, no amount left", "Validation error"));
						}
						flag2++;
						show=false;
						termAmount=0;
					}

				}

				if(flag2==0)
				{
					new DatabaseMethods1().insertfee(name,className);
					number=new DatabaseMethods1().feeserailno();

					if(number==0)
					{
						sno="1";
					}
					else
					{
						sno=String.valueOf(number);
					}

					if(examinationFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,examinationAmount,"Security Fee",paymentMode,bankName,chequeNumber,sno,examinationDiscount,preSession);
					}

					if(admissionFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,admissionAmount,"Addmission Fee",paymentMode,bankName,chequeNumber,sno,admissionDiscount,preSession);
					}

					if(annualFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,annualAmount,"Registration Fee",paymentMode,bankName,chequeNumber,sno,annualDiscount,preSession);
					}

					if(transportFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,transportAmount,"Transport Fee",paymentMode,bankName,chequeNumber,sno,transportDiscount,preSession);
					}
					if(tutionFeeType==true)
					{
						new DatabaseMethods1().submitFee(sList,tutionAmount,"School Fee",paymentMode,bankName,chequeNumber,sno,tutionDiscount,preSession);
					}

					if(artFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,artAmount,"Art Craft Fee",paymentMode,bankName,chequeNumber,sno,artDiscount,preSession);
					}

					if(activityFeeType==true)
					{
							new DatabaseMethods1().submitFee(sList,activityAmount,"Activity Fee",paymentMode,bankName,chequeNumber,sno,activityDiscount,preSession);
					}
					if(termFeeType==true)
					{
						new DatabaseMethods1().submitFee(sList,termAmount,"Term Fee",paymentMode,bankName,chequeNumber,sno,termDiscount,preSession);
					}

					HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

					rr.setAttribute("examinationfee",examinationAmount);
					rr.setAttribute("admissionfee",admissionAmount );
					rr.setAttribute("tutionfee", tutionAmount);
					rr.setAttribute("artfee",artAmount);
					rr.setAttribute("activityfee",activityAmount );
					rr.setAttribute("termfee", termAmount);
					rr.setAttribute("transpotfee", transportAmount);
					rr.setAttribute("annualfee", annualAmount);
					rr.setAttribute("paymentmode", paymentMode);
					rr.setAttribute("bankname", bankName);
					rr.setAttribute("chequeno", chequeNumber);
					rr.setAttribute("receiptNumber", sno);

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fee submit successfully"));

					show=true;
					annualFeeStatus=null;
					admissionDiscount=0;
					admissionAmount=0;
					annualAmount=0;
					annualDiscount=0;
					transportDiscount=0;
					examinationAmount=0;
					examinationDiscount=0;
					tutionAmount=0;
					tutionDiscount=0;
					transportAmount=0;
					transportDiscount=0;
					termAmount=0;
					termDiscount=0;
					artAmount=0;
					artDiscount=0;
					activityAmount=0;
					activityDiscount=0;




					annualFeeStatus=new ArrayList<FeeStatus>();

					HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					sList=(StudentInfo) ss.getAttribute("selectedStudent");

					name=sList.getFname();
					fathersName=sList.getFathersName();
					className=sList.getClassName();
					sectionName=sList.getSectionName();
					addmissionNumber=sList.getAddNumber();
					dob=sList.getDob();
					gender=sList.getGender();
					ss.setAttribute("admisionnumber", addmissionNumber);


					classFeeList=new DatabaseMethods1().classFee(sList.getClassId(),preSession);
					ClassInfo list=new ClassInfo();
					list=classFeeList.get(classFeeList.size()-1);

					DateFormat df = new SimpleDateFormat("MM");
					String reportDate = df.format(sList.getStartingDate());
					int startingMonth=0;
					int session=new DatabaseMethods1().getpromostionclass(String.valueOf(sList.getAddNumber()));
					if(session==1){

							startingMonth=4;
					}
					else{

						startingMonth=Integer.parseInt(reportDate);
					}
					totalAmount=new DatabaseMethods1().tutionFeePaid(sList,preSession)+DatabaseMethods1.getTutionDiscount(sList.getAddNumber(),preSession);

					feeStatus=new ArrayList<FeeStatus>();
					int feeListSize=classFeeList.size();

					if(startingMonth<=3)
						startingMonth+=12;

					for(int i=startingMonth;i<=15;i++)
					{
						int month=i;
						if(i>12)
							month=i-12;

						for(int j=0;j<feeListSize;j++)
						{

							ClassInfo info=classFeeList.get(j);
							if(info.getMonth() <= i )
							{
								if(j == feeListSize-1)
								{

									FeeStatus fs=new FeeStatus();
									fs.setMonth(new DatabaseMethods1().monthList(month));
									fs.setSchoolFee(String.valueOf(info.getTutionFee()/mn));
									feeStatus.add(fs);

									break;
								}
								else if(classFeeList.get(j+1).getMonth() > i)
								{
									FeeStatus fs=new FeeStatus();
									fs.setMonth(new DatabaseMethods1().monthList(month));
									fs.setSchoolFee(String.valueOf(info.getTutionFee()/mn));
									feeStatus.add(fs);

									break;
								}
							}
						}
					}

					int feeFlag=0;
					int counter=0;
					for(FeeStatus info : feeStatus)
					{
						int tutionFee=Integer.parseInt(info.getSchoolFee());
						if(totalAmount >= tutionFee)
						{
							info.setStatus(String.valueOf(tutionFee));
							info.setBalanceLeft("0");

							totalAmount-=tutionFee;
					//Extra
							if(tutionFeeType==true)
							{
								int month=new DatabaseMethods1().monthListByName(info.getMonth());
								if(month==3)
									lastMonthTutionFee=info.getMonth();
							}
							//
						}
						else
						{
							if(totalAmount>0)
							{
								info.setStatus(String.valueOf(totalAmount)+" paid");
								info.setBalanceLeft(String.valueOf(tutionFee-totalAmount)+" left");

								lastMonthTutionFee=info.getMonth();
								lastMonthTutionFeeTemp=info.getMonth();
								//tutionFeeleft=tutionFee-totalAmount; //Extra
								totalAmount=0;
							}
							else
							{
								if(feeFlag==0)
								{
									info.setStatus("0 paid");
									info.setBalanceLeft(String.valueOf(tutionFee)+" left");

									if(tutionFeeType==true)
									{
										lastMonthTutionFee=feeStatus.get(counter-1).getMonth();
										lastMonthTutionFeeTemp=info.getMonth();
									}
								}
								else
								{
									info.setStatus("0");
									info.setBalanceLeft(String.valueOf(tutionFee));
								}
								//tutionFeeleft+=tutionFee; //Extra
							}
							feeFlag++;
						}
						counter++;
					}

					ss.setAttribute("tutionFirstMonth", firstMonthTutionFee);
					ss.setAttribute("tutionLastMonth", lastMonthTutionFee);

					firstMonthTutionFee=lastMonthTutionFeeTemp;

					int totalExaminationFeePaid=0;
					if(session==1)
					{
						totalExaminationFeePaid=list.getExaminationFee();
					}
					else
					{
						totalExaminationFeePaid=new DatabaseMethods1().exmainationFeePaid(sList,preSession);
					}
					int examinationFeeDiscount=DatabaseMethods1.getExaminationDiscount(sList.getAddNumber(),preSession);
					examinationFeeStatus=new ArrayList<FeeStatus>();

					if(list.getExaminationFee()>totalExaminationFeePaid+examinationFeeDiscount)
					{

						FeeStatus fs=new FeeStatus();
						fs.setExaminationFee(String.valueOf(list.getExaminationFee()));
						fs.setStatus(String.valueOf(totalExaminationFeePaid)+" paid");
						fs.setBalanceLeft(String.valueOf(list.getExaminationFee()-totalExaminationFeePaid-examinationFeeDiscount)+ " left");

						examinationFeeLeft=list.getExaminationFee()-totalExaminationFeePaid-examinationFeeDiscount;
						examinationFeeStatus.add(fs);
					}
					else
					{
						FeeStatus fs=new FeeStatus();
						fs.setExaminationFee(String.valueOf(list.getExaminationFee()));
						fs.setStatus(String.valueOf(list.getExaminationFee())+" paid");
						fs.setBalanceLeft("0 left");

						examinationFeeStatus.add(fs);
					}

					//  edited by me //////////////////////////
					int totalAnnualFeePaid=0;
					if(session==1)
					{
						totalAnnualFeePaid=list.getAnnaulFee();
					}
					else
					{
						totalAnnualFeePaid=new DatabaseMethods1().annualFeeRecord(sList,preSession);
					}

					int annualFeeDiscount=DatabaseMethods1.getAnnualDiscount(sList.getAddNumber(),preSession);
					annualFeeStatus=new ArrayList<>();
					if(list.getAnnaulFee() > totalAnnualFeePaid+annualFeeDiscount)
					{
						FeeStatus fs=new FeeStatus();
						fs.setAddmissionFee(String.valueOf(list.getAnnaulFee()));
						fs.setStatus(String.valueOf(totalAnnualFeePaid)+" paid");
						fs.setBalanceLeft(String.valueOf(list.getAnnaulFee()-totalAnnualFeePaid-annualFeeDiscount)+ " left");

						annualFeeLeft=list.getAnnaulFee()-totalAnnualFeePaid-annualFeeDiscount;

						annualFeeStatus.add(fs);
					}
					else
					{
						FeeStatus fs=new FeeStatus();
						fs.setAddmissionFee(String.valueOf(list.getAnnaulFee()));
						fs.setStatus(String.valueOf(list.getAnnaulFee())+" paid");
						fs.setBalanceLeft("0 left");

						annualFeeStatus.add(fs);
					}


					///////////////////////////////////////////

					int totalActivityFeePaid=0;
					totalActivityFeePaid=new DatabaseMethods1().activityFeeRecord(sList,preSession);

					int activityFeeDiscount=DatabaseMethods1.getActivityDiscount(sList.getAddNumber(),preSession);
					activityFeeStatus=new ArrayList<>();
					if(list.getActivityFee() > totalActivityFeePaid+activityFeeDiscount)
					{
						FeeStatus fs=new FeeStatus();
						fs.setActivityFee(String.valueOf(list.getActivityFee()));
						fs.setStatus(String.valueOf(totalActivityFeePaid)+" paid");
						fs.setBalanceLeft(String.valueOf(list.getActivityFee()-totalActivityFeePaid-activityFeeDiscount)+ " left");

						activityFeeLeft=list.getActivityFee()-totalActivityFeePaid-activityFeeDiscount;

						activityFeeStatus.add(fs);
					}
					else
					{
						FeeStatus fs=new FeeStatus();
						fs.setActivityFee(String.valueOf(list.getActivityFee()));
						fs.setStatus(String.valueOf(list.getActivityFee())+" paid");
						fs.setBalanceLeft("0 left");

						activityFeeStatus.add(fs);
					}


					///////////////////////////////////////////
					int totalTermFeePaid=0;
					totalTermFeePaid=new DatabaseMethods1().termFeeRecord(sList,preSession);

					int termFeeDiscount=DatabaseMethods1.getTermDiscount(sList.getAddNumber(),preSession);
					termStatus=new ArrayList<>();
					if(list.getTermFee() > totalTermFeePaid+termFeeDiscount)
					{
						FeeStatus fs=new FeeStatus();
						fs.setTermFee(String.valueOf(list.getTermFee()));
						fs.setStatus(String.valueOf(totalTermFeePaid)+" paid");
						fs.setBalanceLeft(String.valueOf(list.getTermFee()-totalTermFeePaid-termFeeDiscount)+ " left");

						termFeeLeft=list.getTermFee()-totalTermFeePaid-termFeeDiscount;

						termStatus.add(fs);
					}
					else
					{
						FeeStatus fs=new FeeStatus();
						fs.setTermFee(String.valueOf(list.getTermFee()));
						fs.setStatus(String.valueOf(list.getTermFee())+" paid");
						fs.setBalanceLeft("0 left");

						termStatus.add(fs);
					}

					///////////////////////////////////////////
					int totalArtFeePaid=0;
					totalArtFeePaid=new DatabaseMethods1().artFeeRecord(sList,preSession);

					int artFeeDiscount=DatabaseMethods1.getArtDiscount(sList.getAddNumber(),preSession);
					artFeeStatus=new ArrayList<>();
					if(list.getArtFee() > totalArtFeePaid+artFeeDiscount)
					{
						FeeStatus fs=new FeeStatus();
						fs.setArtFee(String.valueOf(list.getArtFee()));
						fs.setStatus(String.valueOf(totalArtFeePaid)+" paid");
						fs.setBalanceLeft(String.valueOf(list.getArtFee()-totalArtFeePaid-artFeeDiscount)+ " left");

						artFeeLeft=list.getArtFee()-totalArtFeePaid-artFeeDiscount;

						artFeeStatus.add(fs);
					}
					else
					{
						FeeStatus fs=new FeeStatus();
						fs.setArtFee(String.valueOf(list.getArtFee()));
						fs.setStatus(String.valueOf(list.getArtFee())+" paid");
						fs.setBalanceLeft("0 left");

						artFeeStatus.add(fs);
					}

					/////////////////////////////////////////

					    int totalAddmissionFeePaid=0;

					    if(session==1){
					    		totalAddmissionFeePaid=list.getAddmissionFee();
					    	}
					    else{
					    		totalAddmissionFeePaid=new DatabaseMethods1().addmissionFeePaid(sList,preSession);
							}

						int admissionFeeDiscount=DatabaseMethods1.getAdmissionDiscount(sList.getAddNumber(),preSession);

						addmissionFeeStatus=new ArrayList<FeeStatus>();
						if((list.getAddmissionFee()) > totalAddmissionFeePaid+admissionFeeDiscount)
						{
							FeeStatus fs=new FeeStatus();
							fs.setAddmissionFee(String.valueOf(list.getAddmissionFee()));
							fs.setStatus(String.valueOf(totalAddmissionFeePaid)+" paid");
							fs.setBalanceLeft(String.valueOf(list.getAddmissionFee()-totalAddmissionFeePaid-admissionFeeDiscount)+ " left");

							admissionFeeLeft=list.getAddmissionFee()-totalAddmissionFeePaid-admissionFeeDiscount;
							addmissionFeeStatus.add(fs);
						}
						else
						{
							FeeStatus fs=new FeeStatus();
							fs.setAddmissionFee(String.valueOf(list.getAddmissionFee()));
							fs.setStatus(String.valueOf(list.getAddmissionFee())+" paid");
							fs.setBalanceLeft("0 left");

							addmissionFeeStatus.add(fs);
						}


						int transportFeePaid=new DatabaseMethods1().transportFeePaid(sList,preSession)+DatabaseMethods1.getTransportDiscount(sList.getAddNumber(),preSession);  //already paid student transport fee

						transportfeeStatus=new ArrayList<FeeStatus>();
						ArrayList<Transport> transportFeeDetails=new DatabaseMethods1().transportListDetails(sList.getAddNumber(),preSession);
						for(Transport t: transportFeeDetails)
						{
							FeeStatus fs=new FeeStatus();
							fs.setTransportRouteName(new DatabaseMethods1().transportRouteNameFromId(String.valueOf(t.getRouteId()),preSession));
							fs.setMonth(t.getMonth());
							fs.setStatus(t.getStatus());

							ArrayList<ClassInfo> transportFeeList = new DatabaseMethods1().transportRouteDetailsWithFee(t.getRouteId(),preSession);

							for(int j=0;j<transportFeeList.size();j++)
							{
								ClassInfo info1=transportFeeList.get(j);
								if(info1.getMonth() <= t.getMonthInt())
								{

									if(j == transportFeeList.size()-1)
									{

										fs.setTransportFee(String.valueOf(info1.getTransportFee()));
										transportfeeStatus.add(fs);

										break;
									}
									else if(transportFeeList.get(j+1).getMonth() > t.getMonthInt())
									{
										fs.setTransportFee(String.valueOf(info1.getTransportFee()));
										transportfeeStatus.add(fs);

										break;
									}
								}
							}
						}

				     	flag=0;
				     	counter=0;
						transportFeeLeft=0;
						for(FeeStatus info : transportfeeStatus)
						{
							int transportFee=Integer.parseInt(info.getTransportFee());
							if(transportFeePaid >= transportFee)
							{
								info.setStatus(String.valueOf(transportFee));
								info.setBalanceLeft("0");

								transportFeePaid-=transportFee;
								//Extra
								if(transportFeeType==true)
								{
									int month=new DatabaseMethods1().monthListByName(info.getMonth());
									if(month==3)
										lastMonthTransportPeriod=info.getMonth();
								}
								//
							}
							else
							{
								if(transportFeePaid>0)
								{
									info.setStatus(String.valueOf(transportFeePaid)+" paid");
									info.setBalanceLeft(String.valueOf(transportFee-transportFeePaid)+" left");

									lastMonthTransportPeriod=info.getMonth();
									lastMonthTransportPeriodTemp=info.getMonth();

									transportFeeLeft+=transportFee-transportFeePaid;
									transportFeePaid=0;
								}
								else
								{
									if(flag==0)
									{
										info.setStatus("0 paid");
										info.setBalanceLeft(String.valueOf(transportFee)+" left");

										if(transportFeeType==true)
										{
											lastMonthTransportPeriod=transportfeeStatus.get(counter-1).getMonth();
											lastMonthTransportPeriodTemp=info.getMonth();
										}
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
							counter++;
						}

						ss.setAttribute("transportFirstMonth", firstMonthTransportPeriod);
						ss.setAttribute("transportLastMonth", lastMonthTransportPeriod);

						firstMonthTransportPeriod=lastMonthTransportPeriodTemp;

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}



		amount=0;
		paymentMode=null;
		feeType=null;
		bankName=null;
		chequeNumber=null;

		admissionFeeType=false;
		tutionFeeType=false;
		examinationFeeType=false;
		annualFeeType=false;
		transportFeeType=false;
		artFeeType=false;
		activityFeeType=false;
		termFeeType=false;

		showAdmissionFee=false;
		showTutionFee=false;
		showExaminationFee=false;
		showAnnualFee=false;
		showTransportFee=false;
		showArtFee=false;
		showActivityFee=false;
		showTermFee=false;

	}

//	public String submitHere()
//	{
//		int flag=0;
//		if(amount<=0)
//		{
//			flag++;
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Amount must be greater than zero", "Validation error"));
//			return null;
//		}
//
//		if(flag==0)
//		{
//
//			try
//			{
//				int flag2=0;
//				if(feeType.equals("Examination Fee"))
//				{
//					if(amount>examinationFeeLeft)
//					{
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+examinationFeeLeft+"' amount left ", "Validation error"));
//
//						flag2++;
//					}
//
//				}
//				if(feeType.equals("Addmission Fee"))
//				{
//					if(amount>admissionFeeLeft)
//					{
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+admissionFeeLeft+"' amount left ", "Validation error"));
//
//						flag2++;
//					}
//
//				}
//				if(feeType.equals("Annual Fee"))
//				{
//					if(amount>annualFeeLeft)
//					{
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+annualFeeLeft+"' amount left ", "Validation error"));
//
//						flag2++;
//					}
//
//				}
//
//				if(feeType.equals("Transport Fee"))
//				{
//					if(amount>transportFeeLeft)
//					{
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+transportFeeLeft+"' amount left ", "Validation error"));
//
//						flag2++;
//					}
//
//				}
//				if(feeType.equals("School Fee"))
//				{
//					if(amount>tutionFeeleft)
//					{
//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Only '"+tutionFeeleft+"' amount left ", "Validation error"));
//
//						flag2++;
//					}
//
//				}
//
//
//
//
//		/*		for(ClassInfo feeStatus:classFeeList)
//				{
//				   transportFee=feeStatus.getTransportFee();
//				}
//				for(ClassInfo feeStatus:classFeeList)
//				{
//					admissionFee=feeStatus.getAddmissionFee();
//				}
//				for(ClassInfo feeStatus:classFeeList)
//				{
//					examinationFee=feeStatus.getExaminationFee();
//				}
//				for(ClassInfo feeStatus:classFeeList)
//				{
//					schoolFee=feeStatus.getAddmissionFee();
//				}
//				for(ClassInfo feeStatus:classFeeList)
//				{
//					annualFee=feeStatus.getAnnaulFee();
//				}
//
//
//               if(new DatabaseMethods1().totaldueFee(sList, feeType,amount) > 0)
//				{
//
//					return "FeeReceipt";
//				}
//				else
//				{
//					FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_INFO,"This fee submission not valid ", ""));
//				}
//
//				if(flag2==0)
//				{
//					new DatabaseMethods1().submitFee(sList, amount,feeType,paymentMode,bankName,chequeNumber);
//
//					return "FeeReceipt";
//				}
//
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
//		}
//
//
//		amount=0;
//		paymentMode=null;
//		feeType=null;
//		bankName=null;
//		chequeNumber=null;
//
//		return null;
//
//	}


	public int getAdmissionAmount() {
		return admissionAmount;
	}

	public void setAdmissionAmount(int admissionAmount) {
		this.admissionAmount = admissionAmount;
	}

	public int getExaminationAmount() {
		return examinationAmount;
	}

	public void setExaminationAmount(int examinationAmount) {
		this.examinationAmount = examinationAmount;
	}

	public int getTutionAmount() {
		return tutionAmount;
	}

	public void setTutionAmount(int tutionAmount) {
		this.tutionAmount = tutionAmount;
	}

	public int getAnnualAmount() {
		return annualAmount;
	}
	public void setAnnualAmount(int annualAmount) {
		this.annualAmount = annualAmount;
	}
	public int getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(int transportAmount) {
		this.transportAmount = transportAmount;
	}
	public StudentInfo getsList() {
		return sList;
	}
	public void setsList(StudentInfo sList) {
		this.sList = sList;
	}
	public ArrayList<FeeStatus> getAnnualFeeStatus() {
		return annualFeeStatus;
	}
	public void setAnnualFeeStatus(ArrayList<FeeStatus> annualFeeStatus) {
		this.annualFeeStatus = annualFeeStatus;
	}

	public CollectFeeHereBean()
	{

		preSession=DatabaseMethods1.selectedSessionDetails();

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sList=(StudentInfo) ss.getAttribute("selectedStudent");
		name=sList.getFname();
		fathersName=sList.getFathersName();
		className=sList.getClassName();
		sectionName=sList.getSectionName();
		addmissionNumber=sList.getAddNumber();
		dob=sList.getDob();
		gender=sList.getGender();
		ss.setAttribute("admisionnumber", addmissionNumber);

		classFeeList=new DatabaseMethods1().classFee(sList.getClassId(),preSession);
		ClassInfo list=new ClassInfo();
		list=classFeeList.get(classFeeList.size()-1);

		DateFormat df = new SimpleDateFormat("MM");
		String reportDate = df.format(sList.getStartingDate());
		int startingMonth=0;
		int session=new DatabaseMethods1().getpromostionclass(String.valueOf(sList.getAddNumber()));
		if(session==1){

				startingMonth=4;
		}
		else{

			startingMonth=Integer.parseInt(reportDate);
		}
		totalAmount=new DatabaseMethods1().tutionFeePaid(sList,preSession)+DatabaseMethods1.getTutionDiscount(sList.getAddNumber(),preSession);

		feeModuleType=new DatabaseMethods1().checkFeeType();
		mn=0;
		if(feeModuleType.equals("month"))
		{
			mn=1;
		}
		else if(feeModuleType.equals("quarter"))
		{
			mn=3;
		}
		else if(feeModuleType.equals("year"))
		{
			mn=12;
		}


		//Edited by Me Avesh

		feeStatus=new ArrayList<FeeStatus>();
		int feeListSize=classFeeList.size();

		if(startingMonth<=3)
			startingMonth+=12;

		for(int i=startingMonth;i<=15;i++)
		{
			int month=i;
			if(i>12)
				month=i-12;

			for(int j=0;j<feeListSize;j++)
			{

				ClassInfo info=classFeeList.get(j);
				if(info.getMonth() <= i )
				{
					if(j == feeListSize-1)
					{

						FeeStatus fs=new FeeStatus();
						fs.setMonth(new DatabaseMethods1().monthList(month));
						fs.setSchoolFee(String.valueOf(info.getTutionFee()/mn));
						feeStatus.add(fs);

						break;
					}
					else if(classFeeList.get(j+1).getMonth() > i)
					{
						FeeStatus fs=new FeeStatus();
						fs.setMonth(new DatabaseMethods1().monthList(month));
						fs.setSchoolFee(String.valueOf(info.getTutionFee()/mn));
						feeStatus.add(fs);

						break;
					}
				}
			}
		}

		int flag=0;
		tutionFeeleft=0;
		for(FeeStatus info : feeStatus)
		{

			int tutionFee=Integer.parseInt(info.getSchoolFee());
			if(totalAmount >= tutionFee)
			{
				info.setStatus(String.valueOf(tutionFee));
				info.setBalanceLeft("0");

				totalAmount-=tutionFee;
			}
			else
			{
				if(totalAmount>0)
				{
					info.setStatus(String.valueOf(totalAmount)+" paid");
					info.setBalanceLeft(String.valueOf(tutionFee-totalAmount)+" left");

					firstMonthTutionFee=info.getMonth();
					tutionFeeleft+=tutionFee-totalAmount;
					totalAmount=0;
				}
				else
				{
					if(flag==0)
					{
						info.setStatus("0 paid");
						info.setBalanceLeft(String.valueOf(tutionFee)+" left");

						firstMonthTutionFee=info.getMonth();
					}
					else
					{
						info.setStatus("0");
						info.setBalanceLeft(String.valueOf(tutionFee));
					}
					tutionFeeleft+=tutionFee;
				}
				flag++;
			}
		}

		int totalExaminationFeePaid=0;
		if(session==1)
		{
			totalExaminationFeePaid=list.getExaminationFee();
		}
		else
		{
			totalExaminationFeePaid=new DatabaseMethods1().exmainationFeePaid(sList,preSession);
		}

		int examinationFeeDiscount=DatabaseMethods1.getExaminationDiscount(sList.getAddNumber(),preSession);
		examinationFeeStatus=new ArrayList<FeeStatus>();

		if(list.getExaminationFee()>totalExaminationFeePaid+examinationFeeDiscount)
		{

			FeeStatus fs=new FeeStatus();
			fs.setExaminationFee(String.valueOf(list.getExaminationFee()));
			fs.setStatus(String.valueOf(totalExaminationFeePaid)+" paid");
			fs.setBalanceLeft(String.valueOf(list.getExaminationFee()-totalExaminationFeePaid-examinationFeeDiscount)+ " left");


			examinationFeeLeft=list.getExaminationFee()-totalExaminationFeePaid-examinationFeeDiscount;
			examinationFeeStatus.add(fs);
		}
		else
		{

			FeeStatus fs=new FeeStatus();
			fs.setExaminationFee(String.valueOf(list.getExaminationFee()));
			fs.setStatus(String.valueOf(list.getExaminationFee())+" paid");
			fs.setBalanceLeft("0 left");

			examinationFeeStatus.add(fs);
		}

		//  edited by me //////////////////////////
		int totalAnnualFeePaid=0;
		if(session==1)
		{
			totalAnnualFeePaid=list.getAnnaulFee();
		}
		else
		{
			totalAnnualFeePaid=new DatabaseMethods1().annualFeeRecord(sList,preSession);
		}

		int annualFeeDiscount=DatabaseMethods1.getAnnualDiscount(sList.getAddNumber(),preSession);
		annualFeeStatus=new ArrayList<>();
		if(list.getAnnaulFee() > totalAnnualFeePaid+annualFeeDiscount)
		{
			FeeStatus fs=new FeeStatus();
			fs.setAddmissionFee(String.valueOf(list.getAnnaulFee()));
			fs.setStatus(String.valueOf(totalAnnualFeePaid)+" paid");
			fs.setBalanceLeft(String.valueOf(list.getAnnaulFee()-totalAnnualFeePaid-annualFeeDiscount)+ " left");

			annualFeeLeft=list.getAnnaulFee()-totalAnnualFeePaid-annualFeeDiscount;

			annualFeeStatus.add(fs);
		}
		else
		{
			FeeStatus fs=new FeeStatus();
			fs.setAddmissionFee(String.valueOf(list.getAnnaulFee()));
			fs.setStatus(String.valueOf(list.getAnnaulFee())+" paid");
			fs.setBalanceLeft("0 left");

			annualFeeStatus.add(fs);
		}

		///////////////////////////////////////////

		int totalActivityFeePaid=0;
		totalActivityFeePaid=new DatabaseMethods1().activityFeeRecord(sList,preSession);

		int activityFeeDiscount=DatabaseMethods1.getActivityDiscount(sList.getAddNumber(),preSession);
		activityFeeStatus=new ArrayList<>();
		if(list.getActivityFee() > totalActivityFeePaid+activityFeeDiscount)
		{
			FeeStatus fs=new FeeStatus();
			fs.setActivityFee(String.valueOf(list.getActivityFee()));
			fs.setStatus(String.valueOf(totalActivityFeePaid)+" paid");
			fs.setBalanceLeft(String.valueOf(list.getActivityFee()-totalActivityFeePaid-activityFeeDiscount)+ " left");

			activityFeeLeft=list.getActivityFee()-totalActivityFeePaid-activityFeeDiscount;

			activityFeeStatus.add(fs);
		}
		else
		{
			FeeStatus fs=new FeeStatus();
			fs.setActivityFee(String.valueOf(list.getActivityFee()));
			fs.setStatus(String.valueOf(list.getActivityFee())+" paid");
			fs.setBalanceLeft("0 left");

			activityFeeStatus.add(fs);
		}


		///////////////////////////////////////////
		int totalTermFeePaid=0;
		totalTermFeePaid=new DatabaseMethods1().termFeeRecord(sList,preSession);

		int termFeeDiscount=DatabaseMethods1.getTermDiscount(sList.getAddNumber(),preSession);
		termStatus=new ArrayList<>();
		if(list.getTermFee() > totalTermFeePaid+termFeeDiscount)
		{
			FeeStatus fs=new FeeStatus();
			fs.setTermFee(String.valueOf(list.getTermFee()));
			fs.setStatus(String.valueOf(totalTermFeePaid)+" paid");
			fs.setBalanceLeft(String.valueOf(list.getTermFee()-totalTermFeePaid-termFeeDiscount)+ " left");

			termFeeLeft=list.getTermFee()-totalTermFeePaid-termFeeDiscount;

			termStatus.add(fs);
		}
		else
		{
			FeeStatus fs=new FeeStatus();
			fs.setTermFee(String.valueOf(list.getTermFee()));
			fs.setStatus(String.valueOf(list.getTermFee())+" paid");
			fs.setBalanceLeft("0 left");

			termStatus.add(fs);
		}

		///////////////////////////////////////////
		int totalArtFeePaid=0;
		totalArtFeePaid=new DatabaseMethods1().artFeeRecord(sList,preSession);

		int artFeeDiscount=DatabaseMethods1.getArtDiscount(sList.getAddNumber(),preSession);
		artFeeStatus=new ArrayList<>();
		if(list.getArtFee() > totalArtFeePaid+artFeeDiscount)
		{
			FeeStatus fs=new FeeStatus();
			fs.setArtFee(String.valueOf(list.getArtFee()));
			fs.setStatus(String.valueOf(totalArtFeePaid)+" paid");
			fs.setBalanceLeft(String.valueOf(list.getArtFee()-totalArtFeePaid-artFeeDiscount)+ " left");

			artFeeLeft=list.getArtFee()-totalArtFeePaid-artFeeDiscount;

			artFeeStatus.add(fs);
		}
		else
		{
			FeeStatus fs=new FeeStatus();
			fs.setArtFee(String.valueOf(list.getArtFee()));
			fs.setStatus(String.valueOf(list.getArtFee())+" paid");
			fs.setBalanceLeft("0 left");

			artFeeStatus.add(fs);
		}

		/////////////////////////////////////////

		///////////////////////////////////////////
		int totalAddmissionFeePaid=0;
		if(session==1){

				totalAddmissionFeePaid=list.getAddmissionFee();
		}
		else{
			totalAddmissionFeePaid=new DatabaseMethods1().addmissionFeePaid(sList,preSession);
		}

			int admissionFeeDiscount=DatabaseMethods1.getAdmissionDiscount(sList.getAddNumber(),preSession);

			addmissionFeeStatus=new ArrayList<FeeStatus>();
			if((list.getAddmissionFee()) > totalAddmissionFeePaid+admissionFeeDiscount)
			{
				FeeStatus fs=new FeeStatus();
				fs.setAddmissionFee(String.valueOf(list.getAddmissionFee()));
				fs.setStatus(String.valueOf(totalAddmissionFeePaid)+" paid");
				fs.setBalanceLeft(String.valueOf(list.getAddmissionFee()-totalAddmissionFeePaid-admissionFeeDiscount)+ " left");

				admissionFeeLeft=list.getAddmissionFee()-totalAddmissionFeePaid-admissionFeeDiscount;
				addmissionFeeStatus.add(fs);
			}
			else
			{
				FeeStatus fs=new FeeStatus();
				fs.setAddmissionFee(String.valueOf(list.getAddmissionFee()));
				fs.setStatus(String.valueOf(list.getAddmissionFee())+" paid");
				fs.setBalanceLeft("0 left");

				addmissionFeeStatus.add(fs);
			}


		int transportFeePaid=new DatabaseMethods1().transportFeePaid(sList,preSession)+DatabaseMethods1.getTransportDiscount(sList.getAddNumber(),preSession);  //already paid student transport fee

		transportfeeStatus=new ArrayList<FeeStatus>();
		ArrayList<Transport> transportFeeDetails=new DatabaseMethods1().transportListDetails(sList.getAddNumber(),preSession);
		for(Transport t: transportFeeDetails)
		{
			FeeStatus fs=new FeeStatus();
			fs.setTransportRouteName(new DatabaseMethods1().transportRouteNameFromId(String.valueOf(t.getRouteId()),preSession));
			fs.setMonth(t.getMonth());
			fs.setStatus(t.getStatus());

			ArrayList<ClassInfo> transportFeeList = new DatabaseMethods1().transportRouteDetailsWithFee(t.getRouteId(),preSession);

			for(int j=0;j<transportFeeList.size();j++)
			{
				ClassInfo info1=transportFeeList.get(j);
				if(info1.getMonth() <= t.getMonthInt())
				{

					if(j == transportFeeList.size()-1)
					{

						fs.setTransportFee(String.valueOf(info1.getTransportFee()));
						transportfeeStatus.add(fs);

						break;
					}
					else if(transportFeeList.get(j+1).getMonth() > t.getMonthInt())
					{
						fs.setTransportFee(String.valueOf(info1.getTransportFee()));
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

			int transportFee=Integer.parseInt(info.getTransportFee());
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
	}

	public void showInputTextOfFee(){

		if(admissionFeeType==true){
			showAdmissionFee=true;
		}
		else{
			showAdmissionFee=false;
		}
		if(examinationFeeType==true){
			showExaminationFee=true;
		}
		else{
			showExaminationFee=false;
		}
		if(annualFeeType==true){
			showAnnualFee=true;
		}
		else{
			showAnnualFee=false;
		}
		if(transportFeeType==true){
			showTransportFee=true;
		}
		else{
			showTransportFee=false;
		}
		if(tutionFeeType==true){
			showTutionFee=true;
		}
		else{
			showTutionFee=false;
		}
		//*****
		if(activityFeeType==true){
			showActivityFee=true;
		}
		else{
			showActivityFee=false;
		}
		if(artFeeType==true){
			showArtFee=true;
		}
		else{
			showArtFee=false;
		}
		if(termFeeType==true){
			showTermFee=true;
		}
		else{
			showTermFee=false;
		}
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
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
	public boolean isAdmissionFeeType() {
		return admissionFeeType;
	}
	public void setAdmissionFeeType(boolean admissionFeeType) {
		this.admissionFeeType = admissionFeeType;
	}
	public boolean isTutionFeeType() {
		return tutionFeeType;
	}
	public void setTutionFeeType(boolean tutionFeeType) {
		this.tutionFeeType = tutionFeeType;
	}
	public boolean isExaminationFeeType() {
		return examinationFeeType;
	}
	public void setExaminationFeeType(boolean examinationFeeType) {
		this.examinationFeeType = examinationFeeType;
	}
	public boolean isAnnualFeeType() {
		return annualFeeType;
	}
	public void setAnnualFeeType(boolean annualFeeType) {
		this.annualFeeType = annualFeeType;
	}
	public boolean isTransportFeeType() {
		return transportFeeType;
	}
	public void setTransportFeeType(boolean transportFeeType) {
		this.transportFeeType = transportFeeType;
	}
	public boolean isShowAdmissionFee() {
		return showAdmissionFee;
	}
	public void setShowAdmissionFee(boolean showAdmissionFee) {
		this.showAdmissionFee = showAdmissionFee;
	}
	public boolean isShowTutionFee() {
		return showTutionFee;
	}
	public void setShowTutionFee(boolean showTutionFee) {
		this.showTutionFee = showTutionFee;
	}
	public boolean isShowExaminationFee() {
		return showExaminationFee;
	}
	public void setShowExaminationFee(boolean showExaminationFee) {
		this.showExaminationFee = showExaminationFee;
	}
	public boolean isShowAnnualFee() {
		return showAnnualFee;
	}
	public void setShowAnnualFee(boolean showAnnualFee) {
		this.showAnnualFee = showAnnualFee;
	}
	public boolean isShowTransportFee() {
		return showTransportFee;
	}
	public void setShowTransportFee(boolean showTransportFee) {
		this.showTransportFee = showTransportFee;
	}
	public int getAdmissionFeeLeft() {
		return admissionFeeLeft;
	}
	public void setAdmissionFeeLeft(int admissionFeeLeft) {
		this.admissionFeeLeft = admissionFeeLeft;
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
	public boolean isShowPaymentMode() {
		return showPaymentMode;
	}
	public void setShowPaymentMode(boolean showPaymentMode) {
		this.showPaymentMode = showPaymentMode;
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
	public ArrayList<FeeStatus> getExaminationFeeStatus() {
		return examinationFeeStatus;
	}
	public void setExaminationFeeStatus(ArrayList<FeeStatus> examinationFeeStatus) {
		this.examinationFeeStatus = examinationFeeStatus;
	}
	public ArrayList<FeeStatus> getAddmissionFeeStatus() {
		return addmissionFeeStatus;
	}
	public void setAddmissionFeeStatus(ArrayList<FeeStatus> addmissionFeeStatus) {
		this.addmissionFeeStatus = addmissionFeeStatus;
	}
	public ArrayList<ClassInfo> getClassFeeList() {
		return classFeeList;
	}
	public void setClassFeeList(ArrayList<ClassInfo> classFeeList) {
		this.classFeeList = classFeeList;
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
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public ArrayList<FeeStatus> getTransportfeeStatus() {
		return transportfeeStatus;
	}
	public void setTransportfeeStatus(ArrayList<FeeStatus> transportfeeStatus) {
		this.transportfeeStatus = transportfeeStatus;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public ArrayList<FeeStatus> getFeeStatus() {
		return feeStatus;
	}
	public void setFeeStatus(ArrayList<FeeStatus> feeStatus) {
		this.feeStatus = feeStatus;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAddmissionNumber() {
		return addmissionNumber;
	}
	public void setAddmissionNumber(int addmissionNumber) {
		this.addmissionNumber = addmissionNumber;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType)
	{
		this.feeType = feeType;
	}
	public ArrayList<StudentInfo> getSerialno() {
		return serialno;
	}
	public void setSerialno(ArrayList<StudentInfo> serialno) {
		this.serialno = serialno;
	}
	public String getSno() {
		return sno;
	}

	public String getPreSession() {
		return preSession;
	}

	public void setPreSession(String preSession) {
		this.preSession = preSession;
	}

	public String getFirstMonthTutionFee() {
		return firstMonthTutionFee;
	}

	public void setFirstMonthTutionFee(String firstMonthTutionFee) {
		this.firstMonthTutionFee = firstMonthTutionFee;
	}

	public String getLastMonthTutionFee() {
		return lastMonthTutionFee;
	}

	public void setLastMonthTutionFee(String lastMonthTutionFee) {
		this.lastMonthTutionFee = lastMonthTutionFee;
	}

	public String getLastMonthTutionFeeTemp() {
		return lastMonthTutionFeeTemp;
	}

	public void setLastMonthTutionFeeTemp(String lastMonthTutionFeeTemp) {
		this.lastMonthTutionFeeTemp = lastMonthTutionFeeTemp;
	}

	public String getFirstMonthTermFee() {
		return firstMonthTermFee;
	}

	public void setFirstMonthTermFee(String firstMonthTermFee) {
		this.firstMonthTermFee = firstMonthTermFee;
	}

	public String getLastMonthTermFee() {
		return lastMonthTermFee;
	}

	public void setLastMonthTermFee(String lastMonthTermFee) {
		this.lastMonthTermFee = lastMonthTermFee;
	}

	public String getLastMonthTermFeeTemp() {
		return lastMonthTermFeeTemp;
	}

	public void setLastMonthTermFeeTemp(String lastMonthTermFeeTemp) {
		this.lastMonthTermFeeTemp = lastMonthTermFeeTemp;
	}

	public String getLastMonthTransportPeriodTemp() {
		return lastMonthTransportPeriodTemp;
	}

	public void setLastMonthTransportPeriodTemp(String lastMonthTransportPeriodTemp) {
		this.lastMonthTransportPeriodTemp = lastMonthTransportPeriodTemp;
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

	public int getTermFeeLeft() {
		return termFeeLeft;
	}

	public void setTermFeeLeft(int termFeeLeft) {
		this.termFeeLeft = termFeeLeft;
	}

	public int getArtFeeLeft() {
		return artFeeLeft;
	}

	public void setArtFeeLeft(int artFeeLeft) {
		this.artFeeLeft = artFeeLeft;
	}

	public int getActivityFeeLeft() {
		return activityFeeLeft;
	}

	public void setActivityFeeLeft(int activityFeeLeft) {
		this.activityFeeLeft = activityFeeLeft;
	}

	public int getTutionFeeleft() {
		return tutionFeeleft;
	}

	public void setTutionFeeleft(int tutionFeeleft) {
		this.tutionFeeleft = tutionFeeleft;
	}

	public int getExaminationFeeLeft() {
		return examinationFeeLeft;
	}

	public void setExaminationFeeLeft(int examinationFeeLeft) {
		this.examinationFeeLeft = examinationFeeLeft;
	}

	public int getAnnualFeeLeft() {
		return annualFeeLeft;
	}

	public void setAnnualFeeLeft(int annualFeeLeft) {
		this.annualFeeLeft = annualFeeLeft;
	}

	public int getTransportFeeLeft() {
		return transportFeeLeft;
	}

	public void setTransportFeeLeft(int transportFeeLeft) {
		this.transportFeeLeft = transportFeeLeft;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getArtAmount() {
		return artAmount;
	}

	public void setArtAmount(int artAmount) {
		this.artAmount = artAmount;
	}

	public int getTermAmount() {
		return termAmount;
	}

	public void setTermAmount(int termAmount) {
		this.termAmount = termAmount;
	}

	public int getActivityAmount() {
		return activityAmount;
	}

	public void setActivityAmount(int activityAmount) {
		this.activityAmount = activityAmount;
	}

	public boolean isActivityFeeType() {
		return activityFeeType;
	}

	public void setActivityFeeType(boolean activityFeeType) {
		this.activityFeeType = activityFeeType;
	}

	public boolean isArtFeeType() {
		return artFeeType;
	}

	public void setArtFeeType(boolean artFeeType) {
		this.artFeeType = artFeeType;
	}

	public boolean isTermFeeType() {
		return termFeeType;
	}

	public void setTermFeeType(boolean termFeeType) {
		this.termFeeType = termFeeType;
	}

	public boolean isShowActivityFee() {
		return showActivityFee;
	}

	public void setShowActivityFee(boolean showActivityFee) {
		this.showActivityFee = showActivityFee;
	}

	public boolean isShowArtFee() {
		return showArtFee;
	}

	public void setShowArtFee(boolean showArtFee) {
		this.showArtFee = showArtFee;
	}

	public boolean isShowTermFee() {
		return showTermFee;
	}

	public void setShowTermFee(boolean showTermFee) {
		this.showTermFee = showTermFee;
	}

	public ArrayList<FeeStatus> getTermStatus() {
		return termStatus;
	}

	public void setTermStatus(ArrayList<FeeStatus> termStatus) {
		this.termStatus = termStatus;
	}

	public ArrayList<FeeStatus> getArtFeeStatus() {
		return artFeeStatus;
	}

	public void setArtFeeStatus(ArrayList<FeeStatus> artFeeStatus) {
		this.artFeeStatus = artFeeStatus;
	}

	public ArrayList<FeeStatus> getActivityFeeStatus() {
		return activityFeeStatus;
	}

	public void setActivityFeeStatus(ArrayList<FeeStatus> activityFeeStatus) {
		this.activityFeeStatus = activityFeeStatus;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}


}
 */