package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


@ManagedBean(name="billPayment")
@ViewScoped
public class BillPaymentBean implements Serializable
{

	StudentInfo selectedStudent;
	String totalHostelFee;
	String otherExpe;
	int TOTALAMOUNT;
	int totalpaidamount=0;
	int leftAmount;
	String currentDate;
	String billtype;
	public BillPaymentBean() {

		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedStudent=(StudentInfo) ss.getAttribute("selectedStudent");
		billtype=(String) ss.getAttribute("billtype");
		String selectedSession=DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn);
		String[] session=selectedSession.split("-");
		if(billtype.equals("Ist Bill"))
		{

			currentDate="30/09/"+session[0];

			totalHostelFee=DBM.getHostelFee(selectedStudent.getAddNumber(),conn);
			//("1st hostel : "+totalHostelFee);
			otherExpe=DBM.totalOtherExpense(selectedStudent.getAddNumber(),currentDate,billtype,conn);
			//("1st other : "+otherExpe);

			totalpaidamount=DBM.FeePaidRecordForStudentExpence(selectedStudent,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),"-3",/*currentDate,*/billtype,conn);
			TOTALAMOUNT=Integer.parseInt(otherExpe)+Integer.parseInt(totalHostelFee);
			leftAmount=TOTALAMOUNT-totalpaidamount;
			if(leftAmount<0)
			{
				totalpaidamount=TOTALAMOUNT;
				leftAmount=0;
			}
		}
		else
		{

			String totalHostelFee1=DBM.getHostelFee(selectedStudent.getAddNumber(),conn);
			//("1st hostel f : "+totalHostelFee1);
			String otherExpe1=DBM.totalOtherExpense(selectedStudent.getAddNumber(),"30/09/"+session[0],"Ist Bill",conn);
			//("1st other f : "+otherExpe1);
			int totalpaidamount1=DBM.FeePaidRecordForStudentExpence(selectedStudent,DatabaseMethods1.selectedSessionDetails(DBM.schoolId(),conn),"-3",/*"30/09/"+session[0],*/"Ist Bill",conn);
			int TOTALAMOUNT1=Integer.parseInt(otherExpe1)+Integer.parseInt(totalHostelFee1);
			int leftAmount1=TOTALAMOUNT1-totalpaidamount1;
			int negativeAmount=0;
			if(leftAmount1<0)
			{
				negativeAmount= (-1)*leftAmount1;
				leftAmount1=0;
			}


			currentDate="28/02/"+session[1];
			totalHostelFee=String.valueOf(leftAmount1);
			otherExpe=DBM.totalOtherExpense(selectedStudent.getAddNumber(),"30/09/"+session[0],billtype,conn);
			//totalpaidamount=DBM.FeePaidRecordForStudentExpence(selectedStudent,DBM.selectedSessionDetails(),"-3","30/09/"+session[0],billtype,conn);
			totalpaidamount=negativeAmount;
			TOTALAMOUNT=Integer.parseInt(otherExpe)+Integer.parseInt(totalHostelFee);
			leftAmount=TOTALAMOUNT-totalpaidamount;

		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public String getTotalHostelFee() {
		return totalHostelFee;
	}
	public void setTotalHostelFee(String totalHostelFee) {
		this.totalHostelFee = totalHostelFee;
	}
	public String getOtherExpe() {
		return otherExpe;
	}
	public void setOtherExpe(String otherExpe) {
		this.otherExpe = otherExpe;
	}
	public int getTOTALAMOUNT() {
		return TOTALAMOUNT;
	}
	public void setTOTALAMOUNT(int tOTALAMOUNT) {
		TOTALAMOUNT = tOTALAMOUNT;
	}
	public int getTotalpaidamount() {
		return totalpaidamount;
	}
	public void setTotalpaidamount(int totalpaidamount) {
		this.totalpaidamount = totalpaidamount;
	}
	public int getLeftAmount() {
		return leftAmount;
	}
	public void setLeftAmount(int leftAmount) {
		this.leftAmount = leftAmount;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public String getBilltype() {
		return billtype;
	}
	public void setBilltype(String billtype) {
		this.billtype = billtype;
	}




}
