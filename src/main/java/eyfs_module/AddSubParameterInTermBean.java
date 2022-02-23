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
import schooldata.DatabaseMethods1;

@ManagedBean(name="addSubParaInTerm")
@ViewScoped
public class AddSubParameterInTermBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<SelectItem> ageGroupList,mainParaList,selectedSubPara,subParaList,termList;
	String schid,session,selectedAgeGroup,id,termId,mainParaId;
	ArrayList<EyfsInfo> allParameterList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	EyfsInfo selectedItem;
	int totalParameter,oldSize;
	boolean checkForUpdate;

	public AddSubParameterInTermBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
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

	public void classMainParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		mainParaList=objEYFS.ageGroupMainParameterList(selectedAgeGroup,schid, session, conn);
		String selectedClass=objEYFS.classOfAgeGroup(selectedAgeGroup,schid, session, conn);
		termList=obj.selectedTermOfClass(selectedClass,conn,session,schid);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void subParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subParaList=objEYFS.subParameterListByMainParameter(mainParaId, schid, session, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allParameterList=objEYFS.allAgeGroupTermSubParaList(schid,session,conn);
		totalParameter=allParameterList.size();
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
		selectedAgeGroup=selectedItem.getAgeGroupId();
		classMainParameter();
		mainParaId=selectedItem.getMainParameterId();
		termId=selectedItem.getTermId();
		subParameter();
		selectedSubPara=new ArrayList<>();
		String mainIdArr[]=selectedItem.getSubParaId().split(",");
		for(int i=0;i<mainIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.subParameterInfoById(mainIdArr[i], conn).getSubParaName());
			ll.setValue(mainIdArr[i]);
			selectedSubPara.add(ll);
		}
		oldSize=selectedSubPara.size();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<SelectItem> temp1=new ArrayList<>();

		String mainIdArr[]=selectedItem.getSubParaId().split(",");
		for(int i=0;i<mainIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.subParameterInfoById(mainIdArr[i], conn).getSubParaName());
			ll.setValue(mainIdArr[i]);
			temp1.add(ll);
		}
		selectedItem.setClassSubParaList(temp1);

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
			if(selectedSubPara.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Sub Parameter"));
			}
			else
			{
				String subId="";
				for(SelectItem main:selectedSubPara)
				{
					subId+=main.getValue()+",";
				}
				subId=subId.substring(0,subId.lastIndexOf(","));

				int i=objEYFS.updateSubParaInAgeGroup(id,selectedAgeGroup,termId,mainParaId,subId,schid,session,oldSize,conn);
				if(i>=0)
				{
					selectedAgeGroup="";termId=mainParaId="";checkForUpdate=false;
					selectedSubPara=subParaList=new ArrayList<>();
					allParameter();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
				}
			}
		}
		else
		{
			if(selectedSubPara.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Main Parameter"));
			}
			else
			{
				String subId="";
				for(SelectItem main:selectedSubPara)
				{
					subId+=main.getValue()+",";
				}
				subId=subId.substring(0,subId.lastIndexOf(","));

				boolean check=objEYFS.checkAlreadyAddedSubParaInAgeGroupOrNot(selectedAgeGroup,termId,mainParaId,schid,session,conn);
				if(check==true)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parameter Already Added In Age Group... Please Edit For Change"));
				}
				else
				{
					int i=objEYFS.addSubParaInAgeGroup(selectedAgeGroup,termId,mainParaId,subId,schid,session,conn);
					if(i>=0)
					{
						selectedAgeGroup="";termId=mainParaId="";
						selectedSubPara=subParaList=new ArrayList<>();
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

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteMainParameter()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=objEYFS.deleteSubParameterInAgeGroup(selectedItem.getId(),conn);
		if(i>=0)
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
	
	
	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}

	public String getSelectedAgeGroup() {
		return selectedAgeGroup;
	}

	public void setSelectedAgeGroup(String selectedAgeGroup) {
		this.selectedAgeGroup = selectedAgeGroup;
	}

	public boolean isCheckForUpdate() {
		return checkForUpdate;
	}

	public void setCheckForUpdate(boolean checkForUpdate) {
		this.checkForUpdate = checkForUpdate;
	}

	public ArrayList<SelectItem> getMainParaList() {
		return mainParaList;
	}

	public void setMainParaList(ArrayList<SelectItem> mainParaList) {
		this.mainParaList = mainParaList;
	}

	public ArrayList<EyfsInfo> getAllParameterList() {
		return allParameterList;
	}

	public void setAllParameterList(ArrayList<EyfsInfo> allParameterList) {
		this.allParameterList = allParameterList;
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

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getMainParaId() {
		return mainParaId;
	}

	public void setMainParaId(String mainParaId) {
		this.mainParaId = mainParaId;
	}

	public ArrayList<SelectItem> getSelectedSubPara() {
		return selectedSubPara;
	}

	public void setSelectedSubPara(ArrayList<SelectItem> selectedSubPara) {
		this.selectedSubPara = selectedSubPara;
	}

	public ArrayList<SelectItem> getSubParaList() {
		return subParaList;
	}

	public void setSubParaList(ArrayList<SelectItem> subParaList) {
		this.subParaList = subParaList;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}
}
