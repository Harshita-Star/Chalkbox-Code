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

@ManagedBean(name="permission")
@ViewScoped
public class PermissionBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String userType;
	ArrayList<ModuleInfo> moduleList;
	ArrayList<SelectItem> pageList,innerPageList,userList;
	ArrayList<String> selectedValue,selectedInnerValue;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	
	public PermissionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		userList=obj.getAllUserTypeForPermission(conn);
		SelectItem ss = new SelectItem();
//		ss.setLabel("Vice Principal");
//		ss.setValue("Vice Principal");
		for(SelectItem si : userList)
		{
			if(si.getLabel().equalsIgnoreCase("Vice Principal"))
			{
				ss = si;
			}
			
			if(si.getLabel().equalsIgnoreCase("Principal"))
			{
				si.setLabel("Principal / Vice Principal");
			}
		}
		userList.remove(ss);
		moduleList=obj.allUserPermissionModules(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkUserType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		obj.permissionOfUserForAdmin(userType,moduleList,conn);
		/*for(ModuleInfo mm : moduleList)
		{

			if(mm.getSelectedPages().length>0)
			{
				for(int i=0;i<mm.getSelectedPages().length;i++)
				{
					////// // System.out.println("sds : "+mm.getSelectedPages()[i]);
				}
			}
		}*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkSelectedValue()
	{
		if(selectedValue.contains("all"))
		{
			selectedValue=new ArrayList<>();
			selectedValue.add("all");
			for(SelectItem ll:pageList)
			{
				selectedValue.add((String) ll.getValue());
			}
		}
		else if(selectedValue.size()==pageList.size() && !selectedValue.contains("all"))
		{
			selectedValue=new ArrayList<>();
		}

	}

	public void checkInnerSelectedValue()
	{
		if(selectedInnerValue.contains("all"))
		{
			selectedInnerValue=new ArrayList<>();
			selectedInnerValue.add("all");
			for(SelectItem ll:innerPageList)
			{
				selectedInnerValue.add((String) ll.getValue());
			}
		}
		/*else if(selectedInnerValue.size()==innerPageList.size() && !selectedInnerValue.contains("all"))
		{
			selectedInnerValue=new ArrayList<>();
		}*/

	}

	public void submitValue()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i = obj.setUserPermissionsByAdmin(userType,moduleList,conn);
		if(i>=1)
		{
			String refNo;
		     try {
				  refNo=addWorkLog(conn);
				}
		     catch (Exception e) {
		    	 e.printStackTrace();
				}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permissions Added Sucessfully!"));
			selectedValue=selectedInnerValue=new ArrayList<>();
			userType="";
			moduleList=obj.allUserPermissionModules(conn);		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please Check!"));
		}

		/*//// // System.out.println(selectedValue);
		if(selectedValue.size()==0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select Atleast one Permission"));
		}
		else
		{
			int i=new DatabaseMethods1().addPermission(userType,selectedValue,selectedInnerValue);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permission Added Sucessfully"));
				selectedValue=selectedInnerValue=new ArrayList<>();
				userType="";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
			}
		}*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value =  "";
		String language= userType;
		
		for(ModuleInfo lss:moduleList)
		{
		  String subValue = "";
		  
		   if (lss.getSelectedPages().length > 0) 
		   {
					
			   for (int i = 0; i < lss.getSelectedPages().length; i++)
			   {	
					 subValue +=  lss.getSelectedPages()[i]+"--";
			   }
			   if(!subValue.equalsIgnoreCase(""))
			   {
				   subValue = subValue.substring(0,subValue.length()-2);
			   }
			   
			  
			   
			  if(value.equals(""))
			{
			
					value="("+lss.getModuleName()+"----"+subValue+")";
				
			}
			else
			{
				
					value=value+"("+lss.getModuleName()+"----"+subValue+")";
				
			}
		  }	
		}
		
		String refNo = workLg.saveWorkLogMehod(language,"Update Permissions","WEB",value,conn);
		return refNo;
	}


	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public ArrayList<SelectItem> getPageList() {
		return pageList;
	}
	public void setPageList(ArrayList<SelectItem> pageList) {
		this.pageList = pageList;
	}
	public ArrayList<String> getSelectedValue() {
		return selectedValue;
	}
	public void setSelectedValue(ArrayList<String> selectedValue) {
		this.selectedValue = selectedValue;
	}
	public ArrayList<SelectItem> getInnerPageList() {
		return innerPageList;
	}
	public void setInnerPageList(ArrayList<SelectItem> innerPageList) {
		this.innerPageList = innerPageList;
	}
	public ArrayList<String> getSelectedInnerValue() {
		return selectedInnerValue;
	}
	public void setSelectedInnerValue(ArrayList<String> selectedInnerValue) {
		this.selectedInnerValue = selectedInnerValue;
	}

	public ArrayList<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<SelectItem> userList) {
		this.userList = userList;
	}

	public ArrayList<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

}
