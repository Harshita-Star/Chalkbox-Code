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
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;
@ManagedBean(name="oldStudentDailyFeeCollectionReport")
@ViewScoped
public class OldStudentDailyFeeCollectionReport implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	Date feedate=new Date(),endDate;
	String date;
	boolean show;
	String remark,selectedOperator;
	String feetype,studentclass,studentname,fathername,selectedClass,selectedSection;
	ArrayList<DailyFeeCollectionBean> feedetail,dailycollection,dailyfee=new ArrayList<>();
	int cashAmount,chequeAmount;
	static int count=1;
	ArrayList<Feecollectionc> getdailyfeecollection;
	ArrayList<SelectItem> classList,sectionList,operatorList;
	DailyFeeCollectionBean selectedstudent;
	ArrayList<StudentInfo> studentList;
	String name;
	ArrayList<FeeInfo>feelist;
	double tamount,tdiscount;
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	public OldStudentDailyFeeCollectionReport()
	{
		selectedOperator = "all";
		Connection conn = DataBaseConnection.javaConnection();
		feedate=new Date();
		endDate=new Date();
		operatorList = new DatabaseMethods1().allOperatorList(conn);
		showdailyfeelist();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void showdailyfeelist()
	{
		Connection conn=DataBaseConnection.javaConnection();
		count=1;cashAmount=0;chequeAmount=0;tamount=tdiscount=0;
		dailyfee=new ArrayList<>();

		feelist=new DatabaseMethods1().viewFeeList1(new DatabaseMethods1().schoolId(),conn);

		HashMap<String, String> tempMap=new HashMap<>();
		getdailyfeecollection=new DatabaseMethods1().tempGetfeedailydetailForOldStudent(feedate,/*selectedSection,*/endDate,tempMap,conn,selectedOperator);
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
					ll.setStudentid(list.getStudentid());
					ll.setUser(list.getUser());
					ll.setSrNo(list.getSrNo());
					ll.setMothername(list.getMotherName());
					ll.setSection(list.getSectionName());
					ll.setFeeRemark(list.getFeeRemark());
					ll.setFeedate(list.getFeedate());
					ll.setChallanDate(list.getChallanDate());
					ll.setDueDateStr(list.getDueDateStr());
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

		date=sdf.format(new Timestamp(feedate.getTime()))+"-"+sdf.format(new Timestamp(endDate.getTime()));
		show=true;

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
	public void duplicateFee()
	{
		HttpSession rr=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		rr.setAttribute("selectedStudent", selectedstudent);
		rr.setAttribute("type1", "duplicate");
		rr.setAttribute("chaqueDate", selectedstudent.getChallanDate());
		rr.setAttribute("paymentmode", selectedstudent.getPaymentmode());
		rr.setAttribute("bankname", selectedstudent.getBankname());
		rr.setAttribute("chequeno",selectedstudent.getChequenumber());
		rr.setAttribute("remark", selectedstudent.getFeeRemark());
		rr.setAttribute("type1","duplicate");
		rr.setAttribute("receiptNumber", String.valueOf(selectedstudent.getReciptno()));
		rr.setAttribute("feeupto", selectedstudent.getDueDateStr());
		ArrayList<FeeInfo> selectedFees=new ArrayList<>();
		int i=1;

		for(FeeInfo ff:feelist)
		{
			String fee=selectedstudent.getAllFess().get(ff.getFeeId());
			String disc=selectedstudent.getAllDiscount().get(ff.getFeeId());
			String totalAmt=selectedstudent.getAllTotalAmount().get(ff.getFeeId());
			if(fee==null)
			{

			}
			else
			{

				FeeInfo info=new FeeInfo();
				info.setSno(i);
				info.setFeeName(ff.getFeeName());
				info.setPayAmount(Integer.parseInt(fee));
				info.setPayDiscount(Integer.valueOf(disc));
				info.setDueamount(totalAmt);
				selectedFees.add(info);
				i=i+1;
			}
		}
		rr.setAttribute("selectedFee", selectedFees);
		rr.setAttribute("receiptDate", selectedstudent.getFeedate());
		PrimeFaces.current().executeInitScript("window.open('FeeReceipt1.xhtml')");

	}
	public Date getFeedate() {
		return feedate;
	}
	public void setFeedate(Date feedate) {
		this.feedate = feedate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSelectedOperator() {
		return selectedOperator;
	}
	public void setSelectedOperator(String selectedOperator) {
		this.selectedOperator = selectedOperator;
	}
	public String getFeetype() {
		return feetype;
	}
	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}
	public String getStudentclass() {
		return studentclass;
	}
	public void setStudentclass(String studentclass) {
		this.studentclass = studentclass;
	}
	public String getStudentname() {
		return studentname;
	}
	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}
	public String getFathername() {
		return fathername;
	}
	public void setFathername(String fathername) {
		this.fathername = fathername;
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
	public ArrayList<DailyFeeCollectionBean> getFeedetail() {
		return feedetail;
	}
	public void setFeedetail(ArrayList<DailyFeeCollectionBean> feedetail) {
		this.feedetail = feedetail;
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
		OldStudentDailyFeeCollectionReport.count = count;
	}
	public ArrayList<Feecollectionc> getGetdailyfeecollection() {
		return getdailyfeecollection;
	}
	public void setGetdailyfeecollection(
			ArrayList<Feecollectionc> getdailyfeecollection) {
		this.getdailyfeecollection = getdailyfeecollection;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public ArrayList<SelectItem> getOperatorList() {
		return operatorList;
	}
	public void setOperatorList(ArrayList<SelectItem> operatorList) {
		this.operatorList = operatorList;
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
	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}
	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
