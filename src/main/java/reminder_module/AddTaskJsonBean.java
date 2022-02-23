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

@ManagedBean(name="addTaskJson")
@ViewScoped

public class AddTaskJsonBean implements Serializable
{
	String json;
	String type,frequency;

	public AddTaskJsonBean()
	{
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();

		Connection conn=DataBaseConnection.javaConnection();

		String username = params.get("username");
		String schoolId = params.get("Schoolid");

		String title = params.get("title");
		String description = params.get("description");
		type = params.get("type");
		frequency = params.get("frequency");
		String startDate=params.get("date");

		SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");

		Date date=null;
		try {
			date=ss.parse(startDate);
		} catch (java.text.ParseException e)
		{
			e.printStackTrace();
		}

		String id="na";

		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();

		checkType();

		/*Date tempDate = new Date();
		Timestamp dt = new Timestamp(tempDate.getTime());
		dt.setHours(0);dt.setMinutes(0);dt.setSeconds(0);dt.setNanos(0);

		Timestamp sdt = new Timestamp(date.getTime());
		sdt.setHours(0);sdt.setMinutes(0);sdt.setSeconds(0);sdt.setNanos(0);



        if(type.equalsIgnoreCase("Recurring") && frequency.equalsIgnoreCase("NA"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Frequency."));
		}
        else if(type.equalsIgnoreCase("One Time") && sdt.before(dt))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("One Time Reminder Cannot Be In Back Date."));
		}*/

		int i = new DataBaseMethodsReminder().addTasks(title,description,date,type,frequency,id,conn,username,schoolId);
		if(i>=1)
		{
			obj.put("status", "Task Added Successfully");
		}
		else
		{
			obj.put("status", "Something Went Wrong. Try Again");
		}

		arr.put(obj);
		json=arr.toString();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkType()
	{
		if(type.equalsIgnoreCase("One Time"))
		{
			frequency="NA";
		}
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
