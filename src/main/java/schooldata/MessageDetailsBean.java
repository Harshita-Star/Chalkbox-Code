package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "messageDetails")
@ViewScoped
public class MessageDetailsBean implements Serializable {
	ArrayList<SelectItem> schoolList;
	String schNm;
	ArrayList<MessageHistory> allDetails = new ArrayList<>();
	double debit, credit;
	double sumaryBalance;
	int i;

	public MessageDetailsBean() {
		Connection conn = DataBaseConnection.javaConnection();
		schoolList = new DatabaseMethods1().getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allHistory() {
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		i = 1;
		debit = 0;
		credit = 0;
		sumaryBalance = 0;
		/*
		 * HttpSession ss=(HttpSession)
		 * FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		 * String schoolid=(String) ss.getAttribute("schoolid");
		 */
		allDetails = new ArrayList<>();
		ArrayList<MessageHistory> list = obj.messageHistory(schNm, conn);
		ArrayList<MessageHistory> message = obj.messageDebitWise(schNm, conn);

		allDetails.addAll(list);
		allDetails.addAll(message);

		Collections.sort(allDetails);
		for (MessageHistory ss1 : allDetails) {
			if (ss1.getDebit() != null && !ss1.getDebit().equals("")) {
				debit += Double.parseDouble(ss1.getDebit());

			}

			if (ss1.getCredit() != null && !ss1.getCredit().equals("")) {
				credit += Double.parseDouble(ss1.getCredit());
			}

			ss1.setSno(i++);
		}

		sumaryBalance = credit - debit;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getSchNm() {
		return schNm;
	}

	public void setSchNm(String schNm) {
		this.schNm = schNm;
	}

	public ArrayList<MessageHistory> getAllDetails() {
		return allDetails;
	}

	public void setAllDetails(ArrayList<MessageHistory> allDetails) {
		this.allDetails = allDetails;
	}

	public double getDebit() {
		return debit;
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getSumaryBalance() {
		return sumaryBalance;
	}

	public void setSumaryBalance(double sumaryBalance) {
		this.sumaryBalance = sumaryBalance;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

}
