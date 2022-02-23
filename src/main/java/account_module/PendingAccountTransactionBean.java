package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;


@ManagedBean(name = "pendingAccountTransaction")
@ViewScoped
public class PendingAccountTransactionBean implements Serializable
{
	ArrayList<UserInfo> crList = new ArrayList<>();
	DataBase dbObj = new DataBase();
	UserInfo selectedVar;
	Date chequeApproveDate= new Date(), chequeDenyDate = new Date();
	String schId,financialYear;
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public PendingAccountTransactionBean()
	{
		Connection con = DataBaseConnection.javaConnection();
		schId = obj1.schoolId();
		financialYear = obj1.selectedSessionDetails(schId,con);
		
		crList = dbObj.pendingCreditAccountCheque(schId,financialYear,con);
		try
		{
			con.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String approveCheque()
	{
		Connection con = DataBaseConnection.javaConnection();

		int i = dbObj.approveCheque(selectedVar.getId(),chequeApproveDate,con);
		if(i==1)
		{
			if(selectedVar.getDescription().contains("Fee Recieved"))
			{
				int index=selectedVar.getDescription().lastIndexOf(":")+1;
				String rno=selectedVar.getDescription().substring(index);
				dbObj.updateFeeChequeStatus(rno,"cleared",con);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Approve Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured"));
		}

		crList = dbObj.pendingCreditAccountCheque(schId,financialYear,con);
		try
		{
			con.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "pendingAccountTransaction.xhtml";
	}


	public String denyCheque()
	{
		Connection con = DataBaseConnection.javaConnection();
		int i = dbObj.denyCheque(selectedVar.getId(),chequeDenyDate,con);
		if(i==1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deny Successfully"));
			int index=selectedVar.getDescription().lastIndexOf(":")+1;
			String rno=selectedVar.getDescription().substring(index);
			dbObj.updateFeeChequeStatus(rno,"returned",con);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured"));
		}

		crList = dbObj.pendingCreditAccountCheque(schId,financialYear,con);
		try
		{
			con.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "pendingAccountTransaction.xhtml";
	}


	public ArrayList<UserInfo> getCrList() {
		return crList;
	}
	public void setCrList(ArrayList<UserInfo> crList) {
		this.crList = crList;
	}
	public UserInfo getSelectedVar() {
		return selectedVar;
	}
	public void setSelectedVar(UserInfo selectedVar) {
		this.selectedVar = selectedVar;
	}

	public Date getChequeApproveDate() {
		return chequeApproveDate;
	}

	public void setChequeApproveDate(Date chequeApproveDate) {
		this.chequeApproveDate = chequeApproveDate;
	}

	public Date getChequeDenyDate() {
		return chequeDenyDate;
	}

	public void setChequeDenyDate(Date chequeDenyDate) {
		this.chequeDenyDate = chequeDenyDate;
	}

}
