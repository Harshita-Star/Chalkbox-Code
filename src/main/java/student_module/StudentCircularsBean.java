package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.NotificationInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="studentCirculars")
@ViewScoped

public class StudentCircularsBean implements Serializable
{
	ArrayList<NotificationInfo> newsList = new ArrayList<>();
	NotificationInfo selected = new NotificationInfo();
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
    String schoolId,session;

	public StudentCircularsBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String studentid = (String) ss.getAttribute("username");
		schoolId = obj.schoolId();
		session = obj.selectedSessionDetails(schoolId,conn);
		newsList=DBJ.allCircularList(studentid,schoolId,conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void view()
	{
		Connection conn = DataBaseConnection.javaConnection();
		SchoolInfoList ls=DBJ.fullSchoolInfo(schoolId,conn);
		PrimeFaces.current().executeInitScript("window.open('"+ls.getDownloadpath()+selected.getFilename()+"')");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NotificationInfo> getNewsList() {
		return newsList;
	}

	public void setNewsList(ArrayList<NotificationInfo> newsList) {
		this.newsList = newsList;
	}

	public NotificationInfo getSelected() {
		return selected;
	}

	public void setSelected(NotificationInfo selected) {
		this.selected = selected;
	}


}
