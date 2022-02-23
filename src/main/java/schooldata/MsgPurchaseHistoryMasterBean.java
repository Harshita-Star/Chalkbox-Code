package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import reports_module.DataBaseMethodsReports;

@ManagedBean(name="msgPurchaseHistoryMaster")
@ViewScoped
public class MsgPurchaseHistoryMasterBean implements Serializable{
	ArrayList<SelectItem> schoolList = new ArrayList<>(),sessionList = new ArrayList<>();
	ArrayList<MessagePackInfo>smsList = new ArrayList<>();
	MessagePackInfo selected = new MessagePackInfo();
	String type,schoolId,action,session;
	double quantity,amount,tax,totalAmount;
	double qSum=0.0,aSum=0.0,tSum=0.0,taSum=0.0;
	int count1,count2,count3;
	boolean showType;
	public MsgPurchaseHistoryMasterBean()
	{
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;
		
		sessionList=new ArrayList<>();
		for(int i=2016;i<=year;i++)
		{
			SelectItem item=new SelectItem();
			item.setLabel(String.valueOf(i)+"-"+String.valueOf(i+1));
			item.setValue(String.valueOf(i)+"-"+String.valueOf(i+1));

			sessionList.add(item);
		}

		if(month>=4)
		{
			session=String.valueOf(year)+"-"+String.valueOf(year+1);
		}
		else
		{
			session=String.valueOf(year-1)+"-"+String.valueOf(year);
		}
		
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		showType=false;
		action="all";
		allData();
		try
		{
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allData()
	{
		action="all";
		showType=true;
		Connection conn = DataBaseConnection.javaConnection();

		String tempArr[] = session.split("-");
		String startYear = tempArr[0];
		String endYear = tempArr[1];
		String startDate = startYear+"-04-01";
		String endDate = endYear+"-03-31";
		
		smsList=new DatabaseMethods1().selectAllMsgPackHistry(startDate,endDate,"all","all",conn);
		qSum=aSum=tSum=taSum=0.0;
		for(MessagePackInfo ss:smsList)
		{
			quantity=Double.valueOf(ss.getQuantity());
			amount=Double.valueOf(ss.getAmount());
			tax=Double.valueOf(ss.getTax());
			totalAmount=Double.valueOf(ss.getTotalAmount());
			qSum=qSum+quantity;
			aSum=aSum+amount;
			tSum=tSum+tax;
			taSum=taSum+totalAmount;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void show()
	{
		action="filter";
		showType=true;
		Connection conn = DataBaseConnection.javaConnection();

		String tempArr[] = session.split("-");
		String startYear = tempArr[0];
		String endYear = tempArr[1];
		String startDate = startYear+"-04-01";
		String endDate = endYear+"-03-31";
		
		smsList=new DatabaseMethods1().selectAllMsgPackHistry(startDate,endDate,schoolId,"all",conn);
		qSum=aSum=tSum=taSum=0.0;
		for(MessagePackInfo ss:smsList)
		{
			quantity=Double.valueOf(ss.getQuantity());
			amount=Double.valueOf(ss.getAmount());
			tax=Double.valueOf(ss.getTax());
			totalAmount=Double.valueOf(ss.getTotalAmount());
			qSum=qSum+quantity;
			aSum=aSum+amount;
			tSum=tSum+tax;
			taSum=taSum+totalAmount;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void report()
	{
		if(action.equalsIgnoreCase("all"))
		{
			allData();
		}
		else
		{
			show();
		}
	}

	public void paid()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DataBaseMethodsReports().updatePaidStatusMsgPurchase(selected.getId(),"paid",conn);
		if(i>=1)
		{
			selected.setPaidStatus("paid");
			selected.setShow(false);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Updated Successfully!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong. Please Try Again!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<MessagePackInfo> getSmsList() {
		return smsList;
	}

	public void setSmsList(ArrayList<MessagePackInfo> smsList) {
		this.smsList = smsList;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}


	public boolean isShowType() {
		return showType;
	}

	public void setShowType(boolean showType) {
		this.showType = showType;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getqSum() {
		return qSum;
	}

	public void setqSum(double qSum) {
		this.qSum = qSum;
	}

	public double getaSum() {
		return aSum;
	}

	public void setaSum(double aSum) {
		this.aSum = aSum;
	}

	public double gettSum() {
		return tSum;
	}

	public void settSum(double tSum) {
		this.tSum = tSum;
	}

	public double getTaSum() {
		return taSum;
	}

	public void setTaSum(double taSum) {
		this.taSum = taSum;
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MessagePackInfo getSelected() {
		return selected;
	}

	public void setSelected(MessagePackInfo selected) {
		this.selected = selected;
	}

	public ArrayList<SelectItem> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<SelectItem> sessionList) {
		this.sessionList = sessionList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}


}