package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="feeStructure")
@ViewScoped
public class FeeStructureReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	Date startDate;
	String date,selectedClass="-1",session;
	ArrayList<SelectItem> classList;
	ArrayList<ClassInfo> classDetailList=new ArrayList<>();
	boolean showData,showPrintButton;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	DataBaseMethodsReports objReport = new DataBaseMethodsReports();
	String schoolId;

	public FeeStructureReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=obj1.schoolId();
		session=obj1.selectedSessionDetails(schoolId,conn);
		classList=obj1.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedClass.equals("-1"))
		{
			classDetailList=obj1.allClassList("", conn);
		}
		else
		{
			ClassInfo ll=new ClassInfo();
			ll.setClassName(obj1.classNameFromidSchid(schoolId,selectedClass, session,conn));
			ll.setGroupid(selectedClass);
			classDetailList.add(ll);
		}

		//date=new SimpleDateFormat("dd-MM-yyyy").format(startDate);

		objReport.feeStructureReport(/*startDate,*/classDetailList,session,conn);

		int totalnew=0,totalOld=0;
		for(ClassInfo info:classDetailList)
		{
			totalnew=totalOld=0;
			for(FeeReportInfo fee:info.getFeeList())
			{
				if(fee.getFeeName().equalsIgnoreCase("Total") && fee.getFeeFor().equalsIgnoreCase("New"))
				{
					totalnew+=fee.getAprFee()+fee.getMayFee()+fee.getJunFee()+fee.getJulyFee()+fee.getAugFee()+fee.getSepFee()+fee.getOctFee()+fee.getNovFee()+fee.getDecFee()+fee.getJanFee()+fee.getFebFee()+fee.getMarFee();
				}
				if(fee.getFeeName().equalsIgnoreCase("Total") && fee.getFeeFor().equalsIgnoreCase("Old"))
				{
					totalOld+=fee.getAprFee()+fee.getMayFee()+fee.getJunFee()+fee.getJulyFee()+fee.getAugFee()+fee.getSepFee()+fee.getOctFee()+fee.getNovFee()+fee.getDecFee()+fee.getJanFee()+fee.getFebFee()+fee.getMarFee();
				}
			}
			info.setTotalNew(totalnew);
			info.setTotalOld(totalOld);
		}

		if(classDetailList.size()>0)
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public ArrayList<ClassInfo> getClassDetailList() {
		return classDetailList;
	}

	public void setClassDetailList(ArrayList<ClassInfo> classDetailList) {
		this.classDetailList = classDetailList;
	}

	public boolean isShowPrintButton() {
		return showPrintButton;
	}

	public void setShowPrintButton(boolean showPrintButton) {
		this.showPrintButton = showPrintButton;
	}
}
