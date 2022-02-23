package reminder_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import schooldata.DataBaseConnection;
@ManagedBean(name="taskReminder")
@ViewScoped

public class TaskReminderBean implements Serializable
{
	ArrayList<TaskInfo> list = new ArrayList<>();
	TaskInfo selectedTask;
	int snoozDays;
	DataBaseMethodsReminder objReminder=new DataBaseMethodsReminder();
	public TaskReminderBean()
	{
		/*HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		list = (ArrayList<TaskInfo>) ss.getAttribute("reminderList");*/
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) ss.getAttribute("username");
		String schoolid = (String) ss.getAttribute("schoolId");
		Connection conn=DataBaseConnection.javaConnection();
		list = objReminder.taskReminderList(conn,username,schoolid);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clear()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) ss.getAttribute("username");
		String schoolId = (String) ss.getAttribute("schoolId");
		Connection conn=DataBaseConnection.javaConnection();
		objReminder.updateReminderStatus(selectedTask.getId(), "complete", "0", conn);
		if(selectedTask.getType().equalsIgnoreCase("One Time"))
		{
			objReminder.updateTaskStatus(selectedTask.getTaskId(), "inactive", conn);
		}

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reminder Cleared"));

		list = objReminder.taskReminderList(conn, username, schoolId);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void snooze()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) ss.getAttribute("username");
		String schoolId = (String) ss.getAttribute("schoolId");

		Connection conn=DataBaseConnection.javaConnection();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt=selectedTask.getDate();
		dt.setDate(dt.getDate()+snoozDays);

		String nextDate = sdf.format(dt);

		objReminder.updateReminderDate(selectedTask.getId(), "snooze", nextDate, snoozDays, conn);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reminder Snoozed"));

		list = objReminder.taskReminderList(conn, username, schoolId);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<TaskInfo> getList() {
		return list;
	}

	public void setList(ArrayList<TaskInfo> list) {
		this.list = list;
	}

	public TaskInfo getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(TaskInfo selectedTask) {
		this.selectedTask = selectedTask;
	}

	public int getSnoozDays() {
		return snoozDays;
	}

	public void setSnoozDays(int snoozDays) {
		this.snoozDays = snoozDays;
	}


}
