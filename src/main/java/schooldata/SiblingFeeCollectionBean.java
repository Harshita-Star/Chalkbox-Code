package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name="siblingFee")
@ViewScoped
public class SiblingFeeCollectionBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	boolean showSibling1=false,showSibling2=false,showOther=false;
	StudentInfo selectedStudent;
	int flag,transportFeeLeft;
	String fname1,className1,fName2,className2,preSession,firstMonthTransportPeriod,addNumber,addNo1,addNo2;
	ArrayList<FeeInfo> classFeeList,feeStructureList1=new ArrayList<>(),feeStructureList2=new ArrayList<>(),tempFeeList=new ArrayList<>();
	ArrayList<FeeStatus> transportfeeStatus;
	ArrayList<SubjectInfo> subjectList;

	public SiblingFeeCollectionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedStudent=(StudentInfo) ss.getAttribute("selectedStudent");

		addNumber=selectedStudent.getAddNumber();
		ParentsInfo list=DBM.editParentsDetail(addNumber,conn);

		if(list.getSname1().contains("-"))
		{

			fname1=list.getSname1();

			int index=fname1.lastIndexOf("-")+1;
			addNo1=fname1.substring(index);

			String classid=DBM.classSectionNameFromidSchid(DBM.schoolId(),list.getSclassid1(),conn);
			className1=DBM.classNameFromidSchid(DBM.schoolId(),classid,preSession,conn);

			StudentInfo selectedStudent=DBM.studentDetailslistByAddNo(DBM.schoolId(),addNo1,conn);
			showSibling1=true;
			calculateStudentFee(selectedStudent);
		}

		if(list.getSname2().contains("-"))
		{
			fName2=list.getSname2();

			int index=fName2.lastIndexOf("-")+1;
			addNo2=fName2.substring(index);

			String classid1=DBM.classSectionNameFromidSchid(DBM.schoolId(),list.getSclassid2(),conn);
			className2=DBM.classNameFromidSchid(DBM.schoolId(),classid1,preSession,conn);

			StudentInfo selectedStudent=DBM.studentDetailslistByAddNo(DBM.schoolId(),addNo2,conn);
			calculateStudentFee2(selectedStudent);
			showSibling2=true;
		}

		if(showSibling1==false && showSibling2==false)
		{
			showOther=true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void calculateStudentFee2(StudentInfo selectedStudent)
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		preSession=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		classFeeList=DBM.classFees(selectedStudent.getClassId(),/*preSession,*/conn);

		DateFormat df = new SimpleDateFormat("MM");
		String reportDate = df.format(selectedStudent.getStartingDate());
		int startingMonth=0;
		int session=DBM.getpromostionclass(String.valueOf(selectedStudent.getAddNumber()),conn);
		if(session==1)
			startingMonth=4;
		else
			startingMonth=Integer.parseInt(reportDate);

		tempFeeList=new ArrayList<>();
		for(FeeInfo rr:classFeeList)
		{
			if(!rr.getFeeName().equals("Transport"))
			{
				if(rr.getFeeType().equals("year"))
				{
					int fee=rr.getFeeAmount();
					int feepaidAmount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					int leftamount=fee-feepaidAmount;
					FeeInfo info=new FeeInfo();
					info.setFeeName(rr.getFeeName());
					info.setTotalFeeAmount(fee);
					info.setTotalFeePaidAmount(feepaidAmount);
					info.setTotalFeeLeftAmount(leftamount);
					tempFeeList.add(info);
					fee=feepaidAmount=leftamount=0;

				}
				else if(rr.getFeeType().equals("month"))
				{
					int totalpaidamount=0;
					totalpaidamount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					for(int i=startingMonth;i<=15;i++)
					{
						int fee=rr.getFeeAmount();
						if(fee<=totalpaidamount)
						{
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(fee);
							info.setTotalFeeLeftAmount(0);
							tempFeeList.add(info);
							totalpaidamount=totalpaidamount-fee;
						}
						else
						{
							int leftamount=fee-totalpaidamount;
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(totalpaidamount);
							info.setTotalFeeLeftAmount(leftamount);
							tempFeeList.add(info);
						}
					}
				}
				else if(rr.getFeeType().equals("quarter"))
				{
					if(startingMonth>=4&&startingMonth<=6)
					{
						startingMonth=1;
					}
					else if(startingMonth>=7&&startingMonth<=9)
					{
						startingMonth=2;
					}
					else if(startingMonth>=10&&startingMonth<=12)
					{
						startingMonth=3;
					}
					else if(startingMonth>=1&&startingMonth<=3)
					{
						startingMonth=4;
					}

					int totalpaidamount=0;
					totalpaidamount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					for(int i=startingMonth;i<=4;i++)
					{
						int fee=rr.getFeeAmount();
						if(fee<=totalpaidamount)
						{
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(fee);
							info.setTotalFeeLeftAmount(0);
							tempFeeList.add(info);
							totalpaidamount=totalpaidamount-fee;
						}
						else
						{
							int leftamount=fee-totalpaidamount;
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(totalpaidamount);
							info.setTotalFeeLeftAmount(leftamount);
							tempFeeList.add(info);
						}
					}
				}
			}
		}

		int transportFeePaid=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,"0",conn);  //already paid student transport fee

		transportfeeStatus=new ArrayList<>();
		ArrayList<Transport> transportFeeDetails=DBM.transportListDetails(DBM.schoolId(),selectedStudent.getAddNumber(),preSession,conn);
		for(Transport t: transportFeeDetails)
		{
			FeeStatus fs=new FeeStatus();
			fs.setTransportRouteName(DBM.transportRouteNameFromId(DBM.schoolId(),String.valueOf(t.getRouteId()),preSession,conn));
			fs.setMonth(t.getMonth());
			fs.setStatus(t.getStatus());

			ArrayList<ClassInfo> transportFeeList = DBM.transportRouteDetailsWithFee(DBM.schoolId(),t.getRouteId(),preSession,conn);

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
					info.setBalanceLeft(String.valueOf(transportFee-transportFeePaid));
					firstMonthTransportPeriod=info.getMonth();
					transportFeeLeft+=transportFee-transportFeePaid;
					transportFeePaid=0;
				}
				else
				{
					if(flag==0)
					{
						info.setStatus("0 paid");
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

		int fee=0,totalpaidamount=0,leftamount=0;

		for(FeeInfo ll:classFeeList)
		{
			fee=totalpaidamount=leftamount=0;
			String feeName=ll.getFeeName();
			for(FeeInfo info:tempFeeList)
			{
				if(info.getFeeName().equals(feeName))
				{
					fee+=info.getTotalFeeAmount();
					totalpaidamount+=info.getTotalFeePaidAmount();
					leftamount+=info.getTotalFeeLeftAmount();
				}
			}
			FeeInfo info=new FeeInfo();
			info.setFeeName(ll.getFeeName());
			info.setTotalFeeAmount(fee);
			info.setTotalFeePaidAmount(totalpaidamount);
			info.setTotalFeeLeftAmount(leftamount);
			feeStructureList2.add(info);
		}

		fee=totalpaidamount=leftamount=0;
		for(FeeStatus ll: transportfeeStatus)
		{
			fee+=Integer.parseInt(ll.getTransportFee());
			leftamount+=Integer.parseInt(ll.getBalanceLeft());
		}
		FeeInfo info1=new FeeInfo();
		info1.setFeeName("Transport Fee");
		info1.setTotalFeeAmount(fee);
		info1.setTotalFeePaidAmount(fee-leftamount);
		info1.setTotalFeeLeftAmount(leftamount);
		tempFeeList.add(info1);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void calculateStudentFee(StudentInfo selectedStudent)
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		preSession=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		classFeeList=DBM.classFees(selectedStudent.getClassId(),/*preSession,*/conn);

		DateFormat df = new SimpleDateFormat("MM");
		String reportDate = df.format(selectedStudent.getStartingDate());
		int startingMonth=0;
		int session=DBM.getpromostionclass(String.valueOf(selectedStudent.getAddNumber()),conn);
		if(session==1)
			startingMonth=4;
		else
			startingMonth=Integer.parseInt(reportDate);

		tempFeeList=new ArrayList<>();
		for(FeeInfo rr:classFeeList)
		{
			if(!rr.getFeeName().equals("Transport"))
			{
				if(rr.getFeeType().equals("year"))
				{
					int fee=rr.getFeeAmount();
					int feepaidAmount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					int leftamount=fee-feepaidAmount;
					FeeInfo info=new FeeInfo();
					info.setFeeName(rr.getFeeName());
					info.setTotalFeeAmount(fee);
					info.setTotalFeePaidAmount(feepaidAmount);
					info.setTotalFeeLeftAmount(leftamount);
					tempFeeList.add(info);
					fee=feepaidAmount=leftamount=0;

				}
				else if(rr.getFeeType().equals("month"))
				{
					int totalpaidamount=0;
					totalpaidamount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					for(int i=startingMonth;i<=15;i++)
					{
						int fee=rr.getFeeAmount();
						if(fee<=totalpaidamount)
						{
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(fee);
							info.setTotalFeeLeftAmount(0);
							tempFeeList.add(info);
							totalpaidamount=totalpaidamount-fee;
						}
						else
						{
							int leftamount=fee-totalpaidamount;
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(totalpaidamount);
							info.setTotalFeeLeftAmount(leftamount);
							tempFeeList.add(info);
						}
					}
				}
				else if(rr.getFeeType().equals("quarter"))
				{
					if(startingMonth>=4&&startingMonth<=6)
					{
						startingMonth=1;
					}
					else if(startingMonth>=7&&startingMonth<=9)
					{
						startingMonth=2;
					}
					else if(startingMonth>=10&&startingMonth<=12)
					{
						startingMonth=3;
					}
					else if(startingMonth>=1&&startingMonth<=3)
					{
						startingMonth=4;
					}

					int totalpaidamount=0;
					totalpaidamount=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,rr.getFeeId(),conn);
					for(int i=startingMonth;i<=4;i++)
					{
						int fee=rr.getFeeAmount();
						if(fee<=totalpaidamount)
						{
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(fee);
							info.setTotalFeeLeftAmount(0);
							tempFeeList.add(info);
							totalpaidamount=totalpaidamount-fee;
						}
						else
						{
							int leftamount=fee-totalpaidamount;
							FeeInfo info=new FeeInfo();
							info.setFeeName(rr.getFeeName());
							info.setTotalFeeAmount(fee);
							info.setTotalFeePaidAmount(totalpaidamount);
							info.setTotalFeeLeftAmount(leftamount);
							tempFeeList.add(info);
						}
					}
				}
			}

		}

		int transportFeePaid=DBM.FeePaidRecord(DBM.schoolId(),selectedStudent,preSession,"0",conn);  //already paid student transport fee

		transportfeeStatus=new ArrayList<>();
		ArrayList<Transport> transportFeeDetails=DBM.transportListDetails(DBM.schoolId(),selectedStudent.getAddNumber(),preSession,conn);
		for(Transport t: transportFeeDetails)
		{
			FeeStatus fs=new FeeStatus();
			fs.setTransportRouteName(DBM.transportRouteNameFromId(DBM.schoolId(),String.valueOf(t.getRouteId()),preSession,conn));
			fs.setMonth(t.getMonth());
			fs.setStatus(t.getStatus());

			ArrayList<ClassInfo> transportFeeList = DBM.transportRouteDetailsWithFee(DBM.schoolId(),t.getRouteId(),preSession,conn);

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
					info.setBalanceLeft(String.valueOf(transportFee-transportFeePaid));
					firstMonthTransportPeriod=info.getMonth();
					transportFeeLeft+=transportFee-transportFeePaid;
					transportFeePaid=0;
				}
				else
				{
					if(flag==0)
					{
						info.setStatus("0 paid");
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

		int fee=0,totalpaidamount=0,leftamount=0;

		for(FeeInfo ll:classFeeList)
		{
			fee=totalpaidamount=leftamount=0;
			String feeName=ll.getFeeName();
			for(FeeInfo info:tempFeeList)
			{
				if(info.getFeeName().equals(feeName))
				{
					fee+=info.getTotalFeeAmount();
					totalpaidamount+=info.getTotalFeePaidAmount();
					leftamount+=info.getTotalFeeLeftAmount();
				}
			}
			FeeInfo info=new FeeInfo();
			info.setFeeName(ll.getFeeName());
			info.setTotalFeeAmount(fee);
			info.setTotalFeePaidAmount(totalpaidamount);
			info.setTotalFeeLeftAmount(leftamount);
			feeStructureList1.add(info);
		}

		fee=totalpaidamount=leftamount=0;
		for(FeeStatus ll: transportfeeStatus)
		{
			fee+=Integer.parseInt(ll.getTransportFee());
			leftamount+=Integer.parseInt(ll.getBalanceLeft());
		}
		FeeInfo info1=new FeeInfo();
		info1.setFeeName("Transport Fee");
		info1.setTotalFeeAmount(fee);
		info1.setTotalFeePaidAmount(fee-leftamount);
		info1.setTotalFeeLeftAmount(leftamount);
		tempFeeList.add(info1);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}
	public boolean isShowSibling1() {
		return showSibling1;
	}

	public void setShowSibling1(boolean showSibling1) {
		this.showSibling1 = showSibling1;
	}

	public boolean isShowSibling2() {
		return showSibling2;
	}

	public void setShowSibling2(boolean showSibling2) {
		this.showSibling2 = showSibling2;
	}

	public String getFname1() {
		return fname1;
	}

	public void setFname1(String fname1) {
		this.fname1 = fname1;
	}

	public String getClassName1() {
		return className1;
	}

	public void setClassName1(String className1) {
		this.className1 = className1;
	}

	public String getfName2() {
		return fName2;
	}

	public void setfName2(String fName2) {
		this.fName2 = fName2;
	}

	public String getClassName2() {
		return className2;
	}

	public void setClassName2(String className2) {
		this.className2 = className2;
	}

	public String getAddNo1() {
		return addNo1;
	}

	public void setAddNo1(String addNo1) {
		this.addNo1 = addNo1;
	}

	public String getAddNo2() {
		return addNo2;
	}

	public void setAddNo2(String addNo2) {
		this.addNo2 = addNo2;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<FeeInfo> getFeeStructureList1() {
		return feeStructureList1;
	}

	public void setFeeStructureList1(ArrayList<FeeInfo> feeStructureList1) {
		this.feeStructureList1 = feeStructureList1;
	}

	public ArrayList<FeeInfo> getFeeStructureList2() {
		return feeStructureList2;
	}

	public void setFeeStructureList2(ArrayList<FeeInfo> feeStructureList2) {
		this.feeStructureList2 = feeStructureList2;
	}

	public boolean isShowOther() {
		return showOther;
	}

	public void setShowOther(boolean showOther) {
		this.showOther = showOther;
	}

}
