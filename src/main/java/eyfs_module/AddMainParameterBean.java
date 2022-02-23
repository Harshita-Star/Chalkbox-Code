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
import session_work.RegexPattern;

@ManagedBean(name="addMainPara")
@ViewScoped
public class AddMainParameterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,description,schid,session,id;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<EyfsInfo> allParameterList;
	EyfsInfo selectedPara;
	int totalParameter;
	boolean checkForUpdate=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public AddMainParameterBean()
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
		allParameterList=objEYFS.allMainParameter(schid, session, conn);
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
		name=selectedPara.getMainParameterName();
		description=selectedPara.getDescription();
		id=selectedPara.getId();
		checkForUpdate=true;
	}

	public void addMainParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		if(checkForUpdate==true)
		{
			boolean check=objEYFS.checkDuplicateMainParameter(id,name, schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.updateMainParameter(name,description,id,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully"));
					name=description="";checkForUpdate=false;
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
			boolean check=objEYFS.checkDuplicateMainParameter("",name, schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.addMainParameter(name,description,schid,session,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added Successfully"));
					name=description="";checkForUpdate=false;
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

	public void deleteMainParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objEYFS.deleteMainParameter(selectedPara.getId(),conn);
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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
