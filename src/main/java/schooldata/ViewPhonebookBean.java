package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="viewPhonebook")
@ViewScoped

public class ViewPhonebookBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<NotificationInfo> contactList;
	NotificationInfo selectedContact;
	String contactName="",designation="",contactNo="";
	public ViewPhonebookBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		contactList = new DatabaseMethods1().viewPhonebook(new DatabaseMethods1().schoolId(),conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void navigatePhonebook() throws IOException
	{
		FacesContext.getCurrentInstance().getExternalContext().redirect("phonebook.xhtml");
	}

	public void editDetail()
	{
		contactName=selectedContact.getContactName();
		designation=selectedContact.getDesignation();
		contactNo=selectedContact.getContactNo();
	}

	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		String id=selectedContact.getId();
		int i = obj.updatePhonebook(contactName,designation,contactNo,id,conn);
		if(i>=1)
		{
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Contact Updated Successfully!"));
			contactList = obj.viewPhonebook(new DatabaseMethods1().schoolId(),conn);
			contactName=designation=contactNo=id="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur.Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		language = "ContactName-"+contactName+" --Designation-"+designation+" --Contact No-"+contactNo;
		value = "Selected Contact Id-"+selectedContact.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Directory Contact","WEB",value,conn);
		return refNo;
	}
	

	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		String id = selectedContact.getId();
		int i=obj.deleteContact(id,conn);
		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Contact Deleted Successfully!"));
			contactList = obj.viewPhonebook(new DatabaseMethods1().schoolId(),conn);
			id="";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur.Please Try Again!"));
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		value = "Selected Contact Id-"+selectedContact.getId();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Directory Contact","WEB",value,conn);
		return refNo;
	}

	public ArrayList<NotificationInfo> getContactList() {
		return contactList;
	}

	public void setContactList(ArrayList<NotificationInfo> contactList) {
		this.contactList = contactList;
	}

	public NotificationInfo getSelectedContact() {
		return selectedContact;
	}

	public void setSelectedContact(NotificationInfo selectedContact) {
		this.selectedContact = selectedContact;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
