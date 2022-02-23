package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;


@ManagedBean(name="transportBean")
@ViewScoped
public class TransportBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<RouteStop>list,routeInfoList,selectedList = new ArrayList<RouteStop>();
	ArrayList<SelectItem> busList,attendantList,categoryList;
	ArrayList<SelectItem> monthList;
	String routeName="",selectedBus,selectedMonth;
	List<SelectItem> routeList;
	boolean view,showCategory,showAmount;
	int stopAmount;
	String selectedRoute,route,source,name,destination="School",description,path,schid,session,stopName,attendant;
	Date creatingDate;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseValidator validator = new DataBaseValidator();


	public TransportBean()
	{
		creatingDate=new Date();
		Connection conn=DataBaseConnection.javaConnection();
		
		schid=obj.schoolId();
		//showCategory=true;
		showAmount=true;
		session=obj.selectedSessionDetails(schid,conn);
		busList=obj.allBuses(schid, conn);
		attendantList=obj.allEmployeeListWithId(conn);
		categoryList=obj.stopCategoryList(schid, session, conn);
		emptyStopList();
		try
		{
			routeList=obj.allTransportRoutes(conn);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void gotoaddmissioncon() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("conductorAdmission.xhtml");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void gotoaddmission() {

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("driverAdmission.xhtml");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void emptyStopList()
	{
		routeInfoList=new ArrayList<>();
		for(int i=1;i<=10;i++)
		{
			RouteStop ll=new RouteStop();
			ll.setSerialNumber(String.valueOf(i));
			ll.setStopName("");
			ll.setAmount(0);
			routeInfoList.add(ll);
		}
	}

	public void sectionpage()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("addNewStop.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void editSection()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editRouteStop.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void editClass()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editRouteDetails.xhtml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void showNow()
	{
		Connection conn=DataBaseConnection.javaConnection();

		list=obj.routeStopListAll(selectedRoute,conn);
		routeName=obj.routeNameById(schid,selectedRoute, conn);
		view=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateStopFees() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(selectedList.size()>0)
		{
			boolean check=obj.checkPromotionForSession(conn);
			if(check==true)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Edit Details.. Because You Promoted Student From This Session"));
			}
			else
			{
				int x = 0;
				for(RouteStop rr : selectedList)
				{
					int i = obj.updateRouteStop(selectedRoute, rr.getStopName(), rr.getNewAmount(), rr.getGroupid(), selectedMonth, rr.getCategoryId(), conn);
					if(i>=1)
					{
						x+=1;
					}
				}
				
				if(x>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Stop updated successfully", "Validation error"));
					showNow();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select atleast one stop to update the fees", "Validation error"));
		}
		
		list=obj.routeStopListAll(selectedRoute,conn);
		routeName=obj.routeNameById(schid,selectedRoute, conn);
		view=true;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void monthDetails()
	{
		monthList=new ArrayList<>();
		//int month=list.getMonth();
		monthList=new DatabaseMethods1().monthListHandling();

	}
	
	public ArrayList<RouteStop> getList() {
		return list;
	}

	public void setList(ArrayList<RouteStop> list) {
		this.list = list;
	}

	public String addNewStop()
	{
		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			int flag=0;
			if(stopAmount<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Fee amount must be greater than zero", "Validation error"));

			}
			if(flag==0)
			{
				int status=validator.duplicateRouteStop(schid, selectedRoute, stopName,conn,session);
				if(status==0)
				{
					flag++;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,choose a diffenent one", "Validation error"));

				}
			}
			if(flag==0)
			{
				try
				{
					//HttpSession httpSession=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					//String session=(String)httpSession.getAttribute("selectedSession");
					int i=obj.addNewStop(selectedRoute, stopName, stopAmount,"from single add",conn);
					if(i==1)
					{
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("New stop added successfully"));

						selectedRoute=null;
						stopAmount=0;
						stopName=null;
						return "addNewStop.xhtml";
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}


			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";

	}

	public String addNewStopList()
	{
		int j=0;
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			for(RouteStop ll:routeInfoList)
			{
				if(!ll.getStopName().equals("") && ll.getStopName()!=null)
				{
					try
					{
						if(!ll.getCategoryId().equals("0"))
							ll.setAmount(Integer.parseInt(obj.stopCategoryInfoById(ll.getCategoryId(),conn).getAmount()));
						int i=obj.addNewStop(selectedRoute, ll.getStopName().toUpperCase(), ll.getAmount(),ll.getCategoryId(),conn);
						if(i==1)
						{
							
							FacesContext fc=FacesContext.getCurrentInstance();
							fc.addMessage(null, new FacesMessage("New stop :"+ll.getStopName().toUpperCase()+", added successfully!"));
							j=1;
						}
						/*else
						{
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));
						}*/
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			
			

			if(j==1)
			{
				String refNo3;
				try {
					refNo3=addWorkLog3(conn);
				} catch (Exception e) {
					// TODO: handle exception
				}
				emptyStopList();
				showNow();
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

		return "";
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= selectedRoute;
	
		
		for (RouteStop ls : routeInfoList) {	
		
			if(!ls.getStopName().equals("") && ls.getStopName()!=null)	
			{	
				if(value.equals(""))
				{
					value = "("+ls.getStopName().toUpperCase()+"---"+ls.getAmount()+"---"+ls.getCategoryId()+")";
				}
				else
				{
					value = value + "("+ls.getStopName().toUpperCase()+"---"+ls.getAmount()+"---"+ls.getCategoryId()+")";
				}
			}
		}	
		String refNo = workLg.saveWorkLogMehod(language,"Add Stop","WEB",value,conn);
		return refNo;
	}


	public void checkStop()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		int flag=0;
		String selectedRow= (String) UIComponent.getCurrentComponent(context).getAttributes().get("serialNumber");
		for(RouteStop ll:routeInfoList)
		{
			if(ll.getSerialNumber().equals(selectedRow))
			{
				stopName=ll.getStopName().toUpperCase();
				for(RouteStop info:routeInfoList)
				{
					if(!info.getSerialNumber().equals(selectedRow.toUpperCase()) && (info.getStopName()!=null && !info.getStopName().equals("")))
					{
						if(info.getStopName().equalsIgnoreCase(stopName))
						{
							flag=1;
							break;
						}
					}
				}
				if(flag==1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Duplicate Stop Found"));
					ll.setStopName("");
				}
				else
				{
					int status=validator.duplicateRouteStop(schid,selectedRoute, stopName,conn,session);
					if(status==0)
					{
						//flag++;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,choose a diffenent one", "Validation error"));
						ll.setStopName("");
					}
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String getRoute() {

		Connection conn=DataBaseConnection.javaConnection();
		try
		{
			String temp=String.valueOf(obj.transportRoute(conn));
			route="Route"+temp;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return route;
	}


	public String addRoute()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int flag=0;
		/*int status=new DataBaseValidator().duplicateTransportRoute(source, destination,conn);
		if(status==0)
		{
			flag++;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,try a different one", "Validation error"));

		}*/
		if(flag==0)
		{
			try
			{

				/*HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				String session = (String) httpSession.getAttribute("selectedSession");
				 */
				if(source.equals(destination)){

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Source and destination can't be same", "Validation error"));

				}
				else{
					/*int i= new DatabaseMethods1().newRoute(route+"-"+name, source, destination, vehicleNumber, driverName,
							contactNumber, creatingDate,session,conn,gps,conductorName,conContactNumber,busAttendant);*/
					int i=obj.newRoute(route+"-"+name, source, destination,creatingDate,selectedBus,attendant,conn);
					if(i==1)
					{
						String refNo;
						try {
							refNo=addWorkLog(conn);
						} catch (Exception e) {
							// TODO: handle exception
						}
						FacesContext fc=FacesContext.getCurrentInstance();
						fc.addMessage(null, new FacesMessage("Route added successfully"));

						source=attendant=selectedBus=null;
						destination="School";
						description=null;
						creatingDate=new Date();
						name=null;
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred,try again", "Validation error"));

					}
				}

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
	
		Timestamp postdate = new Timestamp(creatingDate.getTime());
		postdate.setHours(0);
		postdate.setMinutes(0);
		postdate.setSeconds(0);
		postdate.setNanos(0);
		
		language = "Name - "+name+" --- Source - "+source+" --- Bus - "+selectedBus+" --- Attendant - "+attendant;
		value = route+"-"+name+" --- "+source+" --- "+destination+" --- "+postdate+" --- "+selectedBus+" --- "+attendant;
		
		
		String refNo = workLg.saveWorkLogMehod(language,"Add Route","WEB",value,conn);
		return refNo;
	}


	public String getStopName() {
		return stopName;
	}
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}
	public int getStopAmount() {
		return stopAmount;
	}
	public void setStopAmount(int stopAmount) {
		this.stopAmount = stopAmount;
	}
	public List<SelectItem> getRouteList() {
		return routeList;
	}
	public void setRouteList(List<SelectItem> routeList) {
		this.routeList = routeList;
	}
	public String getSelectedRoute() {
		return selectedRoute;
	}
	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}
	public boolean isView() {
		return view;
	}
	public void setView(boolean view) {
		this.view = view;
	}

	public Date getCreatingDate() {
		return creatingDate;
	}

	public void setCreatingDate(Date creatingDate) {
		this.creatingDate = creatingDate;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public void setRoute(String route) {
		this.route = route;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<RouteStop> getRouteInfoList() {
		return routeInfoList;
	}

	public void setRouteInfoList(ArrayList<RouteStop> routeInfoList) {
		this.routeInfoList = routeInfoList;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public ArrayList<SelectItem> getBusList() {
		return busList;
	}

	public void setBusList(ArrayList<SelectItem> busList) {
		this.busList = busList;
	}

	public String getSelectedBus() {
		return selectedBus;
	}

	public void setSelectedBus(String selectedBus) {
		this.selectedBus = selectedBus;
	}

	public ArrayList<SelectItem> getAttendantList() {
		return attendantList;
	}

	public void setAttendantList(ArrayList<SelectItem> attendantList) {
		this.attendantList = attendantList;
	}

	public String getAttendant() {
		return attendant;
	}

	public void setAttendant(String attendant) {
		this.attendant = attendant;
	}

	public ArrayList<SelectItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public boolean isShowCategory() {
		return showCategory;
	}

	public void setShowCategory(boolean showCategory) {
		this.showCategory = showCategory;
	}

	public boolean isShowAmount() {
		return showAmount;
	}

	public void setShowAmount(boolean showAmount) {
		this.showAmount = showAmount;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public ArrayList<RouteStop> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<RouteStop> selectedList) {
		this.selectedList = selectedList;
	}

	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}
	
}
