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

@ManagedBean(name="phonebook")
@ViewScoped

public class PhonebookBean implements Serializable
{
	ArrayList<NotificationInfo> contactList = new ArrayList<>();
	int j=0;
	public PhonebookBean()
	{
		contactList =new ArrayList<>();
		for(int i=1;i<=5;i++)
		{
			NotificationInfo in=new NotificationInfo();
			in.setSno(i);
			contactList.add(in);
			j=i;
		}
	}

	public void addNewRow()
	{
		int k=j;
		for(int i=k+1;i==k+1;k++)
		{
			NotificationInfo in=new NotificationInfo();
			in.setSno(i);
			contactList.add(in);
			j=i;
		}
	}

	public String submit()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			int i=new DatabaseMethods1().addPhonebook(contactList,conn);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Contacts Added successfully"));

				return "phonebook.xhtml";
			}
			else
			{
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Some Error Occur.Please Try Again!"));

				return "";
			}
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		for(NotificationInfo ni : contactList)
		{
			value += "("+ni.getContactName()+" - "+ni.getDesignation()+" - "+ni.getContactNo()+")";
		}
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Directory Contacts","WEB",value,conn);
		return refNo;
	}
	
	

	public void navigateViewPhonebook() throws IOException
	{
		FacesContext.getCurrentInstance().getExternalContext().redirect("viewPhonebook.xhtml");
	}

	public ArrayList<NotificationInfo> getContactList() {
		return contactList;
	}

	public void setContactList(ArrayList<NotificationInfo> contactList) {
		this.contactList = contactList;
	}


}
