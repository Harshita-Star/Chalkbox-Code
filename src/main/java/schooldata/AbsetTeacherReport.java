package schooldata;

import java.io.IOException;
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
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
@ManagedBean(name="absentTeacherReport")
@ViewScoped
public class AbsetTeacherReport implements Serializable{

	ArrayList<EmpInfo> studentList;
	boolean b;
	Date date=new Date();
	String total,strDate;
	ArrayList<SelectItem> employeeList;
	String selectedEmployee,selectedStatus="all";
	DatabaseMethods1 DBM= new DatabaseMethods1();
	String schoolId,session;

	public AbsetTeacherReport()
	{
		selectedEmployee="all";
		Connection conn=DataBaseConnection.javaConnection();
		date=new Date();
		employeeList=DBM.allteacher(conn);
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

		strDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
		studentList=DBM.allAbsentTeacherAttendance(date,selectedEmployee,selectedStatus,conn);
		if(studentList.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No record Found"));
			b=false;
		}
		else
		{
			b=true;
			total=String.valueOf(studentList.size());
		}


		PrimeFaces.current().ajax().update(":form");

		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print() throws IOException
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("absentList", studentList);
		ss.setAttribute("total", total);
		ss.setAttribute("strDate", strDate);
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getExternalContext().redirect("printAbsentTeacherReport.xhtml");
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public String getTotal() {
		return total;
	}


	public void setTotal(String total) {
		this.total = total;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public ArrayList<EmpInfo> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<EmpInfo> studentList) {
		this.studentList = studentList;
	}

	public String getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(String selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public String getSelectedStatus() {
		return selectedStatus;
	}

	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}
}
