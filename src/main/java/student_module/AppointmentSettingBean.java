package student_module;

import java.io.Serializable;
import java.sql.Connection;
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
import session_work.RegexPattern;

@ManagedBean(name="appointmentSetting")
@ViewScoped

public class AppointmentSettingBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String startTime,endTime,name,designation;
	SchoolInfoList info;
	SelectItem selected;
	//fullSchoolInfo;
	DataBaseOnlineAdm obj = new DataBaseOnlineAdm();
	ArrayList<SelectItem> appointmentList=new ArrayList<>();
	ArrayList<SelectItem> detailLists=new ArrayList<>();
	DatabaseMethods1 dbm=new DatabaseMethods1();
	String schoolId,sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	
	public AppointmentSettingBean()
	{
		Connection conn=  DataBaseConnection.javaConnection();
		schoolId = dbm.schoolId();
		sessionValue = dbm.selectedSessionDetails(schoolId,conn);
		info=dbm.fullSchoolInfo(schoolId, conn);
		startTime=info.getAppointStartTime();
		endTime=info.getAppointEndTime();
		detailLists=obj.selectAllAppointMentPerson(conn);
		appointmentList=new ArrayList<>();
		int i=1;
		for(i=1;i<=5;i++)
		{
			SelectItem ss=new SelectItem();
			ss.setDescription(String.valueOf(i));
			appointmentList.add(ss);
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void check()
	{
		String arr1[]=startTime.split(":");
		int stime=Integer.valueOf(arr1[0]);
		String arr2[]=endTime.split(":");
		int etime=Integer.valueOf(arr2[0]);

		if(stime>=etime)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Start time is greater than end time!Please enter large start time"));
		}
	}
	public void delete()
	{
		Connection conn=  DataBaseConnection.javaConnection();
		try 
		{
			int i=obj.deleteAppointmentSettingByTableId(conn,selected.getDescription());
			if(i>=1)
			{
				String refNo2=addWorkLog2(conn);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This name and designation is deleted successfully!"));
				detailLists=obj.selectAllAppointMentPerson(conn);
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
			}
		} catch (Exception e) {
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
	public void edit()
	{
		name=selected.getLabel();
		designation=String.valueOf(selected.getValue());
	}
	public void update()
	{
		Connection conn=  DataBaseConnection.javaConnection();
		try 
		{
			if(name.equalsIgnoreCase(selected.getLabel()) && designation.equals(selected.getValue()))
			{

			}
			else
			{
				String k=obj.checkDuplicacyOfNameAndDesignation(conn,name,designation);
				if(k.equalsIgnoreCase("true"))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This name and designation is already exist!"));
				}
				else
				{
					int i=obj.updateNameAndDesignation(conn,name,designation,selected.getDescription());
					if(i>=1)
					{
						String refNo3No3=addWorkLog3(conn);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This name and designation is updated successfully!"));
						detailLists=obj.selectAllAppointMentPerson(conn);
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
					}
				}
			}
		} catch (Exception e) {
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
	public void submit()
	{
		Connection conn=  DataBaseConnection.javaConnection();
		
		try 
		{
			obj.updateAppointmentSettingInSchoolInfo(conn,schoolId,startTime,endTime);

			for(SelectItem ss:appointmentList)
			{
				String k=obj.checkDuplicacyOfNameAndDesignation(conn,ss.getLabel(),String.valueOf(ss.getValue()));
				if(k.equalsIgnoreCase("true"))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This name and designation is already exist!"));
				}
				else
				{
					if(ss.getLabel()==null || ss.getLabel().equals("") && ss.getValue()==null || ss.getValue().equals(""))
					{

					}
					else
					{
						int i=obj.submitAppointmentSetting(conn,schoolId,ss.getLabel(),ss.getValue().toString());
						if(i>=1)
						{		
							String refNo=addWorkLog(conn,ss.getLabel(),ss.getValue());
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Appointment setting is completed!"));
							detailLists=obj.selectAllAppointMentPerson(conn);
							/*if(j>=1)
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Time is updated successfully!"));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Time is not updated...somthing is wrong!"));
					}*/
							ss.setLabel("");
							ss.setValue("");
						}
						else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!"));
						}
					}
				}

			}
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
	
	
	public String addWorkLog(Connection conn,String label ,Object valueOb)
	{
	    String value = "";
		String language= "";
		
		
		language = startTime +" --- "+endTime;
		
				  value = "("+label+" --- "+valueOb.toString()+")";
			
		String refNo = workLg.saveWorkLogMehod(language,"Appointment Setting","WEB",value,conn);
		return refNo;
	}

public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "";
	
		language = name +" ---- "+designation;
		value = name +" ---- "+designation+" ---- "+selected.getDescription();
		
		String refNo = workLg.saveWorkLogMehod(language,"Appointment Edit","WEB",value,conn);
		return refNo;
	}

	
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		
		value = selected.getDescription()+" - "+selected.getLabel() ;
	

		
		String refNo = workLg.saveWorkLogMehod(language,"Appointment Delete","WEB",value,conn);
		return refNo;
	}


	
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public ArrayList<SelectItem> getAppointmentList() {
		return appointmentList;
	}

	public void setAppointmentList(ArrayList<SelectItem> appointmentList) {
		this.appointmentList = appointmentList;
	}
	public SchoolInfoList getInfo() {
		return info;
	}
	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}
	public DataBaseOnlineAdm getObj() {
		return obj;
	}
	public void setObj(DataBaseOnlineAdm obj) {
		this.obj = obj;
	}
	public ArrayList<SelectItem> getDetailLists() {
		return detailLists;
	}
	public void setDetailLists(ArrayList<SelectItem> detailLists) {
		this.detailLists = detailLists;
	}
	public SelectItem getSelected() {
		return selected;
	}
	public void setSelected(SelectItem selected) {
		this.selected = selected;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
