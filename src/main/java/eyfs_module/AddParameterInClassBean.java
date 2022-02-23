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

@ManagedBean(name="addParaInClass")
@ViewScoped
public class AddParameterInClassBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	DataBaseMethodsEYFS objEYFS=new DataBaseMethodsEYFS();
	ArrayList<SelectItem> ageGroupList,gradingParaList,mainParaList,selectedGradPara;
	String schid,session,selectedAgeGroup,id,mainParaId;
	ArrayList<EyfsInfo> allParameterList;
	EyfsInfo selectedItem;
	int totalParameter;
	boolean checkForUpdate;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();


	public AddParameterInClassBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid,conn);
		
		ageGroupList=objEYFS.allAgeGroupListSelectItem(schid, session, conn);
		gradingParaList=objEYFS.gradingParameterList(schid, session, conn);
		mainParaList=objEYFS.mainParameterList(schid, session, conn);
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
		allParameterList=objEYFS.allAgeGroupMainParaList("all","-1",schid,session,conn);
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
		selectedGradPara=new ArrayList<>();
		String gradeIdArr[]=selectedItem.getAgeGrade().split(",");
		for(int i=0;i<gradeIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.gradeParameterInfoById(gradeIdArr[i], conn).getGradeParameterName());
			ll.setValue(gradeIdArr[i]);
			selectedGradPara.add(ll);
		}

		mainParaId=selectedItem.getMainParameterId();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void viewDetails()
	{
		Connection conn=DataBaseConnection.javaConnection();
		selectedAgeGroup=selectedItem.getAgeGroupId();
		ArrayList<SelectItem> temp1=new ArrayList<>();
		String gradeIdArr[]=selectedItem.getAgeGrade().split(",");
		for(int i=0;i<gradeIdArr.length;i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(objEYFS.gradeParameterInfoById(gradeIdArr[i], conn).getGradeParameterName());
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
			if(selectedGradPara.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Grading Parameter"));
			}
			else
			{
				String gradeId="";
				int weight=0,maxWeight=0;
				for(SelectItem grade:selectedGradPara)
				{
					gradeId+=grade.getValue()+",";
					maxWeight=Integer.parseInt(objEYFS.gradeParameterInfoById(String.valueOf(grade.getValue()), conn).getWeightage());
					if(maxWeight>weight)
						weight=maxWeight;
				}
				gradeId=gradeId.substring(0,gradeId.lastIndexOf(","));

				int i=objEYFS.updateParaInAgeGroup(id,selectedAgeGroup,gradeId,mainParaId,weight,schid,session,conn);
				if(i>=0)
				{
					selectedAgeGroup=mainParaId="";checkForUpdate=false;
					selectedGradPara=new ArrayList<>();
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
			if(selectedGradPara.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select At Least One Grading Parameter"));
			}
			else
			{
				String gradeId="";int weight=0,maxWeight=0;
				for(SelectItem grade:selectedGradPara)
				{
					gradeId+=grade.getValue()+",";
					maxWeight=Integer.parseInt(objEYFS.gradeParameterInfoById(String.valueOf(grade.getValue()), conn).getWeightage());
					if(maxWeight>weight)
						weight=maxWeight;
				}
				gradeId=gradeId.substring(0,gradeId.lastIndexOf(","));

				boolean check=objEYFS.checkAlreadyAddedParaInAgeGroupOrNot(selectedAgeGroup,mainParaId,schid,session,conn);
				if(check==true)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parameter Already Added In Age Group... Please Edit For Change"));
				}
				else
				{
					int i=objEYFS.addParaInAgeGroup(selectedAgeGroup,gradeId,mainParaId,weight,schid,session,conn);
					if(i>=0)
					{
						selectedAgeGroup=mainParaId="";
						selectedGradPara=new ArrayList<>();
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
		int i=objEYFS.deleteMainParameterInAgeGroup(id,conn);
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

	/*
	public void editDetails()
	{
		name=selectedPara.getMainParameterName();
		description=selectedPara.getDescription();
		id=selectedPara.getId();
		checkForUpdate=true;
	}

	 */

	public ArrayList<SelectItem> getGradingParaList() {
		return gradingParaList;
	}

	public ArrayList<SelectItem> getAgeGroupList() {
		return ageGroupList;
	}

	public void setAgeGroupList(ArrayList<SelectItem> ageGroupList) {
		this.ageGroupList = ageGroupList;
	}

	public void setGradingParaList(ArrayList<SelectItem> gradingParaList) {
		this.gradingParaList = gradingParaList;
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

	public ArrayList<SelectItem> getSelectedGradPara() {
		return selectedGradPara;
	}

	public void setSelectedGradPara(ArrayList<SelectItem> selectedGradPara) {
		this.selectedGradPara = selectedGradPara;
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

	public String getMainParaId() {
		return mainParaId;
	}

	public void setMainParaId(String mainParaId) {
		this.mainParaId = mainParaId;
	}
}
