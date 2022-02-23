package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import schooldata.DataBaseConnection;
import schooldata.Route;

public class GPSTrackingJob implements Job
{
	ArrayList<Route> routeList;
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=DataBaseConnection.javaConnection();

		routeList=new DataBaseMethods().allRouteListWithGpsDevice(conn);
		if(routeList.size()>0)
		{
			for(Route rr : routeList)
			{
				if(!rr.getGpsImei().equals("0"))
				{
					String dataPoints=new DataBaseMethods().obtainGpsDataPoints(rr.getGpsImei());
					new DataBaseMethods().storeGpsDataPoints(rr.getRouteId(),dataPoints,rr.getSchoolId(),conn);
				}

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
