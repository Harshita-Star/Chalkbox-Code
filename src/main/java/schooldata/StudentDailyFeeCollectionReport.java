package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.DatabaseMethodSession;
import session_work.RegexPattern;
@ViewScoped
@ManagedBean(name="studentDailyFeeCollectionReport")
public class StudentDailyFeeCollectionReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	Date feedate=new Date(),endDate;
	String date;
	boolean show;
	String remark;
	String feetype,studentclass,studentname,fathername,selectedClass,selectedSection;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	int cashAmount,chequeAmount;
	static int count=1;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<SelectItem> classList,sectionList;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<StudentInfo> studentList;
	String name;
	double tamount,tdiscount;
	ArrayList<FeeInfo>feelist;

	public StudentDailyFeeCollectionReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		feedate=new Date();
		endDate=new Date();
		//classList=new DatabaseMethods1().allClass(conn);
		//selectedSection="-1";
		//showdailyfeelist();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=new DatabaseMethods1().allSection(selectedClass,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void showdailyfeelist()
	{
		Connection conn=DataBaseConnection.javaConnection();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		dailyfee=new ArrayList<>();

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss1.getAttribute("schoolid");

		feelist=new DatabaseMethods1().viewFeeList1(new DatabaseMethods1().schoolId(), conn);

		HashMap<String, String> tempMap=new HashMap<>();
		getdailyfeecollection=new DatabaseMethods1().tempGetfeedailydetail(feedate,selectedSection,endDate,tempMap,conn,"all");
		ArrayList<String> temp=new ArrayList<>();
		for(Feecollectionc mm:getdailyfeecollection)
		{
			temp.add(mm.getRecipietNo());
		}
		String a[]=new String[temp.size()];
		for(int i=0;i<temp.size();i++)
		{
			a[i]=temp.get(i);
		}
		a=removeDuplicates(a);

		String value="";
		ArrayList<String>tempdeatils;
		for(int i=0;i<a.length;i++)
		{
			tempdeatils=new ArrayList<>();
			HashMap<String, String> feecllection=new HashMap<>();
			HashMap<String, String> totalAmoutMap=new HashMap<>();
			HashMap<String, String> discountMap=new HashMap<>();
			DailyFeeCollectionBean ll=new DailyFeeCollectionBean();
			int totalamuont=0,totalDiscount=0;

			for(Feecollectionc list : getdailyfeecollection)
			{

				
				if(list.getRecipietNo().equals(String.valueOf(a[i])))
				{
					ll.setStudentname(list.getStudentname());
					ll.setClassname(list.getClassName());
					ll.setFathername(list.getFathername());
					////// // System.out.println(list.getAddNo());
					ll.setStudentid(list.getAddNo());
					ll.setSrNo(list.getSrNo());
					ll.setUser(list.getUser());
					ll.setMothername(list.getMotherName());
					ll.setDueDateStr(list.getDueDateStr());
					ll.setSection(list.getSectionName());
					ll.setFeeRemark(list.getFeeRemark());
					ll.setFeedate(list.getFeedate());
					ll.setChallanDate(list.getChallanDate());
					ll.setReciptno(list.getRecipietNo());
					ll.setFeeDateStr(sdf.format(list.getFeedate()));
					String feetype=list.getFeeName();
					feecllection.put(feetype, String.valueOf(list.getFeeamunt()));
					discountMap.put(feetype, String.valueOf(list.getDiscount()));
					totalAmoutMap.put(feetype, String.valueOf(list.getTotalAmount()));
					ll.setAllFess(feecllection);
					ll.setAllDiscount(discountMap);
					ll.setAllTotalAmount(totalAmoutMap);
					ll.setBankname(list.getBankname());
					ll.setChequenumber(list.getChequeno());
					ll.setPaymentmode(list.getPaymentmode());
					if(tempdeatils.size()==0)
					{

						value="orignal";
					}
					else
					{
						for(String ls:tempdeatils)
						{
							if(ls.equals(feetype))
							{
								value="duplicate";
								break;
							}
							else
							{
								value="orignal";
							}
						}

					}

					if(value.equals("orignal"))
					{
						tempdeatils.add(feetype);
						totalamuont+=list.getFeeamunt();
						totalDiscount+=list.getDiscount();
					}

					ll.setAmount(String.valueOf(totalamuont));
					ll.setDiscount(String.valueOf(totalDiscount));

					ll.setPaymentmode(list.getPaymentmode());
					ll.setSrno(count);

				}



			}
			dailyfee.add(ll);
			count++;
			show=true;
		}
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

		date=new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(feedate.getTime()))+"-"+new SimpleDateFormat("dd/MM/yyyy").format(new Timestamp(endDate.getTime()));
		show=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> autoCompleteStudentInfo(String query)
	{
		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethodSession().searchStudentListWithPreSessionStudent("byName",query, "full", conn,"","");
		
		List<String> studentListt = new ArrayList<>();

		for (StudentInfo info : studentList)
		{
			//studentListt.add(info.getFname() + "-" + info.getAddNumber());
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentListt;
	}

	public void duplicateFee()
	{
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());

		Connection conn=DataBaseConnection.javaConnection();
		
		DatabaseMethods1 obj=new DatabaseMethods1();
		ArrayList<Feecollectionc>feesSelected=obj.studetFeeCollectionByRecipietNo(selectedstudent.getStudentid(),selectedstudent.getReciptno(),selectedstudent.getSchid(),conn);
		
		
		
		ArrayList<FeeInfo> selectedFees=new ArrayList<>();
		int i=1;

		for(Feecollectionc ff:feesSelected)
		{
			/*String fee=selectedstudent.getAllFess().get(ff.getFeeId());
			String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
			String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
			if(fee==null)
			{

			}
			else
			{*/

				FeeInfo info=new FeeInfo();
				info.setSno(i);
				info.setFeeName(ff.getFeeName());
				info.setPayAmount(ff.getFeeamunt());
				info.setPayDiscount(ff.getDiscount());
				info.setDueamount(String.valueOf(ff.getTotalAmount()));

				selectedFees.add(info);
				i=i+1;
			/*}*/
		}



		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

	}

	public void searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM=new DatabaseMethods1();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		feelist=DBM.viewFeeList1(DBM.schoolId(),conn);

		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String schid = (String) ss1.getAttribute("schoolid");

		dailyfee=new ArrayList<>();
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		int index=name.lastIndexOf(":")+1;
		String id=name.substring(index);
		Date lockDate = DBM.checkLockDate(schid,conn);
			HashMap<String, String> tempMap=new HashMap<>();
			dailyfee=new DatabaseMethods1().dailyFeeReportHeaderWiseForStudentBLM(schid,id,tempMap, conn);
		if(dailyfee.size()>0)
		{
			show = true;
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

	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
	}

	public void cancelReceipt()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().cancelReceipt(selectedstudent.getReciptno(),remark,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Receipt Cancelled Sucessfully"));
			show=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public Date getFeedate() {
		return feedate;
	}
	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}
	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}
	public void setGetdailyfeecollection(
			ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailycollection() {
		return dailycollection;
	}
	public void setDailycollection(ArrayList<DailyFeeCollectionBean> dailycollection) {
		this.dailycollection = dailycollection;
	}
	public ArrayList<DailyFeeCollectionBean> getDailyfee() {
		return dailyfee;
	}
	public void setDailyfee(ArrayList<DailyFeeCollectionBean> dailyfee) {
		this.dailyfee = dailyfee;
	}
	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
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
	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
	}
	public String getDate() {
		return date;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
