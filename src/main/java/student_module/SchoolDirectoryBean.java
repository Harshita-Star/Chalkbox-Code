package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.NotificationInfo;
@ManagedBean(name="schoolDirectory")
@ViewScoped
public class SchoolDirectoryBean implements Serializable
{
	ArrayList<NotificationInfo> contactList = new ArrayList<>();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
    String schoolId,session;
	
	public SchoolDirectoryBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		schoolId=DBM.schoolId();
		session = DBM.selectedSessionDetails(schoolId,conn);
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss1.getAttribute("username");
		
		contactList = DBM.viewPhonebook(schoolId,conn);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public DataBaseMeathodJson getDBJ() {
		return DBJ;
	}
	public void setDBJ(DataBaseMeathodJson dBJ) {
		DBJ = dBJ;
	}

	public ArrayList<NotificationInfo> getContactList() {
		return contactList;
	}

	public void setContactList(ArrayList<NotificationInfo> contactList) {
		this.contactList = contactList;
	}
}