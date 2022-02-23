package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="editDeleteTemplate")
@ViewScoped
public class EditDeleteTemplateBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	ArrayList<AllGroupList> list=new ArrayList<>();
	AllGroupList selected;
	String schoolId,message;
	Boolean showName,showDate;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EditDeleteTemplateBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId=new DatabaseMethods1().schoolId();
		list=DBJ.allMessage(schoolId,conn);
		try
		{
			conn.close();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public void check()
	{
		message=selected.getAddNumber();
		if(message.contains("#name"))
		{
			showName=true;
		}
		else
		{
			showName=false;
		}

		if(message.contains("#date"))
		{

			int i = 0;
			Pattern p = Pattern.compile("#date");
			Matcher m = p.matcher( message );
			while (m.find()) {
				i++;
			}

			if(i<2)
			{
				showDate=false;
			}
			else
			{
				showDate=true;
			}
		}
		else
		{
			showDate=false;
		}
	}

	public void name()
	{
		message=selected.getAddNumber();
		message=message+"#name";
		selected.setAddNumber(message);
		showName=true;
	}
	public void date()
	{
		int i = 0;
		message=selected.getAddNumber();
		message=message+"#date";
		selected.setAddNumber(message);
		Pattern p = Pattern.compile("#date");
		Matcher m = p.matcher( message );
		while (m.find()) {
			i++;
		}

		if(i<2)

		{
			showDate=false;
		}
		else
		{
			showDate=true;
		}
	}
	public void reset()
	{
		String newMessage="";
		message=selected.getAddNumber();
		if(message.contains("#name"))
		{

			message = message.replace("#name", newMessage);

		}

		if(message.contains("#date"))
		{
			message = message.replace("#date", newMessage);
		}

		selected.setAddNumber(message);
		showDate=false;
		showName=false;

	}
	public void edit()
	{

		Connection conn = DataBaseConnection.javaConnection();

		int j=new DataBaseMeathodJson().allMessageEdit(selected.getId(),selected.getAddNumber(),selected.getName(),schoolId,conn);
		if(j>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successfully edited!"));
			list=DBJ.allMessage(schoolId,conn);

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occure during edition"));
		}
		try
		{
			conn.close();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	public Boolean getShowName() {
		return showName;
	}
	public void setShowName(Boolean showName) {
		this.showName = showName;
	}
	public Boolean getShowDate() {
		return showDate;
	}
	public void setShowDate(Boolean showDate) {
		this.showDate = showDate;
	}
	public void delete()
	{
		Connection conn = DataBaseConnection.javaConnection();
		int i=new DataBaseMeathodJson().allMessagedelete(selected.getId(),schoolId,conn);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Successfully deleted!"));
			list=DBJ.allMessage(schoolId,conn);


		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occure during deletion"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<AllGroupList> getList() {
		return list;
	}

	public AllGroupList getSelected() {
		return selected;
	}

	public void setSelected(AllGroupList selected) {
		this.selected = selected;
	}

	public void setList(ArrayList<AllGroupList> list) {
		this.list = list;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}

	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
