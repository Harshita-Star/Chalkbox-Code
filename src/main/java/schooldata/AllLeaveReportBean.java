package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


@ManagedBean(name="leaveReport")
@ViewScoped
public class AllLeaveReportBean implements Serializable {

	private static final long serialVersionUID = 1L;
	Date startDate,endDate;
	ArrayList<EmployeInfo> reportsList;
	boolean showTable=false;
	EmployeInfo selectedItem;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public AllLeaveReportBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		endDate=new Date();
		startDate=new Date();
		startDate.setDate(1);
		reportsList=new ArrayList<>();
		reportsList=obj.allLeaveReport(startDate, endDate,conn);
		if(reportsList.size()>0)
		{
			showTable=true;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void searchReport()
	{
		Connection conn= DataBaseConnection.javaConnection();
		reportsList=obj.allLeaveReport(startDate, endDate,conn);
		if(reportsList.size()>0)
		{
			showTable=true;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No reports for selected dates"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public void deleteLeave()
	{
		Connection conn= DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		int i=obj.deleteLeaveById(selectedItem.getId(),conn);
		if(i==1)
		{
			 String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			context.addMessage(null, new FacesMessage("Leave Deleted SuccesFully"));
			reportsList=obj.allLeaveReport(startDate, endDate,conn);
		}
		else
		{
			context.addMessage(null, new FacesMessage("Error Occured..... Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Leave Id-"+selectedItem.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Leave","WEB",value,conn);
		return refNo;
	}
	
	
	
	

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public ArrayList<EmployeInfo> getReportsList() {
		return reportsList;
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
	public void setReportsList(ArrayList<EmployeInfo> reportsList) {
		this.reportsList = reportsList;
	}

	public EmployeInfo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(EmployeInfo selectedItem) {
		this.selectedItem = selectedItem;
	}

}
