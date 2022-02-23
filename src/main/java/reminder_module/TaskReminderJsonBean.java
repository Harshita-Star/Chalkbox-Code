package reminder_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.DataBaseConnection;

@ManagedBean(name="taskReminderJson")
@ViewScoped

public class TaskReminderJsonBean implements Serializable
{
	String json;
	public TaskReminderJsonBean()
	{
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();
		String username = params.get("username");
		String schoolId = params.get("Schoolid");

		Connection conn=DataBaseConnection.javaConnection();
		ArrayList<TaskInfo> list = new ArrayList<>();
		list = new DataBaseMethodsReminder().taskReminderList(conn, username, schoolId);

		JSONArray arr=new JSONArray();
		for(TaskInfo ls:list)
		{
			JSONObject obj = new JSONObject();
			obj.put("description",ls.getDescription());
			obj.put("date",ls.getStrDate());
			obj.put("title",ls.getTitle());
			obj.put("reminderId", ls.getId());
			obj.put("taskId", ls.getTaskId());
			obj.put("type", ls.getType());

			arr.put(obj);
		}

		json=arr.toString();

		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
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
