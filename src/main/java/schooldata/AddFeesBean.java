package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import session_work.RegexPattern;

@ManagedBean(name="addFees")
@ViewScoped
public class AddFeesBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String feeType, feeName,feeId,feeCheckType="classWise",FeeMonth="every";
	ArrayList<FeeInfo> list;
	FeeInfo selectedFees;
	String tax_percentage="0";
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public AddFeesBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		list=obj.viewFeeList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addNewFees()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(feeName.contains("#") || feeName.contains("'") || feeName.contains("--"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Fees Name is invalid","Validation Error"));
				feeName=feeType="";
				FeeMonth="every";
			}
			else
			{
				int i=obj.addFees(feeName, feeType,feeCheckType,FeeMonth,tax_percentage,conn);
				if(i>0)
				{
					String refNo=addWorkLog(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Add Sucessfully"));
					feeName=feeType="";
					FeeMonth="every";
					list=obj.viewFeeList(conn);
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
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
		
		language = feeName+" --- "+feeCheckType+" --- "+feeType+" --- "+FeeMonth+" --- "+tax_percentage;
		value = feeName+" --- "+feeType+" --- "+feeCheckType+" --- "+FeeMonth+" --- "+tax_percentage;
	
		String refNo = workLg.saveWorkLogMehod(language,"Add Fees","WEB",value,conn);
		return refNo;
	}

	//For Edit
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = feeName+" --- "+feeType+" --- "+feeId;
		language = feeName;
	
		String refNo = workLg.saveWorkLogMehod(language,"Add Fees","WEB",value,conn);
		return refNo;
	}

	
	public void editDetails()
	{
		feeName=selectedFees.getFeeName();
		feeType=selectedFees.getFeeType();
		feeId=selectedFees.getFeeId();
		PrimeFaces context = PrimeFaces.current();
		context.executeInitScript("PF('editDialog').show();");

	}




	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			if(feeName.contains("#") || feeName.contains("'") || feeName.contains("--"))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Fees Name is invalid","Validation Error"));
				feeName=feeType="";
				FeeMonth="every";
			}
			else
			{
				int i=obj.updateFeeDetails(feeName,feeType,feeId,conn);
				if(i==1)
				{
					String refNo2=addWorkLog2(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Fees Details Updated Successfully"));
					feeName="";
					feeType="";
					list=obj.viewFeeList(conn);

				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again  ", "Validation error"));
				}
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public ArrayList<FeeInfo> getList() {
		return list;
	}

	public void setList(ArrayList<FeeInfo> list) {
		this.list = list;
	}

	public FeeInfo getSelectedFees() {
		return selectedFees;
	}

	public void setSelectedFees(FeeInfo selectedFees) {
		this.selectedFees = selectedFees;
	}

	public String getFeeId() {
		return feeId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}


	public String getFeeMonth() {
		return FeeMonth;
	}

	public void setFeeMonth(String feeMonth) {
		FeeMonth = feeMonth;
	}

	public String getFeeCheckType() {
		return feeCheckType;
	}

	public void setFeeCheckType(String feeCheckType) {
		this.feeCheckType = feeCheckType;
	}

	public String getTax_percentage() {
		return tax_percentage;
	}

	public void setTax_percentage(String tax_percentage) {
		this.tax_percentage = tax_percentage;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}
