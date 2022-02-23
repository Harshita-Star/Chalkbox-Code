package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="schoolEnquiryReport")
@ViewScoped
public class SchoolEnquiryReport implements Serializable
{
    
	
	ArrayList<SchoolEnquiryList>list=new ArrayList<>();
	SchoolEnquiryList selectedList;
	
	String updatedComment;
	
	public SchoolEnquiryReport() 
	{

		Connection conn=DataBaseConnection.javaConnection();
	list=new DatabaseMethods1().allSchoolEnquiry(conn);
	
	try {
		conn.close();
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
	}

	public void deletEnquiry()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int i=new DatabaseMethods1().deleteSchoolEnquiry(selectedList.getId(),conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Enquiry Deleted Successfully!"));
			
			list=new DatabaseMethods1().allSchoolEnquiry(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur Please Try Again !"));
			
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public void viewComment()
	{
		updatedComment=selectedList.getComment();
		
	}
	
	public void updateEnquiryComment()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int i=new DatabaseMethods1().updateSchoolComment(selectedList.getId(),updatedComment,conn);
		if(i>0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("School Enquiry Comment Updated Successfully!"));
			
			list=new DatabaseMethods1().allSchoolEnquiry(conn);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur Please Try Again !"));
			
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	
	
	
	

	public ArrayList<SchoolEnquiryList> getList() {
		return list;
	}


	public void setList(ArrayList<SchoolEnquiryList> list) {
		this.list = list;
	}

	public SchoolEnquiryList getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(SchoolEnquiryList selectedList) {
		this.selectedList = selectedList;
	}

	public String getUpdatedComment() {
		return updatedComment;
	}

	public void setUpdatedComment(String updatedComment) {
		this.updatedComment = updatedComment;
	}
	
	
	
	
}
