package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.Route;
import schooldata.SchoolInfoList;

@ManagedBean(name="trackBlackboxGPS")
@ViewScoped

public class TrackBlackboxGPSBean implements Serializable
{
	ArrayList<SelectItem> gpsList;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsReports dbr=new DataBaseMethodsReports();
	
	Route info = new Route();
	String schid,imeino,url,session;
	boolean showGPS;
	
	public TrackBlackboxGPSBean() 
	{
		Connection conn=DataBaseConnection.javaConnection();
		url="https://www.google.com/maps/embed/v1/place?q=28.61564,77.206588&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
		//url="http://maps.google.com/maps?q=28.61564,77.206588&z=15&output=embed";
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);

		String checkGPS=obj.checkGPS(conn);
		if(checkGPS.equalsIgnoreCase("yes"))
		{
			gpsList=obj.gpsListNew(schid,conn);
			showGPS=true;
		}
		else
		{
			showGPS=false;
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void track()
	{
		//// // System.out.println(imeino);
		Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList list = obj.fullSchoolInfo(conn);
		if(list.getGps().equalsIgnoreCase("yes"))
		{
			if(list.getGpsProvider().equalsIgnoreCase("blackbox"))
			{
				if(list.getGpsApi()==null || list.getGpsApi().equals(""))
				{
					url="https://www.google.com/maps/embed/v1/place?q=28.61564,77.206588&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Tracking is Unavailable in This Account."));
				}
				else
				{
					info = dbr.obtainBlackboxData(list.getGpsApi(),imeino);
					if(info.getLatitude().equalsIgnoreCase("na") || info.getLongitude().equalsIgnoreCase("na") || 
							info.getLocation().equalsIgnoreCase("na") || info.getDistance().equalsIgnoreCase("na") || 
							info.getLastDate().equalsIgnoreCase("na") || info.getStatus().equalsIgnoreCase("na"))
					{
						url="https://www.google.com/maps/embed/v1/place?q=28.61564,77.206588&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Currently GPS Tracking is Unavailable. Please Try Later!"));
					}
					else
					{
						url="https://www.google.com/maps/embed/v1/place?q="+info.getLatitude()+","+info.getLongitude()+"&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
						//String strUrl = "https://www.google.com/maps/search/?api=1&query="+info.getLatitude()+","+info.getLongitude();
						//String strUrl = "https://www.google.com/maps/search/?api=1&query="+info.getLocation();
						//PrimeFaces.current().executeInitScript("window.open('"+strUrl+"')");
					}
				}
			}
			else if(list.getGpsProvider().equalsIgnoreCase("xtrack"))
			{
				if(list.getGpsUser()==null || list.getGpsPwd()==null || list.getGpsUser().equals("") || list.getGpsPwd().equals(""))
				{
					url="https://www.google.com/maps/embed/v1/place?q=28.61564,77.206588&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Tracking is Unavailable in This Account."));
				}
				else
				{
					String strUrl = "https://track.chalkboxerp.in/Login_end?e="+list.getGpsUser()+"&p="+list.getGpsPwd();

					PrimeFaces.current().executeInitScript("window.open('"+strUrl+"')");
				}
			}
			
		}
		else
		{
			url="https://www.google.com/maps/embed/v1/place?q=28.61564,77.206588&key=AIzaSyAuPUXqTBXELNaxd9MH-lilZ3cw76ch0uo&zoom=18";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("GPS Tracking is Unavailable in This Account."));
		}
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<SelectItem> getGpsList() {
		return gpsList;
	}

	public void setGpsList(ArrayList<SelectItem> gpsList) {
		this.gpsList = gpsList;
	}

	public Route getInfo() {
		return info;
	}

	public void setInfo(Route info) {
		this.info = info;
	}

	public boolean isShowGPS() {
		return showGPS;
	}

	public void setShowGPS(boolean showGPS) {
		this.showGPS = showGPS;
	}

	public String getImeino() {
		return imeino;
	}

	public void setImeino(String imeino) {
		this.imeino = imeino;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
