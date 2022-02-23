package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
@ManagedBean(name="transferEnquiryInst")
@ViewScoped
public class TransferEnquiryInstitute implements Serializable
{
	String selectedSourceEmployee,selectedTargetEmployee;
	ArrayList<StudentInfo1>list,selectedList;
	ArrayList<SelectItem> sourceEmpList;
	String userId,type;
	Date date=new Date();
	DatabaseMethods1 obj=new DatabaseMethods1();
	public TransferEnquiryInstitute()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		userId=(String) ss.getAttribute("username");
		type=(String) ss.getAttribute("type");

		sourceEmpList=obj.allEmployeeList(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void search()
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=new DatabaseMethods1().allEnquiryListInstitute("All", selectedSourceEmployee, conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void transferLead()
	{
		Connection conn= DataBaseConnection.javaConnection();
		if(selectedList.size()>0)
		{
			for(StudentInfo1 info:selectedList)
			{
				obj.addFollowUpInstitute(info.getId(),date,"Lead Transfered","Accepted", userId,"","view", conn);
			}

			int i=obj.transferEnquiryInstitute(selectedSourceEmployee,selectedTargetEmployee,selectedList,conn);
			if(i>=1)
			{
				list=new ArrayList<>();selectedSourceEmployee=selectedTargetEmployee="";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lead Transfered Sucessfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select At least One Lead"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public String getSelectedSourceEmployee() {
		return selectedSourceEmployee;
	}
	public void setSelectedSourceEmployee(String selectedSourceEmployee) {
		this.selectedSourceEmployee = selectedSourceEmployee;
	}
	public String getSelectedTargetEmployee() {
		return selectedTargetEmployee;
	}
	public void setSelectedTargetEmployee(String selectedTargetEmployee) {
		this.selectedTargetEmployee = selectedTargetEmployee;
	}
	public ArrayList<StudentInfo1> getList() {
		return list;
	}
	public void setList(ArrayList<StudentInfo1> list) {
		this.list = list;
	}
	public ArrayList<StudentInfo1> getSelectedList() {
		return selectedList;
	}
	public void setSelectedList(ArrayList<StudentInfo1> selectedList) {
		this.selectedList = selectedList;
	}
	public ArrayList<SelectItem> getSourceEmpList() {
		return sourceEmpList;
	}
	public void setSourceEmpList(ArrayList<SelectItem> sourceEmpList) {
		this.sourceEmpList = sourceEmpList;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
