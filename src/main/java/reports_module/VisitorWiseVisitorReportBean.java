package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="visitorWiseVisitorReport")
@ViewScoped
public class VisitorWiseVisitorReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	int totalVisitor;
	String selectedVisitor="-1";
	ArrayList<SelectItem> allVisitor;

	ArrayList<reports_module.VisitorInfo> visitorList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	String schoolId,session;
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();



	public VisitorWiseVisitorReportBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		session=dbm.selectedSessionDetails(schoolId, conn);
		allVisitor=dbm.visitorList(conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		searchData();


	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		visitorList=dbr.visitorWiseVisitorReport(selectedVisitor,conn);
		totalVisitor=visitorList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<reports_module.VisitorInfo> getVisitorList() {
		return visitorList;
	}
	public void setVisitorList(ArrayList<reports_module.VisitorInfo> visitorList) {
		this.visitorList = visitorList;
	}
	public int getTotalVisitor() {
		return totalVisitor;
	}
	public void setTotalVisitor(int totalVisitor) {
		this.totalVisitor = totalVisitor;
	}
	public String getSelectedVisitor() {
		return selectedVisitor;
	}
	public void setSelectedVisitor(String selectedVisitor) {
		this.selectedVisitor = selectedVisitor;
	}
	public ArrayList<SelectItem> getAllVisitor() {
		return allVisitor;
	}
	public void setAllVisitor(ArrayList<SelectItem> allVisitor) {
		this.allVisitor = allVisitor;
	}
}
