package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;
@ManagedBean(name="imprestAccountBean")
@ViewScoped
public class ImprestAccountBean  implements Serializable
{
	String regex=RegexPattern.REGEX;
	String studentName;
	ArrayList<StudentInfo> studentList;
	String paymentMode,paymentType,amount,description;
	Date addexpdate=new Date(),maxDate=new Date();
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

	public ImprestAccountBean() {
		paymentMode="cash";
		paymentType="Credit";
	}


	public String addStudentExpense()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int index=studentName.lastIndexOf(":")+1;
			String id=studentName.substring(index);

			int i=new DatabaseMethods1().addImprestAccountBal(id,paymentMode,paymentType,amount,description,conn,addexpdate);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn,id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(paymentType.equalsIgnoreCase("credit"))
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Amount Submitted","Amount Submitted"));

				}
				else
				{
					FacesContext fc=FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Student Expence","Student Expence"));

				}

				return "imprestAcount.xhtml";
			}
			else
			{
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Some Error Occur","Some Error Occur"));

				return "";

			}


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

 
	public String addWorkLog(Connection conn,String id)
	{
	    String value = "";
		String language= "";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String addDt = formater.format(addexpdate);
	
		language = "StudentId-"+id+" -- Date-"+addDt+" -- Payment Mode-"+paymentMode+" --Pament Type-"+paymentType+" --Amount-"+amount+" --Description-"+description;
		value = id+" -- "+paymentMode+" -- "+paymentType+" -- "+amount+" -- "+description+" -- "+addDt;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Imprest Account","WEB",value,conn);
		return refNo;
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



	public String getPaymentMode() {
		return paymentMode;
	}



	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}



	public String getPaymentType() {
		return paymentType;
	}



	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}



	public String getAmount() {
		return amount;
	}



	public void setAmount(String amount) {
		this.amount = amount;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	public Date getAddexpdate() {
		return addexpdate;
	}

	public void setAddexpdate(Date addexpdate) {
		this.addexpdate = addexpdate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
