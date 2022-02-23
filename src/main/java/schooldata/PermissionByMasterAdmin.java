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
import javax.servlet.http.HttpSession;
@ManagedBean(name="permissionByMasterAdmin")
@ViewScoped
public class PermissionByMasterAdmin implements Serializable
{
	private static final long serialVersionUID = 1L;
	String userType="Admin",schid;
	ArrayList<ModuleInfo> moduleList,userPermissionList;
	ArrayList<SelectItem> pageList,innerPageList,userList;
	ArrayList<String> selectedValue,selectedInnerValue;

	public PermissionByMasterAdmin()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//userList=new DatabaseMethods1().getAllUserTypeForPermission(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schid=(String) ss.getAttribute("schid");
		//userList=new DatabaseMethods1().getAllUser(schid,conn);
		moduleList=new DatabaseMethods1().allPermissionModules(schid,conn);
		checkUserType();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkUserType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1.permissionOfUser(userType,moduleList,schid,conn);
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
		DatabaseMethods1 obj=new DatabaseMethods1();
		/*String status="",page="";
        for(SelectItem ss:userList)
        {
            for(ModuleInfo mm:moduleList)
            {
                for(SelectItem pp:mm.getPageList())
                {
                    page=pp.getLabel();
                    status=new DatabaseMethods1().checkPermissionForOtherCategory(ss.getLabel(),pp.getLabel(),schid,conn);
                    if(status.equalsIgnoreCase(ss.getLabel()))
                    {
                        break;
                    }
                }
                if(status.equalsIgnoreCase(ss.getLabel()))
                {
                    break;
                }
            }
            if(status.equalsIgnoreCase(ss.getLabel()))
            {
                break;
            }
            }
        if(status.equalsIgnoreCase(""))
        {*/
		String pages="";
		int i = obj.setUserPermissions(userType,moduleList,schid,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permissions Added Successfully!"));
			selectedValue=selectedInnerValue=new ArrayList<>();

			for(ModuleInfo mm : moduleList)
			{
				if(mm.getSelectedPages().length>0)
				{
					for(int x=0;x<mm.getSelectedPages().length;x++)
					{
						if(pages.equals(""))
						{
							pages=mm.getSelectedPages()[x];
						}
						else
						{
							pages=pages+"','"+mm.getSelectedPages()[x];
						}
					}
				}
			}

			//String permit=new DatabaseMethods1().checkPermissionOfAdmin(userType,schid,conn);
			String permit="'"+pages+"'";
			////// // System.out.println(permit);
			obj.deletePermission(schid,permit,conn);
			//userType="";
			moduleList=obj.allPermissionModules(schid, conn);
			checkUserType();
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please Check!"));
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public ArrayList<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

	public ArrayList<SelectItem> getPageList() {
		return pageList;
	}

	public void setPageList(ArrayList<SelectItem> pageList) {
		this.pageList = pageList;
	}

	public ArrayList<SelectItem> getInnerPageList() {
		return innerPageList;
	}

	public void setInnerPageList(ArrayList<SelectItem> innerPageList) {
		this.innerPageList = innerPageList;
	}

	public ArrayList<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<SelectItem> userList) {
		this.userList = userList;
	}

	public ArrayList<String> getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(ArrayList<String> selectedValue) {
		this.selectedValue = selectedValue;
	}

	public ArrayList<String> getSelectedInnerValue() {
		return selectedInnerValue;
	}

	public void setSelectedInnerValue(ArrayList<String> selectedInnerValue) {
		this.selectedInnerValue = selectedInnerValue;
	}


}
