package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DailyFeeCollectionBean;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name = "tcStudentFeeStatement")
@ViewScoped

public class TCStudentFeeStatementBean implements Serializable 
{
	String studentid, className,sectionName, month;
	ArrayList<FeeInfo>feelist;
	String date;
	ArrayList<DailyFeeCollectionBean> dailyfee=new ArrayList<>();
	int cashAmount,chequeAmount,totalStudent;
	static int count=1;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<StudentInfo> studentList;
	double tamount,tdiscount,totalDueAmount;
	Date leavingDate;
	public TCStudentFeeStatementBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		studentid = (String) ss.getAttribute("studentid");
		leavingDate = (Date) ss.getAttribute("leavingDate");
		searchStudentPaid();
		searchStudentDues();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentPaid()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		feelist=DBM.viewFeeList1(DBM.schoolId(),conn);

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss1.getAttribute("schoolid");

		dailyfee=new ArrayList<>();
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		Date lockDate = DBM.checkLockDate(schid,conn);
			HashMap<String, String> tempMap=new HashMap<>();
			dailyfee=new DatabaseMethods1().dailyFeeReportHeaderWiseForStudentBLM(schid,studentid,tempMap, conn);
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
				tdiscount+=Integer.parseInt(info.getDiscount());
			}
			tamount=cashAmount+chequeAmount;
		}

		
			
		date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void searchStudentDues()
	{
		totalDueAmount=0;
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		try
		{
			SchoolInfoList list1=DBM.fullSchoolInfo(conn);

			String session=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);

			studentList=DBM.searchStudentListForDueFeeReport(studentid,leavingDate,session,conn,"dueReport", "allStatus");
			SchoolInfoList info = DBM.fullSchoolInfo(conn);
			if(info.getBranch_id().equals("54"))
			{
				Collections.sort(studentList, new MySalaryComp());
			}
			if(studentList.size()>0)
			{
				for(StudentInfo ll:studentList)
				{
					totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				}
				String selectedCLassSection=studentList.get(0).getClassId();
				String selectedSection=studentList.get(0).getSectionid();
				feelist=DBM.classFeesForStudentForDues(selectedCLassSection,session,studentList.get(0).getStudentStatus(),studentList.get(0).getConcession(),conn);
				feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);

				className=DBM.classNameFromidSchid(DBM.schoolId(),selectedCLassSection,session,conn);
				sectionName=DBM.sectionNameByIdSchid(DBM.schoolId(),selectedSection,conn);
				totalStudent=studentList.size();
				month=studentList.get(0).getMonth();

			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No Record found", "Validation error"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	class MySalaryComp implements Comparator<StudentInfo>
	{
		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{

			String srno1 = e1.getSrNo().substring(4, e1.getSrNo().length());
			String srno2 = e2.getSrNo().substring(4, e2.getSrNo().length());

			int sr1 = Integer.parseInt(srno1);
			int sr2 = Integer.parseInt(srno2);

			if(sr1 >= sr2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	public String getStudentid() {
		return studentid;
	}

	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}

	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}

	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}

	public int getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(int cashAmount) {
		this.cashAmount = cashAmount;
	}

	public int getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(int chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		TCStudentFeeStatementBean.count = count;
	}

	public DailyFeeCollectionBean getSelectedstudent() {
		return selectedstudent;
	}

	public void setSelectedstudent(DailyFeeCollectionBean selectedstudent) {
		this.selectedstudent = selectedstudent;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}

	public double getTamount() {
		return tamount;
	}

	public void setTamount(double tamount) {
		this.tamount = tamount;
	}

	public double getTdiscount() {
		return tdiscount;
	}

	public void setTdiscount(double tdiscount) {
		this.tdiscount = tdiscount;
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

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getTotalStudent() {
		return totalStudent;
	}

	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}

}
