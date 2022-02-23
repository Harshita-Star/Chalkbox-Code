package trigger_module;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class InitializeServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
//		try
//		{
//			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//			scheduler.start();

			// Time Table Job

//			JobDetail timeTableJob = JobBuilder.newJob(TimeTableJob.class).withIdentity("timeTableJob", "group3").build();
//			Trigger timeTableTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("timeTableTrigger", "group3")
//					.forJob(timeTableJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 0 06 * * ?"))
//					.build();
//			scheduler.scheduleJob(timeTableJob,timeTableTrigger);
//
//			//Reminder Job
//
//			JobDetail reminderJob = JobBuilder.newJob(ReminderJob.class).withIdentity("reminderJob", "group4").build();
//			Trigger reminderTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("reminderTrigger", "group4")
//					.forJob(reminderJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 0 07 * * ?"))
//					.build();
//			scheduler.scheduleJob(reminderJob,reminderTrigger);
//
//			//("Started");
//
//			//Auto collection job
//
//			JobDetail collectionJob = JobBuilder.newJob(AutoCollectionJob.class).withIdentity("collectionJob", "group5").build();
//			Trigger collectionTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("collectionTrigger", "group5")
//					.forJob(collectionJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 0 20 * * ?"))
//					.build();
//			scheduler.scheduleJob(collectionJob,collectionTrigger);
//
//			// Auto Birthday Job
//
//			JobDetail birthJob = JobBuilder.newJob(birthdayMessageJob.class).withIdentity("birthJob", "group6").build();
//			Trigger birthTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("birthTrigger", "group6")
//					.forJob(birthJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 0 09 * * ?"))
//					.build();
//			scheduler.scheduleJob(birthJob,birthTrigger);
//
//			// Auto Event Reminder Job
//
//			JobDetail eventJob = JobBuilder.newJob(EventReminderJob.class).withIdentity("eventJob", "group7").build();
//			Trigger eventTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("eventTrigger", "group7")
//					.forJob(eventJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 0 08 * * ?"))
//					.build();
//			scheduler.scheduleJob(eventJob,eventTrigger);
			
			
			
			//Meeting notification
//			JobDetail meetingNotificationJob = JobBuilder.newJob(MeetingNotificationJob.class).withIdentity("meetingNotificationJob", "group8").build();
//			Trigger meetingNotificationTrigger = TriggerBuilder
//					.newTrigger()
//					.withIdentity("meetingNotificationTrigger", "group8")
//					.forJob(meetingNotificationJob)
//					.withSchedule(
//							CronScheduleBuilder.cronSchedule("0 */1 * * * ?"))
//					.build();
//			scheduler.scheduleJob(meetingNotificationJob,meetingNotificationTrigger);

//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}

	}

}