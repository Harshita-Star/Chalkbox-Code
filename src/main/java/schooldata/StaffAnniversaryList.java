package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

@ManagedBean(name = "staffAnniversary")
@ViewScoped
public class StaffAnniversaryList implements Serializable {
	Date date;
	ArrayList<EmployeeInfo> staffList;
	String status, template, birthdayWish, errorLabel, temp1, temp2, temp3, temp4, bdyPreview, name, schoolName,userType,balMsg;
	boolean showWishAll;
	EmployeeInfo selectedStaff;
	double smsLimit;

	public StaffAnniversaryList() {
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls = new DatabaseMethods1().fullSchoolInfo(conn);
		HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userType = (String) httpSession.getAttribute("type");
		schoolName = ls.getSmsSchoolName();
		date = new Date();

		smsLimit = new DatabaseMethods1().smsLimitReminder(new DatabaseMethods1().schoolId(), conn);

		searchData();

		temp1 = "An anniversary is a time to celebrate today's moments of joy, yesterday's great memories, and share hopes of tomorrow. Wishing you both a HAPPY WEDDING ANNIVERSARY! and many more to come.";
		temp2 = "HAPPY WEDDING ANNIVERSARY to an auspicious couple! Wishing you even more success and great delight in the company of each other!";
		temp3="HAPPY WEDDING ANNIVERSARY! May your marriage continue to lift your spirits to new heights! May you constantly be a couple who is outgoing and a pleasure to be around!";
		temp4 = "Wishing you both another awesome year together. Congratulations on your WEDDING ANNIVERSARY.";

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void templateMsg()
	{
		birthdayWish = template;
	}

	public void searchData() {
		Connection conn = DataBaseConnection.javaConnection();

		int bDate = date.getDate();
		int bMonth = date.getMonth() + 1;

		DatabaseMethods1 obj = new DatabaseMethods1();
		staffList = obj.allAnniversaryListOfStaff(bDate, bMonth, conn);

		if (staffList.isEmpty()) {
			showWishAll = false;
		} else {
			showWishAll = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void allStudentMsz() {
		status = "all";
		template = birthdayWish = "";
		if (!staffList.isEmpty()) {
			PrimeFaces.current().executeInitScript("PF('birthdayDialog').show()");
			PrimeFaces.current().ajax().update(":birthdayForm");
		} else {
			errorLabel = "No Students to Wish.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
	}

	public void selectStudentMsz()
	{
		status = "individual";
		template = birthdayWish = "";
	}

	public String wishPreview()
	{
		Connection conn=DataBaseConnection.javaConnection();
		double balance = new DatabaseMethods1().smsBalance(new DatabaseMethods1().schoolId(), conn);
		if(balance >0 && balance <= smsLimit)
		{
			balMsg = "Dear User, you are about to reach maximum limit of SMS credit. "
					+ "We suggest you to top-up your account today to ensure uninterrupted activity";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgLmtDlg1').show()");
				PrimeFaces.current().ajax().update("MsgLimitForm1");
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return "";
			}
		}
		else if(balance <= 0)
		{
			balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please renew immediately to continue.";
			if (userType.equalsIgnoreCase("admin"))
			{
				PrimeFaces.current().executeInitScript("PF('MsgOvrDlg').show()");
				PrimeFaces.current().ajax().update("MsgOverForm");
			}
			else
			{
				balMsg = "Dear User,\n You have consumed the SMS credits received with your licence. Please contact administrator to renew SMS pack.";

				PrimeFaces.current().executeInitScript("PF('MsgOthDlg').show()");
				PrimeFaces.current().ajax().update("MsgOtherForm");
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "";
		}

		if (template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if (!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + template + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}


		return "";

	}

	public void allBdMsg()
	{

		if (template.isEmpty() && birthdayWish.isEmpty())
		{
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else if (!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + template + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}
		else if (!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase(""))
		{
			bdyPreview = "Dear Staff Member, " + birthdayWish + " Regards," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}

	}

	/*public void wishPreview() {
		if (template.isEmpty() && birthdayWish.isEmpty()) {
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		} else if (!template.equalsIgnoreCase("") && birthdayWish.equalsIgnoreCase("")) {
			bdyPreview = "Dear Student, " + template + " Thanks," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		} else if (template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase("")) {
			bdyPreview = "Dear Student, " + birthdayWish + " Thanks," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		} else if (!template.equalsIgnoreCase("") && !birthdayWish.equalsIgnoreCase("")) {
			bdyPreview = "Dear Student, " + birthdayWish + " Thanks," + schoolName;
			PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').show()");
			PrimeFaces.current().ajax().update("bdyPrevForm");
		}

	}*/

	public void sendWish() throws IOException {
		PrimeFaces.current().executeInitScript("PF('bdyPrevDialog').hide()");
		PrimeFaces.current().ajax().update("bdyPrevForm");
		String message = "", contactNumber = "";

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj = new DatabaseMethods1();
		String id = "";
		if (status.equalsIgnoreCase("all"))
		{
			for (EmployeeInfo info : staffList)
			{
				id = String.valueOf(info.getId());
				if (String.valueOf(info.getMobile()).length() == 10
						&& !String.valueOf(info.getMobile()).equals("9999999999")
						&& !String.valueOf(info.getMobile()).equals("1111111111")
						&& !String.valueOf(info.getMobile()).equals("1234567890")
						&& !String.valueOf(info.getMobile()).equals("0123456789"))
				{
					name=info.getFname();
					message = "Dear " + name + "\n" + birthdayWish + "\nRegards\n" + schoolName;
					obj.messageurlStaff(String.valueOf(info.getMobile()), message, id, conn,obj.schoolId(),"");
				}
			}
		}
		else
		{
			contactNumber = String.valueOf(selectedStaff.getMobile());
			if (contactNumber.length() == 10
					&& !contactNumber.equals("9999999999")
					&& !contactNumber.equals("1111111111")
					&& !contactNumber.equals("1234567890")
					&& !contactNumber.equals("0123456789"))
			{
				message = "Dear " + selectedStaff.getFname() + "\n" + birthdayWish + "\nRegards\n" + schoolName;
				obj.messageurlStaff(contactNumber, message, id, conn,obj.schoolId(),"");
			}

		}

		if (template.isEmpty() && birthdayWish.isEmpty()) {
			errorLabel = "Please Write a Message First.";
			PrimeFaces.current().executeInitScript("PF('errorDialog').show()");
			PrimeFaces.current().ajax().update("errorForm");
		}
		else
		{

			PrimeFaces.current().ajax().update("birthdayForm");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Wishes Sent Successfully"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList<EmployeeInfo> getStaffList() {
		return staffList;
	}

	public void setStaffList(ArrayList<EmployeeInfo> staffList) {
		this.staffList = staffList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getBirthdayWish() {
		return birthdayWish;
	}

	public void setBirthdayWish(String birthdayWish) {
		this.birthdayWish = birthdayWish;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public String getTemp1() {
		return temp1;
	}

	public void setTemp1(String temp1) {
		this.temp1 = temp1;
	}

	public String getTemp2() {
		return temp2;
	}

	public void setTemp2(String temp2) {
		this.temp2 = temp2;
	}

	public String getTemp3() {
		return temp3;
	}

	public void setTemp3(String temp3) {
		this.temp3 = temp3;
	}

	public String getTemp4() {
		return temp4;
	}

	public void setTemp4(String temp4) {
		this.temp4 = temp4;
	}

	public String getBdyPreview() {
		return bdyPreview;
	}

	public void setBdyPreview(String bdyPreview) {
		this.bdyPreview = bdyPreview;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public boolean isShowWishAll() {
		return showWishAll;
	}

	public void setShowWishAll(boolean showWishAll) {
		this.showWishAll = showWishAll;
	}

	public EmployeeInfo getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(EmployeeInfo selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public String getBalMsg() {
		return balMsg;
	}

	public void setBalMsg(String balMsg) {
		this.balMsg = balMsg;
	}

}
