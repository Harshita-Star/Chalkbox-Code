package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name="editSubGroup")
@ViewScoped
public class EditSubjectGroupBean implements Serializable{
	private static final long serialVersionUID = 1L;
	String classId,selectedType,groupName,id; 
	ArrayList<SelectItem> subjectTypeList,subjectList,classList;
	ArrayList<SubjectInfo> allSubjectGroupList;
	SubjectInfo selSubGroup;
	DatabaseMethods1 obj=new DatabaseMethods1();
	ArrayList<String> selectedSubList;
	String rank;
	
	public EditSubjectGroupBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		allSubjectGroupList=obj.allSubjectGroupList(conn);
		subjectTypeList=obj.subjectTypeList();
		classList=obj.allClass(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void allSubjectList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		subjectList=new DatabaseMethods1().allSubjectListBySubjectType(classId, selectedType, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editDetails()
	{
		classId=String.valueOf(selSubGroup.getClassId());
		selectedType=selSubGroup.getSubjectType();
		allSubjectList();
		selectedSubList=selSubGroup.getSupplimentarySubject();
		groupName=selSubGroup.getGrade();
		rank = selSubGroup.getRank();
		id=selSubGroup.getId();
	}
	
	public void update()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String subject="";
		if(selectedSubList.size()<=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Add More Than One Subject"));
		}
		else
		{
			int flag=0;
			for(String sub:selectedSubList)
			{
				boolean checkAlreadyAdded=new DatabaseMethods1().checkSubjectAlreadyAddedInGroup(classId,sub,id,conn);
				if(checkAlreadyAdded==true)
				{
					flag=1;
				}
				subject+=sub+",";
			}
			if(flag==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Subject Is Already Added In Another Group"));
			}
			else
			{
				if(subject.contains(","))
					subject=subject.substring(0, subject.lastIndexOf(","));
				
				boolean duplicate=new DatabaseMethods1().checkDuplicateSubjectGroup(classId,groupName,id,conn);
				if(duplicate==true)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Entry Found.. Please Try With Another One"));
				}
				else
				{		
					int i=obj.updateSubjectGroup(groupName,subject,id,conn,rank);
					if(i>=1)
					{
						classId=groupName=selectedType="";
						selectedSubList=null;
						subjectList=classList=null;
						allSubjectGroupList=obj.allSubjectGroupList(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Group Updated Successfully"));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
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
	
	
	public void deleteSubjectGroup()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=obj.deleteSubjectGroup(selSubGroup.getId(),conn);
		if(i>=1)
		{
			allSubjectGroupList=obj.allSubjectGroupList(conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Group Deleted Successfully"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured.. Please Try Again"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getClassList()
	{
		return classList;
	}
	
	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public ArrayList<SelectItem> getSubjectTypeList() {
		return subjectTypeList;
	}
	public void setSubjectTypeList(ArrayList<SelectItem> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}

	public ArrayList<String> getSelectedSubList() {
		return selectedSubList;
	}

	public void setSelectedSubList(ArrayList<String> selectedSubList) {
		this.selectedSubList = selectedSubList;
	}

	public ArrayList<SubjectInfo> getAllSubjectGroupList() {
		return allSubjectGroupList;
	}

	public void setAllSubjectGroupList(ArrayList<SubjectInfo> allSubjectGroupList) {
		this.allSubjectGroupList = allSubjectGroupList;
	}

	public SubjectInfo getSelSubGroup() {
		return selSubGroup;
	}

	public void setSelSubGroup(SubjectInfo selSubGroup) {
		this.selSubGroup = selSubGroup;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
}
