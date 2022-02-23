package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;


@ManagedBean(name = "addAccountBean")
@ViewScoped
public class AddAccountBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String schId,financialYear,headName,accountName,address1,address2,pincode,state;
	ArrayList<SelectItem> childHeadList= new ArrayList<>();
	ArrayList<String> list = new ArrayList<>();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBase dtb = new DataBase();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public AddAccountBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schId = obj.schoolId();
		financialYear = obj.selectedSessionDetails(schId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public List<String> completeText(String name)
	{
		Connection conn=DataBaseConnection.javaConnection();
		list=dtb.headList(name,financialYear,schId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	public String submit()
	{

		Connection con = DataBaseConnection.javaConnection();
		int r= dtb.addAccount(headName,accountName,address1,address2,pincode,state,schId,financialYear,con);
		if(r==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Account Added Successfully"));
		}
		else if(r==-1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Account Already Added With This Name"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured"));
		}
		return "addAccount";
	}



	public ArrayList<SelectItem> getChildHeadList() {
		return childHeadList;
	}

	public void setChildHeadList(ArrayList<SelectItem> childHeadList) {
		this.childHeadList = childHeadList;
	}

	public String getHeadName() {
		return headName;
	}


	public void setHeadName(String headName) {
		this.headName = headName;
	}


	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	public String getAddress1() {
		return address1;
	}


	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	public String getAddress2() {
		return address2;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	public String getPincode() {
		return pincode;
	}


	public void setPincode(String pincode) {
		this.pincode = pincode;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getRegex() {
		return regex;
	}


	public void setRegex(String regex) {
		this.regex = regex;
	}



}