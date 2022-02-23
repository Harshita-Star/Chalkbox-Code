package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.RouteStop;

@ManagedBean(name="routeWiseAttendantRecord")
@ViewScoped

public class RouteWiseAttendantRecordBean implements Serializable
{
	Date date;
	String dateStr = "",username="",schoolid="",type="",routeId="",sessionValue;
	ArrayList<SelectItem> routelist = new ArrayList<>();
	ArrayList<RouteStop> slist = new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 dbm = new DatabaseMethods1();

	
	public RouteWiseAttendantRecordBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		
		date = new Date();
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		schoolid=(String) ss.getAttribute("schoolid");
		type=(String) ss.getAttribute("type");
		sessionValue=dbm.selectedSessionDetails(schoolid, conn);

		String id = DBJ.employeeIdbyEmployeeName(username, schoolid, conn);
		//DBJ.employeeCategIdByEmployeeId(id, schoolid, conn);

		routelist = DBJ.attendantRouteList(type,id,schoolid,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void search()
	{
		dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		Connection conn= DataBaseConnection.javaConnection();
		
		slist = DBJ.routeWiseStopList(schoolid,routeId,strDate,conn);
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public ArrayList<SelectItem> getRoutelist() {
		return routelist;
	}

	public void setRoutelist(ArrayList<SelectItem> routelist) {
		this.routelist = routelist;
	}

	public ArrayList<RouteStop> getSlist() {
		return slist;
	}

	public void setSlist(ArrayList<RouteStop> slist) {
		this.slist = slist;
	}
}
