package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="dashboardFrontOffice")
@ViewScoped
public class DashboardFrontOfficeBean implements Serializable{

	String totalEnquiry,accepted,pending,totalNewAdmission;
	ArrayList<StudentInfo>enquiryList = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String schid,session;

	public DashboardFrontOfficeBean()
	{
		Connection conn  =  DataBaseConnection.javaConnection();
		totalEnquiry = DBM.allStudentEnquiry(conn);
		accepted = DBM.acceptedStudent(conn);
		pending = DBM.pendingStudent(conn);
		
		schid=DBM.schoolId();
		session=DBM.selectedSessionDetails(schid, conn);
		totalNewAdmission = DBM.allStudentcount(DBM.schoolId(), "newAddmission","",session,"",conn);
		//enquiryList = DBM.alltranportStudentList(conn);
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

	public String getTotalEnquiry() {
		return totalEnquiry;
	}
	public void setTotalEnquiry(String totalEnquiry) {
		this.totalEnquiry = totalEnquiry;
	}
	public String getAccepted() {
		return accepted;
	}
	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}
	public String getPending() {
		return pending;
	}
	public void setPending(String pending) {
		this.pending = pending;
	}
	public String getTotalNewAdmission() {
		return totalNewAdmission;
	}
	public void setTotalNewAdmission(String totalNewAdmission) {
		this.totalNewAdmission = totalNewAdmission;
	}
	public DatabaseMethods1 getDBM() {
		return DBM;
	}
	public void setDBM(DatabaseMethods1 dBM) {
		DBM = dBM;
	}




}