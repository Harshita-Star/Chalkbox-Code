package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


@ManagedBean(name="allEmpList")
@ViewScoped
public class AllEmployeeLoginListBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<EmployeeInfo> employeeList;
	String schid;
	EmployeeInfo selectedEmployee;
	DatabaseMethods1 obj=new DatabaseMethods1();

	public AllEmployeeLoginListBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid = obj.schoolId();
		employeeList=new DatabaseMethods1().allActiveEmployeeLoginList(schid,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public void blockUser()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=new DatabaseMethods1().blockUser(selectedEmployee.getEmplyeeuserid(),"block",conn);
		if(i>=1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("User Blocked Successfully"));
			employeeList=new DatabaseMethods1().allActiveEmployeeLoginList(schid,conn);
			selectedEmployee=null;

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}



	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}



	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}



	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
}