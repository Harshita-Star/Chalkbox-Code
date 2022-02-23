package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;

@ManagedBean(name="bulkEditStaffDetails")
@ViewScoped

public class BulkEditStaffDetailsBean implements Serializable
{
	String option,selectedCategory,selectedCategoryName;
	ArrayList<EmployeeInfo> employeeList = new ArrayList<>(), selectEmpPic = new ArrayList<>(), selectEmpDet = new ArrayList<>();
	boolean show,showPic;
	ArrayList<SelectItem> categoryList;
	DatabaseMethods1 dbm = new DatabaseMethods1();
    String schoolId,sessionValue;
    DataBaseMethodsReports dbr = new DataBaseMethodsReports();
    DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public BulkEditStaffDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=dbm.schoolId();
		sessionValue=dbm.selectedSessionDetails(schoolId,conn);
		categoryList=dbm.allEmployeeCategory(conn);
		option = "details";
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

			employeeList=dbm.searchEmployeebyCategorySchidd(schoolId,selectedCategory,conn);
			if(selectedCategory.equalsIgnoreCase("All"))
			{
				selectedCategoryName="All";
			}
			else
			{
				selectedCategoryName=dbm.employeeCategoryByIdSchid(schoolId,selectedCategory,conn);
			}

			if(employeeList.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No record Found", "Validation Error"));
				show=false;
				showPic=false;
			}
			else
			{
				if(option.equalsIgnoreCase("details"))
				{
					show=true;
					showPic=false;
				}
				else
				{
					show=false;
					showPic=true;
				}
			}


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

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
			if(option.equalsIgnoreCase("details"))
			{
				if(selectEmpDet.size()>0)
				{
					
					i=dbr.updateStaffBasicDetails(selectEmpDet,option,conn);
					if(i>0)
					{
						String refNo;
						try {
							refNo=addWorkLog(conn);
						} catch (Exception e) {
							e.printStackTrace();
						}
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Detail Updated Successfully"));
						selectEmpDet=new ArrayList<>();
						//						employeeList = new ArrayList<>();
						//						show=false;

					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Employee(s) to Edit Details."));
				}
			}
			else
			{
				if(selectEmpPic.size()>0)
				{
					i=dbr.updateStaffBasicDetails(selectEmpPic,option,conn);
					if(i>0)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Photos Updated Successfully"));
						selectEmpPic=new ArrayList<>();
						employeeList = new ArrayList<>();
						showPic=false;

					}
					else
					{
						
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occured"));
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Employee(s) to Edit Details."));
				}
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
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		
		for(EmployeeInfo ef: selectEmpDet)
		{
			String joinDt ="";
			try {
				joinDt = formatter.format(ef.getStartingDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String dobDt ="";
			try {
				dobDt = formatter.format(ef.getDob());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			value += "( Name-"+ef.getFname()+" Father-"+ef.getLname()+" Dob-"+dobDt+" Join-"+joinDt+" Gender-"+ef.getGender()+" Category-"+ef.getCategoryid()+" SubCateg-"+
			ef.getSubCateg()+" Address-"+ef.getAddress()+" Mobile-"+ef.getMobile()+" email-"+ef.getEmail()+" Salary-"+ef.getSalary()+" Leave-"+ef.getLeave()+
			" Da-"+ef.getDaamt()+" Hra-"+ef.getHraamt()+" Marital-"+ef.getMarital()+" Spouse-"+ef.getSpouseName()+" Pan-"+ef.getPanNo()+" Aadhar-"+ef.getAadhaarNo()+
			" Acct no.-"+ef.getBankAccountNo()+" Bank-"+ef.getBankName()+" Branch-"+ef.getBankBranch()+" Ifsc-"+ef.getIfscCode()+" Epf-"+ef.getEpfUanNo()+" Esi-"+ef.getEsiNo()+")";	
		}
		
		String refNo = workLg.saveWorkLogMehod(language,"Bulk edit Employee","WEB",value,conn);
		return refNo;
	}

	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public ArrayList<EmployeeInfo> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(ArrayList<EmployeeInfo> employeeList) {
		this.employeeList = employeeList;
	}
	public ArrayList<EmployeeInfo> getSelectEmpPic() {
		return selectEmpPic;
	}
	public void setSelectEmpPic(ArrayList<EmployeeInfo> selectEmpPic) {
		this.selectEmpPic = selectEmpPic;
	}
	public ArrayList<EmployeeInfo> getSelectEmpDet() {
		return selectEmpDet;
	}
	public void setSelectEmpDet(ArrayList<EmployeeInfo> selectEmpDet) {
		this.selectEmpDet = selectEmpDet;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public boolean isShowPic() {
		return showPic;
	}
	public void setShowPic(boolean showPic) {
		this.showPic = showPic;
	}
	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public String getSelectedCategoryName() {
		return selectedCategoryName;
	}

	public void setSelectedCategoryName(String selectedCategoryName) {
		this.selectedCategoryName = selectedCategoryName;
	}
}
