package reports_module;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;

@ManagedBean (name="stdDefaultList")
@ViewScoped
public class StudentDefaulterListForBLM implements Serializable
{
	String selectedSchool="All";
	ArrayList<FeeInfo> feeList=new ArrayList<>(),feeInfo;
	ArrayList<SelectItem> classList=new ArrayList<>(),installmentList,selectedInstallList;
	String [] checkMonthSelected;
	Date date;
	DataBaseMethodsReports obj=new DataBaseMethodsReports();
	String schoolId,sessionValue;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	ArrayList<FeeEstimateInfo> feeEstimateList=new ArrayList<>(),feeEstimateList1=new ArrayList<>(),feeEstimateList2=new ArrayList<>();
	// General category For 251 ='121'     252 ='122'

	public StudentDefaulterListForBLM()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		installment();
	}

	public void searchReport()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=dbm.allClass(selectedSchool,conn);
		feeInfo=dbm.viewFeeListForDues(selectedSchool,conn);
		FeeInfo ff=new FeeInfo();
		ff.setFeeName("Total");
		ff.setFeeId("Total");
		feeInfo.add(ff);

		selectedInstallList=new ArrayList<>();
		for(SelectItem ll:installmentList)
		{
			for(String ss:checkMonthSelected)
			{
				if(ss.equals(ll.getValue().toString()))
				{
					selectedInstallList.add(ll);
				}
			}
		}

		if(selectedSchool.equals("All"))
		{
			classList=dbm.allClass("251",conn);
			feeInfo=dbm.viewFeeListForDues("251",conn);
			feeEstimateList=obj.defaulterReport(selectedInstallList,classList,feeInfo,"251",conn);

			classList=dbm.allClass("252",conn);
			feeInfo=dbm.viewFeeListForDues("252",conn);
			feeEstimateList2=obj.defaulterReport(selectedInstallList,classList,feeInfo,"252",conn);

			Map<String, String> map=new HashMap<>();
			Map<String, String> map2=new HashMap<>();
			double amount=0,amount1=0;
			for(FeeEstimateInfo info:feeEstimateList)
			{
				for(FeeEstimateInfo info2:feeEstimateList2)
				{
					if(info.getInstallmentName().equals(info2.getInstallmentName()))
					{
						map=info.getFeeMap();
						map2=info2.getFeeMap();
						for(FeeInfo fee:feeInfo)
						{
							amount=amount1=0;
							amount=Double.parseDouble(map.get(fee.getFeeName()));
							amount1=Double.parseDouble(map2.get(fee.getFeeName()));
							map.put(fee.getFeeName(), BigDecimal.valueOf(amount+amount1).toPlainString());
						}
					}
				}
			}
		}
		else
		{
			feeEstimateList=obj.defaulterReport(selectedInstallList,classList,feeInfo,selectedSchool,conn);
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void installment()
	{
		installmentList = new ArrayList<>();

		SelectItem ss1 = new SelectItem();
		ss1.setLabel("April");
		ss1.setValue("4");
		installmentList.add(ss1);

		SelectItem ss2 = new SelectItem();
		ss2.setLabel("May-June");
		ss2.setValue("5,6");
		installmentList.add(ss2);

		SelectItem ss3 = new SelectItem();
		ss3.setLabel("Jul-Aug");
		ss3.setValue("7,8");
		installmentList.add(ss3);

		SelectItem ss4 = new SelectItem();
		ss4.setLabel("September");
		ss4.setValue("9");
		installmentList.add(ss4);

		SelectItem ss5 = new SelectItem();
		ss5.setLabel("Oct-Nov");
		ss5.setValue("10,11");
		installmentList.add(ss5);

		SelectItem ss6 = new SelectItem();
		ss6.setLabel("December");
		ss6.setValue("12");
		installmentList.add(ss6);

		SelectItem ss7 = new SelectItem();
		ss7.setLabel("January");
		ss7.setValue("1");
		installmentList.add(ss7);

		SelectItem ss8 = new SelectItem();
		ss8.setLabel("Feb-Mar");
		ss8.setValue("2,3");
		installmentList.add(ss8);
	}

	public String getSelectedSchool() {
		return selectedSchool;
	}

	public void setSelectedSchool(String selectedSchool) {
		this.selectedSchool = selectedSchool;
	}

	public ArrayList<FeeInfo> getFeeList() {
		return feeList;
	}

	public void setFeeList(ArrayList<FeeInfo> feeList) {
		this.feeList = feeList;
	}

	public ArrayList<FeeInfo> getFeeInfo() {
		return feeInfo;
	}

	public void setFeeInfo(ArrayList<FeeInfo> feeInfo) {
		this.feeInfo = feeInfo;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public ArrayList<SelectItem> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(ArrayList<SelectItem> installmentList) {
		this.installmentList = installmentList;
	}

	public ArrayList<FeeEstimateInfo> getFeeEstimateList() {
		return feeEstimateList;
	}

	public void setFeeEstimateList(ArrayList<FeeEstimateInfo> feeEstimateList) {
		this.feeEstimateList = feeEstimateList;
	}

	public String[] getCheckMonthSelected() {
		return checkMonthSelected;
	}

	public void setCheckMonthSelected(String[] checkMonthSelected) {
		this.checkMonthSelected = checkMonthSelected;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList<SelectItem> getSelectedInstallList() {
		return selectedInstallList;
	}

	public void setSelectedInstallList(ArrayList<SelectItem> selectedInstallList) {
		this.selectedInstallList = selectedInstallList;
	}
}
