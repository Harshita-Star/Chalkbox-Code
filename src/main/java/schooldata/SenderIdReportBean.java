package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import session_work.RegexPattern;

@ManagedBean(name="senderIdReport")
@ViewScoped
public class SenderIdReportBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<SchoolInfo> dataList;
	SchoolInfo selected = new SchoolInfo();
	DatabaseMethods1 obj = new DatabaseMethods1();
	public SenderIdReportBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		dataList=obj.selectAllDataListFromSchoolInfoTable(conn,"school");

	}
	public void edit() {
		Connection conn = DataBaseConnection.javaConnection();
		int i= obj.UpdateSenderIdInSchoolInfo(conn,selected);
		if(i>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Updated Successfully!"));
			dataList=obj.selectAllDataListFromSchoolInfoTable(conn,"school");
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occur!Please try again."));
		}
	}
	public ArrayList<SchoolInfo> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<SchoolInfo> dataList) {
		this.dataList = dataList;
	}
	public DatabaseMethods1 getObj() {
		return obj;
	}
	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public SchoolInfo getSelected() {
		return selected;
	}
	public void setSelected(SchoolInfo selected) {
		this.selected = selected;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
}
