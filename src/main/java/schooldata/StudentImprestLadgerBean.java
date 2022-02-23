package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import session_work.RegexPattern;

@ViewScoped
@ManagedBean(name="studentImprestladgerBean")
public class StudentImprestLadgerBean implements Serializable
{

	String regex=RegexPattern.REGEX;
	String studentName,strDate,endDate;
	ArrayList<StudentInfo> studentList;
	ArrayList<ImprestList>list;
	int totalCredit,totalDebit,remaningBal;
	Date startDate,enddate;
	public StudentImprestLadgerBean() {

		startDate=new Date(); enddate=new Date();
		startDate.setDate(1);

		strDate = new SimpleDateFormat("dd/MM/yyyy").format(startDate);
		endDate = new SimpleDateFormat("dd/MM/yyyy").format(enddate);


	}
	public ArrayList<String> autoCompleteStudentInfo(String query)
	{

		Connection conn = DataBaseConnection.javaConnection();
		studentList=new DatabaseMethods1().searchStudentList(query,conn);
		ArrayList<String> studentListt=new ArrayList<>();

		for(StudentInfo info : studentList)
		{
			studentListt.add(info.getFname()+ " / "+info.getFathersName()+" / "+info.getSrNo()+"-"+info.getClassName()+"-:"+info.getAddNumber());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentListt;

	}




	public void searchLadger()
	{
		strDate = new SimpleDateFormat("dd/MM/yyyy").format(startDate);
		endDate = new SimpleDateFormat("dd/MM/yyyy").format(enddate);
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int index=studentName.lastIndexOf(":")+1;
			String id=studentName.substring(index);
			list=new DatabaseMethods1().showimaprestlist(id,conn,startDate,enddate,new DatabaseMethods1().schoolId());
			totalCredit=totalDebit=remaningBal=0;
			for(ImprestList ls:list)
			{

				if(ls.getPaymentType().equalsIgnoreCase("debit"))
				{
					totalDebit+=Integer.parseInt(ls.getDebitamount());
				}
				else
				{
					totalCredit+=Integer.parseInt(ls.getCreditAmount());
				}
			}

			remaningBal= totalCredit-totalDebit;
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}




	public String getStudentName() {
		return studentName;
	}




	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}




	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}




	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}




	public ArrayList<ImprestList> getList() {
		return list;
	}




	public void setList(ArrayList<ImprestList> list) {
		this.list = list;
	}




	public int getTotalCredit() {
		return totalCredit;
	}




	public void setTotalCredit(int totalCredit) {
		this.totalCredit = totalCredit;
	}




	public int getTotalDebit() {
		return totalDebit;
	}




	public void setTotalDebit(int totalDebit) {
		this.totalDebit = totalDebit;
	}




	public int getRemaningBal() {
		return remaningBal;
	}




	public void setRemaningBal(int remaningBal) {
		this.remaningBal = remaningBal;
	}




	public Date getStartDate() {
		return startDate;
	}




	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}




	public Date getEnddate() {
		return enddate;
	}




	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public String getStrDate() {
		return strDate;
	}
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
