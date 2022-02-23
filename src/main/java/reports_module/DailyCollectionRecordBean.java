package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.FeeInfo;

@ManagedBean(name="dailyFeeCollection")
@ViewScoped
public class DailyCollectionRecordBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate,endDate;
	String date;
	ArrayList<FeeReportInfo> feeList;
	ArrayList<FeeInfo> classFeeList;
	boolean showData,showPrintButton;
	DatabaseMethods1 dbm = new DatabaseMethods1(); 
	String schoolId,sessionValue;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();


	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId, conn);
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		date=sdf.format(startDate)+" - "+sdf.format(endDate);
		classFeeList=dbm.viewFeeList1(schoolId, conn);

		FeeInfo ff=new FeeInfo();
		ff.setFeeName("Cash");
		classFeeList.add(ff);

		ff=new FeeInfo();
		ff.setFeeName("Cheque / Bank Transfer / Challan");
		classFeeList.add(ff);

		ff=new FeeInfo();
		ff.setFeeName("Total");
		classFeeList.add(ff);

		feeList=dbr.dailyCollectionRecord(startDate,endDate,classFeeList,conn);
		if(feeList.size()>0)
		{
			showPrintButton=true;
		}
		else
		{
			showPrintButton=false;
		}
		showData=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<FeeReportInfo> getFeeList() {
		return feeList;
	}
	public void setFeeList(ArrayList<FeeReportInfo> feeList) {
		this.feeList = feeList;
	}
	public ArrayList<FeeInfo> getClassFeeList() {
		return classFeeList;
	}
	public void setClassFeeList(ArrayList<FeeInfo> classFeeList) {
		this.classFeeList = classFeeList;
	}
	public boolean isShowData() {
		return showData;
	}
	public void setShowData(boolean showData) {
		this.showData = showData;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public boolean isShowPrintButton() {
		return showPrintButton;
	}

	public void setShowPrintButton(boolean showPrintButton) {
		this.showPrintButton = showPrintButton;
	}
}
