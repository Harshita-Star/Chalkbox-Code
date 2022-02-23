package reports_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;

@ManagedBean(name="inactiveEmployeeReport")
@ViewScoped
public class InactiveEmployeeReportBean implements Serializable{
	
	
	ArrayList<EmployeeInfo> employeeList = new ArrayList<EmployeeInfo>();
	EmployeeInfo selectedEmployee = new EmployeeInfo();
	DataBaseMethodsReports obj = new DataBaseMethodsReports();
	DatabaseMethods1 dmb = new DatabaseMethods1();
	String schoolId,session;
	DatabaseMethodWorkLog wlg = new DatabaseMethodWorkLog();
	
	public InactiveEmployeeReportBean()
	{
		
		Connection con = DataBaseConnection.javaConnection();
		schoolId=dmb.schoolId();
		session=dmb.selectedSessionDetails(schoolId,con);
		employeeList = obj.InactiveEmployeesListt(schoolId,con);
		
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void viewDetails() throws IOException
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//EmployeeInfo selectedStudent=new DatabaseMethods1().teacherInfoByUserName(selectedEmployee.getEmplyeeuserid(),conn);
		ss.setAttribute("selectedEmployee", selectedEmployee);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ExternalContext cc=FacesContext.getCurrentInstance().getExternalContext();
		cc.redirect("viewEmployeeDetails.xhtml");
	}
	
	
	public void activesEmpl()
	{
      Connection con = DataBaseConnection.javaConnection();
      
      int i = obj.makeEmployeeActive(selectedEmployee.getId(),schoolId,con);
      
      if(i>=1)
      {
    	  String refNo;
			try {
				refNo=addWorkLog(con);
			} catch (Exception e) {
				e.printStackTrace();
			}  
    	    obj.activateEmployeeLogins(selectedEmployee.getEmplyeeuserid(),schoolId,con); 
    	    
    	    String refNo2;
			try {
				refNo2=addWorkLog2(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	  
    	    FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Employee Activated"));
	 		
	 		 employeeList = obj.InactiveEmployeesListt(schoolId,con);
      }
      else 
      {
    	  FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("Some Error Occured"));  
      }
		
	  employeeList = obj.InactiveEmployeesListt(schoolId,con);
		
		
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Employee id-"+selectedEmployee.getId()+" --First name-"+selectedEmployee.getFname();
		
		String refNo = wlg.saveWorkLogMehod(language,"Employee Inactive","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Employee User Id-"+selectedEmployee.getEmplyeeuserid()+" --First name-"+selectedEmployee.getFname();
		
		String refNo = wlg.saveWorkLogMehod(language,"Activate Employee Logins","WEB",value,conn);
		return refNo;
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
