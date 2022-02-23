package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import Json.DataBaseMeathodJson;
import session_work.RegexPattern;

@ManagedBean(name="addTemplates")
@ViewScoped
public class AddTemplateBean implements Serializable{
	
	String regex=RegexPattern.REGEX;
	
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	Boolean showName,showDate;
	String name,message,schoolId,date,dynamic,n,d;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AddTemplateBean()
	{
		schoolId=new DatabaseMethods1().schoolId();


	}
	public void name()
	{
		message=message+"#name";
		showName=true;
	}
	public void date()
	{
		int i = 0;
		message=message+"#date";
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
		if(message.contains("#name"))
		{

			message = message.replace("#name", newMessage);

		}

		if(message.contains("#date"))
		{
			message = message.replace("#date", newMessage);
		}


		showDate=false;
		showName=false;

	}
	public String submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		try 
		{
			if(message.contains("#name") || message.contains("#date"))
			{
				dynamic="true";
			}
			else
			{
				dynamic="false";
			}
			
			if(name.trim().equals("") || name.isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Add template title name"));
			}
			else
			{
				int i=DBJ.addMessageTemplate(message,name,dynamic,schoolId,conn);
				if(i>=1)
				{
					String refNo=addWorkLog(conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Template Added successfully!"));
					name=message="";
					return "addTemplate.xhtml";
					
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur! Please try again."));
				}
			}
			
		}
		catch (Exception e) {
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		return "";
		
	}

	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";

		
		language = "Title - "+name+" --- Message - "+message;
		value = "Title - "+name+" --- Message - "+message+" --- Dynamic - "+dynamic;
		
		
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add template","WEB",value,conn);
		return refNo;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDynamic() {
		return dynamic;
	}
	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
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