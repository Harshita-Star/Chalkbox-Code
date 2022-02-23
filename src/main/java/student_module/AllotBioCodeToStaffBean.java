package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.file.UploadedFile;

import reports_module.DataBaseMethodsReports;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import session_work.RegexPattern;

@ManagedBean(name="allotBioCodeToStaff")
@ViewScoped

public class AllotBioCodeToStaffBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<EmployeeInfo> employeeList,selectEmp=new ArrayList<>(),empList = new ArrayList<>();;
	boolean show;
	ArrayList<SelectItem> categoryList,bioDeviceList;
	EmployeeInfo selectedEmployee;
	String selectedCategory,deviceCode;
	String name;
	String selectedCategoryName;
	String schid;
	String filePath;
	transient
	UploadedFile excelFile;
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public AllotBioCodeToStaffBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		categoryList=dbm.allEmployeeCategory(conn);
		schid = dbm.schoolId();
		bioDeviceList = dbr.allBioDeviceList(schid, conn);
		show=false;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void searchByCategory()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try
		{
			employeeList=dbm.searchEmployeebyCategorySchidd(schid,selectedCategory,conn);
			if(selectedCategory.equalsIgnoreCase("All"))
			{
				selectedCategoryName="All";
			}
			else
			{
				selectedCategoryName=dbm.employeeCategoryByIdSchid(schid,selectedCategory,conn);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		show=true;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int i=0;
			if(selectEmp.size()>0)
			{
				i=dbr.updateStaffBioCode(selectEmp,deviceCode,conn);
				if(i>0)
				{
					String refNo;
					try {
						refNo=addWorkLog(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Staff Biometric Code Updated Successfully"));
					show=false;
					employeeList = new ArrayList<>();
					selectEmp = new ArrayList<>();
				}
				else	
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Staff Biomteric Code"));
				}
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Employee(s) to Update Staff Biomteric Code"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		
		language = "DeviceCode-"+deviceCode;
		
		for(EmployeeInfo vb: selectEmp)
		{	
		  value += "(Empl. Id-"+vb.getId()+" --BioCode-"+vb.getBioCode()+")";
		}
		
		String refNo = workLg.saveWorkLogMehod(language,"Allot BioCode To Staff","WEB",value,conn);
		return refNo;
	}


	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}


	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}


	public ArrayList<EmployeeInfo> getSelectEmp() {
		return selectEmp;
	}


	public void setSelectEmp(ArrayList<EmployeeInfo> selectEmp) {
		this.selectEmp = selectEmp;
	}


	public ArrayList<EmployeeInfo> getEmpList() {
		return empList;
	}


	public void setEmpList(ArrayList<EmployeeInfo> empList) {
		this.empList = empList;
	}


	public boolean isShow() {
		return show;
	}


	public void setShow(boolean show) {
		this.show = show;
	}


	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}


	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}


	public ArrayList<SelectItem> getBioDeviceList() {
		return bioDeviceList;
	}


	public void setBioDeviceList(ArrayList<SelectItem> bioDeviceList) {
		this.bioDeviceList = bioDeviceList;
	}


	public EmployeeInfo getSelectedEmployee() {
		return selectedEmployee;
	}


	public void setSelectedEmployee(EmployeeInfo selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}


	public String getSelectedCategory() {
		return selectedCategory;
	}


	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSelectedCategoryName() {
		return selectedCategoryName;
	}


	public void setSelectedCategoryName(String selectedCategoryName) {
		this.selectedCategoryName = selectedCategoryName;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public UploadedFile getExcelFile() {
		return excelFile;
	}


	public void setExcelFile(UploadedFile excelFile) {
		this.excelFile = excelFile;
	}


	public String getDeviceCode() {
		return deviceCode;
	}


	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
