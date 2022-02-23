package trigger_module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;
import schooldata.StudentInfo;
import session_work.QueryConstants;

public class birthdayMessageJob implements Job
{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		//String strDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		ArrayList<SchoolInfo> dataList=new ArrayList<>();
		dataList=DBM.allSchoolListForBirthday(conn);

		if(dataList.size()>0)
		{
			//String collection = "0";
			String message = "";
			for(SchoolInfo ss : dataList)
			{
				//collection = "0";
				ArrayList<String> list=new DataBaseMethodStudent().birthdayFieldList();
				String session=DBM.selectedSessionDetails(ss.getId(), conn);
				ArrayList<StudentInfo> studentList=new DataBaseMethodStudent().studentDetail("","","",QueryConstants.IN_SCHOOL,QueryConstants.BIRTHDAY,new Date(),new Date(),"","","","", session, ss.getId(), list, conn);
				if(!studentList.isEmpty())
				{
					for(StudentInfo si : studentList)
					{
						if (String.valueOf(si.getFathersPhone()).length() == 10
								&& !String.valueOf(si.getFathersPhone()).equals("2222222222")
								&& !String.valueOf(si.getFathersPhone()).equals("9999999999")
								&& !String.valueOf(si.getFathersPhone()).equals("1111111111")
								&& !String.valueOf(si.getFathersPhone()).equals("1234567890")
								&& !String.valueOf(si.getFathersPhone()).equals("0123456789"))
						{
							if(ss.getType() == null || ss.getType().equals(""))
							{

							}
							else
							{
								message = "Dear " + si.getFname() + ",\n" + ss.getType() + " \nThanks," + ss.getSchoolName();
								DBM.messageurl1Auto(String.valueOf(si.getFathersPhone()), message,si.getAddNumber(),conn,ss.getId());
							}
						}

					}
				}

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
