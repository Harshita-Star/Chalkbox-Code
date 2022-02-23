package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import reminder_module.DataBaseMethodsReminder;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;

public class ReminderJob implements Job
{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();

		ArrayList<SchoolInfo> dataList=new ArrayList<>();
		dataList=DBM.allSchoolList(conn);
		if(dataList.size()>0)
		{
			for(SchoolInfo ss : dataList)
			{
				new DataBaseMethodsReminder().checkOneTimeReminders(conn,ss.getId());
				new DataBaseMethodsReminder().checkRecurringReminders(conn,ss.getId());

			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
