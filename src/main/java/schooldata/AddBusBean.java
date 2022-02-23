package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import reports_module.DataBaseMethodsReports;
import session_work.RegexPattern;

@ManagedBean(name="addBus")
@ViewScoped
public class AddBusBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	String busName,busNo,gpsNo,rfidno="",driver,conductor,schid,id;
	long conContactNo,driverConNo;
	ArrayList<SelectItem> driverList,conductorList,gpsList;
	ArrayList<Route> rfidList= new ArrayList<>();
	Date insuranceDate,fitnessDate,balVahiniPermit,pollutionDue,roadTaxDate;
	ArrayList<BusInfo> busList;
	int totalBus;
	boolean checkUpdate=false,showGPS,showRfid;
	BusInfo selectedBus;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsReports dbr = new DataBaseMethodsReports();


	public AddBusBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schid=obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schid,conn);
		String checkGPS=obj.checkGPS(conn);
		showRfid = obj.checkRfid(schid,"Transport",conn);
		rfidList = dbr.rfidDeviceList(schid,"Transport",conn);
		if(checkGPS.equalsIgnoreCase("yes"))
		{
			gpsList=obj.gpsList(conn);
			showGPS=true;
		}
		else
		{
			showGPS=false;
		}
		String cat=obj.drivercat(conn);
		String concat=obj.concat(conn);
		driverList=obj.drierlist(conn,cat);
		conductorList=obj.conList(conn,concat);

		busList();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateNo() {

		Connection conn=DataBaseConnection.javaConnection();

		driverConNo=obj.drivercontact(driver,conn);
		conContactNo=obj.concontact(conductor,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void busList()
	{
		Connection conn=DataBaseConnection.javaConnection();
		busList=obj.allBusList(schid,conn);
		totalBus=busList.size();
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void editDetails()
	{
		id=selectedBus.getId();
		busName=selectedBus.getBusName();
		busNo=selectedBus.getBusNo();
		rfidno=selectedBus.getRfidDeviceId();
		gpsNo=selectedBus.getGpsId();
		driver=selectedBus.getDriverId();
		conductor=selectedBus.getConductorId();
		insuranceDate=selectedBus.getInsuranceDate();
		fitnessDate=selectedBus.getFitnessDate();
		roadTaxDate=selectedBus.getRoadTaxDate();
		balVahiniPermit=selectedBus.getBalVahiniPermit();
		pollutionDue=selectedBus.getPollutionDue();
		driverConNo=Long.parseLong(selectedBus.getDriverContact());
		conContactNo=Long.parseLong(selectedBus.getConductorContact());
		checkUpdate=true;
		PrimeFaces.current().scrollTo("form:mainPnl");
	}

	public void saveData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int i=0;
		if(checkUpdate==true)
		{
			i=obj.updateBus(id,busName,busNo,gpsNo,driver,conductor,insuranceDate,fitnessDate,balVahiniPermit, 
					pollutionDue,roadTaxDate,rfidno,schid,conn);
			if(i>0)
			{
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				busName=busNo=gpsNo=driver=conductor=rfidno="";
				driverConNo=conContactNo=0;
				insuranceDate=fitnessDate=balVahiniPermit=pollutionDue=roadTaxDate=null;
				checkUpdate=false;
				busList();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bus Info Updated Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
			}
		}
		else
		{
			i=obj.addBus(busName,busNo,gpsNo,driver,conductor,insuranceDate,fitnessDate,balVahiniPermit, 
					pollutionDue,roadTaxDate,rfidno,schid,conn);
			if(i>0)
			{
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				busName=busNo=gpsNo=driver=conductor=rfidno="";
				driverConNo=conContactNo=0;
				checkUpdate=false;
				insuranceDate=fitnessDate=balVahiniPermit=pollutionDue=roadTaxDate=null;
				busList();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bus Added Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
			}
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String insurance = sdf.format(insuranceDate);
		String fitness = sdf.format(fitnessDate);
		String balVahine = sdf.format(balVahiniPermit);
		String pollution = sdf.format(pollutionDue);
		String road = sdf.format(roadTaxDate);
		
		
	
		language = busName+" -- "+busNo+" -- "+driver+" -- "+conductor+" -- "+insurance+" -- "+fitness+" -- "+balVahine+" -- "+pollution+" -- "+road+" -- "+gpsNo; 
		value = busName+" -- "+busNo+" -- "+driver+" -- "+conductor+" -- "+insurance+" -- "+fitness+" -- "+balVahine+" -- "+pollution+" -- "+road+" -- "+gpsNo +" -- "+rfidno; 

		
		String refNo = workLg.saveWorkLogMehod(language,"Add Bus","WEB",value,conn);
		return refNo;
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String insurance = sdf.format(insuranceDate);
		String fitness = sdf.format(fitnessDate);
		String balVahine = sdf.format(balVahiniPermit);
		String pollution = sdf.format(pollutionDue);
		String road = sdf.format(roadTaxDate);
		
		
	
		language = busName+" -- "+busNo+" -- "+driver+" -- "+conductor+" -- "+insurance+" -- "+fitness+" -- "+balVahine+" -- "+pollution+" -- "+road+" -- "+gpsNo; 
		value = busName+" -- "+busNo+" -- "+driver+" -- "+conductor+" -- "+insurance+" -- "+fitness+" -- "+balVahine+" -- "+pollution+" -- "+road+" -- "+gpsNo +" -- "+rfidno; 

		
		String refNo = workLg.saveWorkLogMehod(language,"Edit Bus","WEB",value,conn);
		return refNo;
	}


	public void deleteBus()
	{
		Connection conn=DataBaseConnection.javaConnection();
		boolean check=obj.checkBusDeletedOrNot(selectedBus.getId(),schid,conn);
		if(check==true)
		{
			int i=obj.deleteBus(selectedBus.getId(),conn);
			if(i>0)
			{
				busList();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bus Deleted Successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured... Please Try Again"));
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Bus Can Not Be Deleted Because It Is Assigned To Route"));
		}

		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getBusName() {
		return busName;
	}
	public void setBusName(String busName) {
		this.busName = busName;
	}
	public String getBusNo() {
		return busNo;
	}
	public void setBusNo(String busNo) {
		this.busNo = busNo;
	}
	public String getGpsNo() {
		return gpsNo;
	}
	public void setGpsNo(String gpsNo) {
		this.gpsNo = gpsNo;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getConductor() {
		return conductor;
	}
	public void setConductor(String conductor) {
		this.conductor = conductor;
	}
	public Date getInsuranceDate() {
		return insuranceDate;
	}
	public void setInsuranceDate(Date insuranceDate) {
		this.insuranceDate = insuranceDate;
	}
	public Date getFitnessDate() {
		return fitnessDate;
	}
	public void setFitnessDate(Date fitnessDate) {
		this.fitnessDate = fitnessDate;
	}
	public Date getBalVahiniPermit() {
		return balVahiniPermit;
	}
	public void setBalVahiniPermit(Date balVahiniPermit) {
		this.balVahiniPermit = balVahiniPermit;
	}
	public Date getPollutionDue() {
		return pollutionDue;
	}
	public void setPollutionDue(Date pollutionDue) {
		this.pollutionDue = pollutionDue;
	}
	public ArrayList<BusInfo> getBusList() {
		return busList;
	}
	public void setBusList(ArrayList<BusInfo> busList) {
		this.busList = busList;
	}
	public BusInfo getSelectedBus() {
		return selectedBus;
	}
	public void setSelectedBus(BusInfo selectedBus) {
		this.selectedBus = selectedBus;
	}

	public int getTotalBus() {
		return totalBus;
	}

	public void setTotalBus(int totalBus) {
		this.totalBus = totalBus;
	}

	public ArrayList<SelectItem> getDriverList() {
		return driverList;
	}

	public void setDriverList(ArrayList<SelectItem> driverList) {
		this.driverList = driverList;
	}

	public ArrayList<SelectItem> getConductorList() {
		return conductorList;
	}

	public void setConductorList(ArrayList<SelectItem> conductorList) {
		this.conductorList = conductorList;
	}

	public ArrayList<SelectItem> getGpsList() {
		return gpsList;
	}

	public void setGpsList(ArrayList<SelectItem> gpsList) {
		this.gpsList = gpsList;
	}

	public boolean isShowGPS() {
		return showGPS;
	}

	public void setShowGPS(boolean showGPS) {
		this.showGPS = showGPS;
	}

	public long getConContactNo() {
		return conContactNo;
	}

	public void setConContactNo(long conContactNo) {
		this.conContactNo = conContactNo;
	}

	public long getDriverConNo() {
		return driverConNo;
	}

	public void setDriverConNo(long driverConNo) {
		this.driverConNo = driverConNo;
	}

	public Date getRoadTaxDate() {
		return roadTaxDate;
	}

	public void setRoadTaxDate(Date roadTaxDate) {
		this.roadTaxDate = roadTaxDate;
	}

	public String getRfidno() {
		return rfidno;
	}

	public void setRfidno(String rfidno) {
		this.rfidno = rfidno;
	}

	public boolean isShowRfid() {
		return showRfid;
	}

	public void setShowRfid(boolean showRfid) {
		this.showRfid = showRfid;
	}

	public ArrayList<Route> getRfidList() {
		return rfidList;
	}

	public void setRfidList(ArrayList<Route> rfidList) {
		this.rfidList = rfidList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
