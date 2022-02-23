package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="addGradePara")
@ViewScoped
public class AddGradingParameterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name,weightage,schid,session,id;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<EyfsInfo> allParameterList;
	EyfsInfo selectedPara;
	int totalParameter;
	boolean checkForUpdate=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public AddGradingParameterBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		allParameter();
	}

	public void allParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allParameterList=objEYFS.allGradingParameter(schid, session, conn);
		totalParameter=allParameterList.size();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		name=selectedPara.getGradeParameterName();
		weightage=selectedPara.getWeightage();
		id=selectedPara.getId();
		checkForUpdate=true;
	}

	public void addGradingParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		if(checkForUpdate==true)
		{
			boolean check=objEYFS.checkDuplicateParameter(id,name, schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.updateGradingParameter(name,weightage,id,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully"));
					name=weightage="";checkForUpdate=false;
					allParameter();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
				}
			}
		}
		else
		{
			boolean check=objEYFS.checkDuplicateParameter("",name, schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.addGradingParameter(name,weightage,schid,session,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added Successfully"));
					name=weightage="";checkForUpdate=false;
					allParameter();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
				}
			}
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void deleteGradingParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objEYFS.deleteGradingParameter(selectedPara.getId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deleted Successfully"));
			allParameter();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
		}
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWeightage() {
		return weightage;
	}
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}
	public EyfsInfo getSelectedPara() {
		return selectedPara;
	}
	public void setSelectedPara(EyfsInfo selectedPara) {
		this.selectedPara = selectedPara;
	}
	public ArrayList<EyfsInfo> getAllParameterList() {
		return allParameterList;
	}
	public void setAllParameterList(ArrayList<EyfsInfo> allParameterList) {
		this.allParameterList = allParameterList;
	}

	public int getTotalParameter() {
		return totalParameter;
	}

	public void setTotalParameter(int totalParameter) {
		this.totalParameter = totalParameter;
	}
}
