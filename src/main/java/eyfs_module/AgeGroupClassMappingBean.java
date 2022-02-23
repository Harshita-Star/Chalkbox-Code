package eyfs_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;

@ManagedBean(name="ageGroupMapping")
@ViewScoped
public class AgeGroupClassMappingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<SelectItem> classList,ageGroupList,selectedAgeGroup;
	String schid,session,selectedClass,id;
	ArrayList<EyfsInfo> allMappingList;
	EyfsInfo selectedItem;
	int totalParameter;
	boolean checkForUpdate;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public AgeGroupClassMappingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classList=obj.allClass(conn);
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		ageGroupList=objEYFS.allAgeGroupListSelectItem(schid, session, conn);
		allParameter();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allMappingList=objEYFS.allClassAgeMappingList("all","-1",schid,session,conn);
		totalParameter=allMappingList.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		checkForUpdate=true;
		id=selectedItem.getId();
		selectedClass=selectedItem.getClassId();
		selectedAgeGroup=new ArrayList<>();
		String gradeIdArr[]=selectedItem.getAgeGroupId().split(",");
		for(int i=0;i<gradeIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.ageGroupInfoById(gradeIdArr[i], conn).getAgeGroupName());
			ll.setValue(gradeIdArr[i]);
			selectedAgeGroup.add(ll);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedClass=selectedItem.getClassId();
		ArrayList<SelectItem> temp1=new ArrayList<>();
		String gradeIdArr[]=selectedItem.getAgeGroupId().split(",");
		for(int i=0;i<gradeIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.ageGroupInfoById(gradeIdArr[i], conn).getAgeGroupName());
			ll.setValue(gradeIdArr[i]);
			temp1.add(ll);
		}
		selectedItem.setAgeGroupGradeList(temp1);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(checkForUpdate==true)
		{
			if(selectedAgeGroup.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Age Group"));
			}
			else
			{
				int flag=0;
				String ageGroupId="";
				for(SelectItem grade:selectedAgeGroup)
				{
					ageGroupId+=grade.getValue()+",";
					boolean check=objEYFS.checkAlreadyAddedAgeGroupOrNot(selectedClass,String.valueOf(grade.getValue()),schid,session,conn);
					if(check==true)
					{
						flag=1;
					}
				}
				if(flag==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Age group Already Added In Some Class"));
				}
				else
				{
					int i=objEYFS.updateAgeGroupInClass(id,ageGroupId,conn);
					if(i>=0)
					{
						selectedClass="";
						selectedAgeGroup=new ArrayList<>();checkForUpdate=false;
						allParameter();
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully"));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
					}
				}
			}
		}
		else
		{
			if(selectedAgeGroup.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Age Group"));
			}
			else
			{
				boolean checkEntryForClass=objEYFS.checkEntryOfAgeGroupForClass(selectedClass,schid,session,conn);
				if(checkEntryForClass==true)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Age Group Already Added For This Class"));
				}
				else
				{
					int flag=0;
					String ageGroupId="";
					for(SelectItem grade:selectedAgeGroup)
					{
						ageGroupId+=grade.getValue()+",";
						boolean check=objEYFS.checkAlreadyAddedAgeGroupOrNot("",String.valueOf(grade.getValue()),schid,session,conn);
						if(check==true)
						{
							flag=1;
						}
					}
					ageGroupId=ageGroupId.substring(0,ageGroupId.lastIndexOf(","));
					
					if(flag==1)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Age group Already Added In Some Class"));
					}
					else
					{
						int i=objEYFS.addAgeGroupInClass(selectedClass,ageGroupId,schid,session,conn);
						if(i>=0)
						{
							selectedClass="";
							selectedAgeGroup=new ArrayList<>();
							allParameter();
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added Successfully"));
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
						}
					}
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteAgeGroupMapping()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objEYFS.deleteAgeGroupMapping(id,conn);
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
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public boolean isCheckForUpdate() {
		return checkForUpdate;
	}

	public void setCheckForUpdate(boolean checkForUpdate) {
		this.checkForUpdate = checkForUpdate;
	}


	public EyfsInfo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(EyfsInfo selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getTotalParameter() {
		return totalParameter;
	}

	public void setTotalParameter(int totalParameter) {
		this.totalParameter = totalParameter;
	}

	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}

	public ArrayList<SelectItem> getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(ArrayList<SelectItem> selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public ArrayList<EyfsInfo> getAllMappingList() {
		return allMappingList;
	}

	public void setAllMappingList(ArrayList<EyfsInfo> allMappingList) {
		this.allMappingList = allMappingList;
	}
}
