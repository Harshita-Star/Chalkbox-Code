package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import schooldata.DataBaseConnection;
import timetable_module.DataBaseMethodsTimeTable;

public class TimeTableJob implements Job
{
	/*public void execute(JobExecutionContext context)throws JobExecutionException
    {
    	//// // System.out.println("Method Start Time table job");
    	Timestamp currentDate=new Timestamp(new Date().getTime());currentDate.setHours(0);currentDate.setMinutes(0);currentDate.setSeconds(0);currentDate.setNanos(0);
    	String session=new DataBase().currentSessionFormSessionTable();
    	new DataBaseMethodsTimeTable().replaceTimeTable(currentDate,session);
    	//// // System.out.println("method End");
    }*/



	//Run Wher Servlet Not Working

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=DataBaseConnection.javaConnection();
		Timestamp currentDate=new Timestamp(new Date().getTime());currentDate.setHours(0);currentDate.setMinutes(0);currentDate.setSeconds(0);currentDate.setNanos(0);
		new DataBaseMethodsTimeTable().replaceTimeTable(currentDate,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


}