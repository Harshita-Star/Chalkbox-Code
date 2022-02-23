package reminder_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import session_work.RegexPattern;

@ManagedBean(name="addTask")
@ViewScoped

public class AddTaskBean implements Serializable
{
	String regex=RegexPattern.REGEX;
	String title,description,type,frequency;
	Date date=new Date();
	boolean showFreq=false;
	DataBaseMethodsReminder objReminder=new DataBaseMethodsReminder();
	TaskInfo selectedTask;
	ArrayList<TaskInfo> list = new ArrayList<>();

	public AddTaskBean()
	{
		showFreq = false;
		type = "One Time";
		Connection conn = DataBaseConnection.javaConnection();
		list = objReminder.taskList(conn);

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
			showFreq=false;
			frequency="NA";
		}

		/*else
		{
			showFreq=true;
			frequency="1";
		}*/

	}

	public String submit()
	{
		checkType();

		if(type.equalsIgnoreCase("Recurring") && frequency.equalsIgnoreCase("NA"))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Frequency."));
			return "";
		}

		Date tempDate = new Date();
		Timestamp dt = new Timestamp(tempDate.getTime());
		dt.setHours(0);dt.setMinutes(0);dt.setSeconds(0);dt.setNanos(0);

		Timestamp sdt = new Timestamp(date.getTime());
		sdt.setHours(0);sdt.setMinutes(0);sdt.setSeconds(0);sdt.setNanos(0);


		if(!sdt.after(dt))
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reminder Date Must be After Todays Date."));
			return "";
		}

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) ss.getAttribute("username");
		String schoolId = (String) ss.getAttribute("schoolId");

		Connection conn = DataBaseConnection.javaConnection();
		String id = "na";
		int i = objReminder.addTasks(title,description,date,type,frequency,id,conn,username,schoolId);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Task Added Successfully"));
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "addTask.xhtml";
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something Went Wrong. Try Again"));
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		
	}

	public void editDetails()
	{
		title=selectedTask.getTitle();
		description=selectedTask.getDescription();
		type=selectedTask.getType();
		if(type.equalsIgnoreCase("One Time"))
		{
			showFreq=false;
			frequency="NA";
		}
		else
		{
			showFreq=true;
			frequency=selectedTask.getFrequency();
		}
		date=selectedTask.getDate();

	}

	public String delete()
	{
		Connection conn=DataBaseConnection.javaConnection();
		String id=selectedTask.getId();
		int n=objReminder.removeTask(id,conn);
		if(n>=1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Task Removed Successfully!"));

			list = objReminder.taskList(conn);

			return "addTask.xhtml";
			//customerList=new DataBaseMethodJson().allCustomerList(conn);
		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Error Occurred,Please check!"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String update()
	{
		Connection conn = DataBaseConnection.javaConnection();
		String id = selectedTask.getId();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) ss.getAttribute("username");
		String schoolId = (String) ss.getAttribute("schoolId");
		int i = objReminder.addTasks(title,description,date,type,frequency,id,conn,username,schoolId);
		if(i>=1)
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Task Updated Successfully"));
			list = objReminder.taskList(conn);

			PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
			PrimeFaces.current().ajax().update("form");
			PrimeFaces.current().ajax().update("form1");
			return "addTask.xhtml";

		}
		else
		{
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Error Occurred,Please check!"));
		}

		try {
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isShowFreq() {
		return showFreq;
	}
	public void setShowFreq(boolean showFreq) {
		this.showFreq = showFreq;
	}
	public TaskInfo getSelectedTask() {
		return selectedTask;
	}
	public void setSelectedTask(TaskInfo selectedTask) {
		this.selectedTask = selectedTask;
	}
	public ArrayList<TaskInfo> getList() {
		return list;
	}
	public void setList(ArrayList<TaskInfo> list) {
		this.list = list;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}


}
