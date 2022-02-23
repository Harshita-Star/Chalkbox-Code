package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="dashboardAccount")
@ViewScoped
public class DashboardAccountBean implements Serializable{

	String cunt,totalCollection,todaysExpense,totalDuefees,totalDue;

	String schid,session;

	ArrayList<StudentInfo>enquiryList = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();


	public DashboardAccountBean()
	{
		Connection conn  =  DataBaseConnection.javaConnection();

		schid=DBM.schoolId();
		session=DBM.selectedSessionDetails(schid, conn);
		double totalDueAmount=0;
		cunt=DBM.allStudentcount(DBM.schoolId(),"","",session,"",conn);
		totalCollection=DBM.todaysCollection(DBM.schoolId(),"dashboard",conn);
		todaysExpense=DBM.todaysExpensesFromExpenseTable(conn);




		totalDuefees = new DecimalFormat("##").format(totalDueAmount);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public ArrayList<StudentInfo> getEnquiryList() {
		return enquiryList;
	}
	public void setEnquiryList(ArrayList<StudentInfo> enquiryList) {
		this.enquiryList = enquiryList;
	}


	public DatabaseMethods1 getDBM() {
		return DBM;
	}
	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
	}

	public String getTodaysExpense() {
		return todaysExpense;
	}
	public void setTodaysExpense(String todaysExpense) {
		this.todaysExpense = todaysExpense;
	}
	public String getTotalDuefees() {
		return totalDuefees;
	}
	public void setTotalDuefees(String totalDuefees) {
		this.totalDuefees = totalDuefees;
	}
	public String getCunt() {
		return cunt;
	}
	public void setCunt(String cunt) {
		this.cunt = cunt;
	}
	public String getTotalCollection() {
		return totalCollection;
	}
	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}
	public String getTotalDue() {
		return totalDue;
	}
	public void setTotalDue(String totalDue) {
		this.totalDue = totalDue;
	}


}