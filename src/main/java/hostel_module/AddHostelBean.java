package hostel_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;


@ManagedBean (name="addHostel")
@ViewScoped

public class AddHostelBean implements Serializable{
	String regex=RegexPattern.REGEX;
	String hostelName;
	public ArrayList<RoomInfo> viewHostelName;
	boolean showdetail;
	RoomInfo selectedHostel;
	DatabaseMethods1 obj1=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsHostelModule obj=new DataBaseMethodsHostelModule();
	
	public AddHostelBean()
	{
		Connection conn= DataBaseConnection.javaConnection();
		schoolId = obj1.schoolId();
		sessionValue = obj1.selectedSessionDetails(schoolId,conn);
		viewHostelName = obj.hostelNameList(conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void hostelName()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try 
		{
			if(hostelName.trim().equals("")) 
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hostel Name Cannot be blank"));
			}
			else
			{
				boolean duplicateHostelName=obj.checkForDuplicateHostelName(conn,hostelName);
				if(duplicateHostelName==false)
				{
					int i=obj.addHostelName(hostelName,conn);
					if(i>0)
					{
						addWorkLog(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hostel Added Sucessfully."));
						viewHostelName = obj.hostelNameList(conn);
						hostelName="";
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
					}}
				else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Hostel Name Found"));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		language = value = hostelName;
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Hostel","WEB",value,conn);
		return refNo;
	}
	

	public String updateHostelName()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			if(selectedHostel.getHostelName().trim().equals("") || selectedHostel.getHostelName().isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hostel Name Cannot be blank"));
			}
			else
			{

				int k=obj.editHostelName(conn,selectedHostel.getId(),selectedHostel.getHostelName());
				if(k>0)
				{
					addWorkLog3(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("updated sucessfully"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hostel Name already exists"));
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "addHostel.xhtml";
	}
	
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";

		language = value = "Id-"+selectedHostel.getId()+" -- Name-"+selectedHostel.getHostelName();
		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Hostel","WEB",value,conn);
		return refNo;
	}
	

	public void deleteHostel()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			int k=obj.deleteHostel(conn,selectedHostel.getId());

			if(k>0)
			{
				addWorkLog2(conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted sucessfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error"));
			}

			viewHostelName = obj.hostelNameList(conn);
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		finally 
		{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";

		language = value = "Hostel Id-"+selectedHostel.getId();
		
		String refNo = workLg.saveWorkLogMehod(language,"Delete Hostel","WEB",value,conn);
		return refNo;
	}


	public String getHostelName() {
		return hostelName;
	}

	public void setHostelName(String hostelName) {
		this.hostelName = hostelName;
	}

	public ArrayList<RoomInfo> getViewHostelName() {
		return viewHostelName;
	}

	public void setViewHostelName(ArrayList<RoomInfo> viewHostelName) {
		this.viewHostelName = viewHostelName;
	}

	public boolean isShowdetail() {
		return showdetail;
	}

	public void setShowdetail(boolean showdetail) {
		this.showdetail = showdetail;
	}

	public RoomInfo getSelectedHostel() {
		return selectedHostel;
	}

	public void setSelectedHostel(RoomInfo selectedHostel) {
		this.selectedHostel = selectedHostel;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}