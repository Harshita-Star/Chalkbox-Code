package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="addSubPara")
@ViewScoped
public class AddSubParameterBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String name,mainParameterId,schid,session,id;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<SelectItem> mainParaList;
	ArrayList<EyfsInfo> allParameterList;
	EyfsInfo selectedPara;
	int totalParameter;
	boolean checkForUpdate=false;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public AddSubParameterBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		mainParaList=objEYFS.mainParameterList(schid, session, conn);
		allParameter();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void allParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allParameterList=objEYFS.allSubParameter(schid, session, conn);
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
		name=selectedPara.getSubParaName();
		mainParameterId=selectedPara.getMainParameterId();
		id=selectedPara.getId();
		checkForUpdate=true;
	}

	public void addSubParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		if(checkForUpdate==true)
		{
			boolean check=objEYFS.checkDuplicateSubParameter(id,name,mainParameterId,schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.updateSubParameter(name,mainParameterId,id,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully"));
					name=mainParameterId="";checkForUpdate=false;
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
			boolean check=objEYFS.checkDuplicateSubParameter(id,name,mainParameterId,schid, session, conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Parameter Found.. Try Again With Different Name"));
			}
			else
			{
				i=objEYFS.addSubParameter(name,mainParameterId,schid,session,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added Successfully"));
					name=mainParameterId="";checkForUpdate=false;
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

	public void deleteSubParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objEYFS.deleteSubParameter(selectedPara.getId(),conn);
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

	public String getMainParameterId() {
		return mainParameterId;
	}

	public void setMainParameterId(String mainParameterId) {
		this.mainParameterId = mainParameterId;
	}

	public ArrayList<SelectItem> getMainParaList() {
		return mainParaList;
	}

	public void setMainParaList(ArrayList<SelectItem> mainParaList) {
		this.mainParaList = mainParaList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
