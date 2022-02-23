package reports_module;

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
import schooldata.SchoolInfoList;

@ManagedBean(name="waiveTransport")
@ViewScoped

public class WaiveTransportBean implements Serializable
{
	ArrayList<SelectItem> classList, list = new ArrayList<>();
	boolean show;
	SchoolInfoList ls = new SchoolInfoList();
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();
	String sessionValue,schoolId,selectedClass;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();

	public WaiveTransportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		selectedClass = "";
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		classList=dbm.allClass(conn);
		list = dbm.allMonthsTransport(conn);
		//proceed();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void proceed()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String status = "";
		for(SelectItem ss : list)
		{
			status = dbr.checkWhetherTransportWaived(schoolId,sessionValue,
					String.valueOf(ss.getValue()),selectedClass,conn);

			ss.setDescription(status);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = 0;
		for(SelectItem ss : list)
		{
			i = dbr.updateTransportWaived(schoolId,sessionValue,String.valueOf(ss.getValue()),
					ss.getDescription(),selectedClass,conn);
			
			
			dbr.updateTransportWaiverInTransportFeeTable(ss.getDescription(), "0", String.valueOf(ss.getValue()),schoolId,
					sessionValue,selectedClass, conn);
			
		}

		if(i>=1)
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String refNo2;
			try {
				refNo2=addWorkLog2(conn);
			} catch (Exception e) {
			    e.printStackTrace();
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Transport Settings Updated Successfully!"));
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong. Please Try Again!"));
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
		
	   for(SelectItem ss : list)
	   {
		   if(value.equals(""))
		  {
			  value = "( Description - "+ss.getDescription()+" -- Value - "+ss.getValue()+")"; 
		  }
		   else
		   {
			    value = value + "( Description - "+ss.getDescription()+" -- Value - "+ss.getValue()+")";
		   }
		   
	   }
		
		String refNo = workLg.saveWorkLogMehod(language,"Waive Transport","WEB",value,conn);
		
		
		
		return refNo;
	}
	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
	   for(SelectItem ss : list)
	   {
		   if(value.equals(""))
		  {
			 if(ss.getDescription().equalsIgnoreCase("No"))  
			 {
				 value = "(RouteId - 0 -- Status - "+ss.getDescription()+" -- Month - "+ss.getValue()+")"; 
			 }
			 else
			 {
				 value = "( Status - "+ss.getDescription()+" -- Month - "+ss.getValue()+")";
			 }
			   
		  }
		   else
		   {
			   if(ss.getDescription().equalsIgnoreCase("No"))  
				 {
					 value = value + "(RouteId - 0 -- Status - "+ss.getDescription()+" -- Month - "+ss.getValue()+")"; 
				 }
				 else
				 {
					 value = value + "( Status - "+ss.getDescription()+" -- Month - "+ss.getValue()+")";
				 }
		   }
		   
	   }
		
		String refNo = workLg.saveWorkLogMehod(language,"Waive Transport Fee","WEB",value,conn);
		
		
		
		return refNo;
	}


	public ArrayList<SelectItem> getList() {
		return list;
	}

	public void setList(ArrayList<SelectItem> list) {
		this.list = list;
	}

	public ArrayList<SelectItem> getClassList() {
		return classList;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
}
