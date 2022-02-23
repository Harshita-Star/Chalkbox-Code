package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import session_work.RegexPattern;

@ManagedBean(name="parentWiseFeeCollection")
@ViewScoped
public class ParentWiseFeeCollection implements Serializable
{
	String regex=RegexPattern.REGEX;
	String name;
	boolean show;
	ArrayList<FeeInfo> feelist;
	ArrayList<StudentInfo> studentList;
	List<String> nameList;
	double totalFee,totalPaid,totalDiscount,totalDueAmount,totalLeft;
	StudentInfo selectedStudent;

	public List<String> completeParentName(String name)
	{
		Connection conn = DataBaseConnection.javaConnection();
		nameList=new DatabaseMethods1().autoCompleteForSearchStudentListBYParentNameOrContactNum(name,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	public String editNow()
	{
		if(selectedStudent!=null)
		{
			HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			ss.setAttribute("selectedStudent", selectedStudent);
			selectedStudent=null;
			show=false;
			name=null;
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			try {
				ec.redirect("studentFeeCollection.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			//return "studentFeeCollection";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select a student from the list", "Validation error"));
		}
		return "";
	}

	public String searchStudentByName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		studentList=new ArrayList<>();
		String[] temp=name.split("/");
		studentList=DBM.searchStudentListBYParentNameOrContactNumForFees(temp[1],conn);
		if(studentList.size()>0)
		{

			for(StudentInfo ll:studentList)
			{
				totalFee+=ll.getTotalFee();
				totalPaid+=ll.getPaidFee();
				totalDiscount+=Double.parseDouble(ll.getDiscountfee());
				totalDueAmount+=Double.parseDouble(ll.getTutionFeeDueAmount());
				totalLeft+=ll.getLeftFee();
			}
			feelist=DBM.viewFeeList(conn);
			feelist=DBM.addPreviousFee(feelist,studentList.get(0).getAddNumber(),conn);   // only for adding previous fee label
			show=true;
		}
		else
		{
			show=false;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public StudentInfo getSelectedStudent() {
		return selectedStudent;
	}
	public void setSelectedStudent(StudentInfo selectedStudent) {
		this.selectedStudent = selectedStudent;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<FeeInfo> getFeelist() {
		return feelist;
	}

	public void setFeelist(ArrayList<FeeInfo> feelist) {
		this.feelist = feelist;
	}

	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}

	public double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public double getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}

	public double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getTotalLeft() {
		return totalLeft;
	}

	public void setTotalLeft(double totalLeft) {
		this.totalLeft = totalLeft;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}
