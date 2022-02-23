package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.GeoFenceList;

@ManagedBean(name="geoFenceReport")
@ViewScoped
public class GeoFenceReportBean implements Serializable {

	
	
	ArrayList<GeoFenceList> list=new ArrayList<>();
	DatabaseMethods1 obj1=new DatabaseMethods1();

	
	public GeoFenceReportBean() {
      
		Connection conn=DataBaseConnection.javaConnection();
		
		list=obj1.createGeofenceReport(conn);
		
		
		try {
			
			
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
	}
	
	
	
	
}
