package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;

@ManagedBean(name="allSectionList")
@ViewScoped
public class AllSectionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<Category> categoryList;
	Category selectedSection;
	String username, schoolid, userType;
	DatabaseMethods1 obj= new DatabaseMethods1();
	public AllSectionBean() 
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		
		ArrayList<SelectItem> classList = new ArrayList<SelectItem>();
		String flag = "";
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			categoryList=obj.sectionList(conn);
			flag = "done";
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.cordinatorClassList(empid, schoolid, conn);
			flag="pending";
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolid,conn);
			classList=obj.allClassListForClassTeacher(empid,schoolid,conn);
			flag="pending";
		}
		
		if(flag.equalsIgnoreCase("pending"))
		{
			categoryList = new ArrayList<Category>();
			
			for(SelectItem si : classList)
			{
				ArrayList<Category> temp = obj.sectionListByid(schoolid, "nostudent", String.valueOf(si.getValue()), conn);
				if(temp.size()>0)
				{
					categoryList.addAll(temp);
				}
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void viewDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedSection", selectedSection);
		try 
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("studentPhotoList.xhtml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void viewDocDetails()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("selectedSection", selectedSection);
		try 
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect("studentDocumentList.xhtml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}
	public Category getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(Category selectedSection) {
		this.selectedSection = selectedSection;
	}
}
