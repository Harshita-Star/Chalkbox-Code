package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "schoolLedger")
@ViewScoped
public class SchoolLedger implements Serializable {
	String schoolName;
	ArrayList<SelectItem> schoolList;
	ArrayList<LedgerInfo> dataList;
	double totalCredit, totalDebit, closingBalance, givenAmount, totalBalance;

	public SchoolLedger() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolList = new DatabaseMethods1().getSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void searchData() {
		Connection conn = DataBaseConnection.javaConnection();
		dataList = new DatabaseMethods1().createSchoolLedger(schoolName, conn);
		totalCredit = totalDebit = 0;
		closingBalance = 0;
		for (LedgerInfo ll : dataList) {
			totalCredit += ll.getCredit();
			totalDebit += ll.getDebit();
		}
		closingBalance = totalCredit - totalDebit;
		givenAmount = totalDebit + closingBalance;
		totalBalance = totalDebit - totalCredit;
		// //// // System.out.println(totalCredit+"..........."+totalDebit+"..........."+givenAmount+"....."+closingBalance);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public ArrayList<LedgerInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<LedgerInfo> dataList) {
		this.dataList = dataList;
	}

	public double getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(double totalCredit) {
		this.totalCredit = totalCredit;
	}

	public double getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(double totalDebit) {
		this.totalDebit = totalDebit;
	}

	public double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

}
