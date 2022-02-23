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
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
@ManagedBean(name="schoolTransportDetails")
@ViewScoped
public class SchoolTransportDetailsBean implements Serializable
{
	String conductor,attendant,driver;
	StudentInfo list;
	ArrayList<NotificationInfo> contactList = new ArrayList<>();
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	String schid,session;

	public SchoolTransportDetailsBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss1 = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss1.getAttribute("username");
		schid = DBM.schoolId();
		SchoolInfoList ls=DBJ.fullSchoolInfo(schid, conn);

		list=new DataBaseMeathodJson().studentDetailslistByAddNo(studentid,schid,conn);
		if( list.getEmpImageDriver()==null || list.getEmpImageDriver().equals("") || list.getEmpImageDriver().equalsIgnoreCase("no"))
		{
			driver="";
		}
		else
		{
			driver=ls.getDownloadpath()+list.getEmpImageDriver();
		}
		if( list.getEmpImageConductor()==null || list.getEmpImageConductor().equals("") || list.getEmpImageConductor().equalsIgnoreCase("no"))
		{
			conductor="";
		}
		else
		{
			conductor=ls.getDownloadpath()+list.getEmpImageConductor();
		}
		if( list.getEmpImageAttendant()==null || list.getEmpImageAttendant().equals("") || list.getEmpImageAttendant().equalsIgnoreCase("no"))
		{
			attendant="";
		}
		else
		{
			attendant=ls.getDownloadpath()+list.getEmpImageAttendant();
		}



		try {
			conn.close();
		} catch (SQLException e)
		{
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

	public StudentInfo getList() {
		return list;
	}

	public void setList(StudentInfo list) {
		this.list = list;
	}

	public String getConductor() {
		return conductor;
	}

	public void setConductor(String conductor) {
		this.conductor = conductor;
	}

	public String getAttendant() {
		return attendant;
	}

	public void setAttendant(String attendant) {
		this.attendant = attendant;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}
}