package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;

public class AutoCollectionJob implements Job
{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String strDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		ArrayList<SchoolInfo> dataList=new ArrayList<>();
		dataList=DBM.allSchoolListForCollection(conn);
		if(dataList.size()>0)
		{
			String collection = "0";
			String message = "";
			for(SchoolInfo ss : dataList)
			{
				collection = "0";
				collection = DBM.todaysCollection(ss.getId(),"collectionTrigger", conn);
				message = "Hello Sir, today's("+strDate+") fee collection in "+ss.getSchoolName()+" is Rs."+collection;
				DBM.messageurlStaff(ss.getAutoCollectionNo(), message, "Admin", conn, ss.getId(),"");
				////// // System.out.println(message);
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
