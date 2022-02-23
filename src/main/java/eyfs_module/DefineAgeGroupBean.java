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
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;

@ManagedBean(name="defineAgeGroup")
@ViewScoped
public class DefineAgeGroupBean implements Serializable  
{
	String regex=RegexPattern.REGEX;
	String id,name,schid,session;
	ArrayList<EyfsInfo> ageGroupList;
	EyfsInfo selectedAgeGroup;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	boolean checkForEdit;
	
	public DefineAgeGroupBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		searchData();
	}

	public void searchData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		ageGroupList=objEYFS.allAgeGroupList(schid,session,conn);
		
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addGroup()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		if(checkForEdit==false)
		{
			boolean duplicate=objEYFS.checkDuplicateAgeGroup("",name,schid,session,conn);
			if(duplicate==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Age Group Found... Try With Another Name"));
			}
			else
			{
				int i=objEYFS.addAgeGroup(name,schid,session,conn);
				if(i>0)
				{
					name="";
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Age Group Added Successfully"));
					searchData();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
				}
			}
		}
		else
		{
			boolean duplicate=objEYFS.checkDuplicateAgeGroup(id,name,schid,session,conn);
			if(duplicate==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Age Group Found... Try With Another Name"));
			}
			else
			{
				int i=objEYFS.updateAgeGroup(id,name,conn);
				if(i>0)
				{
					name="";checkForEdit=false;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Age Group Updated Successfully"));
					searchData();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
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
	
	public void editDetails()
	{
		checkForEdit=true;
		id=selectedAgeGroup.getAgeGroupId();
		name=selectedAgeGroup.getAgeGroupName();
	}
	
	public void deleteGroup()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean checkMap=objEYFS.checkMappingOfAgeGroup(selectedAgeGroup.getAgeGroupId(),conn);
		if(checkMap==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Can't Be Deleted Because This Age Group Is Mapped With Some Class"));
		}
		else
		{
			int i=objEYFS.deleteAgeGroup(selectedAgeGroup.getAgeGroupId(), conn);
			if(i>0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Age Group Deleted Successfully"));
				searchData();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
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
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public boolean isCheckForEdit() {
		return checkForEdit;
	}
	public void setCheckForEdit(boolean checkForEdit) {
		this.checkForEdit = checkForEdit;
	}

	public ArrayList<EyfsInfo> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<EyfsInfo> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}

	public EyfsInfo getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(EyfsInfo selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
