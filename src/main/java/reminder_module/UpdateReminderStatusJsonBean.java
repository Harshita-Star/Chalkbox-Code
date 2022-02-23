package reminder_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;

@ManagedBean(name="updateReminderStatusJson")
@ViewScoped

public class UpdateReminderStatusJsonBean implements Serializable
{
	String json;
	DataBaseMethodsReminder objReminder=new DataBaseMethodsReminder();
	public UpdateReminderStatusJsonBean()
	{
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();

		Connection conn=DataBaseConnection.javaConnection();

		String status = params.get("status"); // clear or snooze

		String remId = params.get("reminderId");
		String taskId = params.get("taskId");
		String type = params.get("type");

		String days = params.get("days");
		String startDate=params.get("date");

		SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");

		Date date=null;
		try {
			date=ss.parse(startDate);
		} catch (java.text.ParseException e)
		{
			e.printStackTrace();
		}

		if(status.equalsIgnoreCase("clear"))
		{
			days="0";
		}

		int snDays = Integer.valueOf(days);

		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();

		if(status.equalsIgnoreCase("clear"))
		{
			String result = clear(remId, taskId, type);
			obj.put("status", result);
		}
		else if(status.equalsIgnoreCase("snooze"))
		{
			String result = snooze(date, snDays, remId);
			obj.put("status", result);
		}
		else
		{
			obj.put("status", "Action Unavailable");
		}

		arr.put(obj);
		json=arr.toString();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String clear(String remId, String taskId, String type)
	{
		Connection conn=DataBaseConnection.javaConnection();
		objReminder.updateReminderStatus(remId, "complete", "0", conn);
		if(type.equalsIgnoreCase("One Time"))
		{
			objReminder.updateTaskStatus(taskId, "inactive", conn);
		}


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "Reminder Cleared";
	}

	public String snooze(Date remDate, int snoozDays, String remId )
	{
		Connection conn=DataBaseConnection.javaConnection();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt=remDate;
		dt.setDate(dt.getDate()+snoozDays);

		String nextDate = sdf.format(dt);

		objReminder.updateReminderDate(remId, "snooze", nextDate, snoozDays, conn);


		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "Reminder Snoozed";
	}

	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();
	}


}
