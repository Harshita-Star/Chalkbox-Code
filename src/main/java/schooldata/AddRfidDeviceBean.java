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

import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@ManagedBean(name="addRfidDevice")
@ViewScoped

public class AddRfidDeviceBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	ArrayList<SelectItem>schoolList;
	String schid,name,imei,type,dvcType,schnm,oldImei;
	ArrayList<Route> list = new ArrayList<>();
	Route selectedDevice = new Route();
	public AddRfidDeviceBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolList=new DatabaseMethods1().getAllSchool(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		boolean check = new DataBaseMethodsReports().checkDuplicateRfidDevice(imei,conn);
		if(check)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate IMEI No. Try Again!"));
			return "";
		}
		
		int i = new DataBaseMethodsReports().addRfid(schid,name,imei,type,"add",conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Device added successfully"));
			name = imei = type = "";
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public void deviceList()
	{
		Connection conn = DataBaseConnection.javaConnection();
		list = new DataBaseMethodsReports().rfidDeviceList(schnm,dvcType,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void editDetail()
	{
		oldImei=selectedDevice.getRfidImei();
	}
	
	public String update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		if(!oldImei.equalsIgnoreCase(selectedDevice.getRfidImei()))
		{
			boolean check = new DataBaseMethodsReports().checkDuplicateRfidDevice(selectedDevice.getRfidImei(),conn);
			if(check)
			{
				selectedDevice.setRfidImei(oldImei);
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate IMEI No. Try Again!"));
				return "";
			}
		}
		
		int i = new DataBaseMethodsReports().addRfid(selectedDevice.getRfidDeviceId(),selectedDevice.getRfidName(), 
				selectedDevice.getRfidImei(),selectedDevice.getRfidType(),"edit",conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Device updated successfully"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i = new DataBaseMethodsReports().deleteRfidDevice(selectedDevice.getRfidDeviceId(),conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Device deleted successfully"));
			list = new DataBaseMethodsReports().rfidDeviceList(schnm,dvcType,conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<SelectItem> schoolList) {
		this.schoolList = schoolList;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSchnm() {
		return schnm;
	}

	public void setSchnm(String schnm) {
		this.schnm = schnm;
	}

	public ArrayList<Route> getList() {
		return list;
	}

	public void setList(ArrayList<Route> list) {
		this.list = list;
	}

	public Route getSelectedDevice() {
		return selectedDevice;
	}

	public void setSelectedDevice(Route selectedDevice) {
		this.selectedDevice = selectedDevice;
	}

	public String getDvcType() {
		return dvcType;
	}

	public void setDvcType(String dvcType) {
		this.dvcType = dvcType;
	}

	public String getOldImei() {
		return oldImei;
	}

	public void setOldImei(String oldImei) {
		this.oldImei = oldImei;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
