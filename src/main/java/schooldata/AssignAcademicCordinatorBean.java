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

@ManagedBean(name="assignAcademicCordinator")
@ViewScoped

public class AssignAcademicCordinatorBean implements Serializable 
{
	String staffid, schid;
	ArrayList<SelectItem> staffList, classList, assignList, selectedList;
	SelectItem selected = new SelectItem();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DBMethodsNew objnew = new DBMethodsNew();
	
	public AssignAcademicCordinatorBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		schid = obj.schoolId();
		String categid = obj.employeeCategoryIdByName("Administrative Officer", conn);
		staffList = obj.allteacherOnly(categid,schid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		Connection conn = DataBaseConnection.javaConnection();
		selectedList = new ArrayList<SelectItem>();
		assignList = obj.cordinatorClassList(staffid, schid, conn);
		ArrayList<SelectItem> temp = obj.allClass(conn);
		classList = new ArrayList<SelectItem>();
//		for(SelectItem si : temp)
//		{
//			if(!assignList.contains(si))
//			{
//				classList.add(si);
//			}
//		}
		
		boolean check = false;
		for(SelectItem ss : temp)
		{
			check = false;
			loop:for(SelectItem si : assignList)
			{
				if(ss.getDescription().equalsIgnoreCase(si.getDescription()))
				{
					check = true;
					break loop;
				}
			}
			
			if(!check)
			{
				classList.add(ss);
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		if(selectedList.size()>0)
		{
			int i = objnew.addCordinatorClasses(selectedList, staffid, schid, conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Classes Assigned Successfully!"));
				search();
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something Went Wrong. Please Try Again!", "Action Blocked"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select atleast 1 class to submit!", "Action Blocked"));
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = objnew.deleteCordinatorClass(staffid, String.valueOf(selected.getValue()), schid, conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class Deleted Successfully!"));
			search();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something Went Wrong. Please Try Again!", "Action Blocked"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getStaffid() {
		return staffid;
	}

	public void setStaffid(String staffid) {
		this.staffid = staffid;
	}

	public ArrayList<SelectItem> getStaffList() {
		return staffList;
	}

	public void setStaffList(ArrayList<SelectItem> staffList) {
		this.staffList = staffList;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public ArrayList<SelectItem> getAssignList() {
		return assignList;
	}

	public void setAssignList(ArrayList<SelectItem> assignList) {
		this.assignList = assignList;
	}

	public ArrayList<SelectItem> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<SelectItem> selectedList) {
		this.selectedList = selectedList;
	}

	public SelectItem getSelected() {
		return selected;
	}

	public void setSelected(SelectItem selected) {
		this.selected = selected;
	}
}
