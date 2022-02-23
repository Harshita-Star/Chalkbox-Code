package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import schooldata.DataBaseConnection;
import timetable_module.DataBaseMethodsTimeTable;

public class RunSchedulerInCore {

	public static void main(String[] args) {
		Connection conn=DataBaseConnection.javaConnection();
		Timestamp currentDate=new Timestamp(new Date().getTime());currentDate.setHours(0);currentDate.setMinutes(0);currentDate.setSeconds(0);currentDate.setNanos(0);
		new DataBaseMethodsTimeTable().replaceTimeTable(currentDate,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*int i=17;
		if(i==18);
		{
			//// // System.out.println("welcome");
		}*/
	}

}
